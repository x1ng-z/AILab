package hs.Bean;

import com.alibaba.fastjson.JSONObject;
import hs.ApcAlgorithm.ExecutePythonBridge;
import hs.Opc.OPCService;

import java.util.*;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/18 8:24
 */
public class ControlModle {
    private long validkey=System.currentTimeMillis();
    private int totalPv = 8;
    private int totalFf = 8;
    private int totalMv = 8;

    /**
     * 模型运算状态值
     * */
    private double[][] backPVPrediction;//pv的预测曲线
    private double[][] backPVFunelUp;//PV的漏斗上限



    private double[][] backPVFunelDown;//PV的漏斗下限
    private double[] backDmvWrite;//MV写入值
    private double[] backPVPredictionError;//预测误差


    private OPCService OPCserver;
    private BaseConf baseConf;
    private ExecutePythonBridge executePythonBridge;
    private int modleId;
    private String modleName;

    /**
     * 模型定义
     * */
    private Integer predicttime_P = 12;//预测时域
    private Integer controltime_M = 6;//单一控制输入未来控制M步增量(控制域)
    private Integer timeserise_N = 40;//响应序列长度
    private Integer controlAPCOutCycle = 0;//控制周期
    private int modleEnable;//模块使能，用于设置算法是否运行，算法是否运行

    private List<ModlePin> modlePins;//引脚
    private List<ResponTimeSerise> responTimeSerises;//响应

    private Integer outpoints_p = 0;//输出个数量
    private Integer feedforwardpoints_v = 0;//前馈数量
    private Integer inputpoints_m = 0;//可控制输入数量

    private List<ModlePin> categoryPVmodletag = new ArrayList<>();
    private List<ModlePin> categorySPmodletag = new ArrayList<>();
    private List<ModlePin> categoryMVmodletag = new ArrayList<>();
    private List<ModlePin> categoryFFmodletag = new ArrayList<>();


    private ModlePin autoEnbalePin =null;//dcs手自动切换引脚


    private Double[][][] A_timeseriseMatrix = null;//输入响应 [pv][mv][resp_N]

    private Double[][][] B_timeseriseMatrix = null;//前馈响应

    private Double[] Q = null;
    private Double[] R = null;
    private Double[] alpheTrajectoryCoefficients=null;

    private Double[] deadZones = null;
    private Double[] funelinitvalues = null;

    /**指示PV用了哪几个mv
     * 如[[1,0]，
     *    [0,1]
     * ]
     * 意思是pv1用了mv1,pv2用了mv2
     * */
    private int[][] pvusemv=null;

    public Map<String, ModlePin> getStringmodlePinsMap() {
        return stringmodlePinsMap;
    }

    private Map<String, ModlePin> stringmodlePinsMap = new HashMap<>();//方便引脚索引

    //   public  void  selfinit(){
//        modleBuild();
//    }
    public Boolean modleBuild() {

        for (ModlePin modlePin : modlePins) {
            /**注册opc点号*/
            stringmodlePinsMap.put(modlePin.getModlePinName(), modlePin);
            if ((OPCserver.getGroup()!=null)&&(modlePin != null) && modlePin.getResource().equals("opc")) {
                if (!OPCserver.register(modlePin)) {
                    return false;
                }
            }

            /**
             * 手自动切换引脚提取
             * */
            if((modlePin!=null)&&(modlePin.getResource().equals("opc"))&&(modlePin.getModlePinName().equals("auto"))){
                autoEnbalePin =modlePin;
            }
        }







        /**
         * pv and sp
         * */

        for (int i = 1; i <= totalPv; i++) {
            ModlePin pvPin = stringmodlePinsMap.get("pv" + i);
            if (pvPin != null) {
                categoryPVmodletag.add(pvPin);
                ModlePin spPin = stringmodlePinsMap.get("sp" + i);
                if (spPin != null) {
                    categorySPmodletag.add(spPin);
                } else {
                    return false;
                }

            }
        }

        /**
         *ff
         * */
        for (int i = 1; i <= totalFf; ++i) {
            ModlePin ffPin = stringmodlePinsMap.get("ff" + i);

            if (ffPin != null) {
                ModlePin ffdown = stringmodlePinsMap.get("ffdown" + i);
                ModlePin ffup = stringmodlePinsMap.get("ffup" + i);
                if (ffdown != null && ffup != null) {
                    ffPin.setDownLmt(ffdown);
                    ffPin.setUpLmt(ffup);
                    categoryFFmodletag.add(ffPin);
                } else {
                    return false;
                }

            }

        }

        /**
         * mv mvfb,mvdown mvup
         * */
        for (int i = 1; i < totalMv; i++) {
            ModlePin mvPin = stringmodlePinsMap.get("mv" + i);
            if (mvPin != null) {
                ModlePin mvfbPin = stringmodlePinsMap.get("mvfb" + i);
                ModlePin mvupPin = stringmodlePinsMap.get("mvup" + i);
                ModlePin mvdownPin = stringmodlePinsMap.get("mvdown" + i);

                if (mvfbPin != null && mvupPin != null && mvdownPin != null) {
                    mvPin.setUpLmt(mvupPin);
                    mvPin.setDownLmt(mvdownPin);
                    mvPin.setFeedBack(mvfbPin);
                    categoryMVmodletag.add(mvPin);
                } else {
                    return false;
                }


            }
        }

        A_timeseriseMatrix = new Double[categoryPVmodletag.size()][categoryMVmodletag.size()][timeserise_N];
        /***
         * init A matrix
         * */
        for (int i = 0; i < categoryPVmodletag.size(); ++i) {

            for (int j = 0; j < categoryMVmodletag.size(); ++j) {
                Double[] initserise = new Double[timeserise_N];
                for (int index_N = 0; index_N < timeserise_N; index_N++) {
                    initserise[index_N] = 0d;
                }
                A_timeseriseMatrix[i][j] = initserise;
            }

        }

        /***
         * init B matrix
         * */
        B_timeseriseMatrix = new Double[categoryPVmodletag.size()][categoryFFmodletag.size()][timeserise_N];
        for (int i = 0; i < categoryPVmodletag.size(); ++i) {
            for (int j = 0; j < categoryFFmodletag.size(); ++j) {
                Double[] initserise = new Double[timeserise_N];
                for (int index_N = 0; index_N < timeserise_N; index_N++) {
                    initserise[index_N] = 0d;
                }
                B_timeseriseMatrix[i][j] = initserise;
            }

        }


        /***
         *fill respon into A matrix
         * */
        pvusemv=new int[categoryPVmodletag.size()][categoryMVmodletag.size()];
        for (int i = 0; i < categoryPVmodletag.size(); ++i) {
            for (int j = 0; j < categoryMVmodletag.size(); ++j) {

                for (ResponTimeSerise responTimeSerise : responTimeSerises) {
                    if (responTimeSerise.getInputPins().equals(categoryMVmodletag.get(j).getModlePinName()) && responTimeSerise.getOutputPins().equals(categoryPVmodletag.get(i).getModlePinName())) {
                        A_timeseriseMatrix[i][j] = responTimeSerise.responOneTimeSeries(timeserise_N, controlAPCOutCycle);
                        pvusemv[i][j]=1;
                        break;
                    }

                }

            }


        }

        /**
         *ill respon into B matrix
         *
         * */
        for (int i = 0; i < categoryPVmodletag.size(); ++i) {
            for (int j = 0; j < categoryFFmodletag.size(); ++j) {

                for (ResponTimeSerise responTimeSerise : responTimeSerises) {
                    if (responTimeSerise.getInputPins().equals(categoryFFmodletag.get(j).getModlePinName()) && responTimeSerise.getOutputPins().equals(categoryPVmodletag.get(i).getModlePinName())) {
                        B_timeseriseMatrix[i][j] = responTimeSerise.responOneTimeSeries(timeserise_N, controlAPCOutCycle);
                        break;
                    }

                }

            }

        }


        Q = new Double[categoryPVmodletag.size()];
        R = new Double[categoryMVmodletag.size()];
        alpheTrajectoryCoefficients=new Double[categoryPVmodletag.size()];
        int loop = 0;
        for (ModlePin pvpin : categoryPVmodletag) {
            Q[loop] = pvpin.getQ();
            alpheTrajectoryCoefficients[loop]=pvpin.getReferTrajectoryCoef();
            loop++;
        }
        loop = 0;
        for (ModlePin mvpin : categoryMVmodletag) {
            R[loop++] = mvpin.getR();
        }

        /***
         * 死区时间和漏洞初始值
         * */

        deadZones = new Double[categoryPVmodletag.size()];
        funelinitvalues = new Double[categoryPVmodletag.size()];
        loop=0;
        for (ModlePin modlePin : categoryPVmodletag) {
            deadZones[loop]=modlePin.getDeadZone();
            funelinitvalues[loop]=modlePin.getFunelinitValue();
            ++loop;
        }

        outpoints_p = categoryPVmodletag.size();
        feedforwardpoints_v = categoryFFmodletag.size();
        inputpoints_m = categoryMVmodletag.size();



        backPVPrediction=new double[outpoints_p][timeserise_N] ;//pv的预测曲线

        backPVFunelUp = new double[outpoints_p][timeserise_N];//PV的漏斗

        backPVFunelDown=new double[outpoints_p][timeserise_N];

        backDmvWrite =new double[inputpoints_m];//MV写入值

        backPVPredictionError=new double[outpoints_p];//预测误差

        validkey=System.currentTimeMillis();
        return true;
    }


    public JSONObject getrealData() {

        /**
         * y0(pv)
         * limitU(mv)
         * limitY()
         * detaFF
         * Wi(sp)
         *
         * */

        JSONObject jsonObject = new JSONObject();

        //sp
        Double[] sp = new Double[categorySPmodletag.size()];
        int loop = 0;
        for (ModlePin sppin : categorySPmodletag) {
            sp[loop] = sppin.modleGetReal();
            loop++;
        }
        jsonObject.put("wi", sp);

        //pv
        Double[] pv = new Double[categoryPVmodletag.size()];
        loop = 0;
        for (ModlePin pvpin : categoryPVmodletag) {
            /***
             * 是否有滤波器，有则使用滤波器的值，不然就使用实时数据
             * */
                pv[loop++] = pvpin.modleGetReal();

        }
        jsonObject.put("y0", pv);

        //limitU输入限制
        Double[][] limitU = new Double[categoryMVmodletag.size()][2];
        Double[][] limitDU = new Double[categoryMVmodletag.size()][2];
        loop = 0;
        //U执行器当前给定
        Double[] U = new Double[categoryMVmodletag.size()];
        //U执行器当前反馈
        Double[] UFB = new Double[categoryMVmodletag.size()];

        for (ModlePin mvpin : categoryMVmodletag) {


            Double[] mvminmax = new Double[2];
            ModlePin mvdown = mvpin.getDownLmt();
            ModlePin mvup = mvpin.getUpLmt();

            if (mvdown.getResource().equals("constant")) {
                mvminmax[0] = Double.valueOf(mvdown.getModleOpcTag());
            } else if (mvdown.getResource().equals("opc")) {
                mvminmax[0] = mvdown.modleGetReal();
            }

            if (mvup.getResource().equals("constant")) {
                mvminmax[1] = Double.valueOf(mvup.getModleOpcTag());
            } else if (mvup.getResource().equals("opc")) {
                mvminmax[1] = mvup.modleGetReal();
            }
            //执行器限制
            limitU[loop] = mvminmax;

            Double[] dmvminmax = new Double[2];
            dmvminmax[0]=mvpin.getDmvLow();
            dmvminmax[1]=mvpin.getDmvHigh();
            limitDU[loop]=dmvminmax;

            //执行器给定
            U[loop] = mvpin.modleGetReal();
            UFB[loop] = mvpin.getFeedBack().modleGetReal();
            loop++;

        }
        jsonObject.put("limitU", limitU);
        jsonObject.put("limitDU",limitDU);
        jsonObject.put("U", U);
        jsonObject.put("UFB", UFB);

        //FF
        loop = 0;
        if (categoryFFmodletag.size() != 0) {
            Double[] ff = new Double[categoryFFmodletag.size()];
            Double[] fflmt = new Double[categoryFFmodletag.size()];
            for (ModlePin ffpin : categoryFFmodletag) {
                ff[loop] = ffpin.modleGetReal();
                ModlePin ffuppin = ffpin.getUpLmt();
                ModlePin ffdownpin = ffpin.getDownLmt();

                Double ffHigh = 0d;
                Double ffLow = 0d;
                if (ffuppin.getResource().equals("opc")) {
                    ffHigh = ffuppin.modleGetReal();
                } else {
                    if (ffuppin.getResource().equals("constant")) {
                        ffHigh = Double.valueOf(ffuppin.getModleOpcTag());
                    }
                }


                if (ffdownpin.getResource().equals("opc")) {
                    ffLow = ffdownpin.modleGetReal();
                } else {
                    if (ffdownpin.getResource().equals("constant")) {
                        ffLow = Double.valueOf(ffdownpin.getModleOpcTag());
                    }
                }

                if ((ffLow <= ffpin.modleGetReal()) && (ffHigh >= ffpin.modleGetReal())) {
                    fflmt[loop] = 1d;
                } else {
                    fflmt[loop] = 0d;
                }

                loop++;
            }

            jsonObject.put("FF", ff);
            jsonObject.put("FFLmt", fflmt);

        }

        jsonObject.put("enable", getModleEnable());


        /**
         *死区时间和漏斗初始值
         * */
        jsonObject.put("deadZones",deadZones);
        jsonObject.put("funelInitValues",funelinitvalues);
        jsonObject.put("validekey",validkey);
        return jsonObject;

    }


    public void restart() {

    }

    public boolean writeData(Double[] values) {
        int loop = 0;
        boolean result = true;
        for (ModlePin mvpin : categoryMVmodletag) {
            mvpin.setWriteValue(values[loop]);
            result = result && OPCserver.writeTagvalue(mvpin.getModleOpcTag(), values[loop]);
            loop++;
        }
        return result;

    }

    /**
     * @param funelupAnddown 尺寸：2XpN
     *第0行存漏斗的上限；[0~N-1]第一个pv的，[N~2N-1]为第二个pv的漏斗上限
     *第1行存漏斗的下限；
     * @param backPVPrediction
     *
     * */
    public boolean updateModleReal(double[] backPVPrediction,double[][] funelupAnddown,double[] backDmvWrite,double[] backPVPredictionError){
        /**
         * 模型运算状态值
         * */

        for(int i=0;i<categoryPVmodletag.size();i++){
            this.backPVPrediction[i]=Arrays.copyOfRange(backPVPrediction,0+timeserise_N*i,timeserise_N+timeserise_N*i);//pv的预测曲线
            this.backPVFunelUp[i]=Arrays.copyOfRange(funelupAnddown[0],0+timeserise_N*i,timeserise_N+timeserise_N*i);//PV的漏斗上限
            this.backPVFunelDown[i]=Arrays.copyOfRange(funelupAnddown[1],0+timeserise_N*i,timeserise_N+timeserise_N*i);//PV的漏斗下限
        }
        this.backDmvWrite=backDmvWrite;//MV写入值
        this.backPVPredictionError=backPVPredictionError;//预测误差
        return true;
    }

    public void removeopctag(){
        for(ModlePin pin:modlePins){
            if((pin.getResource()!=null)&&(pin.getResource().equals("opc"))){
                OPCserver.unregister(pin);
            }

        }

    }


    public Integer getPredicttime_P() {
        return predicttime_P;
    }

    public void setPredicttime_P(Integer predicttime_P) {
        this.predicttime_P = predicttime_P;
    }

    public Integer getOutpoints_p() {
        return outpoints_p;
    }

    public void setOutpoints_p(Integer outpoints_p) {
        this.outpoints_p = outpoints_p;
    }

    public Integer getControltime_M() {
        return controltime_M;
    }

    public void setControltime_M(Integer controltime_M) {
        this.controltime_M = controltime_M;
    }

    public Integer getInputpoints_m() {
        return inputpoints_m;
    }

    public void setInputpoints_m(Integer inputpoints_m) {
        this.inputpoints_m = inputpoints_m;
    }

    public Integer getFeedforwardpoints_v() {
        return feedforwardpoints_v;
    }

    public void setFeedforwardpoints_v(Integer feedforwardpoints_v) {
        this.feedforwardpoints_v = feedforwardpoints_v;
    }

    public Integer getTimeserise_N() {
        return timeserise_N;
    }

    public void setTimeserise_N(Integer timeserise_N) {
        this.timeserise_N = timeserise_N;
    }

    public String getModleName() {
        return modleName;
    }

    public void setModleName(String modleName) {
        this.modleName = modleName;
    }


    public int getModleId() {
        return modleId;
    }

    public void setModleId(int modleId) {
        this.modleId = modleId;
    }


    public void setOPCserver(OPCService OPCserver) {
        this.OPCserver = OPCserver;
    }


    public Double[][][] getA_timeseriseMatrix() {
        return A_timeseriseMatrix;
    }

    public Double[][][] getB_timeseriseMatrix() {
        return B_timeseriseMatrix;
    }


    public void setExecutePythonBridge(ExecutePythonBridge executePythonBridge) {
        this.executePythonBridge = executePythonBridge;
    }

    public ExecutePythonBridge getExecutePythonBridge() {
        return executePythonBridge;
    }

    public Integer getControlAPCOutCycle() {
        return controlAPCOutCycle;
    }

    public void setControlAPCOutCycle(Integer controlAPCOutCycle) {
        this.controlAPCOutCycle = controlAPCOutCycle;
    }

    public int getModleEnable() {
        return modleEnable;
    }

    public void setModleEnable(int modleEnable) {
        this.modleEnable = modleEnable;
    }

    public List<ModlePin> getModlePins() {
        return modlePins;
    }

    public void setModlePins(List<ModlePin> modlePins) {
        this.modlePins = modlePins;
    }

    public List<ResponTimeSerise> getResponTimeSerises() {
        return responTimeSerises;
    }

    public void setResponTimeSerises(List<ResponTimeSerise> responTimeSerises) {
        this.responTimeSerises = responTimeSerises;
    }

    public List<ModlePin> getCategoryPVmodletag() {
        return categoryPVmodletag;
    }

    public List<ModlePin> getCategorySPmodletag() {
        return categorySPmodletag;
    }

    public List<ModlePin> getCategoryMVmodletag() {
        return categoryMVmodletag;
    }

    public List<ModlePin> getCategoryFFmodletag() {
        return categoryFFmodletag;
    }

    public Double[] getQ() {
        return Q;
    }

    public Double[] getR() {
        return R;
    }

    public BaseConf getBaseConf() {
        return baseConf;
    }

    public void setBaseConf(BaseConf baseConf) {
        this.baseConf = baseConf;
        totalFf = baseConf.getFf();
        totalMv = baseConf.getMv();
        totalPv = baseConf.getPv();
    }

    public double[][] getBackPVPrediction() {
        return backPVPrediction;
    }

    public double[][] getBackPVFunelUp() {
        return backPVFunelUp;
    }

    public double[][] getBackPVFunelDown() {
        return backPVFunelDown;
    }

    public double[] getBackDmvWrite() {
        return backDmvWrite;
    }

    public double[] getBackPVPredictionError() {
        return backPVPredictionError;
    }



    public ModlePin getAutoEnbalePin() {
        return autoEnbalePin;
    }

    public int[][] getPvusemv() {
        return pvusemv;
    }

    public void setPvusemv(int[][] pvusemv) {
        this.pvusemv = pvusemv;
    }

    public long getValidkey() {
        return validkey;
    }

    public void setValidkey(long validkey) {
        this.validkey = validkey;
    }

    public Double[] getAlpheTrajectoryCoefficients() {
        return alpheTrajectoryCoefficients;
    }

    public void setAlpheTrajectoryCoefficients(Double[] alpheTrajectoryCoefficients) {
        this.alpheTrajectoryCoefficients = alpheTrajectoryCoefficients;
    }
}

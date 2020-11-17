package hs.Bean;

import com.alibaba.fastjson.JSONObject;
import hs.ApcAlgorithm.ExecutePythonBridge;
import hs.Controller.ModleController;
import hs.Opc.OpcServicConstainer;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/18 8:24
 */
public class ControlModle implements Modle {
    public static Logger logger = Logger.getLogger(ModleController.class);
    public static final Integer RUNSTYLEBYAUTO=0;//运行方式0-自动分配模式 1-手动分配模式
    public static final Integer RUNSTYLEBYMANUL=1;//1-手动分配模式
    /**
     * 模型定义
     */
    private int modleId;//模型id主键
    private String modleName;//模型名称
    private Integer predicttime_P = 12;//预测时域
    private Integer controltime_M = 6;//单一控制输入未来控制M步增量(控制域)
    private Integer timeserise_N = 40;//响应序列长度
    private Integer controlAPCOutCycle = 0;//控制周期
    private volatile int modleEnable = 0;//模块使能，用于设置算法是否运行，算法是否运行
    private Integer runstyle=0;//运行方式0-自动分配模式 1-手动分配模式
    private List<ModlePin> modlePins;//引脚,from db
    private List<ResponTimeSerise> responTimeSerises;//响应 from db


    /**
     * 模型的标识token，用于apc算法判别自己的算法是否已经过时，需要停止
     */
    private long validkey = System.currentTimeMillis();
    /**
     * 模型各个类型引脚数量
     */
    private int totalPv = 8;
    private int totalFf = 8;
    private int totalMv = 8;

    /**模型真实运行状态*/
    /**
     * apc反馈的y0的预测值
     */
    private double[][] backPVPrediction;//pv的预测曲线
    /**
     * apc反馈的pv的漏斗的上边界
     */
    private double[][] backPVFunelUp;//PV的漏斗上限
    /**
     * apc反馈的pv的漏斗的下边界
     */
    private double[][] backPVFunelDown;//PV的漏斗下限
    /**
     * apc反馈的dmv增量的预测值shape=(p,m)
     */
    private double[][] backDmvWrite;
    private double[] backrawDmv;
    private double[] backrawDff;
    /**
     * apc反馈的y0与yreal的error预测值
     */
    private double[] backPVPredictionError;//预测误差

    /**
     * 模型计算时候的前馈变换值dff shape=(p,num_ff)
     */
    private double[][] backDff;


    private OpcServicConstainer opcServicConstainer;//opcserviceconstainer
    private BaseConf baseConf;//控制器的基本配置，在数据库中所定义，进行注入

    /**
     * 执行apc算法的桥接器
     */
    private ExecutePythonBridge executePythonBridge;


    /**
     * 原始多目标pv输出数量
     */
    private Integer baseoutpoints_p = 0;//输出个数量
    /**
     * 可运行的的pv引脚
     */
    private Integer numOfRunnablePVPins_pp = 0;

    /**
     * 原始前馈数量
     */
    private Integer basefeedforwardpoints_v = 0;
    /**
     * 对应可运行的pv引脚的可运行ff引脚数量
     */
    private Integer numOfRunnableFFpins_vv = 0;

    /**
     * 原始可控制输入数量
     */
    private Integer baseinputpoints_m = 0;
    /**
     * 对应可运行pv引脚所用的可运行mv引脚数量
     */
    private Integer numOfRunnableMVpins_mm = 0;

    private List<ModlePin> categoryPVmodletag = new ArrayList<>();//已经分类号的PV引脚
    private List<ModlePin> categorySPmodletag = new ArrayList<>();//已经分类号的SP引脚
    private List<ModlePin> categoryMVmodletag = new ArrayList<>();//已经分类号的MV引脚
    private List<ModlePin> categoryFFmodletag = new ArrayList<>();//已经分类号的FF引脚
    private ModlePin autoEnbalePin = null;//dcs手自动切换引脚


    private double[][][] A_RunnabletimeseriseMatrix = null;//输入响应 shape=[pv][mv][resp_N]
    private double[][][] B_RunnabletimeseriseMatrix = null;//前馈响应 shape=[pv][ff][resp_N]
    private Double[] Q = null;//误差权重矩阵
    private Double[] R = null;//控制权矩阵、正则化
    private Double[] alpheTrajectoryCoefficients = null;//参考轨迹的柔化系数


    private String[] alpheTrajectoryCoefmethod = null;//参考轨迹的柔化系数方法
    private Double[] deadZones = null;//漏斗死区
    private Double[] funelinitvalues = null;//漏斗初始值
    private double[][] funneltype;


    /**
     * 仿真模型
     */
    private SimulatControlModle simulatControlModle;
    /**
     * 仿真器地址
     */
    private String simulatorbuilddir;
    /**
     * 上一次仿真器工作状态
     */

    /**
     * DB模型配置了mv1 mv2 mv3.....mvn
     * pv1  1
     * pv2  1
     * pv3
     * pv4
     * ...
     * pvn
     * <p>
     * 指示PV用了哪几个mv
     * pvusemv矩阵shape=(num_pv,num_mv)
     * 如：pv1用了mv1,pv2用了mv1
     * 如[[1,0]，
     * [0,1]]
     */
    private int[][] maskBaseMapPvUseMvMatrix = null;

    /**
     * 基本pv对mv的基本作用比例
     * **/
    private float[][] maskBaseMapPvEffectMvMatrix = null;



    /**
     * 激活的pv引脚对应的mv
     */
    private int[][] maskMatrixRunnablePVUseMV = null;


    /**
     * 基本可运行的pv对mv的基本作用比例
     * **/
    private float[][] maskMatrixRunnablePvEffectMv = null;


    /**
     * DB模型配置表示Pv使用了哪些ff
     * pvuseff矩阵shape=(num_pv,num_ff)
     * 如pv1用了ff1,pv2用了ff2
     * 如[[1,0],
     * [0,1]]
     */
    private int[][] maskBaseMapPvUseFfMatrix = null;
    /**
     * 激活的pv对应的ff
     */
    private int[][] maskMatrixRunnablePVUseFF = null;


    /**
     * 本次参与控制的FF引脚的标记矩阵，在内容为1的地方就说明改引脚被引用了
     */
    int[] maskisRunnableFFMatrix = null;
    /**
     * 本次参与控制的PV引脚的标记矩阵，在内容为1的地方就说明改引脚被引用了
     */
    int[] maskisRunnablePVMatrix = null;
    /**
     * 本次参与控制的MV引脚的标记矩阵，在内容为1的地方就说明改引脚被引用了
     */
    int[] maskisRunnableMVMatrix = null;

    /**apc程序位置*/
    String apcdir;

    private Map<String, ModlePin> stringmodlePinsMap = new HashMap<>();//方便引脚索引key=pv1.mv2,sp1,ff1等 value=引脚类

    public ControlModle() {
    }


    public ControlModle(OpcServicConstainer opcServicConstainer, BaseConf baseConf, String simulatordir) {
        this.setOpcServicConstainer(opcServicConstainer);
        this.setBaseConf(baseConf);
        this.setSimulatorbuilddir(simulatordir);
        this.totalFf = baseConf.getFf();
        this.totalMv = baseConf.getMv();
        this.totalPv = baseConf.getPv();

    }

    /**
     * 初始化 ControlModle的重要属性，使其成为真正的ControlModle
     */
    public void toBeRealControlModle(String apcdir,OpcServicConstainer opcServicConstainer, BaseConf baseConf, String simulatordir) {
        this.opcServicConstainer = opcServicConstainer;
        this.baseConf = baseConf;
        this.simulatorbuilddir = simulatordir;
        /***
         * 可以新建的引脚的数量
         * */
        this.totalFf = baseConf.getFf();
        this.totalMv = baseConf.getMv();
        this.totalPv = baseConf.getPv();
        this.apcdir=apcdir;
    }

    /**
     * 需要检查是否在运行状态，不在运行状态才运行 模型运行
     */
    public void modleCheckStatusRun() {
        if (getModleEnable() == 0) {
            generateValidkey();
            executePythonBridge.execute();
            setModleEnable(1);
        }
    }


    /**
     * 模型停止
     */
    public void modleCheckStatusStop() {
        if (getModleEnable() == 1) {
            generateValidkey();
            executePythonBridge.stop();
            setModleEnable(0);
        }
    }


    /**
     * 1、引脚注册进引脚的map中，key=pvn/mvn/spn等(n=1,2,3..8) value=pin
     * 2将引脚进行分类，按照顺序单独存储到各自类别的list，ex:categoryPVmodletag内存储pv的引脚
     *
     * @return false 对应位号不完整
     */
    private boolean classAndCombineRegiterPinsToMap() {
        /**将定义的引脚按照key=pvn/mvn/spn等(n=1,2,3..8) value=pin 放进索引*/

        for (ModlePin modlePin : modlePins) {
            stringmodlePinsMap.put(modlePin.getModlePinName(), modlePin);
        }

        /**分类引脚*/
        for (int i = 1; i <= totalPv; i++) {
            ModlePin pvPin = stringmodlePinsMap.get(ModlePin.TYPE_PIN_PV + i);
            if (pvPin != null) {
                categoryPVmodletag.add(pvPin);
                ModlePin dcsEnablepin = stringmodlePinsMap.get(ModlePin.TYPE_PIN_PIN_PVENABLE + i);
                if (dcsEnablepin != null) {
                    pvPin.setDcsEnabePin(dcsEnablepin);//dcs作用用于模型是否启用
                }

                ModlePin pvdown = stringmodlePinsMap.get(ModlePin.TYPE_PIN_PVDOWN + i);
                ModlePin pvup = stringmodlePinsMap.get(ModlePin.TYPE_PIN_PVUP + i);
                /**没有的话也不强制退出*/
                if ((null == pvdown) || (null == pvup)) {
                    logger.warn("modleid=" + modleId + ",存在pv的置信区间不完整");
                }
                pvPin.setDownLmt(pvdown);//置信区间下限值位号
                pvPin.setUpLmt(pvup);//置信区间上限值位号


                ModlePin spPin = stringmodlePinsMap.get(ModlePin.TYPE_PIN_SP + i);//目标值sp位号
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
            ModlePin ffPin = stringmodlePinsMap.get(ModlePin.TYPE_PIN_FF + i);

            if (ffPin != null) {
                ModlePin dcsEnablepin = stringmodlePinsMap.get(ModlePin.TYPE_PIN_PIN_FFENABLE + i);
                if (dcsEnablepin != null) {
                    ffPin.setDcsEnabePin(dcsEnablepin);//dcs作用用于模型是否启用
                }
                ModlePin ffdown = stringmodlePinsMap.get(ModlePin.TYPE_PIN_FFDOWN + i);
                ModlePin ffup = stringmodlePinsMap.get(ModlePin.TYPE_PIN_FFUP + i);
                ffPin.setDownLmt(ffdown);//置信区间下限值位号
                ffPin.setUpLmt(ffup);//置信区间上限值位号
                categoryFFmodletag.add(ffPin);
                if ((null == ffdown) || (null == ffup)) {
                    logger.warn("modleid=" + modleId + ",存在ff的置信区间不完整");
                }
            }

        }

        /**
         * mv mvfb,mvdown mvup
         * */
        for (int i = 1; i < totalMv; i++) {
            ModlePin mvPin = stringmodlePinsMap.get(ModlePin.TYPE_PIN_MV + i);

            if (mvPin != null) {

                ModlePin dcsEnablepin = stringmodlePinsMap.get(ModlePin.TYPE_PIN_PIN_MVENABLE + i);
                if (dcsEnablepin != null) {
                    mvPin.setDcsEnabePin(dcsEnablepin);//dcs作用用于模型是否启用
                }

                ModlePin mvfbPin = stringmodlePinsMap.get(ModlePin.TYPE_PIN_MVFB + i);
                ModlePin mvupPin = stringmodlePinsMap.get(ModlePin.TYPE_PIN_MVUP + i);
                ModlePin mvdownPin = stringmodlePinsMap.get(ModlePin.TYPE_PIN_MVDOWN + i);
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
        return true;
    }


    /**
     * 1将点号注册进opcserve
     * 2设置模型的的以dcs来控制的模型run/stop的引脚
     */
    private void firstTimeRegiterPinsToOPCServe() {
        for (ModlePin modlePin : modlePins) {
            /** 注册opc点号
             * 将引脚注册进行opcserice中
             * */
            if ((opcServicConstainer.getOPcserveGroup() != null) && (modlePin != null)) {
                opcServicConstainer.registerModlePinAndComponent(modlePin);
            } else {
                logger.warn("modle id=" + modleId + " pinid=" + modlePin.getModlepinsId() + " build failed, because opc group is null");
            }

            /**
             * 手自动切换引脚提取
             * */
            if ((modlePin != null) && (modlePin.getModlePinName().equals(ModlePin.TYPE_PIN_MODLE_AUTO))) {
                autoEnbalePin = modlePin;
            }


        }
    }


    /**
     * 初始化maskRunnablePVMatrix  maskRunnableMVMatrix  maskRunnableFFMatrix maskBaseMapPvEffectMvMatrix
     */
    private void initRunnableMatrixAndBaseMapMatrix() {
        for (int indexpv = 0; indexpv < categoryPVmodletag.size(); ++indexpv) {
            /**pv引脚启用，并且参与本次控制*/
            if (isThisTimeRunnablePin(categoryPVmodletag.get(indexpv))) {
                maskisRunnablePVMatrix[indexpv] = 1;
            }

            /**1\marker total pvusemv
             * 2\marker participate mv
             * */
            for (int indexmv = 0; indexmv < categoryMVmodletag.size(); ++indexmv) {
                ResponTimeSerise ismapping = isPVMappingMV(categoryPVmodletag.get(indexpv).getModlePinName(), categoryMVmodletag.get(indexmv).getModlePinName());
                maskBaseMapPvUseMvMatrix[indexpv][indexmv] = (null != ismapping ? 1 : 0);
                maskBaseMapPvEffectMvMatrix[indexpv][indexmv]=(null!=ismapping?ismapping.getEffectRatio():0f);
                /**1是否有映射关系、2、pv是否启用 3mv是否启用*/
                if ((null != ismapping) && isThisTimeRunnablePin(categoryPVmodletag.get(indexpv)) && isThisTimeRunnablePin(categoryMVmodletag.get(indexmv))) {
                    maskisRunnableMVMatrix[indexmv] = 1;
                }
            }

            /**1\marker total pvuseff
             * 2\marker participate ff
             * */
            for (int indexff = 0; indexff < categoryFFmodletag.size(); ++indexff) {
                ResponTimeSerise ismapping = isPVMappingFF(categoryPVmodletag.get(indexpv).getModlePinName(), categoryFFmodletag.get(indexff).getModlePinName());
                maskBaseMapPvUseFfMatrix[indexpv][indexff] = (null != ismapping ? 1 : 0);
                if ((null != ismapping) && isThisTimeRunnablePin(categoryPVmodletag.get(indexpv)) && isThisTimeRunnablePin(categoryFFmodletag.get(indexff))) {
                    maskisRunnableFFMatrix[indexff] = 1;
                }
            }
        }
    }


    /**
     * 1统计参与本次runnbale的pv
     * 2统计runnbale的pv的 runnbale mv的数量
     * 3统计参与runnbale的pv的runnbale ff的数量
     */
    private void initStatisticRunnablePinNum() {

        /**统计runnbale的pv的mv的数量*/
        for (int pvi : maskisRunnablePVMatrix) {
            if (1 == pvi) {
                ++numOfRunnablePVPins_pp;
            }
        }

        /**统计runnbale的pv的mv的数量*/
        for (int mvi : maskisRunnableMVMatrix) {
            if (1 == mvi) {
                ++numOfRunnableMVpins_mm;
            }
        }
        /**统计runnbale的pv的ff的数量*/
        for (int ffi : maskisRunnableFFMatrix) {
            if (1 == ffi) {
                ++numOfRunnableFFpins_vv;
            }
        }

    }


    /**
     * 初始化pv有关的参数
     * Q预测域参数
     * alpheTrajectoryCoefficients轨迹软化系统
     * deadZones死区
     * funelinitvalues漏斗初始值
     * funneltype漏斗类型
     **/

    private void initPVparams() {

        List<ModlePin> runablePVPins = getRunablePins(categoryPVmodletag, maskisRunnablePVMatrix);
        List<ModlePin> runableMVPins = getRunablePins(categoryMVmodletag, maskisRunnableMVMatrix);

        int looppv = 0;
        for (ModlePin runpvpin : runablePVPins) {
            Q[looppv] = runpvpin.getQ();
            alpheTrajectoryCoefficients[looppv] = runpvpin.getReferTrajectoryCoef();
            alpheTrajectoryCoefmethod[looppv]=runpvpin.getTracoefmethod();
            deadZones[looppv] = runpvpin.getDeadZone();
            funelinitvalues[looppv] = runpvpin.getFunelinitValue();
            double[] fnl = new double[2];
            if (runpvpin.getFunneltype() != null) {
                switch (runpvpin.getFunneltype()) {
                    case ModlePin.TYPE_FUNNEL_FULL:
                        fnl[0] = 0d;
                        fnl[1] = 0d;
                        funneltype[looppv] = fnl;
                        break;
                    case ModlePin.TYPE_FUNNEL_UP:
                        fnl[0] = 0;
                        //乘负无穷
                        fnl[1] = 1;
                        funneltype[looppv] = fnl;
                        break;
                    case ModlePin.TYPE_FUNNEL_DOWN:
                        //乘正无穷
                        fnl[0] = 1;
                        fnl[1] = 0;
                        funneltype[looppv] = fnl;
                        break;
                    default:
                        fnl[0] = 0;
                        fnl[1] = 0;
                        funneltype[looppv] = fnl;
                }
            } else {
                //匹配不到就是全漏斗
                fnl[0] = 0;
                fnl[1] = 0;
                funneltype[looppv] = fnl;
            }


            int loopmv = 0;
            for (ModlePin runmvpin : runableMVPins) {

                /**查找映射关系*/
                ResponTimeSerise responTimeSerisePVMV = isPVMappingMV(runpvpin.getModlePinName(), runmvpin.getModlePinName());
                if (responTimeSerisePVMV != null) {
                    A_RunnabletimeseriseMatrix[looppv][loopmv] = responTimeSerisePVMV.responOneTimeSeries(timeserise_N, controlAPCOutCycle);
                    maskMatrixRunnablePVUseMV[looppv][loopmv] = 1;
                    maskMatrixRunnablePvEffectMv[looppv][loopmv]=responTimeSerisePVMV.getEffectRatio();
                }

                ++loopmv;
            }

            ++looppv;
        }

    }

    /**
     * 累计可运行的pv和mv的映射关系数量
     */
    private void accumulativeNumOfRunnablePVMVMaping() {
        for (int p = 0; p < numOfRunnablePVPins_pp; ++p) {
            for (int m = 0; m < numOfRunnableMVpins_mm; ++m) {
                if (1 == maskMatrixRunnablePVUseMV[p][m]) {
                    simulatControlModle.addNumOfIOMappingRelation();
                }
            }

        }
    }


    private void initRMatrix() {
        int indevEnableMV = 0;
        for (ModlePin runmv : getRunablePins(categoryMVmodletag, maskisRunnableMVMatrix)) {
            R[indevEnableMV] = runmv.getR();
            ++indevEnableMV;
        }
    }


    private void initFFparams() {

        List<ModlePin> runablePVPins = getRunablePins(categoryPVmodletag, maskisRunnablePVMatrix);//获取可运行的pv引脚
        List<ModlePin> runableFFPins = getRunablePins(categoryFFmodletag, maskisRunnableFFMatrix);//获取可运行的ff引脚

        int looppv = 0;
        for (ModlePin runpv : runablePVPins) {

            int loopff = 0;
            for (ModlePin runff : runableFFPins) {

                ResponTimeSerise responTimeSerisePVFF = isPVMappingFF(runpv.getModlePinName(), runff.getModlePinName());

                if (responTimeSerisePVFF != null) {
                    B_RunnabletimeseriseMatrix[looppv][loopff] = responTimeSerisePVFF.responOneTimeSeries(timeserise_N, controlAPCOutCycle);
                    maskMatrixRunnablePVUseFF[looppv][loopff] = 1;
                }
                ++loopff;
            }
            ++looppv;
        }
    }


    /**
     * 因脱离置信区间，设置PV为不可运行
     */
    public synchronized void disRunnablePinByDCS(ModlePin pin) {

        /**运行，需要进行停止运行，*/
        pin.setThisTimeParticipate(false);

        generateValidkey();
        simulatControlModle.generateSimulatevalidkey();

        modleBuild(false);

        if (1 == modleEnable) {
            executePythonBridge.stop();
            executePythonBridge.execute();
        }
        if (simulatControlModle.isIssimulation()) {
            simulatControlModle.getExecutePythonBridgeSimulate().stop();
            simulatControlModle.getExecutePythonBridgeSimulate().execute();
        }
        logger.info("DCS控制：模型id=" + getModleId() + " pinid=" + pin.getModlepinsId() + "设置为不可运行");

    }

    /**
     * 因置信区间恢复将pv设置为可运行
     */
    public synchronized void runnablePinByDCS(ModlePin pin) {

        /**没有运行，需要进行运行，*/
        pin.setThisTimeParticipate(true);

        generateValidkey();
        simulatControlModle.generateSimulatevalidkey();

        modleBuild(false);

        if (1 == modleEnable) {
            executePythonBridge.stop();
            executePythonBridge.execute();
        }
        if (simulatControlModle.isIssimulation()) {
            simulatControlModle.getExecutePythonBridgeSimulate().stop();
            simulatControlModle.getExecutePythonBridgeSimulate().execute();
        }
        logger.info("DCS控制：模型id=" + getModleId() + " pinid=" + pin.getModlepinsId() + "设置为可运行");
    }

    /**
     * 设置引脚启用
     */
    public synchronized void disablePinByDCS(ModlePin pin) {
        if (pin.getPinEnable() == 1) {
            pin.setPinEnable(0);

            generateValidkey();
            simulatControlModle.generateSimulatevalidkey();

            modleBuild(false);

            if (1 == modleEnable) {
                executePythonBridge.stop();
                executePythonBridge.execute();
            }

            if (simulatControlModle.isIssimulation()) {
                simulatControlModle.getExecutePythonBridgeSimulate().stop();
                simulatControlModle.getExecutePythonBridgeSimulate().execute();
            }
            logger.info("DCS控制：模型id=" + getModleId() + " pinid=" + pin.getModlepinsId() + "停用");
        } else {
            logger.info("DCS控制：模型id=" + getModleId() + " pinid=" + pin.getModlepinsId() + "本来就已经停用");
        }
    }

    /**
     * 将设置引脚为不启用
     */
    public synchronized void enablePinByDCS(ModlePin pin) {
        if (pin.getPinEnable() == 0) {
            /**没有启用，需要进行启用，*/
            pin.setPinEnable(1);

            generateValidkey();
            simulatControlModle.generateSimulatevalidkey();

            modleBuild(false);

            if (1 == modleEnable) {
                executePythonBridge.stop();
                executePythonBridge.execute();
            }
            if (simulatControlModle.isIssimulation()) {
                simulatControlModle.getExecutePythonBridgeSimulate().stop();
                simulatControlModle.getExecutePythonBridgeSimulate().execute();
            }
            logger.info("DCS控制：模型id=" + getModleId() + " pinid=" + pin.getModlepinsId() + "启用");
        } else {
            logger.info("DCS控制：模型id=" + getModleId() + " pinid=" + pin.getModlepinsId() + "本来就已经启用");
        }
    }


    public synchronized void enableModleByDCS() {
        if (0 == modleEnable) {

            generateValidkey();
            simulatControlModle.generateSimulatevalidkey();

            modleEnable = 1;
            simulatControlModle.setIssimulation(true);


            modleBuild(false);

            if(!executePythonBridge.execute()){
               // modleEnable=0;
            }
           if(!simulatControlModle.getExecutePythonBridgeSimulate().execute()){
               //simulatControlModle.setIssimulation(false);
           }


            logger.info("DCS contrl: modle id=" + modleId + "run complet");
        } else {
            logger.info("DCS contrl: modle id=" + modleId + "already run status");
        }
    }

    public synchronized void disEnableModleByDCS() {
        if (1 == modleEnable) {

            /*刷新验证码*/
            generateValidkey();
            simulatControlModle.generateSimulatevalidkey();

            modleEnable = 0;
            simulatControlModle.setIssimulation(false);

            if(!executePythonBridge.stop()){
                //modleEnable=1;
            };
            if(!simulatControlModle.getExecutePythonBridgeSimulate().stop()){
                //simulatControlModle.setIssimulation(true);
            }
            logger.error("DCS contrl: modle id=" + modleId + "stop complet");
        } else {
            logger.error("DCS contrl: modle id=" + modleId + "already stop");
        }

    }


    public synchronized void enableModleByWeb() {

        if (0 == modleEnable) {

            generateValidkey();
            simulatControlModle.generateSimulatevalidkey();

            modleEnable = 1;
            simulatControlModle.setIssimulation(true);

            modleBuild(false);

            if(!executePythonBridge.execute()){
               //modleEnable=0;
                logger.warn("web contrl:modle id=" + modleId + "run failed");
            }
            if(!simulatControlModle.getExecutePythonBridgeSimulate().execute()){
                //simulatControlModle.setIssimulation(false);
                logger.warn("web contrl:modle id=" + modleId + "run simulate failed");
            }


            logger.info("web contrl:modle id=" + modleId + "run execute conplete");
        } else {
            logger.info("web contrl:modle id=" + modleId + "already run status");
        }

    }

    public synchronized void disableModleByWeb() {
        if (1 == modleEnable) {

            generateValidkey();
            simulatControlModle.generateSimulatevalidkey();

            modleEnable = 0;
            simulatControlModle.setIssimulation(false);

            if(!executePythonBridge.stop()){
//                modleEnable=1;
                logger.warn("web contrl:modle id=" + modleId + "stop failed");
            };
            if(!simulatControlModle.getExecutePythonBridgeSimulate().stop()){
//                simulatControlModle.setIssimulation(true);
                logger.warn("web contrl:modle id=" + modleId + "stop simulte failed");
            }
            logger.info("web contrl:modle id=" + modleId + "stop execute complet");
        } else {
            logger.error("web contrl:modle id=" + modleId + "already stop");
        }
    }


    /**停止仿真*/
    public synchronized void disablesimulateModleByWeb() {
        if (simulatControlModle.isIssimulation()) {

            simulatControlModle.generateSimulatevalidkey();

            simulatControlModle.setIssimulation(false);

            if(!simulatControlModle.getExecutePythonBridgeSimulate().stop()){
                //simulatControlModle.setIssimulation(true);
            }

            logger.info("web contrl: simulate modle id=" + modleId + "stop complet");
        } else {
            logger.error("web contrl: simulate modle id=" + modleId + "already stop");
        }
    }
    /**开始仿真*/
    public synchronized void enablesimulateModleByWeb() {
        if (!simulatControlModle.isIssimulation()) {

            simulatControlModle.generateSimulatevalidkey();

            simulatControlModle.setIssimulation(true);

            if(!simulatControlModle.getExecutePythonBridgeSimulate().execute()){
                //simulatControlModle.setIssimulation(false);
            }
            logger.error("web contrl: simulate modle id=" + modleId + "run complet");
        } else {
            logger.error("web contrl: simulate modle id=" + modleId + "already run");
        }
    }


    /**
     * 设置引脚不启用
     */
    public synchronized void disablePinByWeb(int pinid) {

        ModlePin pin=null;
        for(ModlePin searchpin:modlePins){
            if(searchpin.getModlepinsId()==pinid){
                pin=searchpin;
                break;
            }
        }
        if(pin==null){
            return;
        }

        if (pin.getPinEnable() == 1) {
            pin.setPinEnable(0);

            generateValidkey();
            simulatControlModle.generateSimulatevalidkey();

            modleBuild(false);

            if (1 == modleEnable) {
                executePythonBridge.stop();
                executePythonBridge.execute();
            }

            if (simulatControlModle.isIssimulation()) {
                simulatControlModle.getExecutePythonBridgeSimulate().stop();
                simulatControlModle.getExecutePythonBridgeSimulate().execute();
            }
            logger.info("web contrl:modle id=" + getModleId() + " pinid=" + pin.getModlepinsId() + "disable");
        } else {
            logger.info("web contrl:modle id=" + getModleId() + " pinid=" + pin.getModlepinsId() + "already disable");
        }
    }

    /**
     * 将设置引脚为启用
     */
    public synchronized void enablePinByWeb(int pinid) {


        ModlePin pin=null;
        for(ModlePin searchpin:modlePins){
            if(searchpin.getModlepinsId()==pinid){
                pin=searchpin;
                break;
            }
        }
        if(pin==null){
            return;
        }



        if (pin.getPinEnable() == 0) {
            /**没有启用，需要进行启用，*/
            pin.setPinEnable(1);

            generateValidkey();
            simulatControlModle.generateSimulatevalidkey();

            modleBuild(false);

            if (1 == modleEnable) {
                executePythonBridge.stop();
                executePythonBridge.execute();
            }
            if (simulatControlModle.isIssimulation()) {
                simulatControlModle.getExecutePythonBridgeSimulate().stop();
                simulatControlModle.getExecutePythonBridgeSimulate().execute();
            }
            logger.info("web contrl:id=" + getModleId() + " pinid=" + pin.getModlepinsId() + " enable");
        } else {
            logger.info("web contrl:id=" + getModleId() + " pinid=" + pin.getModlepinsId() + " already enable");
        }
    }


    /**
     * 获取标记组数不为0位置上的引脚
     *
     * @param categorypins 待提取引脚
     * @param maskmatrix   标记数组
     */
    public List<ModlePin> getRunablePins(List<ModlePin> categorypins, int[] maskmatrix) {
        List<ModlePin> result = new LinkedList<>();
        for (int indexpin = 0; indexpin < categorypins.size(); ++indexpin) {
            if (1 == maskmatrix[indexpin]) {
                result.add(categorypins.get(indexpin));
            }
        }
        return result;
    }

    /**
     * 判断引脚是否启用，并且本次参与控制
     */
    private boolean isThisTimeRunnablePin(ModlePin pin) {
        /**启用，并且本次参与控制*/
        if ((1 == pin.getPinEnable() && (pin.isThisTimeParticipate()))) {
            return true;
        }
        return false;
    }

    /**
     * 模型构建函数
     *
     * @param isfirsttime 如果是第一次构建，那么需要重新重新分类引脚并注册进opcserv
     */
    public synchronized Boolean modleBuild(boolean isfirsttime) {
        logger.info("modle id=" + modleId + " is building");

        try {
            if (isfirsttime) {
                /**将引脚进行分类*/
                if (!classAndCombineRegiterPinsToMap()) {
                    return false;
                }
                /**第一次将点号注册进lopcserve*/
                firstTimeRegiterPinsToOPCServe();
            }

            /**新建仿真器
             * 吧仿真器之前运行状态记录下来
             * */
            if(simulatControlModle!=null){
                boolean lasttimesimulatstatus=simulatControlModle.isIssimulation();
                simulatControlModle = new SimulatControlModle(controltime_M, predicttime_P, timeserise_N, controlAPCOutCycle, simulatorbuilddir, modleId);
                simulatControlModle.setIssimulation(lasttimesimulatstatus);
            }else {
                simulatControlModle = new SimulatControlModle(controltime_M, predicttime_P, timeserise_N, controlAPCOutCycle, simulatorbuilddir, modleId);
            }


            /**init pvusemv and pvuseff matrix*/
            maskBaseMapPvUseMvMatrix = new int[categoryPVmodletag.size()][categoryMVmodletag.size()];
            maskBaseMapPvUseFfMatrix = new int[categoryPVmodletag.size()][categoryFFmodletag.size()];

            /**pv对mv的作用关系*/
            maskBaseMapPvEffectMvMatrix=new float[categoryPVmodletag.size()][categoryMVmodletag.size()];

            maskisRunnableMVMatrix = new int[categoryMVmodletag.size()];
            maskisRunnableFFMatrix = new int[categoryFFmodletag.size()];
            maskisRunnablePVMatrix = new int[categoryPVmodletag.size()];

            initRunnableMatrixAndBaseMapMatrix();

            numOfRunnablePVPins_pp = 0;
            numOfRunnableMVpins_mm = 0;
            numOfRunnableFFpins_vv = 0;
            initStatisticRunnablePinNum();

            logger.debug("p="+numOfRunnablePVPins_pp+" ,m="+numOfRunnableMVpins_mm+" ,v="+numOfRunnableFFpins_vv);

            /***
             * 输入输出响应对应矩阵
             * init A matrix
             * */
            A_RunnabletimeseriseMatrix = new double[numOfRunnablePVPins_pp][numOfRunnableMVpins_mm][timeserise_N];


            /**可运行的pv和mv的作用比例矩阵*/
            maskMatrixRunnablePvEffectMv=new float[numOfRunnablePVPins_pp][numOfRunnableMVpins_mm];


            /***
             *1、fill respon into A matrix
             *2、and init matrixEnablePVUseMV
             * */

            /**predict zone params*/
            Q = new Double[numOfRunnablePVPins_pp];//use for pv
            /**trajectry coefs*/
            alpheTrajectoryCoefficients = new Double[numOfRunnablePVPins_pp];//use for pv
            alpheTrajectoryCoefmethod=new String[numOfRunnablePVPins_pp];
            /**死区时间和漏洞初始值*/
            deadZones = new Double[numOfRunnablePVPins_pp];//use for pv
            funelinitvalues = new Double[numOfRunnablePVPins_pp];//use for pv
            /**funnel type*/
            funneltype = new double[numOfRunnablePVPins_pp][2];//use for pv


            maskMatrixRunnablePVUseMV = new int[numOfRunnablePVPins_pp][numOfRunnableMVpins_mm];//recording enablepv use which mvs

            initPVparams();

            /**累计映射关系*/
            accumulativeNumOfRunnablePVMVMaping();


            /**init R control zone params*/
            R = new Double[numOfRunnableMVpins_mm];//use for mv
            initRMatrix();

            /***
             * 前馈输出对应矩阵
             * init B matrix
             * */
            B_RunnabletimeseriseMatrix = new double[numOfRunnablePVPins_pp][numOfRunnableFFpins_vv][timeserise_N];

            /**
             *fill respon into B matrix
             *填入前馈输出响应矩阵
             * */
            maskMatrixRunnablePVUseFF = new int[numOfRunnablePVPins_pp][numOfRunnableFFpins_vv];

            initFFparams();


            /**模型数据库配置的 pv**/
            baseoutpoints_p = categoryPVmodletag.size();

            basefeedforwardpoints_v = categoryFFmodletag.size();

            baseinputpoints_m = categoryMVmodletag.size();

            /***************acp算法回传的数据********************/
            backPVPrediction = new double[numOfRunnablePVPins_pp][timeserise_N];//pv的预测曲线

            backPVFunelUp = new double[numOfRunnablePVPins_pp][timeserise_N];//PV的漏斗上曲线

            backPVFunelDown = new double[numOfRunnablePVPins_pp][timeserise_N];//漏斗下曲线

            backDmvWrite = new double[numOfRunnablePVPins_pp][numOfRunnableMVpins_mm];//MV写入值
            backrawDmv = new double[numOfRunnableMVpins_mm];
            backrawDff = new double[numOfRunnableFFpins_vv];

            backPVPredictionError = new double[numOfRunnablePVPins_pp];//预测误差

            backDff = new double[numOfRunnablePVPins_pp][numOfRunnableFFpins_vv];//前馈变换值

            validkey = System.currentTimeMillis();

            simulatControlModle.setModlePins(modlePins);

            simulatControlModle.setControlModle(this);
            simulatControlModle.build();

            executePythonBridge= new ExecutePythonBridge(
                    apcdir,
                    "http://localhost:8080/AILab/python/modlebuild/" + modleId + ".do", modleId + "");

            if(isfirsttime){
                if (getModleEnable() == 1) {
                    simulatControlModle.setIssimulation(true);
                    executePythonBridge.execute();
                    simulatControlModle.getExecutePythonBridgeSimulate().execute();

                }

            }


            return true;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }


    public boolean unregisterpin() {
        boolean result = true;
        for (ModlePin pin : modlePins) {
            if ((opcServicConstainer.isAnyConnectOpcServe()) && (pin != null)) {
                result = opcServicConstainer.unregisterModlePinAndComponent(pin) && result;
            }
        }
        return result;
    }


    /**
     *返回原始MV结构相关数据，包括mv上下限，dmv上下限，mv和mvfb当前数值
     * */
    public void getMVRelationData(JSONObject jsonObject){

        /**limitU输入限制 mv上下限*/
        Double[][] limitU = new Double[numOfRunnableMVpins_mm][2];
        Double[][] limitDU = new Double[numOfRunnableMVpins_mm][2];
        /**U执行器当前给定*/
        Double[] U = new Double[numOfRunnableMVpins_mm];
        /**U执行器当前反馈**/
        Double[] UFB = new Double[numOfRunnableMVpins_mm];

        int indexEnableMV = 0;
        for (int indexmv = 0; indexmv < categoryMVmodletag.size(); ++indexmv) {
            if (maskisRunnableMVMatrix[indexmv] == 0) {
                continue;
            }
            Double[] mvminmax = new Double[2];
            ModlePin mvdown = categoryMVmodletag.get(indexmv).getDownLmt();
            ModlePin mvup = categoryMVmodletag.get(indexmv).getUpLmt();

            mvminmax[0] = mvdown.modleGetReal();

            mvminmax[1] = mvup.modleGetReal();

            //执行器限制
            limitU[indexEnableMV] = mvminmax;

            Double[] dmvminmax = new Double[2];
            dmvminmax[0] = categoryMVmodletag.get(indexmv).getDmvLow();
            dmvminmax[1] = categoryMVmodletag.get(indexmv).getDmvHigh();
            limitDU[indexEnableMV] = dmvminmax;

            //执行器给定
            U[indexEnableMV] = categoryMVmodletag.get(indexmv).modleGetReal();
            UFB[indexEnableMV] = categoryMVmodletag.get(indexmv).getFeedBack().modleGetReal();
            indexEnableMV++;

        }
        jsonObject.put("origionstructlimitmv", limitU);
        jsonObject.put("origionstructlimitDmv", limitDU);
        jsonObject.put("origionstructmv", U);
        jsonObject.put("originstructmvfb", UFB);
    }


    public void getPVRealData(JSONObject jsonObject){
        Double[] pv = new Double[numOfRunnablePVPins_pp];
        int indexEnablePV = 0;
        for (int indexpv = 0; indexpv < categoryPVmodletag.size(); ++indexpv) {
            if (maskisRunnablePVMatrix[indexpv] == 0) {
                continue;
            }
            /***
             * 是否有滤波器，有则使用滤波器的值，不然就使用实时数据
             * */
            pv[indexEnablePV] = categoryPVmodletag.get(indexpv).modleGetReal();
            ++indexEnablePV;
        }
        jsonObject.put("origiony0", pv);
    }




    public JSONObject getRealData() {
        try {
            /**
             * y0(pv)
             * limitU(mv)
             * limitY()
             * FF
             * Wi(sp)
             *
             * */
            JSONObject jsonObject = new JSONObject();
            //sp
            Double[] sp = new Double[numOfRunnablePVPins_pp];
            int indexEnableSP = 0;
            for (int indexsp = 0; indexsp < categorySPmodletag.size(); ++indexsp) {
                if (maskisRunnablePVMatrix[indexsp] == 0) {
                    continue;
                }
                sp[indexEnableSP] = categorySPmodletag.get(indexsp).modleGetReal();
                indexEnableSP++;
            }
            jsonObject.put("wi", sp);

            //pv
            Double[] pv = new Double[numOfRunnablePVPins_pp];
            int indexEnablePV = 0;
            for (int indexpv = 0; indexpv < categoryPVmodletag.size(); ++indexpv) {
                if (maskisRunnablePVMatrix[indexpv] == 0) {
                    continue;
                }
                /***
                 * 是否有滤波器，有则使用滤波器的值，不然就使用实时数据
                 * */
                pv[indexEnablePV] = categoryPVmodletag.get(indexpv).modleGetReal();
                ++indexEnablePV;
            }
            jsonObject.put("y0", pv);

            /**limitU输入限制 mv上下限*/
            Double[][] limitU = new Double[numOfRunnableMVpins_mm][2];
            Double[][] limitDU = new Double[numOfRunnableMVpins_mm][2];
            /**U执行器当前给定*/
            Double[] U = new Double[numOfRunnableMVpins_mm];
            /**U执行器当前反馈**/
            Double[] UFB = new Double[numOfRunnableMVpins_mm];

            int indexEnableMV = 0;
            for (int indexmv = 0; indexmv < categoryMVmodletag.size(); ++indexmv) {
                if (maskisRunnableMVMatrix[indexmv] == 0) {
                    continue;
                }
                Double[] mvminmax = new Double[2];
                ModlePin mvdown = categoryMVmodletag.get(indexmv).getDownLmt();
                ModlePin mvup = categoryMVmodletag.get(indexmv).getUpLmt();

                mvminmax[0] = mvdown.modleGetReal();

                mvminmax[1] = mvup.modleGetReal();

                //执行器限制
                limitU[indexEnableMV] = mvminmax;

                Double[] dmvminmax = new Double[2];
                dmvminmax[0] = categoryMVmodletag.get(indexmv).getDmvLow();
                dmvminmax[1] = categoryMVmodletag.get(indexmv).getDmvHigh();
                limitDU[indexEnableMV] = dmvminmax;

                //执行器给定
                U[indexEnableMV] = categoryMVmodletag.get(indexmv).modleGetReal();
                UFB[indexEnableMV] = categoryMVmodletag.get(indexmv).getFeedBack().modleGetReal();
                indexEnableMV++;

            }
            jsonObject.put("limitU", limitU);
            jsonObject.put("limitDU", limitDU);
            jsonObject.put("U", U);
            jsonObject.put("UFB", UFB);

            //FF
            int indexEnableFF = 0;
            if (numOfRunnableFFpins_vv != 0) {
                Double[] ff = new Double[numOfRunnableFFpins_vv];
                Double[] fflmt = new Double[numOfRunnableFFpins_vv];
                for (int indexff = 0; indexff < categoryFFmodletag.size(); ++indexff) {
                    if (maskisRunnableFFMatrix[indexff] == 0) {
                        continue;
                    }

                    ff[indexEnableFF] = categoryFFmodletag.get(indexff).modleGetReal();
                    ModlePin ffuppin = categoryFFmodletag.get(indexff).getUpLmt();
                    ModlePin ffdownpin = categoryFFmodletag.get(indexff).getDownLmt();

                    /**
                     *ff信号是否在置信区间内
                     * */
                    Double ffHigh = 0d;
                    Double ffLow = 0d;

                    ffHigh = ffuppin.modleGetReal();
                    ffLow = ffdownpin.modleGetReal();

                    if ((ffLow <= categoryFFmodletag.get(indexff).modleGetReal()) && (ffHigh >= categoryFFmodletag.get(indexff).modleGetReal())) {
                        fflmt[indexEnableFF] = 1d;
                    } else {
                        fflmt[indexEnableFF] = 0d;
                    }
                    indexEnableFF++;
                }

                jsonObject.put("FF", ff);
                jsonObject.put("FFLmt", fflmt);

            }

            jsonObject.put("enable", modleEnable);

            /**
             *死区时间和漏斗初始值
             * */
            jsonObject.put("deadZones", deadZones);
            jsonObject.put("funelInitValues", funelinitvalues);
            jsonObject.put("validekey", validkey);
            return jsonObject;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    @Deprecated
    public JSONObject getrealSimulateData() {

        try {
            /**
             * y0(pv)
             * limitU(mv)
             * limitY()
             * FF
             * Wi(sp)
             *
             * */
            JSONObject jsonObject = new JSONObject();
            //sp
            Double[] sp = new Double[simulatControlModle.getSimulateOutpoints_p()];
            int loop = 0;
            for (ModlePin sppin : simulatControlModle.getExtendModleSPPins()) {
                sp[loop] = sppin.modleGetReal();
                loop++;
            }
            jsonObject.put("wi", sp);

            //pv
            Double[] pv = new Double[simulatControlModle.getSimulateOutpoints_p()];
            loop = 0;
            for (ModlePin pvpin : simulatControlModle.getExtendModlePVPins()) {
                /***
                 * 是否有滤波器，有则使用滤波器的值，不然就使用实时数据
                 * */
                pv[loop++] = pvpin.modleGetReal();
            }
            jsonObject.put("y0", pv);

            //limitU输入限制
            Double[][] limitU = new Double[simulatControlModle.getSimulateInputpoints_m()][2];
            Double[][] limitDU = new Double[simulatControlModle.getSimulateInputpoints_m()][2];
            loop = 0;
            //U执行器当前给定
            Double[] U = new Double[simulatControlModle.getSimulateInputpoints_m()];
            //U执行器当前反馈
            Double[] UFB = new Double[simulatControlModle.getSimulateInputpoints_m()];

            for (ModlePin mvpin : simulatControlModle.getExtendModleMVPins()) {
                Double[] mvminmax = new Double[2];
                ModlePin mvdown = mvpin.getDownLmt();
                ModlePin mvup = mvpin.getUpLmt();

                mvminmax[0] = mvdown.modleGetReal();

                mvminmax[1] = mvup.modleGetReal();

                //执行器限制
                limitU[loop] = mvminmax;

                Double[] dmvminmax = new Double[2];
                dmvminmax[0] = mvpin.getDmvLow();
                dmvminmax[1] = mvpin.getDmvHigh();
                limitDU[loop] = dmvminmax;

                //执行器给定
                U[loop] = mvpin.modleGetReal();
                UFB[loop] = mvpin.getFeedBack().modleGetReal();
                loop++;

            }
            jsonObject.put("limitU", limitU);
            jsonObject.put("limitDU", limitDU);
            jsonObject.put("U", U);
            jsonObject.put("UFB", UFB);

            //FF
            int indexEnableFF = 0;
            if (categoryFFmodletag.size() != 0) {
                Double[] ff = new Double[numOfRunnableFFpins_vv];
                Double[] fflmt = new Double[numOfRunnableFFpins_vv];
                for (int indexff = 0; indexff < categoryFFmodletag.size(); indexff++) {
                    if (maskisRunnableFFMatrix[indexff] == 0) {
                        continue;
                    }
                    ff[indexEnableFF] = categoryFFmodletag.get(indexff).modleGetReal();
                    ModlePin ffuppin = categoryFFmodletag.get(indexff).getUpLmt();
                    ModlePin ffdownpin = categoryFFmodletag.get(indexff).getDownLmt();

                    /**
                     *ff信号是否在置信区间内
                     * */
                    Double ffHigh = 0d;
                    Double ffLow = 0d;

                    ffHigh = ffuppin.modleGetReal();
                    ffLow = ffdownpin.modleGetReal();

                    if ((ffLow <= categoryFFmodletag.get(indexff).modleGetReal()) && (ffHigh >= categoryFFmodletag.get(indexff).modleGetReal())) {
                        fflmt[indexEnableFF] = 1d;
                    } else {
                        fflmt[indexEnableFF] = 0d;
                    }

                    indexEnableFF++;
                }

                jsonObject.put("FF", ff);
                jsonObject.put("FFLmt", fflmt);

            }

            jsonObject.put("enable", simulatControlModle.isIssimulation() ? 1 : 0);

            /**
             *死区时间和漏斗初始值
             * */
            jsonObject.put("deadZones", simulatControlModle.getSimulatedeadZones());
            jsonObject.put("funelInitValues", simulatControlModle.getSimulatefunelinitvalues());
            jsonObject.put("validekey", simulatControlModle.getSimulatevalidkey());
            return jsonObject;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    public boolean writeData(Double[] values) {
        try {
//            int loop = 0;
            int indexENableMV = 0;
            boolean result = true;
            for (int indexmv = 0; indexmv < categoryMVmodletag.size(); ++indexmv) {
                if (maskisRunnableMVMatrix[indexmv] == 0) {
                    continue;
                }
                ModlePin mvpin = categoryMVmodletag.get(indexmv);
                mvpin.setWriteValue(values[indexENableMV]);
                result = opcServicConstainer.writeModlePinValue(mvpin, values[indexENableMV]) && result;
                indexENableMV++;
            }
            return result;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;

    }

    /**
     * 更新模型计算后的数据
     *
     * @param funelupAnddown   尺寸：2XpN
     *                         第0行存漏斗的上限；[0~N-1]第一个pv的，[N~2N-1]为第二个pv的漏斗上限
     *                         第1行存漏斗的下限；
     * @param backPVPrediction
     */
    public boolean updateModleComputeResult(double[] backPVPrediction, double[][] funelupAnddown, double[] backDmvWrite, double[] backPVPredictionError, double[] dff) {
        /**
         * 模型运算状态值
         * */
        try {
            for (int i = 0; i < numOfRunnablePVPins_pp; i++) {
                this.backPVPrediction[i] = Arrays.copyOfRange(backPVPrediction, 0 + timeserise_N * i, timeserise_N + timeserise_N * i);//pv的预测曲线
                this.backPVFunelUp[i] = Arrays.copyOfRange(funelupAnddown[0], 0 + timeserise_N * i, timeserise_N + timeserise_N * i);//PV的漏斗上限
                this.backPVFunelDown[i] = Arrays.copyOfRange(funelupAnddown[1], 0 + timeserise_N * i, timeserise_N + timeserise_N * i);//PV的漏斗下限
            }

            /**预测误差*/
            this.backPVPredictionError = backPVPredictionError;
            this.backrawDmv = backDmvWrite;
            this.backrawDff = dff;
            for (int indexpv = 0; indexpv < numOfRunnablePVPins_pp; indexpv++) {

                /**dMV写入值*/
                for (int indexmv = 0; indexmv < numOfRunnableMVpins_mm; indexmv++) {
                    if (maskMatrixRunnablePVUseMV[indexpv][indexmv] == 1) {
                        this.backDmvWrite[indexpv][indexmv] = backDmvWrite[indexmv];
                    }
                }

                /**前馈增量*/
                for (int indexff = 0; indexff < numOfRunnableFFpins_vv; indexff++) {
                    if (maskMatrixRunnablePVUseFF[indexpv][indexff] == 1) {
                        this.backDff[indexpv][indexff] = dff[indexff];
                    }
                }

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }


    /**
     * pv与mv是否有映射关系
     **/
    private ResponTimeSerise isPVMappingMV(String pvpin, String mvpin) {

        for (ResponTimeSerise responTimeSerise : responTimeSerises) {
            if (
                    responTimeSerise.getInputPins().equals(mvpin)
                            &&
                            responTimeSerise.getOutputPins().equals(pvpin)
            ) {
                return responTimeSerise;
            }
        }
        return null;
    }

    /**
     * pv与ff是否有映射关系
     */
    private ResponTimeSerise isPVMappingFF(String pvpin, String ffpin) {

        for (ResponTimeSerise responTimeSerise : responTimeSerises) {
            if (
                    responTimeSerise.getInputPins().equals(ffpin)
                            &&
                            responTimeSerise.getOutputPins().equals(pvpin)
            ) {
                return responTimeSerise;
            }

        }
        return null;
    }


    public Integer getPredicttime_P() {
        return predicttime_P;
    }

    public void setPredicttime_P(Integer predicttime_P) {
        this.predicttime_P = predicttime_P;
    }

    public Integer getBaseoutpoints_p() {
        return baseoutpoints_p;
    }

    public void setBaseoutpoints_p(Integer baseoutpoints_p) {
        this.baseoutpoints_p = baseoutpoints_p;
    }

    public Integer getControltime_M() {
        return controltime_M;
    }

    public void setControltime_M(Integer controltime_M) {
        this.controltime_M = controltime_M;
    }

    public Integer getBaseinputpoints_m() {
        return baseinputpoints_m;
    }

    public void setBaseinputpoints_m(Integer baseinputpoints_m) {
        this.baseinputpoints_m = baseinputpoints_m;
    }

    public Integer getBasefeedforwardpoints_v() {
        return basefeedforwardpoints_v;
    }

    public void setBasefeedforwardpoints_v(Integer basefeedforwardpoints_v) {
        this.basefeedforwardpoints_v = basefeedforwardpoints_v;
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


    public double[][][] getA_RunnabletimeseriseMatrix() {
        return A_RunnabletimeseriseMatrix;
    }

    public double[][][] getB_RunnabletimeseriseMatrix() {
        return B_RunnabletimeseriseMatrix;
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

    public synchronized int getModleEnable() {
        return modleEnable;
    }

    public synchronized void setModleEnable(int modleEnable) {
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


    public double[] getBackPVPredictionError() {
        return backPVPredictionError;
    }


    public ModlePin getAutoEnbalePin() {
        return autoEnbalePin;
    }

    public int[][] getMaskBaseMapPvUseMvMatrix() {
        return maskBaseMapPvUseMvMatrix;
    }

    public void setMaskBaseMapPvUseMvMatrix(int[][] maskBaseMapPvUseMvMatrix) {
        this.maskBaseMapPvUseMvMatrix = maskBaseMapPvUseMvMatrix;
    }

    public long getValidkey() {
        return validkey;
    }

    public void generateValidkey() {
        this.validkey = System.currentTimeMillis();
    }

    public Double[] getAlpheTrajectoryCoefficients() {
        return alpheTrajectoryCoefficients;
    }

    public void setAlpheTrajectoryCoefficients(Double[] alpheTrajectoryCoefficients) {
        this.alpheTrajectoryCoefficients = alpheTrajectoryCoefficients;
    }

    public OpcServicConstainer getOpcServicConstainer() {
        return opcServicConstainer;
    }

    public void setOpcServicConstainer(OpcServicConstainer opcServicConstainer) {
        this.opcServicConstainer = opcServicConstainer;
    }

    public double[][] getFunneltype() {
        return funneltype;
    }

    public void setFunneltype(double[][] funneltype) {
        this.funneltype = funneltype;
    }


    public SimulatControlModle getSimulatControlModle() {
        return simulatControlModle;
    }

    public String getSimulatorbuilddir() {
        return simulatorbuilddir;
    }

    public void setSimulatorbuilddir(String simulatorbuilddir) {
        this.simulatorbuilddir = simulatorbuilddir;
    }


    public int[][] getMaskBaseMapPvUseFfMatrix() {
        return maskBaseMapPvUseFfMatrix;
    }

    public void setMaskBaseMapPvUseFfMatrix(int[][] maskBaseMapPvUseFfMatrix) {
        this.maskBaseMapPvUseFfMatrix = maskBaseMapPvUseFfMatrix;
    }

    public double[][] getBackDmvWrite() {
        return backDmvWrite;
    }

    public double[][] getBackDff() {
        return backDff;
    }

    public double[] getBackrawDmv() {
        return backrawDmv;
    }

    public void setBackrawDmv(double[] backrawDmv) {
        this.backrawDmv = backrawDmv;
    }

    public double[] getBackrawDff() {
        return backrawDff;
    }

    public void setBackrawDff(double[] backrawDff) {
        this.backrawDff = backrawDff;
    }

    public Integer getNumOfRunnablePVPins_pp() {
        return numOfRunnablePVPins_pp;
    }

    public int[][] getMaskMatrixRunnablePVUseFF() {
        return maskMatrixRunnablePVUseFF;
    }

    public void setMaskMatrixRunnablePVUseFF(int[][] maskMatrixRunnablePVUseFF) {
        this.maskMatrixRunnablePVUseFF = maskMatrixRunnablePVUseFF;
    }

    public int[] getMaskisRunnableMVMatrix() {
        return maskisRunnableMVMatrix;
    }

    public void setMaskisRunnableMVMatrix(int[] maskisRunnableMVMatrix) {
        this.maskisRunnableMVMatrix = maskisRunnableMVMatrix;
    }

    public int[] getMaskisRunnableFFMatrix() {
        return maskisRunnableFFMatrix;
    }

    public void setMaskisRunnableFFMatrix(int[] maskisRunnableFFMatrix) {
        this.maskisRunnableFFMatrix = maskisRunnableFFMatrix;
    }

    public int[] getMaskisRunnablePVMatrix() {
        return maskisRunnablePVMatrix;
    }

    public void setMaskisRunnablePVMatrix(int[] maskisRunnablePVMatrix) {
        this.maskisRunnablePVMatrix = maskisRunnablePVMatrix;
    }


    public void setNumOfRunnablePVPins_pp(Integer numOfRunnablePVPins_pp) {
        this.numOfRunnablePVPins_pp = numOfRunnablePVPins_pp;
    }

    public Integer getNumOfRunnableFFpins_vv() {
        return numOfRunnableFFpins_vv;
    }

    public void setNumOfRunnableFFpins_vv(Integer numOfRunnableFFpins_vv) {
        this.numOfRunnableFFpins_vv = numOfRunnableFFpins_vv;
    }

    public Integer getNumOfRunnableMVpins_mm() {
        return numOfRunnableMVpins_mm;
    }

    public void setNumOfRunnableMVpins_mm(Integer numOfRunnableMVpins_mm) {
        this.numOfRunnableMVpins_mm = numOfRunnableMVpins_mm;
    }

    public int[][] getMaskMatrixRunnablePVUseMV() {
        return maskMatrixRunnablePVUseMV;
    }

    public void setMaskMatrixRunnablePVUseMV(int[][] maskMatrixRunnablePVUseMV) {
        this.maskMatrixRunnablePVUseMV = maskMatrixRunnablePVUseMV;
    }
    public String[] getAlpheTrajectoryCoefmethod() {
        return alpheTrajectoryCoefmethod;
    }

    public void setAlpheTrajectoryCoefmethod(String[] alpheTrajectoryCoefmethod) {
        this.alpheTrajectoryCoefmethod = alpheTrajectoryCoefmethod;
    }



    public Map<String, ModlePin> getStringmodlePinsMap() {
        return stringmodlePinsMap;
    }

    public Integer getRunstyle() {
        return runstyle;
    }

    public void setRunstyle(Integer runstyle) {
        this.runstyle = runstyle;
    }

    public float[][] getMaskBaseMapPvEffectMvMatrix() {
        return maskBaseMapPvEffectMvMatrix;
    }

    public void setMaskBaseMapPvEffectMvMatrix(float[][] maskBaseMapPvEffectMvMatrix) {
        this.maskBaseMapPvEffectMvMatrix = maskBaseMapPvEffectMvMatrix;
    }

    public float[][] getMaskMatrixRunnablePvEffectMv() {
        return maskMatrixRunnablePvEffectMv;
    }

    public void setMaskMatrixRunnablePvEffectMv(float[][] maskMatrixRunnablePvEffectMv) {
        this.maskMatrixRunnablePvEffectMv = maskMatrixRunnablePvEffectMv;
    }
}

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
@Deprecated
public class ControlModle916 implements Modle {
    public static Logger logger = Logger.getLogger(ModleController.class);
    /**
     * 模型的标识token，用于apc算法判别自己的算法是否已经过时，需要停止
     */
    private long validkey = System.currentTimeMillis();
    /**
     * 模型的标识token，用于apc仿真算法判别自己的算法是否已经过去，需要停止
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

    private int modleId;//模型id主键
    private String modleName;//模型名称

    /**
     * 模型定义
     */
    private Integer predicttime_P = 12;//预测时域
    private Integer controltime_M = 6;//单一控制输入未来控制M步增量(控制域)
    private Integer timeserise_N = 40;//响应序列长度
    private Integer controlAPCOutCycle = 0;//控制周期

    private volatile int modleEnable;//模块使能，用于设置算法是否运行，算法是否运行


    private List<ModlePin> modlePins;//引脚,from db
    private List<ResponTimeSerise> responTimeSerises;//响应 from db

    /**
     * 原始多目标pv输出数量
     */
    private Integer outpoints_p = 0;//输出个数量
    /**
     * 激活的pv引脚
     */
    private Integer numOfEnablePVPins_pp = 0;

    /**
     * 原始前馈数量
     */
    private Integer feedforwardpoints_v = 0;
    /**
     * 对应激活的pv引脚ff引脚数量
     */
    private Integer numOfEnableFFpins_vv = 0;

    /**
     * 原始可控制输入数量
     */
    private Integer inputpoints_m = 0;
    /**
     * 对应激活pv引脚所用的mv引脚数量
     */
    private Integer numOfEnableMVpins_mm = 0;

    private List<ModlePin> categoryPVmodletag = new ArrayList<>();//已经分类号的PV引脚
    private List<ModlePin> categorySPmodletag = new ArrayList<>();//已经分类号的SP引脚
    private List<ModlePin> categoryMVmodletag = new ArrayList<>();//已经分类号的MV引脚
    private List<ModlePin> categoryFFmodletag = new ArrayList<>();//已经分类号的FF引脚
    private ModlePin autoEnbalePin = null;//dcs手自动切换引脚


    private double[][][] A_timeseriseMatrix = null;//输入响应 shape=[pv][mv][resp_N]
    private double[][][] B_timeseriseMatrix = null;//前馈响应 shape=[pv][ff][resp_N]
    private Double[] Q = null;//误差权重矩阵
    private Double[] R = null;//控制权矩阵、正则化
    private Double[] alpheTrajectoryCoefficients = null;//参考轨迹的柔化系数
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
    private boolean lastsimulaterunorstop = false;


    /**
     * mv1 mv2 mv3.....mvn
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
    private int[][] matrixPvUseMv = null;

    /**
     * 激活的pv引脚对应的mv
     */
    private int[][] matrixEnablePVUseMV = null;


    /**
     * 表示Pv使用了哪些ff
     * pvuseff矩阵shape=(num_pv,num_ff)
     * 如pv1用了ff1,pv2用了ff2
     * 如[[1,0],
     * [0,1]]
     */
    private int[][] matrixPvUseFf = null;
    /**
     * 激活的pv对应的ff
     */
    private int[][] matrixEnablePVUseFF = null;

    /**
     * 参与的引脚，在内容为1的地方就说明改引脚被引用了
     */
    int[] participateMVMatrix = null;
    int[] participateFFMatrix = null;
    int[] participatePVMatrix = null;

    private Map<String, ModlePin> stringmodlePinsMap = new HashMap<>();//方便引脚索引key=pv1.mv2,sp1,ff1等 value=引脚类


    /**
     * 第一次将点号注册进opcserve
     */
    private void firstTimeRegiterPinsToOPCServe() {
        for (ModlePin modlePin : modlePins) {
            /**注册opc点号*/
            stringmodlePinsMap.put(modlePin.getModlePinName(), modlePin);//将定义的引脚按照key=pvn/mvn/spn等(n=1,2,3..8) value=pin
            /**将引脚注册进行opcserice中*/
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
     * 模型构建函数
     *
     * @param isfirsttime 如果是第一次构建，那么需要重新注册引脚进opcserv
     */
    public synchronized Boolean modleBuild(boolean isfirsttime) {
        logger.info("modle id=" + modleId + " is build");

        numOfEnablePVPins_pp = 0;
        numOfEnableMVpins_mm = 0;
        numOfEnableFFpins_vv = 0;

        try {

            /**第一次将点号注册进opcserve*/
            if (isfirsttime) {
                firstTimeRegiterPinsToOPCServe();
            }

            //simulatControlModle = new SimulatControlModle(simulatorbuilddir, modleId, lastsimulaterunorstop);
            /**引脚分类*/

            /**
             * pv and sp
             * */

            if (isfirsttime) {
                for (int i = 1; i <= totalPv; i++) {
                    ModlePin pvPin = stringmodlePinsMap.get(ModlePin.TYPE_PIN_PV + i);
                    if (pvPin != null) {
                        categoryPVmodletag.add(pvPin);

                        ModlePin dcsEnablepin = stringmodlePinsMap.get(ModlePin.TYPE_PIN_PIN_PVENABLE + i);
                        if (dcsEnablepin != null) {
                            pvPin.setDcsEnabePin(dcsEnablepin);
                        }

                        ModlePin spPin = stringmodlePinsMap.get(ModlePin.TYPE_PIN_SP + i);
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
                        ModlePin ffdown = stringmodlePinsMap.get(ModlePin.TYPE_PIN_FFDOWN + i);
                        ModlePin ffup = stringmodlePinsMap.get(ModlePin.TYPE_PIN_FFUP + i);
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
                    ModlePin mvPin = stringmodlePinsMap.get(ModlePin.TYPE_PIN_MV + i);
                    if (mvPin != null) {
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
            }


            /**init pvusemv and pvuseff matrix*/
            matrixPvUseMv = new int[categoryPVmodletag.size()][categoryMVmodletag.size()];
            matrixPvUseFf = new int[categoryPVmodletag.size()][categoryFFmodletag.size()];

            participateMVMatrix = new int[categoryMVmodletag.size()];
            participateFFMatrix = new int[categoryFFmodletag.size()];
            participatePVMatrix = new int[categoryPVmodletag.size()];

            for (int indexpv = 0; indexpv < categoryPVmodletag.size(); ++indexpv) {
                if (categoryPVmodletag.get(indexpv).getPinEnable() == 1) {
                    /**激活的pv数量*/
                    ++numOfEnablePVPins_pp;
                    participatePVMatrix[indexpv] = 1;
                }

                /**1\marker total pvusemv
                 * 2\marker participate mv
                 * */
                for (int indexmv = 0; indexmv < categoryMVmodletag.size(); ++indexmv) {
                    ResponTimeSerise ismapping = isPVMappingMV(categoryPVmodletag.get(indexpv).getModlePinName(), categoryMVmodletag.get(indexmv).getModlePinName());
                    matrixPvUseMv[indexpv][indexmv] = (ismapping != null ? 1 : 0);
                    if ((categoryPVmodletag.get(indexpv).getPinEnable() == 1) && (ismapping != null)) {
                        participateMVMatrix[indexmv] = 1;
                    }

                }

                /**1\marker total pvuseff
                 * 2\marker participate ff
                 * */
                for (int indexff = 0; indexff < categoryFFmodletag.size(); ++indexff) {
                    ResponTimeSerise ismapping = isPVMappingFF(categoryPVmodletag.get(indexpv).getModlePinName(), categoryFFmodletag.get(indexff).getModlePinName());
                    matrixPvUseFf[indexpv][indexff] = (ismapping != null ? 1 : 0);
                    if ((categoryPVmodletag.get(indexpv).getPinEnable() == 1) && (ismapping != null)) {
                        participateFFMatrix[indexff] = 1;
                    }
                }
            }


            /**统计参与激活的pv的mv的数量*/
            for (int mvi : participateMVMatrix) {
                if (mvi == 1) {
                    ++numOfEnableMVpins_mm;
                }
            }
            /**统计参与激活的pv的ff的数量*/
            for (int ffi : participateFFMatrix) {
                if (ffi == 1) {
                    ++numOfEnableFFpins_vv;
                }
            }


            /***
             * 输入输出响应对应矩阵
             * init A matrix
             * */
            A_timeseriseMatrix = new double[numOfEnablePVPins_pp][numOfEnableMVpins_mm][timeserise_N];
            /***
             *1、fill respon into A matrix
             *2、and init matrixEnablePVUseMV
             * */

            /**predict zone params*/
            Q = new Double[numOfEnablePVPins_pp];//use for pv
            /**trajectry coefs*/
            alpheTrajectoryCoefficients = new Double[numOfEnablePVPins_pp];//use for pv
            /**死区时间和漏洞初始值*/
            deadZones = new Double[numOfEnablePVPins_pp];//use for pv
            funelinitvalues = new Double[numOfEnablePVPins_pp];//use for pv
            /**funnel type*/
            funneltype = new double[numOfEnablePVPins_pp][2];//use for pv


            matrixEnablePVUseMV = new int[numOfEnablePVPins_pp][numOfEnableMVpins_mm];//recording enablepv use which mvs

            int indexEnablePV = 0;
            for (int indexpv = 0; indexpv < categoryPVmodletag.size(); ++indexpv) {

                /**如果未进行使能，则直接跳过*/
                if (participatePVMatrix[indexpv] == 0) {
                    continue;
                }

                Q[indexEnablePV] = categoryPVmodletag.get(indexpv).getQ();
                alpheTrajectoryCoefficients[indexEnablePV] = categoryPVmodletag.get(indexpv).getReferTrajectoryCoef();
                deadZones[indexEnablePV] = categoryPVmodletag.get(indexpv).getDeadZone();
                funelinitvalues[indexEnablePV] = categoryPVmodletag.get(indexpv).getFunelinitValue();

                double[] fnl = new double[2];
                if (categoryPVmodletag.get(indexpv).getFunneltype() != null) {
                    switch (categoryPVmodletag.get(indexpv).getFunneltype()) {
                        case ModlePin.TYPE_FUNNEL_FULL:
                            fnl[0] = 0d;
                            fnl[1] = 0d;
                            funneltype[indexEnablePV] = fnl;
                            break;
                        case ModlePin.TYPE_FUNNEL_UP:
                            fnl[0] = 0;
                            //乘负无穷
                            fnl[1] = 1;
                            funneltype[indexEnablePV] = fnl;
                            break;
                        case ModlePin.TYPE_FUNNEL_DOWN:
                            //乘正无穷
                            fnl[0] = 1;
                            fnl[1] = 0;
                            funneltype[indexEnablePV] = fnl;
                            break;
                        default:
                            fnl[0] = 0;
                            fnl[1] = 0;
                            funneltype[indexEnablePV] = fnl;
                    }
                } else {
                    //匹配不到就是全漏斗
                    fnl[0] = 0;
                    fnl[1] = 0;
                    funneltype[indexEnablePV] = fnl;
                }


                int indexEnableMV = 0;
                for (int indexmv = 0; indexmv < categoryMVmodletag.size(); ++indexmv) {

                    /**mv未参与控制，直接跳过*/
                    if (participateMVMatrix[indexmv] == 0) {
                        continue;
                    }
                    /**查找映射关系*/
                    ResponTimeSerise responTimeSerisePVMV = isPVMappingMV(categoryPVmodletag.get(indexpv).getModlePinName(), categoryMVmodletag.get(indexmv).getModlePinName());
                    if (responTimeSerisePVMV != null) {
                        A_timeseriseMatrix[indexEnablePV][indexEnableMV] = responTimeSerisePVMV.responOneTimeSeries(timeserise_N, controlAPCOutCycle);
                        matrixEnablePVUseMV[indexEnablePV][indexEnableMV] = 1;
                        simulatControlModle.addNumOfIOMappingRelation();
                    }
                    ++indexEnableMV;
                }
                ++indexEnablePV;
            }


            /**init R control zone params*/
            R = new Double[numOfEnableMVpins_mm];//use for mv
            int indevEnableMV = 0;
            for (int indexmv = 0; indexmv < categoryMVmodletag.size(); ++indexmv) {
                if (participateMVMatrix[indexmv] == 0) {
                    continue;
                }
                R[indevEnableMV] = categoryMVmodletag.get(indexmv).getR();

                ++indevEnableMV;
            }


            /***
             * 前馈输出对应矩阵
             * init B matrix
             * */
            B_timeseriseMatrix = new double[numOfEnablePVPins_pp][numOfEnableFFpins_vv][timeserise_N];

            /**
             *fill respon into B matrix
             *填入前馈输出响应矩阵
             * */
            matrixEnablePVUseFF = new int[numOfEnablePVPins_pp][numOfEnableFFpins_vv];
            indexEnablePV = 0;
            for (int indexpv = 0; indexpv < categoryPVmodletag.size(); ++indexpv) {

                if (participatePVMatrix[indexpv] == 0) {
                    continue;
                }

                int indexEnableFF = 0;
                for (int indexff = 0; indexff < categoryFFmodletag.size(); ++indexff) {

                    if (participateFFMatrix[indexff] == 0) {
                        continue;
                    }

                    ResponTimeSerise responTimeSerisePVFF = isPVMappingFF(categoryPVmodletag.get(indexpv).getModlePinName(), categoryFFmodletag.get(indexff).getModlePinName());

                    if (responTimeSerisePVFF != null) {
                        B_timeseriseMatrix[indexEnablePV][indexEnableFF] = responTimeSerisePVFF.responOneTimeSeries(timeserise_N, controlAPCOutCycle);
                        matrixEnablePVUseFF[indexEnablePV][indexEnableFF] = 1;
                    }
                    ++indexEnableFF;
                }
                ++indexEnablePV;
            }


            outpoints_p = categoryPVmodletag.size();

            feedforwardpoints_v = categoryFFmodletag.size();

            inputpoints_m = categoryMVmodletag.size();

            simulatControlModle.setControltime_M(controltime_M);
            simulatControlModle.setPredicttime_P(predicttime_P);
            simulatControlModle.setTimeserise_N(timeserise_N);
            simulatControlModle.setControlAPCOutCycle(controlAPCOutCycle);


            backPVPrediction = new double[numOfEnablePVPins_pp][timeserise_N];//pv的预测曲线

            backPVFunelUp = new double[numOfEnablePVPins_pp][timeserise_N];//PV的漏斗

            backPVFunelDown = new double[numOfEnablePVPins_pp][timeserise_N];

            backDmvWrite = new double[numOfEnablePVPins_pp][numOfEnableMVpins_mm];//MV写入值
            backrawDmv = new double[numOfEnableMVpins_mm];
            backrawDff = new double[numOfEnableFFpins_vv];

            backPVPredictionError = new double[numOfEnablePVPins_pp];//预测误差

            backDff = new double[numOfEnablePVPins_pp][numOfEnableFFpins_vv];//前馈变换值

            validkey = System.currentTimeMillis();

            simulatControlModle.setModlePins(modlePins);

//            simulatControlModle.setControlModle(this); zzx
            simulatControlModle.build();
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

    public JSONObject getrealData() {
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
            Double[] sp = new Double[numOfEnablePVPins_pp];
            int indexEnableSP = 0;
            for (int indexsp = 0; indexsp < categorySPmodletag.size(); ++indexsp) {
                if (participatePVMatrix[indexsp] == 0) {
                    continue;
                }
                sp[indexEnableSP] = categorySPmodletag.get(indexsp).modleGetReal();
                indexEnableSP++;
            }
            jsonObject.put("wi", sp);

            //pv
            Double[] pv = new Double[numOfEnablePVPins_pp];
            int indexEnablePV = 0;
            for (int indexpv = 0; indexpv < categoryPVmodletag.size(); ++indexpv) {
                if (participatePVMatrix[indexpv] == 0) {
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
            Double[][] limitU = new Double[numOfEnableMVpins_mm][2];
            Double[][] limitDU = new Double[numOfEnableMVpins_mm][2];
            /**U执行器当前给定*/
            Double[] U = new Double[numOfEnableMVpins_mm];
            /**U执行器当前反馈**/
            Double[] UFB = new Double[numOfEnableMVpins_mm];

            int indexEnableMV = 0;
            for (int indexmv = 0; indexmv < categoryMVmodletag.size(); ++indexmv) {
                if (participateMVMatrix[indexmv] == 0) {
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
            if (numOfEnableFFpins_vv != 0) {
                Double[] ff = new Double[numOfEnableFFpins_vv];
                Double[] fflmt = new Double[numOfEnableFFpins_vv];
                for (int indexff = 0; indexff < categoryFFmodletag.size(); ++indexff) {
                    if (participateFFMatrix[indexff] == 0) {
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
                Double[] ff = new Double[numOfEnableFFpins_vv];
                Double[] fflmt = new Double[numOfEnableFFpins_vv];
                for (int indexff = 0; indexff < categoryFFmodletag.size(); indexff++) {
                    if (participateFFMatrix[indexff] == 0) {
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
                if (participateMVMatrix[indexmv] == 0) {
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
    public boolean updateModleReal(double[] backPVPrediction, double[][] funelupAnddown, double[] backDmvWrite, double[] backPVPredictionError, double[] dff) {
        /**
         * 模型运算状态值
         * */
        try {
            for (int i = 0; i < numOfEnablePVPins_pp; i++) {
                this.backPVPrediction[i] = Arrays.copyOfRange(backPVPrediction, 0 + timeserise_N * i, timeserise_N + timeserise_N * i);//pv的预测曲线
                this.backPVFunelUp[i] = Arrays.copyOfRange(funelupAnddown[0], 0 + timeserise_N * i, timeserise_N + timeserise_N * i);//PV的漏斗上限
                this.backPVFunelDown[i] = Arrays.copyOfRange(funelupAnddown[1], 0 + timeserise_N * i, timeserise_N + timeserise_N * i);//PV的漏斗下限
            }

            /**预测误差*/
            this.backPVPredictionError = backPVPredictionError;
            this.backrawDmv = backDmvWrite;
            this.backrawDff = dff;
            for (int indexpv = 0; indexpv < numOfEnablePVPins_pp; indexpv++) {

                /**dMV写入值*/
                for (int indexmv = 0; indexmv < numOfEnableMVpins_mm; indexmv++) {
                    if (matrixEnablePVUseMV[indexpv][indexmv] == 1) {
                        this.backDmvWrite[indexpv][indexmv] = backDmvWrite[indexmv];
                    }
                }

                /**前馈增量*/
                for (int indexff = 0; indexff < numOfEnableFFpins_vv; indexff++) {
                    if (matrixEnablePVUseFF[indexpv][indexff] == 1) {
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


    public double[][][] getA_timeseriseMatrix() {
        return A_timeseriseMatrix;
    }

    public double[][][] getB_timeseriseMatrix() {
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


    public double[] getBackPVPredictionError() {
        return backPVPredictionError;
    }


    public ModlePin getAutoEnbalePin() {
        return autoEnbalePin;
    }

    public int[][] getMatrixPvUseMv() {
        return matrixPvUseMv;
    }

    public void setMatrixPvUseMv(int[][] matrixPvUseMv) {
        this.matrixPvUseMv = matrixPvUseMv;
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

    public boolean isLastsimulaterunorstop() {
        return lastsimulaterunorstop;
    }

    public void setLastsimulaterunorstop(boolean lastsimulaterunorstop) {
        this.lastsimulaterunorstop = lastsimulaterunorstop;
    }

    public int[][] getMatrixPvUseFf() {
        return matrixPvUseFf;
    }

    public void setMatrixPvUseFf(int[][] matrixPvUseFf) {
        this.matrixPvUseFf = matrixPvUseFf;
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

    public Integer getNumOfEnablePVPins_pp() {
        return numOfEnablePVPins_pp;
    }

    public int[][] getMatrixEnablePVUseFF() {
        return matrixEnablePVUseFF;
    }

    public void setMatrixEnablePVUseFF(int[][] matrixEnablePVUseFF) {
        this.matrixEnablePVUseFF = matrixEnablePVUseFF;
    }

    public int[] getParticipateMVMatrix() {
        return participateMVMatrix;
    }

    public void setParticipateMVMatrix(int[] participateMVMatrix) {
        this.participateMVMatrix = participateMVMatrix;
    }

    public int[] getParticipateFFMatrix() {
        return participateFFMatrix;
    }

    public void setParticipateFFMatrix(int[] participateFFMatrix) {
        this.participateFFMatrix = participateFFMatrix;
    }

    public int[] getParticipatePVMatrix() {
        return participatePVMatrix;
    }

    public void setParticipatePVMatrix(int[] participatePVMatrix) {
        this.participatePVMatrix = participatePVMatrix;
    }


    public void setNumOfEnablePVPins_pp(Integer numOfEnablePVPins_pp) {
        this.numOfEnablePVPins_pp = numOfEnablePVPins_pp;
    }

    public Integer getNumOfEnableFFpins_vv() {
        return numOfEnableFFpins_vv;
    }

    public void setNumOfEnableFFpins_vv(Integer numOfEnableFFpins_vv) {
        this.numOfEnableFFpins_vv = numOfEnableFFpins_vv;
    }

    public Integer getNumOfEnableMVpins_mm() {
        return numOfEnableMVpins_mm;
    }

    public void setNumOfEnableMVpins_mm(Integer numOfEnableMVpins_mm) {
        this.numOfEnableMVpins_mm = numOfEnableMVpins_mm;
    }

    public int[][] getMatrixEnablePVUseMV() {
        return matrixEnablePVUseMV;
    }

    public void setMatrixEnablePVUseMV(int[][] matrixEnablePVUseMV) {
        this.matrixEnablePVUseMV = matrixEnablePVUseMV;
    }


    public Map<String, ModlePin> getStringmodlePinsMap() {
        return stringmodlePinsMap;
    }

}

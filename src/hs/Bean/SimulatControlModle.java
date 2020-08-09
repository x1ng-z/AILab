package hs.Bean;

import hs.ApcAlgorithm.ExecutePythonBridge;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/8/7 13:34
 */
public class SimulatControlModle {

    public static Logger logger = Logger.getLogger(SimulatControlModle.class);
    private String simulatorbuilddir;
    private Integer modleid;

    /**
     * 模型的标识token，用于apc仿真算法判别自己的算法是否已经过去，需要停止
     */
    private long simulatevalidkey = System.currentTimeMillis();
    /***********************仿真系统应用**********************/

    /**
     * 仿真标志位
     */
    private boolean issimulation = false;

    /**
     * 表示mv使用了哪些pv
     * mvusepv矩阵shape=(num_mv,num_pv)
     * 如mv1用了pv1,mv2用了pv2
     * 如[[1,0],
     * [0,1]]
     */
//    private int[][] matrixMvUsePv = null;


    /**
     * 表示FF使用了哪些pv
     * ffusepv矩阵shape=(num_ff,num_pv)
     * 如ff1用了pv1,ff2用了pv2
     * 如[[1,0],
     * [0,1]]
     */
//    private int[][] matrixFfUsePv = null;


    /**
     * 输入输出映射关系数量
     */
    private int numOfIOMappingRelation = 0;


    /**
     * 前馈输出映射关系数量
     */
//    private int numOfFOMappingRelation = 0;

    /**
     * 输入响应 shape=[pv][mv][resp_N]
     */
    private Double[][][] A_SimulatetimeseriseMatrix = null;
    /**
     * 前馈响应 shape=[pv][ff][resp_N]
     */
    private Double[][][] B_SimulatetimeseriseMatrix = null;

    private int[][] matrixSimulatePvUseMv = null;


    private Double[] simulatQ;
    private Double[] simulatR;
    private Double[] simulateAlpheTrajectoryCoefficients;

    private Double[] simulatedeadZones;
    private Double[] simulatefunelinitvalues;
    private Double[][] simulatefunneltype;

    /**
     * 输出数量
     */
    private Integer simulateOutpoints_p = 0;
    /**
     * 前馈数量
     */
    private Integer simulateFeedforwardpoints_v = 0;
    /**
     * 输入量数量
     */
    private Integer simulateInputpoints_m = 0;


    /**
     * 预测时域
     */
    private Integer predicttime_P = 12;
    /**
     * 单一控制输入未来控制M步增量(控制域)
     */
    private Integer controltime_M = 6;
    /**
     * 响应序列长度
     */
    private Integer timeserise_N = 40;
    /**
     * 控制周期
     */
    private Integer controlAPCOutCycle = 0;


    /**
     * 模型仿真计算出的dmv数据
     * shape=(num_of_pv,num_of_mv)
     */
    private double[][] backSimulateDmv;

    /**
     * 引脚
     */
    private List<ModlePin> modlePins;


    /**
     * 扩展引脚
     * 比如pv与mv的响应矩阵为:
     * 假设pv数量为p,mv数量为m
     * [ [pv1Tomv1 response shape=(N,1)],[..],...
     * [pv2Tomv1 response shape=(N,1)],[..],...
     * ....
     * ]
     * <p>
     * 扩展引脚是拆分把每行pv对应的每一个mv记入到扩extendModlePins
     * mv1    mv2
     * pv1  [pmr1],[pmr2],...
     * pv2  []     [pmr3],..
     * pv3 ....
     * 因此可以将这个pv1,pv1,pv2这样的顺序放入到extendModlePins中
     */
    private List<ModlePin> extendModlePins = new ArrayList<>();


    /**
     * 响应
     */
    private List<ResponTimeSerise> responTimeSerises;
    /**
     * 已经分类号的PV引脚
     */
    private List<ModlePin> categoryPVmodletag = null;
    /**
     * 已经分类号的SP引脚
     */
    private List<ModlePin> categorySPmodletag = null;
    /**
     * 已经分类号的MV引脚
     */
    private List<ModlePin> categoryMVmodletag = null;
    /**
     * 已经分类号的FF引脚
     */
    private List<ModlePin> categoryFFmodletag = null;

    private int[][] matrixPvUseMv = null;

    /**
     * 执行apc算法的仿真桥接器
     */
    private ExecutePythonBridge executePythonBridgeSimulate;

    /*******************仿真属性结束**************************/


    public SimulatControlModle(String simulatorbuilddir, Integer modleid, boolean issimulate) {
        this.simulatorbuilddir = simulatorbuilddir;
        this.modleid = modleid;
        this.issimulation = issimulate;
    }

    /**
     * 获取输入输出响应
     */
    private Double[] getSpecialIORespon(String pvpinname, String mvpinname) {
        for (ResponTimeSerise responTimeSerise : responTimeSerises) {
            if (responTimeSerise.getInputPins().equals(mvpinname) && responTimeSerise.getOutputPins().equals(pvpinname)) {
                return responTimeSerise.responOneTimeSeries(timeserise_N, controlAPCOutCycle);
            }
        }
        return null;
    }


    /**
     * 获取前馈输出响应
     */
    private Double[] getSpecialFORespon(String pvpinname, String ffpinname) {
        for (ResponTimeSerise responTimeSerise : responTimeSerises) {
            if (responTimeSerise.getInputPins().equals(ffpinname) && responTimeSerise.getOutputPins().equals(pvpinname)) {
                return responTimeSerise.responOneTimeSeries(timeserise_N, controlAPCOutCycle);
            }
        }
        return null;
    }


    /**
     * 计算说明：
     * 1、根据mvUsePv矩阵、numOfMappingRelation映射关系数量，构建输入、输出、前馈引脚数目
     * 2、构建出输入输出映射关系矩阵
     * 3、构建出前馈对输出的映射关系矩阵
     * 4、漏斗类型等
     */
    public void build() {

        /***
         * 输入输出响应对应矩阵
         * init A matrix
         * */
        A_SimulatetimeseriseMatrix = new Double[numOfIOMappingRelation][numOfIOMappingRelation][timeserise_N];

        /**pv*/
        for (int indexpv = 0; indexpv < numOfIOMappingRelation; ++indexpv) {
            /**mv*/
            for (int indexmv = 0; indexmv < numOfIOMappingRelation; ++indexmv) {
                Double[] initserise = new Double[timeserise_N];
                for (int indexn = 0; indexn < timeserise_N; indexn++) {
                    initserise[indexn] = 0d;
                }
                A_SimulatetimeseriseMatrix[indexpv][indexmv] = initserise;
            }
        }

        /***
         * 前馈输出对应矩阵
         * init B matrix
         * */
        if (simulateFeedforwardpoints_v > 0) {
            B_SimulatetimeseriseMatrix = new Double[numOfIOMappingRelation][simulateFeedforwardpoints_v][timeserise_N];
            for (int indexpv = 0; indexpv < numOfIOMappingRelation; ++indexpv) {
                for (int indexff = 0; indexff < simulateFeedforwardpoints_v; ++indexff) {
                    Double[] initserise = new Double[timeserise_N];
                    for (int indexn = 0; indexn < timeserise_N; indexn++) {
                        initserise[indexn] = 0d;
                    }
                    B_SimulatetimeseriseMatrix[indexpv][indexff] = initserise;
                }

            }
        }


        /***
         *1、fill respon into 输入输出respon 仿真 matrix
         *2、and init matrixSimulatePvUseMv
         *3、死区时间和漏洞初始值
         *
         * */


        /**仿真Q参数*/
        simulatQ = new Double[numOfIOMappingRelation];
        /**仿真R*/
        simulatR = new Double[numOfIOMappingRelation];
        /**仿真轨迹柔化系数*/
        simulateAlpheTrajectoryCoefficients = new Double[numOfIOMappingRelation];

        /**仿真死区*/
        simulatedeadZones = new Double[numOfIOMappingRelation];
        /**仿真漏斗初始值*/
        simulatefunelinitvalues = new Double[numOfIOMappingRelation];
        /**仿真漏斗类型*/
        simulatefunneltype = new Double[numOfIOMappingRelation][2];
        /**pv用了哪些mv,标记矩阵*/
        matrixSimulatePvUseMv = new int[numOfIOMappingRelation][numOfIOMappingRelation];
        int index4IOMappingRelation = 0;
        for (int indexpv = 0; indexpv < categoryPVmodletag.size(); ++indexpv) {
            for (int indexmv = 0; indexmv < categoryMVmodletag.size(); ++indexmv) {
                Double[] ioRespon = getSpecialIORespon(categoryPVmodletag.get(indexpv).getModlePinName(), categoryMVmodletag.get(indexmv).getModlePinName());
                if ((ioRespon != null) && (index4IOMappingRelation < numOfIOMappingRelation)) {
                    /**重构的响应矩阵*/
                    A_SimulatetimeseriseMatrix[index4IOMappingRelation][index4IOMappingRelation] = ioRespon;
                    /**重构剥离的pv用了哪些mv*/
                    matrixSimulatePvUseMv[index4IOMappingRelation][index4IOMappingRelation] = 1;
                    /**预测域系数*/
                    simulatQ[index4IOMappingRelation] = categoryPVmodletag.get(indexpv).getQ();
                    /**控制域参数*/
                    simulatR[index4IOMappingRelation] = categoryMVmodletag.get(indexmv).getR();
                    /**柔化系数*/
                    simulateAlpheTrajectoryCoefficients[index4IOMappingRelation] = categoryPVmodletag.get(indexpv).getReferTrajectoryCoef();
                    /***/
                    simulatedeadZones[index4IOMappingRelation]=categoryPVmodletag.get(indexpv).getDeadZone();
                    /***/
                    simulatefunelinitvalues[index4IOMappingRelation]=categoryPVmodletag.get(indexpv).getFunelinitValue();

                    /**漏斗类型*/
                    Double[] fnl = new Double[2];
                    if (categoryPVmodletag.get(indexpv).getFunneltype() != null) {
                        switch (categoryPVmodletag.get(indexpv).getFunneltype()) {
                            case ModlePin.TYPE_FUNNEL_FULL:
                                /**全漏斗*/
                                fnl[0] = 0d;
                                fnl[1] = 0d;
                                simulatefunneltype[index4IOMappingRelation] = fnl;
                                break;
                            case ModlePin.TYPE_FUNNEL_UP:
                                /**上漏斗*/
                                fnl[0] = 0d;
                                //乘负无穷
                                fnl[1] = 1d;
                                simulatefunneltype[index4IOMappingRelation] = fnl;
                                break;
                            case ModlePin.TYPE_FUNNEL_DOWN:
                                /**下漏斗*/
                                //乘正无穷
                                fnl[0] = 1d;
                                fnl[1] = 0d;
                                simulatefunneltype[index4IOMappingRelation] = fnl;
                                break;
                            default:
                                /**匹配不到就是全漏斗*/
                                fnl = new Double[2];
                                fnl[0] = 0d;
                                fnl[1] = 0d;
                                simulatefunneltype[index4IOMappingRelation] = fnl;
                        }
                    } else {
                        /**匹配不到就是全漏斗*/
                        fnl[0] = 0d;
                        fnl[1] = 0d;
                        simulatefunneltype[index4IOMappingRelation] = fnl;
                    }
                    extendModlePins.add(categoryPVmodletag.get(indexpv));
                    ++index4IOMappingRelation;
                }
            }
        }


        /**
         *fill respon into 前馈与输出 响应matrix
         *填入前馈输出响应矩阵
         * */
//        int index4FOMappingRelation = 0;
        for (int indexpv = 0; indexpv < numOfIOMappingRelation; ++indexpv) {
            for (int indexff = 0; indexff < categoryFFmodletag.size(); ++indexff) {
                Double[] foRespon = getSpecialFORespon(extendModlePins.get(indexpv).getModlePinName(), categoryFFmodletag.get(indexff).getModlePinName());
                if (foRespon != null) {
                    B_SimulatetimeseriseMatrix[indexpv][indexff] = foRespon;
//                    ++index4FOMappingRelation;
                }

            }

        }
        backSimulateDmv = new double[simulateOutpoints_p][simulateInputpoints_m];
        generateSimulatevalidkey();

        executePythonBridgeSimulate = new ExecutePythonBridge(simulatorbuilddir, "http://localhost:8080/AILab/pythonsimulate/modlebuild/" + modleid + ".do", modleid + "");
        if (issimulation) {
            executePythonBridgeSimulate.execute();
        }
    }


    public boolean isIssimulation() {
        return issimulation;
    }

    public void setIssimulation(boolean issimulation) {
        this.issimulation = issimulation;
    }


    public int getNumOfIOMappingRelation() {
        return numOfIOMappingRelation;
    }

    public void addNumOfIOMappingRelation() {
        ++numOfIOMappingRelation;
    }


    public int[][] getMatrixSimulatePvUseMv() {
        return matrixSimulatePvUseMv;
    }

    public long getSimulatevalidkey() {
        return simulatevalidkey;
    }

    public void generateSimulatevalidkey() {
        this.simulatevalidkey = System.currentTimeMillis();
    }


    public void setNumOfIOMappingRelation(int numOfIOMappingRelation) {
        this.numOfIOMappingRelation = numOfIOMappingRelation;
    }


    public Double[][][] getA_SimulatetimeseriseMatrix() {
        return A_SimulatetimeseriseMatrix;
    }

    public void setA_SimulatetimeseriseMatrix(Double[][][] a_SimulatetimeseriseMatrix) {
        A_SimulatetimeseriseMatrix = a_SimulatetimeseriseMatrix;
    }

    public Double[][][] getB_SimulatetimeseriseMatrix() {
        return B_SimulatetimeseriseMatrix;
    }

    public void setB_SimulatetimeseriseMatrix(Double[][][] b_SimulatetimeseriseMatrix) {
        B_SimulatetimeseriseMatrix = b_SimulatetimeseriseMatrix;
    }

    public void setMatrixSimulatePvUseMv(int[][] matrixSimulatePvUseMv) {
        this.matrixSimulatePvUseMv = matrixSimulatePvUseMv;
    }

    public Double[] getSimulatQ() {
        return simulatQ;
    }

    public void setSimulatQ(Double[] simulatQ) {
        this.simulatQ = simulatQ;
    }

    public Double[] getSimulatR() {
        return simulatR;
    }

    public void setSimulatR(Double[] simulatR) {
        this.simulatR = simulatR;
    }

    public Double[] getSimulateAlpheTrajectoryCoefficients() {
        return simulateAlpheTrajectoryCoefficients;
    }

    public void setSimulateAlpheTrajectoryCoefficients(Double[] simulateAlpheTrajectoryCoefficients) {
        this.simulateAlpheTrajectoryCoefficients = simulateAlpheTrajectoryCoefficients;
    }

    public Double[] getSimulatedeadZones() {
        return simulatedeadZones;
    }

    public void setSimulatedeadZones(Double[] simulatedeadZones) {
        this.simulatedeadZones = simulatedeadZones;
    }

    public Double[] getSimulatefunelinitvalues() {
        return simulatefunelinitvalues;
    }

    public void setSimulatefunelinitvalues(Double[] simulatefunelinitvalues) {
        this.simulatefunelinitvalues = simulatefunelinitvalues;
    }


    public Double[][] getSimulatefunneltype() {
        return simulatefunneltype;
    }

    public void setSimulatefunneltype(Double[][] simulatefunneltype) {
        this.simulatefunneltype = simulatefunneltype;
    }

    public Integer getSimulateOutpoints_p() {
        return simulateOutpoints_p;
    }

    public void setSimulateOutpoints_p(Integer simulateOutpoints_p) {
        this.simulateOutpoints_p = simulateOutpoints_p;
    }

    public Integer getSimulateFeedforwardpoints_v() {
        return simulateFeedforwardpoints_v;
    }

    public void setSimulateFeedforwardpoints_v(Integer simulateFeedforwardpoints_v) {
        this.simulateFeedforwardpoints_v = simulateFeedforwardpoints_v;
    }

    public Integer getSimulateInputpoints_m() {
        return simulateInputpoints_m;
    }

    public void setSimulateInputpoints_m(Integer simulateInputpoints_m) {
        this.simulateInputpoints_m = simulateInputpoints_m;
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


    public Integer getTimeserise_N() {
        return timeserise_N;
    }

    public void setTimeserise_N(Integer timeserise_N) {
        this.timeserise_N = timeserise_N;
    }

    public Integer getPredicttime_P() {
        return predicttime_P;
    }

    public void setPredicttime_P(Integer predicttime_P) {
        this.predicttime_P = predicttime_P;
    }

    public Integer getControltime_M() {
        return controltime_M;
    }

    public void setControltime_M(Integer controltime_M) {
        this.controltime_M = controltime_M;
    }

    public Integer getControlAPCOutCycle() {
        return controlAPCOutCycle;
    }

    public void setControlAPCOutCycle(Integer controlAPCOutCycle) {
        this.controlAPCOutCycle = controlAPCOutCycle;
    }

    public List<ModlePin> getCategoryPVmodletag() {
        return categoryPVmodletag;
    }

    public void setCategoryPVmodletag(List<ModlePin> categoryPVmodletag) {
        this.categoryPVmodletag = categoryPVmodletag;
    }

    public List<ModlePin> getCategorySPmodletag() {
        return categorySPmodletag;
    }

    public void setCategorySPmodletag(List<ModlePin> categorySPmodletag) {
        this.categorySPmodletag = categorySPmodletag;
    }

    public List<ModlePin> getCategoryMVmodletag() {
        return categoryMVmodletag;
    }

    public void setCategoryMVmodletag(List<ModlePin> categoryMVmodletag) {
        this.categoryMVmodletag = categoryMVmodletag;
    }

    public List<ModlePin> getCategoryFFmodletag() {
        return categoryFFmodletag;
    }

    public void setCategoryFFmodletag(List<ModlePin> categoryFFmodletag) {
        this.categoryFFmodletag = categoryFFmodletag;
    }

    public ExecutePythonBridge getExecutePythonBridgeSimulate() {
        return executePythonBridgeSimulate;
    }

    public void setExecutePythonBridgeSimulate(ExecutePythonBridge executePythonBridgeSimulate) {
        this.executePythonBridgeSimulate = executePythonBridgeSimulate;
    }

    public double[][] getBackSimulateDmv() {
        return backSimulateDmv;
    }

    /**
     * 更新仿真器dmv值
     */
    public boolean uodateBackSimulateDmv(double[] simulateDmv) {
        int indexmappingration = 0;
        try {
            for (int indexpv = 0; indexpv < simulateOutpoints_p; indexpv++) {

                for (int indexmv = 0; indexmv < simulateInputpoints_m; indexmv++) {
                    if (matrixPvUseMv[indexpv][indexmv] == 1) {
                        this.backSimulateDmv[indexpv][indexmv] = simulateDmv[indexmappingration];
                        ++indexmappingration;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    public void setMatrixPvUseMv(int[][] matrixPvUseMv) {
        this.matrixPvUseMv = matrixPvUseMv;
    }
}

package hs.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.Bean.ControlModle;
import hs.Bean.ModleConstainer;
import hs.Bean.SimulatControlModle;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/8/7 7:52
 */
@RestController("pythonSimulateController")
@RequestMapping("/pythonsimulate")
public class PythonSimulateController {
    public static Logger logger = Logger.getLogger(PythonSimulateController.class);
    private ModleConstainer modleConstainer;


    @RequestMapping("/modlebuild/{id}")
    public String ModelBuild(@PathVariable("id") int id) {

        ControlModle modle = modleConstainer.getRunnableModulepool().get(id);
        JSONObject jsonObject = new JSONObject();

        /**
         *计算说明：
         * 1、根据mvUsePv矩阵、numOfMappingRelation映射关系数量，构建输入、输出、前馈引脚数目
         * 2、构建出输入输出映射关系矩阵
         *
         * 3、构建出前馈对输出的映射关系矩阵
         *
         * */


        /**
         * base
         * */
        SimulatControlModle simulatControlModle = modle.getSimulatControlModle();
        jsonObject.put("m", simulatControlModle.getSimulateInputpoints_m());//映射数量
        jsonObject.put("p", simulatControlModle.getSimulateOutpoints_p());//映射数量
        jsonObject.put("M", simulatControlModle.getControltime_M());
        jsonObject.put("P", simulatControlModle.getPredicttime_P());
        jsonObject.put("N", simulatControlModle.getTimeserise_N());
        jsonObject.put("fnum", simulatControlModle.getSimulateFeedforwardpoints_v());
        jsonObject.put("pvusemv", simulatControlModle.getMatrixSimulatePvUseMv());
        jsonObject.put("APCOutCycle", simulatControlModle.getControlAPCOutCycle());
        jsonObject.put("enable", simulatControlModle.isIssimulation() ? 1 : 0);//是否仿真
        jsonObject.put("validekey", simulatControlModle.getSimulatevalidkey());
        jsonObject.put("funneltype", simulatControlModle.getSimulatefunneltype());


        /**
         *mv
         * */
        if (modle.getCategoryMVmodletag().size() != 0) {
            jsonObject.put("A", simulatControlModle.getA_SimulatetimeseriseMatrix());
            jsonObject.put("origionA",modle.getA_RunnabletimeseriseMatrix());
            /**
             * 添加映射矩阵
             * */
            jsonObject.put("pvmvmapping",modle.getMaskMatrixRunnablePVUseMV());
            jsonObject.put("pvmveffect",modle.getMaskMatrixRunnablePvEffectMv());//pv对mv输出占比影响权重

        }

        /**
         *ff
         */
        if (modle.getCategoryFFmodletag().size() != 0) {
            jsonObject.put("B", simulatControlModle.getB_SimulatetimeseriseMatrix());
            jsonObject.put("origionB",modle.getB_RunnabletimeseriseMatrix());
            jsonObject.put("pvffmapping",modle.getMaskMatrixRunnablePVUseFF());
        }

        jsonObject.put("Q", simulatControlModle.getSimulatQ());
        jsonObject.put("R", simulatControlModle.getSimulatR());
        jsonObject.put("alphe", simulatControlModle.getSimulateAlpheTrajectoryCoefficients());//柔化系数
        jsonObject.put("alphemethod", simulatControlModle.getSimulateAlpheTrajectoryCoefmethods());//柔化系数
        return jsonObject.toJSONString();
    }


    @RequestMapping("/opcread/{id}")
    public String ModelReadData(@PathVariable("id") int id) {
        ControlModle controlModle = modleConstainer.getRunnableModulepool().get(id);
        JSONObject realjson=controlModle.getSimulatControlModle().getRealSimulateData();//getrealSimulateData();
        return realjson.toJSONString();
    }


    @RequestMapping("/opcwrite")
    @ResponseBody
    public String ModelWriteData(@RequestParam("id") int id, @RequestParam("U") Double[] u, @RequestParam("validekey") long validekey) {

        ControlModle controlModle = modleConstainer.getRunnableModulepool().get(id);
            if(controlModle.getRunstyle().equals(ControlModle.RUNSTYLEBYMANUL)){
                if (validekey != controlModle.getSimulatControlModle().getSimulatevalidkey()) {
                    return "false";
                }
                if (!controlModle.writeData(u)) {
                    return "false";
                }
            }else {
                return "false";
            }
        return "true";
    }



    /**
     * 更新模型的状态数据
     * mv 该mv为计算出现来的各个mv预测多步的数据
     */
//    @RequestMapping("/updateModleData")
//    public String ModelUpdateData(@RequestParam("id") int id, @RequestParam(value = "data", required = false) String data, @RequestParam("validekey") long validekey) {
//        //,@RequestParam("predict") double[] predictpv,@RequestParam("mv") double[]mv,@RequestParam("e") double[]e,@RequestParam("funelupAnddown") double[][]funelupAnddown,@RequestParam("dmv") double[] dmv
//        try {
//
//            ControlModle controlModle = modleConstainer.getRunnableModulepool().get(id);
//            if (controlModle.getSimulatControlModle().getSimulatevalidkey() != validekey) {
//                return "false";
//            }
//            JSONObject modlestatus = JSONObject.parseObject(data);
//            JSONArray dmvJson = modlestatus.getJSONArray("dmv");
//            int m = controlModle.getSimulatControlModle().getSimulateInputpoints_m();
//            double[] dmvArray = new double[m];
//            for (int i = 0; i < m; i++) {
//                dmvArray[i] = dmvJson.getDouble(i);
//            }
//            if (!controlModle.getSimulatControlModle().updateBackSimulateComputeResult(dmvArray)) {
//                return "false";
//            }
//            return "true";
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//        return "false";
//    }


    @RequestMapping("/updateModleData")
    @ResponseBody
    public String ModelUpdateData(@RequestParam("id") int id, @RequestParam(value = "data", required = false) String data, @RequestParam("validekey") long validekey) {
        //,@RequestParam("predict") double[] predictpv,@RequestParam("mv") double[]mv,@RequestParam("e") double[]e,@RequestParam("funelupAnddown") double[][]funelupAnddown,@RequestParam("dmv") double[] dmv
        try {
            ControlModle controlModle = modleConstainer.getRunnableModulepool().get(id);
            if (controlModle.getSimulatControlModle().getSimulatevalidkey() != validekey) {
                return "false";
            }
            JSONObject modlestatus = JSONObject.parseObject(data);
            JSONArray predictpvJson = modlestatus.getJSONArray("predict");
//            JSONArray mvJson = modlestatus.getJSONArray("mv");
            JSONArray eJson = modlestatus.getJSONArray("e");
            JSONArray funelupAnddownJson = modlestatus.getJSONArray("funelupAnddown");
            JSONArray dmvJson = modlestatus.getJSONArray("dmv");
            JSONArray dffJson=modlestatus.getJSONArray("dff");

            int p = controlModle.getNumOfRunnablePVPins_pp();
            int m = controlModle.getNumOfRunnableMVpins_mm();
            int v=controlModle.getNumOfRunnableFFpins_vv();
            int N = controlModle.getTimeserise_N();

            double[] predictpvArray = new double[p * N];
            double[][] funelupAnddownArray = new double[2][p * N];
            double[] eArray = new double[p];
            double[] dmvArray = new double[m];

            double[] dffArray=null;
            if(v!=0){
                dffArray=new double[v];
            }
            for (int i = 0; i < p * N; i++) {
                predictpvArray[i] = predictpvJson.getDouble(i);
                funelupAnddownArray[0][i] = funelupAnddownJson.getJSONArray(0).getDouble(i);
                funelupAnddownArray[1][i] = funelupAnddownJson.getJSONArray(1).getDouble(i);
            }

            for (int i = 0; i < p; i++) {
                eArray[i] = eJson.getDouble(i);
            }

            for (int i = 0; i < m; i++) {
                dmvArray[i] = dmvJson.getDouble(i);
            }
            for(int i=0;i<v;++i){
                dffArray[i]=dffJson.getDouble(i);
            }

            if (!controlModle.getSimulatControlModle().updateModleComputeResult(predictpvArray, funelupAnddownArray, dmvArray, eArray,dffArray)) {
                return "false";
            }

            return "true";

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "false";
    }


    @Autowired
    @Qualifier("apcmodles")
    public void setModleConstainer(ModleConstainer modleConstainer) {
        this.modleConstainer = modleConstainer;
    }
}

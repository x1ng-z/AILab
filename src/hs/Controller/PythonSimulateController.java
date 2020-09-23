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
        }

        /**
         *ff
         */
        if (modle.getCategoryFFmodletag().size() != 0) {
            jsonObject.put("B", simulatControlModle.getB_SimulatetimeseriseMatrix());
        }

        jsonObject.put("Q", simulatControlModle.getSimulatQ());
        jsonObject.put("R", simulatControlModle.getSimulatR());
        jsonObject.put("alphe", simulatControlModle.getSimulateAlpheTrajectoryCoefficients());//柔化系数
        return jsonObject.toJSONString();
    }


    @RequestMapping("/opcread/{id}")
    public String ModelReadData(@PathVariable("id") int id) {
        ControlModle controlModle = modleConstainer.getRunnableModulepool().get(id);
        JSONObject realjson=controlModle.getrealSimulateData();
        return realjson.toJSONString();
    }


    /**
     * 更新模型的状态数据
     * mv 该mv为计算出现来的各个mv预测多步的数据
     */
    @RequestMapping("/updateModleData")
    public String ModelUpdateData(@RequestParam("id") int id, @RequestParam(value = "data", required = false) String data, @RequestParam("validekey") long validekey) {
        //,@RequestParam("predict") double[] predictpv,@RequestParam("mv") double[]mv,@RequestParam("e") double[]e,@RequestParam("funelupAnddown") double[][]funelupAnddown,@RequestParam("dmv") double[] dmv
        try {

            ControlModle controlModle = modleConstainer.getRunnableModulepool().get(id);
            if (controlModle.getSimulatControlModle().getSimulatevalidkey() != validekey) {
                return "false";
            }
            JSONObject modlestatus = JSONObject.parseObject(data);
            JSONArray dmvJson = modlestatus.getJSONArray("dmv");
            int m = controlModle.getSimulatControlModle().getSimulateInputpoints_m();
            double[] dmvArray = new double[m];
            for (int i = 0; i < m; i++) {
                dmvArray[i] = dmvJson.getDouble(i);
            }
            if (!controlModle.getSimulatControlModle().updateBackSimulateDmv(dmvArray)) {
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

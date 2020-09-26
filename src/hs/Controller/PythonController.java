package hs.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.Bean.ControlModle;
import hs.Bean.ModleConstainer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/19 15:09
 */
@Controller("pythoncontrol")
@RequestMapping("/python")
public class PythonController {
    public static Logger logger = Logger.getLogger(ModleController.class);
    private ModleConstainer modleConstainer;

    @RequestMapping("/modlebuild/{id}")
    @ResponseBody
    public String ModelBuild(@PathVariable("id") int id) {

//        ModelAndView andView=new ModelAndView();
        ControlModle modle = modleConstainer.getRunnableModulepool().get(id);

        JSONObject jsonObject = new JSONObject();
        /**
         * base
         * */
        jsonObject.put("m", modle.getNumOfRunnableMVpins_mm());
        jsonObject.put("p", modle.getNumOfRunnablePVPins_pp());
        jsonObject.put("M", modle.getControltime_M());
        jsonObject.put("P", modle.getPredicttime_P());
        jsonObject.put("N", modle.getTimeserise_N());
        jsonObject.put("fnum", modle.getNumOfRunnableFFpins_vv());
        jsonObject.put("pvusemv", modle.getMaskMatrixRunnablePVUseMV());
        jsonObject.put("APCOutCycle", modle.getControlAPCOutCycle());
        jsonObject.put("enable", modle.getModleEnable());
        jsonObject.put("validekey", modle.getValidkey());
        jsonObject.put("funneltype", modle.getFunneltype());


        /**
         *mv
         * */
        if (modle.getNumOfRunnablePVPins_pp() != 0) {
            jsonObject.put("A", modle.getA_RunnabletimeseriseMatrix());
        }

        /**
         *ff
         */
        if (modle.getNumOfRunnableFFpins_vv() != 0) {
            jsonObject.put("B", modle.getB_RunnabletimeseriseMatrix());
        }

        jsonObject.put("Q", modle.getQ());
        jsonObject.put("R", modle.getR());
        jsonObject.put("alphe", modle.getAlpheTrajectoryCoefficients());
        jsonObject.put("alphemethod", modle.getAlpheTrajectoryCoefmethod());


        return jsonObject.toJSONString();
    }


    @RequestMapping("/opcread/{id}")
    @ResponseBody
    public String ModelReadData(@PathVariable("id") int id) {
        ControlModle controlModle = modleConstainer.getRunnableModulepool().get(id);
        return controlModle.getrealData().toJSONString();
    }

    @RequestMapping("/opcwrite")
    @ResponseBody
    public String ModelWriteData(@RequestParam("id") int id, @RequestParam("U") Double[] u, @RequestParam("validekey") long validekey) {

        ControlModle controlModle = modleConstainer.getRunnableModulepool().get(id);
        if (validekey != controlModle.getValidkey()) {
            return "false";
        }
        if (!controlModle.writeData(u)) {
            return "false";
        }

        return "true";
    }

    /**
     * 更新模型的状态数据
     * mv 该mv为计算出现来的各个mv预测多步的数据
     */
    @RequestMapping("/updateModleData")
    @ResponseBody
    public String ModelUpdateData(@RequestParam("id") int id, @RequestParam(value = "data", required = false) String data, @RequestParam("validekey") long validekey) {
        //,@RequestParam("predict") double[] predictpv,@RequestParam("mv") double[]mv,@RequestParam("e") double[]e,@RequestParam("funelupAnddown") double[][]funelupAnddown,@RequestParam("dmv") double[] dmv
        try {
            ControlModle controlModle = modleConstainer.getRunnableModulepool().get(id);
            if (controlModle.getValidkey() != validekey) {
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

            if (!controlModle.updateModleReal(predictpvArray, funelupAnddownArray, dmvArray, eArray,dffArray)) {
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

package hs.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.Bean.ControlModle;
import hs.Bean.ModleConstainer;
import hs.Bean.ModlePin;
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
        ControlModle modle = modleConstainer.getModulepool().get(id);

        JSONObject jsonObject = new JSONObject();
        /**
         * base
         * */
        jsonObject.put("m", modle.getInputpoints_m());
        jsonObject.put("p", modle.getOutpoints_p());
        jsonObject.put("M", modle.getControltime_M());
        jsonObject.put("P", modle.getPredicttime_P());
        jsonObject.put("N", modle.getTimeserise_N());
        jsonObject.put("fnum", modle.getFeedforwardpoints_v());
        jsonObject.put("pvusemv", modle.getPvusemv());
        jsonObject.put("APCOutCycle", modle.getControlAPCOutCycle());
        jsonObject.put("enable", modle.getModleEnable());
        jsonObject.put("validekey", modle.getValidkey());

        /**funnel type*/

        double[][] funneltype = new double[modle.getCategoryPVmodletag().size()][2];
        int indexpin = 0;
        for (ModlePin pin : modle.getCategoryPVmodletag()) {
            if (pin.getFunneltype() != null) {
                if (pin.getFunneltype().equals("fullfunnel")) {
                    double[] fnl = new double[2];
                    fnl[0] = 0;
                    fnl[1] = 0;
                    funneltype[indexpin] = fnl;
                } else if (pin.getFunneltype().equals("upfunnel")) {
                    double[] fnl = new double[2];
                    fnl[0] = 0;
                    fnl[1] = 1;//乘负无穷
                    funneltype[indexpin] = fnl;
                } else if (pin.getFunneltype().equals("downfunnel")) {
                    double[] fnl = new double[2];
                    fnl[0] = 1;//乘正无穷
                    fnl[1] = 0;
                    funneltype[indexpin] = fnl;
                } else {
                    //匹配不到就是全漏斗
                    double[] fnl = new double[2];
                    fnl[0] = 0;
                    fnl[1] = 0;
                    funneltype[indexpin] = fnl;
                }
            } else {
                //匹配不到就是全漏斗
                double[] fnl = new double[2];
                fnl[0] = 0;
                fnl[1] = 0;
                funneltype[indexpin] = fnl;
            }
            ++indexpin;
        }
        jsonObject.put("funneltype", funneltype);


        /**
         *mv
         * */
        if (modle.getCategoryMVmodletag().size() != 0) {
            jsonObject.put("A", modle.getA_timeseriseMatrix());
        }

        /**
         *ff
         */
        if (modle.getCategoryFFmodletag().size() != 0) {
            jsonObject.put("B", modle.getB_timeseriseMatrix());
        }

        jsonObject.put("Q", modle.getQ());
        jsonObject.put("R", modle.getR());
        jsonObject.put("alphe", modle.getAlpheTrajectoryCoefficients());


        return jsonObject.toJSONString();
    }


    @RequestMapping("/opcread/{id}")
    @ResponseBody
    public String ModelReadData(@PathVariable("id") int id) {
        ControlModle controlModle = modleConstainer.getModulepool().get(id);
        return controlModle.getrealData().toJSONString();
    }

    @RequestMapping("/opcwrite")
    @ResponseBody
    public String ModelWriteData(@RequestParam("id") int id, @RequestParam("U") Double[] u, @RequestParam("validekey") long validekey) {

        ControlModle controlModle = modleConstainer.getModulepool().get(id);
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

            ControlModle controlModle = modleConstainer.getModulepool().get(id);
            if (controlModle.getValidkey() != validekey) {
                return "false";
            }
            JSONObject modlestatus = JSONObject.parseObject(data);
            JSONArray predictpvJson = modlestatus.getJSONArray("predict");
            JSONArray mvJson = modlestatus.getJSONArray("mv");
            JSONArray eJson = modlestatus.getJSONArray("e");
            JSONArray funelupAnddownJson = modlestatus.getJSONArray("funelupAnddown");
            JSONArray dmvJson = modlestatus.getJSONArray("dmv");

            int p = controlModle.getCategoryPVmodletag().size();
            int m = controlModle.getCategoryMVmodletag().size();
            int N = controlModle.getTimeserise_N();

            double[] predictpvArray = new double[p * N];
            double[][] funelupAnddownArray = new double[2][p * N];
            double[] eArray = new double[p];
            double[] dmvArray = new double[m];

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

            if (!controlModle.updateModleReal(predictpvArray, funelupAnddownArray, dmvArray, eArray)) {
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

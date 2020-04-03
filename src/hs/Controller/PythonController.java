package hs.Controller;

import com.alibaba.fastjson.JSONObject;
import hs.Bean.ControlModle;
import hs.Bean.ModleConstainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/19 15:09
 */
@Controller("pythoncontrol")
@RequestMapping("/python")
public class PythonController {
    private ModleConstainer modleConstainer;
    @RequestMapping("/modlebuild/{id}")
    @ResponseBody
    public String ModelBuild(@PathVariable("id") int id) {

//        ModelAndView andView=new ModelAndView();
        ControlModle modle = modleConstainer.getModules().get(id);

        JSONObject jsonObject = new JSONObject();
        /**
         * mv
         * */
        jsonObject.put("m", modle.getInputpoints_m());
        jsonObject.put("p", modle.getOutpoints_p());
        jsonObject.put("M", modle.getControltime_M());
        jsonObject.put("P", modle.getPredicttime_P());
        jsonObject.put("N", modle.getTimeserise_N());
        jsonObject.put("f", modle.getFeedforwardpoints_v());
        jsonObject.put("APCOutCycle",modle.getControlAPCOutCycle());


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
        jsonObject.put("R",modle.getR());
        return jsonObject.toJSONString();
    }


    @RequestMapping("/opcread/{id}")
    @ResponseBody
    public String ModelReadData(@PathVariable("id") int id) {
        ControlModle controlModle=modleConstainer.getModules().get(id);
        return controlModle.getrealData().toJSONString();
    }

    @RequestMapping("/opcwrite")
    @ResponseBody
    public String ModelWriteData(@RequestParam("id") int id,@RequestParam("U") Double[] u) {
        ControlModle controlModle=modleConstainer.getModules().get(id);
            if(!controlModle.writeData(u)){
                return "false";
            }

        return "true";
    }




    @Autowired
    @Qualifier("apcmodles")
    public void setModleConstainer(ModleConstainer modleConstainer) {
        this.modleConstainer = modleConstainer;
    }

}

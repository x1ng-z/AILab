package hs.Controller;

import com.alibaba.fastjson.JSONObject;
import hs.Bean.ControlModle;
import hs.Bean.ModleConstainer;
import hs.Bean.ModleTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

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
        jsonObject.put("m", modle.getInputpoints());
        jsonObject.put("p", modle.getOutpoints());
        jsonObject.put("M", modle.getControltime());
        jsonObject.put("P", modle.getPredicttime());
        jsonObject.put("N", modle.getTimeserisN());
        jsonObject.put("f", modle.getFeedforwardpoints());

        /**
         *mv
         * */
        if (modle.getMvsort().size() != 0) {
            jsonObject.put("mv", modle.getMvtimeserise());
        }

        /**
         *ff
         */
        if (modle.getFfsort().size() != 0) {
            jsonObject.put("ff", modle.getFftimeserise());
            //jsonObject.put("deltaff",modle);
        }


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
        int loop=0;
        for(Integer tgid:controlModle.getMvsort()){
            if(!controlModle.writeData(tgid,u[loop++])){
                return "false";
            }
        }
        return "true";
    }


    @Autowired
    @Qualifier("apcmodles")
    public void setModleConstainer(ModleConstainer modleConstainer) {
        this.modleConstainer = modleConstainer;
    }

}

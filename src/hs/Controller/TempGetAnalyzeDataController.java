package hs.Controller;

import com.alibaba.fastjson.JSONObject;
import hs.Bean.ModlePin;
import hs.Opc.OpcServicConstainer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/8/31 6:19
 */
//@RestController
//@RequestMapping("/analyze")
public class TempGetAnalyzeDataController {
    public static Logger logger = Logger.getLogger(TempGetAnalyzeDataController.class);
    @Autowired
    @NonNull
    private OpcServicConstainer opcServicConstainer;

    private ModlePin stonenum = null;

    private ModlePin stoneArea = null;

    @PostConstruct
    public void selfinit() {

        stonenum = new ModlePin();
        stonenum.setModlePinName("vedioanalyze");
        stonenum.setResource("opc192.168.156.27");
        stonenum.setModleOpcTag("DCS.APC.SPSB.SPSB_1");
        opcServicConstainer.registerModlePinAndComponent(stonenum);

                                                                                                                                                                                                                                                            stoneArea = new ModlePin();
        stoneArea.setModlePinName("vedioanalyze");
        stoneArea.setResource("opc192.168.156.27");
        stoneArea.setModleOpcTag("DCS.APC.SPSB.SPSB_2");
        opcServicConstainer.registerModlePinAndComponent(stoneArea);

    }


//    "192.168.156.27"

    @RequestMapping("/rawbelt")
    public String writeanalyzedata(HttpServletRequest httpServletRequest) {
        JSONObject jsonObject = new JSONObject();
        try {
            String valuestoneNum = httpServletRequest.getParameter("stoneNum");
            String valuestoneArea = httpServletRequest.getParameter("stoneArea");

            logger.debug("valuestoneNum=" + valuestoneNum + ",valuestoneArea=" + valuestoneArea);

            opcServicConstainer.writeModlePinValue(stonenum, Double.valueOf(valuestoneNum));
            opcServicConstainer.writeModlePinValue(stoneArea, Double.valueOf(valuestoneArea));
            jsonObject.put("msg", "success");
            return jsonObject.toJSONString();
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
        }
        jsonObject.put("msg", "failed");

        return jsonObject.toJSONString();
    }



    @PreDestroy
    public void selfclose() {

        opcServicConstainer.unregisterModlePinAndComponent(stonenum);

        opcServicConstainer.unregisterModlePinAndComponent(stoneArea);

    }

}

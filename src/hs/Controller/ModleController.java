package hs.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.Bean.BaseConf;
import hs.Bean.ControlModle;
import hs.Bean.ModlePin;
import hs.Bean.ResponTimeSerise;
import hs.Dao.Service.ModleServe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/19 14:11
 */

@Controller("modle")
@RequestMapping("/modle")
public class ModleController {

    @Autowired
    private BaseConf baseConf;
    @Autowired
    private ModleServe modleServe;

    @RequestMapping("/newmodle")
    public ModelAndView newModel(){
        ModelAndView mv=new ModelAndView();
        mv.setViewName("newModle");
        JSONArray mvrespon= new JSONArray();
        JSONArray ffrespon= new JSONArray();

        List<Integer> pvlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getPv();++i){
            pvlist.add(i);
        }

        List<Integer> fflist=new ArrayList<>();
        for(int i=1;i<=baseConf.getFf();++i){
            fflist.add(i);

            JSONObject mvjson=new JSONObject();
            mvjson.put("ff","ff"+i);
            for(int j=1;j<=baseConf.getPv();++j){
                mvjson.put("pv"+j,"");
            }
            ffrespon.add(mvjson);

        }



        List<Integer> mvlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getMv();++i){
            mvlist.add(i);

            JSONObject mvjson=new JSONObject();
            mvjson.put("mv","mv"+i);
            for(int j=1;j<=baseConf.getPv();++j){
                mvjson.put("pv"+j,"");
            }
            mvrespon.add(mvjson);

        }

        mv.addObject("pvlist",pvlist);
        mv.addObject("fflist",fflist);
        mv.addObject("mvlist",mvlist);
        mv.addObject("mvresp",mvrespon.toJSONString());
        mv.addObject("ffresp",ffrespon.toJSONString());

        return mv;
    }

    @RequestMapping("/savemodle")
    @ResponseBody
    public String saveModel(@RequestParam("modle")String modle,@RequestParam("mvresp") String mvresp,@RequestParam("ffresp") String ffresp){//
        JSONObject modlejsonObject =JSON.parseObject(modle);

        ControlModle controlModle=new ControlModle();

        controlModle.setModleName(modlejsonObject.getString("modleName"));
        controlModle.setPredicttime_P(Integer.valueOf(modlejsonObject.getString("P")));
        controlModle.setControltime_M(Integer.valueOf(modlejsonObject.getString("M")));
        controlModle.setTimeserise_N(Integer.valueOf(modlejsonObject.getString("N")));
        controlModle.setControlAPCOutCycle(Integer.valueOf(modlejsonObject.getString("O")));
        modleServe.insertModle(controlModle);
        controlModle.setModlePins(new ArrayList<ModlePin>());
        for(int i=1;i<= baseConf.getPv();i++){

            String pvTag=modlejsonObject.getString("pv"+i);
            String QTag=modlejsonObject.getString("q"+i);
            String spTag=modlejsonObject.getString("sp"+i);
          //pvTag!=null&&pvTag.trim()!=""
            if((pvTag!=null&&!pvTag.trim().equals(""))&&(QTag!=null&&!QTag.trim().equals(""))&&(spTag!=null&&!spTag.trim().equals(""))){

                ModlePin pvpin=new ModlePin();
                pvpin.setResource("opc");
                pvpin.setModleOpcTag(pvTag);
                pvpin.setQ(Double.valueOf(QTag));
                pvpin.setReference_modleId(controlModle.getModleId());
                pvpin.setModlePinName("pv"+i);
                controlModle.getModlePins().add(pvpin);

                ModlePin sppin=new ModlePin();
                sppin.setModleOpcTag(spTag);
                sppin.setResource("opc");
                sppin.setReference_modleId(controlModle.getModleId());
                sppin.setModlePinName("sp"+i);
                controlModle.getModlePins().add(sppin);

            }
        }


        for(int i=1;i<= baseConf.getMv();i++){
            String mv=modlejsonObject.getString("mv"+i);
            String r=modlejsonObject.getString("r"+i);
            String mvfb=modlejsonObject.getString("mvfb"+i);
            String mvup=modlejsonObject.getString("mvup"+i);
            String mvdown=modlejsonObject.getString("mvdown"+i);

            String mvupresoure=modlejsonObject.getString("mvup"+i+"resource");
            String mvdownresource=modlejsonObject.getString("mvdown"+i+"resource");

            if(mv!=null&&!mv.trim().equals("")){
                ModlePin mvpin=new ModlePin();
                mvpin.setR(Double.valueOf(r));
                mvpin.setResource("opc");
                mvpin.setReference_modleId(controlModle.getModleId());
                mvpin.setModleOpcTag(mv);
                mvpin.setModlePinName("mv"+i);
                controlModle.getModlePins().add(mvpin);


                ModlePin mvfbpin=new ModlePin();
                mvfbpin.setReference_modleId(controlModle.getModleId());
                mvfbpin.setModlePinName("mvfb"+i);
                mvfbpin.setResource("opc");
                mvfbpin.setModleOpcTag(mvfb);
                controlModle.getModlePins().add(mvfbpin);


                ModlePin mvuppin=new ModlePin();
                mvuppin.setReference_modleId(controlModle.getModleId());
                mvuppin.setModlePinName("mvup"+i);
                mvuppin.setResource(mvupresoure);
                mvuppin.setModleOpcTag(mvup);
                controlModle.getModlePins().add(mvuppin);

                ModlePin mvdownpin=new ModlePin();
                mvdownpin.setReference_modleId(controlModle.getModleId());
                mvdownpin.setModlePinName("mvdown"+i);
                mvdownpin.setResource(mvdownresource);
                mvdownpin.setModleOpcTag(mvdown);
                controlModle.getModlePins().add(mvdownpin);
            }

        }



        for(int i=1;i<= baseConf.getFf();i++){
            String ff=modlejsonObject.getString("ff"+i);
            String ffup=modlejsonObject.getString("ffup"+i);
            String ffdown=modlejsonObject.getString("ffdown"+i);

            String ffupresource=modlejsonObject.getString("ffup"+i+"resource");
            String ffdownresource=modlejsonObject.getString("ffdown"+i+"resource");//ffdwon4resource
            if(ff!=null&&!ff.trim().equals("")){
                ModlePin ffpin=new ModlePin();
                ffpin.setReference_modleId(controlModle.getModleId());
                ffpin.setModlePinName("ff"+i);
                ffpin.setResource("opc");
                ffpin.setModleOpcTag(ff);
                controlModle.getModlePins().add(ffpin);


                ModlePin ffuppin=new ModlePin();
                ffuppin.setReference_modleId(controlModle.getModleId());
                ffuppin.setModlePinName("ffup"+i);
                ffuppin.setResource(ffupresource);
                ffuppin.setModleOpcTag(ffup);
                controlModle.getModlePins().add(ffuppin);


                ModlePin ffdownpin=new ModlePin();
                ffdownpin.setReference_modleId(controlModle.getModleId());
                ffdownpin.setModlePinName("ffdown"+i);
                ffdownpin.setResource(ffdownresource);
                ffdownpin.setModleOpcTag(ffdown);
                controlModle.getModlePins().add(ffdownpin);

            }


        }

        if(controlModle.getModlePins().size()!=0){
            modleServe.insertModlePins(controlModle);
        }

        List<ResponTimeSerise> responTimeSeriseArrayList=new ArrayList<>();

        JSONArray mvrespjsonObject =JSONArray.parseArray(mvresp);
        for(int i=0;i<baseConf.getMv();++i){
            JSONObject mvrespjsonObjectJSONObject=mvrespjsonObject.getJSONObject(i);
            String mvnamepin=mvrespjsonObjectJSONObject.getString("mv");
            for(int j=1;j<=baseConf.getPv();++j){
                if (!mvrespjsonObjectJSONObject.getString("pv"+j).trim().equals("")){
                    ResponTimeSerise responTimeSerise=new ResponTimeSerise();
                    responTimeSerise.setInputPins(mvnamepin);
                    responTimeSerise.setOutputPins("pv"+j);
                    responTimeSerise.setRefrencemodleId(controlModle.getModleId());
                    responTimeSerise.setStepRespJson(mvrespjsonObjectJSONObject.getString("pv"+j).trim());
                    responTimeSeriseArrayList.add(responTimeSerise);
                }
            }

        }


        JSONArray ffrespjsonObject =JSONArray.parseArray(ffresp);
        for(int i=0;i<baseConf.getFf();++i){
            JSONObject ffrespjsonObjectJSONObject=ffrespjsonObject.getJSONObject(i);
            String ffnamepin=ffrespjsonObjectJSONObject.getString("ff");
            for(int j=1;j<=baseConf.getPv();++j){
                if (!ffrespjsonObjectJSONObject.getString("pv"+j).trim().equals("")){
                    ResponTimeSerise ffresponTimeSerise=new ResponTimeSerise();
                    ffresponTimeSerise.setInputPins(ffnamepin);
                    ffresponTimeSerise.setOutputPins("pv"+j);
                    ffresponTimeSerise.setRefrencemodleId(controlModle.getModleId());
                    ffresponTimeSerise.setStepRespJson(ffrespjsonObjectJSONObject.getString("pv"+j).trim());
                    responTimeSeriseArrayList.add(ffresponTimeSerise);
                }
            }

        }

        if(responTimeSeriseArrayList.size()!=0){
            modleServe.insertModleResp(responTimeSeriseArrayList);
        }

//        for(:mvrespjsonObject.values())
//        modleServe.insertModleResp();

//        String mvresp,@RequestParam("ffresp") String ffresp





        return "/modle/newmodle.do";
    }



}

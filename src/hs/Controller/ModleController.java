package hs.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.Bean.*;
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
    @Autowired
    private ModleConstainer modleConstainer;


    @RequestMapping("/modlestatus")
    public ModelAndView modelStatus(){

        ModelAndView mv=new ModelAndView();
        mv.setViewName("index");
        mv.addObject("modles",modleConstainer.getModules().values());
        mv.addObject("basedata",baseConf);
        return mv;
    }


    @RequestMapping("/stopModle")
    public ModelAndView stopModel(@RequestParam("modleid") String modleid){

        ControlModle controlModle=modleConstainer.getModules().get(Integer.valueOf(modleid.trim()));
        if(controlModle!=null){
            if(controlModle.getEnable()==1){
                controlModle.getExecutePythonBridge().stop();
                controlModle.setEnable(0);
                modleServe.modifymodleEnable(controlModle.getModleId(),0);
            }
        }
        ModelAndView mv=new ModelAndView();
        mv.setViewName("redirect:modlestatus.do");
        return mv;
//        return mv;
    }

    @RequestMapping("/runModle")
    public ModelAndView runModel(@RequestParam("modleid") String modleid){

        ControlModle controlModle=modleConstainer.getModules().get(Integer.valueOf(modleid.trim()));
        if(controlModle!=null){
            if(controlModle.getEnable()==0){
                controlModle.getExecutePythonBridge().execute();
                controlModle.setEnable(1);
                modleServe.modifymodleEnable(controlModle.getModleId(),1);
            }
        }
        ModelAndView mv=new ModelAndView();
        mv.setViewName("redirect:modlestatus.do");
        return mv;
//        return "/modle/modlestatus.do";
    }

    @RequestMapping("/deleteModle")
    public ModelAndView deleteModel(@RequestParam("modleid") String modleid){

        ControlModle controlModle=modleConstainer.getModules().get(Integer.valueOf(modleid.trim()));
        if(controlModle!=null){
            if(controlModle.getEnable()==1){
                controlModle.getExecutePythonBridge().stop();
            }
            modleServe.deleteModleResp(controlModle.getModleId());
            modleServe.deleteModlePins(controlModle.getModleId());
            modleServe.deleteModle(controlModle.getModleId());
            modleConstainer.getModules().remove(Integer.valueOf(modleid.trim()));

        }
        ModelAndView mv=new ModelAndView();
        mv.setViewName("redirect:modlestatus.do");
        return mv;
//        return "/modle/modlestatus.do";
    }



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




    @RequestMapping("/modifymodle")
    public ModelAndView modifyModel(@RequestParam("modleid") int modleid){

        ControlModle controlModle=modleConstainer.getModules().get(modleid);

        ModelAndView mv=new ModelAndView();
        mv.setViewName("modifyModle");
        JSONArray mvrespon= new JSONArray();
        JSONArray ffrespon= new JSONArray();

        List<String> pvlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getPv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get("pv"+i);
            if(modlePin==null){
                pvlist.add("");
            }else {
                pvlist.add(modlePin.getModleOpcTag());
            }
        }

        List<String> qlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getPv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get("pv"+i);
            if(modlePin==null){
                qlist.add("");
            }else {
                qlist.add(modlePin.getQ()+"");
            }
        }



        List<String> splist=new ArrayList<>();
        for(int i=1;i<=baseConf.getPv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get("sp"+i);

            if(modlePin==null){
                splist.add("");
            }else {
                splist.add(modlePin.getModleOpcTag());
            }

        }


        List<String> mvlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getMv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get("mv"+i);

            if(modlePin==null){
                mvlist.add("");
            }else {
                mvlist.add(modlePin.getModleOpcTag());
            }

        }


        List<String> rlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getMv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get("mv"+i);
            if(modlePin==null){
                rlist.add("");
            }else {
                rlist.add(modlePin.getR()+"");
            }

        }


        List<ModlePin> mvuplist=new ArrayList<>();
        for(int i=1;i<=baseConf.getMv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get("mvup"+i);

            if(modlePin==null){
                mvuplist.add(new ModlePin() );
            }else {
                mvuplist.add(modlePin);
            }

        }

        List<ModlePin> mvdownlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getMv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get("mvdown"+i);

            if(modlePin==null){
                mvdownlist.add(new ModlePin() );
            }else {
                mvdownlist.add(modlePin);
            }

        }


        List<String> mvfblist=new ArrayList<>();
        for(int i=1;i<=baseConf.getMv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get("mvfb"+i);

            if(modlePin==null){
                mvfblist.add("");
            }else {
                mvfblist.add(modlePin.getModleOpcTag());
            }

        }

        List<String> fflist=new ArrayList<>();
        for(int i=1;i<=baseConf.getFf();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get("ff"+i);

            if(modlePin==null){
                fflist.add("");
            }else {
                fflist.add(modlePin.getModleOpcTag());
            }

        }


        List<ModlePin> ffuplist=new ArrayList<>();
        for(int i=1;i<=baseConf.getFf();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get("ffup"+i);

            if(modlePin==null){
                ffuplist.add(new ModlePin());
            }else {
                ffuplist.add(modlePin);
            }

        }

        List<ModlePin> ffdownlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getFf();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get("ffdown"+i);

            if(modlePin==null){
                ffdownlist.add(new ModlePin());
            }else {
                ffdownlist.add(modlePin);
            }

        }

        //A

        for(int i=1;i<=baseConf.getMv();++i){

            JSONObject mvjson=new JSONObject();
            mvjson.put("mv","mv"+i);
            for(int j=1;j<=baseConf.getPv();++j){

                boolean isfind=false;
                for(ResponTimeSerise responTimeSerise:controlModle.getResponTimeSerises()){
                    if(responTimeSerise.getInputPins().equals("mv"+i)&&responTimeSerise.getOutputPins().equals("pv"+j)){
                        mvjson.put("pv"+j,responTimeSerise.getStepRespJson());
                        isfind=true;
                    }
                }
                if(!isfind){
                    mvjson.put("pv"+j,"");
                }

            }

            mvrespon.add(mvjson);

        }



        //B

        for(int i=1;i<=baseConf.getFf();++i){

            JSONObject ffjson=new JSONObject();
            ffjson.put("ff","ff"+i);
            for(int j=1;j<=baseConf.getPv();++j){

                boolean isfind=false;
                for(ResponTimeSerise responTimeSerise:controlModle.getResponTimeSerises()){
                    if(responTimeSerise.getInputPins().equals("ff"+i)&&responTimeSerise.getOutputPins().equals("pv"+j)){
                        ffjson.put("pv"+j,responTimeSerise.getStepRespJson());
                        isfind=true;
                    }
                }
                if(!isfind){
                    ffjson.put("pv"+j,"");
                }

            }
            ffrespon.add(ffjson);
        }


        //deadZone

        List<String> deadZnoelist=new ArrayList<>();
        for(int i=1;i<=baseConf.getPv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get("pv"+i);
            if(modlePin==null){
                deadZnoelist.add("");
            }else {
                deadZnoelist.add(modlePin.getDeadZone()+"");
            }
        }




        //funelInitvalue



        List<String> funelInitValuelist=new ArrayList<>();
        for(int i=1;i<=baseConf.getPv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get("pv"+i);
            if(modlePin==null){
                funelInitValuelist.add("");
            }else {
                funelInitValuelist.add(modlePin.getFunelinitValue()+"");
            }
        }


        mv.addObject("modle",controlModle);
        mv.addObject("pvlist",pvlist);
        mv.addObject("qlist",qlist);

        mv.addObject("splist",splist);
        mv.addObject("mvlist",mvlist);
        mv.addObject("rlist",rlist);
        mv.addObject("mvuplist",mvuplist);
        mv.addObject("mvdownlist",mvdownlist);
        mv.addObject("mvfblist",mvfblist);

        mv.addObject("fflist",fflist);
        mv.addObject("ffuplist",ffuplist);
        mv.addObject("ffdownlist",ffdownlist);

        mv.addObject("mvresp",mvrespon.toJSONString());
        mv.addObject("ffresp",ffrespon.toJSONString());

        mv.addObject("pvDeadZones",deadZnoelist);
        mv.addObject("pvFunelInitValues",funelInitValuelist);

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

        if (modlejsonObject.getString("modleid").trim().equals("")){
            modleServe.insertModle(controlModle);
        }else {
            modleServe.modifymodle(Integer.valueOf(modlejsonObject.getString("modleid").trim()),controlModle);
            controlModle.setModleId(Integer.valueOf(modlejsonObject.getString("modleid").trim()));
        }

        controlModle.setModlePins(new ArrayList<ModlePin>());
        for(int i=1;i<= baseConf.getPv();i++){

            String pvTag=modlejsonObject.getString("pv"+i);
            String QTag=modlejsonObject.getString("q"+i);
            String spTag=modlejsonObject.getString("sp"+i);
            String spDeadZone=modlejsonObject.getString("pv"+i+"DeadZone");
            String spFunelInitValue=modlejsonObject.getString("pv"+i+"FunelInitValue");
          //pvTag!=null&&pvTag.trim()!=""
            if((pvTag!=null&&!pvTag.trim().equals(""))&&(QTag!=null&&!QTag.trim().equals(""))&&(spTag!=null&&!spTag.trim().equals(""))&&(spDeadZone!=null&&!spDeadZone.trim().equals(""))&&(spFunelInitValue!=null&&!spFunelInitValue.trim().equals(""))){

                ModlePin pvpin=new ModlePin();
                pvpin.setResource("opc");
                pvpin.setModleOpcTag(pvTag);
                pvpin.setQ(Double.valueOf(QTag));
                pvpin.setReference_modleId(controlModle.getModleId());
                pvpin.setModlePinName("pv"+i);
                pvpin.setDeadZone(Double.valueOf(spDeadZone));
                pvpin.setFunelinitValue(Double.valueOf(spFunelInitValue));
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

            if (modlejsonObject.getString("modleid").trim().equals("")){
                modleServe.insertModlePins(controlModle);
            }else {
                modleServe.deleteModlePins(Integer.valueOf(modlejsonObject.getString("modleid").trim()));
                modleServe.insertModlePins(controlModle);
            }

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


            if(controlModle.getModlePins().size()!=0){

                if (modlejsonObject.getString("modleid").trim().equals("")){
                    modleServe.insertModleResp(responTimeSeriseArrayList);
                }else {
                    modleServe.deleteModleResp(Integer.valueOf(modlejsonObject.getString("modleid").trim()));
                    modleServe.insertModleResp(responTimeSeriseArrayList);
                }

            }
        }
        if(modlejsonObject.getString("modleid").trim().equals("")){
            ControlModle controlModle1=modleServe.getModle(controlModle.getModleId());
            modleConstainer.registerModle(controlModle1);
        }else {
            ControlModle controlModle1=modleConstainer.getModules().get(Integer.valueOf(modlejsonObject.getString("modleid").trim()));
            controlModle1.getExecutePythonBridge().stop();
            modleConstainer.getModules().remove(Integer.valueOf(modlejsonObject.getString("modleid").trim()));
            ControlModle newcontrolModle2=modleServe.getModle(controlModle.getModleId());
            modleConstainer.registerModle(newcontrolModle2);
        }


        ModelAndView mv=new ModelAndView();
        mv.setViewName("redirect:modlestatus.do");
//        return mv;
        return "modlestatus.do";
    }



}

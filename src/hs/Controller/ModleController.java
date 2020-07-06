package hs.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.Bean.*;
import hs.Dao.Service.ModleDBServe;
import hs.Filter.Filter;
import hs.Filter.FirstOrderLagFilter;
import hs.Filter.MoveAverageFilter;
import hs.Opc.OPCService;
import hs.Opc.OpcServicConstainer;
import hs.Utils.Tool;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 对于模型的操作
 * @author zzx
 * @version 1.0
 * @date 2020/3/19 14:11
 */

@Controller("modle")
@RequestMapping("/modle")
public class ModleController {
    public  static Logger logger = Logger.getLogger(ModleController.class);
    @Autowired
    private BaseConf baseConf;
    @Autowired
    private ModleDBServe modleDBServe;
    @Autowired
    private ModleConstainer modleConstainer;

    @Autowired
    private OpcServicConstainer opcServicConstainer;


    @RequestMapping("/modlestatus/{modleId}")
    public ModelAndView modelStatus(@PathVariable("modleId") String modleid){
        ControlModle controlModle=modleConstainer.getModulepool().get(Integer.valueOf(modleid.trim()));
        ModelAndView mv=new ModelAndView();
        mv.setViewName("modleStatus");
        mv.addObject("modle",controlModle);
        return mv;
    }


    @RequestMapping("/modleRealStatus/{modleId}")
    @ResponseBody
    public String modelRealStatusforweb(@PathVariable("modleId") String modleid){
        int loop=0;
        ControlModle controlModle=modleConstainer.getModulepool().get(Integer.valueOf(modleid.trim()));

        JSONObject result=new JSONObject();

        result.put("funelUp",controlModle.getBackPVFunelUp());
        result.put("funelDwon",controlModle.getBackPVFunelDown());
        result.put("predict",controlModle.getBackPVPrediction());

        int[]xaxis=new int[controlModle.getTimeserise_N()];
        for(int i=0;i<controlModle.getTimeserise_N();i++){
            xaxis[i]=i;
        }
        result.put("xaxis",xaxis);
        loop=0;
//        double[] mvs=new double[controlModle.getCategoryMVmodletag().size()];
//        for(ModlePin mvpin:controlModle.getCategoryMVmodletag()){
//            mvs[loop]=mvpin.getWriteValue();
//            loop++;
//        }
//        result.put("mv",mvs);

        result.put("outSetp",controlModle.getControlAPCOutCycle());


//        loop=0;
//        double[] sps=new double[controlModle.getCategorySPmodletag().size()];
//        for(ModlePin sppin:controlModle.getCategorySPmodletag()){
//            sps[loop]=sppin.modleGetReal();
//            loop++;
//        }
//        result.put("sp",sps);


        loop=0;
        double[] ffs=new double[controlModle.getCategoryFFmodletag().size()];
        for(ModlePin ffpin:controlModle.getCategoryFFmodletag()){
            ffs[loop]=ffpin.modleGetReal();
            loop++;
        }
        result.put("ff",ffs);



        loop=0;
        double[] pvs=new double[controlModle.getCategoryPVmodletag().size()];
        for(ModlePin pvpin:controlModle.getCategoryPVmodletag()){
            pvs[loop]=pvpin.modleGetReal();
            loop++;
        }
        result.put("pv",pvs);


        loop=0;
        String[]pvcurveNames=new String[controlModle.getCategoryPVmodletag().size()];
        String[] funelUpcurveNames=new String[controlModle.getCategoryPVmodletag().size()];
        String[] funelDowncurveNames=new String[controlModle.getCategoryPVmodletag().size()];
        for(ModlePin pvpin:controlModle.getCategoryPVmodletag()){
            pvcurveNames[loop]=pvpin.getModlePinName();
            funelUpcurveNames[loop]="funelUp";
            funelDowncurveNames[loop]="funelDown";
            loop++;
        }
        result.put("curveNames4funelUp",funelUpcurveNames);
        result.put("curveNames4pv",pvcurveNames);
        result.put("curveNames4funelDown",funelDowncurveNames);

        int pvnum=controlModle.getCategoryPVmodletag().size();//2
        int mvnum=controlModle.getCategoryMVmodletag().size();//1
        int maxrownum=pvnum>mvnum?pvnum:mvnum;
        JSONArray modlereadData=new JSONArray();
        for(loop=0;loop<maxrownum;loop++){
            JSONObject rowcontext =new JSONObject();
            if(loop<pvnum){
                ModlePin pv=controlModle.getCategoryPVmodletag().get(loop);
                ModlePin sp=controlModle.getCategorySPmodletag().get(loop);
               ;
                rowcontext.put("pvName",pv.getModleOpcTag());
                rowcontext.put("pvValue", Tool.getSpecalScale(3,pv.modleGetReal()));
                rowcontext.put("spValue",Tool.getSpecalScale(3,sp.modleGetReal()));
                rowcontext.put("e", Tool.getSpecalScale(3,controlModle.getBackPVPredictionError()[loop]));
            }
            rowcontext.put("modleName",controlModle.getModleName());


            if(loop<mvnum){//1,1
                ModlePin mv=controlModle.getCategoryMVmodletag().get(loop);
                ModlePin mvDownLmt=mv.getDownLmt();
                ModlePin mvUpLmt=mv.getUpLmt();
                ModlePin mvFeedBack=mv.getFeedBack();
                rowcontext.put("mvvalue",Tool.getSpecalScale(3,mv.modleGetReal()));
                rowcontext.put("mvDownLmt",Tool.getSpecalScale(3,mvDownLmt.modleGetReal()));
                rowcontext.put("mvUpLmt",Tool.getSpecalScale(3,mvUpLmt.modleGetReal()));
                rowcontext.put("mvFeedBack",Tool.getSpecalScale(3,mvFeedBack.modleGetReal()));
                rowcontext.put("dmv",Tool.getSpecalScale(3,controlModle.getBackDmvWrite()[loop]));
            }

            rowcontext.put("auto",controlModle.getAutoEnbalePin()==null?"手动":(controlModle.getAutoEnbalePin().modleGetReal()==0?"手动":"自动"));
            modlereadData.add(rowcontext);
        }

        result.put("modleRealData",modlereadData);


        return result.toJSONString();

    }



    @RequestMapping("/stopModle")
    @ResponseBody
    public String stopModel(@RequestParam("modleid") String modleid){

        ControlModle controlModle=modleConstainer.getModulepool().get(Integer.valueOf(modleid.trim()));
        if(controlModle!=null){
            if(controlModle.getModleEnable()==1){
                controlModle.getExecutePythonBridge().stop();
                controlModle.setModleEnable(0);
                modleDBServe.modifymodleEnable(controlModle.getModleId(),0);
                return "success";
            }
        }else {
            return "error";
        }
//        ModelAndView mv=new ModelAndView();
//        mv.setViewName("redirect:/login/index.do");
        return "error";
//        return mv;
    }

    @RequestMapping("/runModle")
    @ResponseBody
    public String runModel(@RequestParam("modleid") String modleid){

        ControlModle controlModle=modleConstainer.getModulepool().get(Integer.valueOf(modleid.trim()));
        if(controlModle!=null){
            if(controlModle.getModleEnable()==0){
                controlModle.getExecutePythonBridge().execute();
                controlModle.setModleEnable(1);
                modleDBServe.modifymodleEnable(controlModle.getModleId(),1);
                return "success";
            }
        }else {
            return "error";
        }
//        ModelAndView mv=new ModelAndView();
//        mv.setViewName("redirect:/login/index.do");
        return "error";
//        return "/modle/modlestatus.do";
    }

    @RequestMapping("/deleteModle")
    @ResponseBody
    public String deleteModel(@RequestParam("modleid") String modleid){
        try {
            ControlModle controlModle=modleConstainer.getModulepool().get(Integer.valueOf(modleid.trim()));
            if(controlModle!=null){
                if(controlModle.getModleEnable()==1){
                    controlModle.getExecutePythonBridge().stop();
                }
                /**
                 * 1、删除响应序列
                 * 2、删除应交过滤器
                 * 3、删除引脚
                 * 4、移除opc点号
                 * 4、删除模型
                 * */
                modleDBServe.deleteModleResp(controlModle.getModleId());
                for(ModlePin modlePin:controlModle.getModlePins()){
                    if(modlePin.getFilter()!=null){
                        modleDBServe.deletePinsFilter(modlePin.getFilter().getPk_pinid());
                    }
                }

                modleDBServe.deleteModlePins(controlModle.getModleId());
                controlModle.unregisterpin();
                modleDBServe.deleteModle(controlModle.getModleId());
                modleConstainer.getModulepool().remove(Integer.valueOf(modleid.trim()));
                return "success";
            }else{
                return "error";
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(),e);
            return "error";
        }
//        ModelAndView mv=new ModelAndView();
//        mv.setViewName("redirect:/login/index.do");
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
            mvjson.put(ModlePin.TYPE_PIN_FF,ModlePin.TYPE_PIN_FF+i);
            for(int j=1;j<=baseConf.getPv();++j){
                mvjson.put(ModlePin.TYPE_PIN_PV+j,"");
            }
            ffrespon.add(mvjson);

        }


        List<Integer> mvlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getMv();++i){
            mvlist.add(i);

            JSONObject mvjson=new JSONObject();
            mvjson.put(ModlePin.TYPE_PIN_MV,ModlePin.TYPE_PIN_MV+i);
            for(int j=1;j<=baseConf.getPv();++j){
                mvjson.put(ModlePin.TYPE_PIN_PV+j,"");
            }
            mvrespon.add(mvjson);

        }

        mv.addObject("pvlist",pvlist);
        mv.addObject("fflist",fflist);
        mv.addObject("mvlist",mvlist);
        mv.addObject("mvresp",mvrespon.toJSONString());
        mv.addObject("ffresp",ffrespon.toJSONString());
        /**opc点号来源*/
        List<String> opcresources=new ArrayList<>();
        for(OPCService opcService:opcServicConstainer.getOpcservepool().values()){
            opcresources.add("opc"+opcService.getOpcip());
        }
        opcresources.add("constant");
        mv.addObject("opcresources",opcresources);
        return mv;
    }




    @RequestMapping("/modifymodle")
    public ModelAndView modifyModel(@RequestParam("modleid") int modleid){

        ControlModle controlModle=modleConstainer.getModulepool().get(modleid);

        ModelAndView mv=new ModelAndView();
        mv.setViewName("modifyModle");
        JSONArray mvrespon= new JSONArray();
        JSONArray ffrespon= new JSONArray();

        List<String> pvlist=new ArrayList<>();
        List<ModlePin> pvpinlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getPv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_PV+i);
            if(modlePin==null){
                pvlist.add("");
                pvpinlist.add(new ModlePin());
            }else {
                pvlist.add(modlePin.getModleOpcTag());
                pvpinlist.add(modlePin);
            }
        }

        List<String> qlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getPv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_PV+i);
            if(modlePin==null){
                qlist.add("");
            }else {
                qlist.add(modlePin.getQ()+"");
            }
        }

        List<String> alphelist=new ArrayList<>();
        for(int i=1;i<=baseConf.getPv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_PV+i);
            if(modlePin==null){
                alphelist.add("");
            }else {
                alphelist.add(modlePin.getReferTrajectoryCoef()+"");
            }
        }



        List<String> splist=new ArrayList<>();
        List<ModlePin> sppinlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getPv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_SP+i);

            if(modlePin==null){
                splist.add("");
                sppinlist.add(new ModlePin());
            }else {
                splist.add(modlePin.getModleOpcTag());
                sppinlist.add(modlePin);
            }

        }


        List<String> mvlist=new ArrayList<>();
        List<ModlePin> mvpinlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getMv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_MV+i);

            if(modlePin==null){
                mvlist.add("");
                mvpinlist.add(new ModlePin());
            }else {
                mvlist.add(modlePin.getModleOpcTag());
                mvpinlist.add(modlePin);
            }

        }


        List<String> rlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getMv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_MV+i);
            if(modlePin==null){
                rlist.add("");
            }else {
                rlist.add(modlePin.getR()+"");
            }

        }


        List<String> dmvHighlist=new ArrayList<>();
        List<String> dmvLowlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getMv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_MV+i);
            if(modlePin==null){
                dmvHighlist.add("");
                dmvLowlist.add("");
            }else {
                dmvHighlist.add(modlePin.getDmvHigh()+"");
                dmvLowlist.add(modlePin.getDmvLow()+"");
            }

        }


        List<ModlePin> mvuplist=new ArrayList<>();
        for(int i=1;i<=baseConf.getMv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_MVUP+i);

            if(modlePin==null){
                mvuplist.add(new ModlePin() );
            }else {
                mvuplist.add(modlePin);
            }

        }


        List<Filter> filterpvlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getMv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_PV+i);

            if(modlePin==null){
                filterpvlist.add(new MoveAverageFilter());//这里仅仅是用于填充也页面数据，不是说没有滤波器的默认就是move
            }else {
                filterpvlist.add(modlePin.getFilter()==null?new MoveAverageFilter():modlePin.getFilter());
            }

        }


        List<Filter> filtermvfblist=new ArrayList<>();
        for(int i=1;i<=baseConf.getMv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_MVFB+i);
            if(modlePin==null){
                filtermvfblist.add(new MoveAverageFilter());//这里仅仅是用于填充也页面数据，不是说没有滤波器的默认就是move
            }else {
                filtermvfblist.add(modlePin.getFilter()==null?new MoveAverageFilter():modlePin.getFilter());
            }

        }



        List<Filter> filterfflist=new ArrayList<>();
        for(int i=1;i<=baseConf.getMv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_FF+i);
            if(modlePin==null){
                filterfflist.add(new MoveAverageFilter());//这里仅仅是用于填充也页面数据，不是说没有滤波器的默认就是move
            }else {
                filterfflist.add(modlePin.getFilter()==null?new MoveAverageFilter():modlePin.getFilter());
            }

        }






        List<ModlePin> mvdownlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getMv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_MVDOWN+i);

            if(modlePin==null){
                mvdownlist.add(new ModlePin() );
            }else {
                mvdownlist.add(modlePin);
            }

        }


        List<String> mvfblist=new ArrayList<>();
        List<ModlePin> mvfbpinlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getMv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_MVFB+i);

            if(modlePin==null){
                mvfblist.add("");
                mvfbpinlist.add(new ModlePin());
            }else {
                mvfblist.add(modlePin.getModleOpcTag());
                mvfbpinlist.add(modlePin);
            }

        }

        List<String> fflist=new ArrayList<>();
        List<ModlePin> ffpinlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getFf();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_FF+i);

            if(modlePin==null){
                fflist.add("");
                ffpinlist.add(new ModlePin());
            }else {
                fflist.add(modlePin.getModleOpcTag());
                ffpinlist.add(modlePin);
            }

        }


        /**前馈上限*/
        List<ModlePin> ffuplist=new ArrayList<>();
        for(int i=1;i<=baseConf.getFf();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_FFUP+i);

            if(modlePin==null){
                ffuplist.add(new ModlePin());
            }else {
                ffuplist.add(modlePin);
            }

        }

        /**前馈下限*/
        List<ModlePin> ffdownlist=new ArrayList<>();
        for(int i=1;i<=baseConf.getFf();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_FFDOWN+i);

            if(modlePin==null){
                ffdownlist.add(new ModlePin());
            }else {
                ffdownlist.add(modlePin);
            }

        }

        //A

        for(int i=1;i<=baseConf.getMv();++i){

            JSONObject mvjson=new JSONObject();
            mvjson.put(ModlePin.TYPE_PIN_MV,ModlePin.TYPE_PIN_MV+i);
            for(int j=1;j<=baseConf.getPv();++j){

                boolean isfind=false;
                for(ResponTimeSerise responTimeSerise:controlModle.getResponTimeSerises()){
                    if(
                            responTimeSerise.getInputPins().equals(ModlePin.TYPE_PIN_MV+i)
                                    &&
                                    responTimeSerise.getOutputPins().equals(ModlePin.TYPE_PIN_PV+j)
                    ){
                        mvjson.put(ModlePin.TYPE_PIN_PV+j,responTimeSerise.getStepRespJson());
                        isfind=true;
                    }
                }
                if(!isfind){
                    mvjson.put(ModlePin.TYPE_PIN_PV+j,"");
                }

            }

            mvrespon.add(mvjson);

        }



        //B

        for(int i=1;i<=baseConf.getFf();++i){

            JSONObject ffjson=new JSONObject();
            ffjson.put(ModlePin.TYPE_PIN_FF,ModlePin.TYPE_PIN_FF+i);
            for(int j=1;j<=baseConf.getPv();++j){

                boolean isfind=false;
                for(ResponTimeSerise responTimeSerise:controlModle.getResponTimeSerises()){
                    if(
                            responTimeSerise.getInputPins().equals(ModlePin.TYPE_PIN_FF+i)
                                    &&
                                    responTimeSerise.getOutputPins().equals(ModlePin.TYPE_PIN_PV+j)
                    ){
                        ffjson.put(ModlePin.TYPE_PIN_PV+j,responTimeSerise.getStepRespJson());
                        isfind=true;
                    }
                }
                if(!isfind){
                    ffjson.put(ModlePin.TYPE_PIN_PV+j,"");
                }

            }
            ffrespon.add(ffjson);
        }


        //deadZone

        List<String> deadZnoelist=new ArrayList<>();
        for(int i=1;i<=baseConf.getPv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_PV+i);
            if(modlePin==null){
                deadZnoelist.add("");
            }else {
                deadZnoelist.add(modlePin.getDeadZone()+"");
            }
        }




        //funelInitvalue
        List<String> funelInitValuelist=new ArrayList<>();
        for(int i=1;i<=baseConf.getPv();++i){
            ModlePin modlePin=controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_PV+i);
            if(modlePin==null){
                funelInitValuelist.add("");
            }else {
                funelInitValuelist.add(modlePin.getFunelinitValue()+"");
            }
        }


        mv.addObject("modle",controlModle);
        mv.addObject("pvlist",pvlist);
        mv.addObject("pvpinlist",pvpinlist);
        mv.addObject("qlist",qlist);
        mv.addObject("alphelist",alphelist);


        mv.addObject("splist",splist);
        mv.addObject("sppinlist",sppinlist);
        mv.addObject("mvlist",mvlist);
        mv.addObject("mvpinlist",mvpinlist);
        mv.addObject("rlist",rlist);
        mv.addObject("dmvHighlist",dmvHighlist);
        mv.addObject("dmvLowlist",dmvLowlist);

        mv.addObject("mvuplist",mvuplist);
        mv.addObject("mvdownlist",mvdownlist);
        mv.addObject("mvfblist",mvfblist);
        mv.addObject("mvfbpinlist",mvfbpinlist);

        mv.addObject("fflist",fflist);
        mv.addObject("ffpinlist",ffpinlist);
        mv.addObject("ffuplist",ffuplist);
        mv.addObject("ffdownlist",ffdownlist);

        mv.addObject("mvresp",mvrespon.toJSONString());
        mv.addObject("ffresp",ffrespon.toJSONString());

        mv.addObject("pvDeadZones",deadZnoelist);
        mv.addObject("pvFunelInitValues",funelInitValuelist);

        mv.addObject("filterpvlist",filterpvlist);

        mv.addObject("filtermvfblist",filtermvfblist);
        mv.addObject("filterfflist",filterfflist);

        /**opc点号来源*/
        List<String> opcresources=new ArrayList<>();
        for(OPCService opcService:opcServicConstainer.getOpcservepool().values()){
            opcresources.add("opc"+opcService.getOpcip());
        }
        opcresources.add("constant");
        mv.addObject("opcresources",opcresources);
        return mv;
    }




    @RequestMapping("/savemodle")
    @ResponseBody
    public String saveModel(@RequestParam("modle")String modle,@RequestParam("mvresp") String mvresp,@RequestParam("ffresp") String ffresp){//
        try {
            boolean newmodle=false;
            JSONObject modlejsonObject =JSON.parseObject(modle);

            ControlModle controlModle=new ControlModle();

            controlModle.setModleName(modlejsonObject.getString("modleName"));
            controlModle.setPredicttime_P(Integer.valueOf(modlejsonObject.getString("P")));
            controlModle.setControltime_M(Integer.valueOf(modlejsonObject.getString("M")));
            controlModle.setTimeserise_N(Integer.valueOf(modlejsonObject.getString("N")));
            controlModle.setControlAPCOutCycle(Integer.valueOf(modlejsonObject.getString("O")));

            if (modlejsonObject.getString("modleid").trim().equals("")){
                modleDBServe.insertModle(controlModle);
                newmodle=true;

            }else {
                modleDBServe.modifymodle(Integer.valueOf(modlejsonObject.getString("modleid").trim()),controlModle);
                controlModle.setModleId(Integer.valueOf(modlejsonObject.getString("modleid").trim()));
            }

            controlModle.setModlePins(new ArrayList<ModlePin>());

            /**
             *
             * auto*/
            ModlePin autopin=new ModlePin();
            autopin.setResource(modlejsonObject.getString("autoresource"));
            autopin.setModleOpcTag(modlejsonObject.getString("autoTag"));

            autopin.setReference_modleId(controlModle.getModleId());
            autopin.setModlePinName(ModlePin.TYPE_PIN_AUTO);
            controlModle.getModlePins().add(autopin);

            /**
             * pv*/
            for(int i=1;i<= baseConf.getPv();i++){

                String pvTag=modlejsonObject.getString(ModlePin.TYPE_PIN_PV+i).trim();
                String QTag=modlejsonObject.getString("q"+i);
                String spTag=modlejsonObject.getString(ModlePin.TYPE_PIN_SP+i);
                String spDeadZone=modlejsonObject.getString("pv"+i+"DeadZone");
                String spFunelInitValue=modlejsonObject.getString("pv"+i+"FunelInitValue");
                String spfuneltype=modlejsonObject.getString("funneltype"+i);

                String pvtracoef=modlejsonObject.getString("tracoef"+i);//参考轨迹系数

                /**
                 * 滤波相关处理
                 * */
                String filtercoefpv=modlejsonObject.getString("filtercoefpv"+i);
                String filteropctagpv=modlejsonObject.getString("filteropctagpv"+i);
                String filternamepv=modlejsonObject.getString("filternamepv"+i);

                String filterpvres=modlejsonObject.getString("filterpv"+i+"resource");

              //pvTag!=null&&pvTag.trim()!=""
                if((pvTag!=null&&!pvTag.trim().equals(""))&&(QTag!=null&&!QTag.trim().equals(""))&&(spTag!=null&&!spTag.trim().equals(""))&&(spDeadZone!=null&&!spDeadZone.trim().equals(""))&&(spFunelInitValue!=null&&!spFunelInitValue.trim().equals(""))){

                    ModlePin pvpin=new ModlePin();
                    pvpin.setResource(modlejsonObject.getString(ModlePin.TYPE_PIN_PV+i+"resource"));
                    pvpin.setModleOpcTag(pvTag);
                    pvpin.setQ(Double.valueOf(QTag));
                    pvpin.setReference_modleId(controlModle.getModleId());
                    pvpin.setModlePinName("pv"+i);
                    pvpin.setDeadZone(Double.valueOf(spDeadZone));
                    pvpin.setFunelinitValue(Double.valueOf(spFunelInitValue));
                    pvpin.setFunneltype(spfuneltype);

                    /**
                     * 滤波器
                     * */
                    if(filternamepv!=null){
                        if(filternamepv.equals("mvav")){
                            MoveAverageFilter moveAverageFilter=new MoveAverageFilter();
                            moveAverageFilter.setBackToDCSTag(filteropctagpv);
                            moveAverageFilter.setOpcresource(filterpvres);
                            moveAverageFilter.setCapacity(Double.valueOf(filtercoefpv).intValue());//窗口时间长度
                            moveAverageFilter.setFiltername(filternamepv);
                            pvpin.setFilter(moveAverageFilter);
                        }else if(filternamepv.equals("fodl")){
                            FirstOrderLagFilter firstOrderLagFilter=new FirstOrderLagFilter();
                            firstOrderLagFilter.setBackToDCSTag(filteropctagpv);
                            firstOrderLagFilter.setOpcresource(filterpvres);
                            firstOrderLagFilter.setFilter_alphe(Double.valueOf(filtercoefpv));
                            firstOrderLagFilter.setFiltername(filternamepv);
                            pvpin.setFilter(firstOrderLagFilter);
                        }
                    }


                    controlModle.getModlePins().add(pvpin);
                    if(pvtracoef!=null){
                        pvpin.setReferTrajectoryCoef(Double.valueOf(pvtracoef.trim()));
                    }else{
                        pvpin.setReferTrajectoryCoef(0.7);//默认值0.35
                    }

                    ModlePin sppin=new ModlePin();
                    sppin.setModleOpcTag(spTag);
                    sppin.setResource(modlejsonObject.getString(ModlePin.TYPE_PIN_SP+i+"resource"));
                    sppin.setReference_modleId(controlModle.getModleId());
                    sppin.setModlePinName("sp"+i);
                    controlModle.getModlePins().add(sppin);

                }
            }


            for(int i=1;i<= baseConf.getMv();i++){
                String mv=modlejsonObject.getString("mv"+i).trim();
                String r=modlejsonObject.getString("r"+i).trim();
                String mvfb=modlejsonObject.getString("mvfb"+i).trim();
                String mvup=modlejsonObject.getString("mvup"+i).trim();
                String mvdown=modlejsonObject.getString("mvdown"+i).trim();


                String dmvhigh=modlejsonObject.getString("dmv"+i+"High");
                String dmvlow=modlejsonObject.getString("dmv"+i+"Low");

                String mvupresoure=modlejsonObject.getString("mvup"+i+"resource");
                String mvdownresource=modlejsonObject.getString("mvdown"+i+"resource");

                //滤波器设置

                /**
                 * 滤波相关处理
                 * */
                String filtercoefmv=modlejsonObject.getString("filtercoefmv"+i);
                String filteropctagmv=modlejsonObject.getString("filteropctagmv"+i);
                String filternamemv=modlejsonObject.getString("filternamemv"+i);

                if(mv!=null&&!mv.trim().equals("")){
                    ModlePin mvpin=new ModlePin();
                    mvpin.setR(Double.valueOf(r));
                    mvpin.setResource(modlejsonObject.getString(ModlePin.TYPE_PIN_MV+i+"resource"));
                    mvpin.setReference_modleId(controlModle.getModleId());
                    mvpin.setModleOpcTag(mv);
                    mvpin.setModlePinName("mv"+i);
                    mvpin.setDmvHigh(Double.valueOf(dmvhigh));
                    mvpin.setDmvLow(Double.valueOf(dmvlow));
                    controlModle.getModlePins().add(mvpin);

                    
                    ModlePin mvfbpin=new ModlePin();
                    mvfbpin.setReference_modleId(controlModle.getModleId());
                    mvfbpin.setModlePinName("mvfb"+i);
                    mvfbpin.setResource(modlejsonObject.getString(ModlePin.TYPE_PIN_MVFB+i+"resource"));
                    mvfbpin.setModleOpcTag(mvfb);
                    controlModle.getModlePins().add(mvfbpin);

                    if(filternamemv!=null){

                        if(filternamemv.equals("mvav")){
                            MoveAverageFilter moveAverageFilter=new MoveAverageFilter();
                            moveAverageFilter.setBackToDCSTag(filteropctagmv);
                            moveAverageFilter.setOpcresource(modlejsonObject.getString("filtermvfb"+i+"resource"));
                            moveAverageFilter.setCapacity(Double.valueOf(filtercoefmv).intValue());//窗口时间长度
                            moveAverageFilter.setFiltername(filternamemv);
                            mvfbpin.setFilter(moveAverageFilter);
                        }else if(filternamemv.equals("fodl")){
                            FirstOrderLagFilter firstOrderLagFilter=new FirstOrderLagFilter();
                            firstOrderLagFilter.setBackToDCSTag(filteropctagmv);
                            firstOrderLagFilter.setOpcresource(modlejsonObject.getString("filtermvfb"+i+"resource"));
                            firstOrderLagFilter.setFilter_alphe(Double.valueOf(filtercoefmv));
                            firstOrderLagFilter.setFiltername(filternamemv);
                            mvfbpin.setFilter(firstOrderLagFilter);
                        }


                    }



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
                String ff=modlejsonObject.getString("ff"+i).trim();
                String ffup=modlejsonObject.getString("ffup"+i).trim();
                String ffdown=modlejsonObject.getString("ffdown"+i).trim();

                String ffupresource=modlejsonObject.getString("ffup"+i+"resource");
                String ffdownresource=modlejsonObject.getString("ffdown"+i+"resource");//ffdwon4resource

                /**
                 * 滤波相关处理
                 * */
                String filtercoefff=modlejsonObject.getString("filtercoefff"+i);
                String filteropctagff=modlejsonObject.getString("filteropctagff"+i);
                String filternameff=modlejsonObject.getString("filternameff"+i);

                if(ff!=null&&!ff.trim().equals("")){
                    ModlePin ffpin=new ModlePin();
                    ffpin.setReference_modleId(controlModle.getModleId());
                    ffpin.setModlePinName("ff"+i);
                    ffpin.setResource(modlejsonObject.getString(ModlePin.TYPE_PIN_FF+i+"resource"));
                    ffpin.setModleOpcTag(ff);

                    if(filternameff!=null){
                        if(filternameff.equals("mvav")){
                            MoveAverageFilter moveAverageFilter=new MoveAverageFilter();
                            moveAverageFilter.setBackToDCSTag(filteropctagff);
                            moveAverageFilter.setOpcresource(modlejsonObject.getString("filterff"+i+"resource"));

                            moveAverageFilter.setCapacity(Double.valueOf(filtercoefff).intValue());//窗口时间长度
                            moveAverageFilter.setFiltername(filternameff);
                            ffpin.setFilter(moveAverageFilter);
                        }else if(filternameff.equals("fodl")){
                            FirstOrderLagFilter firstOrderLagFilter=new FirstOrderLagFilter();
                            firstOrderLagFilter.setBackToDCSTag(filteropctagff);
                            firstOrderLagFilter.setOpcresource(modlejsonObject.getString("filterff"+i+"resource"));

                            firstOrderLagFilter.setFilter_alphe(Double.valueOf(filtercoefff));
                            firstOrderLagFilter.setFiltername(filternameff);
                            ffpin.setFilter(firstOrderLagFilter);
                        }
                    }

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



            /**
             * 数据库引脚信息更新
             * */
            if(controlModle.getModlePins().size()!=0){

                if (modlejsonObject.getString("modleid").trim().equals("")){
                    //新模型

                    modleDBServe.insertModlePins(controlModle.getModlePins());
                    //插入滤波器数据
                    for(ModlePin modlePin:controlModle.getModlePins()){

                        if(modlePin.getFilter()!=null){
                            modlePin.getFilter().setPk_pinid(modlePin.getModlepinsId());

                            if(modlePin.getFilter() instanceof MoveAverageFilter){

                                modleDBServe.insertPinsMVAVFilter((MoveAverageFilter)modlePin.getFilter());

                            }else if(modlePin.getFilter() instanceof FirstOrderLagFilter){

                                modleDBServe.insertPinsFODLFilter((FirstOrderLagFilter)modlePin.getFilter());

                            }


                        }
                    }


                }else {
                    //老模型
                    /**
                     * 删除引脚和对应的filter
                     * */
                    modleDBServe.deleteModlePins(Integer.valueOf(modlejsonObject.getString("modleid").trim()));

                    ControlModle oldmodle=modleConstainer.getModulepool().get(Integer.valueOf(modlejsonObject.getString("modleid").trim()));

                    for(ModlePin modlePin:oldmodle.getModlePins()){
                        if(modlePin.getFilter()!=null){
                            modleDBServe.deletePinsFilter(modlePin.getFilter().getPk_pinid());
                        }

                    }

                    /**
                     * 重新插入
                     * */
                    modleDBServe.insertModlePins(controlModle.getModlePins());


                    for(ModlePin modlePin:controlModle.getModlePins()){

                        if(modlePin.getFilter()!=null){
                            modlePin.getFilter().setPk_pinid(modlePin.getModlepinsId());

                            if(modlePin.getFilter() instanceof MoveAverageFilter){

                                modleDBServe.insertPinsMVAVFilter((MoveAverageFilter)modlePin.getFilter());

                            }else if(modlePin.getFilter() instanceof FirstOrderLagFilter){

                                modleDBServe.insertPinsFODLFilter((FirstOrderLagFilter)modlePin.getFilter());

                            }


                        }
                    }
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
                        modleDBServe.insertModleResp(responTimeSeriseArrayList);
                    }else {
                        modleDBServe.deleteModleResp(Integer.valueOf(modlejsonObject.getString("modleid").trim()));
                        modleDBServe.insertModleResp(responTimeSeriseArrayList);
                    }

                }
            }
            /***
             * 如果找不模型的id,那么直接从数据库中初始化模型
             * */
            if(modlejsonObject.getString("modleid").trim().equals("")){
                ControlModle controlModle1= modleDBServe.getModle(controlModle.getModleId());
                modleConstainer.registerModle(controlModle1);
            }else {
                /**
                 * 找到id，那么就需要停止运行，然后移除模型，然后重新从数据库初始化模型，开始运行
                 * */
                ControlModle oldcontrolModle=modleConstainer.getModulepool().get(Integer.valueOf(modlejsonObject.getString("modleid").trim()));
                oldcontrolModle.getExecutePythonBridge().stop();
                oldcontrolModle.unregisterpin();

                modleConstainer.getModulepool().remove(Integer.valueOf(modlejsonObject.getString("modleid").trim()));
                ControlModle newcontrolModle2= modleDBServe.getModle(controlModle.getModleId());
                modleConstainer.registerModle(newcontrolModle2);
            }



//        ModelAndView mv=new ModelAndView();
//        mv.setViewName("redirect:index.do");
//        return mv;
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("msg","success");
        if(newmodle){
            jsonObject.put("go","/modle/newmodle.do");
        }else {
            jsonObject.put("go","/modle/modifymodle.do?modleid="+controlModle.getModleId());
        }

        jsonObject.put("modleName",controlModle.getModleName());
        jsonObject.put("modleId",controlModle.getModleId());
        return jsonObject.toJSONString();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("msg","error");
            return jsonObject.toJSONString();
        }
    }



}

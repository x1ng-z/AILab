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
import hs.ShockDetect.ShockDetector;
import hs.Utils.Tool;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对于模型的操作
 *
 * @author zzx
 * @version 1.0
 * @date 2020/3/19 14:11
 */

@Controller("contrlmodle")
@RequestMapping("/contrlmodle")
public class ContrlModleController {
    public static Logger logger = Logger.getLogger(ContrlModleController.class);
    private static Pattern pvpattern = Pattern.compile("(^pv(\\d+)$)");
    private static Pattern ffpattern = Pattern.compile("(^ff(\\d+)$)");
    private static Pattern mvpattern = Pattern.compile("(^mv(\\d+)$)");

    @Autowired
    private BaseConf baseConf;
    @Autowired
    private ModleDBServe modleDBServe;
    @Autowired
    private ModleConstainer modleConstainer;

    @Autowired
    private OpcServicConstainer opcServicConstainer;


    @RequestMapping("/newmodle")
    public ModelAndView newModel() {
        ModelAndView mv = new ModelAndView();
        /**opc点号来源*/
        List<String> opcresources = new ArrayList<>();
        for (OPCService opcService : opcServicConstainer.getOpcservepool().values()) {
            opcresources.add("opc" + opcService.getOpcip());
        }
        opcresources.add("constant");
        mv.addObject("opcresources", opcresources);
        mv.setViewName("contrlmodle/newmodle");
        return mv;
    }


    @RequestMapping("/savenewmodle")
    @ResponseBody
    public String savenewmodle(@RequestParam("modlecontext") String modlecontext) {
        JSONObject jsonmodlecontext = JSONObject.parseObject(modlecontext);
        JSONObject reult = new JSONObject();
        String modleName = jsonmodlecontext.getString("modleName");
        int N = jsonmodlecontext.getInteger("N");
        int P = jsonmodlecontext.getInteger("P");
        int M = jsonmodlecontext.getInteger("M");
        int O = jsonmodlecontext.getInteger("O");
        ControlModle controlModle = new ControlModle();
        controlModle.setModleName(modleName);
        controlModle.setTimeserise_N(N);
        controlModle.setPredicttime_P(P);
        controlModle.setControltime_M(M);
        controlModle.setControlAPCOutCycle(O);


        String autoTag = jsonmodlecontext.getString("autoTag");
        String autoresource = jsonmodlecontext.getString("autoresource");
        boolean isneedinsertautopin = false;
        ModlePin autopin = null;
        if ((autoTag != null) && (!autoTag.equals(""))) {
            isneedinsertautopin = true;
            autopin = new ModlePin();
            autopin.setModlePinName(ModlePin.TYPE_PIN_MODLE_AUTO);
            autopin.setPintype(ModlePin.TYPE_PIN_MODLE_AUTO);
            autopin.setResource(autoresource);
            autopin.setModleOpcTag(autoTag);
        }

        try {
            if (isneedinsertautopin) {
                modleDBServe.insertModleAndAutoPin(controlModle, autopin);
            } else {
                modleDBServe.insertModle(controlModle);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            reult.put("msg", "error");
        }
        reult.put("msg", "success");
        reult.put("modleid", controlModle.getModleId());
        reult.put("modlename", controlModle.getModleName());
        return reult.toJSONString();
    }


    @RequestMapping("/modlestatus/{modleid}")
    public ModelAndView modelStatus(@PathVariable("modleid") String modleid) {
        ControlModle controlModle = modleConstainer.getRunnableModulepool().get(Integer.valueOf(modleid.trim()));
        ModelAndView mv = new ModelAndView();
        mv.setViewName("contrlmodle/modlestatus");
        if (null != controlModle) {
            mv.addObject("isrunable", true);
            List<ModlePin> runnablepvpins = controlModle.getRunablePins(controlModle.getCategoryPVmodletag(), controlModle.getMaskisRunnablePVMatrix());

            mv.addObject("enablePVPins", runnablepvpins);

            List<ModlePin> runnableMVPins = controlModle.getRunablePins(controlModle.getCategoryMVmodletag(), controlModle.getMaskisRunnableMVMatrix());

            mv.addObject("enableMVPins", runnableMVPins);

            List<ModlePin> runnableFFPins = controlModle.getRunablePins(controlModle.getCategoryFFmodletag(), controlModle.getMaskisRunnableFFMatrix());

            mv.addObject("enableFFPins", runnableFFPins);
        } else {
            mv.addObject("isrunable", false);
            controlModle = modleDBServe.getModle(Integer.valueOf(modleid));
        }
        mv.addObject("modle", controlModle);
        return mv;
    }


    @RequestMapping("/deletemodle")
    @ResponseBody
    public String deleteModel(@RequestParam("modleid") String modleid) {
        JSONObject result = new JSONObject();
        try {


            /**first delete db data*/
            ControlModle controlModle = modleDBServe.getModle(Integer.valueOf(modleid));
            if (null != controlModle) {
                modleDBServe.deletemodle(controlModle);
            }

            /**sec release ram data*/
            ControlModle runnablecontrolodle = modleConstainer.getRunnableModulepool().get(Integer.valueOf(modleid.trim()));
            if (runnablecontrolodle != null) {
                if (1 == runnablecontrolodle.getModleEnable()) {
                    runnablecontrolodle.generateValidkey();
                    runnablecontrolodle.setModleEnable(0);
                    runnablecontrolodle.getExecutePythonBridge().stop();
                }

                if (runnablecontrolodle.getSimulatControlModle().isIssimulation()) {
                    runnablecontrolodle.getSimulatControlModle().setIssimulation(false);
                    runnablecontrolodle.getSimulatControlModle().generateSimulatevalidkey();
                    runnablecontrolodle.getSimulatControlModle().getExecutePythonBridgeSimulate().stop();
                }
                runnablecontrolodle.unregisterpin();
                modleConstainer.getRunnableModulepool().remove(Integer.valueOf(modleid.trim()));
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
        }
        result.put("msg", "success");
        return result.toJSONString();
    }


    @RequestMapping("/modifymodle/{modleid}")
    public ModelAndView modifymodle(@PathVariable("modleid") int modleid) {

        ControlModle modle = modleDBServe.getModle(modleid);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("contrlmodle/modifymodle");
        mv.addObject("modle", modle);
        for (ModlePin pin : modle.getModlePins()) {
            if (pin.getPintype().equals(ModlePin.TYPE_PIN_MODLE_AUTO)) {
                mv.addObject("autopin", pin);
                break;
            }
        }

        List<String> opcresources = new ArrayList<>();
        for (OPCService opcService : opcServicConstainer.getOpcservepool().values()) {
            opcresources.add("opc" + opcService.getOpcip());
        }
        opcresources.add("constant");
        mv.addObject("opcresources", opcresources);
        return mv;
    }


    @RequestMapping("/savemodifymodle")
    @ResponseBody
    public String savemodifymodle(@RequestParam("modlecontxt") String modlecontxt) {

        JSONObject jsonmodlecontext = JSONObject.parseObject(modlecontxt);
        JSONObject reult = new JSONObject();
        String modleName = jsonmodlecontext.getString("modleName");
        int N = jsonmodlecontext.getInteger("N");
        int P = jsonmodlecontext.getInteger("P");
        int M = jsonmodlecontext.getInteger("M");
        int O = jsonmodlecontext.getInteger("O");
        int modleId = jsonmodlecontext.getInteger("modleid");
        ControlModle controlModle = new ControlModle();
        controlModle.setModleName(modleName);
        controlModle.setTimeserise_N(N);
        controlModle.setPredicttime_P(P);
        controlModle.setControltime_M(M);
        controlModle.setControlAPCOutCycle(O);
        controlModle.setModleId(modleId);


        String autoTag = jsonmodlecontext.getString("autoTag");
        String autoresource = jsonmodlecontext.getString("autoresource");

        String modlepinsId = jsonmodlecontext.getString("modlepinsId");

        ModlePin autopin = new ModlePin();
        autopin.setModlePinName(ModlePin.TYPE_PIN_MODLE_AUTO);
        autopin.setPintype(ModlePin.TYPE_PIN_MODLE_AUTO);
        autopin.setResource(autoresource);
        autopin.setModleOpcTag(autoTag);
        autopin.setReference_modleId(modleId);

        try {
            if ((modlepinsId != null) && modlepinsId.equals("")) {
                /**delet or insert
                 * 以前没有auto
                 * */

                if ((autoTag != null) && (!autoTag.equals(""))) {
                    /*现在有*/
                /*1更新控制器
                2插入pin操作*/
                    modleDBServe.updateModleInsertAutopin(controlModle, autopin);
                } else {
                    /*现在无
                     * 1只要更新控制器基本属性
                     * */
                    modleDBServe.modifymodle(controlModle.getModleId(), controlModle);
                }

            } else {
                /**以前有*/
                autopin.setModlepinsId(Integer.valueOf(modlepinsId));
                if ((autoTag != null) && (!autoTag.equals(""))) {
                    /*现在有*/
                /*1更新控制器
                2更新pin操作*/
                    modleDBServe.updateModleAndAutopin(controlModle, autopin);

                } else {
                    /*现在无
                     * 1更新控制器
                     * 2删除pin
                     * */
                    modleDBServe.updateModleAndDeleteAutopin(controlModle, autopin);

                }

            }


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            reult.put("msg", "error");
            return reult.toJSONString();
        }
        reult.put("msg", "success");
        reult.put("modleid", controlModle.getModleId());
        reult.put("modlename", controlModle.getModleName());
        return reult.toJSONString();
    }


    @RequestMapping("/pagemodelpin")
    @ResponseBody
    public String pagemodelpin(@RequestParam("modleid") int modleid, @RequestParam("pintype") String pintype, @RequestParam("page") int page, @RequestParam("pagesize") int pagesize) {
        List<ModlePin> pins = modleDBServe.pagepinsbymodleid(modleid, pintype, (page - 1) * pagesize, pagesize);

        int count = modleDBServe.countPVpinsbymodleid(modleid, pintype);

        JSONArray datas = new JSONArray();

        for (ModlePin pin : pins) {
            JSONObject pincontext = new JSONObject();
            pincontext.put("modleid", pin.getReference_modleId());
            pincontext.put("pinid", pin.getModlepinsId());
            pincontext.put("modlePinName", pin.getModlePinName());
            pincontext.put("modleOpcTag", pin.getModleOpcTag());
            pincontext.put("opcTagName", pin.getOpcTagName());
            datas.add(pincontext);
        }
        return Tool.sendLayuiPage(count, datas).toJSONString();
    }

    @RequestMapping("/pagerespon")
    @ResponseBody
    public String pagerespon(@RequestParam("modleid") int modleid, @RequestParam("page") int page, @RequestParam("pagesize") int pagesize) {

        List<ResponTimeSerise> resps = modleDBServe.pageresponbymodleid(modleid, (page - 1) * pagesize, pagesize);

        int count = modleDBServe.countresponbymodleid(modleid);

        JSONArray datas = new JSONArray();

        for (ResponTimeSerise resp : resps) {
            JSONObject jsonresp = JSONObject.parseObject(resp.getStepRespJson());//{k:1,t:180,tao:1}
            JSONObject pincontext = new JSONObject();
            pincontext.put("modleid", resp.getRefrencemodleId());
            pincontext.put("responid", resp.getModletagId());
            pincontext.put("input", resp.getInputPins());
            pincontext.put("output", resp.getOutputPins());
            pincontext.put("K", jsonresp.getFloat("k"));
            pincontext.put("T", jsonresp.getFloat("t"));
            pincontext.put("Tau", jsonresp.getFloat("tao"));
            datas.add(pincontext);
        }
        return Tool.sendLayuiPage(count, datas).toJSONString();
    }

    @RequestMapping("/newmodelpvpin")
    public ModelAndView newmodelpvpin(@RequestParam("modleid") int modleid, @RequestParam("pintype") String pintype) {

        List<ModlePin> usepinscope = modleDBServe.pinsbypintype(modleid, pintype);

        List<Integer> unuserpinscope = Tool.getunUserPinScope(pvpattern, usepinscope, baseConf.getPv());


        ModelAndView view = new ModelAndView();
        view.setViewName("contrlmodle/newpvpin");
        view.addObject("modleid", modleid);
        view.addObject("pintype", pintype);
        List<String> opcresources = new ArrayList<>();
        for (OPCService opcService : opcServicConstainer.getOpcservepool().values()) {
            opcresources.add("opc" + opcService.getOpcip());
        }
        opcresources.add("constant");
        view.addObject("opcresources", opcresources);
        view.addObject("unuserpinscope", unuserpinscope);
        return view;
    }


    @RequestMapping("/newmodelmvpin")
    public ModelAndView newmodelmvpin(@RequestParam("modleid") int modleid, @RequestParam("pintype") String pintype) {

        List<ModlePin> usepinscope = modleDBServe.pinsbypintype(modleid, pintype);

        List<Integer> unuserpinscope = Tool.getunUserPinScope(mvpattern, usepinscope, baseConf.getMv());


        ModelAndView view = new ModelAndView();
        view.setViewName("contrlmodle/newmvpin");
        view.addObject("modleid", modleid);
        view.addObject("pintype", pintype);
        List<String> opcresources = new ArrayList<>();
        for (OPCService opcService : opcServicConstainer.getOpcservepool().values()) {
            opcresources.add("opc" + opcService.getOpcip());
        }
        opcresources.add("constant");
        view.addObject("opcresources", opcresources);
        view.addObject("unuserpinscope", unuserpinscope);
        return view;
    }


    @RequestMapping("/newmodelffpin")
    public ModelAndView newmodelffpin(@RequestParam("modleid") int modleid, @RequestParam("pintype") String pintype) {

        List<ModlePin> usepinscope = modleDBServe.pinsbypintype(modleid, pintype);

        List<Integer> unuserpinscope = Tool.getunUserPinScope(ffpattern, usepinscope, baseConf.getFf());


        ModelAndView view = new ModelAndView();
        view.setViewName("contrlmodle/newffpin");
        view.addObject("modleid", modleid);
        view.addObject("pintype", pintype);
        List<String> opcresources = new ArrayList<>();
        for (OPCService opcService : opcServicConstainer.getOpcservepool().values()) {
            opcresources.add("opc" + opcService.getOpcip());
        }
        opcresources.add("constant");
        view.addObject("opcresources", opcresources);
        view.addObject("unuserpinscope", unuserpinscope);
        return view;
    }


    @RequestMapping("/newrespon")
    public ModelAndView newrespon(@RequestParam("modleid") int modleid) {


        /**
         * 1、选择出已经使用mv ff引脚
         * 2、
         * */
        List<ModlePin> userinputpinscope = new ArrayList<>();

        List<ModlePin> usemvpinscope = null;
        List<ModlePin> useffpinscope = null;


        List<ModlePin> useroutputpinscope = null;


        try {
            usemvpinscope = modleDBServe.pinsbypintype(modleid, ModlePin.TYPE_PIN_MV);
            useffpinscope = modleDBServe.pinsbypintype(modleid, ModlePin.TYPE_PIN_FF);
            userinputpinscope.addAll(usemvpinscope);
            userinputpinscope.addAll(useffpinscope);

            useroutputpinscope = modleDBServe.pinsbypintype(modleid, ModlePin.TYPE_PIN_PV);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }


        ModelAndView view = new ModelAndView();
        view.setViewName("contrlmodle/newrespon");
        view.addObject("modleid", modleid);

        view.addObject("userinputpinscope", userinputpinscope);
        view.addObject("useroutputpinscope", useroutputpinscope);
        return view;
    }


    @RequestMapping("/savenewmodelpvpin")
    @ResponseBody
    public String savenewmodelpvpin(@RequestParam("modlepincontext") String modlepincontext) {

        JSONObject modlejsonObject = JSONObject.parseObject(modlepincontext);
        JSONObject result = new JSONObject();

        Integer modleid;
        /**pv*/
        String pinName = null;
        String pvTag = null;
        String resource = null;
        String pvcomment = null;
        String QTag = null;
        Double pvDeadZone = null;
        Double pvFunelInitValue = null;
        String pvfuneltype = null;
        Double pvtracoef = null;//参考轨迹系数
        String pvpinid;
        ModlePin pvpin = null;

        int pinscope;
        try {
            modleid = modlejsonObject.getInteger("modleid");
            pinName = modlejsonObject.getString("pinName");

            List<ModlePin> usemodlePins = modleDBServe.pinsbypintype(modleid, ModlePin.TYPE_PIN_PV);


            pvTag = modlejsonObject.getString(ModlePin.TYPE_PIN_PV).trim();
            resource = modlejsonObject.getString(ModlePin.TYPE_PIN_PV + "resource");
            pvcomment = modlejsonObject.getString(ModlePin.TYPE_PIN_PV + "comment").trim();
            QTag = modlejsonObject.getString("q");
            pvDeadZone = modlejsonObject.getDouble(ModlePin.TYPE_PIN_PV + "DeadZone");
            pvFunelInitValue = modlejsonObject.getDouble(ModlePin.TYPE_PIN_PV + "FunelInitValue");
            pvfuneltype = modlejsonObject.getString("funneltype");
            pvtracoef = modlejsonObject.getDouble("tracoef");
            pvpinid = modlejsonObject.getString("pvpinid");


            pvpin = new ModlePin();
            pvpin.setResource(resource);
            pvpin.setModleOpcTag(pvTag);
            pvpin.setQ(Double.valueOf(QTag));
            pvpin.setReference_modleId(modleid);
            pvpin.setModlePinName(pinName);
            pvpin.setPintype(ModlePin.TYPE_PIN_PV);
            pvpin.setDeadZone(pvDeadZone);
            pvpin.setFunelinitValue(pvFunelInitValue);
            pvpin.setFunneltype(pvfuneltype);
            pvpin.setOpcTagName(pvcomment);
            pvpin.setReferTrajectoryCoef(pvtracoef);
            pvpin.setModlepinsId(pvpinid.equals("") ? -1 : Integer.parseInt(pvpinid));

            Matcher pvmatch = pvpattern.matcher(pinName);
            if (pvmatch.find()) {
                pinscope = Integer.parseInt(pvmatch.group(2));
                for (ModlePin usepvpin : usemodlePins) {
                    if (usepvpin.getModlePinName().equals(ModlePin.TYPE_PIN_PV + pinscope)) {
                        throw new RuntimeException("引脚已经使用");
                    }
                }
            } else {
                throw new RuntimeException("引脚匹配错误");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /**pv enable*/
        String pvenable = null;
        String pvenableresource = null;
        String pvenableid;
        ModlePin pvenablepin = null;
        try {
            pvenable = modlejsonObject.getString(ModlePin.TYPE_PIN_PIN_PVENABLE);
            pvenableresource = modlejsonObject.getString(ModlePin.TYPE_PIN_PIN_PVENABLE + "resource");
            pvenableid = modlejsonObject.getString("pvenableid");

            //if((!pvenable.equals(""))&&(!pvenableresource.equals(""))){
            pvenablepin = new ModlePin();
            pvenablepin.setReference_modleId(modleid);

            pvenablepin.setModlePinName(ModlePin.TYPE_PIN_PIN_PVENABLE + pinscope);
            pvenablepin.setPintype(ModlePin.TYPE_PIN_PIN_PVENABLE);
            pvenablepin.setModleOpcTag(pvenable);
            pvenablepin.setResource(pvenableresource);
            pvenablepin.setModlepinsId(pvenableid.equals("") ? -1 : Integer.parseInt(pvenableid));
            // }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /**pv置信区间位号*/
        ModlePin pvuppin = null;
        try {
            String pvup = modlejsonObject.getString(ModlePin.TYPE_PIN_PVUP).trim();
            String pvupresource = modlejsonObject.getString(ModlePin.TYPE_PIN_PVUP + "resource").trim();
            String pvuppinid = modlejsonObject.getString("pvuppinid");

            //if((!pvup.equals(""))&&(!pvupresource.equals(""))){
            pvuppin = new ModlePin();
            pvuppin.setReference_modleId(modleid);
            pvuppin.setModlePinName(ModlePin.TYPE_PIN_PVUP + pinscope);
            pvuppin.setPintype(ModlePin.TYPE_PIN_PVUP);
            pvuppin.setModleOpcTag(pvup);
            pvuppin.setResource(pvupresource);
            pvuppin.setModlepinsId(pvuppinid.equals("") ? -1 : Integer.parseInt(pvuppinid));
            //}

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        ModlePin pvdownpin = null;
        try {
            String pvdown = modlejsonObject.getString(ModlePin.TYPE_PIN_PVDOWN).trim();
            String pvdownresource = modlejsonObject.getString(ModlePin.TYPE_PIN_PVDOWN + "resource").trim();
            String pvdownpinid = modlejsonObject.getString("pvdownpinid");

            //if((!pvdown.equals(""))&&(!pvdownresource.equals(""))){
            pvdownpin = new ModlePin();
            pvdownpin.setReference_modleId(modleid);
            pvdownpin.setModlePinName(ModlePin.TYPE_PIN_PVDOWN + pinscope);
            pvdownpin.setPintype(ModlePin.TYPE_PIN_PVDOWN);
            pvdownpin.setModleOpcTag(pvdown);
            pvdownpin.setResource(pvdownresource);
            pvdownpin.setModlepinsId(pvdownpinid.equals("") ? -1 : Integer.parseInt(pvdownpinid));
            //}

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        /**
         * pv震荡检测属性
         * */
        String detectwindowstimepv = null;//震荡检测检测窗口时间
        String detectdampcoepv = null;//震荡检测检测阻尼
        String detectfiltercoepv = null;//震荡检测滤波系数
        String detectfilteroutopctagpv = null;//震荡检测检滤波输出
        String detectfilteroutopctagpvresource = null;//
        String detectamplitudeoutopctagpv = null;//震荡检测振幅
        String detectamplitudeoutopctagpvresource = null;//
        String shockerid;

        ShockDetector shockDetector = null;
        try {
            detectwindowstimepv = modlejsonObject.getString("detectwindowstimepv");
            detectdampcoepv = modlejsonObject.getString("detectdampcoepv");
            detectfiltercoepv = modlejsonObject.getString("detectfiltercoepv");
            detectfilteroutopctagpv = modlejsonObject.getString("detectfilteroutopctagpv");
            detectfilteroutopctagpvresource = modlejsonObject.getString("detectfilteroutopctagpv" + "resource");
            detectamplitudeoutopctagpv = modlejsonObject.getString("detectamplitudeoutopctagpv");
            detectamplitudeoutopctagpvresource = modlejsonObject.getString("detectamplitudeoutopctagpv" + "resource");
            shockerid = modlejsonObject.getString("shockerid");

            shockDetector = new ShockDetector();
            if (!detectwindowstimepv.equals("")) {
                shockDetector.setWindowstime(Integer.valueOf(detectwindowstimepv));
            }
            if (!detectdampcoepv.equals("")) {
                shockDetector.setDampcoeff(Double.valueOf(detectdampcoepv));
            }

            if (!detectfiltercoepv.equals("")) {
                shockDetector.setFiltercoeff(Math.abs(Double.valueOf(detectfiltercoepv)) > 1 ? 1 : Math.abs(Double.valueOf(detectfiltercoepv)));
            }
            shockDetector.setBackToDCSTag(detectamplitudeoutopctagpv);
            shockDetector.setOpcresource(detectamplitudeoutopctagpvresource);
            shockDetector.setFilterbacktodcstag(detectfilteroutopctagpv);
            shockDetector.setFilteropcresource(detectfilteroutopctagpvresource);
            shockDetector.setPk_shockdetectid(shockerid.equals("") ? -1 : Integer.valueOf(shockerid));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /**
         * 滤波相关处理
         * */
        String filtercoefpv = null;
        String filteropctagpv = null;
        String filternamepv = null;
        String filterpvres = null;
        String pinfilterid = null;
        Filter pinfiter = null;
        try {
            filtercoefpv = modlejsonObject.getString("filtercoefpv");
            filteropctagpv = modlejsonObject.getString("filteropctagpv");
            filternamepv = modlejsonObject.getString("filternamepv");
            filterpvres = modlejsonObject.getString("filterpv" + "resource");
            pinfilterid = modlejsonObject.getString("pinfilterid");

            if (filternamepv != null) {
                if (filternamepv.equals("mvav")) {
                    MoveAverageFilter moveAverageFilter = new MoveAverageFilter();
                    moveAverageFilter.setBackToDCSTag(filteropctagpv);
                    moveAverageFilter.setOpcresource(filterpvres);
                    moveAverageFilter.setCapacity(Double.valueOf(filtercoefpv).intValue());//窗口时间长度
                    moveAverageFilter.setFiltername(filternamepv);
                    moveAverageFilter.setPk_filterid(pinfilterid.equals("") ? -1 : Integer.valueOf(pinfilterid));
                    pinfiter = moveAverageFilter;
                } else if (filternamepv.equals("fodl")) {
                    FirstOrderLagFilter firstOrderLagFilter = new FirstOrderLagFilter();
                    firstOrderLagFilter.setBackToDCSTag(filteropctagpv);
                    firstOrderLagFilter.setOpcresource(filterpvres);
                    firstOrderLagFilter.setFilter_alphe(Math.abs(Double.valueOf(filtercoefpv)) > 1 ? 1 : Math.abs(Double.valueOf(filtercoefpv)));
                    firstOrderLagFilter.setFiltername(filternamepv);
                    firstOrderLagFilter.setPk_filterid(pinfilterid.equals("") ? -1 : Integer.valueOf(pinfilterid));
                    pinfiter = firstOrderLagFilter;
                } else if (filternamepv.equals("")) {
                    MoveAverageFilter moveAverageFilter = new MoveAverageFilter();
                    moveAverageFilter.setPk_filterid(pinfilterid.equals("") ? -1 : Integer.valueOf(pinfilterid));
                    pinfiter = moveAverageFilter;
                }
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /****SP**/
        String spTag = null;
        String spcomment = null;
        ModlePin sppin = null;
        String sppinid;
        try {
            spTag = modlejsonObject.getString(ModlePin.TYPE_PIN_SP);
            spcomment = modlejsonObject.getString(ModlePin.TYPE_PIN_SP + "comment").trim();
            String spresource = modlejsonObject.getString(ModlePin.TYPE_PIN_SP + "resource");

            sppinid = modlejsonObject.getString("sppinid");


            sppin = new ModlePin();
            sppin.setReference_modleId(modleid);
            sppin.setModleOpcTag(spTag);
            sppin.setOpcTagName(spcomment);
            sppin.setResource(spresource);

            sppin.setModlePinName(ModlePin.TYPE_PIN_SP + pinscope);
            sppin.setPintype(ModlePin.TYPE_PIN_SP);
            sppin.setModlepinsId(sppinid.equals("") ? -1 : Integer.parseInt(sppinid));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        try {
            if ((!pinName.equals("")) && (!pvTag.equals("")) && (!spTag.equals(""))) {
                modleDBServe.insertpvandsp(pvpin, pvenablepin, pvuppin, pvdownpin, shockDetector, pinfiter, sppin);
            } else {
                result.put("msg", "error");
                return result.toJSONString();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        result.put("msg", "success");
        return result.toJSONString();
    }


    @RequestMapping("/savenewmodelmvpin")
    @ResponseBody
    public String savenewmodelmvpin(@RequestParam("modlepincontext") String modlepincontext) {

        JSONObject modlejsonObject = JSONObject.parseObject(modlepincontext);
        JSONObject result = new JSONObject();

        String mvopctag = null;
        String mvresource = null;
        String mvcomment = null;
        String r = null;
        String dmvhigh = null;
        String dmvlow = null;
        String mvpinid = null;
        Integer modleid;

        int pinscope = -1;
        ModlePin mvpin = null;
        try {
            mvopctag = modlejsonObject.getString("mv").trim();
            mvcomment = modlejsonObject.getString("mv" + "comment").trim();
            r = modlejsonObject.getString("r").trim();
            mvresource = modlejsonObject.getString(ModlePin.TYPE_PIN_MV + "resource");

            dmvhigh = modlejsonObject.getString("dmv" + "High");
            dmvlow = modlejsonObject.getString("dmv" + "Low");


            mvpinid = modlejsonObject.getString("mvpinid").trim();
            modleid = modlejsonObject.getInteger("modleid");


            String pinName = modlejsonObject.getString("pinName");
            List<ModlePin> usemodlePins = modleDBServe.pinsbypintype(modleid, ModlePin.TYPE_PIN_MV);

            Matcher pvmatch = mvpattern.matcher(pinName);
            if (pvmatch.find()) {
                pinscope = Integer.parseInt(pvmatch.group(2));
                for (ModlePin usepvpin : usemodlePins) {
                    if (usepvpin.getModlePinName().equals(ModlePin.TYPE_PIN_MV + pinscope)) {
                        throw new RuntimeException("引脚已经使用");
                    }
                }
            } else {
                throw new RuntimeException("引脚匹配错误");
            }


            mvpin = new ModlePin();
            mvpin.setR(Double.valueOf(r));
            mvpin.setResource(mvresource);
            mvpin.setModleOpcTag(mvopctag);
            mvpin.setModlePinName(ModlePin.TYPE_PIN_MV + pinscope);
            mvpin.setPintype(ModlePin.TYPE_PIN_MV);
            mvpin.setDmvHigh(Double.valueOf(dmvhigh));
            mvpin.setDmvLow(Double.valueOf(dmvlow));
            mvpin.setOpcTagName(mvcomment);

            mvpin.setModlepinsId(mvpinid.equals("") ? -1 : Integer.parseInt(mvpinid));
            mvpin.setReference_modleId(modleid);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /***上下限**/

        ModlePin mvdownpin = null;
        ModlePin mvuppin = null;
        String mvuppinid;
        String mvdownpinid;
        String mvup = null;
        String mvdown = null;
        String mvupresoure = null;
        String mvdownresource = null;
        try {

            mvuppinid = modlejsonObject.getString("mvuppinid");
            mvup = modlejsonObject.getString("mvup").trim();
            mvupresoure = modlejsonObject.getString("mvup" + "resource");

            mvdown = modlejsonObject.getString("mvdown").trim();
            mvdownresource = modlejsonObject.getString("mvdown" + "resource");
            mvdownpinid = modlejsonObject.getString("mvdownpinid");


            mvuppin = new ModlePin();

            mvuppin.setModlePinName(ModlePin.TYPE_PIN_MVUP + pinscope);
            mvuppin.setPintype(ModlePin.TYPE_PIN_MVUP);
            mvuppin.setResource(mvupresoure);
            mvuppin.setModleOpcTag(mvup);

            mvuppin.setReference_modleId(modleid);
            mvuppin.setModlepinsId(mvuppinid.equals("") ? -1 : Integer.parseInt(mvuppinid));

            mvdownpin = new ModlePin();

            mvdownpin.setModlePinName(ModlePin.TYPE_PIN_MVDOWN + pinscope);
            mvdownpin.setPintype(ModlePin.TYPE_PIN_MVDOWN);
            mvdownpin.setResource(mvdownresource);
            mvdownpin.setModleOpcTag(mvdown);

            mvdownpin.setModlepinsId(mvdownpinid.equals("") ? -1 : Integer.parseInt(mvdownpinid));
            mvdownpin.setReference_modleId(modleid);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /**mvfb*/
        ModlePin mvfbpin;
        String mvfbpinid;
        String mvfb = null;
        String mvfbcomment = null;
        String mvfbresource = null;
        try {
            mvfb = modlejsonObject.getString("mvfb").trim();
            mvfbcomment = modlejsonObject.getString("mvfb" + "comment").trim();
            mvfbresource = modlejsonObject.getString(ModlePin.TYPE_PIN_MVFB + "resource");
            mvfbpinid = modlejsonObject.getString("mvfbpinid");


            mvfbpin = new ModlePin();
            mvfbpin.setModlePinName(ModlePin.TYPE_PIN_MVFB + pinscope);
            mvfbpin.setPintype(ModlePin.TYPE_PIN_MVFB);
            mvfbpin.setResource(mvfbresource);
            mvfbpin.setModleOpcTag(mvfb);
            mvfbpin.setOpcTagName(mvfbcomment);

            mvfbpin.setModlepinsId(mvfbpinid.equals("") ? -1 : Integer.parseInt(mvfbpinid));
            mvfbpin.setReference_modleId(modleid);
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /**mvfb滤波相关处理*/
        String filtercoefmv = null;
        String filteropctagmv = null;
        String filternamemv = null;
        Filter mvfbfilter = null;
        String filtermvfbresource;
        String mvfbfilterpinid;
        try {
            filtercoefmv = modlejsonObject.getString("filtercoefmv");
            filteropctagmv = modlejsonObject.getString("filteropctagmv");
            filternamemv = modlejsonObject.getString("filternamemv");
            mvfbfilterpinid = modlejsonObject.getString("mvfbfilterpinid");
            filtermvfbresource = modlejsonObject.getString("filtermvfb" + "resource");

            if (filternamemv.equals("mvav")) {
                MoveAverageFilter moveAverageFilter = new MoveAverageFilter();
                moveAverageFilter.setBackToDCSTag(filteropctagmv);
                moveAverageFilter.setOpcresource(filtermvfbresource);
                moveAverageFilter.setCapacity(Double.valueOf(filtercoefmv).intValue());//窗口时间长度
                moveAverageFilter.setFiltername(filternamemv);
                moveAverageFilter.setPk_filterid(mvfbfilterpinid.equals("") ? -1 : Integer.parseInt(mvfbfilterpinid));
                mvfbfilter = (moveAverageFilter);
            } else if (filternamemv.equals("fodl")) {
                FirstOrderLagFilter firstOrderLagFilter = new FirstOrderLagFilter();
                firstOrderLagFilter.setBackToDCSTag(filteropctagmv);
                firstOrderLagFilter.setOpcresource(filtermvfbresource);
                firstOrderLagFilter.setFilter_alphe(Double.valueOf(filtercoefmv));
                firstOrderLagFilter.setFiltername(filternamemv);
                firstOrderLagFilter.setPk_filterid(mvfbfilterpinid.equals("") ? -1 : Integer.parseInt(mvfbfilterpinid));
                mvfbfilter = (firstOrderLagFilter);
            } else if (filternamemv.equals("")) {
                MoveAverageFilter moveAverageFilter = new MoveAverageFilter();
                moveAverageFilter.setPk_filterid(mvfbfilterpinid.equals("") ? -1 : Integer.parseInt(mvfbfilterpinid));
                mvfbfilter = moveAverageFilter;
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        /**
         * mv震荡检测属性
         * */
        String detectwindowstimemv = null;//震荡检测检测窗口时间
        String detectdampcoemv = null;//震荡检测检测阻尼
        String detectfiltercoemv = null;//震荡检测滤波系数
        String mvfbshockerid = null;
        String detectfilteroutopctagmv;
        String detectfilteroutopctagmvresource;
        String detectamplitudeoutopctagmv;
        String detectamplitudeoutopctagmvresource;
        ShockDetector mvfbshockDetector = null;

        try {
            detectwindowstimemv = modlejsonObject.getString("detectwindowstimemv");
            detectdampcoemv = modlejsonObject.getString("detectdampcoemv");
            detectfiltercoemv = modlejsonObject.getString("detectfiltercoemv");
            detectfilteroutopctagmv = modlejsonObject.getString("detectfilteroutopctagmv");//震荡检测检测阻尼
            detectfilteroutopctagmvresource = modlejsonObject.getString("detectfilteroutopctagmv" + "resource");//震荡检测检测阻尼
            detectamplitudeoutopctagmv = modlejsonObject.getString("detectamplitudeoutopctagmv");//震荡检测检测阻尼
            detectamplitudeoutopctagmvresource = modlejsonObject.getString("detectamplitudeoutopctagmv" + "resource");//震荡检测检测阻尼
            mvfbshockerid = modlejsonObject.getString("mvfbshockerid");//震荡检测检测阻尼


            mvfbshockDetector = new ShockDetector();
            mvfbshockDetector.setEnable(1);
            if (!detectwindowstimemv.equals("")) {
                mvfbshockDetector.setWindowstime(Math.abs(Integer.valueOf(detectwindowstimemv)));
            }
            if (!detectdampcoemv.equals("")) {
                mvfbshockDetector.setDampcoeff(Double.valueOf(detectdampcoemv));
            }

            if (!detectfiltercoemv.equals("")) {
                mvfbshockDetector.setFiltercoeff(Math.abs(Double.valueOf(detectfiltercoemv)) > 1 ? 1 : Math.abs(Double.valueOf(detectfiltercoemv)));
            }


            mvfbshockDetector.setBackToDCSTag(detectamplitudeoutopctagmv);
            mvfbshockDetector.setOpcresource(detectamplitudeoutopctagmvresource);
            mvfbshockDetector.setFilterbacktodcstag(detectfilteroutopctagmv);
            mvfbshockDetector.setFilteropcresource(detectfilteroutopctagmvresource);
            mvfbshockDetector.setPk_shockdetectid(mvfbshockerid.equals("") ? -1 : Integer.parseInt(mvfbshockerid));


        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        ModlePin mvenbalepin = null;
        String mvenableid;
        String mvenable;
        String mvenableresource;
        try {
            mvenableid = modlejsonObject.getString("mvenableid");
            mvenable = modlejsonObject.getString("mvenable");
            mvenableresource = modlejsonObject.getString("mvenableresource");


            mvenbalepin = new ModlePin();
            mvenbalepin.setModleOpcTag(mvenable);
            mvenbalepin.setResource(mvenableresource);
            mvenbalepin.setModlepinsId(mvenableid.equals("") ? -1 : Integer.parseInt(mvenableid));
            mvenbalepin.setReference_modleId(modleid);
            mvenbalepin.setModlePinName(ModlePin.TYPE_PIN_PIN_MVENABLE + pinscope);
            mvenbalepin.setPintype(ModlePin.TYPE_PIN_PIN_MVENABLE);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        try {
            if ((!mvopctag.equals("")) && (!mvup.equals("")) && (!mvdown.equals("")) && (!mvfb.equals(""))) {
                modleDBServe.insertmvandmvfb(mvpin, mvdownpin, mvuppin, mvfbpin, mvfbfilter, mvfbshockDetector, mvenbalepin);
            } else {
                result.put("msg", "error");
                return result.toJSONString();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        result.put("msg", "success");
        return result.toJSONString();
    }

    @RequestMapping("/savenewmodelffpin")
    @ResponseBody
    public String savenewmodelffpin(@RequestParam("modlepincontext") String modlepincontext) {

        JSONObject modlejsonObject = JSONObject.parseObject(modlepincontext);
        JSONObject result = new JSONObject();


        /*****ff***/
        int modleid;
        String ff = null;
        ModlePin ffpin = null;
        String ffcomment = null;
        String ffpinid = null;
        String ffpinopcresurce;
        String pinName;
        int pinscope;
        try {
            modleid = modlejsonObject.getInteger("modleid");
            ff = modlejsonObject.getString("ff").trim();
            ffcomment = modlejsonObject.getString("ff" + "comment").trim();
            ffpinid = modlejsonObject.getString("ffpinid").trim();
            ffpinopcresurce = modlejsonObject.getString(ModlePin.TYPE_PIN_FF + "resource");
            pinName = modlejsonObject.getString("pinName");


            List<ModlePin> usemodlePins = modleDBServe.pinsbypintype(modleid, ModlePin.TYPE_PIN_FF);

            Matcher pvmatch = ffpattern.matcher(pinName);
            if (pvmatch.find()) {
                pinscope = Integer.parseInt(pvmatch.group(2));
                for (ModlePin usepvpin : usemodlePins) {
                    if (usepvpin.getModlePinName().equals(ModlePin.TYPE_PIN_FF + pinscope)) {
                        throw new RuntimeException("引脚已经使用");
                    }
                }
            } else {
                throw new RuntimeException("引脚匹配错误");
            }


            ffpin = new ModlePin();
            ffpin.setReference_modleId(modleid);
            ffpin.setModlePinName(ModlePin.TYPE_PIN_FF + pinscope);
            ffpin.setPintype(ModlePin.TYPE_PIN_FF);
            ffpin.setResource(ffpinopcresurce);
            ffpin.setModleOpcTag(ff);
            ffpin.setOpcTagName(ffcomment);
            ffpin.setModlepinsId(ffpinid.equals("") ? -1 : Integer.parseInt(ffpinid));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /******up and down*****/

        String ffup = null;
        String ffdown = null;
        String ffupresource = null;
        String ffdownresource = null;//ffdwon4resource
        ModlePin ffuppin;
        ModlePin ffdownpin;
        String ffuppinid;
        String ffdownpinid;
        try {
            ffup = modlejsonObject.getString("ffup").trim();
            ffdown = modlejsonObject.getString("ffdown").trim();

            ffuppinid = modlejsonObject.getString("ffuppinid").trim();


            ffdownpinid = modlejsonObject.getString("ffdownpinid").trim();

            ffupresource = modlejsonObject.getString("ffup" + "resource");
            ffdownresource = modlejsonObject.getString("ffdown" + "resource");


            ffuppin = new ModlePin();
            ffuppin.setReference_modleId(modleid);
            ffuppin.setModlePinName(ModlePin.TYPE_PIN_FFUP + pinscope);
            ffuppin.setPintype(ModlePin.TYPE_PIN_FFUP);
            ffuppin.setResource(ffupresource);
            ffuppin.setModleOpcTag(ffup);
            ffuppin.setModlepinsId(ffuppinid.equals("") ? -1 : Integer.parseInt(ffuppinid));


            ffdownpin = new ModlePin();
            ffdownpin.setReference_modleId(modleid);
            ffdownpin.setModlePinName(ModlePin.TYPE_PIN_FFDOWN + pinscope);
            ffdownpin.setPintype(ModlePin.TYPE_PIN_FFDOWN);
            ffdownpin.setResource(ffdownresource);
            ffdownpin.setModleOpcTag(ffdown);
            ffdownpin.setModlepinsId(ffdownpinid.equals("") ? -1 : Integer.parseInt(ffdownpinid));

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /**滤波相关处理*/
        String filternameff = null;
        String filtercoefff;
        String filteropctagff;
        String filterffresource;
        String ffpinfilterid;
        Filter ffpinfilter = null;
        try {
            filtercoefff = modlejsonObject.getString("filtercoefff");
            filteropctagff = modlejsonObject.getString("filteropctagff");
            filternameff = modlejsonObject.getString("filternameff");
            filterffresource = modlejsonObject.getString("filterff" + "resource");
            ffpinfilterid = modlejsonObject.getString("ffpinfilterid");

            if (filternameff.equals("mvav")) {
                MoveAverageFilter moveAverageFilter = new MoveAverageFilter();
                moveAverageFilter.setBackToDCSTag(filteropctagff);
                moveAverageFilter.setOpcresource(filterffresource);
                moveAverageFilter.setCapacity(Double.valueOf(filtercoefff).intValue());//窗口时间长度
                moveAverageFilter.setFiltername(filternameff);
                moveAverageFilter.setPk_filterid(ffpinfilterid.equals("") ? -1 : Integer.valueOf(ffpinfilterid));
                ffpinfilter = moveAverageFilter;
            } else if (filternameff.equals("fodl")) {
                FirstOrderLagFilter firstOrderLagFilter = new FirstOrderLagFilter();
                firstOrderLagFilter.setBackToDCSTag(filteropctagff);
                firstOrderLagFilter.setOpcresource(filterffresource);
                firstOrderLagFilter.setFilter_alphe(Math.abs(Double.valueOf(filtercoefff)) > 1 ? 1 : Math.abs(Double.valueOf(filtercoefff)));
                firstOrderLagFilter.setFiltername(filternameff);
                firstOrderLagFilter.setPk_filterid(ffpinfilterid.equals("") ? -1 : Integer.valueOf(ffpinfilterid));
                ffpinfilter = firstOrderLagFilter;
            } else if (filternameff.equals("")) {
                ffpinfilter = new MoveAverageFilter();
                ffpinfilter.setPk_filterid(ffpinfilterid.equals("") ? -1 : Integer.valueOf(ffpinfilterid));
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /********enable****/
        String ffenableid = null;
        String ffenable;
        String ffenableresource;
        ModlePin ffenablepin;

        try {
            ffenableid = modlejsonObject.getString("ffenableid");
            ffenable = modlejsonObject.getString("ffenable");
            ffenableresource = modlejsonObject.getString("ffenableresource");

            ffenablepin = new ModlePin();
            ffenablepin.setPintype(ModlePin.TYPE_PIN_PIN_FFENABLE);
            ffenablepin.setModlePinName(ModlePin.TYPE_PIN_PIN_FFENABLE + pinscope);
            ffenablepin.setModlepinsId(ffenableid.equals("") ? -1 : Integer.parseInt(ffenableid));
            ffenablepin.setReference_modleId(modleid);
            ffenablepin.setModleOpcTag(ffenable);
            ffenablepin.setResource(ffenableresource);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        try {
            if ((!ffpin.getModleOpcTag().equals("")) && (!ffuppin.getModleOpcTag().equals("")) && (!ffdownpin.getModleOpcTag().equals(""))) {
                modleDBServe.insertff(ffpin, ffuppin, ffdownpin, ffenablepin, ffpinfilter);
            } else {
                result.put("msg", "error");
                return result.toJSONString();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        result.put("msg", "success");
        return result.toJSONString();
    }


    @RequestMapping("/savenewrespon")
    @ResponseBody
    public String savenewrespon(@RequestParam("responcontext") String responcontext) {

        JSONObject modlejsonObject = JSONObject.parseObject(responcontext);
        JSONObject result = new JSONObject();


        int modleid;
        String responid;
        String inputpinName;
        String outputpinName;
        float K;
        float T;
        float Tau;
        ResponTimeSerise respontimeserise;
        JSONObject jsonres;

        try {
            modleid = modlejsonObject.getInteger("modleid");
            responid = modlejsonObject.getString("responid").trim();
            inputpinName = modlejsonObject.getString("inputpinName").trim();
            outputpinName = modlejsonObject.getString("outputpinName").trim();
            K = modlejsonObject.getFloat("K");
            T = modlejsonObject.getFloat("T");
            Tau = modlejsonObject.getFloat("Tau");
            respontimeserise = new ResponTimeSerise();

            respontimeserise.setInputPins(inputpinName);
            respontimeserise.setOutputPins(outputpinName);
            respontimeserise.setRefrencemodleId(modleid);
            respontimeserise.setModletagId(responid.equals("") ? -1 : Integer.valueOf(responid));
            jsonres = new JSONObject();
            jsonres.put("k", K);
            jsonres.put("t", T);
            jsonres.put("tao", Tau);
            respontimeserise.setStepRespJson(jsonres.toJSONString());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        try {
            if (jsonres != null) {
                modleDBServe.insertOrUpdateTimeSerise(respontimeserise);
            } else {
                result.put("msg", "error");
                return result.toJSONString();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        result.put("msg", "success");
        return result.toJSONString();
    }


    @RequestMapping("/modofymodelpvpin")
    public ModelAndView modofymodelpvpin(@RequestParam("modleid") int modleid, @RequestParam("pinid") int pinid, @RequestParam("pintype") String pintype) {

        ModlePin pvpin = modleDBServe.findPinbypinid(pinid);
        ModlePin pvuppin;
        ModlePin pvdownpin;
        ModlePin pvpinenable;
        ModlePin sppin;
        int pvpinscope = -1;
        Matcher matcher = pvpattern.matcher(pvpin.getModlePinName());
        if (matcher.find()) {
            pvpinscope = Integer.parseInt(matcher.group(2));
        }
        if (-1 == pvpinscope) {
            throw new RuntimeException("要求修改的位号有索引不对");
        }

        pvuppin = modleDBServe.findPinbypinmodleidAndpinname(modleid, ModlePin.TYPE_PIN_PVUP + pvpinscope);
        pvdownpin = modleDBServe.findPinbypinmodleidAndpinname(modleid, ModlePin.TYPE_PIN_PVDOWN + pvpinscope);
        pvpinenable = modleDBServe.findPinbypinmodleidAndpinname(modleid, ModlePin.TYPE_PIN_PIN_PVENABLE + pvpinscope);
        sppin = modleDBServe.findPinbypinmodleidAndpinname(modleid, ModlePin.TYPE_PIN_SP + pvpinscope);


//        modleDBServe.findPinbypinmodleidAndpinname(modleid, ModlePin.TYPE_PIN_PV + pvpinscope);

        List<ModlePin> usepinscope = modleDBServe.pinsbypintype(modleid, pintype);
        List<Integer> unuserpinscope = Tool.getunUserPinScope(pvpattern, usepinscope, baseConf.getPv());


        ModelAndView view = new ModelAndView();
        view.setViewName("contrlmodle/modifypvpin");
        view.addObject("pvpin", pvpin);
        view.addObject("pvuppin", pvuppin);
        view.addObject("pvdownpin", pvdownpin);
        view.addObject("pvpinenable", pvpinenable);
        view.addObject("pintype", pintype);
        view.addObject("sppin", sppin);

        List<String> opcresources = new ArrayList<>();
        for (OPCService opcService : opcServicConstainer.getOpcservepool().values()) {
            opcresources.add("opc" + opcService.getOpcip());
        }
        opcresources.add("constant");
        view.addObject("opcresources", opcresources);


        unuserpinscope.add(0, pvpinscope);

        view.addObject("unuserpinscope", unuserpinscope);
        return view;
    }


    @RequestMapping("/modofymodelmvpin")
    public ModelAndView modofymodelmvpin(@RequestParam("modleid") int modleid, @RequestParam("pinid") int pinid, @RequestParam("pintype") String pintype) {

        ModlePin mvpin = modleDBServe.findPinbypinid(pinid);
        ModlePin mvuppin;
        ModlePin mvdownpin;
        ModlePin mvpinenable;
        ModlePin mvfbpin;
        int mvpinscope = -1;
        Matcher matcher = mvpattern.matcher(mvpin.getModlePinName());
        if (matcher.find()) {
            mvpinscope = Integer.parseInt(matcher.group(2));
        }
        if (-1 == mvpinscope) {
            throw new RuntimeException("要求修改的位号有索引不对");
        }

        mvuppin = modleDBServe.findPinbypinmodleidAndpinname(modleid, ModlePin.TYPE_PIN_MVUP + mvpinscope);
        mvdownpin = modleDBServe.findPinbypinmodleidAndpinname(modleid, ModlePin.TYPE_PIN_MVDOWN + mvpinscope);
        mvpinenable = modleDBServe.findPinbypinmodleidAndpinname(modleid, ModlePin.TYPE_PIN_PIN_MVENABLE + mvpinscope);
        mvfbpin = modleDBServe.findPinbypinmodleidAndpinname(modleid, ModlePin.TYPE_PIN_MVFB + mvpinscope);


//        modleDBServe.findPinbypinmodleidAndpinname(modleid, ModlePin.TYPE_PIN_MV + mvpinscope);

        List<ModlePin> usepinscope = modleDBServe.pinsbypintype(modleid, pintype);
        List<Integer> unuserpinscope = Tool.getunUserPinScope(mvpattern, usepinscope, baseConf.getMv());


        ModelAndView view = new ModelAndView();
        view.setViewName("contrlmodle/modifymvpin");
        view.addObject("mvpin", mvpin);
        view.addObject("mvuppin", mvuppin);
        view.addObject("mvdownpin", mvdownpin);
        view.addObject("mvpinenable", mvpinenable);
        view.addObject("pintype", pintype);
        view.addObject("mvfbpin", mvfbpin);


        List<String> opcresources = new ArrayList<>();
        for (OPCService opcService : opcServicConstainer.getOpcservepool().values()) {
            opcresources.add("opc" + opcService.getOpcip());
        }
        opcresources.add("constant");
        view.addObject("opcresources", opcresources);


        unuserpinscope.add(0, mvpinscope);

        view.addObject("unuserpinscope", unuserpinscope);
        return view;
    }

    @RequestMapping("/modofymodelffpin")
    public ModelAndView modofymodelffpin(@RequestParam("modleid") int modleid, @RequestParam("pinid") int pinid, @RequestParam("pintype") String pintype) {

        ModlePin ffpin = modleDBServe.findPinbypinid(pinid);
        ModlePin ffuppin;
        ModlePin ffdownpin;
        ModlePin ffpinenable;
        int mvpinscope = -1;
        Matcher matcher = ffpattern.matcher(ffpin.getModlePinName());
        if (matcher.find()) {
            mvpinscope = Integer.parseInt(matcher.group(2));
        }
        if (-1 == mvpinscope) {
            throw new RuntimeException("要求修改的位号有索引不对");
        }

        ffuppin = modleDBServe.findPinbypinmodleidAndpinname(modleid, ModlePin.TYPE_PIN_FFUP + mvpinscope);
        ffdownpin = modleDBServe.findPinbypinmodleidAndpinname(modleid, ModlePin.TYPE_PIN_FFDOWN + mvpinscope);
        ffpinenable = modleDBServe.findPinbypinmodleidAndpinname(modleid, ModlePin.TYPE_PIN_PIN_FFENABLE + mvpinscope);

        List<ModlePin> usepinscope = modleDBServe.pinsbypintype(modleid, pintype);
        List<Integer> unuserpinscope = Tool.getunUserPinScope(ffpattern, usepinscope, baseConf.getFf());


        ModelAndView view = new ModelAndView();
        view.setViewName("contrlmodle/modifyffpin");
        view.addObject("ffpin", ffpin);
        view.addObject("ffuppin", ffuppin);
        view.addObject("ffdownpin", ffdownpin);
        view.addObject("ffpinenable", ffpinenable);
        view.addObject("pintype", pintype);


        List<String> opcresources = new ArrayList<>();
        for (OPCService opcService : opcServicConstainer.getOpcservepool().values()) {
            opcresources.add("opc" + opcService.getOpcip());
        }
        opcresources.add("constant");
        view.addObject("opcresources", opcresources);


        unuserpinscope.add(0, mvpinscope);

        view.addObject("unuserpinscope", unuserpinscope);
        return view;
    }


    @RequestMapping("/modofyrespon")
    public ModelAndView modofyrespon(@RequestParam("modleid") int modleid, @RequestParam("responid") int responid) {

        ResponTimeSerise respontimeserise = modleDBServe.findPinbyresponid(responid);

        List<ModlePin> userinputpinscope = new ArrayList<>();

        List<ModlePin> usemvpinscope = null;
        List<ModlePin> useffpinscope = null;


        List<ModlePin> useroutputpinscope = null;


        try {
            usemvpinscope = modleDBServe.pinsbypintype(modleid, ModlePin.TYPE_PIN_MV);
            useffpinscope = modleDBServe.pinsbypintype(modleid, ModlePin.TYPE_PIN_FF);
            userinputpinscope.addAll(usemvpinscope);
            userinputpinscope.addAll(useffpinscope);

            useroutputpinscope = modleDBServe.pinsbypintype(modleid, ModlePin.TYPE_PIN_PV);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }


        ModelAndView view = new ModelAndView();
        view.setViewName("contrlmodle/modifyrespon");
        view.addObject("modleid", modleid);

        view.addObject("userinputpinscope", userinputpinscope);
        view.addObject("useroutputpinscope", useroutputpinscope);
        view.addObject("respontimeserise", respontimeserise);
        JSONObject jsonrespm = JSONObject.parseObject(respontimeserise.getStepRespJson());
        view.addObject("K", jsonrespm.get("k"));
        view.addObject("T", jsonrespm.get("t"));
        view.addObject("Tau", jsonrespm.get("tao"));

        return view;
    }


    @RequestMapping("/savemodifymodelpvpin")
    @ResponseBody
    public String savemodifymodelpvpin(@RequestParam("modlepincontext") String modlepincontext) {

        JSONObject modlejsonObject = JSONObject.parseObject(modlepincontext);
        JSONObject result = new JSONObject();


        Integer modleid;


        /**pv*/
        String pinName = null;
        String pvTag = null;
        String resource = null;
        String pvcomment = null;
        String QTag = null;
        Double pvDeadZone = null;
        Double pvFunelInitValue = null;
        String pvfuneltype = null;
        Double pvtracoef = null;//参考轨迹系数
        String pvpinid;
        ModlePin pvpin = null;

        int pinscope;
        try {
            modleid = modlejsonObject.getInteger("modleid");
            pinName = modlejsonObject.getString("pinName");

            List<ModlePin> usemodlePins = modleDBServe.pinsbypintype(modleid, ModlePin.TYPE_PIN_PV);


            pvTag = modlejsonObject.getString(ModlePin.TYPE_PIN_PV).trim();
            resource = modlejsonObject.getString(ModlePin.TYPE_PIN_PV + "resource");
            pvcomment = modlejsonObject.getString(ModlePin.TYPE_PIN_PV + "comment").trim();
            QTag = modlejsonObject.getString("q");
            pvDeadZone = modlejsonObject.getDouble(ModlePin.TYPE_PIN_PV + "DeadZone");
            pvFunelInitValue = modlejsonObject.getDouble(ModlePin.TYPE_PIN_PV + "FunelInitValue");
            pvfuneltype = modlejsonObject.getString("funneltype");
            pvtracoef = modlejsonObject.getDouble("tracoef");
            pvpinid = modlejsonObject.getString("pvpinid");


            pvpin = new ModlePin();
            pvpin.setResource(resource);
            pvpin.setModleOpcTag(pvTag);
            pvpin.setQ(Double.valueOf(QTag));
            pvpin.setReference_modleId(modleid);
            pvpin.setModlePinName(pinName);
            pvpin.setPintype(ModlePin.TYPE_PIN_PV);
            pvpin.setDeadZone(pvDeadZone);
            pvpin.setFunelinitValue(pvFunelInitValue);
            pvpin.setFunneltype(pvfuneltype);
            pvpin.setOpcTagName(pvcomment);
            pvpin.setReferTrajectoryCoef(pvtracoef);
            pvpin.setModlepinsId(pvpinid.equals("") ? -1 : Integer.parseInt(pvpinid));

            Matcher pvmatch = pvpattern.matcher(pinName);
            if (pvmatch.find()) {
                pinscope = Integer.parseInt(pvmatch.group(2));
            } else {
                throw new RuntimeException("引脚匹配错误");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /**pv enable*/
        String pvenable = null;
        String pvenableresource = null;
        String pvenableid;
        ModlePin pvenablepin = null;
        try {
            pvenable = modlejsonObject.getString(ModlePin.TYPE_PIN_PIN_PVENABLE);
            pvenableresource = modlejsonObject.getString(ModlePin.TYPE_PIN_PIN_PVENABLE + "resource");
            pvenableid = modlejsonObject.getString("pvenableid");

            //if((!pvenable.equals(""))&&(!pvenableresource.equals(""))){
            pvenablepin = new ModlePin();
            pvenablepin.setReference_modleId(modleid);

            pvenablepin.setModlePinName(ModlePin.TYPE_PIN_PIN_PVENABLE + pinscope);
            pvenablepin.setPintype(ModlePin.TYPE_PIN_PIN_PVENABLE);
            pvenablepin.setModleOpcTag(pvenable);
            pvenablepin.setResource(pvenableresource);
            pvenablepin.setModlepinsId(pvenableid.equals("") ? -1 : Integer.parseInt(pvenableid));
            // }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /**pv置信区间位号*/
        ModlePin pvuppin = null;
        try {
            String pvup = modlejsonObject.getString(ModlePin.TYPE_PIN_PVUP).trim();
            String pvupresource = modlejsonObject.getString(ModlePin.TYPE_PIN_PVUP + "resource").trim();
            String pvuppinid = modlejsonObject.getString("pvuppinid");

            //if((!pvup.equals(""))&&(!pvupresource.equals(""))){
            pvuppin = new ModlePin();
            pvuppin.setReference_modleId(modleid);
            pvuppin.setModlePinName(ModlePin.TYPE_PIN_PVUP + pinscope);
            pvuppin.setPintype(ModlePin.TYPE_PIN_PVUP);
            pvuppin.setModleOpcTag(pvup);
            pvuppin.setResource(pvupresource);
            pvuppin.setModlepinsId(pvuppinid.equals("") ? -1 : Integer.parseInt(pvuppinid));
            //}

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        ModlePin pvdownpin = null;
        try {
            String pvdown = modlejsonObject.getString(ModlePin.TYPE_PIN_PVDOWN).trim();
            String pvdownresource = modlejsonObject.getString(ModlePin.TYPE_PIN_PVDOWN + "resource").trim();
            String pvdownpinid = modlejsonObject.getString("pvdownpinid");

            //if((!pvdown.equals(""))&&(!pvdownresource.equals(""))){
            pvdownpin = new ModlePin();
            pvdownpin.setReference_modleId(modleid);
            pvdownpin.setModlePinName(ModlePin.TYPE_PIN_PVDOWN + pinscope);
            pvdownpin.setPintype(ModlePin.TYPE_PIN_PVDOWN);
            pvdownpin.setModleOpcTag(pvdown);
            pvdownpin.setResource(pvdownresource);
            pvdownpin.setModlepinsId(pvdownpinid.equals("") ? -1 : Integer.parseInt(pvdownpinid));
            //}

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        /**
         * pv震荡检测属性
         * */
        String detectwindowstimepv = null;//震荡检测检测窗口时间
        String detectdampcoepv = null;//震荡检测检测阻尼
        String detectfiltercoepv = null;//震荡检测滤波系数
        String detectfilteroutopctagpv = null;//震荡检测检滤波输出
        String detectfilteroutopctagpvresource = null;//
        String detectamplitudeoutopctagpv = null;//震荡检测振幅
        String detectamplitudeoutopctagpvresource = null;//
        String shockerid;

        ShockDetector shockDetector = null;
        try {
            detectwindowstimepv = modlejsonObject.getString("detectwindowstimepv");
            detectdampcoepv = modlejsonObject.getString("detectdampcoepv");
            detectfiltercoepv = modlejsonObject.getString("detectfiltercoepv");
            detectfilteroutopctagpv = modlejsonObject.getString("detectfilteroutopctagpv");
            detectfilteroutopctagpvresource = modlejsonObject.getString("detectfilteroutopctagpv" + "resource");
            detectamplitudeoutopctagpv = modlejsonObject.getString("detectamplitudeoutopctagpv");
            detectamplitudeoutopctagpvresource = modlejsonObject.getString("detectamplitudeoutopctagpv" + "resource");
            shockerid = modlejsonObject.getString("shockerid");

            shockDetector = new ShockDetector();
            if (!detectwindowstimepv.equals("")) {
                shockDetector.setWindowstime(Integer.valueOf(detectwindowstimepv));
            }
            if (!detectdampcoepv.equals("")) {
                shockDetector.setDampcoeff(Double.valueOf(detectdampcoepv));
            }

            if (!detectfiltercoepv.equals("")) {
                shockDetector.setFiltercoeff(Math.abs(Double.valueOf(detectfiltercoepv)) > 1 ? 1 : Math.abs(Double.valueOf(detectfiltercoepv)));
            }
            shockDetector.setBackToDCSTag(detectamplitudeoutopctagpv);
            shockDetector.setOpcresource(detectamplitudeoutopctagpvresource);
            shockDetector.setFilterbacktodcstag(detectfilteroutopctagpv);
            shockDetector.setFilteropcresource(detectfilteroutopctagpvresource);
            shockDetector.setPk_shockdetectid(shockerid.equals("") ? -1 : Integer.valueOf(shockerid));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /**
         * 滤波相关处理
         * */
        String filtercoefpv = null;
        String filteropctagpv = null;
        String filternamepv = null;
        String filterpvres = null;
        String pinfilterid = null;
        Filter pinfiter = null;
        try {
            filtercoefpv = modlejsonObject.getString("filtercoefpv");
            filteropctagpv = modlejsonObject.getString("filteropctagpv");
            filternamepv = modlejsonObject.getString("filternamepv");
            filterpvres = modlejsonObject.getString("filterpv" + "resource");
            pinfilterid = modlejsonObject.getString("pinfilterid");

            if (filternamepv != null) {
                if (filternamepv.equals("mvav")) {
                    MoveAverageFilter moveAverageFilter = new MoveAverageFilter();
                    moveAverageFilter.setBackToDCSTag(filteropctagpv);
                    moveAverageFilter.setOpcresource(filterpvres);
                    moveAverageFilter.setCapacity(Double.valueOf(filtercoefpv).intValue());//窗口时间长度
                    moveAverageFilter.setFiltername(filternamepv);
                    moveAverageFilter.setPk_filterid(pinfilterid.equals("") ? -1 : Integer.valueOf(pinfilterid));
                    pinfiter = moveAverageFilter;
                } else if (filternamepv.equals("fodl")) {
                    FirstOrderLagFilter firstOrderLagFilter = new FirstOrderLagFilter();
                    firstOrderLagFilter.setBackToDCSTag(filteropctagpv);
                    firstOrderLagFilter.setOpcresource(filterpvres);
                    firstOrderLagFilter.setFilter_alphe(Math.abs(Double.valueOf(filtercoefpv)) > 1 ? 1 : Math.abs(Double.valueOf(filtercoefpv)));
                    firstOrderLagFilter.setFiltername(filternamepv);
                    firstOrderLagFilter.setPk_filterid(pinfilterid.equals("") ? -1 : Integer.valueOf(pinfilterid));
                    pinfiter = firstOrderLagFilter;
                } else if (filternamepv.equals("")) {
                    MoveAverageFilter moveAverageFilter = new MoveAverageFilter();
                    moveAverageFilter.setPk_filterid(pinfilterid.equals("") ? -1 : Integer.valueOf(pinfilterid));
                    pinfiter = moveAverageFilter;
                }
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /****SP**/
        String spTag = null;
        String spcomment = null;
        ModlePin sppin = null;
        String sppinid;
        try {
            spTag = modlejsonObject.getString(ModlePin.TYPE_PIN_SP);
            spcomment = modlejsonObject.getString(ModlePin.TYPE_PIN_SP + "comment").trim();
            String spresource = modlejsonObject.getString(ModlePin.TYPE_PIN_SP + "resource");

            sppinid = modlejsonObject.getString("sppinid");


            sppin = new ModlePin();
            sppin.setReference_modleId(modleid);
            sppin.setModleOpcTag(spTag);
            sppin.setOpcTagName(spcomment);
            sppin.setResource(spresource);

            sppin.setModlePinName(ModlePin.TYPE_PIN_SP + pinscope);
            sppin.setPintype(ModlePin.TYPE_PIN_SP);
            sppin.setModlepinsId(sppinid.equals("") ? -1 : Integer.parseInt(sppinid));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        try {
            if ((!pinName.equals("")) && (!pvTag.equals("")) && (!spTag.equals(""))) {
                modleDBServe.insertpvandsp(pvpin, pvenablepin, pvuppin, pvdownpin, shockDetector, pinfiter, sppin);
            } else {
                result.put("msg", "error");
                return result.toJSONString();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        result.put("msg", "success");
        return result.toJSONString();
    }


    @RequestMapping("/savemodifymodelmvpin")
    @ResponseBody
    public String savemodifymodelmvpin(@RequestParam("modlepincontext") String modlepincontext) {


        JSONObject modlejsonObject = JSONObject.parseObject(modlepincontext);
        JSONObject result = new JSONObject();

        String mvopctag = null;
        String mvresource = null;
        String mvcomment = null;
        String r = null;
        String dmvhigh = null;
        String dmvlow = null;
        String mvpinid = null;
        Integer modleid;

        int pinscope = -1;
        ModlePin mvpin = null;
        try {
            mvopctag = modlejsonObject.getString("mv").trim();
            mvcomment = modlejsonObject.getString("mv" + "comment").trim();
            r = modlejsonObject.getString("r").trim();
            mvresource = modlejsonObject.getString(ModlePin.TYPE_PIN_MV + "resource");

            dmvhigh = modlejsonObject.getString("dmv" + "High");
            dmvlow = modlejsonObject.getString("dmv" + "Low");


            mvpinid = modlejsonObject.getString("mvpinid").trim();
            modleid = modlejsonObject.getInteger("modleid");


            String pinName = modlejsonObject.getString("pinName");
            List<ModlePin> usemodlePins = modleDBServe.pinsbypintype(modleid, ModlePin.TYPE_PIN_MV);

            Matcher pvmatch = mvpattern.matcher(pinName);
            if (pvmatch.find()) {
                pinscope = Integer.parseInt(pvmatch.group(2));
//                for (ModlePin usepvpin : usemodlePins) {
//                    if (usepvpin.getModlePinName().equals(ModlePin.TYPE_PIN_MV + pinscope)) {
//                        throw new RuntimeException("引脚已经使用");
//                    }
//                }
            } else {
                throw new RuntimeException("引脚匹配错误");
            }


            mvpin = new ModlePin();
            mvpin.setR(Double.valueOf(r));
            mvpin.setResource(mvresource);
            mvpin.setModleOpcTag(mvopctag);
            mvpin.setModlePinName(ModlePin.TYPE_PIN_MV + pinscope);
            mvpin.setPintype(ModlePin.TYPE_PIN_MV);
            mvpin.setDmvHigh(Double.valueOf(dmvhigh));
            mvpin.setDmvLow(Double.valueOf(dmvlow));
            mvpin.setOpcTagName(mvcomment);

            mvpin.setModlepinsId(mvpinid.equals("") ? -1 : Integer.parseInt(mvpinid));
            mvpin.setReference_modleId(modleid);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /***上下限**/

        ModlePin mvdownpin = null;
        ModlePin mvuppin = null;
        String mvuppinid;
        String mvdownpinid;
        String mvup = null;
        String mvdown = null;
        String mvupresoure = null;
        String mvdownresource = null;
        try {

            mvuppinid = modlejsonObject.getString("mvuppinid");
            mvup = modlejsonObject.getString("mvup").trim();
            mvupresoure = modlejsonObject.getString("mvup" + "resource");

            mvdown = modlejsonObject.getString("mvdown").trim();
            mvdownresource = modlejsonObject.getString("mvdown" + "resource");
            mvdownpinid = modlejsonObject.getString("mvdownpinid");


            mvuppin = new ModlePin();

            mvuppin.setModlePinName(ModlePin.TYPE_PIN_MVUP + pinscope);
            mvuppin.setPintype(ModlePin.TYPE_PIN_MVUP);
            mvuppin.setResource(mvupresoure);
            mvuppin.setModleOpcTag(mvup);

            mvuppin.setReference_modleId(modleid);
            mvuppin.setModlepinsId(mvuppinid.equals("") ? -1 : Integer.parseInt(mvuppinid));

            mvdownpin = new ModlePin();

            mvdownpin.setModlePinName(ModlePin.TYPE_PIN_MVDOWN + pinscope);
            mvdownpin.setPintype(ModlePin.TYPE_PIN_MVDOWN);
            mvdownpin.setResource(mvdownresource);
            mvdownpin.setModleOpcTag(mvdown);

            mvdownpin.setModlepinsId(mvdownpinid.equals("") ? -1 : Integer.parseInt(mvdownpinid));
            mvdownpin.setReference_modleId(modleid);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /**mvfb*/
        ModlePin mvfbpin;
        String mvfbpinid;
        String mvfb = null;
        String mvfbcomment = null;
        String mvfbresource = null;
        try {
            mvfb = modlejsonObject.getString("mvfb").trim();
            mvfbcomment = modlejsonObject.getString("mvfb" + "comment").trim();
            mvfbresource = modlejsonObject.getString(ModlePin.TYPE_PIN_MVFB + "resource");
            mvfbpinid = modlejsonObject.getString("mvfbpinid");


            mvfbpin = new ModlePin();
            mvfbpin.setModlePinName(ModlePin.TYPE_PIN_MVFB + pinscope);
            mvfbpin.setPintype(ModlePin.TYPE_PIN_MVFB);
            mvfbpin.setResource(mvfbresource);
            mvfbpin.setModleOpcTag(mvfb);
            mvfbpin.setOpcTagName(mvfbcomment);

            mvfbpin.setModlepinsId(mvfbpinid.equals("") ? -1 : Integer.parseInt(mvfbpinid));
            mvfbpin.setReference_modleId(modleid);
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /**mvfb滤波相关处理*/
        String filtercoefmv = null;
        String filteropctagmv = null;
        String filternamemv = null;
        Filter mvfbfilter = null;
        String filtermvfbresource;
        String mvfbfilterpinid;
        try {
            filtercoefmv = modlejsonObject.getString("filtercoefmv");
            filteropctagmv = modlejsonObject.getString("filteropctagmv");
            filternamemv = modlejsonObject.getString("filternamemv");
            mvfbfilterpinid = modlejsonObject.getString("mvfbfilterpinid");
            filtermvfbresource = modlejsonObject.getString("filtermvfb" + "resource");

            if (filternamemv.equals("mvav")) {
                MoveAverageFilter moveAverageFilter = new MoveAverageFilter();
                moveAverageFilter.setBackToDCSTag(filteropctagmv);
                moveAverageFilter.setOpcresource(filtermvfbresource);
                moveAverageFilter.setCapacity(Double.valueOf(filtercoefmv).intValue());//窗口时间长度
                moveAverageFilter.setFiltername(filternamemv);
                moveAverageFilter.setPk_filterid(mvfbfilterpinid.equals("") ? -1 : Integer.parseInt(mvfbfilterpinid));
                mvfbfilter = (moveAverageFilter);
            } else if (filternamemv.equals("fodl")) {
                FirstOrderLagFilter firstOrderLagFilter = new FirstOrderLagFilter();
                firstOrderLagFilter.setBackToDCSTag(filteropctagmv);
                firstOrderLagFilter.setOpcresource(filtermvfbresource);
                firstOrderLagFilter.setFilter_alphe(Double.valueOf(filtercoefmv));
                firstOrderLagFilter.setFiltername(filternamemv);
                firstOrderLagFilter.setPk_filterid(mvfbfilterpinid.equals("") ? -1 : Integer.parseInt(mvfbfilterpinid));
                mvfbfilter = (firstOrderLagFilter);
            } else if (filternamemv.equals("")) {
                MoveAverageFilter moveAverageFilter = new MoveAverageFilter();
                moveAverageFilter.setPk_filterid(mvfbfilterpinid.equals("") ? -1 : Integer.parseInt(mvfbfilterpinid));
                mvfbfilter = moveAverageFilter;
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        /**
         * mv震荡检测属性
         * */
        String detectwindowstimemv = null;//震荡检测检测窗口时间
        String detectdampcoemv = null;//震荡检测检测阻尼
        String detectfiltercoemv = null;//震荡检测滤波系数
        String mvfbshockerid = null;
        String detectfilteroutopctagmv;
        String detectfilteroutopctagmvresource;
        String detectamplitudeoutopctagmv;
        String detectamplitudeoutopctagmvresource;
        ShockDetector mvfbshockDetector = null;

        try {
            detectwindowstimemv = modlejsonObject.getString("detectwindowstimemv");
            detectdampcoemv = modlejsonObject.getString("detectdampcoemv");
            detectfiltercoemv = modlejsonObject.getString("detectfiltercoemv");
            detectfilteroutopctagmv = modlejsonObject.getString("detectfilteroutopctagmv");//震荡检测检测阻尼
            detectfilteroutopctagmvresource = modlejsonObject.getString("detectfilteroutopctagmv" + "resource");//震荡检测检测阻尼
            detectamplitudeoutopctagmv = modlejsonObject.getString("detectamplitudeoutopctagmv");//震荡检测检测阻尼
            detectamplitudeoutopctagmvresource = modlejsonObject.getString("detectamplitudeoutopctagmv" + "resource");//震荡检测检测阻尼
            mvfbshockerid = modlejsonObject.getString("mvfbshockerid");//震荡检测检测阻尼


            mvfbshockDetector = new ShockDetector();
            mvfbshockDetector.setEnable(1);
            if (!detectwindowstimemv.equals("")) {
                mvfbshockDetector.setWindowstime(Math.abs(Integer.valueOf(detectwindowstimemv)));
            }

            if (!detectdampcoemv.equals("")) {
                mvfbshockDetector.setDampcoeff(Double.valueOf(detectdampcoemv));
            }
            if (!detectfiltercoemv.equals("")) {
                mvfbshockDetector.setFiltercoeff(Math.abs(Double.valueOf(detectfiltercoemv)) > 1 ? 1 : Math.abs(Double.valueOf(detectfiltercoemv)));

            }
            mvfbshockDetector.setBackToDCSTag(detectamplitudeoutopctagmv);
            mvfbshockDetector.setOpcresource(detectamplitudeoutopctagmvresource);
            mvfbshockDetector.setFilterbacktodcstag(detectfilteroutopctagmv);
            mvfbshockDetector.setFilteropcresource(detectfilteroutopctagmvresource);
            mvfbshockDetector.setPk_shockdetectid(mvfbshockerid.equals("") ? -1 : Integer.parseInt(mvfbshockerid));


        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        ModlePin mvenbalepin = null;
        String mvenableid;
        String mvenable;
        String mvenableresource;
        try {
            mvenableid = modlejsonObject.getString("mvenableid");
            mvenable = modlejsonObject.getString("mvenable");
            mvenableresource = modlejsonObject.getString("mvenableresource");


            mvenbalepin = new ModlePin();
            mvenbalepin.setModleOpcTag(mvenable);
            mvenbalepin.setResource(mvenableresource);
            mvenbalepin.setModlepinsId(mvenableid.equals("") ? -1 : Integer.parseInt(mvenableid));
            mvenbalepin.setReference_modleId(modleid);
            mvenbalepin.setModlePinName(ModlePin.TYPE_PIN_PIN_MVENABLE + pinscope);
            mvenbalepin.setPintype(ModlePin.TYPE_PIN_PIN_MVENABLE);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        try {
            if ((!mvopctag.equals("")) && (!mvup.equals("")) && (!mvdown.equals("")) && (!mvfb.equals(""))) {
                modleDBServe.insertmvandmvfb(mvpin, mvdownpin, mvuppin, mvfbpin, mvfbfilter, mvfbshockDetector, mvenbalepin);
            } else {
                result.put("msg", "error");
                return result.toJSONString();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        result.put("msg", "success");
        return result.toJSONString();
    }


    @RequestMapping("/savemodifymodelffpin")
    @ResponseBody
    public String savemodifymodelffpin(@RequestParam("modlepincontext") String modlepincontext) {


        JSONObject modlejsonObject = JSONObject.parseObject(modlepincontext);
        JSONObject result = new JSONObject();
        /*****ff***/
        int modleid;
        String ff = null;
        ModlePin ffpin = null;
        String ffcomment = null;
        String ffpinid = null;
        String ffpinopcresurce;
        String pinName;
        int pinscope;
        try {
            modleid = modlejsonObject.getInteger("modleid");
            ff = modlejsonObject.getString("ff").trim();
            ffcomment = modlejsonObject.getString("ff" + "comment").trim();
            ffpinid = modlejsonObject.getString("ffpinid").trim();
            ffpinopcresurce = modlejsonObject.getString(ModlePin.TYPE_PIN_FF + "resource");
            pinName = modlejsonObject.getString("pinName");


            List<ModlePin> usemodlePins = modleDBServe.pinsbypintype(modleid, ModlePin.TYPE_PIN_FF);

            Matcher pvmatch = ffpattern.matcher(pinName);
            if (pvmatch.find()) {
                pinscope = Integer.parseInt(pvmatch.group(2));
//                for (ModlePin usepvpin : usemodlePins) {
//                    if (usepvpin.getModlePinName().equals(ModlePin.TYPE_PIN_FF + pinscope)) {
//                        throw new RuntimeException("引脚已经使用");
//                    }
//                }
            } else {
                throw new RuntimeException("引脚匹配错误");
            }


            ffpin = new ModlePin();
            ffpin.setReference_modleId(modleid);
            ffpin.setModlePinName(ModlePin.TYPE_PIN_FF + pinscope);
            ffpin.setPintype(ModlePin.TYPE_PIN_FF);
            ffpin.setResource(ffpinopcresurce);
            ffpin.setModleOpcTag(ff);
            ffpin.setOpcTagName(ffcomment);
            ffpin.setModlepinsId(ffpinid.equals("") ? -1 : Integer.parseInt(ffpinid));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /******up and down*****/

        String ffup = null;
        String ffdown = null;
        String ffupresource = null;
        String ffdownresource = null;//ffdwon4resource
        ModlePin ffuppin;
        ModlePin ffdownpin;
        String ffuppinid;
        String ffdownpinid;
        try {
            ffup = modlejsonObject.getString("ffup").trim();
            ffdown = modlejsonObject.getString("ffdown").trim();

            ffuppinid = modlejsonObject.getString("ffuppinid").trim();


            ffdownpinid = modlejsonObject.getString("ffdownpinid").trim();

            ffupresource = modlejsonObject.getString("ffup" + "resource");
            ffdownresource = modlejsonObject.getString("ffdown" + "resource");


            ffuppin = new ModlePin();
            ffuppin.setReference_modleId(modleid);
            ffuppin.setModlePinName(ModlePin.TYPE_PIN_FFUP + pinscope);
            ffuppin.setPintype(ModlePin.TYPE_PIN_FFUP);
            ffuppin.setResource(ffupresource);
            ffuppin.setModleOpcTag(ffup);
            ffuppin.setModlepinsId(ffuppinid.equals("") ? -1 : Integer.parseInt(ffuppinid));


            ffdownpin = new ModlePin();
            ffdownpin.setReference_modleId(modleid);
            ffdownpin.setModlePinName(ModlePin.TYPE_PIN_FFDOWN + pinscope);
            ffdownpin.setPintype(ModlePin.TYPE_PIN_FFDOWN);
            ffdownpin.setResource(ffdownresource);
            ffdownpin.setModleOpcTag(ffdown);
            ffdownpin.setModlepinsId(ffdownpinid.equals("") ? -1 : Integer.parseInt(ffdownpinid));

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /**滤波相关处理*/
        String filternameff = null;
        String filtercoefff;
        String filteropctagff;
        String filterffresource;
        String ffpinfilterid;
        Filter ffpinfilter = null;
        try {
            filtercoefff = modlejsonObject.getString("filtercoefff");
            filteropctagff = modlejsonObject.getString("filteropctagff");
            filternameff = modlejsonObject.getString("filternameff");
            filterffresource = modlejsonObject.getString("filterff" + "resource");
            ffpinfilterid = modlejsonObject.getString("ffpinfilterid");

            if (filternameff.equals("mvav")) {
                MoveAverageFilter moveAverageFilter = new MoveAverageFilter();
                moveAverageFilter.setBackToDCSTag(filteropctagff);
                moveAverageFilter.setOpcresource(filterffresource);
                moveAverageFilter.setCapacity(Double.valueOf(filtercoefff).intValue());//窗口时间长度
                moveAverageFilter.setFiltername(filternameff);
                moveAverageFilter.setPk_filterid(ffpinfilterid.equals("") ? -1 : Integer.valueOf(ffpinfilterid));
                ffpinfilter = moveAverageFilter;
            } else if (filternameff.equals("fodl")) {
                FirstOrderLagFilter firstOrderLagFilter = new FirstOrderLagFilter();
                firstOrderLagFilter.setBackToDCSTag(filteropctagff);
                firstOrderLagFilter.setOpcresource(filterffresource);
                firstOrderLagFilter.setFilter_alphe(Math.abs(Double.valueOf(filtercoefff)) > 1 ? 1 : Math.abs(Double.valueOf(filtercoefff)));
                firstOrderLagFilter.setFiltername(filternameff);
                firstOrderLagFilter.setPk_filterid(ffpinfilterid.equals("") ? -1 : Integer.valueOf(ffpinfilterid));
                ffpinfilter = firstOrderLagFilter;
            } else if (filternameff.equals("")) {
                ffpinfilter = new MoveAverageFilter();
                ffpinfilter.setPk_filterid(ffpinfilterid.equals("") ? -1 : Integer.valueOf(ffpinfilterid));
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        /********enable****/
        String ffenableid = null;
        String ffenable;
        String ffenableresource;
        ModlePin ffenablepin;

        try {
            ffenableid = modlejsonObject.getString("ffenableid");
            ffenable = modlejsonObject.getString("ffenable");
            ffenableresource = modlejsonObject.getString("ffenableresource");

            ffenablepin = new ModlePin();
            ffenablepin.setPintype(ModlePin.TYPE_PIN_PIN_FFENABLE);
            ffenablepin.setModlePinName(ModlePin.TYPE_PIN_PIN_FFENABLE + pinscope);
            ffenablepin.setModlepinsId(ffenableid.equals("") ? -1 : Integer.parseInt(ffenableid));
            ffenablepin.setReference_modleId(modleid);
            ffenablepin.setModleOpcTag(ffenable);
            ffenablepin.setResource(ffenableresource);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        try {
            if ((!ffpin.getModleOpcTag().equals("")) && (!ffuppin.getModleOpcTag().equals("")) && (!ffdownpin.getModleOpcTag().equals(""))) {
                modleDBServe.insertff(ffpin, ffuppin, ffdownpin, ffenablepin, ffpinfilter);
            } else {
                result.put("msg", "error");
                return result.toJSONString();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        result.put("msg", "success");
        return result.toJSONString();
    }


    @RequestMapping("/deletemodelpvpin")
    @ResponseBody
    public String deletemodelpvpin(@RequestParam("pinid") int pinid) {

        JSONObject result = new JSONObject();

        try {
            ModlePin pvpin = modleDBServe.findPinbypinid(pinid);

            ModlePin sppin = modleDBServe.findPinbypinmodleidAndpinname(pvpin.getReference_modleId(), ModlePin.TYPE_PIN_SP + Tool.getPinindex(pvpattern, pvpin));


            ModlePin pvpinenable = modleDBServe.findPinbypinmodleidAndpinname(pvpin.getReference_modleId(), ModlePin.TYPE_PIN_PIN_PVENABLE + Tool.getPinindex(pvpattern, pvpin));

            ModlePin puuppin = modleDBServe.findPinbypinmodleidAndpinname(pvpin.getReference_modleId(), ModlePin.TYPE_PIN_PVUP + Tool.getPinindex(pvpattern, pvpin));

            ModlePin pvdownpin = modleDBServe.findPinbypinmodleidAndpinname(pvpin.getReference_modleId(), ModlePin.TYPE_PIN_PVDOWN + Tool.getPinindex(pvpattern, pvpin));
            if (pvpin != null) {
                modleDBServe.deletePinAndComphone(Arrays.asList(pvpin, sppin, pvpinenable, puuppin, pvdownpin));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        result.put("msg", "success");
        return result.toJSONString();
    }


    @RequestMapping("/deletemodelmvpin")
    @ResponseBody
    public String deletemodelmvpin(@RequestParam("pinid") int pinid) {

        JSONObject result = new JSONObject();

        try {
            ModlePin mvpin = modleDBServe.findPinbypinid(pinid);

            ModlePin mvfbpin = modleDBServe.findPinbypinmodleidAndpinname(mvpin.getReference_modleId(), ModlePin.TYPE_PIN_MVFB + Tool.getPinindex(mvpattern, mvpin));


            ModlePin mvpinenable = modleDBServe.findPinbypinmodleidAndpinname(mvpin.getReference_modleId(), ModlePin.TYPE_PIN_PIN_MVENABLE + Tool.getPinindex(mvpattern, mvpin));

            ModlePin mvuppin = modleDBServe.findPinbypinmodleidAndpinname(mvpin.getReference_modleId(), ModlePin.TYPE_PIN_MVUP + Tool.getPinindex(mvpattern, mvpin));

            ModlePin mvdownpin = modleDBServe.findPinbypinmodleidAndpinname(mvpin.getReference_modleId(), ModlePin.TYPE_PIN_MVDOWN + Tool.getPinindex(mvpattern, mvpin));

            if (mvpin != null) {
                modleDBServe.deletePinAndComphone(Arrays.asList(mvpin, mvfbpin, mvpinenable, mvuppin, mvdownpin));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        result.put("msg", "success");
        return result.toJSONString();
    }

    @RequestMapping("/deletemodelffpin")
    @ResponseBody
    public String deletemodelffpin(@RequestParam("pinid") int pinid) {

        JSONObject result = new JSONObject();

        try {
            ModlePin ffpin = modleDBServe.findPinbypinid(pinid);

            ModlePin ffpinenable = modleDBServe.findPinbypinmodleidAndpinname(ffpin.getReference_modleId(), ModlePin.TYPE_PIN_PIN_FFENABLE + Tool.getPinindex(ffpattern, ffpin));

            ModlePin mvuppin = modleDBServe.findPinbypinmodleidAndpinname(ffpin.getReference_modleId(), ModlePin.TYPE_PIN_FFUP + Tool.getPinindex(ffpattern, ffpin));

            ModlePin mvdownpin = modleDBServe.findPinbypinmodleidAndpinname(ffpin.getReference_modleId(), ModlePin.TYPE_PIN_FFDOWN + Tool.getPinindex(ffpattern, ffpin));

            if (ffpin != null) {
                modleDBServe.deletePinAndComphone(Arrays.asList(ffpin, ffpinenable, mvuppin, mvdownpin));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        result.put("msg", "success");
        return result.toJSONString();
    }


    @RequestMapping("/deletemodelrespon")
    @ResponseBody
    public String deletemodelrespon(@RequestParam("responid") int responid) {

        JSONObject result = new JSONObject();
        try {
            modleDBServe.deleteModleRespbyresponid(responid);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        result.put("msg", "success");
        return result.toJSONString();
    }


    @RequestMapping("/savemodifyrespon")
    @ResponseBody
    public String savemodifyrespon(@RequestParam("responcontext") String responcontext) {

        JSONObject modlejsonObject = JSONObject.parseObject(responcontext);
        JSONObject result = new JSONObject();


        int modleid;
        String responid;
        String inputpinName;
        String outputpinName;
        float K;
        float T;
        float Tau;
        ResponTimeSerise respontimeserise;
        JSONObject jsonres;

        try {
            modleid = modlejsonObject.getInteger("modleid");
            responid = modlejsonObject.getString("responid").trim();
            inputpinName = modlejsonObject.getString("inputpinName").trim();
            outputpinName = modlejsonObject.getString("outputpinName").trim();
            K = modlejsonObject.getFloat("K");
            T = modlejsonObject.getFloat("T");
            Tau = modlejsonObject.getFloat("Tau");
            respontimeserise = new ResponTimeSerise();

            respontimeserise.setInputPins(inputpinName);
            respontimeserise.setOutputPins(outputpinName);
            respontimeserise.setRefrencemodleId(modleid);
            respontimeserise.setModletagId(responid.equals("") ? -1 : Integer.valueOf(responid));
            jsonres = new JSONObject();
            jsonres.put("k", K);
            jsonres.put("t", T);
            jsonres.put("tao", Tau);
            respontimeserise.setStepRespJson(jsonres.toJSONString());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        try {
            if (jsonres != null) {
                modleDBServe.insertOrUpdateTimeSerise(respontimeserise);
            } else {
                result.put("msg", "error");
                return result.toJSONString();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        result.put("msg", "success");
        return result.toJSONString();
    }


    @RequestMapping("/modifymodlestruct")
    public ModelAndView modifymodlestruct(@RequestParam("modleid") int modleid) {

        ModelAndView view = new ModelAndView();
        view.setViewName("contrlmodle/modifymodlestruct");
        view.addObject("modleid", modleid);
        return view;
    }

    @RequestMapping("/pagemodlestruct")
    @ResponseBody
    public String pagemodlestruct(@RequestParam("modleid") int modleid) {

        ControlModle controlModle = null;
        try {
            controlModle = modleDBServe.getModle(modleid);
        } catch (Exception e) {
            logger.error(e);
        }
        List<ModlePin> pvlist = new ArrayList<>();

        List<ModlePin> mvlist = new ArrayList<>();

        List<ModlePin> fflist = new ArrayList<>();

        if (controlModle != null) {
            for (ModlePin modlepin : controlModle.getModlePins()) {

                switch (modlepin.getPintype()) {

                    case ModlePin.TYPE_PIN_PV:
                        pvlist.add(modlepin);
                        break;
                    case ModlePin.TYPE_PIN_MV:
                        mvlist.add(modlepin);
                        break;
                    case ModlePin.TYPE_PIN_FF:
                        fflist.add(modlepin);
                        break;
                }
            }
        }


        JSONArray tabdata=new JSONArray();

        List<ModlePin> allpins = new ArrayList<>();
        allpins.addAll(pvlist);
        allpins.addAll(mvlist);
        allpins.addAll(fflist);


        for(ModlePin pin:allpins){
            JSONObject pinjsoncontext=new JSONObject();
            pinjsoncontext.put("pinName",pin.getModlePinName());
            pinjsoncontext.put("pinNote",pin.getOpcTagName());
            pinjsoncontext.put("pinStatus",pin.getReference_modleId()+"_"+pin.getModlepinsId()+"_"+pin.getPinEnable());
            tabdata.add(pinjsoncontext);
        }

        return Tool.sendLayuiPage(allpins.size(),tabdata).toJSONString();
    }



    @RequestMapping("/savemodlestruct")
    @ResponseBody
    public String savemodlestruct(@RequestParam("modleid") int modleid,@RequestParam("pinid") int pinid,@RequestParam("pinstatus") int onOroff) {

        JSONObject result = new JSONObject();
        try {
            modleDBServe.updatepinEnable(pinid, (onOroff == 0 ? 1 : 0));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        result.put("msg", "success");
        return result.toJSONString();
    }


}

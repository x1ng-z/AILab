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
    public String pagerespon(@RequestParam("modleid") int modleid, @RequestParam("page") int page, @RequestParam("pagesize") int pagesize){

        List<ResponTimeSerise> resps = modleDBServe.pageresponbymodleid(modleid, (page - 1) * pagesize, pagesize);

        int count = modleDBServe.countresponbymodleid(modleid);

        JSONArray datas = new JSONArray();

        for (ResponTimeSerise resp : resps) {
            JSONObject jsonresp=JSONObject.parseObject(resp.getStepRespJson());//{k:1,t:180,tao:1}
            JSONObject pincontext = new JSONObject();
            pincontext.put("modleid", resp.getRefrencemodleId());
            pincontext.put("responid", resp.getModletagId());
            pincontext.put("input", resp.getInputPins());
            pincontext.put("output", resp.getOutputPins());
            pincontext.put("K", jsonresp.getFloat("k"));
            pincontext.put("T", jsonresp.getFloat("t"));
            pincontext.put("Tau",jsonresp.getFloat("tao"));
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
        Filter ffpinfilter=null;
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

            ffenablepin=new ModlePin();
            ffenablepin.setPintype(ModlePin.TYPE_PIN_PIN_FFENABLE);
            ffenablepin.setModlePinName(ModlePin.TYPE_PIN_PIN_FFENABLE+pinscope);
            ffenablepin.setModlepinsId(ffenableid.equals("")?-1:Integer.parseInt(ffenableid));
            ffenablepin.setReference_modleId(modleid);
            ffenablepin.setModleOpcTag(ffenable);
            ffenablepin.setResource(ffenableresource);

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        try {
            if ((!ffpin.getModleOpcTag().equals("")) && (!ffuppin.getModleOpcTag().equals("")) && (!ffdownpin.getModleOpcTag().equals(""))) {
                modleDBServe.insertff(ffpin,ffuppin,ffdownpin,ffenablepin,ffpinfilter);
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
        Filter ffpinfilter=null;
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

            ffenablepin=new ModlePin();
            ffenablepin.setPintype(ModlePin.TYPE_PIN_PIN_FFENABLE);
            ffenablepin.setModlePinName(ModlePin.TYPE_PIN_PIN_FFENABLE+pinscope);
            ffenablepin.setModlepinsId(ffenableid.equals("")?-1:Integer.parseInt(ffenableid));
            ffenablepin.setReference_modleId(modleid);
            ffenablepin.setModleOpcTag(ffenable);
            ffenablepin.setResource(ffenableresource);

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            result.put("msg", "error");
            return result.toJSONString();
        }


        try {
            if ((!ffpin.getModleOpcTag().equals("")) && (!ffuppin.getModleOpcTag().equals("")) && (!ffdownpin.getModleOpcTag().equals(""))) {
                modleDBServe.insertff(ffpin,ffuppin,ffdownpin,ffenablepin,ffpinfilter);
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

    /**
     * pv切出
     */
    @RequestMapping("/modlepvcheckout/{modleid}/{pinid}/{onOroff}")
    @ResponseBody
    public String modelpvcheckout(@PathVariable("modleid") String modleid, @PathVariable("pinid") String pinid, @PathVariable("onOroff") String onOroff) {
        ControlModle controlModle = modleConstainer.getRunnableModulepool().get(Integer.valueOf(modleid.trim()));
        for (ModlePin pvpin : controlModle.getCategoryPVmodletag()) {
            if (pvpin.getModlepinsId() == Integer.valueOf(pinid)) {
                try {
                    /**如果当前是0,这要切入，如果当前是1,这要切除，*/
                    pvpin.setPinEnable(Integer.valueOf(onOroff) == 0 ? 1 : 0);
                    modleDBServe.updatepinEnable(pvpin.getModlepinsId(), Integer.valueOf(onOroff) == 0 ? 1 : 0);
                    /**重新build下*/
                    controlModle.getExecutePythonBridge().stop();
                    controlModle.getSimulatControlModle().getExecutePythonBridgeSimulate().stop();
                    controlModle.modleBuild(false);
                    if (controlModle.getModleEnable() == 1) {
                        controlModle.getExecutePythonBridge().execute();
                    }
                    JSONObject result = new JSONObject();
                    result.put("msg", "success");
                    return result.toJSONString();
                } catch (NumberFormatException e) {
                    logger.error(e.getMessage(), e);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }

        }

        JSONObject result = new JSONObject();
        result.put("msg", "error");
        return result.toJSONString();
    }


    @RequestMapping("/modleRealStatus/{modleId}")
    @ResponseBody
    public String modelRealStatusforweb(@NonNull @PathVariable("modleId") String modleid) {
        try {

            ControlModle controlModle = modleConstainer.getRunnableModulepool().get(Integer.valueOf(modleid.trim()));

            JSONObject result = new JSONObject();

            result.put("outSetp", controlModle.getControlAPCOutCycle());
            /****曲线***/


            result.put("funelUp", controlModle.getBackPVFunelUp());
            result.put("funelDwon", controlModle.getBackPVFunelDown());
            result.put("funneltype", controlModle.getFunneltype());
            result.put("predict", controlModle.getBackPVPrediction());
            int[] xaxis = new int[controlModle.getTimeserise_N()];
            for (int i = 0; i < controlModle.getTimeserise_N(); i++) {
                xaxis[i] = i;
            }
            result.put("xaxis", xaxis);

            String[] pvcurveNames = new String[controlModle.getNumOfRunnablePVPins_pp()];
            String[] funelUpcurveNames = new String[controlModle.getNumOfRunnablePVPins_pp()];
            String[] funelDowncurveNames = new String[controlModle.getNumOfRunnablePVPins_pp()];

            int indexEnablepv = 0;
            for (int pvindex = 0; pvindex < controlModle.getCategoryPVmodletag().size(); pvindex++) {
                if (controlModle.getMaskisRunnablePVMatrix()[pvindex] == 0) {
                    continue;
                }

                pvcurveNames[indexEnablepv] = controlModle.getCategoryPVmodletag().get(pvindex).getModlePinName();
                funelUpcurveNames[indexEnablepv] = "funelUp";
                funelDowncurveNames[indexEnablepv] = "funelDown";
                indexEnablepv++;
            }
            result.put("curveNames4funelUp", funelUpcurveNames);
            result.put("curveNames4pv", pvcurveNames);
            result.put("curveNames4funelDown", funelDowncurveNames);


            /**表格内容*/

//        double[] pvs = new double[controlModle.getCategoryPVmodletag().size()];
//        for (ModlePin pvpin : controlModle.getCategoryPVmodletag()) {
//
//            pvs[loop] = pvpin.modleGetReal();
//            loop++;
//        }
//        result.put("pv", pvs);

            int pvnum = controlModle.getCategoryPVmodletag().size();//2
            int mvnum = controlModle.getCategoryMVmodletag().size();//1
            int maxrownum = Math.max(pvnum, mvnum);

            JSONArray modlereadData = new JSONArray();
            JSONArray sdmvData = new JSONArray();
            JSONArray ffData = new JSONArray();

            int indexEnableMV = 0;
            int indexEnablePV = 0;
            for (int loop = 0; loop < maxrownum; loop++) {
                JSONObject modlereadDatarowcontext = new JSONObject();


                String mainrowpinname = "";
                if (loop < pvnum) {//pv
                    ModlePin pv = controlModle.getCategoryPVmodletag().get(loop);
                    ModlePin sp = controlModle.getCategorySPmodletag().get(loop);

                    //                rowcontext.put("pvName", pv.getModleOpcTag());
                    modlereadDatarowcontext.put("pvValue", Tool.getSpecalScale(3, pv.modleGetReal()));
                    modlereadDatarowcontext.put("spValue", Tool.getSpecalScale(3, sp.modleGetReal()));
                    if (controlModle.getMaskisRunnablePVMatrix()[loop] == 1) {
                        modlereadDatarowcontext.put("e", Tool.getSpecalScale(3, controlModle.getBackPVPredictionError()[indexEnablePV]));
                        ++indexEnablePV;
                    }
                    modlereadDatarowcontext.put("shockpv", pv.getShockDetector() == null ? "" : Tool.getSpecalScale(3, pv.getShockDetector().getLowhzA()));

                    modlereadDatarowcontext.put("checkIO", pv.getReference_modleId() + "_" + pv.getModlepinsId() + "_" + pv.getPinEnable());


                    mainrowpinname += pv.getModlePinName();
                }


                if (loop < mvnum) {//1,1,mv
                    ModlePin mv = controlModle.getCategoryMVmodletag().get(loop);
                    ModlePin mvDownLmt = mv.getDownLmt();
                    ModlePin mvUpLmt = mv.getUpLmt();
                    ModlePin mvFeedBack = mv.getFeedBack();
                    modlereadDatarowcontext.put("mvvalue", Tool.getSpecalScale(3, mv.modleGetReal()));
                    modlereadDatarowcontext.put("mvDownLmt", Tool.getSpecalScale(3, mvDownLmt.modleGetReal()));
                    modlereadDatarowcontext.put("mvUpLmt", Tool.getSpecalScale(3, mvUpLmt.modleGetReal()));
                    modlereadDatarowcontext.put("mvFeedBack", Tool.getSpecalScale(3, mvFeedBack.modleGetReal()));
                    if (controlModle.getMaskisRunnableMVMatrix()[loop] == 1) {
                        modlereadDatarowcontext.put("dmv", Tool.getSpecalScale(3, controlModle.getBackrawDmv()[indexEnableMV]));
                        //                    modlereadDatarowcontext.put("shockmv", mv.getShockDetector() == null ? "" : Tool.getSpecalScale(3, mv.getShockDetector().getLowhzA()));
                        ++indexEnableMV;
                    }
                    modlereadDatarowcontext.put("shockmv", mv.getShockDetector() == null ? "" : Tool.getSpecalScale(3, mv.getShockDetector().getLowhzA()));

                    mainrowpinname += (mainrowpinname.equals("") ? mv.getModlePinName() : "|" + mv.getModlePinName());
                }

                modlereadDatarowcontext.put("pinName", mainrowpinname);
                //            mainmodlerowcontext.put("auto", controlModle.getAutoEnbalePin() == null ? "手动" : (controlModle.getAutoEnbalePin().modleGetReal() == 0 ? "手动" : "自动"));
                modlereadData.add(modlereadDatarowcontext);

            }


            /***仿真数据，ff数据展示*/
            indexEnablePV = 0;
            for (int indexpv = 0; indexpv < controlModle.getCategoryPVmodletag().size(); ++indexpv) {

                if (controlModle.getMaskisRunnablePVMatrix()[indexpv] == 0) {
                    continue;
                }

                JSONObject sdmvrowcontext = new JSONObject();
                JSONObject ffrowcontext = new JSONObject();


                sdmvrowcontext.put("pinName", controlModle.getCategoryPVmodletag().get(indexpv).getModlePinName());
                ffrowcontext.put("pinName", controlModle.getCategoryPVmodletag().get(indexpv).getModlePinName());


                indexEnableMV = 0;
                /**仿真dmv*/
                for (int indexmv = 0; indexmv < controlModle.getCategoryMVmodletag().size(); ++indexmv) {

                    if (controlModle.getMaskisRunnableMVMatrix()[indexmv] == 0) {
                        continue;
                    }

                    if (controlModle.getMaskMatrixRunnablePVUseMV()[indexEnablePV][indexEnableMV] == 1) {
                        sdmvrowcontext.put(controlModle.getCategoryMVmodletag().get(indexmv).getModlePinName(), Tool.getSpecalScale(3, controlModle.getSimulatControlModle().getBackSimulateDmv()[indexEnablePV][indexEnableMV]));
                    }
                    ++indexEnableMV;
                }

                int indexEnableFF = 0;
                /**ff*/
                if (controlModle.getBasefeedforwardpoints_v() > 0) {
                    for (int indexff = 0; indexff < controlModle.getBasefeedforwardpoints_v(); ++indexff) {

                        if (controlModle.getMaskisRunnableFFMatrix()[indexff] == 0) {
                            continue;
                        }

                        if (controlModle.getMaskMatrixRunnablePVUseFF()[indexEnablePV][indexEnableFF] == 1) {
                            /**dff*/
                            ffrowcontext.put("d" + controlModle.getCategoryFFmodletag().get(indexff).getModlePinName(), Tool.getSpecalScale(3, controlModle.getBackDff()[indexEnablePV][indexEnableFF]));
                            /**ff值*/
                            ffrowcontext.put(controlModle.getCategoryFFmodletag().get(indexff).getModlePinName(), Tool.getSpecalScale(3, controlModle.getCategoryFFmodletag().get(indexff).modleGetReal()));
                        }

                        ++indexEnableFF;
                    }
                }

                if (!sdmvrowcontext.equals("")) {
                    sdmvData.add(sdmvrowcontext);
                }

                ffData.add(ffrowcontext);
                ++indexEnablePV;
            }


            result.put("modleRealData", modlereadData);
            result.put("modlestatus", controlModle.getAutoEnbalePin() != null ? controlModle.getAutoEnbalePin().modleGetReal() : 1);
            result.put("sdmvData", sdmvData);

            if (controlModle.getBasefeedforwardpoints_v() > 0) {
                result.put("ffData", ffData);
            }


            return result.toJSONString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "";

    }


    @RequestMapping("/stopModle")
    @ResponseBody
    public String stopModel(@RequestParam("modleid") String modleid) {

        ControlModle controlModle = modleConstainer.getRunnableModulepool().get(Integer.valueOf(modleid.trim()));
        if (controlModle != null) {
            if (controlModle.getModleEnable() == 1) {
                controlModle.getSimulatControlModle().generateSimulatevalidkey();
                /**停止仿真运行*/
                controlModle.getSimulatControlModle().setIssimulation(false);
                controlModle.getSimulatControlModle().getExecutePythonBridgeSimulate().stop();


                controlModle.generateValidkey();
                /**停止模型*/
                controlModle.setModleEnable(0);
                controlModle.getExecutePythonBridge().stop();
                modleDBServe.modifymodleEnable(controlModle.getModleId(), 0);

                return "success";
            }
        } else {
            return "error";
        }
//        ModelAndView mv=new ModelAndView();
//        mv.setViewName("redirect:/login/index.do");
        return "error";
//        return mv;
    }


    @RequestMapping("/stopSimulateModle")
    @ResponseBody
    public String stopSimulateModel(@RequestParam("modleid") String modleid) {
        ControlModle controlModle = modleConstainer.getRunnableModulepool().get(Integer.valueOf(modleid.trim()));
        if (controlModle != null) {
            if (controlModle.getSimulatControlModle().isIssimulation()) {
                /**停止仿真运行*/
                controlModle.modleCheckStatusStop();
                controlModle.getSimulatControlModle().simulateModleStop();
                return "success";
            }
        } else {
            return "error";
        }
        return "error";
    }


    @RequestMapping("/runModle")
    @ResponseBody
    public String runModel(@RequestParam("modleid") String modleid) {

        ControlModle controlModle = modleConstainer.getRunnableModulepool().get(Integer.valueOf(modleid.trim()));
        if (controlModle != null) {
            if (controlModle.getModleEnable() == 0) {
                controlModle.setModleEnable(1);
                controlModle.getExecutePythonBridge().execute();
                modleDBServe.modifymodleEnable(controlModle.getModleId(), 1);
                return "success";
            }
        } else {
            return "error";
        }
//        ModelAndView mv=new ModelAndView();
//        mv.setViewName("redirect:/login/index.do");
        return "error";
//        return "/modle/modlestatus.do";
    }


    @RequestMapping("/runSimulateModle")
    @ResponseBody
    public String runSimulateModel(@RequestParam("modleid") String modleid) {
        ControlModle controlModle = modleConstainer.getRunnableModulepool().get(Integer.valueOf(modleid.trim()));
        if (controlModle != null) {
            if (!controlModle.getSimulatControlModle().isIssimulation()) {
                controlModle.getSimulatControlModle().simulateModleRun();
                return "success";
            }
        } else {
            return "error";
        }
        return "error";
    }


    @RequestMapping("/modifymodle")
    public ModelAndView modifyModel(@RequestParam("modleid") int modleid) {

        ControlModle controlModle = modleConstainer.getRunnableModulepool().get(modleid);

        ModelAndView mv = new ModelAndView();
        mv.setViewName("modifyModle");
        JSONArray mvrespon = new JSONArray();
        JSONArray ffrespon = new JSONArray();

        List<String> pvlist = new ArrayList<>();
        List<ModlePin> pvpinlist = new ArrayList<>();
        List<ShockDetector> shockDetectorPVlist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getPv(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_PV + i);
            if (modlePin == null) {
                pvlist.add("");
                pvpinlist.add(new ModlePin());
                shockDetectorPVlist.add(new ShockDetector());
            } else {
                pvlist.add(modlePin.getModleOpcTag());
                pvpinlist.add(modlePin);
                shockDetectorPVlist.add(modlePin.getShockDetector() == null ? new ShockDetector() : modlePin.getShockDetector());
            }
        }

        List<String> qlist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getPv(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_PV + i);
            if (modlePin == null) {
                qlist.add("");
            } else {
                qlist.add(modlePin.getQ() + "");
            }
        }

        List<String> alphelist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getPv(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_PV + i);
            if (modlePin == null) {
                alphelist.add("");
            } else {
                alphelist.add(modlePin.getReferTrajectoryCoef() + "");
            }
        }


        List<String> splist = new ArrayList<>();
        List<ModlePin> sppinlist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getPv(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_SP + i);

            if (modlePin == null) {
                splist.add("");
                sppinlist.add(new ModlePin());
            } else {
                splist.add(modlePin.getModleOpcTag());
                sppinlist.add(modlePin);
            }

        }


        List<String> mvlist = new ArrayList<>();
        List<ModlePin> mvpinlist = new ArrayList<>();
        List<ShockDetector> shockDetectorMVlist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getMv(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_MV + i);

            if (modlePin == null) {
                mvlist.add("");
                mvpinlist.add(new ModlePin());
                shockDetectorMVlist.add(new ShockDetector());
            } else {
                mvlist.add(modlePin.getModleOpcTag());
                mvpinlist.add(modlePin);
                shockDetectorMVlist.add(modlePin.getShockDetector() == null ? new ShockDetector() : modlePin.getShockDetector());
            }

        }


        List<String> rlist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getMv(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_MV + i);
            if (modlePin == null) {
                rlist.add("");
            } else {
                rlist.add(modlePin.getR() + "");
            }

        }


        List<String> dmvHighlist = new ArrayList<>();
        List<String> dmvLowlist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getMv(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_MV + i);
            if (modlePin == null) {
                dmvHighlist.add("");
                dmvLowlist.add("");
            } else {
                dmvHighlist.add(modlePin.getDmvHigh() + "");
                dmvLowlist.add(modlePin.getDmvLow() + "");
            }

        }


        List<ModlePin> mvuplist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getMv(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_MVUP + i);

            if (modlePin == null) {
                mvuplist.add(new ModlePin());
            } else {
                mvuplist.add(modlePin);
            }

        }


        List<Filter> filterpvlist = new ArrayList<>();

        for (int i = 1; i <= baseConf.getPv(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_PV + i);

            if (modlePin == null) {
                filterpvlist.add(new MoveAverageFilter());//这里仅仅是用于填充也页面数据，不是说没有滤波器的默认就是move

            } else {
                filterpvlist.add(modlePin.getFilter() == null ? new MoveAverageFilter() : modlePin.getFilter());

            }

        }


        List<Filter> filtermvfblist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getMv(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_MVFB + i);
            if (modlePin == null) {
                filtermvfblist.add(new MoveAverageFilter());//这里仅仅是用于填充也页面数据，不是说没有滤波器的默认就是move
            } else {
                filtermvfblist.add(modlePin.getFilter() == null ? new MoveAverageFilter() : modlePin.getFilter());
            }

        }


        List<Filter> filterfflist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getMv(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_FF + i);
            if (modlePin == null) {
                filterfflist.add(new MoveAverageFilter());//这里仅仅是用于填充也页面数据，不是说没有滤波器的默认就是move
            } else {
                filterfflist.add(modlePin.getFilter() == null ? new MoveAverageFilter() : modlePin.getFilter());
            }

        }


        List<ModlePin> mvdownlist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getMv(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_MVDOWN + i);

            if (modlePin == null) {
                mvdownlist.add(new ModlePin());
            } else {
                mvdownlist.add(modlePin);
            }

        }


        List<String> mvfblist = new ArrayList<>();
        List<ModlePin> mvfbpinlist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getMv(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_MVFB + i);

            if (modlePin == null) {
                mvfblist.add("");
                mvfbpinlist.add(new ModlePin());
            } else {
                mvfblist.add(modlePin.getModleOpcTag());
                mvfbpinlist.add(modlePin);
            }

        }

        List<String> fflist = new ArrayList<>();
        List<ModlePin> ffpinlist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getFf(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_FF + i);

            if (modlePin == null) {
                fflist.add("");
                ffpinlist.add(new ModlePin());
            } else {
                fflist.add(modlePin.getModleOpcTag());
                ffpinlist.add(modlePin);
            }

        }


        /**前馈上限*/
        List<ModlePin> ffuplist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getFf(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_FFUP + i);

            if (modlePin == null) {
                ffuplist.add(new ModlePin());
            } else {
                ffuplist.add(modlePin);
            }

        }

        /**前馈下限*/
        List<ModlePin> ffdownlist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getFf(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_FFDOWN + i);

            if (modlePin == null) {
                ffdownlist.add(new ModlePin());
            } else {
                ffdownlist.add(modlePin);
            }

        }

        //A

        for (int i = 1; i <= baseConf.getMv(); ++i) {

            JSONObject mvjson = new JSONObject();
            mvjson.put(ModlePin.TYPE_PIN_MV, ModlePin.TYPE_PIN_MV + i);
            for (int j = 1; j <= baseConf.getPv(); ++j) {

                boolean isfind = false;
                for (ResponTimeSerise responTimeSerise : controlModle.getResponTimeSerises()) {
                    if (
                            responTimeSerise.getInputPins().equals(ModlePin.TYPE_PIN_MV + i)
                                    &&
                                    responTimeSerise.getOutputPins().equals(ModlePin.TYPE_PIN_PV + j)
                    ) {
                        mvjson.put(ModlePin.TYPE_PIN_PV + j, responTimeSerise.getStepRespJson());
                        isfind = true;
                    }
                }
                if (!isfind) {
                    mvjson.put(ModlePin.TYPE_PIN_PV + j, "");
                }

            }

            mvrespon.add(mvjson);

        }


        //B

        for (int i = 1; i <= baseConf.getFf(); ++i) {

            JSONObject ffjson = new JSONObject();
            ffjson.put(ModlePin.TYPE_PIN_FF, ModlePin.TYPE_PIN_FF + i);
            for (int j = 1; j <= baseConf.getPv(); ++j) {

                boolean isfind = false;
                for (ResponTimeSerise responTimeSerise : controlModle.getResponTimeSerises()) {
                    if (
                            responTimeSerise.getInputPins().equals(ModlePin.TYPE_PIN_FF + i)
                                    &&
                                    responTimeSerise.getOutputPins().equals(ModlePin.TYPE_PIN_PV + j)
                    ) {
                        ffjson.put(ModlePin.TYPE_PIN_PV + j, responTimeSerise.getStepRespJson());
                        isfind = true;
                    }
                }
                if (!isfind) {
                    ffjson.put(ModlePin.TYPE_PIN_PV + j, "");
                }

            }
            ffrespon.add(ffjson);
        }


        //deadZone

        List<String> deadZnoelist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getPv(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_PV + i);
            if (modlePin == null) {
                deadZnoelist.add("");
            } else {
                deadZnoelist.add(modlePin.getDeadZone() + "");
            }
        }


        //funelInitvalue
        List<String> funelInitValuelist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getPv(); ++i) {
            ModlePin modlePin = controlModle.getStringmodlePinsMap().get(ModlePin.TYPE_PIN_PV + i);
            if (modlePin == null) {
                funelInitValuelist.add("");
            } else {
                funelInitValuelist.add(modlePin.getFunelinitValue() + "");
            }
        }


        mv.addObject("modle", controlModle);
        mv.addObject("pvlist", pvlist);
        mv.addObject("pvpinlist", pvpinlist);
        mv.addObject("shockDetectorPVlist", shockDetectorPVlist);
        mv.addObject("qlist", qlist);
        mv.addObject("alphelist", alphelist);


        mv.addObject("splist", splist);
        mv.addObject("sppinlist", sppinlist);
        mv.addObject("mvlist", mvlist);
        mv.addObject("mvpinlist", mvpinlist);
        mv.addObject("shockDetectorMVlist", shockDetectorMVlist);
        mv.addObject("rlist", rlist);
        mv.addObject("dmvHighlist", dmvHighlist);
        mv.addObject("dmvLowlist", dmvLowlist);

        mv.addObject("mvuplist", mvuplist);
        mv.addObject("mvdownlist", mvdownlist);
        mv.addObject("mvfblist", mvfblist);
        mv.addObject("mvfbpinlist", mvfbpinlist);

        mv.addObject("fflist", fflist);
        mv.addObject("ffpinlist", ffpinlist);
        mv.addObject("ffuplist", ffuplist);
        mv.addObject("ffdownlist", ffdownlist);

        mv.addObject("mvresp", mvrespon.toJSONString());
        mv.addObject("ffresp", ffrespon.toJSONString());

        mv.addObject("pvDeadZones", deadZnoelist);
        mv.addObject("pvFunelInitValues", funelInitValuelist);

        mv.addObject("filterpvlist", filterpvlist);

        mv.addObject("filtermvfblist", filtermvfblist);
        mv.addObject("filterfflist", filterfflist);

        /**opc点号来源*/
        List<String> opcresources = new ArrayList<>();
        for (OPCService opcService : opcServicConstainer.getOpcservepool().values()) {
            opcresources.add("opc" + opcService.getOpcip());
        }
        opcresources.add("constant");
        mv.addObject("opcresources", opcresources);
        return mv;
    }


    @RequestMapping("/savemodle")
    @ResponseBody
    public String saveModel(@RequestParam("modle") String modle, @RequestParam("mvresp") String mvresp, @RequestParam("ffresp") String ffresp) {//
        try {
            boolean newmodle = false;
            JSONObject modlejsonObject = JSON.parseObject(modle);

            ControlModle fleshcontrolModle = new ControlModle();

            fleshcontrolModle.setModleName(modlejsonObject.getString("modleName"));
            fleshcontrolModle.setPredicttime_P(Integer.valueOf(modlejsonObject.getString("P")));
            fleshcontrolModle.setControltime_M(Integer.valueOf(modlejsonObject.getString("M")));
            fleshcontrolModle.setTimeserise_N(Integer.valueOf(modlejsonObject.getString("N")));
            fleshcontrolModle.setControlAPCOutCycle(Integer.valueOf(modlejsonObject.getString("O")));
            /**
             *记录历史的pv引脚是否使能
             * key=引脚名称 pv1 pv2
             * */
            Map<String, Integer> historyPinsEnable = new HashMap<>();
            if (modlejsonObject.getString("modleid").trim().equals("")) {
                modleDBServe.insertModle(fleshcontrolModle);
                newmodle = true;

            } else {
                modleDBServe.modifymodle(Integer.valueOf(modlejsonObject.getString("modleid").trim()), fleshcontrolModle);
                fleshcontrolModle.setModleId(Integer.valueOf(modlejsonObject.getString("modleid").trim()));
            }

            fleshcontrolModle.setModlePins(new ArrayList<ModlePin>());

            /**
             *
             * auto*/
            ModlePin autopin = new ModlePin();
            autopin.setResource(modlejsonObject.getString("autoresource"));
            autopin.setModleOpcTag(modlejsonObject.getString("autoTag"));
            if ((autopin.getResource() != null) && (!autopin.getResource().equals("")) && (autopin.getModleOpcTag() != null) && (!autopin.getModleOpcTag().equals(""))) {
                autopin.setReference_modleId(fleshcontrolModle.getModleId());
                autopin.setModlePinName(ModlePin.TYPE_PIN_MODLE_AUTO);
                autopin.setPintype(ModlePin.TYPE_PIN_MODLE_AUTO);
                fleshcontrolModle.getModlePins().add(autopin);
            }


            /**
             * pv*/
            for (int i = 1; i <= baseConf.getPv(); i++) {

                String pvTag = modlejsonObject.getString(ModlePin.TYPE_PIN_PV + i).trim();
                String pvcomment = modlejsonObject.getString(ModlePin.TYPE_PIN_PV + i + "comment").trim();
                String QTag = modlejsonObject.getString("q" + i);
                String spTag = modlejsonObject.getString(ModlePin.TYPE_PIN_SP + i);
                String spcomment = modlejsonObject.getString(ModlePin.TYPE_PIN_SP + i + "comment").trim();
                String spDeadZone = modlejsonObject.getString("pv" + i + "DeadZone");
                String spFunelInitValue = modlejsonObject.getString("pv" + i + "FunelInitValue");
                String spfuneltype = modlejsonObject.getString("funneltype" + i);
                String pvtracoef = modlejsonObject.getString("tracoef" + i);//参考轨迹系数

                String pvenable = modlejsonObject.getString(ModlePin.TYPE_PIN_PIN_PVENABLE + i);
                String pvenableresource = modlejsonObject.getString(ModlePin.TYPE_PIN_PIN_PVENABLE + i + "resource");

                /**
                 * pv震荡检测属性
                 * */
                String detectwindowstimepv = modlejsonObject.getString("detectwindowstimepv" + i);//震荡检测检测窗口时间
                String detectdampcoepv = modlejsonObject.getString("detectdampcoepv" + i);//震荡检测检测阻尼
                String detectfiltercoepv = modlejsonObject.getString("detectfiltercoepv" + i);//震荡检测滤波系数
                String detectfilteroutopctagpv = modlejsonObject.getString("detectfilteroutopctagpv" + i);//震荡检测检测阻尼
                String detectfilteroutopctagpvresource = modlejsonObject.getString("detectfilteroutopctagpv" + i + "resource");//震荡检测检测阻尼
                String detectamplitudeoutopctagpv = modlejsonObject.getString("detectamplitudeoutopctagpv" + i);//震荡检测检测阻尼
                String detectamplitudeoutopctagpvresource = modlejsonObject.getString("detectamplitudeoutopctagpv" + i + "resource");//震荡检测检测阻尼

                /**
                 * 滤波相关处理
                 * */
                String filtercoefpv = modlejsonObject.getString("filtercoefpv" + i);
                String filteropctagpv = modlejsonObject.getString("filteropctagpv" + i);
                String filternamepv = modlejsonObject.getString("filternamepv" + i);

                String filterpvres = modlejsonObject.getString("filterpv" + i + "resource");

                //pvTag!=null&&pvTag.trim()!=""
                if ((pvTag != null && !pvTag.trim().equals("")) && (QTag != null && !QTag.trim().equals("")) && (spTag != null && !spTag.trim().equals("")) && (spDeadZone != null && !spDeadZone.trim().equals("")) && (spFunelInitValue != null && !spFunelInitValue.trim().equals(""))) {


                    ModlePin pvpin = new ModlePin();
                    pvpin.setResource(modlejsonObject.getString(ModlePin.TYPE_PIN_PV + i + "resource"));
                    pvpin.setModleOpcTag(pvTag);
                    pvpin.setQ(Double.valueOf(QTag));
                    pvpin.setReference_modleId(fleshcontrolModle.getModleId());
                    pvpin.setModlePinName("pv" + i);
                    pvpin.setPintype(ModlePin.TYPE_PIN_PV);
                    pvpin.setDeadZone(Double.valueOf(spDeadZone));
                    pvpin.setFunelinitValue(Double.valueOf(spFunelInitValue));
                    pvpin.setFunneltype(spfuneltype);
                    pvpin.setOpcTagName(pvcomment);


                    /**
                     * 滤波器
                     * */
                    if (filternamepv != null) {
                        if (filternamepv.equals("mvav")) {
                            MoveAverageFilter moveAverageFilter = new MoveAverageFilter();
                            moveAverageFilter.setBackToDCSTag(filteropctagpv);
                            moveAverageFilter.setOpcresource(filterpvres);
                            moveAverageFilter.setCapacity(Double.valueOf(filtercoefpv).intValue());//窗口时间长度
                            moveAverageFilter.setFiltername(filternamepv);
                            pvpin.setFilter(moveAverageFilter);
                        } else if (filternamepv.equals("fodl")) {
                            FirstOrderLagFilter firstOrderLagFilter = new FirstOrderLagFilter();
                            firstOrderLagFilter.setBackToDCSTag(filteropctagpv);
                            firstOrderLagFilter.setOpcresource(filterpvres);
                            firstOrderLagFilter.setFilter_alphe(Math.abs(Double.valueOf(filtercoefpv)) > 1 ? 1 : Math.abs(Double.valueOf(filtercoefpv)));
                            firstOrderLagFilter.setFiltername(filternamepv);
                            pvpin.setFilter(firstOrderLagFilter);
                        }
                    }

                    /**
                     *震荡检测
                     * */
                    if ((detectwindowstimepv != null) && (!detectwindowstimepv.trim().equals("")) && (detectdampcoepv != null) && (!detectdampcoepv.trim().equals("")) && (detectfiltercoepv != null) && (!detectfiltercoepv.trim().equals(""))) {
                        ShockDetector shockDetector = new ShockDetector();
                        shockDetector.setEnable(1);
                        shockDetector.setWindowstime(Integer.valueOf(detectwindowstimepv));
                        shockDetector.setDampcoeff(Double.valueOf(detectdampcoepv));
                        shockDetector.setFiltercoeff(Math.abs(Double.valueOf(detectfiltercoepv)) > 1 ? 1 : Math.abs(Double.valueOf(detectfiltercoepv)));
                        shockDetector.setBackToDCSTag(detectamplitudeoutopctagpv);
                        shockDetector.setOpcresource(detectamplitudeoutopctagpvresource);
                        shockDetector.setFilterbacktodcstag(detectfilteroutopctagpv);
                        shockDetector.setFilteropcresource(detectfilteroutopctagpvresource);
                        pvpin.setShockDetector(shockDetector);
                    }


                    fleshcontrolModle.getModlePins().add(pvpin);
                    if (pvtracoef != null) {
                        pvpin.setReferTrajectoryCoef(Double.valueOf(pvtracoef.trim()));
                    } else {
                        pvpin.setReferTrajectoryCoef(0.7);//默认值0.35
                    }

                    ModlePin sppin = new ModlePin();
                    sppin.setModleOpcTag(spTag);
                    sppin.setResource(modlejsonObject.getString(ModlePin.TYPE_PIN_SP + i + "resource"));
                    sppin.setReference_modleId(fleshcontrolModle.getModleId());
                    sppin.setModlePinName("sp" + i);
                    sppin.setPintype(ModlePin.TYPE_PIN_SP);
                    sppin.setOpcTagName(spcomment);
                    fleshcontrolModle.getModlePins().add(sppin);

                    if (pvenableresource != null && !pvenableresource.equals("") && pvenable != null & !pvenable.equals("")) {
                        ModlePin pvenablepin = new ModlePin();
                        pvenablepin.setModleOpcTag(pvenable);
                        pvenablepin.setResource(pvenableresource);
                        pvenablepin.setReference_modleId(fleshcontrolModle.getModleId());
                        pvenablepin.setModlePinName(ModlePin.TYPE_PIN_PIN_PVENABLE + i);
                        pvenablepin.setPintype(ModlePin.TYPE_PIN_PIN_PVENABLE);
                        fleshcontrolModle.getModlePins().add(pvenablepin);
//                        pvpin.setDcsEnabePin(pvenablepin);在build中会进行关系建立
                    }


                }
            }


            for (int i = 1; i <= baseConf.getMv(); i++) {
                String mvopctag = modlejsonObject.getString("mv" + i).trim();
                String mvcomment = modlejsonObject.getString("mv" + i + "comment").trim();
                String r = modlejsonObject.getString("r" + i).trim();
                String mvfb = modlejsonObject.getString("mvfb" + i).trim();
                String mvfbcomment = modlejsonObject.getString("mvfb" + i + "comment").trim();
                String mvup = modlejsonObject.getString("mvup" + i).trim();
                String mvdown = modlejsonObject.getString("mvdown" + i).trim();


                String dmvhigh = modlejsonObject.getString("dmv" + i + "High");
                String dmvlow = modlejsonObject.getString("dmv" + i + "Low");

                String mvupresoure = modlejsonObject.getString("mvup" + i + "resource");
                String mvdownresource = modlejsonObject.getString("mvdown" + i + "resource");

                //滤波器设置

                /**
                 * 滤波相关处理
                 * */
                String filtercoefmv = modlejsonObject.getString("filtercoefmv" + i);
                String filteropctagmv = modlejsonObject.getString("filteropctagmv" + i);
                String filternamemv = modlejsonObject.getString("filternamemv" + i);


                /**
                 * mv震荡检测属性
                 * */
                String detectwindowstimemv = modlejsonObject.getString("detectwindowstimemv" + i);//震荡检测检测窗口时间
                String detectdampcoemv = modlejsonObject.getString("detectdampcoemv" + i);//震荡检测检测阻尼
                String detectfiltercoemv = modlejsonObject.getString("detectfiltercoemv" + i);//震荡检测滤波系数
                String detectfilteroutopctagmv = modlejsonObject.getString("detectfilteroutopctagmv" + i);//震荡检测检测阻尼
                String detectfilteroutopctagmvresource = modlejsonObject.getString("detectfilteroutopctagmv" + i + "resource");//震荡检测检测阻尼
                String detectamplitudeoutopctagmv = modlejsonObject.getString("detectamplitudeoutopctagmv" + i);//震荡检测检测阻尼
                String detectamplitudeoutopctagmvresource = modlejsonObject.getString("detectamplitudeoutopctagmv" + i + "resource");//震荡检测检测阻尼


                if (mvopctag != null && !mvopctag.trim().equals("")) {
                    ModlePin mvpin = new ModlePin();
                    mvpin.setR(Double.valueOf(r));
                    mvpin.setResource(modlejsonObject.getString(ModlePin.TYPE_PIN_MV + i + "resource"));
                    mvpin.setReference_modleId(fleshcontrolModle.getModleId());
                    mvpin.setModleOpcTag(mvopctag);
                    mvpin.setModlePinName("mv" + i);
                    mvpin.setPintype(ModlePin.TYPE_PIN_MV);
                    mvpin.setDmvHigh(Double.valueOf(dmvhigh));
                    mvpin.setDmvLow(Double.valueOf(dmvlow));
                    mvpin.setOpcTagName(mvcomment);
                    fleshcontrolModle.getModlePins().add(mvpin);


                    ModlePin mvfbpin = new ModlePin();
                    mvfbpin.setReference_modleId(fleshcontrolModle.getModleId());
                    mvfbpin.setModlePinName("mvfb" + i);
                    mvfbpin.setPintype(ModlePin.TYPE_PIN_MVFB);
                    mvfbpin.setResource(modlejsonObject.getString(ModlePin.TYPE_PIN_MVFB + i + "resource"));
                    mvfbpin.setModleOpcTag(mvfb);
                    mvfbpin.setOpcTagName(mvfbcomment);

                    /**
                     * mv震荡检测设置
                     * */

                    if ((detectwindowstimemv != null) && (!detectwindowstimemv.trim().equals("")) && (detectdampcoemv != null) && (!detectdampcoemv.trim().equals("")) && (detectfiltercoemv != null) && (!detectfiltercoemv.trim().equals(""))) {
                        ShockDetector shockDetector = new ShockDetector();
                        shockDetector.setEnable(1);
                        shockDetector.setWindowstime(Math.abs(Integer.valueOf(detectwindowstimemv)));
                        shockDetector.setDampcoeff(Double.valueOf(detectdampcoemv));
                        shockDetector.setFiltercoeff(Math.abs(Double.valueOf(detectfiltercoemv)) > 1 ? 1 : Math.abs(Double.valueOf(detectfiltercoemv)));
                        shockDetector.setBackToDCSTag(detectamplitudeoutopctagmv);
                        shockDetector.setOpcresource(detectamplitudeoutopctagmvresource);
                        shockDetector.setFilterbacktodcstag(detectfilteroutopctagmv);
                        shockDetector.setFilteropcresource(detectfilteroutopctagmvresource);
                        mvpin.setShockDetector(shockDetector);
                    }
                    fleshcontrolModle.getModlePins().add(mvfbpin);

                    /**
                     * 滤波器设置
                     * */
                    if (filternamemv != null) {

                        if (filternamemv.equals("mvav")) {
                            MoveAverageFilter moveAverageFilter = new MoveAverageFilter();
                            moveAverageFilter.setBackToDCSTag(filteropctagmv);
                            moveAverageFilter.setOpcresource(modlejsonObject.getString("filtermvfb" + i + "resource"));
                            moveAverageFilter.setCapacity(Double.valueOf(filtercoefmv).intValue());//窗口时间长度
                            moveAverageFilter.setFiltername(filternamemv);
                            mvfbpin.setFilter(moveAverageFilter);
                        } else if (filternamemv.equals("fodl")) {
                            FirstOrderLagFilter firstOrderLagFilter = new FirstOrderLagFilter();
                            firstOrderLagFilter.setBackToDCSTag(filteropctagmv);
                            firstOrderLagFilter.setOpcresource(modlejsonObject.getString("filtermvfb" + i + "resource"));
                            firstOrderLagFilter.setFilter_alphe(Double.valueOf(filtercoefmv));
                            firstOrderLagFilter.setFiltername(filternamemv);
                            mvfbpin.setFilter(firstOrderLagFilter);
                        }


                    }

                    ModlePin mvuppin = new ModlePin();
                    mvuppin.setReference_modleId(fleshcontrolModle.getModleId());
                    mvuppin.setModlePinName("mvup" + i);
                    mvuppin.setPintype(ModlePin.TYPE_PIN_MVUP);
                    mvuppin.setResource(mvupresoure);
                    mvuppin.setModleOpcTag(mvup);
                    fleshcontrolModle.getModlePins().add(mvuppin);

                    ModlePin mvdownpin = new ModlePin();
                    mvdownpin.setReference_modleId(fleshcontrolModle.getModleId());
                    mvdownpin.setModlePinName("mvdown" + i);
                    mvdownpin.setPintype(ModlePin.TYPE_PIN_MVDOWN);
                    mvdownpin.setResource(mvdownresource);
                    mvdownpin.setModleOpcTag(mvdown);


                    fleshcontrolModle.getModlePins().add(mvdownpin);
                }

            }


            for (int i = 1; i <= baseConf.getFf(); i++) {
                String ff = modlejsonObject.getString("ff" + i).trim();
                String ffcomment = modlejsonObject.getString("ff" + i + "comment").trim();
                String ffup = modlejsonObject.getString("ffup" + i).trim();
                String ffdown = modlejsonObject.getString("ffdown" + i).trim();

                String ffupresource = modlejsonObject.getString("ffup" + i + "resource");
                String ffdownresource = modlejsonObject.getString("ffdown" + i + "resource");//ffdwon4resource

                /**
                 * 滤波相关处理
                 * */
                String filtercoefff = modlejsonObject.getString("filtercoefff" + i);
                String filteropctagff = modlejsonObject.getString("filteropctagff" + i);
                String filternameff = modlejsonObject.getString("filternameff" + i);

                if (ff != null && !ff.trim().equals("")) {
                    ModlePin ffpin = new ModlePin();
                    ffpin.setReference_modleId(fleshcontrolModle.getModleId());
                    ffpin.setModlePinName("ff" + i);
                    ffpin.setPintype(ModlePin.TYPE_PIN_FF);
                    ffpin.setResource(modlejsonObject.getString(ModlePin.TYPE_PIN_FF + i + "resource"));
                    ffpin.setModleOpcTag(ff);
                    ffpin.setOpcTagName(ffcomment);

                    if (filternameff != null) {
                        if (filternameff.equals("mvav")) {
                            MoveAverageFilter moveAverageFilter = new MoveAverageFilter();
                            moveAverageFilter.setBackToDCSTag(filteropctagff);
                            moveAverageFilter.setOpcresource(modlejsonObject.getString("filterff" + i + "resource"));

                            moveAverageFilter.setCapacity(Double.valueOf(filtercoefff).intValue());//窗口时间长度
                            moveAverageFilter.setFiltername(filternameff);
                            ffpin.setFilter(moveAverageFilter);
                        } else if (filternameff.equals("fodl")) {
                            FirstOrderLagFilter firstOrderLagFilter = new FirstOrderLagFilter();
                            firstOrderLagFilter.setBackToDCSTag(filteropctagff);
                            firstOrderLagFilter.setOpcresource(modlejsonObject.getString("filterff" + i + "resource"));

                            firstOrderLagFilter.setFilter_alphe(Math.abs(Double.valueOf(filtercoefff)) > 1 ? 1 : Math.abs(Double.valueOf(filtercoefff)));
                            firstOrderLagFilter.setFiltername(filternameff);
                            ffpin.setFilter(firstOrderLagFilter);
                        }
                    }

                    fleshcontrolModle.getModlePins().add(ffpin);


                    ModlePin ffuppin = new ModlePin();
                    ffuppin.setReference_modleId(fleshcontrolModle.getModleId());
                    ffuppin.setModlePinName("ffup" + i);
                    ffuppin.setPintype(ModlePin.TYPE_PIN_FFUP);
                    ffuppin.setResource(ffupresource);
                    ffuppin.setModleOpcTag(ffup);
                    fleshcontrolModle.getModlePins().add(ffuppin);


                    ModlePin ffdownpin = new ModlePin();
                    ffdownpin.setReference_modleId(fleshcontrolModle.getModleId());
                    ffdownpin.setModlePinName("ffdown" + i);
                    ffdownpin.setPintype(ModlePin.TYPE_PIN_FFDOWN);
                    ffdownpin.setResource(ffdownresource);
                    ffdownpin.setModleOpcTag(ffdown);
                    fleshcontrolModle.getModlePins().add(ffdownpin);

                }


            }


            /**数据库引脚信息更新*/
            if (fleshcontrolModle.getModlePins().size() != 0) {

                /**新模型*/
                if ((modlejsonObject.getString("modleid") == null) || (modlejsonObject.getString("modleid").trim().equals(""))) {

                    /**
                     * 新模型
                     * */
                    modleDBServe.insertModlePins(fleshcontrolModle.getModlePins());
                    /**
                     * 插入滤波器数据 和 插入震荡检测器
                     * */
                    for (ModlePin modlePin : fleshcontrolModle.getModlePins()) {

                        /**滤波器*/
                        if (modlePin.getFilter() != null) {
                            modlePin.getFilter().setPk_pinid(modlePin.getModlepinsId());

                            if (modlePin.getFilter() instanceof MoveAverageFilter) {

                                modleDBServe.insertPinsMVAVFilter((MoveAverageFilter) modlePin.getFilter());

                            } else if (modlePin.getFilter() instanceof FirstOrderLagFilter) {

                                modleDBServe.insertPinsFODLFilter((FirstOrderLagFilter) modlePin.getFilter());

                            }


                        }

                        /**震荡检测器*/
                        if (modlePin.getShockDetector() != null) {
                            modlePin.getShockDetector().setPk_pinid(modlePin.getModlepinsId());
                            modleDBServe.insertShockDetetetor(modlePin.getShockDetector());
                        }


                    }


                } else {
                    /**
                     * 老模型
                     * 删除引脚和对应的filter 和检测计
                     * */
                    modleDBServe.deleteModlePins(Integer.valueOf(modlejsonObject.getString("modleid").trim()));

                    ControlModle oldmodle = modleConstainer.getRunnableModulepool().get(Integer.valueOf(modlejsonObject.getString("modleid").trim()));

                    /**获取老模型的pv引脚使能情况*/
                    for (ModlePin oldmodlePin : oldmodle.getCategoryPVmodletag()) {
                        if ((oldmodlePin.getModleOpcTag() != null) && (!"".equals(oldmodlePin.getModleOpcTag()))) {
                            historyPinsEnable.put(oldmodlePin.getModlePinName(), oldmodlePin.getPinEnable());
                        }
                    }


                    /**保存前一次仿真器运行状态运行状态*/
//                    fleshcontrolModle.setLastsimulaterunorstop(oldmodle.getSimulatControlModle().isIssimulation());

                    for (ModlePin modlePin : oldmodle.getModlePins()) {
                        if (modlePin.getFilter() != null) {
                            modleDBServe.deletePinsFilter(modlePin.getFilter().getPk_pinid());
                        }
                        if (modlePin.getShockDetector() != null) {
                            modleDBServe.removeShockDetetetor(modlePin.getShockDetector().getPk_shockdetectid());
                        }

                    }

                    /**重新插入引脚
                     * 1、插入引脚之前把他设置下以前的引脚使能*/
                    for (ModlePin modlePin : fleshcontrolModle.getModlePins()) {

                        Matcher pvmatcher = pvpattern.matcher(modlePin.getModlePinName());
                        if (pvmatcher.find()) {
//                            if ((modlePin.getModleOpcTag() != null) && (!"".equals(modlePin.getModleOpcTag()))) {
                            Integer onOroff = historyPinsEnable.get(modlePin.getModlePinName());
                            modlePin.setPinEnable(onOroff == null ? 1 : onOroff);
//                            }
                        }
                    }


                    modleDBServe.insertModlePins(fleshcontrolModle.getModlePins());

                    for (ModlePin modlePin : fleshcontrolModle.getModlePins()) {

                        if (modlePin.getFilter() != null) {
                            modlePin.getFilter().setPk_pinid(modlePin.getModlepinsId());

                            if (modlePin.getFilter() instanceof MoveAverageFilter) {

                                modleDBServe.insertPinsMVAVFilter((MoveAverageFilter) modlePin.getFilter());

                            } else if (modlePin.getFilter() instanceof FirstOrderLagFilter) {

                                modleDBServe.insertPinsFODLFilter((FirstOrderLagFilter) modlePin.getFilter());

                            }


                        }

                        /**震荡检测器*/
                        if (modlePin.getShockDetector() != null) {
                            modlePin.getShockDetector().setPk_pinid(modlePin.getModlepinsId());
                            modleDBServe.insertShockDetetetor(modlePin.getShockDetector());
                        }
                    }
                }

            }

            /**响应处理*/
            List<ResponTimeSerise> responTimeSeriseArrayList = new ArrayList<>();
            JSONArray mvrespjsonObject = JSONArray.parseArray(mvresp.trim());

            for (int i = 0; i < baseConf.getMv(); ++i) {
                JSONObject mvrespjsonObjectJSONObject = mvrespjsonObject.getJSONObject(i);
                String mvnamepin = mvrespjsonObjectJSONObject.getString("mv");
                for (int j = 1; j <= baseConf.getPv(); ++j) {
                    if (!mvrespjsonObjectJSONObject.getString("pv" + j).trim().equals("")) {
                        ResponTimeSerise responTimeSerise = new ResponTimeSerise();
                        responTimeSerise.setInputPins(mvnamepin);
                        responTimeSerise.setOutputPins("pv" + j);
                        responTimeSerise.setRefrencemodleId(fleshcontrolModle.getModleId());
                        responTimeSerise.setStepRespJson(mvrespjsonObjectJSONObject.getString("pv" + j).trim());
                        responTimeSeriseArrayList.add(responTimeSerise);
                    }
                }
            }


            JSONArray ffrespjsonObject = JSONArray.parseArray(ffresp.trim());
            for (int i = 0; i < baseConf.getFf(); ++i) {
                JSONObject ffrespjsonObjectJSONObject = ffrespjsonObject.getJSONObject(i);
                String ffnamepin = ffrespjsonObjectJSONObject.getString("ff");
                for (int j = 1; j <= baseConf.getPv(); ++j) {
                    if (!ffrespjsonObjectJSONObject.getString("pv" + j).trim().equals("")) {
                        ResponTimeSerise ffresponTimeSerise = new ResponTimeSerise();
                        ffresponTimeSerise.setInputPins(ffnamepin);
                        ffresponTimeSerise.setOutputPins("pv" + j);
                        ffresponTimeSerise.setRefrencemodleId(fleshcontrolModle.getModleId());
                        ffresponTimeSerise.setStepRespJson(ffrespjsonObjectJSONObject.getString("pv" + j).trim());
                        responTimeSeriseArrayList.add(ffresponTimeSerise);
                    }
                }

            }

            if (responTimeSeriseArrayList.size() != 0) {

                if (fleshcontrolModle.getModlePins().size() != 0) {

                    if (modlejsonObject.getString("modleid").trim().equals("")) {
                        modleDBServe.insertModleResp(responTimeSeriseArrayList);
                    } else {
                        modleDBServe.deleteModleResp(Integer.valueOf(modlejsonObject.getString("modleid").trim()));
                        modleDBServe.insertModleResp(responTimeSeriseArrayList);
                    }

                }
            }
            /***
             * 如果找不模型的id,说明是新模型那么直接从数据库中初始化模型
             * */
            if (modlejsonObject.getString("modleid").trim().equals("")) {
                ControlModle controlModle1 = modleDBServe.getModle(fleshcontrolModle.getModleId());
                modleConstainer.registerModle(controlModle1);
            } else {
                /**
                 * 找到id，那么就需要停止运行，然后移除模型，然后重新从数据库初始化模型，开始运行
                 * */

                ControlModle oldcontrolModle = modleConstainer.getRunnableModulepool().get(Integer.valueOf(modlejsonObject.getString("modleid").trim()));
                oldcontrolModle.getExecutePythonBridge().stop();

                /**停止仿真器*/
                if (oldcontrolModle.getSimulatControlModle().isIssimulation()) {
                    oldcontrolModle.getSimulatControlModle().getExecutePythonBridgeSimulate().stop();
                }
                oldcontrolModle.unregisterpin();

                modleConstainer.getRunnableModulepool().remove(Integer.valueOf(modlejsonObject.getString("modleid").trim()));
                ControlModle newcontrolModle2 = modleDBServe.getModle(fleshcontrolModle.getModleId());
//                newcontrolModle2.setLastsimulaterunorstop(fleshcontrolModle.isLastsimulaterunorstop());
                modleConstainer.registerModle(newcontrolModle2);
            }


//        ModelAndView mv=new ModelAndView();
//        mv.setViewName("redirect:index.do");
//        return mv;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "success");
            if (newmodle) {
                jsonObject.put("go", "/modle/newmodle.do");
            } else {
                jsonObject.put("go", "/modle/modifymodle.do?modleid=" + fleshcontrolModle.getModleId());
            }

            jsonObject.put("modleName", fleshcontrolModle.getModleName());
            jsonObject.put("modleId", fleshcontrolModle.getModleId());
            return jsonObject.toJSONString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "error");
            return jsonObject.toJSONString();
        }
    }


}

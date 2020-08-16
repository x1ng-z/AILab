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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对于模型的操作
 *
 * @author zzx
 * @version 1.0
 * @date 2020/3/19 14:11
 */

@Controller("modle")
@RequestMapping("/modle")
public class ModleController {
    public static Logger logger = Logger.getLogger(ModleController.class);
    private static Pattern pvpattern = Pattern.compile("(^pv\\d+$)");

    @Autowired
    private BaseConf baseConf;
    @Autowired
    private ModleDBServe modleDBServe;
    @Autowired
    private ModleConstainer modleConstainer;

    @Autowired
    private OpcServicConstainer opcServicConstainer;


    @RequestMapping("/modlestatus/{modleId}")
    public ModelAndView modelStatus(@PathVariable("modleId") String modleid) {
        ControlModle controlModle = modleConstainer.getModulepool().get(Integer.valueOf(modleid.trim()));
        ModelAndView mv = new ModelAndView();
        mv.setViewName("modleStatus");
        mv.addObject("modle", controlModle);
        return mv;
    }


    /**
     * pv切出
     */
    @RequestMapping("/modlepvcheckout/{modleid}/{pinid}/{onOroff}")
    @ResponseBody
    public String modelpvcheckout(@PathVariable("modleid") String modleid, @PathVariable("pinid") String pinid, @PathVariable("onOroff") String onOroff) {
        ControlModle controlModle = modleConstainer.getModulepool().get(Integer.valueOf(modleid.trim()));
        for (ModlePin pvpin : controlModle.getCategoryPVmodletag()) {
            if(pvpin.getModlepinsId()==Integer.valueOf(pinid)){
                try {
                    /**如果当前是0,这要切入，如果当前是1,这要切除，*/
                    pvpin.setPinEnable(Integer.valueOf(onOroff)==0?1:0);
                    modleDBServe.updatepinEnable(pvpin.getModlepinsId(), Integer.valueOf(onOroff)==0?1:0);
                    JSONObject result = new JSONObject();
                    result.put("msg", "success");
                    return result.toJSONString();
                } catch (NumberFormatException e) {
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
        int loop = 0;
        ControlModle controlModle = modleConstainer.getModulepool().get(Integer.valueOf(modleid.trim()));

        JSONObject result = new JSONObject();

        result.put("funelUp", controlModle.getBackPVFunelUp());
        result.put("funelDwon", controlModle.getBackPVFunelDown());
        result.put("funneltype", controlModle.getFunneltype());

        result.put("predict", controlModle.getBackPVPrediction());

        int[] xaxis = new int[controlModle.getTimeserise_N()];
        for (int i = 0; i < controlModle.getTimeserise_N(); i++) {
            xaxis[i] = i;
        }
        result.put("xaxis", xaxis);
        result.put("outSetp", controlModle.getControlAPCOutCycle());


        loop = 0;
        double[] pvs = new double[controlModle.getCategoryPVmodletag().size()];
        for (ModlePin pvpin : controlModle.getCategoryPVmodletag()) {
            pvs[loop] = pvpin.modleGetReal();
            loop++;
        }
        result.put("pv", pvs);


        loop = 0;
        String[] pvcurveNames = new String[controlModle.getCategoryPVmodletag().size()];
        String[] funelUpcurveNames = new String[controlModle.getCategoryPVmodletag().size()];
        String[] funelDowncurveNames = new String[controlModle.getCategoryPVmodletag().size()];
        for (ModlePin pvpin : controlModle.getCategoryPVmodletag()) {
            pvcurveNames[loop] = pvpin.getModlePinName();
            funelUpcurveNames[loop] = "funelUp";
            funelDowncurveNames[loop] = "funelDown";
            loop++;
        }
        result.put("curveNames4funelUp", funelUpcurveNames);
        result.put("curveNames4pv", pvcurveNames);
        result.put("curveNames4funelDown", funelDowncurveNames);


        /**表格内容*/
        int pvnum = controlModle.getCategoryPVmodletag().size();//2
        int mvnum = controlModle.getCategoryMVmodletag().size();//1
        int maxrownum = Math.max(pvnum, mvnum);

        JSONArray modlereadData = new JSONArray();
        JSONArray sdmvData = new JSONArray();
        JSONArray ffData = new JSONArray();

        for (loop = 0; loop < maxrownum; loop++) {
            JSONObject mainmodlerowcontext = new JSONObject();
            JSONObject sdmvrowcontext = new JSONObject();
            JSONObject ffrowcontext = new JSONObject();

            String mainrowpinname = "";
            if (loop < pvnum) {//pv
                ModlePin pv = controlModle.getCategoryPVmodletag().get(loop);
                ModlePin sp = controlModle.getCategorySPmodletag().get(loop);

//                rowcontext.put("pvName", pv.getModleOpcTag());
                mainmodlerowcontext.put("pvValue", Tool.getSpecalScale(3, pv.modleGetReal()));
                mainmodlerowcontext.put("spValue", Tool.getSpecalScale(3, sp.modleGetReal()));
                mainmodlerowcontext.put("e", Tool.getSpecalScale(3, controlModle.getBackPVPredictionError()[loop]));
                mainmodlerowcontext.put("shockpv", pv.getShockDetector() == null ? "" : Tool.getSpecalScale(3, pv.getShockDetector().getLowhzA()));

                sdmvrowcontext.put("pinName", pv.getModlePinName());
                ffrowcontext.put("pinName", pv.getModlePinName());

                mainmodlerowcontext.put("checkIO", pv.getReference_modleId()+"_"+pv.getModlepinsId()+"_"+pv.getPinEnable());

                /**仿真dmv*/
                for (int indexmv = 0; indexmv < controlModle.getInputpoints_m(); ++indexmv) {
                    if (controlModle.getMatrixPvUseMv()[loop][indexmv] == 1) {
                        sdmvrowcontext.put(controlModle.getCategoryMVmodletag().get(indexmv).getModlePinName(), Tool.getSpecalScale(3, controlModle.getSimulatControlModle().getBackSimulateDmv()[loop][indexmv]));
                    }
                }

                /**ff*/
                if (controlModle.getFeedforwardpoints_v() > 0) {
                    for (int indexff = 0; indexff < controlModle.getFeedforwardpoints_v(); ++indexff) {
                        if (controlModle.getMatrixPvUseFf()[loop][indexff] == 1) {
                            /**dff*/
                            ffrowcontext.put("d" + controlModle.getCategoryFFmodletag().get(indexff).getModlePinName(), Tool.getSpecalScale(3, controlModle.getBackDff()[loop][indexff]));
                            /**ff值*/
                            ffrowcontext.put(controlModle.getCategoryFFmodletag().get(indexff).getModlePinName(), Tool.getSpecalScale(3, controlModle.getCategoryFFmodletag().get(indexff).modleGetReal()));
                        }
                    }
                }

                mainrowpinname += pv.getModlePinName();
            }


            if (loop < mvnum) {//1,1,mv
                ModlePin mv = controlModle.getCategoryMVmodletag().get(loop);
                ModlePin mvDownLmt = mv.getDownLmt();
                ModlePin mvUpLmt = mv.getUpLmt();
                ModlePin mvFeedBack = mv.getFeedBack();
                mainmodlerowcontext.put("mvvalue", Tool.getSpecalScale(3, mv.modleGetReal()));
                mainmodlerowcontext.put("mvDownLmt", Tool.getSpecalScale(3, mvDownLmt.modleGetReal()));
                mainmodlerowcontext.put("mvUpLmt", Tool.getSpecalScale(3, mvUpLmt.modleGetReal()));
                mainmodlerowcontext.put("mvFeedBack", Tool.getSpecalScale(3, mvFeedBack.modleGetReal()));
                mainmodlerowcontext.put("dmv", Tool.getSpecalScale(3, controlModle.getBackrawDmv()[loop]));
                mainmodlerowcontext.put("shockmv", mv.getShockDetector() == null ? "" : Tool.getSpecalScale(3, mv.getShockDetector().getLowhzA()));
                mainrowpinname += "|" + mv.getModlePinName();
            }

            mainmodlerowcontext.put("pinName", mainrowpinname);
//            mainmodlerowcontext.put("auto", controlModle.getAutoEnbalePin() == null ? "手动" : (controlModle.getAutoEnbalePin().modleGetReal() == 0 ? "手动" : "自动"));
            modlereadData.add(mainmodlerowcontext);
            if (!sdmvrowcontext.equals("")) {
                sdmvData.add(sdmvrowcontext);
            }

            ffData.add(ffrowcontext);
        }

        result.put("modleRealData", modlereadData);
        result.put("modlestatus",controlModle.getModleEnable());
        result.put("sdmvData", sdmvData);

        if (controlModle.getFeedforwardpoints_v() > 0) {
            result.put("ffData", ffData);
        }


        return result.toJSONString();

    }


    @RequestMapping("/stopModle")
    @ResponseBody
    public String stopModel(@RequestParam("modleid") String modleid) {

        ControlModle controlModle = modleConstainer.getModulepool().get(Integer.valueOf(modleid.trim()));
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
        ControlModle controlModle = modleConstainer.getModulepool().get(Integer.valueOf(modleid.trim()));
        if (controlModle != null) {
            if (controlModle.getSimulatControlModle().isIssimulation()) {
                controlModle.getSimulatControlModle().generateSimulatevalidkey();
                /**停止仿真运行*/
                controlModle.getSimulatControlModle().setIssimulation(false);
                controlModle.setLastsimulaterunorstop(false);
                controlModle.getSimulatControlModle().getExecutePythonBridgeSimulate().stop();
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

        ControlModle controlModle = modleConstainer.getModulepool().get(Integer.valueOf(modleid.trim()));
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
        ControlModle controlModle = modleConstainer.getModulepool().get(Integer.valueOf(modleid.trim()));
        if (controlModle != null) {
            if (!controlModle.getSimulatControlModle().isIssimulation()) {
                controlModle.setLastsimulaterunorstop(true);
                controlModle.getSimulatControlModle().setIssimulation(true);
                controlModle.getSimulatControlModle().getExecutePythonBridgeSimulate().execute();
                return "success";
            }
        } else {
            return "error";
        }
        return "error";
    }

    @RequestMapping("/deleteModle")
    @ResponseBody
    public String deleteModel(@RequestParam("modleid") String modleid) {
        try {
            ControlModle controlModle = modleConstainer.getModulepool().get(Integer.valueOf(modleid.trim()));
            if (controlModle != null) {
                if (controlModle.getModleEnable() == 1) {
                    controlModle.generateValidkey();
                    controlModle.setModleEnable(0);
                    controlModle.getExecutePythonBridge().stop();
                }

                if (controlModle.getSimulatControlModle().isIssimulation()) {
                    controlModle.getSimulatControlModle().setIssimulation(false);
                    controlModle.getSimulatControlModle().generateSimulatevalidkey();
                    controlModle.getSimulatControlModle().getExecutePythonBridgeSimulate().stop();
                }
                /**
                 * 1、删除响应序列
                 * 2、删除应交过滤器
                 * 3、删除引脚
                 * 4、移除opc点号
                 * 4、删除模型
                 * */
                modleDBServe.deleteModleResp(controlModle.getModleId());
                for (ModlePin modlePin : controlModle.getModlePins()) {
                    if (modlePin.getFilter() != null) {
                        modleDBServe.deletePinsFilter(modlePin.getFilter().getPk_pinid());
                    }
                    if (modlePin.getShockDetector() != null) {
                        modleDBServe.removeShockDetetetor(modlePin.getShockDetector().getPk_shockdetectid());
                    }
                }

                modleDBServe.deleteModlePins(controlModle.getModleId());
                controlModle.unregisterpin();
                modleDBServe.deleteModle(controlModle.getModleId());
                modleConstainer.getModulepool().remove(Integer.valueOf(modleid.trim()));
                return "success";
            } else {
                return "error";
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            return "error";
        }
//        ModelAndView mv=new ModelAndView();
//        mv.setViewName("redirect:/login/index.do");
//        return "/modle/modlestatus.do";
    }


    @RequestMapping("/newmodle")
    public ModelAndView newModel() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("newModle");
        JSONArray mvrespon = new JSONArray();
        JSONArray ffrespon = new JSONArray();

        List<Integer> pvlist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getPv(); ++i) {
            pvlist.add(i);
        }

        List<Integer> fflist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getFf(); ++i) {
            fflist.add(i);

            JSONObject mvjson = new JSONObject();
            mvjson.put(ModlePin.TYPE_PIN_FF, ModlePin.TYPE_PIN_FF + i);
            for (int j = 1; j <= baseConf.getPv(); ++j) {
                mvjson.put(ModlePin.TYPE_PIN_PV + j, "");
            }
            ffrespon.add(mvjson);

        }


        List<Integer> mvlist = new ArrayList<>();
        for (int i = 1; i <= baseConf.getMv(); ++i) {
            mvlist.add(i);

            JSONObject mvjson = new JSONObject();
            mvjson.put(ModlePin.TYPE_PIN_MV, ModlePin.TYPE_PIN_MV + i);
            for (int j = 1; j <= baseConf.getPv(); ++j) {
                mvjson.put(ModlePin.TYPE_PIN_PV + j, "");
            }
            mvrespon.add(mvjson);

        }

        mv.addObject("pvlist", pvlist);
        mv.addObject("fflist", fflist);
        mv.addObject("mvlist", mvlist);
        mv.addObject("mvresp", mvrespon.toJSONString());
        mv.addObject("ffresp", ffrespon.toJSONString());
        /**opc点号来源*/
        List<String> opcresources = new ArrayList<>();
        for (OPCService opcService : opcServicConstainer.getOpcservepool().values()) {
            opcresources.add("opc" + opcService.getOpcip());
        }
        opcresources.add("constant");
        mv.addObject("opcresources", opcresources);
        return mv;
    }


    @RequestMapping("/modifymodle")
    public ModelAndView modifyModel(@RequestParam("modleid") int modleid) {

        ControlModle controlModle = modleConstainer.getModulepool().get(modleid);

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
             * key=引脚opc所对应的
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
                    sppin.setOpcTagName(spcomment);
                    fleshcontrolModle.getModlePins().add(sppin);

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
                    mvpin.setDmvHigh(Double.valueOf(dmvhigh));
                    mvpin.setDmvLow(Double.valueOf(dmvlow));
                    mvpin.setOpcTagName(mvcomment);
                    fleshcontrolModle.getModlePins().add(mvpin);


                    ModlePin mvfbpin = new ModlePin();
                    mvfbpin.setReference_modleId(fleshcontrolModle.getModleId());
                    mvfbpin.setModlePinName("mvfb" + i);
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
                    mvuppin.setResource(mvupresoure);
                    mvuppin.setModleOpcTag(mvup);
                    fleshcontrolModle.getModlePins().add(mvuppin);

                    ModlePin mvdownpin = new ModlePin();
                    mvdownpin.setReference_modleId(fleshcontrolModle.getModleId());
                    mvdownpin.setModlePinName("mvdown" + i);
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
                    ffuppin.setResource(ffupresource);
                    ffuppin.setModleOpcTag(ffup);
                    fleshcontrolModle.getModlePins().add(ffuppin);


                    ModlePin ffdownpin = new ModlePin();
                    ffdownpin.setReference_modleId(fleshcontrolModle.getModleId());
                    ffdownpin.setModlePinName("ffdown" + i);
                    ffdownpin.setResource(ffdownresource);
                    ffdownpin.setModleOpcTag(ffdown);
                    fleshcontrolModle.getModlePins().add(ffdownpin);

                }


            }


            /**
             * 数据库引脚信息更新
             * */
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
                    //老模型
                    /**
                     * 删除引脚和对应的filter 和检测计
                     * */
                    modleDBServe.deleteModlePins(Integer.valueOf(modlejsonObject.getString("modleid").trim()));

                    ControlModle oldmodle = modleConstainer.getModulepool().get(Integer.valueOf(modlejsonObject.getString("modleid").trim()));

                    /**获取老模型的pv引脚使能情况*/
                    for (ModlePin oldmodlePin : oldmodle.getCategoryPVmodletag()) {
                        if ((oldmodlePin.getModleOpcTag() != null) && (!"".equals(oldmodlePin.getModleOpcTag()))) {
                            historyPinsEnable.put(oldmodlePin.getModleOpcTag(), oldmodlePin.getPinEnable());
                        }
                    }


                    /**保存前一次仿真器运行状态运行状态*/
                    fleshcontrolModle.setLastsimulaterunorstop(oldmodle.getSimulatControlModle().isIssimulation());

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
                            if ((modlePin.getModleOpcTag() != null) && (!"".equals(modlePin.getModleOpcTag()))) {
                                Integer onOroff = historyPinsEnable.get(modlePin.getModleOpcTag());
                                modlePin.setPinEnable(onOroff == null ? 1 : onOroff);
                            }
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
             * 如果找不模型的id,那么直接从数据库中初始化模型
             * */
            if (modlejsonObject.getString("modleid").trim().equals("")) {
                ControlModle controlModle1 = modleDBServe.getModle(fleshcontrolModle.getModleId());
                modleConstainer.registerModle(controlModle1);
            } else {
                /**
                 * 找到id，那么就需要停止运行，然后移除模型，然后重新从数据库初始化模型，开始运行
                 * */

                ControlModle oldcontrolModle = modleConstainer.getModulepool().get(Integer.valueOf(modlejsonObject.getString("modleid").trim()));
                oldcontrolModle.getExecutePythonBridge().stop();

                /**停止仿真器*/
                if (oldcontrolModle.getSimulatControlModle().isIssimulation()) {
                    oldcontrolModle.getSimulatControlModle().getExecutePythonBridgeSimulate().stop();
                }
                oldcontrolModle.unregisterpin();

                modleConstainer.getModulepool().remove(Integer.valueOf(modlejsonObject.getString("modleid").trim()));
                ControlModle newcontrolModle2 = modleDBServe.getModle(fleshcontrolModle.getModleId());
                newcontrolModle2.setLastsimulaterunorstop(fleshcontrolModle.isLastsimulaterunorstop());
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

package hs.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.Dao.Service.AlgorithmDBServe;
import hs.Dao.Service.ModleDBServe;
import hs.Dao.Service.OpcDBServe;
import hs.Opc.OPCService;
import hs.Opc.OpcServicConstainer;
import hs.Opc.OpcVeriTag;
import hs.Utils.Tool;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/8/28 23:42
 */

/**
 * 主要用于OPC连接服务的操作
 */
@Controller
@RequestMapping("/opc")
public class OPCInfoController {
    public static Logger logger = Logger.getLogger(OPCInfoController.class);

    @Autowired
    private OpcServicConstainer opcServicConstainer;

    @Autowired
    private OpcDBServe opcDBServe;

    @Autowired
    private AlgorithmDBServe algorithmDBServe;

    @Autowired
    private ModleDBServe modleDBServe;


    /**
     * 获取分页的opc服务器连接信息
     */
    @RequestMapping("/pageopcinfo")
    @ResponseBody
    private String pageOPCInfo(@RequestParam("page") int page, @RequestParam("pagesize") int pagesize) {
        List<OPCService> opcServices = opcDBServe.pageopcserves((page - 1) * pagesize, pagesize);
        int count = opcDBServe.getopcservescount();
        JSONArray results = new JSONArray();
        for (OPCService opcserve : opcServices) {
            int opcserveid = opcserve.getOpcserveid();
            String opcuser = opcserve.getOpcuser();
            String opcpassword = opcserve.getOpcpassword();
            String opcip = opcserve.getOpcip();
            String opcclsid = opcserve.getOpcclsid();
            JSONObject object = new JSONObject();

            object.put("opcserveid", opcserveid);
            object.put("opcuser", opcuser);
            object.put("opcpassword", opcpassword);
            object.put("opcip", opcip);
            object.put("opcclsid", opcclsid);
            results.add(object);
        }
        return Tool.sendLayuiPage(count, results).toJSONString();
    }


    @RequestMapping("/opcserveinfo")
    private ModelAndView getOPCInfo() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("opc/opcserveinfo");
        return modelAndView;
    }


    /**
     * x新建opc服务器
     */
    @RequestMapping("/newopcserve")
    private ModelAndView newopcserve() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("opc/newopcserve");
        return modelAndView;
    }


    /**
     * x新建opc服务器
     */
    @RequestMapping("/savenewopcserve")
    @ResponseBody
    private String savenewopcserve(@RequestParam("opccontext") String opccontext) {

        JSONObject result = new JSONObject();
        try {
            JSONObject jsonopccontext = JSONObject.parseObject(opccontext);

            String opcserveid = jsonopccontext.getString("opcserveid");
            String opcuser = jsonopccontext.getString("opcuser");
            String opcpassword = jsonopccontext.getString("opcpassword");
            String opcip = jsonopccontext.getString("opcip");
            String opcclsid = jsonopccontext.getString("opcclsid");

            OPCService opcService = new OPCService();
            opcService.setOpcuser(opcuser);
            opcService.setOpcpassword(opcpassword);
            opcService.setOpcip(opcip);
            opcService.setOpcclsid(opcclsid);

            opcDBServe.insertopcserves(opcService);

            //加入新的
            opcServicConstainer.addOPCServe(opcService);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

            result.put("msg", "error");
            return result.toJSONString();
        }


        result.put("msg", "success");

        return result.toJSONString();
    }


    /**
     * x新建opc服务器
     */
    @RequestMapping("/modifyopcserve")
    private ModelAndView modifyopcserve(@RequestParam("opcserveid") int opcserveid) {

        OPCService opcservice = opcDBServe.findopcservebyid(opcserveid);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("opc/modifyopcserve");
        modelAndView.addObject("opcserve", opcservice);
        return modelAndView;
    }


    /**
     * x新建opc服务器
     */
    @RequestMapping("/updateopcserve")
    @ResponseBody
    private String updateopcserve(@RequestParam("opccontext") String opccontext) {
        JSONObject result = new JSONObject();
        try {
            JSONObject jsonObject = JSONObject.parseObject(opccontext);
            int opcserveid = jsonObject.getInteger("opcserveid");
            String opcuser = jsonObject.getString("opcuser");
            String opcpassword = jsonObject.getString("opcpassword");
            String opcip = jsonObject.getString("opcip");
            String opcclsid = jsonObject.getString("opcclsid");

            OPCService opcService = new OPCService();
            opcService.setOpcuser(opcuser);
            opcService.setOpcpassword(opcpassword);
            opcService.setOpcip(opcip);
            opcService.setOpcclsid(opcclsid);
            opcService.setOpcserveid(opcserveid);

            OPCService oldopcserve=opcDBServe.findopcservebyid(opcserveid);
            opcDBServe.updateopcserves(opcService);

            String isrepalceips=jsonObject.getString("like[repacle]");
            if((isrepalceips!=null)&&(isrepalceips.equals("on"))){
                if(oldopcserve!=null){
                    algorithmDBServe.updateallalgorithmips("opc"+oldopcserve.getOpcip(),"opc"+opcip);
                    modleDBServe.updateallmodleips("opc"+oldopcserve.getOpcip(),"opc"+opcip);
                }

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
     * x新建opc服务器
     */
    @RequestMapping("/deleteopcserve")
    @ResponseBody
    private String deleteopcserve(@RequestParam("opcserveid") int opcserveid) {
        JSONObject result = new JSONObject();
        try {
            opcDBServe.deleteopcservesAndverTags(opcserveid);
            opcServicConstainer.removeOPCServe(opcserveid);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        result.put("msg", "success");
        return result.toJSONString();
    }


    /**
     * opc验证的位号列表
     */
    @RequestMapping("/opcvertagstatus")
    @ResponseBody
    private ModelAndView opcvertagstatus(@RequestParam("opcserveid") int opcserveid) {
        ModelAndView modelandview = new ModelAndView();
        modelandview.setViewName("opc/opcvertagstatus");
        modelandview.addObject("opcserveid", opcserveid);
        return modelandview;
    }


    /**
     * opc验证的位号分页列表
     */
    @RequestMapping("/pageopcvertagstatus")
    @ResponseBody
    private String pageopcvertagstatus(@RequestParam("opcserveid") int opcserveid,@RequestParam("page")int page, @RequestParam("pagesize")int pagesize) {
        int count = 0;
        JSONArray data = null;
        try {
            List<OpcVeriTag> opcvertags = opcDBServe.findopcverificationtagbyopcserveid(opcserveid,(page-1)*pagesize,pagesize);
            count = opcDBServe.countopcverificationtagbyopcserveid(opcserveid);
            data = new JSONArray();
            for (OpcVeriTag tag : opcvertags) {
                JSONObject element = new JSONObject();
                element.put("tagid", tag.getTagid());
                element.put("tag", tag.getTag());
                element.put("tagName", tag.getTagName());
                element.put("opcserveid", tag.getOpcserveid());
                data.add(element);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Tool.sendLayuiPage(count, data).toJSONString();
    }


    /**
     * 新建opc校验位号
     */
    @RequestMapping("/newopcserveVerTag")
    private ModelAndView newopcserveVerTag(@RequestParam("opcserveid") int opcserveid) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("opcserveid", opcserveid);
        modelAndView.setViewName("opc/newopcverification");
        return modelAndView;
    }


    /**
     * 保存新建opc校验位号
     */
    @RequestMapping("/savenewopcserveVertag")
    @ResponseBody
    private String savenewopcserveVertag(@RequestParam("opcvertagcontext") String opcvertagcontext) {

        JSONObject jsonObject = JSONObject.parseObject(opcvertagcontext);

        Integer tagid = jsonObject.getInteger("tagid");
        String tagName = jsonObject.getString("tagName");
        String tag = jsonObject.getString("tag");
        Integer opcserveid = jsonObject.getInteger("opcserveid");
        JSONObject result = new JSONObject();
        try {
            OpcVeriTag opcveritag = new OpcVeriTag();
            opcveritag.setTagName(tagName);
            opcveritag.setTag(tag);
            opcveritag.setOpcserveid(opcserveid);
            opcDBServe.insertopcverificationtag(opcveritag);

            opcServicConstainer.addOPCServevertag(opcveritag.getOpcserveid(),opcveritag);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }
        result.put("msg", "success");
        return result.toJSONString();
    }


    /**
     * 修改opc服务器验证位号
     */
    @RequestMapping("/modifyopcservevertag")
    private ModelAndView modifyopcservevertag(@RequestParam("tagid")int tagid) {
        ModelAndView modleandview=new ModelAndView();
        modleandview.setViewName("opc/modifyopcverification");
        OpcVeriTag  opcvertag=opcDBServe.findopcverificationtagbytagid(tagid);
        modleandview.addObject("opcvertag",opcvertag);
        return modleandview;
    }

    /**
     * 保存修改opc服务器验证位号
     */
    @RequestMapping("/savemodifyopcservevertag")
    @ResponseBody
    private String savemodifyopcservevertag(@RequestParam("opcvertagcontext") String opcvertagcontext) {
        JSONObject jsonObject = JSONObject.parseObject(opcvertagcontext);
        Integer tagid = jsonObject.getInteger("tagid");
        String tagName = jsonObject.getString("tagName");
        String tag = jsonObject.getString("tag");
        Integer opcserveid = jsonObject.getInteger("opcserveid");
        JSONObject result = new JSONObject();
        try {
            OpcVeriTag opcveritag = new OpcVeriTag();
            opcveritag.setTagid(tagid);
            opcveritag.setTagName(tagName);
            opcveritag.setTag(tag);
            opcveritag.setOpcserveid(opcserveid);


            OpcVeriTag waitmodifytag=opcDBServe.findopcverificationtagbytagid(tagid);
            opcDBServe.updateopcverificationtag(opcveritag);
            if(waitmodifytag!=null){
                opcServicConstainer.modifyOPCServevertag(waitmodifytag.getOpcserveid(),waitmodifytag.getTag(),opcveritag);
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
     * 删除opc服务器验证tag
     */
    @RequestMapping("/deleteopcservevertag")
    @ResponseBody
    private String deleteopcservevertag(@RequestParam("tagid") int tagid) {
        JSONObject result = new JSONObject();
        try {

            OpcVeriTag waitdeletetag=opcDBServe.findopcverificationtagbytagid(tagid);
            opcDBServe.deleteopcverificationtag(tagid);
            if(waitdeletetag!=null){
                opcServicConstainer.removeOPCServevertag(waitdeletetag.getOpcserveid(),waitdeletetag);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        result.put("msg", "success");
        return result.toJSONString();
    }


}

package hs.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.Bean.Algorithm.AIModleConstainer;
import hs.Bean.Algorithm.AlgorithmModle;
import hs.Bean.Algorithm.AlgorithmProperty;
import hs.Dao.Service.AlgorithmDBServe;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/9/1 9:47
 */

@Controller
@RequestMapping("/aimodle")
public class AIModleController {
    public static Logger logger = Logger.getLogger(AIModleController.class);


    private OpcServicConstainer opcServicConstainer;

    @Autowired
    public void setOpcServicConstainer(OpcServicConstainer opcServicConstainer) {
        this.opcServicConstainer = opcServicConstainer;
    }


    private AIModleConstainer aiModleConstainer;

    @Autowired
    public void setAlgorithmConstraints(AIModleConstainer aiModleConstainer) {
        this.aiModleConstainer = aiModleConstainer;
    }


    private AlgorithmDBServe algorithmDBServe;

    @Autowired
    public void setAlgorithmDBServe(AlgorithmDBServe algorithmDBServe) {
        this.algorithmDBServe = algorithmDBServe;
    }


    /**
     * get modle status
     */

    @RequestMapping("/aimodlestatus/{modleid}")
    public ModelAndView getaimodlestatus(@PathVariable("modleid") int modleid) {
        ModelAndView modelAndView = new ModelAndView();
        AlgorithmModle algorithmModle = algorithmDBServe.findAlgorithmModlebyId(modleid);
        modelAndView.setViewName("aimodle/aimodleStatus");
        modelAndView.addObject("aimodle", algorithmModle);
        return modelAndView;
    }


    @RequestMapping("/aimodlepropertypic/{modleid}/{property}")
    public void getaimodlepropertypic(@PathVariable("modleid") int modleid, @PathVariable("property") String property, HttpServletResponse httpServletResponse) {
        ModelAndView modelAndView = new ModelAndView();
        AlgorithmModle algorithmModle = aiModleConstainer.getModulepool().get(modleid);

        for (AlgorithmProperty algorithmProperty : algorithmModle.getAlgorithmProperties()) {

            if (algorithmProperty.getProperty().equals(property) && algorithmProperty.getDatatype().equals(AlgorithmProperty.DATATYPE_IMAGE)) {

                try {
                    httpServletResponse.setContentType("image/*");
                    if (algorithmProperty.getPictureJPG() != null) {
                        httpServletResponse.getOutputStream().write(algorithmProperty.getPictureJPG().getData());
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
                break;
            }
        }

        return;
    }


    @RequestMapping("/aimodlepropertyvalue/{modleid}")
    @ResponseBody
    public String getaimodlepropertyvalue(@PathVariable("modleid") int modleid) {

        JSONObject message = new JSONObject();
        JSONArray result = new JSONArray();
        AlgorithmModle algorithmModle = aiModleConstainer.getModulepool().get(modleid);
        if (algorithmModle != null && algorithmModle.getAlgorithmProperties() != null) {
            for (AlgorithmProperty algorithmProperty : algorithmModle.getAlgorithmProperties()) {

                if (algorithmProperty.getDatatype().equals(AlgorithmProperty.DATATYPE_VALUE)) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("propertyname", algorithmProperty.getPropertyName());
                    jsonObject.put("propertyvalue", Tool.getSpecalScale(3,algorithmProperty.getPropertyValue()));
                    result.add(jsonObject);
                }
            }
            message.put("msg", "success");
        } else {
            message.put("msg", "error");
        }




        message.put("data", result);

        return message.toJSONString();
    }


    /**
     * delete aimodle
     */

    @RequestMapping("/deletaimodle")
    @ResponseBody
    public String deleteaimodle(@RequestParam("modleid") int modleid) {
        JSONObject result = new JSONObject();

        /**
         * 1\删除数据库模型数据
         * 2、移除模型池中的数据
         * 3、注销opc服务中的模型位号
         * 4、返回msg
         * */
        try {
            AlgorithmModle algorithmModle = algorithmDBServe.findAlgorithmModlebyId(modleid);

            if (algorithmModle != null && algorithmModle.getAlgorithmProperties() != null) {
                for (AlgorithmProperty algorithmProperty : algorithmModle.getAlgorithmProperties()) {
                    if (algorithmProperty.getFilter() != null) {
                        algorithmDBServe.deleteAlgorithmfilter(algorithmProperty.getFilter().getPk_filterid());
                    }
                    algorithmDBServe.deleteAlgorithmProperty(algorithmProperty.getPropertyid());
                }
            }
            algorithmDBServe.deleteAlgorithmModles(modleid);


            boolean resultofunregister = aiModleConstainer.getModulepool().get(modleid).unregisterproperties2OPC();

            /** 2、移除模型池中的数据*/
            aiModleConstainer.getModulepool().remove(modleid);
            result.put("msg", "success");
        } catch (Exception e) {
            result.put("msg", "error");
            logger.error(e.getMessage(), e);
        }
        return result.toJSONString();
    }


    /**
     * 新建模型 new modle
     */

    @RequestMapping("/newaimodle")
    public ModelAndView newAIModle() {
        ModelAndView view = new ModelAndView();
        view.setViewName("aimodle/newaimodle");
        return view;
    }


    @RequestMapping("/savenewaimodle")
    @ResponseBody
    public String saveNewAIModle(@RequestParam("aimodlecontext") String aimodlecontext) {
        JSONObject result = new JSONObject();
        AlgorithmModle algorithmModle = null;
        try {
            JSONObject modle = JSONObject.parseObject(aimodlecontext);
            algorithmModle = new AlgorithmModle();
            algorithmModle.setAlgorithmName(modle.getString("algorithmName"));
            algorithmDBServe.insertAlgorithmModles(algorithmModle);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }
        result.put("msg", "success");
        result.put("modlename", algorithmModle.getAlgorithmName());
        result.put("modleid", algorithmModle.getModleid());
        return result.toJSONString();
    }


    @RequestMapping("/updateaimodle")
    @ResponseBody
    public String updateaimodle(@RequestParam("aimodlecontext") String aimodlecontext) {
        JSONObject result = new JSONObject();
        AlgorithmModle algorithmModle = null;
        try {
            JSONObject modle = JSONObject.parseObject(aimodlecontext);
            algorithmModle = new AlgorithmModle();
            algorithmModle.setAlgorithmName(modle.getString("modlename"));
            algorithmModle.setModleid(modle.getInteger("modleid"));
            algorithmDBServe.updateAlgorithmModles(algorithmModle);


            /**
             * 1查看是否已经存储进constainer
             * 2如果存在则对相关点位opc进行移除\然后移除出constainer
             * 3从数据库中加载出模型并进行注册
             * **/
            AlgorithmModle buidledalgorithmModle = aiModleConstainer.getModulepool().get(modle.getInteger("modleid"));
            if (buidledalgorithmModle != null) {
                aiModleConstainer.unregisterModle(buidledalgorithmModle);
            }
            AlgorithmModle allnewmodle = algorithmDBServe.findAlgorithmModlebyId(modle.getInteger("modleid"));
            aiModleConstainer.registerModle(allnewmodle);


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }
        result.put("msg", "success");
        result.put("modlename", algorithmModle.getAlgorithmName());
        result.put("modleid", algorithmModle.getModleid());
        return result.toJSONString();
    }


    /**
     * 模型修改页面
     **/
    @RequestMapping("/modifyaimodle/{modleid}")
    public ModelAndView modifyAIModle(@PathVariable("modleid") int modleid) {

        AlgorithmModle algorithmModle = algorithmDBServe.findAlgorithmModlebyId(modleid);


        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("aimodle/modifyaimodle");

        modelAndView.addObject("modle", algorithmModle);

        return modelAndView;

    }


    /**
     * 分页获取模型的属性
     */
    @RequestMapping("/pageformodleproperty")
    @ResponseBody
    public String pageformodleproperty(@RequestParam("modleid") int modleid, @RequestParam("page") int page, @RequestParam("pagesize") int pagesize) {
        List<AlgorithmProperty> algorithmProperties = algorithmDBServe.getAlgorithmPropertys(modleid, (page - 1) * pagesize, pagesize);
        int count = algorithmDBServe.getAlgorithmPropertyscount(modleid);

        JSONArray datas = new JSONArray();
        for (AlgorithmProperty algorithmModle : algorithmProperties) {
            JSONObject data = new JSONObject();
            data.put("propertyid", algorithmModle.getPropertyid());
            data.put("propertyName", algorithmModle.getPropertyName());
            data.put("property", algorithmModle.getProperty());
            data.put("refrencealgorithmid", algorithmModle.getRefrencealgorithmid());
            data.put("resource", algorithmModle.getResource());
            data.put("opctag", algorithmModle.getOpctag());
            data.put("datatype", algorithmModle.getDatatype());
            datas.add(data);
        }
        return Tool.sendLayuiPage(count,datas).toJSONString();
    }


    /**
     * 新建模型的属性
     */
    @RequestMapping("/newaimodleproperty")
    public ModelAndView newaimodleproperty(@RequestParam("modleid") int modleid) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("aimodle/newaimodleproperty");

        modelAndView.addObject("modleid", modleid);

        List<String> datatype = new ArrayList<>();
        datatype.add(AlgorithmProperty.DATATYPE_VALUE);
        datatype.add(AlgorithmProperty.DATATYPE_IMAGE);
        modelAndView.addObject("datatype", datatype);


        List<String> opcresources = new ArrayList<>();
        for (OPCService opcService : opcServicConstainer.getOpcservepool().values()) {
            opcresources.add("opc" + opcService.getOpcip());
        }
        opcresources.add("constant");
        modelAndView.addObject("resource", opcresources);

        return modelAndView;
    }


    /**
     * 新建模型的属性
     */
    @RequestMapping("/saveaimodleproperty")
    @ResponseBody
    public String savenewaimodleproperty(@RequestParam("aimodlepropertycontext") String aimodlpropertyecontext) {

        JSONObject respon = new JSONObject();

        try {
            JSONObject propertyjson = JSONObject.parseObject(aimodlpropertyecontext);

            AlgorithmProperty algorithmProperty = new AlgorithmProperty();

            algorithmProperty.setRefrencealgorithmid(propertyjson.getInteger("modleid"));
            algorithmProperty.setDatatype(propertyjson.getString("datatype"));
            algorithmProperty.setOpctag(propertyjson.getString("opctag"));
            algorithmProperty.setProperty(propertyjson.getString("property"));
            algorithmProperty.setPropertyName(propertyjson.getString("propertyname"));
            algorithmProperty.setResource(propertyjson.getString("source"));


            if (!propertyjson.getString("filtername").equals("")) {
                if (propertyjson.getString("filtername").equals("mvav")) {
                    MoveAverageFilter filter = null;
                    filter = new MoveAverageFilter();
                    filter.setBackToDCSTag(propertyjson.getString("filteropctag"));
                    filter.setFiltername(propertyjson.getString("filtername"));
                    filter.setOpcresource(propertyjson.getString("filterresource"));
                    filter.setCapacity(propertyjson.getInteger("filtercoef"));
                    algorithmProperty.setFilter(filter);


                } else if (propertyjson.getString("filtername").equals("fodl")) {
                    FirstOrderLagFilter filter = null;
                    filter = new FirstOrderLagFilter();
                    filter.setBackToDCSTag(propertyjson.getString("filteropctag"));
                    filter.setFiltername(propertyjson.getString("filtername"));
                    filter.setOpcresource(propertyjson.getString("filterresource"));
                    filter.setFilter_alphe(propertyjson.getDouble("filtercoef"));
                    algorithmProperty.setFilter(filter);
                }
            }

            algorithmDBServe.insertAlgorithmPropertyAndFilter(algorithmProperty);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            respon.put("msg", "error");
            return respon.toJSONString();
        }
        respon.put("msg", "success");
        return respon.toJSONString();
    }


    @RequestMapping("/deleteaimodleproperty")
    @ResponseBody
    public String deleteaimodleproperty(@RequestParam("modleid") int modleid, @RequestParam("propertyid") int propertyid) {
        JSONObject respon = new JSONObject();
        try {
            algorithmDBServe.deleteAlgorithmPropertyAndFilter(modleid, propertyid);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            respon.put("msg", "error");
            return respon.toJSONString();
        }
        respon.put("msg", "success");
        return respon.toJSONString();
    }


    @RequestMapping("/modifyaimodleproperty")
    @ResponseBody
    public ModelAndView modifyaimodleproperty(@RequestParam("modleid") int modleid, @RequestParam("propertyid") int propertyid) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("aimodle/modifyaimodleproperty");
        AlgorithmProperty algorithmProperty = algorithmDBServe.findAlgorithmPropertybyId(propertyid);
        modelAndView.addObject("algorithmProperty", algorithmProperty);

        List<String> opcresources = new ArrayList<>();
        for (OPCService opcService : opcServicConstainer.getOpcservepool().values()) {
            opcresources.add("opc" + opcService.getOpcip());
        }
        opcresources.add("constant");
        modelAndView.addObject("resource", opcresources);

        List<String> datatype = new ArrayList<>();
        datatype.add(AlgorithmProperty.DATATYPE_VALUE);
        datatype.add(AlgorithmProperty.DATATYPE_IMAGE);
        modelAndView.addObject("datatype", datatype);
        return modelAndView;
    }


    /**
     * 新建模型的属性
     */
    @RequestMapping("/updateaimodleproperty")
    @ResponseBody
    public String updateaimodleproperty(@RequestParam("aimodlepropertycontext") String aimodlpropertyecontext) {

        JSONObject respon = new JSONObject();

        try {
            JSONObject propertyjson = JSONObject.parseObject(aimodlpropertyecontext);

            AlgorithmProperty algorithmProperty = new AlgorithmProperty();

            algorithmProperty.setPropertyid(propertyjson.getInteger("propertyid"));
            algorithmProperty.setDatatype(propertyjson.getString("datatype"));
            algorithmProperty.setOpctag(propertyjson.getString("opctag"));
            algorithmProperty.setProperty(propertyjson.getString("property"));
            algorithmProperty.setPropertyName(propertyjson.getString("propertyname"));
            algorithmProperty.setResource(propertyjson.getString("source"));


            /**
             * 1之前是否有过滤器  1-1之前有，现在有没有   1-2现在有
             *                 2-1 现在没有   2-2之前没有，现在有
             *
             *
             * */

            /**之前没有*/
            if(propertyjson.getString("filterid").equals("")){

                if (propertyjson.getString("filtername").equals("")) {

                    /**现在没有*/
                }else {
                    /**现在有,插入*/
                    if (propertyjson.getString("filtername").equals("mvav")) {
                        MoveAverageFilter filter = null;
                        filter = new MoveAverageFilter();
                        filter.setBackToDCSTag(propertyjson.getString("filteropctag"));
                        filter.setFiltername(propertyjson.getString("filtername"));
                        filter.setOpcresource(propertyjson.getString("filterresource"));
                        filter.setCapacity(propertyjson.getInteger("filtercoef"));
                        filter.setPk_pinid(propertyjson.getInteger("propertyid"));
                        algorithmDBServe.insertAlgorithmMVAVFilter(filter);
                    } else if (propertyjson.getString("filtername").equals("fodl")) {
                        FirstOrderLagFilter filter = null;
                        filter = new FirstOrderLagFilter();
                        filter.setBackToDCSTag(propertyjson.getString("filteropctag"));
                        filter.setFiltername(propertyjson.getString("filtername"));
                        filter.setOpcresource(propertyjson.getString("filterresource"));
                        filter.setFilter_alphe(propertyjson.getDouble("filtercoef"));
                        filter.setPk_pinid(propertyjson.getInteger("propertyid"));
                        algorithmDBServe.insertAlgorithmFODLFilter(filter);
                    }
                }
                algorithmDBServe.updateAlgorithmProperty(algorithmProperty);
            }else {
                /**之前有*/

                if(propertyjson.getString("filtername").equals("")){
                    /**现在无 删除*/
                    algorithmDBServe.deleteAlgorithmfilter(propertyjson.getInteger("filterid"));
                    algorithmDBServe.updateAlgorithmProperty(algorithmProperty);
                }else {
                    /**现在有  更新*/
                    if (propertyjson.getString("filtername").equals("mvav")) {
                        MoveAverageFilter filter = null;
                        filter = new MoveAverageFilter();
                        filter.setBackToDCSTag(propertyjson.getString("filteropctag"));
                        filter.setFiltername(propertyjson.getString("filtername"));
                        filter.setOpcresource(propertyjson.getString("filterresource"));
                        filter.setCapacity(propertyjson.getInteger("filtercoef"));
                        filter.setPk_filterid(propertyjson.getInteger("filterid"));
                        algorithmProperty.setFilter(filter);
                    } else if (propertyjson.getString("filtername").equals("fodl")) {
                        FirstOrderLagFilter filter = null;
                        filter = new FirstOrderLagFilter();
                        filter.setBackToDCSTag(propertyjson.getString("filteropctag"));
                        filter.setFiltername(propertyjson.getString("filtername"));
                        filter.setOpcresource(propertyjson.getString("filterresource"));
                        filter.setFilter_alphe(propertyjson.getDouble("filtercoef"));
                        filter.setPk_filterid(propertyjson.getInteger("filterid"));
                        algorithmProperty.setFilter(filter);
                    }
                    algorithmDBServe.updateAlgorithmPropertyAndFilter(algorithmProperty);

                }

            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            respon.put("msg", "error");
            return respon.toJSONString();
        }
        respon.put("msg", "success");
        return respon.toJSONString();
    }


}

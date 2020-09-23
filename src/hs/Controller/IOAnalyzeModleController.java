package hs.Controller;

import com.alibaba.fastjson.JSONObject;
import hs.Bean.Algorithm.AIModleConstainer;
import hs.Bean.Algorithm.AlgorithmModle;
import hs.Bean.Algorithm.AlgorithmProperty;
import hs.Bean.PictureJPG;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.PreDestroy;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/8/31 6:19
 */
@RestController
@RequestMapping("/aianalyze")
public class IOAnalyzeModleController {
    public static Logger logger = Logger.getLogger(IOAnalyzeModleController.class);
    private AIModleConstainer aiModleConstainer;


    @Autowired
    @NonNull
    public void setAiModleConstainer(@NonNull AIModleConstainer aiModleConstainer) {
        this.aiModleConstainer = aiModleConstainer;
    }


    @RequestMapping("/writemodledata/{modleid}")
    public String writeanalyzedata(@PathVariable("modleid") int modleid, HttpServletRequest httpServletRequest) {
        JSONObject jsonObject = new JSONObject();
        try {
            AlgorithmModle algorithmModle = aiModleConstainer.getModulepool().get(modleid);
            if (algorithmModle == null) {
                jsonObject.put("msg", "failed");
                return jsonObject.toJSONString();
            }
            for (AlgorithmProperty algorithmProperty : algorithmModle.getAlgorithmProperties()) {

                if (!algorithmProperty.getDatatype().equals(AlgorithmProperty.DATATYPE_VALUE)) {
                    continue;
                }
                String propertyvalue = httpServletRequest.getParameter(algorithmProperty.getProperty());
                if (propertyvalue != null) {
                    try {
                        algorithmProperty.setValue(Double.valueOf(propertyvalue));
                        logger.debug("property name:" + algorithmProperty.getProperty() + ",value=" + propertyvalue);
                    } catch (NumberFormatException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            boolean writereult = algorithmModle.writePropertiesValue();
            jsonObject.put("msg", writereult ? "success" : "failed");
            return jsonObject.toJSONString();
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
        }
        jsonObject.put("msg", "failed");

        return jsonObject.toJSONString();
    }


    @RequestMapping("/writemodlepic/{modleid}")
    public String writeanalyzepic(@PathVariable("modleid") int modleid, HttpServletRequest httpServletRequest) {

        JSONObject jsonObject = new JSONObject();
        if(!(httpServletRequest instanceof MultipartHttpServletRequest)){
            jsonObject.put("msg", "error");
            return jsonObject.toJSONString();
        }
        //转化成MultipartHttpServletRequest
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) httpServletRequest;
        //表单除了文件外的参数
        Map<String, String[]> parameterMap = multipartHttpServletRequest.getParameterMap();
        //表单上传的文件
        MultiValueMap<String, MultipartFile> multiValueMap = multipartHttpServletRequest.getMultiFileMap();



        AlgorithmModle algorithmModle = aiModleConstainer.getModulepool().get(modleid);
        if (algorithmModle == null) {
            jsonObject.put("msg", "failed");
            return jsonObject.toJSONString();
        }



        for (Map.Entry<String, List<MultipartFile>> fileentry : multiValueMap.entrySet()) {
            List<Byte> tempstore = new ArrayList<>();
            DataInputStream dataInputStream = null;
            try {

                String filename=fileentry.getKey();
                List<MultipartFile> subfiles =fileentry.getValue();
                dataInputStream = new DataInputStream(new BufferedInputStream(subfiles.get(0).getInputStream()));
                while (dataInputStream.available() != 0) {
                    tempstore.add(dataInputStream.readByte());
                }
                if (tempstore.size() >= 0) {
                    PictureJPG picture = new PictureJPG();
                    byte[] picbyyes = new byte[tempstore.size()];

                    for (int index = 0; index < tempstore.size(); index++) {
                        picbyyes[index] = tempstore.get(index);
                    }

                    picture.setData(picbyyes);
                    picture.setInstant(Instant.now());


                    for (AlgorithmProperty algorithmProperty : algorithmModle.getAlgorithmProperties()) {

                        if (algorithmProperty.getProperty().equals(filename)) {
                            algorithmProperty.setPictureJPG(picture);
                        }
                    }

                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            } finally {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        jsonObject.put("msg", "success");
        return jsonObject.toJSONString();
    }


}

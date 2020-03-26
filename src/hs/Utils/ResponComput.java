package hs.Utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/19 11:46
 */
public class ResponComput {
    public static Double[] responTwoTimeSeries(String json) {
        if (json==null||json.trim().equals("")){
            return null;
        }
        //String json = "{'1':{'k':1.6,'wn':1,'zata':0.16,'tao':1},'2':{'k':1.6,'wn':1,'zata':0.16,'tao':1},'T':100}";
        //new {'k':1.6,'wn':1,'zata':0.16,'tao':1,'T':100}
        JSONObject json_test = JSON.parseObject(json);
        int periodT = json_test.getInteger("T");

        int sampe_step=json_test.getInteger("S");
        Double[] respon = new Double[periodT/sampe_step];
        //float delta=1f;
        Double Wdi = Math.sqrt(1 - Math.pow(json_test.getFloat("zata"), 2)) * json_test.getFloat("wn");
        for (int i = 0; i < periodT; i++) {
            if (i < json_test.getFloat("tao")) {
                if((i%sampe_step)==0) {
                    respon[i/sampe_step] = 0d;
                }
                continue;
            }
            if((i%sampe_step)==0) {
                double temp_e = (Math.exp(-1 * json_test.getFloat("wn") * json_test.getFloat("zata") * ((i + 1) - json_test.getFloat("tao")))) / Math.sqrt(1 - Math.pow(json_test.getFloat("zata"), 2));
                double temp_sin = Math.sin(Wdi * ((i + 1) - json_test.getFloat("tao")) + Math.atan(Math.sqrt(1 - Math.pow(json_test.getFloat("zata"), 2)) / json_test.getFloat("zata")));
                respon[i/sampe_step] = json_test.getFloat("k") * (1 - temp_e * temp_sin);
            }

         }

        return respon;
    }



    public static Double[] responOneTimeSeries(String json) {
        if (json==null||json.trim().equals("")){
            return null;
        }
        //String json = "{'1':{'k':1.6,'wn':1,'zata':0.16,'tao':1},'2':{'k':1.6,'wn':1,'zata':0.16,'tao':1},'T':100}";
        //new {'k':1.6,'t':1,'tao':0.16,'T':100}
        JSONObject json_test = JSON.parseObject(json);
        int periodT = json_test.getInteger("T");
        int sampe_step=json_test.getInteger("S");

        Double[] respon = new Double[periodT/sampe_step];
        //float delta=1f;

        for (int i = 0; i < periodT; i++) {
            if (i < json_test.getFloat("tao")) {
                if((i%sampe_step)==0){
                    respon[i/sampe_step] = 0d;
                }

                continue;
            }
            if((i%sampe_step)==0){
                if (i/sampe_step>=periodT/sampe_step){
                    break;
                }
                respon[i/sampe_step] =json_test.getFloat("k")*(1-Math.exp(-(i-json_test.getFloat("tao"))/json_test.getFloat("t")));
            }
        }

        return respon;
    }

    public static int getSampleStep(String json){
        if (json==null||json.trim().equals("")){
            return 0;
        }
        //String json = "{'1':{'k':1.6,'wn':1,'zata':0.16,'tao':1},'2':{'k':1.6,'wn':1,'zata':0.16,'tao':1},'T':100}";
        //new {'k':1.6,'t':1,'tao':0.16,'T':100}
        JSONObject json_test = JSON.parseObject(json);
        return json_test.getInteger("S");
    }



}

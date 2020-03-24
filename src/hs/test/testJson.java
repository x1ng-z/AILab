package hs.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import hs.Configuartion.SpringAnnotationConfigure;
import hs.Opc.OPCService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.*;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/19 10:41
 */
public class testJson {

    public static void main(String[] args) {
        System.out.println(Math.exp(2));;
        AnnotationConfigApplicationContext applicationContext =new AnnotationConfigApplicationContext(SpringAnnotationConfigure.class);


        OPCService server=applicationContext.getBean(OPCService.class);
        server.run();
        Set<Integer> ss=new LinkedHashSet<>();
        ss.add(2);
        ss.add(1);
        ss.add(1);
        ss.add(1);

        System.out.println(new ArrayList<Integer>(ss));


        Map<String,Double[]> responsmap=new HashMap<String,Double[]>();
        String json = "{'1':{'k':1.6,'wn':1,'zata':0.16,'tao':1},'2':{'k':1.6,'wn':1,'zata':0.16,'tao':1},'T':100}";
        JSONObject json_test = JSON.parseObject(json);
        int periodT=json_test.getInteger("T");
        for(String key:json_test.keySet()){
            if(key!="T"){
                JSONObject modlemath =json_test.getJSONObject(key);
                Double[] respon=new Double[periodT];

                //float delta=1f;
                Double Wdi= Math.sqrt(1 - Math.pow(modlemath.getFloat("zata"), 2)) * modlemath.getFloat("wn");
                for(int i=0;i<periodT;i++){
                    if(i<modlemath.getFloat("tao")){
                        respon[i]=0d;
                        continue;
                    }
                    double temp_e=(Math.exp(-1 * modlemath.getFloat("wn") * modlemath.getFloat("zata") * ((i+1) -modlemath.getFloat("tao")))) / Math.sqrt(1-Math.pow(modlemath.getFloat("zata"),2));
                    double temp_sin=Math.sin(Wdi * ((i+1) -modlemath.getFloat("tao")) + Math.atan(Math.sqrt(1-Math.pow(modlemath.getFloat("zata"),2)) / modlemath.getFloat("zata")));
                    respon[i]=modlemath.getInteger("k")*(1-temp_e*temp_sin);
                }
                responsmap.put(key,respon);

            }

        }

        new JSONObject();
        System.out.println(json_test);
    }
}

package hs.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.ApcAlgorithm.ExecutePythonBridge;
import hs.Configuartion.SpringAnnotationConfigure;
import hs.Opc.OPCService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/19 10:41
 */
public class testJson {

    public static void main(String[] args) {


        ExecutePythonBridge executePythonBridge=new ExecutePythonBridge(
                "E:\\project\\2020_Project\\MPC\\Model\\test.py",
                "http://localhost:8080/python/modlebuild/"+1+".do","1");

            executePythonBridge.execute();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executePythonBridge.stop();
//        for(JSONObject jsonObject1:jsonObject.values()){
//
//        }

        //ffresp: [{"ff":"ff1","pv7":"","pv6":"","pv8":"","pv1":"{\"k\":10,\"t\":180,\"tao\":200}","pv3":"","pv2":"","pv5":"","pv4":"","LAY_TABLE_INDEX":0},{"ff":"ff2","pv7":"","pv6":"","pv8":"","pv1":"","pv3":"","pv2":"","pv5":"","pv4":"","LAY_TABLE_INDEX":1},{"ff":"ff3","pv7":"","pv6":"","pv8":"","pv1":"","pv3":"","pv2":"","pv5":"","pv4":"","LAY_TABLE_INDEX":2},{"ff":"ff4","pv7":"","pv6":"","pv8":"","pv1":"","pv3":"","pv2":"","pv5":"","pv4":"","LAY_TABLE_INDEX":3},{"ff":"ff5","pv7":"","pv6":"","pv8":"","pv1":"","pv3":"","pv2":"","pv5":"","pv4":"","LAY_TABLE_INDEX":4},{"ff":"ff6","pv7":"","pv6":"","pv8":"","pv1":"","pv3":"","pv2":"","pv5":"","pv4":"","LAY_TABLE_INDEX":5},{"ff":"ff7","pv7":"","pv6":"","pv8":"","pv1":"","pv3":"","pv2":"","pv5":"","pv4":"","LAY_TABLE_INDEX":6},{"ff":"ff8","pv7":"","pv6":"","pv8":"","pv1":"","pv3":"","pv2":"","pv5":"","pv4":"","LAY_TABLE_INDEX":7}]

//        Map<String,Double[]> responsmap=new HashMap<String,Double[]>();
//        String json = "{k:1.6,wn:1,zata:0.16,tao:1}";//"{'1':{'k':1.6,'wn':1,'zata':0.16,'tao':1},'2':{'k':1.6,'wn':1,'zata':0.16,'tao':1},'T':100}";
//        JSONObject json_test = JSON.parseObject(json);
//        int periodT=json_test.getInteger("T");
//        for(String key:json_test.keySet()){
//            if(key!="T"){
//                JSONObject modlemath =json_test.getJSONObject(key);
//                Double[] respon=new Double[periodT];
//
//                //float delta=1f;
//                Double Wdi= Math.sqrt(1 - Math.pow(modlemath.getFloat("zata"), 2)) * modlemath.getFloat("wn");
//                for(int i=0;i<periodT;i++){
//                    if(i<modlemath.getFloat("tao")){
//                        respon[i]=0d;
//                        continue;
//                    }
//                    double temp_e=(Math.exp(-1 * modlemath.getFloat("wn") * modlemath.getFloat("zata") * ((i+1) -modlemath.getFloat("tao")))) / Math.sqrt(1-Math.pow(modlemath.getFloat("zata"),2));
//                    double temp_sin=Math.sin(Wdi * ((i+1) -modlemath.getFloat("tao")) + Math.atan(Math.sqrt(1-Math.pow(modlemath.getFloat("zata"),2)) / modlemath.getFloat("zata")));
//                    respon[i]=modlemath.getInteger("k")*(1-temp_e*temp_sin);
//                }
//                responsmap.put(key,respon);
//
//            }
//
//        }
//
//        new JSONObject();
//        System.out.println(json_test);
    }
}

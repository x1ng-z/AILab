package hs.Utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.Bean.Algorithm.AlgorithmProperty;
import hs.Bean.ModlePin;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/6/16 13:21
 */
public class Tool {

    /**
     *数据保留位数设置函数
     * */
    public static double getSpecalScale(int scaledouble ,double value){
        BigDecimal a = new BigDecimal(value);
      return a.setScale(scaledouble,BigDecimal.ROUND_UP).doubleValue();
    }


    /**
     * @param count 所有的opc服务器的数量
     * @param datas 本页opc服务器信息
     *
     * */
    public static JSONObject sendLayuiPage(int count,JSONArray datas){
        JSONObject result = new JSONObject();
        result.put("code", 0);
        result.put("msg", "");
        result.put("count", count);
        result.put("data", datas);
        return result;
    }



    public static List<Integer> getunUserPinScope(Pattern pvpattern,List<ModlePin> usepinscope,int maxindex){

        List<Integer> usepvpinindex=new LinkedList<>();
        List<Integer> allpinindex=new LinkedList<>();


        for(ModlePin usepin:usepinscope){
            Matcher matcher=pvpattern.matcher(usepin.getModlePinName());
            if(matcher.find()){
                usepvpinindex.add(Integer.parseInt(matcher.group(2)));
            }
        }

        for(int indexpv=1;indexpv<=maxindex;indexpv++){
            allpinindex.add(indexpv);
        }

        allpinindex.removeAll(usepvpinindex);
        return allpinindex;
    }


    public  static int getPinindex(Pattern pattern,ModlePin pin){
        int pvpinscope = -1;
        Matcher matcher = pattern.matcher(pin.getModlePinName());
        if (matcher.find()) {
            pvpinscope = Integer.parseInt(matcher.group(2));
        }
        return pvpinscope;
    }

}

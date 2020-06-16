package hs.Utils;

import java.math.BigDecimal;

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

}

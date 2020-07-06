package hs.Bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/31 11:00
 */
public class ResponTimeSerise {
    private int modletagId;//模型
    private int refrencemodleId;//模型id
    private String stepRespJson;//响应的json
    private String inputPins;//引脚名称ff mv
    private String outputPins;//引脚名称pv

    /**
     *  * timeserise_N = 40;//响应序列长度
     *  * controlAPCOutCycle = 0;//控制输出间隔周期
     * 二阶时序*/
    public Double[] responTwoTimeSeries(Integer timeserise_N ,Integer controlAPCOutCycle) {
        String json = stepRespJson;
        if (json == null || json.trim().equals("")) {
            return null;
        }

        JSONObject json_test = JSON.parseObject(json);

        Double[] respon = new Double[timeserise_N];
        //float delta=1f;
        Double Wdi = Math.sqrt(1 - Math.pow(json_test.getFloat("zata"), 2)) * json_test.getFloat("wn");
        for (int i = 0; i < timeserise_N; i++) {
            if (i*controlAPCOutCycle < json_test.getFloat("tao")) {
                    respon[i] = 0d;
                continue;
            }

            double temp_e = (Math.exp(-1 * json_test.getFloat("wn") * json_test.getFloat("zata") * ((i *controlAPCOutCycle) - json_test.getFloat("tao")))) / Math.sqrt(1 - Math.pow(json_test.getFloat("zata"), 2));
            double temp_sin = Math.sin(Wdi * ((i *controlAPCOutCycle) - json_test.getFloat("tao")) + Math.atan(Math.sqrt(1 - Math.pow(json_test.getFloat("zata"), 2)) / json_test.getFloat("zata")));
            respon[i] = json_test.getFloat("k") * (1 - temp_e * temp_sin);

        }

        return respon;
    }



    /**
     * 一阶
     *
     * timeserise_N = 40;//响应序列长度
     * controlAPCOutCycle = 0;//控制输出间隔周期
     * */
    public Double[] responOneTimeSeries(int timeserise_N, int controlAPCOutCycle) {
        String json = stepRespJson;
        if (json == null || json.trim().equals("")) {
            return null;
        }
        JSONObject jsonmodlerespon = JSON.parseObject(json);
        Double[] respon = new Double[timeserise_N];
        /**
         * 原先是从0开始的到timeserise_N，但是我们要的事从k+1到 N+1
         * */
        for (int i = 0; i < (timeserise_N); i++) {
            if ((i+1)*controlAPCOutCycle < jsonmodlerespon.getFloat("tao")) {
                respon[i] = 0d;
                continue;
            }
            respon[i] = jsonmodlerespon.getFloat("k") * (1 - Math.exp(-(((i+1) * controlAPCOutCycle) - jsonmodlerespon.getFloat("tao")) / (jsonmodlerespon.getFloat("t")+0.0000001)));
        }
        return respon;
    }


    public int getModletagId() {
        return modletagId;
    }

    public void setModletagId(int modletagId) {
        this.modletagId = modletagId;
    }

    public int getRefrencemodleId() {
        return refrencemodleId;
    }

    public void setRefrencemodleId(int refrencemodleId) {
        this.refrencemodleId = refrencemodleId;
    }

    public String getStepRespJson() {
        return stepRespJson;
    }

    public void setStepRespJson(String stepRespJson) {
        this.stepRespJson = stepRespJson;
    }

    public String getInputPins() {
        return inputPins;
    }

    public void setInputPins(String inputPins) {
        this.inputPins = inputPins;
    }

    public String getOutputPins() {
        return outputPins;
    }

    public void setOutputPins(String outputPins) {
        this.outputPins = outputPins;
    }
}

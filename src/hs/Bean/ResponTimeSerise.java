package hs.Bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/31 11:00
 */
public class ResponTimeSerise {
    private int modletagId;
    private int refrencemodleId;
    private String stepRespJson;
    private String inputPins;
    private String outputPins;


    /**
     * private Integer timeserise_N = 40;//响应序列长度
     * private Integer controlAPCOutCycle = 0;//控制周期
     */
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
     * private Integer timeserise_N = 40;//响应序列长度
     * private Integer controlAPCOutCycle = 0;//控制周期
     */
    public Double[] responOneTimeSeries(int timeserise_N, int controlAPCOutCycle) {
        String json = stepRespJson;
        if (json == null || json.trim().equals("")) {
            return null;
        }
        JSONObject json_test = JSON.parseObject(json);
        Double[] respon = new Double[timeserise_N];
        for (int i = 0; i < timeserise_N; i++) {
            if (i*controlAPCOutCycle < json_test.getFloat("tao")) {
                respon[i] = 0d;
                continue;
            }

            respon[i] = json_test.getFloat("k") * (1 - Math.exp(-((i * controlAPCOutCycle) - json_test.getFloat("tao")) / json_test.getFloat("t")));

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
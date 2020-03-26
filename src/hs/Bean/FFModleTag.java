package hs.Bean;

import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/18 9:06
 */
public class FFModleTag implements ModleTag {
    private int sample_step;
    private int modletagId;//这个指示表示数据库那一行的id，和真正的tagid无关，tag在tagclazz这个里面
    private int modleId;
    private String iotype;
    private Tag pvTag;
    private String limitHigh;
    private String limitLow;
    private String limitHighRes;
    private String limitLowRes;
    private String stepRespJson;
    private Tag tagclazz;
    private Tag backValueTag;
    private Double[] responTimeSeries;


    @Override
    public int compareTo(ModleTag o) {
        if(this.tagclazz.getTagId()>o.getTagclazz().getTagId()){
            return 1;
        }else{
            if(this.tagclazz.getTagId()<o.getTagclazz().getTagId()){
                return -1;
            }else {
                return 0;
            }
        }
    }

    public int getModletagId() {
        return modletagId;
    }

    public void setModletagId(int modletagId) {
        this.modletagId = modletagId;
    }

    public int getModleId() {
        return modleId;
    }

    public void setModleId(int modleId) {
        this.modleId = modleId;
    }

    public String getIotype() {
        return iotype;
    }

    public void setIotype(String iotype) {
        this.iotype = iotype;
    }


    public String getLimitHigh() {
        return limitHigh;
    }

    public void setLimitHigh(String limitHigh) {
        this.limitHigh = limitHigh;
    }

    public String getLimitLow() {
        return limitLow;
    }

    public void setLimitLow(String limitLow) {
        this.limitLow = limitLow;
    }

    public String getLimitHighRes() {
        return limitHighRes;
    }

    public void setLimitHighRes(String limitHighRes) {
        this.limitHighRes = limitHighRes;
    }

    public String getLimitLowRes() {
        return limitLowRes;
    }

    public void setLimitLowRes(String limitLowRes) {
        this.limitLowRes = limitLowRes;
    }

    public String getStepRespJson() {
        return stepRespJson;
    }

    public void setStepRespJson(String stepRespJson) {
        this.stepRespJson = stepRespJson;
    }

    public Tag getTagclazz() {
        return tagclazz;
    }

    public void setTagclazz(Tag tagclazz) {
        this.tagclazz = tagclazz;
    }

    public Tag getPvTag() {
        return pvTag;
    }

    public void setPvTag(Tag pvTag) {
        this.pvTag = pvTag;
    }

    public Double[] getResponTimeSeries() {
        return responTimeSeries;
    }

    public void setResponTimeSeries(Double[] responTimeSeries) {
        this.responTimeSeries = responTimeSeries;
    }

    public Tag getBackValueTag() {
        return backValueTag;
    }

    public void setBackValueTag(Tag backValueTag) {
        this.backValueTag = backValueTag;
    }

    public int getSample_step() {
        return sample_step;
    }

    public void setSample_step(int sample_step) {
        this.sample_step = sample_step;
    }
}
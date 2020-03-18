package hs.Bean;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/18 8:44
 */
public class MVModleTag implements ModleTag {
    private int modletagId;

    private int modleId;
    private String iotype;
    private  Tag pvTag;
    private String limitHigh;
    private String limitLow;
    private String limitHighRes;
    private String limitLowRes;
    private String stepRespJson;
    private Tag tagclazz;

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
}

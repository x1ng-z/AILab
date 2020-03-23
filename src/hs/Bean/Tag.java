package hs.Bean;

import java.time.Instant;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/18 12:52
 */
public class Tag {
    private int tagId;
    private String TagName;
    private int refrencedeviceId;
    private String tag;
    private Instant updateTime;
    private Double newvalue=0d;
    private Double oldvalue=0d;
    private TagLimit maxlimit=null;
    private TagLimit minlimit=null;


    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return TagName;
    }

    public void setTagName(String tagName) {
        TagName = tagName;
    }

    public int getRefrencedeviceId() {
        return refrencedeviceId;
    }

    public void setRefrencedeviceId(int refrencedeviceId) {
        this.refrencedeviceId = refrencedeviceId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    public Double getNewvalue() {
        return newvalue;
    }

    public void setNewvalue(Double newvalue) {
        this.newvalue = newvalue;
    }

    public Double getOldvalue() {
        return oldvalue;
    }

    public void setOldvalue(Double oldvalue) {
        this.oldvalue = oldvalue;
    }
    public void updateValue(double value){
        oldvalue=newvalue;
        newvalue=value;
        updateTime=Instant.now();
    }

    public TagLimit getMaxlimit() {
        return maxlimit;
    }

    public void setMaxlimit(TagLimit maxlimit) {
        this.maxlimit = maxlimit;
    }

    public TagLimit getMinlimit() {
        return minlimit;
    }

    public void setMinlimit(TagLimit minlimit) {
        this.minlimit = minlimit;
    }
}

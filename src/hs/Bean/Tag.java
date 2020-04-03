package hs.Bean;

import java.time.Instant;
import java.util.LinkedList;


/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/18 12:52
 */
public class Tag {
    private int tagId;
    private String TagName;
    private String tag;
    private Instant updateTime;
    private Double newvalue=0d;
    private LinkedList<Double> oldvalueStack =new LinkedList<>();
    private int stackSize=100;
    private TagLimit maxlimit=null;
    private TagLimit minlimit=null;
    private int sampleStep;
    private String filterMethod;


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

    public double diffBetweenSample(){
        if(oldvalueStack.size()<sampleStep){
            return 0d;
        }
        return oldvalueStack.get(0)- oldvalueStack.get(sampleStep-1);
    }
    public void updateValue(double value){
        if(oldvalueStack.size()<stackSize){
            oldvalueStack.addFirst(newvalue);
        }else {
            oldvalueStack.removeLast();
        }

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

    public int getSampleStep() {
        return sampleStep;
    }

    public void setSampleStep(int sampleStep) {
        this.sampleStep = sampleStep;
    }

    public String getFilterMethod() {
        return filterMethod;
    }

    public void setFilterMethod(String filterMethod) {
        this.filterMethod = filterMethod;
    }
}

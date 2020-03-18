package hs.Bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/18 8:24
 */
public class ControlModle {
    private int modleId;
    private String modleName;
    private Map<String,FeedFordwardModleTag> feedForwards=new HashMap<>();//FF前馈feedforward
    private Map<String,MVModleTag> manVariables=new HashMap<>();//MV
    private Map<String,SPModleTag> setpointVariables=new HashMap<>();//SP

    private List<ModleTag> unhandleTag;


    public String getModleName() {
        return modleName;
    }

    public void setModleName(String modleName) {
        this.modleName = modleName;
    }

    public Map<String, FeedFordwardModleTag> getFeedForwards() {
        return feedForwards;
    }

    public void setFeedForwards(Map<String, FeedFordwardModleTag> feedForwards) {
        this.feedForwards = feedForwards;
    }

    public Map<String, MVModleTag> getManVariables() {
        return manVariables;
    }

    public void setManVariables(Map<String, MVModleTag> manVariables) {
        this.manVariables = manVariables;
    }

    public Map<String, SPModleTag> getSetpointVariables() {
        return setpointVariables;
    }

    public void setSetpointVariables(Map<String, SPModleTag> setpointVariables) {
        this.setpointVariables = setpointVariables;
    }

    public List<ModleTag> getUnhandleTag() {
        return unhandleTag;
    }

    public void setUnhandleTag(List<ModleTag> unhandleTag) {
        this.unhandleTag = unhandleTag;
    }

    public int getModleId() {
        return modleId;
    }

    public void setModleId(int modleId) {
        this.modleId = modleId;
    }

    public void realizeModle(){

        for(ModleTag tag:unhandleTag){
            if(tag instanceof FeedFordwardModleTag){
                feedForwards.put(tag.getTagclazz().getTag(),(FeedFordwardModleTag)tag);//FF前馈feedforward
            }
            if(tag instanceof MVModleTag){
                manVariables.put(tag.getTagclazz().getTag(),(MVModleTag)tag);//MV
            }
            if(tag instanceof SPModleTag){
                setpointVariables.put(tag.getTagclazz().getTag(),(SPModleTag)tag);//SP
            }

        }

    }
}

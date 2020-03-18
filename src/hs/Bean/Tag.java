package hs.Bean;

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
}

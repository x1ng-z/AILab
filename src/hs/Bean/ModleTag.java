package hs.Bean;

import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/18 8:28
 */
public interface ModleTag extends Comparable<ModleTag> {

    public int getModletagId() ;

    public void setModletagId(int modletagId) ;

    public int getModleId() ;

    public void setModleId(int modleId) ;

    public String getIotype() ;

    public void setIotype(String iotype) ;


    public Tag getPvTag() ;

    public void setPvTag(Tag pvTag) ;

    public String getLimitHigh() ;

    public void setLimitHigh(String limitHigh) ;
    public String getLimitLow() ;

    public void setLimitLow(String limitLow);

    public String getLimitHighRes() ;

    public void setLimitHighRes(String limitHighRes) ;

    public String getLimitLowRes() ;

    public void setLimitLowRes(String limitLowRes) ;

    public String getStepRespJson();

    public void setStepRespJson(String stepRespJson) ;
    public Tag getTagclazz() ;

    public void setTagclazz(Tag tagclazz) ;


    public Double[] getResponTimeSeries();

    public void setResponTimeSeries(Double[] responTimeSeries);

    public Tag getBackValueTag() ;

    public void setBackValueTag(Tag backValueTag) ;

}

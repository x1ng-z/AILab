package hs.Filter;

import hs.Bean.OPCComponent;
import hs.ShockDetect.ShockDetector;

import java.util.LinkedList;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/6/10 17:35
 */
public interface Filter extends OPCComponent {
    void setsampledata(double sampledata);

    int getPk_pinid();

    void setPk_pinid(int pk_pinid);

    public int getPk_filterid();

    public void setPk_filterid(int pk_filterid);

    public Double getLastfilterdata();

    void putDataTofilterdatas(double data);

    public String getBackToDCSTag();

    public void setBackToDCSTag(String backToDCSTag);

    public String getFiltername();

    public void setFiltername(String filtername);

    public Double getcoeff();


    public String getOpcresource() ;

    public void setOpcresource(String opcresource) ;

}

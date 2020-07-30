package hs.ShockDetect;

import hs.Controller.ModleController;
import hs.Filter.FirstOrderLagFilter;
import org.apache.log4j.Logger;
import org.openscada.opc.lib.da.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/7/20 13:28
 */
public class ShockDetector {
    public static Logger logger = Logger.getLogger(ShockDetector.class);

    private int pk_shockdetectid ;
    private  int pk_pinid;//'引用的过滤器主键id',
    private String backToDCSTag;//'有效幅值计算结果反写位号',
    private String opcresource;//'opc反写位号源',
    private Double dampcoeff;//'动态阻尼系数
    private int windowstime;//'窗口时间',
    private Double filtercoeff;//'一阶滤波系数',
    private int enable;//'是否启用'
    private String filterbacktodcstag; //'滤波后数据反写位号',
    private String filteropcresource; // '滤波数据反写位号opc源'
    private double lowhzA=0;//低频率幅值
    private FirstOrderLagFilter firstOrderLagFilterl;


    public void componentrealize(){
        firstOrderLagFilterl=new FirstOrderLagFilter();
        firstOrderLagFilterl.setFilter_alphe(filtercoeff);
        firstOrderLagFilterl.setCapacity(windowstime);
        firstOrderLagFilterl.setOpcresource(filteropcresource);
        firstOrderLagFilterl.setBackToDCSTag(filterbacktodcstag);
    }


    public ShockTask generShockTask(){
        if(firstOrderLagFilterl!=null&&firstOrderLagFilterl.getFilterdataspool().size()>=windowstime){
            ShockTask shockTask=new ShockTask(this);
            logger.info("filter size="+firstOrderLagFilterl.getFilterdataspool().size()+"windown size="+windowstime);
            return shockTask;
        }else {
            logger.info("filter size="+firstOrderLagFilterl.getFilterdataspool().size()+"windown size="+windowstime);
        }
        return null;
    }

    public List<Double> getFiltdata(){
        Double[] temp=new Double[firstOrderLagFilterl.getFilterdataspool().size()];
        temp=firstOrderLagFilterl.getFilterdataspool().toArray(temp);
        List<Double> filtdata=new ArrayList<>();
        for(int index=0;index<temp.length;index++){
            filtdata.add(temp[index]);
        }
        return filtdata;
    }


    public int getPk_shockdetectid() {
        return pk_shockdetectid;
    }

    public void setPk_shockdetectid(int pk_shockdetectid) {
        this.pk_shockdetectid = pk_shockdetectid;
    }

    public int getPk_pinid() {
        return pk_pinid;
    }

    public void setPk_pinid(int pk_pinid) {
        this.pk_pinid = pk_pinid;
    }

    public String getBackToDCSTag() {
        return backToDCSTag;
    }

    public void setBackToDCSTag(String backToDCSTag) {
        this.backToDCSTag = backToDCSTag;
    }

    public String getOpcresource() {
        return opcresource;
    }

    public void setOpcresource(String opcresource) {
        this.opcresource = opcresource;
    }

    public Double getDampcoeff() {
        return dampcoeff;
    }

    public void setDampcoeff(Double dampcoeff) {
        this.dampcoeff = dampcoeff;
    }

    public int getWindowstime() {
        return windowstime;
    }

    public void setWindowstime(int windowstime) {
        this.windowstime = windowstime;
    }

    public Double getFiltercoeff() {
        return filtercoeff;
    }

    public void setFiltercoeff(Double filtercoeff) {
        this.filtercoeff = filtercoeff;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public double getLowhzA() {
        return lowhzA;
    }

    public void setLowhzA(double lowhzA) {
        this.lowhzA = lowhzA;
    }

    public String getFilterbacktodcstag() {
        return filterbacktodcstag;
    }

    public void setFilterbacktodcstag(String filterbacktodcstag) {
        this.filterbacktodcstag = filterbacktodcstag;
    }

    public String getFilteropcresource() {
        return filteropcresource;
    }

    public void setFilteropcresource(String filteropcresource) {
        this.filteropcresource = filteropcresource;
    }

    public FirstOrderLagFilter getFirstOrderLagFilterl() {
        return firstOrderLagFilterl;
    }

}

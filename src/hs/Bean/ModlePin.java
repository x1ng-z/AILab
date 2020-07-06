package hs.Bean;

import hs.Filter.Filter;

import java.time.Instant;
import java.util.LinkedList;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/30 12:28
 */
public class ModlePin {
    public final static String TYPE_PIN_PV="pv";
    public final static String TYPE_PIN_SP="sp";
    public final static String TYPE_PIN_MV="mv";
    public final static String TYPE_PIN_MVFB="mvfb";
    public final static String TYPE_PIN_FF="ff";
    public final static String TYPE_PIN_AUTO="auto";
    public final static String TYPE_PIN_FFDOWN="ffdown";
    public final static String TYPE_PIN_FFUP="ffup";
    public final static String TYPE_PIN_MVUP="mvup";
    public final static String TYPE_PIN_MVDOWN="mvdown";
    private int modlepinsId;
    private int reference_modleId;
    private String modleOpcTag;
    private String modlePinName;//引脚名称 pv1,sp1...
    private String opcTagName;
    private String resource="";
    private ModlePin upLmt;//高限
    private ModlePin downLmt;//低限
    private ModlePin feedBack;//反馈
    private Double Q;
    private Double R;
    private Double deadZone;//死区时间
    private Double funelinitValue;//漏洞初始值
    private String funneltype;
    private Double writeValue;
    private Double newReadValue;//opc更新的新值
    private Double oldReadValue;//opc更新旧值
    private Double lastTimeValue;//上一次客户端算法进行读取
    private Instant updateOpcTime;
    private LinkedList<Double> oldvalueStack = new LinkedList<>();
    private int stackSize = 100;
    private Boolean isFristTimeModle =true;
    private Double dmvHigh;
    private Double dmvLow;
    private Filter filter=null;//滤波器
    private Double referTrajectoryCoef;//pv的柔化系数(参考轨迹参数)


    public void opcUpdateValue(double value) {
        oldReadValue = newReadValue;
        newReadValue = value;
        updateOpcTime = Instant.now();
    }

    public double modleGetReal() {
        //有过滤器吗，有就充过滤器中获取，没有就直接冲
        if(filter==null){
            return  (newReadValue==null?0:newReadValue);
        }else {
            return filter.getLastfilterdata();
        }
    }


    public int getModlepinsId() {
        return modlepinsId;
    }

    public void setModlepinsId(int modlepinsId) {
        this.modlepinsId = modlepinsId;
    }

    public int getReference_modleId() {
        return reference_modleId;
    }

    public void setReference_modleId(int reference_modleId) {
        this.reference_modleId = reference_modleId;
    }

    public String getModleOpcTag() {
        return modleOpcTag;
    }

    public void setModleOpcTag(String modleOpcTag) {
        this.modleOpcTag = modleOpcTag;
    }

    public String getModlePinName() {
        return modlePinName;
    }

    public void setModlePinName(String modlePinName) {
        this.modlePinName = modlePinName;
    }

    public String getOpcTagName() {
        return opcTagName;
    }

    public void setOpcTagName(String opcTagName) {
        this.opcTagName = opcTagName;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public ModlePin getUpLmt() {
        return upLmt;
    }

    public void setUpLmt(ModlePin upLmt) {
        this.upLmt = upLmt;
    }

    public ModlePin getDownLmt() {
        return downLmt;
    }

    public void setDownLmt(ModlePin downLmt) {
        this.downLmt = downLmt;
    }

    public ModlePin getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(ModlePin feedBack) {
        this.feedBack = feedBack;
    }


    public Double getOldReadValue() {
        return oldReadValue;
    }

    public void setOldReadValue(Double oldReadValue) {
        this.oldReadValue = oldReadValue;
    }

    public Instant getUpdateOpcTime() {
        return updateOpcTime;
    }

    public void setUpdateOpcTime(Instant updateOpcTime) {
        this.updateOpcTime = updateOpcTime;
    }

    public Double getQ() {
        return Q;
    }

    public void setQ(Double q) {
        Q = q;
    }

    public Double getR() {
        return R;
    }

    public void setR(Double r) {
        R = r;
    }

    public Double getDeadZone() {
        return deadZone;
    }

    public void setDeadZone(Double deadZone) {
        this.deadZone = deadZone;
    }

    public Double getFunelinitValue() {
        return funelinitValue;
    }

    public void setFunelinitValue(Double funelinitValue) {
        this.funelinitValue = funelinitValue;
    }

    public Double getWriteValue() {
        return writeValue==null?0:writeValue;
    }

    public void setWriteValue(Double writeValue) {
        this.writeValue = writeValue;
    }

    public Double getDmvHigh() {
        return dmvHigh;
    }

    public void setDmvHigh(Double dmvHigh) {
        this.dmvHigh = dmvHigh;
    }

    public Double getDmvLow() {
        return dmvLow;
    }

    public void setDmvLow(Double dmvLow) {
        this.dmvLow = dmvLow;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Double getReferTrajectoryCoef() {
        return referTrajectoryCoef;
    }

    public void setReferTrajectoryCoef(Double referTrajectoryCoef) {
        this.referTrajectoryCoef = referTrajectoryCoef;
    }

    public String getFunneltype() {
        return funneltype;
    }

    public void setFunneltype(String funneltype) {
        this.funneltype = funneltype;
    }
}

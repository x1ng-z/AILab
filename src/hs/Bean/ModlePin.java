package hs.Bean;

import java.time.Instant;
import java.util.LinkedList;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/30 12:28
 */
public class ModlePin {
    private int modlepinsId;
    private int reference_modleId;
    private String modleOpcTag;
    private String filterMethod;
    private String modlePinName;//引脚名称
    private String opcTagName;
    private String resource;
    private ModlePin upLmt;
    private ModlePin downLmt;
    private ModlePin feedBack;
    private Double Q;
    private Double R;
    private Double deadZone;//死区时间
    private Double funelinitValue;//漏洞初始值

    private Double newValue;//opc更新的新值
    private Double oldValue;//opc更新旧值
    private Double lastTimeValue;//上一次客户端算法进行读取
    private Instant updateOpcTime;
    private LinkedList<Double> oldvalueStack = new LinkedList<>();
    private int stackSize = 100;
    private Boolean isFristTimeModle =true;


    public void opcUpdateValue(double value) {
//        if(oldvalueStack.size()<stackSize){
//            oldvalueStack.addFirst(newvalue);
//        }else {
//            oldvalueStack.removeLast();
//        }
        oldValue = newValue;
        newValue = value;
        updateOpcTime = Instant.now();
    }

    public double modleGetReal() {
        return newValue;
    }

    public double modleGetDiff() {
        Double diffif=0d;
        if(!isFristTimeModle){
            diffif= newValue - lastTimeValue;
            lastTimeValue = newValue;

        }else{
            lastTimeValue=newValue;
            isFristTimeModle =false;

        }
       return diffif;
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

    public String getFilterMethod() {
        return filterMethod;
    }

    public void setFilterMethod(String filterMethod) {
        this.filterMethod = filterMethod;
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


    public Double getOldValue() {
        return oldValue;
    }

    public void setOldValue(Double oldValue) {
        this.oldValue = oldValue;
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
}

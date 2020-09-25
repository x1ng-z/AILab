package hs.Bean;

import hs.Filter.Filter;
import hs.ShockDetect.ShockDetector;

import java.time.Instant;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/30 12:28
 */
public class ModlePin implements ModleProperty {
    private static Pattern pvenablepattern = Pattern.compile("(^pvenable\\d+$)");
    public final static String TYPE_PIN_PV = "pv";
    public final static String TYPE_PIN_PVDOWN = "pvdown";
    public final static String TYPE_PIN_PVUP = "pvup";
    public final static String TYPE_PIN_SP = "sp";
    public final static String TYPE_PIN_MV = "mv";
    public final static String TYPE_PIN_MVFB = "mvfb";
    public final static String TYPE_PIN_FF = "ff";
    public final static String TYPE_PIN_MODLE_AUTO = "auto";//模型整体是否进行运行
    public final static String TYPE_PIN_FFDOWN = "ffdown";
    public final static String TYPE_PIN_FFUP = "ffup";
    public final static String TYPE_PIN_MVUP = "mvup";
    public final static String TYPE_PIN_MVDOWN = "mvdown";
    public final static String TYPE_PIN_PIN_PVENABLE = "pvenable";//pv引脚是否启用,控制方为dcs
    public final static String TYPE_PIN_PIN_FFENABLE = "ffenable";//ff引脚是否启用,控制方为dcs
    public final static String TYPE_PIN_PIN_MVENABLE = "mvenable";//mv引脚是否启用,控制方为dcs
    public final static String SOURCE_TYPE_CONSTANT = "constant";
    public final static String TYPE_FUNNEL_FULL = "fullfunnel";
    public final static String TYPE_FUNNEL_UP = "upfunnel";
    public final static String TYPE_FUNNEL_DOWN = "downfunnel";


    private int modlepinsId;
    private int reference_modleId;
    /**
     * 引脚使能位，一般用于pv，判断pv是否启用
     * 标志位引脚是否参与控制
     */
    private volatile int pinEnable = 1;

    /**
     * opc位号
     */
    private String modleOpcTag;
    /**
     * 引脚名称 pv1,sp1...
     */
    private String modlePinName;
    /**
     * 中文注释
     */
    private String pintype;
    private String opcTagName;
    private String resource = "";
    private ModlePin upLmt;//高限
    private ModlePin downLmt;//低限
    private ModlePin feedBack;//反馈
    private Double Q;
    private Double R;
    private Double deadZone;//死区时间
    private Double funelinitValue;//漏洞初始值
    private String funneltype;//漏斗类型
    private Double writeValue;//写入的值
    private Double newReadValue;//opc更新的新值
    private Double oldReadValue;//opc更新旧值
    private Double lastTimeValue;//上一次客户端算法进行读取
    private Instant updateOpcTime;
    private LinkedList<Double> oldvalueStack = new LinkedList<>();
    private int stackSize = 100;
    private Boolean isFristTimeModle = true;
    private Double dmvHigh;
    private Double dmvLow;
    private Filter filter = null;//滤波器
    private Double referTrajectoryCoef;//pv的柔化系数(参考轨迹参数)
    private ShockDetector shockDetector;
    private Instant updateTime;


    /**运行时钟，用于判断引脚是否进行run*/
    private Instant runClock;




    /**由于计算环境的变化，有些不在置信区间内的引脚需要移除控制，
     * 改标识符是用于查看现在引脚是否参与控制
     * */
    private volatile boolean thisTimeParticipate=true;
    /**
     * dcs端控制引脚是否切入控制
     */
    private ModlePin dcsEnabePin;


    public void opcUpdateValue(double value) {
        oldReadValue = newReadValue;
        newReadValue = value;
        updateOpcTime = Instant.now();
    }

    /**
     * 1实数直接将opctag转换为实数
     * 2有滤波器直接进行取滤波后的值
     * 3无滤波直接获取opcserve更新过来的newreadvalue
     * 脚使能数据默认为1
     * 非脚使能数据默认数据为0
     **/
    public double modleGetReal() {

        /**常量数据直接转换提起就行*/
        if (resource.equals(SOURCE_TYPE_CONSTANT)) {
            /***常数*/
            return Double.valueOf(modleOpcTag);
        }
        /**有过滤器吗，有就充过滤器中获取，没有就直接opc更新来的newvalue中获取值就行*/
        if (filter == null) {
            if (pvenablepattern.matcher(modlePinName).find()) {
                /**引脚使能类型的引脚 pin type :enablepin,no pv mv..
                 * 如果配置了opc位号*/
                if ((modleOpcTag != null) && (!modleOpcTag.equals(""))) {
                    return (newReadValue == null ? 1 : newReadValue);
                } else {
                    /**如果没有配置了opc位号*/
                    return 1;
                }
            } else {
                /***非引脚使能数据*/
                return (newReadValue == null ? 0 : newReadValue);
            }
        } else {
            //有滤波器
            Double filterresult = filter.getLastfilterdata();
            if (filterresult != null) {
                return filterresult;
            } else {
                return (newReadValue == null ? 0 : newReadValue);
            }
        }
    }

    /**
     * 判断是否超过的上下界限
     * @return true 突破了;false 没有突破
     * */
    public boolean isBreakLimit(){
        //判断是否超过置信区间
        boolean breaklow=((downLmt!=null)&&(oldReadValue!=null)&&(modleGetReal()<downLmt.modleGetReal()));
        boolean breakup=((upLmt!=null)&&(oldReadValue!=null)&&(modleGetReal()>upLmt.modleGetReal()));
        return (breaklow||breakup);
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
        return writeValue == null ? 0 : writeValue;
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

    public Double getNewReadValue() {
        return newReadValue;
    }

    public ShockDetector getShockDetector() {
        return shockDetector;
    }

    public void setShockDetector(ShockDetector shockDetector) {
        this.shockDetector = shockDetector;
    }

    public synchronized int getPinEnable() {
        return pinEnable;
    }

    public synchronized void setPinEnable(int pinEnable) {
        this.pinEnable = pinEnable;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    public ModlePin getDcsEnabePin() {
        return dcsEnabePin;
    }

    public void setDcsEnabePin(ModlePin dcsEnabePin) {
        this.dcsEnabePin = dcsEnabePin;
    }

    public synchronized boolean isThisTimeParticipate() {
        return thisTimeParticipate;
    }
    public synchronized void setThisTimeParticipate(boolean thisTimeParticipate) {
        this.thisTimeParticipate = thisTimeParticipate;
    }

    public Instant getRunClock() {
        return runClock;
    }

    public void setRunClock(Instant runClock) {
        this.runClock = runClock;
    }

    public void clearRunClock() {
        this.runClock = null;
    }

    /**
     * 检测引脚闹铃时间到了，可以就可以运行引脚了
     * */
    public boolean clockAlarm(){
        return Instant.now().isAfter(runClock);
    }

    @Override
    public String getOPCTAG() {
        return modleOpcTag;
    }

    public String getPintype() {
        return pintype;
    }

    public void setPintype(String pintype) {
        this.pintype = pintype;
    }
}

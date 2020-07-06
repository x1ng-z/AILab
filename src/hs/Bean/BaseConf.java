package hs.Bean;

/**
 *
 * mpv基础配置信息
 * @author zzx
 * @version 1.0
 * @date 2020/4/2 8:19
 */
public class BaseConf {
    private int ompanyId;//公司主键id
    private String commenName;//公司名称
    private int companyOrder;//无用
    private int ff;//前馈数量
    private int mv;//mv数量
    private int pv;//pv的数量


    public int getOmpanyId() {
        return ompanyId;
    }

    public void setOmpanyId(int ompanyId) {
        this.ompanyId = ompanyId;
    }

    public String getCommenName() {
        return commenName;
    }

    public void setCommenName(String commenName) {
        this.commenName = commenName;
    }

    public int getCompanyOrder() {
        return companyOrder;
    }

    public void setCompanyOrder(int companyOrder) {
        this.companyOrder = companyOrder;
    }

    public int getFf() {
        return ff;
    }

    public void setFf(int ff) {
        this.ff = ff;
    }

    public int getMv() {
        return mv;
    }

    public void setMv(int mv) {
        this.mv = mv;
    }

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }
}

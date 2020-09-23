package hs.ServiceBus;


import hs.Filter.Filter;
import hs.ShockDetect.ShockDetector;
import org.openscada.opc.lib.da.Item;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/6/12 13:47
 */
public class FiltTask{
    private Filter filter;
    private Double[] unfiltdatas;//用于移动平均
    private double unfiltdata;//用于一阶滤波
    private Item itemfilterback;//用于数据反写
    private ShockDetector shockDetector;
    private Item itemshockback;



    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Double[] getUnfiltdatas() {
        return unfiltdatas;
    }

    public void setUnfiltdatas(Double[] unfiltdatas) {
        this.unfiltdatas = unfiltdatas;
    }

    public double getUnfiltdata() {
        return unfiltdata;
    }

    public void setUnfiltdata(double unfiltdata) {
        this.unfiltdata = unfiltdata;
    }


    public Item getItemfilterback() {
        return itemfilterback;
    }

    public void setItemfilterback(Item itemfilterback) {
        this.itemfilterback = itemfilterback;
    }

    public ShockDetector getShockDetector() {
        return shockDetector;
    }

    public void setShockDetector(ShockDetector shockDetector) {
        this.shockDetector = shockDetector;
    }

    public Item getItemshockback() {
        return itemshockback;
    }

    public void setItemshockback(Item itemshockback) {
        this.itemshockback = itemshockback;
    }
}

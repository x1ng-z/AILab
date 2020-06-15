package hs.Filter;


import org.openscada.opc.lib.da.Item;

import java.util.LinkedList;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/6/12 13:47
 */
public class FiltTask{
    private Filter filter;
    private Double[] unfiltdatas;//用于移动平均
    private double unfiltdata;//用于一阶滤波
    private Item item;//用于数据反写


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


    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}

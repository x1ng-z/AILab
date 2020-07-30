package hs.Filter;

import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/6/12 8:01
 */
public class FirstOrderLagFilter implements Filter {
    private static final Logger logger = Logger.getLogger(FirstOrderLagFilter.class);

    private int pk_filterid;
    private int pk_pinid;
    private String filtername;
    private String backToDCSTag;//反写进dcs的位号
    private String opcresource;
    private Double filter_alphe;
    private ConcurrentLinkedQueue<Double> unfilterdatapool =new ConcurrentLinkedQueue();
    private ConcurrentLinkedQueue<Double> filterdataspool =new ConcurrentLinkedQueue();
    private Integer capacity=30;//固定值
    private int unfilterdatalength =0;//未滤波数据存入长度
    private int filterdatalength =0;//已滤波数据存入长度


    public Double getcoeff(){
        return filter_alphe;
    }


//    public FirstOrderLagFilter(double alphe) {
//        this.filter_alphe = alphe;
//    }

     public  void putDataTofilterdatas(double data){
        while (filterdatalength>capacity){
            filterdataspool.poll();
            filterdatalength--;
        }
        filterdataspool.offer(data);
        filterdatalength++;
    }

    /**
     * 仅仅是获取上一个滤波数据
     * */
    public  Double getLastfilterdata(){
        Double[] temp=new Double[filterdataspool.size()];
        temp=filterdataspool.toArray(temp);

        for(int i=0;i<temp.length;i++){
            if(temp[temp.length-1-i]!=null){
                return temp[temp.length-1-i];
            }
        }
        logger.error("滤波器工作失败！");
        return null;
    }


    /**
     * 将首个未滤波的数据同时交给unfilterdata和filterdata
     * */
    public  void setsampledata(double sampledata){
        while (unfilterdatalength >capacity){
            unfilterdatapool.poll();
            unfilterdatalength--;
        }
        unfilterdatapool.offer(sampledata);
        unfilterdatalength++;
        //第一个初始值线放到已滤波的列表中
        if(filterdatalength==0){
            putDataTofilterdatas(sampledata);
            filterdatalength++;
        }
    }


    /**
     * 一阶滤波
     * @param yn_1  上一次滤波值
     * @param xn    本次采集数值
     */
    public void FirstOrderLagfilter( double yn_1, double xn) {
        double yn = (1 - filter_alphe) * yn_1 + filter_alphe * xn;
        putDataTofilterdatas(yn);
    }




    public int getPk_filterid() {
        return pk_filterid;
    }

    public void setPk_filterid(int pk_filterid) {
        this.pk_filterid = pk_filterid;
    }

    public Double getFilter_alphe() {
        return filter_alphe;
    }

    public void setFilter_alphe(Double filter_alphe) {
        this.filter_alphe = filter_alphe;
    }

    public int getPk_pinid() {
        return pk_pinid;
    }

    public void setPk_pinid(int pk_pinid) {
        this.pk_pinid = pk_pinid;
    }

    public String getFiltername() {
        return filtername;
    }

    public void setFiltername(String filtername) {
        this.filtername = filtername;
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

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public ConcurrentLinkedQueue<Double> getUnfilterdatapool() {
        return unfilterdatapool;
    }

    public ConcurrentLinkedQueue<Double> getFilterdataspool() {
        return filterdataspool;
    }
}

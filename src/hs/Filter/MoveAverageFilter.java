package hs.Filter;

import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/6/12 8:02
 */
public class MoveAverageFilter implements Filter {
    private static final Logger logger = Logger.getLogger(MoveAverageFilter.class);
    private int pk_filterid;
    private int pk_pinid;
    private String backToDCSTag;//反写进dcs的位号
    private String opcresource;
    private ConcurrentLinkedQueue<Double> unfilterdatapool =new ConcurrentLinkedQueue();
    private ConcurrentLinkedQueue<Double> filterdataspool =new ConcurrentLinkedQueue();
    private Integer capacity;//队列容量移动平均滤波时间
    private int unfilterdatalength = 0;//未滤波数据存入长度
    private int filterdatalength = 0;//已滤波数据存入长度
    private String filtername;

//    public MoveAverageFilter(int datacapacity) {
//        this.capacity = datacapacity;
//    }

    public Double getcoeff(){
        if(capacity==null){
            return null;
        }
        return capacity.doubleValue();
    }

    public  Double getLastfilterdata(){
        Double[] temp=new Double[filterdataspool.size()];
        temp=filterdataspool.toArray(temp);

        for(int i=0;i<temp.length;i++){
            if(temp[temp.length-1-i]!=null){
                logger.debug("滤波器size="+filterdataspool.size()+"拾取位置"+(temp.length-1-i));
                return temp[temp.length-1-i];
            }
        }
        logger.error("滤波器工作失败！");
        return null;
    }

    public  void setsampledata(double sampledata) {
        while (unfilterdatalength > capacity) {
            unfilterdatapool.poll();
            --unfilterdatalength;
        }
        unfilterdatapool.offer(sampledata);
        unfilterdatalength++;
    }

    public  Double[] getUnfilterdatas(){
        Double[] temp=new Double[unfilterdatapool.size()];
        unfilterdatapool.toArray(temp);
        return temp;
    }


     public void putDataTofilterdatas(double data){
        while (filterdatalength>capacity){
            filterdataspool.poll();
            filterdatalength--;
        }
        filterdataspool.offer(data);
        filterdatalength++;
    }


    /**
     * 移动平均滤波
     *
     *             滤波时间(opc采集数据1秒一个值，所以就是N秒)。值越大滤波效果越好
     * @param unfilerdata 未进行滤波的数据
     */
    public void moveAveragefilter( Double[] unfilerdata) {
        if (unfilerdata.length < capacity) {
            putDataTofilterdatas(unfilerdata[unfilerdata.length-1]);
        } else {
            double sum = 0;
            for (Double udata : unfilerdata) {
                sum += udata;
            }
            putDataTofilterdatas(sum / capacity);
        }
    }


    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public int getPk_filterid() {
        return pk_filterid;
    }

    public void setPk_filterid(int pk_filterid) {
        this.pk_filterid = pk_filterid;
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
}

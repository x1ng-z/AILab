package hs.Filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import hs.Bean.ModlePin;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/6/12 15:38
 */
public class test {


    public static double prouctdata(int time){
        return 10*Math.sin(2*3.14*time/100)+Math.random()*10;
    }
    public static void main(String[] args) {
        double pi=3.1415;
        int N=2;
        double r=pi/N;
        int a=3/2;
         ConcurrentLinkedQueue<Double> unfilterdatapool =new ConcurrentLinkedQueue();
        unfilterdatapool.offer(1d);
        unfilterdatapool.offer(2d);
        Double[] temp=new Double[3];
        temp=unfilterdatapool.toArray(temp);
       for(int i=0;i<temp.length;i++){
           if(temp[temp.length-1-i]!=null){
               System.out.println(temp[temp.length-1-i]);
               break;
           }
       }



        JSONObject jsonObject = new JSONObject();


        System.out.println(jsonObject.toJSONString());;

        List<ModlePin> pins=null;

        for(ModlePin pin:pins){
            System.out.println(pin);

        }
        Filter filter =new FirstOrderLagFilter();
        if(filter instanceof MoveAverageFilter){
            System.out.println("yes");
        }


        FirstOrderLagFilter firstOrderLagFilter=new FirstOrderLagFilter();
        firstOrderLagFilter.setFilter_alphe(0.3);

        for(int i=0;i<100;i++){
            Double xn=prouctdata(i);
            firstOrderLagFilter.setsampledata(xn);
            firstOrderLagFilter.FirstOrderLagfilter(firstOrderLagFilter.getLastfilterdata(),xn);

        }






        MoveAverageFilter moveAverageFilter=new MoveAverageFilter();
        moveAverageFilter.setCapacity(5);



        for(int i=0;i<100;i++){

            moveAverageFilter.setsampledata(prouctdata(i));
            moveAverageFilter.moveAveragefilter(moveAverageFilter.getUnfilterdatas());

        }



    }
}

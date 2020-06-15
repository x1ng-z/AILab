package hs.Filter;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/6/12 15:38
 */
public class test {
    public static void main(String[] args) {
        Filter filter =new FirstOrderLagFilter();
        if(filter instanceof MoveAverageFilter){
            System.out.println("yes");
        }

        ConcurrentLinkedQueue<Double> unfilterdata =new ConcurrentLinkedQueue();
        unfilterdata.add(1d);
        unfilterdata.add(2d);
        unfilterdata.add(3d);
        unfilterdata.add(4d);

        Double[] a=new Double[1];
        a=unfilterdata.toArray(a);
        for(Double v:a){
            System.out.println(v);
        }

        unfilterdata.poll();


        Double[] b=new Double[1];
        b=unfilterdata.toArray(b);
        for(Double v:b){
            System.out.println(v);
        }

    }
}

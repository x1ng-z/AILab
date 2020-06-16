package hs.Filter;

import org.apache.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;


/**
 * @author zzx
 * @version 1.0
 * @date 2020/6/12 7:49
 */

@Component()
public class FilterService implements Runnable {
    private static final Logger logger = Logger.getLogger(FilterService.class);

    private LinkedBlockingQueue<FiltTask> filtetaskpool = new LinkedBlockingQueue<>();


    public void putfiltertask(FiltTask filtTask){
        filtetaskpool.offer(filtTask);
    }


    private ExecutorService exec = Executors.newFixedThreadPool(10,new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    });

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                FiltTask filtTask = filtetaskpool.take();
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        Filter filter = filtTask.getFilter();
                        if (filter instanceof FirstOrderLagFilter) {
                            //一阶滤波
                            FirstOrderLagFilter folf = (FirstOrderLagFilter) filter;
                            folf.FirstOrderLagfilter(folf.getLastfilterdata(), filtTask.getUnfiltdata());

                        } else if (filter instanceof MoveAverageFilter) {
                            //移动平均滤波
                            MoveAverageFilter mvav = (MoveAverageFilter) filter;
                            mvav.moveAveragefilter(filtTask.getUnfiltdatas());
                        }

                        if(filtTask.getItem()!=null){
                            try {
                                filtTask.getItem().write(new JIVariant(filter.getLastfilterdata(), false));
                            } catch (JIException e) {
                                logger.error(e);
                            }
                        }
                    }
                });

            } catch (InterruptedException e) {
                logger.error(e);
            }catch (Exception e){
                logger.error(e);
            }
        }

    }
}

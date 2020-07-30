package hs.Filter;

import hs.ShockDetect.ShockTask;
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


    public void putfiltertask(FiltTask filtTask) {
        filtetaskpool.offer(filtTask);
    }


    private ExecutorService exec = Executors.newFixedThreadPool(10, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    });


    public void selfinit() {
        Thread filterServicethread = new Thread(this);
        filterServicethread.setDaemon(true);
        filterServicethread.start();

    }


    @Override
    public void run() {
        logger.info(FilterService.class + " setup!");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                FiltTask filtTask = filtetaskpool.take();
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Filter filter = filtTask.getFilter();
                            logger.info("filtetaskpool size="+filtetaskpool.size());
                            if (filter instanceof FirstOrderLagFilter) {
                                //一阶滤波
                                FirstOrderLagFilter folf = (FirstOrderLagFilter) filter;
                                folf.FirstOrderLagfilter(folf.getLastfilterdata(), filtTask.getUnfiltdata());
                                //附属任务：震荡检测
                                if (filtTask.getShockDetector() != null) {
                                    ShockTask shockTask = filtTask.getShockDetector().generShockTask();
                                    if (shockTask != null) {
                                        shockTask.job();
                                    }
                                }

                            } else if (filter instanceof MoveAverageFilter) {
                                //移动平均滤波
                                MoveAverageFilter mvav = (MoveAverageFilter) filter;
                                mvav.moveAveragefilter(filtTask.getUnfiltdatas());
                            }

                            if (filtTask.getItemfilterback() != null) {
                                try {
                                    filtTask.getItemfilterback().write(new JIVariant(filter.getLastfilterdata(), false));
                                } catch (JIException e) {
                                    logger.error(e.getMessage(), e);
                                }
                            }

                            //震荡检测数据反写
                            if (filtTask.getItemshockback() != null) {
                                try {
                                    filtTask.getItemshockback().write(new JIVariant(filtTask.getShockDetector().getLowhzA(), false));
                                } catch (JIException e) {
                                    logger.error(e.getMessage(), e);
                                }
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(),e);
                        }
                    }
                });

            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

    }
}

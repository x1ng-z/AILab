package hs.ServiceBus;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import hs.Filter.Filter;
import hs.Filter.FirstOrderLagFilter;
import hs.Filter.MoveAverageFilter;
import hs.Opc.ItemMangerContext;
import hs.ShockDetect.ShockDetector;
import hs.ShockDetect.ShockTask;
import org.apache.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author zzx
 * @version 1.0
 * @date 2020/6/12 7:49
 */

@Component
public class FilterService implements Runnable {
    private static final Logger logger = Logger.getLogger(FilterService.class);
    private Pattern opcpattern = Pattern.compile("([a-zA-Z]*)([0-9|.]*)");
    private LinkedBlockingQueue<FiltTask> filtetaskpool = new LinkedBlockingQueue<>();
    private ExecutorService threadpool;
    @Autowired
    private ItemMangerContext itemMangerContext;

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

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("filter-threadpool-%d").setDaemon(true).build();
        /**
         *new ThreadPoolExecutor.CallerRunsPolicy()是一个RejectedExecutorHandler
         *RejectedExecutionHandler：当线程池不能执行提交的线程任务时使用的策略
         * -DiscardOldestPolicy：丢弃最先提交到线程池的任务
         *-AbortPolicy： 中断此次提交，并抛出异常
         *-CallerRunsPolicy： 主线程自己执行此次任务
         *-DiscardPolicy： 直接丢弃此次任务，不抛出异常
         *
         *1）如果没有空闲的线程执行该任务且当前运行的线程数少于corePoolSize，则添加新的线程执行该任务。
         *（2）如果没有空闲的线程执行该任务且当前的线程数等于corePoolSize同时阻塞队列未满，则将任务入队列，而不添加新的线程。
         *3）如果没有空闲的线程执行该任务且阻塞队列已满同时池中的线程数小于maximumPoolSize，则创建新的线程执行任务。
         *4）如果没有空闲的线程执行该任务且阻塞队列已满同时池中的线程数等于maximumPoolSize，则根据构造函数中的handler指定的策略来拒绝新的任务。
         * */
        threadpool = new ThreadPoolExecutor(31/* 线程池维护的线程数量，即使其中有闲置线程*/, 1024/*线程池能容纳的最大线程数量*/,
                60L/*当前线程数量超出CORE_POOL_SIZE时，过量线程在开始任务前的等待时间，超时将被关闭*/, TimeUnit.MILLISECONDS,/*KEEP_ALIVE_TIME的单位*/
                new LinkedBlockingQueue<Runnable>(1), namedThreadFactory,  new ThreadPoolExecutor.CallerRunsPolicy()/*当执行被阻塞时要使用的处理程序,因为达到了线程界限和队列容量*/);
        threadpool.execute(this);
    }


    /**
     *
     * 创建一个过滤器任务，并放入到待执行列表中
     * @param filter 滤波器
     * @param value 本次采样数据
     * @param shockDetector 振荡器
     * */
    public void creatFilterTaskAndPut(Filter filter, double value, ShockDetector shockDetector){
        if (filter instanceof FirstOrderLagFilter) {
            FirstOrderLagFilter folf = (FirstOrderLagFilter)filter;
            //更新本次数据采样数据
            folf.setsampledata(value);//在一阶里面没什么用其实

            //新建过滤器的执行任务
            FiltTask folftask = new FiltTask();
            folftask.setFilter(folf);
            //滤波器反写属性设置item
            if ((folf.getBackToDCSTag() != null) && (!folf.getBackToDCSTag().equals(""))) {
                Matcher matcher = opcpattern.matcher(folf.getOpcresource());
                if (matcher.find()) {
                    folftask.setItemfilterback(itemMangerContext.getItemUnit(matcher.group(2), folf.getBackToDCSTag()) != null ? itemMangerContext.getItemUnit(matcher.group(2), folf.getBackToDCSTag()).getItem() : null);
                }
            }

            //振荡器反写属性设置item
            if ((shockDetector!=null)&&(shockDetector.getBackToDCSTag() != null) && (!shockDetector.getBackToDCSTag().trim().equals(""))) {
                Matcher matcher = opcpattern.matcher(shockDetector.getBackToDCSTag());
                if (matcher.find()) {
                    folftask.setItemshockback(itemMangerContext.getItemUnit(matcher.group(2), shockDetector.getBackToDCSTag()) != null ? itemMangerContext.getItemUnit(matcher.group(2), shockDetector.getBackToDCSTag()).getItem() : null);
                }
            }
            folftask.setUnfiltdata(value);//未滤波的数据
            folftask.setShockDetector(shockDetector);
            putfiltertask(folftask);

        } else if (filter instanceof MoveAverageFilter) {

            MoveAverageFilter mvav = (MoveAverageFilter) filter;
            //更新本次采集数据
            mvav.setsampledata(value);
            //新建滤波器执行任务
            FiltTask mvavtask = new FiltTask();
            mvavtask.setFilter(mvav);
            if ((mvav.getBackToDCSTag() != null) && (!mvav.getBackToDCSTag().equals(""))) {
                Matcher matcher = opcpattern.matcher(mvav.getOpcresource());
                if (matcher.find()) {
                    mvavtask.setItemfilterback(itemMangerContext.getItemUnit(matcher.group(2), mvav.getBackToDCSTag()) != null ? itemMangerContext.getItemUnit(matcher.group(2), mvav.getBackToDCSTag()).getItem() : null);
                }
            }

            //抽取本次需要滤波的窗口数据
            mvavtask.setUnfiltdatas(mvav.getUnfilterdatas());
            putfiltertask(mvavtask);
        }

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
//                            logger.info("filtetaskpool size="+filtetaskpool.size());
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

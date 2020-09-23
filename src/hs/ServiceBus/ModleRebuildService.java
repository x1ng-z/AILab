package hs.ServiceBus;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import hs.Bean.ControlModle;
import hs.Bean.ModleConstainer;
import hs.Dao.Service.ModleDBServe;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.util.concurrent.*;


/**
 *用于延时执行模型重构
 * */
@Component
public class ModleRebuildService implements Runnable {
    private static Logger logger= Logger.getLogger(ModleRebuildService.class);
    private DelayQueue<ModleRebuildTask> queue=new DelayQueue<ModleRebuildTask>();
    private ExecutorService threadpool;

    public void setModleConstainer(ModleConstainer modleConstainer) {
        this.modleConstainer = modleConstainer;
    }

    public ModleConstainer getModleConstainer() {
        return modleConstainer;
    }

    private ModleConstainer modleConstainer;

    public ModleDBServe getModleDBServe() {
        return modleDBServe;
    }

    @Autowired
    public void setModleDBServe(ModleDBServe modleDBServe) {
        this.modleDBServe = modleDBServe;
    }

    private ModleDBServe modleDBServe;


    public void putRebuildstak(ModleRebuildTask task){
        ControlModle controlmodle=modleConstainer.getRunnableModulepool().get(task.getModleid());
        if(controlmodle!=null){
            task.setControlModle(controlmodle);
            task.setModleDBServe(modleDBServe);
            queue.put(task);
        }
    }


    public void selfinit(){
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("modlerebuild-threadpool-%d").setDaemon(true).build();
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


    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            try {
                ModleRebuildTask modlerebuildtask =queue.take();
                modlerebuildtask.execute();
            }catch (Exception e){
                logger.error(e.getMessage(),e);
                if(e instanceof InterruptedException){
                    return;
                }
            }
        }
    }


}

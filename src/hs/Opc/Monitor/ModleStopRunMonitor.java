package hs.Opc.Monitor;

import hs.Bean.ModleConstainer;
import hs.Dao.Service.ModleDBServe;
import hs.Filter.*;
import org.apache.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/7/14 0:22
 */
@Component
public class ModleStopRunMonitor implements Runnable {
    private static final Logger logger = Logger.getLogger(ModleStopRunMonitor.class);
    private LinkedBlockingQueue<MonitorTask> taskpool = new LinkedBlockingQueue<>();
    private ModleDBServe modleDBServe;
    private ModleConstainer modleConstainer;

    @Autowired
    public ModleStopRunMonitor(ModleDBServe modleDBServe) {
        this.modleDBServe = modleDBServe;
    }


    public void putTask(MonitorTask task){
        task.setControlModle(modleConstainer.getModulepool().get(task.getModleid()));
        task.setModleDBServe(modleDBServe);
        taskpool.offer(task);
    }
    private ExecutorService exec = Executors.newFixedThreadPool(4,new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    });



    public void selfinit(){
        Thread monitorthread=new Thread(this);
        monitorthread.setDaemon(true);
        monitorthread.start();
        logger.info(ModleStopRunMonitor.class+" setup!");
    }



    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                MonitorTask Task = taskpool.take();
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        Task.work();
                    }
                });
            } catch (InterruptedException e) {
                logger.error(e.getMessage(),e);
            }catch (Exception e){
                logger.error(e.getMessage(),e);
            }
        }

    }

    public ModleConstainer getModleConstainer() {
        return modleConstainer;
    }

    public void setModleConstainer(ModleConstainer modleConstainer) {
        this.modleConstainer = modleConstainer;
    }
}

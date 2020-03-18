package hs.ApcAlgorithm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingQueue;

public class ExecutePythonBridge {
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ExecutePythonBridge.class);
    public  Process p=null;
    public  void destory(){
        if(p!=null){
            p.destroy();
        }
        p=null;
    }

    public  void execute(LinkedBlockingQueue<String> linkedBlockingQueue,String[] cmd, String... encoding) {
        if(p!=null){
            return;
        }
        BufferedReader bReader = null;
        InputStreamReader sReader = null;
        try {
            p = Runtime.getRuntime().exec(cmd);
            Thread result= new Thread(new InputStreamRunnable(p.getInputStream(),"Result",linkedBlockingQueue));
            result.setDaemon(true);
            result.start();

            /* 为"错误输出流"单独开一个线程读取之,否则会造成标准输出流的阻塞 */
            Thread error = new Thread(new InputStreamRunnable(p.getErrorStream(), "ErrorStream",null));
            error.setDaemon(true);
            error.start();

        } catch (Exception e) {
           logger.error(e);
        }
    }

}



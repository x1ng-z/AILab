package hs.ApcAlgorithm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingQueue;

public class ExecutePythonBridge {
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ExecutePythonBridge.class);
    public  Process p=null;
    private String pythonjs="E:\\LinkAPC.py";
    private String url;//new String[]{pythonhome,pythonjs,"http://localhost:8080/python/modlebuild/"+key+".do"};
    private String modleid;
    Thread result=null;
    Thread error=null;

    public ExecutePythonBridge( String pythonjs, String url,String modleid) {
        this.pythonjs = pythonjs;
        this.url = url;
        this.modleid=modleid;
    }

    public  boolean stop(){
        if(p!=null){
            p.destroy();
            result.interrupt();
            error.interrupt();
            return true;
        }
        p=null;
        return true;
    }

    public  boolean execute() {
        if(p!=null){
            return true;
        }
        //LinkedBlockingQueue<String> linkedBlockingQueue=new LinkedBlockingQueue();
//        BufferedReader bReader = null;
//        InputStreamReader sReader = null;
        try {
            p = Runtime.getRuntime().exec(new String[]{pythonjs, url,modleid});
             result= new Thread(new InputStreamRunnable(p.getInputStream(),"Result",null));
            result.setDaemon(true);
            result.start();

            /* 为"错误输出流"单独开一个线程读取之,否则会造成标准输出流的阻塞 */
             error = new Thread(new InputStreamRunnable(p.getErrorStream(), "ErrorStream",null));
            error.setDaemon(true);
            error.start();

        } catch (Exception e) {
           logger.error(e.getMessage(),e);
           logger.error("jsdir"+pythonjs);
           return false;
        }
        return true;
    }

}



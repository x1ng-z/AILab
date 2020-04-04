package hs.ApcAlgorithm;


import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingQueue;

public class InputStreamRunnable implements Runnable {
    private static Logger log = Logger.getLogger(InputStreamRunnable.class);

    private BufferedReader bReader = null;
    private String _type;
    private LinkedBlockingQueue linkedBlockingQueue=null;

    public InputStreamRunnable(InputStream is, String _type, LinkedBlockingQueue linkedBlockingQueue) {
        this.linkedBlockingQueue=linkedBlockingQueue;
        try {
            this._type=_type;
            bReader = new BufferedReader(new InputStreamReader((is), "UTF-8"));
        } catch (Exception ex) {
            log.error(ex);
        }
    }
    public void run() {
        String line;
        int num = 0;
        try {
            while ((line = bReader.readLine()) != null) {
                if(linkedBlockingQueue!=null){
                    log.info(line);
                    linkedBlockingQueue.put(line);
                }else {
                    log.info(line);

                }
            }
            log.info("end "+_type);
            bReader.close();
        } catch (Exception ex) {
            log.error(ex);

        }
    }
}

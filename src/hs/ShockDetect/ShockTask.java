package hs.ShockDetect;

import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/7/20 18:18
 */
public class ShockTask {
    public static Logger logger = Logger.getLogger(ShockDetector.class);
    private ShockDetector shockDetector;
    public ShockTask(ShockDetector shockTask) {
        this.shockDetector = shockTask;
    }
    public void job(){
        List<Double> data=shockDetector.getFiltdata();
        if(data.size()==0){
            return;
        }
        FFT fft=new  FFT(data);
        double lowfrequen=fft.computeLowFrequenByMean();
        logger.info("lowfrequen="+lowfrequen);
        shockDetector.setLowhzA(lowfrequen);
    }
}

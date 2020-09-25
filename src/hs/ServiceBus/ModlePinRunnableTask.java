package hs.ServiceBus;

import hs.Bean.ModlePin;
import org.apache.log4j.Logger;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/9/18 8:25
 */
public class ModlePinRunnableTask extends ModleRebuildTask{
    private static Logger logger= Logger.getLogger(ModlePinRunnableTask.class);

    private ModlePin modlePin;

    public ModlePinRunnableTask(int delaymillisec, int modleid, ModlePin modlePin) {
        super(delaymillisec,modleid);
        this.modlePin = modlePin;
    }

    @Override
    public void execute() {
        /**
         * 检测模型是否运行状态
         *
         * 1、停止模型
         * 2、构建模型
         * 3、启动模型
         * */

        if(null!=getControlModle()){
            logger.info("%%##"+this.getClass().toString());
            getControlModle().runnablePinByDCS(modlePin);
        }

    }
}

package hs.ServiceBus;

import org.apache.log4j.Logger;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/7/14 0:49
 */

public class ModleEnableTask extends ModleRebuildTask {
    private static final Logger logger = Logger.getLogger(ModleEnableTask.class);


    public ModleEnableTask(int delaymillisec, int modleid) {
        super(delaymillisec, modleid);
    }

    @Override
    public void execute() {
        if (getControlModle() != null) {
            getControlModle() .enableModleByDCS();
            getModleDBServe().modifymodleEnable(getControlModle().getModleId(), 1);
        }else {
            logger.error("DCS控制：模型id="+getModleid()+"启动失败");
        }
    }

}

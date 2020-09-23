package hs.ServiceBus;

import org.apache.log4j.Logger;


/**
 * @author zzx
 * @version 1.0
 * @date 2020/7/14 0:30
 */
public class ModleDisEnableTask extends ModleRebuildTask {
    private static final Logger logger = Logger.getLogger(ModleDisEnableTask.class);

    public ModleDisEnableTask(int delaymillisec, int modleid) {
        super(delaymillisec, modleid);
    }

    @Override
    public void execute() {
        if(getControlModle()!=null){
            getControlModle().disEnableModleByDCS();
            getModleDBServe().modifymodleEnable(getControlModle().getModleId(),0);
        }else {
            logger.error("DCS控制：模型id="+getModleid()+"停止失败");
        }
    }

}

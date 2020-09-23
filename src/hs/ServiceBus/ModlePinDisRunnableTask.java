package hs.ServiceBus;

import hs.Bean.ModlePin;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/9/18 8:25
 */
public class ModlePinDisRunnableTask extends ModleRebuildTask {
    private ModlePin modlePin;

    public ModlePinDisRunnableTask(int delaymillisec, int modleid, ModlePin modlePin) {
        super(delaymillisec,modleid);
        this.modlePin = modlePin;
    }

    @Override
    public void execute() {
        if(null!=getControlModle()){
          getControlModle().disRunnablePinByDCS(modlePin);
        }
    }

}

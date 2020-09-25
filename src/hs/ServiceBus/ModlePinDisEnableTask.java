package hs.ServiceBus;

import hs.Bean.ModlePin;
import org.apache.log4j.Logger;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/8/17 9:28
 */
public class ModlePinDisEnableTask extends ModleRebuildTask {
    private static final Logger logger = Logger.getLogger(ModlePinDisEnableTask.class);
    private ModlePin pin;

    public ModlePinDisEnableTask(int delaymillisec, ModlePin pin) {
        super(delaymillisec, pin.getReference_modleId());
        this.pin = pin;
    }

    @Override
    public void execute() {
        if (getControlModle() != null) {
            getControlModle().disablePinByDCS(pin);
            getModleDBServe().updatepinEnable(pin.getModlepinsId(), 0);
        }
    }
}

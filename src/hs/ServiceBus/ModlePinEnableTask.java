package hs.ServiceBus;

import hs.Bean.ControlModle;
import hs.Bean.ModlePin;
import hs.Dao.Service.ModleDBServe;
import hs.Opc.Monitor.MonitorTask;
import org.apache.log4j.Logger;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/8/17 9:28
 */
public class ModlePinEnableTask extends ModleRebuildTask {
    private static final Logger logger = Logger.getLogger(ModlePinEnableTask.class);
    private ModlePin pin;

    public ModlePinEnableTask(int delaymillisec, ModlePin pin) {
        super(delaymillisec, pin.getReference_modleId());
        this.pin = pin;
    }

    @Override
    public void execute() {
        try {
            if(getControlModle()!=null){
                getControlModle().enablePinByDCS(pin);
                getModleDBServe().updatepinEnable(pin.getModlepinsId(), 1);
            }
        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }


    }
}

package hs.Opc.Monitor;

import hs.Bean.ControlModle;
import hs.Dao.Service.ModleDBServe;
import org.apache.log4j.Logger;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/7/14 0:49
 */

public class ModleRunTask implements MonitorTask {
    private static final Logger logger = Logger.getLogger(ModleRunTask.class);
    private ModleDBServe modleDBServe;
    private ControlModle controlModle;
    private Integer modleid;


    @Override
    public void work() {
        runModel();
    }

    /**
     * 运行模型
     */
    public void runModel() {
        if (controlModle != null) {
            if (controlModle.getModleEnable() == 0) {
                controlModle.setModleEnable(1);
                controlModle.generateValidkey();
                controlModle.getExecutePythonBridge().execute();
                modleDBServe.modifymodleEnable(controlModle.getModleId(), 1);
                logger.error("DCS控制：模型运行成功");
            } else {
                logger.error("DCS控制：模型本来就是运行状态！");
            }

        } else {
            logger.error("DCS控制：模型运行失败");
        }
    }


    public void setModleDBServe(ModleDBServe modleDBServe) {
        this.modleDBServe = modleDBServe;
    }

    public void setControlModle(ControlModle controlModle) {
        this.controlModle = controlModle;
    }

    public void setModleid(Integer modleid) {
        this.modleid = modleid;
    }

    public Integer getModleid() {
        return modleid;
    }
}

package hs.Opc.Monitor;

import hs.Bean.ControlModle;
import hs.Dao.Service.ModleDBServe;
import org.apache.log4j.Logger;


/**
 * @author zzx
 * @version 1.0
 * @date 2020/7/14 0:30
 */
public class ModleStopTask implements MonitorTask{
    private static final Logger logger = Logger.getLogger(ModleStopTask.class);

    private ModleDBServe modleDBServe;
    private ControlModle controlModle;
    private Integer modleid;

    @Override
    public void work() {
        stopModel();
    }

    /**
     *
     * 停止模型
     * */
    public void stopModel(){
        if(controlModle!=null){
            if(controlModle.getModleEnable()==1){
                controlModle.generateValidkey();
                controlModle.setModleEnable(0);
                controlModle.getExecutePythonBridge().stop();
                modleDBServe.modifymodleEnable(controlModle.getModleId(),0);
                logger.error("DCS控制：模型id="+controlModle.getModleId()+"停止成功");
            }else {
                logger.error("DCS控制：模型id="+controlModle.getModleId()+"本来就是停止");
            }
        }else {
            logger.error("DCS控制：模型id="+modleid+"停止失败");
        }


    }


//    /**
//     * 运行模型
//     * */
//    public void runModel(ControlModle controlModle){
//        if(controlModle!=null){
//            if(controlModle.getModleEnable()==0){
//                controlModle.setModleEnable(1);
//                controlModle.getExecutePythonBridge().execute();
//                modleDBServe.modifymodleEnable(controlModle.getModleId(),1);
//
//            }
//        }
//    }


    public void setModleDBServe(ModleDBServe modleDBServe) {
        this.modleDBServe = modleDBServe;
    }

    public void setControlModle(ControlModle controlModle) {
        this.controlModle = controlModle;
    }

    public Integer getModleid() {
        return modleid;
    }

    @Override
    public void setModleid(Integer modleid) {
        this.modleid = modleid;
    }
}

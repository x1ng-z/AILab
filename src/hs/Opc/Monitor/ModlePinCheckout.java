package hs.Opc.Monitor;

import hs.Bean.ControlModle;
import hs.Bean.ModlePin;
import hs.Dao.Service.ModleDBServe;
import org.apache.log4j.Logger;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/8/17 9:28
 */
public class ModlePinCheckout implements MonitorTask {
    private static final Logger logger = Logger.getLogger(ModlePinCheckout.class);

    private ModleDBServe modleDBServe;
    private ControlModle controlModle;
    private Integer modleid;
    private Integer pinid;


    @Override
    public void work() {


        if(controlModle!=null){

            for (ModlePin pvpin : controlModle.getCategoryPVmodletag()) {
                if(pvpin.getDcsEnabePin()!=null&&pvpin.getDcsEnabePin().getModlepinsId()==pinid){
                    try {

                        if(pvpin.getPinEnable()==1){
                            /**如果当前是0,这要切入，如果当前是1,这要切除，*/
                            pvpin.setPinEnable(0);
                            modleDBServe.updatepinEnable(pinid,0);
                            /**重新build下*/
                            controlModle.getExecutePythonBridge().stop();
                            controlModle.getSimulatControlModle().getExecutePythonBridgeSimulate().stop();
                            controlModle.modleBuild(false);
                            controlModle.getExecutePythonBridge().execute();
                            logger.info("DCS控制：模型id="+controlModle.getModleId()+" pinid="+pinid+"切出成功");
                        }else {
                            logger.info("DCS控制：模型id="+controlModle.getModleId()+" pinid="+pinid+"本来就已经切出");
                        }

                    } catch (NumberFormatException e) {
                        logger.error(e.getMessage(), e);
                    }catch (Exception e){
                        logger.error(e.getMessage(),e);
                    }

                    break;
                }

            }

        }else {
            logger.info("DCS控制：模型id="+modleid+"不存在");
        }

    }

    @Override
    public void setModleDBServe(ModleDBServe modleDBServe) {
        this.modleDBServe=modleDBServe;
    }

    @Override
    public void setControlModle(ControlModle controlModle) {
        this.controlModle=controlModle;
    }

    @Override
    public void setModleid(Integer modleid) {
        this.modleid=modleid;
    }

    @Override
    public Integer getModleid() {
        return modleid;
    }

    public Integer getPinid() {
        return pinid;
    }

    public void setPinid(Integer pinid) {
        this.pinid = pinid;
    }
}

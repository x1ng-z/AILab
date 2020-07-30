package hs.Opc.Monitor;

import hs.Bean.ControlModle;
import hs.Dao.Service.ModleDBServe;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/7/14 0:48
 */
public interface MonitorTask {
    void work();
     void setModleDBServe(ModleDBServe modleDBServe);

     void setControlModle(ControlModle controlModle);

     void setModleid(Integer modleid);
     Integer getModleid() ;
}

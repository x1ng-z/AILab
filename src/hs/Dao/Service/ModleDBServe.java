package hs.Dao.Service;

import hs.Bean.BaseConf;
import hs.Bean.ControlModle;
import hs.Bean.ModlePin;
import hs.Bean.ResponTimeSerise;
import hs.Dao.ModleDBMapper;
import hs.Filter.FirstOrderLagFilter;
import hs.Filter.MoveAverageFilter;
import hs.ShockDetect.ShockDetector;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/18 14:01
 */
@Service
public class ModleDBServe {
    private ModleDBMapper modleMapper;


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ControlModle> getAllModle() {
        return modleMapper.getModles();
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BaseConf getBaseConf() {
        return modleMapper.getBaseConf();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertModle(ControlModle controller) {
        modleMapper.insertModle(controller);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteModlePins(int modleid) {
        modleMapper.deleteModlePins(modleid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void modifymodleEnable(int modleid, int enable) {
        modleMapper.modifymodleEnable(modleid, enable);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteModle(int modleid) {
        modleMapper.deleteModle(modleid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ControlModle getModle(int modleid) {
        return modleMapper.getModle(modleid);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteModleResp(int modleid) {
        modleMapper.deleteModleResp(modleid);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void modifymodle(int modleid, ControlModle controlModle) {
        modleMapper.modifymodle(modleid, controlModle);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertModlePins(List<ModlePin> modlePins) {
        modleMapper.insertModlePins(modlePins);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertPinsMVAVFilter(MoveAverageFilter filter) {
        modleMapper.insertPinsMVAVFilter(filter);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertPinsFODLFilter(FirstOrderLagFilter filter) {
        modleMapper.insertPinsFODLFilter(filter);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deletePinsFilter(int modlepinsId) {
        modleMapper.deletePinsFilter(modlepinsId);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertModleResp(List<ResponTimeSerise> responTimeSerises) {
        modleMapper.insertModleResp(responTimeSerises);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertShockDetetetor(ShockDetector shockDetector) {
        modleMapper.insertShockDetetetor(shockDetector);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ShockDetector getShockDetetetor(int id) {
        return modleMapper.getShockDetetetor(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeShockDetetetor(int id) {
        modleMapper.removeShockDetetetor(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateShockDetetetor(ShockDetector shockDetector) {
        modleMapper.updateShockDetetetor(shockDetector);
    }


    @Autowired
    public void setModleMapper(ModleDBMapper modleMapper) {
        this.modleMapper = modleMapper;
    }
}

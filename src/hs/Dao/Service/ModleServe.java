package hs.Dao.Service;

import hs.Bean.BaseConf;
import hs.Bean.ControlModle;
import hs.Bean.ModlePin;
import hs.Bean.ResponTimeSerise;
import hs.Dao.ModleMapper;
import hs.Filter.FirstOrderLagFilter;
import hs.Filter.MoveAverageFilter;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/18 14:01
 */
@Component
public class ModleServe {
    private ModleMapper modleMapper;


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ControlModle> getAllModle(){
        return modleMapper.getModles();
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BaseConf getBaseConf(){
        return modleMapper.getBaseConf();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertModle(ControlModle controller){
        modleMapper.insertModle(controller);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteModlePins(int modleid){
        modleMapper.deleteModlePins(modleid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void modifymodleEnable(int modleid,int enable){
        modleMapper.modifymodleEnable(modleid,enable);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteModle(int modleid){
        modleMapper.deleteModle(modleid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ControlModle getModle(int modleid){
        return modleMapper.getModle(modleid);
    }



    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteModleResp(int modleid){
        modleMapper.deleteModleResp(modleid);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void modifymodle(int modleid,ControlModle controlModle){
        modleMapper.modifymodle(modleid, controlModle);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertModlePins(List<ModlePin> modlePins){
        modleMapper.insertModlePins(modlePins);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertPinsMVAVFilter(MoveAverageFilter filter){
        modleMapper.insertPinsMVAVFilter(filter);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertPinsFODLFilter(FirstOrderLagFilter filter){
        modleMapper.insertPinsFODLFilter(filter);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deletePinsFilter( int modlepinsId){
        modleMapper.deletePinsFilter(modlepinsId);
    }



    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertModleResp(List<ResponTimeSerise> responTimeSerises){
        modleMapper.insertModleResp(responTimeSerises);
    }



    @Autowired
    public void setModleMapper(ModleMapper modleMapper) {
        this.modleMapper = modleMapper;
    }
}

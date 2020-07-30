package hs.Dao;

import hs.Bean.*;
import hs.Filter.Filter;
import hs.Filter.FirstOrderLagFilter;
import hs.Filter.MoveAverageFilter;
import hs.ShockDetect.ShockDetector;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/2/29 11:12
 */
@Repository
public interface ModleDBMapper {

    List<ControlModle> getModles();

    BaseConf getBaseConf();

    void insertModle(@Param("controlModle") ControlModle controlModle);
     void modifymodle(@Param("modleid") int modleid, @Param("controlModle") ControlModle controlModle);

    void insertModlePins( @Param("modlePins") List<ModlePin> modlePins);
    void deleteModlePins( @Param("modleid") int modleid);


    void insertPinsMVAVFilter( @Param("filter") MoveAverageFilter filter);
    void insertPinsFODLFilter( @Param("filter") FirstOrderLagFilter filter);

    void deletePinsFilter( @Param("modlepinsId") int modlepinsId);;



    void insertShockDetetetor(@Param("detector")ShockDetector shockDetector);
    ShockDetector getShockDetetetor(int id);
    void removeShockDetetetor(int id);
    void updateShockDetetetor(@Param("detector")ShockDetector shockDetector);

    void insertModleResp( @Param("responTimeSerises")List<ResponTimeSerise> responTimeSerises);

    void deleteModleResp( @Param("modleid") int modleid);

    void deleteModle(@Param("modleid") int modleid);

    void modifymodleEnable(@Param("modleid") int modleid,@Param("enable") int enable);
    ControlModle getModle(@Param("modleid") int modleid);
//    List<ModleTag> getModle(@Param("modleidid")int modleid);
//
//    void insertmodleTag(@Param("modleTag")List<ModleTag> modleTag);
//    void modifyModleTag(ModleTag modleTag);
//    void deleteModleTag(@Param("modleidid")int modleid);
}

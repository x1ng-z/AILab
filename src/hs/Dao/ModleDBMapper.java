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

    void insertModlePins(@Param("modlePins") List<ModlePin> modlePins);

    void insertModlePin(@Param("pin") ModlePin modlePins);

    void deleteModlePins(@Param("modleid") int modleid);



    void deleteModlePinbypinid(@Param("pinid") int pinid);

    void updatepin(@Param("pin") ModlePin pin);

    List<ModlePin> pagepinsbymodleid(@Param("modleid") int modleid, @Param("pintype") String pintype, @Param("page") int page, @Param("pagesize") int pagesize);

    List<ResponTimeSerise> pageresponymodleid(@Param("modleid") int modleid,@Param("page") int page, @Param("pagesize") int pagesize);


    List<ModlePin> pinsbypintype(@Param("modleid") int modleid, @Param("pintype") String pintype);


    int countpinsbymodleid(@Param("modleid") int modleid, @Param("pintype") String pintype);

    int countresponbymodleid(@Param("modleid") int modleid);


    ModlePin  findPinbypinid(@Param("pinid") int pinid);

    ModlePin  findPinbypinmodleidAndpinname(@Param("modleid") int modleid,@Param("pinname")String pinname);

    void insertPinsMVAVFilter(@Param("filter") MoveAverageFilter filter);

    void insertPinsFODLFilter(@Param("filter") FirstOrderLagFilter filter);

    void updatePinsFODLFilter(@Param("filter") FirstOrderLagFilter filter);

    void updatePinsMVAVFilter(@Param("filter") MoveAverageFilter filter);

    void deletePinsFilter(@Param("modlepinsId") int modlepinsId);

    void deletePinsFilterbyfilterid(@Param("filterid") int filterid);



    void insertShockDetetetor(@Param("detector") ShockDetector shockDetector);

    ShockDetector getShockDetetetor(int id);

    void removeShockDetetetor(int shockid);

    void updateShockDetetetor(@Param("detector") ShockDetector shockDetector);

    void insertModleResp(@Param("responTimeSerises") List<ResponTimeSerise> responTimeSerises);

    void deleteModleResp(@Param("modleid") int modleid);

    void deleteModle(@Param("modleid") int modleid);

    void updatemodleEnable(@Param("modleid") int modleid, @Param("enable") int enable);

    void updatepinEnable(@Param("pinid") int pinid, @Param("enable") int enable);

    ControlModle getModle(@Param("modleid") int modleid);

    void updateallpinip(@Param("oldip") String oldip, @Param("newip") String newip);

    void updateallfilterip(@Param("oldip") String oldip, @Param("newip") String newip);

    void updateallshockdetectip(@Param("oldip") String oldip, @Param("newip") String newip);

    void updateallshockdetectfilterip(@Param("oldip") String oldip, @Param("newip") String newip);


//    List<ModleTag> getModle(@Param("modleidid")int modleid);
//
//    void insertmodleTag(@Param("modleTag")List<ModleTag> modleTag);
//    void modifyModleTag(ModleTag modleTag);
//    void deleteModleTag(@Param("modleidid")int modleid);
}

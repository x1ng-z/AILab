package hs.Dao;

import hs.Bean.*;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/2/29 11:12
 */
public interface ModleMapper {

    List<ControlModle> getModles();

    BaseConf getBaseConf();

    void insertModle(@Param("controlModle") ControlModle controlModle);

    void insertModlePins( @Param("controlModle") ControlModle controlModle);

    void insertModleResp( @Param("responTimeSerises")List<ResponTimeSerise> responTimeSerises);


//    List<ModleTag> getModle(@Param("modleidid")int modleid);
//
//    void insertmodleTag(@Param("modleTag")List<ModleTag> modleTag);
//    void modifyModleTag(ModleTag modleTag);
//    void deleteModleTag(@Param("modleidid")int modleid);
}

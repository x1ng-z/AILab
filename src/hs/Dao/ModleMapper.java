package hs.Dao;

import hs.Bean.ControlModle;
import hs.Bean.Tag;
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

    @MapKey("tagId")
    Map<Integer,Tag> getAllTags();

    Tag findTagById(@Param("id")int id);
}

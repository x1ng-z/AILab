package hs.Dao.Service;

import hs.Bean.ControlModle;
import hs.Bean.Tag;
import hs.Dao.ModleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
    public Map<Integer,Tag> getAllTag(){
        return modleMapper.getAllTags();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Tag findTag(int tagId){
        return modleMapper.findTagById(tagId);
    }


    @Autowired
    public void setModleMapper(ModleMapper modleMapper) {
        this.modleMapper = modleMapper;
    }
}

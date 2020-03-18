package hs.Dao.Service;

import hs.Bean.ControlModle;
import hs.Dao.ModleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/18 14:01
 */
@Component
public class ModleServe {


    private ModleMapper modleMapper;


    public List<ControlModle> findAllModle(){
        return modleMapper.getModles();
    }


    public ModleMapper getModleMapper() {
        return modleMapper;
    }

    @Autowired
    public void setModleMapper(ModleMapper modleMapper) {
        this.modleMapper = modleMapper;
    }
}

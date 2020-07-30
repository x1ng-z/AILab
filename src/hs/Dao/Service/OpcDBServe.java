package hs.Dao.Service;

import hs.Dao.OpcServeDBMapper;
import hs.Opc.OPCService;
import hs.Opc.OpcVeriTag;
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
 * @date 2020/7/2 10:25
 */
@Service
public class OpcDBServe {
    private OpcServeDBMapper opcServeDBMapper;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<OPCService> getopcserves(){
        return opcServeDBMapper.getopcserves();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateopcserves(@Param("opcserve")OPCService opcserve){
        opcServeDBMapper.updateopcserves(opcserve);
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteopcserves(int opcserveid){
        opcServeDBMapper.deleteopcserves(opcserveid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertopcserves(@Param("opcserve")OPCService opcserve){
        opcServeDBMapper.insertopcserves(opcserve);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateopcverificationtag(@Param("opcVeriTag") OpcVeriTag opcVeriTag){
        opcServeDBMapper.updateopcverificationtag(opcVeriTag);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteopcverificationtag(int tagid){
        opcServeDBMapper.deleteopcverificationtag(tagid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertopcverificationtag(@Param("opcVeriTag")OpcVeriTag opcVeriTag){
        opcServeDBMapper.insertopcverificationtag(opcVeriTag);
    }

    @Autowired
    public void setOpcServeDBMapper(OpcServeDBMapper opcServeDBMapper) {
        this.opcServeDBMapper = opcServeDBMapper;
    }
}

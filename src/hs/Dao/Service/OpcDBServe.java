package hs.Dao.Service;

import hs.Dao.OpcServeDBMapper;
import hs.Opc.OPCService;
import hs.Opc.OpcVeriTag;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
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
    public int getopcservescount(){
        return opcServeDBMapper.getopcservescount();
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<OPCService>  pageopcserves(int page,int pagesize){
        return opcServeDBMapper.pageopcserves(page,pagesize);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public OPCService  findopcservebyid(int opcserveid){
        return opcServeDBMapper.findopcservebyid(opcserveid);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateopcserves(OPCService opcserve){
        opcServeDBMapper.updateopcserves(opcserve);
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteopcserves(int opcserveid){
        opcServeDBMapper.deleteopcserves(opcserveid);
    }




    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteopcservesAndverTags(int opcserveid){
        opcServeDBMapper.deleteopcserves(opcserveid);
        opcServeDBMapper.deleteopcverificationtagbyopcserveid(opcserveid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    void deleteopcverificationtagbyopcserveid(int opcserveid){
        opcServeDBMapper.deleteopcverificationtagbyopcserveid(opcserveid);
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertopcserves(OPCService opcserve){
        opcServeDBMapper.insertopcserves(opcserve);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateopcverificationtag( OpcVeriTag opcVeriTag){
        opcServeDBMapper.updateopcverificationtag(opcVeriTag);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteopcverificationtag(int tagid){
        opcServeDBMapper.deleteopcverificationtagbytagid(tagid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertopcverificationtag(OpcVeriTag opcVeriTag){
        opcServeDBMapper.insertopcverificationtag(opcVeriTag);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<OpcVeriTag> findopcverificationtagbyopcserveid(int opcserveid,int page,int pagesize){
        return opcServeDBMapper.findopcverificationtagbyopcserveid(opcserveid,page,pagesize);
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public OpcVeriTag findopcverificationtagbytagid(int tagid){
        return opcServeDBMapper.findopcverificationtagbytagid(tagid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public int countopcverificationtagbyopcserveid(int opcserveid){
        return opcServeDBMapper.countopcverificationtagbyopcserveid(opcserveid);
    }



    @Autowired
    public void setOpcServeDBMapper(OpcServeDBMapper opcServeDBMapper) {
        this.opcServeDBMapper = opcServeDBMapper;
    }
}

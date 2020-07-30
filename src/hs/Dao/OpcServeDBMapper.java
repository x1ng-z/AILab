package hs.Dao;

import hs.Opc.OPCService;
import hs.Opc.OpcVeriTag;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/7/2 9:05
 */
@Repository
public interface OpcServeDBMapper {
    List<OPCService> getopcserves();
    void updateopcserves(@Param("opcserve")OPCService opcserve);
    void deleteopcserves(int opcserveid);
    void insertopcserves(@Param("opcserve")OPCService opcserve);

    void updateopcverificationtag(@Param("opcVeriTag")OpcVeriTag opcVeriTag);
    void deleteopcverificationtag(int tagid);
    void insertopcverificationtag(@Param("opcVeriTag")OpcVeriTag opcVeriTag);
}

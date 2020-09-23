package hs.Dao;

import hs.Bean.Algorithm.AlgorithmModle;
import hs.Bean.Algorithm.AlgorithmProperty;
import hs.Filter.FirstOrderLagFilter;
import hs.Filter.MoveAverageFilter;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/8/31 15:35
 */

@Repository
public interface AlgorithmModleDBMapper {

    List<AlgorithmModle> getAlgorithmModles();

    void insertAlgorithmModles(@Param("modle") AlgorithmModle modle);

    void deleteAlgorithmModles(@Param("modleid") int modleid);

    void updateAlgorithmModles(@Param("modle") AlgorithmModle modle);

    AlgorithmModle findAlgorithmModlebyId(@Param("modleid") int modleid);




    void insertAlgorithmProperty(@Param("property")AlgorithmProperty property);

    void deleteAlgorithmProperty(@Param("propertyid") int propertyid);

    AlgorithmProperty findAlgorithmPropertybyId(@Param("propertyid") int propertyid);

    List<AlgorithmProperty> getAlgorithmPropertys(@Param("modleid") int modleid,@Param("page") int page,@Param("pagesize") int pagesize);

    Integer getAlgorithmPropertyscount(@Param("modleid") int modleid);

    void deleteAlgorithmfilterbypropertyid(@Param("propertyid") int propertyid);

    void updateAlgorithmProperty(@Param("property")AlgorithmProperty property);




    void insertAlgorithmMVAVFilter( @Param("filter") MoveAverageFilter filter);

    void insertAlgorithmFODLFilter( @Param("filter") FirstOrderLagFilter filter);

    void deleteAlgorithmfilterbyfilterid(@Param("filterid") int filterid);


    void updateAlgorithmPropertyMVAVFilter(@Param("filter") MoveAverageFilter filter);
    void updateAlgorithmPropertyFODLFilter(@Param("filter") FirstOrderLagFilter filter);

    void updateallshockdetectalgorithmfilterip(@Param("oldip") String oldip, @Param("newip") String newip);
    void updateallshockdetectalgorithmpropertiesip(@Param("oldip") String oldip, @Param("newip") String newip);












}

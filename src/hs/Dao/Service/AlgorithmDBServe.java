package hs.Dao.Service;

import hs.Bean.Algorithm.AlgorithmModle;
import hs.Bean.Algorithm.AlgorithmProperty;
import hs.Dao.AlgorithmModleDBMapper;
import hs.Filter.FirstOrderLagFilter;
import hs.Filter.MoveAverageFilter;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/9/1 5:26
 */

@Service
public class AlgorithmDBServe {


    private AlgorithmModleDBMapper algorithmModleDBMapper;
    @Autowired
    public void setAlgorithmModleDBMapper(AlgorithmModleDBMapper algorithmModleDBMapper) {
        this.algorithmModleDBMapper = algorithmModleDBMapper;
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
   public List<AlgorithmModle> getAlgorithmModles(){
       return algorithmModleDBMapper.getAlgorithmModles();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
   public void insertAlgorithmModles(AlgorithmModle modle){
        algorithmModleDBMapper.insertAlgorithmModles(modle);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteAlgorithmModles( int modleid){
        algorithmModleDBMapper.deleteAlgorithmModles(modleid);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateAlgorithmModles(AlgorithmModle modle){
        algorithmModleDBMapper.updateAlgorithmModles(modle);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AlgorithmModle findAlgorithmModlebyId(int modleid){
        return algorithmModleDBMapper.findAlgorithmModlebyId(modleid);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertAlgorithmProperty(AlgorithmProperty property){
        algorithmModleDBMapper.insertAlgorithmProperty(property);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertAlgorithmPropertyAndFilter(AlgorithmProperty property){
        algorithmModleDBMapper.insertAlgorithmProperty(property);
        if(property.getFilter()!=null){
            property.getFilter().setPk_pinid(property.getPropertyid());
            if(property.getFilter() instanceof FirstOrderLagFilter){
                algorithmModleDBMapper.insertAlgorithmFODLFilter((FirstOrderLagFilter)property.getFilter());
            }else if(property.getFilter() instanceof  MoveAverageFilter){
                algorithmModleDBMapper.insertAlgorithmMVAVFilter((MoveAverageFilter)property.getFilter());
            }
        }
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateAlgorithmPropertyAndFilter(AlgorithmProperty property){
        algorithmModleDBMapper.updateAlgorithmProperty(property);
        if(property.getFilter()!=null){
            if(property.getFilter() instanceof FirstOrderLagFilter){
                algorithmModleDBMapper.updateAlgorithmPropertyFODLFilter((FirstOrderLagFilter)property.getFilter());
            }else if(property.getFilter() instanceof  MoveAverageFilter){
                algorithmModleDBMapper.updateAlgorithmPropertyMVAVFilter((MoveAverageFilter)property.getFilter());
            }
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateAlgorithmProperty(AlgorithmProperty property){
        algorithmModleDBMapper.updateAlgorithmProperty(property);
    }





    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteAlgorithmPropertyAndFilter(int modleid,int propertyid){
        algorithmModleDBMapper.deleteAlgorithmProperty(propertyid);
        algorithmModleDBMapper.deleteAlgorithmfilterbypropertyid(propertyid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteAlgorithmProperty(int propertyid){
        algorithmModleDBMapper.deleteAlgorithmProperty(propertyid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AlgorithmProperty findAlgorithmPropertybyId(int propertyid){
        return algorithmModleDBMapper.findAlgorithmPropertybyId(propertyid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<AlgorithmProperty> getAlgorithmPropertys(int modleid,int page,int pagesize){
        return algorithmModleDBMapper.getAlgorithmPropertys(modleid,page,pagesize);
    }



    @Transactional(isolation = Isolation.READ_COMMITTED)
    public int getAlgorithmPropertyscount(int modleid){
        return algorithmModleDBMapper.getAlgorithmPropertyscount(modleid);
    }



    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertAlgorithmMVAVFilter(  MoveAverageFilter filter){
        algorithmModleDBMapper.insertAlgorithmMVAVFilter(filter);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertAlgorithmFODLFilter(  FirstOrderLagFilter filter){
        algorithmModleDBMapper.insertAlgorithmFODLFilter(filter);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteAlgorithmfilter(int filterid){
        algorithmModleDBMapper.deleteAlgorithmfilterbyfilterid(filterid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateallshockdetectalgorithmfilterip(String oldip,  String newip){
        algorithmModleDBMapper.updateallshockdetectalgorithmfilterip(oldip,newip);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateallshockdetectalgorithmpropertiesip(String oldip, String newip){
        algorithmModleDBMapper.updateallshockdetectalgorithmpropertiesip(oldip,newip);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateallalgorithmips(String oldip, String newip){
        algorithmModleDBMapper.updateallshockdetectalgorithmfilterip(oldip,newip);
        algorithmModleDBMapper.updateallshockdetectalgorithmpropertiesip(oldip,newip);
    }





}

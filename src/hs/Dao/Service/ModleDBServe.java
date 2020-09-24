package hs.Dao.Service;

import hs.Bean.BaseConf;
import hs.Bean.ControlModle;
import hs.Bean.ModlePin;
import hs.Bean.ResponTimeSerise;
import hs.Dao.ModleDBMapper;
import hs.Filter.Filter;
import hs.Filter.FirstOrderLagFilter;
import hs.Filter.MoveAverageFilter;
import hs.ShockDetect.ShockDetector;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/18 14:01
 */
@Service
public class ModleDBServe {
    private ModleDBMapper modleMapper;


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ControlModle> getAllModle() {
        return modleMapper.getModles();
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BaseConf getBaseConf() {
        return modleMapper.getBaseConf();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertModle(ControlModle controller) {
        modleMapper.insertModle(controller);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertModleAndAutoPin(ControlModle controller,ModlePin modlePins) {
        modleMapper.insertModle(controller);
        modlePins.setReference_modleId(controller.getModleId());
        modleMapper.insertModlePin(modlePins);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateModleAndAutopin(ControlModle controller,ModlePin modlepin) {
        modleMapper.modifymodle(controller.getModleId(),controller);
        modleMapper.updatepin(modlepin);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateModleInsertAutopin(ControlModle controller,ModlePin modlepin) {
        modleMapper.modifymodle(controller.getModleId(),controller);
        modleMapper.insertModlePin(modlepin);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertpvandsp(ModlePin pvpin, ModlePin pvenablepin, ModlePin pvuppin, ModlePin pvdownpin, ShockDetector shockDetector, Filter pinfiter, ModlePin sppin) {
        CRUDMOldePin(pvpin);
        CRUDMOldePin(pvenablepin);
        CRUDMOldePin(pvuppin);
        CRUDMOldePin(pvdownpin);
        CRUDMOldePin(sppin);
        shockDetector.setPk_pinid(pvpin.getModlepinsId());
        CRUDShocker(shockDetector);
        pinfiter.setPk_pinid(pvpin.getModlepinsId());
        CRUDFilter(pinfiter);
    }



    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertff(ModlePin ffpin,ModlePin ffuppin,ModlePin ffdownpin,ModlePin ffenablepin,Filter ffpinfilter){

        CRUDMOldePin(ffpin);
        CRUDMOldePin(ffuppin);
        CRUDMOldePin(ffdownpin);
        CRUDMOldePin(ffenablepin);
        ffpinfilter.setPk_pinid(ffpin.getModlepinsId());
        CRUDFilter(ffpinfilter);

    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertmvandmvfb(ModlePin mvpin, ModlePin mvdownpin,ModlePin mvuppin, ModlePin mvfbpin, Filter mvfbfilter, ShockDetector mvfbshockDetector, ModlePin mvenbalepin) {
        CRUDMOldePin(mvpin);
        CRUDMOldePin(mvdownpin);
        CRUDMOldePin(mvuppin);
        CRUDMOldePin(mvfbpin);
        CRUDMOldePin(mvenbalepin);
        mvfbshockDetector.setPk_pinid(mvpin.getModlepinsId());
        CRUDShocker(mvfbshockDetector);
        mvfbfilter.setPk_pinid(mvfbpin.getModlepinsId());
        CRUDFilter(mvfbfilter);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateModleAndDeleteAutopin(ControlModle controller,ModlePin modlepin) {
        modleMapper.modifymodle(controller.getModleId(),controller);
        modleMapper.deleteModlePinbypinid(modlepin.getModlepinsId());
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deletePinAndComphone(List<ModlePin> pins){
        for(ModlePin pin: pins){
            if(pin!=null){
                modleMapper.deleteModlePinbypinid(pin.getModlepinsId());
                if(null!=pin.getFilter()) {
                    modleMapper.deletePinsFilterbyfilterid(pin.getFilter().getPk_filterid());
                }
                if(null!=pin.getShockDetector()){
                    modleMapper.removeShockDetetetor(pin.getShockDetector().getPk_shockdetectid());
                }
            }

        }

    }




    @Transactional(isolation = Isolation.READ_COMMITTED)
    void updatepin(ModlePin pin){
        modleMapper.updatepin(pin);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deletemodle(ControlModle modle){
        modleMapper.deleteModle(modle.getModleId());
        modleMapper.deleteModleRespbymodleid(modle.getModleId());
        modleMapper.deleteModlePins(modle.getModleId());
        List<ModlePin> pins=modle.getModlePins();
        if(pins!=null){
            for(ModlePin pin:pins){
                if(pin.getFilter()!=null){
                    modleMapper.deletePinsFilter(pin.getModlepinsId());
                }

                if(pin.getShockDetector()!=null){
                    modleMapper.removeShockDetetetor(pin.getShockDetector().getPk_shockdetectid());
                }
            }
        }
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ModlePin> pagepinsbymodleid(int modleid,String pintype,int page,int pagesize){
        return modleMapper.pagepinsbymodleid(modleid,pintype,page,pagesize);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ModlePin> pinsbypintype(int modleid,String pintype){
        return modleMapper.pinsbypintype(modleid,pintype);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ModlePin findPinbypinid(int pinid){
        return modleMapper.findPinbypinid(pinid);
    }



    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ResponTimeSerise findPinbyresponid(int responid){
        return modleMapper.findPinbyresponid(responid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ModlePin findPinbypinmodleidAndpinname(int modleid,String pinname){
        return modleMapper.findPinbypinmodleidAndpinname(modleid,pinname);
    }



    @Transactional(isolation = Isolation.READ_COMMITTED)
    public int countPVpinsbymodleid(int modleid,String pintype){
        return modleMapper.countpinsbymodleid(modleid,pintype);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ResponTimeSerise> pageresponbymodleid( int modleid,int page,  int pagesize){
        return modleMapper.pageresponymodleid(modleid,page,pagesize);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public int countresponbymodleid(int modleid){
        return modleMapper.countresponbymodleid(modleid);
    }





    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteModlePins(int modleid) {
        modleMapper.deleteModlePins(modleid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void modifymodleEnable(int modleid, int enable) {
        modleMapper.updatemodleEnable(modleid, enable);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updatepinEnable(int pinid,int enable){
        modleMapper.updatepinEnable(pinid, enable);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteModle(int modleid) {
        modleMapper.deleteModle(modleid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ControlModle getModle(int modleid) {
        return modleMapper.getModle(modleid);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteModleRespbymodleid(int modleid) {
        modleMapper.deleteModleRespbymodleid(modleid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteModleRespbyresponid(int responid) {
        modleMapper.deleteModleRespbyresponid(responid);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void modifymodle(int modleid, ControlModle controlModle) {
        modleMapper.modifymodle(modleid, controlModle);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertModlePins(List<ModlePin> modlePins) {
        modleMapper.insertModlePins(modlePins);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertPinsMVAVFilter(MoveAverageFilter filter) {
        modleMapper.insertPinsMVAVFilter(filter);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertPinsFODLFilter(FirstOrderLagFilter filter) {
        modleMapper.insertPinsFODLFilter(filter);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deletePinsFilter(int modlepinsId) {
        modleMapper.deletePinsFilter(modlepinsId);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertModleResp(List<ResponTimeSerise> responTimeSerises) {
        modleMapper.insertModleResps(responTimeSerises);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertOrUpdateTimeSerise(ResponTimeSerise responTimeSerise)
    {CRUDRESP(responTimeSerise);}

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void insertShockDetetetor(ShockDetector shockDetector) {
        modleMapper.insertShockDetetetor(shockDetector);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ShockDetector getShockDetetetor(int id) {
        return modleMapper.getShockDetetetor(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeShockDetetetor(int id) {
        modleMapper.removeShockDetetetor(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateShockDetetetor(ShockDetector shockDetector) {
        modleMapper.updateShockDetetetor(shockDetector);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateallpinip(String oldip,String newip){
        modleMapper.updateallpinip(oldip,newip);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateallfilterip(String oldip, String newip){
        modleMapper.updateallfilterip(oldip,newip);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public  void updateallshockdetectip(String oldip, String newip){
        modleMapper.updateallshockdetectip(oldip,newip);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateallshockdetectfilterip( String oldip, String newip){
        modleMapper.updateallshockdetectfilterip(oldip,newip);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateallmodleips( String oldip, String newip){
        modleMapper.updateallpinip(oldip,newip);
        modleMapper.updateallfilterip(oldip,newip);
        modleMapper.updateallshockdetectip(oldip,newip);
        modleMapper.updateallshockdetectfilterip(oldip,newip);
    }


    private void CRUDFilter(Filter filter){
        if(filter.getPk_filterid()==-1){
            /****以前没有
             * 没有id*/
            if(filter.getFiltername()!=null){
                /**现在有*/

                if(filter.getFiltername().equals("fodl")){
                    modleMapper.insertPinsFODLFilter((FirstOrderLagFilter) filter);
                }else if(filter.getFiltername().equals("mvav")){
                    modleMapper.insertPinsMVAVFilter((MoveAverageFilter) filter);
                }

            }else {
                /**现在也没用*/
            }

        }else {
            /*****
             * 以前有
             * ***有id,有tag********/
            if(filter.getFiltername()!=null){
                /**现在有*/

                if(filter.getFiltername().equals("fodl")){
                    modleMapper.updatePinsFODLFilter((FirstOrderLagFilter)filter);
                }else if(filter.getFiltername().equals("mvav")){
                    modleMapper.updatePinsMVAVFilter((MoveAverageFilter)filter);
                }


            }else {
                /**现在无*/
                modleMapper.deletePinsFilter(filter.getPk_pinid());
            }
        }
    }




    private void CRUDRESP(ResponTimeSerise respon){
        if(respon.getModletagId()==-1){
            /****以前没有
             * 没有id*/
            modleMapper.insertModleResp(respon);
        }else {
            /*****
             * 以前有s
             * ***有id,有tag********/
            modleMapper.updateModleResp(respon);
        }
    }



    private void CRUDShocker(ShockDetector shocker){

        if(shocker.getPk_shockdetectid()==-1){
            /****以前没有
             * 没有id*/
            if(shocker.getWindowstime()!=null){
                /**现在有*/
                modleMapper.insertShockDetetetor(shocker);

            }else {
                /**现在也没用*/
            }

        }else {
            /*****
             * 以前有
             * ***有id,有tag********/
            if(shocker.getWindowstime()!=null){
                /**现在有*/
                modleMapper.updateShockDetetetor(shocker);

            }else {
                /**现在无*/
                modleMapper.removeShockDetetetor(shocker.getPk_shockdetectid());
            }
        }

    }



    private void CRUDMOldePin(ModlePin pin){
        if(pin.getModlepinsId()==-1){
            /****以前没有
             * 没有id*/
            if(!pin.getModleOpcTag().equals("")){
                /**现在有*/
                modleMapper.insertModlePin(pin);

            }else {
                /**现在也没用*/
            }

        }else {
            /*****
             * 以前有
             * ***有id,有tag********/
            if(!pin.getModleOpcTag().equals("")){
                /**现在有*/
                modleMapper.updatepin(pin);

            }else {
                /**现在无*/
                modleMapper.deleteModlePinbypinid(pin.getModlepinsId());
            }
        }

    }


    @Autowired
    public void setModleMapper(ModleDBMapper modleMapper) {
        this.modleMapper = modleMapper;
    }
}

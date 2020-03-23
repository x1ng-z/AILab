package hs.Bean;

import hs.Dao.Service.ModleServe;
import hs.Opc.OPCserver;
import hs.Utils.ResponComput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/21 16:13
 */
@Component("apcmodles")
public class ModleConstainer {

    private OPCserver OPCserver;
    private Map<Integer,ControlModle> Modules;
    private ModleServe modleServe;

    @Autowired
    public ModleConstainer(OPCserver OPCserver, ModleServe modleServe) {
        this.OPCserver = OPCserver;
        this.modleServe=modleServe;

        Modules=new HashMap<>();
        List<ControlModle> controlModleList=modleServe.getAllModle();
        for(ControlModle controlModle:controlModleList){
            controlModle.setOPCserver(OPCserver);
            for(ModleTag modleTag:controlModle.getUnhandleTag()){
                modleTag.setResponTimeSeries(ResponComput.responTimeSeries(modleTag.getStepRespJson()));
            }
            controlModle.realizeModle();

            Modules.put(controlModle.getModleId(),controlModle);
        }

    }

    public  void selfclose(){
        for(ControlModle controlModle:Modules.values()){
            controlModle.getExecutePythonBridge().destory();
        }
    }

    public Map<Integer, ControlModle> getModules() {
        return Modules;
    }
}

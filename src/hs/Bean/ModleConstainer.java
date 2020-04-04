package hs.Bean;

import hs.ApcAlgorithm.ExecutePythonBridge;
import hs.Dao.Service.ModleServe;
import hs.Opc.OPCService;
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

    private OPCService OPCserver;
    private Map<Integer,ControlModle> Modules;
    private ModleServe modleServe;
    private BaseConf baseConf;

    @Autowired
    public ModleConstainer(OPCService OPCserver, ModleServe modleServe,BaseConf baseConf) {
        this.OPCserver = OPCserver;
        this.modleServe=modleServe;
        this.baseConf=baseConf;

        Modules=new HashMap<>();
        List<ControlModle> controlModleList=modleServe.getAllModle();
        for(ControlModle controlModle:controlModleList){
            controlModle.setOPCserver(OPCserver);
            controlModle.setBaseConf(baseConf);
            controlModle.modleBuild();

            Modules.put(controlModle.getModleId(),controlModle);
        }

    }

    public void registerModle(ControlModle controlModle){
        controlModle.setOPCserver(OPCserver);
        controlModle.setBaseConf(baseConf);
        controlModle.modleBuild();
        Modules.put(controlModle.getModleId(),controlModle);

        ExecutePythonBridge executePythonBridge=new ExecutePythonBridge(
                "C:\\Program Files\\apache-tomcat-9.0.14\\webapps\\AILab\\LinkAPC.exe",
                "http://localhost:8080/AILab/python/modlebuild/"+controlModle.getModleId()+".do",controlModle.getModleId()+"");
        controlModle.setExecutePythonBridge(executePythonBridge);
        if(controlModle.getEnable()==1){
            executePythonBridge.execute();
        }

    }
    public  void selfclose(){
        for(ControlModle controlModle:Modules.values()){
            controlModle.getExecutePythonBridge().stop();
        }
    }

    public Map<Integer, ControlModle> getModules() {
        return Modules;
    }
}

package hs.Bean;

import hs.ApcAlgorithm.ExecutePythonBridge;
import hs.Dao.Service.ModleDBServe;
import hs.Opc.Monitor.ModleStopRunMonitor;
import hs.Opc.OPCService;
import hs.Opc.OpcServicConstainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 模块容器
 * @author zzx
 * @version 1.0
 * @date 2020/3/21 16:13
 */
@Component("apcmodles")
@PropertySource("classpath:apc.properties")
@DependsOn("opcServicConstainer")
public class ModleConstainer {

    private OpcServicConstainer opcServicConstainer;
    private Map<Integer, ControlModle> Modulepool;
    private ModleDBServe modleDBServe;
    private BaseConf baseConf;
    @Value("${apc.dir}")
    private String apcdir;

    @Autowired
    public ModleConstainer(OpcServicConstainer opcServicConstainer, ModleStopRunMonitor modleStopRunMonitor, ModleDBServe modleDBServe, BaseConf baseConf) {
        this.opcServicConstainer = opcServicConstainer;
        this.modleDBServe = modleDBServe;
        this.baseConf = baseConf;
        Modulepool = new ConcurrentHashMap<>();
        modleStopRunMonitor.setModleConstainer(this);
        
    }

    public void selfinit(){
        List<ControlModle> controlModleList = modleDBServe.getAllModle();
        for (ControlModle controlModle : controlModleList) {
            controlModle.setOpcServicConstainer(opcServicConstainer);
            controlModle.setBaseConf(baseConf);
            controlModle.modleBuild();
            Modulepool.put(controlModle.getModleId(), controlModle);
            ExecutePythonBridge executePythonBridge = new ExecutePythonBridge(
                    apcdir,
                    "http://localhost:8080/AILab/python/modlebuild/" + controlModle.getModleId() + ".do", controlModle.getModleId() + "");
            controlModle.setExecutePythonBridge(executePythonBridge);
            if (controlModle.getModleEnable() == 1) {
                executePythonBridge.execute();
            }

        }
    }

    public void registerModle(ControlModle controlModle) {
        if (!Modulepool.containsKey(controlModle.getModleId())) {
            controlModle.setOpcServicConstainer(opcServicConstainer);
            controlModle.setBaseConf(baseConf);
            controlModle.modleBuild();
            Modulepool.put(controlModle.getModleId(), controlModle);
        }

        ExecutePythonBridge executePythonBridge = new ExecutePythonBridge(apcdir,
                "http://localhost:8080/AILab/python/modlebuild/" + controlModle.getModleId() + ".do", controlModle.getModleId() + "");
        controlModle.setExecutePythonBridge(executePythonBridge);
        if (controlModle.getModleEnable() == 1) {
            executePythonBridge.execute();
        }

    }

    public void selfclose() {
        for (ControlModle controlModle : Modulepool.values()) {
            controlModle.getExecutePythonBridge().stop();
        }
    }

    public Map<Integer, ControlModle> getModulepool() {
        return Modulepool;
    }
}

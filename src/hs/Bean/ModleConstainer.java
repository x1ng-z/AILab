package hs.Bean;

import hs.ApcAlgorithm.ExecutePythonBridge;
import hs.Dao.Service.ModleDBServe;
import hs.Opc.OpcServicConstainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模块容器
 *
 * @author zzx
 * @version 1.0
 * @date 2020/3/21 16:13
 */
@Component("apcmodles")
@PropertySource("classpath:apc.properties")
@DependsOn("opcServicConstainer")
public class ModleConstainer {
    @Autowired
    public void setOpcServicConstainer(OpcServicConstainer opcServicConstainer) {
        this.opcServicConstainer = opcServicConstainer;
    }

    private OpcServicConstainer opcServicConstainer;
    /**可运行模型池*/
    private Map<Integer, ControlModle> runnableModulepool;
    private ModleDBServe modleDBServe;
    private BaseConf baseConf;
    @Value("${apc.dir}")
    @NonNull
    private String apcdir;

    @Value("${simulator.dir}")
    @NonNull
    private String simulatordir;

    @Autowired
    public ModleConstainer(ModleDBServe modleDBServe, BaseConf baseConf) {

        this.modleDBServe = modleDBServe;
        this.baseConf = baseConf;
        runnableModulepool = new ConcurrentHashMap<>();
    }

    public void selfinit() {
        List<ControlModle> controlModleList = modleDBServe.getAllModle();
        for (ControlModle controlModle : controlModleList) {
            /**
             * 1初始化控制模型的重要属性，使其成为真正的控制器
             * 2、对控制器进行构建
             * 3放置到模型池中
             * */
            controlModle.toBeRealControlModle(opcServicConstainer,baseConf,simulatordir);
            if(controlModle.modleBuild(true)){
                /**只有构建成功的模型才可以加入到可运行模型池*/
                runnableModulepool.put(controlModle.getModleId(), controlModle);
            }

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
        if (!runnableModulepool.containsKey(controlModle.getModleId())) {
            controlModle.setOpcServicConstainer(opcServicConstainer);
            controlModle.setBaseConf(baseConf);
            controlModle.setSimulatorbuilddir(simulatordir);
            controlModle.modleBuild(true);
            runnableModulepool.put(controlModle.getModleId(), controlModle);
        }

        ExecutePythonBridge executePythonBridge = new ExecutePythonBridge(apcdir,
                "http://localhost:8080/AILab/python/modlebuild/" + controlModle.getModleId() + ".do", controlModle.getModleId() + "");
        controlModle.setExecutePythonBridge(executePythonBridge);
        if (controlModle.getModleEnable() == 1) {
            controlModle.modleCheckStatusRun();
            controlModle.getSimulatControlModle().simulateModleRun();
        }

    }


    public void selfclose() {
        for (ControlModle controlModle : runnableModulepool.values()) {
            controlModle.getExecutePythonBridge().stop();
        }
    }

    public Map<Integer, ControlModle> getRunnableModulepool() {
        return runnableModulepool;
    }
}

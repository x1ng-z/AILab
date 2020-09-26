package hs.Bean;

import hs.ApcAlgorithm.ExecutePythonBridge;
import hs.Dao.Service.ModleDBServe;
import hs.Opc.OpcServicConstainer;
import org.apache.log4j.Logger;
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
    public static Logger logger = Logger.getLogger(ModleConstainer.class);

    @Autowired
    public void setOpcServicConstainer(OpcServicConstainer opcServicConstainer) {
        this.opcServicConstainer = opcServicConstainer;

        opcServicConstainer.getModleRebuildService().setModleConstainer(this);
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
            registerModle(controlModle);
        }
    }

    public synchronized void registerModle(ControlModle controlModle) {
        if (!runnableModulepool.containsKey(controlModle.getModleId())) {
            controlModle.toBeRealControlModle(apcdir,opcServicConstainer,baseConf,simulatordir);
            controlModle.modleBuild(true);
            runnableModulepool.put(controlModle.getModleId(), controlModle);
        }else {
            logger.warn("重复注册模型");
        }
    }


    public synchronized void unregisterModle(ControlModle controlModle){
        if (runnableModulepool.containsKey(controlModle.getModleId())) {
            controlModle.unregisterpin();
            controlModle.disableModleByWeb();
            runnableModulepool.remove(controlModle);
        }else {
            logger.warn("不是可运行的模型");
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

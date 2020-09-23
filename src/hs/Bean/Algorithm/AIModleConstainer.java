package hs.Bean.Algorithm;

import hs.ApcAlgorithm.ExecutePythonBridge;
import hs.Bean.ControlModle;
import hs.Dao.Service.AlgorithmDBServe;
import hs.Opc.ItemMangerContext;
import hs.Opc.OpcServicConstainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/9/1 5:46
 */

@Component
public class AIModleConstainer {

    private OpcServicConstainer opcServicConstainer;
    private Map<Integer, AlgorithmModle> Modulepool;
    private AlgorithmDBServe algorithmDBServe;
    private ItemMangerContext itemMangerContext;


    @Autowired
    public AIModleConstainer(OpcServicConstainer opcServicConstainer, AlgorithmDBServe algorithmDBServe, ItemMangerContext itemMangerContext) {
        this.opcServicConstainer = opcServicConstainer;
        this.algorithmDBServe= algorithmDBServe;
        this.itemMangerContext=itemMangerContext;
        Modulepool=new ConcurrentHashMap<>();
    }




    public void selfinit() {
        List<AlgorithmModle>  algorithmModles=algorithmDBServe.getAlgorithmModles();
        for(AlgorithmModle algorithmModle:algorithmModles){
            registerModle(algorithmModle);
        }
    }


    /**注册*/
    public void registerModle(AlgorithmModle algorithmModle) {
        if (!Modulepool.containsKey(algorithmModle.getModleid())) {
            algorithmModle.setOpcServicConstainer(opcServicConstainer);
            algorithmModle.setItemMangerContext(itemMangerContext);
            algorithmModle.build();
            Modulepool.put(algorithmModle.getModleid(), algorithmModle);
        }

    }


    /**注销*/
    public void unregisterModle(AlgorithmModle algorithmModle){
        if (Modulepool.containsKey(algorithmModle.getModleid())) {
            algorithmModle.unregisterproperties2OPC();
            Modulepool.remove(algorithmModle.getModleid());
        }

    }



    public Map<Integer, AlgorithmModle> getModulepool() {
        return Modulepool;
    }
}

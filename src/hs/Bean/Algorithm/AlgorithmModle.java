package hs.Bean.Algorithm;

import hs.Bean.Modle;
import hs.Bean.PictureJPG;
import hs.Opc.ItemMangerContext;
import hs.Opc.OpcServicConstainer;

import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/8/31 15:26
 */
public class AlgorithmModle implements Modle {

    private int modleid;
    private String algorithmName;//'算法名称',
    private Instant updatetime;
    private List<AlgorithmProperty> algorithmProperties;//这里是否需要加一个校验的位号



    private OpcServicConstainer opcServicConstainer;
    private ItemMangerContext itemMangerContext;
    private final Pattern opcpattern = Pattern.compile("([a-zA-Z]*)([0-9|.]*)");


    /**
     * 模型构建
     * */
    public void build() {
        registerproperties2OPC();
    }


    /**
     *模型重构
     * */
    public void rebuild() {
        registerproperties2OPC();
    }


    /**
     * register a opc tag of property  into serve.
     * filter of property
     * */
    public void registerproperties2OPC() {

        for (AlgorithmProperty algorithmProperty : algorithmProperties) {
            if((opcServicConstainer.isAnyConnectOpcServe()) && (algorithmProperty != null)){
                opcServicConstainer.registerModlePinAndComponent(algorithmProperty);
            }

        }
    }

    /**unregister a opc tag of property  out  serve*/
    public boolean unregisterproperties2OPC(){
        boolean result=true;
        for (AlgorithmProperty algorithmProperty : algorithmProperties) {
            if((opcServicConstainer.isAnyConnectOpcServe()) && (algorithmProperty != null)){
                result =opcServicConstainer.unregisterModlePinAndComponent(algorithmProperty)&&result;
            }
        }

        return result;
    }




    /**
     * check is the filter have filter
     * have filter to write back by filtertask
     * no filter write directly
     */
    public boolean writePropertiesValue() {
        boolean result = true;
        for (AlgorithmProperty property : algorithmProperties) {
            result = opcServicConstainer.writeModlePinValue(property, property.getValue())&&result;
        }
        return result;
    }


    public int getModleid() {
        return modleid;
    }

    public void setModleid(int modleid) {
        this.modleid = modleid;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public Instant getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Instant updatetime) {
        this.updatetime = updatetime;
    }

    public List<AlgorithmProperty> getAlgorithmProperties() {
        return algorithmProperties;
    }

    public void setAlgorithmProperties(List<AlgorithmProperty> algorithmProperties) {
        this.algorithmProperties = algorithmProperties;
    }

    public OpcServicConstainer getOpcServicConstainer() {
        return opcServicConstainer;
    }

    public void setOpcServicConstainer(OpcServicConstainer opcServicConstainer) {
        this.opcServicConstainer = opcServicConstainer;
    }

    public ItemMangerContext getItemMangerContext() {
        return itemMangerContext;
    }

    public void setItemMangerContext(ItemMangerContext itemMangerContext) {
        this.itemMangerContext = itemMangerContext;
    }


}

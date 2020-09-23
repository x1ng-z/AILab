package hs.Opc;

import hs.Controller.OPCInfoController;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/8/31 10:56
 */
@Component
public class ItemMangerContext {
    private OpcServicConstainer opcServicConstainer;
    /**key=opcserve ip,*/
    private Map<String,ItemManger> itemMangerPool=new ConcurrentHashMap();


    public ItemManger generateNewItemManger(String ip){
         ItemManger itemManger=new ItemManger();
        itemMangerPool.put(ip,itemManger);
         return itemManger;
    }


    public Map<String, ItemManger> getItemMangerPool() {
        return itemMangerPool;
    }

    public ItemUnit getItemUnit(String ip ,String opctag){
        ItemManger itemManger=itemMangerPool.get(ip);
        if(itemManger!=null){
           return  itemManger.getOpcitemunitPool().get(opctag);
        }
        return null;
    }


    public void setOpcServicConstainer(OpcServicConstainer opcServicConstainer) {
        this.opcServicConstainer = opcServicConstainer;
    }
}

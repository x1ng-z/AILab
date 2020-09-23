package hs.Opc;


import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.openscada.opc.lib.da.Item;
import org.springframework.stereotype.Component;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/7/16 17:29
 */

public class ItemManger {
    private static final Logger logger = Logger.getLogger(ItemManger.class);

    private Map<String, ItemUnit> opcitemunitPool = new ConcurrentHashMap<>();//key=标签，value=ItemUnit
    private Map<String, Item> opcitemPool = new ConcurrentHashMap<>();//key=标签，value=ItemUnit
    private Map<Item,String> itemAndTag=new ConcurrentHashMap<>();//为了方便通过item来找到opc位号

    public boolean iscontainstag(String tagname){
        return opcitemunitPool.containsKey(tagname);
    }

    public void addItemUnit(String tagname,ItemUnit itemUnit){
        if(itemUnit.getItem()!=null){
            additem(tagname,itemUnit.getItem());
            additemunit(tagname,itemUnit);
            itemAndTag.put(itemUnit.getItem(),tagname);
        }else {
            logger.error("Item 为空！");
        }
    }

    public String findTagnamebyItem(Item item){
        return  this.itemAndTag.get(item);
    }


    public ItemUnit removeItemUnit(String tagname){
        ItemUnit itemUnit=opcitemunitPool.remove(tagname);
        opcitemPool.remove(tagname);
        itemAndTag.remove(itemUnit.getItem());
        return itemUnit;
    }


    public ItemUnit getItemUnit(String tagname){
        return opcitemunitPool.get(tagname);
    }

    public Item getItem(String tagname){
        return opcitemPool.get(tagname);
    }


    private void additem(String tagname, Item item) {
        opcitemPool.put(tagname, item);
    }

    private void additemunit(String tagname, ItemUnit itemUnit) {
        opcitemunitPool.put(tagname, itemUnit);
    }

    public Map<String, ItemUnit> getOpcitemunitPool() {
        return opcitemunitPool;
    }

    public Map<String, Item> getOpcitemPool() {
        return opcitemPool;
    }

    public Item[] getOpcitemPoolArraystyle() {
        Item[] result=new Item[opcitemPool.size()];
        int index=0;
        for(Item item:opcitemPool.values()){
            result[index]=item;
            index++;
        }
        return result;
    }



}

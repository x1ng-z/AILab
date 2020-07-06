package hs.Opc;

import org.openscada.opc.lib.da.Item;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/6/15 11:51
 */
public class ItemUnit {
    private  Item item;//opc iterm
    private volatile int refrencecount=0;//pin、filter的引用次数

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getRefrencecount() {
        return refrencecount;
    }

    public void setRefrencecount(int refrencecount) {
        this.refrencecount = refrencecount;
    }

    public synchronized void  addrefrencecount(){
        refrencecount++;
    }
    public synchronized void minsrefrencecount(){
        refrencecount--;
    }

    public  synchronized boolean isnorefrence(){
        if(refrencecount==0){
            return true;
        }else {
            return false;
        }

    }
}

package hs.Opc;

import hs.Bean.Tag;
import hs.Dao.ModleMapper;
import org.apache.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/23 12:10
 */
@Component()
public class OPCService implements Runnable{
    private static final Logger logger=Logger.getLogger(OPCService.class);
    private Server server;
    private ModleMapper modleMapper;
    private Map<Integer,Tag> opctags;
    private Group group=null;
    private Map<Integer,Item> itemLists=new ConcurrentHashMap<>();

    @Autowired
    public OPCService(Server server, ModleMapper modleMapper) {
        this.server=server;
        this.modleMapper=modleMapper;
    }

    public void selfinit(){
        try {
            server.connect();
        } catch (UnknownHostException e) {
            logger.error(e);
        } catch (JIException e) {
            logger.error(e);
        } catch (AlreadyConnectedException e) {
            logger.error(e);
        }
        opctags=modleMapper.getAllTags();
        for(Integer tagid:opctags.keySet()){
            logger.debug(opctags.get(tagid).getTagName()+opctags.get(tagid).getTagId());
        }
        logger.debug("opc selfinit"+opctags.size());
        try {
            group =server.addGroup("opc");
        } catch (UnknownHostException e) {
            logger.error(e);
        } catch (NotConnectedException e) {
            logger.error(e);
        } catch (JIException e) {
            logger.error(e);
        } catch (DuplicateGroupException e) {
            logger.error(e);
        }
        if(group!=null){
            logger.debug("group no null");
            for(Tag tag:opctags.values()){
                try {
                    itemLists.put(tag.getTagId(),group.addItem(tag.getTag()));
                    logger.debug("register "+tag.getTagId()+" "+tag.getTagName()+" success");
                } catch (JIException e) {
                    logger.error(e);
                } catch (AddFailedException e) {
                    logger.error(e);
                }
            }

        }


    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()){
            logger.debug("opc serve run"+itemLists.size());

            for(Map.Entry<Integer, Item> integerItemEntry:itemLists.entrySet()){
                try {
                    logger.debug("update"+integerItemEntry.getKey());
                    Tag uptag=opctags.get(integerItemEntry.getKey());
                    String stringvalue=integerItemEntry.getValue().read(false).getValue().getObject().toString();
                    logger.info(stringvalue);
                    if(stringvalue.equals("true")){
                        stringvalue=1+"";
                    }
                    if(stringvalue.equals("false")){
                        stringvalue=0+"";
                    }
                    uptag.updateValue(Double.valueOf(stringvalue));
                    logger.debug(uptag.getTagName()+uptag.getNewvalue());
                } catch (JIException e) {
                    logger.error(e);
                }
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                logger.error(e);
            }

        }

    }

    public Double readTagvalue(Integer tagid){
        return opctags.get(tagid).getNewvalue();
    }


    public Double readTagDeltaValue(Integer tagid){
        return opctags.get(tagid).diffBetweenSample();
    }

    public boolean writeTagvalue(Integer tagid,Double value){
        Item item=itemLists.get(tagid);
        if(item!=null){
            try {
                item.write( new JIVariant(value,false));
            } catch (JIException e) {
                logger.error(e);
                return false;
            }
            return true;
        }
        return false;

    }

    public Map<Integer, Tag> getOpctags() {
        return opctags;
    }
}

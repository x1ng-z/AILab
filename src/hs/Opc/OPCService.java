package hs.Opc;

import hs.Bean.ModlePin;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/23 12:10
 */
@Component()
public class OPCService implements Runnable {
    private static final Logger logger = Logger.getLogger(OPCService.class);
    private Server server;
    private ModleMapper modleMapper;
    private Map<String, List<ModlePin>> opctags = new ConcurrentHashMap();//key=标签,value=引脚s
    private Group group = null;
    private Map<String, Item> itemLists = new ConcurrentHashMap<>();

    @Autowired
    public OPCService(Server server, ModleMapper modleMapper) {
        this.server = server;
        this.modleMapper = modleMapper;
    }

    public void selfinit() {
        try {
            server.connect();
        } catch (UnknownHostException e) {
            logger.error(e);
        } catch (JIException e) {
            logger.error(e);
        } catch (AlreadyConnectedException e) {
            logger.error(e);
        }

        logger.debug("opc selfinit" + opctags.size());
        try {
            group = server.addGroup("opc");
        } catch (UnknownHostException e) {
            logger.error(e);
        } catch (NotConnectedException e) {
            logger.error(e);
        } catch (JIException e) {
            logger.error(e);
        } catch (DuplicateGroupException e) {
            logger.error(e);
        }
        if (group != null) {
            logger.debug("group no null");
            for (String tag : opctags.keySet()) {
                try {
                    itemLists.put(tag, group.addItem(tag));
                    logger.debug("register " + tag + " success");
                } catch (JIException e) {
                    logger.error(e);
                } catch (AddFailedException e) {
                    logger.error(e);
                }
            }

        }


    }


    public Boolean register(ModlePin modlePins) {
        List<ModlePin> modlePinsList = opctags.get(modlePins.getModleOpcTag());
        if (modlePinsList != null) {
            modlePinsList.add(modlePins);
            return true;
        } else {
            try {
                /***
                 *加入到opc获取数据group
                 * */
                itemLists.put(modlePins.getModleOpcTag(), group.addItem(modlePins.getModleOpcTag()));
                modlePinsList = new ArrayList<>();
                /**
                 * 注册
                 * */
                modlePinsList.add(modlePins);
                opctags.put(modlePins.getModleOpcTag(), modlePinsList);

                return true;
            } catch (JIException e) {
                logger.error(e);
                return false;
            } catch (AddFailedException e) {
                logger.error(e);
                return false;
            }

        }


    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            logger.debug("opc serve run" + itemLists.size());

            for (Map.Entry<String, Item> integerItemEntry : itemLists.entrySet()) {
                try {

                    List<ModlePin> pins = opctags.get(integerItemEntry.getKey());
                    String stringvalue = integerItemEntry.getValue().read(false).getValue().getObject().toString();
                    logger.debug("update" + integerItemEntry.getKey()+"value: "+stringvalue);
                    if (stringvalue.equals("true")) {
                        stringvalue = 1 + "";
                    }
                    if (stringvalue.equals("false")) {
                        stringvalue = 0 + "";
                    }
                    for(ModlePin pin:pins){
                        pin.opcUpdateValue(Double.valueOf(stringvalue));
                    }
                } catch (JIException e) {
                    logger.error(e);
                }catch (Exception e){
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


    public boolean writeTagvalue(String tag, Double value) {
        Item item = itemLists.get(tag);
        if (item != null) {
            try {
                item.write(new JIVariant(value, false));
                return true;
            } catch (JIException e) {
                logger.error(e);
                return false;
            }

        }
        return false;

    }

    public Map<String, List<ModlePin>> getOpctags() {
        return opctags;
    }
}

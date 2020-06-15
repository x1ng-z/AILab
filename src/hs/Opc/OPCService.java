package hs.Opc;

import hs.Bean.BaseConf;
import hs.Bean.ModlePin;
import hs.Dao.ModleMapper;
import hs.Filter.*;
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
import java.util.LinkedList;
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
    private Map<String, List<ModlePin>> opctagModlePinPool = new ConcurrentHashMap();//key=标签,value=引脚s
    private Map<String, List<Filter>> opctagFilterPool = new ConcurrentHashMap();//key=filetr反写的opctag,value=filter
    private Group group = null;
    private Map<String, Item> opcitemPool = new ConcurrentHashMap<>();//key=标签，value=OPCItem
    private BaseConf baseConf;
    private Item varytag = null;
    private FilterService filterService;
    private boolean connectStatus = false;

    @Autowired
    public OPCService(Server server, ModleMapper modleMapper, BaseConf baseConf, FilterService filterService) {
        this.server = server;
        this.modleMapper = modleMapper;
        this.baseConf = baseConf;
        this.filterService = filterService;
    }

    public void selfinit() {
        try {
            server.connect();
            connectStatus = true;
        } catch (UnknownHostException e) {
            logger.error(e);
        } catch (JIException e) {
            logger.error(e);
        } catch (AlreadyConnectedException e) {
            logger.error(e);
        }

        //logger.debug("opc selfinit" + opctagModlePinPool.size());
        try {
            group = server.addGroup("opc");
            /**设置平台通信验证点号，这个要实时写入*/
            if (baseConf.getVerification() != null && !baseConf.getVerification().trim().equals("")) {
                try {
                    varytag = group.addItem(baseConf.getVerification().trim());
                } catch (JIException e) {
                    logger.error(e);
                } catch (AddFailedException e) {
                    logger.error(e);
                }
            }

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
            for (String tag : opctagModlePinPool.keySet()) {
                try {
                    opcitemPool.put(tag, group.addItem(tag));
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

        List<ModlePin> modlePinsList = opctagModlePinPool.get(modlePins.getModleOpcTag());
        if (modlePinsList != null) {
            modlePinsList.add(modlePins);
//            return true;
        } else {
            try {
                /***
                 *加入到opc获取数据group
                 * */
                opcitemPool.put(modlePins.getModleOpcTag(), group.addItem(modlePins.getModleOpcTag()));
                modlePinsList = new ArrayList<>();
                /**
                 * 注册
                 * */
                modlePinsList.add(modlePins);
                opctagModlePinPool.put(modlePins.getModleOpcTag(), modlePinsList);

//                return true;
            } catch (JIException e) {
                logger.error(e);
                return false;
            } catch (AddFailedException e) {
                logger.error(e);
                return false;
            }

        }

        if ((modlePins.getFilter() != null) && (modlePins.getFilter().getBackToDCSTag() != null) && (!modlePins.getFilter().getBackToDCSTag().trim().equals(""))) {
            List<Filter> filterList;
            filterList = opctagFilterPool.get(modlePins.getFilter().getBackToDCSTag());
            if (filterList != null) {
                filterList.add(modlePins.getFilter());
            } else {
                try {
                    if (!opcitemPool.containsKey(modlePins.getFilter().getBackToDCSTag())) {
                        opcitemPool.put(modlePins.getFilter().getBackToDCSTag(), group.addItem(modlePins.getFilter().getBackToDCSTag()));
                    }
                    filterList = new LinkedList();
                    opctagFilterPool.put(modlePins.getFilter().getBackToDCSTag(), filterList);
                    filterList.add(modlePins.getFilter());
                } catch (JIException e) {
                    logger.error(e);
                    return false;
                } catch (AddFailedException e) {
                    logger.error(e);
                    return false;
                }


            }

        }
        return true;

    }


    public Boolean unregister(ModlePin modlePins) {

        List<ModlePin> modlePinsList = opctagModlePinPool.get(modlePins.getModleOpcTag());
        if (modlePinsList != null) {
            modlePinsList.remove(modlePins);
            /**
             * 移除filter opciterm
             * */
            if ((modlePins.getFilter() != null) && (modlePins.getFilter().getBackToDCSTag() != null) && (!modlePins.getFilter().getBackToDCSTag().equals(""))) {

                Item removeItem = opcitemPool.remove(modlePins.getFilter().getBackToDCSTag());
                if (removeItem != null) {
                    try {
                        group.removeItem(removeItem.getId());
                    } catch (UnknownHostException e) {
                        logger.error(e);
                    } catch (JIException e) {
                        logger.error(e);
                    }
                }
            }


            if (modlePinsList.size() == 0) {
                /**
                 * 当移除opc点位时候，如果对应的点位没有任何的模型引脚了，那么需要移除opc点位
                 * 1、移除opc点位池
                 * 2、移除group中item
                 * 3、移除tag模型引脚池中的引脚列表
                 * */
                Item removeItem = opcitemPool.remove(modlePins.getModleOpcTag());
                if (removeItem != null) {
                    try {
                        group.removeItem(removeItem.getId());
                    } catch (UnknownHostException e) {
                        logger.error(e);
                    } catch (JIException e) {
                        logger.error(e);
                    }
                }
                opctagModlePinPool.remove(modlePins.getModleOpcTag());

            }
            return true;
        } else {
            return false;
        }


    }


    @Override
    public void run() {
        Integer writevloop = 0;
        while (!Thread.currentThread().isInterrupted()) {

            if (!connectStatus) {
                logger.info("reconnect opc server");
                reconnect();
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    logger.error(e);
                }
                continue;
            }

            logger.debug("opc serve run" + opcitemPool.size());

            /**平台通信校验位号写入数据*/
            if (baseConf.getVerification() != null && !baseConf.getVerification().trim().equals("")) {
                try {
                    varytag.write(new JIVariant(new JIVariant((Float) writevloop.floatValue())));
                    if (writevloop++ > 100) {
                        writevloop = 0;
                    }
                } catch (JIException e) {
                    logger.error(e);
                    connectStatus = false;
                    continue;
                }
            }


            /**实时读取数据**/
            for (Map.Entry<String, Item> integerItemEntry : opcitemPool.entrySet()) {
                String stringvalue = null;
                try {

                    List<ModlePin> pins = opctagModlePinPool.get(integerItemEntry.getKey());
                    stringvalue = integerItemEntry.getValue().read(false).getValue().getObject().toString();
                    //logger.debug("update" + integerItemEntry.getKey() + "value: " + stringvalue);
                    if (stringvalue.equals("true") || stringvalue.equals("on")) {
                        stringvalue = 1 + "";
                    }
                    if (stringvalue.equals("false") || stringvalue.equals("off")) {
                        stringvalue = 0 + "";
                    }
                    for (ModlePin pin : pins) {
                        //更新引脚数据数据
                        pin.opcUpdateValue(Double.valueOf(stringvalue));

                        //检查是否存在滤波器，存在的话则根据滤波器类型生成滤波器执行任务
                        if (pin.getFilter() != null) {

                            if (pin.getFilter() instanceof FirstOrderLagFilter) {

                                FirstOrderLagFilter folf = (FirstOrderLagFilter) pin.getFilter();
                                //更新本次数据采样数据
                                folf.setsampledata(Double.valueOf(stringvalue));

                                //新建过滤器的执行任务
                                FiltTask folftask = new FiltTask();
                                folftask.setFilter(folf);

                                if ((folf.getBackToDCSTag() != null) && (!folf.getBackToDCSTag().equals(""))) {
                                    folftask.setItem(opcitemPool.get(folf.getBackToDCSTag()));
                                }

                                folftask.setUnfiltdata(Double.valueOf(stringvalue));//未滤波的数据

                                filterService.putfiltertask(folftask);

                            } else if (pin.getFilter() instanceof MoveAverageFilter) {

                                MoveAverageFilter mvav = (MoveAverageFilter) pin.getFilter();
                                //更新本次采集数据
                                mvav.setsampledata(Double.valueOf(stringvalue));

                                //新建滤波器执行任务
                                FiltTask mvavtask = new FiltTask();
                                mvavtask.setFilter(mvav);
                                //抽取本次需要滤波的窗口数据
                                mvavtask.setUnfiltdatas(mvav.getUnfilterdatas());
                                filterService.putfiltertask(mvavtask);
                            }

                        }
                    }
                } catch (JIException e) {
                    logger.error(e);
                    connectStatus = false;
                    break;

                } catch (Exception e) {
                    logger.error(e);
                    logger.error("tagname" + integerItemEntry.getKey() + "value:" + stringvalue);
                }
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                logger.error(e);
            }

        }

    }


    public void reconnect() {
        /***/
        try {
            server.removeGroup(group, true);
            server.disconnect();
            opcitemPool = new ConcurrentHashMap<>();
            selfinit();
        } catch (JIException jiException) {
            logger.error(jiException);
        } catch (Exception exception) {
            logger.error(exception);
        }
    }


    public boolean writeTagvalue(String tag, Double value) {
        Item item = opcitemPool.get(tag);
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

    public void selfclose() {
        try {
            server.removeGroup(group, true);
            server.disconnect();
        } catch (JIException e) {
            logger.error(e);
        }
    }

    public Map<String, List<ModlePin>> getOpctagModlePinPool() {
        return opctagModlePinPool;
    }

    public Group getGroup() {
        return group;
    }

    public Map<String, List<Filter>> getOpctagFilterPool() {
        return opctagFilterPool;
    }
}

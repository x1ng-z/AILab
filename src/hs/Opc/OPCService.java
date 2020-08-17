package hs.Opc;

import hs.Bean.ModleConstainer;
import hs.Bean.ModlePin;
import hs.Filter.*;
import hs.Opc.Monitor.*;
import hs.ShockDetect.ShockDetector;
import org.apache.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.*;

import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/23 12:10
 */

public class OPCService implements Runnable {
    private static final Logger logger = Logger.getLogger(OPCService.class);
    private static Pattern pvenablepattern = Pattern.compile("(^pvenable\\d+$)");

    /**
     * opc 配置信息
     */
    private int opcserveid;
    private String opcuser;
    private String opcpassword;
    private String opcip;
    private String opcclsid;
    private Server server;
    private ItemManger itemManger = new ItemManger();
    /**
     * 引脚pool,索引是opctag
     * key=标签,value=引脚s
     */
    private Map<String, List<ModlePin>> opctagModlePinPool = new ConcurrentHashMap();
    /**
     * 引脚中的过滤器的pool,索引是opctag
     * key=filetr反写的opctag,value=filter
     */
    private Map<String, List<Filter>> opctagFilterPool = new ConcurrentHashMap();

    private Group group = null;
    /**
     * opc serve中group添加以后的item pool
     */
    private ConcurrentHashMap<String, OpcVeriTag> varytag = new ConcurrentHashMap();

    private List<OpcVeriTag> initopcVeriTagsInDB;

    /**
     * 实时滤波服务
     */
    private FilterService filterService = null;

    private ModleStopRunMonitor modleStopRunMonitor;

    /**
     * 链接状态
     */
    private boolean connectStatus = false;


    private ModleConstainer modleConstainerl;

    public void initAndConnect(FilterService filterService, ModleStopRunMonitor modleStopRunMonitor) {
        this.filterService = filterService;
        this.modleStopRunMonitor = modleStopRunMonitor;
        if (initopcVeriTagsInDB != null) {
            for (OpcVeriTag opcVeriTag : initopcVeriTagsInDB) {
                varytag.put(opcVeriTag.getTag(), opcVeriTag);
            }
        }

        ConnectionInformation ci = new ConnectionInformation();
        ci.setHost(opcip);
        ci.setUser(opcuser);
        ci.setPassword(opcpassword);
        ci.setProgId(null);
        ci.setClsid(opcclsid);

        server = new Server(ci, Executors.newSingleThreadScheduledExecutor());

        try {
            server.connect();
            connectStatus = true;
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        } catch (JIException e) {
            logger.error(e.getMessage(), e);
        } catch (AlreadyConnectedException e) {
            logger.error(e.getMessage(), e);
        }

        try {
            group = server.addGroup("opc");
            /**设置平台通信验证点号，这个要实时写入*/

            for (OpcVeriTag opcVeriTag : varytag.values()) {
                if (opcVeriTag.getTag() != null && !opcVeriTag.getTag().trim().equals("")) {
                    try {
                        opcVeriTag.setItem(group.addItem(opcVeriTag.getTag().trim()));

                    } catch (JIException e) {
                        logger.error(e.getMessage(), e);
                    } catch (AddFailedException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }

        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        } catch (NotConnectedException e) {
            logger.error(e.getMessage(), e);
        } catch (JIException e) {
            logger.error(e.getMessage(), e);
        } catch (DuplicateGroupException e) {
            logger.error(e.getMessage(), e);
        }
        if (group != null) {
            logger.debug("group no null");
            for (String tag : opctagModlePinPool.keySet()) {
                try {
                    //新位号
                    if (!itemManger.iscontainstag(tag)) {
                        Item item = group.addItem(tag);

                        ItemUnit itemUnit = new ItemUnit();
                        itemUnit.setItem(item);
                        itemUnit.addrefrencecount();

                        itemManger.addItemUnit(tag, itemUnit);
                    } else {
                        //位号已添加
                        itemManger.getItemUnit(tag).addrefrencecount();
                    }

                    logger.debug("register " + tag + " success");
                } catch (JIException e) {
                    logger.error(e.getMessage(), e);
                } catch (AddFailedException e) {
                    logger.error(e.getMessage(), e);
                }
            }

        }


    }


    /**
     * 重连初始化
     */
    public void initAndReConnect() {
        //
        if (initopcVeriTagsInDB != null) {
            for (OpcVeriTag opcVeriTag : initopcVeriTagsInDB) {
                varytag.put(opcVeriTag.getTag(), opcVeriTag);
            }
        }

        ConnectionInformation ci = new ConnectionInformation();
        ci.setHost(opcip);
        ci.setUser(opcuser);
        ci.setPassword(opcpassword);
        ci.setProgId(null);
        ci.setClsid(opcclsid);
        server = new Server(ci, Executors.newSingleThreadScheduledExecutor());

        try {
            server.connect();
            connectStatus = true;
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        } catch (JIException e) {
            logger.error(e.getMessage(), e);
        } catch (AlreadyConnectedException e) {
            logger.error(e.getMessage(), e);
        }

        try {
            group = server.addGroup("opc");
            /**设置平台通信验证点号，这个要实时写入*/

            for (OpcVeriTag opcVeriTag : varytag.values()) {
                if (opcVeriTag.getTag() != null && !opcVeriTag.getTag().trim().equals("")) {
                    try {
                        opcVeriTag.setItem(group.addItem(opcVeriTag.getTag().trim()));

                    } catch (JIException e) {
                        logger.error(e.getMessage(), e);
                    } catch (AddFailedException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }

        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        } catch (NotConnectedException e) {
            logger.error(e.getMessage(), e);
        } catch (JIException e) {
            logger.error(e.getMessage(), e);
        } catch (DuplicateGroupException e) {
            logger.error(e.getMessage(), e);
        }
        if (group != null) {
            logger.debug("group no null");
            for (String tag : opctagModlePinPool.keySet()) {
                try {
                    //新位号
                    if (!itemManger.iscontainstag(tag)) {

                        Item item = group.addItem(tag);

                        ItemUnit itemUnit = new ItemUnit();
                        itemUnit.setItem(item);
                        itemUnit.addrefrencecount();

                        itemManger.addItemUnit(tag, itemUnit);
                    } else {
                        //位号已添加
                        itemManger.getItemUnit(tag).addrefrencecount();
                    }

                    logger.debug("register " + tag + " success");
                } catch (JIException e) {
                    logger.error(e.getMessage(), e);
                } catch (AddFailedException e) {
                    logger.error(e.getMessage(), e);
                }
            }

        }


    }


    public Boolean registerModlePin(ModlePin modlePins) {
        /**
         * pinpool中将opc位号对应的pin容器获取出来
         * 如果没有的，创建一个pin容器，将pin的opc位号添加进opcgroup，并添加进pin的容器中
         * */
        List<ModlePin> modlePinsList = opctagModlePinPool.get(modlePins.getModleOpcTag());
        if (modlePinsList != null) {
            //添加pins至list
            modlePinsList.add(modlePins);
            //增加引用
            itemManger.getItemUnit(modlePins.getModleOpcTag()).addrefrencecount();
            logger.info(modlePins.getModleOpcTag() + "位号注册成功" + " refrence:" + itemManger.getItemUnit(modlePins.getModleOpcTag()).getRefrencecount());
        } else {
            try {
                /***
                 *加入到opc获取数据group
                 * */
                if (!itemManger.iscontainstag(modlePins.getModleOpcTag())) {
                    //添加opc位号至service
                    Item item = group.addItem(modlePins.getModleOpcTag());

                    ItemUnit itemUnit = new ItemUnit();
                    itemUnit.setItem(item);
                    itemUnit.addrefrencecount();//

                    itemManger.addItemUnit(modlePins.getModleOpcTag(), itemUnit);
                    logger.debug(modlePins.getModleOpcTag() + "新位号注册成功" + " refrence:" + itemUnit.getRefrencecount());
                } else {
                    //位号已添加
                    itemManger.getItemUnit(modlePins.getModleOpcTag()).addrefrencecount();
                    logger.debug(modlePins.getModleOpcTag() + "位号已添加" + "refrence:" + itemManger.getItemUnit(modlePins.getModleOpcTag()).getRefrencecount());
                }
                modlePinsList = new ArrayList<>();
                /**
                 * 注册
                 * */
                modlePinsList.add(modlePins);
                opctagModlePinPool.put(modlePins.getModleOpcTag(), modlePinsList);
            } catch (JIException e) {
                logger.error("位号添加失败: " + modlePins.getModleOpcTag());
                logger.error(e.getMessage(), e);
                return false;
            } catch (AddFailedException e) {
                logger.error("位号添加失败: " + modlePins.getModleOpcTag());
                logger.error(e.getMessage(), e);
                return false;
            } catch (Exception e) {
                logger.error("位号添加失败: " + modlePins.getModleOpcTag());
                logger.error(e.getMessage(), e);
                return false;
            }

        }
        return true;

    }

    public Boolean registerFilter(ModlePin modlePins) {
        /**
         *
         *滤波器位号
         * 1、判断是否位号是否有List(有的话就是已经加到group里了)  是，则添加到pool的list中，并增加opctag的索引
         * 2、否，则加入group，并新建一个list，放入filter，并添加至opctagFilterPool
         *
         * */
        if ((modlePins.getFilter() != null) && (modlePins.getFilter().getBackToDCSTag() != null) && (!modlePins.getFilter().getBackToDCSTag().trim().equals(""))) {

            List<Filter> filterList = opctagFilterPool.get(modlePins.getFilter().getBackToDCSTag());

            if (filterList != null) {
                //有相同位号的filter了
                filterList.add(modlePins.getFilter());
                itemManger.getItemUnit(modlePins.getFilter().getBackToDCSTag()).addrefrencecount();
                logger.info(modlePins.getFilter().getBackToDCSTag() + "位号注册成功" + " refrence:" + itemManger.getItemUnit(modlePins.getFilter().getBackToDCSTag()).getRefrencecount());
            } else {
                try {

                    if (!itemManger.iscontainstag(modlePins.getFilter().getBackToDCSTag())) {
                        //加入group
                        Item item = group.addItem(modlePins.getFilter().getBackToDCSTag());

                        ItemUnit itemUnit = new ItemUnit();
                        itemUnit.setItem(item);
                        itemUnit.addrefrencecount();

                        itemManger.addItemUnit(modlePins.getFilter().getBackToDCSTag(), itemUnit);
                        logger.info(modlePins.getFilter().getBackToDCSTag() + "新位号注册成功" + " refrence:" + itemUnit.getRefrencecount());
                    } else {
                        //位号已添加
                        itemManger.getItemUnit(modlePins.getFilter().getBackToDCSTag()).addrefrencecount();
                        logger.warn(modlePins.getFilter().getBackToDCSTag() + "位号已添加");
                    }

                    filterList = new LinkedList();
                    filterList.add(modlePins.getFilter());
                    opctagFilterPool.put(modlePins.getFilter().getBackToDCSTag(), filterList);
                } catch (JIException e) {
                    logger.error("位号添加失败: " + modlePins.getFilter().getBackToDCSTag());
                    logger.error(e.getMessage(), e);
                    return false;
                } catch (AddFailedException e) {
                    logger.error("位号添加失败: " + modlePins.getFilter().getBackToDCSTag());
                    logger.error(e.getMessage(), e);
                    return false;
                } catch (Exception e) {
                    logger.error("位号添加时候报错: " + modlePins.getFilter().getBackToDCSTag());
                    logger.error(e.getMessage(), e);
                    return false;
                }


            }

        }
        return true;

    }


    public Boolean registerShockDetectortag(String opctag) {
        /**
         * 2、否，则加入group，并新建一个list，放入filter，并添加至opctagFilterPool
         *
         * */
        if ((opctag != null) && (!opctag.trim().equals(""))) {
            try {
                if (!itemManger.iscontainstag(opctag)) {
                    //加入group
                    Item item = group.addItem(opctag);
                    ItemUnit itemUnit = new ItemUnit();
                    itemUnit.setItem(item);
                    itemUnit.addrefrencecount();

                    itemManger.addItemUnit(opctag, itemUnit);
                    logger.info(opctag + "新位号注册成功" + " refrence:" + itemUnit.getRefrencecount());
                } else {
                    //位号已添加
                    itemManger.getItemUnit(opctag).addrefrencecount();
                    logger.warn(opctag + "位号已添加");
                }

            } catch (JIException e) {
                logger.error("位号添加失败: " + opctag);
                logger.error(e.getMessage(), e);
                return false;
            } catch (AddFailedException e) {
                logger.error("位号添加失败: " + opctag);
                logger.error(e.getMessage(), e);
                return false;
            } catch (Exception e) {
                logger.error("位号添加时候报错: " + opctag);
                logger.error(e.getMessage(), e);
                return false;
            }
        }
        return true;
    }


    public Boolean unregisterModlePin(ModlePin modlePins) {
        List<ModlePin> modlePinsList = opctagModlePinPool.get(modlePins.getModleOpcTag());
        if (modlePinsList != null) {
            modlePinsList.remove(modlePins);
            itemManger.getItemUnit(modlePins.getModleOpcTag()).minsrefrencecount();
            logger.info("位号移除成功:" + modlePins.getModleOpcTag() + "refence" + itemManger.getItemUnit(modlePins.getModleOpcTag()).getRefrencecount());
            if (modlePinsList.size() == 0) {//pins的pool里为0，那么肯定没有其他pins引用这个位号了
                /**
                 * 当移除opc点位时候，如果对应的点位没有任何的模型引脚了，那么需要移除opc点位
                 * 2如果引用用完： 2.1、移除opc点位池  2.2、移除group中item
                 * 3、移除tag模型引脚池中的引脚列表
                 * */
                if (itemManger.getItemUnit(modlePins.getModleOpcTag()).isnorefrence()) {
                    ItemUnit removeItem = itemManger.removeItemUnit(modlePins.getModleOpcTag());
                    if (removeItem != null) {
                        try {
                            group.removeItem(removeItem.getItem().getId());
                            logger.info("opcSERVE位号:" + modlePins.getModleOpcTag() + "refrence:" + removeItem.getRefrencecount());
                        } catch (UnknownHostException e) {
                            logger.error(e.getMessage(), e);
                        } catch (JIException e) {
                            logger.error(e.getMessage(), e);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    } else {
                        logger.info("opcSERVE位号未找到，移除失败:" + modlePins.getModleOpcTag());
                    }

                }
                opctagModlePinPool.remove(modlePins.getModleOpcTag());
                logger.info(modlePins.getModleOpcTag() + "所有引脚全部移除");
            }
            return true;
        } else {
            logger.info("位号不存在,移除失败:" + modlePins.getModleOpcTag());
            return false;
        }


    }


    public Boolean unregisterFilter(ModlePin modlePins) {
        /**
         * 移除filter opciterm
         * */
        if ((modlePins.getFilter() != null) && (modlePins.getFilter().getBackToDCSTag() != null) && (!modlePins.getFilter().getBackToDCSTag().equals(""))) {
            //移除filterpool中的filter
            opctagFilterPool.get(modlePins.getFilter().getBackToDCSTag()).remove(modlePins.getFilter());
            //削减引用
            itemManger.getItemUnit(modlePins.getFilter().getBackToDCSTag()).minsrefrencecount();
            logger.info("位号移除:" + modlePins.getModleOpcTag() + "refrence:" + itemManger.getItemUnit(modlePins.getFilter().getBackToDCSTag()).getRefrencecount());

            if (itemManger.getItemUnit(modlePins.getFilter().getBackToDCSTag()).isnorefrence()) {

                ItemUnit removeItem = itemManger.removeItemUnit(modlePins.getFilter().getBackToDCSTag());
                if (removeItem != null) {
                    try {
                        group.removeItem(removeItem.getItem().getId());
                        logger.info("opcSERVE位号移除:" + modlePins.getFilter().getBackToDCSTag() + "refrence:" + removeItem.getRefrencecount());
                    } catch (UnknownHostException e) {
                        logger.error(e.getMessage(), e);
                    } catch (JIException e) {
                        logger.error(e.getMessage(), e);
                    }
                } else {
                    logger.info("opcSERVE位号未找到，移除失败:" + modlePins.getFilter().getBackToDCSTag() + "refrence:" + removeItem.getRefrencecount());
                }

            }

            if (opctagFilterPool.get(modlePins.getFilter().getBackToDCSTag()).size() == 0) {
                //如果引用全部用完，那么把pool中的list也移除
                opctagFilterPool.remove(modlePins.getFilter().getBackToDCSTag());
                logger.info(modlePins.getFilter().getBackToDCSTag() + "所有引脚全部移除");
            }
        }

        return true;
    }






    /**移除一般的opctag*/
    public Boolean unregisterCommonOPCTag(String opctag) {
        /**
         * 移除filter opciterm
         * */
        if ((opctag != null) && (!opctag.equals(""))) {
            //移除filterpool中的filter
//            opctagFilterPool.get(modlePins.getFilter().getBackToDCSTag()).remove(modlePins.getFilter());
            //削减引用
            itemManger.getItemUnit(opctag).minsrefrencecount();
            logger.info("位号移除:" + opctag + "refrence:" + itemManger.getItemUnit(opctag).getRefrencecount());
            if (itemManger.getItemUnit(opctag).isnorefrence()) {
                ItemUnit removeItem = itemManger.removeItemUnit(opctag);
                if (removeItem != null) {
                    try {
                        group.removeItem(removeItem.getItem().getId());
                        logger.info("opcSERVE位号移除:" + opctag + "refrence:" + removeItem.getRefrencecount());
                    } catch (UnknownHostException e) {
                        logger.error(e.getMessage(), e);
                    } catch (JIException e) {
                        logger.error(e.getMessage(), e);
                    }
                } else {
                    logger.info("opcSERVE位号未找到，移除失败:" + opctag + "refrence:");
                }
            }
        }
        return true;
    }



    public Boolean unregisterShockdetectortag(String opctag) {
        /**
         * 移除filter opciterm
         * */
        if ((opctag != null) && (!opctag.equals(""))) {
            //移除filterpool中的filter
//            opctagFilterPool.get(modlePins.getFilter().getBackToDCSTag()).remove(modlePins.getFilter());
            //削减引用
            itemManger.getItemUnit(opctag).minsrefrencecount();
            logger.info("位号移除:" + opctag + "refrence:" + itemManger.getItemUnit(opctag).getRefrencecount());
            if (itemManger.getItemUnit(opctag).isnorefrence()) {
                ItemUnit removeItem = itemManger.removeItemUnit(opctag);
                if (removeItem != null) {
                    try {
                        group.removeItem(removeItem.getItem().getId());
                        logger.info("opcSERVE位号移除:" + opctag + "refrence:" + removeItem.getRefrencecount());
                    } catch (UnknownHostException e) {
                        logger.error(e.getMessage(), e);
                    } catch (JIException e) {
                        logger.error(e.getMessage(), e);
                    }
                } else {
                    logger.info("opcSERVE位号未找到，移除失败:" + opctag + "refrence:");
                }
            }
        }
        return true;
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
                    logger.error(e.getMessage(), e);
                }
                continue;
            }

            logger.debug("opc serve run" + itemManger.getOpcitemunitPool().size());

            /**平台通信校验位号写入数据*/
            for (OpcVeriTag opcVeriTag : varytag.values()) {
                if (opcVeriTag.getTag() != null && !opcVeriTag.getTag().trim().equals("") && (opcVeriTag.getItem() != null)) {
                    try {
                        opcVeriTag.getItem().write(new JIVariant(new JIVariant((Float) writevloop.floatValue())));
                        if (writevloop++ > 100) {
                            writevloop = 0;
                        }
                    } catch (JIException e) {
                        logger.error(e.getMessage(), e);
                        connectStatus = false;
                        continue;
                    }
                }
            }


            //数据读取
            long startgetdate = System.currentTimeMillis();
            if (itemManger.getOpcitemunitPool().size() > 0) {
                try {
                    readAndProcessDataByOnce();
                } catch (JIException e) {
                    logger.error(e.getMessage(), e);
                    connectStatus = false;//进行重新连接
                }
            }
            long endgetdate = System.currentTimeMillis();
            long spendtime = endgetdate - startgetdate;
            logger.info("所有数据处理耗时=" + spendtime + "ms");

            try {
                TimeUnit.MILLISECONDS.sleep((1000 - spendtime) > 0 ? (1000 - spendtime) : 0);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }

        }

    }


    /**
     * 一次性获取所有数据，并进行处理
     */
    public void readAndProcessDataByOnce() throws JIException {
        Map<Item, ItemState> itemItemStateMap = group.read(true, itemManger.getOpcitemPoolArraystyle());
        logger.info("in readAndProcessDataByOnce size=" + itemItemStateMap.size());
        int errortagnamenum = 0;//取数报错的tag
        for (Map.Entry<Item, ItemState> entrie : itemItemStateMap.entrySet()) {
            Item item = entrie.getKey();
            ItemState itemState = entrie.getValue();
            try {
                pinValueUpdate(item, itemState);
            } catch (JIException e) {
                ++errortagnamenum;
                logger.error("opc tag=" + itemManger.findTagnamebyItem(item) + " maybe error when get real data");
                logger.error(e.getMessage(), e);
            } catch (Exception e) {
                ++errortagnamenum;
                logger.error(e.getMessage(), e);
            }
        }
        logger.info("error tag num=" + errortagnamenum);
        if (errortagnamenum == itemManger.getOpcitemunitPool().size()) {
            connectStatus = false;//进行重新连接
        }

    }


    /**
     * s数据处理分发
     */
    public void pinValueUpdate(Item item, ItemState itemState) throws JIException {

        String valueStringstyle = itemState.getValue().getObject().toString();
        String opcname = itemManger.findTagnamebyItem(item);
        if (opcname == null) {
            //位号被移除了
            return;
        }
        List<ModlePin> pins = opctagModlePinPool.get(opcname);

        if (pins == null) {
            //pins列表为null说明这个不是pin引脚的tag，可能是filter的opctag，这个主要是用于反写的，不需要读取
            return;
        }

        if (valueStringstyle.equals("true") || valueStringstyle.equals("on")) {
            valueStringstyle = 1 + "";
        }
        if (valueStringstyle.equals("false") || valueStringstyle.equals("off")) {
            valueStringstyle = 0 + "";
        }
        for (ModlePin pin : pins) {
            //更新引脚数据数据
            pin.opcUpdateValue(Double.valueOf(valueStringstyle));

            //DCS手自动切换监视
            if (pin.getModlePinName() != null && pin.getModlePinName().trim().equals(ModlePin.TYPE_PIN_MODLE_AUTO)) {
                logger.info("modle id=" + pin.getReference_modleId() + "old value=" + pin.getOldReadValue() + ",new value=" + pin.getNewReadValue());
                if ((pin.getOldReadValue() != null) && (pin.getOldReadValue() == 0) && (pin.getNewReadValue() != 0) /*&& (modleConstainerl != null) && (modleConstainerl.getModulepool().get(pin.getReference_modleId()).getModleEnable() == 0)*/) {
                    //run
                    logger.debug("模型运行，modleid=" + pin.getReference_modleId());
                    ModleRunTask modleRunTask = new ModleRunTask();
                    modleRunTask.setModleid(pin.getReference_modleId());
                    modleStopRunMonitor.putTask(modleRunTask);
                    logger.debug("模型运行，modleid=" + pin.getReference_modleId());

                } else if ((pin.getOldReadValue() != null) && (pin.getOldReadValue() != 0) && (pin.getNewReadValue() == 0) /*&& (modleConstainerl != null) && (modleConstainerl.getModulepool().get(pin.getReference_modleId()).getModleEnable() == 1)*/) {
                    //stop
                    logger.debug("模型停止，modleid=" + pin.getReference_modleId());
                    ModleStopTask modleStopTask = new ModleStopTask();
                    modleStopTask.setModleid(pin.getReference_modleId());
                    modleStopRunMonitor.putTask(modleStopTask);
                    logger.debug("模型停止，modleid=" + pin.getReference_modleId());
                } else {
//                    logger.debug("手自动位号未进行切换，modleid=" + pin.getReference_modleId());
                }
            }


            /**引脚使能数值监视*/
            if(pin.getModlePinName()!=null && pvenablepattern.matcher(pin.getModlePinName()).find()){
                logger.info("modle id=" + pin.getReference_modleId() + "old value=" + pin.getOldReadValue() + ",new value=" + pin.getNewReadValue());
                if((pin.getOldReadValue() != null) && (pin.getOldReadValue() == 0) && (pin.getNewReadValue() != 0)){
                    /**checkin*/
                    logger.debug("模型运行，modleid=" + pin.getReference_modleId()+"pinid="+pin.getModlepinsId()+"切入");
                    ModlePinCheckin modlePinCheckin=new ModlePinCheckin();
                    modlePinCheckin.setModleid(pin.getReference_modleId());
                    modlePinCheckin.setPinid(pin.getModlepinsId());
                    modleStopRunMonitor.putTask(modlePinCheckin);

                }else if((pin.getOldReadValue() != null) && (pin.getOldReadValue() != 0) && (pin.getNewReadValue() == 0)) {
                    /**checkout*/
                    logger.debug("模型运行，modleid=" + pin.getReference_modleId()+"pinid="+pin.getModlepinsId()+"切出");
                    ModlePinCheckout modlePinCheckout=new ModlePinCheckout();
                    modlePinCheckout.setModleid(pin.getReference_modleId());
                    modlePinCheckout.setPinid(pin.getModlepinsId());
                    modleStopRunMonitor.putTask(modlePinCheckout);
                }

            }




            //检查是否存在滤波器，存在的话则根据滤波器类型生成滤波器执行任务
            if (pin.getFilter() != null) {

                if (pin.getFilter() instanceof FirstOrderLagFilter) {

                    FirstOrderLagFilter folf = (FirstOrderLagFilter) pin.getFilter();
                    //更新本次数据采样数据
                    folf.setsampledata(Double.valueOf(valueStringstyle));

                    //新建过滤器的执行任务
                    FiltTask folftask = new FiltTask();
                    folftask.setFilter(folf);

                    if ((folf.getBackToDCSTag() != null) && (!folf.getBackToDCSTag().equals(""))) {
                        folftask.setItemfilterback(itemManger.getItemUnit(folf.getBackToDCSTag()).getItem());
                    }

                    folftask.setUnfiltdata(Double.valueOf(valueStringstyle));//未滤波的数据

                    filterService.putfiltertask(folftask);

                } else if (pin.getFilter() instanceof MoveAverageFilter) {

                    MoveAverageFilter mvav = (MoveAverageFilter) pin.getFilter();
                    //更新本次采集数据
                    mvav.setsampledata(Double.valueOf(valueStringstyle));

                    //新建滤波器执行任务
                    FiltTask mvavtask = new FiltTask();
                    mvavtask.setFilter(mvav);

                    if ((mvav.getBackToDCSTag() != null) && (!mvav.getBackToDCSTag().equals(""))) {
                        mvavtask.setItemfilterback(itemManger.getItemUnit(mvav.getBackToDCSTag()).getItem());
                    }

                    //抽取本次需要滤波的窗口数据
                    mvavtask.setUnfiltdatas(mvav.getUnfilterdatas());
                    filterService.putfiltertask(mvavtask);
                }

            }

            //震荡检测器
            if (pin.getShockDetector() != null) {
                ShockDetector shockDetector = pin.getShockDetector();
                FirstOrderLagFilter filterindetect = pin.getShockDetector().getFirstOrderLagFilterl();

                filterindetect.setsampledata(Double.valueOf(valueStringstyle));
                FiltTask folftask = new FiltTask();
                folftask.setFilter(filterindetect);

                if ((filterindetect.getBackToDCSTag() != null) && (!filterindetect.getBackToDCSTag().trim().equals(""))) {
                    folftask.setItemfilterback(itemManger.getItemUnit(filterindetect.getBackToDCSTag()).getItem());
                }
                if ((shockDetector.getBackToDCSTag() != null) && (!shockDetector.getBackToDCSTag().trim().equals(""))) {
                    folftask.setItemshockback(itemManger.getItemUnit(shockDetector.getBackToDCSTag()).getItem());
                }

                folftask.setUnfiltdata(Double.valueOf(valueStringstyle));//未滤波的数据

                folftask.setShockDetector(pin.getShockDetector());

                filterService.putfiltertask(folftask);
            }


        }
    }


    /**
     * onebyone 获取oopc点位数据，并处理
     */
//    @Deprecated
//    public void readAndProcessDataByIter() {
//        /**实时读取数据**/
//        for (Map.Entry<String, ItemUnit> integerItemEntry : itemManger.getOpcitemunitPool().entrySet()) {
//            String stringvalue = null;
//            try {
//
//                List<ModlePin> pins = opctagModlePinPool.get(integerItemEntry.getKey());
//
//                stringvalue = integerItemEntry.getValue().getItem().read(false).getValue().getObject().toString();
//
//
//                if (pins == null) {
//                    //pins列表为null说明这个不是pin引脚的tag，可能是filter的opctag，这个主要是用于反写的，不需要读取
//                    continue;
//                }
//                //logger.debug("update" + integerItemEntry.getKey() + "value: " + stringvalue);
//                if (stringvalue.equals("true") || stringvalue.equals("on")) {
//                    stringvalue = 1 + "";
//                }
//                if (stringvalue.equals("false") || stringvalue.equals("off")) {
//                    stringvalue = 0 + "";
//                }
//                for (ModlePin pin : pins) {
//                    /**更新引脚数据数据*/
//                    pin.opcUpdateValue(Double.valueOf(stringvalue));
//
//                    /**DCS手自动切换监视*/
//                    if (pin.getModlePinName() != null && pin.getModlePinName().equals(ModlePin.TYPE_PIN_MODLE_AUTO)) {
//                        if (pin.getNewReadValue() == 1 && (modleConstainerl != null) && (modleConstainerl.getModulepool().get(pin.getReference_modleId()).getModleEnable() == 0)) {
//                            /**run*/
//                            ModleRunTask modleRunTask = new ModleRunTask();
//                            modleRunTask.setModleid(pin.getReference_modleId());
//                            modleStopRunMonitor.putTask(modleRunTask);
//                        } else if (pin.getNewReadValue() == 0 && (modleConstainerl != null) && (modleConstainerl.getModulepool().get(pin.getReference_modleId()).getModleEnable() == 1)) {
//                            /**stop*/
//                            ModleStopTask modleStopTask = new ModleStopTask();
//                            modleStopTask.setModleid(pin.getReference_modleId());
//                            modleStopRunMonitor.putTask(modleStopTask);
//                        } else {
//                            logger.debug("手自动位号未进行切换，modleid=" + pin.getReference_modleId());
//                        }
//                    }
//
//                    /**检查是否存在滤波器，存在的话则根据滤波器类型生成滤波器执行任务*/
//                    if (pin.getFilter() != null) {
//
//                        if (pin.getFilter() instanceof FirstOrderLagFilter) {
//
//                            FirstOrderLagFilter folf = (FirstOrderLagFilter) pin.getFilter();
//                            //更新本次数据采样数据
//                            folf.setsampledata(Double.valueOf(stringvalue));
//
//                            //新建过滤器的执行任务
//                            FiltTask folftask = new FiltTask();
//                            folftask.setFilter(folf);
//
//                            if ((folf.getBackToDCSTag() != null) && (!folf.getBackToDCSTag().equals(""))) {
//                                folftask.setItemfilterback(itemManger.getItemUnit(folf.getBackToDCSTag()).getItem());
//                            }
//
//                            folftask.setUnfiltdata(Double.valueOf(stringvalue));//未滤波的数据
//
//                            filterService.putfiltertask(folftask);
//
//                        } else if (pin.getFilter() instanceof MoveAverageFilter) {
//
//                            MoveAverageFilter mvav = (MoveAverageFilter) pin.getFilter();
//                            //更新本次采集数据
//                            mvav.setsampledata(Double.valueOf(stringvalue));
//
//                            //新建滤波器执行任务
//                            FiltTask mvavtask = new FiltTask();
//                            mvavtask.setFilter(mvav);
//
//                            if ((mvav.getBackToDCSTag() != null) && (!mvav.getBackToDCSTag().equals(""))) {
//                                mvavtask.setItemfilterback(itemManger.getItemUnit(mvav.getBackToDCSTag()).getItem());
//                            }
//
//
//                            //抽取本次需要滤波的窗口数据
//                            mvavtask.setUnfiltdatas(mvav.getUnfilterdatas());
//                            filterService.putfiltertask(mvavtask);
//                        }
//
//                    }
//                }
//            } catch (JIException e) {
//                logger.error(e.getMessage(), e);
//                connectStatus = false;
//                break;
//
//            } catch (Exception e) {
//                logger.error(e.getMessage(), e);
//                logger.error("tagname" + integerItemEntry.getKey() + "value:" + stringvalue);
//            }
//        }
//    }


    public void reconnect() {
        /***/
        try {
            server.removeGroup(group, true);
            server.disconnect();
        } catch (JIException jiException) {
            logger.error(jiException.getMessage(), jiException);
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
        }
        itemManger = new ItemManger();
        initAndReConnect();
    }


    public boolean writeTagvalue(String tag, Double value) {
        ItemUnit item = itemManger.getItemUnit(tag);
        if (item != null) {
            try {
                item.getItem().write(new JIVariant(value, false));
                return true;
            } catch (JIException e) {
                logger.error(e.getMessage(), e);
                return false;
            }

        }
        return false;

    }

    public void disconnect() {
        try {
            group.clear();
            server.removeGroup(group, true);
            server.disconnect();
        } catch (JIException e) {
            logger.error(e.getMessage(), e);
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

    public FilterService getFilterService() {
        return filterService;
    }

    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }

    public List<OpcVeriTag> getInitopcVeriTagsInDB() {
        return initopcVeriTagsInDB;
    }

    public void setInitopcVeriTagsInDB(List<OpcVeriTag> initopcVeriTagsInDB) {
        this.initopcVeriTagsInDB = initopcVeriTagsInDB;
    }

    public int getOpcserveid() {
        return opcserveid;
    }

    public void setOpcserveid(int opcserveid) {
        this.opcserveid = opcserveid;
    }

    public String getOpcuser() {
        return opcuser;
    }

    public void setOpcuser(String opcuser) {
        this.opcuser = opcuser;
    }

    public String getOpcpassword() {
        return opcpassword;
    }

    public void setOpcpassword(String opcpassword) {
        this.opcpassword = opcpassword;
    }

    public String getOpcip() {
        return opcip;
    }

    public void setOpcip(String opcip) {
        this.opcip = opcip;
    }

    public String getOpcclsid() {
        return opcclsid;
    }

    public void setOpcclsid(String opcclsid) {
        this.opcclsid = opcclsid;
    }

    public ModleConstainer getModleConstainerl() {
        return modleConstainerl;
    }

    public void setModleConstainerl(ModleConstainer modleConstainerl) {
        this.modleConstainerl = modleConstainerl;
    }
}

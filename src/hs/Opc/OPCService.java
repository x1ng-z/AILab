package hs.Opc;

import hs.Bean.Algorithm.AlgorithmProperty;
import hs.Bean.ModlePin;
import hs.Bean.OPCComponent;
import hs.Filter.*;
import hs.ServiceBus.*;
import hs.ShockDetect.ShockDetector;
import org.apache.log4j.Logger;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.*;

import java.net.UnknownHostException;
import java.time.Instant;
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
    private static final boolean DEBUG = true;
    private static Pattern pvenablepattern = Pattern.compile("(^pvenable\\d+$)");
    private static Pattern pvpattern = Pattern.compile("(^pv\\d+$)");
    private static Pattern ffpattern = Pattern.compile("(^ff\\d+$)");
    private static Pattern mvpattern = Pattern.compile("(^mv\\d+$)");

    private Pattern opcpattern = Pattern.compile("([a-zA-Z]*)([0-9|.]*)");

    /**
     * opc 配置信息
     */
    private int opcserveid;
    private String opcuser;
    private String opcpassword;
    private String opcip;
    private String opcclsid;
    private List<OpcVeriTag> initopcVeriTagsInDB;//直接mabaties中初始化来的


    private Server server;
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


    /**
     * 引脚中的shock detect
     */
    private Map<String, List<ShockDetector>> opctagShockDetectPool = new ConcurrentHashMap();


    /**
     * 引脚中的shock detect，滤波器反写
     */
    private Map<String, List<Filter>> opctagShockDetectFilterPool = new ConcurrentHashMap();


    /**
     * 引脚pool,索引是opctag
     * key=标签
     * ai模型的属性
     */
    private Map<String, List<AlgorithmProperty>> opctagAIModlePropertyPool = new ConcurrentHashMap();


    /**
     * 引脚中的过滤器的pool,索引是opctag
     * key=filetr反写的opctag,value=filter
     * ai模型的属性的过滤器
     */
    private Map<String, List<Filter>> opctagAIModlePropertyFilterPool = new ConcurrentHashMap();


    private Group group = null;


    /**
     * opc serve中group添加以后的item pool
     * key=tag
     */
    private ConcurrentHashMap<String, OpcVeriTag> alreadregistervarytagpool = new ConcurrentHashMap();


    /**
     * 实时滤波服务
     */
    private FilterService filterService = null;

    private ModleRebuildService modleRebuildService;

    private ItemManger itemManger = null;
    /**
     * 链接状态
     */
    private boolean connectStatus = false;


    private ItemMangerContext itemMangerContext;


    /**
     * add ver tag in opc serve group
     *
     * @param opcVeriTag      验证位号
     * @param isneedaddvertag 是否注册进验证位号 dblist
     */
    public void addvertag(OpcVeriTag opcVeriTag, boolean isneedaddvertag) {
        if (opcVeriTag.getTag() != null && !opcVeriTag.getTag().trim().equals("")) {
            try {

                if (!alreadregistervarytagpool.containsKey(opcVeriTag.getTag())) {
                    /**不包含改位号，则进行注册*/
                    opcVeriTag.setItem(group.addItem(opcVeriTag.getTag().trim()));
                    alreadregistervarytagpool.put(opcVeriTag.getTag(), opcVeriTag);
                } else {
                    logger.warn(opcVeriTag.getTag() + "已经加入了");
                }
                if (isneedaddvertag) {
                    initopcVeriTagsInDB.add(opcVeriTag);
                }
            } catch (JIException e) {
                logger.error(e.getMessage(), e);
            } catch (AddFailedException e) {
                logger.error(e.getMessage(), e);
            }

        }

    }


    /**
     * add ver tag in opc serve group
     *
     * @param opctag 验证位号
     */
    public void removevertag(String opctag) {
        if (opctag != null && !opctag.trim().equals("")) {
            try {
                if (alreadregistervarytagpool.containsKey(opctag)) {
                    /**包含该位号、进行移除操作*/
                    OpcVeriTag opcvertag = alreadregistervarytagpool.get(opctag);
                    group.removeItem(opcvertag.getItem().getId());

                    initopcVeriTagsInDB.remove(opcvertag);
                    alreadregistervarytagpool.remove(opcvertag.getTag());
                }
            } catch (UnknownHostException e) {
                logger.error(e.getMessage(), e);
            } catch (JIException e) {
                logger.error(e.getMessage(), e);
            }
        }

    }


    public void initAndConnect(FilterService filterService, ModleRebuildService modleRebuildService, ItemMangerContext itemMangerContext) {
        this.filterService = filterService;
        this.modleRebuildService = modleRebuildService;
        this.itemMangerContext = itemMangerContext;

        itemManger = itemMangerContext.generateNewItemManger(opcip);

        alreadregistervarytagpool = new ConcurrentHashMap<>();


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
            for (OpcVeriTag opcVeriTag : initopcVeriTagsInDB) {
                addvertag(opcVeriTag, false);
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
            logger.debug("opc connect success");
        }


    }


    /**
     * 重连初始化
     */
    public void initAndReConnect() {
        /**数据管理器*/
        itemManger = itemMangerContext.generateNewItemManger(opcip);
        /**覆盖验证表*/
        alreadregistervarytagpool = new ConcurrentHashMap();
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
            for (OpcVeriTag opcVeriTag : initopcVeriTagsInDB) {
                addvertag(opcVeriTag, false);
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
            logger.debug("reconnect success");
            logger.debug("begin to register pin opc tag");
            for (Map.Entry<String, List<ModlePin>> entrypin : opctagModlePinPool.entrySet()) {
                List<ModlePin> pinlist = entrypin.getValue();
                for (ModlePin pin : pinlist) {
                    /**pin register*/
                    try {
                        registertagtoItemManger(pin.getModleOpcTag());
                    } catch (JIException e) {
                        logger.error(e.getMessage(), e);
                    } catch (AddFailedException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }

            logger.debug("begin to register filter opc tag");
            for (Map.Entry<String, List<Filter>> opcpinentry : opctagFilterPool.entrySet()) {
                List<Filter> filterswithsameopctag = opcpinentry.getValue();
                for (Filter filter : filterswithsameopctag) {
                    try {
                        registertagtoItemManger(filter.getBackToDCSTag());
                    } catch (JIException e) {
                        logger.error(e.getMessage(), e);
                    } catch (AddFailedException e) {
                        logger.error(e.getMessage(), e);
                    }

                }

            }


            logger.debug("begin to register pin shock detect tag");
            for (Map.Entry<String, List<ShockDetector>> entydetector : opctagShockDetectPool.entrySet()) {
                List<ShockDetector> shockdetectorlist = entydetector.getValue();
                for (ShockDetector sheock : shockdetectorlist) {
                    try {
                        registertagtoItemManger(sheock.getBackToDCSTag());
                    } catch (JIException e) {
                        logger.error(e.getMessage(), e);
                    } catch (AddFailedException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }

            logger.debug("begin to register pin shock detect filter tag");
            for (Map.Entry<String, List<Filter>> entydetectorfilter : opctagShockDetectFilterPool.entrySet()) {
                List<Filter> shockdetectorFilterlist = entydetectorfilter.getValue();
                for (Filter sheockfilter : shockdetectorFilterlist) {
                    try {
                        registertagtoItemManger(sheockfilter.getBackToDCSTag());
                    } catch (JIException e) {
                        logger.error(e.getMessage(), e);
                    } catch (AddFailedException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }


            logger.debug("begin to register aimodle propeties tag");
            for (Map.Entry<String, List<AlgorithmProperty>> entyaimodleproperty : opctagAIModlePropertyPool.entrySet()) {
                List<AlgorithmProperty> algorithmproperties = entyaimodleproperty.getValue();
                for (AlgorithmProperty algorithmProperty : algorithmproperties) {
                    try {
                        registertagtoItemManger(algorithmProperty.getOpctag());
                    } catch (JIException e) {
                        logger.error(e.getMessage(), e);
                    } catch (AddFailedException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }


            logger.debug("begin to register aimodle propeties filter tag");
            for (Map.Entry<String, List<Filter>> entyaimodlepropertyfilter : opctagAIModlePropertyFilterPool.entrySet()) {
                List<Filter> algorithmpropertyfilter = entyaimodlepropertyfilter.getValue();
                for (Filter propertyfilter : algorithmpropertyfilter) {
                    try {
                        registertagtoItemManger(propertyfilter.getBackToDCSTag());
                    } catch (JIException e) {
                        logger.error(e.getMessage(), e);
                    } catch (AddFailedException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }


        } else {
            connectStatus = false;
            logger.warn("opc serve reconnect failed!");
        }

    }


    public <T extends OPCComponent> Boolean registerOPCComphoned(Map<String, List<T>> pool, T comphone) {
        /**
         * pinpool中将opc位号对应的pin容器获取出来
         * 如果没有的，创建一个pin容器，将pin的opc位号添加进opcgroup，并添加进pin的容器中
         * */
        List<T> modlePinsList = pool.get(comphone.getOPCTAG());
        if (modlePinsList != null) {
            //添加pins至list
            modlePinsList.add(comphone);
            //增加引用
            itemManger.getItemUnit(comphone.getOPCTAG()).addrefrencecount();
            logger.info(comphone.getOPCTAG() + "位号注册成功" + " refrence:" + itemManger.getItemUnit(comphone.getOPCTAG()).getRefrencecount());
        } else {
            try {
                /***
                 *加入到opc获取数据group
                 * */
                registertagtoItemManger(comphone.getOPCTAG());
                modlePinsList = new CopyOnWriteArrayList<>();
                /**注册*/
                modlePinsList.add(comphone);
                pool.put(comphone.getOPCTAG(), modlePinsList);
            } catch (JIException e) {
                logger.error("位号添加失败: " + comphone.getOPCTAG());
                logger.error(e.getMessage(), e);
                return false;
            } catch (AddFailedException e) {
                logger.error("位号添加失败: " + comphone.getOPCTAG());
                logger.error(e.getMessage(), e);
                return false;
            } catch (Exception e) {
                logger.error("位号添加失败: " + comphone.getOPCTAG());
                logger.error(e.getMessage(), e);
                return false;
            }
        }
        return true;
    }


    @Deprecated
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

                registertagtoItemManger(modlePins.getModleOpcTag());

                modlePinsList = new CopyOnWriteArrayList<>();
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


    @Deprecated
    public Boolean registerAIModleProperty(AlgorithmProperty property) {
        /**
         * pinpool中将opc位号对应的pin容器获取出来
         * 如果没有的，创建一个pin容器，将pin的opc位号添加进opcgroup，并添加进pin的容器中
         * */
        List<AlgorithmProperty> modlepropertyList = opctagAIModlePropertyPool.get(property.getOpctag());
        if (modlepropertyList != null) {
            //添加pins至list
            modlepropertyList.add(property);
            //增加引用
            itemManger.getItemUnit(property.getOpctag()).addrefrencecount();
            logger.info(property.getOpctag() + "位号注册成功" + " refrence:" + itemManger.getItemUnit(property.getOpctag()).getRefrencecount());
        } else {
            try {
                /***
                 *加入到opc获取数据group
                 * */
                registertagtoItemManger(property.getOpctag());

                modlepropertyList = new CopyOnWriteArrayList<>();
                /**
                 * 注册
                 * */
                modlepropertyList.add(property);
                opctagAIModlePropertyPool.put(property.getOpctag(), modlepropertyList);
            } catch (JIException e) {
                logger.error("位号添加失败: " + property.getOpctag());
                logger.error(e.getMessage(), e);
                return false;
            } catch (AddFailedException e) {
                logger.error("位号添加失败: " + property.getOpctag());
                logger.error(e.getMessage(), e);
                return false;
            } catch (Exception e) {
                logger.error("位号添加失败: " + property.getOpctag());
                logger.error(e.getMessage(), e);
                return false;
            }

        }
        return true;

    }

    @Deprecated
    public Boolean registerAIModlePropertyFilter(Filter filter) {
        /**
         * pinpool中将opc位号对应的pin容器获取出来
         * 如果没有的，创建一个pin容器，将pin的opc位号添加进opcgroup，并添加进pin的容器中
         * */
        List<Filter> filterList = opctagAIModlePropertyFilterPool.get(filter.getBackToDCSTag());
        if (filterList != null) {
            //添加pins至list
            filterList.add(filter);
            //增加引用
            itemManger.getItemUnit(filter.getBackToDCSTag()).addrefrencecount();
            logger.info(filter.getBackToDCSTag() + "位号注册成功" + " refrence:" + itemManger.getItemUnit(filter.getBackToDCSTag()).getRefrencecount());
        } else {
            try {
                /***
                 *加入到opc获取数据group
                 * */

                registertagtoItemManger(filter.getBackToDCSTag());

                filterList = new CopyOnWriteArrayList<>();
                /**
                 * 注册
                 * */
                filterList.add(filter);
                opctagAIModlePropertyFilterPool.put(filter.getBackToDCSTag(), filterList);
            } catch (JIException e) {
                logger.error("位号添加失败: " + filter.getBackToDCSTag());
                logger.error(e.getMessage(), e);
                return false;
            } catch (AddFailedException e) {
                logger.error("位号添加失败: " + filter.getBackToDCSTag());
                logger.error(e.getMessage(), e);
                return false;
            } catch (Exception e) {
                logger.error("位号添加失败: " + filter.getBackToDCSTag());
                logger.error(e.getMessage(), e);
                return false;
            }

        }
        return true;

    }


    @Deprecated
    public Boolean registerFilter(ModlePin modlePins) {
        return registerFilter(modlePins.getFilter());
    }

    @Deprecated
    public Boolean registerFilter(Filter filter) {
        /**
         *
         *滤波器位号
         * 1、判断是否位号是否有List(有的话就是已经加到group里了)  是，则添加到pool的list中，并增加opctag的索引
         * 2、否，则加入group，并新建一个list，放入filter，并添加至opctagFilterPool
         *
         * */
        if ((filter != null) && (filter.getBackToDCSTag() != null) && (!filter.getBackToDCSTag().trim().equals(""))) {

            List<Filter> filterList = opctagFilterPool.get(filter.getBackToDCSTag());

            if (filterList != null) {
                //有相同位号的filter了
                filterList.add(filter);
                itemManger.getItemUnit(filter.getBackToDCSTag()).addrefrencecount();
                logger.info(filter.getBackToDCSTag() + "位号注册成功" + " refrence:" + itemManger.getItemUnit(filter.getBackToDCSTag()).getRefrencecount());
            } else {
                try {

                    registertagtoItemManger(filter.getBackToDCSTag());

                    filterList = new CopyOnWriteArrayList<>();
                    filterList.add(filter);
                    opctagFilterPool.put(filter.getBackToDCSTag(), filterList);
                } catch (JIException e) {
                    logger.error("位号添加失败: " + filter.getBackToDCSTag());
                    logger.error(e.getMessage(), e);
                    return false;
                } catch (AddFailedException e) {
                    logger.error("位号添加失败: " + filter.getBackToDCSTag());
                    logger.error(e.getMessage(), e);
                    return false;
                } catch (Exception e) {
                    logger.error("位号添加时候报错: " + filter.getBackToDCSTag());
                    logger.error(e.getMessage(), e);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 注册振荡器的幅值反写
     */
    @Deprecated
    public Boolean registerShockDetector(ModlePin modlePin) {
        if ((modlePin.getShockDetector() != null)) {
            /**注册振荡器的幅值反写*/
            if ((modlePin.getShockDetector().getBackToDCSTag() != null) && (!modlePin.getShockDetector().getBackToDCSTag().trim().equals(""))) {
                List<ShockDetector> shockDetectorList = opctagShockDetectPool.get(modlePin.getShockDetector().getBackToDCSTag());

                if (shockDetectorList != null) {
                    //有相同位号的filter了
                    shockDetectorList.add(modlePin.getShockDetector());
                    itemManger.getItemUnit(modlePin.getShockDetector().getBackToDCSTag()).addrefrencecount();
                    logger.info(modlePin.getShockDetector().getBackToDCSTag() + "位号注册成功" + " refrence:" + itemManger.getItemUnit(modlePin.getShockDetector().getBackToDCSTag()).getRefrencecount());
                } else {
                    try {

                        registertagtoItemManger(modlePin.getShockDetector().getBackToDCSTag());


                        shockDetectorList = new CopyOnWriteArrayList<>();
                        shockDetectorList.add(modlePin.getShockDetector());
                        opctagShockDetectPool.put(modlePin.getShockDetector().getBackToDCSTag(), shockDetectorList);
                    } catch (JIException e) {
                        logger.error("位号添加失败: " + modlePin.getShockDetector().getBackToDCSTag());
                        logger.error(e.getMessage(), e);
                        return false;
                    } catch (AddFailedException e) {
                        logger.error("位号添加失败: " + modlePin.getShockDetector().getBackToDCSTag());
                        logger.error(e.getMessage(), e);
                        return false;
                    } catch (Exception e) {
                        logger.error("位号添加时候报错: " + modlePin.getShockDetector().getBackToDCSTag());
                        logger.error(e.getMessage(), e);
                        return false;
                    }

                }

            }

        }
        return true;
    }

    @Deprecated
    public Boolean registerShockDetectorFilter(ModlePin modlePin) {
        /**
         *滤波器位号
         * 1注册振荡器滤波值反写
         * */
        if ((modlePin.getShockDetector() != null)) {

            /**1注册振荡器的幅值反写*/
            if ((modlePin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag() != null) && (!modlePin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag().trim().equals(""))) {
                List<Filter> shockDetectorFilterList = opctagShockDetectFilterPool.get(modlePin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag());

                if (shockDetectorFilterList != null) {
                    //有相同位号的filter了
                    shockDetectorFilterList.add(modlePin.getShockDetector().getFirstOrderLagFilterl());
                    itemManger.getItemUnit(modlePin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag()).addrefrencecount();
                    logger.info(modlePin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag() + "位号注册成功" + " refrence:" + itemManger.getItemUnit(modlePin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag()).getRefrencecount());
                } else {
                    try {

                        registertagtoItemManger(modlePin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag());

                        shockDetectorFilterList = new CopyOnWriteArrayList<>();
                        shockDetectorFilterList.add(modlePin.getShockDetector().getFirstOrderLagFilterl());
                        opctagShockDetectFilterPool.put(modlePin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag(), shockDetectorFilterList);
                    } catch (JIException e) {
                        logger.error("位号添加失败: " + modlePin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag());
                        logger.error(e.getMessage(), e);
                        return false;
                    } catch (AddFailedException e) {
                        logger.error("位号添加失败: " + modlePin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag());
                        logger.error(e.getMessage(), e);
                        return false;
                    } catch (Exception e) {
                        logger.error("位号添加时候报错: " + modlePin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag());
                        logger.error(e.getMessage(), e);
                        return false;
                    }
                }

            }

        }
        return true;
    }

    public <T extends OPCComponent> Boolean unregisterOPCComphoned(Map<String, List<T>> pool, T comphone) {
        List<T> modlePinsList = pool.get(comphone.getOPCTAG());
        if (modlePinsList != null) {
            modlePinsList.remove(comphone);
            itemManger.getItemUnit(comphone.getOPCTAG()).minsrefrencecount();
            logger.info("位号移除成功:" + comphone.getOPCTAG() + "refence" + itemManger.getItemUnit(comphone.getOPCTAG()).getRefrencecount());
            /* 当移除opc点位时候，如果对应的点位没有任何的模型引脚了，那么需要移除opc点位*/
            isNeedRemoveTagFromoItemManger(comphone.getOPCTAG());
            if (modlePinsList.size() == 0) {//pins的pool里为0，那么肯定没有其他pins引用这个位号了
                /**
                 * 2如果引用用完： 2.1、移除opc点位池  2.2、移除group中item
                 * 3、移除tag模型引脚池中的引脚列表
                 * */
                pool.remove(comphone.getOPCTAG());
                logger.info(comphone.getOPCTAG() + "所有引脚全部移除");
            }
            return true;
        } else {
            logger.info("位号不存在,移除失败:" + comphone.getOPCTAG());
            return false;
        }

    }

    @Deprecated
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

                isNeedRemoveTagFromoItemManger(modlePins.getModleOpcTag());

                opctagModlePinPool.remove(modlePins.getModleOpcTag());
                logger.info(modlePins.getModleOpcTag() + "所有引脚全部移除");
            }
            return true;
        } else {
            logger.info("位号不存在,移除失败:" + modlePins.getModleOpcTag());
            return false;
        }


    }

    @Deprecated
    public Boolean unregisterAIModleProperty(AlgorithmProperty property) {
        List<AlgorithmProperty> algorithmProperties = opctagAIModlePropertyPool.get(property.getOpctag());
        if (algorithmProperties != null) {
            algorithmProperties.remove(property);
            itemManger.getItemUnit(property.getOpctag()).minsrefrencecount();
            logger.info("位号移除成功:" + property.getOpctag() + "refence" + itemManger.getItemUnit(property.getOpctag()).getRefrencecount());
            if (algorithmProperties.size() == 0) {//pins的pool里为0，那么肯定没有其他pins引用这个位号了
                /**
                 * 当移除opc点位时候，如果对应的点位没有任何的模型引脚了，那么需要移除opc点位
                 * 2如果引用用完： 2.1、移除opc点位池  2.2、移除group中item
                 * 3、移除tag模型引脚池中的引脚列表
                 * */
                isNeedRemoveTagFromoItemManger(property.getOpctag());

                opctagAIModlePropertyPool.remove(property.getOpctag());
                logger.info(property.getOpctag() + "所有引脚全部移除");
            }
            return true;
        } else {
            logger.info("位号不存在,移除失败:" + property.getOpctag());
            return false;
        }


    }

    @Deprecated
    public Boolean unregisterAIModlePropertyFilter(Filter filter) {
        List<Filter> algorithmPropertyfilters = opctagAIModlePropertyFilterPool.get(filter.getBackToDCSTag());
        if (algorithmPropertyfilters != null) {
            algorithmPropertyfilters.remove(filter);
            itemManger.getItemUnit(filter.getBackToDCSTag()).minsrefrencecount();
            logger.info("位号移除成功:" + filter.getBackToDCSTag() + "refence" + itemManger.getItemUnit(filter.getBackToDCSTag()).getRefrencecount());
            if (algorithmPropertyfilters.size() == 0) {//pins的pool里为0，那么肯定没有其他pins引用这个位号了
                /**
                 * 当移除opc点位时候，如果对应的点位没有任何的模型引脚了，那么需要移除opc点位
                 * 2如果引用用完： 2.1、移除opc点位池  2.2、移除group中item
                 * 3、移除tag模型引脚池中的引脚列表
                 * */
                isNeedRemoveTagFromoItemManger(filter.getBackToDCSTag());

                opctagAIModlePropertyFilterPool.remove(filter.getBackToDCSTag());
                logger.info(filter.getBackToDCSTag() + "所有引脚全部移除");
            }
            return true;
        } else {
            logger.info("位号不存在,移除失败:" + filter.getBackToDCSTag());
            return false;
        }


    }

    @Deprecated
    public Boolean unregisterFilter(ModlePin modlePins) {
        return unregisterFilter(modlePins.getFilter());
    }

    @Deprecated
    public Boolean unregisterFilter(Filter filter) {
        /**
         * 移除filter opciterm
         * */
        if ((filter != null) && (filter.getBackToDCSTag() != null) && (!filter.getBackToDCSTag().equals(""))) {
            //移除filterpool中的filter
            opctagFilterPool.get(filter.getBackToDCSTag()).remove(filter);
            //削减引用
            itemManger.getItemUnit(filter.getBackToDCSTag()).minsrefrencecount();
            logger.info("位号移除:" + filter.getBackToDCSTag() + "refrence:" + itemManger.getItemUnit(filter.getBackToDCSTag()).getRefrencecount());

            isNeedRemoveTagFromoItemManger(filter.getBackToDCSTag());

            if (opctagFilterPool.get(filter.getBackToDCSTag()).size() == 0) {
                //如果引用全部用完，那么把pool中的list也移除
                opctagFilterPool.remove(filter.getBackToDCSTag());
                logger.info(filter.getBackToDCSTag() + "所有引脚全部移除");
            }
        }

        return true;

    }

    @Deprecated
    public Boolean unregisterShockdetector(ModlePin pin) {
        /**
         * 移除filter opciterm
         * */
        if ((pin.getShockDetector() != null) && (pin.getShockDetector().getBackToDCSTag() != null) && (!pin.getShockDetector().getBackToDCSTag().equals(""))) {
            //移除filterpool中的filter
            opctagShockDetectPool.get(pin.getShockDetector().getBackToDCSTag()).remove(pin.getShockDetector());
            //削减引用
            itemManger.getItemUnit(pin.getShockDetector().getBackToDCSTag()).minsrefrencecount();
            logger.info("位号移除:" + pin.getShockDetector().getBackToDCSTag() + "refrence:" + itemManger.getItemUnit(pin.getShockDetector().getBackToDCSTag()).getRefrencecount());

            isNeedRemoveTagFromoItemManger(pin.getShockDetector().getBackToDCSTag());

            if (opctagShockDetectPool.get(pin.getShockDetector().getBackToDCSTag()).size() == 0) {
                //如果引用全部用完，那么把pool中的list也移除
                opctagShockDetectPool.remove(pin.getShockDetector().getBackToDCSTag());
                logger.info(pin.getShockDetector().getBackToDCSTag() + "所有引脚全部移除");
            }
        }
        return true;
    }

    @Deprecated
    public Boolean unregisterShockdetectorFilter(ModlePin pin) {
        /**
         * 移除filter opciterm
         * */
        if ((pin.getShockDetector() != null) && (pin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag() != null) && (!pin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag().equals(""))) {
            //移除filterpool中的filter
            opctagShockDetectFilterPool.get(pin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag()).remove(pin.getShockDetector().getFirstOrderLagFilterl());
            //削减引用
            itemManger.getItemUnit(pin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag()).minsrefrencecount();
            logger.info("位号移除:" + pin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag() + "refrence:" + itemManger.getItemUnit(pin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag()).getRefrencecount());

            isNeedRemoveTagFromoItemManger(pin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag());

            if (opctagShockDetectFilterPool.get(pin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag()).size() == 0) {
                //如果引用全部用完，那么把pool中的list也移除
                opctagShockDetectFilterPool.remove(pin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag());
                logger.info(pin.getShockDetector().getFirstOrderLagFilterl().getBackToDCSTag() + "所有引脚全部移除");
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
            for (OpcVeriTag opcVeriTag : alreadregistervarytagpool.values()) {
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


            //数据读取统计数据读取
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
                logger.error("opc tag=" + itemManger.findTagnamebyItem(item) + " maybe styele error when get real data");

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
    private void pinValueUpdate(Item item, ItemState itemState) throws JIException {

        String valueStringstyle;
        if (DEBUG) {
            valueStringstyle = "" + (itemState.getValue().getObjectAsUnsigned().getValue().shortValue());
        } else {
            valueStringstyle = itemState.getValue().getObject().toString();
        }


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

            //DCS手自动切换监视,监视模型是否运行
            modleDisOrEnableByDCS(pin);

            //检测模型的引脚是否需要停止启用
            modlepinDisOrEnableByDCS(pin);

            //检测模型引脚是否不运行
            modlepinDisOrRunnableByDCS(pin);


            //检查是否存在滤波器，存在的话则根据滤波器类型生成滤波器执行任务
            if (pin.getFilter() != null) {
                filterService.creatFilterTaskAndPut(pin.getFilter(), Double.valueOf(valueStringstyle), null);
            }

            //震荡检测器
            if (pin.getShockDetector() != null) {
                ShockDetector shockDetector = pin.getShockDetector();
                FirstOrderLagFilter filterindetect = pin.getShockDetector().getFirstOrderLagFilterl();
                filterService.creatFilterTaskAndPut(filterindetect, Double.valueOf(valueStringstyle), shockDetector);
            }


        }
    }


    /**
     * 监视模型是否从dcs端控制停止和运行
     */
    private void modleDisOrEnableByDCS(ModlePin pin) {
        /**首先需要匹配到模型手自动位号*/
        if (pin.getModlePinName() != null && pin.getModlePinName().trim().equals(ModlePin.TYPE_PIN_MODLE_AUTO)) {
            logger.info("modle id=" + pin.getReference_modleId() + "old value=" + pin.getOldReadValue() + ",new value=" + pin.getNewReadValue());
            if ((pin.getOldReadValue() != null) && (pin.getOldReadValue() == 0) && (pin.getNewReadValue() != 0) /*&& (modleConstainerl != null) && (modleConstainerl.getModulepool().get(pin.getReference_modleId()).getModleEnable() == 0)*/) {
                //run
                logger.debug("模型运行，modleid=" + pin.getReference_modleId());
                modleRebuildService.putRebuildstak(new ModleEnableTask(0, pin.getReference_modleId()));
                logger.debug("模型运行，modleid=" + pin.getReference_modleId());
            } else if ((pin.getOldReadValue() != null) && (pin.getOldReadValue() != 0) && (pin.getNewReadValue() == 0) /*&& (modleConstainerl != null) && (modleConstainerl.getModulepool().get(pin.getReference_modleId()).getModleEnable() == 1)*/) {
                //stop
                logger.debug("模型停止，modleid=" + pin.getReference_modleId());

                modleRebuildService.putRebuildstak(new ModleDisEnableTask(0, pin.getReference_modleId()));
                logger.debug("模型停止，modleid=" + pin.getReference_modleId());
            } else {
//                    logger.debug("手自动位号未进行切换，modleid=" + pin.getReference_modleId());
            }
        }

    }


    /**
     * 模型引脚启用停用
     */
    private void modlepinDisOrEnableByDCS(ModlePin pin) {

        /**匹配到Enable引脚**/
        if (null != pin.getDcsEnabePin()) {
            /**有dcs端启用引脚的组件*/
            logger.info("modle id=" + pin.getDcsEnabePin().getReference_modleId() + "old value=" + pin.getDcsEnabePin().getOldReadValue() + ",new value=" + pin.getDcsEnabePin().getNewReadValue());
            if ((pin.getDcsEnabePin().getOldReadValue() != null) && (pin.getDcsEnabePin().getOldReadValue() == 0) && (pin.getDcsEnabePin().getNewReadValue() != 0)) {
                /**pin enable*/
                logger.debug("模型运行，modleid=" + pin.getReference_modleId() + "pinid=" + pin.getModlepinsId() + "启用");

                modleRebuildService.putRebuildstak(new ModlePinEnableTask(0, pin));

            } else if ((pin.getDcsEnabePin().getOldReadValue() != null) && (pin.getDcsEnabePin().getOldReadValue() != 0) && (pin.getDcsEnabePin().getNewReadValue() == 0)) {
                /**pv disenable*/
                logger.debug("模型运行，modleid=" + pin.getReference_modleId() + "pinid=" + pin.getModlepinsId() + "切出");
                modleRebuildService.putRebuildstak(new ModlePinDisEnableTask(0, pin));
            }

        }
    }


    /**
     * 模型引脚启用停用
     * 0判断是pv、ff类型的引脚
     * 1停止
     * 1-1、如果是则设置模型引脚停运，新建一个引脚停止运行的任务
     * 2运行
     * 2-1设置引脚运行,设置引脚，并设置引脚下次正真参与控制的时间
     */
    private void modlepinDisOrRunnableByDCS(ModlePin pin) {
        /**引脚类型判断，筛选出pv或者ff引脚，这里限制了pv和ff这个范围，因为如果是mv也是有上下限的，二mv是不需要通过这个进行设置引脚运行还是停止*/
        if ((0 != pin.getPinEnable()) && (pvpattern.matcher(pin.getModlePinName()).find() || ffpattern.matcher(pin.getModlePinName()).find())) {
            /**模型引脚停止*/

            /**
             * 判断是否超过置信区间
             * 是否正在运行
             * */
            if (pin.isBreakLimit()) {
                /**突破边界*/
                logger.warn("break limit ! modle id=" + pin.getReference_modleId() + "value=" + pin.getOldReadValue());
                if (pin.isThisTimeParticipate()) {
                    /*参与控制了，停止他*/
                    pin.setThisTimeParticipate(false);
                    modleRebuildService.putRebuildstak(new ModlePinDisRunnableTask(0, pin.getReference_modleId(), pin));
                }

            } else {
                /**在边界内*/
                if (pin.isThisTimeParticipate()) {
                    /*参与控制*/
                    if (null != pin.getRunClock()) {
                        /*闹铃时间到了吗*/
                        if (pin.clockAlarm()) {
                            modleRebuildService.putRebuildstak(new ModlePinRunnableTask(0, pin.getReference_modleId(), pin));
                            pin.clearRunClock();
                        }
                    }
                } else {
                    /*没参与控制,设立闹钟*/
                    if (null != modleRebuildService.getModleConstainer().getRunnableModulepool().get(pin.getReference_modleId())) {
                        pin.setThisTimeParticipate(true);
                        int checktime = 0;
                        /*如果模型的输出周期为null/0，则直接设置引脚保持在置信区间为10s*/
                        if ((null == modleRebuildService.getModleConstainer().getRunnableModulepool().get(pin.getReference_modleId()).getControlAPCOutCycle()) || (0 == modleRebuildService.getModleConstainer().getRunnableModulepool().get(pin.getReference_modleId()).getControlAPCOutCycle())) {
                            checktime = 10;
                            logger.warn("输出周期设置存在问题,引脚正常周期设置10秒。异常模型id=" + pin.getReference_modleId());
                        } else {
                            checktime = 2 * modleRebuildService.getModleConstainer().getRunnableModulepool().get(pin.getReference_modleId()).getControlAPCOutCycle();
                        }
                        pin.setRunClock(Instant.now().plusSeconds(checktime));
                    }
                }


            }

        }
    }


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
        group = null;
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


    private void registertagtoItemManger(String tag) throws AddFailedException, JIException {
        if (!itemManger.iscontainstag(tag)) {
            //加入group
            Item item = group.addItem(tag);
            ItemUnit itemUnit = new ItemUnit();
            itemUnit.setItem(item);
            itemUnit.addrefrencecount();
            itemManger.addItemUnit(tag, itemUnit);
            logger.info(tag + "注册成功" + " refrence:" + itemUnit.getRefrencecount());
        } else {
            //位号已添加
            itemManger.getItemUnit(tag).addrefrencecount();
            logger.warn(tag + "位号已添加");
        }
    }

    /**
     * 判断这个tag的所有引用都为0时，进移除，否则不进行移除
     */
    private void isNeedRemoveTagFromoItemManger(String tag) {
        if (itemManger.getItemUnit(tag).isnorefrence()) {
            ItemUnit removeItem = itemManger.removeItemUnit(tag);
            if (removeItem != null) {
                try {
                    group.removeItem(removeItem.getItem().getId());
                    logger.info("opcSERVE位号:" + tag + "refrence:" + removeItem.getRefrencecount());
                } catch (UnknownHostException e) {
                    logger.error(e.getMessage(), e);
                } catch (JIException e) {
                    logger.error(e.getMessage(), e);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                logger.info("opcSERVE位号未找到，移除失败:" + tag);
            }

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


    public ConcurrentHashMap<String, OpcVeriTag> getAlreadregistervarytagpool() {
        return alreadregistervarytagpool;
    }

    public Map<String, List<AlgorithmProperty>> getOpctagAIModlePropertyPool() {
        return opctagAIModlePropertyPool;
    }

    public void setOpctagAIModlePropertyPool(Map<String, List<AlgorithmProperty>> opctagAIModlePropertyPool) {
        this.opctagAIModlePropertyPool = opctagAIModlePropertyPool;
    }

    public Map<String, List<Filter>> getOpctagAIModlePropertyFilterPool() {
        return opctagAIModlePropertyFilterPool;
    }

    public void setOpctagAIModlePropertyFilterPool(Map<String, List<Filter>> opctagAIModlePropertyFilterPool) {
        this.opctagAIModlePropertyFilterPool = opctagAIModlePropertyFilterPool;
    }


    public Map<String, List<ShockDetector>> getOpctagShockDetectPool() {
        return opctagShockDetectPool;
    }

    public void setOpctagShockDetectPool(Map<String, List<ShockDetector>> opctagShockDetectPool) {
        this.opctagShockDetectPool = opctagShockDetectPool;
    }

    public Map<String, List<Filter>> getOpctagShockDetectFilterPool() {
        return opctagShockDetectFilterPool;
    }

    public void setOpctagShockDetectFilterPool(Map<String, List<Filter>> opctagShockDetectFilterPool) {
        this.opctagShockDetectFilterPool = opctagShockDetectFilterPool;
    }
}

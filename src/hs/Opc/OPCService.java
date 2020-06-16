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
    private Map<String, ItemUnit> opcitemPool = new ConcurrentHashMap<>();//key=标签，value=ItemUnit
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
                    //新位号
                    if(!opcitemPool.containsKey(tag)){

                        Item item=group.addItem(tag);

                        ItemUnit itemUnit=new ItemUnit();
                        itemUnit.setItem(item);
                        itemUnit.addrefrencecount();

                        opcitemPool.put(tag, itemUnit);
                    }else{
                        //位号已添加
                        opcitemPool.get(tag).addrefrencecount();
                    }

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
            //添加pins至list
            modlePinsList.add(modlePins);
            //增加引用
            opcitemPool.get(modlePins.getModleOpcTag()).addrefrencecount();

        } else {
            try {
                /***
                 *加入到opc获取数据group
                 * */
                if(!opcitemPool.containsKey(modlePins.getModleOpcTag())){
                    //添加opc位号至service
                    Item item=group.addItem(modlePins.getModleOpcTag());

                    ItemUnit itemUnit=new ItemUnit();
                    itemUnit.setItem(item);
                    itemUnit.addrefrencecount();

                    opcitemPool.put(modlePins.getModleOpcTag(), itemUnit);
                }else{
                    //位号已添加
                    opcitemPool.get(modlePins.getModleOpcTag()).addrefrencecount();
                }


                modlePinsList = new ArrayList<>();
                /**
                 * 注册
                 * */
                modlePinsList.add(modlePins);
                opctagModlePinPool.put(modlePins.getModleOpcTag(), modlePinsList);

            } catch (JIException e) {
                logger.error(e);
                return false;
            } catch (AddFailedException e) {
                logger.error(e);
                return false;
            }

        }


        /**
         *
         *滤波器位号
         * 1、判断是否位号是否有List(有的话就是已经加到group里了)  是，则添加到pool的list中，并增加opctag的索引
         * 2、否，则加入group，并新建一个list，放入filter，并添加至opctagFilterPool
         *
         * */
        if ((modlePins.getFilter() != null) && (modlePins.getFilter().getBackToDCSTag() != null) && (!modlePins.getFilter().getBackToDCSTag().trim().equals(""))) {

            List<Filter> filterList=opctagFilterPool.get(modlePins.getFilter().getBackToDCSTag());

            if (filterList != null) {

                filterList.add(modlePins.getFilter());
                opcitemPool.get(modlePins.getFilter().getBackToDCSTag()).addrefrencecount();

            } else {
                try {

                    if(!opcitemPool.containsKey(modlePins.getFilter().getBackToDCSTag())){
                        //加入group
                        Item item=group.addItem(modlePins.getFilter().getBackToDCSTag());

                        ItemUnit itemUnit=new ItemUnit();
                        itemUnit.setItem(item);
                        itemUnit.addrefrencecount();

                        opcitemPool.put(modlePins.getFilter().getBackToDCSTag(), itemUnit);
                    }else{
                        //位号已添加
                        opcitemPool.get(modlePins.getFilter().getBackToDCSTag()).addrefrencecount();
                    }

                    filterList = new LinkedList();
                    filterList.add(modlePins.getFilter());
                    opctagFilterPool.put(modlePins.getFilter().getBackToDCSTag(), filterList);

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
            opcitemPool.get(modlePins.getModleOpcTag()).minsrefrencecount();
            /**
             * 移除filter opciterm
             * */
            if ((modlePins.getFilter() != null) && (modlePins.getFilter().getBackToDCSTag() != null) && (!modlePins.getFilter().getBackToDCSTag().equals(""))) {
                //移除filterpool中的filter
                opctagFilterPool.get(modlePins.getFilter().getBackToDCSTag()).remove(modlePins.getFilter());
                //削减引用
                opcitemPool.get(modlePins.getFilter().getBackToDCSTag()).minsrefrencecount();

                if(opcitemPool.get(modlePins.getFilter().getBackToDCSTag()).isnorefrence()){

                    ItemUnit removeItem = opcitemPool.remove(modlePins.getFilter().getBackToDCSTag());
                    if (removeItem != null) {
                        try {
                            group.removeItem(removeItem.getItem().getId());
                        } catch (UnknownHostException e) {
                            logger.error(e);
                        } catch (JIException e) {
                            logger.error(e);
                        }
                    }

                }

                if(opctagFilterPool.get(modlePins.getFilter().getBackToDCSTag()).size()==0){
                    //如果引用全部用完，那么把pool中的list也移除
                    opctagFilterPool.remove(modlePins.getFilter().getBackToDCSTag());
                }


            }


            if (modlePinsList.size() == 0) {//pins的pool里为0，那么肯定没有其他pins引用这个位号了
                /**
                 * 当移除opc点位时候，如果对应的点位没有任何的模型引脚了，那么需要移除opc点位
                 * 2如果引用用完： 1、移除opc点位池  2、移除group中item
                 *
                 * 3、移除tag模型引脚池中的引脚列表
                 * */

                if(opcitemPool.get(modlePins.getModleOpcTag()).isnorefrence()){

                    ItemUnit removeItem = opcitemPool.remove(modlePins.getModleOpcTag());
                    if (removeItem != null) {
                        try {
                            group.removeItem(removeItem.getItem().getId());
                        } catch (UnknownHostException e) {
                            logger.error(e);
                        } catch (JIException e) {
                            logger.error(e);
                        }
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
            for (Map.Entry<String, ItemUnit> integerItemEntry : opcitemPool.entrySet()) {
                String stringvalue = null;
                try {

                    List<ModlePin> pins = opctagModlePinPool.get(integerItemEntry.getKey());
                    stringvalue = integerItemEntry.getValue().getItem().read(false).getValue().getObject().toString();
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
                                    folftask.setItem(opcitemPool.get(folf.getBackToDCSTag()).getItem());
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

                                if ((mvav.getBackToDCSTag() != null) && (!mvav.getBackToDCSTag().equals(""))) {
                                    mvavtask.setItem(opcitemPool.get(mvav.getBackToDCSTag()).getItem());
                                }


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
        ItemUnit item = opcitemPool.get(tag);
        if (item != null) {
            try {
                item.getItem().write(new JIVariant(value, false));
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

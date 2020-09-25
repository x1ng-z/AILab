package hs.Opc;

import hs.Bean.Algorithm.AlgorithmProperty;
import hs.Bean.ModleConstainer;
import hs.Bean.ModlePin;
import hs.Bean.ModleProperty;
import hs.Dao.Service.OpcDBServe;
import hs.Filter.*;
import hs.Opc.Monitor.ModleStopRunMonitor;
import hs.ServiceBus.FilterService;
import hs.ServiceBus.ModleRebuildService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.openscada.opc.lib.da.*;

import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/7/2 8:55
 */

@Component("opcServicConstainer")
public class OpcServicConstainer {
    private static final Logger logger = Logger.getLogger(OpcServicConstainer.class);
    private FilterService filterService = null;//实时滤波服务

    private ModleRebuildService modleRebuildService = null;//dcs模块运行与停止监视服务
    private OpcDBServe opcDBServe = null;
    private ConcurrentHashMap<Integer, OPCService> opcservepool = new ConcurrentHashMap();//key=OPCserveid
    private ExecutorService executorService;
    private Pattern opcpattern = Pattern.compile("([a-zA-Z]*)([0-9|.]*)");
    private ModleConstainer modleConstainerl;
    private ItemMangerContext itemMangerContext;


    @Autowired
    public OpcServicConstainer(FilterService filterService, OpcDBServe opcDBServe, ModleRebuildService modleRebuildService, ItemMangerContext itemMangerContext) {
        this.filterService = filterService;
        this.opcDBServe = opcDBServe;
        this.modleRebuildService = modleRebuildService;
        this.itemMangerContext = itemMangerContext;
        itemMangerContext.setOpcServicConstainer(this);
        this.executorService = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });
    }


    /**
     * 添加opcserve进opc池
     */
    public void addOPCServe(OPCService service) {
        try {
            service.initAndConnect(filterService, modleRebuildService, itemMangerContext);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        try {
            opcservepool.put(service.getOpcserveid(), service);
            executorService.execute(service);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * 添加opcserve进opc池
     */
    public void removeOPCServe(int opcseveid) {
        try {
            OPCService opcService = opcservepool.get(opcseveid);//这里考虑是否移除
            opcService.disconnect();
            opcservepool.remove(opcseveid);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 重新添加所有的opcserve的验证位号进行opcserve进opc验证池
     */
    public void reAddAllOPCServevertags(int opcserveid, List<OpcVeriTag> opcvertaglist) {
        try {

            OPCService opcService = opcservepool.get(opcserveid);
            if (opcService != null) {
                /**移除不在将要添加进行待添加验证位号列表中的*/
                for (OpcVeriTag opcvertag : opcService.getAlreadregistervarytagpool().values()) {
                    boolean ishasinwaitlist = false;
                    for (OpcVeriTag waitregistertag : opcvertaglist) {
                        if (opcvertag.getTag().equals(waitregistertag.getTag())) {
                            ishasinwaitlist = true;
                            break;
                        }
                    }
                    if (!ishasinwaitlist) {
                        opcService.removevertag(opcvertag.getTag());
                    }
                }
                for (OpcVeriTag waitregistertag : opcvertaglist) {
                    opcService.addvertag(waitregistertag, true);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * 添加opcserve的验证位号进行opcserve进opc验证池
     */
    public void addOPCServevertag(int opcserveid, OpcVeriTag opcvertag) {
        try {
            OPCService opcService = opcservepool.get(opcserveid);
            if (opcService != null) {
                opcService.addvertag(opcvertag,true);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 重新添加opcserve的验证位号进行opcserve进opc验证池
     */
    public void modifyOPCServevertag(int opcserveid, String oldvertag,OpcVeriTag opcvertag) {
        try {
            OPCService opcService = opcservepool.get(opcserveid);
            if (opcService != null) {

                opcService.removevertag(oldvertag);

                opcService.addvertag(opcvertag,true);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * 重新添加opcserve的验证位号进行opcserve进opc验证池
     */
    public void removeOPCServevertag(int opcserveid, OpcVeriTag opcvertag) {
        try {
            OPCService opcService = opcservepool.get(opcserveid);
            if (opcService != null) {
                opcService.removevertag(opcvertag.getTag());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * 得到group
     */
    public Group getOPcserveGroup(ModlePin modlePin) {
        if ((modlePin.getResource() != null) && (modlePin.getResource().equals("constant"))) {
            return null;
        } else if ((modlePin.getResource() != null)) {
            /**register pin*/
            for (OPCService opcService : opcservepool.values()) {
//                Pattern pattern = Pattern.compile("([a-zA-Z]*)([0-9|.]*)");
                Matcher matcher = opcpattern.matcher(modlePin.getResource());
                if (matcher.find()) {
                    if (matcher.group(2).equals(opcService.getOpcip())) {
//                        opcService.registerModlePin(modlePin);
                        opcService.registerOPCComphoned(opcService.getOpctagModlePinPool(),modlePin);
                        break;
                    }
                }

            }
        }

        return null;
    }


    /**
     * 验证是否有一个opc group，用于判断是否有连接上的opcserve
     */
    public boolean isAnyConnectOpcServe() {
        /**register pin*/
        boolean isanyconn = false;
        for (OPCService opcService : opcservepool.values()) {
            if (opcService.getGroup() == null) {
                continue;
            } else {
                isanyconn = true;
                return isanyconn;
            }

        }
        return isanyconn;
    }

    /**
     * 是否有一个opc group，用于判断是否有连接上的opcserve
     */
    public Group getOPcserveGroup() {
        /**register pin*/
        for (OPCService opcService : opcservepool.values()) {
            if (opcService.getGroup() == null) {
                continue;
            } else {
                return opcService.getGroup();
            }

        }
        return null;
    }


    /**
     * 1\将模型的pin/property,以及他的其他组件：滤波器、振荡器等注册进opcserve
     * 2初始化振荡器
     * **/
    public void registerModlePinAndComponent(ModleProperty property) {

        if (property instanceof ModlePin) {
            ModlePin modlePin = (ModlePin) property;
            if ((modlePin.getResource() != null) && (modlePin.getResource().equals(ModlePin.SOURCE_TYPE_CONSTANT))) {
                return;
            } else if ((modlePin.getResource() != null)) {
                /**register pin*/
                for (OPCService opcService : opcservepool.values()) {
                    Matcher matcher = opcpattern.matcher(modlePin.getResource());
                    if (matcher.find()) {
                        if (matcher.group(2).equals(opcService.getOpcip())) {
                            opcService.registerOPCComphoned(opcService.getOpctagModlePinPool(),modlePin);
                            break;
                        }
                    }

                }

                /**register filter*/
                if ((modlePin.getFilter() != null) && (modlePin.getFilter().getBackToDCSTag() != null) && !(modlePin.getFilter().getBackToDCSTag().equals(""))) {

                    for (OPCService opcService : opcservepool.values()) {
                        Matcher matcher = opcpattern.matcher(modlePin.getFilter().getOpcresource());
                        if (matcher.find()) {
                            if (matcher.group(2).equals(opcService.getOpcip())) {
                                opcService.registerOPCComphoned(opcService.getOpctagFilterPool(),modlePin.getFilter());
                                break;
                            }
                        }

                    }

                }


                /**register shockdetect*/
                if ((modlePin.getShockDetector() != null)) {
                    /**幅值震荡检测器的反写数据位号注册*/
                    if ((modlePin.getShockDetector().getBackToDCSTag() != null) && !(modlePin.getShockDetector().getOpcresource().equals(""))) {
                        for (OPCService opcService : opcservepool.values()) {
                            Matcher matcher = opcpattern.matcher(modlePin.getShockDetector().getOpcresource());
                            if (matcher.find()) {
                                if (matcher.group(2).equals(opcService.getOpcip())) {
                                    opcService.registerOPCComphoned(opcService.getOpctagShockDetectPool(),modlePin.getShockDetector());
                                    break;
                                }
                            }

                        }
                    }
                    /**振荡器滤波器滤波器注册*/
                    if ((modlePin.getShockDetector().getFilterbacktodcstag() != null) && !(modlePin.getShockDetector().getFilteropcresource().equals(""))) {
                        for (OPCService opcService : opcservepool.values()) {
                            Matcher matcher = opcpattern.matcher(modlePin.getShockDetector().getFilteropcresource());
                            if (matcher.find()) {
                                if (matcher.group(2).equals(opcService.getOpcip())) {
                                    opcService.registerOPCComphoned(opcService.getOpctagShockDetectFilterPool(),modlePin.getShockDetector().getFirstOrderLagFilterl());
                                    break;
                                }
                            }

                        }
                    }
                    /**初始化检测器*/
                    modlePin.getShockDetector().componentrealize();
                }
            }
        } else if (property instanceof AlgorithmProperty) {

            AlgorithmProperty algorithmProperty = (AlgorithmProperty) property;
            /**
             * 1\regiter properties to opc
             * 2\register filter if backtodcs is no null or ''
             * */

            if ((algorithmProperty.getResource() != null) && (!algorithmProperty.getResource().equals(""))) {
                for (OPCService opcService : opcservepool.values()) {
                    Matcher matcher = opcpattern.matcher(algorithmProperty.getResource());
                    if (matcher.find()) {
                        if (matcher.group(2).equals(opcService.getOpcip())) {
                            opcService.registerOPCComphoned(opcService.getOpctagAIModlePropertyPool(),algorithmProperty);
                            break;
                        }
                    }

                }

            }


            if ((algorithmProperty.getFilter() != null) && (algorithmProperty.getFilter().getOpcresource() != null) && (!algorithmProperty.getFilter().getOpcresource().equals(""))) {

                for (OPCService opcService : opcservepool.values()) {
                    Matcher matcher = opcpattern.matcher(algorithmProperty.getFilter().getOpcresource());
                    if (matcher.find()) {
                        if (matcher.group(2).equals(opcService.getOpcip())) {
                            opcService.registerOPCComphoned(opcService.getOpctagAIModlePropertyFilterPool(),algorithmProperty.getFilter());
                            break;
                        }
                    }

                }
            }

        } else {
            logger.warn("no match property class");
        }

    }


    @Deprecated
    public void registerComponent(Filter filter) {

        /**register filter*/
        if ((filter != null) && (filter.getBackToDCSTag() != null) && !(filter.getBackToDCSTag().equals(""))) {

            for (OPCService opcService : opcservepool.values()) {
                Matcher matcher = opcpattern.matcher(filter.getOpcresource());
                if (matcher.find()) {
                    if (matcher.group(2).equals(opcService.getOpcip())) {
                        opcService.registerFilter(filter);
                        break;
                    }
                }

            }

        }

    }


    public boolean writeModlePinValue(ModleProperty property, Double value) {

        if (property instanceof ModlePin) {
            ModlePin modlePin = (ModlePin) property;
            if ((modlePin.getResource() != null) && (modlePin.getResource().equals("constant"))) {
                return true;
            } else if ((modlePin.getResource() != null)) {
                /**register pin*/
                for (OPCService opcService : opcservepool.values()) {

                    Matcher matcher = opcpattern.matcher(modlePin.getResource());
                    if (matcher.find()) {
                        if (matcher.group(2).equals(opcService.getOpcip())) {
                            return opcService.writeTagvalue(modlePin.getModleOpcTag(), value);
                        }
                    }

                }

            }
        } else if (property instanceof AlgorithmProperty) {

            AlgorithmProperty algorithmProperty = (AlgorithmProperty) property;
            if (algorithmProperty.getDatatype().equals(AlgorithmProperty.DATATYPE_VALUE)) {
                if (algorithmProperty.getFilter() != null) {

                    filterService.creatFilterTaskAndPut(algorithmProperty.getFilter(), algorithmProperty.getValue(), null);
                }
                /**如果同时配置了属性的反写opc，那么也需要进行反写操作*/
                if ((algorithmProperty.getResource() != null) && (algorithmProperty.getResource().equals("constant"))) {
                    return true;
                } else if ((algorithmProperty.getResource() != null)) {
                    /**直接写入数据*/
                    for (OPCService opcService : opcservepool.values()) {

                        Matcher matcher = opcpattern.matcher(algorithmProperty.getResource());
                        if (matcher.find()) {
                            if (matcher.group(2).equals(opcService.getOpcip())) {
                                return opcService.writeTagvalue(algorithmProperty.getOpctag(), value);
                            }
                        }

                    }

                }
            }


        }

        return false;

    }


    public boolean unregisterModlePinAndComponent(ModleProperty property) {

        if (property instanceof ModlePin) {
            ModlePin modlepin = (ModlePin) property;
            if ((modlepin.getResource() != null) && (modlepin.getResource().equals("constant"))) {
                /**opc resure is constant, so no neet register*/
                logger.warn("the propert " + modlepin.getModleOpcTag() + " ,it's resouce from constant. so no neet register");
                return true;
            } else if ((modlepin.getResource() != null)) {
                /**unregister pin*/
                for (OPCService opcService : opcservepool.values()) {
                    Matcher matcher = opcpattern.matcher(modlepin.getResource());
                    if (matcher.find()) {
                        if (matcher.group(2).equals(opcService.getOpcip())) {
                            try {
                                opcService.unregisterOPCComphoned(opcService.getOpctagModlePinPool(),modlepin);
                            } catch (Exception e) {
                                logger.error("opc位号:" + modlepin.getModleOpcTag() + "移除失败");
                                logger.error(e.getMessage(), e);
                            }
                            break;
                        }
                    }

                }

                /**unregister filter*/
                if ((modlepin.getFilter() != null) && (modlepin.getFilter().getBackToDCSTag() != null) && (!modlepin.getFilter().getBackToDCSTag().equals(""))) {
                    for (OPCService opcService : opcservepool.values()) {
                        Matcher matcher = opcpattern.matcher(modlepin.getFilter().getOpcresource());
                        if (matcher.find()) {
                            if (matcher.group(2).equals(opcService.getOpcip())) {
                                opcService.unregisterOPCComphoned(opcService.getOpctagFilterPool(),modlepin.getFilter());
                                break;
                            }
                        }

                    }

                }


                /**unregister shockdetect*/
                if ((modlepin.getShockDetector() != null)) {
                    if (modlepin.getShockDetector().getBackToDCSTag() != null && (modlepin.getShockDetector().getOpcresource() != null)) {
                        for (OPCService opcService : opcservepool.values()) {
                            Matcher matcher = opcpattern.matcher(modlepin.getShockDetector().getOpcresource());
                            if (matcher.find()) {
                                if (matcher.group(2).equals(opcService.getOpcip())) {
                                    opcService.unregisterOPCComphoned(opcService.getOpctagShockDetectPool(),modlepin.getShockDetector());
                                    break;
                                }
                            }

                        }
                    }
                    /**震荡输出位号*/
                    if ((modlepin.getShockDetector().getFilterbacktodcstag() != null) && (!modlepin.getShockDetector().getFilteropcresource().equals(""))) {
                        for (OPCService opcService : opcservepool.values()) {
                            Matcher matcher = opcpattern.matcher(modlepin.getShockDetector().getFilteropcresource());
                            if (matcher.find()) {
                                if (matcher.group(2).equals(opcService.getOpcip())) {
                                    opcService.unregisterOPCComphoned(opcService.getOpctagShockDetectFilterPool(),modlepin.getShockDetector().getFirstOrderLagFilterl());
                                    break;
                                }
                            }

                        }
                    }

                }

            }
            return true;

        } else if (property instanceof AlgorithmProperty) {
            AlgorithmProperty algorithmProperty = (AlgorithmProperty) property;
            if ((algorithmProperty.getResource() != null) && (algorithmProperty.getResource().equals(ModlePin.SOURCE_TYPE_CONSTANT))) {
                logger.warn("the opctagof propert " + algorithmProperty.getOpctag() + " ,it's resouce from constant. so no neet register");
                return true;
            }

            if ((algorithmProperty.getResource() != null) && (!algorithmProperty.getResource().equals(""))) {
                for (OPCService opcService : opcservepool.values()) {
                    Matcher matcher = opcpattern.matcher(algorithmProperty.getResource());
                    if (matcher.find()) {
                        if (matcher.group(2).equals(opcService.getOpcip())) {
                            opcService.unregisterOPCComphoned(opcService.getOpctagAIModlePropertyPool(),algorithmProperty);
                            break;
                        }
                    }

                }
            }

            if ((algorithmProperty.getFilter() != null) && (algorithmProperty.getFilter().getOpcresource() != null) && (!algorithmProperty.getFilter().getOpcresource().equals(""))) {
                for (OPCService opcService : opcservepool.values()) {
                    Matcher matcher = opcpattern.matcher(algorithmProperty.getFilter().getOpcresource());
                    if (matcher.find()) {
                        if (matcher.group(2).equals(opcService.getOpcip())) {
                            opcService.unregisterOPCComphoned(opcService.getOpctagAIModlePropertyFilterPool(),algorithmProperty.getFilter());
                            break;
                        }
                    }
                }
            }

            return true;

        } else {

            logger.warn("no match property type");
        }

        return false;

    }


    /**
     * inite any opc serve
     */
    public void selfinit() {
        List<OPCService> opcServiceList = opcDBServe.getopcserves();
        for (OPCService service : opcServiceList) {
            try {
                service.initAndConnect(filterService, modleRebuildService, itemMangerContext);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            try {
                opcservepool.put(service.getOpcserveid(), service);
                executorService.execute(service);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }

    }
    public ModleRebuildService getModleRebuildService() {
        return modleRebuildService;
    }

    public void selfclose() {
        for (OPCService opcService : opcservepool.values()) {
            opcService.disconnect();
        }

    }


    public ConcurrentHashMap<Integer, OPCService> getOpcservepool() {
        return opcservepool;
    }

    public ModleConstainer getModleConstainerl() {
        return modleConstainerl;
    }


}

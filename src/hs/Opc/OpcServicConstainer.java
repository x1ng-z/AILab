package hs.Opc;

import hs.Bean.ModleConstainer;
import hs.Bean.ModlePin;
import hs.Dao.Service.OpcDBServe;
import hs.Filter.FilterService;
import hs.Opc.Monitor.ModleStopRunMonitor;
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
    private ModleStopRunMonitor modleStopRunMonitor = null;//dcs模块运行与停止监视服务
    private OpcDBServe opcDBServe = null;
    private ConcurrentHashMap<Integer, OPCService> opcservepool = new ConcurrentHashMap();//key=OPCserveid
    private ExecutorService executorService;
    private Pattern opcpattern = Pattern.compile("([a-zA-Z]*)([0-9|.]*)");
    private ModleConstainer modleConstainerl;

    @Autowired
    public OpcServicConstainer(FilterService filterService, OpcDBServe opcDBServe, ModleStopRunMonitor modleStopRunMonitor) {
        this.filterService = filterService;
        this.opcDBServe = opcDBServe;
        this.modleStopRunMonitor = modleStopRunMonitor;
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
                        opcService.registerModlePin(modlePin);
                        break;
                    }
                }

            }
        }

        return null;
    }


    /**
     * 是否有一个opc group，用于判断是否有连接上的opcserve
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


    public void registerModlePinAndComponent(ModlePin modlePin) {
        if ((modlePin.getResource() != null) && (modlePin.getResource().equals("constant"))) {
            return;
        } else if ((modlePin.getResource() != null)) {
            /**register pin*/
            for (OPCService opcService : opcservepool.values()) {
                Matcher matcher = opcpattern.matcher(modlePin.getResource());
                if (matcher.find()) {
                    if (matcher.group(2).equals(opcService.getOpcip())) {
                        opcService.registerModlePin(modlePin);
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
                            opcService.registerFilter(modlePin);
                            break;
                        }
                    }

                }

            }


            /**register shockdetect*/
            if ((modlePin.getShockDetector() != null)) {

                if((modlePin.getShockDetector().getBackToDCSTag() != null) && !(modlePin.getShockDetector().getOpcresource().equals(""))){
                    for (OPCService opcService : opcservepool.values()) {
                        Matcher matcher = opcpattern.matcher(modlePin.getShockDetector().getOpcresource());
                        if (matcher.find()) {
                            if (matcher.group(2).equals(opcService.getOpcip())) {
                                opcService.registerShockDetectortag(modlePin.getShockDetector().getBackToDCSTag());
                                break;
                            }
                        }

                    }
                }
                if((modlePin.getShockDetector().getFilterbacktodcstag() != null) && !(modlePin.getShockDetector().getFilteropcresource().equals(""))){
                    for (OPCService opcService : opcservepool.values()) {
                        Matcher matcher = opcpattern.matcher(modlePin.getShockDetector().getFilteropcresource());
                        if (matcher.find()) {
                            if (matcher.group(2).equals(opcService.getOpcip())) {
                                opcService.registerShockDetectortag(modlePin.getShockDetector().getFilterbacktodcstag() );
                                break;
                            }
                        }

                    }
                }

                //初始化检测器
                modlePin.getShockDetector().componentrealize();


            }



        }

    }


    public boolean writeModlePinValie(ModlePin modlePin, Double value) {
        if ((modlePin.getResource() != null) && (modlePin.getResource().equals("constant"))) {
            return true;
        } else if ((modlePin.getResource() != null)) {
            /**register pin*/
            for (OPCService opcService : opcservepool.values()) {
                Pattern pattern = Pattern.compile("([a-zA-Z]*)([0-9|.]*)");
                Matcher matcher = pattern.matcher(modlePin.getResource());
                if (matcher.find()) {
                    if (matcher.group(2).equals(opcService.getOpcip())) {
                        return opcService.writeTagvalue(modlePin.getModleOpcTag(), value);
                    }
                }

            }

        }
        return false;

    }


    public boolean unregisterModlePinAndComponent(ModlePin modlePin) {
        if ((modlePin.getResource() != null) && (modlePin.getResource().equals("constant"))) {
            return true;
        } else if ((modlePin.getResource() != null)) {
            /**unregister pin*/
            for (OPCService opcService : opcservepool.values()) {
                Matcher matcher = opcpattern.matcher(modlePin.getResource());
                if (matcher.find()) {
                    if (matcher.group(2).equals(opcService.getOpcip())) {
                        try {
                            opcService.unregisterModlePin(modlePin);
                        } catch (Exception e) {
                            logger.error("opc位号:" + modlePin.getModleOpcTag() + "移除失败");
                            logger.error(e.getMessage(), e);

                        }
                    }
                }

            }

            /**unregister filter*/
            if ((modlePin.getFilter() != null) && (modlePin.getFilter().getBackToDCSTag() != null) && !(modlePin.getFilter().getBackToDCSTag().equals(""))) {
                for (OPCService opcService : opcservepool.values()) {
                    Matcher matcher = opcpattern.matcher(modlePin.getFilter().getOpcresource());
                    if (matcher.find()) {
                        if (matcher.group(2).equals(opcService.getOpcip())) {
                            opcService.unregisterFilter(modlePin);
                        }
                    }

                }

            }


            /**unregister shockdetect*/
            if ((modlePin.getShockDetector() != null)) {
                if (modlePin.getShockDetector().getBackToDCSTag() != null && (modlePin.getShockDetector().getOpcresource() != null)) {
                    for (OPCService opcService : opcservepool.values()) {
                        Matcher matcher = opcpattern.matcher(modlePin.getShockDetector().getOpcresource());
                        if (matcher.find()) {
                            if (matcher.group(2).equals(opcService.getOpcip())) {
                                opcService.unregisterShockdetectortag(modlePin.getShockDetector().getBackToDCSTag());
                            }
                        }

                    }
                }
                /**震荡输出位号*/
                if ((modlePin.getShockDetector().getFilterbacktodcstag() != null) && !(modlePin.getShockDetector().getFilteropcresource().equals(""))) {
                    for (OPCService opcService : opcservepool.values()) {
                        Matcher matcher = opcpattern.matcher(modlePin.getShockDetector().getFilteropcresource());
                        if (matcher.find()) {
                            if (matcher.group(2).equals(opcService.getOpcip())) {
                                opcService.unregisterShockdetectortag(modlePin.getShockDetector().getFilterbacktodcstag());
                            }
                        }

                    }
                }

            }


            /**pv引脚位号使能opc位号*/
            /*for (OPCService opcService : opcservepool.values()) {
                if((modlePin.getDcsEnabePin()!=null)&&(modlePin.getDcsEnabePin().getModleOpcTag()!=null)&&(modlePin.getDcsEnabePin().getResource()!=null)&&(!modlePin.getDcsEnabePin().getModleOpcTag().equals(""))){
                    Matcher matcher = opcpattern.matcher(modlePin.getDcsEnabePin().getResource());
                    if (matcher.find()) {
                        if (matcher.group(2).equals(opcService.getOpcip())) {
                            try {
                                opcService.unregisterCommonOPCTag(modlePin.getDcsEnabePin().getModleOpcTag());
                            } catch (Exception e) {
                                logger.error("opc位号:" + modlePin.getModleOpcTag() + "移除失败");
                                logger.error(e.getMessage(), e);

                            }
                        }
                    }
                }

            }*/


        }
        return true;

    }


    public void selfinit() {
        List<OPCService> opcServiceList = opcDBServe.getopcserves();
        for (OPCService service : opcServiceList) {
            try {
                service.initAndConnect(filterService, modleStopRunMonitor);
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

    /**
     * register modleConstainerl into OPCserviceConstainer and OPCServe
     * */
    public void setModleConstainerl(ModleConstainer modleConstainerl) {
        this.modleConstainerl = modleConstainerl;
        for(OPCService opcService : opcservepool.values()){
            opcService.setModleConstainerl(modleConstainerl);
        }

    }

}

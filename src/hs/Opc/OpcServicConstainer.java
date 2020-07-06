package hs.Opc;

import hs.Bean.ModlePin;
import hs.Dao.Service.OpcDBServe;
import hs.Filter.FilterService;
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

@Component
public class OpcServicConstainer {
    private static final Logger logger = Logger.getLogger(OpcServicConstainer.class);
    private FilterService filterService = null;//实时滤波服务
    private OpcDBServe opcDBServe = null;
    private ConcurrentHashMap<Integer, OPCService> opcservepool = new ConcurrentHashMap();//key=OPCserveid
    private ExecutorService executorService;

    @Autowired
    public OpcServicConstainer(FilterService filterService, OpcDBServe opcDBServe) {
        this.filterService = filterService;
        this.opcDBServe = opcDBServe;
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
                Pattern pattern = Pattern.compile("([a-zA-Z]*)([0-9|.]*)");
                Matcher matcher = pattern.matcher(modlePin.getResource());
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


    public void registerModlePin(ModlePin modlePin) {
        if ((modlePin.getResource() != null) && (modlePin.getResource().equals("constant"))) {
            return ;
        } else if ((modlePin.getResource() != null)) {
            /**register pin*/
            for (OPCService opcService : opcservepool.values()) {
                Pattern pattern = Pattern.compile("([a-zA-Z]*)([0-9|.]*)");
                Matcher matcher = pattern.matcher(modlePin.getResource());
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
                    Pattern pattern = Pattern.compile("([a-zA-Z]*)([0-9|.]*)");
                    Matcher matcher = pattern.matcher(modlePin.getFilter().getOpcresource());
                    if (matcher.find()) {
                        if (matcher.group(2).equals(opcService.getOpcip())) {
                            opcService.registerFilter(modlePin);
                            break;
                        }
                    }

                }

            }


        }

    }




    public boolean writeModlePinValie(ModlePin modlePin,Double value) {
        if ((modlePin.getResource() != null) && (modlePin.getResource().equals("constant"))) {
            return true;
        } else if ((modlePin.getResource() != null)) {
            /**register pin*/
            for (OPCService opcService : opcservepool.values()) {
                Pattern pattern = Pattern.compile("([a-zA-Z]*)([0-9|.]*)");
                Matcher matcher = pattern.matcher(modlePin.getResource());
                if (matcher.find()) {
                    if (matcher.group(2).equals(opcService.getOpcip())) {
                        return opcService.writeTagvalue(modlePin.getModleOpcTag(),value);
                    }
                }

            }

        }
        return false;

    }



    public boolean unregisterModlePin(ModlePin modlePin) {
        if ((modlePin.getResource() != null) && (modlePin.getResource().equals("constant"))) {
            return true;
        } else if ((modlePin.getResource() != null)) {
            /**unregister pin*/
            for (OPCService opcService : opcservepool.values()) {
                Pattern pattern = Pattern.compile("([a-zA-Z]*)([0-9|.]*)");
                Matcher matcher = pattern.matcher(modlePin.getResource());
                if (matcher.find()) {
                    if (matcher.group(2).equals(opcService.getOpcip())) {
                        opcService.unregisterModlePin(modlePin);
                    }
                }

            }

            /**unregister filter*/
            if ((modlePin.getFilter() != null) && (modlePin.getFilter().getBackToDCSTag() != null) && !(modlePin.getFilter().getBackToDCSTag().equals(""))) {
                for (OPCService opcService : opcservepool.values()) {
                    Pattern pattern = Pattern.compile("([a-zA-Z]*)([0-9|.]*)");
                    Matcher matcher = pattern.matcher(modlePin.getFilter().getOpcresource());
                    if (matcher.find()) {
                        if (matcher.group(2).equals(opcService.getOpcip())) {
                            opcService.unregisterFilter(modlePin);
                        }
                    }

                }

            }


        }
        return true;

    }


    public void selfinit() {
        List<OPCService> opcServiceList = opcDBServe.getopcserves();
        for (OPCService service : opcServiceList) {
            try {
                service.initAndConnect(filterService);
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
}

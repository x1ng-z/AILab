package hs.Configuartion;
import hs.Bean.ControlModle;
import hs.Dao.Service.ModleServe;
import org.apache.log4j.Logger;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.AutoReconnectController;
import org.openscada.opc.lib.da.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/17 10:24
 */
@Configuration
@ImportResource("classpath:applicationContext.xml")
public class SpringAnnotationConfigure {
    private static final Logger logger=Logger.getLogger(SpringAnnotationConfigure.class);

    @Autowired
    private ModleServe modleServe;

    @Bean
    @Qualifier("opcconninfo")
    public AutoReconnectController  newOpcServeInstance(ConnectionInformation ci) {
        Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());

        AutoReconnectController controller = new AutoReconnectController(server);
        //controller.connect();

        return controller;
    }

    @Bean
    public void newModleConstainerInstance(){

        List<ControlModle> controlModleList=modleServe.findAllModle();
       for(ControlModle controlModle:controlModleList){
           controlModle.realizeModle();
       }

    }


}

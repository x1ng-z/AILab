package hs.Servlet;

import hs.ApcAlgorithm.ExecutePythonBridge;
import hs.Bean.ModleConstainer;
import hs.Opc.OPCService;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletException;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/18 15:37
 */
public class BootstatServlet extends DispatcherServlet {
    private static final Logger logger=Logger.getLogger(BootstatServlet.class);
    @Override
    protected void initFrameworkServlet() throws ServletException {
        super.initFrameworkServlet();
        logger.info("init complet");
        ApplicationContext applicationContext =getWebApplicationContext();

        OPCService OPCserver =applicationContext.getBean(OPCService.class);
        Thread opcthread=new Thread(OPCserver);
        opcthread.setDaemon(true);
        opcthread.start();

        ModleConstainer  modleConstainer=applicationContext.getBean(ModleConstainer.class);
//        List<ControlModle> modles=modleServe.getAllModle();
        logger.info("modle size is: "+modleConstainer.getModules().size());
        for(Integer key:modleConstainer.getModules().keySet()){
            String pythonhome="python.exe";
            String pythonjs="E:\\LinkAPC.py";
            String[] aa=new String[]{pythonhome,pythonjs,"http://localhost:8080/python/modlebuild/"+key+".do"};
            ExecutePythonBridge executePythonBridge=new ExecutePythonBridge();
            modleConstainer.getModules().get(key).setExecutePythonBridge(executePythonBridge);
            //executePythonBridge.execute(new LinkedBlockingQueue<>(),aa);
        }

    }
}

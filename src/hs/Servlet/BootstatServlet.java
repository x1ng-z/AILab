package hs.Servlet;

import hs.Bean.ModleConstainer;
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
        ModleConstainer modleConstainer =applicationContext.getBean(ModleConstainer.class);
//        modleConstainer.init();



//        Test4filter test4filter =applicationContext.getBean(Test4filter.class);
//        test4filter.update(2);
//        System.out.println(test4filter.getClass()+",hashcode="+test4filter.hashCode());

//
//        opcServicConstainer.toString();

//        OPCService OPCserver =applicationContext.getBean(OPCService.class);
//        Thread opcthread=new Thread(OPCserver);
//        opcthread.setDaemon(true);
//        opcthread.start();


//        FilterService filterService =applicationContext.getBean(FilterService.class);
//        Thread filterServicethread=new Thread(filterService);
//        filterServicethread.setDaemon(true);
//        filterServicethread.start();

//        ModleConstainer  modleConstainer=applicationContext.getBean(ModleConstainer.class);
////        List<ControlModle> modles=modleServe.getAllModle();
//        logger.info("modle size is: "+modleConstainer.getModules().size());
//        for(Integer key:modleConstainer.getModules().keySet()){
//
//            ExecutePythonBridge executePythonBridge=new ExecutePythonBridge(
//            "C:\\Program Files\\apache-tomcat-9.0.14\\webapps\\AILab\\LinkAPC.exe",
//            "http://localhost:8080/AILab/python/modlebuild/"+key+".do",key+"");
//            modleConstainer.getModules().get(key).setExecutePythonBridge(executePythonBridge);
//            if(modleConstainer.getModules().get(key).getModleEnable()==1){
//                executePythonBridge.execute();
//            }


//        }

    }
}

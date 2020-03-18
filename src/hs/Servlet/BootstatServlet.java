package hs.Servlet;

import hs.Bean.ControlModle;
import hs.Dao.Service.ModleServe;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletException;
import java.util.List;

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
        logger.error("init complet");
        ApplicationContext applicationContext =getWebApplicationContext();
        ModleServe modleServe=applicationContext.getBean(ModleServe.class);
        List<ControlModle> modles=modleServe.findAllModle();
        logger.info("modle size is: "+modles.size());
    }
}

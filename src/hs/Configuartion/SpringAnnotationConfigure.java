package hs.Configuartion;
import hs.Bean.BaseConf;
import hs.Dao.Service.ModleDBServe;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/17 10:24
 */
@Configuration
//@EnableAspectJAutoProxy
@ImportResource("classpath:applicationContext.xml")
public class SpringAnnotationConfigure {
    private static final Logger logger=Logger.getLogger(SpringAnnotationConfigure.class);

    @Autowired
    private ModleDBServe modleDBServe;

    @Bean
    public BaseConf BaseConf(){
        return modleDBServe.getBaseConf();
    }



}

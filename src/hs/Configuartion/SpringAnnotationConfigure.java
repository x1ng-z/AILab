package hs.Configuartion;
import hs.Bean.BaseConf;
import hs.Dao.Service.ModleServe;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

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
    public BaseConf BaseConf(){
        return modleServe.getBaseConf();
    }



}

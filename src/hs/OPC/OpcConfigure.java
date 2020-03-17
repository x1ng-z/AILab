import org.openscada.opc.lib.common.ConnectionInformation;

import java.io.IOException;
import java.util.Properties;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/17 8:50
 */

public class OpcConfigure {

    public static ConnectionInformation createInstance(String host,String domain,String username,String password,String clsid){
        ConnectionInformation ci=new ConnectionInformation();
        ci.setHost(host);
        ci.setDomain(domain);
        ci.setUser(username);
        ci.setPassword(password);
        ci.setProgId(null);
        ci.setClsid(clsid);
        return ci;
    }

}

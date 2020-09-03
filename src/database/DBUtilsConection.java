package screen;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;

public class DBUtilsConection {
    public static void doSshTunnel(String strSshUser, String strSshPassword,
                                    String strSshHost, int nSshPort, String strRemoteHost,
                                    int nLocalPort, int nRemotePort) throws JSchException {
        final JSch jsch = new JSch();
        Session session = jsch.getSession(strSshUser, strSshHost, 22);
        session.setPassword(strSshPassword);
        final Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        session.setPortForwardingL(nLocalPort, strRemoteHost, nRemotePort);
    }

}

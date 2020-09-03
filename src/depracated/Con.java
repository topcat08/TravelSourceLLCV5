import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.util.Properties;

public class Con {
	
	  private static void doSshTunnel( String strSshUser, String strSshPassword, String strSshHost, int nSshPort, String strRemoteHost, int nLocalPort, int nRemotePort ) throws JSchException
	  {
	    final JSch jsch = new JSch();
	    Session session = jsch.getSession( strSshUser, strSshHost, 22 );
	    session.setPassword( strSshPassword );
	     
	    final Properties config = new Properties();
	    config.put( "StrictHostKeyChecking", "no" );
	    session.setConfig( config );
	     
	    session.connect();
	    session.setPortForwardingL(nLocalPort, strRemoteHost, nRemotePort);
	  }

	public static void main(String[] args) {

		try {
			String strSshUser = "root"; // SSH loging username
			String strSshPassword = "TravelSourceLLC"; // SSH login password
			String strSshHost = "45.56.72.19"; // hostname or ip or
															// SSH server
			int nSshPort = 22; // remote SSH host port number
			String strRemoteHost = "localhost"; // hostname or
																	// ip of
																	// your
																	// database
																	// server
			int nLocalPort = 3366; // local port number use to bind SSH tunnel
			int nRemotePort = 3306; // remote port number of your database
			String strDbUser = "root"; // database loging username
			String strDbPassword = "samsung23"; // database login password

			doSshTunnel(strSshUser, strSshPassword, strSshHost,
					nSshPort, strRemoteHost, nLocalPort, nRemotePort);

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:" + nLocalPort, strDbUser,
					strDbPassword);
			Statement st = con.createStatement();
			String sql="SELECT * FROM Keywords ";
			st.execute(sql);
				con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

}

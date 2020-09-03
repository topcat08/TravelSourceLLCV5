import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.monitorjbl.xlsx.StreamingReader;

public class DataViewer {
	private static final String APPLICATION_NAME = "Google Sheets TravelSource applet";
	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";

	private static final List<String> SCOPES = Collections
			.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
	private static final String CREDENTIALS_FILE_PATH = "./cred.json";
	public static final String SAMPLE_XLSX_FILE_PATH = "./moveyourass.xlsx";

	static String strSshUser = "root"; // SSH loging username
	static String strSshPassword = "TravelSourceLLC"; // SSH login password
	static String strSshHost = "45.56.72.19"; // hostname or ip or
	static int nSshPort = 22; // remote SSH host port number
	static String strRemoteHost = "127.0.0.1"; // hostname
	static int nLocalPort = 3306; // local port number use to bind SSH tunnel
	static int nRemotePort = 3306; // remote port number of your database
	static String strDbUser = "root"; // database loging username
	static String strDbPassword = "samsung23"; // database login password

	private static Credential getCredentials(
			final NetHttpTransport HTTP_TRANSPORT) throws IOException {

		File f = new File("./cred.json");
		InputStream in = new FileInputStream(f);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: "
					+ CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
				JSON_FACTORY, new InputStreamReader(in));
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(
						new FileDataStoreFactory(new java.io.File(
								TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder()
				.setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver)
				.authorize("user");
	}

	private static HttpRequestInitializer setHttpTimeout(
			final HttpRequestInitializer requestInitializer) {
		return new HttpRequestInitializer() {
			@Override
			public void initialize(HttpRequest httpRequest) throws IOException {
				requestInitializer.initialize(httpRequest);
				httpRequest.setConnectTimeout(99 * 60000);
				httpRequest.setReadTimeout(99 * 60000);
			}
		};
	}

	static Session session = null;

	static void doSshTunnel(String strSshUser, String strSshPassword,
			String strSshHost, int nSshPort, String strRemoteHost,
			int nLocalPort, int nRemotePort) throws JSchException {
		final JSch jsch = new JSch();
		session = jsch.getSession(strSshUser, strSshHost, 22);
		session.setPassword(strSshPassword);
		final Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
		System.out.println("Connected");
		session.setPortForwardingL(nLocalPort, strRemoteHost, nRemotePort);
	}

	public static void main(String... args)   {

		// ----------------DB OPEN--------------------
 		 try {
		 doSshTunnel(strSshUser, strSshPassword, strSshHost,
		 nSshPort, strRemoteHost, nLocalPort, nRemotePort);
		 } catch (JSchException e1) {
		 e1.printStackTrace();
		 }
		 try {
		 Class.forName("com.mysql.jdbc.Driver");
		 } catch (ClassNotFoundException e1) {
		 e1.printStackTrace();
		 }
		 Connection con = null;
		 try {
		 con = DriverManager.getConnection("jdbc:mysql://localhost:"
		 + nLocalPort + "/TravelSourceLLC", strDbUser,
		 strDbPassword);
		 } catch (SQLException e1) {
		 e1.printStackTrace();
		 }
			InputStream is = null;
			try {
				is = new FileInputStream(new File("./moveyourass.xlsx"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			Statement st = null ;
			 try {
					  st = con.createStatement();
							 System.out.println("Hurray, connected.");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			Workbook workbook = StreamingReader.builder()
			        .rowCacheSize(1000)    // number of rows to keep in memory (defaults to 10)
			        .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
			        .open(is);            // InputStream or File for XLSX file (required)
			int index = 0;
		      Statement stmt=null;
			try {
				stmt = con.createStatement();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			for (Sheet sheet : workbook){
				  for (Row row : sheet) {

 				  }
				}
			 try {
				stmt.executeBatch();
			} catch (SQLException e1) {
			 	e1.printStackTrace();
			}
		 
		 try {
		 con.close();
		 } catch (SQLException e) {
		 e.printStackTrace();
		 }
		 System.out.println("Keyword TravelSourceLLC Viewer has finished!");
	//TO DO: set listener and do exit on close application listener


	}

	private static String getKeywords() {
		String insertStatement = "Select * from Keywords";
		return insertStatement;
	}
}
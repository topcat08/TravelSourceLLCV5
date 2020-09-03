import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Test extends JFrame {

	private JPanel contentPane;
	private JTable table;

	public static final String SAMPLE_XLSX_FILE_PATH = "./moveyourass.xlsx";

	private Label label;
	private JButton btnNewButton;
	private TextField textField;
	private Panel panel;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Test frame = new Test();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static void doSshTunnel(String strSshUser, String strSshPassword,
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

	public Test() {
		setBackground(UIManager.getColor("Button.select"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBackground(UIManager.getColor("Button.shadow"));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
		setVisible(true);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		panel = new Panel();
		panel.setBounds(12, 10, 716, 41);
		contentPane.add(panel);

		label = new Label("TravelSourceLLC Keyword Viewer");
		panel.add(label);

		textField = new TextField();
		textField.setColumns(30);
		panel.add(textField);

		btnNewButton = new JButton("Go");
		panel.add(btnNewButton);
		btnNewButton.setVerticalAlignment(SwingConstants.TOP);
		btnNewButton.setHorizontalAlignment(SwingConstants.LEADING);

		table = new JTable();
		table.setBackground(UIManager.getColor("Button.highlight"));

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(12, 57, 1082, 679);
		scrollPane.setMaximumSize(new Dimension(Short.MAX_VALUE,
				Short.MAX_VALUE));
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(scrollPane);

		Connection con = null;

 			String strSshUser = "root";
			String strSshPassword = "TravelSourceLLC";
			String strSshHost = "45.56.72.19";
			int nSshPort = 22;
			String strRemoteHost = "localhost";
			int nLocalPort = 3366;
			int nRemotePort = 3306;
			String strDbUser = "root";
			String strDbPassword = "samsung23";

			try {
				doSshTunnel(strSshUser, strSshPassword, strSshHost, nSshPort,
						strRemoteHost, nLocalPort, nRemotePort);
			} catch (JSchException e1) {
				e1.printStackTrace();
			}

			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			try {
				con = DriverManager
						.getConnection("jdbc:mysql://localhost:" + nLocalPort
								+ "/TravelSourceLLC", strDbUser, strDbPassword);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			Statement st = null;
			try {
				st = con.createStatement();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			System.out.println("Hurray, connected.");

			DefaultTableModel model = new DefaultTableModel(new String[] {
					"Keywords", "Frequency", "Category", "Color",
					"Title Category" }, 0);
			String sql = "SELECT * FROM Keywords";
			ResultSet rs=null;
			try {
				rs = st.executeQuery(sql);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			try {
				while (rs.next()) {
					String d = rs.getString("keyword");
					String e = rs.getString("frequency");
					String f = rs.getString("category");
					String g = rs.getString("color");
					String h = rs.getString("title_category");
					model.addRow(new Object[] { d, e, f, g, h });
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			table.setModel(model);
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(
					model);
			table.setRowSorter(sorter);

			textField.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent arg0) {

				}
				@Override
				public void keyReleased(KeyEvent arg0) {

				}
				@Override
				public void keyTyped(KeyEvent arg0) {
					String text = textField.getText();
					if (text.length() == 0) {
						sorter.setRowFilter(null);
					} else {
						sorter.setRowFilter(RowFilter.regexFilter(text));
					}
				}
			});

			btnNewButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String text = textField.getText();
					if (text.length() == 0) {
						sorter.setRowFilter(null);
					} else {
						sorter.setRowFilter(RowFilter.regexFilter(text));
					}
				}
			});
			System.out.println("Task finished!");
	}
}

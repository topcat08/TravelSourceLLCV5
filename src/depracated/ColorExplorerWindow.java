import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import screen.Colourful;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

public class ColorExplorerWindow extends JFrame {

	private JPanel contentPane;
	private JTable table;
	/**
	 * Global instance of the scopes required by this quickstart. If modifying
	 * these scopes, delete your previously saved tokens/ folder.
	 */
	public static final String SAMPLE_XLSX_FILE_PATH = "./moveyourass.xlsx";
	// db
	private Label label;
	private JButton btnNewButton;
	private TextField textField;
	private Panel panel;
	ArrayList<Colourful> colors = new ArrayList<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ColorExplorerWindow frame = new ColorExplorerWindow();
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

	boolean anyMatch(String[] words, String input) {
	    for(String s : words)
	        if(input.contains(s))
	            return true;
	    return false;
	}
	public static boolean useList(String[] arr, String targetValue) {
		return Arrays.asList(arr).contains(targetValue);
	}

	public ColorExplorerWindow() {
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

		label = new Label("TravelSourceLLC Color Viewer");
		panel.add(label);

		textField = new TextField();
		textField.setColumns(30);
		panel.add(textField);

		btnNewButton = new JButton("Go");
		panel.add(btnNewButton);
		btnNewButton.setVerticalAlignment(SwingConstants.TOP);
		btnNewButton.setHorizontalAlignment(SwingConstants.LEADING);
		DefaultTableModel model = new DefaultTableModel(new String[] {
				"Keyword", "Color", "Hex", "Text Color","Text Hex",
		}, 0);

		table = new JTable(model) {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer,
											 int rowIndex, int columnIndex) {
				JComponent component = (JComponent) super.prepareRenderer(
						renderer, rowIndex, columnIndex);
				try {
//					System.out.print(getValueAt(rowIndex, columnIndex));
					String value = getValueAt(rowIndex, columnIndex).toString();
					String[] differences = value.split(" ");
					if (differences.length <= 1) {
//						if (useList(red, getValueAt(rowIndex, columnIndex).toString())) {
//							component.setBackground(Color.RED);
//						} else if (useList(grey, getValueAt(rowIndex, columnIndex).toString())) {
//							component.setBackground(Color.GRAY);
//						} else if (useList(yellow, getValueAt(rowIndex, columnIndex).toString())) {
//							component.setBackground(Color.YELLOW);
//						} else component.setBackground(Color.WHITE);
						for(Colourful c: colors) {
						//	System.out.println(c.color);
						}
					} else {
					}
				}catch (Exception e){
					e.printStackTrace();
				}
				return component;
			}
		};
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
		loadDataWithSchedueleRightNow(model, false);
	}

	private void loadDataWithSchedueleRightNow(DefaultTableModel model, boolean remoteDBC){
		Connection con = null;
		int nLocalPort = 3366; // local port number use to bind SSH tunnel

		if (remoteDBC) {
			String strSshUser = "root"; // SSH loging username
			String strSshPassword = "TravelSourceLLC"; // SSH login password
			String strSshHost = "45.56.72.19"; // hostname or ip or SSH server
			int nSshPort = 22; // remote SSH host port number
			String strRemoteHost = "localhost"; // hostname or ip of your database server
			int nRemotePort = 3306; // remote port number of your database
			String strDbUser = "root"; // database loging username
			String strDbPassword = "samsung23"; // database login password
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
		} else {
 			try { // local port number use to bind SSH tunnel
				String strDbUser = "root"; // database loging username
				String strDbPassword = "Samsung23@!"; // database login
				Class.forName("com.mysql.cj.jdbc.Driver");
				nLocalPort=3306;
				con = DriverManager.getConnection(
						"jdbc:mysql://localhost:" + nLocalPort + "/TravelSourceLLC", strDbUser,
						strDbPassword);
 			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Statement st = null;
		try {
			st = con.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
//		System.out.println("Hurray, connected.");

		String sql = "Select * from Color";
		ResultSet rs = null;
		try {
			rs = st.executeQuery(sql);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		try {
			while (rs.next()) {
				Colourful data = new Colourful();
				String a = rs.getString("Color");
				String b = rs.getString("keyword");
				String c = rs.getString("TextColor");
				String d = rs.getString("HexColor");
				data.color=a;
				data.keyword=b;
				data.textHex=c;
				data.hex=d;
				colors.add(data);
				model.addRow(new Object[] {b, a, c,d});
			}
		}catch (SQLException e){
			e.printStackTrace();
		}

		 TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(
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
			public void keyTyped(KeyEvent arg0){
				if (textField.getText().toString().length() == 0) {
					sorter.setRowFilter(null);
				} else {
					sorter.setRowFilter(RowFilter.regexFilter(textField.getText()));
				}
			}
		});

		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (textField.getText().toString().length() == 0) {
					sorter.setRowFilter(null);
				} else {
					sorter.setRowFilter(RowFilter.regexFilter(textField.getText().toString()));
				}
			}
		});

		 try {
		con.close();
		 } catch (SQLException e) {
		 e.printStackTrace();
		 }
//		System.out.println("Goodbye!");
		 //TO DO: remember to close connection on application exit
	}
	// a simple object that holds data about a particular wine


	class ColourfulTableModel extends AbstractTableModel {
		// holds the strings to be displayed in the column headers of our table
		final String[] columnNames = { "Keyword", "Color", "Hex Color", "Text Color"};

		// holds the data types for all our columns
		final Class[] columnClasses = { String.class, String.class,
				Float.class, Boolean.class };

		// holds our data
		final Vector data = new Vector();

		// adds a row
		public void addWine(Colourful w) {
			data.addElement(w);
			fireTableRowsInserted(data.size() - 1, data.size() - 1);
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return null;
		}

		public Object getValueAtRow(int row) {
			Colourful rowa = (Colourful) data.elementAt(row);
			return rowa;
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}

}

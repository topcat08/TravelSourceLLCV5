import java.awt.Color;
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
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import screen.ColorUtils;
import screen.Colourful;

public class DataExplorer extends JFrame {

	private JPanel contentPane;
	private JTable table;

	private static DecimalFormat df2 = new DecimalFormat("#.###############");
	private static DecimalFormat df1 = new DecimalFormat("#.##");

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
					DataExplorer frame = new DataExplorer();
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
	private String getColorName(String keyword) {
		for (Colourful c:colors) {
			if (keyword.equalsIgnoreCase(c.keyword) || keyword.contains(c.keyword)) {
				return c.color;
			}
		}
		return null;
	}

//	public static <T> List<T> searchIn(List<T> list,Matcher<T> m) {

//	}

	public static boolean useList(String[] arr, String targetValue) {		
		return Arrays.asList(arr).contains(targetValue);
	}
//	TableRowSorter<DefaultTableModel> sorter=null;
	public DataExplorer() {
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
		DefaultTableModel model = new DefaultTableModel(new String[] {"#",
				"Keyword 1", "Frequency 1", "Keyword 2", "Frequency 2",
				"Keyword 3", "Frequency 3", "Keyword 4", "Frequency 4",
				"Title Category"
				,"Average rate","F1","F2","F3","F4"
		}, 0);

		table = new JTable(model) {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer,
											 int rowIndex, int columnIndex) {
				JComponent component = (JComponent) super.prepareRenderer(
						renderer, rowIndex, columnIndex);

				if (columnIndex<8){
					try {
						String value = getValueAt(rowIndex, columnIndex).toString();
						String[] differences = value.split(" ");
						ArrayList<String> t = new ArrayList<>();
						for(Colourful c: colors) {
							t.add(c.keyword);
						}
						if (differences.length<=1) {
							if (t.contains(value)) {
								String color = getColorName(value);
								Color dd = ColorUtils.getColorFromName(color);
								component.setBackground(dd);
							} else component.setBackground(Color.WHITE);
						} else {
							for (int i=0;i<differences.length;i++){
								//		if (t.contains(value)) {
								String color = getColorName(value);
								if(color!=null){			Color dd = ColorUtils.getColorFromName(color);
									component.setBackground(dd);}
								//		} else component.setBackground(Color.WHITE);
							}
						}//a list of keywords
						// keywords contains value see in keywords sheet what color needs to be added

//					if (differences.length <= 1) {
//						try {
//							if (t.contains(differences[0])) {
//							for (screen.Colourful c:colors) {
//								if (c.keyword.equalsIgnoreCase(differences[0])) {
//									Color dd = screen.ColorUtils.getColorFromName(c.color);
//									component.setBackground(dd);
//								}
//							}
//						} else component.setBackground(Color.WHITE);
//								} catch (Exception e) {
//									e.printStackTrace();
//						}
//					} else {
//						for (int i = 0; i < differences.length; i++) {
//							try {
//								if (t.contains(differences[i])) {
//									for (screen.Colourful c:colors) {
//										if (c.keyword.equalsIgnoreCase(differences[i])) {
//											Color dd = screen.ColorUtils.getColorFromName(c.color);
//											component.setBackground(dd);
//										}
//									}
//								} else {
//									component.setBackground(Color.WHITE);
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//					}

					}catch (Exception e){
						e.printStackTrace();
					}
				} else component.setBackground(Color.WHITE);



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

		table.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				TableModel model = (TableModel) e.getSource();
				Object d= model.getValueAt(e.getFirstRow(),e.getColumn());
				if (e.getType()== TableModelEvent.UPDATE) {
					try {
						String strDbUser = "root"; // database loging username
						String strDbPassword = "Samsung23@!"; // database login
						Class.forName("com.mysql.cj.jdbc.Driver");
						int nLocalPort = 3306;
						Connection con = DriverManager.getConnection(
								"jdbc:mysql://localhost:" + nLocalPort + "/TravelSourceLLC", strDbUser,
								strDbPassword);

						String query = "UPDATE `TravelSourceLLC`.`KeywordExpanded` SET `"+columnNames[e.getColumn()]+"`='" + table.getValueAt(table.getSelectedRow(), table.getSelectedColumn())
								+ "' WHERE `idKeywordExpanded`='" + table.getValueAt(table.getSelectedRow(),0) + "'";

						System.out.println(query + "//"+table.getValueAt(table.getSelectedRow(),0) +" --- "+table.getValueAt(table.getRowSorter().convertRowIndexToView(table.getSelectedRow()),0));
						PreparedStatement ps = con.prepareStatement(query);
						ps.execute();
					} catch (ClassNotFoundException ex) {
						ex.printStackTrace();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}

			}
		});

	}

	String columnNames[] =null;
	ArrayList<Integer> ids = new ArrayList<Integer>();
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
		ResultSet rsd = null;
		try {
			rsd = st.executeQuery(sql);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		try {
			while (rsd.next()) {
				Colourful data = new Colourful();
				String a = rsd.getString("Color");
				String b = rsd.getString("keyword");
				String c = rsd.getString("TextColor");
				String d = rsd.getString("HexColor");
				data.color=a;
				data.keyword=b;
				data.textHex=c;
				data.hex=d;
				colors.add(data);
			}
		}catch (SQLException e){
			e.printStackTrace();
		}


		sql = "SELECT * FROM KeywordExpanded";
		ResultSet rs = null;
		try {
			rs = st.executeQuery(sql);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		double totalSumF1 =0.0,totalSumF2=0.0,totalSumF3=0.0,totalSumF4=0.0;

		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int count =1;
			int columnCount=rsmd.getColumnCount();
			 columnNames = new String[columnCount];
			while (count<columnCount) {
				columnNames[count-1]= rsmd.getColumnLabel(count);
				count++;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}


		try{
			while(rs.next()) {
				ids.add(rs.getInt("idKeywordExpanded"));
				double buf= 0.0;
				double buffer1 =0.0,buffer2=0.0,buffer3=0.0,buffer4=0.0;
				try {
					buffer1 = rs.getDouble("frequency_1");
					totalSumF1 +=buffer1;
				} catch(NumberFormatException de) {
					de.printStackTrace();
				} finally {
					try{
					buffer2=rs.getDouble("frequency_2");
					totalSumF1 +=buffer2;
					}catch (NumberFormatException z) {
					z.printStackTrace();
					}finally {
						try{
							buffer3=rs.getDouble("frequency_3");
							totalSumF1 +=buffer3;
						}catch (NumberFormatException z) {
						z.printStackTrace();
						}finally {
							try{
								buffer4=rs.getDouble("frequency_4");
								totalSumF1 +=buffer4;
							}catch (NumberFormatException z) {
							z.printStackTrace();
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			rs.beforeFirst();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			while (rs.next()) {
				double e=0.0;
				double g=0.0;
				double i=0.0;
				double k= 0.0;
				int index = rs.getInt("idKeywordExpanded");
				String d = rs.getString("keyword_1");
				String f = rs.getString("keyword_2");
				String h = rs.getString("keyword_3");
				String j = rs.getString("keyword_4");
				String l = rs.getString("title_category");
				DecimalFormat kk = new DecimalFormat();
				try{
					 e = rs.getDouble("frequency_1");
				} catch (NumberFormatException aa) {
				}finally {
					try{
						g = rs.getDouble("frequency_2");
					} catch (NumberFormatException bb) {
					} finally {
						try{
							i = rs.getDouble("frequency_3");
						} catch (NumberFormatException cc) {
						} finally {
							try{
								k = rs.getDouble("frequency_4");
							} catch (NumberFormatException ee) {
							}
						}
					}
				}
				System.out.println(totalSumF1+"");
				String eet="";
				String ggt="";
				String iit="";
				String kky="";
				String eer = String.valueOf(e);
				if (e ==0) {
					eet ="";
				} else {
					double ds = (double) e*100.0/totalSumF1;
					System.out.println(ds+"");
//					eer= String.valueOf(ds);
//	eet = ds+"";
					try{
						eet = df2.format(ds);
					}catch(Exception oodd) {
					oodd.printStackTrace();
					}
				}
//				String ggr = String.valueOf(g);
//				if (g==0) {
//					ggr ="";
//				} else {
//					Double gfd = (double) g*100.0/totalSumF1;
//					ggr= String.valueOf(gfd);
//					ggt=String.valueOf(g);
//					try{
//						ggt = df2.format(ggt);
//					}catch(Exception dd) {
//					}
//					try{
//						//ggr = df2.format(ggr);
//					}catch(Exception dd) {
//					}
//				}
//				String iir = String.valueOf(i);
//				if (i ==0) {
//					iir ="";
//				} else {
//					Double llk = (double) i*100.0/totalSumF1;
//
//					iir= String.valueOf(llk);
//					iit = String.valueOf(i);
//					try{
//						iit = df2.format(iit);
//					}catch(Exception dd) {
//					}
//					try{
//						//iir = df2.format(iir);
//					}catch(Exception dd) {
//					}
//				}
//				String kkr = String.valueOf(k);
//				if (k ==0) {
//					kkr ="";
//				} else {
//					Double llde = (double) k*100.0/totalSumF1;
//					kkr= String.valueOf(llde);
//					kky=String.valueOf(k);
//					try{
//						kky = df2.format(kky);
//					}catch(Exception dd) {
//						dd.printStackTrace();
//					}
//					try{
//					//	kkr = df2.format(kkr);
//					}catch(Exception dd) {
//					}
//				}
				model.addRow(new Object[] { index,d, e, f, g, h, iit, j, kky, l ," ", (eet),(eet),(eet),(eet)});
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		table.setModel(model);
//		sorter = new TableRowSorter<DefaultTableModel>(
//				model);

//		class IntComparator implements Comparator {
//
//			@Override
//			public int compare(Object o1, Object o2) {
//				Integer int1 = (Integer)o1;
//				Integer int2 = (Integer)o2;
//				return int1.compareTo(int2);
//			}
//
//			public boolean equals(Object o2) {
//				return this.equals(o2);
//			}
//		}
//
//		sorter.setComparator(0, new IntComparator());
//		sorter.setComparator(1, new IntComparator());

//		table.setRowSorter(sorter);
		table.setAutoCreateRowSorter(true);

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
//					loadDataWithSchedueleRightNow(model,false);
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
	public class Keywords {
		private String category_title;
		private String frequency_1;
		private String freqeuncy_2;
		private String frequency_3;
		private String frequency_4;

		private String keyword_1;
		private String keyword_2;
		private String keyword_3;
		private String keyword_4;

		public Keywords(String category_title, String frequency_1,
				String frequency_2, String frequency_3, String frequency_4,
				String keyword_1, String keyword_2, String keyword_3,
				String keyword_4) {
			this.category_title = category_title;
			this.frequency_1 = frequency_1;
			this.freqeuncy_2 = frequency_2;
			this.frequency_3 = frequency_3;
			this.frequency_4 = frequency_4;
		}

		public String getCategory_title() {
			return category_title;
		}

		public void setCategory_title(String category_title) {
			this.category_title = category_title;
		}

		public String getFrequency_1() {
			return frequency_1;
		}

		public void setFrequency_1(String frequency_1) {
			this.frequency_1 = frequency_1;
		}

		public String getFreqeuncy_2() {
			return freqeuncy_2;
		}

		public void setFreqeuncy_2(String freqeuncy_2) {
			this.freqeuncy_2 = freqeuncy_2;
		}

		public String getFrequency_3() {
			return frequency_3;
		}

		public void setFrequency_3(String frequency_3) {
			this.frequency_3 = frequency_3;
		}

		public String getFrequency_4() {
			return frequency_4;
		}

		public void setFrequency_4(String frequency_4) {
			this.frequency_4 = frequency_4;
		}

		public String getKeyword_1() {
			return keyword_1;
		}

		public void setKeyword_1(String keyword_1) {
			this.keyword_1 = keyword_1;
		}

		public String getKeyword_2() {
			return keyword_2;
		}

		public void setKeyword_2(String keyword_2) {
			this.keyword_2 = keyword_2;
		}

		public String getKeyword_3() {
			return keyword_3;
		}

		public void setKeyword_3(String keyword_3) {
			this.keyword_3 = keyword_3;
		}

		public String getKeyword_4() {
			return keyword_4;
		}

		public void setKeyword_4(String keyword_4) {
			this.keyword_4 = keyword_4;
		}
	}

	class KeywordTableModel extends AbstractTableModel {
		// holds the strings to be displayed in the column headers of our table
		final String[] columnNames = { "Keyword 1", "Frequency 1", "Keyword 2", "Frequency 2", "Keyword 3", "Frequency 3", "Keyword 4", "Frequency 4", "Title Category" };

		// holds the data types for all our columns
		final Class[] columnClasses = { String.class, String.class,
				Float.class, Boolean.class };

		// holds our data
		final Vector data = new Vector();

		// adds a row
		public void addWine(Keywords w) {
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
			Keywords rowa = (Keywords) data.elementAt(row);
			return rowa;
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}
	}

}

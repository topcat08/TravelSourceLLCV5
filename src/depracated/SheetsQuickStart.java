import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.monitorjbl.xlsx.StreamingReader;

public class SheetsQuickStart {
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

	public static void main(String... args) {
		new java.util.Timer().schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				Connection con=null;
				try {
					String strSshUser = "root"; // SSH loging username
					String strSshPassword = "TravelSourceLLC"; // SSH login
																// password
					String strSshHost = "45.56.72.19"; // hostname or ip or
														// SSH server
					int nSshPort = 22; // remote SSH host port number
					String strRemoteHost = "localhost"; // hostname or
														// ip of
														// your
														// database
														// server
					int nLocalPort = 3366; // local port number use to bind SSH
											// tunnel
					int nRemotePort = 3306; // remote port number of your
											// database
					String strDbUser = "root"; // database loging username
					String strDbPassword = "samsung23"; // database login
														// password

					doSshTunnel(strSshUser, strSshPassword, strSshHost,
							nSshPort, strRemoteHost, nLocalPort, nRemotePort);

					Class.forName("com.mysql.jdbc.Driver");
					 con = DriverManager.getConnection(
							"jdbc:mysql://localhost:" + nLocalPort + "/TravelSourceLLC", strDbUser,
							strDbPassword);
					Statement st = con.createStatement();
				} catch (Exception e) {
					e.printStackTrace();
				}  
				InputStream is = null;
				
				try {
					is = new FileInputStream(new File("./THE FILE"));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				 
				Workbook workbook = StreamingReader.builder()
						.rowCacheSize(1000) // number of rows to keep in memory
											// (defaults to 10)
						.bufferSize(4096) // buffer size to use when reading
											// InputStream to file (defaults to
											// 1024)
						.open(is); // InputStream or File for XLSX file
									// (required)
				int index = 0;
				Statement stmt = null;
				try {
					stmt = con.createStatement();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				for (Sheet sheet : workbook) {
					for (Row row : sheet) {
						try {

							String keyword1 = ((row.getCell(0) == null)
									|| (row.getCell(0).getStringCellValue() == null) ? ""
									: row.getCell(0).getStringCellValue());
 							String frequency1 =  ((row.getCell(1) == null)
									|| (row.getCell(1).getStringCellValue() == null) ? ""
									: row.getCell(1).getStringCellValue());
							 
 							
							String keyword2 = ((row.getCell(2) == null)
									|| (row.getCell(2).getStringCellValue() == null) ? ""
									: row.getCell(2).getStringCellValue());
 							String frequency2 =  ((row.getCell(3) == null)
									|| (row.getCell(3).getStringCellValue() == null) ? ""
									: row.getCell(3).getStringCellValue());
				
							String keyword3 = ((row.getCell(4) == null)
									|| (row.getCell(4).getStringCellValue() == null) ? ""
									: row.getCell(4).getStringCellValue());

							String frequency3 =  ((row.getCell(5) == null)
									|| (row.getCell(5).getStringCellValue() == null) ? ""
									: row.getCell(5).getStringCellValue());

							String keyword4 = ((row.getCell(6) == null)
									|| (row.getCell(6).getStringCellValue() == null) ? ""
									: row.getCell(6).getStringCellValue());

							String frequency4 =  ((row.getCell(7) == null)
									|| (row.getCell(7).getStringCellValue() == null) ? ""
									: row.getCell(7).getStringCellValue());


							
							index = index + 1;
//							if (s0.length() > 1) {
								stmt.addBatch(insertKeywordsExp(keyword1, frequency1, keyword2, frequency2,keyword3,frequency3
										,keyword4,frequency4,sheet.getSheetName()));

//								insertKeywordsExp(String keyword1, double frequency1,String keyword2,double frequency2, 
//										String keyword3, double frequency3, String keyword4, double frequency4, String titleCategory) {
 							
//							}
//							if (s2.length() > 1) {
//								// PreparedStatement stmt =
//								// con.prepareStatement(
//								stmt.addBatch(insertKeywords(s2, s3, 2, "",
//										sheet.getSheetName()));
//							}
//							if (s4.length() > 1) {
//								// PreparedStatement stmt =
//								// con.prepareStatement(
//								stmt.addBatch(insertKeywords(s4, s5, 3, "",
//										sheet.getSheetName()));
//							}
//							if (s6.length() > 1) {
//								// PreparedStatement stmt =
//								// con.prepareStatement(
//								stmt.addBatch(insertKeywords(s6, s7, 4, "",
//										sheet.getSheetName()));
//							}
							if (index % 1000 == 0)stmt.executeBatch();
							System.out.print(sheet.getSheetName());
						} catch (NullPointerException e) {
							e.printStackTrace();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
				try {
					stmt.executeBatch();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// if (values == null || values.isEmpty()) {
				// System.out.println("No data found.");
				// } else {
				// // System.out.println("Name, Major");
				// for (List row : values) {
				// // Print columns A and E, which correspond to
				// // indices 0 and
				// // 4.

				// }
				// }
				// System.out.println("Database updated successfully...");
				// } catch (SQLException se) {
				// se.printStackTrace();
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
//				try {
//					con.close();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				System.out.println("Goodbye!");
			}
		}, 500);

		// Workbook workbook = null;
		// try {
		// workbook = WorkbookFactory.create(new File(
		// SAMPLE_XLSX_FILE_PATH));
		// } catch (EncryptedDocumentException | InvalidFormatException |
		// IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println("Workbook has " + workbook.getNumberOfSheets()
		// + " Sheets : ");
		//
		// /*
		// * =============================================================
		// * Iterating over all the sheets in the workbook (Multiple ways)
		// * =============================================================
		// */
		//
		// // 1. You can obtain a sheetIterator and iterate over it
		// Iterator<Sheet> sheetIterator = workbook.sheetIterator();
		// System.out.println("Retrieving Sheets using Iterator");
		// while (sheetIterator.hasNext()) {
		// Sheet sheet = sheetIterator.next();
		// System.out.println("=> " + sheet.getSheetName());
		// }
		//
		// // 2. Or you can use a for-each loop
		// System.out.println("Retrieving Sheets using for-each loop");
		// for (Sheet sheet : workbook) {
		// System.out.println("=> " + sheet.getSheetName());
		// }
		//
		// // 3. Or you can use a Java 8 forEach with lambda
		// System.out
		// .println("Retrieving Sheets using Java 8 forEach with lambda");
		// workbook.forEach(sheet -> {
		// System.out.println("=> " + sheet.getSheetName());
		// });
		//
		// /*
		// * ==================================================================
		// * Iterating over all the rows and columns in a Sheet (Multiple ways)
		// * ==================================================================
		// */
		//
		// // Getting the Sheet at index zero
		// Sheet sheet = workbook.getSheetAt(0);
		//
		// // Create a DataFormatter to format and get each cell's value as
		// String
		// DataFormatter dataFormatter = new DataFormatter();
		//
		// // 1. You can obtain a rowIterator and columnIterator and iterate
		// over
		// // them
		// System.out
		// .println("\n\nIterating over Rows and Columns using Iterator\n");
		// Iterator<Row> rowIterator = sheet.rowIterator();
		// while (rowIterator.hasNext()) {
		// Row row = rowIterator.next();
		//
		// // Now let's iterate over the columns of the current row
		// Iterator<Cell> cellIterator = row.cellIterator();
		//
		// while (cellIterator.hasNext()) {
		// Cell cell = cellIterator.next();
		// String cellValue = dataFormatter.formatCellValue(cell);
		// System.out.print(cellValue + "\t");
		// }
		// System.out.println();
		// }
		//
		// // 2. Or you can use a for-each loop to iterate over the rows and
		// // columns
		// System.out
		// .println("\n\nIterating over Rows and Columns using for-each loop\n");
		// for (Row row : sheet) {
		// for (Cell cell : row) {
		// String cellValue = dataFormatter.formatCellValue(cell);
		// System.out.print(cellValue + "\t");
		// }
		// System.out.println();
		// }
		//
		// // 3. Or you can use Java 8 forEach loop with lambda
		// System.out
		// .println("\n\nIterating over Rows and Columns using Java 8 forEach with lambda\n");
		// sheet.forEach(row -> {
		// row.forEach(cell -> {
		// String cellValue = dataFormatter.formatCellValue(cell);
		// System.out.print(cellValue + "\t");
		// });
		// System.out.println();
		// });
		//
		// // Closing the workbook
		// try {
		// workbook.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}
	protected static String insertKeywordsExp(String keyword1,
			double frequency1, String keyword2, double frequency2,
			String keyword3, double frequency3, double frequency4,
			String sheetName) {
 		return null;
	}

	private static String insertKeywordsExp(String keyword1, String frequency1,String keyword2,String frequency2, 
			String keyword3, String frequency3, String keyword4, String frequency4, String titleCategory) {
		String tenta="INSERT INTO `TravelSourceLLC`.`KeywordExpanded` ( `keyword_1`, `frequency_1`,"
				+ " `keyword_2`, `frequency_2`, `keyword_3`, `frequency_3`, `keyword_4`, `frequency_4`, `title_category`)"
				+ " VALUES ('"+keyword1+"', '"+frequency1+"', '"+keyword2+"', '"+frequency2+"', '"+keyword3+"', '"+frequency3
				+"', '"+keyword4+"', '"+frequency4+"', '"+titleCategory+"');";
		return tenta;
	}
}
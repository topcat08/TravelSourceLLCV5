import com.monitorjbl.xlsx.StreamingReader;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalDBInserter {

	public static void main(String... args) {
		int nLocalPort = 3306;
				Connection con = null;
				try { // local port number use to bind SSH tunnel
					String strDbUser = "root"; // database loging username
					String strDbPassword = "Samsung23@!"; // database login
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
					is = new FileInputStream(new File("./moveyourass.xlsx"));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				Workbook workbook = StreamingReader.builder()
						.rowCacheSize(100) // number of rows to keep in memory
						.bufferSize(4096) // buffer size to use when reading
						.open(is); // InputStream or File for XLSX file
				int index = 0;
				Statement stmt = null;
				try {
					stmt = con.createStatement();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

				for (Sheet sheet : workbook) {
					for (Row row : sheet) {
						try {
							String keyword1 = ((row.getCell(0) == null)
									|| (row.getCell(0).getStringCellValue() == null) ? ""
									: row.getCell(0).getStringCellValue());

							String frequency1 = ((row.getCell(1) == null)
									|| (row.getCell(1).getStringCellValue() == null) ? ""
									: row.getCell(1).getStringCellValue());

							String keyword2 = ((row.getCell(2) == null)
									|| (row.getCell(2).getStringCellValue() == null) ? ""
									: row.getCell(2).getStringCellValue());

							String frequency2 = ((row.getCell(3) == null)
									|| (row.getCell(3).getStringCellValue() == null) ? ""
									: row.getCell(3).getStringCellValue());

							String keyword3 = ((row.getCell(4) == null)
									|| (row.getCell(4).getStringCellValue() == null) ? ""
									: row.getCell(4).getStringCellValue());

							String frequency3 = ((row.getCell(5) == null)
									|| (row.getCell(5).getStringCellValue() == null) ? ""
									: row.getCell(5).getStringCellValue());

							String keyword4 = ((row.getCell(6) == null)
									|| (row.getCell(6).getStringCellValue() == null) ? ""
									: row.getCell(6).getStringCellValue());

							String frequency4 = ((row.getCell(7) == null)
									|| (row.getCell(7).getStringCellValue() == null) ? ""
									: row.getCell(7).getStringCellValue());

							index = index + 1;
							try {
								stmt.addBatch(insertKeywordsExp(keyword1, frequency1, keyword2, frequency2, keyword3, frequency3
										, keyword4, frequency4, sheet.getSheetName(), false));
							} catch (Exception e)  {
							e.printStackTrace();
							}
							try {
								//if (index % 100 == 0)
								stmt.executeBatch();
								//System.out.print(sheet.getSheetName());
							}catch (SQLSyntaxErrorException e) {
								e.printStackTrace();
							} catch (NullPointerException e) {
								e.printStackTrace();
								insertKeywordsExp(keyword1, frequency1, keyword2, frequency2, keyword3, frequency3
										, keyword4, frequency4, sheet.getSheetName(),true);
							} catch (Exception d) {
								insertKeywordsExp(keyword1, frequency1, keyword2, frequency2, keyword3, frequency3
										, keyword4, frequency4, sheet.getSheetName(),true);
								d.printStackTrace();
							}
						} catch (NullPointerException e) {
							e.printStackTrace();
						}
					}
				}
		try {
			stmt.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected static String insertKeywordsExp(String keyword1,
			double frequency1, String keyword2, double frequency2,
			String keyword3, double frequency3, double frequency4,
			String sheetName) {
		return null;
	}

	private static String insertKeywordsExp(String keyword1, String frequency1,String keyword2,String frequency2, 
			String keyword3, String frequency3, String keyword4, String frequency4, String titleCategory, boolean showLog) {
		Pattern pattern = Pattern.compile("'");

		String k1="",f1,k2="",f2,k3="",f3,k4="",f4,tcl ="";
		Matcher matcher = pattern.matcher(keyword1);
	//	if (      !matcher.matches()) {
			k1 = keyword1.replaceAll("'","-");
	//	}
	//	if (keyword2.contains("'")) {
			k2 = keyword2.replaceAll("'","-");
	//	}
	//	if (keyword3.contains("'")) {
			k3 = keyword3.replaceAll("'","-");
	//	}
	//	if (keyword4.contains("'")) {
			k4 = keyword4.replaceAll("'","-");
	//	}
	//	if (titleCategory.contains("'")) {
			tcl = titleCategory.replaceAll("'","-");
	//	}
		String tent = "INSERT INTO `TravelSourceLLC`.`KeywordExpanded` (`keyword_1`, `frequency_1`, `keyword_2`, `frequency_2`, `keyword_3`," +
				" `frequency_3`, `keyword_4`, `frequency_4`, `title_category`)" +
				" VALUES ('"+k1+"', '"+frequency1+"', '"+k2+"', '"+frequency2+"', '"+k3+"', '"+frequency3+"'" +
				", '"+k4+"', '"+frequency4+"', '"+tcl+"');";
		if (showLog) System.out.println(tent);
		return tent;
	}
}

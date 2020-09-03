import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalDBColorInserter extends JFrame {

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
					is = new FileInputStream(new File("./colors.xlsx"));
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


							String red = ((row.getCell(0) == null)
									|| (row.getCell(0).getStringCellValue() == null) ? ""
									: row.getCell(0).getStringCellValue());

							String red2 = ((row.getCell(1) == null)
									|| (row.getCell(1).getStringCellValue() == null) ? ""
									: row.getCell(1).getStringCellValue());

							String yellow = ((row.getCell(2) == null)
									|| (row.getCell(2).getStringCellValue() == null) ? ""
									: row.getCell(2).getStringCellValue());

							String yellow2 = ((row.getCell(3) == null)
									|| (row.getCell(3).getStringCellValue() == null) ? ""
									: row.getCell(3).getStringCellValue());

							String  yellow3= ((row.getCell(4) == null)
									|| (row.getCell(4).getStringCellValue() == null) ? ""
									: row.getCell(4).getStringCellValue());

							String yellow4 = ((row.getCell(5) == null)
									|| (row.getCell(5).getStringCellValue() == null) ? ""
									: row.getCell(5).getStringCellValue());

							String gray = ((row.getCell(6) == null)
									|| (row.getCell(6).getStringCellValue() == null) ? ""
									: row.getCell(6).getStringCellValue());

							String black = ((row.getCell(7) == null)
									|| (row.getCell(7).getStringCellValue() == null) ? ""
									: row.getCell(7).getStringCellValue());

							String black2 = ((row.getCell(8) == null)
									|| (row.getCell(8).getStringCellValue() == null) ? ""
									: row.getCell(8).getStringCellValue());

							String black3 = ((row.getCell(9) == null)
									|| (row.getCell(9).getStringCellValue() == null) ? ""
									: row.getCell(9).getStringCellValue());

							String black4 = ((row.getCell(10) == null)
									|| (row.getCell(10).getStringCellValue() == null) ? ""
									: row.getCell(10).getStringCellValue());

							String grayx1 = ((row.getCell(11) == null)
									|| (row.getCell(11).getStringCellValue() == null) ? ""
									: row.getCell(11).getStringCellValue());

							String grayx2 = ((row.getCell(12) == null)
									|| (row.getCell(12).getStringCellValue() == null) ? ""
									: row.getCell(12).getStringCellValue());

							String light_green = ((row.getCell(13) == null)
									|| (row.getCell(13).getStringCellValue() == null) ? ""
									: row.getCell(13).getStringCellValue());

							String blue = ((row.getCell(14) == null)
									|| (row.getCell(14).getStringCellValue() == null) ? ""
									: row.getCell(14).getStringCellValue());

							String blue2 = ((row.getCell(15) == null)
									|| (row.getCell(15).getStringCellValue() == null) ? ""
									: row.getCell(15).getStringCellValue());

							String blue3 = ((row.getCell(16) == null)
									|| (row.getCell(16).getStringCellValue() == null) ? ""
									: row.getCell(16).getStringCellValue());

							String blue4 = ((row.getCell(17) == null)
									|| (row.getCell(17).getStringCellValue() == null) ? ""
									: row.getCell(17).getStringCellValue());

							String blue5 = ((row.getCell(18) == null)
									|| (row.getCell(18).getStringCellValue() == null) ? ""
									: row.getCell(18).getStringCellValue());

							String blue6 = ((row.getCell(19) == null)
									|| (row.getCell(19).getStringCellValue() == null) ? ""
									: row.getCell(19).getStringCellValue());

							String[] s = {
									red,red2,yellow,yellow2,yellow3,yellow4,gray,black,black2,black3,black4,grayx1,grayx2,light_green,
									blue,blue2,blue3,blue4,blue5,blue6
							};
							index = index + 1;

							try {
								stmt.addBatch(insertKeywordsExp("Red",red,"",""));
								stmt.addBatch(insertKeywordsExp("Red",red2,"",""));
								stmt.addBatch(insertKeywordsExp("Yellow",yellow,"",""));
								stmt.addBatch(insertKeywordsExp("Yellow",yellow2,"",""));
								stmt.addBatch(insertKeywordsExp("Yellow",yellow3,"",""));
								stmt.addBatch(insertKeywordsExp("Gray",gray,"",""));
								stmt.addBatch(insertKeywordsExp("Black",black,"",""));
								stmt.addBatch(insertKeywordsExp("Black",black2,"",""));
								stmt.addBatch(insertKeywordsExp("Black",black3,"",""));
								stmt.addBatch(insertKeywordsExp("Black",black4,"",""));
								stmt.addBatch(insertKeywordsExp("Gray",grayx1,"",""));
								stmt.addBatch(insertKeywordsExp("Gray",grayx2,"",""));
								stmt.addBatch(insertKeywordsExp("light_green",light_green,"",""));
								stmt.addBatch(insertKeywordsExp("Blue",blue,"",""));
								stmt.addBatch(insertKeywordsExp("Blue",blue2,"",""));
								stmt.addBatch(insertKeywordsExp("Blue",blue3,"",""));
								stmt.addBatch(insertKeywordsExp("Blue",blue4,"",""));
								stmt.addBatch(insertKeywordsExp("Blue",blue5,"",""));
								stmt.addBatch(insertKeywordsExp("Blue",blue6,"",""));

							} catch (Exception e)  {
							e.printStackTrace();
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

	private static String insertKeywordsExp(String color,String keyword,String textColor,String hexColor) {
		String a = "INSERT INTO `TravelSourceLLC`.`Color` ( `Color`, `Keyword`, `TextColor`, `HexColor`)" +
				" VALUES ( '"+color+"', '"+keyword+"', '"+textColor+"', '"+hexColor+"')";
		System.out.println(a);
		return a;
	}
}

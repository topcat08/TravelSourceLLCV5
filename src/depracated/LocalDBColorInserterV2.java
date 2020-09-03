import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class LocalDBColorInserterV2 extends JFrame {

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
							String category = ((row.getCell(0) == null)
									|| (row.getCell(0).getStringCellValue() == null) ? ""
									: row.getCell(0).getStringCellValue());
							String color = ((row.getCell(1) == null)
									|| (row.getCell(1).getStringCellValue() == null) ? ""
									: row.getCell(1).getStringCellValue());
							String hexColor = ((row.getCell(2) == null)
									|| (row.getCell(2).getStringCellValue() == null) ? ""
									: row.getCell(2).getStringCellValue());
							String textColor = ((row.getCell(3) == null)
									|| (row.getCell(3).getStringCellValue() == null) ? ""
									: row.getCell(3).getStringCellValue());
							String  keyword = ((row.getCell(4) == null)
									|| (row.getCell(4).getStringCellValue() == null) ? ""
									: row.getCell(4).getStringCellValue());
							index = index + 1;
							try {
								stmt.addBatch(insertKeywordsExp(category,color,hexColor,textColor,keyword));
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

	private static String insertKeywordsExp(String category, String color,String hexColor,String textColor,String keyword) {
		String query = "INSERT INTO `TravelSourceLLC`.`Color` ( `Category`, `Color`, `HexColor`, `TextColor`, `Keyword`)" +
				" VALUES ( '"+category+"', '"+color+"', '"+hexColor+"', '"+textColor+"', '"+keyword+"');";
		System.out.println(query);
		return query;
	}
}
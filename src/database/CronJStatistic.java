package screen;

import com.google.common.base.Stopwatch;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

public class CronJStatistic {
    TableRowSorter<DefaultTableModel> sorter = null;
    String columnNames[] = null;
//    static DefaultTableModel model = null;
    static double totalSumF1 = 0.0;
//    private JTable table1;
//    private JScrollPane scrollPane1;
//     static Connection con = null;
    private static DecimalFormat df1 = new DecimalFormat("#.##");
    static ArrayList<Colourful> colors = new ArrayList<>();
    static Statement statement = null;
    public static void main(String[] args) {
        Timer timer = new Timer();
        runOnServer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

            }
        }, 0, 6 * 3600000);
     }

     public static void runOnServer() {

         Stopwatch timer = Stopwatch.createUnstarted();
         timer.start();
         statement = getStatement();
         loadColours();
         loadProductFromDB();
         timer.stop();
         System.out.println("Method took loadProductFromDB: " + timer);
         timer.start();
         listModel = new DefaultListModel<>();
         String[][] matrix = new String[productsList.size()][colors.size()+1];
         ArrayList<String> columns = loadCategories();
         timer.stop();
         System.out.println("Method took loadCategories: " + timer);
         timer.start();
         HashMap<String, Double> map = getTotalSum();
         ArrayList<KeyWordSum> keyWordSums = getKeywordSumV4("");
         timer.stop();
         System.out.println("Method took getTotalSum:  +getKeywordSumV4 " + timer);
         timer.start();

         int rowIndex = 0;
         int columnIndex = 0;

         for (String product : productsList) {
             columnIndex = 1;
             double totalSum = map.get(product);
             for (Colourful co : colors) {
                 double d = 0.0;
                 if (co.categories != null) {
                     if (!listModel.contains(co.categories)) {
                         listModel.addElement(co.categories);
                         for (KeyWordSum kws : keyWordSums) {
                             try {
                                 if (co.categories.equals(kws.category) && kws.title_category.equals(product)) {
                                     d = d + kws.frequencySum;
                                 }
                             } catch (Exception e) {
                                 e.printStackTrace();
                             }
                             co.setTotalSumPerColor(d);
                             try {
                                 double result = d * 100 / totalSum;
                                 matrix[rowIndex][columnIndex] = df1.format(result);
                             } catch (Exception e) {
                                 e.printStackTrace();
                             }
                         }
                     }
                 }
                 columnIndex++;
             }
             matrix[rowIndex][0] = product;
             System.out.println(product);
             rowIndex++;
             listModel.clear();
//               if (rowIndex ==4) break;
         }

         timer.stop();
         System.out.println(" loaded and filtered the keyword sums " + timer);
         timer.start();
         String[] item = columns.toArray(new String[columns.size()]);
         DefaultTableModel model = getTableModel(item);
         ArrayList<ArrayList<String>> cleanedRows = new ArrayList<ArrayList<String>>();
         for (int u = 0; u < matrix.length; u++) {
             ArrayList<String> row = new ArrayList<>();
             StringBuffer s = new StringBuffer();
             Object[] buf = new Object[matrix[u].length];
             for (int j = 0; j < matrix[u].length; j++) {
                 if (matrix[u][j] != null) {
                     s = s.append(matrix[u][j]);
                     buf[j] = matrix[u][j];
                     row.add(matrix[u][j]);
                 }
             }
             if (s.length() > 1) {
                 cleanedRows.add(row);
             }
         }
         timer.stop();
         System.out.println("Method took getTableModel: " + timer);
         timer.start();
         System.out.println("Method took getTableModel: " + timer);
         try {
             getStatement().execute(dropStatisticsTable("Statistica"));
         } catch (SQLException e) {
             e.printStackTrace();
         }
         try {
             System.out.println("dropStatisticsTable");

             getStatement().execute(createStatsTable(item));
         } catch (SQLException e) {
             e.printStackTrace();
         }

         for (ArrayList<String> s : cleanedRows) {
             Object dc[] = new Object[s.size()];
             dc[0] = s.get(0);
             for (int i = 1; i < s.size(); i++) {
                 double dd = 0.0;
                 if (i > 0) {
                     try {
                         dd = Double.parseDouble(s.get(i));
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                     dc[i] = dd;
                 } else {
                     dc[i] = s.get(i);
                 }
             }
             model.addRow(dc);
             try {
                 getStatement().execute(insertStatisticsRow(item,dc));
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
         System.out.println("Statistics refreshed and updated");
     }

    public static String dropStatisticsTable(String tableName) {
        String delete = "DROP TABLE IF EXISTS TravelSourceLLC."+tableName+";";
        return delete;
    }

    public static String insertStatisticsRow(String columns[],Object row[]) {
        StringBuffer columnHolder = new StringBuffer();

        columnHolder.append("INSERT INTO `TravelSourceLLC`.`Statistica` (" );
        int index = 0;
        for (String s : columns) {
            index++;
//            if (s!=null) {
                if (index == columns.length) columnHolder.append("`" + s + "`) VALUES ( ");
                else columnHolder.append("`" + s + "`, ");
//            }
        }
        index =0;
        for (Object m : row) {
            index++;
            if (index==row.length) columnHolder.append("'"+m+"');");
            else  columnHolder.append("'"+m+"', ");
        }
        System.out.println(columnHolder.toString());
        return columnHolder.toString();
    }

//    static class Test extends  TimerTask {
//        public Test() {
//
//        }
//        @Override
//        public void run() {
//
//        }

    // ==================================================== DO BINARY SEARCH =========================================
    public static String createStatsTable(String columns[]) {
        StringBuffer buffer = new StringBuffer();

        buffer.append("CREATE TABLE `TravelSourceLLC`.`Statistica` (`idStatistica` INT NOT NULL AUTO_INCREMENT,");
        for(int i=0; i<columns.length;i++) {
//            if (columns[i]!=null) {
                buffer.append("`" + columns[i] + "` TEXT NULL,");
//            }
        }
        buffer.append("PRIMARY KEY (`idStatistica`));");
        System.out.println(buffer.toString());
        return buffer.toString();
    }
    private int elementFound(String arr[], String myString) {
        int r = arr.length;
        for (int l = 0; l < arr.length; l++) {
            int m = l + (arr.length - l) / 2;
            int res = myString.compareTo(arr[m]);
            //check if in middle
            if (res == 0) return m;
            // if myString greater, ignore left half
            if (res > 0)
                l = m + 1;
                //if myString is smaller , ignore right h                                                                                                                                                                                                                                                                                                                                                                                                                                                               alf
            else r = m - 1;
        }
        return -1;
    }

    public static boolean equals(final String s1, final String s2) {
        return s1 != null && s2 != null && s1.hashCode() == s2.hashCode()
                && s1.equals(s2);
    }

    static int binarySearch(String[] arr, String x) {
        int l = 0, r = arr.length - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;

            int res = x.compareTo(arr[m]);

            // Check if x is present at mid
            if (res == 0)
                return m;

            // If x greater, ignore left half
            if (res > 0)
                l = m + 1;

                // If x is smaller, ignore right half
            else
                r = m - 1;
        }

        return -1;
    }

    //-------------------------------------------------------------------------------------------------------------------------------

    static ArrayList<String> productsList = new ArrayList<String>();
    static DefaultListModel<String> listModel = null;

    private static HashMap<String, Double> getTotalSum() {
        HashMap<String, Double> map = new HashMap<String, Double>();
        String query =
                "SELECT sum(frequency_1+frequency_2+frequency_3+frequency_4) as u, title_category FROM TravelSourceLLC.KeywordExpanded group by title_category";
        double d = 0.0;
        String s = "";
        try {
            ResultSet rs = getStatement().executeQuery(query);
            while (rs.next()) {
                d = rs.getDouble("u");
                s = rs.getString("title_category");
                map.put(s, map.getOrDefault(s, 0.0) + d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    private static DefaultTableModel getTableModel(String[] columns) {
        DefaultTableModel model = new DefaultTableModel(columns
                , 0) {
            Class[] types = {String.class, Integer.class};

            @Override
            public Class getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return String.class;
                    default:
                        return Integer.class;
                }
            }
        };
        return model;
    }

    private static void loadProductFromDB() {
        String sql = "SELECT title_category FROM TravelSourceLLC.KeywordExpanded group by title_category";
        ResultSet rs = null;
        try {
            rs = getStatement().executeQuery(sql);
            while (rs.next()) {
                productsList.add(rs.getString("title_category"));
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private static void loadColours() {
        String sql = "Select * from Color";
        ResultSet rsd = null;
        ResultSet rs = null;
        try {
            rsd = getStatement().executeQuery(sql);
            while (rsd.next()) {
                Colourful data = new Colourful();
                data.idKeywordTable = rsd.getInt("idColor");
                data.categories = rsd.getString("Category");
                data.color = rsd.getString("Color");
                data.hex = rsd.getString("HexColor");
                data.textHex = rsd.getString("TextColor");
                data.keyword = rsd.getString("keyword");
                colors.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> loadCategories() {
        String sql = "SELECT distinct Category FROM TravelSourceLLC.Color ";
        ResultSet rsd = null;
        ResultSet rs = null;
        int index = 0;
        ArrayList<String> buf = new ArrayList<>();
        buf.add("Product");
        try {
            rsd = getStatement().executeQuery(sql);
            while (rsd.next()) {
                index = index++;
                buf.add(rsd.getString("Category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buf;
    }

    private String getKeywordSumQuery(String sheet, String categoryCondition) {
        String query = "select distinct idKeywordExpanded, t2.Category ,keyword_1,keyword_2,keyword_3,keyword_4," +
                "frequency_1,frequency_2,frequency_3, frequency_4,idKeywordExpanded " +
                "from KeywordExpanded t1  JOIN (select distinct keyword as fieldx,Category from Color) " +
                " t2  ON (t1.keyword_1 like concat('%',fieldx,'%') OR t1.keyword_2 like concat('%',fieldx,'%') " +
                " OR t1.keyword_3 like concat('%',fieldx,'%')  OR t1.keyword_4 like concat('%',fieldx,'%'))" +
                "and title_category='" + sheet + "'";
        return query;
    }

    private HashMap<String, Double> getKeywordSumV3(String sheet, String categoryCondition) {
        String query = "select distinct idKeywordExpanded, t2.Category ,keyword_1,keyword_2,keyword_3,keyword_4," +
                "frequency_1,frequency_2,frequency_3, frequency_4,idKeywordExpanded " +
                "from KeywordExpanded t1  JOIN (select distinct keyword as fieldx,Category from Color) " +
                " t2  ON (t1.keyword_1 like concat('%',fieldx,'%') OR t1.keyword_2 like concat('%',fieldx,'%') " +
                " OR t1.keyword_3 like concat('%',fieldx,'%')  OR t1.keyword_4 like concat('%',fieldx,'%'))" +
                "and title_category='" + sheet + "'";

        HashMap<String, Double> map = new HashMap<String, Double>();
        ArrayList<String> keywordPreconditions = checkIfKeywordHasCondition();
        try {
            ResultSet rs = Explorer.con.createStatement().executeQuery(query);
            while (rs.next()) {
                double d = 0.0;
                String keyword_1 = rs.getString("keyword_1");
                String keyword_2 = rs.getString("keyword_2");
                String keyword_3 = rs.getString("keyword_3");
                String keyword_4 = rs.getString("keyword_4");
                String categoryofW = rs.getString("t2.Category");
                if (Explorer.containsKeyword(keywordPreconditions, keyword_1, categoryCondition)) {
                    try {
                        d = d + rs.getDouble("frequency_1");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (Explorer.containsKeyword(keywordPreconditions, keyword_2, categoryCondition)) {
                    try {
                        d = d + rs.getDouble("frequency_2");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (Explorer.containsKeyword(keywordPreconditions, keyword_3, categoryCondition)) {
                    try {
                        d = d + rs.getDouble("frequency_3");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (Explorer.containsKeyword(keywordPreconditions, keyword_4, categoryCondition)) {
                    try {
                        d = d + rs.getDouble("frequency_4");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                map.put(categoryofW, map.getOrDefault(categoryofW, 0.0) + d);
            }
        } catch (Exception ed) {
        }
        return map;
    }

    private static class KeyWordSum {
        String category = "";
        String title_category = "";
        double frequencySum = 0.0;

        public KeyWordSum(String category, String title_category, double frequencySum) {
            this.category = category;
            this.title_category = title_category;
            this.frequencySum = frequencySum;
        }
    }

    public static ArrayList<KeyWordSum> getKeywordSumV4(String categoryCondition) {
        String query = "select distinct idKeywordExpanded, title_category, t2.Category ,keyword_1,keyword_2,keyword_3,keyword_4," +
                "frequency_1,frequency_2,frequency_3, frequency_4,idKeywordExpanded " +
                "from KeywordExpanded t1  JOIN (select distinct keyword as fieldx,Category from Color) " +
                " t2  ON (t1.keyword_1 like concat('%',fieldx,'%') OR t1.keyword_2 like concat('%',fieldx,'%') " +
                " OR t1.keyword_3 like concat('%',fieldx,'%')  OR t1.keyword_4 like concat('%',fieldx,'%'))";

        ArrayList<KeyWordSum> map = new ArrayList<>();
        ArrayList<String> keywordPreconditions = checkIfKeywordHasCondition();
        try {
            ResultSet rs = Explorer.con.createStatement().executeQuery(query);
            while (rs.next()) {
                double d = 0.0;
                String keyword_1 = rs.getString("keyword_1");
                String keyword_2 = rs.getString("keyword_2");
                String keyword_3 = rs.getString("keyword_3");
                String keyword_4 = rs.getString("keyword_4");
                String categoryofW = rs.getString("t2.Category");
                String title_category = rs.getString("title_category");
                if (Explorer.containsKeyword(keywordPreconditions, keyword_1, categoryCondition)) {
                    try {
                        d = d + rs.getDouble("frequency_1");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (Explorer.containsKeyword(keywordPreconditions, keyword_2, categoryCondition)) {
                    try {
                        d = d + rs.getDouble("frequency_2");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (Explorer.containsKeyword(keywordPreconditions, keyword_3, categoryCondition)) {
                    try {
                        d = d + rs.getDouble("frequency_3");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (Explorer.containsKeyword(keywordPreconditions, keyword_4, categoryCondition)) {
                    try {
                        d = d + rs.getDouble("frequency_4");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                KeyWordSum k = new KeyWordSum(categoryofW, title_category, d);
                map.add(k);
            }
        } catch (Exception ed) {
        }
        System.out.println(query);
        return map;
    }


    public static Statement getStatement() {
        if (statement == null) {
            try {
                Explorer.loadDatabase();
                return Explorer.con.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return statement;
    }

    private static ArrayList<String> checkIfKeywordHasCondition() {
        String query = "select keyword from Color";
        ResultSet rs = null;
        ArrayList<String> results = new ArrayList<>();
        try {
            rs = getStatement().executeQuery(query);
            while (rs.next()) {
                String d = rs.getString("keyword");
                results.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */


    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */

}


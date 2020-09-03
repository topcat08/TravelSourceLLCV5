package screen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Stats {
    private JTable tableo;
    private JPanel panel1;
    private JScrollPane scrollPaner;
    Statement statement;
    List<String> productsList = new ArrayList<String>();
    ArrayList<Colourful> colors = new ArrayList<>();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Stats");
        frame.setContentPane(new Stats().panel1);
        frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);
    }

    public Stats() {
        $$$setupUI$$$();
        //    loadDBData();
//        scrollpane = new JScrollPane(table1, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableo.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        scrollpane.createVerticalScrollBar();
        scrollPaner.createHorizontalScrollBar();
        tableo.setAutoscrolls(true);
//
    }

    private void loadDBData() {
        statement = getStatement();
        loadColours();
        loadProductFromDB();
        DefaultListModel listModel = new DefaultListModel<>();
        String[][] matrix = new String[productsList.size()][colors.size()];
        ArrayList<String> columns = loadCategories();
        columns.add("Product");
//        ArrayList<String> alreadyContainsCondition= new ArrayList<>();
        for (int z = 0; z < productsList.size(); z++) {
//            HashMap<String, Double> eval = getKeywordSumV3(productsList.get(z),);
            matrix[z][0] = productsList.get(z) + "";
            double totalSum = getTotalSum(productsList.get(z));
            for (int i = 1; i < colors.size(); i++) {
                double d = 0.0;
                if (colors.get(i).categories != null) {
                    if (!listModel.contains(colors.get(i).categories)) {
                        //adding new columns & categ.
                        HashMap<String, Double> eval = getKeywordSumV3(productsList.get(z), colors.get(i).categoryCondition);

                        listModel.addElement(colors.get(i).categories);
                        //calculating new value of formatting
                        for (HashMap.Entry<String, Double> m : eval.entrySet()) {
                            if (m.getKey().equals(colors.get(i).categories)) {
                                d = d + m.getValue();
                            }
                        }
                        colors.get(i).setTotalSumPerColor(d);
                        //adding new category to matrix
                        try {
                            double result = d * 100 / totalSum;
                            matrix[z][i] = Explorer.df2.format(result);
                        } catch (Exception e) {
                        }
                    }
                }
            }
            listModel.clear();
        }

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
        for (ArrayList<String> s : cleanedRows) {
            Object dc[] = new Object[s.size()];
            for (int i = 0; i < s.size(); i++) {
                dc[i] = s.get(i);
            }
            model.addRow(dc);
        }
        tableo = new JTable(model);
        tableo.setModel(model);
    }

    private double getTotalSum(String category) {
        String query = "SELECT sum(frequency_1+frequency_2+frequency_3+frequency_4) as u FROM TravelSourceLLC.KeywordExpanded where title_category='" + category + "'";
        double d = 0.0;
        try {
            ResultSet rs = getStatement().executeQuery(query);
            while (rs.next()) {
                d = rs.getDouble("u");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return d;
    }

    private DefaultTableModel getTableModel(String[] columns) {
        DefaultTableModel model = new DefaultTableModel(columns
                , 0);
        return model;
    }

    private void loadProductFromDB() {
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

    private void loadColours() {
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

    private ArrayList<String> loadCategories() {
        String sql = "SELECT distinct Category FROM TravelSourceLLC.Color ";
        ResultSet rsd = null;
        ResultSet rs = null;
        int index = 0;
        ArrayList<String> buf = new ArrayList<>();
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

    private HashMap<String, Double> getKeywordSumV3(String sheet, String categoryCondition) {
        String query = "select distinct idKeywordExpanded, t2.Category ,keyword_1,keyword_2,keyword_3,keyword_4," +
                "frequency_1,frequency_2,frequency_3, frequency_4,idKeywordExpanded " +
                "from KeywordExpanded t1  JOIN (select distinct keyword as fieldx,Category from Color) " +
                " t2  ON (t1.keyword_1 like concat('%',fieldx,'%') OR t1.keyword_2 like concat('%',fieldx,'%') " +
                " OR t1.keyword_3 like concat('%',fieldx,'%')  OR t1.keyword_4 like concat('%',fieldx,'%'))" +
                "and title_category='" + sheet + "'";
//        System.out.println(query);
        HashMap<String, Double> map = new HashMap<String, Double>();
        ArrayList<String> keywordPreconditions = checkIfKeywordHasCondition();
        try {
            ResultSet rs = getStatement().executeQuery(query);
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

    public Statement getStatement() {
        if (statement == null) {
            try {
                return Explorer.con.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return statement;
    }

    private ArrayList<String> checkIfKeywordHasCondition() {
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

    private void createUIComponents() {
        loadDBData();
        tableo.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPaner = new JScrollPane();
        scrollPaner.setHorizontalScrollBarPolicy(32);
        scrollPaner.setVerticalScrollBarPolicy(22);
        panel1.add(scrollPaner, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tableo.setAutoResizeMode(0);
        scrollPaner.setViewportView(tableo);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */

}

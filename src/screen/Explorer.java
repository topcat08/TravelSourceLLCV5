package screen;

import java.text.DecimalFormat;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.text.JTextComponent;
import com.jcraft.jsch.JSchException;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import screen.*;
import screen.Colourful;

public class Explorer {
    private JPanel panel1;
    TableRowSorter<DefaultTableModel> sorter = null;
    String columnNames[] = null;
    public static boolean REMOTE_DBC = true;
    private static final int LIMIT = 100;
    static DefaultTableModel model = null;
    static double totalSumF1 = 0.0;
    private JTable table1;
    private JTextField keywordFilter;
    private JButton colorButton;
    private JScrollPane scrollPane1;
    private JButton add;
    private JButton deleteIdButton;
    private JComboBox comboBox1;
    private JList list1;
    private JButton statsButton;
    static Connection con = null;
    public static DecimalFormat df2 = new DecimalFormat("#.###############");
    private static DecimalFormat df1 = new DecimalFormat("#.##");
    static ArrayList<Colourful> colors = new ArrayList<>();
    static String strSshUser = "root"; // SSH loging username
    static String strSshPassword = "TravelSourceDatabase"; //it should be TravelSourceLLC// SSH login password
    static String strSshHost = "45.79.55.27"; // hostname or ip or SSH server
    static int nSshPort = 22; // remote SSH host port number
    static String strRemoteHost = "45.79.55.27"; // hostname or ip of your database server
    static int nRemotePort = 3306; // remote port number of your database
    static String strDbUser = "root"; // database loging username
    static String strDbPassword = "samsung23"; //"new-password"; // database login password
    static int nLocalPort = 3366; // local port number use to bind SSH tunnel
    Statement statement = null;
    DefaultListModel<String> listModel = null;
    private static String loadStatistic = "Select * from Statistica";
    private String deleteById = "DELETE FROM `TravelSourceLLC`.`KeywordExpanded` WHERE `idKeywordExpanded`='";
    String loadDataWithSchedueleRightNow = "select sum(frequency_1)+ sum(frequency_2)+sum(frequency_3)+sum(frequency_4) as value from TravelSourceLLC.KeywordExpanded";
    String loadDataWithSchedueleRightNowTwo = "SELECT * FROM KeywordExpanded limit " + LIMIT; //this is my demo test mode
    String loadColours = "Select * from Color";
    private String getTotalSum = "SELECT sum(frequency_1+frequency_2+frequency_3+frequency_4) as u FROM TravelSourceLLC.KeywordExpanded where title_category='";
    String checkIfKeywordHasCondition = "select keyword from TravelSourceLLC.Color";
    String getKeywordSumV3 = "select distinct idKeywordExpanded, t2.Category ,keyword_1,keyword_2,keyword_3,keyword_4," +
            "frequency_1,frequency_2,frequency_3, frequency_4,idKeywordExpanded " +
            "from KeywordExpanded t1  JOIN (select distinct keyword as fieldx,Category from Color) " +
            " t2  ON (t1.keyword_1 like concat('%',fieldx,'%') OR t1.keyword_2 like concat('%',fieldx,'%') " +
            " OR t1.keyword_3 like concat('%',fieldx,'%')  OR t1.keyword_4 like concat('%',fieldx,'%'))" +
            "and title_category='"; // + sheet + "'";
    String loadDatabase = "select sum(frequency_1)+ sum(frequency_2)+sum(frequency_3)+sum(frequency_4) as value from TravelSourceLLC.KeywordExpanded";
    String filtered = "";
    List<String> mySpreadSheets = new ArrayList<String>();
    String filterByCategory = "SELECT title_category FROM TravelSourceLLC.KeywordExpanded group by title_category";
    public String productName = "";

    public static void main(String[] args) {
        JFrame frame = new JFrame("Explorer");
        frame.setContentPane(new Explorer().panel1);
        frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);
    }

    public Explorer() {
        $$$setupUI$$$();
        scrollPane1.createVerticalScrollBar();
        hookUiWithData();
        Runtime.getRuntime().
                addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            con.close();
                        } catch (SQLException e) {
                        }
                    }
                });
        new Thread() {
            public void run() {
                loadProduct("");
            }
        }.start();
    }

    ArrayList<String> productNames = new ArrayList<>();

    public void loadProduct(String productN) {
        try {
            ResultSet rs = con.createStatement().executeQuery(loadStatistic);
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            DefaultListModel<String> fRes = new DefaultListModel<>();
            ArrayList<Double> freq = new ArrayList<>();
            //find out the values for a specific product
            productNames = new ArrayList<>();
            while (rs.next()) {
                for (int c = 1; c < columnCount; c++) {
                    if (rs.getString(2).equals(productN)) {
                        try {
                            if (c > 2) {
                                freq.add(rs.getDouble(c));
                                productNames.add(meta.getColumnLabel(c));
                                fRes.addElement(meta.getColumnLabel(c));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            freq.add(0.0);
                            productNames.add(meta.getColumnLabel(c));
                        }
                    }
                }
            }
            freq.add(0.0);
            productNames.add("New Category");
            fRes.addElement("New Category");

            list1.setModel(fRes);
            list1.setCellRenderer(new ColorRenderer(freq, productNames));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                    if (list1.getSelectedValue().toString().contains("New Category")) {
                        InsertCategoryDialog dialog = new InsertCategoryDialog(list1, productNames);
                        if (dialog != null) {
                            dialog.pack();
                            dialog.setResizable(true);
                            dialog.setVisible(true);
                        }
                    }
                }
            }
        });
    }

    private void hookUiWithData() {
        keywordFilter.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent arg0) {
            }

            @Override
            public void keyReleased(KeyEvent arg0) {
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
                if (keywordFilter.getText().toString().length() != 0)
                    sorter.setRowFilter(RowFilter.regexFilter(keywordFilter.getText()));
                else sorter.setRowFilter(null);
            }
        });
        colorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFrame frame = new JFrame("ColorViewer");
                //frame.setContentPane(new ColorViewer(table1).panel);
                frame.setContentPane(new ColorViewer(table1).panel);
                frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
                frame.setResizable(true);
                frame.pack();
                frame.setVisible(true);
            }
        });

        statsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Statstics");
                frame.setContentPane(new Statistica().panel1);
                frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
                frame.setResizable(true);
                frame.pack();
                frame.setVisible(true);
            }
        });

        deleteIdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    DefaultTableModel m = (DefaultTableModel) table1.getModel();
                    String query = deleteById + table1.getValueAt(table1.getSelectedRow(), 0) + "';";
                    try {
                        con.createStatement().execute(query);
                    } catch (SQLException e) {
                    }
                    m.removeRow(table1.getSelectedRow());
                    table1.revalidate();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                InsertKeywordDialog dialog = new InsertKeywordDialog(table1);
                dialog.pack();
                dialog.setResizable(true);
                dialog.setVisible(true);
            }
        });
    }

    public Statement getStatement() {
        if (statement == null) {
            try {
                loadDatabase();
                return con.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return statement;
    }

    private String getColorName(String keyword) {
        for (Colourful c : colors) {
            if (keyword.equalsIgnoreCase(c.keyword)) {
                return c.hex;
            }
        }
        return null;
    }

    public static String getHexColorName(String keyword) {
        for (Colourful c : colors) {
            if (keyword.equalsIgnoreCase(c.keyword)) {
                return c.hex;
            }
        }
        return null;
    }

    public static String getTextColorName(String keyword) {
        for (Colourful c : colors) {
            if (keyword.equalsIgnoreCase(c.keyword)) {
                return c.textHex;
            }
        }
        return null;
    }

    private void loadColours() {
        ResultSet rsd = null;
        ResultSet rs = null;
        try {
            rsd = con.createStatement().executeQuery(loadColours);
            while (rsd.next()) {
                Colourful data = new Colourful();
                data.idKeywordTable = rsd.getInt("idColor");
                data.categories = rsd.getString("Category");
                data.color = rsd.getString("HexColor");
                data.hex = rsd.getString("HexColor");
                data.textHex = rsd.getString("TextColor");
                data.keyword = rsd.getString("keyword");
                data.categoryCondition = rsd.getString("CategoryCondition");
                colors.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createUIComponents() {
        setupTableColours();
        loadDatabase();
        table1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        model = new DefaultTableModel(new String[]{"#",
                "Keyword 1", "#F1", "Keyword 2", "#F2",
                "Keyword 3", "#F3", "Keyword 4", "#F4",
                "Product"
                , "Avg. rate"
        }, 0) {
            @Override
            public Class getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Integer.class;
                    case 2:
                        return Integer.class;
                    case 4:
                        return Integer.class;
                    case 6:
                        return Integer.class;
                    case 8:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };
        Statement st = null;
        loadColours();
        try {
            st = con.createStatement();
            ResultSet rs = st.executeQuery(loadDataWithSchedueleRightNow);
            while (rs.next()) {
                totalSumF1 = rs.getDouble("value");
            }
            rs = st.executeQuery(loadDataWithSchedueleRightNowTwo);
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = 1;
            int columnCount = rsmd.getColumnCount();
            columnNames = new String[columnCount];
            while (count < columnCount) {
                columnNames[count - 1] = rsmd.getColumnLabel(count);
                count++;
            }
            rs.beforeFirst();
            while (rs.next()) {
                double e = 0.0;
                double g = 0.0;
                double i = 0.0;
                double k = 0.0;
                int index = rs.getInt("idKeywordExpanded");
                String d = rs.getString("keyword_1");
                String f = rs.getString("keyword_2");
                String h = rs.getString("keyword_3");
                String j = rs.getString("keyword_4");
                String l = rs.getString("title_category");
                try {
                    e = rs.getDouble("frequency_1");
                } catch (NumberFormatException aa) {
                } catch (Exception de) {
                } finally {
                    try {
                        g = rs.getDouble("frequency_2");
                    } catch (NumberFormatException bb) {
                    } catch (Exception deade) {
                    } finally {
                        try {
                            i = rs.getDouble("frequency_3");
                        } catch (NumberFormatException cc) {
                        } catch (Exception mded) {
                        } finally {
                            try {
                                k = rs.getDouble("frequency_4");
                            } catch (NumberFormatException ee) {
                            } catch (Exception dd) {
                                k = 0.0;
                            }
                        }
                    }
                }
                model.addRow(new Object[]{index, d, (int) e, f, (int) g, h, (int) i, j, (int) k, l, " "});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table1.setModel(model);
        table1.setAutoCreateRowSorter(true);
        sorter = new TableRowSorter<DefaultTableModel>(model);
        table1.setRowSorter(sorter);
        table1.getColumnModel().getColumn(0).setPreferredWidth(50);
        table1.getColumnModel().getColumn(1).setPreferredWidth(150);
        table1.getColumnModel().getColumn(2).setPreferredWidth(40);
        table1.getColumnModel().getColumn(3).setPreferredWidth(150);
        table1.getColumnModel().getColumn(4).setPreferredWidth(40);
        table1.getColumnModel().getColumn(5).setPreferredWidth(170);
        table1.getColumnModel().getColumn(6).setPreferredWidth(40);
        table1.getColumnModel().getColumn(7).setPreferredWidth(150);
        table1.getColumnModel().getColumn(8).setPreferredWidth(40);
        table1.getColumnModel().getColumn(9).setPreferredWidth(150);
        table1.getColumnModel().getColumn(10).setPreferredWidth(80);
        table1.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table1.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            row = table1.convertRowIndexToModel(row);
        });

        editTableUpdate();
        filterByCategory();
        //maa
//        scrollPane1.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
//            @Override
//            public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
//                System.out.println("t" + adjustmentEvent.getValue());
//                model = new DefaultTableModel(new String[]{"#",
//                        "Keyword 1", "#F1", "Keyword 2", "#F2",
//                        "Keyword 3", "#F3", "Keyword 4", "#F4",
//                        "Product"
//                        , "Avg. rate"
//                }, 0) {
//                    @Override
//                    public Class getColumnClass(int columnIndex) {
//                        switch (columnIndex) {
//                            case 0:
//                                return Integer.class;
//                            case 2:
//                                return Integer.class;
//                            case 4:
//                                return Integer.class;
//                            case 6:
//                                return Integer.class;
//                            case 8:
//                                return Integer.class;
//                            default:
//                                return String.class;
//                        }
//                    }
//                };
//                Statement st = null;
////                loadColours();
//                try {
//                    st = con.createStatement();
//                    ResultSet rs = st.executeQuery(loadDataWithSchedueleRightNow);
////                    while (rs.next()) {
////                        totalSumF1 = rs.getDouble("value");
////                    }
//
//                    rs = st.executeQuery(loadDataWithSchedueleRightNowTwo );
//                    ResultSetMetaData rsmd = rs.getMetaData();
//                    int count = 1;
//                    int columnCount = rsmd.getColumnCount();
//                    columnNames = new String[columnCount];
//                    while (count < columnCount) {
//                        columnNames[count - 1] = rsmd.getColumnLabel(count);
//                        count++;
//                    }
//                    rs.beforeFirst();
//                    while (rs.next()) {
//                        double e = 0.0;
//                        double g = 0.0;
//                        double i = 0.0;
//                        double k = 0.0;
//                        int index = rs.getInt("idKeywordExpanded");
//                        String d = rs.getString("keyword_1");
//                        String f = rs.getString("keyword_2");
//                        String h = rs.getString("keyword_3");
//                        String j = rs.getString("keyword_4");
//                        String l = rs.getString("title_category");
//                        try {
//                            e = rs.getDouble("frequency_1");
//                        } catch (NumberFormatException aa) {
//                        } finally {
//                            try {
//                                g = rs.getDouble("frequency_2");
//                            } catch (NumberFormatException bb) {
//                            } finally {
//                                try {
//                                    i = rs.getDouble("frequency_3");
//                                } catch (NumberFormatException cc) {
//                                } finally {
//                                    try {
//                                        k = rs.getDouble("frequency_4");
//                                    } catch (NumberFormatException ee) {
//                                    }
//                                }
//                            }
//                        }
//                        model.addRow(new Object[]{index, d, (int) e, f, (int) g, h, (int) i, j, (int) k, l, " "});
//                    }
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//                table1.setModel(model);
//
//            }
//        });
    }

    private void setupTableColours() {
        model = new DefaultTableModel(new String[]{"#",
                "Keyword 1", "#F1", "Keyword 2", "#F2",
                "Keyword 3", "#F3", "Keyword 4", "#F4",
                "Title Category"
                , "Average rate", "F1", "F2", "F3", "F4"
        }, 0) {
            Class[] types = {
                    Integer.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };

        table1 = new JTable(model);
        table1.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                //  comp.setForeground(Color.BLUE);
                comp.setForeground(Color.BLACK);
                comp.setBackground(Color.WHITE);
                try {
                    String[] differences = ((String) value).split(" ");
                    ArrayList<String> t = new ArrayList<>();
                    for (Colourful c : colors) {
                        t.add(c.keyword);
                        if (containss((String) value, c.keyword, c.categoryCondition)) {
                            Color cx = null;
                            switch (column) {
                                case 1:
                                    try {
                                        cx = Color.decode("#" + getTextColorName((String) value)); //getTextColorName((String) value));
                                        if (cx != null) comp.setForeground(cx);
                                        Color dd2 = Color.decode("#" + getHexColorName((String) value));
                                        if (dd2 != null) comp.setBackground(dd2);
                                    } catch (Exception e) {
                                    }
                                    return comp;
                                case 3:
                                    try {
                                        cx = Color.decode("#" + getTextColorName((String) value)); //getTextColorName((String) value));
                                        if (cx != null) comp.setForeground(cx);
                                        Color dd2 = Color.decode("#" + getHexColorName((String) value));
                                        if (dd2 != null) comp.setBackground(dd2);
                                    } catch (Exception e) {
                                    }
                                    return comp;
                                case 5:
                                    try {
                                        cx = Color.decode("#" + getTextColorName((String) value)); //getTextColorName((String) value));
                                        if (cx != null) comp.setForeground(cx);
                                        Color dd2 = Color.decode("#" + getHexColorName((String) value));
                                        if (dd2 != null) comp.setBackground(dd2);
                                    } catch (Exception e) {
                                    }
                                    return comp;
                                case 7:
                                    try {
                                        cx = Color.decode("#" + getTextColorName((String) value)); //getTextColorName((String) value));
                                        if (cx != null) comp.setForeground(cx);
                                        Color dd2 = Color.decode("#" + getHexColorName((String) value));
                                        if (dd2 != null) comp.setBackground(dd2);
                                    } catch (Exception e) {
                                    }
                                    return comp;
                                default:
                                    comp.setForeground(Color.BLACK);
                                    comp.setBackground(Color.WHITE);
                                    return comp;
                            }
                        }
                    }
                    return comp;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return comp;
            }
        });
        table1.setBackground(UIManager.getColor("Button.highlight"));
    }

    private double getTotalSum(String category) {
        String query = getTotalSum + category + "'";
        double d = 0.0;
        try {
            ResultSet rs = con.createStatement().executeQuery(query);
            while (rs.next()) {
                d = rs.getDouble("u");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return d;
    }

    public static boolean containss(String haystack, String needle, String categoryType) {
        haystack = haystack == null ? "" : haystack;
        needle = needle == null ? "" : needle;
        int type = 0;
        if (categoryType == null || categoryType.isEmpty() || categoryType.equals("null")) {
            return true;
        }
        if (categoryType.compareTo(InsertCategoryDialog.matchType[0]) == 0) {
            return screen.TextMatcher.wordSituationMatchingMechanism(type, haystack, needle);
        }
        for (int i = 1; i < InsertCategoryDialog.matchType.length; i++) {
            boolean d = screen.TextMatcher.wordSituationMatchingMechanism(i, haystack, needle);
            if (d == true) return d;
        }
        return screen.TextMatcher.wordSituationMatchingMechanism(type, haystack, needle);
    }

    public static boolean containsKeyword(ArrayList<String> keywordPreconditions, String keyword, String type) {

        for (int x = 0; x < keywordPreconditions.size(); x++) {
            String dd = keywordPreconditions.get(x);
            if (containss(keyword, keywordPreconditions.get(x), type)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<String> checkIfKeywordHasCondition() {
        ResultSet rs = null;
        ArrayList<String> results = new ArrayList<>();
        try {
            rs = con.createStatement().executeQuery(checkIfKeywordHasCondition);
            while (rs.next()) {
                String d = rs.getString("keyword");
                results.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    private HashMap<String, Double> getKeywordSumV3(String sheet, String categoryCondition) {
        HashMap<String, Double> map = new HashMap<String, Double>();
        ArrayList<String> keywordPreconditions = checkIfKeywordHasCondition();
        try {
            ResultSet rs = con.createStatement().executeQuery(getKeywordSumV3 + sheet + "'");
            while (rs.next()) {
                double d = 0.0;
                String keyword_1 = rs.getString("keyword_1");
                String keyword_2 = rs.getString("keyword_2");
                String keyword_3 = rs.getString("keyword_3");
                String keyword_4 = rs.getString("keyword_4");
                String categoryofW = rs.getString("t2.Category");

                if (containsKeyword(keywordPreconditions, keyword_1, categoryCondition)) {
                    try {
                        d = d + rs.getDouble("frequency_1");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (containsKeyword(keywordPreconditions, keyword_2, categoryCondition)) {
                    try {
                        d = d + rs.getDouble("frequency_2");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (containsKeyword(keywordPreconditions, keyword_3, categoryCondition)) {
                    try {
                        d = d + rs.getDouble("frequency_3");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (containsKeyword(keywordPreconditions, keyword_4, categoryCondition)) {
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

    public static void loadDatabase() {
        if (REMOTE_DBC) {
            try {
                screen.DBUtilsConection.doSshTunnel(strSshUser, strSshPassword, strSshHost, nSshPort,
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
                String strDbPassword = "new-password"; // database login
                Class.forName("com.mysql.cj.jdbc.Driver");
                nLocalPort = 3306;
                con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:" + nLocalPort + "/TravelSourceLLC?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", strDbUser,
                        strDbPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void editTableUpdate() {
        table1.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                try {
                    TableModel models = (TableModel) e.getSource();
                    Object d = models.getValueAt(e.getFirstRow(), e.getColumn());
                    if (e.getColumn() < 8) {
                        String query = "UPDATE `TravelSourceLLC`.`KeywordExpanded` SET `" + columnNames[e.getColumn()] + "`='" + table1.getValueAt(table1.
                                getSelectedRow(), table1.getSelectedColumn())
                                + "' WHERE `idKeywordExpanded`='" + table1.getValueAt(table1.getSelectedRow(), 0) + "'";
                        try {
                            con.prepareStatement(query).execute();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (e.getType() == TableModelEvent.UPDATE) {
                        if (e.getColumn() == 2 || e.getColumn() == 4 || e.getColumn() == 6 || e.getColumn() == 8) {
                            ResultSet rs = null;
                            try {
                                rs = con.createStatement().executeQuery(loadDatabase);
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                            try {
                                while (rs.next()) {
                                    totalSumF1 = rs.getDouble("value");
                                }
                            } catch (SQLException edd) {
                                edd.printStackTrace();
                            }
                            double m = Double.parseDouble(d.toString());
                            double value = m * 100 / totalSumF1;
                            switch (e.getColumn()) {
                                case 2:
                                    table1.setValueAt(value, e.getFirstRow(), 11);
                                    break;
                                case 4:
                                    table1.setValueAt(value, e.getFirstRow(), 12);
                                    break;
                                case 6:
                                    table1.setValueAt(value, e.getFirstRow(), 13);
                                    break;
                                case 8:
                                    table1.setValueAt(value, e.getFirstRow(), 14);
                                    break;
                            }
                        }
                    }
                } catch (Exception ea) {
                }
            }
        });
    }

    private void filterByCategory() {
        ResultSet rs = null;
        try {
            rs = con.createStatement().executeQuery(filterByCategory);
            while (rs.next()) {
                mySpreadSheets.add(rs.getString("title_category"));
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        screen.StringSearchable searchable = new screen.StringSearchable(mySpreadSheets);
        comboBox1 = new screen.AutocompleteJComboBox(searchable);
        comboBox1.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    if (!keywordFilter.getText().isEmpty()) keywordFilter.setText("");
                    String s = (((JTextComponent) ((JComboBox) ((Component) e.getSource()).getParent()).getEditor().getEditorComponent()).getText());
                    if (!s.isEmpty()) {
                        sorter.setRowFilter(RowFilter.regexFilter(s, 9));
                        productName = s;
                        new Thread() {
                            public void run() {
                                loadProduct(productName);
                                productName = "";
                            }
                        }.start();
                    }
                }
            }
        });
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
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setBackground(new Color(-2848));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 6, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        colorButton = new JButton();
        colorButton.setText("Colors");
        panel2.add(colorButton, new com.intellij.uiDesigner.core.GridConstraints(1, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        keywordFilter = new JTextField();
        keywordFilter.setToolTipText("KKKKK");
        panel2.add(keywordFilter, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 1, false));
        deleteIdButton = new JButton();
        deleteIdButton.setText("Delete");
        panel2.add(deleteIdButton, new com.intellij.uiDesigner.core.GridConstraints(1, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        add = new JButton();
        add.setText("Add");
        panel2.add(add, new com.intellij.uiDesigner.core.GridConstraints(1, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBox1.setAutoscrolls(true);
        comboBox1.setEditable(true);
        comboBox1.setFocusCycleRoot(true);
        comboBox1.setMaximumRowCount(30);
        panel2.add(comboBox1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        statsButton = new JButton();
        statsButton.setText("Stats");
        panel2.add(statsButton, new com.intellij.uiDesigner.core.GridConstraints(1, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(32);
        scrollPane1.setVerticalScrollBarPolicy(22);
        panel1.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 3, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table1.setAutoResizeMode(0);
        scrollPane1.setViewportView(table1);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel1.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(180, -1), new Dimension(180, -1), new Dimension(200, -1), 0, false));
        list1 = new JList();
        list1.setBackground(new Color(-8591));
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        list1.setModel(defaultListModel1);
        scrollPane2.setViewportView(list1);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    public void setModelRowTable(Object[] de) {
        model.addRow(de);
        table1.revalidate();
    }

    public static class ColorRenderer extends DefaultListCellRenderer {
        ArrayList<Double> values = new ArrayList<>();
        ArrayList<String> products = new ArrayList<>();
        List<Double> topResults = new ArrayList<>();
        List<Double> firstNelementsList = null;

        public ColorRenderer(ArrayList<Double> values, ArrayList<String> products) {
            this.values = values;
            this.products = products;
            setOpaque(true);
            for (Double s : values) {
                try {
                    topResults.add(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Collections.sort(topResults);
            try {
                // firstNelementsList = new ArrayList<Double>(topResults.subList(topResults.size() - 3, topResults.size()));
                firstNelementsList = new ArrayList<Double>(topResults);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            this.setBackground(Color.WHITE);
            this.setForeground(Color.BLACK);
            for (Colourful cm : colors) {
                try {
                    if (cm.categories.toLowerCase().equals(products.get(index).toLowerCase())) {
                        try {
                            this.setBackground(Color.decode("#" + cm.hex));
                            this.setForeground(Color.decode("#" + cm.textHex));
                        } catch (Exception e) {
                        }
                    }
                    if (value.toString().contains("New Category")) {
                        this.setBackground(screen.ColorUtils.getColorFromName("Green"));
                        this.setForeground(Color.WHITE);
                    }
                } catch (Exception e) {
                    //   e.printStackTrace();
                }
            }
            try {
                this.setText(products.get(index));
                if (firstNelementsList.contains(values.get(index)) && !Double.isNaN(values.get(index))) {
                    this.setText(df1.format(values.get(index)) + "% <<< " + products.get(index));
                } else {
                    if (!Double.isNaN(values.get(index))) {
                        this.setText(df1.format(values.get(index)) + "% " + products.get(index));
                    }
                }
            } catch (Exception e) {
            }
            return this;
        }
    }
}


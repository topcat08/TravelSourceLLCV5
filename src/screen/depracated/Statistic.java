package screen;

import com.jcraft.jsch.JSchException;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Statistic {
    private JPanel panel1;
    TableRowSorter<DefaultTableModel> sorter = null;
    String columnNames[] = null;
    public boolean REMOTE_DBC = true;
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
    private JTextField sheetfilter;
    static Connection con = null;
    public static DecimalFormat df2 = new DecimalFormat("#.###############");
    private static DecimalFormat df3 = new DecimalFormat("#");

    private static DecimalFormat df1 = new DecimalFormat("#.##");
    ArrayList<Colourful> colors = new ArrayList<>();
    String strSshUser = "root"; // SSH loging username
    String strSshPassword = "TravelSourceLLC"; // SSH login password
    String strSshHost = "45.56.72.19"; // hostname or ip or SSH server
    int nSshPort = 22; // remote SSH host port number
    String strRemoteHost = "localhost"; // hostname or ip of your database server
    int nRemotePort = 3306; // remote port number of your database
    String strDbUser = "root"; // database loging username
    String strDbPassword = "samsung23"; // database login password
    int nLocalPort = 3366; // local port number use to bind SSH tunnel
    Statement statement = null;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Explorer");
        frame.setContentPane(new Statistic().panel1);
        frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);
    }

    public Statistic() {
        $$$setupUI$$$();
        scrollPane1.createVerticalScrollBar();
        scrollPane1.createHorizontalScrollBar();
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
                loadRightListCategories();
            }
        }.start();
    }

    DefaultListModel<String> listModel = null;

//    private void loadRightListCategories() {
//        listModel = new DefaultListModel<>();
//        //get categories
//        //get keyword from categories
//        //set frequencies
//        //load frequency per categories
//
//        //get total
//        //get value
//        // do computation
//        // add to list filtered by category
//        ArrayList<Colourful> categoriesColors = new ArrayList<Colourful>();
//
//        for (int i = 0; i < colors.size(); i++) {
//            if (colors.get(i).categories != null) {
//                if (!listModel.contains(colors.get(i).categories)) {
//                    listModel.addElement(colors.get(i).categories);
//                               colors.get(i).setTotalSumPerColor(getKeywordSum(filtered, colors.get(i).categories));
//                    categoriesColors.add(colors.get(i));
//                }
//            }
//        }
//
//        list1.setModel(listModel);
//        list1.setCellRenderer(new ColorRenderer(categoriesColors));
//    }

    private void loadRightListCategories() {
        listModel = new DefaultListModel<>();
        ArrayList<screen.Colourful> categoriesColors = new ArrayList<>();
        for (int i = 0; i < colors.size(); i++) {
            if (colors.get(i).categories != null) {
                if (!listModel.contains(colors.get(i).categories)) {
                    listModel.addElement(colors.get(i).categories);
                    colors.get(i).setTotalSumPerColor(getKeywordSum(filtered, colors.get(i).categories));
                    categoriesColors.add(colors.get(i));
                }
            }
        }
        list1.setModel(listModel);
        list1.setCellRenderer(new ColorRenderer(categoriesColors));
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
//        colorButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent arg0) {
//                JFrame frame = new JFrame("ColorViewer");
//                frame.setContentPane(new ColorViewer(table1).$$$getRootComponent$$$());
//                frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
//                frame.setResizable(false);
//                frame.pack();
//                frame.setVisible(true);
//            }
//        });
//
//        statsButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JFrame frame = new JFrame("Statstics");
//                frame.setContentPane(new Stats().$$$getRootComponent$$$());
//                frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
//                frame.setResizable(false);
//                frame.pack();
//                frame.setVisible(true);
//            }
//        });

        deleteIdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    DefaultTableModel m = (DefaultTableModel) table1.getModel();
                    String query = "DELETE FROM `TravelSourceLLC`.`KeywordExpanded` WHERE `idKeywordExpanded`='" + table1.getValueAt(table1.getSelectedRow(), 0) + "';";
                    try {
                        getStatement().execute(query);
                    } catch (SQLException e) {
                    }
                    m.removeRow(table1.getSelectedRow());
                    table1.revalidate();
                } catch (Exception e) {
                }
            }
        });
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                InsertKeywordDialog dialog = new InsertKeywordDialog(table1);
                dialog.pack();
                dialog.setResizable(false);
                dialog.setVisible(true);
            }
        });
    }

    public Statement getStatement() {
        if (statement == null) {
            try {
                return con.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return statement;
    }


    private String getColorName(String keyword) {
        for (screen.Colourful c : colors) {
            if (keyword.equalsIgnoreCase(c.keyword) || keyword.contains(c.keyword)) {
                return c.color;
            }
        }
        return null;
    }

    private void loadDataWithSchedueleRightNow(boolean remoteDBC) {
        model = new DefaultTableModel(new String[]{"#",
                "Keyword 1", "#F1", "Keyword 2", "#F2",
                "Keyword 3", "#F3", "Keyword 4", "#F4",
                "Title Category"
                , "Average rate", "F1", "F2", "F3", "F4"
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
                    case 11:
                        return Integer.class;
                    case 12:
                        return Integer.class;
                    case 13:
                        return Integer.class;
                    case 14:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };
        Statement st = null;
        loadColours();
        try {
            st = getStatement();
            String sql = "select sum(frequency_1)+ sum(frequency_2)+sum(frequency_3)+sum(frequency_4) as value from TravelSourceLLC.KeywordExpanded";
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                totalSumF1 = rs.getDouble("value");
            }
            sql = "SELECT * FROM KeywordExpanded";
            rs = st.executeQuery(sql);
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
                } finally {
                    try {
                        g = rs.getDouble("frequency_2");
                    } catch (NumberFormatException bb) {
                    } finally {
                        try {
                            i = rs.getDouble("frequency_3");
                        } catch (NumberFormatException cc) {
                        } finally {
                            try {
                                k = rs.getDouble("frequency_4");
                            } catch (NumberFormatException ee) {
                            }
                        }
                    }
                }
                String eet = "";
                String ggt = "";
                String iit = "";
                String kky = "";
                if (e == 0) {
                    eet = "";
                } else {
                    double ds = (double) e * 100.0 / totalSumF1;
                    try {
                        eet = df2.format(ds);
                    } catch (Exception oodd) {
                        oodd.printStackTrace();
                    }
                }
                double gfd = (double) g * 100.0 / totalSumF1;
                try {
                    ggt = df2.format(gfd);
                } catch (Exception dd) {
                }
                double llk = (double) i * 100.0 / totalSumF1;
                try {
                    iit = df2.format(llk);
                } catch (Exception dd) {
                }
                Double llde = (double) k * 100.0 / totalSumF1;
                try {
                    kky = df2.format(llde);
                } catch (Exception dd) {
                    dd.printStackTrace();
                }

                model.addRow(new Object[]{index, d, (int) e, f, (int) g, h, (int) i, j, (int) k, l, " ", (eet), (ggt), (iit), (kky)});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        table1.setModel(model);
        table1.setAutoCreateRowSorter(true);
        sorter = new TableRowSorter<DefaultTableModel>(
                model);
        table1.setRowSorter(sorter);
        table1.getColumnModel().getColumn(0).setPreferredWidth(50);
        table1.getColumnModel().getColumn(1).setPreferredWidth(130);
        table1.getColumnModel().getColumn(2).setPreferredWidth(40);
        table1.getColumnModel().getColumn(3).setPreferredWidth(130);
        table1.getColumnModel().getColumn(4).setPreferredWidth(40);
        table1.getColumnModel().getColumn(5).setPreferredWidth(130);
        table1.getColumnModel().getColumn(6).setPreferredWidth(40);
        table1.getColumnModel().getColumn(7).setPreferredWidth(130);
        table1.getColumnModel().getColumn(8).setPreferredWidth(40);
        table1.getColumnModel().getColumn(9).setPreferredWidth(145);
        table1.getColumnModel().getColumn(10).setPreferredWidth(50);
        table1.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table1.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            row = table1.convertRowIndexToModel(row);
        });
    }

    private void loadColours() {
        String sql = "Select * from Color";
        ResultSet rsd = null;
        ResultSet rs = null;
        try {
            rsd = getStatement().executeQuery(sql);
            while (rsd.next()) {
                screen.Colourful data = new screen.Colourful();
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

    private void createUIComponents() {
        setupTableColours();
        loadDatabase();
        table1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        loadDataWithSchedueleRightNow(true);
        editTableUpdate();
        filterByCategory();
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
        table1 = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer,
                                             int rowIndex, int columnIndex) {
                JComponent component = (JComponent) super.prepareRenderer(
                        renderer, rowIndex, columnIndex);

                screen.Keywords k = new screen.Keywords();
                switch (columnIndex) {
                    case 1:
                        k.setKeyword_1(getValueAt(rowIndex, columnIndex).toString());
                        break;
                    case 2:
                        try {
                            k.setFrequency_1((double) getValueAt(rowIndex, columnIndex));
                        } catch (Exception p) {
                        }
                        break;
                    case 3:
                        k.setKeyword_2(getValueAt(rowIndex, columnIndex).toString());
                        break;
                    case 4:
                        try {
                            k.setFreqeuncy_2((double) getValueAt(rowIndex, columnIndex));
                        } catch (Exception p) {
                        }
                        break;
                    case 5:
                        try {
                            k.setKeyword_3(getValueAt(rowIndex, columnIndex).toString());
                        } catch (Exception p) {
                        }
                        break;
                    case 6:
                        try {
                            k.setFrequency_3((double) getValueAt(rowIndex, columnIndex));
                        } catch (Exception p) {
                        }
                        break;
                    case 7:
                        try {
                            k.setKeyword_4(getValueAt(rowIndex, columnIndex).toString());
                        } catch (Exception p) {
                        }
                        break;
                    case 8:
                        try {
                            k.setFrequency_4((double) getValueAt(rowIndex, columnIndex));
                        } catch (Exception p) {
                        }
                        break;
                }
                try {
                    String value = getValueAt(rowIndex, columnIndex).toString();
                    String[] differences = value.split(" ");
                    ArrayList<String> t = new ArrayList<>();
                    for (Colourful c : colors) {
                        t.add(c.keyword);
                        if (containss(value, c.keyword)) {
                            String color = getColorName(value);
                            Color dd = null;
                            switch (columnIndex) {
                                case 1:
                                    dd = ColorUtils.getColorFromName(color);
                                    component.setBackground(dd);
                                    return component;
                                case 3:
                                    dd = ColorUtils.getColorFromName(color);
                                    component.setBackground(dd);
                                    return component;
                                case 5:
                                    dd = ColorUtils.getColorFromName(color);
                                    component.setBackground(dd);
                                    return component;
                                case 7:
                                    dd = ColorUtils.getColorFromName(color);
                                    component.setBackground(dd);
                                    return component;
                                case 9:
                                    dd = ColorUtils.getColorFromName(color);
                                    component.setBackground(dd);
                                    return component;
                                default:
                                    component.setBackground(Color.WHITE);
                                    return component;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                component.setBackground(Color.WHITE);
                return component;
            }
        };
        table1.setBackground(UIManager.getColor("Button.highlight"));
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

    public static boolean containss(String haystack, String needle) {
        haystack = haystack == null ? "" : haystack;
        needle = needle == null ? "" : needle;
        return haystack.toLowerCase().contains(needle.toLowerCase());
    }

    public static boolean containsKeyword(ArrayList<String> keywordPreconditions, String keyword) {
        for (int x = 0; x < keywordPreconditions.size(); x++) {
            String dd = keywordPreconditions.get(x);
            if (containss(keyword, keywordPreconditions.get(x))) {
                return true;
            }
        }
        return false;
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

    private double getKeywordSum(String sheet, String category) {
        String query = "select keyword_1,keyword_2,keyword_3,keyword_4,frequency_1,frequency_2,frequency_3, " +
                "frequency_4,idKeywordExpanded from KeywordExpanded t1 " +
                " JOIN (select distinct keyword as fieldx,Category from Color) " +
                " t2  ON (t1.keyword_1 like concat('%',fieldx,'%')" +
                " OR t1.keyword_2 like concat('%',fieldx,'%') " +
                " OR t1.keyword_3 like concat('%',fieldx,'%') " +
                " OR t1.keyword_4 like concat('%',fieldx,'%'))" +
                " and t1.title_category = '" + sheet + "' " +
                " where t2.Category='" + category + "' group by t1.idKeywordExpanded";
//        System.out.println(query);
        double d = 0.0;
        ArrayList<String> keywordPreconditions = checkIfKeywordHasCondition();

        try {
            ResultSet rs = con.createStatement().executeQuery(query);
            while (rs.next()) {
                String keyword_1 = rs.getString("keyword_1");
                String keyword_2 = rs.getString("keyword_2");
                String keyword_3 = rs.getString("keyword_3");
                String keyword_4 = rs.getString("keyword_4");
                String value = category.toLowerCase();
                if (containsKeyword(keywordPreconditions, keyword_1)) {
                    try {
                        d = d + rs.getDouble("frequency_1");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (containsKeyword(keywordPreconditions, keyword_2)) {
                    try {
                        d = d + rs.getDouble("frequency_2");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (containsKeyword(keywordPreconditions, keyword_3)) {
                    try {
                        d = d + rs.getDouble("frequency_3");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (containsKeyword(keywordPreconditions, keyword_4)) {
                    try {
                        d = d + rs.getDouble("frequency_4");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception ed) {
        }
        return d;
    }

    private HashMap<String, Double> getKeywordSumV3(String sheet) {
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
            ResultSet rs = con.createStatement().executeQuery(query);
            while (rs.next()) {
                double d = 0.0;
                String keyword_1 = rs.getString("keyword_1");
                String keyword_2 = rs.getString("keyword_2");
                String keyword_3 = rs.getString("keyword_3");
                String keyword_4 = rs.getString("keyword_4");
                String categoryofW = rs.getString("t2.Category");

                if (containsKeyword(keywordPreconditions, keyword_1)) {
                    try {
                        d = d + rs.getDouble("frequency_1");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (containsKeyword(keywordPreconditions, keyword_2)) {
                    try {
                        d = d + rs.getDouble("frequency_2");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (containsKeyword(keywordPreconditions, keyword_3)) {
                    try {
                        d = d + rs.getDouble("frequency_3");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (containsKeyword(keywordPreconditions, keyword_4)) {
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
        if (Explorer.REMOTE_DBC) {
            try {
                screen.DBUtilsConection.doSshTunnel(Explorer.strSshUser, Explorer.strSshPassword, Explorer.strSshHost, Explorer.nSshPort,
                        Explorer.strRemoteHost, Explorer.nLocalPort, Explorer.nRemotePort);
            } catch (JSchException e1) {
                e1.printStackTrace();
            }
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
            try {
                Explorer.con = DriverManager
                        .getConnection("jdbc:mysql://localhost:" + Explorer.nLocalPort
                                + "/TravelSourceLLC", Explorer.strDbUser, Explorer.strDbPassword);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } else {
            try { // local port number use to bind SSH tunnel
                String strDbUser = "root"; // database loging username
                String strDbPassword = "new-password"; // database login
                Class.forName("com.mysql.cj.jdbc.Driver");
                Explorer.nLocalPort = 3306;
                Explorer.con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:" + Explorer.nLocalPort + "/TravelSourceLLC?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", strDbUser,
                        strDbPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//
//    private void loadDatabase() {
//        if (REMOTE_DBC) {
//            try {
//                DBUtilsConection.doSshTunnel(strSshUser, strSshPassword, strSshHost, nSshPort,
//                        strRemoteHost, nLocalPort, nRemotePort);
//            } catch (JSchException e1) {
//                e1.printStackTrace();
//            }
//            try {
//                Class.forName("com.mysql.cj.jdbc.Driver");
//            } catch (ClassNotFoundException e1) {
//                e1.printStackTrace();
//            }
//            try {
//                con = DriverManager
//                        .getConnection("jdbc:mysql://localhost:" + nLocalPort
//                                + "/TravelSourceLLC", strDbUser, strDbPassword);
//            } catch (SQLException e1) {
//                e1.printStackTrace();
//            }
//        } else {
//            try { // local port number use to bind SSH tunnel
//                String strDbUser = "root"; // database loging username
//                String strDbPassword = "Samsung23@!"; // database login
//                Class.forName("com.mysql.cj.jdbc.Driver");
//                nLocalPort = 3306;
//                con = DriverManager.getConnection(
//                        "jdbc:mysql://localhost:" + nLocalPort + "/TravelSourceLLC", strDbUser,
//                        strDbPassword);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

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
                            String sql = "select sum(frequency_1)+ sum(frequency_2)+sum(frequency_3)+sum(frequency_4) as value from TravelSourceLLC.KeywordExpanded";
                            ResultSet rs = null;
                            try {
                                rs = getStatement().executeQuery(sql);
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

    String filtered = "";
    List<screen.Keywords> calculations;
    List<String> mySpreadSheets = new ArrayList<String>();

    private void filterByCategory() {
        String sql = "SELECT title_category FROM TravelSourceLLC.KeywordExpanded group by title_category";
        ResultSet rs = null;
        try {
            rs = getStatement().executeQuery(sql);
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
                    String s = (((JTextComponent) ((JComboBox) ((Component) e.getSource()).getParent()).getEditor().getEditorComponent()).getText());
                    if (!s.isEmpty()) {
                        sorter.setRowFilter(RowFilter.regexFilter(s, 9));
                        filtered = s;
                        new Thread() {
                            public void run() {
//                                listModel.clear();
//                                ArrayList<Colourful> categoriesColors = new ArrayList<>();
//                                ArrayList<String> categoriesList = new ArrayList<>();
//                                for (Colourful c : colors) {
//                                    if (!categoriesList.contains(c.getCategories()) && c.getCategories() != null) {
//                                        c.setTotalSumPerColor(getKeywordSum(filtered, c.getCategories()));
//                                        categoriesColors.add(c);
//                                        categoriesList.add(c.getCategories());
//                                        listModel.addElement(c.getCategories());
//                                    }
//                                }
//                                list1.setCellRenderer(new ColorRenderer(categoriesColors));
////                                list1.setModel(listModel);
//                                list1.revalidate();
                                listModel = new DefaultListModel<>();
                                ArrayList<screen.Colourful> categoriesColors = new ArrayList<>();
                                HashMap<String, Double> eval = getKeywordSumV3(filtered);
                                for (int i = 0; i < colors.size(); i++) {
                                    if (colors.get(i).categories != null) {
                                        if (!listModel.contains(colors.get(i).categories)) {
                                            listModel.addElement(colors.get(i).categories);
                                            double d = 0.0;
                                            for (HashMap.Entry<String, Double> m : eval.entrySet()) {
//                                                System.out.println("Key = " + m.getKey() +
//                                                        ", Value = " + m.getValue());
                                                if (m.getKey().toString().equals("Too Thin")) {
                                                    System.out.println("!!!Key = " + m.getKey() +
                                                            ", Value = " + m.getValue());
                                                }
                                                if (m.getKey().equals(colors.get(i).categories)) {
                                                    d = d + m.getValue();
                                                }
                                            }
                                            colors.get(i).setTotalSumPerColor(d);
                                            categoriesColors.add(colors.get(i));
                                        }
                                    }
                                }
                                list1.setModel(listModel);
                                list1.setCellRenderer(new ColorRenderer(categoriesColors));
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
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
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
        panel1.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table1.setAutoResizeMode(0);
        scrollPane1.setViewportView(table1);
        list1 = new JList();
        panel1.add(list1, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
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

    public class ColorRenderer extends DefaultListCellRenderer {
        ArrayList<screen.Colourful> values = new ArrayList<>();
        double totalSum = 0.0;

        public ColorRenderer(ArrayList<screen.Colourful> values) {
            this.values = values;
            setOpaque(true);
            this.totalSum = getTotalSum(filtered);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            try {
                this.setBackground(ColorUtils.getColorFromName(values.get(index).color));
                this.setForeground(ColorUtils.getColorFromHex(values.get(index).textHex));
            } catch (Exception e) {
                // e.printStackTrace();
            }
            try {
                this.setText(values.get(index).categories);
                double average = values.get(index).getTotalSumPerColor();
                double dc = average * 100 / (totalSum);
                if (!Double.isNaN(dc)) {
                    this.setText(df1.format(dc) + "% " + values.get(index).categories);
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
            return this;
        }
    }
}


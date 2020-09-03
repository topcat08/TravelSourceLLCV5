package screen;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static screen.Explorer.getTextColorName;

public class ColorViewer {
    public JPanel panel;
    private JTable table1;
    private JTextField textField1;
    private JButton add;
    private JButton delete;
    ArrayList<screen.Colourful> colors = new ArrayList<>();
    TableRowSorter<DefaultTableModel> sorter = null;
    static JTable tableExplorer;

    public static void main(String[] args) {
        JFrame frame = new JFrame("ColorViewer");
        frame.setContentPane(new ColorViewer(tableExplorer).panel);
        frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    String columnNames[] = null;

    public ColorViewer(JTable tableExplorer) {
        this.tableExplorer = tableExplorer;
        $$$setupUI$$$();
        textField1.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent arg0) {
            }

            @Override
            public void keyReleased(KeyEvent arg0) {
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
                if (textField1.getText().toString().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter(textField1.getText()));
                }
            }
        });
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    DefaultTableModel m = (DefaultTableModel) table1.getModel();
                    System.out.println(m.getRowCount() + " rows");
                    int rows = table1.getSelectedRow();
                    String query = "DELETE FROM `TravelSourceLLC`.`Color` WHERE `idColor`='" + table1.getValueAt(table1.getSelectedRow(), 0) + "';";
                    try {
                        Explorer.con.createStatement().execute(query);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    m.removeRow(rows);
                    System.out.println(query + " row is +" + rows);
                    table1.revalidate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                InsertColorDialog dialog = new InsertColorDialog(table1);
                dialog.pack();
                dialog.setResizable(false);
                dialog.setVisible(true);
            }
        });
    }

    private void createUIComponents() {
        DefaultTableModel model = new DefaultTableModel(new String[]{
                "#", "Category", "Keyword", "Hex", "Text Hex", "Match Type"
        }, 0) {
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

        table1.setModel(model);

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
                    for (screen.Colourful c : colors) {
                        t.add(c.keyword);
                        if (containss((String) value, c.keyword, c.categoryCondition)) {
                            Color dd = null;
                            Color cx = null;
                            switch (column) {
                                case 2:
                                    try {
                                        cx = Color.decode("#" + getTextColorName((String) value)); //getTextColorName((String) value));
                                        if (cx != null) comp.setForeground(cx);
                                        Color dd2 = Color.decode("#" + Explorer.getHexColorName((String) value));
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

        Statement st = null;
        try {
            st = Explorer.con.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        String sql = "Select * from TravelSourceLLC.Color";
        ResultSet rs = null;
        try {
            rs = st.executeQuery(sql);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = 1;
            int columnCount = rsmd.getColumnCount();
            System.out.println("column count is " + columnCount);
            columnNames = new String[columnCount];
            while (count < columnCount) {
                columnNames[count - 1] = rsmd.getColumnLabel(count);
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            while (rs.next()) {
                screen.Colourful data = new screen.Colourful();
                int id = rs.getInt("idColor");
//                String aFirst = rs.getString("Color");

                String a = rs.getString("Category");
                String b = rs.getString("keyword");
                String c = rs.getString("HexColor");
                String d = rs.getString("TextColor");
                String e = rs.getString("CategoryCondition");

                data.categories = a;
                data.keyword = b;
                data.hex = c;
                data.textHex = d;
                data.matctchType = e;
                colors.add(data);
                model.addRow(new Object[]{id, a, b, c, d, e});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sorter = new TableRowSorter<DefaultTableModel>(
                model);
        table1.setRowSorter(sorter);
        table1.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                TableModel model = (TableModel) e.getSource();
                if (e.getType() == TableModelEvent.UPDATE) {
                    try {
                        String query = "UPDATE `TravelSourceLLC`.`Color` SET " +
                                "`" + columnNames[e.getColumn()] + "`='" + table1.getValueAt(table1.getSelectedRow(), table1.getSelectedColumn())
                                + "' WHERE `idColor`='" + table1.getValueAt(table1.getSelectedRow(), 0) + "';";
                        Explorer.con.prepareStatement(query).execute();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        table1.setBackground(UIManager.getColor("Button.highlight"));
        table1.getColumnModel().getColumn(0).setPreferredWidth(40);
        table1.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }

    private String getColorName(String keyword) {
        for (screen.Colourful c : colors) {
            if (keyword.equalsIgnoreCase(c.keyword) || keyword.contains(c.keyword)) {
                return c.color;
            }
        }
        return null;
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

    public static boolean containss(String haystack, String needle, String categoryType) {
        haystack = haystack == null ? "" : haystack;
        needle = needle == null ? "" : needle;
        int type = 0;
        if (categoryType == null || categoryType.isEmpty()) {
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

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 4, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table1 = new JTable();
        scrollPane1.setViewportView(table1);
        textField1 = new JTextField();
        panel.add(textField1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        add = new JButton();
        add.setText("Add");
        panel.add(add, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        delete = new JButton();
        delete.setText("Delete");
        panel.add(delete, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

    //
//    class ColourfulTableModel extends AbstractTableModel {
//        final Class[] columnClasses = {String.class, String.class,
//                Float.class, Boolean.class};
//        // holds our data
//        final Vector data = new Vector();
//        // adds a row
//        public void addWine(Colourful w) {
//            data.addElement(w);
//            fireTableRowsInserted(data.size() - 1, data.size() - 1);
//        }
//
//        public int getColumnCount() {
//            return columnNames.length;
//        }
//
//        public int getRowCount() {
//            return data.size();
//        }
//
//        public String getColumnName(int col) {
//            return columnNames[col];
//        }
//
//        public Object getValueAt(int row, int col) {
//            return null;
//        }
//
//        public Object getValueAtRow(int row) {
//            Colourful rowa = (Colourful) data.elementAt(row);
//            return rowa;
//        }
//        public boolean isCellEditable(int row, int col) {
//            return false;
//        }
//    }


}
//
//    private static void doSshTunnel(String strSshUser, String strSshPassword,
//                                    String strSshHost, int nSshPort, String strRemoteHost,
//                                    int nLocalPort, int nRemotePort) throws JSchException {
//        final JSch jsch = new JSch();
//        Session session = jsch.getSession(strSshUser, strSshHost, 22);
//        session.setPassword(strSshPassword);
//        final Properties config = new Properties();
//        config.put("StrictHostKeyChecking", "no");
//        session.setConfig(config);
//        session.connect();
//        session.setPortForwardingL(nLocalPort, strRemoteHost, nRemotePort);
//    }
//}

//
//public class ColorRenderer extends DefaultListCellRenderer {
//    ArrayList<Colourful> values = new ArrayList<>();
//    double totalSum = 0.0;
//    List<Double> topResults = new ArrayList<>();
//    List<Double> firstNelementsList = null;
//
//    public ColorRenderer(ArrayList<Colourful> values) {
//        this.values = values;
//        setOpaque(true);
//        Collections.sort(topResults);
//        firstNelementsList = new ArrayList<Double>(topResults.subList(topResults.size() - 3, topResults.size()));
//    }
//
//    @Override
//    public Component getListCellRendererComponent(
//            JList<?> list,
//            Object value,
//            int index,
//            boolean isSelected,
//            boolean cellHasFocus) {
//        try {
//            this.setBackground(ColorUtils.getColorFromName(values.get(index).color));
//            this.setForeground(ColorUtils.getColorFromHex(values.get(index).textHex));
//        } catch (IndexOutOfBoundsException d) {
//            this.setBackground(ColorUtils.getColorFromName("Green"));
//            this.setForeground(Color.WHITE);
//            d.printStackTrace();
//        }
//
//        try {
//            this.setText(values.get(index).categories);
//            double average = values.get(index).getTotalSumPerColor();
//            double dc = average * 100 / (totalSum);
//            if (firstNelementsList.contains(dc) && !Double.isNaN(dc)) {
//                this.setText(df1.format(dc) + "% <<< " + values.get(index).categories);
//            } else {
//                if (!Double.isNaN(dc)) {
//                    this.setText(df1.format(dc) + "% " + values.get(index).categories);
//                }
//            }
//
//        } catch (Exception e) {
//            //e.printStackTrace();
//        }
//        return this;
//    }
//}

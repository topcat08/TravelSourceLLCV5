package screen;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class Statsv3 {
    private JPanel panel1;
    TableRowSorter<DefaultTableModel> sorter = null;
    String columnNames[] = null;
    private JTable table1;
    private JScrollPane scrollPane1;
    ArrayList<Colourful> colors = new ArrayList<>();
    Statement statement = null;
    private String getDataForTableV2 = "Select * from Statistica";
    String loadColours = "Select * from Color";
    String loadCategories = "SELECT distinct Category FROM TravelSourceLLC.Color ";

    public static void main(String[] args) {
        JFrame frame = new JFrame("Explorer");
        frame.setContentPane(new Statsv3().panel1);
        frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);
    }

    public Statsv3() {
        $$$setupUI$$$();
    }

    private void createUIComponents() {
        statement = getStatement();
        loadColours();
        ArrayList<String> columns = loadCategories();

        //working 80 s on local db cu ajutorul lui florin cioceanu, el reprezinta americanul
        //v2 : >> working 80 s on local db ca nu m-a ajutat  florin cioceanu, el reprezinta americanul, el m-a indemnat la tuica

        // ========================================================================== end of method =====================================================================================================================
        String[] item = columns.toArray(new String[columns.size()]);
        DefaultTableModel model = getTableModel(item);
//        ArrayList<ArrayList<String>> cleanedRows = new ArrayList<ArrayList<String>>();
        table1 = new JTable(model);
        table1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table1.setAutoCreateRowSorter(true);
        sorter = new TableRowSorter<DefaultTableModel>(
                model);
        table1.setRowSorter(sorter);
        table1.getColumnModel().getColumn(0).
                setPreferredWidth(32);
        table1.getColumnModel().getColumn(1).
                setPreferredWidth(158);
        for (Object[] obj : getDataForTableV2(item)) {
            model.addRow(obj);
        }
        table1.setModel(model);

        table1.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            {
                setOpaque(true);
            }

            @Override
            public Component getTableCellRendererComponent(final JTable table,
                                                           final Object value, final boolean isSelected, final boolean hasFocus,
                                                           final int row, final int column) {
                for (Colourful c : colors) {
                    try {
                        if (c.categories.equals(columns.get(column))) {
                            this.setBackground(Color.WHITE);
                            this.setForeground(Color.BLACK);
                            System.out.println("cell hex is " + c.hex);
                            try {
                                this.setBackground(Color.decode("#" + c.hex));
                                this.setForeground(Color.decode("#" + c.textHex));
                            } catch (Exception e) {
//                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
//                        e.printStackTrace();
                    }
                    this.setText(columns.get(column));
                }
                return this;
            }
        });
    }

    private ArrayList<Object[]> getDataForTableV2(String items[]) {
        ResultSet rs = null;
        ArrayList<Object[]> obj = new ArrayList<>();
        Object dc[] = new Object[items.length];
        try {
            rs = getStatement().executeQuery(getDataForTableV2);
            while (rs.next()) {
                for (int i = 0; i < items.length; i++) {
                    try {
                        dc[i] = rs.getDouble(i + 1);
                    } catch (Exception e) {
                        try {
                            dc[i] = rs.getString(i + 1);
                        } catch (Exception e1) {
                        }
                    }
                }
                obj.add(dc);
                dc = new Object[items.length];
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return obj;
    }

    // ==================================================== DO BINARY SEARCH =========================================
    private DefaultTableModel getTableModel(String[] columns) {
        DefaultTableModel model = new DefaultTableModel(columns
                , 0) {
            Class[] types = {Double.class, String.class, Double.class, Double.class};

            @Override
            public Class getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return String.class;
                    case 1:
                        return String.class;
                    default:
                        return Double.class;
                }
            }
        };
        return model;
    }

    private void loadColours() {
        ResultSet rsd = null;
        ResultSet rs = null;
        try {
            rsd = getStatement().executeQuery(loadColours);
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
        ResultSet rsd = null;
        ResultSet rs = null;
        int index = 0;
        ArrayList<String> buf = new ArrayList<>();
        buf.add("#");
        buf.add("Product");
        try {
            rsd = getStatement().executeQuery(loadCategories);
            while (rsd.next()) {
                index = index++;
                buf.add(rsd.getString("Category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return buf;
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
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane1 = new JScrollPane();
        scrollPane1.setHorizontalScrollBarPolicy(32);
        scrollPane1.setVerticalScrollBarPolicy(22);
        panel1.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table1 = new JTable();
        table1.setAutoResizeMode(0);
        scrollPane1.setViewportView(table1);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
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


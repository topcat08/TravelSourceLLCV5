package screen;

import com.jcraft.jsch.JSchException;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.*;
import java.util.ArrayList;

import static screen.Explorer.getTextColorName;

public class EmptyColorViewer {
    private JTable table1;
    public JPanel panel;
    private JTextField textField1;
    private JButton delete;
    private JButton add;
    TableRowSorter<DefaultTableModel> sorter = null;
    String columnNames[] = null;
    ArrayList<screen.Colourful> colors = new ArrayList<>();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Color Explorer Window");
        frame.setContentPane(new EmptyColorViewer().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        System.out.println("hurray");
    }

    public EmptyColorViewer() {
        this.table1 = table1;
        loadDatabase();
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

        //coloring the table
        table1.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                //  comp.setForeground(Color.BLUE);
                comp.setForeground(Color.BLACK);
                //     comp.setBackground(Color.WHITE);
//                try {
//                    String[] differences = ((String) value).split(" ");
//                    ArrayList<String> t = new ArrayList<>();
//                    for (screen.Colourful c : colors) {
//                        t.add(c.keyword);
//
//                        if (containss((String) value, c.keyword, c.categoryCondition)) {
//                            Color dd = null;
//                            Color cx = null;
//                            switch (column) {
//                                case 2:
//                                    try {
//                                        cx = Color.decode("#" + getTextColorName((String) value)); //getTextColorName((String) value));
//                                        if (cx != null) comp.setForeground(cx);
//                                        Color dd2 = Color.decode("#" + Explorer.getHexColorName((String) value));
//                                        if (dd2 != null) comp.setBackground(dd2);
//                                    } catch (Exception e) {
//                                    }
//                                    return comp;
//                                default:
//                                    comp.setForeground(Color.BLACK);
//                                    comp.setBackground(Color.WHITE);
//                                    return comp;
//                            }
//                        }
//                    }
//                    return comp;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                return comp;
            }
        });


        //loading the table
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
        //add the table to the frame
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

    private void justFillInDummyData() {
        //headers for the table
        DefaultTableModel model = new DefaultTableModel();
        table1.setModel(model);
        model.addColumn("Id");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Company Name");
        //actual data for the table in a 2d array

        model.addRow(new Object[]{"text", "text", "text", "text"});
        model.addRow(new Object[]{"text", "text", "text", "text"});
        model.addRow(new Object[]{"text", "text", "text", "text"});
        model.addRow(new Object[]{"text", "text", "text", "text"});
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
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
        panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        textField1 = new JTextField();
        panel1.add(textField1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        add = new JButton();
        add.setText("Add");
        panel1.add(add, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        delete = new JButton();
        delete.setText("Delete");
        panel1.add(delete, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table1 = new JTable();
        scrollPane1.setViewportView(table1);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}

package screen;

import javax.swing.*;
import java.awt.*;

    public class Statsv2 extends JPanel {
        public Statsv2() {
            initializeUI();
        }

        private void initializeUI() {
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(500, 200));

            JTable table = new JTable(20, 20);

            // Turn off JTable's auto resize so that JScrollPane will show a
            // horizontal scroll bar.
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            JScrollPane pane = new JScrollPane(table);
            add(pane, BorderLayout.CENTER);
        }

        private static void showFrame() {
            JPanel panel = new Statsv2();
            panel.setOpaque(true);

            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setTitle("Scrollable JTable");
            frame.setContentPane(panel);
            frame.pack();
            frame.setVisible(true);
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Statsv2.showFrame();
                }
            });
        }
    }

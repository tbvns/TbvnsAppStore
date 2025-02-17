package xyz.tbvns.UI;

import javax.swing.*;
import java.awt.*;

public class FilterUI {
    public static void show() {
        JFrame frame = new JFrame("Filters");
        frame.setSize(200, 150);
        frame.setResizable(false);
        WindowUtils.setIcon(frame, WindowUtils.Icons.normal);
        WindowUtils.center(frame);

        JPanel panel = new JPanel() {{
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            // Category Panel
            add(new JPanel(new BorderLayout()) {{
                add(new JLabel("Category:"), BorderLayout.WEST);
                JComboBox<String> comboBox = new JComboBox<>(new String[]{"Coming", "Soon", "yay"});
                add(comboBox, BorderLayout.EAST);
                setMaximumSize(new Dimension(Short.MAX_VALUE, comboBox.getPreferredSize().height));
            }});

            // Separator
            add(new JSeparator() {{
                setMaximumSize(new Dimension(Short.MAX_VALUE, getPreferredSize().height));
            }});

            // Tags Label Panel - constrain maximum size to preferred size
            add(new JPanel(new FlowLayout(FlowLayout.LEFT)) {{
                add(new JLabel("Tags:"));
                setMaximumSize(getPreferredSize());
            }});

            // Select Panel
            add(new JPanel(new BorderLayout()) {{
                add(new JLabel("Add:"), BorderLayout.WEST);
                JComboBox<String> comboBox = new JComboBox<>(new String[]{"Coming", "Soon", "yay"});
                add(comboBox, BorderLayout.EAST);
                setMaximumSize(new Dimension(Short.MAX_VALUE, comboBox.getPreferredSize().height));
            }});

            // Selected Panel
            add(new JPanel(new BorderLayout()) {{
                add(new JLabel("Remove:"), BorderLayout.WEST);
                JComboBox<String> comboBox = new JComboBox<>(new String[]{"Coming", "Soon", "yay"});
                add(comboBox, BorderLayout.EAST);
                setMaximumSize(new Dimension(Short.MAX_VALUE, comboBox.getPreferredSize().height));
            }});
        }};

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        frame.setContentPane(panel);
        frame.setVisible(true);
    }
}

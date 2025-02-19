package xyz.tbvns.UI;

import xyz.tbvns.Apps.Manager.AppListManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class FilterUI {
    public static String selectedCategory = "All";
    public static List<String> selectedTags = new ArrayList<>();

    public static void show() {
        JFrame frame = new JFrame("Filters");
        frame.setSize(250, 250);
        frame.setResizable(false);
        WindowUtils.setIcon(frame, WindowUtils.Icons.normal);
        WindowUtils.center(frame);

        JPanel panel = new JPanel() {{
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(10, 10, 10, 10));

            AtomicReference<String> category = new AtomicReference<>("All");

            // Category Panel
            add(new JPanel(new BorderLayout()) {{
                add(new JLabel("Category:"), BorderLayout.WEST);
                ArrayList<String> categories = new ArrayList<>();
                categories.add("All");
                categories.addAll(AppListManager.categories);
                JComboBox<String> comboBox = new JComboBox<>(categories.toArray(new String[0])){{
                    addActionListener(a -> category.set(categories.get(getSelectedIndex())));
                }};
                add(comboBox, BorderLayout.EAST);
                setMaximumSize(new Dimension(Short.MAX_VALUE, comboBox.getPreferredSize().height));
                setBorder(new EmptyBorder(0, 0, 5, 0));
            }});

            // Separator
            add(new JSeparator() {{
                setMaximumSize(new Dimension(Short.MAX_VALUE, getPreferredSize().height));
            }});

            // Tags Label Panel
            add(new JPanel() {{
                add(new JLabel("Tags:"));
                setMaximumSize(getPreferredSize());
            }});

            List<JCheckBox> boxes = new ArrayList<>();

            add(new JScrollPane(new JPanel() {{
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                for (String tag : AppListManager.tags) {
                    JCheckBox box = new JCheckBox(tag);
                    if (selectedTags.contains(tag)) box.setSelected(true);
                    add(box);
                    boxes.add(box);
                }
            }}));

            // Glue to push button to bottom
            add(Box.createVerticalGlue());

            // Button Panel to center the button
            add(new JPanel(new FlowLayout(FlowLayout.CENTER)){{
                JButton applyButton = new JButton("Apply"){{
                    addActionListener(a -> {
                        selectedCategory = category.get();
                        selectedTags = boxes.stream()
                                .filter(JCheckBox::isSelected)
                                .map(JCheckBox::getText)
                                .toList();
                        MainWindow.reloadFromFilters();
                        frame.dispose();
                    });
                    Dimension buttonSize = new Dimension(frame.getSize().width - 20, 30);
                    setPreferredSize(buttonSize);
                    setMinimumSize(buttonSize);
                    setMaximumSize(buttonSize);
                }};
                add(applyButton);
            }});
        }};


        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        frame.setContentPane(panel);
        frame.setVisible(true);
    }
}

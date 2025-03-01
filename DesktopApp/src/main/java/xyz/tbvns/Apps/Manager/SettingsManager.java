package xyz.tbvns.Apps.Manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import xyz.tbvns.Apps.Object.App;
import xyz.tbvns.Apps.Object.AppSettings;
import xyz.tbvns.UI.WindowUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static xyz.tbvns.Utils.sleep;

public class SettingsManager {

    @SneakyThrows
    public static void showSettings(App app) {
        AtomicReference<AppSettings> settings = new AtomicReference<>(getSettings(app));
        JFrame frame = new JFrame(app.getName() + " Settings");
        WindowUtils.setIcon(frame, WindowUtils.Icons.normal);
        frame.setSize(220, 200);
        frame.setResizable(false);
        WindowUtils.center(frame);
        JPanel panel = new JPanel(){{
            setBorder(new EmptyBorder(10, 10, 10, 10));
            JPanel settingsPanel = new JPanel(){{
                add(new JCheckBox("Auto start"){{
                    setSelected(settings.get().isAutoExec());
                    addActionListener(a -> settings.get().setAutoExec(!settings.get().isAutoExec()));
                    setAlignmentX(0);
                }});
                setPreferredSize(new Dimension(180, 30));
            }};
            settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.X_AXIS));
            add(settingsPanel);
            add(new JButton("Auto start rules"){{
                setPreferredSize(new Dimension(180, 30));
                addActionListener(a -> {
                    JFrame f = showAutoStart(app);
                    WindowUtils.setIcon(f, WindowUtils.Icons.normal);
                    frame.dispose();
                    new Thread(() -> {
                        while (f.isVisible() && f.isShowing()) sleep(100);
                        showSettings(app);
                    }).start();
                });
            }});
            add(new JButton("Save"){{
                setPreferredSize(new Dimension(180, 30));
                addActionListener(a -> {
                    try {
                        FileUtils.writeStringToFile(
                                app.getConfigFile(),
                                new ObjectMapper()
                                        .writerWithDefaultPrettyPrinter()
                                        .writeValueAsString(settings),
                                Charset.defaultCharset(),
                                false
                        );
                        frame.dispose();
                    } catch (Exception ignored){}
                });
            }});
            add(new JButton("Uninstall"){{
                setPreferredSize(new Dimension(180, 30));
                setBackground(new Color(199, 84, 80));
                addActionListener(a -> {
                    AppManager.uninstall(app);
                    frame.dispose();
                });
            }});
        }};
        panel.setLayout(new FlowLayout());
        frame.setContentPane(panel);

        frame.setVisible(true);
    }

    @SneakyThrows
    public static JFrame showAutoStart(App app) {
        AppSettings settings = getSettings(app);
        AtomicReference<JFrame> frame = new AtomicReference<>(new JFrame(app.getName() + " auto start settings"));
        WindowUtils.setIcon(frame.get(), WindowUtils.Icons.normal);
        frame.get().setSize(250, 280);
        frame.get().setResizable(false);

        WindowUtils.center(frame.get());

        JPanel panel = new JPanel(){{
            autoExecList list = new autoExecList(settings.getAutoExecList());
            add(list);
            add(new JButton("Add"){{
                addActionListener(l -> {
                    JFrame f = new JFrame("Add auto start rule");
                    WindowUtils.setIcon(f, WindowUtils.Icons.normal);
                    f.setSize(250, 80);
                    f.setResizable(false);
                    JPanel p = new JPanel();
                    JTextField textField = new JTextField();
                    p.add(textField);
                    p.add(new JButton("Save"){{
                        addActionListener(a -> {
                            f.dispose();
                            list.getAutoExec().add(textField.getText());
                            list.redraw();
                        });
                    }});
                    f.setContentPane(p);
                    WindowUtils.center(f);
                    f.setVisible(true);
                    f.toFront();
                });
            }});
            add(new JButton("Save"){{
                addActionListener(a -> {
                    try {
                        settings.setAutoExecList(list.autoExec);
                        FileUtils.writeStringToFile(
                                app.getConfigFile(),
                                new ObjectMapper()
                                        .writerWithDefaultPrettyPrinter()
                                        .writeValueAsString(settings),
                                Charset.defaultCharset(),
                                false
                        );
                    } catch (Exception ignored){}
                    frame.get().dispose();
                });
            }});
        }};
        frame.get().setContentPane(panel);

        frame.get().setVisible(true);

        frame.get().setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        return frame.get();
    }

    private static class autoExecList extends JScrollPane {
        private final JPanel listPanel;

        @Getter
        private final List<String> autoExec;
        public autoExecList(List<String> autoExec) {
            JPanel panel = new JPanel();
            listPanel = panel;
            this.autoExec = autoExec;
            for (String s : autoExec) {
                addExec(s);
            }

            panel.setPreferredSize(new Dimension(175, autoExec.size() * 45 * 10));
            setPreferredSize(new Dimension(200, 200));
            setViewportView(panel);
        }

        public void redraw() {
            listPanel.removeAll();
            for (String s : autoExec) {
                addExec(s);
            }
        }

        public void addExec(String s) {
            JPanel line = new JPanel();
            JLabel label = new JLabel(s){{
                setPreferredSize(new Dimension(100, 30));
            }};
            line.add(label);
            line.add(new JButton("-"){{
                setPreferredSize(new Dimension(30,30));
                addActionListener(a -> {
                    listPanel.remove(line);
                    listPanel.repaint();
                    autoExec.remove(s);
                });
            }});
            line.add(new JButton("E"){{
                setPreferredSize(new Dimension(30,30));
                addActionListener(a -> {
                    JFrame frame = new JFrame("Edit auto start rule");
                    WindowUtils.setIcon(frame, WindowUtils.Icons.normal);
                    frame.setSize(250, 80);
                    frame.setResizable(false);
                    JPanel p = new JPanel();
                    JTextField textField = new JTextField(){{
                        setText(s);
                    }};
                    p.add(textField);
                    p.add(new JButton("Save"){{
                        addActionListener(a -> {
                            for (int i = 0; i < autoExec.size(); i++) {
                                if (autoExec.get(i).equals(s)) {
                                    autoExec.set(i, textField.getText());
                                    break;
                                }
                            }
                            label.setText(textField.getText());
                            frame.dispose();
                        });
                    }});
                    frame.setContentPane(p);
                    WindowUtils.center(frame);
                    frame.setVisible(true);
                });
            }});
            listPanel.add(line);
            listPanel.revalidate();
        }
    }

    @SneakyThrows
    public static AppSettings getSettings(App app) {
        AppSettings settings;
        if (!app.getConfigFile().exists()) {
            settings = new AppSettings(app);
        } else {
            ObjectMapper mapper = new ObjectMapper();
            settings = mapper.readValue(new String(FileUtils.readFileToByteArray(app.getConfigFile())), AppSettings.class);
        }
        return settings;
    }
}

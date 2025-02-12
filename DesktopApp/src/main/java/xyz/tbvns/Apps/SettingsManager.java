package xyz.tbvns.Apps;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

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
        frame.setSize(200, 200);
        WindowUtils.center(frame);
        JPanel panel = new JPanel(){{
            setBorder(new EmptyBorder(10, 10, 10, 10));
            add(new JCheckBox("Auto start"){{
                setSelected(settings.get().isAutoExec());
                addActionListener(a -> {
                    settings.get().setAutoExec(!settings.get().isAutoExec());
                });
            }});
            add(new JButton("Auto start rules"){{
                addActionListener(a -> {
                    JFrame f = showAutoStart(app);
                    frame.dispose();
                    new Thread(() -> {
                        while (f.isVisible() && f.isShowing()) sleep(100);
                        showSettings(app);
                    }).start();
                });
            }});
            add(new JButton("Save"){{
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
                    } catch (Exception e){}
                });
            }});
            add(new JButton("Uninstall"){{
                setBackground(Color.RED);
            }});
        }};
        frame.setContentPane(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        frame.setVisible(true);
    }

    @SneakyThrows
    public static JFrame showAutoStart(App app) {
        AppSettings settings = getSettings(app);
        AtomicReference<JFrame> frame = new AtomicReference<>(new JFrame(app.getName() + " auto start settings"));
        frame.get().setSize(250, 280);
        frame.get().setResizable(false);

        WindowUtils.center(frame.get());

        JPanel panel = new JPanel(){{
            autoExecList list = new autoExecList(settings.getAutoExecList());
            add(list);
            add(new JButton("Add"){{
                addActionListener(l -> {
                    JFrame f = new JFrame("");
                    f.setSize(200, 80);
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
                    } catch (Exception e){}
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
        private JPanel listPanel;

        @Getter
        private List<String> autoExec;
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
                    JFrame frame = new JFrame("");
                    frame.setSize(200, 80);
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

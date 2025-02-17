package xyz.tbvns.UI;

import com.formdev.flatlaf.FlatDarculaLaf;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import xyz.tbvns.*;
import xyz.tbvns.Apps.Launcher.ProcessChecker;
import xyz.tbvns.Apps.Manager.AppListManager;
import xyz.tbvns.Apps.Object.App;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;

import static xyz.tbvns.UI.WindowUtils.icon;

@Slf4j
public class MainWindow {
    public static boolean isShown = false;
    public static JPanel appPanel;
    public static JScrollPane appPane;
    @SneakyThrows
    public static void show() {
        if (isShown) return;
        isShown = true;
        EZConfig.registerClassPath("xyz.tbvns.Configs");
        EZConfig.load();
        EZConfig.save();

        JFrame frame = new JFrame();
        frame.setTitle("Tbvns's app store");
        frame.setSize(350, 500);
        frame.setMaximumSize(new Dimension(300, 500));
        frame.setMinimumSize(new Dimension(300, 500));
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        WindowUtils.center(frame);

        WindowUtils.setIcon(frame, WindowUtils.Icons.normal);

        JPanel main = new JPanel();
        main.setBorder(new EmptyBorder(10, 10, 10, 10));
        main.setLayout(new FlowLayout());

        JTextField search = new JTextField(){{
            setPreferredSize(new Dimension(330, 30));
            addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    String text = getText() + e.getKeyChar();
                    appPanel.removeAll();
                    for (App app : Utils.searchFilter(AppListManager.listApps(), text)) {
                        appPanel.add(AppListManager.appElementHashMap.get(app.getPath()));
                        appPanel.add(new JSeparator());
                    }
                    appPanel.revalidate();
                }
                @Override public void keyPressed(KeyEvent e) {

                }
                @Override public void keyReleased(KeyEvent e) {

                }
            });
        }};
        BufferedImage image = ImageIO.read(Main.class.getResource("/Icons/search.png"));
        ImageIcon imageIcon = new ImageIcon(image.getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        TextPrompt searchPrompt = new TextPrompt("Search", search);
        searchPrompt.setIcon(imageIcon);
        searchPrompt.setHorizontalTextPosition(SwingConstants.TRAILING);
        main.add(search);

        JPanel filterPanel = new JPanel(){{
            add(new JLabel("Order:"));
            add(new JComboBox<>(new String[]{"Download", "Stars", "A-Z", "Z-A"}){{
                addActionListener(a -> {
                    appPanel.removeAll();
                    switch (getSelectedIndex()) {
                        case 0 -> {
                            for (App app : Utils.sort(AppListManager.listApps(), Utils.appSortType.download)) {
                                appPanel.add(AppListManager.appElementHashMap.get(app.getPath()));
                                appPanel.add(new JSeparator());
                            }
                        }
                        case 1 -> {
                            for (App app : Utils.sort(AppListManager.listApps(), Utils.appSortType.stars)) {
                                appPanel.add(AppListManager.appElementHashMap.get(app.getPath()));
                                appPanel.add(new JSeparator());
                            }
                        }
                        case 3 -> {
                            for (App app : Utils.sort(AppListManager.listApps(), Utils.appSortType.ZA)) {
                                appPanel.add(AppListManager.appElementHashMap.get(app.getPath()));
                                appPanel.add(new JSeparator());
                            }
                        }
                        default -> {
                            for (App app : Utils.sort(AppListManager.listApps(), Utils.appSortType.AZ)) {
                                appPanel.add(AppListManager.appElementHashMap.get(app.getPath()));
                                appPanel.add(new JSeparator());
                            }
                        }
                    }
                    appPanel.revalidate();
                    appPanel.repaint();
                });
            }});
            add(new JSeparator(SwingConstants.VERTICAL));
            add(new JLabel(" Filters:"));
            add(new JButton("Select"){{
                addActionListener(a -> {
                    FilterUI.show();
                });
            }});
            add(new JButton("Reset"));
        }};
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));
        main.add(filterPanel);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new JMenu("File"){{
            JMenuItem item = add("Settings");
            item.setIcon(icon("settings"));
            item = add("Exit");
            item.setIcon(icon("exit"));
            item.addActionListener(a -> Runtime.getRuntime().exit(0));
        }});
        menuBar.add(new JMenu("Help"){{
            JMenuItem item = add("About");
            item.setIcon(icon("info"));
            item = add("Donate");
            item.setIcon(icon("donate"));
            item.addActionListener(a -> {
                try {
                    Desktop.getDesktop().browse(URI.create("https://ko-fi.com/tbvns"));
                } catch (IOException e) {
                    ErrorHandler.handle(e, false);
                }
            });
            item = add("Discord");
            item.setIcon(icon("discord"));
            item.addActionListener(a -> {
                try {
                    Desktop.getDesktop().browse(URI.create("https://discord.gg/Vh8QAMq6BY"));
                } catch (IOException e) {
                    ErrorHandler.handle(e, false);
                }
            });
        }});
        frame.setJMenuBar(menuBar);

        appPanel = new JPanel();
        appPanel.setLayout(new BoxLayout(appPanel, BoxLayout.Y_AXIS));
        appPanel.setPreferredSize(new Dimension(frame.getSize().width - 20, 350)); //TODO: fix the size of the pane
        appPane = new JScrollPane(appPanel);

        for (AppElement app : AppListManager.retrieveApps()) {
            appPanel.add(app);
            appPanel.add(new JSeparator());
        }

        main.add(appPane);

        frame.setContentPane(main);
        frame.setVisible(true);

        new Thread(() -> {
            while (frame.isShowing()) Utils.sleep(100);
            isShown = false;
        }) {{
            setName("WindowCheckThread");
            setDaemon(true);
            start();
        }};
    }

    public static void reloadFromFilters() {
        appPanel.removeAll();
        int count = 0;
        for (App app : AppListManager.listApps()) {
            if (
                    new HashSet<>(Arrays.stream(app.getTags()).toList()).containsAll(FilterUI.selectedTags) &&
                    (FilterUI.selectedCategory.equals("All") || FilterUI.selectedCategory.equals(app.getCategory()))
            ) {
                appPanel.add(AppListManager.appElementHashMap.get(app.getPath()));
                appPanel.add(new JSeparator());
                count++;
            }
        }
        if (count == 0) {
            appPanel.add(new JLabel("No result."));
        }

        appPanel.revalidate();
        appPanel.repaint();
    }
}

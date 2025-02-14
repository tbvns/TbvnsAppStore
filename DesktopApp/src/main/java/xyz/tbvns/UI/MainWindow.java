package xyz.tbvns.UI;

import com.formdev.flatlaf.FlatDarculaLaf;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import xyz.tbvns.Apps.Launcher.ProcessChecker;
import xyz.tbvns.Apps.Manager.AppListManager;
import xyz.tbvns.Constant;
import xyz.tbvns.EZConfig;
import xyz.tbvns.ErrorHandler;
import xyz.tbvns.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

import static xyz.tbvns.UI.WindowUtils.icon;

@Slf4j
public class MainWindow {
    @SneakyThrows
    public static void show() {
        EZConfig.registerClassPath("xyz.tbvns.Configs");
        EZConfig.load();
        EZConfig.save();

        FlatDarculaLaf.setup();
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

        JTextField search = new JTextField(){{setPreferredSize(new Dimension(330, 30));}};
        BufferedImage image = ImageIO.read(Main.class.getResource("/Icons/search.png"));
        ImageIcon imageIcon = new ImageIcon(image.getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        TextPrompt searchPrompt = new TextPrompt("Search", search);
        searchPrompt.setIcon(imageIcon);
        searchPrompt.setHorizontalTextPosition(SwingConstants.TRAILING);
        main.add(search);

        JPanel filterPanel = new JPanel(){{
            add(new JLabel("Order:"));
            add(new JComboBox<>(new String[]{"Download", "Stars", "A-Z", "Z-A"}));
            add(new JSeparator(SwingConstants.VERTICAL));
            add(new JLabel(" Filters:"));
            add(new JButton("Select"));
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

        JPanel appPanel = new JPanel();
        appPanel.setLayout(new BoxLayout(appPanel, BoxLayout.Y_AXIS));
        JScrollPane appPane = new JScrollPane(appPanel);

        for (AppElement app : AppListManager.retrieveApps()) {
            appPanel.add(app);
        }

        main.add(appPane);

        frame.setContentPane(main);
        frame.setVisible(true);

        log.info("Server address is {}", Constant.serverUrl);
        log.info("Install folder is {}", Constant.mainFolder);

        ProcessChecker.start();
    }
}

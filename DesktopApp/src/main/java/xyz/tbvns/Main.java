package xyz.tbvns;

import com.formdev.flatlaf.FlatDarculaLaf;
import lombok.SneakyThrows;
import xyz.tbvns.Apps.AppElement;
import xyz.tbvns.Apps.AppListManager;
import xyz.tbvns.UI.TextPrompt;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        JFrame frame = new JFrame();
        frame.setTitle("Tbvns's app store");
        frame.setSize(350, 500);
        frame.setMaximumSize(new Dimension(300, 500));
        frame.setMinimumSize(new Dimension(300, 500));
        frame.setResizable(false);

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
        menuBar.add(new JMenu("File"){{add("Settings"); add("Exit");}});
        menuBar.add(new JMenu("Help"){{add("About"); add("Donate"); add("Discord");}});
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
    }
}
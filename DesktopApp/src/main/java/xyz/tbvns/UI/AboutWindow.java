package xyz.tbvns.UI;

import com.formdev.flatlaf.FlatDarculaLaf;
import lombok.SneakyThrows;
import xyz.tbvns.ErrorHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class AboutWindow {
    @SneakyThrows
    public static void show() {
        FlatDarculaLaf.setup();
        JFrame frame = new JFrame("About tbvns's app store");
        frame.toFront();
        frame.setSize(500, 400);
        WindowUtils.center(frame);
        WindowUtils.setIcon(frame, WindowUtils.Icons.normal);


        JPanel panel = new JPanel(){{
            add(new JLabel(){{
                setIcon(new ImageIcon(ImageIO.read(AboutWindow.class.getResource("/Logos/banner.png")).getScaledInstance(500, 187, Image.SCALE_SMOOTH)));
            }});
            add(new JLabel("A hassle-free way to distribute my code!"){{
                setFont(getFont().deriveFont(20F));
            }});
            add(new JLabel("<html><div style='width: 350px;'>Tbvns's App Store is a lightweight, cross-platform application store designed to simplify the distribution and management of my own software and games. Built with Java and Maven, it provides a seamless experience for users to discover, install, and update my apps and games with ease.</div></html>"));
            add(new JPanel(){{
                setLayout(new FlowLayout());
                add(new JButton("Discord"){{
                    addActionListener(a -> {
                        try {
                            Desktop.getDesktop().browse(URI.create("https://discord.gg/Vh8QAMq6BY"));
                        } catch (IOException e) {
                            ErrorHandler.handle(e, false);
                        }
                    });
                }});
                add(new JButton("Source code"){{
                    addActionListener(a -> {
                        try {
                            Desktop.getDesktop().browse(URI.create("https://github.com/tbvns/TbvnsAppStore"));
                        } catch (IOException e) {
                            ErrorHandler.handle(e, false);
                        }
                    });
                }});
                add(new JButton("Donation"){{
                    addActionListener(a -> {
                        try {
                            Desktop.getDesktop().browse(URI.create("https://ko-fi.com/tbvns"));
                        } catch (IOException e) {
                            ErrorHandler.handle(e, false);
                        }
                    });
                }});
            }});
        }};

        frame.setContentPane(panel);
        frame.setVisible(true);
    }
}

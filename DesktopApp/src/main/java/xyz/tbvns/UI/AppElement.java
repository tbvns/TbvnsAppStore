package xyz.tbvns.UI;

import lombok.Getter;
import lombok.SneakyThrows;
import xyz.tbvns.Api.GitRepoInfo;
import xyz.tbvns.Api.Github;
import xyz.tbvns.Apps.Launcher.AppLauncher;
import xyz.tbvns.Apps.Manager.AppManager;
import xyz.tbvns.Apps.Manager.SettingsManager;
import xyz.tbvns.Apps.Object.App;
import xyz.tbvns.Constant;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class AppElement extends JPanel {
    @Getter
    private JButton dlButton;

    @SneakyThrows
    public AppElement(App app) {
        setBorder(new LineBorder(Color.DARK_GRAY, 1));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        Image logo = ImageIO.read(AppElement.class.getResourceAsStream("/Icons/broken.png")).getScaledInstance(100, 100, Image.SCALE_SMOOTH);

        URL url = new URL(Constant.serverUrl + "/apps/logo?id=" + app.getId());
        try {
            logo = ImageIO.read(url).getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel image = new JLabel();
        image.setSize(20, 20);
        image.setIcon(new ImageIcon(logo));
        add(image);

        GitRepoInfo info = Github.getInfo(app.getPath());

        JPanel infoPanel = new JPanel();
        JLabel infoLabel = new JLabel("<html><b>" + app.getName() + "</b><br><div style='width: 100px; word-wrap: break-word; white-space: normal;'>" + info.getDesc() + "</div></html>");        infoPanel.add(infoLabel);
        add(infoPanel);

        JPanel rightPanel;
        if (!app.isInstalled()) {
            rightPanel = createNotInstalled(app, info);
        } else {
            rightPanel = createInstalled(app, info);
            AppLauncher.launch(app.asInstalledApp());
        }

        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        add(rightPanel);

        setSize(280, 100);
    }

    public JPanel createNotInstalled(App app, GitRepoInfo info) {
        return new JPanel(){{
            add(new JButton("Install"){{
                setAlignmentX(RIGHT_ALIGNMENT);
                addActionListener(a -> {
                    setText("Loading");
                    new Thread(() -> {
                        AppManager.install(app);
                        setText("Settings");
                        for (ActionListener listener : getActionListeners()) {
                            removeActionListener(listener);
                        }
                        addActionListener(b -> {
                            SettingsManager.showSettings(app);
                        });
                    }).start();
                });
                dlButton = this;
            }});
            add(new JButton("Source"){{
                setAlignmentX(RIGHT_ALIGNMENT);
                addActionListener(a -> {
                    try {
                        Desktop.getDesktop().browse(URI.create("https://github.com/" + app.getPath()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }});
            add(new JLabel("<html><b>" + 404 + "</b> Download</html>"){{setAlignmentX(RIGHT_ALIGNMENT);}});
            add(new JLabel("<html><b>"+ info.getStars() +"</b> Stars</html>"){{setAlignmentX(RIGHT_ALIGNMENT);}});
            setAlignmentX(RIGHT_ALIGNMENT);
        }};
    }

    public JPanel createInstalled(App app, GitRepoInfo info) {
        return new JPanel(){{
            add(new JButton("Settings"){{
                setAlignmentX(RIGHT_ALIGNMENT);
                addActionListener(a -> {
                    SettingsManager.showSettings(app);
                });
                dlButton = this;
            }});
            add(new JButton("Source"){{
                setAlignmentX(RIGHT_ALIGNMENT);
                addActionListener(a -> {
                    try {
                        Desktop.getDesktop().browse(URI.create("https://github.com/" + app.getPath()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }});
            add(new JLabel("<html><b>" + 404 + "</b> Download</html>"){{setAlignmentX(RIGHT_ALIGNMENT);}});
            add(new JLabel("<html><b>"+ info.getStars() +"</b> Stars</html>"){{setAlignmentX(RIGHT_ALIGNMENT);}});
            setAlignmentX(RIGHT_ALIGNMENT);
        }};
    }
}

package xyz.tbvns.UI;

import lombok.Getter;
import lombok.SneakyThrows;
import xyz.tbvns.Api.GitRepoInfo;
import xyz.tbvns.Api.Github;
import xyz.tbvns.Apps.Launcher.AppLauncher;
import xyz.tbvns.Apps.Manager.AppListManager;
import xyz.tbvns.Apps.Manager.AppManager;
import xyz.tbvns.Apps.Manager.SettingsManager;
import xyz.tbvns.Apps.Object.App;
import xyz.tbvns.Apps.Object.InstalledApp;
import xyz.tbvns.Constant;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class AppElement extends JPanel {
    @Getter
    private JButton dlButton;
    @Getter
    private App app;
    @Getter
    private boolean offline;

    @SneakyThrows
    public AppElement(App app) {
        this.app = app;
        app.retrievePublicInfo();
        offline = false;
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

        JPanel infoPanel = new JPanel();
        JLabel infoLabel = new JLabel("<html><b>" + app.getName() + "</b><br><div style='width: 100px; word-wrap: break-word; white-space: normal;'>" + app.getDesc() + "</div></html>");
        infoPanel.add(infoLabel);
        add(infoPanel);

        JPanel rightPanel;
        if (!app.isInstalled()) {
            rightPanel = createNotInstalled(app);
        } else if (app.updateAvailable()) {
            rightPanel = createUpdateAvailable(app);
        } else {
            rightPanel = createInstalled(app);
        }

        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        add(rightPanel);


        setSize(280, 100);
        setPreferredSize(new Dimension(280, 100));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
    }

    @SneakyThrows
    public AppElement(InstalledApp app) {
        this.app = app.app;
        offline = true;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        Image logo = ImageIO.read(AppElement.class.getResourceAsStream("/Icons/broken.png")).getScaledInstance(100, 100, Image.SCALE_SMOOTH);

        URL url = new URL("file://" + app.getFolder().getPath() + "/logo.png");
        try {
            logo = ImageIO.read(url).getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel image = new JLabel();
        image.setSize(20, 20);
        image.setIcon(new ImageIcon(logo));
        add(image);

        JPanel infoPanel = new JPanel();
        JLabel infoLabel = new JLabel("<html><b>" + app.getName() + "</b><br><div style='width: 100px; word-wrap: break-word; white-space: normal;'>" + app.getDesc() + "</div></html>");
        infoPanel.add(infoLabel);
        add(infoPanel);
        JPanel rightPanel = createInstallNoConnection(app);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        add(rightPanel);

        setSize(280, 100);
        setPreferredSize(new Dimension(280, 100));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
    }

    public JPanel createNotInstalled(App app) {
        return new JPanel(){{
            add(new JButton("Install"){{
                setPreferredSize(new Dimension(83, 30));
                setMaximumSize(new Dimension(83, 30));
                setMinimumSize(new Dimension(83, 30));
                setAlignmentX(RIGHT_ALIGNMENT);
                addActionListener(a -> {
                    setText("Loading");
                    new Thread(() -> {
                        if (!AppManager.install(app)) {
                            setText("Install");
                            return;
                        }

                        setText("Settings");
                        for (ActionListener listener : getActionListeners()) {
                            removeActionListener(listener);
                        }
                        addActionListener(b -> {
                            SettingsManager.showSettings(app);
                        });
                    }){{
                        setName(app.getName() + "-dlThread");
                    }}.start();
                });
                dlButton = this;
            }});
            add(new JButton("Source"){{
                setPreferredSize(new Dimension(83, 30));
                setMaximumSize(new Dimension(83, 30));
                setMinimumSize(new Dimension(83, 30));
                setAlignmentX(RIGHT_ALIGNMENT);
                addActionListener(a -> {
                    try {
                        Desktop.getDesktop().browse(URI.create("https://github.com/" + app.getPath()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }});
            add(new JLabel("<html><b>" + app.getDownload() + "</b> Download</html>"){{setAlignmentX(RIGHT_ALIGNMENT);}});
            add(new JLabel("<html><b>"+ app.getStars() +"</b> Stars</html>"){{setAlignmentX(RIGHT_ALIGNMENT);}});
            setAlignmentX(RIGHT_ALIGNMENT);
        }};
    }

    public JPanel createUpdateAvailable(App app) {
        return new JPanel(){{
            add(new JButton("Settings"){{
                setPreferredSize(new Dimension(83, 30));
                setMaximumSize(new Dimension(83, 30));
                setMinimumSize(new Dimension(83, 30));
                setAlignmentX(RIGHT_ALIGNMENT);
                addActionListener(a -> {
                    SettingsManager.showSettings(app);
                });
                dlButton = this;
            }});
            add(new JButton("Update"){{
                setPreferredSize(new Dimension(83, 30));
                setMaximumSize(new Dimension(83, 30));
                setMinimumSize(new Dimension(83, 30));
                setAlignmentX(RIGHT_ALIGNMENT);
                setBackground(new Color(0, 113, 149));
                addActionListener(a -> {
                    setText("Loading");
                    new Thread(() -> {
                        if (!AppManager.install(app)) {
                            setText("Update");
                            return;
                        }

                        setText("Source");
                        setBackground(new JButton().getBackground());
                        for (ActionListener listener : getActionListeners()) {
                            removeActionListener(listener);
                        }
                        addActionListener(b -> {
                            try {
                                Desktop.getDesktop().browse(URI.create("https://github.com/" + app.getPath()));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }){{
                        setName(app.getName() + "-dlThread");
                    }}.start();
                });
                dlButton = this;
            }});
            add(new JLabel("<html><b>" + app.getDownload() + "</b> Download</html>"){{setAlignmentX(RIGHT_ALIGNMENT);}});
            add(new JLabel("<html><b>"+ app.getStars() +"</b> Stars</html>"){{setAlignmentX(RIGHT_ALIGNMENT);}});
            setAlignmentX(RIGHT_ALIGNMENT);
        }};
    }


    public JPanel createInstalled(App app) {
        return new JPanel(){{
            add(new JButton("Settings"){{
                setPreferredSize(new Dimension(83, 30));
                setMaximumSize(new Dimension(83, 30));
                setMinimumSize(new Dimension(83, 30));
                setAlignmentX(RIGHT_ALIGNMENT);
                addActionListener(a -> {
                    SettingsManager.showSettings(app);
                });
                dlButton = this;
            }});
            add(new JButton("Source"){{
                setPreferredSize(new Dimension(83, 30));
                setMaximumSize(new Dimension(83, 30));
                setMinimumSize(new Dimension(83, 30));
                setAlignmentX(RIGHT_ALIGNMENT);
                addActionListener(a -> {
                    try {
                        Desktop.getDesktop().browse(URI.create("https://github.com/" + app.getPath()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }});
            add(new JLabel("<html><b>" + app.getDownload() + "</b> Download</html>"){{setAlignmentX(RIGHT_ALIGNMENT);}});
            add(new JLabel("<html><b>"+ app.getStars() +"</b> Stars</html>"){{setAlignmentX(RIGHT_ALIGNMENT);}});
            setAlignmentX(RIGHT_ALIGNMENT);
        }};
    }

    public JPanel createInstallNoConnection(InstalledApp app) {
        return new JPanel(){{
            add(new JButton("Settings"){{
                setPreferredSize(new Dimension(83, 30));
                setMaximumSize(new Dimension(83, 30));
                setMinimumSize(new Dimension(83, 30));
                setAlignmentX(RIGHT_ALIGNMENT);
                addActionListener(a -> {
                    SettingsManager.showSettings(app.app);
                });
                dlButton = this;
            }});
            add(new JButton("Source"){{
                setPreferredSize(new Dimension(83, 30));
                setMaximumSize(new Dimension(83, 30));
                setMinimumSize(new Dimension(83, 30));
                setAlignmentX(RIGHT_ALIGNMENT);
                addActionListener(a -> {
                    try {
                        Desktop.getDesktop().browse(URI.create("https://github.com/" + app.getPath()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }});
//            add(new JLabel("<html><b>?</b> Download</html>"){{setAlignmentX(RIGHT_ALIGNMENT);}});
//            add(new JLabel("<html><b>?</b> Stars</html>"){{setAlignmentX(RIGHT_ALIGNMENT);}});
            setAlignmentX(RIGHT_ALIGNMENT);
        }};
    }
}

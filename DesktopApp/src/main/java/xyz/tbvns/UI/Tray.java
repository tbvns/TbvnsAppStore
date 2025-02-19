package xyz.tbvns.UI;

import lombok.SneakyThrows;
import xyz.tbvns.Apps.Manager.AppListManager;
import xyz.tbvns.Apps.Manager.AppManager;
import xyz.tbvns.Apps.Object.App;
import xyz.tbvns.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Tray {
    public static final List<String> sent = new ArrayList<>();

    @SneakyThrows
    public static void setUp() {
        if (!SystemTray.isSupported()) {
            return;
        }

        TrayIcon icon = new TrayIcon(ImageIO.read(Tray.class.getResource("/Logos/logo.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH)) {{
            setPopupMenu(new PopupMenu("Tbvns's app store"){{
                add(new MenuItem("Show"){{
                    addActionListener(a -> MainWindow.show());
                }});
                add(new MenuItem("Settings"));
                add(new MenuItem("Exit"){{
                    addActionListener(a -> Runtime.getRuntime().exit(0));
                }});
            }});
            setToolTip("Tbvns's app store");
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == 1)
                        MainWindow.show();
                }
            });
        }};
        SystemTray.getSystemTray().add(icon);

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                int availableUpdates = 0;
                String lastUpdateCandidate = "";

                for (App application : AppListManager.listApps()) {
                    if (application.isInstalled() && application.updateAvailable()) {
                        availableUpdates++;
                        lastUpdateCandidate = application.getName(); // Track most recent eligible app
                    }
                }

                if (availableUpdates > 0) {
                    String notificationMessage = availableUpdates == 1
                            ? String.format("An update for %s is available.", lastUpdateCandidate)
                            : String.format("%d updates are available.", availableUpdates);

                    icon.displayMessage(
                            "TBVNS App Store Updater",
                            notificationMessage,
                            TrayIcon.MessageType.INFO
                    );
                }

                Utils.sleep((int) 7.2E6); //That's 2 hours
            }
        }){{
            setName("UpdateNotifier");
            setDaemon(true);
            start();
        }};
    }
}

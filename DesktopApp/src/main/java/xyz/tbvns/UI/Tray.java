package xyz.tbvns.UI;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Tray {
    @SneakyThrows
    public static void setUp() {
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
    }
}

package xyz.tbvns;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import xyz.tbvns.UI.MainWindow;

import javax.imageio.ImageIO;
import java.awt.*;

@Slf4j
public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        TrayIcon icon = new TrayIcon(ImageIO.read(Main.class.getResource("/Logos/logo.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH), "Tbvns's app store");
        icon.setPopupMenu(new PopupMenu("Hello world"));
        SystemTray.getSystemTray().add(icon);
        MainWindow.show();


        if (args.length >= 1 && args[0].equals("autostart")) {
            return;
        }
    }
}
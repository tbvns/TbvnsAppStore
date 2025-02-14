package xyz.tbvns.UI;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public class WindowUtils {
    public static void center(Frame frame) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(screen.width / 2 - frame.getWidth() / 2, screen.height / 2 - frame.getHeight() / 2);
    }

    public enum Icons {
        normal,
        ok,
        warn,
        error
    }

    @SneakyThrows
    public static void setIcon(Frame frame, Icons type) {
        if (type.equals(Icons.normal)) {
            frame.setIconImage(ImageIO.read(WindowUtils.class.getResource("/Logos/logo.png")));
        } else if (type.equals(Icons.ok)) {
            frame.setIconImage(ImageIO.read(WindowUtils.class.getResource("/Logos/logoOk.png")));
        } else if (type.equals(Icons.warn)) {
            frame.setIconImage(ImageIO.read(WindowUtils.class.getResource("/Logos/logoWarn.png")));
        } else if (type.equals(Icons.error)) {
            frame.setIconImage(ImageIO.read(WindowUtils.class.getResource("/Logos/logoError.png")));
        }
    }

    @SneakyThrows
    public static ImageIcon icon(String name) {
        return new ImageIcon(ImageIO.read(WindowUtils.class.getResource("/Icons/" + name + ".png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    }
}

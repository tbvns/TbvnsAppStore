package xyz.tbvns;

import lombok.SneakyThrows;
import xyz.tbvns.Apps.Object.App;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class Utils {
    @SneakyThrows
    public static void sleep(int t) {
        Thread.sleep(t);
    }

    //I did not steal this from deepseek (I obviously did)
    public static BufferedImage convertToBufferedImage(Image image) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid image dimensions");
        }

        // Create ARGB BufferedImage to support transparency
        BufferedImage bufferedImage = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_ARGB
        );

        // Draw image with transparency
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return bufferedImage;
    }

    public static boolean crossContains(Collection<String> l1, Collection<String> l2) {
        boolean value = false;
        for (String s1 : l1) {
            for (String s2 : l2) {
                if (s1.equals(s2)) {
                    value = true;
                    break;
                }
            }
            if (value) {
                break;
            }
        }
        return value;
    }
    
    public enum appSortType {
        download, stars, AZ, ZA
    }
    
    public static List<App> sort(List<App> apps, appSortType type) {
        switch (type) {
            case download -> {
                return apps.stream()
                        .sorted(Comparator.comparing(App::getDownload))
                        .toList();
            }
            case stars -> {
                return apps.stream()
                        .sorted(Comparator.comparingInt(App::getStars))
                        .toList();
            }
            case AZ -> {
                return apps.stream()
                        .sorted(Comparator.comparing(App::getName))
                        .toList();
            }
            default -> {
                List<App> l = apps.stream()
                        .sorted(Comparator.comparing(App::getName))
                        .toList();
                Collections.reverse(l);
                return l;
            }
        }
    }
}

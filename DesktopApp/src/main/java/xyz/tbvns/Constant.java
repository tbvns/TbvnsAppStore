package xyz.tbvns;

import xyz.tbvns.Apps.AppElement;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Constant {
    public static final String serverUrl = "http://localhost:8080";
    public static String mainFolder;
    public static String appFolder;
    static {
        mainFolder = new File(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParent()).getParent();
        File app = new File(mainFolder + "/apps");
        app.mkdirs();
        appFolder = app.getPath();
    }
}

package xyz.tbvns;

import oshi.SystemInfo;

import java.io.File;

public class Constant {
    public static final String serverUrl = "http://localhost:8080";
    public static String mainFolder;
    public static String appFolder;
    public static String javaBin;
    static {
        mainFolder = new File(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParent()).getParent();
        File app = new File(mainFolder + "/apps");
        app.mkdirs();
        appFolder = app.getPath();

        javaBin = mainFolder + "/bin/java/bin/java";
        if (new SystemInfo().getOperatingSystem().getFamily().toLowerCase().contains("win"))
            javaBin += "w.exe";

        if (!new File(javaBin).exists()) {
            System.err.println("ERROR: COULD NOT FIND BUNDLED JAVA. SWITCHING TO SYSTEM DEFAULT");
            javaBin = "java";
        }
    }
}

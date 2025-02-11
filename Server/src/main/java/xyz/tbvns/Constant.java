package xyz.tbvns;

import java.io.File;

public class Constant {
    public static String logosPath;
    static {
        File logoFolder = new File(new File(Constant.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParent() + "/Images");
        if (!logoFolder.exists()) {
            logoFolder.mkdirs();
        }
        logosPath = logoFolder.getPath();
    }
}

package xyz.tbvns.Configs;

import xyz.tbvns.Apps.Object.InstalledApp;
import xyz.tbvns.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DownloadedApps implements Config {
    public static InstalledApp[] list = new InstalledApp[]{};

    public static void add(InstalledApp app) {
        List<InstalledApp> installed = new ArrayList<>(Arrays.asList(list));
        installed.add(app);
        list = installed.toArray(new InstalledApp[0]);
    }

    public static void remove(InstalledApp app) {
        List<InstalledApp> installed = new ArrayList<>(Arrays.asList(list));
        installed.remove(app);
        list = installed.toArray(new InstalledApp[0]);
    }
}

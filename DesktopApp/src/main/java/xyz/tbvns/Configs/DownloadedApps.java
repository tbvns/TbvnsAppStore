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
        if (app == null || app.getPath() == null) return;

        final String targetPath = app.getPath();

        // Check if path exists in the list
        boolean exists = Arrays.stream(list)
                .anyMatch(installedApp -> targetPath.equals(installedApp.getPath()));

        if (exists) {
            // Remove all entries with matching path (safer than equals())
            list = Arrays.stream(list)
                    .filter(installedApp -> !targetPath.equals(installedApp.getPath()))
                    .toArray(InstalledApp[]::new);
        }
    }
}

package xyz.tbvns.Apps.Launcher;

import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import xyz.tbvns.Apps.Object.InstalledApp;
import xyz.tbvns.Configs.DownloadedApps;
import xyz.tbvns.Utils;

import java.util.List;

import static xyz.tbvns.Utils.sleep;

public class ProcessChecker {
    private static boolean check;
    private static Runnable checker = () -> {
        while (check) {
            SystemInfo info = new SystemInfo();
            List<String> processes = info
                    .getOperatingSystem()
                    .getProcesses()
                    .stream()
                    .map(OSProcess::getName)
                    .toList();

            try {
                for (InstalledApp app : DownloadedApps.list) {
                    if (Utils.crossContains(app.getSettings().getAutoExecList(), processes) && !AppLauncher.isRunning(app)) {
                        AppLauncher.launch(app);
                    } else if (!Utils.crossContains(app.getSettings().getAutoExecList(), processes) && AppLauncher.isRunning(app)) {
                        AppLauncher.kill(app);
                    }
                }
            } catch (Exception e) {}
            System.out.println(DownloadedApps.list.length);
            sleep(1000);
        }
    };

    public static void start() {
        if (check) return;

        check = true;
        new Thread(checker){{
            setName("ProcessChecker");
            setDaemon(true);
        }}.start();
    }

    public static void stop() {
        check = false;
    }
}

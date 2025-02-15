package xyz.tbvns.Apps.Launcher;

import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import xyz.tbvns.Apps.Object.InstalledApp;
import xyz.tbvns.Configs.DownloadedApps;
import xyz.tbvns.ErrorHandler;
import xyz.tbvns.Utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static xyz.tbvns.Constant.javaBin;
import static xyz.tbvns.Utils.sleep;

public class ProcessChecker {
    private static boolean check;
    private static final Runnable checker = () -> {
        while (check) {
            SystemInfo info = new SystemInfo();
            List<String> processes = info
                    .getOperatingSystem()
                    .getProcesses()
                    .stream()
                    .map(OSProcess::getName)
                    .toList();

            try {
                for (InstalledApp app :
                        Arrays.stream(DownloadedApps.list)
                                .filter(app -> app.getSettings().isAutoExec())
                                .toList())
                {
                    if (Utils.crossContains(app.getSettings().getAutoExecList(), processes) && !AppLauncher.isRunning(app)) {
                        AppLauncher.launch(app);
                    } else if (!Utils.crossContains(app.getSettings().getAutoExecList(), processes) && AppLauncher.isRunning(app)) {
                        AppLauncher.kill(app);
                    }
                }
            } catch (Exception ignored) {}
            sleep(1000);
        }
    };

    public static void start() {
        if (check) return;

        if (!new File(javaBin).exists()) {
            ErrorHandler.warn("""
                    Bundled Java runtime not found. Falling back to system default.
                    This may be caused by an incorrect installation of Tbvns's App Store.""");
            javaBin = "java";
        }

        check = true;
        new Thread(checker){{
            setName("ProcessChecker");
            start();
        }};
    }

    public static void stop() {
        check = false;
    }
}

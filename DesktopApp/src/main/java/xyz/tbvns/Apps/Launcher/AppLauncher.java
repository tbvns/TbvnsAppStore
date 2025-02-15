package xyz.tbvns.Apps.Launcher;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import xyz.tbvns.Apps.Object.InstalledApp;
import xyz.tbvns.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static xyz.tbvns.Utils.sleep;

@Slf4j
public class AppLauncher {
    private static List<InstalledApp> running = new ArrayList<>();
    private static HashMap<InstalledApp, Process> hashMap = new HashMap<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Killing every processes...");
            hashMap.forEach((k, v) -> {
                v.destroy();
            });
        }, "ShutdownJook-Launcher"));
    }

    @SneakyThrows
    public static void launch(InstalledApp app) {
        log.info("Launching {}", app.getName());
        Process process = Runtime.getRuntime().exec(new String[]{Constant.javaBin, "-jar", app.getBin().getPath(), "appStore"});
        running.add(app);
        hashMap.put(app, process);

        new Thread(() -> {
            while (process.isAlive()) sleep(100);
            running.remove(app);
            hashMap.remove(app);
        }){{
            setDaemon(true);
            setName(app.getName() + "-checkThread");
            setPriority(MIN_PRIORITY);
        }}.start();
    }

    public static void kill(InstalledApp app) {
        Process process = hashMap.get(app);
        process.destroy();
    }

    public static boolean isRunning(InstalledApp app) {
        return running.contains(app);
    }
}

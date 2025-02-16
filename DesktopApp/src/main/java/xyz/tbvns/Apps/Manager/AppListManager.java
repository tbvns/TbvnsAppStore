package xyz.tbvns.Apps.Manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import xyz.tbvns.Apps.Object.App;
import xyz.tbvns.Apps.Object.InstalledApp;
import xyz.tbvns.Configs.DownloadedApps;
import xyz.tbvns.ErrorHandler;
import xyz.tbvns.UI.AppElement;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class AppListManager {
    public static final HashMap<String, AppElement> appElementHashMap = new HashMap<>();
    private static List<App> apps = new ArrayList<>();

    //TODO: implement caching
    @SneakyThrows
    public static Collection<AppElement> retrieveApps() {
        try {
            InputStream in = new URL("http://localhost:8080/apps/list").openStream();
            String json = IOUtils.toString(in);
            ObjectMapper mapper = new ObjectMapper();
            apps = mapper.readValue(json, new TypeReference<List<App>>() {});
            for (App app : apps) {
                AppElement element = new AppElement(app);
                appElementHashMap.put(app.getPath(), element);
            }
            log.info("Retrieved {} app(s) from the server", appElementHashMap.keySet().size());
        } catch (Exception e) {
            ErrorHandler.handle(e, false);
        }

        for (InstalledApp app : DownloadedApps.list) {
            if (!apps.stream().map(App::getPath).toList().contains(app.getPath())) {
                AppElement element = new AppElement(app);
                appElementHashMap.put(app.getPath(), element);
                apps.add(app.app);
            }
        }

        return appElementHashMap.values();
    }

    public static List<App> listApps() {
        return apps;
    }

    public static void markUninstalled(App app) {
        AppElement element = appElementHashMap.get(app.getPath());
        JButton button = element.getDlButton();
        button.setText("Install");
        for (ActionListener listener : button.getActionListeners()) {
            button.removeActionListener(listener);
        }

        button.addActionListener(a -> {
            button.setText("Loading");
            new Thread(() -> {
                AppManager.install(app);
                button.setText("Settings");
                for (ActionListener listener : button.getActionListeners()) {
                    button.removeActionListener(listener);
                }
                button.addActionListener(b -> {
                    SettingsManager.showSettings(app);
                });
            }){{
                setName(app.getName() + "-dlThread");
            }}.start();
        });
    }
}

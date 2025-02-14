package xyz.tbvns.Apps.Manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import xyz.tbvns.Apps.Object.App;
import xyz.tbvns.ErrorHandler;
import xyz.tbvns.UI.AppElement;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class AppListManager {
    private static final HashMap<App, AppElement> appElementHashMap = new HashMap<>();

    //TODO: implement caching
    @SneakyThrows
    public static Collection<AppElement> retrieveApps() {
        //TODO: add caching to not crash if the server is unavailable
        try {
            InputStream in = new URL("http://localhost:8080/apps/list").openStream();
            String json = IOUtils.toString(in);
            ObjectMapper mapper = new ObjectMapper();
            List<App> apps = mapper.readValue(json, new TypeReference<List<App>>() {});
            for (App app : apps) {
                AppElement element = new AppElement(app);
                appElementHashMap.put(app, element);
            }
            System.out.println(json);
        } catch (Exception e) {
            ErrorHandler.handle(e, true);
        }
        return appElementHashMap.values();
    }

    public static List<App> listApps() {
        return appElementHashMap.keySet().stream().toList();
    }

    public static void markUninstalled(App app) {
        AppElement element = appElementHashMap.get(app);
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
            }).start();
        });
    }
}

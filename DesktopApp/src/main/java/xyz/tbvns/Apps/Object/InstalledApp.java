package xyz.tbvns.Apps.Object;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import xyz.tbvns.Apps.Manager.AppListManager;
import xyz.tbvns.Apps.Manager.AppManager;
import xyz.tbvns.Constant;

import java.io.File;
import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InstalledApp {
    private int id;
    private String path;
    private String name;
    public App app;

    public File getConfigFile() {
        return new File(Constant.appFolder + "/" + path + "/app.json");
    }
    public File getFolder() {
        return new File(Constant.appFolder + "/" + path);
    }
    public File getBin() {
        return new File(Constant.appFolder + "/" + path + "/app.jar");
    }

    @SneakyThrows
    public AppSettings getSettings() {
        try {
            AppListManager.listApps().stream()
                    .filter(app -> path.equals(app.getPath()))
                    .findFirst()
                    .ifPresent(app -> {
                        try {writeAppSettingsIfNeeded(app);}
                        catch (IOException ignored) {}
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ObjectMapper().readValue(getConfigFile(), AppSettings.class);
    }

    // Extracted method
    private void writeAppSettingsIfNeeded(App app) throws IOException {
        if (!getConfigFile().exists()) {
            new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(getConfigFile(),
                            new AppSettings(
                                    app.getPath(),
                                    app.getAutoExec() != null && !app.getAutoExec().isEmpty(),
                                    app.getAutoExec()
                            ));
        }
    }
}

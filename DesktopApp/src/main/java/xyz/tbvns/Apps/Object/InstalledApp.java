package xyz.tbvns.Apps.Object;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import xyz.tbvns.Constant;

import java.io.File;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InstalledApp {
    private int id;
    private String path;
    private String name;

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
        return new ObjectMapper().readValue(getConfigFile(), AppSettings.class);
    }
}

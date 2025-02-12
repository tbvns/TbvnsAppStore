package xyz.tbvns.Apps;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.tbvns.Configs.DownloadedApps;
import xyz.tbvns.Constant;

import java.io.File;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class App {
    private int id;
    private String name;
    private String path;
    private String file;
    private List<String> autoExec;

    public boolean isInstalled() {
        return DownloadedApps.list.contains(path);
    }
    public File getConfigFile() {
        return new File(Constant.appFolder + "/" + path + "/app.json");
    }
    public File getFolder() {
        return new File(Constant.appFolder + "/" + path);
    }
}

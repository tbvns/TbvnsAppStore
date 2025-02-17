package xyz.tbvns.Apps.Object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import xyz.tbvns.Api.GitRepoInfo;
import xyz.tbvns.Api.Github;
import xyz.tbvns.Configs.DownloadedApps;
import xyz.tbvns.Constant;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class App {
    private int id;
    private String name;
    private String path;
    private String file;
    private int download;
    private String[] autoExec = new String[]{};
    private String category;
    private String[] tags;

    @JsonIgnore
    private int stars;
    @JsonIgnore
    private String desc;

    public boolean isInstalled() {
        for (InstalledApp installedApp : DownloadedApps.list) {
            if (installedApp.getId() == id) {
                return true;
            }
        }
        return false;
    }
    public File getConfigFile() {
        return new File(Constant.appFolder + "/" + path + "/app.json");
    }
    public File getFolder() {
        return new File(Constant.appFolder + "/" + path);
    }

    public InstalledApp asInstalledApp() {
        return new InstalledApp(id, path, name, desc, this);
    }

    @SneakyThrows
    public Image grabImage() {
        return ImageIO.read(new URL(Constant.serverUrl + "/apps/logo?id=" + id)).getScaledInstance(100, 100, Image.SCALE_SMOOTH);
    }

    public void retrievePublicInfo() {
        GitRepoInfo info = Github.getInfo(path);
        stars = info.getStars();
        desc = info.getDesc();
    }
}

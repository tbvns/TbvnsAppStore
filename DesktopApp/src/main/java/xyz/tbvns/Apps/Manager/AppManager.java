package xyz.tbvns.Apps.Manager;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.tbvns.Apps.Object.App;
import xyz.tbvns.Apps.Object.InstalledApp;
import xyz.tbvns.Configs.DownloadedApps;
import xyz.tbvns.Constant;
import xyz.tbvns.EZConfig;
import xyz.tbvns.Utils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppManager {
     @SneakyThrows
    public static void install(App app) {
        String apiUrl = String.format("https://api.github.com/repos/" + app.getPath() + "/releases/latest");
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.connect();

         int responseCode = connection.getResponseCode();
         if (responseCode != HttpURLConnection.HTTP_OK) {
             throw new IOException("Failed to fetch release data from GitHub: " + responseCode);
         }

         String json = new String(connection.getInputStream().readAllBytes());
         JSONObject jsonResponse = new JSONObject(json);
         JSONArray assets = jsonResponse.getJSONArray("assets");

         File appFolder = new File(Constant.appFolder + "/" + app.getPath());
         appFolder.mkdirs();

         for (int i = 0; i < assets.length(); i++) {
             JSONObject object = assets.getJSONObject(i);
             if (object.getString("name").equals(app.getFile())) {
                 String downloadPath = object.getString("browser_download_url");
                 System.out.println(downloadPath);
                 FileUtils.copyToFile(new URL(downloadPath).openStream(), new File(appFolder.getPath() + "/app.jar"));
                 ImageIO.write(Utils.convertToBufferedImage(app.grabImage()), "png", new File(app.getFolder() + "/logo.png"));
                 DownloadedApps.add(app.asInstalledApp());
                 EZConfig.save();
                 break;
             }
         }
    }

    @SneakyThrows
    public static void uninstall(App app) {
         if (app.isInstalled()) {
             FileUtils.deleteDirectory(app.getFolder());
             DownloadedApps.remove(app.asInstalledApp());
             AppListManager.update(app);
         }
    }
}

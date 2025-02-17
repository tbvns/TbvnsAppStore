package xyz.tbvns.Apps.Manager;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import xyz.tbvns.Apps.Object.App;
import xyz.tbvns.Configs.DownloadedApps;
import xyz.tbvns.Constant;
import xyz.tbvns.EZConfig;
import xyz.tbvns.ErrorHandler;
import xyz.tbvns.Utils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

@Slf4j
public class AppManager {
    public static boolean install(App app) {
        log.info("Installing {}", app.getName());
        try {
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
                    FileUtils.copyToFile(new URL(downloadPath).openStream(), new File(appFolder.getPath() + "/app.jar"));

                    try {
                        ImageIO.write(Utils.convertToBufferedImage(app.grabImage()), "png", new File(app.getFolder() + "/logo.png"));
                    } catch (Exception e) {
                        ErrorHandler.warn("Could not download icon: " + e.getMessage());
                    }

                    //TODO: rate limiter currently filters for every id and not per id
                    URL url = new URL(Constant.serverUrl + "/apps/download?id=" + app.getId());
                    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                        HttpPost httpPost = new HttpPost(String.valueOf(url));
                        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                            int statusCode = response.getCode();
                            if (statusCode != 200) log.warn("Error while sending download: status code {}", statusCode);
                        }
                    } catch (Exception e) {
                        ErrorHandler.handle(e, false);
                    }

                    DownloadedApps.add(app.asInstalledApp());
                    EZConfig.save();
                    break;
                }
            }
            return true;
        } catch (Exception e) {
            ErrorHandler.handle(e, false);
            return false;
        }
    }

    @SneakyThrows
    public static void uninstall(App app) {
        log.info("Uninstalling {}", app.getName());
         if (app.isInstalled()) {
             FileUtils.deleteDirectory(app.getFolder());
             DownloadedApps.remove(app.asInstalledApp());
             EZConfig.save();
             AppListManager.markUninstalled(app);
         }
    }
}

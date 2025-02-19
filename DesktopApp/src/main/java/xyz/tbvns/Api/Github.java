package xyz.tbvns.Api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import xyz.tbvns.Apps.Object.App;
import xyz.tbvns.Utils;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class Github { //TODO: We are rate limited to 60 request per hours. We need to cache data to prevent rate limiting
    private static final HashMap<String, GitRepoInfo> info = new HashMap<>();
    private static final HashMap<String, String> latestTags = new HashMap<>();

    static {
        new Thread(() -> {
            while (true) {
                info.clear();
                latestTags.clear();
                log.info("Github cache cleared.");
                Utils.sleep((int) 7.2E6); //That's 2 hours
            }
        }){{
            setName("CacheCleaner");
            setDaemon(true);
            start();
        }};
    }

    @SneakyThrows
    public static GitRepoInfo getInfo(String repo) {
        if (info.containsKey(repo)) {
            return info.get(repo);
        }

        String url = "https://api.github.com/repos/" + repo;
        InputStream in = new URL(url).openStream();
        String json = IOUtils.toString(in);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);
        int stargazersCount = rootNode.get("stargazers_count").asInt();
        String desc = rootNode.get("description").asText();
        info.put(repo, new GitRepoInfo(stargazersCount, desc));
        return new GitRepoInfo(stargazersCount, desc);
    }

    @SneakyThrows
    public static String getLatestTag(App app) {
        if (latestTags.containsKey(app.getPath())) {
            return latestTags.get(app.getPath());
        }

        String url = "https://api.github.com/repos/" + app.getPath() + "/releases/latest";
        InputStream in = new URL(url).openStream();
        String json = IOUtils.toString(in);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);
        String tag = rootNode.get("tag_name").asText();
        latestTags.put(app.getPath(), tag);
        return tag;
    }
}

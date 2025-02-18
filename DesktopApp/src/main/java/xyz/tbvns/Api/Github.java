package xyz.tbvns.Api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Github { //TODO: We are rate limited to 60 request per hours. We need to cache data to prevent rate limiting
    private static HashMap<String, GitRepoInfo> info = new HashMap<>();
    
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
}

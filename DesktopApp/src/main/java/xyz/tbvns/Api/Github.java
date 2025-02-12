package xyz.tbvns.Api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;

public class Github {
    @SneakyThrows
    public static GitRepoInfo getInfo(String repo) {
        String url = "https://api.github.com/repos/" + repo;
        InputStream in = new URL(url).openStream();
        String json = IOUtils.toString(in);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);
        int stargazersCount = rootNode.get("stargazers_count").asInt();
        String desc = rootNode.get("description").asText();
        return new GitRepoInfo(stargazersCount, desc);
    }
}

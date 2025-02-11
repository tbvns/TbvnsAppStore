package xyz.tbvns.Apps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class AppListManager {
    private static final HashMap<App, AppElement> appElementHashMap = new HashMap<>();

    @SneakyThrows
    public static Collection<AppElement> retrieveApps() {
        InputStream in = new URL("http://localhost:8080/apps/list").openStream();
        String json = IOUtils.toString(in);
        ObjectMapper mapper = new ObjectMapper();
        List<App> apps = mapper.readValue(json, new TypeReference<List<App>>() {});
        for (App app : apps) {
            AppElement element = new AppElement(app);
            appElementHashMap.put(app, element);
        }
        System.out.println(json);
        return appElementHashMap.values();
    }
}

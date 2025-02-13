package xyz.tbvns.Apps;

import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.tbvns.Configs.AppsConfig;
import xyz.tbvns.Constant;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class AppsController {
    @GetMapping("/apps/list")
    public static List<App> getApps() {
        return AppsConfig.apps;
    }

    @SneakyThrows
    @GetMapping(value = "/apps/logo", produces = MediaType.IMAGE_PNG_VALUE)
    public static ResponseEntity<Resource> getLogo(@RequestParam("id") int id) {
        try {
            Resource res = new ByteArrayResource(Files.readAllBytes(Paths.get(
                    Constant.logosPath + "/" + id + ".png"
            )));
            return new ResponseEntity<>(res, HttpStatusCode.valueOf(200));
        } catch (NoSuchFileException e) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }
    }

}

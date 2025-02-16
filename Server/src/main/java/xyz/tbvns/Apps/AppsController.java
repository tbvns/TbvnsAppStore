package xyz.tbvns.Apps;

import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.tbvns.Configs.AppsConfig;
import xyz.tbvns.Constant;
import xyz.tbvns.EZConfig;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class AppsController {
    @GetMapping("/apps/list")
    public static App[] getApps() {
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

    @SneakyThrows
    @PostMapping(value = "/apps/download")
    public static ResponseEntity<String> download(@RequestParam("id") int id) {
        for (App app : AppsConfig.apps) {
            if (app.getId() == id) {
                app.setDownload(app.getDownload() + 1);
                EZConfig.save();
                return new ResponseEntity<>("Ok", HttpStatusCode.valueOf(200));
            }
        }
        return new ResponseEntity("Not found", HttpStatusCode.valueOf(404));
    }

}

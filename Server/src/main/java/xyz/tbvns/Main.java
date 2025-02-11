package xyz.tbvns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws Exception {
        EZConfig.registerClassPath("xyz.tbvns.Configs");
        EZConfig.load();
        EZConfig.save();

        SpringApplication.run(Main.class, args);
    }
}

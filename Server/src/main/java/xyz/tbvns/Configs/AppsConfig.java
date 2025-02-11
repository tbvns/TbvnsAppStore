package xyz.tbvns.Configs;

import xyz.tbvns.Apps.App;
import xyz.tbvns.Config;

import java.util.ArrayList;
import java.util.List;

public class AppsConfig implements Config {
    public static List<App> apps = new ArrayList<>() {{
        add(new App(
                0,
                "Ghost Trainer",
                "tbvns/GhostTrainer",
                "GhostTrainer.jar",
                new ArrayList<>() {{
                    add("valorant");
                    add("cs2");
                }}
        ));
    }};
}

package xyz.tbvns.Configs;

import xyz.tbvns.Apps.App;
import xyz.tbvns.Config;

import java.util.ArrayList;
import java.util.List;

public class AppsConfig implements Config {
    public static App[] apps = new App[] {
            new App(
                    0,
                    "Ghost Trainer",
                    "tbvns/GhostTrainer",
                    "GhostTrainer.jar",
                    0,
                    new String[]{
                            "VALORANT"
                    }
            )
    };
}

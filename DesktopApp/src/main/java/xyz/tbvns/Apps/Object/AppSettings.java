package xyz.tbvns.Apps.Object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AppSettings {
    private String path;
    private boolean autoExec;
    private List<String> autoExecList;

    public AppSettings(App app) {
        this.path = app.getPath();
        autoExec = !app.getAutoExec().isEmpty();
        autoExecList = app.getAutoExec();
    }
}

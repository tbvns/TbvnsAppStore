package xyz.tbvns.Apps;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class App {
    private int id;
    private String name;
    private String path;
    private String file;
    private int download;
    private String[] autoExec;
    private String category;
    private String[] tags;
}

package xyz.tbvns;

import lombok.SneakyThrows;

public class Utils {
    @SneakyThrows
    public static void sleep(int t) {
        Thread.sleep(t);
    }
}

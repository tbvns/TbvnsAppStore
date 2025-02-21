package xyz.tbvns;

import com.formdev.flatlaf.FlatDarculaLaf;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import xyz.tbvns.Apps.Launcher.ProcessChecker;
import xyz.tbvns.UI.MainWindow;
import xyz.tbvns.UI.Tray;

import javax.imageio.ImageIO;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        FlatDarculaLaf.setup();

        Communicator.checkInstances();
        Communicator.startServer();

        Utils.sleep(1000);

        if (!(args.length >= 1 && args[0].equals("autostart"))) MainWindow.show();


        ProcessChecker.start();
        Tray.setUp();

        log.info("Server address is {}", Constant.serverUrl);
        log.info("Install folder is {}", Constant.mainFolder);
    }
}
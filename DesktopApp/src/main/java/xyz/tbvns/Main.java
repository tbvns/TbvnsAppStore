package xyz.tbvns;

import com.formdev.flatlaf.FlatDarculaLaf;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import xyz.tbvns.Apps.Launcher.ProcessChecker;
import xyz.tbvns.UI.MainWindow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.util.List;

@Slf4j
public class Main {


    @SneakyThrows
    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        Communicator communicator = new Communicator(Constant.mainFolder + "/queue");

        if (isAlreadyRunning()) {
            communicator.sendFlag();
            Runtime.getRuntime().exit(0);
            return;
        }
        if (!(args.length >= 1 && args[0].equals("autostart"))) MainWindow.show();

        communicator.start();
        ProcessChecker.start();

        log.info("Server address is {}", Constant.serverUrl);
        log.info("Install folder is {}", Constant.mainFolder);


    }

    public static boolean isAlreadyRunning() {
        List<OSProcess> lsp = new SystemInfo().getOperatingSystem().getProcesses();
        int count = 0;
        for (OSProcess osProcess : lsp) {
            if (osProcess.getCommandLine().equals(new SystemInfo().getOperatingSystem().getCurrentProcess().getCommandLine())) {
                count++;
            }
        }
        if (count > 1) {
            System.out.println("Already running !");
            return true;
        }
        return false;
    }

    //Launch cmd: java --add-exports=java.base/jdk.internal.ref=ALL-UNNAMED --add-exports=java.base/sun.nio.ch=ALL-UNNAMED --add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED --add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED -jar DesktopApp-1.0-SNAPSHOT.jar
}
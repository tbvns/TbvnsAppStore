package xyz.tbvns;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;
import xyz.tbvns.UI.MainWindow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Random;

@Slf4j
public class Communicator {

    public static void checkInstances() {
        try {
            new Client(new URI("ws://localhost:" + readInstancePort())){{
                connect();
                while (!isClosed() || getReadyState() == ReadyState.NOT_YET_CONNECTED || isClosing());
            }};
        } catch (FileNotFoundException | URISyntaxException ignored){}
        catch (IOException exception) {
            ErrorHandler.handle(exception, true);
        }
    }

    public static void startServer() {
        int port = findPort();
        new Server(new InetSocketAddress(port)){{
            setDaemon(true);
            start();
        }};
        writeInstancePort(port);
    }

    private static class Client extends WebSocketClient {
        public Client(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            send("show");
            log.info("Already running !");
            Runtime.getRuntime().exit(0);
        }

        @Override
        public void onMessage(String message) {
            log.info("Received message: {}", message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            log.info("Websocket connection closed.");
        }

        @Override
        public void onError(Exception ex) {}
    }

    private static class Server extends WebSocketServer {
        public Server(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            log.info("A websocket connection was opened.");
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            log.info("A websocket connection was closed.");
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            if (message.equals("show")) {
                MainWindow.show();
            }
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            ErrorHandler.handle(ex, false);
        }

        @Override
        public void onStart() {log.info("Websocket server started.");}

    }

    public static int readInstancePort() throws IOException {
        File file = new File(Constant.mainFolder + "/instance.data");
        if (!file.exists()) throw new FileNotFoundException();
        String port = FileUtils.readFileToString(file, Charset.defaultCharset());
        return Integer.parseInt(port);
    }

    @SneakyThrows
    public static void writeInstancePort(int port) {
        File file = new File(Constant.mainFolder + "/instance.data"){{
            if (!exists()) {
                createNewFile();
            } else {
                delete();
                createNewFile();
            }
            deleteOnExit();
        }};
        FileUtils.write(file, String.valueOf(port), Charset.defaultCharset(), false);
    }

    public static int findPort() {
        while (true) {
            Random random = new Random();
            int port = random.nextInt(20000, 30000);
            if (available(port)) return port;
        }
    }

    public static boolean available(int port) {
        if (port < 20000 || port > 30000) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }
}


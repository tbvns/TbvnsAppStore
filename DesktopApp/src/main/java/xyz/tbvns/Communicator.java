package xyz.tbvns;

import lombok.extern.slf4j.Slf4j;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.apache.commons.io.FileUtils;
import xyz.tbvns.UI.MainWindow;

import java.io.File;
import java.io.IOException;

@Slf4j
public class Communicator implements Runnable {
    private final String queuePath;
    private volatile boolean running = true;

    public Communicator(String queuePath) {
        this.queuePath = queuePath;
    }

    // Method to send a flag message
    public void sendFlag() {
        try (ChronicleQueue queue = SingleChronicleQueueBuilder.binary(queuePath).build()) {
            ExcerptAppender appender = queue.createAppender();
            appender.writeDocument(w -> w.write("flag").text("true"));
            appender.close();
        }
    }

    // Receiver thread logic
    @Override
    public void run() {
        try (ChronicleQueue queue = SingleChronicleQueueBuilder.binary(queuePath).build()) {
            ExcerptTailer tailer = queue.createTailer();
            while (running) {
                boolean found = tailer.readDocument(doc -> {
                    String message = doc.read("flag").text();
                    if (message != null && !message.isEmpty()) {
                        MainWindow.show();
                    }
                });

                if (!found) {
                    Thread.sleep(50);
                }
            }

            tailer.close();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void start() {
        new Thread(this).start();
        Runtime.getRuntime().addShutdownHook(new Thread(this::clearQueue){{
            setName("ShutdownHook");
        }});
    }

    // Stop the receiver gracefully
    public void stop() {
        running = false;
    }

    public void clearQueue() {
        log.info("Deleting queue...");
        stop();
        Utils.sleep(200);
        File file = new File(queuePath);
        if (file.exists()) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void clearRestart() {
        clearQueue();
        start();
    }
}


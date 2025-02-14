package xyz.tbvns;

import lombok.SneakyThrows;
import xyz.tbvns.Apps.Launcher.ProcessChecker;
import xyz.tbvns.UI.WindowUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class ErrorHandler {
    @SneakyThrows
    public static void handle(Exception e, boolean isFatal) {
        if (isFatal) {
            ProcessChecker.stop();
            Arrays.stream(JFrame.getFrames()).forEach(Frame::dispose);
        }

        JFrame frame = new JFrame("Error: " + e.getMessage());
        frame.setSize(800, 400);
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel errorLabel = new JLabel("Error: " + e.getMessage()){{
            setIcon(new ImageIcon(ImageIO.read(ErrorHandler.class.getResource("/Icons/error.png")).getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
            setFont(getFont().deriveFont(20F));
            if (isFatal) {
                setText("Fatal error: " + e.getMessage());
            }
        }};
        panel.add(errorLabel);

        JTextArea area = new JTextArea(){{
            setEditable(false);
            setRows(10);
        }};
//        Arrays.stream(e.getStackTrace()).map(el -> el.toString() + "\n").forEach(area::append);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(stream));
        area.append(new String(stream.toByteArray()));

        panel.add(new JScrollPane(area){{setPreferredSize(new Dimension(300, 300));}});

        JButton button = new JButton("Close") {{
            addActionListener(a -> {
                if (isFatal) {
                    Runtime.getRuntime().exit(1);
                }
                frame.dispose();
            });

            if (isFatal) {
                setBackground(Color.RED);
                setText("End program");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            }
            setAlignmentX(0);
        }};

        panel.add(button);
        frame.setContentPane(panel);
        WindowUtils.center(frame);
        frame.setVisible(true);


        if (isFatal) {
            Thread.currentThread().join();
        }
    }

    public static void warn(String message) {
        new Thread(() -> {
            JOptionPane.showMessageDialog(new Frame(),
                    message,
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }).start();
    }
}

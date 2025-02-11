package xyz.tbvns.Apps;

import lombok.SneakyThrows;
import xyz.tbvns.Constant;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class AppElement extends JPanel {
    @SneakyThrows
    public AppElement(App app) {
        setBorder(new LineBorder(Color.DARK_GRAY, 1));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        Image logo = ImageIO.read(AppElement.class.getResourceAsStream("/Icons/broken.png")).getScaledInstance(100, 100, Image.SCALE_SMOOTH);

        URL url = new URL(Constant.serverUrl + "/apps/logo?id=" + app.getId());
        try {
            logo = ImageIO.read(url).getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel image = new JLabel();
        image.setSize(20, 20);
        image.setIcon(new ImageIcon(logo));
        add(image);

        JPanel infoPanel = new JPanel();
        String desc = "Description !";
        JLabel infoLabel = new JLabel("<html><b>" + app.getName() + "</b><br><div style='width: 100px; word-wrap: break-word; white-space: normal;'>" + desc + "</div></html>");        infoPanel.add(infoLabel);
        add(infoPanel);

        JPanel rightPanel = new JPanel(){{
            add(new JButton("Install"){{setAlignmentX(RIGHT_ALIGNMENT);}});
            add(new JButton("Source"){{setAlignmentX(RIGHT_ALIGNMENT);}});
            add(new JLabel("<html><b>105</b> Download</html>"){{setAlignmentX(RIGHT_ALIGNMENT);}});
            add(new JLabel("<html><b>5</b> Stars</html>"){{setAlignmentX(RIGHT_ALIGNMENT);}});
            setAlignmentX(RIGHT_ALIGNMENT);
        }};
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        add(rightPanel);

        setSize(280, 100);
    }
}

package flappy_birds;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Ground {

    private BufferedImage groundImage;

    private int x1, y1;
    private int x2, y2;

    public Ground() {
        try {
            groundImage = ImageIO.read(getClass().getResourceAsStream("/Assets/ground.png"));
        } catch (IOException e) {

        }

        x1 = 0;
        y1 = 500;
        x2 = x1 + 830;
        y2 = 500;
    }

    public void Update() {
        x1 -=4;
        x2 -=4;

        if (x2 < 0) x1 = x2 + 830;
        if (x1 < 0) x2 = x1 + 830;
    }

    public void Paint(Graphics2D g2) {
        g2.drawImage(groundImage, x1, y1, null);
        g2.drawImage(groundImage, x2, y2, null);

    }

    public int getYGround() {
        return y1;
    }
}

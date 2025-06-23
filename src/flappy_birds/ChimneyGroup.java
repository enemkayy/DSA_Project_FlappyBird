package flappy_birds;

import pkg2dgamesframework.QueueList;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ChimneyGroup {

    // Queue to store and manage chimneys
    private QueueList<Chimney> chimneys;

    private BufferedImage chimneyImage1, chimneyImage2;

    public static int SIZE = 6;

    private int topChimneyY = -350;
    private int bottomChimneyY = 200;
    private int spacing = 287;

    public int getRandomY() {
        Random random = new Random();
        int a = random.nextInt(7);
        return a * 30;
    }

    public ChimneyGroup() {

        // Load chimney images for drawing top and bottom pipes
        try {
            chimneyImage1 = ImageIO.read(getClass().getResourceAsStream("/Assets/chimney.png"));
            chimneyImage2 = ImageIO.read(getClass().getResourceAsStream("/Assets/chimney2.png"));
        } catch (IOException e) {
        }

        chimneys = new QueueList<Chimney>();

        Chimney cn;

        // Create 3 pairs of chimneys (top and bottom)
        for (int i = 0; i < SIZE / 2; i++) {

            int deltaY = getRandomY();

            // Bottom chimney
            cn = new Chimney(830 + i * spacing, bottomChimneyY + deltaY, 74, 400);
            chimneys.push(cn);

            // Top chimney (positioned above the screen)
            cn = new Chimney(830 + i * spacing, topChimneyY + deltaY + 15, 74, 400);
            chimneys.push(cn);
        }
    }

    public void resetChimneys() {
        chimneys = new QueueList<Chimney>();

        Chimney cn;

        // Create 3 pairs of chimneys (top and bottom)
        for (int i = 0; i < SIZE / 2; i++) {

            int deltaY = getRandomY();

            // Bottom chimney
            cn = new Chimney(830 + i * spacing, bottomChimneyY + deltaY, 74, 400);
            chimneys.push(cn);

            // Top chimney (positioned above the screen)
            cn = new Chimney(830 + i * spacing, topChimneyY + deltaY + 15, 74, 400);
            chimneys.push(cn);
        }
    }

    public Chimney getChimney(int i) {
        return chimneys.get(i);
    }

    // Update chimney positions and recycle them when they go off screen
    public void update() {

        // Update position of all chimneys
        for (int i = 0; i < SIZE; i++) {
            chimneys.get(i).update();
        }

        // If the first chimney has moved off screen, reuse it
        if (chimneys.get(0).getPosX() < -74) {

            int deltaY = getRandomY();

            Chimney cn;

            // Reuse bottom chimney
            cn = chimneys.pop(); // Remove from front
            cn.setPosX(chimneys.get(4).getPosX() + spacing); // Reset position
            cn.setPosY(bottomChimneyY + deltaY);
            cn.setIsBehindBird(false);
            chimneys.push(cn); // Add to end

            // Reuse top chimney
            cn = chimneys.pop();
            cn.setPosX(chimneys.get(4).getPosX());
            cn.setPosY(topChimneyY + deltaY + 15);
            cn.setIsBehindBird(false);
            chimneys.push(cn);
        }
    }

    public void paint(Graphics2D g2) {
        for (int i = 0; i < SIZE; i++) {
            if (i % 2 == 0) {
                g2.drawImage(chimneyImage1, (int) chimneys.get(i).getPosX(), (int) chimneys.get(i).getPosY(), null);
            } else {
                g2.drawImage(chimneyImage2, (int) chimneys.get(i).getPosX(), (int) chimneys.get(i).getPosY(), null);

            }
        }
    }


}

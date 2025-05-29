
package pkg2dgamesframework;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class GameThread extends JPanel implements Runnable {

    private GameScreen context;

    private Thread thread;

    private Graphics ThisGraphics;

    public static int FPS_SET = 60;
    public static int UPS_SET = 60;


    private BufferedImage buffImage;

    private int MasterWidth, MasterHeight;
    public static float scaleX_ = 1, scaleY_ = 1;

    public GameThread(GameScreen context) {
        this.context = context;

        MasterWidth = context.CUSTOM_WIDTH;
        MasterHeight = context.CUSTOM_HEIGHT;

        this.thread = new Thread(this);

    }

    public void StartThread() {
        thread.start();
    }

    public void paint(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, context.CUSTOM_WIDTH, context.CUSTOM_HEIGHT);
        Graphics2D g2 = (Graphics2D) g;
        if (buffImage != null) {
            g2.scale(scaleX_, scaleY_);
            g2.drawImage(buffImage, 0, 0, this);
        }
    }

    private void UpdateSize() {
        if (this.getWidth() <= 0) return;

        context.CUSTOM_WIDTH = this.getWidth();
        context.CUSTOM_HEIGHT = this.getHeight();

        scaleX_ = (float) context.CUSTOM_WIDTH / (float) MasterWidth;
        scaleY_ = (float) context.CUSTOM_HEIGHT / (float) MasterHeight;
    }

    private void updateGame() {
        context.GAME_UPDATE(System.currentTimeMillis());
    }

    private void renderGame() {
        if (buffImage == null || buffImage.getWidth() != MasterWidth || buffImage.getHeight() != MasterHeight) {
            buffImage = new BufferedImage(MasterWidth, MasterHeight, BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D g2 = (Graphics2D) buffImage.getGraphics();
        if (g2 != null) {
            context.GAME_PAINT(g2);
            g2.dispose();
        }
    }

    @Override
    public void run() {

        double timePerFrame = 1_000_000_000.0 / FPS_SET;
        double timePerUpdate = 1_000_000_000.0 / UPS_SET;

        long lastFrame = System.nanoTime();
        long lastUpdate = System.nanoTime();
        long lastTimeCheck = System.currentTimeMillis();

        int frames = 0;
        int updates = 0;

        while (true) {
            long now = System.nanoTime();

            // Update game state
            while (now - lastUpdate >= timePerUpdate) {
                UpdateSize();
                updateGame();
                lastUpdate += timePerUpdate;
                updates++;
            }

            // Render game frame
            if (now - lastFrame >= timePerFrame) {
                renderGame();
                repaint();
                lastFrame += timePerFrame;
                frames++;
            }

            // Output FPS/UPS every second
            if (System.currentTimeMillis() - lastTimeCheck >= 1000) {
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0;
                updates = 0;
                lastTimeCheck = System.currentTimeMillis();
            }

            // Give the CPU some breathing room
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}

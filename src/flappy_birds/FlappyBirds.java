package flappy_birds;

import java.awt.*;
import java.awt.event.KeyEvent;


import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.font.GlyphVector;

import pkg2dgamesframework.AFrameOnImage;
import pkg2dgamesframework.Animation;
import pkg2dgamesframework.GameScreen;

import javax.imageio.ImageIO;

import static java.awt.event.MouseEvent.MOUSE_CLICKED;

public class FlappyBirds extends GameScreen {

    private BufferedImage backgroundDay;
    private BufferedImage pauseButton;
    private BufferedImage resumeButton;
    private BufferedImage promptToPlay;
    private BufferedImage getReady;
    private BufferedImage birds;
    private Animation bird_anim;

    public static float g = 0.16f;

    private Bird bird;
    private Ground ground;
    private ChimneyGroup chimneyGroup;


    private int Point = 0;

    private final int BEGIN_SCREEN = 0;
    private final int GAMEPLAY_SCREEN = 1;
    private final int GAMEOVER_SCREEN = 2;

    private int currentScreen = BEGIN_SCREEN;

    private boolean isPaused = false;

    public FlappyBirds() throws IOException {

        super(800, 600);

        try {
            backgroundDay = ImageIO.read(
                    new File("Assets/background-day.png"));
            pauseButton = ImageIO.read(
                    new File("Assets/button_pause.png"));
            resumeButton = ImageIO.read(
                    new File("Assets/button_resume.png"));
            promptToPlay = ImageIO.read(
                    new File("Assets/prompt.png"));
            getReady = ImageIO.read(
                    new File("Assets/label_get_ready.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            birds = ImageIO.read(new File("Assets/bird_sprite.png"));


        } catch (IOException e) {
        }

        bird_anim = new Animation(70);
        AFrameOnImage f;
        f = new AFrameOnImage(0, 0, 60, 60);
        bird_anim.AddFrame(f);
        f = new AFrameOnImage(60, 0, 60, 60);
        bird_anim.AddFrame(f);
        f = new AFrameOnImage(120, 0, 60, 60);
        bird_anim.AddFrame(f);
        f = new AFrameOnImage(60, 0, 60, 60);
        bird_anim.AddFrame(f);

        bird = new Bird(300, 230, 50, 50);
        ground = new Ground();

        chimneyGroup = new ChimneyGroup();

        BeginGame();
    }

    public static void main(String[] args) throws IOException {
        new FlappyBirds();
    }

    private void resetGame() {
        bird.setPos(300, 230);
        bird.setVt(0);
        bird.setLive(true);
        Point = 0;
        chimneyGroup.resetChimneys();
    }

    private void checkScore() {
        for (int i = 0; i < ChimneyGroup.SIZE; i++) {
            if (bird.getPosX() > chimneyGroup.getChimney(i).getPosX() && !chimneyGroup.getChimney(i).getIsBehindBird()
                    && i % 2 == 0) {
                Point++;
                bird.getMoneySound.play();
                chimneyGroup.getChimney(i).setIsBehindBird(true);
            }
        }
    }

    private void checkCollisions() {
        for (int i = 0; i < ChimneyGroup.SIZE; i++) {
            if (bird.getRect().intersects(chimneyGroup.getChimney(i).getRect())) {
                if (bird.getLive()) {
                    bird.bupSound.play();
                }
                bird.setLive(false);

            }
        }

        if (bird.getPosY() + bird.getH() > ground.getYGround()) {
            if (bird.getLive()) {
                bird.bupSound.play();
                bird.setLive(false);
            }
            currentScreen = GAMEOVER_SCREEN;
        }

        checkScore();
    }

    @Override
    public void GAME_UPDATE(long deltaTime) {

        if (isPaused) {
            return; // Skip updates if paused
        }

        if (currentScreen == BEGIN_SCREEN) {
            resetGame();
        } else if (currentScreen == GAMEPLAY_SCREEN) {

            if (bird.getLive())
                bird_anim.Update_Me(deltaTime);

            bird.update(deltaTime);

            if (bird.getLive()) {
                ground.Update();
                chimneyGroup.update();
            }

            checkCollisions();

        } else {

        }

    }


    @Override
    public void GAME_PAINT(Graphics2D g2) {
        String scoreText = "" + Point;
        Font scoreFont = new Font("Verdana", Font.BOLD, 50);

        // Create the outline shape from the text
        GlyphVector gv = scoreFont.createGlyphVector(g2.getFontRenderContext(), scoreText);
        Shape textShape = gv.getOutline(400, 60);

        if (backgroundDay != null) {
            g2.drawImage(backgroundDay, 0, 0, getWidth(), getHeight(), null);
        }

        chimneyGroup.paint(g2);

        BufferedImage buttonToDraw = isPaused ? resumeButton : pauseButton;
        if (buttonToDraw != null) {
            g2.drawImage(buttonToDraw, 20, 20, 40, 45, null);
        }

        ground.Paint(g2);

        if (bird.getIsFlying())
            bird_anim.PaintAnims((int) bird.getPosX(), (int) bird.getPosY(), birds, g2, 0, -1);
        else
            bird_anim.PaintAnims((int) bird.getPosX(), (int) bird.getPosY(), birds, g2, 0, 0);


        if (currentScreen == BEGIN_SCREEN) {
            g2.drawImage(getReady, 220, 100, 400, 100, null);
            g2.drawImage(promptToPlay, 290, 280, 270, 80, null);
        }

        if (currentScreen == GAMEOVER_SCREEN) {
            g2.setColor(Color.RED);
            g2.drawString("Press space to turn back begin screen", 200, 300);
        }
        // Draw the black outline
        g2.setFont(scoreFont);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(5)); // Thickness of the outline
        g2.draw(textShape);

        // Fill with white text inside
        g2.setColor(Color.WHITE);
        g2.fill(textShape);
    }

    @Override
    public void KEY_ACTION(KeyEvent e, int Event) {
        if (Event == KEY_PRESSED) {

            // SPACE Key
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (currentScreen == BEGIN_SCREEN) {
                    currentScreen = GAMEPLAY_SCREEN;
                } else if (currentScreen == GAMEPLAY_SCREEN) {
                    if (bird.getLive() && !isPaused)
                        bird.fly();
                } else if (currentScreen == GAMEOVER_SCREEN) {
                    currentScreen = BEGIN_SCREEN;
                }
            }

            // ESC Key
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE && currentScreen == GAMEPLAY_SCREEN) {
                isPaused = !isPaused;
                System.out.println("Game Paused? " + isPaused);
            }
        }
    }


    @Override
    public void MOUSE_ACTION(MouseEvent e, int Event) {
        if (Event == MouseEvent.MOUSE_CLICKED) {
            int mouseX = e.getX();
            int mouseY = e.getY();
            System.out.println("Mouse clicked at: " + mouseX + "," + mouseY);
            // Pause button position and size (adjust to match your image position)
            int pausebtnX = 20, pausebtnY = 20, pausebtnWidth = 40, pausebtnHeight = 45;

            // If click is inside the pause button area
            if (mouseX >= pausebtnX && mouseX <= pausebtnX + pausebtnWidth && mouseY >= pausebtnY && mouseY <= pausebtnY + pausebtnHeight) {
                isPaused = !isPaused;  // Toggle pause
                System.out.println("Game Paused? " + isPaused);
            }
        }
    }
}
package flappy_birds;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;


import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import pkg2dgamesframework.AFrameOnImage;
import pkg2dgamesframework.Animation;
import pkg2dgamesframework.GameScreen;

import javax.imageio.ImageIO;

public class FlappyBirds extends GameScreen {

    private BufferedImage backgroundDay;


    private BufferedImage birds;
    private Animation bird_anim;

    public static float g = 0.1f;

    private Bird bird;
    private Ground ground;
    private ChimneyGroup chimneyGroup;


    private int Point = 0;

    private final int BEGIN_SCREEN = 0;
    private final int GAMEPLAY_SCREEN = 1;
    private final int GAMEOVER_SCREEN = 2;

    private int currentScreen = BEGIN_SCREEN;


    public FlappyBirds() throws IOException {

        super(800, 600);

        try {
            backgroundDay = ImageIO.read(
                    new File("Assets/background-day.png"));
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

        bird = new Bird(350, 250, 50, 50);
        ground = new Ground();

        chimneyGroup = new ChimneyGroup();

        BeginGame();
    }

    public static void main(String[] args) throws IOException {
        new FlappyBirds();
    }

    private void resetGame() {
        bird.setPos(350, 250);
        bird.setVt(0);
        bird.setLive(true);
        Point = 0;
        chimneyGroup.resetChimneys();
    }

    @Override
    public void GAME_UPDATE(long deltaTime) {

        if (currentScreen == BEGIN_SCREEN) {
            resetGame();
        } else if (currentScreen == GAMEPLAY_SCREEN) {

            if (bird.getLive())
                bird_anim.Update_Me(deltaTime);

            bird.update(deltaTime);
            ground.Update();

            chimneyGroup.update();

            for (int i = 0; i < ChimneyGroup.SIZE; i++) {
                if (bird.getRect().intersects(chimneyGroup.getChimney(i).getRect())) {
                    bird.setLive(false);
                }
            }

            for(int i = 0; i < ChimneyGroup.SIZE; i++) {
                if (bird.getPosX() > chimneyGroup.getChimney(i).getPosX() && !chimneyGroup.getChimney(i).getIsBehindBird()
                        && i % 2 == 0) {
                    Point++;
                    chimneyGroup.getChimney(i).setIsBehindBird(true);
                }
            }

            if (bird.getPosY() + bird.getH() > ground.getYGround()) {
                currentScreen = GAMEOVER_SCREEN;
            }

        } else {

        }

    }


    @Override
    public void GAME_PAINT(Graphics2D g2) {

        if (backgroundDay != null) {
            g2.drawImage(backgroundDay, 0, 0, getWidth(), getHeight(), null);
        }


        chimneyGroup.paint(g2);

        ground.Paint(g2);

        if (bird.getIsFlying()) bird_anim.PaintAnims((int) bird.getPosX(), (int) bird.getPosY(), birds, g2, 0, -1);
        else bird_anim.PaintAnims((int) bird.getPosX(), (int) bird.getPosY(), birds, g2, 0, 0);

        if (currentScreen == BEGIN_SCREEN) {
            g2.setColor(Color.RED);
            g2.drawString("Press space to play game", 200, 300);
        }

        if (currentScreen == GAMEOVER_SCREEN) {
            g2.setColor(Color.RED);
            g2.drawString("Press space to turn back begin screen", 200, 300);
        }
        g2.setColor(Color.RED);
        g2.drawString("Point: "+Point,20, 50);
    }

    @Override
    public void KEY_ACTION(KeyEvent e, int Event) {
        if (Event == KEY_PRESSED) {

            if (currentScreen == BEGIN_SCREEN) {
                currentScreen = GAMEPLAY_SCREEN;

            } else if (currentScreen == GAMEPLAY_SCREEN) {
                if (bird.getLive())
                    bird.fly();

            } else if (currentScreen == GAMEOVER_SCREEN) {
                currentScreen = BEGIN_SCREEN;
            }
        }
    }

    @Override
    public void MOUSE_ACTION(MouseEvent e, int Event) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
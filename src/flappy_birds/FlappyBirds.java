package flappy_birds;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import pkg2dgamesframework.AFrameOnImage;
import pkg2dgamesframework.Animation;
import pkg2dgamesframework.GameScreen;

import javax.imageio.ImageIO;

public class FlappyBirds extends GameScreen {

    private BufferedImage birds;
    private Animation bird_anim;


    public static float g = 0.1f;

    private Bird bird;

    public FlappyBirds() throws IOException {
        super(800, 600);

        try {
            birds = ImageIO.read(new File("Assets/bird_sprite.png"));
        } catch (IOException e) {}

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

        bird = new Bird(350, 250, 50,50);

        BeginGame();
    }

    public static void main(String[] args) throws IOException {
        new FlappyBirds();
    }

    @Override
    public void GAME_UPDATE(long deltaTime) {
        bird_anim.Update_Me(deltaTime);
        bird.update(deltaTime);
    }

    @Override
    public void GAME_PAINT(Graphics2D g2) {
        bird_anim.PaintAnims((int) bird.getPosX(), (int) bird.getPosY(), birds, g2, 0, 0);
    }

    @Override
    public void KEY_ACTION(KeyEvent e, int Event) {
        if (Event == KEY_PRESSED) {
            bird.fly();
        }
    }
}
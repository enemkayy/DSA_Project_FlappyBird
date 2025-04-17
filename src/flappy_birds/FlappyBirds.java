package flappy_birds;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import pkg2dgamesframework.GameScreen;

public class FlappyBirds extends GameScreen {

    public FlappyBirds() {
        super(800, 600);
        this.BeginGame();
    }

    public static void main(String[] args) {
        new FlappyBirds();
    }

    public void GAME_UPDATE(long deltaTime) {
    }

    public void GAME_PAINT(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.fillRect(50, 100, 100, 100);
    }

    public void KEY_ACTION(KeyEvent e, int Event) {
    }
}
package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import flappy_birds.FlappyBirds;

public class KeyboardListener implements KeyListener {

    private FlappyBirds game;

    public static int KEY_PRESSED = 0;
    public static int KEY_RELEASED = 1;

    public KeyboardListener(FlappyBirds game) {
        this.game = game;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        game.KEY_ACTION(e, FlappyBirds.KEY_PRESSED);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        game.KEY_ACTION(e, FlappyBirds.KEY_RELEASED);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
}

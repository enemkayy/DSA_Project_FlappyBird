package pkg2dgamesframework;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public abstract class GameScreen extends JFrame implements KeyListener, MouseListener, MouseWheelListener {
    public static int KEY_PRESSED = 0;
    public static int KEY_RELEASED = 1;
    public int CUSTOM_WIDTH = 500;
    public int CUSTOM_HEIGHT = 500;
    private GameThread G_Thread;
    public static int MASTER_WIDTH = 500;
    public static int MASTER_HEIGHT = 500;

    public GameScreen() {
        this.InitThread();
        this.InitScreen();
    }

    public void RegisterImage(int id, BufferedImage image) {
    }

    public BufferedImage getImageWithID(int id) {
        return null;
    }

    public GameScreen(int w, int h) {
        this.CUSTOM_WIDTH = w;
        this.CUSTOM_HEIGHT = h;
        MASTER_WIDTH = this.CUSTOM_WIDTH;
        MASTER_HEIGHT = this.CUSTOM_HEIGHT;
        this.InitThread();
        this.InitScreen();
    }

    private void InitScreen() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseWheelListener(this);
        this.setTitle("Flappy Bird remake");
        this.setSize(this.CUSTOM_WIDTH, this.CUSTOM_HEIGHT);
        this.setVisible(true);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
    }

//    public void initInputs() {
//        myMouseListener = new MyMouseListener(game);
//        keyboardListener = new KeyboardListener(game);
//
//        addMouseListener(myMouseListener);
//        addMouseMotionListener(myMouseListener);
//        addKeyListener(keyboardListener);
//
//        requestFocus();
//    }

    public void BeginGame() {

        this.G_Thread.StartThread();
    }

    private void InitThread() {
        this.G_Thread = new GameThread(this);
        this.add(this.G_Thread);
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        this.KEY_ACTION(e, KEY_PRESSED);
    }

    public void keyReleased(KeyEvent e) {
        this.KEY_ACTION(e, KEY_RELEASED);
    }

    public abstract void GAME_UPDATE(long var1);

    public abstract void GAME_PAINT(Graphics2D var1);

    public abstract void KEY_ACTION(KeyEvent var1, int var2);

    public abstract void MOUSE_ACTION(MouseEvent var1, int var2);

    public abstract void MOUSE_EXITED(MouseEvent var1);

    public abstract void MOUSE_WHEEL_ACTION(int notches);

    @Override
    public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
        MOUSE_WHEEL_ACTION(e.getWheelRotation());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        MOUSE_ACTION(e, MouseEvent.MOUSE_CLICKED);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        MOUSE_ACTION(e, MouseEvent.MOUSE_PRESSED);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        MOUSE_ACTION(e, MouseEvent.MOUSE_RELEASED);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        MOUSE_ACTION(e, MouseEvent.MOUSE_ENTERED);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        MOUSE_ACTION(e, MouseEvent.MOUSE_EXITED);
    }

}

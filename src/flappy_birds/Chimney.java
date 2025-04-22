package flappy_birds;

import pkg2dgamesframework.Objects;

import java.awt.*;

public class Chimney extends Objects {

    private Rectangle rect;

    public Chimney (int x, int y, int w, int h) {
        super(x, y, w, h);
        rect = new Rectangle(x, y, w, h);
    }

    public void update() {
        setPosX(getPosX() - 2);
        rect.setLocation((int) this.getPosX(), (int) this.getPosY());
    }

    public Rectangle getRect() {
        return rect;
    }


}

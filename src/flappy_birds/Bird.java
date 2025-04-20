package flappy_birds;

import pkg2dgamesframework.Objects;

public class Bird extends Objects {

    private float vt = 0; // The bird's falling speed

    public Bird(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public void update(long deltaTime) {

        vt += FlappyBirds.g;

        this.setPosY(this.getPosY() + vt);

    }

    public void fly() {
        vt = -3;
    }
}

package pkg2dgamesframework;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Animation {

    private long beginTime = 0;

    private long mesure = 20;

    private AFrameOnImage[] frames;
    private int NumOfFrame = 0;
    private int CurrentFrame = 0;

    public Animation(long mesure) {

        // parameter mesure means how long will a frame in the picture exist?
        this.mesure = mesure;
    }

    public void Update_Me(long deltaTime) {
        if (NumOfFrame > 0) {
            if (deltaTime - beginTime > mesure) {
                CurrentFrame++;
                if (CurrentFrame >= NumOfFrame)
                    CurrentFrame = 0;
                beginTime = deltaTime;
            }
        }
    }

    public void AddFrame(AFrameOnImage sprite) {
        AFrameOnImage[] bufferSprites = frames;
        frames = new AFrameOnImage[NumOfFrame + 1];
        for (int i = 0; i < NumOfFrame; i++) frames[i] = bufferSprites[i];
        frames[NumOfFrame] = sprite;
        NumOfFrame++;
    }

    public void PaintAnims(int x, int y, BufferedImage image, Graphics2D g2, int anchor, float rotation) {
        frames[CurrentFrame].Paint(x, y, image, g2, anchor, rotation);
    }
}

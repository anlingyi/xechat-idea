package cn.xeblog.plugin.game.game2048;

import java.awt.*;

public class Block {
    int value;

    public Block() {
        this(0);
    }

    public Block(int num) {
        value = num;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public Color getForeground() {
        return value < 16 ? new Color(0x776e65) : new Color(0xf9f6f2);
    }

    public Color getBackground() {
        switch (value) {
            case 2:
                return new Color(0xeee4da);
            case 4:
                return new Color(0xF1E1CA);
            case 8:
                return new Color(0xF6B17A);
            case 16:
                return new Color(0xF99563);
            case 32:
                return new Color(0xFB7B5E);
            case 64:
                return new Color(0xFA5D3A);
            case 128:
                return new Color(0xF1D071);
            case 256:
                return new Color(0xF1CC61);
            case 512:
                return new Color(0xF1C94E);
            case 1024:
                return new Color(0xF1C73F);
            case 2048:
                return new Color(0xF2C32D);
        }
        return new Color(0xcdc1b4);
    }
}

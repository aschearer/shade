package com.shade.controls;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;

public class StatMeter implements Animatable {

    private float x, y;
    private Image front, back;
    private float actual, current, total;
    private TrueTypeFont font;
    private String stat;
    private float rateOfChange;

    public StatMeter(TrueTypeFont f, float x, float y, int actual, int total)
            throws SlickException {
        font = f;
        this.x = x;
        this.y = y;
        this.current = 0;
        this.actual = actual;
        this.total = total;
        stat = actual + "";
        rateOfChange = this.total / 5000;

        front = new Image("states/recap/meter-front.png");
        back = new Image("states/recap/meter-back.png");
    }

    public void render(StateBasedGame game, Graphics g) {
        back.draw(x, y);
        float w = front.getWidth();
        float h = front.getHeight();
        float adjustment = (w * (current / total));
        front.draw(x, y, x + adjustment, y + h, 0, 0, adjustment, h);
        font.drawString(x + 8, y + 4, stat);
    }

    public void update(StateBasedGame game, int delta) {
        if (current < actual) {
            current += delta * rateOfChange;
        }
    }

}

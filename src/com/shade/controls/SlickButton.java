package com.shade.controls;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;

import mdes.slick.animation.Easing;
import mdes.slick.animation.Timeline;
import mdes.slick.animation.entity.SizeEntity;
import mdes.slick.animation.entity.renderable.ImageAlpha;
import mdes.slick.animation.fx.AlphaFx;
import mdes.slick.animation.fx.CompoundFx;
import mdes.slick.animation.fx.SizeFx;

public class SlickButton implements SizeEntity, Animatable, Button {

    private static Sound click, hover;
    
    private Timeline timeline;
    private ImageAlpha up, down;
    private float width, height;
    private float startWidth, startHeight;

    private float x, y;
    private boolean mouseInside, mouseDown;
    private ClickListener listener;

    private Object userData;
    
    static {
        try {
            click = new Sound("states/common/click.ogg");
            hover = new Sound("states/common/hover.ogg");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public SlickButton(int x, int y, Image u, Image d) throws SlickException {
        this.x = x;
        this.y = y;
        up = new ImageAlpha(u);
        down = new ImageAlpha(d);
        down.setAlpha(0f);

        width = u.getWidth();
        height = u.getHeight();
        startWidth = width;
        startHeight = height;

        final Easing fadeIn = Easing.CUBIC_OUT;
        final int durationIn = 500;
        AlphaFx upFadeIn = new AlphaFx(durationIn, up, 0f, 1f, fadeIn);
        AlphaFx downFadeIn = new AlphaFx(durationIn, down, 0f, 1f, fadeIn);

        final Easing fadeOut = Easing.LINEAR;
        final int durationOut = 500;
        AlphaFx upFadeOut = new AlphaFx(durationOut, up, 1f, 0f, fadeOut);
        AlphaFx downFadeOut = new AlphaFx(durationOut, down, 1f, 0f, fadeOut);

        CompoundFx fade1 = new CompoundFx(upFadeOut, downFadeIn);
        CompoundFx fade2 = new CompoundFx(upFadeIn, downFadeOut);

        float endWidth = startWidth * 1.1f;
        float endHeight = startHeight * 1.1f;
        SizeFx scale1 = new SizeFx(1000, this, startWidth, startHeight,
                endWidth, endHeight, Easing.ELASTIC_OUT);
        SizeFx scale2 = new SizeFx(400, this, endWidth, endHeight, startWidth,
                startHeight, Easing.CUBIC_OUT);

        timeline = new Timeline();
        timeline.add(new CompoundFx(fade1, scale1));
        timeline.add(new CompoundFx(fade2, scale2));
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void render(StateBasedGame game, Graphics g) {
        up.draw(x, y, width, height);
        down.draw(x, y, width, height);
    }

    public void update(StateBasedGame game, int delta) {
        Input input = game.getContainer().getInput();
        int mx = input.getMouseX();
        int my = input.getMouseY();
        boolean pressed = input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON);
        if (mx >= x && mx <= x + width &&
                my >= y && my <= y + height) {
            if (!mouseInside) {
                mouseInside = true;
                onMouseEnter();
            }
        } else {
            if (mouseInside) {
                mouseInside = false;
                onMouseExit();
            }
        }
        if (pressed && !mouseDown && mouseInside) {
            mouseDown = true;
            press();
            listener.onClick(game, this);
        } else if (!pressed && mouseDown) {
            mouseDown = false;
        }
        timeline.update(delta);
    }

    public void press() {
        click.play();
        timeline.setActive(true);
        timeline.setRange(0, 1);
        timeline.restart();
    }

    public void onMouseEnter() {
        hover.play();
        timeline.setActive(true);
        timeline.setRange(0, 1);
        timeline.restart();
    }

    public void onMouseExit() {
        timeline.setActive(true);
        timeline.setRange(1, 2);
        timeline.restart();
    }
    
    public void reset() {
        timeline.rewind();
    }

    public void addListener(ClickListener l) {
        listener = l;
    }

    public void setUserData(Object o) {
        userData = o;
    }
    
    public Object getUserData() {
        return userData;
    }

}

package com.shade.controls;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;

public class ScrollingText implements Animatable {
	
	private enum Status {
		IDLE, SCROLLING, OFFSCREEN
	}
	
	private static final float SCROLL_SPEED = -.5f;

	private String text;
	private TrueTypeFont font;
	private float x, y;
	private float target;
	private Status status;

	public ScrollingText(String text, TrueTypeFont font, float x, float y) {
		this.text = text;
		this.font = font;
		this.x = x;
		this.y = y;
		this.target = -20;
		status = Status.IDLE;
	}

	public void start() {
		status = Status.SCROLLING;
	}

	public void render(StateBasedGame game, Graphics g) {
		if (status != Status.OFFSCREEN) {
			font.drawString(x, y, text);
		}
	}

	public void update(StateBasedGame game, int delta) {
		if (x == 0) {
			x = (game.getContainer().getWidth() - font.getWidth(text)) / 2;
		}
		if (status == Status.SCROLLING) {
			y += SCROLL_SPEED;
		}
		if (y + font.getHeight() < target) {
			status = Status.IDLE;
		}
		if (y + font.getHeight() < 0) {
			status = Status.OFFSCREEN;
		}
	}
}

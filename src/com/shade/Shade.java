package com.shade;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Shade extends StateBasedGame {

	public static final String TITLE = "Shade";

	public Shade() {
		super(TITLE);
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		// TODO Auto-generated method stub

	}
	
	public static void main(String[] args) {
		try {
			AppGameContainer container = new AppGameContainer(new Shade(), 800, 600, false);
			container.setVSync(true);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

}

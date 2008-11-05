package com.shade;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.states.InGameState;

public class Shade extends StateBasedGame {

	public static final String TITLE = "Shade";

	public Shade() {
		super(TITLE);
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
	    addState(new InGameState());
	}
	
	public static void main(String[] args) {
		try {
			AppGameContainer container = new AppGameContainer(new Shade(), 800, 600, false);
			//container.setTargetFrameRate(60);
			//container.setVSync(true);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

}

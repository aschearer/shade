package com.shade.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Level;
import com.shade.crash.CrashLevel;
import com.shade.entities.Block;
import com.shade.entities.Dome;
import com.shade.entities.Player;
import com.shade.lighting.GlobalLight;
import com.shade.lighting.LightMask;
import com.shade.lighting.LightSourceProxy;
import com.shade.lighting.LuminousEntity;

public class InGameState extends BasicGameState {

	public static final int ID = 1;
	
	private Image background;
	private LightMask view;
	private Level<LuminousEntity> model;
	private LightSourceProxy lights;

	@Override
	public int getID() {
		return ID;
	}

	public void init(GameContainer container,
			StateBasedGame game) throws SlickException {
		lights = new LightSourceProxy();
		lights.add(new GlobalLight((float) (4 * Math.PI / 3)));
		
		view = new LightMask();
		view.add(lights);
		background = new Image("states/ingame/background.png");
		
		model = new CrashLevel<LuminousEntity>(8, 6, 100);
		model.add(new Player(300, 200));
		model.add(new Block(50, 50, 100, 100, 6));
		model.add(new Block(400, 100, 150, 150, 9));
		model.add(new Dome(500, 500, 50, 12));
	}

	public void render(GameContainer container,
			StateBasedGame game, Graphics g)
			throws SlickException {
		view.render(game, g, model.toArray(new LuminousEntity[0]), background);
	}

	public void update(GameContainer container,
			StateBasedGame game, int delta)
			throws SlickException {
		model.update(game, delta);
		lights.update(game, delta);
	}

}

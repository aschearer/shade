package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.base.util.State;
import com.shade.base.util.StateManager;
import com.shade.crash.CrashLevel;
import com.shade.crash.Repelable;
import com.shade.entities.bird.Bird;
import com.shade.entities.mushroom.Mushroom;
import com.shade.entities.util.Sizzle2;
import com.shade.entities.util.Sparkler;
import com.shade.lighting.LuminousEntity;

public class Player extends Linkable {

	public static final int INVINCIBLE_START = 3000;
	public static final float MIN_SPEED = 2.4f;
	public static final float MAX_SPEED = 3.6f;
	public static final float INITIAL_SPEED = MAX_SPEED / 2 + MIN_SPEED / 2;
	private static final int MUSHROOM_LIMIT = 3;
	private static final int PLAYER_HEIGHT = 3;

	private enum PlayerState {
		NORMAL, STUNNED
	};

	private StateManager manager;
	protected Image normal;
	private float luminosity;
	private Sound register, damage;
	private boolean impeded;
	private float mileage;
	protected int invincibleTimer, flipper, flipthreshold;
	private Sizzle2[] sizzles;
	private float speed;

	public Player(int x, int y) throws SlickException {
		initShape(x, y);
		initResources();
		initSizzles();
		initStates();
		invincibleTimer = INVINCIBLE_START;
		flipthreshold = 1;
		speed = INITIAL_SPEED;
	}

	public void initSizzles() {
		sizzles = new Sizzle2[8];
		for (int i = 0; i < sizzles.length; i++) {
			int x = (int) (Math.cos(Math.PI * 2 * i / 8) * getWidth() * 2 / 5);
			int y = (int) (Math.sin(Math.PI * 2 * i / 8) * getHeight() * 2 / 5);
			try {
				sizzles[i] = new Sizzle2(this,10, x, y, "entities/sparkle/puff.png");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void sizzle(Graphics g) {
		for (int i = 0; i < sizzles.length; i++) {
			sizzles[i].animate(g);
		}
	}


	private void initShape(int x, int y) {
		shape = new Circle(x, y, 18);
	}

	private void initResources() throws SlickException {
		normal = new Image("entities/player/player.png");
		register = new Sound("entities/player/register.ogg");
		damage = new Sound("entities/player/hit.ogg");
	}

	private void initStates() {
		manager = new StateManager();
		manager.add(new NormalState());
		manager.add(new StunnedState());
	}

	public void stun() {
		manager.enter(Player.PlayerState.STUNNED);
		damage.play();
	}

	private class NormalState implements State {

		public boolean isNamed(Object state) {
			return state == PlayerState.NORMAL;
		}

		public void enter() {

		}

		public int getRole() {
			// TODO Auto-generated method stub
			return 0;
		}

		public void onCollision(Entity obstacle) {
			if (obstacle.getRole() == Roles.BASKET.ordinal() && next != null) {
				Linkable m = next;
				next = null;
				Linkable l = (Linkable) obstacle;
				l.attach(m);
				register.play();
			}
			if (obstacle.getRole() == Roles.MONSTER.ordinal()) {
				manager.enter(Player.PlayerState.STUNNED);
				damage.play();
			}
			if (obstacle.getRole() == Roles.BIRD.ordinal()) {
				Bird b = (Bird) obstacle;
				if (b.isAttacking()) {
					manager.enter(Player.PlayerState.STUNNED);
					damage.play();
				}
			}

			if (obstacle.getRole() == Roles.SANDPIT.ordinal()) {
				impeded = true;
			}
		}

		public void render(StateBasedGame game, Graphics g) {
			double inverse = INVINCIBLE_START * 7 / 6 - invincibleTimer;
			if (Math.sin(inverse * inverse / (40000.0 * Math.PI)) > 0) {
				normal.drawCentered(getXCenter(), getYCenter());
			}
			if (invincibleTimer <= 0) {
				normal.drawCentered(getXCenter(), getYCenter());
				sizzle(g);
			}
		}

		public void update(StateBasedGame game, int delta) {
			testAndMove(game.getContainer().getInput(), delta);
			testAndWrap();
			for (int i = 0; i < sizzles.length; i++)
				sizzles[i].update(delta);
			impeded = false;
			if(invincibleTimer-delta<0&&invincibleTimer>0)
				for (int i = 0; i < sizzles.length; i++)
				sizzles[i].resetTimer();
			if (invincibleTimer > 0) {
				invincibleTimer -= delta;
			}
		}

		private void testAndMove(Input input, int delta) {
			xVelocity = 0;
			yVelocity = 0;
			if (input.isKeyDown(Input.KEY_LEFT)) {
				xVelocity--;
			}
			if (input.isKeyDown(Input.KEY_RIGHT)) {
				xVelocity++;
			}
			if (input.isKeyDown(Input.KEY_UP)) {
				yVelocity--;
			}
			if (input.isKeyDown(Input.KEY_DOWN)) {
				yVelocity++;
			}
			double mag = Math.sqrt(xVelocity * xVelocity + yVelocity
					* yVelocity);
			// make it uniform speed
			float sped = (impeded) ? speed / 2 : speed;
			xVelocity = (float) (1.0 * sped * xVelocity / mag);
			yVelocity = (float) (1.0 * sped * yVelocity / mag);
			if (mag != 0) {
				nudge(xVelocity, yVelocity);
				mileage += sped;
			} else {
				xVelocity = 0;
				yVelocity = 0;
			}
		}
	}

	public void setSpeed(float newSpeed) {
		speed = newSpeed;
	}

	private class StunnedState implements State {

		private int timer;
		private int failmer;

		public boolean isNamed(Object state) {
			return state == PlayerState.STUNNED;
		}

		public void enter() {
			scatterShrooms();
			for (int i = 0; i < sizzles.length; i++)
				sizzles[i].resetTimer();

			timer = 0;
			failmer = 0;
		}

		// HACK! TODO: KILL HACK!
		private void scatterShrooms() {
			Linkable la = next;
			while (la != null) {
				Mushroom m = (Mushroom) la;
				la = la.next;
				m.scatter();

			}
		}

		public int getRole() {
			// TODO Auto-generated method stub
			return PlayerState.STUNNED.ordinal();
		}

		public void onCollision(Entity obstacle) {

		}

		public void render(StateBasedGame game, Graphics g) {
			if (failmer % 5 > 2) {
				normal.drawCentered(getXCenter(), getYCenter());
			}

		}

		public void update(StateBasedGame game, int delta) {
			timer += delta;
			failmer++;
			if (timer > 800){
				manager.enter(PlayerState.NORMAL);
				invincibleTimer = 2000;
			}
		}

		private void testAndMove(Input input, int delta) {
			xVelocity = 0;
			yVelocity = 0;
			if (input.isKeyDown(Input.KEY_LEFT)) {
				xVelocity--;
			}
			if (input.isKeyDown(Input.KEY_RIGHT)) {
				xVelocity++;
			}
			if (input.isKeyDown(Input.KEY_UP)) {
				yVelocity--;
			}
			if (input.isKeyDown(Input.KEY_DOWN)) {
				yVelocity++;
			}
			double mag = Math.sqrt(xVelocity * xVelocity + yVelocity
					* yVelocity);
			// make it uniform speed
			float sped = (impeded) ? speed / 2 : speed;
			xVelocity = (float) (1.0 * sped * xVelocity / mag);
			yVelocity = (float) (1.0 * sped * yVelocity / mag);
			if (mag != 0) {
				nudge(xVelocity, yVelocity);
			} else {
				xVelocity = 0;
				yVelocity = 0;
			}
		}

	}

	@Override
	public void attach(Linkable l) {
		if (next == null) {
			super.attach(l);
			return;
		}
		int i = 1;
		Linkable head = next;
		while (head.next != null) {
			i++;
			head = head.next;
		}
		if (i < MUSHROOM_LIMIT) {
			super.attach(l);
		}
	}

	public Shape castShadow(float direction, float depth) {
		return null;
	}

	public float getLuminosity() {
		float max = 0;
		for (int i = 0; i < sizzles.length; i++) {
			max += sizzles[i].getLuminosity();
		}
		return invincibleTimer > 0 || manager.currentState().getRole()==PlayerState.STUNNED.ordinal() ? 0 : max;
	}

	public int getZIndex() {
		return PLAYER_HEIGHT;
	}

	public void setLuminosity(float l) {
		luminosity = l;
	}

	public void addToLevel(Level<?> l) {

	}

	public float totalMileage() {
		return mileage;
	}

	public boolean isStunned() {
		return manager.currentState().isNamed(PlayerState.STUNNED);
	}

	public int getRole() {
		return Roles.PLAYER.ordinal();
	}

	public void setSmokeCount(int count) {
		// smoky.changeCount(count);
	}

	public int getSmokeCount() {
		// return smoky.getCount();
		return 0;
	}

	public void onCollision(Entity obstacle) {
		manager.onCollision(obstacle);
		if (obstacle.getRole() == Roles.OBSTACLE.ordinal()) {
			Repelable b = (Repelable) obstacle;
			b.repel(this);
		}
		/*
		 * TODO: determine if we want the chest to be an obstacle. if
		 * (obstacle.getRole() == Roles.TREASURE.ordinal()) { Repelable b =
		 * (Repelable) obstacle; b.repel(this); }
		 */
	}

	public void removeFromLevel(Level<?> l) {
		CrashLevel level = (CrashLevel) l;
		try {
			level.add(new MockPlayer((int) getXCenter(), (int) getYCenter()));
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public void render(StateBasedGame game, Graphics g) {
		//ghost.animate(g);
		if (speed > MAX_SPEED - .4) {
			//smoky.animate(g);
		}
		manager.render(game, g);

	}

	public void update(StateBasedGame game, int delta) {
		manager.update(game, delta);


	}

	public int compareTo(LuminousEntity e) {
		return getZIndex() - e.getZIndex();
	}

}

package com.shade.entities.treasure;


import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

import com.crash.Body;
import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.base.util.StateManager;
import com.shade.crash.CrashLevel;
import com.shade.crash.Repelable;
import com.shade.entities.mushroom.Mushroom;
import com.shade.entities.util.Sparkler;
import com.shade.lighting.LuminousEntity;
import com.shade.states.MasterState;

public class Treasure extends Mushroom implements Repelable {

	protected static final float SPEED = 2.2f;

	private static final float RADIUS = 12f;
	public static final float MAX_SCALE = 1f;
	private static final float MIN_SCALE = 0.3f;
	public static final int SECONDS_OF_LIFE = 1700;
	public static final float SCALE_INCREMENT = (MAX_SCALE - MIN_SCALE)
			/ SECONDS_OF_LIFE;

	private float spawn_x;
	private float spawn_y;
	private boolean open;
	protected boolean collect;
	private Sparkler sparky;
	private float sunAngle;

	protected enum States {
		RETURNING, NORMAL, PICKED, COLLECTED, FLYING
	};

	protected enum Types {
		POISON, NORMAL, GOOD, RARE
	};

	protected StateManager manager;
	protected float scale;

	private Types type;
	private float luminosity;
	protected CrashLevel level;

	private static SpriteSheet chestOpen;
	private static SpriteSheet chestClosed;
	protected static Sound spawning, picked, poisonPicked, collected;

	static {
		try {
			chestOpen = new SpriteSheet("entities/treasure/TreasureOpened.png",
					64, 51);
			chestClosed = new SpriteSheet(
					"entities/treasure/TreasureClosed.png", 39, 50);
			spawning = new Sound("entities/mushroom/sprout.ogg");
			picked = new Sound("entities/mushroom/picked.ogg");
			poisonPicked = new Sound("entities/mushroom/poison-picked.ogg");
			collected = new Sound("entities/mushroom/collected.ogg");
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	public Treasure(int x, int y, int z) throws SlickException {
		type = null;
		scale = MAX_SCALE;
		open = false;
		collect = false;
		initShape(x, y);
		spawn_x = shape.getX();
		spawn_y = shape.getY();
		initResources();
		initStates();
		sparky = new Sparkler(this,3);

	}

	private void initShape(float x, float y) {
		shape = new Rectangle(x, y, chestClosed.getWidth(), chestClosed
				.getHeight());
		shape.setCenterX(x);
		shape.setCenterY(y);
	}

	public void open() {
		open = true;
		float centx = shape.getCenterX();
		float centy = shape.getCenterY();
		shape = new Circle(spawn_x, spawn_y, RADIUS * scale);
		shape.setCenterX(centx);
		shape.setCenterY(centy);
	}

	public void close() {
		open = false;
		float centx = shape.getCenterX();
		float centy = shape.getCenterY();
		shape = new Rectangle(spawn_x, spawn_y, chestClosed.getWidth(),
				chestClosed.getHeight());
		shape.setCenterX(centx);
		shape.setCenterY(centy);
		scale = MAX_SCALE;
	}

	private void initResources() throws SlickException {
		// mushroom = sheet.getSprite(type.ordinal(), 0);
	}

	private void initStates() {
		manager = new StateManager();
		manager.add(new NormalTreasure(this));
		manager.add(new PickedTreasure(this));
		manager.add(new CollectedTreasure(this));
		manager.add(new FlyingTreasure(this));
		manager.add(new ReturningTreasure(this));
	}

	// HACK! TODO: KILL HACK!
	public void scatter() {
		manager.enter(Treasure.States.FLYING);
	}

	public void goHome() {
		nudge((spawn_x+chestClosed.getWidth()/4 - getX()) / 25, 
				(spawn_y+chestClosed.getHeight()/4 - getY()) / 25);
	}

	protected void finish() {
		detach();
	}

	protected void kill() {
		detach();
		level.remove(this);
	}

	protected void reset() {
		manager.enter(Treasure.States.NORMAL);
	}

	protected boolean inShadows() {
		return getLuminosity() < MasterState.SHADOW_THRESHOLD;
	}

	protected boolean tooBig() {
		return scale > MAX_SCALE;
	}

	protected boolean tooSmall() {
		return scale < MIN_SCALE;
	}

	protected void unsize() {
		((Circle) shape).setRadius(RADIUS * scale);
	}

	protected void grow(int delta) {
		scale += SCALE_INCREMENT * delta;
		//resize();
	}

	protected void shrink(int delta) {
		scale -= SCALE_INCREMENT * delta;
		//resize();
	}

	public boolean isPoison() {
		return false;
	}

	private void resize() {
		if (open) {
			float x = shape.getCenterX();
			float y = shape.getCenterY();
			((Circle) shape).setRadius(RADIUS * scale);
			shape.setCenterX(x);
			shape.setCenterY(y);
		}
	}

	protected void draw(Graphics g) {
		// TODO: I could make it so that I only draw the lid when the chest
		// opens and get rid of the weird unaligned nonsense that incurs these
		// messy constants.
		if (open) {
			chestOpen.draw(spawn_x, spawn_y);
			if (!collect) {
				float rmin = 0.1f;
				float gmin = 0.1f;
				float bmin = 0.1f;
				//TODO: 
				GL11.glAlphaFunc(GL11.GL_GREATER,0f);
				g.setColor(new Color(scale*(0.85f-rmin)+rmin, 
						scale*(0.9f-gmin)+gmin, 
						bmin*(1-scale),scale));
				g.fillOval(getX(), getY(), RADIUS * 2, RADIUS * 2);
				if (!inShadows()) {
					g.setColor(new Color(1f, 1f, 1f));
					// HACK! TODO: KILL HACK! MAGIC NUMBERS SRSLY?
					float hack_x = 0.6f * scale;
					float hack_y = 0.6f * scale;
					g.fillOval((float) (getX() + RADIUS + Math.cos(sunAngle
							+ Math.PI / 2)
							* RADIUS * scale / 2)
							+ hack_x, (float) (getY() + RADIUS + Math
							.sin(sunAngle + Math.PI / 2)
							* RADIUS * scale / 2)
							+ hack_y, RADIUS * scale, RADIUS * scale);
				}
				sparky.animate(g);
				GL11.glAlphaFunc(GL11.GL_GREATER,0.95f);
			}
		} else {
			// chestOpen.draw(spawn_x-chestClosed.getWidth()/4+3,
			// spawn_y-chestClosed.getHeight()/4-2);

			// TODO: FIND OUT WHY ITS NOT DRAWING ALIGNED WITH THE DAMN PEARL
			// THING.
			chestClosed.draw(spawn_x, spawn_y);
			sparky.animate(g);
			// g.setColor(new Color(0.7f, 0.7f, 0f));
			// g.fillOval(getXCenter(), getYCenter(), RADIUS * 2 * scale, RADIUS
			// * 2 * scale);

		}

	}

	public Shape castShadow(float direction, float depth) {
		sunAngle = direction;
		return null;
	}

	public int getZIndex() {
		return 1;
	}

	public float getLuminosity() {
		return luminosity;
	}

	public void setLuminosity(float l) {
		luminosity = l;
	}

	public void addToLevel(Level<?> l) {
		level = (CrashLevel) l;
	}

	public void removeFromLevel(Level<?> l) {
		// do nothing
	}

	public float getValue() {
		if (type == Types.POISON) {
			return -scale;
		}
		if (type == Types.NORMAL) {
			return scale;
		}
		if (type == Types.GOOD) {
			return scale * 2;
		}
		if (type == Types.RARE) {
			return scale * 10;
		}
		return 0;
	}

	public int getRole() {
		return manager.getRole();
	}

	public void onCollision(Entity obstacle) {
		manager.onCollision(obstacle);
	}

	public void render(StateBasedGame game, Graphics g) {
		manager.render(game, g);
	}

	public void update(StateBasedGame game, int delta) {
		manager.update(game, delta);
		sparky.update(delta);
		testAndWrap();
	}

	public int compareTo(LuminousEntity l) {
		return getZIndex() - l.getZIndex();
	}

	public void repel(Body b) {
		float velx = b.getXVelocity();
		float vely = b.getYVelocity();
		float playerx = b.getXCenter();
		float playery = b.getYCenter();
		// determine overlap
		float right = playerx - b.getWidth() / 2
				- (getXCenter() + getWidth() / 2);
		float left = playerx + b.getWidth() / 2
				- (getXCenter() - getWidth() / 2);
		float top = playery - b.getHeight() / 2
				- (getYCenter() + getHeight() / 2);
		float bottom = playery + b.getHeight() / 2
				- (getYCenter() - getHeight() / 2);
		float minx = Math.min(Math.abs(right), Math.abs(left));
		float miny = Math.min(Math.abs(top), Math.abs(bottom));
		if (minx < miny) {
			// if we move, move AWAY from the block.
			if (Math.abs(playerx - getXCenter() - velx) < Math.abs(playerx
					- getXCenter()))
				velx = -velx;
			b.nudge(-velx, 0);
		} else {
			if (Math.abs(playery - getYCenter() - vely) < Math.abs(playery
					- getYCenter())) {
				vely = -vely;
			}
			b.nudge(0, -vely);
		}
	}
}

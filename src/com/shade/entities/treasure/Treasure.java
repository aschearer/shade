package com.shade.entities.treasure;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
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
import com.shade.entities.Linkable;
import com.shade.entities.mushroom.Mushroom;
import com.shade.entities.util.Sparkler;
import com.shade.lighting.LuminousEntity;
import com.shade.states.MasterState;

public class Treasure extends Mushroom implements Repelable {

	protected static final float SPEED = 2.2f;

	private static final float RADIUS = 3f;
	public static final float MAX_SCALE = 4f;
	private static final float MIN_SCALE = 0.1f;
	public static final float SCALE_INCREMENT = (MAX_SCALE - MIN_SCALE) / 200;

	private float spawn_x;
	private float spawn_y;
	private boolean open;
	protected boolean collect;
	private Sparkler sparky;

	protected enum States {
		RETURNING, NORMAL, PICKED, COLLECTED, FLYING
	};

	protected enum Types {
		POISON, NORMAL, GOOD, RARE
	};

	protected StateManager manager;
	protected float scale;

	private Types type;
	private Image mushroom;
	private float luminosity;
	protected CrashLevel level;

	// hacky ints! TODO: clean up hacky ints
	private int timer, failmer;

	private static SpriteSheet sheet;
	private static SpriteSheet chestOpen;
	private static SpriteSheet chestClosed;
	protected static Sound spawning, picked, poisonPicked, collected;

	static {
		try {
			sheet = new SpriteSheet("entities/mushroom/mushrooms.png", 40, 40);
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
		sparky = new Sparkler(this);
		
	}

	private void initShape(float x, float y) {
		shape = new Rectangle(x, y, chestClosed.getWidth(), chestClosed
				.getHeight());
	}

	public void open() {
		open = true;
		shape = new Circle(shape.getX(), shape.getY(), RADIUS * scale);
	}

	public void close() {
		open = false;
		shape = new Rectangle(spawn_x, spawn_y, chestClosed.getWidth(),
				chestClosed.getHeight());
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
		this
				.nudge((spawn_x - getXCenter()) / 40, (spawn_y - getYCenter())
						/ 40);
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
		((Circle) shape).setRadius(RADIUS);
	}

	protected void grow() {
		scale += SCALE_INCREMENT;
		resize();
	}

	protected void shrink() {
		scale -= SCALE_INCREMENT / 3;
		resize();
	}

	public boolean isPoison() {
		return false;
	}

	private void resize() {
		float x = shape.getCenterX();
		float y = shape.getCenterY();
		shape.setCenterX(x);
		shape.setCenterY(y);
	}

	protected void draw(Graphics g) {
		// TODO: I could make it so that I only draw the lid when the chest
		// opens and get rid of the weird unaligned nonsense that incurs these
		// messy constants.
		if (open) {
			chestOpen.draw(spawn_x - chestClosed.getWidth() / 4 + 3, spawn_y
					- chestClosed.getHeight() / 4 - 2);
			if (!collect) {
				g.setColor(new Color(0.7f, 0.7f, 0f));
				g.fillOval(getX(), getY(), RADIUS * 2 * scale,
						RADIUS * 2 * scale);
				sparky.animate(g);
			}
		} else {
			// chestOpen.draw(spawn_x-chestClosed.getWidth()/4+3,
			// spawn_y-chestClosed.getHeight()/4-2);

			// TODO: FIND OUT WHY ITS NOT DRAWING ALIGNED WITH THE DAMN PEARL
			// THING.
			chestClosed.draw(spawn_x - chestClosed.getWidth() / 4 - 3, spawn_y
					- chestClosed.getHeight() / 4 - 3);
			sparky.animate(g);
			// g.setColor(new Color(0.7f, 0.7f, 0f));
			// g.fillOval(getXCenter(), getYCenter(), RADIUS * 2 * scale, RADIUS
			// * 2 * scale);

		}

	}

	public Shape castShadow(float direction, float depth) {
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

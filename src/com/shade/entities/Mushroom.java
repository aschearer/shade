package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.crash.util.CrashGeom;
import com.shade.entities.util.State;
import com.shade.entities.util.StateManager;
import com.shade.shadows.ShadowEntity;
import com.shade.util.Geom;

public class Mushroom extends Linkable implements ShadowEntity {

    private static final float RADIUS = 3f;
    private static final float SCALE_INCREMENT = .005f;
    private static final float MAX_SCALE = 3.5f;
    private static final float MIN_SCALE = 1.5f;
    private static final float START_SCALE = MIN_SCALE + .5f;
    private static final float SPEED = 1.6f;

    private enum MushroomState {
        NORMAL, PICKED, COLLECTED, DEAD
    };

    public enum MushroomType {
        POISON, NORMAL, GOOD, RARE
    };

    public MushroomType type;
    private Level level;
    private StateManager manager;
    private ShadowIntensity shadowStatus;
    private Image mushroom;
    private float scale;
    private float myIntensity;
    
	public void updateIntensity(Graphics g) {
		myIntensity = g.getPixel((int)getCenterX(), (int)getCenterY()).a;
		
	}
	
	public float getShadowIntensity(){
		return myIntensity;
	}

    public Mushroom(float x, float y, MushroomType t) throws SlickException {
        initShape(x, y);
        initResources(t);
        initStates();
        scale = START_SCALE;
        shadowStatus = ShadowIntensity.UNSHADOWED;
        type = t;
    }

    private void initShape(float x, float y) {
        shape = new Circle(x, y, RADIUS * START_SCALE);
    }

    private void initResources(MushroomType t) throws SlickException {
        SpriteSheet s = new SpriteSheet("entities/mushroom/mushrooms.png", 40,
                40);
        mushroom = s.getSprite(t.ordinal(), 0);
    }

    private void initStates() {
        manager = new StateManager();
        manager.add(new NormalState());
        manager.add(new PickedState());
        manager.add(new CollectedState());
        manager.add(new DeadState());
    }

    private class NormalState implements State {

        public boolean equals(Object state) {
            return state == MushroomState.NORMAL;
        }

        public void enter() {
            // TODO Auto-generated method stub

        }

        public void onCollision(Entity obstacle) {
            assert (prev == null);
            if (obstacle.getRole() == Role.PLAYER) {
                manager.enter(MushroomState.PICKED);
                ((Linkable) obstacle).attach(Mushroom.this);
                return;
            }
            
            if (obstacle.getRole() == Role.MOLE) {
                manager.enter(MushroomState.PICKED);
                ((Linkable) obstacle).attach(Mushroom.this);
                return;
            }
        }

        public void render(Graphics g) {
            mushroom.draw(getX(), getY(), getWidth(), getHeight());
        }

        public void update(StateBasedGame game, int delta) {
            if (scale < MIN_SCALE) {
                manager.enter(MushroomState.DEAD);
                return;
            }
            if (getShadowIntensity()<0.8 && scale < MAX_SCALE) {
                scale += SCALE_INCREMENT;
                resize();
                return;
            }
            if (shadowStatus == ShadowIntensity.UNSHADOWED) {
                shrink();
                resize();
                return;
            }
        }

    }

    private class PickedState implements State {

        public boolean equals(Object state) {
            return state == MushroomState.PICKED;
        }

        public void enter() {
            // TODO Auto-generated method stub

        }

        public void onCollision(Entity obstacle) {
            if (obstacle.getRole() == Role.MOLE) {
                manager.enter(MushroomState.PICKED);
                detach();
                ((Linkable) obstacle).attach(Mushroom.this);
                return;
            }
        }

        public void render(Graphics g) {
            mushroom.draw(getX(), getY(), getWidth(), getHeight());
        }

        public void update(StateBasedGame game, int delta) {
            if (prev == null) {
                manager.enter(MushroomState.NORMAL);
                return;
            }

            if (prev.getRole() == Role.BASKET) {
                manager.enter(MushroomState.COLLECTED);
                return;
            }

            if (scale < MIN_SCALE) {
                manager.enter(MushroomState.DEAD);
                return;
            }

            if (getShadowIntensity()>0.8) {
                shrink();
                resize();
            }

            if (overThreshold(prev, 12000)) {
                detach();
                manager.enter(MushroomState.NORMAL);
                return;
            }

            if (overThreshold(prev, 1200)) {
                followLeader();
                testAndWrap();
                return;
            }
        }
    }

    private class CollectedState implements State {

        public boolean equals(Object state) {
            return state == MushroomState.COLLECTED;
        }

        public void enter() {
            // TODO Auto-generated method stub

        }

        public void onCollision(Entity obstacle) {
            if (obstacle.getRole() == Role.BASKET) {
                manager.enter(MushroomState.DEAD);
                return;
            }
        }

        public void render(Graphics g) {
            mushroom.draw(getX(), getY(), getWidth(), getHeight());
        }

        public void update(StateBasedGame game, int delta) {
            if (prev == null) {
                manager.enter(MushroomState.NORMAL);
                return;
            }

            if (scale < MIN_SCALE) {
                manager.enter(MushroomState.DEAD);
                return;
            }

            if (getShadowIntensity()>0.8) {
                shrink();
                resize();
            }

            if (overThreshold(prev, 120000)) {
                detach();
                manager.enter(MushroomState.NORMAL);
                return;
            }

            followLeader();
            testAndWrap();
            return;
        }
    }

    private class DeadState implements State {

        public boolean equals(Object state) {
            return state == MushroomState.DEAD;
        }

        public void enter() {
            detach();
            level.remove(Mushroom.this);
        }

        public void onCollision(Entity obstacle) {
            // TODO Auto-generated method stub

        }

        public void render(Graphics g) {
            // TODO Auto-generated method stub

        }

        public void update(StateBasedGame game, int delta) {
            // TODO Auto-generated method stub

        }

    }

    private void resize() {
        float x = shape.getCenterX();
        float y = shape.getCenterY();
        ((Circle) shape).setRadius(RADIUS * scale);
        shape.setCenterX(x);
        shape.setCenterY(y);
    }

    /* Move the shape a given amount across two dimensions. */
    private void move(float magnitude, float direction) {
        Vector2f d = Geom.calculateVector(magnitude, direction);
        xVelocity = d.x;
        yVelocity = d.y;
        shape.setCenterX(shape.getCenterX() + d.x);
        shape.setCenterY(shape.getCenterY() + d.y);
    }

    private void followLeader() {
        float[] d = new float[3];
        d[0] = CrashGeom.distance2(prev, Mushroom.this);
        d[1] = d[0];
        d[2] = d[0];
        // if I'm left of my target
        if (getX() < prev.getX()) {
            d[1] = CrashGeom.distance2(prev, getCenterX() + 800, getCenterY());
        } else {
            d[1] = CrashGeom.distance2(Mushroom.this, prev.getCenterX() + 800,
                                       prev.getCenterY());
        }

        // if I'm above my target
        if (getY() < prev.getY()) {
            d[2] = CrashGeom.distance2(prev, getCenterX(), getCenterY() + 600);
        } else {
            d[2] = CrashGeom.distance2(Mushroom.this, prev.getCenterX(), prev
                    .getCenterY() + 600);
        }

        float angle = CrashGeom.calculateAngle(prev, Mushroom.this);
        if (d[1] < d[0] || d[2] < d[0]) {
            angle += Math.PI;
        }

        move(SPEED, angle);
    }

    public boolean isDead() {
        return manager.currentState().equals(MushroomState.DEAD);
    }

    public int getZIndex() {
        return 2;
    }

    public boolean hasIntensity(ShadowIntensity s) {
        return s == shadowStatus;
    }

    public void setIntensity(ShadowIntensity s) {
        shadowStatus = s;
    }

    public void addToLevel(Level l) {
        level = l;
    }

    public Role getRole() {
        return Role.MUSHROOM;
    }

    public void onCollision(Entity obstacle) {
        if (obstacle.getRole() == Role.OBSTACLE) {
            Body b = (Body) obstacle;
            b.repel(this);
        }
        manager.onCollision(obstacle);
    }

    public void removeFromLevel(Level l) {
        // TODO Auto-generated method stub

    }

    public void repel(Entity repellee) {
        // TODO Auto-generated method stub

    }

    public void render(StateBasedGame game, Graphics g) {
        manager.render(g);
        updateIntensity(g);
    }

    public void update(StateBasedGame game, int delta) {
        manager.update(game, delta);
    }

    public int compareTo(ShadowEntity o) {
        return getZIndex() - o.getZIndex();
    }

    public float getSize() {
        return scale;
    }

    private void shrink() {
        if (type == MushroomType.RARE) {
            scale -= SCALE_INCREMENT / 2;
            return;
        }
        scale -= SCALE_INCREMENT / 4;
    }

}

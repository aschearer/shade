package com.shade.entities;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.crash.util.CrashGeom;
import com.shade.shadows.ShadowCaster;
import com.shade.shadows.ShadowEntity;
import com.shade.shadows.ShadowLevel;
import com.shade.util.Geom;

public class Gargoyle extends Linkable implements ShadowEntity, ShadowCaster {

    private static final int RADIUS = 12;
    private static final float SPEED = .7f;
    private static final int COOLDOWN_TIME = 8000;

    private enum Status {
        ASLEEP, ALERT, CHASING, CONFUSED
    }

    private ShadowLevel level;
    private Status status;
    private Player target;
    private float heading;
    private int timer, cooldown, wander;
    private Animation sniff, move, bored;
    private ShadowIntensity shadowStatus;

    public Gargoyle(int x, int y) throws SlickException {
        initShape();
        initSprites();
    	shape.setLocation(x, y);
        status = Status.ASLEEP;
        cooldown = 0;
        heading = (float) Math.PI;
        wander = 0;
    }

    private void initShape() {
        shape = new Circle(300, 300, RADIUS);
    }

    private void initSprites() throws SlickException {
        SpriteSheet sniffs = new SpriteSheet("entities/mole/sniff.png", 40, 40);

        sniff = new Animation(sniffs, 300);
        sniff.setAutoUpdate(false);
        sniff.setPingPong(true);

        SpriteSheet moves = new SpriteSheet("entities/mole/move.png", 40, 40);

        move = new Animation(moves, 300);
        move.setAutoUpdate(false);
        
        SpriteSheet question = new SpriteSheet("entities/mole/move.png",40,40);
        bored = new Animation(question, 300);
        bored.setAutoUpdate(false);
    }

    public void addToLevel(Level l) {
        level = (ShadowLevel) l;
    }

    public Role getRole() {
        return Role.MONSTER;
    }

    public boolean hasIntensity(ShadowIntensity s) {
        return s == shadowStatus;
    }

    public void setIntensity(ShadowIntensity s) {
        shadowStatus = s;
    }

    public void onCollision(Entity obstacle) {
    	if (obstacle.getRole() == Role.PLAYER && status != Status.ASLEEP) {
            // he got ya
        	Player p = (Player) obstacle;
            p.getHit(this);
            cooldown = COOLDOWN_TIME;
            wander = 1;
            heading = (float)(Math.atan2(getCenterY()-p.getCenterY(), getCenterX()-p.getCenterX()));
        }
    	else{
        	Body b = (Body) obstacle;
        	b.repel(this);
            return;
    	}
        
    }

    public void removeFromLevel(Level l) {
        // TODO Auto-generated method stub

    }

    public void render(StateBasedGame game, Graphics g) {
        g.rotate(getCenterX(), getCenterY(), (float) Math.toDegrees(heading));
    	if(status == Status.ALERT){
            sniff.draw(getX(), getY(), getWidth(), getHeight());
    	}
    	if(status == Status.ASLEEP){
    		bored.draw(getX(), getY(), getWidth(), getHeight());
    	}
    	if(status == Status.CHASING){
            move.draw(getX(), getY(), getWidth(), getHeight());
    	}
    	if(status == Status.CONFUSED){
    		//do nothing right now
    	}
        g.resetTransform();

        // g.draw(shape);
    }

    public void update(StateBasedGame game, int delta) {
        timer += delta;
        sniff.update(delta);
        move.update(delta);
        //let's let the ubermoles stay awake for now
        if(level.getDayLight()==ShadowLevel.DayLightStatus.DAY
        		||level.getDayLight()==ShadowLevel.DayLightStatus.DAWN){
        	status = Status.ASLEEP;
        }
        else status = Status.ALERT;
        if(status == Status.ASLEEP) return;
        if(findTarget()&&cooldown<1)
        	seekTarget();
        else{
        	if(wander<100&&wander>0){
        		move(SPEED/2f,heading);
        		wander++;
        	}
        	else if(wander==100) 
        		wander = -100;
        	else if (wander<0) {
        		wander++;
        	}
        	else {
        		wander = 1;
        		heading = (float)(Math.random()*Math.PI*2);
        	}
        }
    	cooldown-=delta;
        testAndWrap();
    }

    private void seekTarget() {
        float[] d = new float[3];
        d[0] = CrashGeom.distance2(target, this);
        if(d[0]>500&&target.hasIntensity(ShadowIntensity.CASTSHADOWED)){
        	wander = 0;
        	return;
        }
        d[1] = d[0];
        d[2] = d[0];
        // if I'm left of my target
        if (getX() < target.getX()) {
            d[1] = CrashGeom
                    .distance2(target, getCenterX() + 800, getCenterY());
        } else {
            d[1] = CrashGeom.distance2(this, target.getCenterX() + 800, target
                    .getCenterY());
        }

        // if I'm above my target
        if (getY() < target.getY()) {
            d[2] = CrashGeom
                    .distance2(target, getCenterX(), getCenterY() + 600);
        } else {
            d[2] = CrashGeom.distance2(this, target.getCenterX(), target
                    .getCenterY() + 600);
        }

        heading = CrashGeom.calculateAngle(target, this);
        if (d[1] < d[0] || d[2] < d[0]) {
            heading += Math.PI;
        }

        move(SPEED, heading);
    }

    private boolean findTarget() {
        ShadowEntity[] entities = level.nearByEntities(this, 300);

        boolean lineOfSight = false;
        int i = 0;
        while (!lineOfSight && i < entities.length) {
            if (((Entity) entities[i]).getRole() == Role.PLAYER) {
                lineOfSight = level.lineOfSight(this, entities[i]);
            }
            i++;
        }
        i--;

        if (lineOfSight) {
            target = (Player) entities[i];
            status = Status.CHASING;
            move.restart();
            return true;
        }
        return false;
    }


    /* Move the shape a given amount across two dimensions. */
    private void move(float magnitude, float direction) {
        Vector2f d = Geom.calculateVector(magnitude, direction);
        shape.setCenterX(shape.getCenterX() + d.x);
        shape.setCenterY(shape.getCenterY() + d.y);
        xVelocity = d.x;
        yVelocity = d.y;
    }

    public void repel(Entity repellee) {
        Body b = (Body) repellee;
        double playerx = b.getCenterX();
        double playery = b.getCenterY();
        double dist_x = playerx - getCenterX();
        double dist_y = playery - getCenterY();
        double mag = Math.sqrt(dist_x * dist_x + dist_y * dist_y);
        double playradius = b.getWidth() / 2;
        double obstacleradius = getWidth() / 2;
        double angle = Math.atan2(dist_y, dist_x);
        double move = (playradius + obstacleradius - mag) * 1.5;
        b.move(Math.cos(angle) * move, Math.sin(angle) * move);
    }

    public boolean asleep() {
        return status == Status.ASLEEP;
    }

    public int getZIndex() {
        return 4;
    }

    public int compareTo(ShadowEntity s) {
        return getZIndex() - s.getZIndex();
    }

    public Shape castShadow(float direction, float depth) {
        float r = ((Circle) shape).radius;
        float h = 2 * depth * 1.6f;
        float x = getCenterX();
        float y = getCenterY();
        Transform t = Transform.createRotateTransform(direction + 3.14f, x, y);

        RoundedRectangle rr = new RoundedRectangle(getX(), getY(), r * 2, h, r);
        return rr.transform(t);
    }
    
	@Override
	public Vector2f getPosition() {
		// TODO Auto-generated method stub
		return new Vector2f(getCenterX(),getCenterY());
	}

}

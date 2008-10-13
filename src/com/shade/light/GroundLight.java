package com.shade.light;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.shadows.ShadowEntity;

public class GroundLight extends Body implements LightSource,ShadowEntity {
	
	private float myCastLength;
	private int side;
	private float intensity;
	
	public GroundLight(int x, int y, int s, float intense, int castL){
		side = s;
		shape = new Rectangle(0,0, side, side);
		shape.setLocation(x, y);

		intensity = intense;
		myCastLength = castL;
	}

	@Override
	public float getCastLength() {
		return myCastLength;
	}

	@Override
	public float getIntensity() {
		return intensity;
	}

	@Override
	public Vector2f getPosition() {
		return new Vector2f(getX(), getY());
	}

	@Override
	public void renderLight(Graphics g, int width, int height) {
		g.setColor(new Color(0,0,0,intensity));
		g.fill(new Circle(getCenterX(),getCenterY(),200)
			/*,new GradientFill(
						new Vector2f(0,0),new Color(0,0,0,intensity),
						new Vector2f(myCastLength,0), new Color(0,0,0,0)
						)*/
						);
		g.setColor(new Color(1.0f,1.0f,0,1.0f));
		g.fill(shape);
	}

	@Override
	public void update(StateBasedGame game, int delta) {
	}

	@Override
	public void addToLevel(Level l) {
		//l.add(this);
	}

	@Override
	public Role getRole() {
		return Role.MOVEABLE;
	}

	@Override
	public void onCollision(Entity obstacle) {
		//special movement method
		if(obstacle.getRole() == Role.PLAYER){
			Body b = (Body) obstacle;
			Vector2f vect = b.getVelocity();
			double velx = vect.x;
			double vely = vect.y;
			double playerx = b.getCenterX();
			double playery = b.getCenterY();
			//determine overlap
			double right = playerx-b.getWidth()/2-(getCenterX()+getWidth()/2);
			double left = playerx+b.getWidth()/2-(getCenterX()-getWidth()/2);
			double top = playery-b.getHeight()/2-(getCenterY()+getHeight()/2);
			double bottom = playery+b.getHeight()/2-(getCenterY()-getHeight()/2);
			double minx = Math.min(Math.abs(right), Math.abs(left));
			double miny = Math.min(Math.abs(top), Math.abs(bottom));
			if(minx<miny){
				//if we move, move AWAY from the block.
				if(Math.abs(playerx-getCenterX()-velx)>Math.abs(playerx-getCenterX()))
					velx = -velx;
				move(-velx, 0);
			}
			else{
				if(Math.abs(playery-getCenterY()-vely)>Math.abs(playery-getCenterY())){
					vely = -vely;
				}
				move(0,-vely);
			}
		}
		else{
			Body b = (Body) obstacle;
			b.repel(this);
		}
		
	}

	@Override
	public void removeFromLevel(Level l) {
	}

	@Override
	public void repel(Entity repellee) {
		Body b = (Body) repellee;
		Vector2f vect = b.getVelocity();
		double velx = vect.x;
		double vely = vect.y;
		double playerx = b.getCenterX();
		double playery = b.getCenterY();
		//determine overlap
		double right = playerx-b.getWidth()/2-(getCenterX()+getWidth()/2);
		double left = playerx+b.getWidth()/2-(getCenterX()-getWidth()/2);
		double top = playery-b.getHeight()/2-(getCenterY()+getHeight()/2);
		double bottom = playery+b.getHeight()/2-(getCenterY()-getHeight()/2);
		double minx = Math.min(Math.abs(right), Math.abs(left));
		double miny = Math.min(Math.abs(top), Math.abs(bottom));
		if(minx<miny){
			//if we move, move AWAY from the block.
			if(Math.abs(playerx-getCenterX()-velx)<Math.abs(playerx-getCenterX()))
				velx = -velx;
			b.move(-velx, 0);
		}
		else{
			if(Math.abs(playery-getCenterY()-vely)<Math.abs(playery-getCenterY())){
				vely = -vely;
			}
			b.move(0,-vely);
		}
	}

	@Override
	public void render(StateBasedGame game, Graphics g) {
		g.setColor(new Color(1.0f,1.0f,0,1.0f));
		g.fill(shape);
	}

	@Override
	public int getZIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasIntensity(ShadowIntensity s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setIntensity(ShadowIntensity s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int compareTo(ShadowEntity arg0) {
		// TODO Auto-generated method stub
		return 0;
	}


}

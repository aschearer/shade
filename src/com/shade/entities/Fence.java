package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.shadows.ShadowCaster;
import com.shade.util.Geom;

public class Fence extends Body implements ShadowCaster {

	private int depth;
	private Image sprite;

	public Fence(float x, float y, float w, float h, int d)
			throws SlickException {
		initShape(x, y, w, h);
		depth = d;
		initSprite(w, h);
	}

	private void initShape(float x, float y, float w, float h) {
		shape = new Rectangle(x, y, w, h);
	}

	private void initSprite(float w, float h) throws SlickException {
		String path = "entities/fence/fence.vertical.png";
		if (w > h) {
			path = "entities/fence/fence.horizontal.png";
		}
		sprite = new Image(path);
	}

	public Shape castShadow(float direction) {
		Vector2f v = Geom.calculateVector(depth * 10, direction);

		Transform t = Transform.createTranslateTransform(v.x, v.y);
		Polygon extent = (Polygon) shape.transform(t);

		int index = 0;

		if (v.y > 0) { // bottom
			if (v.x > 0) { // right
				index = 0;
			} else { // left
				index = 1;
			}
		} else { // top
			if (v.x > 0) { // right
				index = 3;
			} else { // left
				index = 2;
			}
		}

		Polygon shade = new Polygon();

		for (int i = 1; i < 4; i++) {
			int c = (4 + index + i) % 4;
			float[] p = extent.getPoint(c);
			shade.addPoint(p[0], p[1]);
		}

		for (int i = 3; i > 0; i--) {
			int c = (4 + index + i) % 4;
			float[] p = shape.getPoint(c);
			shade.addPoint(p[0], p[1]);
		}

		return shade;
	}

	public void addToLevel(Level l) {
		// TODO Auto-generated method stub
	}

	public Role getRole() {
		return Role.OBSTACLE;
	}

	public void onCollision(Entity obstacle) {
		// TODO Auto-generated method stub

	}

	public void removeFromLevel(Level l) {
		// TODO Auto-generated method stub

	}

	public void render(Graphics g) {
		sprite.draw(getX(), getY(), getWidth(), getHeight());
		// g.draw(shape);
	}

	public void update(StateBasedGame game, int delta) {
		// TODO Auto-generated method stub

	}

	public int getZIndex() {
		return depth;
	}

	public int compareTo(ShadowCaster s) {
		return (depth - s.getZIndex());
	}

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
		if(minx<miny)
			b.move(-velx, 0);
		else 
			b.move(0,-vely);
		

	}

}

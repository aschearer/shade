package com.shade.util;

import org.newdawn.slick.Color;
import org.newdawn.slick.ShapeFill;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

public class RadialGradient implements ShapeFill {
	
	private Vector2f none = new Vector2f(0,0);
	float centerx;
	float centery;
	Color startColor;
	Color endColor;
	float radius;
	
	public RadialGradient(float x, float y, float maxradius, Color start, Color end){
		centerx = x;
		centery = y;
		startColor = start;
		endColor = end;
		radius = maxradius;
	}

	public Color colorAt(Shape s, float x, float y) {
		return colorAt(x,y);
	}
	
	public Color colorAt(float x, float y){
		System.out.println("My center is "+centerx+", "+centery);
		System.out.println("Color at point "+x+", "+y+":");
		//the equation: (1-distance/radius)*color1 + (distance/radius)*color2
		float diffx = centerx-x;
		float diffy = centery-y;
		System.out.println("x distance: "+diffx+", y distance: "+diffy);
		float distance = (float)Math.sqrt(diffx*diffx+diffy*diffy);
		float factor = (distance/radius)*(distance/radius);
		System.out.println("Distance "+distance+", factor "+factor);
		return new Color(
				(1-factor)*startColor.r + (factor)*endColor.r,
				(1-factor)*startColor.g + (factor)*endColor.g,
				(1-factor)*startColor.b + (factor)*endColor.b,
				(1-factor)*startColor.a + (factor)*endColor.a
				);
	}

	public Vector2f getOffsetAt(Shape s, float x, float y) {
		return none;
	}

}

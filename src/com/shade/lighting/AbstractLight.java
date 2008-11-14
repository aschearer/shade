package com.shade.lightrender;

import org.newdawn.slick.geom.Vector2f;

public abstract class AbstractLight implements LightSource{
	Vector2f myPosition;
	int myCenterx;
	int myCentery;
	double angle;
	float intensity;
	public AbstractLight(int x, int y, float intense){
		myCenterx = x;
		myCentery = y;
		myPosition = new Vector2f(x,y);
		angle = 0;
		intensity = intense;
	}
	
	public float getShadowIntensity() {
		// TODO Auto-generated method stub
		return intensity;
	}
	

	public Vector2f getPosition() {
		// TODO Auto-generated method stub
		return myPosition;
	}



}

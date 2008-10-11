package com.shade.light;

import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

public class InfiniteLight implements LightSource {
	Vector2f myPosition;
	int myCenterx;
	int myCentery;
	int angle;
	public InfiniteLight(int x, int y){
		myCenterx = x;
		myCentery = y;
		myPosition = new Vector2f(x+10,y);
		angle = 0;
	}
	@Override
	public float getIntensity() {
		// TODO Auto-generated method stub
		return 0.9f;
	}
	
	public void update(StateBasedGame game, int delta){
		angle+=delta;
		double newx = myCenterx+100*Math.cos(4.0*angle/30000*Math.PI);
		double newy = myCentery+100*Math.sin(4.0*angle/30000*Math.PI);
		//System.out.println("Moving light to "+newx+", "+newy);
		myPosition.set((float)newx, (float)newy);
	}

	@Override
	public Vector2f getPosition() {
		// TODO Auto-generated method stub
		return myPosition;
	}

}

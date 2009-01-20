package com.shade.controls;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.entities.Player;
import com.shade.entities.Roles;
import com.shade.entities.mushroom.Mushroom;
import com.shade.lighting.LuminousEntity;
import com.shade.states.MasterState;

/**
 * A meter which can go from zero to a hundred.
 *  + The control should be notified whenever a mushroom is collected. 
 *  + The control should 
 *  + When the control reaches zero it will fire an event.
 * 
 * @author aas11
 * 
 */
public class MeterControl implements ControlSlice, MushroomCounter {

	public static final float BASE_DAMAGE = 0.1f;
	public static final float BASE_EXPONENT = 1.0005f;
	public static final float GOLD_SCORE_MULTIPLIER = 40;
	public static final float HEALTH_MULTIPLIER = 4;
	public static final float BAR_MAX = 40f;

    private LuminousEntity target;
    private ControlListener listener;
    private ScoreControl scorecard;

    private float x, y;
    private float value, totalAmountToAdd, rateOfChange;
    private float totalDecrement;
    private int totalTimeInSun;
    private static Image front, back, danger, current;
    private int timeInSun;
    private int dangerTimer;
    
    static {
        try {
            front = new Image("states/ingame/meter-front.png");
            back = new Image("states/ingame/meter-back.png");
            danger = new Image("states/ingame/meter-danger.png");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public MeterControl(float x, float y) throws SlickException {
        this.x = x;
        this.y = y;
        value = 0;
        totalAmountToAdd = 0;
        rateOfChange = 1;
        initResources();
        current = back;
    }

    private void initResources() throws SlickException {
        
    }

    public void track(LuminousEntity e) {
        target = e;
    }

    public void register(ControlListener c) {
        listener = c;
    }

    public void pass(ScoreControl card) {
        scorecard = card;
    }

    public void render(StateBasedGame game, Graphics g) {
        current.draw(x, y);
        
        float w = front.getWidth();
        float h = front.getHeight();
        float adjustment = h - (h * (value / BAR_MAX));
        front.draw(x, y + adjustment, x + w, y + h, 0, adjustment, w, h);
    }

    public void update(StateBasedGame game, int delta) {
        if (value == 0) {
            //listener.fire(this);
        }
        if(value >BAR_MAX) {
            //not sure why this isn't player specific right now. It wil be form now on.
            //TODO: if this shold go somewhere else tell me!
            Player p = (Player) target;
            p.setSpeed(Player.MAX_SPEED*1.2f);
            p.sparkle();
        }
        else {
        	
            Player p = (Player) target;
            p.unsparkle();
            if(p.getSmokeCount()<timeInSun){
            	p.setSpeed((float)Math.max(0,value/BAR_MAX)*(Player.MAX_SPEED-Player.MIN_SPEED)+Player.MIN_SPEED);
            }
        }
        //TODO: move this somwhere
        int scale = 60;
        if (target != null && target.getLuminosity() > MasterState.SHADOW_THRESHOLD) {
            decrement(delta);
            //not sure why this isn't player specific right now. It wil be form now on.
            //TODO: if this shold go somewhere else tell me!
            Player p = (Player) target;
            if(p.getSmokeCount()*scale<timeInSun){
            	p.setSmokeCount((int)Math.pow(1.12,timeInSun/scale)-1);
            }
            
        } else {
            timeInSun = Math.max(timeInSun-delta,0);
            Player p = (Player) target;
            if(p.getXVelocity()+p.getXVelocity()==0) timeInSun*=0.8;
            if(p.getSmokeCount()*scale>timeInSun){
            	p.setSmokeCount((int)Math.pow(1.12,timeInSun/scale)-1);
            }
        }
        
        if (totalAmountToAdd > 0) {
            value += .1f * rateOfChange;
            totalAmountToAdd -= .1f * rateOfChange;
        } else {
            rateOfChange = 1;
        }
        clamp();
        
        if (value < 40) {
            dangerTimer += delta;
            if (dangerTimer > 200) {
                dangerTimer = 0;
                current = (current == danger) ? back : danger;
            }
        } else if (current == danger) {
            current = back;
        }
    }
    
    public void awardBonus() {
        totalAmountToAdd += 20;
    }

    public void onCollect(Mushroom shroomie) {
        valueMushroom(shroomie);
        if (totalAmountToAdd > 0) {
            //rateOfChange++;
        	rateOfChange = 3;
        }
    }

    private void clamp() {
        if (value < 0) {
            value = 0;
        }
        if (value > BAR_MAX+5) {
            scorecard.add(1);
            value--;
        }
    }

    private void valueMushroom(Mushroom shroomie) {
        totalAmountToAdd += shroomie.getValue() * HEALTH_MULTIPLIER;
        if (shroomie.isGolden()) {
            scorecard.add(shroomie.getValue() * GOLD_SCORE_MULTIPLIER);
        }
    }

	private void decrement(int delta) {
		timeInSun += delta;
		float damage = (float) Math.min(1f,
				Math.pow(BASE_EXPONENT, timeInSun) * BASE_DAMAGE);
		if (timeInSun < 1000)
			damage = Math.max(damage, 0.05f);
		value -= damage;
		clamp();
	}
    
    public float totalAmountLost() {
        return totalDecrement;
    }
    
    public int totalTimeInSun() {
        return (totalTimeInSun / 1000);
    }

    public void reset() {
        value = BAR_MAX;
        totalAmountToAdd = 0;
        rateOfChange = 1;
        totalDecrement = 0;
        totalTimeInSun = 0;
        //TODO: why is this so much casting?
        //TODO: figure out how we SHOULD handle this issue. Mock players are the devil.
        try{
        Player p = (Player) target;
        p.setSmokeCount(0);
        }
        catch(Exception e){
        	
        }
    }
}

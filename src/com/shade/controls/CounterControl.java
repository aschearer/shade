package com.shade.controls;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Animatable;
import com.shade.entities.Basket;
import com.shade.entities.Roles;
import com.shade.entities.mushroom.Mushroom;

public class CounterControl implements MushroomCounter, Animatable {

    private static final int X_OFFSET = 10;
    private static final int Y_OFFSET = 10;
    private static final int MUSHROOM_SCORE = 20;
    private static final int GOLD_MUSHROOM_SCORE = 3 * MUSHROOM_SCORE;

    // TODO: multiplier or lotsa points?
    public static final int TREASURE_SCORE = 1000;

    public int countDown, totalCount, goldMushrooms;

    private float x, y;
    private Image sprite;
    private TrueTypeFont font;
    private ScoreControl scorecard;
    private int multiplier; // more points for chains
    private Basket basket;
    private TrueTypeFont bubbleFont;
    private List<ScoreBubble> bubbles;

    public CounterControl(float x, float y, Image s, TrueTypeFont f, TrueTypeFont b) {
        this.x = x;
        this.y = y;
        sprite = s;
        multiplier = 1;
        font = f;
        bubbles = new ArrayList<ScoreBubble>();
        bubbleFont = b;
    }

    public void register(ScoreControl c) {
        scorecard = c;
    }

    public void track(Object b) {
        basket = (Basket) b;
    }

    public void onCollect(Mushroom shroomie) {
        if (shroomie.getRole() == Roles.TREASURE.ordinal()) {
            // WHEEE HACK! TODO: KILL HACK!
            scorecard.add(scorecard.getLevelScore());
        } else {
            int increment = 1;
            if (shroomie.isGolden()) {
                goldMushrooms++;
                increment += 3;
                scorecard.add(GOLD_MUSHROOM_SCORE * multiplier);
                createBubble(shroomie, GOLD_MUSHROOM_SCORE * multiplier);
            } else {
                scorecard.add(MUSHROOM_SCORE * multiplier);
                createBubble(shroomie, MUSHROOM_SCORE * multiplier);
            }
            totalCount += increment;
            if (countDown > 0) {
                countDown -= increment;
                if (countDown < 0) {
                    countDown = 0;
                }
            }
            multiplier = (shroomie.next == null) ? 1 : multiplier + 1;
        }
    }

    private void createBubble(Mushroom shroomie, int score) {
        float x = basket.getX() + basket.getWidth() - 5;
        float y = basket.getY();
        
//        if (shroomie.isGolden()) {
//            score += shroomie.getValue() * MeterControl.GOLD_SCORE_MULTIPLIER;
//        } else {
//            score += shroomie.getValue() * MeterControl.SCORE_MULTIPLIER;
//        }
        
        x += bubbles.size() * 5;
        bubbles.add(new ScoreBubble(x, y, score));
    }

    public void render(StateBasedGame game, Graphics g) {
        sprite.draw(x, y);
        float xBuffer = x + sprite.getWidth() + X_OFFSET;
        float yBuffer = y + Y_OFFSET;
        font.drawString(xBuffer, yBuffer, "" + countDown);
        for (int i = 0; i < bubbles.size(); i++) {
            bubbles.get(i).render(game, g);
        }
    }

    public void update(StateBasedGame game, int delta) {
        for (int i = 0; i < bubbles.size(); i++) {
            bubbles.get(i).update(game, delta);
        }
    }

    public void reset(int par) {
        bubbles.clear();
        countDown = par;
        totalCount = 0;
        goldMushrooms = 0;
        multiplier = 1;
    }

    public boolean parWasMet() {
        return countDown == 0;
    }

    private class ScoreBubble implements Animatable {

        private float x, y;
        private String score;
        private Color filter;

        public ScoreBubble(float x, float y, int score) {
            this.x = x;
            this.y = y;
            this.score = score + "";
            filter = new Color(Color.white);
        }

        public void render(StateBasedGame game, Graphics g) {
            bubbleFont.drawString(x, y, score, filter);
        }

        public void update(StateBasedGame game, int delta) {
            y -= 1;
            if (filter.a > 0) {
                filter.a -= .01f;
            }
            if (finished()) {
                bubbles.remove(this);
            }
        }

        public boolean finished() {
            return filter.a <= 0;
        }

    }
}

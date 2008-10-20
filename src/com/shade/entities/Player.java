package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.crash.Body;
import com.shade.entities.util.State;
import com.shade.entities.util.StateManager;
import com.shade.shadows.ShadowEntity;

public class Player extends Linkable implements ShadowEntity {

    private static final float SPEED = 1.4f;
    private static final int MUSHROOM_LIMIT = 3;

    private enum PlayerState {
        NORMAL, STUNNED
    };

    private StateManager manager;
    private Image normal;
    private ShadowIntensity shadowStatus;

    public Player(float x, float y) throws SlickException {
        initShape(x, y);
        initResources();
        initStates();
    }

    private void initShape(float x, float y) {
        shape = new Circle(x, y, 18);
    }

    private void initResources() throws SlickException {
        normal = new Image("entities/player/player.png");
    }

    private void initStates() {
        manager = new StateManager();
        manager.add(new NormalState());
        manager.add(new StunnedState());
    }

    private class NormalState implements State {

        public boolean equals(Object state) {
            return state == PlayerState.NORMAL;
        }

        public void enter() {
            // TODO Auto-generated method stub

        }

        public void onCollision(Entity obstacle) {
            if ((obstacle.getRole() == Role.MONSTER
                    && ((Monster) obstacle).isActive()) || (obstacle.getRole()==Role.BIRD && ((Bird) obstacle).isActive())) {
                Body b = (Body) obstacle;
                double xdiff = getCenterX() - b.getCenterX();
                double ydiff = getCenterY() - b.getCenterY();
                move(xdiff / 2, ydiff / 2);
                Linkable head = next;
                while (head != null) {
                    Mushroom m = (Mushroom) head;
                    int jump = 100;
                    head.move((Math.random() - 0.5) * jump,
                              (Math.random() - 0.5) * jump);
                    head = head.next;
                    m.detach();
                }
                manager.enter(PlayerState.STUNNED);
            }

            if (obstacle.getRole() == Role.BASKET && next != null) {
                next.prev = (Linkable) obstacle;
                next = null;
            }
        }

        public void render(Graphics g) {
            normal.drawCentered(getCenterX(), getCenterY());
        }

        public void update(StateBasedGame game, int delta) {
            testAndMove(game.getContainer().getInput(), delta);
            testAndWrap();
        }

        private void testAndMove(Input input, int delta) {
            xVelocity = 0;
            yVelocity = 0;
            if (input.isKeyDown(Input.KEY_LEFT)) {
                xVelocity--;
            }
            if (input.isKeyDown(Input.KEY_RIGHT)) {
                xVelocity++;
            }
            if (input.isKeyDown(Input.KEY_UP)) {
                yVelocity--;
            }
            if (input.isKeyDown(Input.KEY_DOWN)) {
                yVelocity++;
            }
            double mag = Math.sqrt(xVelocity * xVelocity + yVelocity
                    * yVelocity);
            // make it uniform speed
            xVelocity = (float) (1.0 * SPEED * xVelocity / mag);
            yVelocity = (float) (1.0 * SPEED * yVelocity / mag);
            if (mag != 0) {
                move(xVelocity, yVelocity);
            } else {
                xVelocity = 0;
                yVelocity = 0;
            }
        }
    }

    private class StunnedState implements State {

        private int timer;

        public boolean equals(Object state) {
            return state == PlayerState.STUNNED;
        }

        public void enter() {
            timer = 0;
        }

        public void onCollision(Entity obstacle) {
            // TODO Auto-generated method stub

        }

        public void render(Graphics g) {
            if (timer % 2 == 0) {
                normal.drawCentered(getCenterX(), getCenterY());
            }
        }

        public void update(StateBasedGame game, int delta) {
            timer += delta;
            if (timer > 1000) {
                manager.enter(PlayerState.NORMAL);
            }
        }

    }

    @Override
    public void attach(Linkable l) {
        if (next == null) {
            super.attach(l);
            return;
        }
        int i = 1;
        Linkable head = next;
        while (head.next != null) {
            i++;
            head = head.next;
        }
        if (i < MUSHROOM_LIMIT) {
            super.attach(l);
        }
    }

    public int getZIndex() {
        return 4;
    }

    public boolean hasIntensity(ShadowIntensity s) {
        return s == shadowStatus;
    }

    public void setIntensity(ShadowIntensity s) {
        shadowStatus = s;
    }

    public void addToLevel(Level l) {
        // TODO Auto-generated method stub

    }

    public Role getRole() {
        return Role.PLAYER;
    }

    public void onCollision(Entity obstacle) {
        manager.onCollision(obstacle);
        if (obstacle.getRole() == Role.OBSTACLE) {
            Body b = (Body) obstacle;
            b.repel(this);
        }
        if (obstacle.getRole() == Role.MONSTER) {
            obstacle.repel(this);
        }
        obstacle.repel(this);
    }

    public void removeFromLevel(Level l) {
        // TODO Auto-generated method stub

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

    public void render(StateBasedGame game, Graphics g) {
        manager.render(g);
    }

    public void update(StateBasedGame game, int delta) {
        manager.update(game, delta);
    }

    public int compareTo(ShadowEntity o) {
        return getZIndex() - o.getZIndex();
    }

    public boolean isStunned() {
        return manager.currentState().equals(PlayerState.STUNNED);
    }

}

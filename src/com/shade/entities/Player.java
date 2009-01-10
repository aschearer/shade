package com.shade.entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;
import com.shade.base.util.State;
import com.shade.base.util.StateManager;
import com.shade.crash.CrashLevel;
import com.shade.crash.Repelable;
import com.shade.entities.bird.Bird;
import com.shade.entities.mushroom.Mushroom;
import com.shade.lighting.LuminousEntity;

public class Player extends Linkable {

    private static final float SPEED = 2.5f;
    private static final int MUSHROOM_LIMIT = 3;
    private static final int PLAYER_HEIGHT = 3;

    private enum PlayerState {
        NORMAL, STUNNED
    };

    private StateManager manager;
    private Image normal;
    private float luminosity;
    private Sound register, damage;
    private boolean impeded;
    private float mileage;

    public Player(int x, int y) throws SlickException {
        initShape(x, y);
        initResources();
        initStates();
    }

    private void initShape(int x, int y) {
        shape = new Circle(x, y, 18);
    }

    private void initResources() throws SlickException {
        normal = new Image("entities/player/player.png");
        register = new Sound("entities/player/register.ogg");
        damage = new Sound("entities/player/hit.ogg");
    }

    private void initStates() {
        manager = new StateManager();
        manager.add(new NormalState());
        manager.add(new StunnedState());
    }

    private class NormalState implements State {

        public boolean isNamed(Object state) {
            return state == PlayerState.NORMAL;
        }

        public void enter() {

        }

        public int getRole() {
            // TODO Auto-generated method stub
            return 0;
        }

        public void onCollision(Entity obstacle) {
            if (obstacle.getRole() == Roles.BASKET.ordinal() && next != null) {
                Linkable m = next;
                next = null;
                Linkable l = (Linkable) obstacle;
                l.attach(m);
                register.play();
            }
            if (obstacle.getRole() == Roles.MONSTER.ordinal()) {
                manager.enter(Player.PlayerState.STUNNED);
                damage.play();
            }
            if (obstacle.getRole() == Roles.BIRD.ordinal()) {
                Bird b = (Bird) obstacle;
                if (b.isAttacking()) {
                    manager.enter(Player.PlayerState.STUNNED);
                    damage.play();
                }
            }

            if (obstacle.getRole() == Roles.SANDPIT.ordinal()) {
                impeded = true;
            }
        }

        public void render(StateBasedGame game, Graphics g) {
            normal.drawCentered(getXCenter(), getYCenter());
        }

        public void update(StateBasedGame game, int delta) {
            testAndMove(game.getContainer().getInput(), delta);
            testAndWrap();
            impeded = false;
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
            float speed = (impeded) ? SPEED / 2 : SPEED;
            xVelocity = (float) (1.0 * speed * xVelocity / mag);
            yVelocity = (float) (1.0 * speed * yVelocity / mag);
            if (mag != 0) {
                nudge(xVelocity, yVelocity);
                mileage += speed;
            } else {
                xVelocity = 0;
                yVelocity = 0;
            }
        }
    }

    private class StunnedState implements State {

        private int timer;
        private int failmer;

        public boolean isNamed(Object state) {
            return state == PlayerState.STUNNED;
        }

        public void enter() {
            scatterShrooms();
            timer = 0;
            failmer = 0;
        }

        // HACK! TODO: KILL HACK!
        private void scatterShrooms() {
            Linkable la = next;
            while (la != null) {
                Mushroom m = (Mushroom) la;
                la = la.next;
                m.scatter();

            }
        }

        public int getRole() {
            // TODO Auto-generated method stub
            return 0;
        }

        public void onCollision(Entity obstacle) {

        }

        public void render(StateBasedGame game, Graphics g) {
            if (timer < 500 || failmer % 5 > 2) {
                normal.drawCentered(getXCenter(), getYCenter());
            }

        }

        public void update(StateBasedGame game, int delta) {
            timer += delta;
            failmer++;
            if (timer > 500)
                testAndMove(game.getContainer().getInput(), delta);
            testAndWrap();
            if (timer > 1000) {
                manager.enter(PlayerState.NORMAL);
            }
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
            float speed = (impeded) ? SPEED / 2 : SPEED;
            xVelocity = (float) (1.0 * speed * xVelocity / mag);
            yVelocity = (float) (1.0 * speed * yVelocity / mag);
            if (mag != 0) {
                nudge(xVelocity, yVelocity);
            } else {
                xVelocity = 0;
                yVelocity = 0;
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
        boolean locked = next.getRole() == Roles.PICKER.ordinal();
        Linkable head = next;
        while (head.next != null) {
            if (head.getRole() == Roles.PICKER.ordinal()) {
                locked = true;
            }
            i++;
            head = head.next;
        }
        if (!locked && i < MUSHROOM_LIMIT) {
            super.attach(l);
        }
    }

    public Shape castShadow(float direction, float depth) {
        return null;
    }

    public float getLuminosity() {
        return luminosity;
    }

    public int getZIndex() {
        return PLAYER_HEIGHT;
    }

    public void setLuminosity(float l) {
        luminosity = l;
    }

    public void addToLevel(Level<?> l) {

    }
    
    public float totalMileage() {
        return mileage;
    }

    public int getRole() {
        return Roles.PLAYER.ordinal();
    }

    public void onCollision(Entity obstacle) {
        manager.onCollision(obstacle);
        if (obstacle.getRole() == Roles.OBSTACLE.ordinal()) {
            Repelable b = (Repelable) obstacle;
            b.repel(this);
        }
    }

    public void removeFromLevel(Level<?> l) {
        CrashLevel level = (CrashLevel) l;
        try {
            level.add(new MockPlayer((int) getXCenter(), (int) getYCenter()));
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public void render(StateBasedGame game, Graphics g) {
        manager.render(game, g);
    }

    public void update(StateBasedGame game, int delta) {
        manager.update(game, delta);
    }

    public int compareTo(LuminousEntity e) {
        return getZIndex() - e.getZIndex();
    }

}

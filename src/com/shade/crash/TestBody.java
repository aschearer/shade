package com.shade.crash;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import com.shade.base.Entity;
import com.shade.base.Level;

public class TestBody extends Body {

    public enum TestShape {
        CIRCLE
    };

    public TestBody(TestShape s, Vector2f p, float w, float h) {
        if (s == TestShape.CIRCLE) {
            shape = new Circle(p.x, p.y, w / 2);
        }
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
        // TODO Auto-generated method stub

    }

    public void update(StateBasedGame game, int delta) {
        // TODO Auto-generated method stub

    }

}

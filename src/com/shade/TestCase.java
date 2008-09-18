package com.shade;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;

import com.shade.crash.Grid;
import com.shade.crash.TestBody;
import com.shade.crash.TestBody.TestShape;
import com.shade.entities.Block;
import com.shade.entities.Mushroom;

public class TestCase extends BasicGame {

    public TestCase() {
        super("TEST CASE");
        // TODO Auto-generated constructor stub
    }
    
    Grid grid;
    private Block block;
    private TestBody test;
    private boolean intersecting;
    private Circle shape;
    private Mushroom mushroom;

    @Override
    public void init(GameContainer container) throws SlickException {
        grid = new Grid(8, 6, 100);
        
        block = new Block(100, 100, 100, 100, 10);
        
        grid.add(block);
        grid.update();
        
        test = new TestBody(TestShape.CIRCLE, new Vector2f(105, 150), 48, 48);
        
        shape = new Circle(105 - 24, 150 - 24, 24);
        
        mushroom = new Mushroom(105 - 24, 150 - 24);
        mushroom.shaded = true;
    }

    @Override
    public void update(GameContainer container, int delta)
            throws SlickException {
        intersecting = !grid.hasRoom(new Vector2f(105, 150), 48);
        mushroom.update(null, delta);
    }

    public void render(GameContainer container, Graphics g)
            throws SlickException {
        grid.debugDraw(g);
        g.drawString("Intersecting: " + intersecting, 10, 30);
        block.render(g);
        g.draw(shape);
        test.render(g);
        mushroom.render(g);
    }
    
    public static void main(String[] args) {
        try {
            AppGameContainer container = new AppGameContainer(new TestCase(), 800, 600, false);
            container.setTargetFrameRate(60);
            container.setVSync(true);
            container.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

}

package com.shade.crash;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

import com.shade.crash.TestBody.TestShape;

public class Grid {
    /** The number of cells across and high. */
    public final int width, height;
    /**
     * A cell's dimension. Bigger cells will have more bodies thus leading to
     * more unnecessary intersection tests. If you make your cells too small
     * you're spend all your time looking at empty cells.
     */
    public final int cellWidth, cellHeight;

    private LinkedList<Body> bodies;
    private Cell[][] cells;

//    private LinkedList<Ray> rays;

    /** Create a new grid with square cells. */
    public Grid(int width, int height, int cell) {
        this.width = width;
        this.height = height;
        this.cellWidth = cell;
        this.cellHeight = cell;
        bodies = new LinkedList<Body>();
//        rays = new LinkedList<Ray>();
        initCells();
    }

    private void initCells() {
        cells = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(this, x, y);
            }
        }
    }

    /**
     * Adding a body to the grid will result in it being checked for
     * intersection at each update.
     */
    public void add(Body b) {
        bodies.add(b);
    }

    /**
     * Remove a body from the grid so that it is not checked for intersection
     * automatically.
     */
    public void remove(Body b) {
        bodies.remove(b);
    }

    /**
     * Removes all the bodies from the grid. Useful if you want to restart the
     * game.
     */
    public void clear() {
        bodies.clear();
    }

    public void debugDraw(Graphics g) {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                g
                        .drawRect(x * cellWidth, y * cellHeight, cellWidth,
                                  cellHeight);
            }
        }
//        for (Ray r : rays) {
//            r.render(null, g);
//        }
    }

    public void update() {
        clearCells(cells);
        partitionBodies(bodies);
        testForIntersection(cells);
    }

    /**
     * Clear each cell so that we can repartition the physical space. This is
     * potentially a performance concern but can be fixed later.
     */
    private void clearCells(Cell[][] cells) {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                cells[x][y].clear();
            }
        }
    }

    /**
     * Place each body in a cell based on its position. This makes it easier to
     * test for intersection by reducing the number of bodies each body must be
     * checked against.
     */
    private void partitionBodies(Iterable<Body> bodies) {
        for (Body subject : bodies) {
            Cell target = getTargetCell(subject);
            target.add(subject);
        }
    }

    /**
     * Test each body for intersection against the other bodies in its cell. If
     * two bodies intersect notify them via the Body.collide method so they can
     * respond.
     */
    private void testForIntersection(Cell[][] cells) {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                cells[x][y].update();
            }
        }
    }

    private Cell point2cell(float x, float y) {
        /* Calculate which column and row the point falls under. */
        int column = (int) Math.floor(x / cellWidth);
        int row = (int) Math.floor(y / cellHeight);
        if (column < 0 || column >= width || row < 0 || row >= height) {
            return null;
        }
        Cell target = cells[column][row];
        return target;
    }

    private Cell getTargetCell(Body subject) {
        return point2cell(subject.getCenterX(), subject.getCenterY());
    }

    /**
     * Check each corner to see whether it is in a neighboring cell. For each
     * corner which resides in a neighboring cell return that cell so that we
     * can test for intersections between this body and the bodies in that cell.
     */
    protected Iterable<Cell> neighbors(Cell c, Body b) {
        Set<Cell> neighbors = new HashSet<Cell>();
        /*
         * Adjust the dimensions to fit inside a cell. This is for objects equal
         * to the size of a cell.
         */
        float width = b.getWidth() - 1;
        float height = b.getHeight() - 1;

        Cell topLeft = point2cell(b.getX(), b.getY());
        if (topLeft != null && !c.equals(topLeft)) {
            neighbors.add(topLeft);
        }

        Cell topRight = point2cell(b.getX() + width, b.getY());
        if (topRight != null && !c.equals(topRight)) {
            neighbors.add(topRight);
        }

        Cell bottomLeft = point2cell(b.getX(), b.getY() + height);
        if (bottomLeft != null && !c.equals(bottomLeft)) {
            neighbors.add(bottomLeft);
        }

        Cell bottomRight = point2cell(b.getX() + width, b.getY() + height);
        if (bottomRight != null && !c.equals(bottomRight)) {
            neighbors.add(bottomRight);
        }
        return neighbors;
    }

    /**
     * Return true if there a circle with radius r and center p does not
     * intersect with anything.
     * 
     * @param p
     * @return
     */
    public boolean hasRoom(Vector2f p, float r) {
        Body test = new TestBody(TestShape.CIRCLE, p, r, r);
        Cell target = getTargetCell(test);
        return (target != null && !target.testForIntersection(test));
    }

    /* Return true if there are no walls between one and two. */
    public boolean ray(Body one, Body two) {
        Ray ray = new Ray(one, two);
//        rays.add(ray);
        Vector2f direction = ray.getDirection();

        /* Current cell to examine. */
        int currentX = (int) Math.floor(one.getCenterX() / cellWidth);
        int currentY = (int) Math.floor(one.getCenterY() / cellHeight);
        /* Target cell. */
        int targetX = (int) Math.floor(two.getCenterX() / cellWidth);
        int targetY = (int) Math.floor(two.getCenterY() / cellHeight);

        /*
         * Hard to explain, please see
         * http://student.kuleuven.be/~m0216922/CG/images/raycastdelta.gif
         */
        float deltaX = (float) Math.sqrt(1 + (direction.y * direction.y)
                / (direction.x * direction.x));
        float deltaY = (float) Math.sqrt(1 + (direction.x * direction.x)
                / (direction.y * direction.y));

        /* Similar to deltaX, deltaY but reflects current position. */
        float sideX, sideY;
        /* Direction to step, -1 or 1. */
        int stepX, stepY;

        if (direction.x < 0) {
            stepX = -1;
            sideX = (one.getCenterX() - (currentX * cellWidth)) * deltaX;
        } else {
            stepX = 1;
            sideX = ((1 + currentX) * cellWidth - one.getCenterX()) * deltaX;
        }
        if (direction.y < 0) {
            stepY = -1;
            sideY = (one.getCenterY() - (currentY * cellHeight)) * deltaY;
        } else {
            stepY = 1;
            sideY = ((1 + currentY) * cellHeight - one.getCenterY()) * deltaY;
        }
        assert (sideX > 0);
        assert (sideX < cellWidth * width);
        assert (sideY > 0);
        assert (sideX < cellHeight * height);

        Body obstacle = null;
        while (obstacle == null && inBounds(currentX, currentY)) {
            obstacle = Collider
                    .testAndReturn(ray, cells[currentX][currentY].bodies, one);
            if (sideX < sideY) {
                sideX += deltaX * cellWidth;
                currentX += stepX;
            } else {
                sideY += deltaY * cellHeight;
                currentY += stepY;
            }
        }

        return (two == obstacle);
    }

    private boolean inBounds(int x, int y) {
        return (x >= 0 && x < width && y >= 0 && y < height);
    }
}

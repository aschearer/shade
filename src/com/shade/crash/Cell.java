package com.shade.crash;

import java.util.LinkedList;

public class Cell {

    /**
     * The grid this cell belongs to. Use this to make queries against the
     * entire set of cells. For instance, getting all this cells neighbors.
     */
    public final Grid grid;
    /**
     * The coordinates of this cell in the grid. This can be used to find
     * neighboring cells.
     */
    public final int x, y;
    /**
     * List of bodies in this cell. These bodies are checked against each other
     * for intersection. Sometimes it may be necessary to check these bodies
     * against neighboring cells, too. This is protected so a cell can access
     * its neighbor's bodies directly.
     */
    protected LinkedList<Body> bodies;

    public Cell(Grid grid, int x, int y) {
        this.grid = grid;
        this.x = x;
        this.y = y;
        bodies = new LinkedList<Body>();
    }

    /**
     * Add a body to this cell. This happens when the grid partitions itself,
     * placing each body into a single cell.
     */
    public void add(Body b) {
        bodies.add(b);
    }

    /**
     * Remove all the bodies from this cell. This is called before partitioning
     * the grid, effectively cleaning the slate. In the future, bodies may be
     * removed individually from cells to improve performance.
     */
    public void clear() {
        bodies.clear();
    }

    /**
     * Returns the number of bodies in this cell. This is useful for faking some
     * physical queries. For instance, when looking for a wall it may suffice
     * check if the cell is occupied.
     */
    public int size() {
        return bodies.size();
    }

    /**
     * Test each body in the cell for intersections with the other bodies. Also
     * test against neighboring cells if the body extends into a neighbor.
     */
    public void update() {
        LinkedList<Body> obstacles = new LinkedList<Body>(bodies);
        for (Body subject : bodies) {
            obstacles.remove(); /* Pop off the current body */
            Collider.testAndAlert(subject, obstacles);
            /* Check against neighbors */
            for (Cell neighbor : grid.neighbors(this, subject)) {
                Collider.testAndAlert(subject, neighbor.bodies);
            }
        }
    }

    /**
     * Returns true if the body intersects with any other bodies.
     * @param b
     * @return
     */
    public boolean testForIntersection(Body b) {
        if (Collider.testAndFlag(b, bodies)) {
            return true;
        }
        /* Check against neighbors */
        for (Cell neighbor : grid.neighbors(this, b)) {
            if (Collider.testAndFlag(b, neighbor.bodies)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Cell[" + x + "," + y + "]";
    }
}

package io.github.purpleloop.game.witchfantasy.model;

import io.github.purpleloop.gameengine.action.model.environment.AbstractCellObjectEnvironment;

/** Environment fog. */
public class Fog {

    /** Associate environment. */
    private AbstractCellObjectEnvironment environment;

    /** The revealed cells of the environment. */
    private boolean[][] revealed;

    /**
     * Constructor of the fog.
     * 
     * @param environment environment to associate
     */
    public Fog(AbstractCellObjectEnvironment environment) {

        this.environment = environment;
        revealed = new boolean[environment.getCellWidth()][environment.getCellHeight()];
    }

    /**
     * Reveals the neighborhood of a cell.
     * 
     * @param x abscissa of the cell
     * @param y ordinate of the cell
     */
    public void revealNeighborHood(int x, int y) {

        int rx;
        int ry;

        revealed[x][y] = true;

        for (int dy = -2; dy < 2; dy++) {
            for (int dx = -2; dx < 2; dx++) {

                rx = x + dx;
                ry = y + dy;

                if (environment.isValidCell(rx, ry)) {
                    revealed[rx][ry] = true;
                }
            }
        }
    }

    /**
     * @param x abscissa of the cell
     * @param y ordinate of the cell
     * @return true if the fog is revealed, false otherwise
     */
    public boolean isRevealed(int x, int y) {
        return revealed[x][y];
    }

}

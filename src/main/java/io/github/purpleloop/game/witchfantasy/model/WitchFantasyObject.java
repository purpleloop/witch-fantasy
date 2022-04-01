package io.github.purpleloop.game.witchfantasy.model;

import java.awt.Rectangle;

import io.github.purpleloop.game.witchfantasy.model.agent.PlayableCharacterAgent;
import io.github.purpleloop.gameengine.action.model.GameTimer;
import io.github.purpleloop.gameengine.action.model.environment.AbstractCellObjectEnvironment;
import io.github.purpleloop.gameengine.action.model.interfaces.IEnvironmentObjet;
import io.github.purpleloop.gameengine.action.model.objects.GameObject;
import io.github.purpleloop.gameengine.core.util.EngineException;
import io.github.purpleloop.gameengine.core.util.Location;

/** Models an object of the Witch Fantasy world. */
public class WitchFantasyObject extends GameObject {

    /** The objet's environment. */
    protected AbstractCellObjectEnvironment environment;

    /** The object speed. */
    private int speed;

    /** Animation delay in milliseconds. */
    private static final int ANIMATION_DELAY = 500;

    /** Index in the animation. */
    private int animationSequence;

    /** The animation timer. */
    private GameTimer animationTimer;

    /** The collision rectangle. */
    private Rectangle collisionRectangle;

    /**
     * Creates a WitchFantasy object.
     * 
     * @param env the environment
     */
    public WitchFantasyObject(AbstractCellObjectEnvironment env) {
        super();
        this.environment = env;

        this.animationSequence = 0;
        animationTimer = new GameTimer(ANIMATION_DELAY);

        collisionRectangle = new Rectangle();
    }

    /** Updates the animation sequence if necessary. */
    private void updateAnimation() {
        if (animationTimer.passed()) {
            animationTimer.reset();

            animationSequence++;
            if (animationSequence > 1) {
                animationSequence = 0;
            }
        }
    }

    @Override
    public int getAnimationSequence() {
        return animationSequence;
    }

    /**
     * Sets the object speed.
     * 
     * @param newSpeed speed value
     */
    public void setSpeed(int newSpeed) {
        this.speed = newSpeed;
    }

    /** @return the object's speed */
    public int getSpeed() {
        return speed;
    }

    @Override
    public void evolve() throws EngineException {
        move();
        updateAnimation();
    }

    /**
     * Make the object move in the environment space according to it's speed.
     * 
     * @throws EngineException in case of errors
     */
    public void move() throws EngineException {

        if (speed == 0) {
            return;
        }

        // Compute new coordinates of the object
        // from current ones according to speed and heading.
        int nx = (int) (xLoc + speed * orientation.getXStep());
        int ny = (int) (yLoc + speed * orientation.getYStep());

        // We must check if new coordinates are valid
        if (!environment.isObjectInBounds(nx, ny)) {

            // New location cannot be out of bounds -> We block the move
            return;
        }

        // If the move is blocked by a wall or something similar
        // we check if the object can slide and correct the orientation
        // temporarily
        if (!environment.isObjectAllowedAtLocation(this, nx, ny)) {

            if (environment.isObjectAllowedAtLocation(this, nx, yLoc)) {
                // the object can slide by keeping just horizontal component
                // of the movement
                ny = yLoc;
                orientation = orientation.keepHorizontalMove();

            } else if (environment.isObjectAllowedAtLocation(this, xLoc, ny)) {
                // the object can slide by keeping just vertical component
                // of the movement
                nx = xLoc;
                orientation = orientation.keepVerticalMove();

            }
        }

        // We check if there is no static object that blocks the target
        // location (nx, ny).
        if (environment.isObjectAllowedAtLocation(this, nx, ny)) {

            int ug = environment.getCellSize();

            // We must check if the object does not collide another object.
            // To do this, we use collision rectangles.

            // At the beginning, no collision has occurred yet.
            boolean collided = false;

            // We compute the collision rectangle resulting of the movement.
            Rectangle resultingCollisionRectangle = new Rectangle(nx, ny, ug - 1, ug - 1);

            // We examine each other object
            for (IEnvironmentObjet other : environment.getObjects()) {

                // If the object is different from this one
                if (other.getId() != getId()) {

                    // We get the other object's collision rectangle.
                    Rectangle otherCollisionRectangle = other.getCollisionRectangle();

                    // In the intersection exists, there is a collision.
                    if (resultingCollisionRectangle.intersects(otherCollisionRectangle)) {

                        // Each object is notified of the collision and the
                        // movement is marked
                        // as producing a collision
                        if (collides(other)) {
                            collided = true;
                        }
                        other.collides(this);
                    }
                }
            }

            // If there is no collision
            if (!collided) {

                // We update the coordinates
                xLoc = nx;
                yLoc = ny;

                // We update the collision rectangle
                collisionRectangle = resultingCollisionRectangle;

                // We check each cells on which the object enters
                int rx = xLoc % ug;
                int ry = yLoc % ug;

                // Compute cell location
                // Trick : local variables helps to prevent object location
                // disturbances due to cell effects.
                // Typically when jumping from a location to another.
                int cx = xLoc / ug;
                int cy = yLoc / ug;

                if (rx == 0) {
                    if (ry == 0) {
                        environment.reachingCell(this, cx, cy);
                    } else {
                        environment.reachingCell(this, cx, cy);
                        environment.reachingCell(this, cx, cy + 1);
                    }
                } else {
                    if (ry == 0) {
                        environment.reachingCell(this, cx, cy);
                        environment.reachingCell(this, cx + 1, cy);
                    } else {
                        environment.reachingCell(this, cx, cy);
                        environment.reachingCell(this, cx, cy + 1);
                        environment.reachingCell(this, cx + 1, cy);
                        environment.reachingCell(this, cx + 1, cy + 1);
                    }
                }

            }
        }

    }

    @Override
    public Rectangle getCollisionRectangle() {
        return collisionRectangle;
    }

    @Override
    public boolean collides(IEnvironmentObjet other) {

        if (hasName(PlayableCharacterAgent.WITCH_NAME)
                && other.hasName(WitchFantasyEnvironment.VILLAGER_NAME)) {

            // When the witch meets a villager
            ((WitchFantasyEnvironment) environment).agentsMeet();
            return false;
        }
        
        return false;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(super.toString()).append(", speed=").append(speed)
                .toString();
    }

    /**
     * Return the agent cell location.
     * 
     * <strong>Warning</strong>, calling this method between two cells may lead
     * to unexpected results.
     * 
     * @see {@link #isBetweenTwoCells()}
     * 
     * @return the agent cell location
     */
    public Location getCellLocation() {
        int cellSize = environment.getCellSize();
        return Location.getLocation(getXLoc() / cellSize, getYLoc() / cellSize);
    }

    /**
     * Tests if the agent position is between two cells.
     * 
     * @see {@link #getCellLocation()}
     * 
     * @return true if the agent is across more than one cell, false otherwise
     */
    protected boolean isBetweenTwoCells() {
        int cellSize = environment.getCellSize();
        int deltaX = xLoc % cellSize;
        int deltaY = yLoc % cellSize;
        return deltaX > 0 || deltaY > 0;
    }

}

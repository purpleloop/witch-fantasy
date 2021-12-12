package io.github.purpleloop.game.witchfantasy.model;

import io.github.purpleloop.commons.math.geom.BoundaryMode;
import io.github.purpleloop.commons.math.geom.CartesianVector2D;
import io.github.purpleloop.commons.math.geom.Vector2D;

/** Models a particle in a 2D environment. */
public class Particle {

    /** Base of the time to live. */
    private static final int BASE_TIME_TO_LIVE = 60;

    /** Randomness of the time to live. */
    private static final int TIME_TO_LIVE_RANDOMNESS = 40;

    /** The location of the particle. */
    private Vector2D location;

    /** The speed of the particle. */
    private Vector2D speed;

    /** Time to live. */
    private int timeToLive;

    /** The owning weather model. */
    private WeatherModel model;

    /**
     * Creates a particle at given coordinates within a given weather model.
     * 
     * @param model the weather model
     * @param x abscissa
     * @param y ordinate
     */
    public Particle(WeatherModel model, int x, int y) {

        this.model = model;
        this.location = new CartesianVector2D(x, y);
        this.speed = new CartesianVector2D(0, 0);
        this.timeToLive = BASE_TIME_TO_LIVE + model.random(TIME_TO_LIVE_RANDOMNESS);
    }

    /**
     * Updates the particle exposed to a given wind vector.
     * 
     * @param wind the wind vector
     * @return liveness of the particle (true if the particle "survives", false otherwise)
     */
    public boolean update(Vector2D wind) {

        timeToLive--;

        speed.add(wind);

        location.add(speed);

        int width = model.getWidth();
        int height = model.getHeight();
        location.bound(width, height, BoundaryMode.TRANSLATE);

        return timeToLive > 0;

    }

    /** @return abscissa */
    public int getX() {
        return (int) location.getX();
    }

    /** @return ordinate */
    public int getY() {
        return (int) location.getY();
    }

}

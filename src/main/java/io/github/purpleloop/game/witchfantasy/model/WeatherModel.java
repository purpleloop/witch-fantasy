package io.github.purpleloop.game.witchfantasy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.github.purpleloop.commons.math.geom.PolarVector2D;
import io.github.purpleloop.commons.math.geom.Vector2D;

/** Models the weather conditions of an environment. */
public class WeatherModel {

    /** Wind variation in percent. */
    private static final int WIND_VARIATION_PERCENT = 10;

    /** Weather condition changes in percent. */
    private static final int WEATHER_CHANGE_PERCENT = 1;

    /** Logger of the class. */
    private static final Log LOG = LogFactory.getLog(WeatherModel.class);

    /** Width of the model. */
    private int width;

    /** Height of the model. */
    private int height;

    /** Random generator. */
    private Random rnd;

    /** Current weather level. */
    private WeatherLevel weatherLevel;

    /** The current wind blowing in the area. */
    private Vector2D wind;

    /** Particles in the model. */
    private List<Particle> particles;

    /** A precipitation scale composed of some levels. */
    public enum WeatherLevel {

        /** Nice conditions, clear, no wind. */
        NONE(0, 0.0),

        /** Light precipitation, calm wind. */
        LIGHT(100, 1.0),

        /** Average precipitation, medium wind. */
        MODERATE(600, 2.0),

        /** Heavy precipitation, strong wind. */
        HIGH(1200, 3.0);

        /** Number of precipitation particles. */
        private int particlesCount;

        /** Wind speed. */
        private double windSpeed;

        /**
         * Create a weather level.
         * 
         * @param particlesCount Number of precipitation particles
         * @param windSpeed Wind speed
         */
        WeatherLevel(int particlesCount, double windSpeed) {
            this.particlesCount = particlesCount;
            this.windSpeed = windSpeed;
        }

        /**
         * Get the next stronger level than the current one, if any.
         * 
         * @return the next weather level
         */
        WeatherLevel increase() {

            WeatherLevel[] values = values();
            int next = ordinal() + 1;
            if (next < values.length) {
                return values[next];

            }
            return this;
        }

        /**
         * Get the next weaker level than the current one, if any.
         * 
         * @return the next weather level
         */
        WeatherLevel decrease() {

            int next = ordinal() - 1;
            if (next >= 0) {
                return values()[next];
            }
            return this;
        }

        /** @return the number of particles */
        int getParticlesCount() {
            return this.particlesCount;
        }

        /** @return the wind speed */
        double getWindSpeed() {
            return this.windSpeed;
        }
    }

    /**
     * Create a weather model.
     * 
     * @param width width of the environment
     * @param height height of the environment
     */
    public WeatherModel(int width, int height) {

        this.width = width;
        this.height = height;
        this.rnd = new Random();

        // No particles, wind & calm weather level a the start
        this.particles = new ArrayList<>();
        this.wind = new PolarVector2D(0, 0);
        this.weatherLevel = WeatherLevel.NONE;
    }

    /**
     * Adjust the number of particles of the model to the targeted amount.
     */
    private void adjustParticlesCount() {

        int currentCount = particles.size();
        int targetCount = weatherLevel.getParticlesCount();
        int distanceToTarget = targetCount - currentCount;

        if (distanceToTarget > 0) {
            // Need to create missing particles
            particles.add(new Particle(this, rnd.nextInt(width), rnd.nextInt(height)));

        } else if (distanceToTarget < 0) {
            // Need to remove extra particles
            particles.remove(0);
        }

    }

    /** @return width of the environment */
    public int getWidth() {
        return width;
    }

    /** @return height of the environment */
    public int getHeight() {
        return height;
    }

    /**
     * @return the particles
     */
    public List<Particle> getParticles() {
        ArrayList<Particle> result = new ArrayList<>();
        result.addAll(this.particles);
        return result;
    }

    /** Updates the weather model. */
    public void update() {

        synchronized (particles) {

            List<Particle> removedParticles = new ArrayList<>();

            for (Particle particle : particles) {
                boolean liveness = particle.update(wind);

                if (!liveness) {
                    removedParticles.add(particle);
                }
            }

            for (Particle particle : removedParticles) {
                particles.remove(particle);
            }

            // Changes the weather conditions
            if (rnd.nextInt(100) < WEATHER_CHANGE_PERCENT) {
                changeWeather();
            }

            adjustParticlesCount();

        } // Sync

    }

    /** Changes the weather conditions. */
    private void changeWeather() {

        // Default angle is downwards (on screen Pi/2)
        double angle = Math.PI / 2.0;
        if (wind.getNorm() > 0) {
            angle = wind.getAngle();
        }

        // Increase / steady / decrease the angle
        int rollAngle = rnd.nextInt(100);
        if (rollAngle < WIND_VARIATION_PERCENT) {
            angle += 0.05;
            if (angle > 2 * Math.PI) {
                angle = angle - 2 * Math.PI;
            }

        } else if (rollAngle < 2 * WIND_VARIATION_PERCENT) {
            angle -= 0.05;
            if (angle < -2 * Math.PI) {
                angle = angle + 2 * Math.PI;
            }
        }

        // Increase / steady / decrease the weather level
        int rollLevel = rnd.nextInt(100);
        if (rollLevel < WIND_VARIATION_PERCENT) {
            weatherLevel = weatherLevel.increase();
        } else if (rollLevel < 2 * WIND_VARIATION_PERCENT) {
            weatherLevel = weatherLevel.decrease();
        }

        wind.setPolar(weatherLevel.getWindSpeed(), angle);

        int angleDegrees = (int) Math.round((angle / 2.0 / Math.PI) * 360.0);

        LOG.debug("Weather switches to " + weatherLevel.name() + " with angle of " + angleDegrees
                + " degrees.");

    }

    /**
     * Get a random value from the weather model random generator.
     * 
     * @param bound generation bound
     * @return random value
     */
    public int random(int bound) {
        return rnd.nextInt(bound);
    }

}

package io.github.purpleloop.game.witchfantasy.model.agent;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.github.purpleloop.commons.direction.Direction;
import io.github.purpleloop.game.witchfantasy.WitchFantasyMapContents;
import io.github.purpleloop.game.witchfantasy.model.WitchFantasyAgent;
import io.github.purpleloop.game.witchfantasy.model.WitchFantasyEnvironment;
import io.github.purpleloop.gameengine.action.model.algorithms.PathFinder;
import io.github.purpleloop.gameengine.core.util.Location;

/** Models a villager agent. */
public class VillagerAgent extends WitchFantasyAgent {

    /** Logger of the class. */
    private static final Log LOG = LogFactory.getLog(VillagerAgent.class);

    /** An internal path finder. */
    private PathFinder pathFinder;

    /** A target location where to head for. */
    private Location targetLocation;

    /**
     * Constructor for a villager agent.
     * 
     * @param witchFantasyEnvironment the environment
     */
    public VillagerAgent(WitchFantasyEnvironment witchFantasyEnvironment) {
        super(witchFantasyEnvironment);

        pathFinder = new PathFinder(witchFantasyEnvironment, this);
        defineNextTarget();
    }

    @Override
    public void behave() {

        // Pragmatic choice : Decide direction to take only when exactly in a
        // cell to avoid to take size into account when moving.
        if (!isBetweenTwoCells() && targetLocation != null) {
            setOrientation(getOrientationForLocation(targetLocation));
            setSpeed(environment.getCellSize() / 20);
        }

    }

    /** Defines the next target (house). */
    public void defineNextTarget() {

        List<Location> houseLocations = environment
                .findAllCellsLocationsMatchingContents(WitchFantasyMapContents.HOUSE);

        int size = houseLocations.size();

        if (size > 0) {
            int houseLocationIndex = random.nextInt(size);
            this.targetLocation = houseLocations.get(houseLocationIndex);

            LOG.debug("Target location is house at " + targetLocation);
        } else {
            this.targetLocation = null;
            LOG.debug("No target");
        }
    }

    /**
     * Tests if the agent position is between two cells.
     * 
     * @return true if the agent is across more than one cell, false otherwise
     */
    private boolean isBetweenTwoCells() {
        int cellSize = environment.getCellSize();
        int deltaX = xLoc % cellSize;
        int deltaY = yLoc % cellSize;
        return deltaX > 0 || deltaY > 0;
    }

    /**
     * Get the direction for heading to a given target.
     * 
     * @param target the target location
     * @return direction to follow
     */
    public Direction getOrientationForLocation(Location target) {

        // The agent uses it's path finder to find the better direction to reach
        // the target
        pathFinder.reset();
        pathFinder.setTarget(target);
        pathFinder.propagate();
        return pathFinder.findBetterDirection();
    }

    /** @return the current target of this agent, if any */
    public Location getTarget() {
        return targetLocation;
    }

}

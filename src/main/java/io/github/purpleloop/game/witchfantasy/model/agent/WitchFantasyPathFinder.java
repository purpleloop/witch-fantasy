package io.github.purpleloop.game.witchfantasy.model.agent;

import io.github.purpleloop.commons.direction.Direction;
import io.github.purpleloop.commons.direction.Direction4;
import io.github.purpleloop.gameengine.action.model.algorithms.PathFinder;
import io.github.purpleloop.gameengine.action.model.environment.AbstractCellObjectEnvironment;
import io.github.purpleloop.gameengine.action.model.interfaces.IEnvironmentObjet;
import io.github.purpleloop.gameengine.action.model.level.LevelLink;
import io.github.purpleloop.gameengine.action.model.level.LocationJump;
import io.github.purpleloop.gameengine.core.util.Location;

/** An enhanced path finder that takes location jumps into account. */
public class WitchFantasyPathFinder extends PathFinder {

    public WitchFantasyPathFinder(AbstractCellObjectEnvironment environment,
            IEnvironmentObjet object) {
        super(environment, object);
    }

    /**
     * Propagation of the target value through the 2D space, taking environment
     * obstacles into account.
     */
    public void propagate() {

        Location locationToExplore;

        int currentLocX;
        int currentLocY;
        int currentValue;

        int neighborLocX;
        int neighborLocY;
        Location neighborLocation;

        // While there are locations to explore
        while (!openLocations.isEmpty()) {

            // Pick the next one, mark it as seen and explore from it
            locationToExplore = openLocations.remove(0);
            closedLocations.add(locationToExplore);

            currentLocX = locationToExplore.getX();
            currentLocY = locationToExplore.getY();
            currentValue = value[currentLocX][currentLocY];

            // For each neighbor
            for (Direction dir : Direction4.values()) {

                neighborLocX = (int) (currentLocX + dir.getXStep());
                neighborLocY = (int) (currentLocY + dir.getYStep());

                // If neighbor cell implies a jump of location, replace it's coordinates by the target location
                for (LevelLink link : environment.getLevel().getLinks()) {
                    if (link instanceof LocationJump) {

                        LocationJump jump = ((LocationJump) link);

                        if (jump.matches(neighborLocX, neighborLocY)) {
                            Location jumpLocation = jump.getDestinationLocation();
                            neighborLocX = jumpLocation.getX();
                            neighborLocY = jumpLocation.getY();
                        }
                    }
                }

                neighborLocation = Location.getLocation(neighborLocX, neighborLocY);

                // If neighbor is reachable, propagates the value with a decay
                if (environment.isObjectAllowedAtCell(object, neighborLocX, neighborLocY)
                        && (value[neighborLocX][neighborLocY] < currentValue - 1)
                        && !closedLocations.contains(neighborLocation)) {
                    openLocations.add(neighborLocation);
                    value[neighborLocX][neighborLocY] = currentValue - 1;
                }
            }
        }
    }

    /**
     * Searches for the better direction to move the object
     * 
     * Warning, the object must be at exact cell location and be no larger than
     * a cell.
     * 
     * @return the direction to take, a value in {@link Direction4}
     */
    public Direction findBetterDirection() {

        int cellSize = environment.getCellSize();

        // Here we expect an exact division
        int cellX = object.getXLoc() / cellSize;
        int cellY = object.getYLoc() / cellSize;

        int bestValue = value[cellX][cellY];
        Direction bestDirection = Direction.NONE;

        int testedX;
        int testedY;
        int testedValue;

        // Try each direction get
        for (Direction testedDirection : Direction4.values()) {

            testedX = (int) (cellX + testedDirection.getXStep());
            testedY = (int) (cellY + testedDirection.getYStep());

            // If tested implies a jump of location, replace it's coordinates by the target location            
            for (LevelLink link : environment.getLevel().getLinks()) {
                if (link instanceof LocationJump) {

                    LocationJump jump = ((LocationJump) link);

                    if (jump.matches(testedX, testedY)) {
                        Location jumpLocation = jump.getDestinationLocation();
                        testedX = jumpLocation.getX();
                        testedY = jumpLocation.getY();
                    }

                }
            }

            if (environment.isObjectAllowedAtCell(object, testedX, testedY)) {

                // Get the direction that minimizes the distance to the target
                testedValue = value[testedX][testedY];

                if (testedValue > bestValue) {
                    bestValue = testedValue;
                    bestDirection = testedDirection;
                }
            }
        }

        return bestDirection;
    }

}

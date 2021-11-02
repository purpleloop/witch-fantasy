package io.github.purpleloop.game.witchfantasy.model.agent;

import java.util.Optional;

import io.github.purpleloop.commons.direction.Direction;
import io.github.purpleloop.commons.direction.Direction8;
import io.github.purpleloop.commons.math.GeomUtils;
import io.github.purpleloop.game.witchfantasy.model.WitchFantasyAgent;
import io.github.purpleloop.game.witchfantasy.model.WitchFantasyEnvironment;
import io.github.purpleloop.gameengine.core.util.Location;

/** Models a villager agent. */
public class VillagerAgent extends WitchFantasyAgent {

	/**
	 * Constructor for a villager agent.
	 * 
	 * @param witchFantasyEnvironment the environment
	 */
	public VillagerAgent(WitchFantasyEnvironment witchFantasyEnvironment) {
		super(witchFantasyEnvironment);
	}

	@Override
	public void behave() {
		
		// Changes randomly it's direction towards a random location
		if (random.nextInt(50) <= 1) {
			Optional<Location> targetLocationOptional = envionment.findRandomAllowedLocationForObject(this);

			if (targetLocationOptional.isPresent()) {

				Location targetLocation = targetLocationOptional.get();
				setOrientation(getOrientationForLocation(targetLocation.getX() * envionment.getCellWidth(),
						targetLocation.getY() * envionment.getCellHeight()));
				setSpeed(envionment.getCellSize() / 20);
			}
		}

	}

	/** Get the direction for heading to a given location */
	public Direction getOrientationForLocation(int targetX, int targetY) {

		double bestDistance = Double.MAX_VALUE;
		Direction bestDirection = Direction.NONE;

		int testedX;
		int testedY;
		double testedDistance;

		// Try each direction get
		for (Direction testedDirection : Direction8.values()) {
			testedX = (int) (xLoc + testedDirection.getXStep());
			testedY = (int) (yLoc + testedDirection.getYStep());

			// Get the direction that minimizes the distance to the target
			testedDistance = GeomUtils.distance(testedX, testedY, targetX, targetY);
			if ((testedDistance <= bestDistance) && (envionment.isObjectAllowedAtLocation(this, testedX, testedY))) {
				bestDistance = testedDistance;
				bestDirection = testedDirection;
			}
		}
		return bestDirection;
	}

}

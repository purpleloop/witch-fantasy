package io.github.purpleloop.game.witchfantasy.model;

import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.github.purpleloop.commons.direction.Direction4;
import io.github.purpleloop.game.witchfantasy.WitchFantasyException;
import io.github.purpleloop.game.witchfantasy.WitchFantasyMapContents;
import io.github.purpleloop.game.witchfantasy.model.agent.PlayableCharacterAgent;
import io.github.purpleloop.gameengine.action.model.environment.AbstractCellObjectEnvironment;
import io.github.purpleloop.gameengine.action.model.interfaces.IAgent;
import io.github.purpleloop.gameengine.action.model.interfaces.IEnvironmentObjet;
import io.github.purpleloop.gameengine.action.model.interfaces.ISession;
import io.github.purpleloop.gameengine.action.model.level.IGameLevel;
import io.github.purpleloop.gameengine.core.util.EngineException;
import io.github.purpleloop.gameengine.core.util.Location;

/** The Witch-Fantasy environment is based on a 2D cell object environment. */
public class WitchFantasyEnvironment extends AbstractCellObjectEnvironment {
	
	/** Logger of the class. */
	private static final Log LOG = LogFactory.getLog(WitchFantasyEnvironment.class);
	
	/**
	 * Constructor of the environment.
	 * 
	 * @param session the game session
	 * @param level   the game level
	 */
	public WitchFantasyEnvironment(ISession session, IGameLevel level) throws EngineException {
		super(session, level);

		try {
			WitchFantasyAgent witchAgent = spawnControlledAgentIn(getStartLocation(),
					(WitchFantasyPlayer) session.getPlayers().get(0));
			addObject(witchAgent);
		} catch (WitchFantasyException e) {
			throw new EngineException("Error during the creation of the environment.", e);
		}

	}

	/**
	 * @return the start location in the map.
	 * @throws EngineException in case of problems
	 */
	private Location getStartLocation() throws EngineException {

		Optional<Location> startLocationOptional = findFirstCellLocationMatchingContents(
				WitchFantasyMapContents.START_PLACE);

		if (startLocationOptional.isEmpty()) {
			throw new EngineException("No start location could be found on the map.");
		}

		return startLocationOptional.get();

	}

	@Override
	protected void initFromGameLevel() {
		WitchFantasyGameLevel witchFantasyGameLevel = (WitchFantasyGameLevel) getLevel();

		WitchFantasyMapContents[][] gameLevelStorage = witchFantasyGameLevel.getStorage();

		int levelWidth = witchFantasyGameLevel.getWidth();
		int levelHeight = witchFantasyGameLevel.getHeight();
		initStorage(levelWidth, levelHeight);

		for (int y = 0; y < cellHeight; y++) {
			for (int x = 0; x < cellWidth; x++) {
				this.setCellContents(x, y, gameLevelStorage[x][y]);
			}
		}
	}

	@Override
	public boolean isObjectAllowedAtCell(IEnvironmentObjet testedObject, int x, int y) {

		WitchFantasyMapContents cellContentsCode = (WitchFantasyMapContents) getCellContents(x, y);

		if (testedObject instanceof IAgent) {
			// No agent can go through a block
			if (cellContentsCode == WitchFantasyMapContents.BLOCK) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void reachingCell(IEnvironmentObjet object, int x, int y) {

		WitchFantasyMapContents cellContents = (WitchFantasyMapContents) getCellContents(x, y);

		if (object instanceof PlayableCharacterAgent) {

			PlayableCharacterAgent playableCharacterAgent = (PlayableCharacterAgent) object;

			switch (cellContents) {

			case CHEST:
				// Wow change appearance test
				
				LOG.debug("Change appaearance");

				if (playableCharacterAgent.getAppearance() == WitchAppearance.NORMAL) {
					playableCharacterAgent.setAppearance(WitchAppearance.SPIDER);
				} else {
					playableCharacterAgent.setAppearance(WitchAppearance.NORMAL);
				}
				
				setCellContents(x, y, WitchFantasyMapContents.EMPTY);

				break;

			case KEY:
				// The agent grabs the key
				playableCharacterAgent.grab(WitchFantasyMapContents.KEY);
				setCellContents(x, y, WitchFantasyMapContents.EMPTY);
				break;

			case FOUNTAIN:
				// The agent reached the exit of the current level
				fireEnvironmentChanged(new WitchFantasyEvent(WitchFantasyEvent.EXIT_REACHED));
				break;

			default:
			}
		}

	}

	/**
	 * This method creates a controlled agent in a specific location of the
	 * environment.
	 * 
	 * @param loc    the location of the creation
	 * @param player the player that controls the agent
	 * @return the created agent
	 * @throws WitchFantasyException in case of error during the agent creation
	 */
	protected WitchFantasyAgent spawnControlledAgentIn(Location loc, WitchFantasyPlayer player)
			throws WitchFantasyException {

		int xl = loc.getX();
		int yl = loc.getY();

		PlayableCharacterAgent agt = new PlayableCharacterAgent(this, player);

		boolean isValidCell = isValidCell(xl, yl) && isObjectAllowedAtCell(agt, xl, yl);
		if (isValidCell) {
			agt.setName("witch");
			agt.setLoc(xl * cellSize, yl * cellSize);
			agt.setOrientation(Direction4.EAST);
			return agt;
		} else {
			throw new WitchFantasyException("Invalid cell for agent creation at location " + loc);
		}

	}

}

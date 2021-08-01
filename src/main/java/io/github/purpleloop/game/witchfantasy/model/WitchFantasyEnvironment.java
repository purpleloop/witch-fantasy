package io.github.purpleloop.game.witchfantasy.model;

import io.github.purpleloop.gameengine.action.model.environment.AbstractCellObjectEnvironment;
import io.github.purpleloop.gameengine.action.model.interfaces.IEnvironmentObjet;
import io.github.purpleloop.gameengine.action.model.interfaces.ISession;
import io.github.purpleloop.gameengine.action.model.level.IGameLevel;
import io.github.purpleloop.gameengine.core.util.EngineException;

/** The Witch-Fantasy environment is based on a 2D cell object environment. */
public class WitchFantasyEnvironment extends AbstractCellObjectEnvironment {

	/** Constructor.
	 * @param session the game session
	 * @param level the game level
	 */
	public WitchFantasyEnvironment(ISession session, IGameLevel level) throws EngineException {
		super(session, level);
	}

	@Override
	protected void initFromGameLevel() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isObjectAllowedAtCell(IEnvironmentObjet objetTeste, int x, int y) {
		return true;
	}

	@Override
	public void reachingCell(IEnvironmentObjet objet, int x, int y) {
		// TODO Auto-generated method stub
	}
	
}

package io.github.purpleloop.game.witchfantasy.model;

import io.github.purpleloop.gameengine.action.model.environment.AbstractObjectEnvironment;
import io.github.purpleloop.gameengine.action.model.interfaces.IAgent;
import io.github.purpleloop.gameengine.action.model.interfaces.ISession;
import io.github.purpleloop.gameengine.action.model.level.IGameLevel;

public class WitchFantasyEnvironment extends AbstractObjectEnvironment {

	public WitchFantasyEnvironment(ISession session, IGameLevel level) {
		super(session);
	}

	@Override
	public void dumpEnvironmentObjects() {		
	}

	@Override
	protected IAgent spawnAgentRandomly(String name) {
		// FIXME Should not be mandatory
		return null;
	}
	
}
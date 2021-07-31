package io.github.purpleloop.game.witchfantasy.model;

import io.github.purpleloop.gameengine.action.model.events.IGameEvent;
import io.github.purpleloop.gameengine.action.model.interfaces.IGameEngine;
import io.github.purpleloop.gameengine.action.model.session.BaseAbstractSession;
import io.github.purpleloop.gameengine.core.util.EngineException;

public class WitchFantasySession extends BaseAbstractSession {

	public WitchFantasySession(IGameEngine gameEngine) throws EngineException {
		super(gameEngine);
	}

	@Override
	public boolean isEnded() {
		return false;
	}

	@Override
	public void environmentChanged(IGameEvent event) {		
	}

	
}
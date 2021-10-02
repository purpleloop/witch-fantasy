package io.github.purpleloop.game.witchfantasy.model;

import io.github.purpleloop.gameengine.action.model.events.IGameEvent;
import io.github.purpleloop.gameengine.action.model.interfaces.IGameEngine;
import io.github.purpleloop.gameengine.action.model.session.BaseAbstractSession;
import io.github.purpleloop.gameengine.core.util.EngineException;

/** The game session. */
public class WitchFantasySession extends BaseAbstractSession {

	/** Is the session finished ? */
	protected boolean finished = false;

	/**
	 * Constructor of the session.
	 * 
	 * @param gameEngine the game engine
	 */
	public WitchFantasySession(IGameEngine gameEngine) throws EngineException {
		super(gameEngine);
	}

	@Override
	public void initSession() {
		finished = false;
		addPlayer(new WitchFantasyPlayer());
	}

	@Override
	public boolean isEnded() {
		return finished;
	}

	@Override
	public void environmentChanged(IGameEvent event) {

        if (event instanceof WitchFantasyEvent) {
        	WitchFantasyEvent gEvent = (WitchFantasyEvent) event;
            int code = gEvent.getCode();

            switch (code) {
            case WitchFantasyEvent.EXIT_REACHED:
                prepareLevelChange();
                break;        
            default:
                LOG.debug("Dropping unmanaged event code=" + code);
            }

        }
	}

}

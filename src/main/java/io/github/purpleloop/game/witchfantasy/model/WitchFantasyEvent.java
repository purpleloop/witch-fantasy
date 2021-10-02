package io.github.purpleloop.game.witchfantasy.model;

import io.github.purpleloop.gameengine.action.model.events.BaseGameEvent;

/** Events of the game. */
public class WitchFantasyEvent extends BaseGameEvent {

	/** The character reached an exit. */
    public static final int EXIT_REACHED = 1;

    /** Constructor of the event.
     * @param code event code
     */
    public WitchFantasyEvent(int code) {
        super(code);
    }

}

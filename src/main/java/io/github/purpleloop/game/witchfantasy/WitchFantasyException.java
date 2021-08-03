package io.github.purpleloop.game.witchfantasy;

/** A base exception class for the game errors. */
public class WitchFantasyException extends Exception {

	/** Serial tag. */
	private static final long serialVersionUID = -5478904055082766609L;

	/**
	 * Exception constructor.
	 * 
	 * @param message the error message
	 */
	public WitchFantasyException(String message) {
		super(message);
	}

}

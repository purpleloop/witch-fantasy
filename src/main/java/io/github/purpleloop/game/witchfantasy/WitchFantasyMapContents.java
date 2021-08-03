package io.github.purpleloop.game.witchfantasy;

import io.github.purpleloop.gameengine.action.model.environment.ICellContents;

/** Cell contents. */
public enum WitchFantasyMapContents implements ICellContents {

	/** An empty space or passage. */
	EMPTY(' '),

	/**
	 * A block that cannot be passed over (dense trees, strong wood fence, stone or
	 * brick wall, stone rock).
	 */
	BLOCK('#'),

	/** The start place where to . */
	START_PLACE('+');

	/** The char used to represent the contents in text files. */
	private char code;

	/**
	 * Private constructor for map contents.
	 * 
	 * @param code character code used in map descriptor
	 */
	WitchFantasyMapContents(char code) {
		this.code = code;
	}

	/**
	 * @return the character code used for this content in the map descriptor
	 */
	@Override
	public char getLevelChar() {
		return code;
	}

	/**
	 * Get a map content by character code.
	 * 
	 * @param code the character code of the content to find
	 * @return the map contents
	 */
	public static WitchFantasyMapContents getByChar(char code) throws WitchFantasyException {

		for (WitchFantasyMapContents cnt : values()) {
			if (cnt.getLevelChar() == code) {
				return cnt;
			}
		}
		throw new WitchFantasyException("Unsupported character " + code + ".");
	}

}

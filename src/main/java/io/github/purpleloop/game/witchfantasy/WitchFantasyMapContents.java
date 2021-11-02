package io.github.purpleloop.game.witchfantasy;

import io.github.purpleloop.gameengine.action.model.environment.ICellContents;

/** Cell contents. */
public enum WitchFantasyMapContents implements ICellContents {

	/** An empty space or passage. */
	EMPTY(' ', true),

	/**
	 * A block that cannot be passed over (dense trees, strong wood fence, stone or
	 * brick wall, stone rock).
	 */
	BLOCK('#', true),

	/** A chest. */
	CHEST('C', false),

	/** A fountain. */
	FOUNTAIN('F', false),

	/** A key. */
	KEY('K', false),

	/** The start place where to . */
	START_PLACE('+', false);

	/** The char used to represent the contents in text files. */
	private char code;

	/**
	 * True if the map contents has an adaptive appearance through the seasons,
	 * false otherwise.
	 */
	private boolean seasonal;

	/**
	 * Private constructor for map contents.
	 * 
	 * @param code     character code used in map descriptor
	 * @param seasonal if the map contents has an adaptive appearance through the
	 *                 seasons, false otherwise
	 */
	WitchFantasyMapContents(char code, boolean seasonal) {
		this.code = code;
		this.seasonal = seasonal;
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

	/**
	 * @return true if the map contents has an adaptive appearance through the
	 *         seasons, false otherwise.
	 */
	public boolean isSeasonal() {
		return seasonal;
	}

}

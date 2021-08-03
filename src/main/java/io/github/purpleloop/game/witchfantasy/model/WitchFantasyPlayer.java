package io.github.purpleloop.game.witchfantasy.model;

import io.github.purpleloop.gameengine.action.model.interfaces.IPlayer;

/**
 * Models a player and the associated played character.
 * 
 * For the moment, the game is single player and has only one character type,
 * that is our little witch. Maybe later we could choose to play a friend, a
 * familiar (typically a cat or something else).
 *
 */
public class WitchFantasyPlayer implements IPlayer {

	/** The name of the played agent. */
	private static final String WITCH_NAME = "witch";

	/** Maximal score value. */
	private static final int MAX_SCORE = 99999;

	/** Initial hope value. */
	private static final int INITIAL_HOPE = 2000;

	/** Current hope value for the character. */
	protected int hope;

	/** The player score. */
	protected int score;

	/**
	 * Creates the player.
	 */
	public WitchFantasyPlayer() {
		this.score = 0;
		this.hope = INITIAL_HOPE;
	}

	/** @return name of the character */
	public String getName() {
		return WITCH_NAME;
	}
	
	/**
	 * add a value to the score.
	 * 
	 * Score maximal value is {@link WitchFantasyPlayer#MAX_SCORE}.
	 * 
	 * @param value value to add
	 */
	public void addToScore(int value) {
		score += value;
		// Limits the score value
		if (score > MAX_SCORE) {
			score = MAX_SCORE;
		}
	}

	/** @return The score value */
	public int getScore() {
		return score;
	}

	/**
	 * Adds value to hope.
	 * 
	 * @param value the value to add
	 */
	public void addToLife(int value) {
		hope += value;
	}

	/** @return hope value */
	public int getHope() {
		return hope;
	}
}

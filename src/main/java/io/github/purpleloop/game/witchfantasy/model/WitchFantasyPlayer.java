package io.github.purpleloop.game.witchfantasy.model;

import io.github.purpleloop.game.witchfantasy.model.agent.Carrier;
import io.github.purpleloop.game.witchfantasy.model.agent.Inventory;
import io.github.purpleloop.gameengine.action.model.environment.ICellContents;
import io.github.purpleloop.gameengine.action.model.interfaces.IPlayer;

/**
 * Models a player and the associated played character.
 * 
 * For the moment, the game is single player and has only one character type,
 * that is our little witch. Maybe later we could choose to play a friend, a
 * familiar (typically a cat or something else).
 *
 */
public class WitchFantasyPlayer implements IPlayer, Carrier {

	/** The name of the played agent. */
	private static final String WITCH_NAME = "witch";

	/** Maximal score value. */
	private static final int MAX_SCORE = 99999;
	
	/** Initial hope value. */
	private static final int INITIAL_HOPE = 2000;

	/** The player score. */
	protected int score;
	
	/** Current hope value for the character. */
	protected int hope;

	/** Inventory carried by the character. */
	private Inventory inventory;

	/**
	 * Creates the player.
	 */
	public WitchFantasyPlayer() {
		this.score = 0;
		this.hope = INITIAL_HOPE;
		this.inventory = new Inventory();
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

	@Override
	public boolean carries(ICellContents object) {
		return inventory.contains(object);
	}

	@Override
	public boolean drop(ICellContents object) {
		return inventory.drop(object);
	}

	@Override
	public void grab(ICellContents object) {
		inventory.add(object);
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}
	
}

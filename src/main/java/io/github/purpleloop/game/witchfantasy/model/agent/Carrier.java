package io.github.purpleloop.game.witchfantasy.model.agent;

import io.github.purpleloop.gameengine.action.model.environment.ICellContents;

/** Interface for a carrier agent. */
public interface Carrier {

	/**
	 * Does the character carry the given object ?
	 * 
	 * @param object the object to test
	 * @return true if the character carries the object, false otherwise
	 */
    boolean carries(ICellContents object);

	/**
	 * Drop the given object from the inventory.
	 * 
	 * @param object the object to drop
	 * @return true if the drop was successful, false otherwise
	 */
    boolean drop(ICellContents object);

	/**
	 * The agent grabs the given object.
	 * 
	 * @param object the object to grab
	 */
    void grab(ICellContents object);

    /** @return the agent inventory */
    Inventory getInventory();
}

package io.github.purpleloop.game.witchfantasy.model.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.github.purpleloop.gameengine.action.model.environment.ICellContents;

/** Inventory for element hold by an player / controlled agent. */
public class Inventory {

	/** Class logger. */
	private static final Log LOG = LogFactory.getLog(Inventory.class);

	/** Maximum size of the inventory. */
	public static final int MAX_SIZE = 50;

	/** Contents of the inventory. */
	private List<ICellContents> contents;

	/** Create an inventory. */
	public Inventory() {
		this.contents = new ArrayList<>();
	}

	/**
	 * Does the inventory contain the given object ?
	 * 
	 * @param object the object to test
	 * @return true if the object is in the inventory, false otherwise
	 */
	public boolean contains(ICellContents object) {
		return contents.contains(object);
	}

	/**
	 * Add add object to the inventory.
	 * 
	 * @param object the object to add
	 * @return true if the object was successfully added to the inventory, false otherwise
	 */
	public boolean add(ICellContents object) {
		if (isFull()) {
			LOG.debug("Inventory is full, unable to add object " + object);
			return false;
		}

		this.contents.add(object);
		return true;
	}

	/** @return true if the inventory is full, false otherwise */
	private boolean isFull() {
		return contents.size() >= MAX_SIZE;
	}

	/**
	 * Drops an object from the inventory.
	 * 
	 * @param object the object to drop
	 * @return true if the drop was successful, false otherwise
	 */
	public boolean drop(ICellContents object) {

		int idx = contents.indexOf(object);
		if (idx == -1) {
			return false;
		}

		contents.remove(idx);
		return true;
	}

	/** @return the inventory contents */
	public List<ICellContents> getContents() {
		return Collections.unmodifiableList(contents);
	}
}

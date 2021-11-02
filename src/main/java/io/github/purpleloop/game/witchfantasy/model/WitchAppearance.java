package io.github.purpleloop.game.witchfantasy.model;

import io.github.purpleloop.gameengine.action.model.objects.IAppearance;

/** Witch appearances. */
public enum WitchAppearance implements IAppearance {

	/** Start appearance, as an apprentice. */
	APPRENTICE("apprentice"),

	/** Appearance, as a witch. */
	WITCH("witch"),	
	
	/** Familiar appearance, as a black spider. */
	SPIDER("spider");

	// Other appearances : black crow ?

	/** The name of the appearance. */
	private String name;

	/**
	 * Creates an appearance.
	 * 
	 * @param name The name of the appearance.
	 */
	WitchAppearance(String name) {
		this.name = name;
	}

	/** @return The name of the appearance. */
	public String getName() {
		return name;
	}

}

package io.github.purpleloop.game.witchfantasy.model.agent;

import java.util.Set;

import io.github.purpleloop.commons.direction.Direction;
import io.github.purpleloop.commons.direction.Direction8;
import io.github.purpleloop.game.witchfantasy.model.WitchFantasyAgent;
import io.github.purpleloop.game.witchfantasy.model.WitchFantasyEnvironment;
import io.github.purpleloop.game.witchfantasy.model.WitchFantasyPlayer;
import io.github.purpleloop.gameengine.action.model.actions.IActionStore;
import io.github.purpleloop.gameengine.action.model.actions.SimpleActionStore;
import io.github.purpleloop.gameengine.action.model.environment.ICellContents;
import io.github.purpleloop.gameengine.action.model.interfaces.IControllableAgent;
import io.github.purpleloop.gameengine.action.model.interfaces.IController;

/** Models a Witch Fantasy playable (controlled) agent. */
public class PlayableCharacterAgent extends WitchFantasyAgent implements IControllableAgent, Carrier {

	/** Speed factor : one tenth of cell size - empirical value. */
	private static final int SPEED_FACTOR = 10;

	/** Action to go left. */
	private static final String ACTION_LEFT = "left";

	/** Action to go right. */
	private static final String ACTION_RIGHT = "right";

	/** Action to go up. */
	private static final String ACTION_UP = "up";

	/** Action to go down. */
	private static final String ACTION_DOWN = "down";

	/** The associated player. */
	private WitchFantasyPlayer player;

	/** The controller used by the player to control the agent. */
	private IController controller;

	/** The agent's action store (memory of what the agent is going to do). */
	private SimpleActionStore actionStore;
	
	/**
	 * Creates a playable character agent.
	 * 
	 * @param witchFantasyEnvironment the environment in which the agent evolves
	 * @param player                  the player that controls the agent
	 */
	public PlayableCharacterAgent(WitchFantasyEnvironment witchFantasyEnvironment, WitchFantasyPlayer player) {
		super(witchFantasyEnvironment);
		this.controller = null;
		this.actionStore = new SimpleActionStore();
		this.player = player;
	}

	/** @return the player that controls this agent */
	public WitchFantasyPlayer getPlayer() {
		return player;
	}

	/** @return the controller of the agent */
	public IController getController() {
		return controller;
	}

	/** @return does the agent have a controller */
	public boolean hasController() {
		return controller != null;
	}

	/** @param controller the controller of the agent */
	public void setController(IController controller) {
		this.controller = controller;
	}

	@Override
	public IActionStore getActionStore() {
		return actionStore;
	}

	@Override
	public void behave() {

		Set<String> actions = actionStore.getCurrentActions();

		Direction newOrientation = determineOrientation(actions);

		if (newOrientation != Direction.NONE) {
			setOrientation(newOrientation);
			setSpeed(envionment.getCellSize() / SPEED_FACTOR);
		} else {
			setSpeed(0);
		}

	}

	/**
	 * Determines the orientation according to the atomic actions.
	 * 
	 * @param actionSet the atomic action set
	 * @return the direction corresponding to actions set
	 */
	private Direction determineOrientation(Set<String> actionSet) {

		Direction orientation = Direction.NONE;

		if (actionSet.contains(ACTION_RIGHT)) {
			orientation = Direction8.EAST;
		}
		if (actionSet.contains(ACTION_UP)) {
			orientation = Direction8.NORTH;
		}
		if (actionSet.contains(ACTION_LEFT)) {
			orientation = Direction8.WEST;
		}
		if (actionSet.contains(ACTION_DOWN)) {
			orientation = Direction8.SOUTH;
		}
		if (actionSet.contains(ACTION_RIGHT) && actionSet.contains(ACTION_UP)) {
			orientation = Direction8.NORTH_EAST;
		}
		if (actionSet.contains(ACTION_RIGHT) && actionSet.contains(ACTION_DOWN)) {
			orientation = Direction8.SOUTH_EAST;
		}
		if (actionSet.contains(ACTION_LEFT) && actionSet.contains(ACTION_UP)) {
			orientation = Direction8.NORTH_WEST;
		}
		if (actionSet.contains(ACTION_LEFT) && actionSet.contains(ACTION_DOWN)) {
			orientation = Direction8.SOUTH_WEST;
		}

		return orientation;
	}

	@Override
	public boolean carries(ICellContents object) {
		return player.carries(object);
	}

	@Override
	public boolean drop(ICellContents object) {
		return player.drop(object);
	}

	@Override
	public void grab(ICellContents object) {
		player.grab(object);
	}

	@Override
	public Inventory getInventory() {
		return player.getInventory();
	}

}

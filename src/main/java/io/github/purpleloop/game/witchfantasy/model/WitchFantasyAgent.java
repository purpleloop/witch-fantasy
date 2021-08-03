package io.github.purpleloop.game.witchfantasy.model;

import io.github.purpleloop.gameengine.action.model.interfaces.IAgent;

/**
 * Models an agent of the Witch Fantasy world.
 * 
 * An agent is an object that has a rational behavior (goal oriented according
 * to rewards).
 */
public class WitchFantasyAgent extends WitchFantasyObject implements IAgent {

	/** Reward of a rational agent. */
	private double cummulativeReward;

	/** Constructor of Witch Fantasy agents.
	 * @param witchFantasyEnvironment the agent environment
	 */
	public WitchFantasyAgent(WitchFantasyEnvironment witchFantasyEnvironment) {
		super(witchFantasyEnvironment);
		cummulativeReward = 0.0;
	}

	@Override
	public double getCummulativeReward() {
		return cummulativeReward;
	}

	@Override
	public void reward(double value) {
		cummulativeReward = cummulativeReward + value;
	}

	@Override
	public void behave() {
		// basic agents are idle ... well for the moment
	}

}

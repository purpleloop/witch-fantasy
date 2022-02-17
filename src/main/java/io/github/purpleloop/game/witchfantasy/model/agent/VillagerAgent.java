package io.github.purpleloop.game.witchfantasy.model.agent;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.github.purpleloop.commons.direction.Direction;
import io.github.purpleloop.game.witchfantasy.WitchFantasyMapContents;
import io.github.purpleloop.game.witchfantasy.model.WitchFantasyAgent;
import io.github.purpleloop.game.witchfantasy.model.WitchFantasyEnvironment;
import io.github.purpleloop.gameengine.action.model.algorithms.PathFinder;
import io.github.purpleloop.gameengine.core.fsm.FiniteStateMachine;
import io.github.purpleloop.gameengine.core.fsm.MachineFact;
import io.github.purpleloop.gameengine.core.fsm.FSMNode;
import io.github.purpleloop.gameengine.core.fsm.MachineState;
import io.github.purpleloop.gameengine.core.util.Location;

/** Models a villager agent. */
public class VillagerAgent extends WitchFantasyAgent {

    /** Possible facts for a villager agents. */
    enum VillagerFacts implements MachineFact {

        /** Villager is hungry (missing food). */
        HUNGRY,

        /** Villager reached a field. */
        AT_FIELD,

        /** Villager has loaded enough food. */
        LOADED,

        /** Village arrived at home. */
        AT_HOME;
    }

    /** Possible states for a villager agent. */
    enum VillagerState implements MachineState {

        /** The villager returns home. */
        STATE_GO_TO_HOUSE,

        /** The villager harvests in a field for food. */
        STATE_HARVESTING,

        /** The villager walks towards a field. */
        STATE_GO_TO_FIELD,

        /** The villager is resting at home. */
        STATE_RESTING;
    }

    /** Logger of the class. */
    private static final Log LOG = LogFactory.getLog(VillagerAgent.class);

    /** Maximum load for the villager. */
    private static final int MAX_LOAD = 1000;

    /** An internal path finder. */
    private PathFinder pathFinder;

    /** The agent's house location. */
    private Location houseLocation;

    /** A target location where to head for. */
    private Location targetLocation;

    /** The finite state machines that rules the agent's behaviour. */
    private FiniteStateMachine automaton;

    /** The current food level. */
    private int foodLevel = 10;

    /**
     * Constructor for a villager agent.
     * 
     * @param witchFantasyEnvironment the environment
     */
    public VillagerAgent(WitchFantasyEnvironment witchFantasyEnvironment) {
        super(witchFantasyEnvironment);

        pathFinder = new WitchFantasyPathFinder(witchFantasyEnvironment, this);

        automaton = new FiniteStateMachine();
        automaton.newState(VillagerState.STATE_RESTING);
        automaton.setInitial(VillagerState.STATE_RESTING);
        automaton.newState(VillagerState.STATE_GO_TO_FIELD);
        automaton.newState(VillagerState.STATE_HARVESTING);
        automaton.newState(VillagerState.STATE_GO_TO_HOUSE);
        automaton.newTransition(VillagerState.STATE_RESTING, VillagerState.STATE_GO_TO_FIELD,
                VillagerFacts.HUNGRY);
        automaton.newTransition(VillagerState.STATE_GO_TO_FIELD, VillagerState.STATE_HARVESTING,
                VillagerFacts.AT_FIELD);
        automaton.newTransition(VillagerState.STATE_HARVESTING, VillagerState.STATE_GO_TO_HOUSE,
                VillagerFacts.LOADED);
        automaton.newTransition(VillagerState.STATE_GO_TO_HOUSE, VillagerState.STATE_RESTING,
                VillagerFacts.AT_HOME);
    }

    @Override
    public void behave() {

        // Pragmatic choice : Decide direction to take only when exactly in a
        // cell to avoid to take size into account when moving.
        if (!isBetweenTwoCells()) {

            automaton.process();

            FSMNode currentState = automaton.getCurrentNode();

            switch ((VillagerState) currentState.getState()) {
            case STATE_RESTING:

                if (foodLevel > 0) {
                    foodLevel--;
                } else {
                    automaton.addFact(VillagerFacts.HUNGRY);
                }

                break;

            case STATE_GO_TO_FIELD:
                targetField();
                break;

            case STATE_GO_TO_HOUSE:
                defineHomeTarget();
                break;

            case STATE_HARVESTING:
                foodLevel++;
                if (foodLevel == MAX_LOAD) {
                    automaton.addFact(VillagerFacts.LOADED);
                }

                break;

            default:
                break;
            }

            if (targetLocation != null) {
                setOrientation(getOrientationForLocation(targetLocation));
                setSpeed(environment.getCellSize() / 20);
            }

        }
    }

    /**
     * Defines the next target.
     * 
     * @param the targetContents to head for
     */
    public void defineNextTarget(WitchFantasyMapContents targetContents) {

        // Get the list of all possible targets that matches the required
        // content.
        List<Location> targetLocations = environment
                .findAllCellsLocationsMatchingContents(targetContents);

        int size = targetLocations.size();

        if (size > 0) {
            // Randomly choose a target
            int fieldLocationIndex = random.nextInt(size);
            setTargetLocation(targetLocations.get(fieldLocationIndex), targetContents.name());

        } else {
            setTargetLocation(null, StringUtils.EMPTY);
        }
    }

    /** Target a field. */
    public void targetField() {

        // If the agent has no target or targets anything else, set a field as
        // target.
        if (targetLocation == null || environment.getCellContents(targetLocation.getX(),
                targetLocation.getY()) != WitchFantasyMapContents.FIELD) {
            defineNextTarget(WitchFantasyMapContents.FIELD);
        }

    }

    /** Define the villager's home as target. */
    public void defineHomeTarget() {

        // If the agent has no target or targets anything else, set the home as
        // target.
        if (targetLocation == null || environment.getCellContents(targetLocation.getX(),
                targetLocation.getY()) != WitchFantasyMapContents.HOUSE) {
            setTargetLocation(houseLocation, "Villager's home");
        }
    }

    /**
     * Set the target location.
     * 
     * @param location the location to head to
     * @param description the target description
     */
    public void setTargetLocation(Location location, String description) {

        this.targetLocation = location;

        if (location == null) {
            LOG.debug("No target");
        } else {
            LOG.debug("Target location is " + description + " at " + targetLocation);
        }
    }

    /**
     * Get the direction for heading to a given target.
     * 
     * @param target the target location
     * @return direction to follow
     */
    public Direction getOrientationForLocation(Location target) {

        // The agent uses it's path finder to find the better direction to reach
        // the target
        pathFinder.reset();
        pathFinder.setTarget(target);
        pathFinder.propagate();
        return pathFinder.findBetterDirection();
    }

    /** @return the current target of this agent, if any */
    public Location getTarget() {
        return targetLocation;
    }

    /**
     * Sets the agent's house.
     * 
     * @param houseLocation the location of the agent's house.
     */
    public void setHouseLocation(Location houseLocation) {
        this.houseLocation = houseLocation;
    }

    /**
     * Notifies the agent of the arrival at a given content.
     * 
     * @param cellContents contents of the cell
     * @param x abscissa of the cell
     * @param y ordinate of the cell
     */
    public void notifyAtContent(WitchFantasyMapContents cellContents, int x, int y) {

        if (!getTarget().equals(x, y)) {
            // This is not a target
            return;
        }

        if (cellContents == WitchFantasyMapContents.FIELD) {
            automaton.addFact(VillagerFacts.AT_FIELD);
        }

        if (cellContents == WitchFantasyMapContents.HOUSE) {
            automaton.addFact(VillagerFacts.AT_HOME);
        }

    }

}

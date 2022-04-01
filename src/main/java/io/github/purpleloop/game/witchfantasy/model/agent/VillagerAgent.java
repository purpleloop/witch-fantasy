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
        AT_HOME,

        /** No more fields. */
        NO_MORE_FIELD;
    }

    /** Possible states for a villager agent. */
    enum VillagerState implements MachineState {

        /** The villager returns home. */
        GO_TO_HOUSE,

        /** The villager harvests in a field for food. */
        HARVESTING,

        /** The villager walks towards a field. */
        GO_TO_FIELD,

        /** The villager is resting at home. */
        RESTING,

        /** The villager is idle (no more fields to harvest). */
        IDLE;
    }

    /** Logger of the class. */
    private static final Log LOG = LogFactory.getLog(VillagerAgent.class);

    /** Maximum load for the villager. */
    private static final int MAX_LOAD = 1000;

    /** An internal path finder. */
    private PathFinder pathFinder;

    /** The agent's home location. */
    private Location homeLocation;

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
        automaton.newState(VillagerState.RESTING);
        automaton.setInitial(VillagerState.RESTING);
        automaton.newState(VillagerState.GO_TO_FIELD);
        automaton.newState(VillagerState.HARVESTING);
        automaton.newState(VillagerState.GO_TO_HOUSE);
        automaton.newState(VillagerState.IDLE);
        automaton.newTransition(VillagerState.RESTING, VillagerState.GO_TO_FIELD,
                VillagerFacts.HUNGRY);
        automaton.newTransition(VillagerState.GO_TO_FIELD, VillagerState.HARVESTING,
                VillagerFacts.AT_FIELD);
        automaton.newTransition(VillagerState.GO_TO_FIELD, VillagerState.IDLE,
                VillagerFacts.NO_MORE_FIELD);
        automaton.newTransition(VillagerState.HARVESTING, VillagerState.GO_TO_HOUSE,
                VillagerFacts.LOADED);
        automaton.newTransition(VillagerState.GO_TO_HOUSE, VillagerState.RESTING,
                VillagerFacts.AT_HOME);
    }

    @Override
    public void behave() {

        // Pragmatic choice : Decide direction to take only when exactly in a
        // cell to avoid to take size into account when moving.
        if (!isBetweenTwoCells()) {

            automaton.process();

            FSMNode currentState = automaton.getCurrentNode();

            if (LOG.isDebugEnabled()) {
                logStatus(currentState);
            }

            switch ((VillagerState) currentState.getState()) {
            case RESTING:

                if (foodLevel > 0) {
                    foodLevel--;
                } else {
                    automaton.addFact(VillagerFacts.HUNGRY);
                }

                break;

            case GO_TO_FIELD:

                // If the agent has no target or targets anything else, set a
                // field as target.
                if (targetLocation == null || environment.getCellContents(targetLocation.getX(),
                        targetLocation.getY()) != WitchFantasyMapContents.FIELD) {

                    boolean targetFound = defineNextTarget(WitchFantasyMapContents.FIELD);

                    if (!targetFound) {
                        automaton.addFact(VillagerFacts.NO_MORE_FIELD);
                    }

                }
                break;

            case GO_TO_HOUSE:
                defineHomeTarget();
                break;

            case HARVESTING:
                foodLevel++;
                if (foodLevel == MAX_LOAD) {
                    automaton.addFact(VillagerFacts.LOADED);

                    ((WitchFantasyEnvironment) environment).exhaustField(getCellLocation());
                }

                break;

            case IDLE:
                // Nothing to do
            default:
                break;
            }

            if (targetLocation != null) {

                setOrientation(getOrientationForLocation(targetLocation));
                setSpeed(environment.getCellSize() / 20);
            } else {
                setSpeed(0);
            }

        }
    }

    /**
     * Log the villager status.
     * 
     * @param currentState the current state
     */
    private void logStatus(FSMNode currentState) {

        LOG.debug("Villager " + getId() + " is in " + getCellLocation() + ", with target "
                + targetLocation + ", in state" + currentState);
    }

    /**
     * Defines the next target.
     * 
     * @param targetContents the targetContents to head for
     * @return true if target allocations was successful, false otherwise
     */
    public boolean defineNextTarget(WitchFantasyMapContents targetContents) {

        // Get the list of all possible targets that matches the required
        // content.
        List<Location> targetLocations = environment
                .findAllCellsLocationsMatchingContents(targetContents);

        int size = targetLocations.size();

        if (size > 0) {
            // Randomly choose a target
            int fieldLocationIndex = random.nextInt(size);
            setTargetLocation(targetLocations.get(fieldLocationIndex), targetContents.name());

            return true;

        } else {
            setTargetLocation(null, StringUtils.EMPTY);

            return false;
        }
    }

    /** Define the villager's home as target. */
    public void defineHomeTarget() {

        // If the agent has no target or targets anything else, set the home as
        // target.
        if (targetLocation == null || environment.getCellContents(targetLocation.getX(),
                targetLocation.getY()) != WitchFantasyMapContents.HOUSE) {
            setTargetLocation(homeLocation, "villager's home");
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
            LOG.info("Villager " + getId() + " has no target");
        } else {
            LOG.info("Villager " + getId() + " target is " + description + " at " + targetLocation);
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
     * Sets the agent's home.
     * 
     * @param houseLocation the location of a house.
     */
    public void setHomeLocation(Location houseLocation) {
        this.homeLocation = houseLocation;
    }

    /**
     * Notifies the agent of the arrival at a given content.
     * 
     * @param cellContents contents of the cell
     * @param x abscissa of the cell
     * @param y ordinate of the cell
     */
    public void notifyAtContent(WitchFantasyMapContents cellContents, int x, int y) {

        if (targetLocation == null || !targetLocation.equals(x, y)) {
            // This is not a target
            return;
        }

        LOG.info("Villager " + getId() + " reached target " + cellContents + " at "
                + targetLocation);

        targetLocation = null;

        if (cellContents == WitchFantasyMapContents.FIELD) {
            automaton.addFact(VillagerFacts.AT_FIELD);
        }

        if (cellContents == WitchFantasyMapContents.HOUSE) {
            automaton.addFact(VillagerFacts.AT_HOME);
        }

    }

    @Override
    public String getExtraDebugInfo() {
        return automaton.getCurrentNode().getState().name();
    }

    /**
     * Is he villager resting ?
     * 
     * @return true if the villager is in resting state, false otherwise
     */
    public boolean isAtHome() {
        return automaton.getCurrentNode().getState().equals(VillagerState.RESTING);
    }

}

package io.github.purpleloop.game.witchfantasy.model;

import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.github.purpleloop.commons.direction.Direction4;
import io.github.purpleloop.game.witchfantasy.WitchFantasyException;
import io.github.purpleloop.game.witchfantasy.WitchFantasyMapContents;
import io.github.purpleloop.game.witchfantasy.model.agent.PlayableCharacterAgent;
import io.github.purpleloop.game.witchfantasy.model.agent.VillagerAgent;
import io.github.purpleloop.gameengine.action.model.environment.AbstractCellObjectEnvironment;
import io.github.purpleloop.gameengine.action.model.interfaces.IAgent;
import io.github.purpleloop.gameengine.action.model.interfaces.IEnvironmentObjet;
import io.github.purpleloop.gameengine.action.model.interfaces.ISession;
import io.github.purpleloop.gameengine.action.model.level.IGameLevel;
import io.github.purpleloop.gameengine.action.model.level.LevelLink;
import io.github.purpleloop.gameengine.action.model.level.LocatedLevelLink;
import io.github.purpleloop.gameengine.action.model.level.LocationJump;
import io.github.purpleloop.gameengine.core.util.EngineException;
import io.github.purpleloop.gameengine.core.util.Location;

/** The Witch-Fantasy environment is based on a 2D cell object environment. */
public class WitchFantasyEnvironment extends AbstractCellObjectEnvironment {

    /** Dummy villager name. */
    private static final String VILLAGER_NAME = "villager";

    /** Logger of the class. */
    private static final Log LOG = LogFactory.getLog(WitchFantasyEnvironment.class);

    /** The weather model. */
    private WeatherModel weatherModel;

    /**
     * Is the weather model active. TODO How to activate custom environment
     * features from configuration ?
     */
    private boolean weatherModelActive = false;

    /** The associated season. */
    private Season season;

    /**
     * Constructor of the environment.
     * 
     * @param session the game session
     * @param level the game level
     */
    public WitchFantasyEnvironment(ISession session, IGameLevel level) throws EngineException {
        super(session, level);

        try {
            WitchFantasyAgent witchAgent = spawnControlledAgentIn(getStartLocation(),
                    (WitchFantasyPlayer) session.getPlayers().get(0));
            addObject(witchAgent);

            List<Location> housesLocations = findAllCellsLocationsMatchingContents(
                    WitchFantasyMapContents.HOUSE);

            for (Location houseLocation : housesLocations) {
                VillagerAgent villagerAgent = spawnVillagerAgentAt(houseLocation);
                
                // An agent's spawning house becomes his own.
                villagerAgent.setHouseLocation(houseLocation);
                addObject(villagerAgent);

            }

            if (weatherModelActive) {
                weatherModel = new WeatherModel(width, height);
            }

        } catch (WitchFantasyException e) {
            throw new EngineException("Error during the creation of the environment.", e);
        }

    }

    /**
     * @return the start location in the map.
     * @throws EngineException in case of problems
     */
    private Location getStartLocation() throws EngineException {

        Optional<Location> startLocationOptional = findFirstCellLocationMatchingContents(
                WitchFantasyMapContents.START_PLACE);

        if (startLocationOptional.isEmpty()) {
            throw new EngineException("No start location could be found on the map.");
        }

        return startLocationOptional.get();

    }

    @Override
    protected void initFromGameLevel() {
        WitchFantasyGameLevel witchFantasyGameLevel = (WitchFantasyGameLevel) getLevel();

        WitchFantasyMapContents[][] gameLevelStorage = witchFantasyGameLevel.getStorage();

        int levelWidth = witchFantasyGameLevel.getWidth();
        int levelHeight = witchFantasyGameLevel.getHeight();
        initStorage(levelWidth, levelHeight);

        for (int y = 0; y < cellHeight; y++) {
            for (int x = 0; x < cellWidth; x++) {
                this.setCellContents(x, y, gameLevelStorage[x][y]);
            }
        }

        this.season = witchFantasyGameLevel.getSeason();
    }

    @Override
    public boolean isObjectAllowedAtCell(IEnvironmentObjet testedObject, int x, int y) {

        WitchFantasyMapContents cellContentsCode = (WitchFantasyMapContents) getCellContents(x, y);

        if (testedObject instanceof IAgent) {

            // No agent can go through a block
            if (cellContentsCode == WitchFantasyMapContents.BLOCK) {
                return false;
            }

        }

        return true;
    }

    @Override
    public void reachingCell(IEnvironmentObjet object, int x, int y) throws EngineException {

        WitchFantasyMapContents cellContents = (WitchFantasyMapContents) getCellContents(x, y);

        if (object instanceof PlayableCharacterAgent) {

            PlayableCharacterAgent playableCharacterAgent = (PlayableCharacterAgent) object;

            switch (cellContents) {

            case CHEST:
                // Wow change appearance test

                LOG.debug("Reaching a chest, so change appaearance");

                if (playableCharacterAgent.getAppearance() == WitchAppearance.APPRENTICE) {
                    playableCharacterAgent.setAppearance(WitchAppearance.NORMAL);
                } else if (playableCharacterAgent.getAppearance() == WitchAppearance.NORMAL) {
                    playableCharacterAgent.setAppearance(WitchAppearance.SPIDER);
                } else {
                    playableCharacterAgent.setAppearance(WitchAppearance.NORMAL);
                }

                setCellContents(x, y, WitchFantasyMapContents.EMPTY);

                break;

            case KEY:
                // The agent grabs the key

                LOG.debug("Reaching a keg - grab it");

                playableCharacterAgent.grab(WitchFantasyMapContents.KEY);
                setCellContents(x, y, WitchFantasyMapContents.EMPTY);
                break;

            case FOUNTAIN:
                // The agent reached a fountain

                LOG.debug("Reaching a fountain in (" + x + "," + y + ") ... applying links");

                for (LevelLink link : getLevel().getLinks()) {
                    LocatedLevelLink abstractLocatedLevelLink = (LocatedLevelLink) link;
                    if (abstractLocatedLevelLink.matches(x, y)) {
                        abstractLocatedLevelLink.applyChanges(this, playableCharacterAgent);
                    }
                }

                break;

            default:
            }
        } else if (object instanceof VillagerAgent) {

            VillagerAgent villagerAgent = (VillagerAgent) object;

            switch (cellContents) {

            case HOUSE:
            case FIELD:
                villagerAgent.notifyAtContent(cellContents, x, y);
                break;

            case FOUNTAIN:
                // The agent reached a fountain

                LOG.debug("Reaching a fountain in (" + x + "," + y + ") ... applying links");

                for (LevelLink link : getLevel().getLinks()) {
                    LocatedLevelLink abstractLocatedLevelLink = (LocatedLevelLink) link;
                    if ((abstractLocatedLevelLink instanceof LocationJump)
                            && abstractLocatedLevelLink.matches(x, y)) {
                        abstractLocatedLevelLink.applyChanges(this, villagerAgent);
                    }
                }

                break;

            default:
            }

        }

    }

    @Override
    public void reachExit(String nextLevel, IEnvironmentObjet object) {

        if (object instanceof PlayableCharacterAgent) {
            fireEnvironmentChanged(new ExitReachedEvent(nextLevel));
        }
    }

    @Override
    public void locationJump(Location destinationLocation, IEnvironmentObjet object) {

        if (object instanceof WitchFantasyObject) {
            ((WitchFantasyObject) object).setLoc(destinationLocation.getX() * cellSize,
                    destinationLocation.getY() * cellSize);
        }

    }

    /**
     * This method creates a controlled agent in a specific location of the
     * environment.
     * 
     * @param loc the location of the creation
     * @param player the player that controls the agent
     * @return the created agent
     * @throws WitchFantasyException in case of error during the agent creation
     */
    protected WitchFantasyAgent spawnControlledAgentIn(Location loc, WitchFantasyPlayer player)
            throws WitchFantasyException {

        int xl = loc.getX();
        int yl = loc.getY();

        PlayableCharacterAgent agt = new PlayableCharacterAgent(this, player);

        boolean isValidCell = isValidCell(xl, yl) && isObjectAllowedAtCell(agt, xl, yl);
        if (isValidCell) {
            agt.setName("witch");
            agt.setLoc(xl * cellSize, yl * cellSize);
            agt.setOrientation(Direction4.EAST);
            return agt;
        } else {
            throw new WitchFantasyException("Invalid cell for agent creation at location " + loc);
        }

    }

    private VillagerAgent spawnVillagerAgentAt(Location spawningLocation) {

        VillagerAgent villagerAgent = new VillagerAgent(this);
        villagerAgent.setName(VILLAGER_NAME);
        villagerAgent.setLoc(spawningLocation.getX() * cellSize,
                spawningLocation.getY() * cellSize);
        villagerAgent.setOrientation(Direction4.WEST);
        return villagerAgent;
    }

    /**
     * Creates a villager agent at a random location of the environment.
     * 
     * @return the created agent
     */
    private Optional<VillagerAgent> spawnVillagerAgentRandomly() {

        VillagerAgent villagerAgent = new VillagerAgent(this);

        Optional<Location> spawningLocationOptional = findRandomAllowedLocationForObject(
                villagerAgent);

        if (spawningLocationOptional.isPresent()) {

            Location spawningLocation = spawningLocationOptional.get();
            villagerAgent.setName(VILLAGER_NAME);
            villagerAgent.setLoc(spawningLocation.getX() * cellSize,
                    spawningLocation.getY() * cellSize);
            villagerAgent.setOrientation(Direction4.WEST);

            return Optional.of(villagerAgent);
        }

        return Optional.empty();
    }

    /** @return The current season */
    public Season getSeason() {
        return this.season;
    }

    @Override
    protected void specificEvolve() throws EngineException {
        if (weatherModelActive) {
            weatherModel.update();
        }
    }

    /** @return an optional of the weather model. */
    public Optional<WeatherModel> getWeatherModel() {
        if (weatherModelActive) {
            return Optional.of(weatherModel);
        } else {
            return Optional.empty();
        }
    }

}

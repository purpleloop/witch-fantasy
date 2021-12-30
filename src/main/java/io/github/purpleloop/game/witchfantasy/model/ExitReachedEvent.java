package io.github.purpleloop.game.witchfantasy.model;

/** Event that occurs when an agent reaches an exit. */
public class ExitReachedEvent extends WitchFantasyEvent {

    /** Target level. */
    private String targetLevelId;

    /** Creates an exit event to the given level.
     * @param targetLevelId target level
     */
    public ExitReachedEvent(String targetLevelId) {
        super(EXIT_REACHED);
        this.targetLevelId = targetLevelId;
    }

    /** @return target level id */
    public String getTargetLevelId() {
        return targetLevelId;
    }
    
}

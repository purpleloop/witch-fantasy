package io.github.purpleloop.game.witchfantasy.model;

import java.util.Random;

import io.github.purpleloop.gameengine.action.model.dialog.DialogController;
import io.github.purpleloop.gameengine.action.model.dialog.DialogObserver;
import io.github.purpleloop.gameengine.action.model.events.IGameEvent;
import io.github.purpleloop.gameengine.action.model.interfaces.IController;
import io.github.purpleloop.gameengine.action.model.interfaces.IDialogEngine;
import io.github.purpleloop.gameengine.action.model.interfaces.IGameEngine;
import io.github.purpleloop.gameengine.action.model.session.BaseAbstractSession;
import io.github.purpleloop.gameengine.core.util.EngineException;

/** The game session. */
public class WitchFantasySession extends BaseAbstractSession {

    /** Is the session finished ? */
    protected boolean finished = false;

    /** Dialog ids. */
    private static final String[] DIALOG_IDS = { "1", "2", "3" };

    /** Random generator. */
    private Random rnd = new Random();

    /**
     * Constructor of the session.
     * 
     * @param gameEngine the game engine
     */
    public WitchFantasySession(IGameEngine gameEngine) throws EngineException {
        super(gameEngine);
    }

    @Override
    public void initSessionSpecific() {
        finished = false;
        addPlayer(new WitchFantasyPlayer());
    }

    @Override
    public boolean isEnded() {
        return finished;
    }

    @Override
    public void environmentChanged(IGameEvent event) {

        if (event instanceof WitchFantasyEvent) {
            WitchFantasyEvent witchFantasyEvent = (WitchFantasyEvent) event;
            int code = witchFantasyEvent.getCode();

            DialogController dialogController;
            switch (code) {
            case WitchFantasyEvent.EXIT_REACHED:
                setTargetLevelId(((ExitReachedEvent) witchFantasyEvent).getTargetLevelId());
                prepareLevelChange();
                break;

            case WitchFantasyEvent.ENGAGE_DIALOG:                
                
                IController controller = gameEngine.getController();
                currentEnvironment.removeController(controller);
                
                LOG.info("Engage a new dialog");                
                
                // Dialog is chosen randomly for the moment
                int index = rnd.nextInt(DIALOG_IDS.length);                
                IDialogEngine dialogEngine = gameEngine.getDialogEngine().get();
                dialogEngine.selectDialog(DIALOG_IDS[index]);

                dialogController = dialogEngine.getDialogController();
                dialogController.addObserver(this);
                dialogController.setController(controller);

                break;
            default:
                LOG.debug("Dropping unmanaged event code=" + code);
            }

        }
    }

    @Override
    public void dialogChanged(int dialogEvent) {

        if (dialogEvent == DialogObserver.DIALOG_ENDED_EVENT) {

            IDialogEngine dialogEngine = gameEngine.getDialogEngine().get();
            IController controller = gameEngine.getController();

            DialogController dialogController = dialogEngine.getDialogController();

            dialogController.removeObserver(this);
            dialogController.removeController(controller);

            currentEnvironment.setController(controller);

            ((WitchFantasyEnvironment) currentEnvironment).setNextMinimalMeetInstant();
        }

    }

}

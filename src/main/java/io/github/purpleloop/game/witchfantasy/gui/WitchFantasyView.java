package io.github.purpleloop.game.witchfantasy.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.purpleloop.commons.direction.Direction;
import io.github.purpleloop.commons.direction.Direction4;
import io.github.purpleloop.commons.direction.Direction8;
import io.github.purpleloop.commons.swing.sprites.Sprite;
import io.github.purpleloop.game.witchfantasy.WitchFantasyMapContents;
import io.github.purpleloop.game.witchfantasy.model.Particle;
import io.github.purpleloop.game.witchfantasy.model.Season;
import io.github.purpleloop.game.witchfantasy.model.WeatherModel;
import io.github.purpleloop.game.witchfantasy.model.WitchFantasyEnvironment;
import io.github.purpleloop.game.witchfantasy.model.WitchFantasyObject;
import io.github.purpleloop.game.witchfantasy.model.agent.PlayableCharacterAgent;
import io.github.purpleloop.gameengine.action.gui.BaseGameView;
import io.github.purpleloop.gameengine.action.gui.GamePanel;
import io.github.purpleloop.gameengine.action.model.environment.AbstractObjectEnvironment;
import io.github.purpleloop.gameengine.action.model.interfaces.IEnvironmentObjet;
import io.github.purpleloop.gameengine.action.model.interfaces.ISession;
import io.github.purpleloop.gameengine.core.config.GameConfig;
import io.github.purpleloop.gameengine.core.config.IDataFileProvider;

/** The game view. */
public class WitchFantasyView extends BaseGameView {

    /** Width of the view in number of visible cells. */
    private static final int VIEWABLE_CELLS_WIDTH = 24;

    /** Height of the view in number of visible cells. */
    private static final int VIEWABLE_CELLS_HEIGHT = 18;

    /** Prefix for all content sprites. */
    public static final String CONTENTS_SPRITE_PREFIX = "sto";

    /**
     * Grid size (this equals the environment grid size used for computations :
     * bounding boxes, precise locations, etc).
     */
    private static final int UG = 40;

    /** X location where to start scrolling. */
    private static final int X_SCROLLING_START = VIEWABLE_CELLS_WIDTH / 2 * UG;

    /** Y location where to start scrolling. */
    private static final int Y_SCROLLING_START = VIEWABLE_CELLS_HEIGHT / 2 * UG;

    /** Environment view width. */
    private static final int VIEW_WIDTH = UG * VIEWABLE_CELLS_WIDTH;

    /** Environment view height. */
    private static final int VIEW_HEIGHT = UG * VIEWABLE_CELLS_HEIGHT;

    /** Grey color. */
    private static final Color GREY = new Color(50, 50, 50);

    /** Should we draw collision rectangles ? */
    private boolean drawCollisions = true;

    /** Should we draw bounding box rectangles ? */
    private boolean drawBoundingBox = true;

    /** X view offset (for scrolling). */
    private int viewX;

    /** Y view offset (for scrolling). */
    private int viewY;

    /** Should tiles be painted ? */
    private boolean paintTiles = true;

    /** Should objects be painted ? */
    private boolean paintObjects = true;

    /** Should overlay be painted ? */
    private boolean paintOverlay = true;

    /**
     * Constructor of the view.
     * 
     * @param conf the game configuration
     * @param dataFileProvider the data file provider
     * @param owner the owning game panel
     */
    public WitchFantasyView(GameConfig conf, IDataFileProvider dataFileProvider, GamePanel owner) {
        super(owner, conf);
        owner.setBackground(Color.BLACK);
        loadSpritesSource(conf, dataFileProvider);
        registerSprites();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /** Register the sprites. */
    private void registerSprites() {
        for (Sprite desc : getSpritesDescriptions()) {
            registerSprite(desc);
        }
    }

    /** @return all sprites descriptions. */
    public static List<Sprite> getSpritesDescriptions() {
        List<Sprite> spriteDesc = new ArrayList<>();

        // Sprites for static contents on the map
        for (WitchFantasyMapContents content : WitchFantasyMapContents.values()) {
            int ordinal = content.ordinal();

            if (content.isSeasonal()) {

                for (Season season : Season.values()) {
                    spriteDesc.add(new Sprite(CONTENTS_SPRITE_PREFIX + ordinal + season.name(),
                            UG * ordinal, UG * season.ordinal(), UG, UG));
                }

            } else {
                spriteDesc
                        .add(new Sprite(CONTENTS_SPRITE_PREFIX + ordinal, UG * ordinal, 0, UG, UG));
            }
        }

        int agentOffset = UG * 4;
        int npcOffset = UG * 4;

        // Sprites for agents (animation / orientation)
        for (int o = 0; o < 4; o++) {
            spriteDesc.add(new Sprite("witch-apprentice0" + o, UG * o, agentOffset, UG, UG));
            spriteDesc
                    .add(new Sprite("witch-apprentice1" + o, UG * o, agentOffset + 1 * UG, UG, UG));
            spriteDesc.add(new Sprite("witch-normal0" + o, UG * o, agentOffset + 2 * UG, UG, UG));
            spriteDesc.add(new Sprite("witch-normal1" + o, UG * o, agentOffset + 3 * UG, UG, UG));
            spriteDesc.add(new Sprite("witch-spider0" + o, UG * o, agentOffset + 4 * UG, UG, UG));
            spriteDesc.add(new Sprite("witch-spider1" + o, UG * o, agentOffset + 5 * UG, UG, UG));
            spriteDesc.add(new Sprite("villager-default-appearance0" + o, UG * o + npcOffset,
                    agentOffset, UG, UG));
            spriteDesc.add(new Sprite("villager-default-appearance1" + o, UG * o + npcOffset,
                    agentOffset + 1 * UG, UG, UG));

        } // for -- direction
        return spriteDesc;
    }

    @Override
    protected void paintView(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        ISession currentSession = getCurrentSession();

        if (currentSession == null) {
            paintWaitPage(g);
        } else {
            WitchFantasyEnvironment currentEnv = (WitchFantasyEnvironment) currentSession
                    .getCurrentEnvironment();

            if (currentEnv != null) {

                shiftViewOriginForAgent((PlayableCharacterAgent) currentEnv.getControllable());
                if (paintTiles) {
                    paintTilesLayer(g2, currentEnv);
                }

                if (paintObjects) {
                    paintObjects(g2, currentEnv);
                }

                if (paintOverlay) {
                    paintWeather(g2, currentEnv);
                }
            } else if (currentSession.isIntermission()) {

                int sx = VIEW_WIDTH / 2;
                int sy = VIEW_WIDTH / 2;

                String levelTitle = "LEVEL " + currentSession.getTargetLevelId();

                g.setColor(Color.WHITE);
                g.drawString(levelTitle, sx, sy);

            }

        } // if currentSession
    }

    private void paintWeather(Graphics2D g2, WitchFantasyEnvironment currentEnv) {

        Optional<WeatherModel> weatherModelOpt = currentEnv.getWeatherModel();
        if (weatherModelOpt.isEmpty()) {
            return;
        }

        WeatherModel weatherModel = weatherModelOpt.get();

        List<Particle> particles = weatherModel.getParticles();

        for (Particle particle : particles) {

            g2.setColor(Color.WHITE);
            g2.fillOval(particle.getX() - viewX, particle.getY() - viewY, 4, 4);

            g2.setColor(Color.GRAY);
            g2.drawOval(particle.getX() - viewX, particle.getY() - viewY, 2, 2);

        }
    }

    /**
     * Adjust the view origin according to the location of the given agent.
     * 
     * @param agent the agent used to adjust the view for scrolling
     */
    private void shiftViewOriginForAgent(PlayableCharacterAgent agent) {
        this.viewX = shiftIfAbove(agent.getXLoc(), X_SCROLLING_START);
        this.viewY = shiftIfAbove(agent.getYLoc(), Y_SCROLLING_START);
    }

    /**
     * Shift to apply if value is over a given limit.
     * 
     * @param value the value
     * @param limit the limit
     * @return the shift if necessary
     */
    private int shiftIfAbove(int value, int limit) {
        if (value <= limit) {
            return 0;
        } else {
            return value - limit;
        }
    }

    /**
     * Paints the wait page.
     * 
     * @param g graphic context
     */
    private void paintWaitPage(Graphics g) {
        g.setColor(GREY);
        super.listControls(g);
    }

    /**
     * Paint objects of the environment.
     * 
     * @param graphics2d the graphic context where to paint
     * @param currentEnv the current environment
     */
    private void paintObjects(Graphics2D graphics2d, AbstractObjectEnvironment currentEnv) {

        List<IEnvironmentObjet> environmentObjects = currentEnv.getObjects();

        synchronized (currentEnv) {
            for (IEnvironmentObjet objectToPaint : environmentObjects) {
                paintObject(graphics2d, environmentObjects, objectToPaint);
            }
        }
    }

    /**
     * Paint an object of the environment.
     * 
     * @param graphics2d the graphic context where to paint
     * @param environmentObjects all environmentObjects (for debug info)
     * @param objectToPaint the object to paint
     */
    private void paintObject(Graphics2D graphics2d, List<IEnvironmentObjet> environmentObjects,
            IEnvironmentObjet objectToPaint) {

        int x = objectToPaint.getXLoc() - viewX;
        int y = objectToPaint.getYLoc() - viewY;

        if (isInView(x, y)) {

            // Simplifies the directions
            int objectOrientation = -1;
            Direction dir = objectToPaint.getOrientation();
            if (dir instanceof Direction8) {

                objectOrientation = dir.getValue() / 2;
            } else if (dir instanceof Direction4) {

                objectOrientation = dir.getValue();
            }

            if (objectOrientation == -1) {
                objectOrientation = 0;
            }

            putSprite(graphics2d,
                    objectToPaint.getName() + "-" + objectToPaint.getAppearance().getName()
                            + objectToPaint.getAnimationSequence() + objectOrientation,
                    x, y);

            if (isDebugInfo()) {
                paintDebugInfo(graphics2d, environmentObjects, objectToPaint, x, y);

            }
        }

    }

    /**
     * Paint the tiles layer.
     * 
     * @param graphics Graphic context where to do the rendering
     * @param currentEnv The current environment
     */
    private void paintTilesLayer(Graphics graphics, WitchFantasyEnvironment currentEnv) {

        WitchFantasyMapContents cellContents;

        String seasonName = currentEnv.getSeason().name();

        for (int y = 0; y < currentEnv.getCellHeight(); y++) {
            for (int x = 0; x < currentEnv.getCellWidth(); x++) {
                int xl = (x * UG) - viewX;
                int yl = (y * UG) - viewY;

                if (isInView(xl, yl)) {

                    // Background is always seasonal
                    putSprite(graphics, CONTENTS_SPRITE_PREFIX + "0" + seasonName, xl, yl);

                    cellContents = (WitchFantasyMapContents) currentEnv.getCellContents(x, y);
                    if (cellContents != WitchFantasyMapContents.START_PLACE) {
                        String name = CONTENTS_SPRITE_PREFIX + cellContents.ordinal();
                        if (cellContents.isSeasonal()) {
                            name = name + seasonName;
                        }
                        putSprite(graphics, name, xl, yl);
                    }
                }

            }
        }

    }

    /**
     * @param x abscissa
     * @param y ordinate
     * @return True if the location (x, y) is in view, false otherwise
     */
    private boolean isInView(int x, int y) {
        return (x + UG) > 0 && (y + UG) > 0 && (x - UG) < VIEW_WIDTH && (y - UG) < VIEW_HEIGHT;
    }

    /**
     * Paint the debug information.
     * 
     * @param graphics2d the graphic context
     * @param environmentObjects all environment objects (of collision
     *            rectangle)
     * @param objectToPaint the current object
     * @param x abscissa where to paint
     * @param y ordinate where to paint
     */
    private void paintDebugInfo(Graphics2D graphics2d, List<IEnvironmentObjet> environmentObjects,
            IEnvironmentObjet objectToPaint, int x, int y) {

        graphics2d.setColor(Color.MAGENTA);

        WitchFantasyObject specificObject = (WitchFantasyObject) objectToPaint;
        int xLoc = specificObject.getXLoc();
        int yLoc = specificObject.getYLoc();

        StringBuilder sb = new StringBuilder();
        sb.append("#");
        sb.append(objectToPaint.getId());
        sb.append(" ");
        sb.append(objectToPaint.getName());
        sb.append(" coords=");
        sb.append(xLoc);
        sb.append(",");
        sb.append(yLoc);
        sb.append(" cell=");
        sb.append(xLoc / UG);
        sb.append(",");
        sb.append(yLoc / UG);
        sb.append(", speed=");
        sb.append(specificObject.getSpeed());
        sb.append(", ori=");
        sb.append(specificObject.getOrientation());
        sb.append(", appearance=");
        sb.append(objectToPaint.getAppearance().getName());

        String extraDebugInfo = objectToPaint.getExtraDebugInfo();
        if (!extraDebugInfo.isBlank()) {
            sb.append(", xtra=");
            sb.append(extraDebugInfo);
        }

        graphics2d.drawString(sb.toString(), x, y);

        // collisions
        if (drawCollisions) {

            Rectangle refCollisionRect = objectToPaint.getCollisionRectangle();

            for (IEnvironmentObjet other : environmentObjects) {

                if (other.getId() < objectToPaint.getId()) {
                    Rectangle otherCollisionRect = other.getCollisionRectangle();

                    if (otherCollisionRect != null) {

                        Rectangle r3 = otherCollisionRect.intersection(refCollisionRect);

                        if (!r3.isEmpty()) {
                            r3.translate(-viewX, -viewY);
                            graphics2d.setColor(Color.RED);
                            graphics2d.fill(r3);
                        }

                    }

                }
            }

        }

        if (drawBoundingBox) {

            Rectangle r = objectToPaint.getCollisionRectangle();
            Rectangle translatedRectangle = (Rectangle) r.clone();
            translatedRectangle.translate(-viewX, -viewY);
            graphics2d.setColor(Color.BLACK);
            graphics2d.draw(translatedRectangle);
        }
    }

}

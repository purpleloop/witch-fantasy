package io.github.purpleloop.game.witchfantasy.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import io.github.purpleloop.commons.xml.XMLTools;
import io.github.purpleloop.game.witchfantasy.WitchFantasyMapContents;
import io.github.purpleloop.gameengine.action.model.level.Exit;
import io.github.purpleloop.gameengine.action.model.level.LocationJump;
import io.github.purpleloop.gameengine.action.model.level.LevelLink;
import io.github.purpleloop.gameengine.action.model.level.LocatedLevelLink;
import io.github.purpleloop.gameengine.action.model.level.XmlGameLevel;
import io.github.purpleloop.gameengine.core.util.EngineException;
import io.github.purpleloop.gameengine.core.util.Location;

/** Models a Witch-Fantasy level. */
public class WitchFantasyGameLevel implements XmlGameLevel {

    /** Logger of the class. */
    private static final Log LOG = LogFactory.getLog(WitchFantasyGameLevel.class);

    /** The level id. */
    private String id;

    /** The level width. */
    private int width;

    /** The level height. */
    private int height;

    /** The storage of the map contents. */
    private WitchFantasyMapContents[][] storage;

    /** The season associated with the map. */
    private Season season;

    /** Links of the level (internals and externals). */
    private List<LevelLink> links;

    @Override
    public void loadFromXml(Element levelElement) throws Exception {

        this.id = levelElement.getAttribute("id");

        LOG.debug("Loading data for level " + id);

        width = Integer.parseInt(levelElement.getAttribute("width"));
        height = Integer.parseInt(levelElement.getAttribute("height"));
        season = Season.valueOf(levelElement.getAttribute("season"));

        storage = new WitchFantasyMapContents[width][height];

        List<Element> lineElements = XMLTools.getChildElements(levelElement, "line");

        int y = 0;

        String lineString;
        for (Element line : lineElements) {

            lineString = line.getTextContent();
            int lineLength = lineString.length();

            if (lineLength != width) {
                throw new EngineException("A line of level " + id + " has an unexpected size : "
                        + lineLength + " instead of " + width);
            }

            // For each cell of the line
            for (int x = 0; x < lineLength; x++) {

                // We get what static element should be placed in the cell.
                char contentsChar = lineString.charAt(x);
                storage[x][y] = WitchFantasyMapContents.getByChar(contentsChar);
            }

            y++;
        }

        links = new ArrayList<>();

        for (Element linkElement : XMLTools.getChildElements(levelElement, "link")) {

            String sxStr = linkElement.getAttribute("sx");
            String syStr = linkElement.getAttribute("sy");

            Location sourceLocation = Location.getLocation(Integer.parseInt(sxStr),
                    Integer.parseInt(syStr));

            LocatedLevelLink link = null;

            String nextLevelStr = linkElement.getAttribute("nextLevel");

            if (!nextLevelStr.isBlank()) {
                link = new Exit(sourceLocation, nextLevelStr);
            } else {
                String dxStr = linkElement.getAttribute("dx");
                String dyStr = linkElement.getAttribute("dy");

                link = new LocationJump(sourceLocation,
                        Location.getLocation(Integer.parseInt(dxStr), Integer.parseInt(dyStr)));
            }

            links.add(link);
        }

    }

    /** @return the map contents storage */
    public WitchFantasyMapContents[][] getStorage() {
        return storage;
    }

    @Override
    public String getId() {
        return id;
    }

    /** @return width of the map */
    public int getWidth() {
        return width;
    }

    /** @return height of the map */
    public int getHeight() {
        return height;
    }

    @Override
    public List<LevelLink> getLinks() {
        return links;
    }

    /** @return The associated season */
    public Season getSeason() {
        return season;
    }

}

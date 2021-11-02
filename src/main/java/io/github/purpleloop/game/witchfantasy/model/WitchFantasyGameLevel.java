package io.github.purpleloop.game.witchfantasy.model;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import io.github.purpleloop.commons.xml.XMLTools;
import io.github.purpleloop.game.witchfantasy.WitchFantasyMapContents;
import io.github.purpleloop.gameengine.action.model.level.XmlGameLevel;
import io.github.purpleloop.gameengine.core.util.EngineException;

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

	/** Next level id. */
	private String nextLevel;

	/** The season associated with the map. */
	private Season season;

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
				throw new EngineException(
						"A line of level " + id + " has an unexpected size : " + lineLength + " instead of " + width);
			}

			// For each cell of the line
			for (int x = 0; x < lineLength; x++) {

				// We get what static element should be placed in the cell.
				char contentsChar = lineString.charAt(x);
				storage[x][y] = WitchFantasyMapContents.getByChar(contentsChar);
			}

			y++;
		}

		Element linkElemnet = XMLTools.getUniqueChildElement(levelElement, "link").get();
		nextLevel = linkElemnet.getAttribute("nextLevel");

	}

	/** @return the map contents storage */
	public WitchFantasyMapContents[][] getStorage() {
		return storage;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getNextLevel() {
		return nextLevel;
	}

	/** @return width of the map */
	public int getWidth() {
		return width;
	}

	/** @return height of the map */
	public int getHeight() {
		return height;
	}

	/** @return The associated season */
	public Season getSeason() {
		return season;
	}

}

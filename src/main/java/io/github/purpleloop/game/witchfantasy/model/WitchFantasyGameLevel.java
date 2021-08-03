package io.github.purpleloop.game.witchfantasy.model;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import io.github.purpleloop.commons.xml.XMLTools;
import io.github.purpleloop.game.witchfantasy.WitchFantasyMapContents;
import io.github.purpleloop.gameengine.action.model.level.XMLGameLevel;

/** Models a Witch-Fantasy level. */
public class WitchFantasyGameLevel implements XMLGameLevel {

	/** Logger of the class. */
	private static final Log LOG = LogFactory.getLog(WitchFantasyGameLevel.class);

	/** The level width. */
	private int width;

	/** The level height. */
	private int height;

	/** The storage of the map contents. */
	private WitchFantasyMapContents[][] storage;

	@Override
	public void loadFromXML(Element levelElement) throws Exception {

		String name = levelElement.getAttribute("name");

		LOG.debug("Loading data for level " + name);

		width = Integer.parseInt(levelElement.getAttribute("width"));
		height = Integer.parseInt(levelElement.getAttribute("height"));

		storage = new WitchFantasyMapContents[width][height];

		List<Element> lineElements = XMLTools.getChildElements(levelElement, "line");

		int y = 0;

		String lineString;
		for (Element line : lineElements) {

			lineString = line.getTextContent();

			// For each cell of the line
			for (int x = 0; x < lineString.length(); x++) {

				// We get what static element should be placed in the cell.
				char contentsChar = lineString.charAt(x);
				storage[x][y] = WitchFantasyMapContents.getByChar(contentsChar);
			}

			y++;
		}

	}

	/** @return the map contents storage */
	public WitchFantasyMapContents[][] getStorage() {
		return storage;
	}

	/** @return width of the map */
	public int getWidth() {
		return width;
	}

	/** @return height of the map */
	public int getHeight() {
		return height;
	}

}

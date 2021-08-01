package io.github.purpleloop.game.witchfantasy.gui;

import java.awt.Dimension;
import java.awt.Graphics;

import io.github.purpleloop.gameengine.action.gui.BaseGameView;
import io.github.purpleloop.gameengine.action.gui.GamePanel;
import io.github.purpleloop.gameengine.core.config.GameConfig;
import io.github.purpleloop.gameengine.core.config.IDataFileProvider;

/** The game view. */
public class WitchFantasyView extends BaseGameView {

	/** Constructor of the view.
	 * @param conf             the game configuration
	 * @param dataFileProvider the data file provider
	 * @param owner            the owning game panel
	 */
	public WitchFantasyView(GameConfig conf, IDataFileProvider dataFileProvider, GamePanel owner) {
		super(owner, conf);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(800, 500);
	}

	@Override
	protected void paintView(Graphics g) {

		if (getSessionCourante() == null) {
			listControls(g);
		}

	}

}

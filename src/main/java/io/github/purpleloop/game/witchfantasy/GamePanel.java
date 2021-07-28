package io.github.purpleloop.game.witchfantasy;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/** The game panel. */
public class GamePanel extends JPanel {
	
	/** Panel width. */
	private static final int PANEL_WIDTH = 500;
	
	/** Panel height. */
	private static final int PANEL_HEIGHT = 500;
	
	/** Serial tag. */
	private static final long serialVersionUID = -294848499067897962L;

	/** Constructor of the game panel. */
	public GamePanel() {
		setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		graphics.drawString("Hit <Escape> key to exit", 20, 10);
		graphics.drawString("Game view placeholder", 100, 50);
	}
}

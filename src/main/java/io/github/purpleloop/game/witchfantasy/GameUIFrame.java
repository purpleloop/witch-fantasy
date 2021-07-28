package io.github.purpleloop.game.witchfantasy;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.github.purpleloop.commons.swing.GraphicDeviceManager;

/** The main class for launching the game and providing the game UI under the form of a Swing JFrame. */
public class GameUIFrame extends JFrame implements KeyListener {

	/** Logger of the class. */
	private static final Log LOG = LogFactory.getLog(GameUIFrame.class);

	/** Serial tag. */
	private static final long serialVersionUID = 1713013384063740843L;

	/** The graphic device manager. */
	private GraphicDeviceManager graphicDeviceManager;

	/** The game panel. */
	private GamePanel mainPanel;

	/** The delegated key listener. */
	private KeyListener delegatedKeyListener;

	/**
	 * Constructor for the game launching user interface.
	 */
	public GameUIFrame() {
		super("Witch Fantasy - Game UI");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Asks the user for the choice of a screen mode
		graphicDeviceManager = GraphicDeviceManager.getInstance();
		graphicDeviceManager.configureScreenMode();

		// Setup a handler for restoring the display if necessary
		// when the game window is closed.
		WindowListener windowListener = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				graphicDeviceManager.restoreDisplay();

				LOG.debug("Normal exiting of the application, shutting down the JVM.");
				System.exit(0);
			}
		};

		addWindowListener(windowListener);

		mainPanel = new GamePanel();
		setContentPane(mainPanel);

		// Prepares the key listener
		// This is important, especially when in full screen
		addKeyListener(this);

		// Initializes the screen mode
		graphicDeviceManager.initDisplayUsingFrame(this, true);

		setVisible(true);
		requestFocus();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {

			// Escape key closes the window
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		} else if (delegatedKeyListener != null) {
			delegatedKeyListener.keyPressed(e);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (delegatedKeyListener != null) {
			delegatedKeyListener.keyTyped(e);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (delegatedKeyListener != null) {
			delegatedKeyListener.keyReleased(e);
		}
	}

	/**
	 * Game application entry point.
	 * 
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {

		LOG.info("Initializing the game UI ...");
		new GameUIFrame();
		LOG.info("Game UI is ready");
	}

}

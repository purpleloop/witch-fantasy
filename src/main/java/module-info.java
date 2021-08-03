module witchfantasy {
	
	exports io.github.purpleloop.game.witchfantasy.gui to io.github.purpleloop.commons;
	exports io.github.purpleloop.game.witchfantasy.model to io.github.purpleloop.commons, game.engine.action;
	
	requires commons.logging;
	requires org.apache.logging.log4j;
	requires org.apache.logging.log4j.core;
	requires org.apache.logging.log4j.jcl;
	requires io.github.purpleloop.commons.swing;
	requires io.github.purpleloop.gameengine.core;
	requires game.engine.action;
	requires game.engine.action.swing;
	requires java.xml;

}

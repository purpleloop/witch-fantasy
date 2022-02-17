module io.github.purpleloop.game.witchfantasy {
	
	exports io.github.purpleloop.game.witchfantasy.gui to io.github.purpleloop.commons;
	exports io.github.purpleloop.game.witchfantasy.model to io.github.purpleloop.commons, io.github.purpleloop.gameengine.action;
	
	requires java.xml;
	requires commons.logging;
	requires org.apache.commons.lang3;
	requires org.apache.logging.log4j;
	requires org.apache.logging.log4j.core;
	requires org.apache.logging.log4j.jcl;
	requires io.github.purpleloop.commons.swing;
	requires transitive io.github.purpleloop.gameengine.core;
	requires transitive io.github.purpleloop.gameengine.action;
	requires transitive io.github.purpleloop.gameengine.action.swing;

}

package it.usna.mvc.singlewindow;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JFrame;

import it.usna.swing.UsnaSwingUtils;
import it.usna.util.AppProperties;

/**
 * A simple JFrame extension with the abilities to store/restore window size
 * @author usna
 */
public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	protected final static String PROP_WIDTH = "APP_USNA_WIDTH";
	protected final static String PROP_HEIGHT = "APP_USNA_HEIGHT";
	protected final static String PROP_XPOS = "APP_USNA_XPOS";
	protected final static String PROP_YPOS = "APP_USNA_YPOS";
	protected final static String PROP_EXT = "APP_USNA_EXTENDED";

	/**
	 * Load windows stored position and size; default position is middle; default size is width/2, height/2
	 */
	public void loadProperties(final AppProperties prop) {
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		loadProperties(prop, screenSize.width / 2, screenSize.height / 2);
	}

	/**
	 * Load windows stored position and size; default position is middle; default size is screenSize.width * defWidth, screenSize.height * defHeight
	 */
	public void loadProperties(final AppProperties prop, float defWidth, float defHeight) {
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		loadProperties(prop, (int)(screenSize.width * defWidth), (int)(screenSize.height * defHeight));
	}

	/**
	 * Load windows stored position and size; default position is middle; default size is defWidth, defHeight
	 */
	public void loadProperties(final AppProperties prop, int defWidth, int defHeight) {
		int width = prop.getIntProperty(PROP_WIDTH, defWidth);
		int height = prop.getIntProperty(PROP_HEIGHT, defHeight);
		int xPos = prop.getIntProperty(PROP_XPOS, Integer.MIN_VALUE);
		int yPos;
		if(xPos == Integer.MIN_VALUE) {
			final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			xPos = (screenSize.width - width) / 2;
			yPos = (screenSize.height - height) / 2;
		} else {
			yPos = prop.getIntProperty(PROP_YPOS, 0);
		}

		if(isLocationInScreenBounds(xPos, yPos) == false) {
			final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			width = Math.min(width, screenSize.width);
			height = Math.min(height, screenSize.height);
			xPos = (screenSize.width - width) / 2;
			yPos = (screenSize.height - height) / 2;
		}
		final boolean extState = prop.getBoolProperty(PROP_EXT);
		if(extState == false || getExtendedState() != JFrame.MAXIMIZED_BOTH) {
			this.setBounds(xPos, yPos, width, height);
		}
		if(extState) {
			setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
	}

	private static boolean isLocationInScreenBounds(int x, int y) {
		GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		for(GraphicsDevice graphicsDevice: graphicsDevices) {
			Rectangle graphicsConfigurationBounds = graphicsDevice.getDefaultConfiguration().getBounds();
			if(graphicsConfigurationBounds.contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Windows do not support MAXIMIZED_HORIZ (return to NORMAL not implemented)
	 */
	@Override
	public void setExtendedState(int state) {
		if(Toolkit.getDefaultToolkit().isFrameStateSupported(state)) {
			super.setExtendedState(state);
		} else if(state == MAXIMIZED_HORIZ && getExtendedState() != JFrame.MAXIMIZED_BOTH) {
			final Rectangle currentBounds = getBounds();
			Rectangle screen = UsnaSwingUtils.getCurrentScreenBounds(this);
			currentBounds.x = screen.x;
			currentBounds.width = screen.width;
			setBounds(currentBounds);
		}
	}
	
	/**
	 * Gets the bounds of the screen. On systems with multiple displays if (getBounds().x, getBounds().y) is not fully contained in a display, bounds of primary display are returned
	 */
	public Rectangle getCurrentScreenBounds() {
		return UsnaSwingUtils.getCurrentScreenBounds(this);
	}

	/**
	 * Stores current XPOS, YPOS, WIDTH, WIDTH or PROP_EXT on a given AppProperties object
	 * @param prop
	 */
	public void storeProperties(final AppProperties prop) {
		final boolean extState = (getExtendedState() == JFrame.MAXIMIZED_BOTH);
		prop.setBoolProperty(PROP_EXT, extState);
		if(extState == false) {
			prop.setIntProperty(PROP_WIDTH, this.getWidth());
			prop.setIntProperty(PROP_HEIGHT, this.getHeight());
			prop.setIntProperty(PROP_XPOS, this.getX());
			prop.setIntProperty(PROP_YPOS, this.getY());
		}
	}
	
	protected void center() {
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension frameSize = getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	}
}
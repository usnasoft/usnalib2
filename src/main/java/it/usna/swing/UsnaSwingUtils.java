package it.usna.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.Enumeration;
import java.util.HashSet;

import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

public class UsnaSwingUtils {
	public final static String LF_NIMBUS = "Nimbus";

	public static boolean setLookAndFeel(String lf) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if (lf.equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					return true;
				}
			}
		} catch (Exception e) {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		return false;
	}
	
	/**
	 * Sets the location of thisComponent according to a reference Component
	 * @param thisComponent
	 * @param reference
	 * @param hRef SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.CENTER
	 */
	public static void setLocationRelativeTo(Component thisComponent, Component reference, int hRef, int hBias, int vBias) {
		Rectangle screenBounds = getCurrentScreenBounds(reference);
		Point newLocation = reference.getLocationOnScreen();
		Rectangle refBounds = reference.getBounds();
		Dimension thisSize = thisComponent.getSize();
		newLocation.x += hBias;
		newLocation.y += vBias;
		if(hRef == SwingConstants.RIGHT) {
			newLocation.x += refBounds.width;
		} else if(hRef == SwingConstants.LEFT) {
			newLocation.x -= thisSize.width;
		} else if(hRef == SwingConstants.CENTER) {
			newLocation.x += refBounds.width / 2 - thisSize.width / 2;
		}
		if(newLocation.x + thisSize.width > screenBounds.x + screenBounds.width) {
			newLocation.x = screenBounds.x + screenBounds.width - thisSize.width;
		}
		if(newLocation.y < screenBounds.y) {
			newLocation.y = screenBounds.y;
		}
		if(newLocation.y + thisSize.height > screenBounds.y + screenBounds.height) {
			newLocation.y = screenBounds.y + screenBounds.height - thisSize.height;
		}
		thisComponent.setLocation(newLocation);
	}
	
	/**
	 * Sets the location of toBeOpened relative to the owner as usual but if a owned window exixts woith the same (x,y) move the new window some puxel right and below.
	 * @param toBeOpened - the window to be opened
	 * @param owner - the owner window
	 * @param sameClass - if true only the same toBeOpened windows (by class) are considered
	 */
	public static void setLocationRelativeTo(Window toBeOpened, Window owner, boolean sameClass) {
		HashSet<Integer> x = new HashSet<>();
		toBeOpened.setLocationRelativeTo(owner);
		for(Window w: owner.getOwnedWindows()) {
			if(w != toBeOpened && w.isVisible() && (sameClass == false || w.getClass() == toBeOpened.getClass())) {
				x.add(w.getX());
			}	
		}
		int newX = toBeOpened.getX();
		int newY = toBeOpened.getY();
		while(x.contains(newX)) {
			newX += 16;
			newY += 16;
		}
		Rectangle screenBounds = getCurrentScreenBounds(owner);
		if(newX + 32 < screenBounds.x + screenBounds.width && newY + 32 < screenBounds.y + screenBounds.height) {
			toBeOpened.setLocation(newX, newY);
		}
	}
	
	/**
	 * Gets the bounds of the screen where the component "c" is.
	 * On systems with multiple displays if (getBounds().x, getBounds().y) is not fully contained in a display, bounds of primary display are returned
	 */
	public static Rectangle getCurrentScreenBounds(Component c) {
		final Point topLeft = c.getLocationOnScreen();
		final GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		for(GraphicsDevice graphicsDevice: graphicsDevices) {
			Rectangle graphicsConfigurationBounds = graphicsDevice.getDefaultConfiguration().getBounds();
			if(graphicsConfigurationBounds.contains(topLeft)) {
				return graphicsConfigurationBounds;
			}
		}
		return graphicsDevices[0].getDefaultConfiguration().getBounds();
	}
	
//	https://stackoverflow.com/questions/1043872/are-there-any-built-in-methods-in-java-to-increase-font-size
	public static void initializeFontSize(float multiplier) {
		UIDefaults defaults = UIManager.getDefaults();
		for (Enumeration<Object> e = defaults.keys(); e.hasMoreElements();) {
			Object key = e.nextElement();
			Object value = defaults.get(key);
//			System.out.println(key + "-" + value);
			@SuppressWarnings("unused")
			String dummy = value != null ? value.toString() : ""; // !!!
			if (value instanceof Font) {
				Font font = (Font) value;
				float newSize = font.getSize() * multiplier;
				if (value instanceof FontUIResource) {
					defaults.put(key, new FontUIResource(font.deriveFont(newSize)));
				} else {
					defaults.put(key, font.deriveFont(newSize));
				}
			}
		}
	}
}
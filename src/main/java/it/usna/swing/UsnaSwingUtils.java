package it.usna.swing;

import java.awt.Font;
import java.util.Enumeration;

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
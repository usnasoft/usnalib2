package it.usna.examples.imgviewer;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class ImgApp {
	public final static String APP_NAME = "MVC Image Viewer";
	public final static String VERSION = "0.1";
	public final static String PROP_FILE = System.getProperty("user.home") + File.separator + ".mvcimgv";
	
	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			error(e);
		}
		try {
			new ImgController();
		} catch (Exception e) {
			error(e);
		}
	}
	
	public static void error(final Throwable t) {
		final String message = t.getMessage();
		JOptionPane.showMessageDialog(null, "Error: " + ((message != null && message.length() > 0) ? message : t.toString()), "Errore", JOptionPane.ERROR_MESSAGE);
		//t.printStackTrace();
	}

	public static void error(final String msg) {
		JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void error(final String msg, final Throwable t) {
		JOptionPane.showMessageDialog(null, msg + '\n' + t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	}
}

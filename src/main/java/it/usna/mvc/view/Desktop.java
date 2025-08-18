package it.usna.mvc.view;

import java.awt.AWTEvent;

import javax.swing.ImageIcon;
import javax.swing.WindowConstants;

import it.usna.mvc.controller.Controller;
import it.usna.mvc.singlewindow.MainWindow;

/**
 * Desktop base class.
 * Implements some useful functionalities
 * @author a.flaccomio
 * @param <C>
 */
public class Desktop<C extends Controller> extends MainWindow {
	private static final String DEF_ICON = "/img/usna16.gif"; 
	
	protected final C controller;

	private static final long serialVersionUID = 1L;
	
	protected ImageIcon desktopIcon = null;
	
	/**
	 * This constructor exist for graphic editors
	 */
	public Desktop() {
		this.controller = null;
	}
	
	public Desktop(final C controller) {
		this.controller = controller;
		setIconImage(getIcon().getImage());
	}

	public ImageIcon getIcon() {
		if(desktopIcon == null) {
			desktopIcon =  new ImageIcon(Desktop.class.getResource(DEF_ICON));
		}
		return desktopIcon;
	}

	protected void initialize() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				controller.closeApp();
			}
		});
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}
}
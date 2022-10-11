package it.usna.mvc.view;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import it.usna.mvc.controller.Controller;
import it.usna.mvc.model.Model;


/**
 * FreeView Abstract View implementation representing a window inside the
 * system desktop
 * @param <M extends Model>
 * <p>Copyright (c) 2006</p>
 * <p>Company: USNA</p>
 * @author - Antonio Flaccomio
 * @version 1.0
 */

public abstract class FreeView <M extends Model> extends JFrame implements View {
	private static final long serialVersionUID = 1L;
	private final static String DEF_ICON = "/img/usna16.gif"; 
	private static int uniqueIdSequence = 0;
	private int uniqueId;
	protected final Controller controller;
	protected M model;
	protected ImageIcon viewIcon = null;

	protected FreeView(final Controller controller, final M model, final boolean resizable
			/*final boolean closable, final boolean maximizable, final boolean iconifiable*/) {
		setResizable(resizable);
		this.controller = controller;
		uniqueId = uniqueIdSequence++;
		this.model = model;
		initialize();
		setTitle(getViewName());
		setIconImage(getIcon().getImage());
	}
	
	protected FreeView(final Controller controller, final M model) {
		this(controller, model, true);
	}
	
	protected FreeView() {
		controller = null;
		initialize();
	}
	
	/**
	 * Center this window; resize it if is bigger than the screen
	 */
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
	
	private void initialize() {
		this.addFocusListener(new java.awt.event.FocusListener() {
			public void focusGained(java.awt.event.FocusEvent e) {
				fireViewGainedFocus();
			}
			public void focusLost(java.awt.event.FocusEvent e) {
				fireViewLoosedFocus();
			}
		});
		this.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {
			}
			public void windowClosing(WindowEvent e) {
				close();
			}
			public void windowClosed(WindowEvent e) {
				fireViewClosed();
			}
			public void windowIconified(WindowEvent e) {
				fireViewIconified();
			}
			public void windowDeiconified(WindowEvent e) {
				fireViewDeiconified();
			}
			public void windowActivated(WindowEvent e) {
				fireViewGainedFocus();	
			}
			public void windowDeactivated(WindowEvent e) {
				fireViewLoosedFocus();
			}
		});
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}
	
	public void fireViewIconified() {
		setTitle(getViewShortName());
		controller.viewIconized(this);
	}
	
	public void fireViewDeiconified() {
		setTitle(getName());
		controller.viewDeiconized(this);
	}
	
	public void fireViewGainedFocus() {
		controller.viewGainedFocus(this);
	}
	
	public void fireViewLoosedFocus() {
		controller.viewLoosedFocus(this);
	}
	
	public void fireViewClosed() {
		controller.viewClosed(this);
	}
	
	public void setSelected(final boolean sel) {
		super.toFront();
	}
	
	public String getViewName() {
		return model.getName();
	}
	
	public String getViewShortName() {
		return model.getShortName();
	}
	
	public ImageIcon getIcon() {
		if(viewIcon == null) {
			viewIcon =  new ImageIcon(FreeView.class.getResource(DEF_ICON));
		}
		return viewIcon;
	}
	
	/**
	 * Close the window. Override this method to release resources
	 * and/or alter standard behaviour.
	 * @return false: please don't close.
	 */
	public boolean close() {
		dispose();
		return true;
	}
	
	public int getIdentifier() {
		return uniqueId;
	}
	
	public boolean isSelected() {
		return this.isActive();
	}
	
	public void setMaximum(final boolean maximize) {
		final int oldState = getExtendedState();
		final int newState = maximize ? (oldState | MAXIMIZED_BOTH) : oldState & ~MAXIMIZED_BOTH; 
		super.setExtendedState(newState);
	}
	
	public Window getDialogParent() {
		return this;
	}
	
	public void update(final Object sender, final Object msg) {
	}
}

package it.usna.mvc.view;

import java.awt.Window;
import java.beans.PropertyVetoException;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import it.usna.mvc.controller.Controller;
import it.usna.mvc.model.Model;


/**
 * FreeView Abstract View implementation representing a window inside a JDesktop
 * <p>Copyright (c) 2006</p>
 * <p>Company: USNA</p>
 * @author - Antonio Flaccomio
 * @version 1.0
 */

public abstract class InternalView<M extends Model> extends JInternalFrame implements View {
	private static final long serialVersionUID = 1L;
	private static final String DEF_ICON = "/img/usna12.gif"; 
	private static int uniqueIdSequence = 0;
	private int uniqueId;
	protected final Controller controller;
	protected M model;
	protected ImageIcon viewIcon = null;

	protected InternalView(final Controller controller, final M model, final boolean resizable,
			 final boolean closable, final boolean maximizable, final boolean iconifiable) {
		super(null, resizable, closable, maximizable, iconifiable);
		this.controller = controller;
		uniqueId = uniqueIdSequence++;
		this.model = model;
		initialize();
		setTitle(getViewName());
		setFrameIcon(getIcon());
	}
	
	protected InternalView(final Controller controller, final M model) {
		this(controller, model, true, true, true, true);
	}
	
	protected InternalView() {
		controller = null;
		initialize();
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
		this.addInternalFrameListener(new InternalFrameListener() {
			public void internalFrameClosed(javax.swing.event.InternalFrameEvent e) {
				fireViewClosed();
			}
			public void internalFrameOpened(InternalFrameEvent arg0) {
			}
			public void internalFrameClosing(InternalFrameEvent arg0) {
				close();
			}
			public void internalFrameIconified(InternalFrameEvent arg0) {
				fireViewIconified();
			}
			public void internalFrameDeiconified(InternalFrameEvent arg0) {
				fireViewDeiconified();
			}
			public void internalFrameActivated(InternalFrameEvent arg0) {
				fireViewGainedFocus();
			}
			public void internalFrameDeactivated(InternalFrameEvent arg0) {
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
		setTitle(getViewName());
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
	
	@Override
	public void setSelected(final boolean sel) {
		try {
			super.setSelected(sel);
		} catch (PropertyVetoException e) {
			//e.printStackTrace();
		}
	}
	
	@Override
	public void setMaximum(boolean maximize) {
		try {
			super.setMaximum(maximize);
		} catch (PropertyVetoException e) {
			//e.printStackTrace();
		}
	}
	
	public String getViewName() {
		return /*model == null ? null :*/ model.getName();
	}
	
	public String getViewShortName() {
		return /*model == null ? null :*/ model.getShortName();
	}
	
	public ImageIcon getIcon() {
		if(viewIcon == null) {
			viewIcon = new ImageIcon(InternalView.class.getResource(DEF_ICON));
		}
		return viewIcon;
	}
	
	/**
	 * Close the window. Override this method to release resources
	 * and/or alter standard behavior.
	 * @return false: please don't close.
	 */
	public boolean close() {
		dispose();
		return true;
	}
	
	public int getIdentifier() {
		return uniqueId;
	}
	
	public Window getDialogParent() {
		return (Window)SwingUtilities.getAncestorOfClass(Window.class, this);
	}
	
	public void update(final Object sender, final Object msg) {
	}
}
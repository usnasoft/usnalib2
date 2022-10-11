package it.usna.mvc.view;

import java.awt.Window;

import javax.swing.ImageIcon;

/**
 * <p>View interface (MVC)</p>
 * <p>Copyright (c) 2006</p>
 * <p>Company: USNA</p>
 * @author - Antonio Flaccomio
 * @version 1.0
 */
public interface View {
	/**
	 * Release resources
	 * @return true if the View can be closed
	 */
	public boolean close();
	
	/**
	 * Select (has focus) o deselect (loose focus) this view;
	 * do noting if not possible
	 */
	public void setSelected(boolean select);
	
	/**
	 * Maximize (has focus) o "demaximize" this view;
	 * do noting if not possible
	 */
	public void setMaximum(boolean maximize);
	
	/**
	 * @return true if the View is the topmost one
	 */
	public boolean isSelected();
	
	/**
	 * @return the name of this view; Model.getName() by default
	 */
	public String getViewName();
	
	/**
	 * @return the short name of this view; Model.getShortName() by default
	 */
	public String getViewShortName();
	
	/**
	 * Return an unique id for this view
	 * @return
	 */
	public int getIdentifier();
	
	/**
	 * @return the title of this view as it is shown on its frame
	 */
	public String getTitle();
	
	/**
	 * @return the icon Image for this view
	 */
	public ImageIcon getIcon();
	
	/**
	 * @return true if view is visible; false otherwise
	 */
	public boolean isVisible();
	
	// Events notified to Controller
	public void fireViewIconified();
	public void fireViewDeiconified();
	public void fireViewGainedFocus();
	public void fireViewLoosedFocus();
	public void fireViewClosed();
	
	/**
	 * Events signalled (usually by model or controller) 
	 */
	public void update(Object sender, Object msg);
	
	/**
	 * Parent window for dialogs; it will be the desktop per an internal view o the view itself
	 * @return
	 */
	public Window getDialogParent();
}

package it.usna.mvc.controller;

import it.usna.mvc.view.View;

/**
 * Controller interface (MVC)
 * @author Antonio Flaccomio
 */
public interface Controller {
	/**
	 * Signal from view.
	 * @param theView
	 */
	public void viewIconized(final View theView);
	
	/**
	 * Signal from view: override this method if useful
	 * @param theView
	 */
	public void viewDeiconized(final View theView);
	
	/**
	 * Signal from view.
	 * @param theView
	 */
	public void viewGainedFocus(final View theView);
	
	/**
	 * Signal from view.
	 * @param theView
	 */
	public void viewLoosedFocus(final View theView);
	
	/**
	 * Signal from view.
	 */
	public void viewClosed(final View theView);
	
	/**
	 * Close application
	 * @return true: the application has been closed
	 */
	public boolean closeApp();
	
	/**
	 * Send a message to the active view
	 * @param msg
	 */
	public void signalTopmostView(final Object msg);
}

package it.usna.mvc.model;

import java.util.List;

import it.usna.mvc.view.View;

/**
 * Abstract Model implementation
 * @author antonio flaccomio
 */
public abstract class ModelImpl implements Model {
	
	private List<View> views;
	
	/**
	 * Overwrite if necessary
	 */
	public void initialize() {
	}

	/**
	 * Overwrite if necessary
	 */
	public void activate() {
	}

	/**
	 * Overwrite if necessary
	 */
	public void deactivate() {
	}
	
	public final void setViewsList(final List<View> views) {
		this.views = views;
	}
	
	/**
	 * By default Model.getName(); override to define a different criteria
	 */
	public String getShortName() {
		return getName();
	}
	
	/**
	 * Send a message to all pertinent (referring to this model) views
	 * @param msg
	 */
	protected void signalViews(final Object msg) {
		signalViews(msg, null);
	}
	
	/**
	 * Send a message to all pertinent (referring to this model) views except exclude
	 * (exclude is probably the View generating the event)
	 * @param msg
	 */
	protected void signalViews(final Object msg, final View exclude) {
		for(final View v: views) {
			if(v != exclude) {
				v.update(this, msg);
			}
		}
	}
}

package it.usna.mvc.model;

import java.io.Closeable;
import java.util.List;

import it.usna.mvc.view.View;

public interface Model extends Closeable {
	/**
	 * Release resources
	 * @return usually true; false: model sudgest not to close
	 */
	@Override
	public void close();
	
	/**
	 * Signal: this model is now ready and active
	 */
	public void initialize();
	
	/**
	 * Signal: this model is now active (one of its views has focus)
	 */
	public void activate();
	
	/**
	 * Signal: this model is now not active (none of its views has focus)
	 */
	public void deactivate();
	
	/**
	 * Get the name of this model
	 * @return
	 */
	public String getName();
	
	/**
	 * Get the short name of this model
	 * @return
	 */
	public String getShortName();
	
	/**
	 * The controller uses this method when the model is added to the application 
	 * @param views
	 */
	public void setViewsList(List<View> views);
}

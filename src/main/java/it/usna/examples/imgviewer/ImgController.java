package it.usna.examples.imgviewer;

import java.io.IOException;

import it.usna.mvc.controller.ControllerImpl;
import it.usna.mvc.view.View;
import it.usna.util.AppProperties;

public class ImgController extends ControllerImpl<ImgModel> {
	private final ImgDesktopWindow desktop;
	
	public ImgController() throws Exception {
		desktop = new ImgDesktopWindow(this);
		loadProperties();
		desktop.validate();
		desktop.setVisible(true);
	}
	
	/**
	 * Overridden to write property file
	 */
	public boolean closeApp() {
		storeProperties();
		return super.closeApp();
	}
	
	private void loadProperties() throws IOException {
		final AppProperties prop = new AppProperties(ImgApp.PROP_FILE);
		prop.load(true);
		desktop.loadProperties(prop);
	}
	
	private void storeProperties() {
		final AppProperties prop = new AppProperties(ImgApp.PROP_FILE);
		desktop.storeProperties(prop);
		try {
			prop.store(false);
		} catch (IOException ex) {
			ImgApp.error(ex);
		}
	}
	
	public ImgView openModel(final String theFile) {
		final ImgModel newMod = new ImgModel(theFile);
		final ImgView win = new ImgView(newMod, this);
		super.addModel(newMod, win);
		desktop.getViewSelectionBar().addView(win);
		desktop.modelExists(true);
		return win;
	}
	
	/**
	 * Overridden to manage view selection bar and desktop title
	 */
	public void viewClosed(final View theView) {
		super.viewClosed(theView);
		desktop.getViewSelectionBar().removeView(theView);
		if(currentActive == null) { // no more opened models in the application
			desktop.setTitle(ImgApp.APP_NAME);
			desktop.modelExists(false);
		}
	}
	
	/**
	 * Overridden to manage view selection bar and desktop title
	 */
	public void viewGainedFocus(final View theView) {
		desktop.getViewSelectionBar().viewSelected(theView, true);
		desktop.setTitle(ImgApp.APP_NAME + " - " + theView.getViewName());
	}
	
	/**
	 * Overridden to manage view selection bar
	 */
	public void viewLoosedFocus(final View theView) {
		desktop.getViewSelectionBar().viewSelected(theView, false);
	}
}
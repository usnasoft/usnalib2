package it.usna.examples.mvcdeskeditor;

import java.io.IOException;

import javax.swing.JFileChooser;

import it.usna.mvc.controller.ControllerImpl;
import it.usna.mvc.controller.ModelViews;
import it.usna.util.AppProperties;

public class EdController extends ControllerImpl<EdModel> {
	private final EdDesktopWindow desktop;
	private int untitledOpened = 0;
	
	public enum CtrlMsg {FIND};
	
	public EdController() throws Exception {
		desktop = new EdDesktopWindow(this);
		loadProperties();
		desktop.validate();
	}
	
	public boolean closeApp() {
		storeProperties();
		return super.closeApp();
	}
	
	private void loadProperties() throws IOException {
		final AppProperties prop = new AppProperties(EdApp.PROP_FILE);
		prop.load(true);
		desktop.loadProperties(prop);
	}
	
	private void storeProperties() {
		final AppProperties prop = new AppProperties(EdApp.PROP_FILE);
		desktop.storeProperties(prop);
		try {
			prop.store(false);
		} catch (IOException ex) {
			EdApp.error(ex);
		}
	}
	
	public void saveCurrentModel() {
		// The model do not contains data in this case; that's why I ask the view.
		if(currentActive.getModel().getNotAFile()) {
			saveAsCurrentModel();
		} else {
			final String fileName = currentActive.getModel().getFileName();
			((EdWindow)currentActive.getCurrentView()).save(fileName);
		}
	}
	
	public void saveAsCurrentModel() {
		// The model do not contains data in this case; that's why I ask the view.
		final JFileChooser chooser = new JFileChooser();
		if(chooser.showSaveDialog(desktop) == JFileChooser.APPROVE_OPTION) {
			final String fileName = chooser.getSelectedFile().getAbsolutePath();
			((EdWindow)currentActive.getCurrentView()).save(fileName);
			desktop.setTitle(EdApp.APP_NAME + " - " + fileName);
	    }
	}
	
	public EdWindow openNewModel() {
		final EdModel newMod = new EdModel("Untitled_" + (++untitledOpened), true);
		final EdWindow win = new EdWindow(newMod, this);
		super.addModel(newMod, win);
		return win;
	}
	
	public EdWindow openModel(final String theFile) {
		final EdModel newMod = new EdModel(theFile, false);
		final EdWindow win = new EdWindow(newMod, this);
		super.addModel(newMod, win);
		return win;
	}
	
	public void setActiveModel(final ModelViews<EdModel> mv) {
		super.setActiveModel(mv);
		if(currentActive != null) {
			desktop.setTitle(EdApp.APP_NAME + " - " + currentActive.getModel().getFileName());
			desktop.modelExists(true);
		} else {
			desktop.setTitle(EdApp.APP_NAME);
			desktop.modelExists(false);
		}
	}
}
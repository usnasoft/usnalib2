package it.usna.examples.mvcfreeeditor;

import javax.swing.JFileChooser;

import it.usna.mvc.controller.ControllerImpl;

public class EdController extends ControllerImpl<EdModel> {
	private int untitledOpened = 0;
	
	public EdController() throws Exception {
		final EdWindow win = openNewModel();
		win.setVisible(true);
	}

	public void saveCurrentModel() {
		// Alas the model do not contains data in this case.
		if(currentActive.getModel().getNotAFile()) {
			saveAsCurrentModel();
		} else {
			final String fileName = currentActive.getModel().getFileName();
			((EdWindow)currentActive.getCurrentView()).save(fileName);
		}
	}
	
	public void saveAsCurrentModel() {
		// Alas the model do not conteins data in this case.
		final JFileChooser chooser = new JFileChooser();
		if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			final String fileName = chooser.getSelectedFile().getAbsolutePath();
			((EdWindow)currentActive.getCurrentView()).save(fileName);
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
}
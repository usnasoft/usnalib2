package it.usna.examples.mvcdeskeditor;

import java.io.File;
import java.io.IOException;

import it.usna.mvc.model.ModelImpl;
import it.usna.util.IOFile;

public class EdModel extends ModelImpl {
	
	private String fileName;
	private boolean docChanged;
	private boolean notAFile;
	
	public enum ModelMsg {LOAD_TEXT, TITLE_CHANGED};
	
	public String getContent() {
		if (notAFile) {
			return "";
		} else {
			try {
				return IOFile.readFile(new File(fileName));
			} catch (IOException e) {
				EdApp.error("File could not be opened.", e);
				notAFile = true;
				return "";
			}
		}
	}
	
	public EdModel(final String fileName, final boolean notAFile) {
		this.fileName = fileName;
		this.notAFile = notAFile;
		docChanged = false;
	}

	public void close() {
	}

	public String getName() {
		return docChanged ? fileName + " *": fileName;
	}
	
	public String getShortName() {
		final int pos = fileName.lastIndexOf(File.separatorChar);
		if(pos > 1) {
			final String shortName = fileName.substring(pos + 1);
			return docChanged ? shortName + " *": shortName;
		} else {
			return getName();
		}
	}
	
	public void initialize() {
		signalViews(ModelMsg.LOAD_TEXT);
	}
	
	// Editor specific methods

	public String getFileName() {
		return fileName;
	}
	
	public boolean getNotAFile() {
		return notAFile;
	}
	
	public boolean getModified() {
		return docChanged;
	}
	
	public boolean documentChanged() {
		final boolean tmp = docChanged;
		docChanged = true;
		return tmp;
	}
	
	public void saveModel(final String fileName, final String text) {
		this.fileName = fileName;
		try {
			IOFile.writeFile(new File(fileName), text);
			docChanged = false;
			notAFile = false;
		} catch (IOException e) {
			EdApp.error("File could not write file.", e);
		}
		signalViews(ModelMsg.TITLE_CHANGED);
	}
}
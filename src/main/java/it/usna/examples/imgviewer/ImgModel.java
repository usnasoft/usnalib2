package it.usna.examples.imgviewer;

import java.io.File;

import javax.swing.ImageIcon;

import it.usna.mvc.model.ModelImpl;

public class ImgModel extends ModelImpl {
	
	private final String fileName;
	
	public ImgModel(final String fileName) {
		this.fileName = fileName;
	}
	
	public ImageIcon getContent() {
		return new ImageIcon(fileName);
	}

	public void close() {
	}

	public String getName() {
		return fileName;
	}
	
	public String getShortName() {
		final int pos = fileName.lastIndexOf(File.separatorChar);
		if(pos > 1) {
			return fileName.substring(pos + 1);
		} else {
			return getName();
		}
	}
}
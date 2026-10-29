package it.usna.swing;

import java.awt.Cursor;
import java.awt.Window;
import java.io.IOException;

/**
 * Using this class on a "try with resources" the cursor will automatically be restored
 * usage: try (var res = new BusyCursor(this)) { ... }
 */
public class BusyCursor implements AutoCloseable {
	private final Window w;
	
	public BusyCursor(Window w) {
		w.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		this.w = w;
	}
	
	@Override
	public void close() throws IOException {
		w.setCursor(Cursor.getDefaultCursor());
	}
}

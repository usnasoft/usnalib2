package it.usna.swing;

import java.awt.Component;
import java.awt.Cursor;

/**
 * Closable cursor setter
 * Using this class on a "try with resources" the cursor will automatically be restored
 * usage: try (var cres = new BusyCursor(this)) { ... }
 */
public class BusyCursor implements AutoCloseable {
	private final Component w;
	
	public BusyCursor(Component w) {
		w.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		this.w = w;
	}
	
	public BusyCursor(Component w, Cursor c) {
		w.setCursor(c);
		this.w = w;
	}
	
	@Override
	public void close() {
		w.setCursor(Cursor.getDefaultCursor());
	}
}

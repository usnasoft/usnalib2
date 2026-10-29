package it.usna.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.KeyboardFocusManager;

/**
 * Using this class on a "try with resources" the cursor will automatically be restored
 * usage: try (var res = new BusyCursor(this)) { ... }
 */
public class BusyCursor implements AutoCloseable {
	private final Component w;
	
	public BusyCursor(Component w) {
		w.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		this.w = w;
	}
	
//	public BusyCursor() {
//		this.w = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
//	}
	
	@Override
	public void close() {
		w.setCursor(Cursor.getDefaultCursor());
	}
}

package it.usna.swing;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Functional DocumentListener; example:
 * <pre>
* {@code
 * textField.getDocument().addDocumentListener((TextDocumentListener)e -> doSomething());
 * }
 * </pre>
 * @author a.flaccomio
 */
@FunctionalInterface
public interface TextDocumentListener extends DocumentListener {
	@Override
	default void changedUpdate(DocumentEvent e) {
	}
	
	@Override
	default void removeUpdate(DocumentEvent e) {
		textChanged(e);
	}
	
	@Override
	default void insertUpdate(DocumentEvent e) {
		textChanged(e);
	}

	void textChanged(DocumentEvent e);
}

package it.usna.swing.texteditor;

import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

/**
 * The standard behavior does not suit for programming languages; this is a simple but effective version
 */
public class SelectWordAction extends TextAction {
	private static final long serialVersionUID = 1L;
	public static final Set<Character> SEPARATORS = Set.of(' ', '\t', '\n', '\r', '"', '\'', '.', ',', ';', '?', ':', '!', '|', '&', '=', '%', '<', '>', '(', ')', '[', ']', '{', '}', '+', '-', '*', '/', '\\');
	
	private final Set<Character> separators;

	public SelectWordAction(Set<Character> separators) {
		super("Select Word Action");
		this.separators = separators;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JTextComponent target = getTextComponent(e);
		if (target != null) {
			int offset = target.getCaretPosition();
			beginWord(target, offset);
			endWord(target, offset);
		}
	}

	private void beginWord(JTextComponent target, int offset) {
		try {
			String txt = target.getDocument().getText(0, target.getDocument().getLength());
			for(int i = offset; i > 0; i--) {
				final char c = txt.charAt(i);
				if(separators.contains(c)) {
					target.setCaretPosition(i + 1);
					return;
				}
			}
			target.setCaretPosition(0);
		} catch (BadLocationException e) {
			UIManager.getLookAndFeel().provideErrorFeedback(target);
		}
	}

	private void endWord(JTextComponent target, int offset) {
		try {
			int docLength = target.getDocument().getLength();
			String txt = target.getDocument().getText(0, docLength);
			for(int i = offset; i < docLength; i++) {
				final char c = txt.charAt(i);
				if(separators.contains(c)) {
					target.moveCaretPosition(i);
					return;
				}
			}
			target.moveCaretPosition(docLength);
		} catch (BadLocationException e) {
			UIManager.getLookAndFeel().provideErrorFeedback(target);
		}
	}
}
package it.usna.swing.texteditor;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * This class extends UndoManager and create an internal document so that styles are ignored. The text content of the tracked document is mirrored on the internal document.
 * On undo/redo the internal document content is copied on the tracked document.
 * Also startCompound() and endCompound() are implemented to let the application group editing events on a unique undo/redo action.
 * @author a.flaccomio
 */
public class ShadowUndoManager extends UndoManager {
	private static final long serialVersionUID = 1L;
	private final PlainDocument undoDoc = new PlainDocument();
	private final DocumentListener caretDocListener;
	private final DocumentListener docListener;
	private final AbstractAction undoAction;
	private final AbstractAction redoAction;
	private final Document doc;
	private int caretOnUndoRedo;
//	private boolean compound = false;
//	private boolean nextCompound = false;
	private CompoundEdit compoundEdit;

	public ShadowUndoManager(JTextComponent textComponent) {
		doc = textComponent.getDocument();
		undoDoc.addUndoableEditListener(this);

		// used for caret position after undo/redo
		caretDocListener = new DocumentListener() {
			@Override
			public void insertUpdate(final DocumentEvent e) {
				int offset = e.getOffset() + e.getLength();
				offset = Math.min(offset, undoDoc.getLength());
				caretOnUndoRedo = offset;
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
//				textComponent.setCaretPosition(e.getOffset());
				caretOnUndoRedo = e.getOffset();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {/* System.out.print(e); */ }
		};

		// align doc & undoDoc
		docListener = new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				try {
					undoDoc.remove(e.getOffset(), e.getLength());
				} catch (BadLocationException e1) { }
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					undoDoc.insertString(e.getOffset(), doc.getText(e.getOffset(), e.getLength()), null);
				} catch (BadLocationException e1) { }
			}

			@Override
			public void changedUpdate(DocumentEvent e) { /* System.out.print(e); */ }
		};

		doc.addDocumentListener(docListener);

		undoAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (canUndo()) {
					undoDoc.addDocumentListener(caretDocListener);
					undo();
					undoDoc.removeDocumentListener(caretDocListener);

					doc.removeDocumentListener(docListener);
					try {
						int l = undoDoc.getLength();
						textComponent.setText(undoDoc.getText(0, l));
					} catch (BadLocationException e1) {}
					doc.addDocumentListener(docListener);

					textComponent.setCaretPosition(caretOnUndoRedo);
				}
			}
		};

		redoAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (canRedo()) {
					undoDoc.addDocumentListener(caretDocListener);
					redo();
					undoDoc.removeDocumentListener(caretDocListener);

					doc.removeDocumentListener(docListener);
					try {
						int l = undoDoc.getLength();
						textComponent.setText(undoDoc.getText(0, l));
					} catch (BadLocationException e1) {}
					doc.addDocumentListener(docListener);

					textComponent.setCaretPosition(caretOnUndoRedo);
				}
			}
		};
	}
	
	@Override
	public boolean addEdit(UndoableEdit anEdit) {
//		if(compound) {
//			if(compoundEdit == null) {
//				compoundEdit = new CompoundEdit();
//				compoundEdit.addEdit(anEdit);
//				return super.addEdit(compoundEdit);
//			} else {
//				return compoundEdit.addEdit(anEdit);
//			}
//		} else {
//			return super.addEdit(anEdit);
//		}
		if(compoundEdit != null) {
			return compoundEdit.addEdit(anEdit);
		} else {
			return super.addEdit(anEdit);
		}
	}

	public void startCompound() {
//		endCompound(); // close an eventually open block
		if(compoundEdit == null) {
			compoundEdit = new CompoundEdit();
			super.addEdit(compoundEdit);
		}
	}

	public void endCompound() {
//		compound = /*nextCompound =*/ false;
		if(compoundEdit != null) {
			compoundEdit.end();
			compoundEdit = null;
		}
	}
	
	public boolean isCompoundMode() {
		return compoundEdit != null;
	}

	public AbstractAction getUndoAction() {
		return undoAction;
	}

	public AbstractAction getRedoAction() {
		return redoAction;
	}
}
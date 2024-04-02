package it.usna.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class ShadowUndoManager extends UndoManager {
	private static final long serialVersionUID = 1L;
	private final PlainDocument undoDoc = new PlainDocument();
	private final DocumentListener caretDocListener;
	private final DocumentListener docListener;
	private final AbstractAction undoAction;
	private final AbstractAction redoAction;
	private final Document doc;
	private int caretOnUndoRedo;
	private boolean compound = false;
	private boolean nextCompound = false;

	public ShadowUndoManager(JTextComponent textComponent) {
		doc = textComponent.getDocument();
		undoDoc.addUndoableEditListener(this);

		caretDocListener = new DocumentListener() {
			@Override
			public void insertUpdate(final DocumentEvent e) {
				int offset = e.getOffset() + e.getLength();
				offset = Math.min(offset, undoDoc.getLength());
				caretOnUndoRedo = offset;
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				textComponent.setCaretPosition(e.getOffset());
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
				} catch (BadLocationException e1) {
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					undoDoc.insertString(e.getOffset(), doc.getText(e.getOffset(), e.getLength()), null);
				} catch (BadLocationException e1) {
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) { /* System.out.print(e); */ }
		};

		doc.addDocumentListener(docListener);


		// used for caret position after undo/redo

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

	//	@Override
	//	public void undoableEditHappened(UndoableEditEvent e) {
	//		if(compound) {
	//			CompoundEdit ce = new CompoundEdit();
	//			ce.addEdit(e.getEdit());
	//		} else {
	//			super.undoableEditHappened(e);	
	//		}
	//	}
	private CompoundEdit compoundEdit;
	
	@Override
	public boolean addEdit(UndoableEdit anEdit) {
		if(compound) {
			if(compoundEdit == null) {
				compoundEdit = new CompoundEdit();
				compoundEdit.addEdit(anEdit);
				return super.addEdit(compoundEdit);
			} else {
				return compoundEdit.addEdit(anEdit);
			}
		} else {
			return super.addEdit(anEdit);
		}
//		if(nextCompound) {
//			return super.addEdit(new MyUE(anEdit));
//		} else {
//			if(compound) {
//				nextCompound = true;
//			}
//			return super.addEdit(anEdit);
//		}
		
//		if(compound) {
//		return super.addEdit(new MyUE(anEdit));
//	} else {
//		return super.addEdit(anEdit);
//	}
	}

	public void startCompound() {
		compound = true;
	}

	public void endCompound() {
		compoundEdit = null;
		compound = nextCompound = false;
		if(compoundEdit != null) {
			compoundEdit.end();
		}
	}

	public AbstractAction getUndoAction() {
		return undoAction;
	}

	public AbstractAction getRedoAction() {
		return redoAction;
	}

	private static class MyUE implements UndoableEdit {
		private final UndoableEdit origin;

		private MyUE(UndoableEdit origin) {
			this.origin = origin;
		}

		@Override
		public void undo() throws CannotUndoException {
			origin.undo();
		}

		@Override
		public boolean canUndo() {
			return origin.canUndo();
		}

		@Override
		public void redo() throws CannotRedoException {
			origin.redo();
		}

		@Override
		public boolean canRedo() {
			return origin.canRedo();
		}

		@Override
		public void die() {
			origin.die();
		}

		@Override
		public boolean addEdit(UndoableEdit anEdit) {
			return origin.addEdit(anEdit);
		}

		@Override
		public boolean replaceEdit(UndoableEdit anEdit) {
			return origin.replaceEdit(anEdit);
		}

		@Override
		public boolean isSignificant() {
			return false;
		}

		@Override
		public String getPresentationName() {
			return origin.getPresentationName();
		}

		@Override
		public String getUndoPresentationName() {
			return origin.getUndoPresentationName();
		}

		@Override
		public String getRedoPresentationName() {
			return origin.getRedoPresentationName();
		}
	}
}
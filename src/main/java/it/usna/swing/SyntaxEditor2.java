package it.usna.swing;

import java.awt.event.ActionEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;

/**
 * @author a.flaccomio
 * @see it.usna.examples.SyntacticTextEditor
 */
public class SyntaxEditor2 extends JTextPane {
	private static final long serialVersionUID = 1L;
	//	private final static Style DEF_STYLE = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
	private SimpleAttributeSet DEF_STYLE = new SimpleAttributeSet();
	private final StyledDocument doc;
	private final PlainDocument undoDoc;
	private DocumentListener docListener;
	private DocumentListener caretDocListener;
	private int caretOnUndoRedo;
	
	private UndoManager undoManager = null;
	private ArrayDeque<BlockSyntax> blocks = new ArrayDeque<>();
	private ArrayList<BlockSyntax> syntax = new ArrayList<>();
	private ArrayList<Keywords> keywords = new ArrayList<>();

	public SyntaxEditor2() {
		this.doc = getStyledDocument();
		this.undoDoc = new PlainDocument();
		
		// align doc & undoDoc - call analizeDocument()
		docListener = new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				try {
					undoDoc.remove(e.getOffset(), e.getLength());
				} catch (BadLocationException e1) {}
				SwingUtilities.invokeLater(() -> {
					analizeDocument();
				});
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					undoDoc.insertString(e.getOffset(), doc.getText(e.getOffset(), e.getLength()), null);
				} catch (BadLocationException e1) {}
				SwingUtilities.invokeLater(() -> {
					analizeDocument();
				});
			}

			@Override
			public void changedUpdate(DocumentEvent e) { /*System.out.print(e);*/ }
		};

		doc.addDocumentListener(docListener);
		
		// used for caret position after undo/redo
		caretDocListener = new DocumentListener() {
			@Override
			public void insertUpdate(final DocumentEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						int offset = e.getOffset() + e.getLength();
						offset = Math.min(offset, undoDoc.getLength());
//						setCaretPosition( offset );
						caretOnUndoRedo = offset;
					}
				});
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				setCaretPosition(e.getOffset());
				caretOnUndoRedo = e.getOffset();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) { /*System.out.print(e);*/ }
		};
	}
	
	@Override
    public boolean getScrollableTracksViewportWidth() {
        return getUI().getPreferredSize(this).width <= getParent().getSize().width;
    }

	public void activateUndo() {
		undoManager = new UndoManager();
		undoDoc.addUndoableEditListener(undoManager);
	}
	
	private AbstractAction undoAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;
		@Override
		public void actionPerformed(ActionEvent e) {
			if(undoManager.canUndo()) {
				undoDoc.addDocumentListener(caretDocListener);
				undoManager.undo();
				undoDoc.removeDocumentListener(caretDocListener);

				doc.removeDocumentListener(docListener);
				try {
					int l = undoDoc.getLength();
					setText(undoDoc.getText(0, l));
//					doc.setCharacterAttributes(0, l, DEF_STYLE, true);
					analizeDocument();
				} catch (BadLocationException e1) {}
				doc.addDocumentListener(docListener);
				
				setCaretPosition(caretOnUndoRedo);
			}
		}
	};
	
	private AbstractAction redoAction = new AbstractAction() {
		private static final long serialVersionUID = 1L;
		@Override
		public void actionPerformed(ActionEvent e) {
			if(undoManager.canRedo()) {
				undoDoc.addDocumentListener(caretDocListener);
				undoManager.redo();
				undoDoc.removeDocumentListener(caretDocListener);

				doc.removeDocumentListener(docListener);
				try {
					int l = undoDoc.getLength();
					setText(undoDoc.getText(0, l));
//					doc.setCharacterAttributes(0, l, DEF_STYLE, true);
					analizeDocument();
				} catch (BadLocationException e1) {}
				doc.addDocumentListener(docListener);
				
				setCaretPosition(caretOnUndoRedo);
			}
		}
	};
	
	public AbstractAction getUndoAction() {
		return undoAction;
	}
	
	public AbstractAction getRedoAction() {
		return redoAction;
	}

	public void append(String str) {
		try {
			doc.insertString(doc.getLength(), str, null);
		} catch (BadLocationException e) {
			//			LOG.error("", e);
		}
	}

	public void insert(String str, int pos) {
		try {
			doc.insertString(pos, str, null);
		} catch (BadLocationException e) {
			//			LOG.error("", e);
		}
	}

	public void setText(String str, Style style) {
		setText(str);
		doc.setCharacterAttributes(0, doc.getLength(), style, true);
	}

	public void addSyntax(BlockSyntax s) {
		syntax.add(s);
	}

	public void addKeywords(Keywords words) {
		keywords.add(words);
	}

	private synchronized void analizeDocument() {
		try {
			blocks.clear();
			final int length = doc.getLength();
			String txt = doc.getText(0, length);

			doc.setCharacterAttributes(0, length, DEF_STYLE, true);

			int adv;
			nextChar:
				for(int i = 0; i < length; i += adv) {
					if(blocks.isEmpty() == false && blocks.peek().inner()) {
						adv = analyzeSyntax(blocks.peek(), txt, i, true);
						if(adv > 0) {
							continue;
						}
					} else {
						for(BlockSyntax syn: syntax) {
							adv = analyzeSyntax(syn, txt, i, false);
							if(adv > 0) {
								continue nextChar;
							}
						}
					}

					adv = 1;
					if(blocks.isEmpty() == false) {
						Style docStyle = blocks.peek().style;
						doc.setCharacterAttributes(i, 1, docStyle, false);
						continue;
					}
					if(blocks.isEmpty() || blocks.peek().inner() == false) {
						for(Keywords k: keywords) {
							adv = analyzeKeys(k, txt, i);
							continue;
						}
					}
				}
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch(RuntimeException e) {
			e.printStackTrace();
		}
	}

	private int analyzeSyntax(BlockSyntax syn, String txt, int index, boolean close) {
		int adv = 1;
		String start = syn.init;
		String end = syn.end;
		String escape = syn.escape;
		if(close == false && txt.startsWith(start, index)) {
			blocks.push(syn);
			adv = start.length();
			doc.setCharacterAttributes(index, adv, syn.style, false); // style on block start
			return adv;
		} else if(blocks.isEmpty() == false && blocks.peek() == syn) {
			if(escape != null && txt.startsWith(escape, index)) {
				adv = escape.length() + end.length();
				doc.setCharacterAttributes(index, adv, syn.style, false); // style on block escape
				return adv;
			} else if(txt.startsWith(end, index)) {
				adv = end.length();
				doc.setCharacterAttributes(index, adv, syn.style, false); // style on block end
				blocks.pop();
				return adv;
			}
		}
		return -1;
	}

	private int analyzeKeys(Keywords k, String txt, int index) {
		for(String key: k.keys) {
			if(txt.startsWith(key, index)) {
				int adv = key.length();
				doc.setCharacterAttributes(index, adv, k.style, false);
				return adv;
			}
		}
		return 1;
	}

	public static class BlockSyntax {
		private String init;
		private String end;
		private String escape;
		private Style style;

		public BlockSyntax(String init, String end, String escape, Style style) {
			this(init, end, style);
			this.escape = escape;
		}

		public BlockSyntax(String init, String end, Style style) {
			this.init = init;
			this.end = end;
			this.style = style;
		}

		public String init() {
			return init;
		}

		public String end() {
			return end;
		}

		public String escape() {
			return escape;
		}

		public Style style() {
			return style;
		}

		public boolean inner() {
			return true;
		}
	}

	public static class Keywords {
		private String[] keys;
		private Style style;

		public Keywords(String[] keys, Style style) {
			this.keys = keys;
			this.style = style;
		}
	}
}
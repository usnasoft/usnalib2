package it.usna.swing;

import java.util.ArrayDeque;
import java.util.ArrayList;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;

public class SyntaxEditor extends JTextPane {
	private static final long serialVersionUID = 1L;
	private final static Style DEF_STYLE = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
	private final StyledDocument doc;
	private UndoManager undoManager = null;
	private ArrayDeque<BlockSyntax> blocks = new ArrayDeque<>();
	private ArrayList<BlockSyntax> syntax = new ArrayList<>();
	private ArrayList<Keywords> keywords = new ArrayList<>();

	public SyntaxEditor() {
		this.doc = getStyledDocument();
		doc.addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				analizeDocument();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				analizeDocument();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
	}

	public UndoManager activateUndo() {
		undoManager = new UndoManager() {
			private static final long serialVersionUID = 1L;

			@Override
//			https://stackoverflow.com/questions/34644306/undo-and-redo-in-jtextpane-ignoring-style-changes
			public void undoableEditHappened(UndoableEditEvent e) {
				//  Check for an attribute change
				AbstractDocument.DefaultDocumentEvent event = (AbstractDocument.DefaultDocumentEvent)e.getEdit();
				if(event.getType().equals(DocumentEvent.EventType.CHANGE) == false) {
					super.undoableEditHappened(e);
				}
			}
		};
		doc.addUndoableEditListener(undoManager);
		return undoManager;
	}

	public void append(String str) {
		try {
			doc.insertString(doc.getLength(), str, null);
		} catch (BadLocationException e) {
			//			LOG.error("", e);
		}
	}

	public void append(String str, Style style) {
		try {
			doc.insertString(doc.getLength(), str, style);
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

	public void insert(String str, int pos, Style style) {
		try {
			doc.insertString(pos, str, style);
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

	private void analizeDocument() {
//		doc.removeUndoableEditListener(undoManager);
		try {
			blocks.clear();
			String txt = doc.getText(0, doc.getLength());
			final int length = txt.length();

			SwingUtilities.invokeLater(() -> doc.setCharacterAttributes(0, length, DEF_STYLE, true));

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
						final int j = i;
						SwingUtilities.invokeLater(() -> doc.setCharacterAttributes(j, 1, docStyle, true));
					}
					if(blocks.isEmpty() || blocks.peek().inner() == false) {
						for(Keywords k: keywords) {
							adv = analyzeKeys(k, txt, i);
						}
					}
				}
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch(RuntimeException e) {
			e.printStackTrace();
		} finally {
//			doc.addUndoableEditListener(undoManager);
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
			int a = adv;
			SwingUtilities.invokeLater(() -> doc.setCharacterAttributes(index, a, syn.style, true)); // style on block start
			return adv;
		} else if(blocks.isEmpty() == false && blocks.peek() == syn) {
			if(escape != null && txt.startsWith(escape, index)) {
				adv = escape.length() + end.length();
				int a = adv;
				SwingUtilities.invokeLater(() -> doc.setCharacterAttributes(index, a, syn.style, true)); // style on block escape
				return adv;
			} else if(txt.startsWith(end, index)) {
				adv = end.length();
				int a = adv;
				SwingUtilities.invokeLater(() -> doc.setCharacterAttributes(index, a, syn.style, true)); // style on block end
				blocks.pop();
				return adv;
			}
		}
		return -1;
	}

	private int analyzeKeys(Keywords k, String txt, int index) {
		for(String key: k.keys) {
			if(txt.startsWith(key, index)) {
				int l = key.length();
				SwingUtilities.invokeLater(() -> doc.setCharacterAttributes(index, l, k.style, true));
				return l;
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
package it.usna.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.undo.UndoManager;

/**
 * @author a.flaccomio
 * @see it.usna.examples.SyntacticTextEditor
 */
public class SyntaxEditor extends JTextPane {
	// TODO regexp delimiters
	// TODO nested blocks (partially implemented)
	private static final long serialVersionUID = 1L;
	private final SimpleAttributeSet baseStyle;
	private final StyledDocument doc;
	private PlainDocument undoDoc;
	private DocumentListener docListener;

	private DocumentListener caretDocListener;
	private AbstractAction undoAction;
	private AbstractAction redoAction;
	private int caretOnUndoRedo;
	private UndoManager undoManager;

	private ArrayList<BlockSyntax> syntax = new ArrayList<>();
	private ArrayList<Keywords> keywords = new ArrayList<>();
	private ArrayList<DelimitedKeywords> delimited = new ArrayList<>();
	private ArrayList<DelimiteRegExpdKeywords> delimitedRegExp = new ArrayList<>();

	private ArrayDeque<BlockAnalize> blocks = new ArrayDeque<>();

	public SyntaxEditor() {
		this(new SimpleAttributeSet());
	}
	
	public SyntaxEditor(SimpleAttributeSet baseStyle) {
		this.baseStyle = baseStyle;
		this.doc = getStyledDocument();

		// align doc & undoDoc - call analizeDocument()
		docListener = new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				if(undoDoc != null) {
					try {
						undoDoc.remove(e.getOffset(), e.getLength());
					} catch (BadLocationException e1) {}
				}
				SwingUtilities.invokeLater(() -> analizeDocument());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if(undoDoc != null) {
					try {
						undoDoc.insertString(e.getOffset(), doc.getText(e.getOffset(), e.getLength()), null);
					} catch (BadLocationException e1) {}
				}
				SwingUtilities.invokeLater(() -> analizeDocument());
			}

			@Override
			public void changedUpdate(DocumentEvent e) { /*System.out.print(e);*/ }
		};

		doc.addDocumentListener(docListener);
		setCaretColor(Color.black);
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		 // Only track viewport width when the viewport is wider than the preferred width
		return getUI().getPreferredSize(this).width <= getParent().getSize().width;
	}

    @Override
    public Dimension getPreferredSize() {
        // Avoid substituting the minimum width for the preferred width when the viewport is too narrow
        return getUI().getPreferredSize(this);
    };

	public void activateUndo() {
		this.undoDoc = new PlainDocument();

		undoManager = new UndoManager();
		undoDoc.addUndoableEditListener(undoManager);

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
				setCaretPosition(e.getOffset());
				caretOnUndoRedo = e.getOffset();
			}

			@Override
			public void changedUpdate(DocumentEvent e) { /*System.out.print(e);*/ }
		};

		undoAction = new AbstractAction() {
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
						analizeDocument();
					} catch (BadLocationException e1) {}
					doc.addDocumentListener(docListener);

					setCaretPosition(caretOnUndoRedo);
				}
			}
		};

		redoAction = new AbstractAction() {
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
						analizeDocument();
					} catch (BadLocationException e1) {}
					doc.addDocumentListener(docListener);

					setCaretPosition(caretOnUndoRedo);
				}
			}
		};
	}

	public AbstractAction getUndoAction() {
		return undoAction;
	}

	public AbstractAction getRedoAction() {
		return redoAction;
	}
	
	public void setTabSize(final int size) {
		final int width = getFontMetrics(doc.getFont(baseStyle)).charWidth('w') * size;
		final TabStop[] tabs = IntStream.range(1, 100).mapToObj(i -> new TabStop(width * i)).toArray(TabStop[]::new);
		final TabSet tabSet = new TabSet(tabs);
		StyleConstants.setTabSet(baseStyle, tabSet);
		setParagraphAttributes(baseStyle, false);
	}

	public void append(String str) {
		try {
			doc.insertString(doc.getLength(), str, null);
		} catch (BadLocationException e) {
			// LOG.error("", e);
		}
	}

	public void insert(String str, int pos) {
		try {
			doc.insertString(pos, str, null);
		} catch (BadLocationException e) {
			// LOG.error("", e);
		}
	}
	
	public void resetUndo() {
		undoManager.die();
	}
	
	public int getCaretRow() {
		final Element root = doc.getDefaultRootElement();
		return root.getElementIndex(getCaretPosition()) + 1;
	}
	
	public int getCaretColumn() {
		int pos = getCaretPosition();
		final Element rowEl = doc.getParagraphElement(pos);
		return pos - rowEl.getStartOffset() + 1;
	}

	// start of "syntax" section
	
	public void addBlockSyntax(BlockSyntax s) {
		syntax.add(s);
	}

	public void addKeywords(Keywords words) {
		keywords.add(words);
	}
	
	public void addDelimitedKeywords(DelimitedKeywords words) {
		delimited.add(words);
	}

	private synchronized void analizeDocument() {
		try {
			blocks.clear();
			final int length = doc.getLength();
			String txt = doc.getText(0, length);

			doc.setCharacterAttributes(0, length, baseStyle, true);
//			doc.setParagraphAttributes(0, length, DEF_STYLE, true); // tabs

			int adv;
			nextChar:
				for(int i = 0; i < length; i += adv) {
					if(blocks.isEmpty() == false && blocks.peek().blockDef.inner()) {
						adv = analyzeSyntax(blocks.peek().blockDef, txt, i, true);
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

					if(blocks.isEmpty() || blocks.peek().blockDef.inner() == false) {
						for(DelimiteRegExpdKeywords k: delimitedRegExp) {
							adv = analyzeDelimitedRegExpKeys(k, txt, i);
							if(adv > 0) {
								continue;
							}
						}
						for(DelimitedKeywords k: delimited) {
							adv = analyzeDelimitedKeys(k, txt, i);
							if(adv > 0) {
								continue;
							}
						}
						for(Keywords k: keywords) {
							adv = analyzeKeys(k, txt, i);
							if(adv > 0) {
								continue;
							}
						}
					}
					adv = 1;
				}
			if(blocks.isEmpty() == false) { // unterminated block left
				doc.setCharacterAttributes(blocks.peek().startPoint, length - blocks.peek().startPoint, blocks.peek().blockDef.style, false);
			}
		} catch (BadLocationException | RuntimeException e) {
			e.printStackTrace();
		}
	}

	private int analyzeSyntax(BlockSyntax blockDef, String txt, int index, boolean findClose) {
		String start = blockDef.init;
		String end = blockDef.end;
		String escape = blockDef.escape;
		if(findClose == false && txt.startsWith(start, index)) {
			blocks.push(new BlockAnalize(blockDef, index));
			return start.length();
		} else if(blocks.isEmpty() == false && blocks.peek().blockDef == blockDef) {
			if(escape != null && txt.startsWith(escape, index)) {
				return escape.length() + end.length();
			} else if(txt.startsWith(end, index)) {
				int adv = end.length();
				int blStart = blocks.peek().startPoint;
				doc.setCharacterAttributes(blStart, index + adv - blStart, blockDef.style, false); // style on block end
				blocks.pop();
				return adv;
			}
		}
		return -1;
	}

	private int analyzeKeys(Keywords k, String txt, int index) {
		for(String keyword: k.keywords) {
			if(txt.startsWith(keyword, index)) {
				int adv = keyword.length();
				doc.setCharacterAttributes(index, adv, k.style, false);
				return adv;
			}
		}
		return -1;
	}
	
	private int analyzeDelimitedRegExpKeys(DelimiteRegExpdKeywords k, String txt, int index) {
		for(Pattern patt: k.keyPattern) {
			Matcher m = patt.matcher(txt).region(index, txt.length());
			if(m.lookingAt())  {
				doc.setCharacterAttributes(index + m.group(1).length(),  m.group(2).length(), k.style, false);
				return m.group(1).length() + m.group(2).length() + m.group(2).length();
			}
		}
		return -1;
	}
	
	private int analyzeDelimitedKeys(DelimitedKeywords k, String txt, int index) {
		if(index == 0 || (Character.isLetterOrDigit(txt.charAt(index - 1)) == false && txt.charAt(index - 1) != '_')) {
			for(String keyword: k.keywords) {
				if(txt.startsWith(keyword, index)) {
					int adv = keyword.length();
					if(txt.length() <= index + adv || (Character.isLetterOrDigit(txt.charAt(index + adv)) == false && txt.charAt(index + adv) != '_')) {
						doc.setCharacterAttributes(index, adv, k.style, false);
						return adv;
					}
				}
			}
		}
		return -1;
	}

	public static class BlockSyntax {
		private final String init;
		private final String end;
		private String escape;
		private final Style style;

		public BlockSyntax(String init, String end, String escape, Style style) {
			this(init, end, style);
			this.escape = escape;
		}

		public BlockSyntax(String init, String end, Style style) {
			this.init = init;
			this.end = end;
			this.style = style;
		}

		boolean inner() {
			return true;
		}
	}

	public static class Keywords {
		private final String[] keywords;
		private final Style style;

		public Keywords(String[] keys, Style style) {
			this.keywords = keys;
			this.style = style;
		}
	}
	
	public static class DelimitedKeywords {
		private final String[] keywords;
		private final Style style;

		public DelimitedKeywords(String[] keys, Style style) {
			this.keywords = keys;
			this.style = style;
		}
	}
	
	public static class DelimiteRegExpdKeywords {
		private final Pattern[] keyPattern;
//		private final String[] keywords;
		private final Style style;
//		private final String lLimit;
//		private final String rLimit;

		public DelimiteRegExpdKeywords(String[] keys, Style style, String lLimit, String rLimit) {
//			this.keyPattern = new Pattern[keys.length];
//			this.keywords = keys;
			this.style = style;
//			this.lLimit = lLimit;
//			this.rLimit = rLimit;
			this.keyPattern = Stream.of(keys).map(k -> Pattern.compile("(" + lLimit + ")(" + Pattern.quote(k) + ")(" + rLimit + ")")).toArray(Pattern[]::new);
		}
	}

	private static class BlockAnalize {
		private final BlockSyntax blockDef;
		private final int startPoint;

		BlockAnalize(BlockSyntax block, int startPoint) {
			this.blockDef = block;
			this.startPoint = startPoint;
		}
	}
	
	public static void main(String ...strings) {
		String txt = "uno due tre quattro cinque";
		Pattern p = Pattern.compile("(.)(no)( )");
		Matcher m = p.matcher(txt);
		m = m.region(0, 8);
//		m.find(1);
		m.lookingAt();
		m.groupCount();
		m.group(2);
		m.start();
	}
}
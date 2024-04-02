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
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import javax.swing.undo.UndoManager;

//TODO nested blocks (partially implemented)

/**
 * @author a.flaccomio
 * @see it.usna.examples.SyntacticTextEditor
 */
public class SyntaxEditor3 extends JTextPane {
	private static final long serialVersionUID = 1L;
	private final SimpleAttributeSet baseStyle;
	protected final StyledDocument doc;
//	private PlainDocument undoDoc;
	private DocumentListener docListener;

//	private DocumentListener caretDocListener;
//	private AbstractAction undoAction;
//	private AbstractAction redoAction;
//	private int caretOnUndoRedo;
	private ShadowUndoManager undoManager;

	private ArrayList<BlockSyntax> syntax = new ArrayList<>();
	private ArrayList<Keywords> keywords = new ArrayList<>();
	private ArrayList<DelimitedKeywords> delimited = new ArrayList<>();
	private ArrayList<DelimiteRegExpdKeywords> delimitedRegExp = new ArrayList<>();

	private ArrayDeque<FoundBlock> blocks = new ArrayDeque<>();

	public SyntaxEditor3() {
		this(new SimpleAttributeSet());
	}

	public SyntaxEditor3(SimpleAttributeSet baseStyle) {
		this.baseStyle = baseStyle;
		this.doc = getStyledDocument();

		// align doc & undoDoc - call analizeDocument()
		docListener = new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				SwingUtilities.invokeLater(() -> analizeDocument(0, doc.getLength()));
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				SwingUtilities.invokeLater(() -> analizeDocument(0, doc.getLength()));
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				/* System.out.print(e); */ }
		};

		doc.addDocumentListener(docListener);
		setCaretColor(Color.black);
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		// Only track viewport width when the viewport is wider than the
		// preferred width
		return getUI().getPreferredSize(this).width <= getParent().getSize().width;
	}

	@Override
	public Dimension getPreferredSize() {
		// Avoid substituting the minimum width for the preferred width when the
		// viewport is too narrow
		return getUI().getPreferredSize(this);
	};

	public void activateUndo() {
		undoManager = new ShadowUndoManager(this);
		
	}

	public AbstractAction getUndoAction() {
		return undoManager.getUndoAction();
	}

	public AbstractAction getRedoAction() {
		return undoManager.getRedoAction();
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
			/* LOG.error("", e); */}
	}

	public void insert(String str, int pos) throws BadLocationException {
		doc.insertString(pos, str, null);
	}

//	public void replace(int offset, int length, String text) throws BadLocationException {
//		if (undoDoc != null) {
//			// doc.removeDocumentListener(docListener);
//			//// undoDoc.replace(offset, length , text, null);
//			// ((AbstractDocument)doc).replace(offset, length , text, null);
//			// undoDoc.setText("")
//			// doc.addDocumentListener(docListener);
//
//			doc.removeDocumentListener(docListener);
//			doc.remove(offset, length);
//			doc.insertString(offset, text, null);
//			doc.addDocumentListener(docListener);
//
//			undoDoc.remove(offset, length);
//			undoDoc.insertString(offset, text, null);
//		} else {
//			((AbstractDocument) doc).replace(offset, length, text, null);
//		}
//	}

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

	/*****************************/
	/* start of "syntax" section */
	/*****************************/
	public void addSyntaxRule(BlockSyntax bl) {
		syntax.add(bl);
	}

	public void addSyntaxRule(Keywords words) {
		keywords.add(words);
	}

	public void addSyntaxRule(DelimitedKeywords words) {
		delimited.add(words);
	}

	public void addSyntaxRule(DelimiteRegExpdKeywords words) {
		delimitedRegExp.add(words);
	}

	private synchronized void analizeDocument(int start, int end) {
		try {
			blocks.clear();
			final String txt = doc.getText(0, end);

			doc.setCharacterAttributes(0, end, baseStyle, true);

			int adv;
			for (int i = start; i < end; i += adv) {
				if (blocks.isEmpty() == false /*
												 * &&
												 * blocks.peek().blockDef.inner
												 */) {
					adv = analyzeSingleBlock(blocks.peek().blockDef, txt, i, true);
					if (adv > 0) {
						continue;
					}
				} else {
					adv = analyzeBlocks(txt, i);
					if (adv > 0) {
						continue;
					}
				}

				if (blocks.isEmpty() /*
										 * || blocks.peek().blockDef.inner ==
										 * false
										 */) {
					adv = analyzeDelimitedRegExpKeys(txt, i);
					if (adv > 0) {
						continue;
					}
					adv = analyzeDelimitedKeys(txt, i);
					if (adv > 0) {
						continue;
					}
					adv = analyzeKeys(txt, i);
					if (adv > 0) {
						continue;
					}
				}
				adv = 1;
			}
			if (blocks.isEmpty() == false) { // unterminated block left
				doc.setCharacterAttributes(blocks.peek().startPoint, end - blocks.peek().startPoint, blocks.peek().blockDef.style, false);
			}
		} catch (BadLocationException | RuntimeException e) {
			e.printStackTrace();
		}
	}

	private int analyzeBlocks(String txt, int index) {
		for (BlockSyntax syn : syntax) {
			int adv = analyzeSingleBlock(syn, txt, index, false);
			if (adv > 0) {
				return adv;
			}
		}
		return -1;
	}

	private int analyzeSingleBlock(BlockSyntax block, String txt, int index, boolean findClose) {
		if (block instanceof BlockSimpleSyntax) {
			BlockSimpleSyntax blockDef = (BlockSimpleSyntax) block;
			String start = blockDef.init;
			String end = blockDef.end;
			String escape = blockDef.escape;
			if (findClose == false && txt.startsWith(start, index)) {
				blocks.push(new FoundBlock(blockDef, index));
				return start.length();
			} else if (blocks.isEmpty() == false && blocks.peek().blockDef == blockDef) {
				if (escape != null && txt.startsWith(escape, index)) {
					return escape.length() + end.length();
				} else if (txt.startsWith(end, index)) {
					int adv = end.length();
					int blStart = blocks.peek().startPoint;
					doc.setCharacterAttributes(blStart, index + adv - blStart, blockDef.style, false); // style
																										// on
																										// block
																										// end
					blocks.pop();
					return adv;
				}
			}
		} else {
			BlockRegExpSyntax blockDef = (BlockRegExpSyntax) block;
			Matcher startM = blockDef.initPattern.matcher(txt).region(index, txt.length());
			String escape = blockDef.escape;
			if (findClose == false && startM.lookingAt()) {
				int startLength = startM.group(0).length();
				int startPos = startM.groupCount() > 0 ? startLength - startM.group(1).length() : startLength;
				blocks.push(new FoundBlock(blockDef, index + startPos));
				return startLength;
			} else if (blocks.isEmpty() == false && blocks.peek().blockDef == blockDef) {
				Matcher endM = blockDef.endPattern.matcher(txt).region(index, txt.length());
				if (escape != null && txt.startsWith(escape, index)) {
					return escape.length() + /* end.length() */1; // todo
				} else if (endM.lookingAt()) {
					int adv = endM.groupCount() > 0 ? endM.group(1).length() : 0;
					int blStart = blocks.peek().startPoint;
					doc.setCharacterAttributes(blStart, index + adv - blStart, blockDef.style, false); // style
																										// on
																										// block
																										// end
					blocks.pop();
					return adv;
				}
			}
		}
		return -1;
	}

	private int analyzeKeys(String txt, int index) {
		for (Keywords k : keywords) {
			for (String keyword : k.keywords) {
				if (txt.startsWith(keyword, index)) {
					int adv = keyword.length();
					doc.setCharacterAttributes(index, adv, k.style, false);
					return adv;
				}
			}
		}
		return -1;
	}

	private int analyzeDelimitedRegExpKeys(String txt, int index) {
		for (DelimiteRegExpdKeywords k : delimitedRegExp) {
			for (Pattern patt : k.keyPattern) {
				Matcher m = patt.matcher(txt).region(index, txt.length());
				if (m.lookingAt()) {
					int lLimitLegth = m.group(1).length();
					int workLength = m.group(2).length();
					doc.setCharacterAttributes(index + lLimitLegth, workLength, k.style, false);
					analyzeKeys(txt, index); // left margin not included ->
												// analyzed
					return lLimitLegth + workLength; // right margin (+
														// m.group(3).length())
														// excluded
				}
			}
		}
		return -1;
	}

	private int analyzeDelimitedKeys(String txt, int index) {
		for (DelimitedKeywords k : delimited) {
			if (index == 0 || (Character.isLetterOrDigit(txt.charAt(index - 1)) == false && txt.charAt(index - 1) != '_')) {
				for (String keyword : k.keywords) {
					if (txt.startsWith(keyword, index)) {
						int adv = keyword.length();
						if (txt.length() <= index + adv || (Character.isLetterOrDigit(txt.charAt(index + adv)) == false && txt.charAt(index + adv) != '_')) {
							doc.setCharacterAttributes(index, adv, k.style, false);
							return adv;
						}
					}
				}
			}
		}
		return -1;
	}

	private abstract static class BlockSyntax {
		protected Style style;
		// boolean inner = true;
	}

	public static class BlockSimpleSyntax extends BlockSyntax {
		private final String init;
		private final String end;
		private String escape;

		public BlockSimpleSyntax(String init, String end, String escape, Style style) {
			this(init, end, style);
			this.escape = escape;
		}

		public BlockSimpleSyntax(String init, String end, Style style) {
			this.init = init;
			this.end = end;
			this.style = style;
		}
	}

	// e.g. new SyntaxEditor.BlockRegExpSyntax(":\\s*(\")", "(\")", "\\",
	// style)); - blocks on limits to include style
	public static class BlockRegExpSyntax extends BlockSyntax {
		private final Pattern initPattern;
		private final Pattern endPattern;
		private String escape;

		public BlockRegExpSyntax(String init, String end, String escape, Style style) {
			this.initPattern = Pattern.compile(init);
			this.endPattern = Pattern.compile(end);
			this.style = style;
			this.escape = escape;
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

	// e.g. new SyntaxEditor.DelimiteRegExpdKeywords(new String[] {"xx"},
	// "[\\W]|^", "\\.", style)
	public static class DelimiteRegExpdKeywords {
		private final Pattern[] keyPattern;
		private final Style style;

		public DelimiteRegExpdKeywords(String[] keys, String lLimit, String rLimit, Style style) {
			this.keyPattern = Stream.of(keys).map(k -> Pattern.compile("(" + lLimit + ")(" + Pattern.quote(k) + ")(" + rLimit + ")")).toArray(Pattern[]::new);
			this.style = style;
		}
	}

	private static class FoundBlock {
		private final BlockSyntax blockDef;
		private final int startPoint;

		private FoundBlock(BlockSyntax block, int startPoint) {
			this.blockDef = block;
			this.startPoint = startPoint;
		}
	}

	// public static void main(String ...strings) {
	// String txt = " : \"test\"";
	// Pattern p = Pattern.compile("(:\\s)\"");
	// Matcher m = p.matcher(txt);
	// m = m.region(0, txt.length());
	// m.lookingAt();
	// m.groupCount();
	// m.group(2);
	// m.start();
	// }

	public static class ShadowUndoManager extends UndoManager {
		private static final long serialVersionUID = 1L;
		private PlainDocument undoDoc = new PlainDocument();
		private DocumentListener caretDocListener;
		private DocumentListener docListener;
		private int caretOnUndoRedo;
		private AbstractAction undoAction;
		private AbstractAction redoAction;
		protected final Document doc;
		// final JTextComponent

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
				public void changedUpdate(DocumentEvent e) {
					/* System.out.print(e); */ }
			};
			
			// align doc & undoDoc - call analizeDocument()
			docListener = new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					if (undoDoc != null) {
						try {
							undoDoc.remove(e.getOffset(), e.getLength());
						} catch (BadLocationException e1) {
						}
					}
//					SwingUtilities.invokeLater(() -> analizeDocument(0, doc.getLength()));
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					if (undoDoc != null) {
						try {
							undoDoc.insertString(e.getOffset(), doc.getText(e.getOffset(), e.getLength()), null);
						} catch (BadLocationException e1) {
						}
					}
//					SwingUtilities.invokeLater(() -> analizeDocument(0, doc.getLength()));
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					/* System.out.print(e); */ }
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
//							analizeDocument(0, doc.getLength());
						} catch (BadLocationException e1) {
						}
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
//							analizeDocument(0, doc.getLength());
						} catch (BadLocationException e1) {
						}
						doc.addDocumentListener(docListener);

						textComponent.setCaretPosition(caretOnUndoRedo);
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
	}
}
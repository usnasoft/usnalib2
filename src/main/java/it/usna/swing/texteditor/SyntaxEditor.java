package it.usna.swing.texteditor;

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

//TODO nested blocks (partially implemented)

/**
 * @author a.flaccomio
 * @see it.usna.examples.SyntacticTextEditor
 */
public class SyntaxEditor extends JTextPane {
	private static final long serialVersionUID = 1L;
	private final SimpleAttributeSet baseStyle;
	private DocumentListener docListener;
	protected final StyledDocument doc;
	protected ShadowUndoManager undoManager;

	private ArrayList<BlockSyntax> syntax = new ArrayList<>();
	private ArrayList<Keywords> keywords = new ArrayList<>();
	private ArrayList<DelimitedKeywords> delimited = new ArrayList<>();
	private ArrayList<DelimiteRegExpdKeywords> delimitedRegExp = new ArrayList<>();

	private ArrayDeque<FoundBlock> blocks = new ArrayDeque<>();

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
				SwingUtilities.invokeLater(() -> analizeDocument(0, doc.getLength()));
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				SwingUtilities.invokeLater(() -> analizeDocument(0, doc.getLength()));
			}

			@Override
			public void changedUpdate(DocumentEvent e) { /* System.out.print(e); */ }
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
		} catch (BadLocationException e) { /* LOG.error("", e); */}
	}

	public void insert(String str, int pos) throws BadLocationException {
		doc.insertString(pos, str, null);
	}

	public void replace(int offset, int length, String text) throws BadLocationException {
		replace​(offset, length, text, null);
	}
	
	// as AbstractDocument replace​
	public void replace​(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		if(undoManager != null) {
			undoManager.startCompound();
			((AbstractDocument)doc).replace(offset, length, text, attrs);
			undoManager.endCompound();
		} else {
			((AbstractDocument)doc).replace(offset, length, text, attrs);
		}
	}
	
	@Override
	public void setText(String text) {
		 try {
			((AbstractDocument)doc).replace(0, doc.getLength(), text, baseStyle); // baseStyle avoid momentary syntax colors
		} catch (BadLocationException e) { }
	}

	@Override
	public void replaceSelection(String text) {
		if(undoManager != null && getSelectionEnd() != getSelectionStart()) {
			undoManager.startCompound();
			super.replaceSelection(text); // actually is removeUpdate + insertUpdate
			undoManager.endCompound();
		} else {
			super.replaceSelection(text);
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
				if (blocks.isEmpty() == false /* && blocks.peek().blockDef.inner */) {
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

				if (blocks.isEmpty() /* || blocks.peek().blockDef.inner == false */) {
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
					doc.setCharacterAttributes(blStart, index + adv - blStart, blockDef.style, false); // style on block end
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
					doc.setCharacterAttributes(blStart, index + adv - blStart, blockDef.style, false); // style on block end
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
					analyzeKeys(txt, index); // left margin not included -> analyzed
					return lLimitLegth + workLength; // right margin (+ m.group(3).length()) excluded
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
}
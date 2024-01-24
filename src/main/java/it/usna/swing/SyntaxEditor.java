package it.usna.swing;

import java.util.ArrayDeque;
import java.util.ArrayList;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class SyntaxEditor extends JTextPane {
	private static final long serialVersionUID = 1L;
	private final static Style DEF_STYLE = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
	private final StyledDocument doc;
	private ArrayDeque<Syntax> blocks = new ArrayDeque<>();
	private ArrayList<Syntax> syntax = new ArrayList<>();

	public SyntaxEditor() {
		this.doc = getStyledDocument();
		doc.addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				analize();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				analize();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
			}
		});
	}

	//	public void append(String str) {
	//		try {
	//			doc.insertString(doc.getLength(), str, null);
	//		} catch (BadLocationException e) {
	//			//			LOG.error("", e);
	//		}
	//	}
	//
	//	public void append(String str, Style style) {
	//		try {
	//			doc.insertString(doc.getLength(), str, style);
	//		} catch (BadLocationException e) {
	//			//			LOG.error("", e);
	//		}
	//	}
	//
	//	public void insert(String str, int pos) {
	//		try {
	//			doc.insertString(pos, str, null);
	//		} catch (BadLocationException e) {
	//			//			LOG.error("", e);
	//		}
	//	}
	//
	//	public void insert(String str, int pos, Style style) {
	//		try {
	//			doc.insertString(pos, str, style);
	//		} catch (BadLocationException e) {
	//			//			LOG.error("", e);
	//		}
	//	}

	public void setText(String str, Style style) {
		setText(str);
		doc.setCharacterAttributes(0, doc.getLength(), style, true);
	}

	public void addSyntax(Syntax s) {
		syntax.add(s);
	}

	private void analize() {
		try {
			blocks.clear();
			String txt = doc.getText(0, doc.getLength());
			final int length = txt.length();

			SwingUtilities.invokeLater(() -> doc.setCharacterAttributes(0, length, DEF_STYLE, true));

			int adv;
			for(int i = 0; i < length; i += adv) {
				adv = 1;

				if(blocks.isEmpty() == false && blocks.peek().inner()) {
					adv = analyzeSyntax(blocks.peek(), txt, i, true);
				} else {
					for(Syntax syn: syntax) {
						adv = analyzeSyntax(syn, txt, i, false);
					}
				}

				if(blocks.isEmpty() == false) {
					Style docStyle = blocks.peek().style;
					final int j = i;
					final int a = adv;
					SwingUtilities.invokeLater(() -> doc.setCharacterAttributes(j, a, docStyle, true));
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch(RuntimeException e) {
			e.printStackTrace();
		}
	}

	private int analyzeSyntax(Syntax syn, String txt, int index, boolean close) {
		int adv = 1;
		String start = syn.init();
		String end = syn.end();
		if(close == false && txt.substring(index).startsWith(start)) {
			blocks.push(syn);
			adv = start.length();
		} else if(txt.substring(index).startsWith(end) && blocks.isEmpty() == false && blocks.peek() == syn) {
			adv = end.length();
			int a = adv;
			SwingUtilities.invokeLater(() -> doc.setCharacterAttributes(index, a, syn.style, true)); // style on block end
			blocks.pop();
		}
		return adv;
	}

	public static class Syntax {
		private String init;
		private String end;
		private Style style;
		
		public Syntax(String init, String end, String escape, Style style) {
			this(init, end, style);
			//todo
		}

		public Syntax(String init, String end, Style style) {
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

		public Style style() {
			return style;
		}

		public boolean inner() {
			return true;
		}
	}
}
package it.usna.swing;

import java.util.ArrayDeque;
import java.util.ArrayList;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

//https://github.com/tips4java/tips4java/blob/main/source/CompoundUndoManager.java

public class SyntaxEditor extends JTextPane {
	private static final long serialVersionUID = 1L;
	//	private final static Style DEF_STYLE = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
	private SimpleAttributeSet DEF_STYLE = new SimpleAttributeSet();
	private final StyledDocument doc;
	private UndoManager undoManager = null;
	private ArrayDeque<BlockSyntax> blocks = new ArrayDeque<>();
	private ArrayList<BlockSyntax> syntax = new ArrayList<>();
	private ArrayList<Keywords> keywords = new ArrayList<>();

	//https://github.com/yannrichet/jxtextpane/blob/master/src/main/java/org/irsn/javax/swing/DefaultSyntaxColorizer.java#L284
	public SyntaxEditor() {
		this.doc = getStyledDocument();
		

		
//		Element rootElement = doc.getDefaultRootElement();
//        doc.putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
		
		doc.addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				SwingUtilities.invokeLater(() -> {
					analizeDocument();
//					StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
				});
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				SwingUtilities.invokeLater(() -> {
					analizeDocument();
//					StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
				});
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				e.getType();
//				System.out.print(e);
			}
		});
	}
	
	@Override
    public boolean getScrollableTracksViewportWidth() {
        return getUI().getPreferredSize(this).width <= getParent().getSize().width;
    }

	public UndoManager activateUndo() {
		undoManager = new UndoManager() {
			private static final long serialVersionUID = 1L;

//			@Override
////						https://stackoverflow.com/questions/34644306/undo-and-redo-in-jtextpane-ignoring-style-changes
//			public synchronized void undoableEditHappened(UndoableEditEvent e) {
//				//  Check for an attribute change
//				javax.swing.event.DocumentEvent event = (javax.swing.event.DocumentEvent)e.getEdit();
//				if(event.getType().equals(DocumentEvent.EventType.CHANGE) == false) {
////					super.undoableEditHappened(e);
//					addEdit(e.getEdit());
//				} else {
//					//					UndoableEdit ed = e.getEdit();
//					//					ed.die();
//				}
//			}
			int c = 0;
			
			@Override
			public boolean addEdit(UndoableEdit anedit) {
//				anedit.
				if(c++ > 3)
				anedit.die();
				
				 return super.addEdit(anedit);
//				return false;
			}
			
//			@Override
//			public void getEdit()  {
//				
//			}
			
//			@Override
//			public boolean isSignificant() {
//				return false;
//			}
			
//			@Override
//			public void	redo() {
//				
//				String txt = getText();
//				doc.removeUndoableEditListener(undoManager);
//				setText(txt);
//				doc.addUndoableEditListener(undoManager);
//				super.redo();
//				
//			}
//			
//			@Override
//			public void	undo() {
//				
//				String txt = getText();
//				doc.removeUndoableEditListener(undoManager);
//				setText(txt);
//				doc.addUndoableEditListener(undoManager);
//				super.undo();
//				
//			}
			
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

	// todo perdiamo i cambi di struttura ???
	private synchronized void analizeDocument() {
		try {
						doc.removeUndoableEditListener(undoManager);
			blocks.clear();
			final int length = doc.getLength();
			String txt = doc.getText(0, length);

			doc.setCharacterAttributes(0, length, DEF_STYLE, true);
			
//			doc.getDefaultRootElement().
//			System.out.println(doc.getCharacterElement(0).);

			int adv;
			nextChar:
				for(int i = 0; i < length; i += adv) {
//					Element el = doc.getParagraphElement(i);
//					if(el.getStartOffset() == i) {
//						doc.setParagraphAttributes(i, el.getEndOffset() - el.getStartOffset(), DEF_STYLE, true);
//					}
					
//					doc.getParagraphElement(i);
					
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
//					if(Character.isWhitespace(doc.getText(i, 1).codePointAt(0)) == false && Character.isISOControl(doc.getText(i, 1).codePointAt(0))) {
//					if(doc.getCharacterElement(i).getAttributes().equals(DEF_STYLE) == false) {
//						doc.setCharacterAttributes(i, 1, DEF_STYLE, true);
//					}
				}
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch(RuntimeException e) {
			e.printStackTrace();
		} finally {
						doc.addUndoableEditListener(undoManager);
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
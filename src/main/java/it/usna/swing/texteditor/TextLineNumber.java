package it.usna.swing.texteditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;
import javax.swing.text.Utilities;

/**
 * From: https://github.com/tips4java/tips4java/blob/main/source/TextLineNumber.java
 * 
 * This class will display line numbers for a related text component. The text
 * component must use the same line height for each line. TextLineNumber
 * supports wrapped lines and will highlight the line number of the current line
 * in the text component.
 *
 * This class was designed to be used as a component added to the row header of
 * a JScrollPane.
 */
public class TextLineNumber extends JPanel implements CaretListener, DocumentListener, PropertyChangeListener {
	private static final long serialVersionUID = 1L;
	public static final float LEFT = 0.0f;
	public static final float CENTER = 0.5f;
	public static final float RIGHT = 1.0f;

	private static final int HEIGHT = Integer.MAX_VALUE - 1000000;

	// Text component this TextTextLineNumber component is in sync with
	private final JTextComponent textComponent;

	// Properties that can be changed
	private boolean updateFont;
	private Color currentLineForeground;
	private float digitAlignment;
	private int minimumDisplayDigits;

	// Keep history information to reduce the number of times the component needs to be repainted
	private int lastDigits;
	private int lastHeight;
	private int lastLine;

	private HashMap<String, FontMetrics> fonts;

	/**
	 * Create a line number component for a text component. This minimum display
	 * width will be based on 3 digits.
	 *
	 * @param component the related text component
	 */
	public TextLineNumber(JTextComponent textComponent) {
		this(textComponent, 3);
	}

	/**
	 * Create a line number component for a text component.
	 *
	 * @param component the related text component
	 * @param minimumDisplayDigits the number of digits used to calculate the
	 *            minimum width of the component
	 */
	public TextLineNumber(JTextComponent component, int minimumDisplayDigits) {
		this.textComponent = component;
		setFont(component.getFont());
//		setBorderGap(5);
		setCurrentLineForeground(Color.RED);
		setDigitAlignment(RIGHT);
		setMinimumDisplayDigits(minimumDisplayDigits);

		component.getDocument().addDocumentListener(this);
		component.addCaretListener(this);
		component.addPropertyChangeListener("font", this);
	}

	/**
	 * Gets the update font property
	 *
	 * @return the update font property
	 */
	public boolean getUpdateFont() {
		return updateFont;
	}

	/**
	 * Set the update font property. Indicates whether this Font should be
	 * updated automatically when the Font of the related text component is
	 * changed.
	 *
	 * @param updateFont when true update the Font and repaint the line numbers,
	 *            otherwise just repaint the line numbers.
	 */
	public void setUpdateFont(boolean updateFont) {
		this.updateFont = updateFont;
	}

	/**
	 * Set a user defined border
	 */
	public void setBorder(Border border) {
		super.setBorder(border);
		if(textComponent != null) {
			lastDigits = 0;
			setPreferredWidth();
		}
	}

	/**
	 * Gets the current line rendering Color
	 *
	 * @return the Color used to render the current line number
	 */
	public Color getCurrentLineForeground() {
		return currentLineForeground == null ? getForeground() : currentLineForeground;
	}

	/**
	 * The Color used to render the current line digits. Default is Coolor.RED.
	 *
	 * @param currentLineForeground the Color used to render the current line
	 */
	public void setCurrentLineForeground(Color currentLineForeground) {
		this.currentLineForeground = currentLineForeground;
	}

	/**
	 * Gets the digit alignment
	 *
	 * @return the alignment of the painted digits
	 */
	public float getDigitAlignment() {
		return digitAlignment;
	}

	/**
	 * Specify the horizontal alignment of the digits within the component.
	 * Common values would be:
	 * <ul>
	 * <li>TextLineNumber.LEFT
	 * <li>TextLineNumber.CENTER
	 * <li>TextLineNumber.RIGHT (default)
	 * </ul>
	 * 
	 * @param currentLineForeground the Color used to render the current line
	 */
	public void setDigitAlignment(float digitAlignment) {
		this.digitAlignment = digitAlignment > 1.0f ? 1.0f : digitAlignment < 0.0f ? -1.0f : digitAlignment;
	}

	/**
	 * Gets the minimum display digits
	 *
	 * @return the minimum display digits
	 */
	public int getMinimumDisplayDigits() {
		return minimumDisplayDigits;
	}

	/**
	 * Specify the mimimum number of digits used to calculate the preferred
	 * width of the component. Default is 3.
	 *
	 * @param minimumDisplayDigits the number digits used in the preferred width
	 *            calculation
	 */
	public void setMinimumDisplayDigits(int minimumDisplayDigits) {
		this.minimumDisplayDigits = minimumDisplayDigits;
		setPreferredWidth();
	}

	/**
	 * Calculate the width needed to display the maximum line number
	 */
	private void setPreferredWidth() {
		Element root = textComponent.getDocument().getDefaultRootElement();
		int lines = root.getElementCount();
		int digits = Math.max(String.valueOf(lines).length(), minimumDisplayDigits);

		// Update sizes when number of digits in the line number changes
		if (lastDigits != digits) {
			lastDigits = digits;
			FontMetrics fontMetrics = getFontMetrics(getFont());
			int width = fontMetrics.charWidth('0') * digits;
			Insets insets = getInsets();
			int preferredWidth = insets.left + insets.right + width;

			Dimension d = getPreferredSize();
			d.setSize(preferredWidth, HEIGHT);
			setPreferredSize(d);
			setSize(d);
		}
	}

	/**
	 * Draw the line numbers
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Determine the width of the space available to draw the line number
		FontMetrics fontMetrics = textComponent.getFontMetrics(textComponent.getFont());
		Insets insets = getInsets();
		int availableWidth = getSize().width - insets.left - insets.right;

		// Determine the rows to draw within the clipped bounds.
		Rectangle clip = g.getClipBounds();
//		int rowStartOffset = textComponent.viewToModel2D(new Point(0, clip.y));
//		int endOffset = textComponent.viewToModel2D(new Point(0, clip.y + clip.height));
		int rowStartOffset = textComponent.viewToModel(new Point(0, clip.y));
		int endOffset = textComponent.viewToModel(new Point(0, clip.y + clip.height));
		
		final Element root = textComponent.getDocument().getDefaultRootElement();
		final int caretElIndex = root.getElementIndex(textComponent.getCaretPosition());

		while (rowStartOffset <= endOffset) {
			try {
				if (root.getElementIndex(rowStartOffset) == caretElIndex)
					g.setColor(getCurrentLineForeground());
				else
					g.setColor(getForeground());

				// Get the line number as a string and then determine the
				// "X" and "Y" offsets for drawing the string.
				String lineNumber = getTextLineNumber(rowStartOffset);
				int stringWidth = fontMetrics.stringWidth(lineNumber);
				int x = getOffsetX(availableWidth, stringWidth) + insets.left;
				int y = getOffsetY(rowStartOffset, fontMetrics);
				g.drawString(lineNumber, x, y);

				// Move to the next row
				rowStartOffset = Utilities.getRowEnd(textComponent, rowStartOffset) + 1;
			} catch (Exception e) {
				break;
			}
		}
	}

	/*
	 * Get the line number to be drawn. The empty string will be returned when a
	 * line of text has wrapped.
	 */
	protected String getTextLineNumber(int rowStartOffset) {
		Element root = textComponent.getDocument().getDefaultRootElement();
		int index = root.getElementIndex(rowStartOffset);
		Element line = root.getElement(index);

		if (line.getStartOffset() == rowStartOffset)
			return String.valueOf(index + 1);
		else
			return "";
	}

	/*
	 * Determine the X offset to properly align the line number when drawn
	 */
	private int getOffsetX(int availableWidth, int stringWidth) {
		return (int) ((availableWidth - stringWidth) * digitAlignment);
	}

	/*
	 * Determine the Y offset for the current row
	 */
	private int getOffsetY(int rowStartOffset, FontMetrics fontMetrics) throws BadLocationException {
		// Get the bounding rectangle of the row

//		Rectangle2D r = textComponent.modelToView2D(rowStartOffset);
		Rectangle2D r = textComponent.modelToView(rowStartOffset);
		int lineHeight = fontMetrics.getHeight();
		int y = (int)r.getY() + (int)r.getHeight();
		int descent = 0;

		// The text needs to be positioned above the bottom of the bounding
		// rectangle based on the descent of the font(s) contained on the row.

		if (r.getHeight() == lineHeight) { // default font is being used
			descent = fontMetrics.getDescent();
		} else { // We need to check all the attributes for font changes
			if (fonts == null)
				fonts = new HashMap<String, FontMetrics>();

			Element root = textComponent.getDocument().getDefaultRootElement();
			int index = root.getElementIndex(rowStartOffset);
			Element line = root.getElement(index);

			for (int i = 0; i < line.getElementCount(); i++) {
				Element child = line.getElement(i);
				AttributeSet as = child.getAttributes();
				String fontFamily = (String) as.getAttribute(StyleConstants.FontFamily);
				Integer fontSize = (Integer) as.getAttribute(StyleConstants.FontSize);
				String key = fontFamily + fontSize;

				FontMetrics fm = fonts.get(key);

				if (fm == null) {
					Font font = new Font(fontFamily, Font.PLAIN, fontSize);
					fm = textComponent.getFontMetrics(font);
					fonts.put(key, fm);
				}
				descent = Math.max(descent, fm.getDescent());
			}
		}
		return y - descent;
	}

//
//  Implement CaretListener interface
//
	@Override
	public void caretUpdate(CaretEvent e) {
		// Get the line the caret is positioned on

		int caretPosition = textComponent.getCaretPosition();
		Element root = textComponent.getDocument().getDefaultRootElement();
		int currentLine = root.getElementIndex(caretPosition);

		// Need to repaint so the correct line number can be highlighted
		if (lastLine != currentLine) {
//			repaint();
			getParent().repaint();
			lastLine = currentLine;
		}
	}

//
//  Implement DocumentListener interface
//
	@Override
	public void changedUpdate(DocumentEvent e) {
		documentChanged();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		documentChanged();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		documentChanged();
	}

	/*
	 * A document change may affect the number of displayed lines of text.
	 * Therefore the lines numbers will also change.
	 */
	private void documentChanged() {
		// View of the component has not been updated at the time the DocumentEvent is fired
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					int endPos = textComponent.getDocument().getLength();
//					Rectangle2D rect = textComponent.modelToView2D(endPos);
					Rectangle2D rect = textComponent.modelToView(endPos);

					if (rect != null && rect.getY() != lastHeight) {
						setPreferredWidth();
//						repaint();
						getParent().repaint();
						lastHeight = (int)rect.getY();
					}
				} catch (BadLocationException ex) { /* nothing to do */ }
			}
		});
	}

//
//  Implement PropertyChangeListener interface
//
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getNewValue() instanceof Font) {
			if (updateFont) {
				Font newFont = (Font) evt.getNewValue();
				setFont(newFont);
				lastDigits = 0;
				setPreferredWidth();
			} else {
//				repaint();
				getParent().repaint();
			}
		}
	}
}
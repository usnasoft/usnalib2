package it.usna.swing;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.text.DefaultFormatterFactory;

import it.usna.swing.texteditor.TextDocumentListener;

/**
 * Text field with autocomplete functionality.
 * @author antonio
 */
public class AutocompleteTextField extends JFormattedTextField {
	private static final long serialVersionUID = 1L;

	/**
	 * include combo items criteria
	 */
	public enum SearchMode {START_WITH, CONTAINS, ALL}

	protected String[] itemList;
	protected int maxItems = 1000;
	protected int minChar = 1;
	private boolean allowNullValue = true;
	private SearchMode searchMode = SearchMode.START_WITH;
	private final JPopupMenu autocompleteList = new JPopupMenu();
	private JMenuItem selected;

	public AutocompleteTextField() {
		this("");
	}

	public AutocompleteTextField(final String text) {
		super(text);
		((DefaultFormatterFactory)getFormatterFactory()).setEditFormatter(new AutocompleteFormatter());
		init();
	}

	public void setItemsList(final String ... itemList) {
		this.itemList = itemList;
		fillCombo();
	}

	public void setMinEditedChar(final int minChar) {
		this.minChar = minChar;
	}

	public void setMaxItems(final int maxItems) {
		this.maxItems = maxItems;
	}

	/**
	 * Specify how to select items to be included in the selection list
	 * @see SearchMode
	 * @param mode
	 */
	public void setListSelecetionMode(final SearchMode mode) {
		searchMode = mode;
	}

	/**
	 * Allow a content not included into the selection list or empty string.
	 * @param allowFreeText; default is false
	 * @param allowNullValue; default is true
	 */
	public void allowFreeText(final boolean allowFreeText, final boolean allowNullValue) {
		if(allowFreeText) {
			((DefaultFormatterFactory)getFormatterFactory()).setEditFormatter(null);
		} else {
			((DefaultFormatterFactory)getFormatterFactory()).setEditFormatter(new AutocompleteFormatter());
			this.allowNullValue = allowNullValue;
		}
	}

	private void init() {
		//autocompleteList.setBorder(BorderFactory.createEmptyBorder()) ;
		
		getDocument().addDocumentListener(new TextDocumentListener() {
			public void textChanged(DocumentEvent e) {
				SwingUtilities.invokeLater(() -> {
					if(AutocompleteTextField.this.isFocusOwner()) {
						fillCombo();
					}
				});
			}
		});

		/*final KeyStroke ks[] = autocompleteList.getInputMap().allKeys();
    autocompleteList.addKeyListener(new KeyAdapter() {     
      public void keyTyped(KeyEvent e) {
        for(KeyStroke tks: ks) {
          if(tks.getKeyCode() == e.getKeyCode()) {
            return;
          }
        }
        AutocompleteTextField.this.dispatchEvent(e);
      }
    });*/

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_DOWN) {
					autocompleteList.dispatchEvent(e);
					if(selected == null) {
						// Quando ci passa il mouse sopra si perde ogni riferimento (1.5);
						// le due righe che seguono ricostruiscono il modello di selezione.
						autocompleteList.setVisible(false);
						autocompleteList.setVisible(true);
						AutocompleteTextField.this.requestFocus();
					}
				} else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					if(selected != null) {
						selected.doClick();
					}
				}
			}
		});

		addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				final Component opposite = e.getOppositeComponent();
				if(opposite != autocompleteList && e.isTemporary() == false) {
					autocompleteList.setVisible(false);
					if(opposite != null && opposite.isVisible()) {
						opposite.requestFocus();
					}
				}
			}
		});
	}

	private void fillCombo() {
		final String edited = getText();
		if(edited.length() >= minChar) {
			final Collection<String> itCollection = getSelectableItems(edited);
			final int itemCount = itCollection.size();
			if(itemCount > 0 && itemCount < maxItems) {
				autocompleteList.removeAll();

				boolean armIt = true;
				final Border itemBorder = BorderFactory.createEmptyBorder(0, 0, 2, 0);
				for(final String itemText: itCollection) {
					final JMenuItem it = new JMenuItem(itemText) {
						private static final long serialVersionUID = 1L;

						@Override
						public void menuSelectionChanged(boolean isIncluded) {
							if(isIncluded) {
								selected = this;
							} else {
								if(this == selected) {
									selected = null;
								}
							}
							super.menuSelectionChanged(isIncluded);
							//AutocompleteTextField.this.requestFocus();
						}
					};
					if(armIt) {
						//it.setArmed(true);
						selected = it;
						armIt = false;
					}
					it.setBorder(itemBorder);
					autocompleteList.add(it);
					it.addActionListener(e -> AutocompleteTextField.this.setText(itemText));
				}

				if(this.isShowing()) { // if the field (or its container) is not visible the list in not shown
					if(autocompleteList.isShowing()) {
						autocompleteList.pack();
					} else {
						autocompleteList.show(this, 0, this.getHeight());
					}
					this.requestFocus();
				}
			} else {
				autocompleteList.setVisible(false);
			}
		} else {
			autocompleteList.setVisible(false);
		}
	}

	/**
	 * Override this method to return a speific Collection of items to be shown
	 * @param edited
	 * @return
	 */
	protected Collection<String> getSelectableItems(final String edited) {
		Pattern pattern;
		if(searchMode == SearchMode.START_WITH) {
			pattern = Pattern.compile(Pattern.quote(edited) + ".*", Pattern.CASE_INSENSITIVE);
		} else if(searchMode == SearchMode.CONTAINS) {
			pattern = Pattern.compile(".*" + Pattern.quote(edited) + ".*", Pattern.CASE_INSENSITIVE);
		} else {
			pattern = Pattern.compile(".*"); // All
		}
		final ArrayList<String> il = new ArrayList<>();
		for(int i = 0; i < itemList.length && il.size() <= maxItems; i++) {
			final String item = itemList[i];
			if(pattern.matcher(item).matches() && edited.equals(item) == false) {
				il.add(item);
			}
		}
		return il;
	}

	private class AutocompleteFormatter extends JFormattedTextField.AbstractFormatter {
		private static final long serialVersionUID = 1L;

		@Override
		public Object stringToValue(final String text) throws ParseException {
			//System.out.println("xx " + text);
			if((allowNullValue && text.isEmpty()) == false && accept(text) == false) {
				throw new ParseException("text not in set", 0);
			}
			return text;
		}

		@Override
		public String valueToString(final Object value) {
			//System.out.println("ww " + value);
			return value == null ? "" : value.toString();
		}

		private boolean accept(final String text) {
			for(final String it: itemList) {
				if(it.equals(text)) {
					return true;
				}
			}
			return false;
		}
	}
}
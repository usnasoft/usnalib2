package it.usna.swing;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 * JPopupMenu extension; utility class.
 * <p>Copyright (c) 2007</p>
 * @author a.flaccomio
 */
public class UsnaPopupMenu extends JPopupMenu {

	private static final long serialVersionUID = 1L;
	protected Point invocationPoint;
	/**
	 * Construct an empty UsnaPopupMenu
	 */
	public UsnaPopupMenu() {
	}

	/**
	 * Construct a UsnaPopupMenu with an array of items
	 * @see add(Object ... items)
	 * @param items
	 */
	public UsnaPopupMenu(final Object ... items) {
		add(items);
	}

	/**
	 * Add an array of items to the menu; null items are rendered as separators,
	 * JMenuItem are simply added with their own action; any other Object is added
	 * after toString() and the action is itemSelected(...).
	 * Warning: adding a single String will result in a call to add(String s).
	 * @param items
	 */
	public void add(final Object ... items) {
		int i = 0;
		for (final Object item: items) {
			if (item == null) {
				addSeparator();
			} else {
				if (item instanceof JMenuItem) {
					add((JMenuItem) item);
				} else if (item instanceof Action) {
//					final Action a = (Action) item; a.putValue("xcxcxc", 2);
					add((Action) item);
				} else {
					final int ind = i;
					final JMenuItem it = add(item.toString());
					it.addActionListener(e -> itemSelected(it, ind));
				}
			}
			i++;
		}	
	}
	
	public Point getInvocationPoint() {
		return invocationPoint;
	}

	/**
	 * Create and return the standard listener for this popup menu
	 * @return an implementation of MouseListener
	 */
	public MouseListener getMouseListener() {
		return new MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				if (e.isPopupTrigger()) {
					doPopup(e);
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					doPopup(e);
				}
			}
		};
	}
	
	/**
	 * Create and return the standard listener for this popup menu for a JTable;
	 * if the row where the event has origin is not selected -> select it and then popup
	 * @return an implementation of MouseListener
	 */
	public MouseListener getMouseListener(JTable table) {
		return new MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				if (e.isPopupTrigger()) {
					selectAndPopup(e);
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					selectAndPopup(e);
				}
			}
			
			private void selectAndPopup(java.awt.event.MouseEvent e) {
				final int r = table.rowAtPoint(e.getPoint());
				if (r >= 0 && table.isRowSelected(r) == false) {
					table.setRowSelectionInterval(r, r);
				}
				doPopup(e);
			}
		};
	}

	/**
	 *  Override to add specific functions e.g.: select item below the pointer
	 */
	protected void doPopup(MouseEvent e) {
		invocationPoint = e.getPoint();
		show(e.getComponent(), e.getX(), e.getY());
	}

	/**
	 * The action for any menu item except original JMenuItem
	 * @param item the resulting JMenuItem
	 * @param ind the index in the array when the item has been added
	 */
	protected void itemSelected(JMenuItem item, int ind) {
		// override if needed
	}
}
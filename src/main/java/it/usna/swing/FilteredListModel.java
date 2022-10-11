package it.usna.swing;

import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

/**
 * <p>JIntegerTextField</p>
 * <p>Modello per JList che consente di filtrare parte della lista
 * sulla base di un criterio. La lista non viene copiata pertanto eventuali modifiche
 * sulla struttura dati si riflettono sulla JList.</p>
 * <p>Copyright (c) 2004</p>
 * <p>Company: USNA</p>
 * @version 1.0
 * @author - Antonio Flaccomio
 */
public abstract class FilteredListModel<T> extends AbstractListModel<T> {

	private static final long serialVersionUID = 1L;

	protected List<? extends T> elements = Collections.emptyList();

	public int getSize() {
		int count = 0;
		for (T e : elements) {
			if (checkShow(e))
				count++;
		}
		return count;
	}

	public T getElementAt(final int ind) {
		int count = 0;
		for (T e : elements) {
			if (checkShow(e) && count++ == ind)
				return e;
		}
		return null;
	}

	/**
	 * Set the list containing data to be showed
	 * @param list List
	 */
	public void setListData(final List<? extends T> list) {
		elements = list;
		fireContentsChanged(this, 0, list.size() - 1);
	}

	/**
	 * Add an element to the list;
	 * call only after setListData.
	 * @param el T
	 */
	/*public void add(final T el) {
		elements.add(el);
		if (checkShow(el)) {
			final int ultimo = getSize() - 1;
			fireIntervalAdded(this, ultimo, ultimo);
		}
	}*/

	/**
	 * Clear list
	 */
	public void clear() {
		elements = Collections.emptyList();
		fireIntervalRemoved(this, 0, 0);
	}

	/**
	 * Filter criteria
	 * @param element T
	 * @return boolean true: element shown, false element filtered out
	 */
	protected abstract boolean checkShow(T element);

	/**
	 * Call this method to refresh JList whenever data of filter changes.
	 */
	public void dataChanged() {
		fireContentsChanged(this, 0, getSize() - 1);
	}
	
	/**
	 * Return the index of the element (use equal)
	 * @param el
	 * @return
	 */
	public int getIndex(final T el) {
		return elements.indexOf(el);
	}
}
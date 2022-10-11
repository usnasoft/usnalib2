package it.usna.swing.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * <p>Title: UsnaTableModel</p>
 * <p>Table model implementation</p>
 * <p>Copyright (c) 2007</p>
 * <p>Company: </p>
 * @author Antonio Flaccomio
 * @version 1.0
 */
public class UsnaTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	protected List<Object[]> lines = new ArrayList<>();
	protected String[] colNames;
	protected int columns;
	
	protected UsnaTableModel() {}

	public UsnaTableModel(final int columns) {
		this.columns = columns;
		colNames = null;
	}

	public UsnaTableModel(final String... colNames) {
		this.columns = colNames.length;
		this.colNames = colNames;
	}

	@Override
	public int getRowCount() {
		return lines.size();
	}

	@Override
	public int getColumnCount() {
		return columns;
	}

	@Override
	public String getColumnName(final int index) {
		if (colNames == null)
			return super.getColumnName(index);
		else
			return colNames[index];
	}

	@Override
	public Object getValueAt(final int y, final int x) {
		if (y < lines.size()) {
			final Object[] line = lines.get(y);
			return (line.length > x) ? line[x] : null;
		} else {
			return null;
		}
	}

	/**
	 * append a row to table
	 * @param row Object[]
	 */
	public int addRow(final Object... row) {
		lines.add(row);
		int rowIndex = lines.size() - 1;
		fireTableRowsInserted(rowIndex, rowIndex);
		return rowIndex;
	}
	
	/**
	 * replace a row to table
	 * @param row Object[]
	 */
	public void setRow(final int ind, final Object... row) {
		lines.set(ind, row);
		fireTableRowsUpdated(ind, ind);
	}
	
	/**
	 * insert a row to table
	 * @param row Object[]
	 */
	public void insertRow(final int ind, final Object... row) {
		lines.add(ind, row);
		fireTableRowsInserted(ind, ind);
	}

	/**
	 * Add a collection of rows to the table
	 * @param lines List
	 */
	public void addRows(List<Object[]> newLines) {
		final int start = lines.size();
		lines.addAll(newLines);
		fireTableRowsInserted(start, lines.size() - 1);
	}

	/** delete row at row index */
	public void removeRow(final int row) {
		lines.remove(row);
		fireTableRowsDeleted(row, row);
	}
	
	/** delete rows at row indexes */
	public void removeRows(final int ...rows) {
		Arrays.sort(rows);
		for(int i = rows.length - 1; i >= 0; i--) {
			lines.remove(rows[i]);
		}
		fireTableRowsDeleted(rows[0], rows[rows.length - 1]);
	}

	/**
	 * clear full table data
	 */
	public void clear() {
		lines.clear();
		fireTableDataChanged();
	}

	/**
	 * clear table data and columns
	 */
	public void clearModel() {
		columns = 0;
		lines.clear();
		fireTableStructureChanged();
	}

	// defined for editing
	@Override
	public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
		lines.get(rowIndex)[columnIndex] = aValue;
		fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	/**
	 * Swap rows at indexes row1 and row2
	 * @param row1
	 * @param row2
	 * @throws IndexOutOfBoundsException
	 */
	public void swapRows(final int row1, final int row2) throws IndexOutOfBoundsException {
		final Object[] r1 = lines.get(row1);
		lines.set(row1, lines.get(row2));
		lines.set(row2, r1);
		fireTableRowsUpdated(Math.min(row1, row2), Math.max(row1, row2));
	}
	
	/**
	 * Move row from position oldPos to position newPos; newPos is computed after removal.
	 * @throws IndexOutOfBoundsException
	 */
	public void moveRow(final int oldPos, final int newPos) throws IndexOutOfBoundsException {
		lines.add(newPos, lines.remove(oldPos));
		fireTableRowsUpdated(Math.min(oldPos, newPos), Math.max(oldPos, newPos));
	}

	/**
	 * get row index of the first occurrence of cell in column col
	 * @param cell Object
	 * @param col int
	 * @return int index or -1 if not found
	 */
	public int rowOf(final Object cell, final int col) {
		for (int i = 0; i < getRowCount(); i++) {
			if (getValueAt(i, col).equals(cell))
				return i;
		}
		return -1;
	}

	@Override
	public Class<?> getColumnClass(final int c) {
		for(int i = 0; i < lines.size(); i++) {
			final Object val = getValueAt(i, c);
			if(val != null) {
				return val.getClass();
			}
		}
		return Object.class;
	}
}
package it.usna.swing.table;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import it.usna.util.AppProperties;

/**
 * A swing JTable with column header tooltips, cell tooltips, column visibility control and
 * property (columns sizes, positions and visibility) saving abilities.
 * @author USNA - Antonio Flaccomio
 * @version 1.1
 */
public class TooltipTable extends JTable {
	private static final long serialVersionUID = 1L;
	protected final static String COL_WIDTH_PROP = "COL_W";
	protected final static String COL_POSITION_PROP = "COL_P";

	private TableColumn[] hiddenColumns;

	/**
	 * Default constructor
	 */
	public TooltipTable() {
	}

	/**
	 * Create a table with specified TableModel
	 * @param tm TableModel
	 */
	public TooltipTable(final TableModel tm) {
		super(tm);
		setHeadersTooltip((String[])null);
	}

	public void setHeadersTooltip(final String ... headerTips) {
		tableHeader = new JTableHeader(columnModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText(final MouseEvent evt) {
				final int tc = columnAtPoint(evt.getPoint());
				final int mc = convertColumnIndexToModel(tc);
				if(headerTips != null && mc < headerTips.length && headerTips[mc] != null) {
					return headerTips[mc];
				} else {
					final String name = getColumnName(tc);
//					final int strWidth = SwingUtilities.computeStringWidth(getGraphics().getFontMetrics(), name);
					final int strWidth = SwingUtilities.computeStringWidth(getFontMetrics(getFont()), name);
					return (getHeaderRect(tc).width <= strWidth) ? name : null;
				}
			}
		};
		tableHeader.setTable(this); // Otherwise sort graphics don't work	
	}

	/**
	 * Override the default method to show tooltip if the cell is not wide enough to fully show the value
	 */
	@Override
	public String getToolTipText(final MouseEvent evt) {
		if(isVisible()) {
			final int row, column;
			if ((row = rowAtPoint(evt.getPoint())) >= 0 && (column = columnAtPoint(evt.getPoint())) >= 0) {
//				final Object value = getValueAt(r, c);
//				final Component rendererComponent = this.getCellRenderer(r, c).getTableCellRendererComponent(this, value, false, false, r, c);
				final Component rendererComponent = prepareRenderer(getCellRenderer(row, column), row, column);
				return getToolTipText(getValueAt(row, column), getCellRect(row, column, false).width <= rendererComponent.getPreferredSize().width, row, column);
			}
		}
		return null;
	}

	/**
	 * By default TooltipTable write a tooltip if the cell is too small to show the full value
	 * @param value
	 * @param cellTooSmall true if getCellRect(r, c, false).width <= rendererComponent.getPreferredSize().width
	 * @param row table coordinates
	 * @param column table coordinates
	 * @return the String value or null if no tooltip must be displayed
	 */
	protected String getToolTipText(Object value, boolean cellTooSmall, int row, int column) {
		if(cellTooSmall) {
			final String strVal = cellValueAsString(value, row, column);
			if(strVal != null && strVal.isEmpty() == false) {
				return strVal;
			}
		}
		return null;
	}

	/**
	 * Try to map an Object cell value to a String value
	 */
	protected String cellValueAsString(Object value, int row, int column) {
		if(value == null) return "";
		else if(value instanceof Object[]) return Arrays.stream((Object[])value).filter(v -> v != null).map(v -> v.toString()).collect(Collectors.joining(" + "));
		else return value.toString();
	}

	/**
	 * Override the default method to show tooltip over the cell (if the cell is not wide enough to fully show the value)
	 */
	@Override
	public Point getToolTipLocation(final MouseEvent evt) {
		final int r = rowAtPoint(evt.getPoint());
		final int c = columnAtPoint(evt.getPoint());
		final Rectangle cellRec = getCellRect(r, c, true);
		return new Point(cellRec.x, cellRec.y);
	}

	/**
	 * Save columns position and visibility
	 */
	public void saveColPos(final AppProperties prop, final String prefix) {
		String pos[] = new String[getColumnCount()];
		for(int i = 0; i < pos.length; i++) {
			pos[i] = convertColumnIndexToModel(i) + "";
		}
		prop.setMultipleProperty(prefix + "." + COL_POSITION_PROP, pos, ',');
	}

	/**
	 * Restore columns position and visibility (columns must be original - model order - position; call restoreColumns() otherwise)
	 * @param prop AppProperties instance where data is stored
	 * @param prefix the prefix used to distinguish attributes among tables if more than one table is saved on the same Properties object
	 */
	public boolean loadColPos(final AppProperties prop, final String prefix) {
		try { // in case a newer/older version had a different number of columns
			String pos[] = prop.getMultipleProperty(prefix + "." + COL_POSITION_PROP, ',');
			if(pos != null) {
				int i;
				for(i = 0; i < pos.length; i++) {
					int modelPos = Integer.parseInt(pos[i]);
					moveColumn(convertColumnIndexToView(modelPos), i);
				}
				while(i < getColumnCount()) { // getColumnCount() decreases a every iteration
					hideColumn(convertColumnIndexToModel(i));
				}
				return true;
			}
		} catch(Exception e) {}
		return false;
	}

	/**
	 * Save table column width
	 * @param prop instance where data is stored
	 * @param prefix the prefix used to distinguish attributes among tables if more than one table is saved on the same Properties object
	 */
	public void saveColWidth(final AppProperties prop, final String prefix) {
		final int modelCol = dataModel.getColumnCount();
		String w[] = new String[modelCol];
		for (int col = 0; col < modelCol; col++) {
			int vc = convertColumnIndexToView(col);
			w[col] = (vc >= 0) ? columnModel.getColumn(vc).getWidth() + "" : "0";
		}
		prop.setMultipleProperty(prefix + "." + COL_WIDTH_PROP, w, ',');
	}

	public boolean loadColWidth(final AppProperties prop, final String prefix) {
		String w[] = prop.getMultipleProperty(prefix + "." + COL_WIDTH_PROP, ',');
		final int modelCol = dataModel.getColumnCount();
		if(w != null && w.length == modelCol) { // in case a newer/older version had a different number of columns
			for (int i = 0; i < modelCol; i++) {
				int vc = convertColumnIndexToView(i);
				if(vc >= 0) {
					columnModel.getColumn(vc).setPreferredWidth(Integer.parseInt(w[i]));	
				}
			}
			return true;
		}
		return false;
	}

	public void restoreColumns() {
		if (hiddenColumns != null) {
			Arrays.stream(hiddenColumns).filter(c -> c != null).forEach(c -> addColumn(c));
			hiddenColumns = null;
		}
		int pos = 0;
		for (int i = 0; i < dataModel.getColumnCount(); i++) {
			if(convertColumnIndexToView(i) >= 0) {
				moveColumn(convertColumnIndexToView(i), pos++);
			}
		}
	}

	/**
	 * Hide a column
	 * @param modelInd column to hide index in the table model
	 * @return the previous view index or -1 if column was not visible
	 */
	public int hideColumn(final int modelInd) {
		final int pos = convertColumnIndexToView(modelInd);
		if (pos != -1) {
			if(hiddenColumns == null) {
				hiddenColumns = new TableColumn[dataModel.getColumnCount()];
			}
			TableColumn col = columnModel.getColumn(pos);
			hiddenColumns[modelInd] = col;
			removeColumn(col);
		}
		return pos;
	}

	/**
	 * Previously hidden column will be shown
	 * @param modelInd column index to show in the table model
	 * @return true if the column was previously hidden
	 */
	public boolean showColumn(final int modelInd) {
		if (hiddenColumns != null && hiddenColumns[modelInd] != null) {
			addColumn(hiddenColumns[modelInd]);
			hiddenColumns[modelInd] = null;
			return true;
		}
		return false;
	}

	/**
	 * Previously hidden column will be shown
	 * @param modelInd column index to show in the table model
	 * @param viewPos new column position; if < 0 try to hint
	 */
	public void showColumn(final int modelInd, int viewPos) {
		if(showColumn(modelInd)) {
			if(viewPos < 0) {
				for(int i = modelInd - 1; i >= 0; i--) {
					if(isColumnVisible(i)) {
						moveColumn(getColumnCount() - 1, convertColumnIndexToView(i) + 1);
						return;
					}
				}
				moveColumn(getColumnCount() - 1, 0);
			} else {
				moveColumn(getColumnCount() - 1, viewPos);
			}
		}
	}

	/**
	 * Check if a column is visible
	 * @param modelInd int column index in the table model
	 * @return boolean
	 */
	public boolean isColumnVisible(final int modelInd) {
		return hiddenColumns == null || hiddenColumns[modelInd] == null;
	}
}
package it.usna.swing.table;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Collections;

import javax.swing.Icon;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;


/**
 * <p>Title: ExTootipTable</p>
 * <p>extends ToolTipTable providing new constructors and sorting support.</p>
 * <p>Company: USNA</p>
 * @author Antonio Flaccomio
 * @version 1.0
 */
public class ExTooltipTable extends TooltipTable {
	private static final long serialVersionUID = 1L;

	/*public ExTooltipTable() throws IntrospectionException {
		BeanInfo info = Introspector.getBeanInfo(ExTooltipTable.class);
		PropertyDescriptor[] propertyDescriptors =
		                             info.getPropertyDescriptors();
		for (int i = 0; i < propertyDescriptors.length; ++i) {
		    PropertyDescriptor pd = propertyDescriptors[i];
		    if (pd.getName().equals("rowSorter")) {
		        pd.setValue("transient", Boolean.TRUE);
		    }
		    if (pd.getName().equals("model")) {
		        pd.setValue("transient", Boolean.TRUE);
		    }
		    if (pd.getName().equals("tableHeader")) {
		        pd.setValue("transient", Boolean.TRUE);
		    }
		}
	}*/

	/**
	 * Create a table with specified TableModel and column widths
	 * @param tm
	 * @param colSize
	 */
	public ExTooltipTable(final TableModel tm, final int ... colSize) {
		super(tm);
		for(int ind = 0; ind < colSize.length && ind < tm.getColumnCount(); ind++) {
			final int size;
			if((size = colSize[ind]) >= 0) {
				columnModel.getColumn(ind).setPreferredWidth(size);
			}
		}
	}

	/**
	 * Create a sortable table with specified TableModel and column widths
	 * @param tm
	 * @param colSize
	 */
	public ExTooltipTable(final TableModel tm, final boolean sort, final int ... colSize) {
		this(tm, colSize);
		setAutoCreateRowSorter(sort);
	}

	public void clearSort() {
		getRowSorter().setSortKeys(Collections.<RowSorter.SortKey>emptyList());
	}

	public void sortByColumn(final int col, final boolean ascending) {
		getRowSorter().setSortKeys(Collections.<RowSorter.SortKey>singletonList(new RowSorter.SortKey(col, ascending ? SortOrder.ASCENDING : SortOrder.DESCENDING)));
	}

	/*public int findFirstRow(final int col, final Object colVal) {
		for(int i = 0; i < getRowCount(); i++) {
			if(colVal.equals(getValueAt(i, col))) {
				return i;
			}
		}
		return -1;
	}*/

	/**
	 * Adapt columns width to content (toString()). Call only after the table is displayed
	 */
	public void columnsWidthAdapt() {
		Graphics g = getGraphics();
		if(g != null) {
			final FontMetrics fm = g.getFontMetrics();
			for(int c = 0; c < getColumnCount(); c++) {
				Object val = columnModel.getColumn(c).getHeaderValue();
				int width = val != null ? SwingUtilities.computeStringWidth(fm, val.toString()) : 1;
				for(int r = 0; r < getRowCount(); r++) {
					val = getValueAt(r, c);
					if(val != null) {
						if(val instanceof Icon) {
							width = Math.max(width, ((Icon)val).getIconWidth());
						} else {
							width = Math.max(width, SwingUtilities.computeStringWidth(fm, val.toString()));
						}
					}
				}
				columnModel.getColumn(c).setPreferredWidth(width);
			}
		}
	}
}
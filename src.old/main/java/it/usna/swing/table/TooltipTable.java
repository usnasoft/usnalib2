package it.usna.swing.table;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Properties;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * A swing JTable with column header tooltips, cell tooltips, column visibility control and
 * property (columns sizes, positions and visibility) saving abilities.
 * @author Antonio Flaccomio
 * @version 1.0
 */
public class TooltipTable extends JTable {

	private static final long serialVersionUID = 1L;
	private final static String COL_WIDTH = "COL_W";
	private final static String COL_POSITION = "COL_P";
	private final static String VISIBLE_COLS = "COL_V";

	private TableColumn[] hiddenColumns;

	/**
	 * Default constructor
	 */
	public TooltipTable() {
	}

	/**
	 * Create a table with specified TableModel and column tooltip values
	 * @param tm TableModel
	 * @param header String[] columns tooltip values
	 */
//	public TooltipTable(final TableModel tm, final String ... headerTips) {
//		super(tm);
//		hiddenColumns = new TableColumn[tm.getColumnCount()];
//		// header tooltips
//		tableHeader = new JTableHeader(super.columnModel) {
//			private static final long serialVersionUID = 1L;
//
//			public String getToolTipText(final MouseEvent evt) {
//				final int c = convertColumnIndexToModel(columnAtPoint(evt.getPoint()));
//				return (c < headerTips.length) ? headerTips[c] : null;
//			}
//		};
//		tableHeader.setTable(this); // Otherwise sort graphics don't work
//	}
	
	/**
	 * Create a table with specified TableModel
	 * @param tm TableModel
	 * @param header String[]
	 */
	public TooltipTable(final TableModel tm) {
		super(tm);
		hiddenColumns = new TableColumn[tm.getColumnCount()];
		tableHeader = new JTableHeader(super.columnModel) {
			private static final long serialVersionUID = 1L;

			public String getToolTipText(final MouseEvent evt) {
				final int c = columnAtPoint(evt.getPoint());
				if(c >= 0) {
					final String name = getColumnName(c);
					final int strWidth = SwingUtilities.computeStringWidth(getGraphics().getFontMetrics(), name);
					return (getHeaderRect(c).width <= strWidth) ? name : null;
//					return (getCellRect(0, c, false).width <= strWidth) ? name : null;
				} else {
					return null;
				}
				 //return (getHeaderRect(c).width <= strWidth) ? name : null;
			}
		};
		tableHeader.setTable(this); // Otherwise sort graphics don't work
	}
	
	public void setHeadersTooltip(final String ... headerTips) {
		tableHeader = new JTableHeader(super.columnModel) {
			private static final long serialVersionUID = 1L;

			public String getToolTipText(final MouseEvent evt) {
				final int c = convertColumnIndexToModel(columnAtPoint(evt.getPoint()));
				return (c < headerTips.length) ? headerTips[c] : null;
			}
		};
		tableHeader.setTable(this); // Otherwise sort graphics don't work	
	}


	@Override
	public void setModel(final TableModel tm) {
		super.setModel(tm);
		hiddenColumns = new TableColumn[tm.getColumnCount()];
	}

	/**
	 * Override the default method to show tooltip if the cell is not wide enough to fully show the value
	 */
	@Override
	public String getToolTipText(final MouseEvent evt) {
		// Questo metodo verifica che la stringa da visualizzare non sia piu' estesa delle cella; se lo e' visualizza il tooltip.
		// Nota: se la stringa e' di tipo html il calcolo dell'estensione non e' valido!
		if (((Component) evt.getSource()).isVisible()) {
			final int r, c;
			final Object value;
			if ((r = rowAtPoint(evt.getPoint())) >= 0 && (c = columnAtPoint(evt.getPoint())) >= 0 && (value = getValueAt(r, c)) != null) {
				if(value instanceof Cell) {
					return ((Cell)value).getDescription();
				} else {
					Component comp = this.getCellRenderer(r, c).getTableCellRendererComponent(this, value, false, false, r, c);
//					final int strWidth = SwingUtilities.computeStringWidth(getGraphics().getFontMetrics(), value.toString());
					if (getCellRect(r, c, false).width <= /*strWidth*/comp.getPreferredSize().width)
						return value.toString();
				}
			}
		}
		return null;
	}

	/**
	 * Override the default method to show tooltip if the cell is not wide enough to fully show the value
	 */
	@Override
	public Point getToolTipLocation(final MouseEvent evt) {
		final int r = rowAtPoint(evt.getPoint());
		final int c = columnAtPoint(evt.getPoint());
		final Rectangle cellRec = getCellRect(r, c, true);
		return new Point(cellRec.x, cellRec.y);
	}

	/**
	 * Save table properties on a Properties object; managed properties are:
	 * columns visibility, columns position, column width
	 * @param prop Properties
	 * @param prefix String the prefix used to distinguish attributes among tables if more than one table is saved on the same Properties object
	 */
	public void saveProperties(final Properties prop, final String prefix) {
		// colonne visibili
		prop.setProperty(prefix + "." + VISIBLE_COLS, getColumnCount() + "");

		// posizione delle colonne visibili
		for (int col = 0; col < getColumnCount(); col++) {
			prop.setProperty(prefix + "." + COL_POSITION + "." + col, convertColumnIndexToModel(col) + "");
		}
		// dimensione delle colonne visibili
		for (int col = 0; col < getColumnCount(); col++) {
			final int w = columnModel.getColumn(col).getWidth();
			prop.setProperty(prefix + "." + COL_WIDTH + "." + col, w + "");
		}
	}

	public void loadProperties(final Properties prop, final String prefix) {
		// colonne visibili
		final int vis = Integer.parseInt(prop.getProperty(prefix + "." + VISIBLE_COLS, getColumnCount() + ""));

		// posizione delle colonne visibili
		for (int i = 0; i < vis; i++) {
			final String propName = prefix + "." + COL_POSITION + "." + i;
			final int pos = Integer.parseInt(prop.getProperty(propName, i + ""));
			moveColumn(convertColumnIndexToView(pos), i);
		}
		// rimozione delle colonne non visibili
		for (int i = getModel().getColumnCount() - 1; i >= vis; i--) {
			hideColumn(convertColumnIndexToModel(i));
		}
		// dimensione delle colonne visibili
		for (int i = 0; i < vis; i++) {
			final String propName = prefix + "." + COL_WIDTH + "." + i;
			final int width = Integer.parseInt(prop.getProperty(propName, "-1"));
			if (width > 0)
				columnModel.getColumn(i).setPreferredWidth(width);
		}
	}

	/**
	 * Define a column not to be rendered
	 * @param modelInd int column index in the table model
	 */
	public void hideColumn(final int modelInd) {
		final int pos = convertColumnIndexToView(modelInd);
		if (pos != -1) {
			hiddenColumns[modelInd] = columnModel.getColumn(pos);
			removeColumn(hiddenColumns[modelInd]);
		}
	}

	/**
	 * Define a previously hidden column to be shown
	 * @param modelInd int column index in the table model
	 */
	public void showColumn(final int modelInd) {
		if (hiddenColumns[modelInd] != null) {
			addColumn(hiddenColumns[modelInd]);
			hiddenColumns[modelInd] = null;
		}
	}

	/**
	 * Check if a column is visible
	 * @param modelInd int column index in the table model
	 * @return boolean
	 */
	public boolean isColumnVisible(final int modelInd) {
		return hiddenColumns[modelInd] == null;
	}
	
	public interface Cell {
		String getDescription();
	}
	
//	public class CellImpl implements Cell {
//		final Object val;
//		
//		@Override
//		public String getDescription() {
//			// TODO Auto-generated method stub
//			return null;
//		}	
//	}
}
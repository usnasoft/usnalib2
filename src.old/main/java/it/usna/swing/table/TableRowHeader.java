package it.usna.swing.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * Use this class as row header for a scrollable JTable.
 * Example:<br> 
 * <code>
 * jScrollPane.setViewportView(jTable);
 * jScrollPane.setRowHeaderView(new TableRowHeader(jTable));
 * </code>
 * @author Antonio Flaccomio
 * @version 1.0
 */
public class TableRowHeader extends JList<String> {
	private static final long serialVersionUID = 1L;
	//private static final int INIT_WIDTH = 10;
	private static final Color BG_COLOR = Color.lightGray;
	private static final Color FG_COLOR = Color.white;
	private int rowCount = 0;

	public TableRowHeader() {
		//setFixedCellWidth(INIT_WIDTH);
		setBackground(BG_COLOR);
		setEnabled(false);
	}

	public TableRowHeader(final JTable table) {
		this();
		setTable(table);
	}

	public void setTable(final JTable table) {
		rowCount = table.getRowCount();
		// JList model
		setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;
			// ListModel constructor
			{
				table.getModel().addTableModelListener(new TableModelListener() {
					public void tableChanged(TableModelEvent e) {
						final int oldCount = rowCount;
						rowCount = table.getRowCount();
						if (rowCount != oldCount) {
							final int strWidth = SwingUtilities.computeStringWidth(getFontMetrics(table.getFont()), rowCount + "");
							setFixedCellWidth(strWidth + 4);
							if (rowCount > oldCount) {
								fireIntervalAdded(this, oldCount, rowCount - 1);
							} else {
								fireIntervalRemoved(this, rowCount, oldCount - 1);
							}
						}
					}
				});
			}

			@Override
			public String getElementAt(final int index) {
				return (index + 1) + "";
			}

			@Override
			public int getSize() {
				return rowCount;
			}
		});
		// JList renderer
		setCellRenderer(new ListCellRenderer<String>() {
			private final JLabel res = new JLabel();
			{
				res.setFont(table.getFont());
				res.setForeground(FG_COLOR);
			}

			@Override
			public Component getListCellRendererComponent(final JList<? extends String> list, final String value, final int index, final boolean isSelected, final boolean cellHasFocus) {
				res.setText((String) value);
				return res;
			}
		});

		final int strWidth = SwingUtilities.computeStringWidth(getFontMetrics(table.getFont()), rowCount + "");
		setFixedCellWidth(strWidth + 4);
		setFixedCellHeight(table.getRowHeight());
	}
}
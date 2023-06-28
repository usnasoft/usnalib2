package it.usna.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

public class ArrayTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	private static Border EMPTY_BORDER;
	private final static Border FOCUS_BORDER = UIManager.getBorder("Table.focusCellHighlightBorder");
	private JPanel p = new JPanel();

	public ArrayTableCellRenderer() {
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		final Insets borderInsets = FOCUS_BORDER.getBorderInsets(this);
		EMPTY_BORDER = BorderFactory.createEmptyBorder(borderInsets.top, borderInsets.left, borderInsets.bottom, borderInsets.right);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if(value instanceof Object[]) {
			p.removeAll();
			final Color foregroundColor = isSelected ? table.getSelectionForeground() : table.getForeground();
			for(Object v: (Object[])value) {
				try {
					JLabel l = new JLabel(v.toString());
					l.setForeground(foregroundColor);
					p.add(l);
				} catch(NullPointerException e) {}
			}
			p.setBorder(hasFocus ? FOCUS_BORDER : EMPTY_BORDER);
			return p;
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}


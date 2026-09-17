package it.usna.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

public class ArrayTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	private static final Border FOCUS_BORDER = UIManager.getBorder("Table.focusCellHighlightBorder");
	private Border emptyBorder;
	private JPanel p = new JPanel();
	
	public ArrayTableCellRenderer(VerticalFlowLayout2.VAlign align, VerticalFlowLayout2.HAlign hAalign, int hgap, int vgap) {
		p.setLayout(new VerticalFlowLayout2(align, hAalign, hgap, vgap));
		final Insets borderInsets = FOCUS_BORDER.getBorderInsets(this);
		emptyBorder = BorderFactory.createEmptyBorder(borderInsets.top, borderInsets.left, borderInsets.bottom, borderInsets.right);
	}

	public ArrayTableCellRenderer() {
		this(VerticalFlowLayout2.VAlign.CENTER, VerticalFlowLayout2.HAlign.LEFT, 0, 0);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if(value instanceof Object[] arr) {
			p.removeAll();
			final Color foregroundColor = isSelected ? table.getSelectionForeground() : table.getForeground();
			for(Object v: arr) {
				try {
					JLabel l = new JLabel(v.toString());
					l.setForeground(foregroundColor);
					p.add(l);
				} catch(NullPointerException e) {}
			}
			p.setBorder(hasFocus ? FOCUS_BORDER : emptyBorder);
			return p;
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}

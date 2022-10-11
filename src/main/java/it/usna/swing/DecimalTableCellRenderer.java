package it.usna.swing;

import java.text.NumberFormat;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class DecimalTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	private final NumberFormat formatter;

	public DecimalTableCellRenderer(int decimalDigits) {
		setHorizontalAlignment(SwingConstants.RIGHT);
		formatter = NumberFormat.getInstance();
		formatter.setMaximumFractionDigits(decimalDigits);
		formatter.setMinimumFractionDigits(decimalDigits);
	}

	@Override
	public void setValue(Object value) {
        setText(value != null ? formatter.format(value) : "");
    }
}

package it.usna.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.text.ParseException;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * <p>IntegerTableCellEditor</p>
 * <p>TableCellEditor implementation for Integer fields</p>
 * <p>Company: USNA</p>
 * @version 1.0
 * @author - Antonio Flaccomio
 */
public class IntegerTableCellEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long serialVersionUID = 1L;
	
	private long min;
	private long max;
	private boolean editable;
	private NumericTextField<Long> itf;
	
	/**
	 * @param min, minimum allowed value
	 * @param max, maximum allowed value
	 */
	public IntegerTableCellEditor(final long min, final long max) {
		this.min = min;
		this.max = max;
		this.editable = true;
	}
	
	/**
	 * @param editable, true the field is editable,
	 * 	false the field is editable only by the arrows
	 */
	public void setEditable(final boolean editable) {
		this.editable = editable;
	}
	
	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int col) {
		final long val = ((Number)value).longValue();
		itf = new NumericTextField<>(val, min, max);
		itf.setHorizontalAlignment(NumericTextField.RIGHT);
		itf.setEditable(editable);
		itf.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		// Button up
		final JButton up = itf.getArrowUp();
//		up.setContentAreaFilled(false);
		up.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));

		// Button down
		final JButton down = itf.getArrowDown();
//		down.setContentAreaFilled(false);
		down.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
		
		final JPanel pan = new JPanel(new BorderLayout());
		pan.add(itf, BorderLayout.CENTER);
		final JPanel buttPan = new JPanel(new BorderLayout());
		buttPan.add(up, BorderLayout.WEST);
		buttPan.add(down, BorderLayout.EAST);
		pan.add(buttPan, BorderLayout.WEST);
		return pan;
	}

	@Override
	public Object getCellEditorValue() {
		try {
			itf.commitEdit();
		} catch (ParseException e) {
			// e.printStackTrace();
		}
		return Integer.valueOf(itf.getIntValue());
	}
}
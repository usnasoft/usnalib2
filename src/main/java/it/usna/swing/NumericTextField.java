package it.usna.swing;

import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.NumberFormatter;

/**
 * <p>NumericTextField</p>
 * <p>Formatted field for Number subclasses with minimum
 * and maximum values specified.<br>
 * Can also generate buttons to increment and decrement the value.</p>
 * <p>Copyright (c) 2021 - 2006 as IntegerTextField</p>
 * <p>Company: USNA</p>
 * @version 2.0
 * @author Antonio Flaccomio
 */
public class NumericTextField<T extends Number & Comparable<T>> extends JFormattedTextField {
	private static final long serialVersionUID = 3L;
	private boolean allowNull;

	/**
	 * Constructors specifying the initial value, the minimum
	 * allowed value and the maximum allowed value for the field.
	 */
	public NumericTextField(final T init, final T min, final T max) {
		this.allowNull = false;
		init(min, max);
		setValue(init);
	}
	
	public NumericTextField(final T init, final T min, final T max, boolean allowNull) {
		this.allowNull = allowNull;
		init(min, max);
		setValue(init);
	}
	
	public NumericTextField(final T min, final T max) {
		this.allowNull = true;
		init(min, max);
	}
	
	public void allowNull(boolean allow) {
		this.allowNull = allow;
	}
	
	private void init(final T min, final T max) {
		final DefaultFormatterFactory ff = new DefaultFormatterFactory();
		final InternationalFormatter formatter = new NumberOrNullFormatter();
		ff.setDefaultFormatter(formatter);
		ff.setEditFormatter(formatter);
		formatter.setMaximum(max);
		formatter.setMinimum(min);
		setFormatterFactory(ff);
	}	

	private class NumberOrNullFormatter extends NumberFormatter {
		private static final long serialVersionUID = 1L;

		@Override
		public Object stringToValue(String string) throws ParseException {
			return string.isEmpty() && allowNull ? null : super.stringToValue(string);
		}
	}

	public void setLimits(final T min, final T max) {
		final DefaultFormatterFactory ff = (DefaultFormatterFactory)getFormatterFactory();
		final InternationalFormatter ediF = (InternationalFormatter)ff.getEditFormatter();
		if(ediF != null) {
			ediF.setMaximum(max);
			ediF.setMinimum(min);
		}
	}

	public void setGroupingUsed(final boolean use) {
		((NumberFormat)((InternationalFormatter) getFormatter()).getFormat()).setGroupingUsed(use);
	}
	
	public boolean isEmpty() {
		 return getValue() == null || getValue() instanceof Number == false;
	}

	public int getIntValue() {
		return /*isEmpty() ? 0 :*/ getNumber().intValue();
	}

	public long getLongValue() {
		return /*isEmpty() ? 0L :*/ getNumber().longValue();
	}
	
	public float getFloatValue() {
		return /*isEmpty() ? 0 :*/ getNumber().floatValue();
	}
	
	public double getDoubleValue() {
		return /*isEmpty() ? 0 :*/ getNumber().doubleValue();
	}
	
	@SuppressWarnings("unchecked")
	public T getNumber() {
		return (T)getValue();
	}
	
	/**
	 * Creates and returns a JButton whose action increment the field by 1
	 * @return
	 */
	public JButton getArrowUp() {
		final JButton up = new JButton(upAction());
		up.setBorderPainted(false);
		up.setContentAreaFilled(false);
		up.setMargin(new java.awt.Insets(1, 0, 1, 0));
		return up;
	}
	
	public Action upAction() {
		return new AbstractAction() {
			private static final long serialVersionUID = 1L;
			{
				putValue(LARGE_ICON_KEY, new ImageIcon(NumericTextField.class.getResource("/img/beck_up.gif")));	
			}

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					commitEdit();
				} catch (ParseException e1) {}
				final DefaultFormatterFactory ff = (DefaultFormatterFactory)getFormatterFactory();
				if(isEmpty()) {
					NumericTextField.this.setValue(((InternationalFormatter)ff.getEditFormatter()).getMinimum());
					NumericTextField.this.fireActionPerformed();
				} else if(((Comparable<T>)((InternationalFormatter)ff.getEditFormatter()).getMaximum()).compareTo(getNumber()) > 0) {
					final Number val = getNumber();
					if(val instanceof Float) {
						NumericTextField.this.setValue(val.floatValue() + 1f);
					} else if (val instanceof Double) {
						NumericTextField.this.setValue(val.doubleValue() + 1d);
					} else if (val instanceof Integer){
						NumericTextField.this.setValue(val.intValue() + 1);
					} else { // Long
						NumericTextField.this.setValue(val.longValue() + 1L);
					}
					NumericTextField.this.fireActionPerformed();
				}
			}
		};
	}
	
	/**
	 * Creates and returns a JButton whose action decrement the field by 1
	 * @return
	 */
	public JButton getArrowDown() {
		final JButton down = new JButton(downAction());
		down.setBorderPainted(false);
		down.setContentAreaFilled(false);
		down.setMargin(new java.awt.Insets(1, 0, 1, 0));
		return down;
	}
	
	public Action downAction() {
		return new AbstractAction() {
			private static final long serialVersionUID = 1L;
			{
				putValue(LARGE_ICON_KEY, new ImageIcon(NumericTextField.class.getResource("/img/beck_down.gif")));	
			}

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					commitEdit();
				} catch (ParseException e1) {}
				final DefaultFormatterFactory ff = (DefaultFormatterFactory)getFormatterFactory();
				if(isEmpty() == false && ((Comparable<T>)((InternationalFormatter)ff.getEditFormatter()).getMinimum()).compareTo(getNumber()) < 0) {
					final Number val = getNumber();
					if(val instanceof Float) {
						NumericTextField.this.setValue(val.floatValue() - 1f);
					} else if (val instanceof Double) {
						NumericTextField.this.setValue(val.doubleValue() - 1d);
					} else if (val instanceof Integer){
						NumericTextField.this.setValue(val.intValue() - 1);
					} else { // Long
						NumericTextField.this.setValue(val.longValue() - 1L);
					}
					NumericTextField.this.fireActionPerformed();
				}
			}
		};
	}
}
package it.usna.swing.gadget;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import it.usna.swing.NumericTextField;

/**
 * <p>Title: CalendarPanel</p>
 * <p>Calendar panel.<br>
 * 3 messages implemented: EVT_DAY_SELECTED, EVT_YEAR_SELECTED, EVT_MONTH_SELECTED.</p>
 * <p>Copyright (c) 2006</p>
 * <p>Company: USNA</p>
 * @author Antonio Flaccomio
 * @version 1.0
 */
public class CalendarPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public final static String EVT_DAY_SELECTED = "DAY_SEL";  //  @jve:decl-index=0:
	public final static String EVT_YEAR_SELECTED = "YEAR_SEL";  //  @jve:decl-index=0:
	public final static String EVT_MONTH_SELECTED = "MONTH_SEL";

	private JPanel daysPanel = null;
	//private JLabel dataOggi = null; // Label in basso con data in chiaro
	//private final DateFormat dateFormatter;
	private final static java.awt.Color SEL_DAY_COLOR = new java.awt.Color(240, 128, 20);
	private JComboBox<String> comboMonths = null;
	private final GregorianCalendar today;
	private JPanel meseAnno = null;
	private JPanel yearPanel = null;
	private NumericTextField<Integer> year = null;
	private JButton yearLess1Btn = null;
	private JButton yearPlus1Btn = null;
	private final String[] dayNames;
	private final Locale loc;

	/**
	 * This is the default constructor
	 */
	public CalendarPanel() {
		this(new GregorianCalendar(), Locale.getDefault());
	}
	
	public CalendarPanel(final Locale loc) {
		this(new GregorianCalendar(loc), loc);
	}

	public CalendarPanel(final GregorianCalendar today) {
		this(today, Locale.getDefault());
	}
	
	public CalendarPanel(final GregorianCalendar today, final Locale loc) {
		this.loc = loc;
		this.today = today;
		dayNames = dayNames();
		initialize();
		//dateFormatter = DateFormat.getDateInstance(DateFormat.LONG, loc);
		fillDays();
		comboMonths.setSelectedIndex(today.get(GregorianCalendar.MONTH) - GregorianCalendar.JANUARY);
		year.setValue(today.get(GregorianCalendar.YEAR));
	}
	
	public void setCalendar(final GregorianCalendar day) {
		today.setTime(day.getTime());
		fillDays();
		comboMonths.setSelectedIndex(today.get(GregorianCalendar.MONTH) - GregorianCalendar.JANUARY);
		year.setValue(today.get(GregorianCalendar.YEAR));
	}

	public GregorianCalendar getCalendar() {
		return today;
	}

	private void initialize() {
		this.setLayout(new BorderLayout(0, 1));
		this.add(getDaysPanel(), java.awt.BorderLayout.CENTER);
		this.add(getMonthYear(), java.awt.BorderLayout.NORTH);
	}

	private void fillDays() {
		EventQueue.invokeLater(() -> {
			daysPanel.removeAll();
			// write days names
			for (int i = 0; i < 7; i++) {
				final JLabel dayName = new JLabel(dayNames[i]);
				dayName.setForeground(java.awt.Color.BLUE);
				daysPanel.add(dayName);
			}
			//daysPanel.validate(); // ?
			// Write today date
			// Shift first days
			final GregorianCalendar gc1 = (GregorianCalendar) today.clone();
			gc1.set(GregorianCalendar.DAY_OF_MONTH, 1);
			final int shift = (7 + gc1.get(GregorianCalendar.DAY_OF_WEEK) - gc1.getFirstDayOfWeek()) % 7;
			for (int i = 0; i < shift; i++) {
				daysPanel.add(new JLabel());
			}
			// Write month
			final int currentDayNum = today.get(GregorianCalendar.DAY_OF_MONTH);
			for (int i = 1; i <= today.getActualMaximum(GregorianCalendar.DAY_OF_MONTH); i++) {
				final JButton dayBut = new JButton(i + "");
				dayBut.setMargin(new java.awt.Insets(0, 0, 0, 0));
				dayBut.setBorder(BorderFactory.createEmptyBorder());
				//dayBut.setBorderPainted(false);
				dayBut.setContentAreaFilled(false);
				final int dayNum = i;
				if (currentDayNum == dayNum) {
					dayBut.setForeground(SEL_DAY_COLOR);
				}
				dayBut.addActionListener(e -> {
					today.set(GregorianCalendar.DAY_OF_MONTH, dayNum);
					fillDays();
					firePropertyChange(EVT_DAY_SELECTED, null, today);
				});
				daysPanel.add(dayBut);
			}
			daysPanel.revalidate();
			daysPanel.repaint();
		});
	}

	private JPanel getDaysPanel() {
		if (daysPanel == null) {
			GridLayout gridLayout = new GridLayout(0, 7);
			daysPanel = new JPanel();
			daysPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			daysPanel.setBackground(java.awt.Color.white);
			daysPanel.setLayout(gridLayout);
		}
		return daysPanel;
	}

	private String[] dayNames() {
		final DateFormat dayNameFormatter = new SimpleDateFormat("EEE", loc);
		String[] ret = new String[7];
		final GregorianCalendar gc = new GregorianCalendar(loc);
		gc.set(GregorianCalendar.DAY_OF_WEEK, gc.getFirstDayOfWeek());
		for (int i = 0; i < 7; i++) {
			ret[i] = dayNameFormatter.format(gc.getTime());
			gc.add(GregorianCalendar.DAY_OF_WEEK, 1);
		}
		return ret;
	}

	private String[] monthNames() {
		final DateFormat monthNameFormatter = new SimpleDateFormat("MMMMM", loc);
		String[] ret = new String[12];
		final GregorianCalendar gc = new GregorianCalendar(loc);
		gc.set(2000, GregorianCalendar.JANUARY, 1);
		for (int i = 0; i < 12; i++) {
			ret[i] = " " + monthNameFormatter.format(gc.getTime());
			gc.add(GregorianCalendar.MONTH, 1);
		}
		return ret;
	}

	private JComboBox<String> getComboMonths() {
		if (comboMonths == null) {
			comboMonths = new JComboBox<String>(monthNames());
			comboMonths.setMaximumRowCount(12);
			comboMonths.addActionListener(e -> {
				final int month = GregorianCalendar.JANUARY + comboMonths.getSelectedIndex();
				// the new month has the current day?
				final GregorianCalendar gc = new GregorianCalendar(loc);
				gc.set(today.get(GregorianCalendar.YEAR), month, 1);
				final int maxDay = gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
				if (maxDay < today.get(GregorianCalendar.DAY_OF_MONTH)) {
					today.set(GregorianCalendar.DAY_OF_MONTH, maxDay);
				}
				today.set(GregorianCalendar.MONTH, month);
				fillDays();
				firePropertyChange(EVT_MONTH_SELECTED, null, today);
			});
		}
		return comboMonths;
	}

	private JPanel getMonthYear() {
		if (meseAnno == null) {
			meseAnno = new JPanel(new BorderLayout());
			meseAnno.setPreferredSize(new java.awt.Dimension(130, 25));
			meseAnno.add(getComboMonths(), BorderLayout.WEST);
			meseAnno.add(getYearPanel(), BorderLayout.EAST);
		}
		return meseAnno;
	}

	private JPanel getYearPanel() {
		if (yearPanel == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setVgap(1);
			yearPanel = new JPanel(flowLayout);
			yearPanel.add(getAnnoMeno(), null);
			yearPanel.add(getYear(), null);
			yearPanel.add(getAnnoPiu(), null);
		}
		return yearPanel;
	}

	private NumericTextField<Integer> getYear() {
		if (year == null) {
			year = new NumericTextField<Integer>(1, 1, 9999);
			year.setColumns(4);
			year.setHorizontalAlignment(JTextField.CENTER);
			year.setGroupingUsed(false);
			year.addActionListener(e -> cambioAnno(year.getIntValue()));
			year.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					try {
						year.commitEdit();
						cambioAnno(year.getIntValue());
					} catch (ParseException e1) {
						//e1.printStackTrace();
					}
				}
			});
		}
		return year;
	}

	private JButton getAnnoMeno() {
		if (yearLess1Btn == null) {
			yearLess1Btn = new JButton(getYear().downAction());
			yearLess1Btn.setIcon(new ImageIcon(CalendarPanel.class.getResource("left.gif")));
			yearLess1Btn.setBorderPainted(false);
			yearLess1Btn.setContentAreaFilled(false);
//			annoMeno.setMargin(new java.awt.Insets(0, 0, 0, 0));
			yearLess1Btn.setBorder(BorderFactory.createEmptyBorder());
		}
		return yearLess1Btn;
	}

	private JButton getAnnoPiu() {
		if (yearPlus1Btn == null) {
			yearPlus1Btn = new JButton(getYear().upAction());
			yearPlus1Btn.setIcon(new ImageIcon(CalendarPanel.class.getResource("right.gif")));
			yearPlus1Btn.setBorderPainted(false);
			yearPlus1Btn.setContentAreaFilled(false);
//			annoPiu.setMargin(new java.awt.Insets(0, 0, 0, 0));
			yearPlus1Btn.setBorder(BorderFactory.createEmptyBorder());
		}
		return yearPlus1Btn;
	}
	
	private void cambioAnno(final int newYear) {
		// Gestione del 29 febbraio
		//System.out.println(nuovoAnno);
		final GregorianCalendar gc = new GregorianCalendar(loc);
		gc.set(newYear, today.get(GregorianCalendar.MONTH), 1);
		final int maxDay = gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		if (maxDay < today.get(GregorianCalendar.DAY_OF_MONTH)) {
			today.set(GregorianCalendar.DAY_OF_MONTH, maxDay);
		}
		today.set(GregorianCalendar.YEAR, newYear);
		fillDays();
		firePropertyChange(EVT_YEAR_SELECTED, null, today);
	}
}
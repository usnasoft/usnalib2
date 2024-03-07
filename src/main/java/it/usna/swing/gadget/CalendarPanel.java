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
	private JPanel annoPanel = null;
	private NumericTextField<Integer> anno = null;
	private JButton annoMeno = null;
	private JButton annoPiu = null;
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
		anno.setValue(today.get(GregorianCalendar.YEAR));
	}
	
	public void setCalendar(final GregorianCalendar day) {
		today.setTime(day.getTime());
		fillDays();
		comboMonths.setSelectedIndex(today.get(GregorianCalendar.MONTH) - GregorianCalendar.JANUARY);
		anno.setValue(today.get(GregorianCalendar.YEAR));
	}

	public GregorianCalendar getCalendar() {
		return today;
	}

	private void initialize() {
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setVgap(1);
		//dataOggi = new JLabel();
		this.setLayout(borderLayout);
		//this.setSize(250, 180);
		this.add(getDaysPanel(), java.awt.BorderLayout.CENTER);
		//this.add(dataOggi, java.awt.BorderLayout.SOUTH);
		this.add(getMeseAnno(), java.awt.BorderLayout.NORTH);
	}

	private void fillDays() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				daysPanel.removeAll();
				// write days names
				for (int i = 0; i < 7; i++) {
					final JLabel dayName = new JLabel(dayNames[i]);
					dayName.setForeground(java.awt.Color.BLUE);
					daysPanel.add(dayName);
				}
				//daysPanel.validate(); // ?
				// Write today date
				//dataOggi.setText(dateFormatter.format(today.getTime()));
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
			}
		});
	}

	private JPanel getDaysPanel() {
		if (daysPanel == null) {
			GridLayout gridLayout = new GridLayout(0, 7);
			daysPanel = new JPanel();
			daysPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
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

	private JPanel getMeseAnno() {
		if (meseAnno == null) {
			BorderLayout layout = new BorderLayout();
			meseAnno = new JPanel();
			meseAnno.setPreferredSize(new java.awt.Dimension(130, 25));
			meseAnno.setLayout(layout);
			meseAnno.add(getComboMonths(), BorderLayout.WEST);
			meseAnno.add(getAnnoPanel(), BorderLayout.EAST);
		}
		return meseAnno;
	}

	private JPanel getAnnoPanel() {
		if (annoPanel == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setVgap(1);
			annoPanel = new JPanel();
			annoPanel.setLayout(flowLayout);
			annoPanel.add(getAnnoMeno(), null);
			annoPanel.add(getAnno(), null);
			annoPanel.add(getAnnoPiu(), null);
		}
		return annoPanel;
	}

	private NumericTextField<Integer> getAnno() {
		if (anno == null) {
			anno = new NumericTextField<Integer>(1, 1, 9999);
			anno.setColumns(4);
			anno.setHorizontalAlignment(JTextField.CENTER);
			anno.setGroupingUsed(false);
			anno.addActionListener(e -> cambioAnno(anno.getIntValue()));
			anno.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					try {
						anno.commitEdit();
						cambioAnno(anno.getIntValue());
					} catch (ParseException e1) {
						//e1.printStackTrace();
					}
				}
			});
		}
		return anno;
	}

	private JButton getAnnoMeno() {
		if (annoMeno == null) {
			annoMeno = new JButton(getAnno().downAction());
			annoMeno.setIcon(new ImageIcon(CalendarPanel.class.getResource("left.gif")));
			annoMeno.setBorderPainted(false);
			annoMeno.setContentAreaFilled(false);
//			annoMeno.setMargin(new java.awt.Insets(0, 0, 0, 0));
			annoMeno.setBorder(BorderFactory.createEmptyBorder());
		}
		return annoMeno;
	}

	private JButton getAnnoPiu() {
		if (annoPiu == null) {
			annoPiu = new JButton(getAnno().upAction());
			annoPiu.setIcon(new ImageIcon(CalendarPanel.class.getResource("right.gif")));
			annoPiu.setBorderPainted(false);
			annoPiu.setContentAreaFilled(false);
//			annoPiu.setMargin(new java.awt.Insets(0, 0, 0, 0));
			annoPiu.setBorder(BorderFactory.createEmptyBorder());
		}
		return annoPiu;
	}
	
	private void cambioAnno(final int nuovoAnno) {
		// Gestione del 29 febbraio
		//System.out.println(nuovoAnno);
		final GregorianCalendar gc = new GregorianCalendar(loc);
		gc.set(nuovoAnno, today.get(GregorianCalendar.MONTH), 1);
		final int maxDay = gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		if (maxDay < today.get(GregorianCalendar.DAY_OF_MONTH)) {
			today.set(GregorianCalendar.DAY_OF_MONTH, maxDay);
		}
		today.set(GregorianCalendar.YEAR, nuovoAnno);
		fillDays();
		firePropertyChange(EVT_YEAR_SELECTED, null, today);
	}
}
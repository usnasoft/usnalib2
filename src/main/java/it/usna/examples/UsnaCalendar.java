package it.usna.examples;

import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.JFrame;

import it.usna.swing.UsnaSwingUtils;
import it.usna.swing.gadget.CalendarPanel;

/**
 * <p>Title: UsnaCalendar</p>
 * <p>Example of use for CalendarPanel</p>
 * <p>Copyright (c) 2006</p>
 * <p>Company: USNA</p>
 * @author - Antonio Flaccomio
 * @version 1.0
 */
public class UsnaCalendar extends JFrame {
	private static final long serialVersionUID = 1L;
	public CalendarPanel calPanel = null;

	public UsnaCalendar() throws Exception {
		UsnaSwingUtils.setLookAndFeel(UsnaSwingUtils.LF_NIMBUS);
		setIconImage(Toolkit.getDefaultToolkit().getImage(UsnaCalendar.class.getResource("/img/usna16.gif")));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(new java.awt.Dimension(220,206));
        this.setTitle("USNA Calendar");
        this.setContentPane(getCalPanel());
        setLocationRelativeTo(null);
	}

	/**
	 * This method initializes jPanel	
	 * @return javax.swing.JPanel	
	 */
	private CalendarPanel getCalPanel() {
		if (calPanel == null) {
			calPanel = new CalendarPanel(Locale.getDefault());
			calPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
		}
		return calPanel;
	}
	
	public static void main(String[] arg) throws Exception {
		UsnaCalendar cp = new UsnaCalendar();
		cp.calPanel.addPropertyChangeListener(CalendarPanel.EVT_DAY_SELECTED,
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
						System.out.println(((GregorianCalendar)propertyChangeEvent.getNewValue()).getTime());
					}
				});
		cp.setVisible(true);
	}
}

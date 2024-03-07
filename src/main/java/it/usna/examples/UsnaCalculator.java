package it.usna.examples;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.SoftBevelBorder;

import it.usna.swing.UsnaSwingUtils;
import it.usna.swing.gadget.CalculatorPanel;

/**
 * <p>Title: UsnaCalculator</p>
 * <p>Example of use for UsnaCalcPanel</p>
 * <p>Copyright (c) 2007</p>
 * <p>Company: USNA</p>
 * @author - Antonio Flaccomio
 * @version 1.0
 */
public class UsnaCalculator extends JFrame implements ClipboardOwner {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private CalculatorPanel usnaCalcPanel = null;
	private JPanel buttons = null;
	private JButton bCopy = null;
	private JButton bClose = null;

	/**
	 * This is the default constructor
	 */
	public UsnaCalculator() throws Exception {
		UsnaSwingUtils.setLookAndFeel(UsnaSwingUtils.LF_NIMBUS);
		setIconImage(Toolkit.getDefaultToolkit().getImage(UsnaCalculator.class.getResource("/img/usna16.gif")));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setContentPane(getJContentPane());
		this.setTitle("USNA Calculator");
		this.pack();
		setLocationRelativeTo(null);
	}

	/**
	 * This method initializes jContentPane
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.setBorder(BorderFactory.createEmptyBorder(4, 3, 0, 3));
			jContentPane.add(getUsnaCalcPanel(), BorderLayout.CENTER);
			jContentPane.add(getButtons(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes usnaCalcPanel	
	 * @return javax.swing.JPanel	
	 */
	private CalculatorPanel getUsnaCalcPanel() {
		if (usnaCalcPanel == null) {
			usnaCalcPanel = new CalculatorPanel();
			usnaCalcPanel.setBorder(BorderFactory.createSoftBevelBorder(SoftBevelBorder.RAISED));
		}
		return usnaCalcPanel;
	}

	/**
	 * This method initializes buttons	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtons() {
		if (buttons == null) {
			buttons = new JPanel();
			buttons.setLayout(new FlowLayout());
			buttons.add(getBCopy(), null);
			buttons.add(getBClose(), null);
		}
		return buttons;
	}

	/**
	 * This method initializes bCopy	
	 * @return javax.swing.JButton	
	 */
	private JButton getBCopy() {
		if (bCopy == null) {
			bCopy = new JButton();
			bCopy.setText("Copy");
			bCopy.setToolTipText("Copy display content into clipboard");
			bCopy.addActionListener(event -> {
				final Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
				final String cp = usnaCalcPanel.getValue().toString();
				cb.setContents(new StringSelection(cp), UsnaCalculator.this);
			});
		}
		return bCopy;
	}

	/**
	 * This method initializes bClose	
	 * @return javax.swing.JButton	
	 */
	private JButton getBClose() {
		if (bClose == null) {
			bClose = new JButton();
			bClose.setText("Close");
			bClose.addActionListener(event -> System.exit(0));
		}
		return bClose;
	}
	
	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		// Defined in ClipboardOwner interface
	}

	public static void main(String[] arg) throws Exception {
		UsnaCalculator cp = new UsnaCalculator();
		cp.setVisible(true);
	}
}
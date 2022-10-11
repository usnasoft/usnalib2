package it.usna.swing.gadget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;

/**
 * <p>
 * Title: Simple Calculator Panel
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * <p>Copyright (c) 2007
 * </p>
 * <p>
 * Company: usna
 * </p>
 * 
 * @author Antonio Flaccomio
 * @version 1.1
 */

public class CalculatorPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private final static Color NUMKEY_COLOR = Color.white;
	private final static Color UTILKEY_COLOR = Color.lightGray;
	private final static Color OPKEY_COLOR = Color.lightGray;
	private final static Color DISP_COLOR = new Color(245, 255, 234);

	private JTextField calcDisplay = null;
	private JPanel calcKeyboard = null;
	private JPanel operators = null;
	private JButton opPlus = null;
	private JButton opMinus = null;

	private final static MathContext MATH_CONT = MathContext.DECIMAL64;
	private BigDecimal acc = BigDecimal.ZERO;
	private BigDecimal mem = BigDecimal.ZERO; 
	private boolean opExecuted = true;

	private enum Oper {
		ADD, SUBTRACT, MUL, DIV, NOP
	}

	private Oper prevOp = Oper.NOP;
	private final static Pattern dispPattern = Pattern.compile("-?([1-9]\\d*(\\.\\d*)?)|(0?\\.\\d*)|0");

	private JButton bPlusMinus = null;
	private JButton opMul = null;
	private JButton opDiv = null;
	private JPanel utilityButtons = null;
	private JButton bBackSpace = null;
	private JButton bClear = null;
	private JButton opCalc = null;
	private JButton opInv = null;
	private JButton bMemStore = null;
	private JButton bMemRecall = null;
	private JButton bMemAdd = null;
	private JButton bMemSubtract = null;

	public CalculatorPanel() {
		super();
		initialize();
		calcDisplay.setText("0");
	}

	/**
	 * This method initializes this
	 * @return void
	 */
	private void initialize() {
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setVgap(6);
		borderLayout.setHgap(6);
		this.setLayout(borderLayout);
		this.setSize(200, 150);
		this.add(getCalcDisplay(), BorderLayout.NORTH);
		this.add(getCalcKeyboard(), BorderLayout.CENTER);
		this.add(getOperators(), BorderLayout.EAST);
		this.add(getUtilityButtons(), BorderLayout.SOUTH);
	}

	/**
	 * This method initializes calcDisplay
	 * @return javax.swing.JTextField
	 */
	private JTextField getCalcDisplay() {
		if (calcDisplay == null) {
			calcDisplay = new JTextField();
			calcDisplay.setEditable(false);
			calcDisplay.setBackground(DISP_COLOR);
			calcDisplay.setHorizontalAlignment(JTextField.RIGHT);
			calcDisplay.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			calcDisplay.setFont(new Font("Dialog", Font.PLAIN, 18));
		}
		return calcDisplay;
	}

	/**
	 * This method initializes calcKeyboard
	 * @return javax.swing.JPanel
	 */
	private JPanel getCalcKeyboard() {
		if (calcKeyboard == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(4);
			gridLayout.setVgap(3);
			gridLayout.setHgap(3);
			gridLayout.setColumns(3);
			calcKeyboard = new JPanel();
			calcKeyboard.setLayout(gridLayout);
			calcKeyboard.add(getNumberButton('7', KeyEvent.VK_NUMPAD7), null);
			calcKeyboard.add(getNumberButton('8', KeyEvent.VK_NUMPAD8), null);
			calcKeyboard.add(getNumberButton('9', KeyEvent.VK_NUMPAD9), null);
			calcKeyboard.add(getNumberButton('4', KeyEvent.VK_NUMPAD4), null);
			calcKeyboard.add(getNumberButton('5', KeyEvent.VK_NUMPAD5), null);
			calcKeyboard.add(getNumberButton('6', KeyEvent.VK_NUMPAD6), null);
			calcKeyboard.add(getNumberButton('1', KeyEvent.VK_NUMPAD1), null);
			calcKeyboard.add(getNumberButton('2', KeyEvent.VK_NUMPAD2), null);
			calcKeyboard.add(getNumberButton('3', KeyEvent.VK_NUMPAD3), null);
			calcKeyboard.add(getNumberButton('0', KeyEvent.VK_NUMPAD0), null);
			calcKeyboard.add(getNumberButton('.', KeyEvent.VK_DECIMAL), null);
			calcKeyboard.add(getBPlusMinus(), null);
		}
		return calcKeyboard;
	}

	private JButton getNumberButton(final char numb, final int keyCode) {
		final JButton b = new JButton(numb + "");
		b.setMargin(new Insets(1, 0, 1, 0));
		b.setBackground(NUMKEY_COLOR);
		final Action bAction = new AbstractAction("usnacalc_" + numb) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (opExecuted) {
					calcDisplay.setText(numb == '.' ? "0." : numb + "");
					opExecuted = false;
				} else {
					final String res = calcDisplay.getText() + numb;
					if (dispPattern.matcher(res).matches()) {
						calcDisplay.setText(res);
						opExecuted = false;
					}
				}
			}
		};
		b.addActionListener(bAction);
		final InputMap imap = b.getInputMap(WHEN_IN_FOCUSED_WINDOW);
		final ActionMap amap = b.getActionMap();
		final KeyStroke ks1 = KeyStroke.getKeyStroke(keyCode, 0);
		final KeyStroke ks2 = KeyStroke.getKeyStroke(numb, 0);
		imap.put(ks1, bAction.getValue(Action.NAME));
		imap.put(ks2, bAction.getValue(Action.NAME));
		amap.put(bAction.getValue(Action.NAME), bAction);
		return b;
	}

	/**
	 * This method initializes operators
	 * @return javax.swing.JPanel
	 */
	private JPanel getOperators() {
		if (operators == null) {
			GridLayout gridLayout1 = new GridLayout();
			gridLayout1.setVgap(3);
			gridLayout1.setRows(4);
			gridLayout1.setHgap(3);
			gridLayout1.setColumns(2);
			operators = new JPanel();
			// operators.setPreferredSize(new Dimension(68, 27));
			operators.setLayout(gridLayout1);
			operators.add(getOpPlus(), null);
			operators.add(getBMemStore(), null);
			operators.add(getOpMinus(), null);
			operators.add(getBMemRecall(), null);
			operators.add(getOpMul(), null);
			operators.add(getBMemAdd(), null);
			operators.add(getOpDiv(), null);
			operators.add(getBMemSubtract(), null);
		}
		return operators;
	}

	/**
	 * This method initializes opPlus
	 * @return javax.swing.JButton
	 */
	private JButton getOpPlus() {
		if (opPlus == null) {
			opPlus = new JButton();
			opPlus.setMargin(new Insets(0, 0, 0, 0));
			opPlus.setText("+");
			opPlus.setBackground(OPKEY_COLOR);
			opPlus.setFont(new Font("Dialog", Font.BOLD, 14));

			final Action bAction = new AbstractAction("usnacalc_plus") {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					executeOp();
					prevOp = Oper.ADD;
				}
			};
			opPlus.addActionListener(bAction);
			final InputMap imap = opPlus.getInputMap(WHEN_IN_FOCUSED_WINDOW);
			final ActionMap amap = opPlus.getActionMap();
			final KeyStroke ks1 = KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0);
			final KeyStroke ks2 = KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0);
			imap.put(ks1, "usnacalc_plus");
			imap.put(ks2, "usnacalc_plus");
			amap.put("usnacalc_plus", bAction);
		}
		return opPlus;
	}

	/**
	 * This method initializes opMinus
	 * @return javax.swing.JButton
	 */
	private JButton getOpMinus() {
		if (opMinus == null) {
			opMinus = new JButton();
			opMinus.setMargin(new Insets(1, 1, 1, 1));
			opMinus.setText("-");
			opMinus.setBackground(OPKEY_COLOR);
			opMinus.setFont(new Font("Dialog", Font.BOLD, 14));

			final Action bAction = new AbstractAction("usnacalc_minus") {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					executeOp();
					prevOp = Oper.SUBTRACT;
				}
			};
			opMinus.addActionListener(bAction);
			final InputMap imap = opMinus.getInputMap(WHEN_IN_FOCUSED_WINDOW);
			final ActionMap amap = opMinus.getActionMap();
			final KeyStroke ks1 = KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0);
			final KeyStroke ks2 = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0);
			imap.put(ks1, "usnacalc_minus");
			imap.put(ks2, "usnacalc_minus");
			amap.put("usnacalc_minus", bAction);
		}
		return opMinus;
	}

	/**
	 * This method initializes bPlusMinus
	 * @return javax.swing.JButton
	 */
	private JButton getBPlusMinus() {
		if (bPlusMinus == null) {
			bPlusMinus = new JButton();
			bPlusMinus.setMargin(new Insets(1, 0, 1, 0));
			bPlusMinus.setText("+/-");
			bPlusMinus.setBackground(NUMKEY_COLOR);

			final Action bAction = new AbstractAction("usnacalc_opp") {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					if (opExecuted) {
						acc = acc.negate();
						displayPut(acc);
					} else {
						final BigDecimal disp = new BigDecimal(calcDisplay.getText(), MATH_CONT);
						displayPut(disp.negate());
					}
				}
			};
			bPlusMinus.addActionListener(bAction);
			final InputMap imap = bPlusMinus.getInputMap(WHEN_IN_FOCUSED_WINDOW);
			final ActionMap amap = bPlusMinus.getActionMap();
			final KeyStroke ks1 = KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, InputEvent.SHIFT_MASK);
			imap.put(ks1, "usnacalc_opp");
			amap.put("usnacalc_opp", bAction);
		}
		return bPlusMinus;
	}

	/**
	 * This method initializes opMul
	 * @return javax.swing.JButton
	 */
	private JButton getOpMul() {
		if (opMul == null) {
			opMul = new JButton();
			opMul.setMargin(new Insets(0, 0, 0, 0));
			opMul.setText("*");
			opMul.setBackground(OPKEY_COLOR);
			opMul.setFont(new Font("Dialog", Font.BOLD, 14));

			final Action bAction = new AbstractAction("usnacalc_mul") {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					executeOp();
					prevOp = Oper.MUL;
				}
			};
			opMul.addActionListener(bAction);
			final InputMap imap = opMul.getInputMap(WHEN_IN_FOCUSED_WINDOW);
			final ActionMap amap = opMul.getActionMap();
			final KeyStroke ks1 = KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, 0);
			final KeyStroke ks2 = KeyStroke.getKeyStroke(KeyEvent.VK_ASTERISK, 0);
			imap.put(ks1, "usnacalc_mul");
			imap.put(ks2, "usnacalc_mul");
			amap.put("usnacalc_mul", bAction);
		}
		return opMul;
	}

	/**
	 * This method initializes opDiv
	 * @return javax.swing.JButton
	 */
	private JButton getOpDiv() {
		if (opDiv == null) {
			opDiv = new JButton();
			opDiv.setMargin(new Insets(0, 0, 0, 0));
			opDiv.setText("/");
			opDiv.setBackground(OPKEY_COLOR);
			opDiv.setFont(new Font("Dialog", Font.BOLD, 14));

			final Action bAction = new AbstractAction("usnacalc_div") {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					executeOp();
					prevOp = Oper.DIV;
				}
			};
			opDiv.addActionListener(bAction);
			final InputMap imap = opDiv.getInputMap(WHEN_IN_FOCUSED_WINDOW);
			final ActionMap amap = opDiv.getActionMap();
			final KeyStroke ks1 = KeyStroke.getKeyStroke(KeyEvent.VK_DIVIDE, 0);
			final KeyStroke ks2 = KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0);
			imap.put(ks1, "usnacalc_div");
			imap.put(ks2, "usnacalc_div");
			amap.put("usnacalc_div", bAction);
		}
		return opDiv;
	}

	private void executeOp() {
		if (!opExecuted) {
			try {
				final BigDecimal disp = new BigDecimal(calcDisplay.getText(), MATH_CONT);
				switch (prevOp) {
				case ADD:
					acc = acc.add(disp, MATH_CONT);
					break;
				case SUBTRACT:
					acc = acc.subtract(disp, MATH_CONT);
					break;
				case MUL:
					acc = acc.multiply(disp, MATH_CONT);
					break;
				case DIV:
					acc = acc.divide(disp, MATH_CONT);
					break;
				default:
					acc = disp;
					break;
				}
				displayPut(acc);
				opExecuted = true;
			} catch (ArithmeticException e) {
				JOptionPane.showMessageDialog(this, "Arithmetic error.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * This method initializes utilityButtons
	 * @return javax.swing.JPanel
	 */
	private JPanel getUtilityButtons() {
		if (utilityButtons == null) {
			GridLayout gridLayout3 = new GridLayout();
			gridLayout3.setRows(1);
			gridLayout3.setHgap(3);
			utilityButtons = new JPanel();
			//utilityButtons.setPreferredSize(new Dimension(113, 18));
			utilityButtons.setLayout(gridLayout3);
			utilityButtons.add(getBBackSpace(), null);
			utilityButtons.add(getBClear(), null);
			utilityButtons.add(getOpInv(), null);
			utilityButtons.add(getOpCalc(), null);
		}
		return utilityButtons;
	}

	/**
	 * This method initializes bBackSpace
	 * @return javax.swing.JButton
	 */
	private JButton getBBackSpace() {
		if (bBackSpace == null) {
			bBackSpace = new JButton();
			bBackSpace.setText("Del");
			bBackSpace.setBackground(UTILKEY_COLOR);
			bBackSpace.setMargin(new Insets(1, 0, 1, 0));

			final Action bAction = new AbstractAction("usnacalc_back") {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					if (!opExecuted) {
						final String disp = calcDisplay.getText();
						final int len = disp.length();
						if (len == 1 || (len == 2 && disp.charAt(0) == '-')) {
							calcDisplay.setText("0");
							opExecuted = true;
						} else {
							calcDisplay.setText(disp.substring(0, disp.length() - 1));
						}
					}
				}
			};
			bBackSpace.addActionListener(bAction);
			final InputMap imap = bBackSpace.getInputMap(WHEN_IN_FOCUSED_WINDOW);
			final ActionMap amap = bBackSpace.getActionMap();
			final KeyStroke ks1 = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0);
			final KeyStroke ks2 = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
			imap.put(ks1, "usnacalc_back");
			imap.put(ks2, "usnacalc_back");
			amap.put("usnacalc_back", bAction);
		}
		return bBackSpace;
	}

	/**
	 * This method initializes bClear
	 * @return javax.swing.JButton
	 */
	private JButton getBClear() {
		if (bClear == null) {
			bClear = new JButton();
			bClear.setText("Clr");
			bClear.setToolTipText("Clear display");
			bClear.setBackground(UTILKEY_COLOR);
			bClear.setMargin(new Insets(1, 0, 1, 0));

			final Action bAction = new AbstractAction("usnacalc_clr") {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					acc = BigDecimal.ZERO;
					calcDisplay.setText("0");
					opExecuted = true;
					prevOp = Oper.NOP;
				}
			};
			bClear.addActionListener(bAction);
			final InputMap imap = bClear.getInputMap(WHEN_IN_FOCUSED_WINDOW);
			final ActionMap amap = bClear.getActionMap();
			final KeyStroke ks1 = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
			imap.put(ks1, "usnacalc_clr");
			amap.put("usnacalc_clr", bAction);
		}
		return bClear;
	}

	/**
	 * This method initializes opCalc
	 * @return javax.swing.JButton
	 */
	private JButton getOpCalc() {
		if (opCalc == null) {
			opCalc = new JButton();
			opCalc.setText("=");
			opCalc.setBackground(UTILKEY_COLOR);
			opCalc.setFont(new Font("Dialog", Font.BOLD, 14));
			opCalc.setMargin(new Insets(1, 0, 1, 0));

			final Action bAction = new AbstractAction("usnacalc_calc") {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					executeOp();
					prevOp = Oper.NOP;
				}
			};
			opCalc.addActionListener(bAction);
			final InputMap imap = opCalc.getInputMap(WHEN_IN_FOCUSED_WINDOW);
			final ActionMap amap = opCalc.getActionMap();
			final KeyStroke ks1 = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
			imap.put(ks1, "usnacalc_calc");
			amap.put("usnacalc_calc", bAction);
		}
		return opCalc;
	}

	/**
	 * This method initializes opInv
	 * @return javax.swing.JButton
	 */
	private JButton getOpInv() {
		if (opInv == null) {
			opInv = new JButton();
			opInv.setText("1/x");
			opInv.setToolTipText("Inversion");
			opInv.setBackground(UTILKEY_COLOR);
			opInv.setMargin(new Insets(1, 0, 1, 0));

			final Action bAction = new AbstractAction("usnacalc_inv") {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent ev) {
					try {
						if (opExecuted) {
							acc = BigDecimal.ONE.divide(acc, MATH_CONT);
							displayPut(acc);
						} else {
							final BigDecimal disp = new BigDecimal(calcDisplay.getText(), MATH_CONT);
							displayPut(BigDecimal.ONE.divide(disp, MATH_CONT));
						}
					} catch (ArithmeticException e) {
						JOptionPane.showMessageDialog(CalculatorPanel.this, "Arithmetic error.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			};
			opInv.addActionListener(bAction);
			final InputMap imap = opInv.getInputMap(WHEN_IN_FOCUSED_WINDOW);
			final ActionMap amap = opInv.getActionMap();
			final KeyStroke ks1 = KeyStroke.getKeyStroke(KeyEvent.VK_DIVIDE, InputEvent.SHIFT_MASK);
			imap.put(ks1, "usnacalc_inv");
			amap.put("usnacalc_inv", bAction);
		}
		return opInv;
	}

	/**
	 * This method initializes bMemStore
	 * @return javax.swing.JButton
	 */
	private JButton getBMemStore() {
		if (bMemStore == null) {
			bMemStore = new JButton();
			bMemStore.setMargin(new Insets(0, 0, 0, 0));
			bMemStore.setText("MS");
			bMemStore.setToolTipText("Store value in memory");
			bMemStore.setBackground(OPKEY_COLOR);
			bMemStore.addActionListener(event -> {
				mem = new BigDecimal(calcDisplay.getText());
			});
		}
		return bMemStore;
	}

	/**
	 * This method initializes bMemRecall
	 * @return javax.swing.JButton
	 */
	private JButton getBMemRecall() {
		if (bMemRecall == null) {
			bMemRecall = new JButton();
			bMemRecall.setMargin(new Insets(0, 0, 0, 0));
			bMemRecall.setText("MR");
			bMemRecall.setToolTipText("Recall value from memory");
			bMemRecall.setBackground(OPKEY_COLOR);
			bMemRecall.addActionListener(event -> {
				displayPut(mem);
				opExecuted = false;
			});
		}
		return bMemRecall;
	}

	/**
	 * This method initializes bMemAdd
	 * @return javax.swing.JButton
	 */
	private JButton getBMemAdd() {
		if (bMemAdd == null) {
			bMemAdd = new JButton();
			bMemAdd.setMargin(new Insets(0, 0, 0, 0));
			bMemAdd.setText("M+");
			bMemAdd.setToolTipText("Add value to memory");
			bMemAdd.setBackground(OPKEY_COLOR);
			bMemAdd.addActionListener(event -> {
				mem = mem.add(new BigDecimal(calcDisplay.getText(), MATH_CONT));
			});
		}
		return bMemAdd;
	}

	/**
	 * This method initializes bMemSubtract
	 * @return javax.swing.JButton
	 */
	private JButton getBMemSubtract() {
		if (bMemSubtract == null) {
			bMemSubtract = new JButton();
			bMemSubtract.setMargin(new Insets(0, 0, 0, 0));
			bMemSubtract.setText("M-");
			bMemSubtract.setToolTipText("Subtract value from memory");
			bMemSubtract.setBackground(OPKEY_COLOR);
			bMemSubtract.addActionListener(event -> {
				mem = mem.subtract(new BigDecimal(calcDisplay.getText(), MATH_CONT));
			});
		}
		return bMemSubtract;
	}

	private void displayPut(final BigDecimal val) {
		String valStr = val.toString();
		if (valStr.indexOf('.') >= 0) {
			int zeropos = valStr.length() - 1;
			while (valStr.charAt(zeropos) == '0') {
				zeropos--;
			}
			if (valStr.charAt(zeropos) == '.') {
				zeropos--;
			}
			valStr = valStr.substring(0, zeropos + 1);
		}
		calcDisplay.setText(valStr);
	}

	public BigDecimal getValue() {
		return new BigDecimal(calcDisplay.getText());
	}
}
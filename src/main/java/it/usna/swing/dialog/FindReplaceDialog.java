package it.usna.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * <p>Title: DialogFind</p>
 * <p>Generic find/replace dialog; replace currently not working for jTextPane</p>
 * <p>Company: USNA</p>
 * @author Antonio Flaccomio
 * @version 2.0
 */
public class FindReplaceDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel jButtonsPanel = null;
	private JButton jButtonFind = null;
	private JPanel jPanel = null;
	private JLabel jLabel = null;
	private JTextField jTextFind = null;
	private JCheckBox jCheckCase = null;
	private JRadioButton jRadioBackward = null;
	private JPanel jOptionsPanel = null;
	private JRadioButton jRadioForward = null;
	private JRadioButton jRadioFromStart = null;
	private JRadioButton jRadioFromEnd = null;
	private JButton jButtonClose = null;
	private JLabel jLabel1 = null;
	private JTextField jTextReplace = null;
	private JButton jButtonReplace = null;
	private JButton jButtonReplaceAll = null;
	private Supplier<JTextComponent> textComp;

	private ResourceBundle labels;

	public FindReplaceDialog(final Window owner, final Supplier<JTextComponent> textComp, final boolean replace, final ResourceBundle labels) {
		super(owner);
		this.textComp = textComp;
		this.labels = labels;
		initialize();
		enableReplace(replace);
	}

	public FindReplaceDialog(final Window owner, final Supplier<JTextComponent> textComp, final boolean replace) {
		super(owner);
		this.textComp = textComp;
		this.labels = ResourceBundle.getBundle("it.usna.swing.dialog.LabelsFindBundle");
		initialize();
		enableReplace(replace);
	}

	/**
	 * @wbp.parser.constructor
	 */
	public FindReplaceDialog(final Window owner, final JTextComponent textComponent, final boolean replace) {
		this(owner, () -> textComponent, replace);
	}

//	@Override
//	public void setVisible(final boolean visible) {
//		super.setVisible(visible);
//		jButtonReplace.setEnabled(false);
//	}

	/**
	 * This method initializes this
	 * @return void
	 */
	private void initialize() {
		this.setMinimumSize(new Dimension(280, 10));
		this.setContentPane(getJContentPane());
		ButtonGroup group = new ButtonGroup();
		group.add(jRadioForward);
		group.add(jRadioBackward);
		jButtonReplace.setEnabled(false);
		getRootPane().setDefaultButton(jButtonFind);
	}

	public void enableReplace(final boolean replace) {
		jLabel1.setVisible(replace);
		jTextReplace.setVisible(replace);
		jButtonReplace.setVisible(replace);
		jButtonReplaceAll.setVisible(replace);
		this.setTitle(replace ? labels.getString("titleFindReplace") : labels.getString("titleFind"));
		pack();
	}

	/**
	 * This method initializes jContentPane
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			BorderLayout borderLayout = new BorderLayout();
			borderLayout.setVgap(4);
			jContentPane = new JPanel();
			jContentPane.setBorder(BorderFactory.createEmptyBorder(2, 4, 0, 4));
			jContentPane.setLayout(borderLayout);
			jContentPane.add(getJButtonsPanel(), BorderLayout.SOUTH);
			jContentPane.add(getJPanel(), BorderLayout.NORTH);
			jContentPane.add(getJOptionsPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanelButtons	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJButtonsPanel() {
		if (jButtonsPanel == null) {
			jButtonsPanel = new JPanel();
			jButtonsPanel.setLayout(new FlowLayout());
			jButtonsPanel.add(getJButtonFind(), null);
			jButtonsPanel.add(getJButtonReplace(), null);
			jButtonsPanel.add(getJButtonReplaceAll(), null);
			jButtonsPanel.add(getJButtonClose(), null);
		}
		return jButtonsPanel;
	}

	/**
	 * This method initializes jButtonFind	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonFind() {
		if (jButtonFind == null) {
			jButtonFind = new JButton(labels.getString("btnFind"));
			//jButtonFind.setMargin(new Insets(2, 8, 2, 8));
			getRootPane().setDefaultButton(jButtonFind);
			jButtonFind.addActionListener(event -> doFind());
		}
		return jButtonFind;
	}

	/**
	 * This method initializes jPanel	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			GridBagLayout gbl_jPanel = new GridBagLayout();
			gbl_jPanel.columnWidths = new int[] {65, 222};
			gbl_jPanel.rowHeights = new int[]{20, 20, 23, 0};
			gbl_jPanel.columnWeights = new double[]{0.0, 0.0};
			gbl_jPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
			jPanel.setLayout(gbl_jPanel);
			jLabel = new JLabel();
			jLabel.setText(labels.getString("lbl_find"));
			jLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			GridBagConstraints gbc_jLabel = new GridBagConstraints();
			gbc_jLabel.anchor = GridBagConstraints.WEST;
			gbc_jLabel.insets = new Insets(0, 0, 5, 5);
			gbc_jLabel.gridx = 0;
			gbc_jLabel.gridy = 0;
			jPanel.add(jLabel, gbc_jLabel);
			GridBagConstraints gbc_jTextFind = new GridBagConstraints();
			gbc_jTextFind.weightx = 1.0;
			gbc_jTextFind.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextFind.insets = new Insets(0, 0, 5, 0);
			gbc_jTextFind.gridx = 1;
			gbc_jTextFind.gridy = 0;
			jPanel.add(getJTextFind(), gbc_jTextFind);
			jLabel1 = new JLabel();
			jLabel1.setText("Replace with:");
			GridBagConstraints gbc_jLabel1 = new GridBagConstraints();
			gbc_jLabel1.anchor = GridBagConstraints.WEST;
			gbc_jLabel1.insets = new Insets(0, 0, 5, 5);
			gbc_jLabel1.gridx = 0;
			gbc_jLabel1.gridy = 1;
			jPanel.add(jLabel1, gbc_jLabel1);
			GridBagConstraints gbc_jTextReplace = new GridBagConstraints();
			gbc_jTextReplace.weightx = 1.0;
			gbc_jTextReplace.fill = GridBagConstraints.HORIZONTAL;
			gbc_jTextReplace.insets = new Insets(0, 0, 5, 0);
			gbc_jTextReplace.gridx = 1;
			gbc_jTextReplace.gridy = 1;
			jPanel.add(getJTextReplace(), gbc_jTextReplace);
			GridBagConstraints gbc_jCheckCase = new GridBagConstraints();
			gbc_jCheckCase.fill = GridBagConstraints.HORIZONTAL;
			gbc_jCheckCase.anchor = GridBagConstraints.WEST;
			gbc_jCheckCase.gridwidth = 2;
			gbc_jCheckCase.gridx = 0;
			gbc_jCheckCase.gridy = 2;
			jPanel.add(getJCheckCase(), gbc_jCheckCase);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextFind	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextFind() {
		if (jTextFind == null) {
			jTextFind = new JTextField(25);
			jTextFind.setFocusAccelerator('T');
		}
		return jTextFind;
	}

	/**
	 * This method initializes jCheckCase	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckCase() {
		if (jCheckCase == null) {
			jCheckCase = new JCheckBox();
			jCheckCase.setText(labels.getString("lbl_case"));
			jCheckCase.setMnemonic(KeyEvent.VK_C);
		}
		return jCheckCase;
	}

	/**
	 * This method initializes jRadioBackward	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioBackward() {
		if (jRadioBackward == null) {
			jRadioBackward = new JRadioButton();
			jRadioBackward.setText(labels.getString("lbl_backward"));
			jRadioBackward.setMnemonic(KeyEvent.VK_B);
			jRadioBackward.addItemListener(event -> {
				if(event.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
					getJRadioForward().setSelected(false);
					getJRadioFromStart().setSelected(false);
				}
			});
		}
		return jRadioBackward;
	}

	/**
	 * This method initializes jPanel1	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJOptionsPanel() {
		if (jOptionsPanel == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.anchor = GridBagConstraints.WEST;
			gridBagConstraints6.insets = new Insets(0, 0, 0, 12);
			gridBagConstraints6.gridy = 0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints5.weighty = 10.0D;
			gridBagConstraints5.insets = new Insets(0, 0, 0, 12);
			gridBagConstraints5.gridy = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 1;
			gridBagConstraints4.anchor = GridBagConstraints.WEST;
			gridBagConstraints4.insets = new Insets(0, 12, 0, 0);
			gridBagConstraints4.gridy = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints3.weighty = 10.0D;
			gridBagConstraints3.insets = new Insets(0, 12, 0, 0);
			gridBagConstraints3.gridy = 1;
			jOptionsPanel = new JPanel();
			jOptionsPanel.setLayout(new GridBagLayout());
			jOptionsPanel.setBorder(BorderFactory.createLineBorder(SystemColor.controlShadow, 1));
			jOptionsPanel.add(getJRadioBackward(), gridBagConstraints3);
			jOptionsPanel.add(getJRadioForward(), gridBagConstraints4);
			jOptionsPanel.add(getJRadioFromStart(), gridBagConstraints6);
			jOptionsPanel.add(getJRadioFromEnd(), gridBagConstraints5);
		}
		return jOptionsPanel;
	}

	/**
	 * This method initializes jRadioForward	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioForward() {
		if (jRadioForward == null) {
			jRadioForward = new JRadioButton();
			jRadioForward.setText(labels.getString("lbl_forward"));
			jRadioForward.setMnemonic(KeyEvent.VK_F);
			jRadioForward.setSelected(true);
			jRadioForward.addItemListener(event -> {
				if(event.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
					getJRadioBackward().setSelected(false);
					getJRadioFromEnd().setSelected(false);
				}
			});
		}
		return jRadioForward;
	}

	/**
	 * This method initializes jRadioFromStart	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioFromStart() {
		if (jRadioFromStart == null) {
			jRadioFromStart = new JRadioButton();
			jRadioFromStart.setText(labels.getString("lbl_fromStart"));
			jRadioFromStart.setMnemonic(KeyEvent.VK_S);
			jRadioFromStart.addItemListener(event -> {
				if(event.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
					getJRadioForward().setSelected(true);
				}
			});
		}
		return jRadioFromStart;
	}

	/**
	 * This method initializes jRadioFromEnd	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioFromEnd() {
		if (jRadioFromEnd == null) {
			jRadioFromEnd = new JRadioButton();
			jRadioFromEnd.setText(labels.getString("lbl_fromEnd"));
			jRadioFromEnd.setMnemonic(KeyEvent.VK_E);
			jRadioFromEnd.addItemListener(event -> {
				if(event.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
					getJRadioBackward().setSelected(true);
				}
			});
		}
		return jRadioFromEnd;
	}

	/**
	 * This method initializes jButtonClose	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton(labels.getString("dlgClose"));
			//jButtonClose.setMargin(new Insets(2, 8, 2, 8));
			jButtonClose.addActionListener(event -> dispose());
		}
		return jButtonClose;
	}

	/**
	 * This method initializes jTextReplace	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextReplace() {
		if (jTextReplace == null) {
			jTextReplace = new JTextField(25);
			jTextReplace.setFocusAccelerator('R');
		}
		return jTextReplace;
	}

	/**
	 * This method initializes jButtonReplace	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonReplace() {
		if (jButtonReplace == null) {
			jButtonReplace = new JButton();
			jButtonReplace.setText(labels.getString("lbl_replace"));
			//jButtonReplace.setMargin(new Insets(2, 8, 2, 8));
			jButtonReplace.addActionListener(event -> doReplace());
		}
		return jButtonReplace;
	}

	/**
	 * This method initializes jButtonReplaceAll	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonReplaceAll() {
		if (jButtonReplaceAll == null) {
			jButtonReplaceAll = new JButton();
			jButtonReplaceAll.setText(labels.getString("lbl_replaceAll"));
			//jButtonReplaceAll.setMargin(new Insets(2, 8, 2, 8));
			jButtonReplaceAll.addActionListener(event -> doReplaceAll());
		}
		return jButtonReplaceAll;
	}

	protected void doFind() {
		try {
			final JTextComponent textComponent = textComp.get();
			Document doc = textComponent.getDocument();
			String text = doc.getText(0, doc.getLength());
			String toFind = jTextFind.getText();
			if(toFind.length() > 0 && text.length() > 0) {
				final boolean forward = jRadioForward.getModel().isSelected();
				int startPos;
				if(jRadioFromStart.getModel().isSelected()) {
					startPos = 0;
					jRadioFromStart.setSelected(false);
				} else if(jRadioFromEnd.getModel().isSelected()) {
					startPos = text.length();
					jRadioFromEnd.setSelected(false);
				} else {
					if(forward) {
						startPos = textComponent.getSelectionEnd(); // Return 0 if the document is empty, or the value of dot (caret) if there is no selection
					} else { // backward
						startPos = textComponent.getSelectionStart() - 1;
					}
				}
				if(find(textComponent, startPos, forward) < 0) { // wrap
					startPos = forward ? 0 : text.length();
					if(find(textComponent, startPos, forward) < 0) {
						JOptionPane.showMessageDialog(FindReplaceDialog.this, labels.getString("msg_text_not_found"), labels.getString("titleFind"), JOptionPane.INFORMATION_MESSAGE);
						jButtonReplace.setEnabled(false);
					}
				}
			} else {
				jButtonReplace.setEnabled(false);
			}
		} catch (BadLocationException e) {
			jButtonReplace.setEnabled(false);
		}
	}

	private int find(final JTextComponent textComponent, final int startPos, final boolean forward) {
		try {
			Document doc = textComponent.getDocument();
			String text = doc.getText(0, doc.getLength());
			String toFind = jTextFind.getText();
			if(!jCheckCase.getModel().isSelected()) {
				toFind = toFind.toLowerCase();
				text = text.toLowerCase();
			}
			final int ind;
			if(forward) {
				ind = text.indexOf(toFind, startPos);
			} else  {
				ind = text.lastIndexOf(toFind, startPos);
			}
			if(ind > 0) {
				textComponent.setCaretPosition(ind);
				textComponent.moveCaretPosition(ind + toFind.length());
				textComponent.requestFocus();
				jButtonReplace.setEnabled(true);
			}
			return ind;
		} catch (BadLocationException e) {
			return -1;
		}
	}

	protected void doReplace() {
		textComp.get().replaceSelection(jTextReplace.getText());
		jButtonReplace.setEnabled(false);
	}

	protected void doReplaceAll() {
		final String findText = jTextFind.getText();
		if(findText.length() > 0) {
			textComp.get().setText(textComp.get().getText().replace(findText, jTextReplace.getText()));
			jButtonReplace.setEnabled(false);
		}
	}
}
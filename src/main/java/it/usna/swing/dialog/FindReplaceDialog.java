package it.usna.swing.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * <p>Title: DialogFind</p>
 * <p>Generic find/replace dialog; replace currently not working for jTextPane</p>
 * <p>Company: USNA</p>
 * @author Antonio Flaccomio
 * @version 2.1
 */
public class FindReplaceDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTextField jTextFind = null;
	private JCheckBox jCheckCase = null;
	private JRadioButton jRadioBackward = null;
	private JRadioButton jRadioForward = null;
	private JRadioButton jRadioFromStart = null;
	private JRadioButton jRadioFromEnd = null;
	private JLabel replaceWithLabel = null;
	private JTextField jTextReplace = null;
	private JButton jButtonReplace = null;
	private JButton jButtonReplaceAll = null;
	private Supplier<JTextComponent> textComp;
//	private boolean keepFocus = false; 
	private ResourceBundle labels;

	public FindReplaceDialog(final Window owner, final Supplier<JTextComponent> textComp, final boolean replace, final ResourceBundle labels) {
		super(owner);
		this.textComp = textComp;
		this.labels = labels;
		initialize();
		enableReplace(replace);
		pack();
	}

	public FindReplaceDialog(final Window owner, final Supplier<JTextComponent> textComp, final boolean replace) {
		super(owner);
		this.textComp = textComp;
		this.labels = ResourceBundle.getBundle("LabelsFindBundle");
		initialize();
		enableReplace(replace);
		pack();
	}

	/**
	 * @wbp.parser.constructor
	 */
	public FindReplaceDialog(final Window owner, final JTextComponent textComponent, final boolean replace) {
		this(owner, () -> textComponent, replace);
	}
	
//	public void setKeepFocus(boolean keep) {
//		keepFocus = keep;
//	}

	private void initialize() {
//		this.setMinimumSize(new Dimension(280, 10));
		JPanel jContentPane = new JPanel(new BorderLayout(0, 4));
		jContentPane.setBorder(BorderFactory.createEmptyBorder(2, 4, 0, 4));
		jContentPane.add(getJButtonsPanel(), BorderLayout.SOUTH);
		jContentPane.add(getMainPanel(), BorderLayout.NORTH);
		jContentPane.add(getJOptionsPanel(), BorderLayout.CENTER);
		
		this.setContentPane(jContentPane);
		ButtonGroup group = new ButtonGroup();
		group.add(jRadioForward);
		group.add(jRadioBackward);
		jButtonReplace.setEnabled(false);
		
		jContentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape_close");
		jContentPane.getActionMap().put("escape_close", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

	public void enableReplace(final boolean replace) {
		replaceWithLabel.setVisible(replace);
		jTextReplace.setVisible(replace);
		jButtonReplace.setVisible(replace);
		jButtonReplaceAll.setVisible(replace);
		this.setTitle(replace ? labels.getString("titleFindReplace") : labels.getString("titleFind"));
	}

	private JPanel getJButtonsPanel() {
		JPanel jButtonsPanel = new JPanel();
		jButtonsPanel.setLayout(new FlowLayout());

		JButton jButtonFind = new JButton(labels.getString("btnFind"));
		jButtonFind.addActionListener(event -> doFind());
		jButtonsPanel.add(jButtonFind, null);

		jButtonReplace = new JButton(labels.getString("lbl_replace"));
		jButtonReplace.addActionListener(event -> doReplace());
		jButtonsPanel.add(jButtonReplace, null);

		jButtonReplaceAll = new JButton(labels.getString("lbl_replaceAll"));
		jButtonReplaceAll.addActionListener(event -> doReplaceAll());
		jButtonsPanel.add(jButtonReplaceAll, null);

		JButton jButtonClose = new JButton(labels.getString("dlgClose"));
		jButtonClose.addActionListener(event -> dispose());
		jButtonsPanel.add(jButtonClose, null);

		getRootPane().setDefaultButton(jButtonFind);
		return jButtonsPanel;
	}

	private JPanel getMainPanel() {
		JPanel jPanel = new JPanel();
		GridBagLayout gbl_jPanel = new GridBagLayout();
		jPanel.setLayout(gbl_jPanel);
		JLabel jLabel = new JLabel(labels.getString("lbl_find"));
		jLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
		GridBagConstraints gbc_jLabel = new GridBagConstraints();
		gbc_jLabel.anchor = GridBagConstraints.WEST;
		gbc_jLabel.insets = new Insets(2, 0, 2, 3);
		gbc_jLabel.gridx = 0;
		gbc_jLabel.gridy = 0;
		jPanel.add(jLabel, gbc_jLabel);
		GridBagConstraints gbc_jTextFind = new GridBagConstraints();
		gbc_jTextFind.weightx = 1.0;
		gbc_jTextFind.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextFind.insets = new Insets(2, 0, 2, 0);
		gbc_jTextFind.gridx = 1;
		gbc_jTextFind.gridy = 0;
		jPanel.add(getJTextFind(), gbc_jTextFind);
		replaceWithLabel = new JLabel(labels.getString("lbl_replaceWith"));
		GridBagConstraints gbc_jLabel1 = new GridBagConstraints();
		gbc_jLabel1.anchor = GridBagConstraints.WEST;
		gbc_jLabel1.insets = new Insets(0, 0, 3, 3);
		gbc_jLabel1.gridx = 0;
		gbc_jLabel1.gridy = 1;
		jPanel.add(replaceWithLabel, gbc_jLabel1);
		GridBagConstraints gbc_jTextReplace = new GridBagConstraints();
		gbc_jTextReplace.weightx = 1.0;
		gbc_jTextReplace.fill = GridBagConstraints.HORIZONTAL;
		gbc_jTextReplace.insets = new Insets(0, 0, 3, 0);
		gbc_jTextReplace.gridx = 1;
		gbc_jTextReplace.gridy = 1;
		jPanel.add(getJTextReplace(), gbc_jTextReplace);
		GridBagConstraints gbc_jCheckCase = new GridBagConstraints();
		gbc_jCheckCase.insets = new Insets(2, 0, 0, 0);
		gbc_jCheckCase.fill = GridBagConstraints.HORIZONTAL;
		gbc_jCheckCase.anchor = GridBagConstraints.WEST;
		gbc_jCheckCase.gridwidth = 2;
		gbc_jCheckCase.gridx = 0;
		gbc_jCheckCase.gridy = 2;
		jPanel.add(getJCheckCase(), gbc_jCheckCase);
		return jPanel;
	}

	private JTextField getJTextFind() {
		if (jTextFind == null) {
			jTextFind = new JTextField(25);
			jTextFind.setFocusAccelerator('T');
		}
		return jTextFind;
	}

	private JCheckBox getJCheckCase() {
		if (jCheckCase == null) {
			jCheckCase = new JCheckBox(labels.getString("lbl_case"));
			jCheckCase.setMnemonic(KeyEvent.VK_C);
		}
		return jCheckCase;
	}

	private JRadioButton getJRadioBackward() {
		if (jRadioBackward == null) {
			jRadioBackward = new JRadioButton(labels.getString("lbl_backward"));
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
	 * This method initializes jOptionsPanel	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJOptionsPanel() {
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.insets = new Insets(3, 0, 0, 0);
		gridBagConstraints6.gridx = 1;
		gridBagConstraints6.anchor = GridBagConstraints.WEST;
		gridBagConstraints6.gridy = 0;
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.insets = new Insets(2, 0, 3, 0);
		gridBagConstraints5.gridx = 1;
		gridBagConstraints5.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints5.weighty = 10.0D;
		gridBagConstraints5.gridy = 1;
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.insets = new Insets(3, 0, 0, 0);
		gridBagConstraints4.gridx = 3;
		gridBagConstraints4.anchor = GridBagConstraints.WEST;
		gridBagConstraints4.gridy = 0;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.insets = new Insets(2, 0, 3, 0);
		gridBagConstraints3.gridx = 3;
		gridBagConstraints3.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints3.weighty = 10.0D;
		gridBagConstraints3.gridy = 1;
		JPanel jOptionsPanel = new JPanel();
		GridBagLayout gbl_jOptionsPanel = new GridBagLayout();
		gbl_jOptionsPanel.columnWidths = new int[] {30, 1, 30, 1, 30};
		gbl_jOptionsPanel.columnWeights = new double[]{2.0, 1.0, 3.0, 1.0, 2.0};
		jOptionsPanel.setLayout(gbl_jOptionsPanel);
		jOptionsPanel.setBorder(BorderFactory.createLineBorder(SystemColor.controlShadow, 1));
		jOptionsPanel.add(getJRadioBackward(), gridBagConstraints3);
		jOptionsPanel.add(getJRadioForward(), gridBagConstraints4);
		jOptionsPanel.add(getJRadioFromStart(), gridBagConstraints6);
		jOptionsPanel.add(getJRadioFromEnd(), gridBagConstraints5);
		return jOptionsPanel;
	}

	private JRadioButton getJRadioForward() {
		if (jRadioForward == null) {
			jRadioForward = new JRadioButton(labels.getString("lbl_forward"));
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

	private JRadioButton getJRadioFromStart() {
		if (jRadioFromStart == null) {
			jRadioFromStart = new JRadioButton(labels.getString("lbl_fromStart"));
			jRadioFromStart.setMnemonic(KeyEvent.VK_S);
			jRadioFromStart.addItemListener(event -> {
				if(event.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
					getJRadioForward().setSelected(true);
				}
			});
		}
		return jRadioFromStart;
	}

	private JRadioButton getJRadioFromEnd() {
		if (jRadioFromEnd == null) {
			jRadioFromEnd = new JRadioButton(labels.getString("lbl_fromEnd"));
			jRadioFromEnd.setMnemonic(KeyEvent.VK_E);
			jRadioFromEnd.addItemListener(event -> {
				if(event.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
					getJRadioBackward().setSelected(true);
				}
			});
		}
		return jRadioFromEnd;
	}

	private JTextField getJTextReplace() {
		if (jTextReplace == null) {
			jTextReplace = new JTextField(25);
			jTextReplace.setFocusAccelerator('R');
		}
		return jTextReplace;
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
			if(ind >= 0) {
				textComponent.setCaretPosition(ind);
				textComponent.moveCaretPosition(ind + toFind.length());
				textComponent.requestFocus(); // to show selection
				jButtonReplace.setEnabled(true);
//				if(keepFocus) {
//					this.requestFocus();
//				}
			}
			return ind;
		} catch (BadLocationException e) {
			return -1;
		}
	}

	protected void doReplace() {
		JTextComponent textComponent = textComp.get();
		String selected = textComponent.getSelectedText();
		if(selected != null && selected.equalsIgnoreCase(jTextFind.getText())) { // do not replace if selection has changed
			textComponent.replaceSelection(jTextReplace.getText());
		}
		jButtonReplace.setEnabled(false);
	}

	protected void doReplaceAll() {
		final String findText = jTextFind.getText();
		if(findText.length() > 0) {
			JTextComponent textComponent = textComp.get();
			textComponent.selectAll();
			textComponent.replaceSelection(textComponent.getText().replace(findText, jTextReplace.getText()));
			jButtonReplace.setEnabled(false);
		}
	}
}
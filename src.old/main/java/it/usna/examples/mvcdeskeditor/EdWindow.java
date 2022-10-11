package it.usna.examples.mvcdeskeditor;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import it.usna.mvc.controller.Controller;
import it.usna.mvc.view.InternalView;
import it.usna.swing.dialog.FindReplaceDialog;

public class EdWindow extends InternalView<EdModel> {

	private static final long serialVersionUID = 1L;
	
	private JPanel jPanel = null;
	private JScrollPane jScrollPane = null;
	private JTextArea jTextArea = null;

	public EdWindow(final EdModel model, final Controller controller) {
		super(controller, model);
		initialize();
	}

	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJPanel());
	}

	/**
	 * This method initializes jPanel	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jPanel.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		}
		return jPanel;
	}

	/**
	 * This method initializes jScrollPane	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTextArea());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTextArea	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					if(!e.isControlDown() && !model.documentChanged()) {
						setTitle(model.getName());
					}
				}
			});
		}
		return jTextArea;
	}

	public void save(final String filename) {
		model.saveModel(filename, jTextArea.getText());
	}
	
	@Override
	public void update(final Object sender, final Object msg) {
		if(sender instanceof EdModel) {
			switch ((EdModel.ModelMsg) msg) {
			case TITLE_CHANGED:
				setTitle(((EdModel) sender).getName());
				break;
			case LOAD_TEXT:
				jTextArea.setText(model.getContent());
				break;
			}
		} else if(sender instanceof Controller) {
			switch ((EdController.CtrlMsg) msg) {
			case FIND:
				if(!isIcon) {
					final FindReplaceDialog df = new FindReplaceDialog(getDialogParent(), jTextArea, true);
					//df.enableReplace(false);
					df.setLocationRelativeTo(this);
					df.setVisible(true);
				}
				break;
			}
		}
	}
	
	@Override
	public boolean close() {
		if (model.getModified()) {
			if (JOptionPane.showConfirmDialog(this,
					"File has been changed; close anyway?", "Close file",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				return super.close();
			} else {
				return false;
			}
		} else {
			return super.close();
		}
	}
}
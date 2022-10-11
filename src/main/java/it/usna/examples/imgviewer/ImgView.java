package it.usna.examples.imgviewer;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import it.usna.mvc.controller.ControllerImpl;
import it.usna.mvc.view.InternalView;

public class ImgView extends InternalView<ImgModel> {

	private static final long serialVersionUID = 1L;

	private JScrollPane jScrollPane = null;

	private JPanel jImgPanel = null;

	public ImgView(final ImgModel model, final ControllerImpl<ImgModel> controller) {
		super(controller, model,
				false, //resizable
				false, //closable
				true, //maximizable
				false);//iconifiable
		initialize();

	    final JLabel p = new JLabel(model.getContent(), JLabel.CENTER);
	    p.setOpaque(true);
	    jImgPanel.add(p, BorderLayout.CENTER);
	}
	
	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJScrollPane());
	}

	// Do not render the title bar / exclude border / maximize
	public void fullScreen() {
		setMaximum(true);
		((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI()).setNorthPane(null);
		setBorder(null);
	}

	/**
	 * This method initializes jScrollPane	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJImgPanel());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jImgPanel	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJImgPanel() {
		if (jImgPanel == null) {
			jImgPanel = new JPanel();
			jImgPanel.setLayout(new BorderLayout());
		}
		return jImgPanel;
	}
}
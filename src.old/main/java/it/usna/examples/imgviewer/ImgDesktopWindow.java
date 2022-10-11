package it.usna.examples.imgviewer;

import java.awt.BorderLayout;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import it.usna.mvc.extra.ViewsTabBar;
import it.usna.mvc.extra.WindowsMenu;
import it.usna.mvc.view.Desktop;

public class ImgDesktopWindow extends Desktop<ImgController> {
	private static final long serialVersionUID = 1L;
	private JMenuBar jJMenuBar = null;
	private JMenu jMenuFile = null;
	private JMenuItem jMenuItemOpen = null;
	private JMenuItem jMenuItemQuit = null;
	private JMenuItem jMenuItemClose = null;
	private JMenu jMenuHelp = null;
	private JMenuItem jMenuItemInfo = null;
	protected JPanel jContentPane = null;
	protected JDesktopPane jDesktopPane = null;
	private ViewsTabBar viewSelectionBar = null;

	public ImgDesktopWindow(final ImgController controller) {
		super(controller);
		initialize();
	}

	protected void initialize() {
		super.initialize();
		this.setSize(300, 200);
		this.setJMenuBar(getJJMenuBar());
		this.setTitle(ImgApp.APP_NAME);
		this.setContentPane(getJContentPane());
	}
	
	protected JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJDesktopPane(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getViewSelectionBar(), java.awt.BorderLayout.NORTH);
		}
		return jContentPane;
	}
	
	/**
	 * This method initializes jDesktopPane	
	 * @return javax.swing.JDesktopPane	
	 */
	protected JDesktopPane getJDesktopPane() {
		if (jDesktopPane == null) {
			jDesktopPane = new JDesktopPane();
		}
		return jDesktopPane;
	}

	/**
	 * This method initializes jJMenuBar	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getJMenuFile());
			jJMenuBar.add(new WindowsMenu("Windows", controller));
			jJMenuBar.add(getJMenuHelp());	
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenuFile	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuFile() {
		if (jMenuFile == null) {
			jMenuFile = new JMenu();
			jMenuFile.setText("File");
			jMenuFile.add(getJMenuItemOpen());
			jMenuFile.addSeparator();
			jMenuFile.add(getJMenuItemClose());
			jMenuFile.addSeparator();
			jMenuFile.add(getJMenuItemQuit());
		}
		return jMenuFile;
	}

	/**
	 * This method initializes jMenuItemOpen	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemOpen() {
		if (jMenuItemOpen == null) {
			jMenuItemOpen = new JMenuItem();
			jMenuItemOpen.setText("Open ...");
			jMenuItemOpen.addActionListener(event -> {
				final JFileChooser chooser = new JFileChooser();
			    chooser.addChoosableFileFilter(new FileNameExtensionFilter("gif Image (*.gif)", ".gif"));
			    chooser.addChoosableFileFilter(new FileNameExtensionFilter("jpeg image (*.jpg, *.jpeg)", ".jpg", ".jpeg"));
			    if(chooser.showOpenDialog(ImgDesktopWindow.this) == JFileChooser.APPROVE_OPTION) {
			    	final ImgView win = controller.openModel(chooser.getSelectedFile().getAbsolutePath());
			    	jDesktopPane.add(win);
					win.setVisible(true);
					win.fullScreen();
			    }
			});
		}
		return jMenuItemOpen;
	}

	/**
	 * This method initializes jMenuItemQuit	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemQuit() {
		if (jMenuItemQuit == null) {
			jMenuItemQuit = new JMenuItem();
			jMenuItemQuit.setText("Quit");
			jMenuItemQuit.addActionListener(event -> controller.closeApp());
		}
		return jMenuItemQuit;
	}

	/**
	 * This method initializes jMenuItemClose	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemClose() {
		if (jMenuItemClose == null) {
			jMenuItemClose = new JMenuItem();
			jMenuItemClose.setText("Close");
			jMenuItemClose.setEnabled(false);
			jMenuItemClose.addActionListener(event -> controller.closeCurrentModel());
		}
		return jMenuItemClose;
	}

	/**
	 * This method initializes jMenuHelp	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuHelp() {
		if (jMenuHelp == null) {
			jMenuHelp = new JMenu();
			jMenuHelp.setText("?");
			jMenuHelp.add(getJMenuItemInfo());
		}
		return jMenuHelp;
	}

	/**
	 * This method initializes jMenuItemInfo	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemInfo() {
		if (jMenuItemInfo == null) {
			jMenuItemInfo = new JMenuItem();
			jMenuItemInfo.setText("Info ...");
			jMenuItemInfo.addActionListener(event -> {
				JOptionPane.showMessageDialog(ImgDesktopWindow.this,
					"<html><center><h1>" + ImgApp.APP_NAME + " " + ImgApp.VERSION + "</h1></center>" +
							"<p><p><i>autore</i>: Antonio Flaccomio" +
							"<br><i>e-mail</i>: aflaccomio@gmail.it" +
							"<br><br>Questa applicazione fa uso della libreria <i>usna</i>.",
							"Informazioni", JOptionPane.PLAIN_MESSAGE);
			});
		}
		return jMenuItemInfo;
	}
	
	/**
	 * This method initializes viewSelectionBar	
	 * @return javax.swing.JToolBar	
	 */
	public ViewsTabBar getViewSelectionBar() {
		if (viewSelectionBar == null) {
			viewSelectionBar = new ViewsTabBar(true, false);
		}
		return viewSelectionBar;
	}

	public void modelExists(final boolean exists) {
		jMenuItemClose.setEnabled(exists);
	}
}  //  @jve:decl-index=0:visual-constraint="18,17"
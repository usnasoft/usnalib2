package it.usna.examples.mvcdeskeditor;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.event.KeyEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultEditorKit;

import it.usna.mvc.extra.LookAndFeelMenu;
import it.usna.mvc.extra.WindowsMenu;
import it.usna.mvc.view.Desktop;

public class EdDesktopWindow extends Desktop<EdController> {
	private static final long serialVersionUID = 1L;
	private JMenuBar jJMenuBar = null;
	private JMenu jMenuFile = null;
	private JMenuItem jMenuItemOpen = null;
	private JMenuItem jMenuItemQuit = null;
	private JMenuItem jMenuItemNew = null;
	private JMenu jMenuWidows = null;
	private JMenuItem jMenuItemClose = null;
	private JMenu jMenuHelp = null;
	private JMenuItem jMenuItemInfo = null;
	private JMenuItem jMenuItemSave = null;
	private JMenuItem jMenuItemSaveAs = null;
	private JMenu jMenuEdit = null;
	private JMenuItem jMenuItemCopy = null;
	private JMenuItem jMenuItemPaste = null;
	private JMenuItem jMenuItemCut = null;
	protected JPanel jContentPane = null;
	protected JDesktopPane jDesktopPane = null;
	private JMenuItem jMenuFind = null;
	
	public EdDesktopWindow(final EdController controller) {
		super(controller);
		initialize();
	}

	protected void initialize() {
		super.initialize();
		this.setSize(300, 200);
		this.setJMenuBar(getJJMenuBar());
		this.setTitle(EdApp.APP_NAME);
		this.setVisible(true);
		this.setContentPane(getJContentPane());
	}
	
	public void modelExists(final boolean exists) {
		jMenuItemSave.setEnabled(exists);
		jMenuItemSaveAs.setEnabled(exists);
		jMenuItemClose.setEnabled(exists);
		jMenuItemCut.setEnabled(exists);
		jMenuItemCopy.setEnabled(exists);
		jMenuItemPaste.setEnabled(exists);
		jMenuFind.setEnabled(exists);
	}
	
	protected JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJDesktopPane(), java.awt.BorderLayout.CENTER);
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
			jJMenuBar.add(getJMenuEdit());
			jJMenuBar.add(getJMenuWidows());
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
			jMenuFile.add(getJMenuItemNew());
			jMenuFile.add(getJMenuItemOpen());
			jMenuFile.addSeparator();
			jMenuFile.add(getJMenuItemSave());
			jMenuFile.add(getJMenuItemSaveAs());
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
			jMenuItemOpen.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					final JFileChooser chooser = new JFileChooser();
				    chooser.addChoosableFileFilter(new FileNameExtensionFilter("SQL script file (*.sql)", ".sql"));
				    chooser.addChoosableFileFilter(new FileNameExtensionFilter("Text file (*.txt)", ".txt"));
				    if(chooser.showOpenDialog(EdDesktopWindow.this) == JFileChooser.APPROVE_OPTION) {
				    	final EdWindow win = controller.openModel(chooser.getSelectedFile().getAbsolutePath());
				    	jDesktopPane.add(win);
						win.setVisible(true);
						win.setMaximum(true);
				    }
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
			jMenuItemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK, false));
			jMenuItemQuit.addActionListener(event -> controller.closeApp());
		}
		return jMenuItemQuit;
	}

	/**
	 * This method initializes jMenuItemNew	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemNew() {
		if (jMenuItemNew == null) {
			jMenuItemNew = new JMenuItem();
			jMenuItemNew.setText("New");
			jMenuItemNew.addActionListener(event -> {
				final EdWindow win = controller.openNewModel();
				jDesktopPane.add(win);
				win.setVisible(true);
				win.setMaximum(true);
			});
		}
		return jMenuItemNew;
	}

	/**
	 * This method initializes jMenuWidows	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuWidows() {
		if (jMenuWidows == null) {
			jMenuWidows = new WindowsMenu(controller);
			jMenuWidows.setText("Windows");
		}
		return jMenuWidows;
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
				JOptionPane.showMessageDialog(EdDesktopWindow.this,
                        "<html><center><h1>" + EdApp.APP_NAME + " " + EdApp.VERSION + "</h1></center>" +
                    	"<p><p><i>autore</i>: Antonio Flaccomio" +
                    	"<br><i>e-mail</i>: aflaccomio@tiscali.it" +
                    	"<br><br>Questa applicazione fa uso della libreria <i>usna</i>.",
                    	"Informazioni", JOptionPane.PLAIN_MESSAGE);
			});
		}
		return jMenuItemInfo;
	}

	/**
	 * This method initializes jMenuItemSave	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemSave() {
		if (jMenuItemSave == null) {
			jMenuItemSave = new JMenuItem();
			jMenuItemSave.setText("Save");
			jMenuItemSave.setEnabled(false);
			jMenuItemSave.addActionListener(event -> controller.saveCurrentModel());
		}
		return jMenuItemSave;
	}

	/**
	 * This method initializes jMenuItemSaveAs	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemSaveAs() {
		if (jMenuItemSaveAs == null) {
			jMenuItemSaveAs = new JMenuItem();
			jMenuItemSaveAs.setText("Save as ...");
			jMenuItemSaveAs.setEnabled(false);
			jMenuItemSaveAs.addActionListener(event -> controller.saveAsCurrentModel());
		}
		return jMenuItemSaveAs;
	}

	/**
	 * This method initializes jMenuEdit	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getJMenuEdit() {
		if (jMenuEdit == null) {
			jMenuEdit = new JMenu();
			jMenuEdit.setText("Edit");
			jMenuEdit.add(getJMenuItemCut());
			jMenuEdit.add(getJMenuItemCopy());
			jMenuEdit.add(getJMenuItemPaste());
			jMenuEdit.addSeparator();
			jMenuEdit.add(getJMenuFind());
			jMenuEdit.addSeparator();
			jMenuEdit.add(new LookAndFeelMenu("Look & Feel", this));	
		}
		return jMenuEdit;
	}

	/**
	 * This method initializes jMenuItemCopy	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemCopy() {
		if (jMenuItemCopy == null) {
			jMenuItemCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
			jMenuItemCopy.setText("Copy");
			jMenuItemCopy.setEnabled(false);
		}
		return jMenuItemCopy;
	}

	/**
	 * This method initializes jMenuItemPaste	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemPaste() {
		if (jMenuItemPaste == null) {
			jMenuItemPaste = new JMenuItem(new DefaultEditorKit.PasteAction());
			jMenuItemPaste.setText("Paste");
			jMenuItemPaste.setEnabled(false);
		}
		return jMenuItemPaste;
	}

	/**
	 * This method initializes jMenuItemCut	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemCut() {
		if (jMenuItemCut == null) {
			jMenuItemCut = new JMenuItem(new DefaultEditorKit.CutAction());
			jMenuItemCut.setText("Cut");
			jMenuItemCut.setEnabled(false);
		}
		return jMenuItemCut;
	}

	/**
	 * This method initializes jMenuFind	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuFind() {
		if (jMenuFind == null) {
			jMenuFind = new JMenuItem();
			jMenuFind.setText("Find/Replace");
			jMenuFind.setEnabled(false);
			jMenuFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK, false));
			jMenuFind.addActionListener(event -> controller.signalTopmostView(EdController.CtrlMsg.FIND));
		}
		return jMenuFind;
	}
}
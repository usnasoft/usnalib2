package it.usna.examples.mvcfreeeditor;

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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultEditorKit;

import it.usna.mvc.controller.Controller;
import it.usna.mvc.extra.LookAndFeelMenu;
import it.usna.mvc.extra.WindowsMenu;
import it.usna.mvc.view.FreeView;
import it.usna.swing.dialog.FindReplaceDialog;

public class EdWindow extends FreeView<EdModel> {

	private static final long serialVersionUID = 1L;
	
	private JPanel jPanel = null;

	private JScrollPane jScrollPane = null;

	private JTextArea jTextArea = null;

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

	private JMenuItem jMenuItemFind = null;

	public EdWindow(final EdModel model, final Controller controller) {
		super(controller, model);
		initialize();
		center();
	}
	
	private void initialize() {
		this.setSize(500, 300);
		this.setJMenuBar(getJJMenuBar());
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
	
	public void update(final Object sender, final Object msg) {
		switch((Integer)msg) {
		case EdModel.TITLE_CHANGED:
			setTitle(((EdModel)sender).getName());
			break;
		case EdModel.LOAD_TEXT:
			jTextArea.setText(model.getContent());
			break;
		}
	}
	
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
			jMenuItemOpen.addActionListener(e -> {
				final JFileChooser chooser = new JFileChooser();
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("SQL script file (*.sql)", ".sql"));
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("Text file (*.txt)", ".txt"));
				if(chooser.showOpenDialog(EdWindow.this) == JFileChooser.APPROVE_OPTION) {
					final String fileName = chooser.getSelectedFile().getAbsolutePath();
					if(model.getNotAFile() && !model.getModified()) {
						model.loadModel(fileName);
					} else {
						final EdWindow win = ((EdController)controller).openModel(fileName);
						win.setVisible(true);
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
			jMenuItemQuit.addActionListener(e -> controller.closeApp());
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
			jMenuItemNew.addActionListener(e -> {
				final EdWindow win = ((EdController)controller).openNewModel();
				win.setVisible(true);
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
			jMenuWidows = new WindowsMenu(((EdController)controller));
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
			jMenuItemClose.addActionListener(e -> close());
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
			jMenuItemInfo.addActionListener(e ->
					JOptionPane.showMessageDialog(EdWindow.this,
                            "<html><center><h1>" + EdApp.APP_NAME + " " + EdApp.VERSION + "</h1></center>" +
                        	"<p><p><i>autore</i>: Antonio Flaccomio" +
                        	"<br><i>e-mail</i>: aflaccomio@tiscali.it" +
                        	"<br><br>Questa applicazione fa uso della libreria <i>usna</i>.",
                        	"Informazioni", JOptionPane.PLAIN_MESSAGE)
			);
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
			jMenuItemSave.addActionListener(e -> ((EdController)controller).saveCurrentModel());
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
			jMenuItemSaveAs.addActionListener(e -> ((EdController)controller).saveAsCurrentModel());
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
			jMenuEdit.add(getJMenuItemFind());
			jMenuEdit.addSeparator();
			jMenuEdit.add(new LookAndFeelMenu("Look & Feel", ((EdController)controller)));
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
		}
		return jMenuItemCut;
	}

	/**
	 * This method initializes jMenuItemFind	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getJMenuItemFind() {
		if (jMenuItemFind == null) {
			jMenuItemFind = new JMenuItem();
			jMenuItemFind.setText("Find/Replace");
			jMenuItemFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK, false));
			jMenuItemFind.addActionListener(e -> {
				final FindReplaceDialog df = new FindReplaceDialog(getDialogParent(), jTextArea, true);
				df.setLocationRelativeTo(EdWindow.this);
				df.setVisible(true);
			});
		}
		return jMenuItemFind;
	}
}
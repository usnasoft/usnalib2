package it.usna.swing.extra;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * This menu item generates a list of items with the added files names and with its same action
 * @author antonio
 */
public class LastFilesMenuItem extends JMenuItem {
	private static final long serialVersionUID = 1L;
	private static final String PROP_LAST_FILE = "LastFiles_";
	private final LinkedList<File> files;
	private final int maxFiles;

	/**
	 * @param parent the parent menu
	 * @param maxFiles maximun number of files in the list
	 */
	public LastFilesMenuItem(final JMenu parent, final int maxFiles) {
		this.maxFiles = maxFiles;
		files = new LinkedList<File>();

		parent.addMenuListener(new javax.swing.event.MenuListener() {
			public void menuSelected(final javax.swing.event.MenuEvent e) {
				// Remove previously generated MenuItems
				for(int i=parent.getItemCount() - 1; i > 0; i--) {
					final JMenuItem item = (JMenuItem)parent.getItem(i);
					if(item instanceof GeneratedLastFilesMenuItem) {
						parent.remove(item);
					}
				}
				// generate new MenuItems
				final int numFiles = files.size();
				if(numFiles > 0) {
					char mnemonic = (char)getMnemonic();
					setupItem(files.get(0), LastFilesMenuItem.this, mnemonic);
					setEnabled(true);
					for(int i=1; i < numFiles; i++) {
						final JMenuItem openFile = new GeneratedLastFilesMenuItem(LastFilesMenuItem.this);
						setupItem(files.get(i), openFile, ++mnemonic);
						parent.insert(openFile, getPosition(parent) + i);
					}
				} else {
					setText("");
					setToolTipText("");
					setEnabled(false);
				}
			}
			
			public void menuDeselected(final javax.swing.event.MenuEvent e) {
			}
			
			public void menuCanceled(final javax.swing.event.MenuEvent e) {
			}
		});
	}
	
	private static void setupItem(final File file, final JMenuItem item, final char mnemonic) {
		if(Character.isDigit(mnemonic)) {
			item.setText(mnemonic + "  " + file.getName());
			item.setToolTipText(file.getAbsolutePath());
			item.setMnemonic(mnemonic);
		} else {
			item.setText(file.getName());
			item.setToolTipText(file.getAbsolutePath());
		}
	}
	
	/**
	 * Remove all files from the list
	 */
	public void removeAllFiles() {
		files.clear();
	}
	
	/**
	 * Remove a file from the list
	 * @param file
	 */
	public void removeFile(final File file) {
		files.remove(file);
	}
	
	/**
	 * Add a file to the list (if the file exists move to the top)
	 * @param file
	 */
	public void addFile(final File file) {
		if(files.contains(file)) {
			files.remove(file);
		}
		if(files.size() >= maxFiles) {
			files.removeLast();
		}
		files.addFirst(file);
	}
	
	private int getPosition(final JMenu parent) {
		for(int i=0; i < parent.getItemCount(); i++) {
			if(parent.getItem(i) == this) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * Get the selected file (use inside the action)
	 * @param parent the parent menu
	 * @param e the event
	 * @return
	 */
	public File getFile(final JMenu parent, final ActionEvent e) {
		final JMenuItem origItem = (JMenuItem)e.getSource();
		final int masterPos = getPosition(parent);
		for(int i = masterPos; i < parent.getItemCount(); i++) {
			if(parent.getItem(i) == origItem) {
				return files.get(i - masterPos);
			}
		}
		return null;
	}

	/**
	 * Store files list
	 * @param prop
	 */
	public void store(final Properties prop) {
		for(int i=0; i < files.size(); i++) {
			prop.setProperty(PROP_LAST_FILE + i, files.get(i).getAbsolutePath());
		}
	}
	
	/**
	 * Load files list
	 * @param prop
	 */
	public void load(final Properties prop) {
		String fileName;
		for(int i=0; (fileName = prop.getProperty(PROP_LAST_FILE + i)) != null; i++) {
			files.add(new File(fileName));
		}
	}
	
	private static class GeneratedLastFilesMenuItem extends JMenuItem {
		private static final long serialVersionUID = 1L;

		private GeneratedLastFilesMenuItem(final LastFilesMenuItem parent) {
			final ActionListener [] listeners = parent.getActionListeners();
			for(ActionListener al: listeners) {
				this.addActionListener(al);
			}
		}
	}
}
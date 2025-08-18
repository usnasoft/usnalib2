package it.usna.mvc.extra;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import it.usna.mvc.controller.ControllerImpl;
import it.usna.mvc.controller.ModelViews;
import it.usna.mvc.view.View;

public class LookAndFeelMenu extends JMenu {

	private static final long serialVersionUID = 1L;
	private static final String PROP_L_AND_F = "LOOK_AND_FEEL";
	protected Container baseContainer;
	protected ControllerImpl<?> contr;

	/**
	 * Constructor; use setBaseContainer(...) or setController(...)
	 * after construction.
	 * @param text the menu's text.
	 */
	public LookAndFeelMenu(final String text) {
		this();
		setText(text);
	}

	/**
	 * Constructor for a desktop based application.
	 * @param text the menu's text.
	 * @param baseContainer typically the desktop container
	 */
	public LookAndFeelMenu(final String text, final Container baseContainer) {
		this();
		this.baseContainer = baseContainer;
		this.contr = null;
		setText(text);
	}

	/**
	 * Constructor for a MVC based application.
	 * @param text the menu's text.
	 * @param baseContainer
	 */
	public LookAndFeelMenu(final String text, final ControllerImpl<?> contr) {
		this();
		this.contr = contr;
		this.baseContainer = null;
		setText(text);
	}

	/**
	 * Constructor; use setBaseContainer(...) or setController(...)
	 * after construction.
	 */
	public LookAndFeelMenu() {
		final ButtonGroup bg = new ButtonGroup();
		final LookAndFeel currentLF = UIManager.getLookAndFeel();
		final UIManager.LookAndFeelInfo[] lfArray = UIManager.getInstalledLookAndFeels();
		for (final UIManager.LookAndFeelInfo lf : lfArray) {
			final String lfName = lf.getName();
			final JRadioButtonMenuItem jMenuItem = new JRadioButtonMenuItem(lfName);
			if (currentLF != null && lfName.equals(currentLF.getName())) {
				jMenuItem.setSelected(true);
			}
			jMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							try {
								setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								UIManager.setLookAndFeel(lf.getClassName());
								if (contr == null) { // There is a desktop frame
									SwingUtilities.updateComponentTreeUI(baseContainer);
								} else { // Not a desktop frame, cycle on all views
									for (final ModelViews<?> mv : contr.getModelsViews()) {
										for (final View view : mv.getViews()) {
											if (view instanceof Container) {
												SwingUtilities.updateComponentTreeUI((Container) view);
											}
										}
									}
								}
							} catch (Exception e) {
								//e.printStackTrace();
							} finally {
								setCursor(Cursor.getDefaultCursor());
							}
						}
					});
				}
			});
			bg.add(jMenuItem);
			add(jMenuItem);
		}
	}

	public void setBaseContainer(final Container baseContainer) {
		this.baseContainer = baseContainer;
	}

	public void setController(final ControllerImpl<?> contr) {
		this.contr = contr;
	}

	/**
	 * Store current Look&Feel
	 * @param prop
	 */
	public static void store(final Properties prop) {
		prop.setProperty(PROP_L_AND_F, UIManager.getLookAndFeel().getClass().getName());
	}

	/**
	 * Load saved look&Fell (execute before any rendering)
	 * @param prop
	 */
	public static void load(final Properties prop) {
		try {
			final String lf = prop.getProperty(PROP_L_AND_F);
			if(lf != null) {
				UIManager.setLookAndFeel(lf);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

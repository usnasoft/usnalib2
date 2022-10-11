package it.usna.mvc.extra;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeMap;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import it.usna.mvc.controller.ControllerImpl;
import it.usna.mvc.controller.ModelViews;
import it.usna.mvc.view.View;

/**
 * This class generate a menu with the list of views (titles);
 * selecting one of them the methos View.takeFocus() is called
 * @author Antonio Flaccomio
 */
public class WindowsMenu extends JMenu {
	private static final long serialVersionUID = 1L;
	private boolean showIcon;

	/**
	 * Contructor
	 * @param contr the Controller
	 * @param conshowIcon
	 */
	public WindowsMenu(final ControllerImpl<?> contr, boolean showIcon) {
		this.showIcon = showIcon;
		
		addMenuListener(new javax.swing.event.MenuListener() {
			public void menuSelected(javax.swing.event.MenuEvent e) {
				populatemenu(contr);
			}
			
			public void menuDeselected(javax.swing.event.MenuEvent e) {
				removeAll();
			}
			
			public void menuCanceled(javax.swing.event.MenuEvent e) {
			}
		});
	}
	
	/**
	 * Contructor
	 * @param contr
	 */
	public WindowsMenu(final ControllerImpl<?> contr) {
		this(contr, false);
	}
	
	/**
	 * Contructor
	 * @param text the menu's text.
	 * @param contr
	 */
	public WindowsMenu(final String text, final ControllerImpl<?> contr) {
		this(contr);
		setText(text);
	}
	
	/**
	 * Show the icon associated with the view
	 * @param show
	 */
	public void setShowIcon(final boolean show) {
		showIcon = show;
	}
	
	private void populatemenu(final ControllerImpl<?> contr) {
		final TreeMap<Integer, View> orderedViews = new TreeMap<>();
		for(final ModelViews<?> mv: contr.getModelsViews()) {
			for(final View view: mv.getViews()) {
				if(view.isVisible()) {
					orderedViews.put(view.getIdentifier(), view);
				}
			}
		}
		for(final View win: orderedViews.values()) {
			final JCheckBoxMenuItem jMenuItem;
			if(showIcon) {
				jMenuItem = new JCheckBoxMenuItem(win.getViewShortName(), win.getIcon());
			} else {
				jMenuItem = new JCheckBoxMenuItem(win.getViewShortName());
			}
			jMenuItem.setState(win.isSelected());
			jMenuItem.addActionListener(new ActionListener() {
		        public void actionPerformed(ActionEvent actionEvent) {
		        	try {
		        		win.setSelected(true);
					} catch (Exception e) {
						//e.printStackTrace();
					}
		        }
		      });
			add(jMenuItem);
		}
	}
}

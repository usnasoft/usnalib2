package it.usna.mvc.extra;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import it.usna.mvc.view.View;

public class ViewsTabBar extends JToolBar {

	private static final long serialVersionUID = 1L;
	private static final Color DEF_SEL_COLOR = Color.RED;
	private static final Color DEF_UNSEL_COLOR = Color.BLACK;
	
	protected final Map<View, JButton> viewsList = new HashMap<>();
	protected boolean showCloseButton;
	protected boolean showIcon;
	protected Color selColor = DEF_SEL_COLOR;
	protected Color unselColor = DEF_UNSEL_COLOR;
	protected ImageIcon closeIcon = null;
	protected ImageIcon closeOverIcon = null;
	
	public ViewsTabBar(final boolean showCloseButton, final boolean showIcon) {
		this.showCloseButton = showCloseButton;
		this.showIcon = showIcon;
	}
	
	public ViewsTabBar() {
		this.showCloseButton = false;
	}
	
	public void setSelectedColor(final Color col) {
		selColor = col;
	}
	
	public void setUnselectedColor(final Color col) {
		unselColor = col;
	}
	
	public void setShowCloseButton(final boolean show) {
		showCloseButton = show;
	}
	
	public void setShowIcon(final boolean show) {
		showIcon = show;
	}

	public void addView(final View view) {
		final JButton viewButton = new JButton();
		final JLabel label = new JLabel(view.getViewShortName());
		viewButton.setLayout(new BoxLayout(viewButton, BoxLayout.X_AXIS));
		viewButton.add(label);
		viewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				view.setSelected(true);
			}
		});
		if(showIcon) {
			label.setIcon(view.getIcon());
		}
		if(showCloseButton) {
			viewButton.add(getCloseButton(view));
		}
		viewsList.put(view, viewButton);
		add(viewButton);
	}
	
	protected JButton getCloseButton(final View view) {
		final JButton closeButton = new JButton(getCloseIcon());
		closeButton.setRolloverIcon(getCloseOverIcon());
		closeButton.setBorderPainted(false);
		closeButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		closeButton.setContentAreaFilled(false);
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				view.close();
			}
		});
		return closeButton;
	}
	
	protected ImageIcon getCloseIcon() {
		if(closeIcon == null) {
			closeIcon = new ImageIcon(ViewsTabBar.class.getResource("/img/close.gif"));
		}
		return closeIcon;
	}
	
	protected ImageIcon getCloseOverIcon() {
		if(closeOverIcon == null) {
			closeOverIcon = new ImageIcon(ViewsTabBar.class.getResource("/img/close_ov.gif"));
		}
		return closeOverIcon;
	}
	
	public void removeView(final View view) {
		final JButton thisButton = viewsList.get(view);
		remove(thisButton);
		repaint(); // strange: I thought this instuction to be not usefull
		           // but if it is missing removing last button in the bar
		           // has non graphic effect
	}
	
	/**
	 * Call to show a view as selected o deselected
	 * @param view
	 * @param selected
	 */
	public void viewSelected(final View view, final boolean selected) {
		final JButton thisButton = viewsList.get(view);
		final Color foreground = selected ? selColor : unselColor;
		thisButton.getComponent(0).setForeground(foreground); // Component(0) == JLabel
	}
}

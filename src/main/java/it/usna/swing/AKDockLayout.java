package it.usna.swing;

import java.awt.*;
import java.util.*;
import javax.swing.*;
	 
public class AKDockLayout extends BorderLayout {
	private static final long serialVersionUID = 1L;
	private java.util.List<Component> north = new ArrayList<Component>(1);
	private java.util.List<Component> south = new ArrayList<Component>(1);
	private java.util.List<Component> east = new ArrayList<Component>(1);
	private java.util.List<Component> west = new ArrayList<Component>(1);
	private Component center = null;
	private int northHeight, southHeight, eastWidth, westWidth;

	public void addLayoutComponent(Component c, Object con) {
		synchronized (c.getTreeLock()) {
			if (con != null) {
				if (con.equals(NORTH)) { north.add(c); }
				else if (con.equals(SOUTH)) { south.add(c); }
	            else if (con.equals(EAST)) { east.add(c); }
	            else if (con.equals(WEST)) { west.add(c); }
	            else if (con.equals(CENTER)) { center = c; }
			}
		}
	}
	 
	public void removeLayoutComponent(Component c) {
		if (c == center) {
			center = null;
		}
		north.remove(c);
		south.remove(c);
		east.remove(c);
		west.remove(c);
	}
	 
	public void layoutContainer(Container target) {
		synchronized (target.getTreeLock()) {
			final Insets insets = target.getInsets();
			int top = insets.top;
			int bottom = target.getHeight() - insets.bottom;
			int left = insets.left;
			int right = target.getWidth() - insets.right;
	 
			northHeight = getPreferredDimension(north).height;
			southHeight = getPreferredDimension(south).height;
			eastWidth = getPreferredDimension(east).width;
			westWidth = getPreferredDimension(west).width;
	 
			placeComponents(target, north, left, top, right - left, northHeight, SwingConstants.TOP);
			top += (northHeight + getVgap());
	 
			placeComponents(target, south, left, bottom - southHeight, right - left, southHeight, SwingConstants.BOTTOM);
			bottom -= (southHeight + getVgap());
	 
			placeComponents(target, east, right - eastWidth, top, eastWidth, bottom - top, SwingConstants.RIGHT);
			right -= (eastWidth + getHgap());
	 
			placeComponents(target, west, left, top, westWidth, bottom - top, SwingConstants.LEFT);
			left += (westWidth + getHgap());
	 
			if (center != null) {
				center.setBounds(left, top, right - left, bottom - top);
			}
		}
	}

	// Returns the ideal width for a vertically oriented toolbar
	// and the ideal height for a horizontally oriented tollbar:
	private Dimension getPreferredDimension(java.util.List<Component> comps) {
		int w = 0, h = 0;
		for(Component c: comps) {
			final Dimension d = c.getPreferredSize();
			w = Math.max(w, d.width);
			h = Math.max(h, d.height);
		}
		return new Dimension(w, h);
	}
	  
	private void placeComponents(Container target, java.util.List<Component> comps, int x, int y, int w, int h, int orientation) {
		int offset = 0;
	 
		if (orientation == SwingConstants.TOP || orientation == SwingConstants.BOTTOM) {
			offset = x;
			int totalWidth=0;
	 
			for (int i = 0; i < comps.size(); i++) {
				final Component c = comps.get(i);
				if (c.isVisible()) {
					int cwidth = c.getPreferredSize().width;
					totalWidth += cwidth;
					if (w < totalWidth && i != 0) {
						offset = x;
						if (orientation == SwingConstants.TOP) {
							y += h;
							northHeight += h;
						} else if (orientation == SwingConstants.BOTTOM) {
							southHeight += h;
							y -= h;
						}
						totalWidth = cwidth;
					}
					c.setBounds(x + offset, y, cwidth, h);
					offset += cwidth;
				}
			}
		} else {
			int totalHeight = 0;
			for (int i = 0; i < comps.size(); i++) {
				final Component c = comps.get(i);
				if (c.isVisible()) {
					int cheight = c.getPreferredSize().height;
					totalHeight += cheight;
					if (h < totalHeight && i != 0) {
						if (orientation == SwingConstants.LEFT) {
							x += w;
							westWidth += w;
						} else if (orientation == SwingConstants.RIGHT) {
							eastWidth += w;
							x -= w;
						}
						totalHeight = cheight;
						offset = 0;
					}
					if (totalHeight > h)
						cheight = h - 1;
					c.setBounds(x, y + offset, w, cheight);
					offset += cheight;
				}
			}
		}
	}
}

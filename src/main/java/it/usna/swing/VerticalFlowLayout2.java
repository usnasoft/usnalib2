package it.usna.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.HashMap;

/**
 * A flow vertical layout arranges components in a vertical flow.
 * The flow direction is determined by the container's <code>componentOrientation</code>
 * property and may be one of two values:
 * <ul>
 * <li><code>ComponentOrientation.TOP_TO_BOTTOM</code>
 * <li><code>ComponentOrientation.BOTTOM_TO_TOP</code>
 * </ul>
 * Vertical alignment can be TOP, CENTER or BOTTOM.<br>
 * Components can be horizontally aligned CENTER, LEFT or RIGHT by default or singularly.<br>
 * javax.swing.Box.Filler are ignored at the top of a column,
 */

// https://stackoverflow.com/questions/8196530/java-vertical-flowlayout-with-horizontal-scrolling

public class VerticalFlowLayout2 implements LayoutManager2, java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	public enum VAlign {
		CENTER,
		TOP,
		BOTTOM
	};

	public enum HAlign {
		LEFT, // This value indicates that each row of components should be left-justified
		RIGHT, // This value indicates that each row of components should be right-justified
		CENTER, // This value indicates that each row of components should be centered
	};
	
	private HashMap<Component, HAlign> compAlign;

	/**
	 * <code>align</code> is the property that determines how each column distributes empty space.
	 * It can be one of the following three values:
	 * <ul>
	 * <code>TOP</code>
	 * <code>BOTTOM</code>
	 * <code>CENTER</code>
	 * </ul>
	 *
	 * @see #getAlignment
	 * @see #setAlignment
	 */
	private VAlign align;
	
	/**
	 * <code>hAlign</code> is the property that determines how each row distributes empty space.
	 * It can be one of the following three values:
	 * <ul>
	 * <code>LEFT</code>
	 * <code>RIGHT</code>
	 * <code>CENTER</code>
	 * </ul>
	 *
	 * @see #getHAlignment
	 * @see #setHAlignment
	 */
	private HAlign hAlign;

	/**
	 * The flow layout manager allows a separation of components with gaps.  The horizontal gap will
	 * specify the space between components and between the components and the borders of the <code>Container</code>.
	 *
	 * @see #getHgap()
	 * @see #setHgap(int)
	 */
	protected int hgap;

	/**
	 * The flow layout manager allows a separation of components with gaps.  The vertical gap will
	 * specify the space between rows and between the the rows and the borders of the <code>Container</code>.
	 *
	 * @see #getHgap()
	 * @see #setHgap(int)
	 */
	protected int vgap;

	/**
	 * Constructs a new <code>VerticalFlowLayout</code> with a centered alignment and a
	 * default 5-unit horizontal and vertical gap.
	 */
	public VerticalFlowLayout2() {
		this(VAlign.CENTER, HAlign.CENTER, 5, 5);
	}

	/**
	 * Constructs a new <code>VerticalFlowLayout</code> with the specified
	 * alignment and a default 5-unit horizontal and vertical gap.
	 * The value of the alignment argument must be one of
	 * <code>VerticalFlowLayout.TOP</code>, <code>VerticalFlowLayout.BOTTOM</code>,
	 * or <code>VerticalFlowLayout.CENTER</code>
	 * @param align the alignment value
	 */
	public VerticalFlowLayout2(VAlign align, HAlign hAalign) {
		this(align, hAalign, 5, 5);
	}

	/**
	 * Creates a new flow layout manager with the indicated alignment
	 * and the indicated horizontal and vertical gaps.
	 * <p>
	 * The value of the alignment argument must be one of
	 * <code>VerticalFlowLayout2.VAlign.TOP</code>, <code>VerticalFlowLayout2.VAlign.BOTTOM</code>,
	 * or <code>VerticalFlowLayout2.VAlign.CENTER</code>.
	 * @param     align   the alignment value
	 * @param     hAlign  the horizontal alignment value
	 * @param     hgap   the horizontal gap between components
	 *                   and between the components and the
	 *                   borders of the <code>Container</code>
	 * @param     vgap   the vertical gap between components
	 *                   and between the components and the
	 *                   borders of the <code>Container</code>
	 */
	public VerticalFlowLayout2(VAlign align, HAlign hAalign, int hgap, int vgap) {
		this.hgap = hgap;
		this.vgap = vgap;
		setAlignment(align);
		setHAlignment(hAalign);
	}

	/**
	 * Gets the alignment for this layout.
	 * Possible values are <code>VerticalFlowLayout.TOP</code>,
	 * <code>VerticalFlowLayout.BOTTOM</code> or <code>VerticalFlowLayout.CENTER</code>,
	 * @return  the alignment value for this layout
	 * @see     java.awt.VerticalFlowLayout#setAlignment
	 */
	public VAlign getAlignment() {
		return align;
	}

	/**
	 * Sets the alignment for this layout. Possible values are
	 * <ul>
	 * <li><code>VerticalFlowLayout2.VAlign.TOP</code>
	 * <li><code>VerticalFlowLayout2.VAlign.BOTTOM</code>
	 * <li><code>VerticalFlowLayout2.VAlign.CENTER</code>
	 * </ul>
	 * @param   align one of the alignment values shown above
	 * @see     #getAlignment()
	 */
	public void setAlignment(VAlign align) {
		this.align = align;
	}
	
	public void setHAlignment(HAlign align) {
		this.hAlign = align;
	}
	
	public HAlign getHAlignment() {
		return hAlign;
	}

	/**
	 * Gets the horizontal gap between components and between the components and the borders
	 * of the <code>Container</code>
	 *
	 * @return   the horizontal gap between components
	 *           and between the components and the borders
	 *           of the <code>Container</code>
	 * @see     java.awt.VerticalFlowLayout#setHgap
	 */
	public int getHgap() {
		return hgap;
	}

	/**
	 * Sets the horizontal gap between components and
	 * between the components and the borders of the
	 * <code>Container</code>.
	 *
	 * @param hgap the horizontal gap between components
	 *           and between the components and the borders
	 *           of the <code>Container</code>
	 * @see     java.awt.VerticalFlowLayout#getHgap
	 */
	public void setHgap(int hgap) {
		this.hgap = hgap;
	}

	/**
	 * Gets the vertical gap between components and
	 * between the components and the borders of the
	 * <code>Container</code>.
	 *
	 * @return   the vertical gap between components
	 *           and between the components and the borders
	 *           of the <code>Container</code>
	 * @see     java.awt.VerticalFlowLayout#setVgap
	 */
	public int getVgap() {
		return vgap;
	}

	/**
	 * Sets the vertical gap between components and between
	 * the components and the borders of the <code>Container</code>.
	 *
	 * @param vgap the vertical gap between components
	 *           and between the components and the borders
	 *           of the <code>Container</code>
	 * @see     java.awt.VerticalFlowLayout#getVgap
	 */
	public void setVgap(int vgap) {
		this.vgap = vgap;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
		// not used on a LayoutManager2 implemetation; addLayoutComponent(Component, Object) instead
	}

	/**
	 * Removes the specified component from the layout.
	 * Not used by this class.
	 * @param comp the component to remove
	 * @see    java.awt.Container#removeAll
	 */
	@Override
	public void removeLayoutComponent(Component comp) {
	}
	
	/**
	 * Adds the specified component to the layout; alignment can be forced to be not default.
	 * @param comp the component to be added
	 * @param constraints horizontal alignment: LEFT, RIGHT or CENTER.
	 */
	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		if(constraints instanceof HAlign ha) {
			if(compAlign == null) compAlign = new HashMap<>();
			compAlign.put(comp, ha);
		}
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return null;
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0.5f;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0.5f;
	}

	@Override
	public void invalidateLayout(Container target) {
		// nothing to do
	}

	/**
	 * Returns the preferred dimensions for this layout given the
	 * <i>visible</i> components in the specified target container.
	 *
	 * @param target the container that needs to be laid out
	 * @return  the preferred dimensions to lay out the
	 *          subcomponents of the specified container
	 * @see Container
	 * @see #minimumLayoutSize
	 * @see    java.awt.Container#getPreferredSize
	 */
	@Override
	public Dimension preferredLayoutSize(Container target) {
		synchronized (target.getTreeLock()) {
			Dimension dim = new Dimension(0, 0);
			int nmembers = target.getComponentCount();
			boolean firstVisibleComponent = true;

			for (int i = 0; i < nmembers; i++) {
				Component m = target.getComponent(i);

				if (m.isVisible()) {
					Dimension d = m.getPreferredSize();
					dim.width = Math.max(dim.width, d.width);

					if (firstVisibleComponent) {
						firstVisibleComponent = false;
						dim.height += d.height;
					} else {
						dim.height += vgap + d.height;
					}
				}
			}

			Insets insets = target.getInsets();
			dim.width += insets.left + insets.right + (hgap << 1);
			dim.height += insets.top + insets.bottom + (vgap << 1);
			return dim;
		}
	}

	/**
	 * Returns the minimum dimensions needed to layout the <i>visible</i>
	 * components contained in the specified target container.
	 * @param target the container that needs to be laid out
	 * @return  the minimum dimensions to lay out the
	 *          subcomponents of the specified container
	 * @see #preferredLayoutSize
	 * @see    java.awt.Container
	 * @see    java.awt.Container#doLayout
	 */
	@Override
	public Dimension minimumLayoutSize(Container target) {
		synchronized (target.getTreeLock()) {
			Dimension dim = new Dimension(0, 0);
			int nmembers = target.getComponentCount();
			boolean firstVisibleComponent = true;

			for (int i = 0; i < nmembers; i++) {
				Component m = target.getComponent(i);
				if (m.isVisible()) {
					Dimension d = m.getMinimumSize();
					dim.width = Math.max(dim.width, d.width);

					if (firstVisibleComponent) {
						firstVisibleComponent = false;
						dim.height += d.height;
					} else {
						dim.height += vgap + d.height;
					}
				}
			}

			Insets insets = target.getInsets();
			dim.width += insets.left + insets.right + (hgap << 1);
			dim.height += insets.top + insets.bottom + (vgap << 1);
			return dim;
		}
	}

	/**
	 * Lays out the container. This method lets each <i>visible</i> component take
	 * its preferred size by reshaping the components in the target container in order to satisfy the alignment of
	 * this <code>VerticalFlowLayout</code> object.
	 *
	 * @param target the specified component being laid out
	 * @see Container
	 * @see    java.awt.Container#doLayout
	 */
	@Override
	public void layoutContainer(Container target) {
		synchronized (target.getTreeLock()) {
			Insets insets = target.getInsets();
			int maxHeight = target.getSize().height - (insets.top + insets.bottom + (vgap << 1));
			int nmembers = target.getComponentCount();
			int x = insets.left + hgap;
			int y = 0;
			int columnWidth = 0;
			int start = 0;

			boolean ttb = target.getComponentOrientation().isLeftToRight();

			for (int i = 0 ; i < nmembers ; i++) {
				Component m = target.getComponent(i);

				if (m.isVisible()) {
					Dimension d = m.getPreferredSize();
					m.setSize(d.width, d.height);

					if(y == 0) {
						y += d.height;
						columnWidth = Math.max(columnWidth, d.width);
					} else if((y + d.height) <= maxHeight) { // y > 0
						y += d.height + vgap;
						columnWidth = Math.max(columnWidth, d.width);
					} else {
						if(m instanceof javax.swing.Box.Filler == false) {
							moveComponents(target, x, insets.top + vgap, columnWidth, maxHeight - y, start, i, ttb);
							y = d.height;
							x += hgap + columnWidth;
							columnWidth = d.width;
							start = i;
						}
					}
				}
			}
			moveComponents(target, x, insets.top + vgap, columnWidth, maxHeight - y, start, nmembers, ttb);
		}
	}

	/**
	 * Centers the elements in the specified row, if there is any slack.
	 * @param target the component which needs to be moved
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width dimensions
	 * @param height the height dimensions
	 * @param columnStart the beginning of the column
	 * @param columnEnd the the ending of the column
	 */
	private void moveComponents(Container target, int x, int y, int width, int height, int columnStart, int columnEnd, boolean ttb) {
		switch (align) {
		case TOP ->	y += ttb ? 0 : height;
		case CENTER -> y += height >> 1;
		case BOTTOM -> y += ttb ? height : 0;
		}

		for (int i = columnStart; i < columnEnd; i++) {
			Component m = target.getComponent(i);

			if (m.isVisible()) {
				int cx;
				HAlign customAlign;
				if(compAlign != null && (customAlign = compAlign.get(m)) != null) {
					if(customAlign == HAlign.LEFT) {
						cx = x;
					} else if(customAlign == HAlign.RIGHT) {
						cx = x + (width - m.getSize().width);
					} else { // customAlign == CENTER
						cx = x + ((width - m.getSize().width) >> 1);
					}
				} else {
					if(hAlign == HAlign.LEFT) {
						cx = x;
					} else if(hAlign == HAlign.RIGHT) {
						cx = x + (width - m.getSize().width);
					} else { // hAlign == CENTER
						cx = x + ((width - m.getSize().width) >> 1);
					}
				}

				if (ttb) {
					m.setLocation(cx, y);
				} else {
					m.setLocation(cx, target.getSize().height - y - m.getSize().height);
				}

				y += m.getSize().height + vgap;
			}
		}
	}
}

//void	setHorizontalFill(boolean hfill)
//void	setVerticalFill(boolean vfill)
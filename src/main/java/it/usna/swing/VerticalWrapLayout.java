package it.usna.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 *  VerticalFlowLayout subclass that fully supports wrapping of components.
 */
public class VerticalWrapLayout extends VerticalFlowLayout {
	private static final long serialVersionUID = 1L;

	private boolean preferVericalScrolling = true;

	/**
	 * Constructs a new <code>WrapLayout</code> with a left
	 * alignment and a default 5-unit horizontal and vertical gap.
	 */
	public VerticalWrapLayout() {
		super();
	}

	/**
	 * Constructs a new <code>FlowLayout</code> with the specified
	 * alignment and a default 5-unit horizontal and vertical gap.
	 * The value of the alignment argument must be one of
	 * <code>WrapLayout</code>, <code>WrapLayout</code>,
	 * or <code>WrapLayout</code>.
	 * @param align the alignment value
	 */
	public VerticalWrapLayout(int align, int hAlign) {
		super(align, hAlign);
	}

	/**
	 * Creates a new flow layout manager with the indicated alignment
	 * and the indicated horizontal and vertical gaps.
	 * <p>
	 * The value of the alignment argument must be one of
	 * <code>WrapLayout</code>, <code>WrapLayout</code>,
	 * or <code>WrapLayout</code>.
	 * @param align the alignment value
	 * @param hgap the horizontal gap between components
	 * @param vgap the vertical gap between components
	 */
	public VerticalWrapLayout(int align, int hAlign, int hgap, int vgap) {
		super(align, hAlign, hgap, vgap);
	}
	
	public void setPreferVerticalScrolling(boolean v) {
		preferVericalScrolling = v;
	}

	/**
	 * Returns the preferred dimensions for this layout given the
	 * <i>visible</i> components in the specified target container.
	 * @param target the component which needs to be laid out
	 * @return the preferred dimensions to lay out the
	 * subcomponents of the specified container
	 */
	@Override
	public Dimension preferredLayoutSize(Container target) {
		return layoutSize(target, true);
	}

	/**
	 * Returns the minimum dimensions needed to layout the <i>visible</i>
	 * components contained in the specified target container.
	 * @param target the component which needs to be laid out
	 * @return the minimum dimensions to lay out the
	 * subcomponents of the specified container
	 */
	@Override
	public Dimension minimumLayoutSize(Container target) {
		Dimension minimum = layoutSize(target, false);
		minimum.height -= (getVgap() + 1);
		return minimum;
	}

	/**
	 * Returns the minimum or preferred dimension needed to layout the target
	 * container.
	 *
	 * @param target target to get layout size for
	 * @param preferred should preferred size be calculated
	 * @return the dimension to layout the target container
	 */
	private Dimension layoutSize(Container target, boolean preferred) {
		synchronized (target.getTreeLock()) {
			Insets insets = target.getInsets();

			Dimension dim;
			Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
			if(scrollPane != null) {
				dim = scrollPane.getSize();
			} else {
				dim = target.getSize();
			}
			//  Each row must fit with the height allocated to the container.
			//  When the container height = 0, the preferred height of the container
			//  has not yet been calculated so lets ask for the maximum.
			if (dim.height == 0) {
				dim.height = Integer.MAX_VALUE;
			}

			int nmembers = target.getComponentCount();

			Dimension max = new Dimension(0, 0);
			for (int i = 0; i < nmembers; i++) {
				Component m = target.getComponent(i);
				if (m.isVisible()) {
					Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
					if(d.width > max.width) {
						max.width = d.width;
					}
					if(d.height > max.height) {
						max.height = d.height;
					}
				}
			}
			if(preferVericalScrolling) {
				int columns = (dim.width - hgap - insets.left - insets.right) / (max.width + hgap);
				if(columns == 0) {
					columns = 1;
				}
				dim.width = columns * (max.width + hgap) + insets.left + insets.right + hgap;

				int rows = nmembers / columns;
				if(nmembers % columns > 0) {
					rows ++;
				}
				dim.height = rows * (max.height + vgap) + insets.top + insets.bottom + vgap;
			} else {
				int rows = (dim.height - vgap - insets.top - insets.bottom) / (max.height + hgap);
				if(rows == 0) {
					rows = 1;
				}
				dim.height = rows * (max.height + vgap) + insets.top + insets.bottom + vgap;

				int columns = nmembers / rows;
				dim.width = columns * (max.width + hgap) + insets.left + insets.right + hgap;
			}
			return dim;
		}
	}

	// Register the wrapping JScrollPane of the target to force target.revalidate() on JScrollPane resize
	public static void listenJScrollPane(final Container target) {
		final Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
		if(scrollPane != null) {
			scrollPane.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					target.revalidate();
					target.repaint();
				}
			});
		}
	}
}
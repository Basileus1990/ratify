package net.darktree;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ModernScrollPane extends JScrollPane {
	private static final int SCROLL_BAR_ALPHA_ROLLOVER = 100;
	private static final int SCROLL_BAR_ALPHA = 50;
	private static final int THUMB_SIZE = 8;
	public static final int SCROLLBAR_SIZE = 10;
	private static final Color THUMB_COLOR = Color.WHITE;

	public ModernScrollPane(Component view) {
		this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	public ModernScrollPane(int vsbPolicy, int hsbPolicy) {
		this(null, vsbPolicy, hsbPolicy);
	}

	public ModernScrollPane(Component view, int vsbPolicy, int hsbPolicy) {

		setBorder(null);

		// Set ScrollBar UI
		JScrollBar verticalScrollBar = getVerticalScrollBar();
		verticalScrollBar.setOpaque(false);
		verticalScrollBar.setUI(new ModernScrollBarUI(this));

		JScrollBar horizontalScrollBar = getHorizontalScrollBar();
		horizontalScrollBar.setOpaque(false);
		horizontalScrollBar.setUI(new ModernScrollBarUI(this));

		setLayout(new ScrollPaneLayout() {

			@Override
			public void layoutContainer(Container parent) {
				Rectangle availR = parent.getBounds();
				availR.x = availR.y = 0;

				// viewport
				Insets insets = parent.getInsets();
				availR.x = insets.left;
				availR.y = insets.top;
				availR.width -= insets.left + insets.right;
				availR.height -= insets.top + insets.bottom;
				if (viewport != null) {
					viewport.setBounds(availR);
				}

				boolean vsbNeeded = isVerticalScrollBarfNecessary();
				boolean hsbNeeded = isHorizontalScrollBarNecessary();

				// vertical scroll bar
				Rectangle vsbR = new Rectangle();
				vsbR.width = SCROLLBAR_SIZE;
				vsbR.height = availR.height - (hsbNeeded ? vsbR.width : 0);
				vsbR.x = availR.x + availR.width - vsbR.width;
				vsbR.y = availR.y;
				if (vsb != null) {
					vsb.setBounds(vsbR);
					vsb.setVisible(vsbNeeded);
				}

				// horizontal scroll bar
				Rectangle hsbR = new Rectangle();
				hsbR.height = SCROLLBAR_SIZE;
				hsbR.width = availR.width - (vsbNeeded ? hsbR.height : 0);
				hsbR.x = availR.x;
				hsbR.y = availR.y + availR.height - hsbR.height;
				if (hsb != null) {
					hsb.setBounds(hsbR);
					hsb.setVisible(hsbNeeded);
				}
			}
		});

		// Layering
		setComponentZOrder(getVerticalScrollBar(), 0);
		setComponentZOrder(getHorizontalScrollBar(), 1);
		setComponentZOrder(getViewport(), 2);

		viewport.setView(view);
	}

	private boolean isVerticalScrollBarfNecessary() {
		Rectangle viewRect = viewport.getViewRect();
		Dimension viewSize = viewport.getViewSize();
		return viewSize.getHeight() > viewRect.getHeight();
	}

	private boolean isHorizontalScrollBarNecessary() {
		Rectangle viewRect = viewport.getViewRect();
		Dimension viewSize = viewport.getViewSize();
		return viewSize.getWidth() > viewRect.getWidth();
	}

	private static class ModernScrollBarUI extends BasicScrollBarUI {

		private final JScrollPane pane;
		public boolean draw = true;

		public ModernScrollBarUI(ModernScrollPane pane) {
			this.pane = pane;
		}

		@Override
		protected JButton createDecreaseButton(int orientation) {
			return new InvisibleScrollBarButton();
		}

		@Override
		protected JButton createIncreaseButton(int orientation) {
			return new InvisibleScrollBarButton();
		}

		@Override
		protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		}

		@Override
		protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
			if (!draw) {
				return;
			}

			int alpha = isThumbRollover() ? SCROLL_BAR_ALPHA_ROLLOVER : SCROLL_BAR_ALPHA;
			int orientation = scrollbar.getOrientation();
			int x = thumbBounds.x;
			int y = thumbBounds.y;

			int width = orientation == JScrollBar.VERTICAL ? THUMB_SIZE : thumbBounds.width;
			width = Math.max(width, THUMB_SIZE);

			int height = orientation == JScrollBar.VERTICAL ? thumbBounds.height : THUMB_SIZE;
			height = Math.max(height, THUMB_SIZE);

			Graphics2D graphics2D = (Graphics2D) g.create();
			graphics2D.setColor(new Color(THUMB_COLOR.getRed(), THUMB_COLOR.getGreen(), THUMB_COLOR.getBlue(), alpha));
			graphics2D.fillRect(x, y, width, height);
			graphics2D.dispose();
		}

		@Override
		protected void setThumbBounds(int x, int y, int width, int height) {
			super.setThumbBounds(x, y, width, height);
			pane.repaint();
		}

		/**
		 * Invisible Buttons, to hide scroll bar buttons
		 */
		private static class InvisibleScrollBarButton extends JButton {

			private InvisibleScrollBarButton() {
				setOpaque(false);
				setFocusable(false);
				setFocusPainted(false);
				setBorderPainted(false);
				setBorder(BorderFactory.createEmptyBorder());
			}
		}
	}
}

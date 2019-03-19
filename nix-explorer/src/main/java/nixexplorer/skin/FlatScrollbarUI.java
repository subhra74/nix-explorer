package nixexplorer.skin;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import nixexplorer.widgets.util.Utility;

public class FlatScrollbarUI extends BasicScrollBarUI {
	public static ComponentUI createUI(JComponent c) {
		return new FlatScrollbarUI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.plaf.basic.BasicScrollBarUI#installUI(javax.swing.JComponent)
	 */
	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setBackground(UIManager.getColor("ScrollBar.background"));
	}

	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		g.setColor(c.getBackground());
		//g.setColor(UIManager.getColor("ScrollBar.background"));
		g.fillRect(0,0,c.getWidth(),c.getHeight());//trackBounds.x, trackBounds.y, trackBounds.width,
				//trackBounds.height);
	}

	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
			return;
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int w = UIManager.getInt("ScrollBar.width") - Utility.toPixel(1);
		// int gap = w / 2;
		if (isThumbRollover()) {
			g.setColor(UIManager.getColor("ScrollBar.thumb"));
		} else {
			g.setColor(UIManager.getColor("ScrollBar.thumbRollover"));
		}

		if (c instanceof JScrollBar) {
			JScrollBar js = (JScrollBar) c;
			if (js.getOrientation() == SwingConstants.NORTH
					|| js.getOrientation() == SwingConstants.SOUTH) {
				g.fillRoundRect(thumbBounds.x, thumbBounds.y, w,
						thumbBounds.height, w, w);
			} else {
				g.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width,
						w, w, w);
			}

		}
	}

	protected JButton createDecreaseButton(int orientation) {
		return createZeroButton();
	}

	protected JButton createIncreaseButton(int orientation) {
		return createZeroButton();
	}

	protected JButton createZeroButton() {
		JButton button = new JButton();
		Dimension zeroDim = new Dimension(0, 0);
		button.setPreferredSize(zeroDim);
		button.setMinimumSize(zeroDim);
		button.setMaximumSize(zeroDim);
		return button;
	}
}

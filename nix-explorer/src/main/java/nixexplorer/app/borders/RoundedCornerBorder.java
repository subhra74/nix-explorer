/**
 * 
 */
package nixexplorer.app.borders;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.UIManager;
import javax.swing.border.Border;

import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class RoundedCornerBorder implements Border {
	private Insets insets;
	private Color color;
	private BasicStroke stoke;

	/**
	 * 
	 */
	public RoundedCornerBorder() {
		insets = new Insets(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5));
		color = UIManager.getColor("RounderBorder.color");
		stoke = new BasicStroke(1.0f);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.border.Border#paintBorder(java.awt.Component,
	 * java.awt.Graphics, int, int, int, int)
	 */
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(color);
		g2.setStroke(stoke);
		g2.drawRoundRect(x, y, width - 1, height - 1, Utility.toPixel(5),
				Utility.toPixel(5));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
	 */
	@Override
	public Insets getBorderInsets(Component c) {
		return insets;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.border.Border#isBorderOpaque()
	 */
	@Override
	public boolean isBorderOpaque() {
		return true;
	}

}

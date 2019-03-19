package nixexplorer.widgets.component;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

public class GradientPanel extends JPanel {
	private GradientPaint gradient;
	private Color color1, color2;

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if (gradient == null) {
			gradient = new GradientPaint(new Point2D.Double(0, 0), color1,
					new Point2D.Double(0, getHeight()), color2);
		} else {
			if (gradient.getPoint1().getY() != getHeight()) {
				gradient.getPoint2()
						.setLocation(new Point2D.Double(0, getHeight()));
			}
		}
		super.paintComponent(g);
		g2.setPaint(gradient);
//		Insets padding = getBorder().getBorderInsets(this);
		// System.out.println(padding);
		// g2.setColor(Color.GRAY);
//		g2.fillRect(padding.left, padding.top,
//				getWidth() - padding.right - padding.left,
//				getHeight() - padding.bottom - padding.top);
		g2.fillRect(0, 0, getWidth(), getHeight());
	}

	public Color getColor1() {
		return color1;
	}

	public void setColor1(Color color1) {
		this.color1 = color1;
	}

	public Color getColor2() {
		return color2;
	}

	public void setColor2(Color color2) {
		this.color2 = color2;
	}
}

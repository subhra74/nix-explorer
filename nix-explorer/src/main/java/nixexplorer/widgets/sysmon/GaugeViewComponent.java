/**
 * 
 */
package nixexplorer.widgets.sysmon;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import nixexplorer.Constants;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class GaugeViewComponent extends JComponent {
	private static final int STROKE_WIDTH1 = Utility.toPixel(10);
	private static final BasicStroke OUTER_STROKE1 = new BasicStroke(
			STROKE_WIDTH1);

	private static final Dimension PREF_SIZE = new Dimension(
			Utility.toPixel(100), Utility.toPixel(100));

	private float value;
	private String valueText = "0 %";
	private String title;
	private Font titleFont;

	/**
	 * 
	 */
	public GaugeViewComponent(String title) {
		setBorder(new EmptyBorder(Utility.toPixel(10), Utility.toPixel(10),
				Utility.toPixel(10), Utility.toPixel(10)));
		setFont(new Font(Font.DIALOG, Font.PLAIN, Utility.toPixel(32)));
		this.title = title;
		this.titleFont = Utility.getFont(Constants.NORMAL);
	}

	@Override
	public Dimension getPreferredSize() {
		return PREF_SIZE;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_NORMALIZE);
		g2.setFont(getFont());
		int fh = g2.getFontMetrics().getHeight();
		g2.setFont(titleFont);
		int fh2 = getFontMetrics(titleFont).getHeight() + Utility.toPixel(5);
		g2.setFont(getFont());
		g2.setColor(UIManager.getColor("Panel.background"));
		g2.fillRect(0, 0, getWidth(), getHeight());
		Insets insets = getInsets();
		int w = Math.min(
				getWidth() - insets.left - insets.right - 2 - 2 * STROKE_WIDTH1,
				getHeight() - insets.top - insets.bottom - fh2
						- 2 * STROKE_WIDTH1);
		if (w < 1) {
			return;
		}

		int x = getWidth() / 2 - w / 2;
		int y = getHeight() / 2 - w / 2 + fh2;
		g2.setStroke(OUTER_STROKE1);

		g2.setColor(UIManager.getColor("LineGraph.gridColor"));

		Arc2D arc = new Arc2D.Double(x, y, w, w, 180.0, -360, Arc2D.OPEN);
		g2.draw(arc);
		// g2.drawArc(x, y, w, w, 180, -180);

		double val = ((value * 360.0) / 100.0);

		g2.setColor(UIManager.getColor("LineGraph.lineColor"));
		g2.setStroke(OUTER_STROKE1);

		Arc2D arc2 = new Arc2D.Double(x, y, w, w, 180.0, -val, Arc2D.OPEN);
		g2.draw(arc2);
		// g2.drawArc(x, y, w, w, 180, -val);

		int fy = getHeight() / 2 + g2.getFontMetrics().getAscent()
				+ Utility.toPixel(5);
		int fx = getWidth() / 2
				- g2.getFontMetrics().stringWidth(valueText) / 2;

		g2.drawString(valueText, fx, fy);
		g2.setColor(UIManager.getColor("Label.foreground"));
		g2.setFont(titleFont);
		g2.drawString(title,
				getWidth() / 2 - g2.getFontMetrics().stringWidth(title) / 2,
				insets.top + g2.getFontMetrics().getAscent()
						+ Utility.toPixel(10));
	}

	/**
	 * @return the value
	 */
	public float getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(float value) {
		this.value = value;
		this.valueText = value + "";
		repaint();
	}

	@Override
	public Dimension getMinimumSize() {
		return PREF_SIZE;
	}
}

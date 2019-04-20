package nixexplorer.widgets.sysmon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Path2D;

import javax.swing.JComponent;
import javax.swing.UIManager;

import nixexplorer.widgets.util.Utility;

public class LineGraph extends JComponent {
	private static final long serialVersionUID = -8887995348037288952L;
	private float[] values = new float[0];

	private Stroke lineStroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND);
	private Stroke gridStroke = new BasicStroke(3.0f, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND);
	private boolean dynamic = false;
	private String suffix = "%";
	private Path2D shape = new Path2D.Double();

//	@Override
//	protected void paintComponent(Graphics g) {
//		Graphics2D g2 = (Graphics2D) g;
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//				RenderingHints.VALUE_ANTIALIAS_ON);
//
//		g2.setColor(bgColor);
//		g2.fillRect(0, 0, getWidth(), getHeight());
//
//		int count = values.length - 1;
//
//		if (count < 1)
//			return;
//
//		float den = 100;
//		if (dynamic) {
//
//			float min = Float.MAX_VALUE;
//			float max = Float.MIN_VALUE;
//
//			for (int i = 0; i < values.length; i++) {
//				if (values[i] < min) {
//					min = values[i];
//				}
//				if (values[i] > max) {
//					max = values[i];
//				}
//			}
//
//			float extra = ((max - min) * 5) / 100;
//			max += extra;
//			min -= extra;
//
//			den = max - min;
//		}
//
//		float denStep = den / 4;
//
//		int labelWidth = Utility.toPixel(100);
//		int labelPaddingX = Utility.toPixel(10);
//		int labelPaddingY = Utility.toPixel(10);
//
//		int height = getHeight() - 2 * labelPaddingY;
//
//		float stepy = height / 4;
//		
//		int ascent = g2.getFontMetrics().getAscent();
//
//		g2.setColor(textColor);
//
//		for (int i = 0; i < 4; i++) {
//			int val = (int) (den - i * denStep);
//			String label = val + "" + suffix;
//			g2.drawString(label, labelPaddingX,
//					(i * stepy + labelPaddingY + ascent));
//		}
//
//		int width = getWidth() - 2 * labelPaddingX - labelWidth;
//
//		float stepx = width / count;
//
//		int xoff = 2 * labelPaddingX + labelWidth;
//		int yoff = labelPaddingY;
//
//		g2.translate(xoff, yoff);
//
//		for (int i = 0; i < count; i++) {
//			g2.setColor(gridColor);
//			g2.setStroke(gridStroke);
//			int y = (int) (i * stepy);
//			int x = (int) (i * stepx);
//			int y1 = (int) ((values[i] * height) / den);
//			int y2 = (int) ((values[i + 1] * height) / den);
//			int x1 = (int) (i * stepx);
//			int x2 = (int) ((i + 1) * stepx);
//			g2.drawLine(0, y, width, y);
//			g2.drawLine(x, 0, x, height);
//			g2.setColor(lineColor);
//			g2.setStroke(lineStroke);
//			g2.drawLine(x1, height - y1, x2, height - y2);
//		}
//
//		g2.translate(-xoff, -yoff);
//	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		Color bgColor = UIManager.getColor("LineGraph.background");
		Color textColor = UIManager.getColor("LineGraph.foreground");

		g2.setColor(bgColor);
		g2.fillRect(0, 0, getWidth(), getHeight());

		int count = values.length - 1;

		if (count < 1)
			return;

		float den = 100;
		if (dynamic) {

			float min = Float.MAX_VALUE;
			float max = Float.MIN_VALUE;

			for (int i = 0; i < values.length; i++) {
				if (values[i] < min) {
					min = values[i];
				}
				if (values[i] > max) {
					max = values[i];
				}
			}

			float extra = ((max - min) * 5) / 100;
			max += extra;
			min -= extra;

			den = max - min;
		}

		float denStep = den / 4;

		int labelWidth = Utility.toPixel(50);
		int labelPaddingX = Utility.toPixel(10);
		int labelPaddingY = Utility.toPixel(5);

		int height = getHeight() - 2 * labelPaddingY;

		float stepy = height / 4;

		int ascent = g2.getFontMetrics().getAscent();

		g2.setColor(textColor);

		for (int i = 0; i < 4; i++) {
			int val = (int) (den - i * denStep);
			String label = val + "" + suffix;
			int w = g2.getFontMetrics().stringWidth(label);
			g2.drawString(label, labelPaddingX + labelWidth - w,
					(i * stepy + labelPaddingY + ascent));
		}

		int width = getWidth() - 3 * labelPaddingX - labelWidth;

		int xoff = 2 * labelPaddingX + labelWidth;
		int yoff = labelPaddingY;

		g2.translate(xoff, yoff);

		drawGraph(width, height, den, count, g2);

		g2.translate(-xoff, -yoff);
	}

	private void drawGraph(int width, int height, float den, int count,
			Graphics2D g2) {
		shape.reset();
		shape.moveTo(width, height);
		shape.lineTo(0, height);

		float stepy = (float) height / 4;
		float stepx = (float) width / count;

		Color lineColor = UIManager.getColor("LineGraph.lineColor");
		Color gridColor = UIManager.getColor("LineGraph.gridColor");

		for (int i = 0; i < count + 1; i++) {
			g2.setColor(gridColor);
			g2.setStroke(gridStroke);
//			int y = (int) Math.floor(i * stepy);
//			int x = (int) Math.floor(i * stepx);
			int y1 = (int) Math.floor((values[i] * height) / den);
			// int y2 = (int) Math.floor((values[i + 1] * height) / den);
			int x1 = (int) Math.floor(i * stepx);
			// int x2 = (int) Math.floor((i + 1) * stepx);

//			g2.setColor(lineColor);
//			g2.drawLine(0, y, width, y);
//			g2.drawLine(x, 0, x, height);
			// g2.drawLine(x1, height - y1, x2, height - y2);
			shape.lineTo(x1, height - y1);
		}

		g2.setColor(gridColor);
		g2.setStroke(gridStroke);
		g2.fill(shape);

		g2.setColor(lineColor);
		g2.setStroke(gridStroke);
		// g2.draw(shape);

		g2.setStroke(lineStroke);

		for (int i = 0; i < count + 1; i++) {
			int y = (int) Math.floor(i * stepy);
			int x = (int) Math.floor(i * stepx);
			g2.drawLine(0, y, width, y);
			g2.drawLine(x, 0, x, height);
		}

		// g2.drawLine(width, 0, width, height);
	}

	public float[] getValues() {
		return values;
	}

	public void setValues(float[] values) {
		this.values = values;
		repaint();
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
}

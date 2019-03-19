package nixexplorer.desktop;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;

public class TransferButton extends JButton {
	private int count = 0;

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		Rectangle2D r2 = g2.getFont().getStringBounds(count + "",
				g2.getFontRenderContext());
		g2.fillRect((int) (super.getWidth() - r2.getWidth()), 0,
				(int) r2.getWidth(), g2.getFontMetrics().getHeight());
		g2.drawString(count + "", (float) (super.getWidth() - r2.getWidth()),
				(float) g2.getFontMetrics().getAscent());
	}
}

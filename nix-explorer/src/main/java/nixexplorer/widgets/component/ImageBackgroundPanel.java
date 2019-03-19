package nixexplorer.widgets.component;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImageBackgroundPanel extends JPanel {
	private BufferedImage img;

	private enum ScaleMode {
		Repeat, Scale
	}

	public ImageBackgroundPanel(LayoutManager lm) {
		this.setLayout(lm);
	}

	public void setImage(URL url) {
		try {
			this.img = ImageIO.read(url.openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		if (this.img != null) {
			g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
		}
		
	}
}

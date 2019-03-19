package nixexplorer.drawables.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.Icon;

public class ScaledIcon implements Icon {

	private int width, height;
	private Image img;

	public ScaledIcon(URL imgFile, int width, int height) {
		try {
			this.width = width;
			this.height = height;
			Image img1 = ImageIO.read(imgFile);
			img = img1.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			img1.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.drawImage(img, x, y, null);
//		g.setColor(Color.WHITE);
//		g.drawRect(x, y, width, height);
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public int getIconHeight() {
		return height;
	}

	/**
	 * @return the img
	 */
	public Image getImg() {
		return img;
	}

}

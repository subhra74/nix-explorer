package nixexplorer.drawables.icons;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class EmptyIcon implements Icon {
	private int width, height;

	public EmptyIcon(int width, int height) {
		super();
		this.width = width;
		this.height = height;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {

	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public int getIconHeight() {
		return height;
	}

}

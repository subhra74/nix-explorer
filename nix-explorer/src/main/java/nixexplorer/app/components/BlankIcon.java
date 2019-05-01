/**
 * 
 */
package nixexplorer.app.components;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * @author subhro
 *
 */
public class BlankIcon implements Icon {
	/**
	 * @param width
	 * @param height
	 */
	public BlankIcon(int width, int height) {
		super();
		this.width = width;
		this.height = height;
	}

	private int width, height;

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

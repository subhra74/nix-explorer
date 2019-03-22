/**
 * 
 */
package nixexplorer.app.components;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

import nixexplorer.core.FileInfo;
import nixexplorer.core.FileType;
import nixexplorer.drawables.icons.ScaledIcon;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class FileIcon implements Icon {

	private Icon icon;
	private Icon linkArrow;
	private boolean showingLinkArrow;

	/**
	 * 
	 */
	public FileIcon(Icon icon, boolean small) {
		this.icon = icon;
		linkArrow = new ScaledIcon(
				getClass().getResource("/images/link_arrow.png"),
				Utility.toPixel(small ? 10 : 14),
				Utility.toPixel(small ? 10 : 14));
	}

	/**
	 * @return the showingLinkArrow
	 */
	public boolean isShowingLinkArrow() {
		return showingLinkArrow;
	}

	/**
	 * @param showingLinkArrow the showingLinkArrow to set
	 */
	public void setShowingLinkArrow(boolean showingLinkArrow) {
		this.showingLinkArrow = showingLinkArrow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics,
	 * int, int)
	 */
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		icon.paintIcon(c, g, x, y);
		if (showingLinkArrow) {
			linkArrow.paintIcon(c, g,
					x + getIconWidth() - linkArrow.getIconWidth(),
					y + getIconHeight() - linkArrow.getIconHeight());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#getIconWidth()
	 */
	@Override
	public int getIconWidth() {
		return icon.getIconWidth();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Icon#getIconHeight()
	 */
	@Override
	public int getIconHeight() {
		return icon.getIconHeight();
	}

}

package nixexplorer.desktop;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.RepaintManager;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import nixexplorer.widgets.util.Utility;

public class TaskbarButton extends JLabel {
	private Color bgColor = UIManager.getColor("Panel.background");;
	private Color focusColor = UIManager.getColor("TaskBar.buttonBackground");
	private Color textColor = UIManager.getColor("Label.foreground");
	private Color selectedTextColor = UIManager.getColor("Label.foreground");
	private boolean transparent = true;
	private String text;
	private BufferedImage img1, img2;
	private JComponent backPanel;

	public TaskbarButton(String text, Icon icon, JComponent backPanel) {
		setText(text);
		setIcon(icon);
		setHorizontalAlignment(JLabel.CENTER);
		this.backPanel = backPanel;
//		setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
//				Utility.toPixel(5), Utility.toPixel(5)));
	}

	@Override
	public void setText(String text) {
		this.text = text;
		setToolTipText(text);
		super.setText(text);
	}

	public void setShowText(boolean showText) {
		if (showText) {
			super.setText(text);
		} else {
			super.setText("");
		}
	}

	public void windowClosed() {
		Container c = this.getParent();
		c.remove(this);
		c.revalidate();
		c.repaint();
	}

	public void setSelected(boolean selected) {
		if (selected) {
			this.setOpaque(true);
			this.setBackground(focusColor);
			this.setForeground(selectedTextColor);
		} else {
			if (transparent) {
				this.setOpaque(false);
			}
			this.setForeground(textColor);
			this.setBackground(bgColor);
		}
//		Component c = getParent().getParent();
//		RepaintManager.currentManager(c).addDirtyRegion((JComponent) c,
//				c.getX(), c.getY(), c.getWidth(), c.getHeight());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
//	@Override
//	protected void paintComponent(Graphics g) {
//		System.out.println("Painting");
//		if (img1 == null) {
//			int iconW = getIcon().getIconWidth();
//			int iconH = getIcon().getIconHeight();
//			int iconX = getWidth() / 2 - iconW / 2;
//			int iconY = getHeight() / 2 - iconH / 2;
//			img1 = new BufferedImage(getWidth(), getHeight(),
//					BufferedImage.TYPE_INT_ARGB);
//			Graphics2D g2 = img1.createGraphics();
//			getIcon().paintIcon(this, g2, iconX, iconY);
//			AlphaComposite c = AlphaComposite.SrcOver.derive(0.5f);
//			g2.setComposite(c);
//			g2.setColor(Color.WHITE);
//			g2.fillRect(0, 0, getWidth(), getHeight());
//			g2.dispose();
//		}
//
//		if (img2 == null) {
//			int iconW = getIcon().getIconWidth();
//			int iconH = getIcon().getIconHeight();
//			int iconX = getWidth() / 2 - iconW / 2;
//			int iconY = getHeight() / 2 - iconH / 2;
//			img2 = new BufferedImage(getWidth(), getHeight(),
//					BufferedImage.TYPE_INT_ARGB);
//			Graphics2D g2 = img2.createGraphics();
//			getIcon().paintIcon(this, g2, iconX, iconY);
//			g2.dispose();
//		}
//
////		System.out.println("bounds: "+getParent().getBounds());
////		RepaintManager.currentManager(getParent().getParent()).addDirtyRegion(this, x, y, w, h);
////		backPanel.repaint(getParent().getParent().getBounds());
//
////		boolean opaque = isOpaque();
////		setOpaque(false);
////		super.paintComponent(g);
////		setOpaque(opaque);
////		if (isOpaque()) {
////			g.setColor(UIManager.getColor("button.highlight"));
////			g.fillRect(0, getHeight() - Utility.toPixel(2), getWidth(),
////					getHeight());
////		}
//		if (isOpaque()) {
//			g.drawImage(img1, 0, 0, this);
//		} else {
//			g.drawImage(img2, 0, 0, this);
//		}
//
//	}

}

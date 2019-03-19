package nixexplorer.widgets.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.Border;

import nixexplorer.widgets.util.Utility;

public class FlatButtonBorder implements Border {
	private Color color;

	public FlatButtonBorder(Color c) {
		this.color = c;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		g.setColor(color);
		if (c instanceof JButton) {
			if (((JButton) c).getModel().isRollover()) {
				g.setColor(UIManager.getColor("Button.highlight"));
			}
		}

		g.drawRect(x, y, width - Utility.toPixel(1),
				height - Utility.toPixel(1));
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return UIManager.getInsets("Button.margin");
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}

}

package nixexplorer.app.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import nixexplorer.widgets.util.Utility;

public class WelcomePageLayout implements LayoutManager {
	private int gap = Utility.toPixel(15);

	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Insets insets = parent.getInsets();
		Component[] components = parent.getComponents();
		int w = 0;
		int h = 0;
		for (Component c : components) {
			Dimension d = c.getPreferredSize();
			w = Math.max(w, d.width);
			h += d.height;
			h += gap;
		}
		return new Dimension(w + insets.left + insets.right,
				h + insets.top + insets.bottom);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}

	@Override
	public void layoutContainer(Container parent) {
		int width = parent.getWidth() + parent.getInsets().left
				+ parent.getInsets().right;
		int height = parent.getHeight() + parent.getInsets().top
				+ parent.getInsets().bottom;
		Dimension d = preferredLayoutSize(parent);
		Component[] components = parent.getComponents();
		int xPadding = width / 2 - d.width / 2;
		int yPadding = height / 2 - d.height / 2;
		int x = xPadding;
		int y = yPadding;
		for (Component c : components) {
			Dimension d2 = c.getPreferredSize();
			c.setBounds(x, y, d.width, d2.height);
			y += d2.height + gap;
		}
	}

}

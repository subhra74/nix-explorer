package nixexplorer.app.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import nixexplorer.widgets.util.Utility;

public class TaskbarLayout implements LayoutManager {

	public TaskbarLayout() {
		super();
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
		// TODO Auto-generated method stub
		// System.out.println("addLayoutComponent");
		// list.add(comp);
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		// TODO Auto-generated method stub

	}

	@Override
	public Dimension preferredLayoutSize(Container c) {
		int w = 0;
		int h = 0;
		for (int i = 0; i < c.getComponentCount(); i++) {
			Component comp = c.getComponent(i);
			Dimension pref = comp.getPreferredSize();

			w += pref.width;

			h = Math.max(h, pref.height);
		}
		// System.out.println("preferredLayoutSize");
		Insets border = c.getInsets();
		return new Dimension(w + border.left + border.right,
				h + border.top + border.bottom);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		// System.out.println("minimumLayoutSize");
		// TODO Auto-generated method stub
		return new Dimension(40, 40);
	}

	private int getTotalLength(Container c) {
		int total = 0;
		for (int i = 0; i < c.getComponentCount(); i++) {
			Component comp = c.getComponent(i);
			Dimension pref = comp.getPreferredSize();

			total += pref.getWidth();
		}
		return total;
	}

	@Override
	public void layoutContainer(Container c) {
		Insets border = c.getInsets();

		int w = c.getWidth() - border.left - border.right;
		int h = c.getHeight() - border.top - border.bottom;

		int len = getTotalLength(c);

		if (len > w) {
			int slice = w / c.getComponentCount();
			int x = 0;
			for (int i = 0; i < c.getComponentCount(); i++) {
				Component comp = c.getComponent(i);
				comp.setBounds(x, 0, slice, h);
				x += slice;
			}
		} else {
			int x = 0;
			for (int i = 0; i < c.getComponentCount(); i++) {
				Component comp = c.getComponent(i);
				comp.setBounds(x, 0, comp.getPreferredSize().width, h);
				x += comp.getPreferredSize().width;
			}
		}
	}

}

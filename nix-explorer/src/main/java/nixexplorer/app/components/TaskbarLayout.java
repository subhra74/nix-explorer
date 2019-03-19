package nixexplorer.app.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import nixexplorer.widgets.util.Utility;

public class TaskbarLayout implements LayoutManager {

	// List<Component> list = new ArrayList<>();
//	FlowLayout fl;
//	int width, height;
	private TaskbarOrientation orientation;

	private int thikness;

	public TaskbarLayout(TaskbarOrientation orientation, int thikness) {
		super();
		this.orientation = orientation;
		this.thikness = thikness;
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
		if (orientation == TaskbarOrientation.Bottom
				|| orientation == TaskbarOrientation.Top) {
			h = thikness;
		} else {
			w = thikness;
		}
		for (int i = 0; i < c.getComponentCount(); i++) {
			Component comp = c.getComponent(i);
			Dimension pref = comp.getPreferredSize();

			if (orientation == TaskbarOrientation.Bottom
					|| orientation == TaskbarOrientation.Top) {
				w += pref.getWidth();
			} else {
				h += pref.getHeight();
			}
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

			if (orientation == TaskbarOrientation.Bottom
					|| orientation == TaskbarOrientation.Top) {
				total += pref.getWidth();
			} else {
				total += pref.getHeight();
			}
		}
		return total;
	}

	@Override
	public void layoutContainer(Container c) {
		// System.out.println("layoutContainer");

		Insets border = c.getInsets();

		int w = c.getWidth() - border.left - border.right;
		int h = c.getHeight() - border.top - border.bottom;

		// System.out.println("Actuals - w:" + w + "h:" + h);

		int len = getTotalLength(c);

		// System.out.println("len - " + len);

		if (orientation == TaskbarOrientation.Bottom
				|| orientation == TaskbarOrientation.Top) {
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
		} else {
			if (len > h) {
				int slice = h / c.getComponentCount();
				int y = 0;
				for (int i = 0; i < c.getComponentCount(); i++) {
					Component comp = c.getComponent(i);
					comp.setBounds(0, y, w, slice);
					y += slice + Utility.toPixel(1);
				}
			} else {
				int y = 0;
				for (int i = 0; i < c.getComponentCount(); i++) {
					Component comp = c.getComponent(i);
					comp.setBounds(0, y, w, comp.getPreferredSize().height);
					y += comp.getPreferredSize().height + Utility.toPixel(1);
				}
			}
		}

//		for (int i = 0; i < c.getComponentCount(); i++) {
//			Component comp = c.getComponent(i);
//			Dimension pref = comp.getPreferredSize();
//
//			if (orientation == TaskbarOrientation.Bottom) {
//				if (h > pref.getHeight()) {
//					h = (int) pref.getHeight();
//				}
//				w += pref.getWidth();
//			}
//
//			comp.setBounds(x, 0, (int) pref.getWidth(), (int) pref.getHeight());
//			width += pref.getWidth();
//			x += pref.getWidth();
//			height = (int) pref.getHeight();
//		}
//		if (width > c.getBounds().getWidth()) {
//			System.out.println("width exceeded");
//		}
//		System.out.println(c.getPreferredSize() + " - "
//				+ new Dimension(width, height) + " " + c.getBounds());
//
//		// parent.setPreferredSize(new Dimension(width, height));
//		// parent.setSize(width, height);
	}

	public TaskbarOrientation getOrientation() {
		return orientation;
	}

	public void setOrientation(TaskbarOrientation orientation) {
		this.orientation = orientation;
	}

	public int getThikness() {
		return thikness;
	}

	public void setThikness(int thikness) {
		this.thikness = thikness;
	}

}

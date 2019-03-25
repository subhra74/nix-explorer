package nixexplorer.skin;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

import nixexplorer.widgets.util.Utility;

public class SeparatorUI extends BasicSeparatorUI {
	public static ComponentUI createUI(JComponent c) {
		return new SeparatorUI();
	}

	@Override
	public void paint(Graphics g, JComponent c) {
//		// TODO Auto-generated method stub
		super.paint(g, c);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		if (((JSeparator) c).getOrientation() == JSeparator.VERTICAL)
			return new Dimension(Utility.toPixel(1), 0);
		else
			return new Dimension(0, Utility.toPixel(1));
	}
}

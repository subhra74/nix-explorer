package nixexplorer.skin;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.Icon;

public class StatefullIcon implements Icon {

	private Icon icon1, icon2;

	public StatefullIcon(Icon icon1, Icon icon2) {
		super();
		this.icon1 = icon1;
		this.icon2 = icon2;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		AbstractButton btn = (AbstractButton) c;
		if (btn.isSelected()) {
			icon2.paintIcon(c, g, x, y);
		} else {
			icon1.paintIcon(c, g, x, y);
		}
	}

	@Override
	public int getIconWidth() {
		return Math.max(icon1.getIconWidth(), icon2.getIconWidth());
	}

	@Override
	public int getIconHeight() {
		return Math.max(icon1.getIconHeight(), icon2.getIconHeight());
	}

}

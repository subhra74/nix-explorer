package nixexplorer.skin;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicCheckBoxUI;

public class FlatCheckBoxUI extends BasicCheckBoxUI {
	public static ComponentUI createUI(JComponent c) {

		return new FlatCheckBoxUI();
	}

	@Override
	protected void installDefaults(AbstractButton b) {
		super.installDefaults(b);
		if (b instanceof JCheckBox) {
			((JCheckBox) b).setSelectedIcon(
					UIManager.getIcon("CheckBox.selectedIcon"));
		}
	}

	@Override
	public synchronized void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(g2, c);
	}
}

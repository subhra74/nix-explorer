/**
 * 
 */
package nixexplorer.skin;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

/**
 * @author subhro
 *
 */
public class FlatTextFieldUI extends BasicTextFieldUI {
	public static ComponentUI createUI(JComponent c) {
		return new FlatTextFieldUI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicTextUI#paintSafely(java.awt.Graphics)
	 */
	@Override
	protected void paintSafely(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintSafely(g2);
	}
}

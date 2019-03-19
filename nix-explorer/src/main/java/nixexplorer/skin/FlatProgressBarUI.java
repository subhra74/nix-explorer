/**
 * 
 */
package nixexplorer.skin;

import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 * @author subhro
 *
 */
public class FlatProgressBarUI extends BasicProgressBarUI {
	public static ComponentUI createUI(JComponent c) {
		return new FlatProgressBarUI();
	}
	
	
	
	/* (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicProgressBarUI#paintString(java.awt.Graphics, int, int, int, int, int, java.awt.Insets)
	 */
	@Override
	protected void paintString(Graphics g, int x, int y, int width, int height,
			int amountFull, Insets b) {
	}
}

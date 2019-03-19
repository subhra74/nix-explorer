/**
 * 
 */
package nixexplorer.widgets.component;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * @author subhro
 *
 */
public class TranslucantPanel extends JPanel {
	private AlphaComposite ac;

	/**
	 * 
	 */
	public TranslucantPanel() {
		setOpacity(0.1f);
	}

	public void setOpacity(float opacity) {
		ac = AlphaComposite.SrcOver.derive(opacity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Composite c = g2.getComposite();
		g2.setColor(getBackground());
		g2.setComposite(ac);
		g2.fillRect(0,0,getWidth(),getHeight());
		g2.setComposite(c);
		super.paintComponent(g);
	}
}

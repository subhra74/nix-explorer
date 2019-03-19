/**
 * 
 */
package nixexplorer.skin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSpinnerUI;

import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class FlatSpinnerUI extends BasicSpinnerUI {
	public static ComponentUI createUI(JComponent c) {
		return new FlatSpinnerUI();
	}

	protected Component createNextButton() {
		JButton btn = new JButton();
		btn.setBackground(UIManager.getColor("Spinner.arrowBackground"));
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		// btn.setContentAreaFilled(false);
		// btn.setRolloverEnabled(false);
		btn.setHorizontalAlignment(JButton.CENTER);
		btn.setIcon(UIManager.getIcon("Spinner.upIcon"));
		btn.setName("Spinner.nextButton");
//		btn.setPreferredSize(
//				new Dimension(Utility.toPixel(20), Utility.toPixel(20)));
//		Component c = createArrowButton(SwingConstants.NORTH);
//		c.setName("Spinner.nextButton");
		installNextButtonListeners(btn);
		return btn;
	}

	protected Component createPreviousButton() {
		JButton btn = new JButton();
		btn.setBackground(UIManager.getColor("Spinner.arrowBackground"));
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
//		btn.setContentAreaFilled(false);
//		btn.setRolloverEnabled(false);
		btn.setHorizontalAlignment(JButton.CENTER);
		btn.setIcon(UIManager.getIcon("Spinner.downIcon"));
		btn.setName("Spinner.previousButton");
//		btn.setPreferredSize(
//				new Dimension(Utility.toPixel(20), Utility.toPixel(20)));
//		Component c = createArrowButton(SwingConstants.NORTH);
//		c.setName("Spinner.nextButton");
		installPreviousButtonListeners(btn);
		return btn;

//        Component c = createArrowButton(SwingConstants.SOUTH);
//        c.setName("Spinner.previousButton");
//        installPreviousButtonListeners(c);
//        return c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#paint(java.awt.Graphics,
	 * javax.swing.JComponent)
	 */
	@Override
	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(g2, c);
	}
}

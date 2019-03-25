package nixexplorer.skin;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.metal.MetalButtonUI;

import nixexplorer.widgets.util.Utility;

public class FlatButtonUI extends BasicButtonUI {

	public static ComponentUI createUI(JComponent c) {
//		if (buttonUI == null) {
//			buttonUI = new FlatButtonUI();
//		}
//		return buttonUI;
		return new FlatButtonUI();
	}

	@Override
	public void installUI(JComponent c) {
//		JButton b = (JButton) c;
//		Insets ins = b.getMargin();
//		System.out.println("Margin1: " + ins);
		// Insets gap = ((JButton) c).getInsets();
		// System.out.println("insets: " + gap);
		super.installUI(c);
//		System.out.println("Margin: " + b.getMargin());
//		if (ins != null) {
//			b.setMargin(ins);
//		}

//		JButton b=(JButton) c;
//		if(b.getMargin() == null || (b.getMargin() instanceof UIResource)) {
//            b.setMargin(UIManager.getInsets(pp + "margin"));
//        }
		// ((JButton) c).setRolloverEnabled(true);
//MetalButtonUI
//		if (gap != null) {
//			if (c instanceof JButton) {
//				JButton btn = (JButton) c;
//				btn.setMargin(gap);
//			}
//		}
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		// TODO Auto-generated method stub
		return super.getPreferredSize(c);
	}

	protected void paintButtonNormal(Graphics g, AbstractButton b) {
		if (b.isOpaque()) {// && b.getClientProperty("button.toolbar") == null)
							// {
			Graphics2D g2 = (Graphics2D) g;
			g2.setPaint(b.getBackground());
			g2.fillRect(0, 0,
					b.getWidth() + b.getInsets().left + b.getInsets().right,
					b.getHeight() + b.getInsets().top + b.getInsets().bottom);
//			if (b.getBorder() != null) {
//				b.getBorder().paintBorder(b, g2, 0, 0, b.getWidth(),
//						b.getHeight());
//			}
		}
	}

	protected void paintButtonPressed(Graphics g, AbstractButton b) {
		if (b.isOpaque()) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(UIManager.getColor("Button.darkShadow"));
			g2.fillRect(0, 0, b.getWidth(), b.getHeight());
		}
	}

	protected void paintButtonRollOver(Graphics g, AbstractButton b) {
		if (b.isOpaque()) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(UIManager.getColor("Button.highlight"));
			g2.fillRect(0, 0, b.getWidth(), b.getHeight());
		}

//		} else if (b.getClientProperty("highlight") != null) {
//			Graphics2D g2 = (Graphics2D) g;
//			g2.setColor(UIManager.getColor("Button.highlight"));
//			g2.fillRect(0, 0, b.getWidth(), b.getHeight());
//		}

//		if (b.isRolloverEnabled()) {
//			Border border = UIManager.getBorder("Button.border");
//			if (border != null) {
//				Graphics2D g2 = (Graphics2D) g;
//				border.paintBorder(b, g2, 1, 1, b.getWidth()-1, b.getHeight()-1);
//			}
//		}
	}

	public void paint(Graphics g, JComponent c) {
	//	System.out.println("Button inset: "+c.getInsets());
		// System.out.println(((JButton)c).isRolloverEnabled());
		// System.out.println(c + " " + c.getInsets());
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		try {
			AbstractButton b = (AbstractButton) c;
			ButtonModel bm = b.getModel();
			if (bm.isRollover()) {
				paintButtonRollOver(g2, b);
			} else if (bm.isPressed()) {
				paintButtonPressed(g2, b);
			} else {
				paintButtonNormal(g2, b);
			}
			super.paint(g2, c);
		} catch (Exception e) {
		}
	}

	@Override
	protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect,
			Rectangle textRect, Rectangle iconRect) {
		// TODO Auto-generated method stub
		// super.paintFocus(g, b, viewRect, textRect, iconRect);
	}
}
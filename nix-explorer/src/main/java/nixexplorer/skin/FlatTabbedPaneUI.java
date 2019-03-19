package nixexplorer.skin;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class FlatTabbedPaneUI extends BasicTabbedPaneUI {

	public FlatTabbedPaneUI() {
		System.out.println("Creating tab ui");
	}

	@Override
	public void installUI(JComponent c) {
		System.out.println("Installing tab ui");
		super.installUI(c);
	}

	public static ComponentUI createUI(JComponent c) {
		return new FlatTabbedPaneUI();
	}

//	@Override
//	public void paint(Graphics g, JComponent c) {
//		// TODO Auto-generated method stub
//		super.paint(g, c);
//	}

	@Override
	protected void paintContentBorder(Graphics g, int tabPlacement,
			int selectedIndex) {
	}

	@Override
	protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
			int x, int y, int w, int h, boolean isSelected) {
	}

	@Override
	protected boolean shouldPadTabRun(int tabPlacement, int run) {
		return false;
	}

//	@Override
//	protected void paintTabArea(Graphics g, int tabPlacement,
//			int selectedIndex) {
//		// TODO Auto-generated method stub
//		super.paintTabArea(g, tabPlacement, selectedIndex);
//	}
//
//	@Override
//	protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects,
//			int tabIndex, Rectangle iconRect, Rectangle textRect) {
//		// TODO Auto-generated method stub
//		//super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
//	}
//	
//	@Override
//	protected void paintText(Graphics g, int tabPlacement, Font font,
//			FontMetrics metrics, int tabIndex, String title, Rectangle textRect,
//			boolean isSelected) {
//		// TODO Auto-generated method stub
////		super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect,
////				isSelected);
//	}
//
//	@Override
//	protected void paintTabBackground(Graphics g, int tabPlacement,
//			int tabIndex, int x, int y, int w, int h, boolean isSelected) {
//		if (isSelected) {
//			g.setColor(Color.GRAY);
//		} else {
//			g.setColor(Color.DARK_GRAY);
//		}
//		g.fillRect(x, y, w, h);
//		// TODO Auto-generated method stub
////		super.paintTabBackground(g, tabPlacement, tabIndex, x, y, w, h,
////				isSelected);
//	}

//	
	@Override
	protected void paintFocusIndicator(Graphics g, int tabPlacement,
			Rectangle[] rects, int tabIndex, Rectangle iconRect,
			Rectangle textRect, boolean isSelected) {
		// TODO Auto-generated method stub
//		super.paintFocusIndicator(g, tabPlacement, rects, tabIndex, iconRect, textRect,
//				isSelected);
	}

	protected JButton createScrollButton(int direction) {
		if (direction != SOUTH && direction != NORTH && direction != EAST
				&& direction != WEST) {
			throw new IllegalArgumentException("Direction must be one of: "
					+ "SOUTH, NORTH, EAST or WEST");
		}
		JButton btn = new JButton();
		if (direction == EAST) {
			btn.setIcon(UIManager.getIcon("AddressBar.forward"));
		} else {
			btn.setIcon(UIManager.getIcon("AddressBar.back"));
		}
		return btn;
	}
}

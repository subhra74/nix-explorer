package nixexplorer.skin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import nixexplorer.widgets.util.Utility;

public class FlatInternalFrameUI extends BasicInternalFrameUI {

	public FlatInternalFrameUI(JInternalFrame b) {
		super(b);
	}

	public static ComponentUI createUI(JComponent c) {
		return new FlatInternalFrameUI((JInternalFrame) c);
	}

	@Override
	protected JComponent createNorthPane(JInternalFrame w) {
		this.titlePane = new FlatBasicInternalFrameTitlePane(w);
		return titlePane;
	}

	@Override
	protected void activateFrame(JInternalFrame f) {
		super.activateFrame(f);
		FlatBasicInternalFrameTitlePane fr = (FlatBasicInternalFrameTitlePane) this.titlePane;
		fr.getCloseButton().setBackground(
				UIManager.getColor("InternalFrame.activeTitleBackground"));
		fr.getMaxButton().setBackground(
				UIManager.getColor("InternalFrame.activeTitleBackground"));
		fr.getIconButton().setBackground(
				UIManager.getColor("InternalFrame.activeTitleBackground"));
		f.setBorder(UIManager.getBorder("InternalFrame.activeBorder"));
	}

	@Override
	protected void deactivateFrame(JInternalFrame f) {
		super.deactivateFrame(f);
		FlatBasicInternalFrameTitlePane fr = (FlatBasicInternalFrameTitlePane) this.titlePane;
		fr.getIconButton().setBackground(
				UIManager.getColor("InternalFrame.inactiveTitleBackground"));
		fr.getCloseButton().setBackground(
				UIManager.getColor("InternalFrame.inactiveTitleBackground"));
		fr.getMaxButton().setBackground(
				UIManager.getColor("InternalFrame.inactiveTitleBackground"));
		f.setBorder(UIManager.getBorder("InternalFrame.border"));
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(g2, c);
	}

}

class FlatBasicInternalFrameTitlePane extends BasicInternalFrameTitlePane {

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintComponent(g2);
	}

	public JButton getCloseButton() {
		return closeButton;
	}

	public JButton getMaxButton() {
		return maxButton;
	}

	public JButton getIconButton() {
		return iconButton;
	}

	@Override
	protected void createButtons() {

		closeButton = new JButton();
		closeButton.addActionListener(closeAction);
		closeButton.setBackground(
				UIManager.getColor("InternalFrame.closeBackground"));
		closeButton.setBorder(null);
		// closeButton.setRolloverEnabled(false);
		// closeButton.setVerticalAlignment(JButton.TOP);
		iconButton = new JButton();
		iconButton.addActionListener(iconifyAction);
		iconButton.setBackground(
				UIManager.getColor("InternalFrame.minBackground"));
		iconButton.setBorder(null);
		// iconButton.setVerticalAlignment(JButton.TOP);
		// iconButton.setRolloverEnabled(false);
		maxButton = new JButton();
		maxButton.setBackground(
				UIManager.getColor("InternalFrame.maxBackground"));
		maxButton.setBorder(null);
		maxButton.addActionListener(maximizeAction);
		// maxButton.setVerticalAlignment(JButton.TOP);
		// maxButton.setRolloverEnabled(false);

		setButtonIcons();
	}

	public FlatBasicInternalFrameTitlePane(JInternalFrame f) {
		super(f);
	}

	@Override
	protected LayoutManager createLayout() {
		return new LayoutManager() {

			@Override
			public void removeLayoutComponent(Component comp) {
				// TODO Auto-generated method stub

			}

			@Override
			public Dimension preferredLayoutSize(Container parent) {
				return minimumLayoutSize(parent);
			}

			@Override
			public Dimension minimumLayoutSize(Container parent) {
				// Calculate width.
				int width = Utility.toPixel(24);

				if (frame.isClosable()) {
					width += UIManager.getInt("InternalFrame.titleButtonWidth");
				}
				if (frame.isMaximizable()) {
					width += UIManager.getInt("InternalFrame.titleButtonWidth");
				}
				if (frame.isIconifiable()) {
					width += UIManager.getInt("InternalFrame.titleButtonWidth");
				}

				FontMetrics fm = frame.getFontMetrics(getFont());
				String frameTitle = frame.getTitle();
				int title_w = frameTitle != null
						? SwingUtilities.computeStringWidth(fm, frameTitle)
						: 0;
				int title_length = frameTitle != null ? frameTitle.length() : 0;

				// Leave room for three characters in the title.
				if (title_length > 3) {
					int subtitle_w = SwingUtilities.computeStringWidth(fm,
							frameTitle.substring(0, 3) + "...");
					width += (title_w < subtitle_w) ? title_w : subtitle_w;
				} else {
					width += title_w;
				}

				// Calculate height.
				Icon icon = frame.getFrameIcon();
				int fontHeight = fm.getHeight();
				fontHeight += Utility.toPixel(2);
				int iconHeight = 0;
				if (icon != null) {
					iconHeight = icon.getIconHeight();
				}
				iconHeight += Utility.toPixel(4);

				int btnHeight = maxButton.getPreferredSize().height;

				int height = Math.max(fontHeight, iconHeight);

				height = Math.max(btnHeight, height);

				Dimension dim = new Dimension(width, height);

				// Take into account the border insets if any.
//				if (getBorder() != null) {
//					dim.height += insets.top + insets.bottom;
//					dim.width += insets.left + insets.right;
//				}
				// dim.height = 50;

				return dim;

			}

			@Override
			public void layoutContainer(Container parent) {
				boolean leftToRight = parent.getComponentOrientation()
						.isLeftToRight();

				int w = getWidth();
				int h = getHeight();
				int x;

				int buttonHeight = closeButton.getIcon().getIconHeight();

				Icon icon = frame.getFrameIcon();
				int iconHeight = 0;
				if (icon != null) {
					iconHeight = icon.getIconHeight();
				}
				x = (leftToRight) ? Utility.toPixel(2)
						: w - UIManager.getInt("InternalFrame.titleButtonWidth")
								- Utility.toPixel(2);
				int pos = 0;

				menuBar.setBounds(x, /* (h - iconHeight) / 2 */pos,
						UIManager.getInt("InternalFrame.titleButtonWidth"),
						UIManager.getInt("InternalFrame.titleButtonHeight"));

				x = (leftToRight)
						? w - UIManager.getInt("InternalFrame.titleButtonWidth")
								- Utility.toPixel(2)
						: Utility.toPixel(2);

				if (frame.isClosable()) {
					closeButton.setBounds(x, pos,
							UIManager.getInt("InternalFrame.titleButtonWidth"),
							UIManager
									.getInt("InternalFrame.titleButtonHeight"));
					x += (leftToRight)
							? -(UIManager
									.getInt("InternalFrame.titleButtonWidth")
									+ Utility.toPixel(2))
							: UIManager.getInt("InternalFrame.titleButtonWidth")
									+ Utility.toPixel(2);
				}

				if (frame.isMaximizable()) {
					maxButton.setBounds(x, pos,
							UIManager.getInt("InternalFrame.titleButtonWidth"),
							UIManager
									.getInt("InternalFrame.titleButtonHeight"));
					x += (leftToRight)
							? -(UIManager.getInt(
									"InternalFrame.titleButtonWidth") + 2)
							: UIManager.getInt("InternalFrame.titleButtonWidth")
									+ Utility.toPixel(2);
				}

				if (frame.isIconifiable()) {
					iconButton.setBounds(x, pos,
							UIManager.getInt("InternalFrame.titleButtonWidth"),
							UIManager
									.getInt("InternalFrame.titleButtonHeight"));
				}

			}

			@Override
			public void addLayoutComponent(String name, Component comp) {
				// TODO Auto-generated method stub

			}
		};
	}

}

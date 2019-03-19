package nixexplorer.skin;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import nixexplorer.widgets.util.Utility;

public class FlatComboBoxUI extends BasicComboBoxUI {
	public static ComponentUI createUI(JComponent c) {
		return new FlatComboBoxUI();
	}

	@Override
	protected JButton createArrowButton() {
		JButton btn = new JButton();
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		btn.setContentAreaFilled(false);
		btn.setRolloverEnabled(false);
		btn.setIcon(UIManager.getIcon("ComboBox.dropIcon"));
		btn.setName("ComboBox.arrowButton");
		btn.setPreferredSize(
				new Dimension(Utility.toPixel(20), Utility.toPixel(20)));
		return btn;
	}

	private boolean isLeftToRight(Component c) {
		return c.getComponentOrientation().isLeftToRight();
	}

	@Override
	protected Rectangle rectangleForCurrentValue() {
		int width = comboBox.getWidth();
		int height = comboBox.getHeight();
		Insets insets = getInsets();
		int buttonSize = height - (insets.top + insets.bottom);
		buttonSize = 0;
		if (isLeftToRight(comboBox)) {
			return new Rectangle(insets.left, insets.top,
					width - (insets.left + insets.right + buttonSize),
					height - (insets.top + insets.bottom));
		} else {
			return new Rectangle(insets.left + buttonSize, insets.top,
					width - (insets.left + insets.right + buttonSize),
					height - (insets.top + insets.bottom));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicComboBoxUI#paint(java.awt.Graphics,
	 * javax.swing.JComponent)
	 */
	@Override
	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(g2, c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicComboBoxUI#createRenderer()
	 */
	@Override
	protected ListCellRenderer<Object> createRenderer() {
		return new DefaultComboRenderer();
	}

	class DefaultComboRenderer implements ListCellRenderer<Object> {

		private JLabel label;

		/**
		 * 
		 */
		public DefaultComboRenderer() {
			label = new JLabel();
			label.setBorder(
					new EmptyBorder(Utility.toPixel(3), Utility.toPixel(5),
							Utility.toPixel(3), Utility.toPixel(3)));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing
		 * .JList, java.lang.Object, int, boolean, boolean)
		 */
		@Override
		public Component getListCellRendererComponent(
				JList<? extends Object> list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {
			label.setOpaque(isSelected);
			if (isSelected) {
				label.setBackground(list.getSelectionBackground());
				label.setForeground(list.getSelectionForeground());
			} else {
				label.setBackground(list.getBackground());
				label.setForeground(list.getForeground());
			}

			label.setFont(list.getFont());

			if (value instanceof Icon) {
				label.setIcon((Icon) value);
			} else {
				label.setText((value == null) ? "" : value.toString());
			}
			return label;
		}

	}
}

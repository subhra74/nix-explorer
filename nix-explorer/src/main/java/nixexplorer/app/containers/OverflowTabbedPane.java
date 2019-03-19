package nixexplorer.app.containers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import nixexplorer.app.components.TaskbarLayout;
import nixexplorer.app.components.TaskbarOrientation;
import nixexplorer.widgets.util.Utility;

public class OverflowTabbedPane extends JPanel {
	private JPanel topPanel;
	private JPanel content;
	private Color tabColor;
	private Color textColor;
	private Icon closeIcon;
	private TabListener tabListener;
	private boolean closable = false;

	public OverflowTabbedPane() {
		super(new BorderLayout());
		topPanel = new JPanel(
				new TaskbarLayout(TaskbarOrientation.Top, Utility.toPixel(30)));
		topPanel.setOpaque(false);
		add(topPanel, BorderLayout.NORTH);
		content = new JPanel(new BorderLayout());
		content.setOpaque(false);
		add(content);
		content.setBorder(UIManager.getBorder("Component.border"));
	}

	public void addTab(String title, Component c, boolean switchToNew) {
		JLabel lblTab = new JLabel(title);
		lblTab.setHorizontalTextPosition(JLabel.LEFT);
		if (closable) {
			lblTab.setIconTextGap(Utility.toPixel(10));
		}
		lblTab.setOpaque(true);
		if (textColor != null) {
			lblTab.setForeground(textColor);
		}
		lblTab.setBackground(UIManager.getColor("FlatTabbedPane.background"));
		lblTab.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		lblTab.putClientProperty("tab.component", c);
		lblTab.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (closable) {
					int index = getSelectedIndex();
					int x = e.getX();
					int iw = lblTab.getWidth() - lblTab.getIcon().getIconWidth()
							- Utility.toPixel(5);
					if (x >= iw) {
						closeTab(index);
						if (getTabCount() > 0) {
							selectTab(index);
						}
						return;
					}
				}
				selectTab(lblTab);
			}
		});
		topPanel.add(lblTab);
		if (switchToNew) {
			selectTab(lblTab);
		}
	}

	private void selectTab(JLabel lblTab) {
		clearSelection();
		// System.out.println("Cleared");
		if (closable) {
			lblTab.setIcon(closeIcon == null
					? UIManager.getIcon("FlatTabbedPane.closeIcon")
					: closeIcon);
		}
		lblTab.putClientProperty("tab.selected", Boolean.TRUE);
		if (tabColor == null) {
			lblTab.setBackground(
					UIManager.getColor("FlatTabbedPane.highlight"));
		} else {
			lblTab.setBackground(tabColor);
		}
		lblTab.setOpaque(true);
//		System.out
//				.println("background set " + lblTab.isOpaque() + " " + lblTab);
		Component c = (Component) lblTab.getClientProperty("tab.component");
		content.removeAll();
		content.add(c);
		lblTab.revalidate();
		lblTab.repaint();
		content.revalidate();
		content.repaint();
	}

	private void selectTab(int index) {
		// System.out.println("Clearing selection");
		clearSelection();
		for (int i = 0, j = 0; i < topPanel.getComponentCount(); i++) {
			Component c = topPanel.getComponent(i);
			if (c instanceof JLabel) {
				if (index == j) {
					// System.out.println("Selecting tab");
					selectTab(((JLabel) c));
					return;
				}
				j++;
			}
		}
	}

	private void clearSelection() {
		for (int i = 0; i < topPanel.getComponentCount(); i++) {
			Component c = topPanel.getComponent(i);
			if (c instanceof JLabel) {
				((JLabel) c).putClientProperty("tab.selected", Boolean.FALSE);
				((JLabel) c).setOpaque(false);
				((JLabel) c).setBackground(
						UIManager.getColor("FlatTabbedPane.background"));
				if (closable) {
					((JLabel) c).setIcon(
							UIManager.getIcon("FlatTabbedPane.blankIcon"));
				}
			}
		}
	}

	public int getSelectedIndex() {
		for (int i = 0, j = 0; i < topPanel.getComponentCount(); i++) {
			Component c = topPanel.getComponent(i);
			if (c instanceof JLabel) {
				if (((JLabel) c)
						.getClientProperty("tab.selected") == Boolean.TRUE) {
					return j;
				}
				j++;
			}
		}
		return -1;
	}

	public void removeAllTabs() {
		content.removeAll();
		topPanel.removeAll();
		revalidate();
		repaint();
	}

	public void closeTab(int index) {
		try {
			for (int i = 0, j = 0; i < topPanel.getComponentCount(); i++) {
				Component c = topPanel.getComponent(i);
				if (c instanceof JLabel) {
					if (j == index) {
						try {
							topPanel.remove(c);
							content.removeAll();
							int prevIndex = j - 1;
							if (prevIndex < 0) {
								return;
							}
							selectTab(prevIndex);
							return;
						} finally {
							revalidate();
							repaint();
							if (tabListener != null) {
								tabListener.tabClosed(index,
										(Component) ((JComponent) c)
												.getClientProperty(
														"tab.component"));
								if (topPanel.getComponentCount() == 0) {
									tabListener.allTabClosed();
								}
							}
						}
					}
					j++;
				}
			}
		} finally {

		}
	}

	public Component getSelectedComponent() {
		for (int i = 0; i < topPanel.getComponentCount(); i++) {
			Component c = topPanel.getComponent(i);
			if (c instanceof JLabel) {
				if (((JLabel) c)
						.getClientProperty("tab.selected") == Boolean.TRUE) {
					return (Component) ((JLabel) c)
							.getClientProperty("tab.component");
				}
			}
		}
		return null;
	}

	public void setTitleAt(int index, String title) {
		for (int i = 0, j = 0; i < topPanel.getComponentCount(); i++) {
			Component c = topPanel.getComponent(i);
			if (c instanceof JLabel) {
				if (index == j) {
					JLabel label = (JLabel) c;
					label.setText(title);
					return;
				}
				j++;
			}
		}
		revalidate();
		repaint();
	}

	public Component getTabAt(int index) {
		for (int i = 0, j = 0; i < topPanel.getComponentCount(); i++) {
			Component c = topPanel.getComponent(i);
			if (c instanceof JLabel) {
				if (index == j) {
					return c;
				}
				j++;
			}
		}
		return null;
	}

	public int getTabCount() {
		int j = 0;
		for (int i = 0; i < topPanel.getComponentCount(); i++) {
			Component c = topPanel.getComponent(i);
			if (c instanceof JLabel) {
				j++;
			}
		}
		return j;
	}

	public Color getTabColor() {
		return tabColor;
	}

	public void setTabColor(Color tabColor) {
		this.tabColor = tabColor;
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	public Icon getCloseIcon() {
		return closeIcon;
	}

	public void setCloseIcon(Icon closeIcon) {
		this.closeIcon = closeIcon;
	}

	public interface TabListener {
		public void allTabClosed();

		public void tabClosed(int index, Component c);
	}

	public TabListener getTabListener() {
		return tabListener;
	}

	public void setTabListener(TabListener tabListener) {
		this.tabListener = tabListener;
	}

	public boolean isClosable() {
		return closable;
	}

	public void setClosable(boolean closable) {
		this.closable = closable;
	}
}

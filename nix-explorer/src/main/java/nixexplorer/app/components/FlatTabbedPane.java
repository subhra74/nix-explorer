package nixexplorer.app.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import nixexplorer.widgets.util.Utility;

public class FlatTabbedPane extends JPanel {
	private JPanel topPanel;
	private JPanel content;
	private Color tabColor;
	private Color textColor;
	private Icon closeIcon;
	private TabListener tabListener;
	private boolean closable = false;
	private boolean closeAllCapable = false;
	// private JButton closeAllBtn;
	private boolean largeTabs = false;

	public FlatTabbedPane() {
		this(false, false);
	}

	public FlatTabbedPane(boolean closeAllCapable, boolean largeTabs) {
		super(new BorderLayout());
		this.largeTabs = largeTabs;
		setBackground(UIManager.getColor("Panel.secondary"));
		topPanel = new JPanel(new TaskbarLayout());
		topPanel.setBackground(UIManager.getColor("Panel.secondary"));// "DefaultBorder.color"));
		JPanel topContainer = new JPanel(new BorderLayout());
		topContainer.add(topPanel);
		topContainer.setBackground(UIManager.getColor("Panel.secondary"));

//		if (closeAllCapable) {
//			closeAllBtn = new JButton(
//					UIManager.getIcon("FlatTabbedPanel.closeAllIcon"));
//			closeAllBtn.setBorderPainted(false);
//			closeAllBtn.setBackground(UIManager.getColor("Panel.secondary"));// UIManager.getColor("DefaultBorder.color"));
//			topContainer.add(closeAllBtn, BorderLayout.EAST);
//		}

		add(topContainer, BorderLayout.NORTH);
		content = new JPanel(new BorderLayout());
		content.setOpaque(false);
		add(content);

//		content.setBorder(new MatteBorder(Utility.toPixel(1),
//				Utility.toPixel(0), Utility.toPixel(0), Utility.toPixel(0),
//				UIManager.getColor("DefaultBorder.color")));

//		setBorder(new MatteBorder(Utility.toPixel(1), Utility.toPixel(1),
//				Utility.toPixel(1), Utility.toPixel(1),
//				UIManager.getColor("DefaultBorder.color")));
	}

	public void addTab(String title, TabbedChild c, boolean switchToNew) {
		JPanel pan = new JPanel(new BorderLayout(5, 5));
		JLabel lblTab = new JLabel(title);
		c.setLabel(lblTab);
		if (this.largeTabs) {
			JLabel lblIcon = new JLabel(c.getIcon());
			pan.add(lblIcon, BorderLayout.WEST);
			pan.putClientProperty("icon.label", lblIcon);
			lblTab.setFont(
					new Font(Font.DIALOG, Font.PLAIN, Utility.toPixel(14)));
		}

		lblTab.setHorizontalTextPosition(JLabel.LEFT);
		if (closable) {
			lblTab.setIconTextGap(Utility.toPixel(10));
		}

		if (textColor != null) {
			lblTab.setForeground(textColor);
		}
		lblTab.setBackground(UIManager.getColor("FlatTabbedPane.background"));
		pan.setBorder(new EmptyBorder(Utility.toPixel(8), Utility.toPixel(8),
				Utility.toPixel(8), Utility.toPixel(8)));
		pan.putClientProperty("tab.component", c);
		lblTab.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (closable) {
					int index = getSelectedIndex();
					int x = e.getX();
					int iw = lblTab.getWidth() - lblTab.getIcon().getIconWidth()
							- Utility.toPixel(5);
					if (x >= iw) {
						if (!closeTab(index)) {
							return;
						}
						int tc = getTabCount();
						if (tc > 0) {
							if (index == tc) {
								selectTab(index - 1);
							} else {
								selectTab(index);
							}
						}
						return;
					}
				}
				selectTab(pan);
			}
		});
		pan.putClientProperty("content.label", lblTab);
		pan.add(lblTab);
		topPanel.add(pan);
		if (switchToNew) {
			selectTab(pan);
		}
	}

	private void selectTab(JPanel pan) {
		clearSelection();
		if (closable) {
			setIcon(pan,
					closeIcon == null
							? UIManager.getIcon("FlatTabbedPane.closeIcon")
							: closeIcon);
		}
		pan.putClientProperty("tab.selected", Boolean.TRUE);

		pan.setBackground(UIManager.getColor("FlatTabbedPane.background"));

		Component c = (Component) pan.getClientProperty("tab.component");
		content.removeAll();
		content.add(c);
		pan.revalidate();
		pan.repaint();
		content.revalidate();
		content.repaint();
		TabbedChild tc = (TabbedChild) c;
		tc.tabSelected();
	}

	private void selectTab(int index) {
		clearSelection();
		for (int i = 0, j = 0; i < topPanel.getComponentCount(); i++) {
			Component c = topPanel.getComponent(i);
			if (c instanceof JComponent) {
				if (index == j) {
					// System.out.println("Selecting tab");
					selectTab(((JPanel) c));
					return;
				}
				j++;
			}
		}
	}

	private void clearSelection() {
		for (int i = 0; i < topPanel.getComponentCount(); i++) {
			Component c = topPanel.getComponent(i);
			if (c instanceof JComponent) {
				((JComponent) c).putClientProperty("tab.selected",
						Boolean.FALSE);
				((JComponent) c).setBackground(
						UIManager.getColor("DefaultBorder.color"));
				if (closable) {
					setIcon((JComponent) c,
							UIManager.getIcon("FlatTabbedPane.blankIcon"));
				}
			}
		}
	}

	public int getSelectedIndex() {
		for (int i = 0, j = 0; i < topPanel.getComponentCount(); i++) {
			Component c = topPanel.getComponent(i);
			if (c instanceof JPanel) {
				if (((JPanel) c)
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

	public boolean closeTab(TabbedChild tc) {
		try {
			for (int i = 0, j = 0; i < topPanel.getComponentCount(); i++) {
				Component c = topPanel.getComponent(i);
				if (c instanceof JPanel) {
					try {
						TabbedChild tc1 = (TabbedChild) ((JComponent) c)
								.getClientProperty("tab.component");
						if (tc == tc1) {
							return closeTab(j);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					j++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Tabbed pane view not found: " + tc);
		return true;
	}

	public boolean closeTab(int index) {
		try {
			for (int i = 0, j = 0; i < topPanel.getComponentCount(); i++) {
				Component c = topPanel.getComponent(i);
				if (c instanceof JPanel) {
					if (j == index) {
						try {
							TabbedChild tc = (TabbedChild) ((JComponent) c)
									.getClientProperty("tab.component");
							if (tc.getWidgetClosed()) {
								System.out.println("already closed: " + tc);
								return true;
							}
							if (!tc.viewClosing()) {
								System.out.println("view not closing: " + tc);
								return false;
							}
							tc.viewClosed();
							tc.setWidgetClosed(true);
							topPanel.remove(c);
							content.removeAll();
							int prevIndex = j - 1;
							if (prevIndex < 0) {
								return true;
							}
							selectTab(prevIndex);
							return true;
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
			System.out.println("view not found: ");
			return false;
		} finally {

		}
	}

	public Component getSelectedComponent() {
		for (int i = 0; i < topPanel.getComponentCount(); i++) {
			Component c = topPanel.getComponent(i);
			if (c instanceof JPanel) {
				if (((JPanel) c)
						.getClientProperty("tab.selected") == Boolean.TRUE) {
					return (Component) ((JPanel) c)
							.getClientProperty("tab.component");
				}
			}
		}
		return null;
	}

	public void setTitleAt(int index, String title) {
//		for (int i = 0, j = 0; i < topPanel.getComponentCount(); i++) {
//			Component c = topPanel.getComponent(i);
//			if (c instanceof JPanel) {
//				if (index == j) {
//					JLabel label = (JLabel) c;
//					label.setText(title);
//					return;
//				}
//				j++;
//			}
//		}
//		revalidate();
//		repaint();
	}

	public Component getTabAt(int index) {
		for (int i = 0, j = 0; i < topPanel.getComponentCount(); i++) {
			Component c = topPanel.getComponent(i);
			if (c instanceof JPanel) {
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
			if (c instanceof JPanel) {
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

	public boolean isCloseAllCapable() {
		return closeAllCapable;
	}

	public void setCloseAllCapable(boolean closeAllCapable) {
		this.closeAllCapable = closeAllCapable;
	}

	private void setIcon(JComponent panel, Icon icon) {
		JLabel lblTab = (JLabel) panel.getClientProperty("content.label");
		lblTab.setIcon(icon);
	}

}

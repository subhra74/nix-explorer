/**
 * 
 */
package nixexplorer.app.components;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author subhro
 *
 */
public class TabbedPanel extends JPanel {
	private List<Tab> tabs = new ArrayList<>();
	private JPanel content;
	private CardLayout card;
	private int selectionIndex;
	private boolean closable;
	private TabListener listener;

	private JPanel tabPanel;

	private TabbedPanel(boolean closable) {
		super(new BorderLayout());
		this.closable = closable;
		card = new CardLayout();
		content = new JPanel(card);
		tabPanel = new JPanel(new TaskbarLayout());
		add(tabPanel, BorderLayout.NORTH);
		add(content);
	}

	public int addTab(String title, Icon icon, Component c, boolean switchTo) {
		Tab tab = closable ? new ClosableTab(title, icon, c, this)
				: new Tab(title, icon, c);
		tabs.add(tab);
		tabPanel.add(tab);
		content.add(c, tab.hashCode() + "");
		clearSelection();
		if (switchTo) {
			selectTab(tab.hashCode());
		}
		return tab.hashCode();
	}

	public void selectTabById(int hashcode) {
		for (Tab tab : tabs) {
			if (hashcode == tab.hashCode()) {
				selectTab(tab);
				return;
			}
		}
	}

	public void selectTab(int index) {
		Tab tab = tabs.get(index);
		selectTab(tab);
	}

	public void selectTab(Tab tab) {
		clearSelection();
		tab.setSelected(true);
		card.show(content, tab.hashCode() + "");
	}

	public void removeTab(int index) {
		Tab tab = tabs.get(index);
		if (tab instanceof ClosableTab) {
			((ClosableTab) tab).closeTab();
		}
	}

	public void removeTabById(int hashCode) {
		for (Tab tab : tabs) {
			if (hashCode == tab.hashCode()) {
				((ClosableTab) tab).closeTab();
				return;
			}
		}
	}

	private int removeTab(Tab tab) {
		int index = tabs.indexOf(tab);
		int idx = index;
		tabs.remove(tab);
		card.removeLayoutComponent(tab.getContent());
		if (tabs.size() == 0) {
			return idx;
		}
		if (index < tabs.size()) {
			selectTab(index);
		} else {
			index = tabs.size() - 1;
			selectTab(index);
		}
		return idx;
	}

	private void clearSelection() {
		for (Tab tab : tabs) {
			tab.setSelected(false);
		}
	}

	private void notifyRemove(int index, int hashCode) {
		if (listener != null) {
			listener.tabClosed(index, hashCode);
		}
	}

	private boolean canClose(Tab tab) {
		if (listener != null
				&& listener.tabClosing(tabs.indexOf(tab), tab.hashCode())) {
			return true;
		}
		return false;
	}

	static class Tab extends JPanel {
		private JLabel lbl;
		private Color selectionBackground, selectionForeground, background,
				foreground;
		private Component c;

		public Tab(String title, Icon icon, Component c) {
			super(new BorderLayout());
			this.c = c;
			this.lbl = new JLabel(title, icon, JLabel.LEFT);
			add(lbl);
		}

		public void setSelected(boolean selected) {
			this.setBackground(selected ? selectionBackground : background);
			this.lbl.setForeground(selected ? selectionForeground : foreground);
		}

		public Component getContent() {
			return c;
		}
	}

	static class ClosableTab extends Tab {
		private Icon closeIcon;
		private JButton closeButton;

		/**
		 * @param title
		 * @param icon
		 */
		public ClosableTab(String title, Icon icon, Component c,
				TabbedPanel tabbedPanel) {
			super(title, icon, c);
			this.closeButton = new JButton();
			this.closeButton.addActionListener(e -> {
				if (tabbedPanel.listener != null) {
					if (!tabbedPanel.canClose(this)) {
						return;
					}
				}
				int index = tabbedPanel.removeTab(this);
				tabbedPanel.notifyRemove(index, hashCode());
			});
			this.closeButton.setRolloverEnabled(true);
			this.closeButton.setRolloverIcon(closeIcon);
			this.closeButton.setRolloverSelectedIcon(closeIcon);
			this.closeButton.setIcon(new BlankIcon(closeIcon.getIconWidth(),
					closeIcon.getIconHeight()));
			add(closeButton, BorderLayout.WEST);
		}

		public void setSelected(boolean selected) {
			super.setSelected(selected);
			closeButton.setIcon(selected ? closeIcon : null);
		}

		public void closeTab() {
			this.closeButton.doClick();
		}

	}

	public interface TabListener {
		public boolean tabClosing(int index, int hashCode);

		public void tabClosed(int index, int hashCode);

		public void tabSelected(int index, int hashCode);
	}

}

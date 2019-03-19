/**
 * 
 */
package nixexplorer.app.components;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class CustomTabbedPane extends JTabbedPane {
	public void addCustomTab(String title, Component component) {
		JPanel tabComponent = new JPanel(new BorderLayout());
		JLabel lblTitle = new JLabel(title);
		lblTitle.setBorder(new EmptyBorder(Utility.toPixel(8),
				Utility.toPixel(8), Utility.toPixel(8), Utility.toPixel(8)));
		tabComponent.add(lblTitle);
		tabComponent.setBorder(UIManager.getBorder("TabbedPane.flatBorder"));
		tabComponent.putClientProperty("tab.label", lblTitle);
		int index = this.getTabCount();
		System.out.println("Inserting tab at: " + index);
		this.insertTab(null, null, component, title, index);
		this.setTabComponentAt(index, tabComponent);
		((JComponent) component).putClientProperty("tab.header", tabComponent);
		this.setSelectedIndex(index);
	}

	/**
	 * 
	 */
	public CustomTabbedPane() {
		this.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		this.addChangeListener(e -> {
			try {
				System.out.println("selected: " + getSelectedIndex());
				int index = getSelectedIndex();
				if (index < 0)
					return;
				System.out.println("Selected index: " + index + " tab count: "
						+ getTabCount());

				for (int i = 0; i < getTabCount(); i++) {
					JComponent c = (JComponent) getTabComponentAt(i);
					if (c == null) {
						continue;
					}
					c.setBorder(UIManager.getBorder("TabbedPane.flatBorder"));
				}

				JComponent c = (JComponent) getTabComponentAt(index);
				System.out.println("selected com: " + c);
				if (c == null) {
					return;
				}
				c.setBorder(
						UIManager.getBorder("TabbedPane.flatHighlightBorder"));
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		});

	}
}

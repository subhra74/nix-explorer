/**
 * 
 */
package nixexplorer.app;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import nixexplorer.Constants;
import nixexplorer.TextHolder;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class ServerToolbar extends JPanel {
	/**
	 * 
	 */
	private Map<String, JButton> buttonMap = new HashMap<String, JButton>();

	public ServerToolbar() {
		BoxLayout box = new BoxLayout(this, BoxLayout.X_AXIS);
		setLayout(box);
		setBorder(new EmptyBorder(Utility.toPixel(10), Utility.toPixel(10),
				Utility.toPixel(10), Utility.toPixel(10)));
	}

	public void createSettingsButton(String key, ActionListener action,
			String title, Icon icon) {
		add(Box.createHorizontalGlue());
		JButton btnSettings = new JButton(title);
		btnSettings.addActionListener(action);
		btnSettings.setFont(Utility.getFont(Constants.SMALL));
		btnSettings.setPreferredSize(new Dimension(
				btnSettings.getPreferredSize().width + Utility.toPixel(10),
				btnSettings.getPreferredSize().height + Utility.toPixel(10)));
		// btnSettings.setIcon(icon);
		add(btnSettings);
		add(Box.createHorizontalStrut(Utility.toPixel(10)));
		// addButton(key, action, title, icon);
	}

	public void createDisconnectButton(String key, ActionListener action,
			String title, Icon icon) {
		//add(Box.createHorizontalStrut(Utility.toPixel(10)));
		JButton btnSettings = new JButton(title);
		btnSettings.addActionListener(action);
		btnSettings.setFont(Utility.getFont(Constants.SMALL));
		btnSettings.setPreferredSize(new Dimension(
				btnSettings.getPreferredSize().width + Utility.toPixel(10),
				btnSettings.getPreferredSize().height + Utility.toPixel(10)));
		// btnSettings.setIcon(icon);
		add(btnSettings);
		add(Box.createHorizontalStrut(Utility.toPixel(10)));
		// addButton(key, action, title, icon);
	}

	public void addButton(String key, ActionListener action, String title,
			Icon icon) {
		JButton btn = new JButton(icon);
		btn.setText(title);
		btn.setVerticalAlignment(JButton.CENTER);
		btn.setHorizontalTextPosition(JButton.CENTER);
		btn.setVerticalTextPosition(JButton.BOTTOM);
		btn.setToolTipText(title);
		btn.setBorderPainted(false);
//		btn.setPreferredSize(
//				new Dimension(Utility.toPixel(64), Utility.toPixel(84)));
		if (action != null) {
			btn.addActionListener(action);
		}

		add(btn);
		buttonMap.put(key, btn);

		adjustButtons();

		revalidate();
		repaint();
	}

	private void adjustButtons() {
		int maxWidth = 0;
		for (String key : buttonMap.keySet()) {
			int w = buttonMap.get(key).getPreferredSize().width;
			if (w > maxWidth) {
				maxWidth = w;
			}
		}

		for (String key : buttonMap.keySet()) {
			JButton btn = buttonMap.get(key);
			Dimension d = new Dimension(maxWidth, Math
					.max(btn.getPreferredSize().height, Utility.toPixel(70)));
			btn.setPreferredSize(d);
			btn.setMinimumSize(d);
			btn.setMaximumSize(d);
		}
	}
}

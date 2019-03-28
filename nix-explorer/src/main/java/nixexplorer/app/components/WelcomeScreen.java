/**
 * 
 */
package nixexplorer.app.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import nixexplorer.Constants;
import nixexplorer.app.AppSidePanel;
import nixexplorer.app.settings.AppConfig;
import nixexplorer.app.settings.ui.ConfigDialog;
import nixexplorer.drawables.icons.ScaledIcon;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class WelcomeScreen extends JPanel {
	/**
	 * 
	 */
	public WelcomeScreen(Window window, AppConfig config, AppSidePanel panel) {
		setLayout(new WelcomePageLayout());// new BorderLayout());
		JLabel lblMainIcon = new JLabel(
				new ScaledIcon(getClass().getResource("/images/app-logo.png"),
						Utility.toPixel(256), Utility.toPixel(256)));
		lblMainIcon.setHorizontalAlignment(JLabel.CENTER);
		lblMainIcon.setVerticalAlignment(JLabel.CENTER);
		add(lblMainIcon);

//		JPanel p = new JPanel();
//
//		Box b1 = Box.createVerticalBox();

		JButton btn1 = new JButton("Connect to a remote server");
		JButton btn2 = new JButton("Settings");
		JButton btn3 = new JButton("Help and support");

		btn1.setHorizontalAlignment(JButton.LEFT);
		btn2.setHorizontalAlignment(JButton.LEFT);
		btn3.setHorizontalAlignment(JButton.LEFT);

		btn1.setBackground(UIManager.getColor("Panel.shadow"));
		btn2.setBackground(UIManager.getColor("Panel.shadow"));
		btn3.setBackground(UIManager.getColor("Panel.shadow"));

		Font font = Utility.getFont(Constants.NORMAL);

		btn1.setBorderPainted(false);
		btn2.setBorderPainted(false);
		btn3.setBorderPainted(false);

		btn1.setIcon(UIManager.getIcon("welcome.new"));
		btn2.setIcon(UIManager.getIcon("welcome.settings"));
		btn3.setIcon(UIManager.getIcon("welcome.help"));

		btn1.setFont(font);
		btn2.setFont(font);
		btn3.setFont(font);

		btn1.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		btn2.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		btn3.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));

		btn1.addActionListener(e -> {
			panel.makeNewSession();
		});

		btn2.addActionListener(e -> {
			new ConfigDialog(window, config).setVisible(true);
		});

		// Dimension d = btn1.getPreferredSize();

		btn1.setPreferredSize(
				new Dimension((int) (btn1.getPreferredSize().width * 1.2),
						(int) (btn1.getPreferredSize().height * 1.2)));
		btn2.setPreferredSize(
				new Dimension((int) (btn2.getPreferredSize().width * 1.2),
						(int) (btn2.getPreferredSize().height * 1.2)));
		btn3.setPreferredSize(
				new Dimension((int) (btn3.getPreferredSize().width * 1.2),
						(int) (btn3.getPreferredSize().height * 1.2)));

		add(btn1);
		add(btn2);
		add(btn3);

//		btn1.setAlignmentX(Box.LEFT_ALIGNMENT);
//		btn2.setAlignmentX(Box.LEFT_ALIGNMENT);
//		btn3.setAlignmentX(Box.LEFT_ALIGNMENT);
//		
//		b1.add(btn1);
//		b1.add(btn2);
//		b1.add(btn3);
//		p.add(b1);
//		add(p, BorderLayout.SOUTH);
	}
}

//package nixexplorer.desktop;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.GridLayout;
//import java.awt.Insets;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.swing.Box;
//import javax.swing.JButton;
//import javax.swing.JPanel;
//import javax.swing.JTextField;
//import javax.swing.UIManager;
//import javax.swing.border.EmptyBorder;
//
//import nixexplorer.registry.PluginEntry;
//import nixexplorer.registry.PluginRegistry;
//import nixexplorer.widgets.util.Utility;
//
//public class StartMenuPanel extends JPanel {
//	private JButton[] items;
//	private JButton[] buttons;
//	private int maxItemOnPage = 16;
//	private JPanel center;
//	private DesktopPanel desktop;
//
//	public StartMenuPanel(DesktopPanel desktop) {
//		this.desktop = desktop;
//		setLayout(new BorderLayout());
//		setBorder(new EmptyBorder(Utility.toPixel(10), Utility.toPixel(10),
//				Utility.toPixel(10), Utility.toPixel(10)));
//		Box b1 = Box.createHorizontalBox();
//
//		JButton btnRecent = new JButton("Recent");
//
////		btnRecent.setMargin(new Insets(Utility.toPixel(5), Utility.toPixel(5),
////				Utility.toPixel(5), Utility.toPixel(5)));
//		JButton btnAll = new JButton("All");
//
//		final Color bgColor = btnRecent.getBackground();
//		final Color hgColor = UIManager.getColor("Button.darkShadow");
//		btnRecent.addActionListener(e -> {
//			btnRecent.setBackground(hgColor);
//			btnAll.setBackground(bgColor);
//		});
//
//		btnAll.addActionListener(e -> {
//			btnAll.setBackground(hgColor);
//			btnRecent.setBackground(bgColor);
//		});
//
//		btnAll.setBackground(hgColor);
//
////		btnAll.setMargin(new Insets(Utility.toPixel(5), Utility.toPixel(5),
////				Utility.toPixel(5), Utility.toPixel(5)));
//
//		Dimension pref1 = btnRecent.getPreferredSize();
//		Dimension pref2 = btnAll.getPreferredSize();
//
//		int width = Math.max(pref1.width, pref2.width);
//		int height = Math.max(pref1.height, pref2.height);
//
//		Dimension d = new Dimension(width, // + Utility.toPixel(5),
//				height);// + Utility.toPixel(5));
//		btnRecent.setPreferredSize(d);
//		btnAll.setPreferredSize(d);
//
//		b1.add(btnRecent);
//		b1.add(btnAll);
//		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
//		b1.add(Box.createHorizontalGlue());
//		b1.add(new JTextField(30));
//		b1.setBorder(
//				new EmptyBorder(Utility.toPixel(5), 0, Utility.toPixel(5), 0));
//		add(b1, BorderLayout.NORTH);
//
//		int count = PluginRegistry.getSharedInstance().getPluginList().size();
//
//		items = new JButton[count];
//
//		for (int i = 0; i < count; i++) {
//			PluginEntry ent = PluginRegistry.getSharedInstance().getPluginList()
//					.get(i);
//			JButton btn = new JButton(ent.getName());
//			btn.putClientProperty("app.menu.launcher", ent);
//			btn.putClientProperty("highlight", true);
//			btn.addActionListener(e -> {
//				desktop.closeStartMenu();
//				PluginEntry item = (PluginEntry) btn
//						.getClientProperty("app.menu.launcher");
//				Map<String, Object> env = new HashMap<>();
//				StartMenuPanel.this.desktop.createWidget(item.getClassName(),
//						env, item.getCommands(), null);
//			});
//			btn.setBorderPainted(false);
//			btn.setFocusPainted(false);
//			// btn.setContentAreaFilled(false);
//			btn.setIcon(ent.getIcon());
//			btn.setPreferredSize(
//					new Dimension(Utility.toPixel(96), Utility.toPixel(96)));
//
//			btn.setVerticalTextPosition(JButton.BOTTOM);
//			btn.setHorizontalTextPosition(JButton.CENTER);
//			btn.setMargin(new Insets(0, 0, 0, 0));
//			items[i] = btn;
//		}
//
//		Box b2 = Box.createHorizontalBox();
//		b2.add(Box.createHorizontalGlue());
//
//		buttons = new JButton[(int) Math.ceil((float) count / maxItemOnPage)];
//		System.out.println("Count: " + buttons.length);
//		for (int i = 0; i < buttons.length; i++) {
//			buttons[i] = new JButton((i + 1) + "");
//			buttons[i].putClientProperty("button.index", Integer.valueOf(i));
////			buttons[i].setHorizontalAlignment(JButton.CENTER);
////			buttons[i].setVerticalAlignment(JButton.CENTER);
//			b2.add(buttons[i]);
//			buttons[i].addActionListener(e -> {
//				Integer index = (Integer) ((JButton) e.getSource())
//						.getClientProperty("button.index");
//				showItems(index);
//			});
//		}
//		b2.add(Box.createHorizontalGlue());
//		b2.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
//				Utility.toPixel(5), Utility.toPixel(5)));
//		center = new JPanel(new GridLayout(0, 3));
//		showItems(0);
//
//		add(center);
//		add(b2, BorderLayout.SOUTH);
//
//	}
//
//	private void showItems(int c) {
//		center.removeAll();
//		int count = 0;
//		for (int i = c * maxItemOnPage; i < items.length; i++) {
//			if (count >= maxItemOnPage) {
//				break;
//			}
//			center.add(items[i]);
//			count++;
//		}
//		revalidate();
//		repaint();
//	}
//
//}

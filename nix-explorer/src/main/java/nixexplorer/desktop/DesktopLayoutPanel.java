//package nixexplorer.desktop;
//
//import java.awt.Color;
//import java.awt.Component;
//import java.awt.Container;
//import java.awt.Dimension;
//import java.awt.LayoutManager;
//import java.awt.event.FocusEvent;
//import java.awt.event.FocusListener;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FilenameFilter;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.UUID;
//
//import javax.swing.Box;
//import javax.swing.ButtonGroup;
//import javax.swing.Icon;
//import javax.swing.JComponent;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JRadioButton;
//import javax.swing.UIManager;
//
//import nixexplorer.App;
//import nixexplorer.registry.PluginEntry;
//import nixexplorer.registry.PluginRegistry;
//import nixexplorer.registry.PluginShortcutEntry;
//import nixexplorer.registry.PluginShortcutRegistry;
//import nixexplorer.widgets.dnd.DesktopPanelTransferHandler;
//import nixexplorer.widgets.util.Utility;
//
//public class DesktopLayoutPanel extends JPanel implements LayoutManager {
//
//	private Box boxBreadCrumbs;
//	private List<JComponent> items;
//	private boolean layoutHorizontally = false;
//	private int itemWidth = Utility.toPixel(128);
//	private int itemHeight = Utility.toPixel(96);
//	private ButtonGroup btnGroup;
//	private JRadioButton[] radArr;
//	private DesktopPanel desktop;
//	private int itemPerScreen;
//	private int displayIndex = 0;
//	private File desktopDir;
//
//	public DesktopLayoutPanel(DesktopPanel desktop) {
//		this.desktop = desktop;
//		loadShortcuts();
////		items = new JButton[50];
////		for (int i = 0; i < items.length; i++) {
////			items[i] = new JButton("Button " + (i + 1));
////			items[i].setPreferredSize(new Dimension(itemWidth, itemHeight));
////		}
//		boxBreadCrumbs = Box.createHorizontalBox();
//		btnGroup = new ButtonGroup();
//		setLayout(this);
//		setTransferHandler(new DesktopPanelTransferHandler(this));
//	}
//
//	@Override
//	public void addLayoutComponent(String name, Component comp) {
//	}
//
//	@Override
//	public void removeLayoutComponent(Component comp) {
//	}
//
//	@Override
//	public Dimension preferredLayoutSize(Container parent) {
//		return new Dimension(Utility.toPixel(50), Utility.toPixel(50));
//	}
//
//	@Override
//	public Dimension minimumLayoutSize(Container parent) {
//		return new Dimension(Utility.toPixel(50), Utility.toPixel(50));
//	}
//
//	@Override
//	public void layoutContainer(Container parent) {
//		boolean changed = false;
//		System.out.println("Layout: " + getBounds());
//		removeAll();
//		boxBreadCrumbs.removeAll();
//		btnGroup = new ButtonGroup();
//		int w = getWidth();
//		int h = getHeight() - Utility.toPixel(15);
//
//		System.out.println("height: " + getHeight() + " container height: "
//				+ parent.getHeight());
//
//		int itemPerScreen1 = (w / itemWidth) * (h / itemHeight);// (w * h) /
//		// (itemWidth *
//		// itemHeight);
//		if (this.itemPerScreen != itemPerScreen1) {
//			changed = true;
//			this.itemPerScreen = itemPerScreen1;
//			this.displayIndex = 0;
//		}
//		if (itemPerScreen < 1) {
//			return;
//		}
////		System.out.println("Re: " + Math.ceil(items.length / itemPerScreen)
////				+ " " + (items.length / itemPerScreen));
//		int screenRequired = (int) Math
//				.ceil((float) items.size() / itemPerScreen);
//
//		System.out.println("itemPerScreen: " + itemPerScreen
//				+ " screenRequired: " + screenRequired);
//
//		if (screenRequired > 1) {
//			boxBreadCrumbs.add(Box.createHorizontalGlue());
//			radArr = new JRadioButton[screenRequired];
//			for (int i = 0; i < radArr.length; i++) {
//				radArr[i] = new JRadioButton();
//				if (i == displayIndex) {
//					radArr[i].setSelected(true);
//				}
//				radArr[i].setBackground(Color.RED);
//				radArr[i].putClientProperty("screen.index", i);
//				radArr[i].addActionListener(e -> {
//					if (e.getSource() instanceof JRadioButton) {
//						JRadioButton rad = (JRadioButton) e.getSource();
//						if ((rad).isSelected()) {
//							Integer index = (Integer) rad
//									.getClientProperty("screen.index");
//							if (index != null) {
//								this.displayIndex = index;
//								revalidate();
//								repaint();
//							}
//						}
//					}
//				});
//				boxBreadCrumbs.add(radArr[i]);
//				btnGroup.add(radArr[i]);
//			}
//			boxBreadCrumbs.add(Box.createHorizontalGlue());
//			boxBreadCrumbs.setBounds(0, h, w, Utility.toPixel(15));
//			add(boxBreadCrumbs);
//		}
//
//		displayItems(this.displayIndex);
//	}
//
//	private void displayItems(int screenIndex) {
//		System.out.println("screenIndex: " + screenIndex + " itemPerScreen: "
//				+ itemPerScreen);
//		int x = 0;
//		int y = 0;
//		for (int i = screenIndex * itemPerScreen, j = 0; i < items.size()
//				&& j < itemPerScreen; i++, j++) {
//			JComponent btn = items.get(i);// [i];
//			btn.setBounds(x, y, btn.getPreferredSize().width,
//					btn.getPreferredSize().height);
//			add(btn);
//			if (layoutHorizontally) {
//				x += btn.getPreferredSize().width;
//
//				if (x + btn.getPreferredSize().width > getWidth()) {
//					x = 0;
//					y += btn.getPreferredSize().height;
//				}
//			} else {
//				y += btn.getPreferredSize().height;
//				if (y + btn.getPreferredSize().height > getHeight()
//						- Utility.toPixel(15)) {
//					y = 0;
//					x += btn.getPreferredSize().width;
//				}
//			}
//		}
//
//		System.out.println("x: " + x + " y: " + y);
//	}
//
//	private Icon getIcon(String className) {
//		for (PluginEntry e : PluginRegistry.getSharedInstance()
//				.getPluginList()) {
//			if (e.getClassName().equals(className)) {
//				return e.getIcon();
//			}
//		}
//		return null;
//	}
//
//	private void addEntry(String text, Icon icon, PluginShortcutEntry ent) {
//		JLabel btn = new JLabel(text);
//		btn.setBackground(UIManager.getColor("Button.highlight"));
//		btn.setFocusable(true);
////		btn.setBorderPainted(false);
//		btn.setIcon(icon);
//		btn.addFocusListener(new FocusListener() {
//			@Override
//			public void focusLost(FocusEvent e) {
//				System.out.println("Focus lost");
//				btn.setOpaque(false);
//				btn.paintImmediately(0, 0, btn.getWidth(), btn.getHeight());
//			}
//
//			@Override
//			public void focusGained(FocusEvent e) {
//				System.out.println("Focus gained");
//				btn.setOpaque(true);
//				btn.paintImmediately(0, 0, btn.getWidth(), btn.getHeight());
//			}
//		});
//		btn.setVerticalTextPosition(JLabel.BOTTOM);
//		btn.setHorizontalTextPosition(JLabel.CENTER);
//		btn.setHorizontalAlignment(JLabel.CENTER);
//		btn.setPreferredSize(new Dimension(itemWidth, itemHeight));
//		btn.putClientProperty("shortcut.entry", ent);
//		btn.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				btn.requestFocusInWindow();
//				if (e.getClickCount() == 2) {
//					Map<String, Object> env = new HashMap<>();
//					PluginShortcutEntry ent = (PluginShortcutEntry) btn
//							.getClientProperty("shortcut.entry");
//					desktop.createWidget(ent.getClassName(), env,
//							ent.getCommand(), null);
//				}
//			}
//		});
//		items.add(btn);
//	}
//
//	private void loadShortcuts() {
//		items = new ArrayList<>();
//		try {
//			for (PluginShortcutEntry ent : PluginShortcutRegistry.getList()) {
//				addEntry(ent.getName(), getIcon(ent.getClassName()), ent);
////				JButton btn = new JButton(ent.toString());
////				btn.setBorderPainted(false);
////				btn.setIcon(ent.getIcon());
////				btn.setVerticalTextPosition(JButton.BOTTOM);
////				btn.setHorizontalTextPosition(JButton.CENTER);
////				btn.setPreferredSize(new Dimension(itemWidth, itemHeight));
////				items.add(btn);
//			}
////			for (int i = 0; i < PluginLauncherRegistry.getList().size(); i++) {
////				PluginLauncherEntry ent = PluginLauncherRegistry.getList()
////						.get(i);
////				if (ent.isShowOnDesktop()) {
////					JButton btn = new JButton(ent.toString());
////					btn.setBorderPainted(false);
////					btn.setIcon(ent.getIcon());
////					btn.setVerticalTextPosition(JButton.BOTTOM);
////					btn.setHorizontalTextPosition(JButton.CENTER);
////					btn.setPreferredSize(new Dimension(itemWidth, itemHeight));
////					items.add(btn);
////				}
////			}
//			desktopDir = new File((String) App.getConfig("temp.dir"),
//					desktop.getInfo().getId());
//			if (desktopDir.exists()) {
//				File files[] = desktopDir.listFiles(new FilenameFilter() {
//
//					@Override
//					public boolean accept(File dir, String name) {
//						return (name.endsWith(".shortcut"));
//					}
//				});
//				for (File f : files) {
//					try (InputStream in = new FileInputStream(f)) {
//						Properties props = new Properties();
//						props.load(in);
//						PluginShortcutEntry ent = new PluginShortcutEntry(
//								props.getProperty("widget.className"),
//								props.getProperty("widget.args", "").split(","),
//								props.getProperty("widget.name"));
//						addEntry(ent.getName(), getIcon(ent.getClassName()),
//								ent);
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void addAndSaveEntry(PluginShortcutEntry ent) {
//		addEntry(ent.getName(), getIcon(ent.getClassName()), ent);
//		System.out.println("Args: " + Arrays.asList(ent.getCommand()));
//		Properties prop = new Properties();
//		prop.put("widget.className", ent.getClassName());
//		prop.put("widget.args", ent.getCommand() == null ? ""
//				: String.join(",", Arrays.asList(ent.getCommand())));
//		prop.put("widget.name", ent.getName());
//		if (desktopDir.exists()) {
//			File f = new File(desktopDir,
//					UUID.randomUUID().toString() + ".shortcut");
//			try (FileOutputStream fs = new FileOutputStream(f)) {
//				prop.store(fs, "shortcut details");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//	}
//
//}

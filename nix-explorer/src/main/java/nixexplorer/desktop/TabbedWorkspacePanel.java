//package nixexplorer.desktop;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Component;
//import java.awt.Dimension;
//import java.awt.Font;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.GridLayout;
//import java.awt.Insets;
//import java.awt.event.ActionListener;
//import java.io.File;
//import java.lang.reflect.Constructor;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import javax.swing.Box;
//import javax.swing.Icon;
//import javax.swing.JButton;
//import javax.swing.JComboBox;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JTabbedPane;
//import javax.swing.border.EmptyBorder;
//import javax.swing.border.LineBorder;
//
//import nixexplorer.App;
//import nixexplorer.TextHolder;
//import nixexplorer.app.components.FlatTabbedPane;
//import nixexplorer.app.components.FlatTabbedPane.TabListener;
//import nixexplorer.app.session.SessionInfo;
//import nixexplorer.drawables.icons.ScaledIcon;
//import nixexplorer.widgets.Widget;
//import nixexplorer.widgets.component.GradientPanel;
//import nixexplorer.widgets.sessionmgr.SessionManagerPanel;
//import nixexplorer.widgets.util.Utility;
//
//public class TabbedWorkspacePanel extends JPanel {
//	private static final long serialVersionUID = -1409650657684488042L;
//	private FlatTabbedPane tabs;
//
//	public enum AppType {
//		Terminal, LocalFolderBrowser, RemoteFolderBrowser
//	}
//
//	public TabbedWorkspacePanel() {
//		createUI();
//	}
//
////	private Component createStyledButton(Icon icon, String title, String desc,
////			ActionListener e) {
//////		JPanel p = new JPanel(new BorderLayout());
//////		p.setOpaque(false);
////		// p.add(new JLabel(icon), BorderLayout.WEST);
////		String html = String.format("<html><b>%s</b><br/><p>%s</p></html>",
////				title, desc);
////		JButton btn = new JButton(html);
////		btn.setBorderPainted(false);
////		btn.setIcon(icon);
////		btn.setForeground(new Color(30, 30, 30));
////		btn.setBackground(Color.WHITE);
////		btn.addActionListener(e);
////		// p.add(btn);
////		return btn;
////	}
//
//	private Icon getIcon(String name) {
//		return new ScaledIcon(App.class.getResource("/images/" + name),
//				Utility.toPixel(128), Utility.toPixel(128));
//	}
//
//	private JButton createButton(String text, String icon) {
//		JButton btn1 = new JButton(text);
//		btn1.setIcon(getIcon(icon));
//		btn1.setHorizontalAlignment(JButton.CENTER);
//		btn1.setHorizontalTextPosition(JButton.CENTER);
//		btn1.setVerticalTextPosition(JButton.BOTTOM);
//		// btn1.setBackground(Color.GRAY);
//		// btn1.setFont(new Font(Font.DIALOG, Font.PLAIN, Utility.toPixel(24)));
////		btn1.setMargin(new Insets(Utility.toPixel(10), Utility.toPixel(10),
////				Utility.toPixel(10), Utility.toPixel(10)));
//		return btn1;
//	}
//
//	private JPanel createHomePage() {
//		JPanel holder = new JPanel();
//		holder.setOpaque(false);
//		// holder.setBorder(new LineBorder(Color.BLACK, Utility.toPixel(2)));
//
//		JLabel lblTitle = new JLabel("Nix explorer");
//		lblTitle.setIcon(getIcon("cube1.png"));
//		// lblTitle.setForeground(Color.DARK_GRAY);
//		lblTitle.setHorizontalAlignment(JLabel.CENTER);
//		lblTitle.setBorder(new EmptyBorder(Utility.toPixel(30),
//				Utility.toPixel(30), Utility.toPixel(0), Utility.toPixel(30)));
//		lblTitle.setFont(
//				new Font(Font.DIALOG, Font.PLAIN, Utility.toPixel(48)));
//
////		GradientPanel page = new GradientPanel();
////		page.setLayout(new BorderLayout());
////		page.setColor1(new Color(230, 230, 230));
////		page.setColor2(new Color(150, 150, 150));
//
//		JPanel page = new JPanel(new BorderLayout());
//
//		page.add(lblTitle, BorderLayout.NORTH);
////		JPanel holder=new JPanel();
////		holder.setBackground(Color.GRAY);
//
//		JPanel buttonPanel = new JPanel();
//		buttonPanel.setOpaque(false);
//
//		JButton btn1 = createButton("Connect to servers", "server.png");
//
//		JButton btn2 = createButton("Settings", "settings.png");
//
//		JButton btn3 = createButton("Help and support", "help.png");
//
//		int w = Math.max(btn1.getPreferredSize().width,
//				btn2.getPreferredSize().width);
//		w = Math.max(w, btn3.getPreferredSize().width);
//
//		int h = Math.max(btn1.getPreferredSize().height,
//				btn2.getPreferredSize().height);
//		h = Math.max(h, btn3.getPreferredSize().height);
//
//		Dimension d = new Dimension(w, h);
//
//		btn1.setPreferredSize(d);
//		btn2.setPreferredSize(d);
//		btn3.setPreferredSize(d);
//
//		buttonPanel.add(btn1);
//		buttonPanel.add(btn2);
//		buttonPanel.add(btn3);
//
////		JPanel panel = new JPanel(
////				new GridLayout(2, 2, Utility.toPixel(15), Utility.toPixel(15)));
////
////		panel.setBorder(new LineBorder(Color.WHITE, Utility.toPixel(25)));
////		panel.setBackground(Color.WHITE);
////		panel.setPreferredSize(
////				new Dimension(Utility.toPixel(640), Utility.toPixel(300)));
////		panel.setMaximumSize(
////				new Dimension(Utility.toPixel(640), Utility.toPixel(300)));
////		holder.setLayout(new GridBagLayout());
////		holder.add(panel, new GridBagConstraints());
////
////		panel.add(createStyledButton(getIcon("server.png"), "Manage servers",
////				"Connect, manage and configure remote servers", e -> {
////					AppSessionPanel.getsharedInstance().newWorkspace();
////				}));
////		panel.add(createStyledButton(getIcon("settings.png"), "Settings",
////				"Configure various options for SSH, SFTP and more", e -> {
////
////				}));
////		panel.add(createStyledButton(getIcon("help.png"), "Help and support",
////				"Quick guide, tutials and videos about using the app", e -> {
////
////				}));
////		panel.add(createStyledButton(getIcon("cube1.png"), "Manage servers",
////				"Connect, manage and configure remote servers", e -> {
////
////				}));
////		panel.add(createStyledButton(getIcon("cube1.png"), "Manage servers",
////				"Connect, manage and configure remote servers", e -> {
////
////				}));
////		panel.add(createStyledButton(getIcon("cube1.png"), "Manage servers",
////				"Connect, manage and configure remote servers", e -> {
////
////				}));
////		panel.add(createStyledButton(getIcon("cube1.png"), "Manage servers",
////				"Connect, manage and configure remote servers", e -> {
////
////				}));
//		page.add(buttonPanel);
//		return page;
//	}
//
//	private void createUI() {
//		setLayout(new BorderLayout());
//		tabs = new FlatTabbedPane();
//		tabs.setClosable(true);
//		// tabs.setBackground(new Color(230, 230, 230));
//		// tabs.setTabColor(new Color(180, 180, 180));
//		tabs.setCloseIcon(
//				new ScaledIcon(getClass().getResource("/images/dark_close.png"),
//						Utility.toPixel(16), Utility.toPixel(16)));
//		// tabs.setTextColor(Color.DARK_GRAY);
//		tabs.setTabListener(new TabListener() {
//
//			@Override
//			public void tabClosed(int index, Component c) {
//				if (c instanceof DesktopPanel) {
//					((DesktopPanel) c).closeSession();
//				}
//			}
//
//			@Override
//			public void allTabClosed() {
//
//			}
//		});
//		add(tabs);
//		tabs.addTab(TextHolder.getString("workspace.home"), createHomePage(),
//				true);
//		// newWorkspace();
//	}
//
//	public void newWorkspace() {
//		SessionInfo info = new SessionManagerPanel().newSession();
//		if (info != null) {
//			File desktopDir = new File((String) App.getConfig("temp.dir"),
//					info.getId());
//			if (!desktopDir.exists()) {
//				desktopDir.mkdir();
//			}
//			DesktopPanel w = new DesktopPanel(info);
//			tabs.addTab(info.getName(), w, true);
//		}
//	}
//
//	public void addWidget(String className, Map<String, Object> env,
//			String[] args, Widget widget) {
//
//		Component c = tabs.getSelectedComponent();
//		if (c instanceof DesktopPanel) {
//			DesktopPanel tt = (DesktopPanel) c;
//			tt.createWidget(className, env, args, widget);
//		}
//	}
//
////	public void addDialogWidget(Widget c, String title, Dimension d,
////			Component parent) {
////		Component ct = tabs.getSelectedComponent();
////		if (ct instanceof DesktopPanel) {
////			DesktopPanel tt = (DesktopPanel) ct;
////			tt.createDialog(c, title, d, parent);
////		}
////	}
//
////	public void newFileBrowser(boolean remote) {
////		TabbedFolderViewWidget w = new TabbedFolderViewWidget(
////				remote ? wrapper : null,
////				remote ? null : wrapper.getInfo().getLocalFolder());
////		if (tabs.getSelectedIndex() != -1) {
////			TilingTabbedPane tt = (TilingTabbedPane) tabs
////					.getComponentAt(tabs.getSelectedIndex());
////			tt.addComponent(w, "Dummy");
////		}
////	}
////
////	public void newTerminal(String cmd) {
////		TabbedConsoleWidget w = new TabbedConsoleWidget(wrapper, cmd);
////		if (tabs.getSelectedIndex() != -1) {
////			TilingTabbedPane tt = (TilingTabbedPane) tabs
////					.getComponentAt(tabs.getSelectedIndex());
////			tt.addComponent(w, "Dummy");
////		}
////	}
//}

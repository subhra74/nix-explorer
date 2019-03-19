//package nixexplorer.desktop;
//
//import java.awt.BorderLayout;
//import java.awt.Component;
//import java.awt.Dimension;
//import java.awt.Point;
//import java.awt.event.ComponentAdapter;
//import java.awt.event.ComponentEvent;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.beans.PropertyVetoException;
//import java.lang.reflect.Constructor;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.concurrent.ConcurrentHashMap;
//
//import javax.swing.Icon;
//import javax.swing.JButton;
//import javax.swing.JDesktopPane;
//import javax.swing.JInternalFrame;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JPopupMenu;
//import javax.swing.UIManager;
//
//import nixexplorer.Constants;
//import nixexplorer.TextHolder;
//import nixexplorer.app.components.TaskbarLayout;
//import nixexplorer.core.FileType;
//import nixexplorer.app.session.SessionInfo;
//import nixexplorer.core.ssh.filetransfer.FileTransfer;
//import nixexplorer.core.ssh.filetransfer.TransferQueue;
//import nixexplorer.core.ssh.filetransfer.local2remote.ExternalEditorHelper;
//import nixexplorer.widgets.AbstractModalDialog;
//import nixexplorer.widgets.Widget;
//import nixexplorer.widgets.component.CustomDesktopManager;
//import nixexplorer.widgets.component.ImageBackgroundPanel;
//import nixexplorer.widgets.listeners.AppEventListener;
//import nixexplorer.widgets.listeners.AppMessageListener;
//import nixexplorer.widgets.listeners.TransferWatcher;
//import nixexplorer.widgets.util.Utility;
//import nixexplorer.worker.editwatcher.ChangeUploader;
//import nixexplorer.worker.editwatcher.EditWatcher;
//
//public class DesktopPanel extends JPanel implements AppMessageListener {
//	private static final long serialVersionUID = 4713362016541905446L;
//	private JDesktopPane desktop;
//	private JPanel taskbar;
//	private SessionInfo info;
//	private JButton btnStart;
//	private JPanel ribbon;
//	private TaskbarLayout ribbonLayout;
//	private TransferQueue bgTransferQueue;
//	private TransferQueue editTransferQueue;
//	private ExternalEditorHelper editorHelper;
//	private EditWatcher editWatcher;
//	private Map<Integer, List<SignalHandler>> signalMap = new ConcurrentHashMap<>();
//	private Map<String, ChangeUploader> editWatchers = new ConcurrentHashMap<>();
//
//	private List<AppEventListener> appListeners = Collections
//			.synchronizedList(new ArrayList<>());
//
//	private JPopupMenu transferPopup, startPopup;
//	private TransferOverviewPanel transferPanel;
//
//	private JLabel btnTransfers;
//
//	private ImageBackgroundPanel desktopPanel;
//
//	public enum TaskbarOrientation {
//		Top, Bottom, Left, Right
//	}
//
//	private TaskbarOrientation taskbarOrientation;
//
//	int taskbarBackground, taskbarForeground, taskbarThikness;
//
////	private DefaultListModel<PluginLauncherEntry> desktopModel;
////	private JList<PluginLauncherEntry> desktopList;
//
//	public DesktopPanel(SessionInfo info) {
//		this.info = info;
//		this.info.setContainterId(this.hashCode());
////		setBackground(Color.green);
//		setLayout(new BorderLayout());
//		desktop = new JDesktopPane();
//		desktop.setOpaque(false);
//		desktop.setBorder(null);
//		CustomDesktopManager mgr = new CustomDesktopManager();
//		mgr.setDesktop(desktop);
//		desktop.setDesktopManager(mgr);
//
//		bgTransferQueue = new TransferQueue(1);
//		editTransferQueue = new TransferQueue(5);
//
//		editorHelper = new ExternalEditorHelper(info, this);
//		Map<String, Object> env = new HashMap<>();
//		env.put("sftp.session", info);
//		env.put("app.msglistener", this);
//		env.put("app.desktop", this);
//		editWatcher = new EditWatcher(info, env, new String[] {});
//
////		JInternalFrame iframe = new JInternalFrame("Frame");
////		iframe.setSize(400, 300);
////		iframe.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
////		desktop.add(iframe, JDesktopPane.PALETTE_LAYER);
////		iframe.show();
//
////		JScrollPane jsp = new JScrollPane(desktop);
////		jsp.setBorder(null);
//		add(desktop);
//
//		createOrUpdateTaskbar();
//
//		DesktopLayoutPanel desktopGrid = new DesktopLayoutPanel(this);
//		desktopGrid.setOpaque(false);
//
////		JPanel fencedPanel = new JPanel(null);
////		fencedPanel.setBackground(new Color(20, 136, 198));
////		JPanel p1 = new FencedPanel();
////		p1.setBackground(Color.black);
////		p1.setBounds(50, 50, 400, 400);
////		fencedPanel.add(p1);
//
//		desktop.addComponentListener(new ComponentAdapter() {
//			@Override
//			public void componentResized(ComponentEvent e) {
//				desktopGrid.setSize(desktop.getSize());
//				desktopGrid.revalidate();
//				desktopGrid.repaint();
//			}
//		});
////
////		desktopPanel = new ImageBackgroundPanel(new BorderLayout());// (new
////																	// BorderLayout());
////		desktopPanel.setSize(desktop.getSize());
////		desktopPanel.setImage(
////				getClass().getResource("/images/backgrounds/solid1.png"));
//		// desktopPanel.setImage(getClass().getResource("/images/backgrounds/solid.png"));
//		// desktopPanel.setImage(getClass().getResource("/images/backgrounds/orange.jpg"));
//		// desktopPanel.setImage(getClass().getResource("/images/backgrounds/12.jpg"));
////		desktopPanel.setImage(getClass().getResource("/images/backgrounds/matrix.jpg"));
////		desktopPanel.setImage(getClass().getResource("/images/backgrounds/shapes-wallpapers-25299-58198.jpg"));
////		desktopPanel.setImage(getClass().getResource("/images/backgrounds/landscape.jpg"));
////		desktopPanel.setImage(getClass().getResource("/images/backgrounds/envelope-2560x1440-minimal-blue-hd-4k-9483.jpg_.jpg"));
////		desktopPanel.setImage(getClass().getResource("/images/backgrounds/22258532.jpg"));
////		desktopPanel.setImage(getClass().getResource("/images/backgrounds/167183-anime-landscape.jpg"));
//		// desktop.add(desktopPanel);
//
////		desktopModel = new DefaultListModel<>();
////		createShortcuts();
////		desktopList = new JList<>(desktopModel);
////		desktopList.setBackground(UIManager.getColor("Desktop.background"));
////		desktopList.setForeground(UIManager.getColor("Desktop.foreground"));
////
////		desktopList.setLayoutOrientation(JList.VERTICAL_WRAP);
////		desktopList.setOpaque(false);
////		desktopList.addMouseListener(new MouseAdapter() {
////			@Override
////			public void mouseClicked(MouseEvent e) {
////				if (e.getClickCount() == 2) {
////					int x = desktopList.getSelectedIndex();
////					if (x != -1) {
////						PluginLauncherEntry item = desktopModel.getElementAt(x);
////						Map<String, Object> env = new HashMap<>();
////						createWidget(item.getClassName(), env,
////								item.getCommands(), null);
////					}
////				}
////			}
////		});
////		desktopList.setFixedCellWidth(Utility.toPixel(96));
////		desktopList.setFixedCellHeight(Utility.toPixel(96));
////		desktopList.setVisibleRowCount(-1);
////		desktopList.setCellRenderer(new ListViewItemRenderer(
////				Utility.toPixel(96), Utility.toPixel(48), Utility.toPixel(48)));
////		desktopPanel.add(desktopList);
//		desktop.add(desktopGrid);
//		new Thread(editWatcher).start();
//	}
//
////	private void createShortcuts() {
////		try {
////			for (int i = 0; i < PluginLauncherRegistry.getList().size(); i++) {
////				PluginLauncherEntry ent = PluginLauncherRegistry.getList()
////						.get(i);
////				if (ent.isShowOnDesktop()) {
////					desktopModel.addElement(ent);
////				}
////			}
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////
////	}
//
//	private void createStartButton() {
//		Icon icon = null;
//		try {
//			icon = UIManager.getIcon("Desktop.menu");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		btnStart = new JButton(icon);
//		btnStart.setBorderPainted(false);
//		btnStart.setFocusPainted(false);
//		btnStart.setContentAreaFilled(false);
//		btnStart.addActionListener(e -> {
//			showStartMenu();
//		});
//	}
//
//	public void closeStartMenu() {
//		if (startPopup.isShowing()) {
//			startPopup.setVisible(false);
//		}
//	}
//
//	private void createTransferButton() {
//		btnTransfers = new JLabel(TextHolder.getString("toolbar.transfers"));
////		btnTransfers.setPreferredSize(
////				new Dimension(Utility.toPixel(taskbarThikness),
////						Utility.toPixel(taskbarThikness)));
//
//		btnTransfers.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				showTransferPanel();
//			}
//		});
//		transferPanel = new TransferOverviewPanel(this);
//		transferPanel.putClientProperty("label", btnTransfers);
//		transferPopup = new JPopupMenu();
//		transferPopup.setLayout(new BorderLayout());
//		transferPopup.add(transferPanel);
//	}
//
//	private void loadTaksbarConfig() {
//		taskbarOrientation = TaskbarOrientation.Bottom;
//		taskbarThikness = UIManager.getInt("Taskbar.height");
//	}
//
//	private void createOrUpdateTaskbar() {
//
//		loadTaksbarConfig();
//
//		if (btnStart == null) {
//			createStartButton();
//		}
//
//		if (btnTransfers == null) {
//			createTransferButton();
//		}
//
//		if (ribbon == null) {
//			ribbonLayout = new TaskbarLayout(taskbarOrientation,
//					taskbarThikness);
//			ribbon = new JPanel(ribbonLayout);
//			ribbon.setOpaque(false);
//		} else {
//			ribbonLayout.setOrientation(taskbarOrientation);
//		}
//
//		if (taskbar != null)
//			this.remove(taskbar);
//		JPanel taskBarNew = new JPanel();
//		taskBarNew.setOpaque(true);// .setBackground(Color.WHITE);
//		taskBarNew.setLayout(new BorderLayout());
//		// JPanel taskBarNew = new JPanel(new BorderLayout());
//		// taskBarNew.setBorder(UIManager.getBorder("TaskBar.border"));
//		// taskBarNew.setBackground(UIManager.getColor("TaskBar.background"));
//		if (taskbarOrientation == TaskbarOrientation.Top
//				|| taskbarOrientation == TaskbarOrientation.Bottom) {
//			taskBarNew.setMaximumSize(
//					new Dimension(getMaximumSize().width, taskbarThikness));
//			taskBarNew.add(btnStart, BorderLayout.WEST);
//			taskBarNew.add(btnTransfers, BorderLayout.EAST);
//			btnStart.setPreferredSize(new Dimension(
//					taskbarThikness + Utility.toPixel(10), taskbarThikness));
//		} else {
//			taskBarNew.setMaximumSize(new Dimension(taskbarThikness,
//					getMaximumSize().height + Utility.toPixel(10)));
//			taskBarNew.add(btnStart, BorderLayout.NORTH);
//			taskBarNew.add(btnTransfers, BorderLayout.SOUTH);
//			btnStart.setPreferredSize(
//					new Dimension(taskbarThikness, taskbarThikness));
//		}
//
//		for (int i = 0; i < ribbon.getComponentCount(); i++) {
//			TaskbarButton btn = (TaskbarButton) ribbon.getComponent(i);
//			fixTaskButton(btn);
//		}
//
////		Box b = (taskbarOrientation == TaskbarOrientation.Top || taskbarOrientation == TaskbarOrientation.Bottom)
////				? Box.createHorizontalBox()
////				: Box.createVerticalBox();
//		taskBarNew.add(ribbon);
//
////		for (Component c : ribbon.getComponents()) {
////			b.add(c);
////		}
////
////		this.ribbon = b;
////
////		taskBarNew.add(this.ribbon);
//		this.taskbar = taskBarNew;
//		if (taskbarOrientation == TaskbarOrientation.Top) {
//			this.add(taskbar, BorderLayout.NORTH);
//		} else if (taskbarOrientation == TaskbarOrientation.Bottom) {
//			this.add(taskbar, BorderLayout.SOUTH);
//		} else if (taskbarOrientation == TaskbarOrientation.Left) {
//			this.add(taskbar, BorderLayout.WEST);
//		} else if (taskbarOrientation == TaskbarOrientation.Right) {
//			this.add(taskbar, BorderLayout.EAST);
//		}
//
//		this.revalidate();
//		this.repaint();
//	}
//
//	private void createStartMenu() {
////		JMenuItem localBrowserMenu = new JMenuItem(
////				TextHolder.getString("toolbar.localFileBrowser"));
////		localBrowserMenu.addActionListener(e -> {
////			Map<String, Object> env = new HashMap<>();
////			createWidget("nixexplorer.widgets.folderview.LocalFolderViewWidget",
////					env, new String[] {}, null);
////		});
////		JMenuItem remoteBrowserMenu = new JMenuItem(
////				TextHolder.getString("toolbar.remoteFileBrowser"));
////		remoteBrowserMenu.addActionListener(e -> {
////			Map<String, Object> env = new HashMap<>();
////			createWidget(
////					"nixexplorer.widgets.folderview.RemoteFolderViewWidget",
////					env, new String[] {}, null);
////		});
////		JMenuItem sshTerminalMenu = new JMenuItem(
////				TextHolder.getString("toolbar.terminal"));
////		sshTerminalMenu.addActionListener(e -> {
////			Map<String, Object> env = new HashMap<>();
////			createWidget("nixexplorer.widgets.console.TabbedConsoleWidget", env,
////					new String[] {}, null);
////		});
////		JMenuItem systemMonitorMenu = new JMenuItem(
////				TextHolder.getString("toolbar.sysmon"));
////		systemMonitorMenu.addActionListener(e -> {
////			Map<String, Object> env = new HashMap<>();
////			createWidget("nixexplorer.widgets.sysmon.SystemMonitorWidget", env,
////					new String[] {}, null);
////		});
////		JMenuItem fileUploaderMenu = new JMenuItem(
////				TextHolder.getString("filetransfer.title"));
////		fileUploaderMenu.addActionListener(e -> {
////			Map<String, Object> env = new HashMap<>();
////			createWidget("nixexplorer.widgets.filetransfer.FileTransferWidget",
////					env, new String[] {}, null);
////		});
////
////		startPopup = new JPopupMenu();
////		startPopup.add(localBrowserMenu);
////		startPopup.add(remoteBrowserMenu);
////		startPopup.add(sshTerminalMenu);
////		startPopup.add(systemMonitorMenu);
////		startPopup.add(fileUploaderMenu);
////
////		startPopup.pack();
//
//		btnStart.addActionListener(e -> {
//			showStartMenu();
//		});
//	}
//
//	public void createDialog(JInternalFrame frame) {
//
//		// frame.pack();
//
//		if ("center".equals(frame.getClientProperty("dialog.location"))) {
//			JInternalFrame parent = (JInternalFrame) frame
//					.getClientProperty("dialog.parent");
//			if (parent != null && parent.isVisible()) {
//				Point p = parent.getLocation();
//				Dimension d = parent.getSize();
//				int dx = d.width / 2 - frame.getWidth() / 2;
//				int dy = d.height / 2 - frame.getHeight() / 2;
//				frame.setLocation(p.x + dx, p.y + dy);
//			} else {
//				Dimension d = getSize();
//				int dx = d.width / 2 - frame.getWidth() / 2;
//				int dy = d.height / 2 - frame.getHeight() / 2;
//				frame.setLocation(dx, dy);
//			}
//
//			// System.out.println("dialog-Location: " + frame.getLocation());
//		}
//
//		desktop.add(frame);
//		frame.show();
//	}
//
//	private void fixTaskButton(TaskbarButton taskbarBtn) {
//		if (taskbarOrientation == TaskbarOrientation.Top
//				|| taskbarOrientation == TaskbarOrientation.Bottom) {
//			taskbarBtn.setPreferredSize(
//					new Dimension(Utility.toPixel(taskbarThikness), // Utility.toPixel(120),
//							Utility.toPixel(taskbarThikness)));
//			taskbarBtn.setShowText(false);
//		} else {
//			taskbarBtn.setPreferredSize(
//					new Dimension(Utility.toPixel(taskbarThikness),
//							Utility.toPixel(taskbarThikness)));
//			taskbarBtn.setShowText(false);
//		}
//	}
//
//	private void createWindow(Widget c) {
//		c.setInfo(info);
//		if (c.isAutoSize()) {
//			Dimension d = desktop.getSize();
//			int width = 0, height = 0;
//			if (d.width < Utility.toPixel(640)) {
//				width = d.width;
//			} else {
//				width = Utility.toPixel(640);
//			}
//			if (d.height < Utility.toPixel(480)) {
//				height = d.height;
//			} else {
//				height = Utility.toPixel(480);
//			}
//			c.setSize(width, height);
//		} else {
//			c.pack();
//		}
//		if (c.isCentered()) {
//			Dimension desktopSize = desktop.getSize();
//			Dimension jInternalFrameSize = c.getSize();
//			c.setLocation((desktopSize.width - jInternalFrameSize.width) / 2,
//					(desktopSize.height - jInternalFrameSize.height) / 2);
//		}
//
//		if (c.isDialog()) {
//			c.setIconifiable(false);
//			c.setMaximizable(false);
//			c.setClosable(true);
//		} else {
//			c.setIconifiable(true);
//			c.setMaximizable(true);
//			c.setClosable(true);
//		}
//
//		desktop.add(c);
//		c.toFront();
//
//		if (!c.isDialog()) {
//			TaskbarButton taskbarBtn = new TaskbarButton(c.getTitle(),
//					c.getFrameIcon(), this);
//			taskbarBtn.setBackground(
//					UIManager.getColor("TaskBar.buttonBackground"));
//			c.putClientProperty("widget.taskbutton", taskbarBtn);
//			fixTaskButton(taskbarBtn);
//			taskbarBtn.putClientProperty("internalFrame", c);
//			taskbarBtn.addMouseListener(new MouseAdapter() {
//				@Override
//				public void mouseClicked(MouseEvent e) {
//					TaskbarButton btn = (TaskbarButton) e.getSource();
//					JInternalFrame frame = (JInternalFrame) btn
//							.getClientProperty("internalFrame");
//					if (frame.getClientProperty("modalChild") != null) {
//						AbstractModalDialog dlg = (AbstractModalDialog) frame
//								.getClientProperty("modalChild");
//						try {
//							frame.setSelected(true);
//						} catch (PropertyVetoException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
//						try {
//							dlg.setSelected(true);
//						} catch (PropertyVetoException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
//						return;
//					}
//					if (!frame.isIcon()) {
//						if (!frame.isSelected()) {
//							try {
//								frame.setSelected(true);
//							} catch (PropertyVetoException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							}
//						} else {
//							try {
//								frame.setIcon(true);
//							} catch (PropertyVetoException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							}
//						}
//					} else {
//						try {
//							frame.setIcon(false);
//							frame.setSelected(true);
//						} catch (PropertyVetoException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
//					}
//				}
//			});
//			ribbon.add(taskbarBtn);
//			ribbon.revalidate();
//			ribbon.repaint();
//		}
//		c.show();
//	}
//
//	public void addWindow(Widget c) {
//		createWindow(c);
//	}
//
//	@Override
//	public void removeAll() {
//		desktop.removeAll();
//	}
//
//	public void removeWindow(Component c) {
//
//	}
//
//	public void createWidget(String className, Map<String, Object> env,
//			String[] args, Widget parent) {
//		try {
//			Class<?> clazz = Class.forName(className);
//			Constructor<?> ctor = clazz.getConstructor(Map.class,
//					String[].class, Widget.class);
//			env.put("sftp.session", info);
//			env.put("app.msglistener", this);
//			env.put("app.desktop", this);
//			Widget widget = (Widget) ctor
//					.newInstance(new Object[] { env, args, parent });
//			createWindow(widget);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	@Override
//	public void notify(TransferStatus status, FileTransfer transfer) {
//		transferPanel.notify(status, transfer);
//		if (status == TransferStatus.Complete) {
//			String host = transfer.getHostName();
//			String src = transfer.getSourceFileName();
//			String dst = transfer.getTargetFileName();
//
//			Properties data = new Properties();
//			data.put("download.host", host);
//			data.put("download.srcfile", src);
//			data.put("download.dstfile", dst);
//			data.put("download.type",
//					transfer.getType() == FileType.Directory ? "folder"
//							: "file");
//			for (AppEventListener l : appListeners) {
//				if (l != null) {
//					l.onEvent(Constants.DOWNLOAD_FINISHED, data);
//				}
//			}
//		}
//	}
//
//	@Override
//	public void registerAppEventListener(AppEventListener l) {
//		this.appListeners.add(l);
//	}
//
//	@Override
//	public void unregisterAppEventListener(AppEventListener l) {
//		this.appListeners.remove(l);
//	}
//
//	@Override
//	public TransferWatcher getTransferWatcher() {
//		return transferPanel;
//	}
//
//	public void showTransferPanel() {
//		Dimension d = transferPopup.getPreferredSize();
//		if (taskbarOrientation == TaskbarOrientation.Bottom) {
//			transferPopup.show(btnTransfers,
//					-(d.width - btnTransfers.getWidth()), -(int) d.getHeight());
//		} else if (taskbarOrientation == TaskbarOrientation.Top) {
//			transferPopup.show(btnTransfers,
//					-(d.width - btnTransfers.getWidth()),
//					btnTransfers.getHeight());
//		} else if (taskbarOrientation == TaskbarOrientation.Left) {
//			transferPopup.show(btnTransfers, btnTransfers.getWidth(),
//					-(int) d.getHeight() + btnTransfers.getHeight());
//		} else if (taskbarOrientation == TaskbarOrientation.Right) {
//			transferPopup.show(btnTransfers, -(int) d.getWidth(),
//					-(int) d.getHeight() + btnTransfers.getHeight());
//		}
//	}
//
//	public void showStartMenu() {
//		if (startPopup == null) {
//			startPopup = new JPopupMenu();
//			startPopup.setLayout(new BorderLayout());
//			startPopup.add(new StartMenuPanel(this));
//			startPopup.pack();
//		}
//		Dimension d = startPopup.getPreferredSize();
//		if (taskbarOrientation == TaskbarOrientation.Bottom) {
//			startPopup.show(btnStart, 0, -(int) d.getHeight());
//		} else if (taskbarOrientation == TaskbarOrientation.Top) {
//			startPopup.show(btnStart, 0, btnStart.getHeight());
//		} else if (taskbarOrientation == TaskbarOrientation.Left) {
//			startPopup.show(btnStart, btnStart.getWidth(), 0);
//		} else if (taskbarOrientation == TaskbarOrientation.Right) {
//			startPopup.show(btnStart, -(int) d.getWidth(), 0);
//		}
//	}
//
//	public JDesktopPane getDesktop() {
//		return desktop;
//	}
//
//	public TransferQueue getBgTransferQueue() {
//		return bgTransferQueue;
//	}
//
//	public void setBgTransferQueue(TransferQueue bgTransferQueue) {
//		this.bgTransferQueue = bgTransferQueue;
//	}
//
//	public TransferQueue getEditTransferQueue() {
//		return editTransferQueue;
//	}
//
//	public void setEditTransferQueue(TransferQueue editTransferQueue) {
//		this.editTransferQueue = editTransferQueue;
//	}
//
//	public ExternalEditorHelper getEditorHelper() {
//		return editorHelper;
//	}
//
//	public void setEditorHelper(ExternalEditorHelper editorHelper) {
//		this.editorHelper = editorHelper;
//	}
//
//	public EditWatcher getEditWatcher() {
//		return editWatcher;
//	}
//
//	public void closeSession() {
//		this.desktop.setVisible(false);
//		new Thread(() -> {
//			ResourceManager.unregisterAll(this.hashCode());
//		}).start();
//	}
//
//	public void registerSignalHandler(SignalHandler h, Integer topic) {
//		List<SignalHandler> list = signalMap.get(topic);
//		if (list == null) {
//			list = new ArrayList<>();
//			signalMap.put(topic, list);
//		}
//		list.add(h);
//	}
//
//	public void unregisterSignalHandler(SignalHandler h, Integer topic) {
//		List<SignalHandler> list = signalMap.get(topic);
//		if (list != null) {
//			list.remove(h);
//		}
//
//	}
//
//	public void signal(Integer topic, Object data) {
//		List<SignalHandler> list = signalMap.get(topic);
//		if (list != null) {
//			for (SignalHandler h : list) {
//				h.onSignal(topic, data);
//			}
//		}
//	}
//
//	/**
//	 * @return the editWatchers
//	 */
//	public Map<String, ChangeUploader> getEditWatchers() {
//		return editWatchers;
//	}
//
//	/**
//	 * @param editWatchers the editWatchers to set
//	 */
//	public void registerEditWatchers(String file, ChangeUploader editWatcher) {
//		this.editWatchers.put(file, editWatcher);
//		Path path = Paths.get(file).getParent();
//		this.editWatcher.register(path);
//		System.out.println("Registered for changes: " + file + " path: "
//				+ path.toString());
//	}
//
//	public SessionInfo getInfo() {
//		return info;
//	}
//
//	public void setInfo(SessionInfo info) {
//		this.info = info;
//	}
//
//}

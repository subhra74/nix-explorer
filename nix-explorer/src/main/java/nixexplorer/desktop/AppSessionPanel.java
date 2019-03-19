//package nixexplorer.desktop;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Component;
//import java.awt.Dimension;
//import java.awt.KeyboardFocusManager;
//import java.awt.Rectangle;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.concurrent.ConcurrentHashMap;
//
//import javax.swing.Box;
//import javax.swing.DefaultComboBoxModel;
//import javax.swing.JButton;
//import javax.swing.JComboBox;
//import javax.swing.JComponent;
//import javax.swing.JPanel;
//import javax.swing.JPopupMenu;
//
//import nixexplorer.Constants;
//import nixexplorer.TextHolder;
//import nixexplorer.core.FileSystemProvider;
//import nixexplorer.core.FileType;
//import nixexplorer.app.session.SessionInfo;
//import nixexplorer.core.ssh.SshWrapper;
//import nixexplorer.core.ssh.filetransfer.FileTransfer;
//import nixexplorer.widgets.Widget;
//import nixexplorer.widgets.archiver.ArchiveCompressWidget;
//import nixexplorer.widgets.archiver.ArchiveExtractWidget;
//import nixexplorer.widgets.console.TabbedConsoleWidget;
//import nixexplorer.widgets.dnd.TransferFileInfo;
//import nixexplorer.widgets.filetransfer.FileTransferWidget;
//import nixexplorer.widgets.folderview.TabbedFolderViewWidget;
//import nixexplorer.widgets.folderview.local.LocalFolderViewWidget;
//import nixexplorer.widgets.folderview.remote.RemoteFolderViewWidget;
//import nixexplorer.widgets.listeners.AppEventListener;
//import nixexplorer.widgets.listeners.SessionActivityListener;
//import nixexplorer.widgets.listeners.AppMessageListener;
//import nixexplorer.widgets.sessionmgr.SessionManagerPanel;
//import nixexplorer.widgets.sysmon.SystemMonitorWidget;
//import nixexplorer.widgets.util.Utility;
//
//public class AppSessionPanel extends JPanel {
//	private static final long serialVersionUID = 4487407761344766372L;
//	private TabbedWorkspacePanel workspace;
//	private Box toolBar;
//	// private JButton btnNewSession;
//	private JPanel contentPanel;
//	private JButton btnLocalFileBrowser, btnRemoteFileBrowser, btnTerminal,
//			btnWorkspace, btnSysMon, btnFileTransfer;
//	// private Map<Integer, TilingPanel> listenerMap = new
//	// ConcurrentHashMap<>();
//	private List<AppEventListener> appListeners = Collections
//			.synchronizedList(new ArrayList<>());
//	private static AppSessionPanel me;
//	private JPopupMenu transferPopup;
//	private TransferOverviewPanel transferPanel;
//
//	public static synchronized AppSessionPanel getsharedInstance() {
//		if (me == null) {
//			me = new AppSessionPanel();
//			me.createUI();
//			// me.trackFocusChange();
//		}
//		return me;
//	}
//
//	private AppSessionPanel() {
//
//	}
//
//	private void createUI() {
//		setLayout(new BorderLayout());
//		contentPanel = new JPanel(new BorderLayout());
//		add(contentPanel);
//		workspace = new TabbedWorkspacePanel();
//		contentPanel.add(workspace);
////		setActiveTab(tab);
//
////		btnNewSession = new JButton(TextHolder.getString("session.newHost"));
////		btnNewSession.addActionListener(new ActionListener() {
////
////			@Override
////			public void actionPerformed(ActionEvent arg0) {
////				SshWrapper wr = new SessionManagerPanel().newSession();
////				if (wr != null) {
////					TabbedWorkspacePanel tab = new TabbedWorkspacePanel(wr);
////					sessionModel.addElement(tab);
////					add(toolBar, BorderLayout.NORTH);
////					setActiveTab(tab);
////				}
////			}
////		});
//
////		btnNewSession.setFont(Utility.getFont(Constants.LARGE));
////		welcomePanel.add(btnNewSession);
//
////		btnLocalFileBrowser = new JButton(
////				TextHolder.getString("toolbar.localFileBrowser"));
////		btnLocalFileBrowser.addActionListener(new ActionListener() {
////			@Override
////			public void actionPerformed(ActionEvent e) {
////				Map<String, Object> env = new HashMap<>();
////				workspace.addWidget(
////						"nixexplorer.widgets.folderview.LocalFolderViewWidget",
////						env, new String[] {}, null);
////				// workspace.addWidget(widget);
////			}
////		});
////		btnRemoteFileBrowser = new JButton(
////				TextHolder.getString("toolbar.remoteFileBrowser"));
////		btnRemoteFileBrowser.addActionListener(new ActionListener() {
////			@Override
////			public void actionPerformed(ActionEvent e) {
////				Map<String, Object> env = new HashMap<>();
////				workspace.addWidget(
////						"nixexplorer.widgets.folderview.RemoteFolderViewWidget",
////						env, new String[] {}, null);
////			}
////		});
////		btnTerminal = new JButton(TextHolder.getString("toolbar.terminal"));
////		btnTerminal.addActionListener(new ActionListener() {
////			@Override
////			public void actionPerformed(ActionEvent e) {
////				Map<String, Object> env = new HashMap<>();
////				workspace.addWidget(
////						"nixexplorer.widgets.console.TabbedConsoleWidget", env,
////						new String[] {}, null);
////			}
////		});
////		btnWorkspace = new JButton(TextHolder.getString("toolbar.workspace"));
////		btnWorkspace.addActionListener(new ActionListener() {
////			@Override
////			public void actionPerformed(ActionEvent e) {
////				workspace.newWorkspace();
////			}
////		});
////
//////		btnTransfers = new JButton(TextHolder.getString("toolbar.transfers"));
//////		btnTransfers.addActionListener(new ActionListener() {
//////			@Override
//////			public void actionPerformed(ActionEvent e) {
//////				showTransferPanel();
//////			}
//////		});
////		btnSysMon = new JButton(TextHolder.getString("toolbar.sysmon"));
////		btnSysMon.addActionListener(new ActionListener() {
////			@Override
////			public void actionPerformed(ActionEvent e) {
////				Map<String, Object> env = new HashMap<>();
////				workspace.addWidget(
////						"nixexplorer.widgets.sysmon.SystemMonitorWidget", env,
////						new String[] {}, null);
////			}
////		});
////
////		btnFileTransfer = new JButton(
////				TextHolder.getString("filetransfer.title"));
////		btnFileTransfer.addActionListener(new ActionListener() {
////			@Override
////			public void actionPerformed(ActionEvent e) {
////				Map<String, Object> env = new HashMap<>();
////				workspace.addWidget(
////						"nixexplorer.widgets.filetransfer.FileTransferWidget",
////						env, new String[] {}, null);
////			}
////		});
//
////		transferPanel = TransferProgressPanel.getSharedInstance();
////		transferPopup = new JPopupMenu();
////		transferPopup.setLayout(new BorderLayout());
////		transferPopup.add(transferPanel);
//
////		toolBar = Box.createHorizontalBox();
////		toolBar.add(Box.createHorizontalGlue());
////		toolBar.add(btnWorkspace);
////		toolBar.add(btnLocalFileBrowser);
////		toolBar.add(btnRemoteFileBrowser);
////		toolBar.add(btnTerminal);
////		toolBar.add(btnSysMon);
////		toolBar.add(btnFileTransfer);
////		toolBar.add(Box.createHorizontalGlue());
////		//toolBar.add(btnTransfers);
////		toolBar.add(Box.createHorizontalStrut(Utility.toPixel(10)));
//
//		// contentPanel.add(toolBar, BorderLayout.NORTH);
//	}
//
////	@Override
////	public void activityClosed(TabbedWorkspacePanel panel) {
////
////	}
//
////	private void trackFocusChange() {
////		KeyboardFocusManager focusManager = KeyboardFocusManager
////				.getCurrentKeyboardFocusManager();
////		focusManager.addPropertyChangeListener(new PropertyChangeListener() {
////			public void propertyChange(PropertyChangeEvent e) {
////				String prop = e.getPropertyName();
////				if (("focusOwner".equals(prop))
////						&& ((e.getNewValue()) instanceof JComponent)) {
////					JComponent comp = (JComponent) e.getNewValue();
////					while (true) {
////						if (comp.getClientProperty("component.tile") != null) {
////							Integer hashCode = (Integer) comp
////									.getClientProperty("tilePanel.id");
////							if (hashCode != null) {
////								listenerMap.get(hashCode)
////										.setFocusedComponent(comp);
////								System.out.println("Focused tile: " + comp);
////								return;
////							}
////						}
////						Component c = comp.getParent();
////						if (c == null)
////							return;
////						if (c instanceof JComponent) {
////							comp = (JComponent) c;
////						} else {
////							return;
////						}
////					}
////				}
////			}
////		});
////	}
//
////	@Override
////	public void register(TilingPanel tilingPanel) {
////		listenerMap.put(tilingPanel.hashCode(), tilingPanel);
////	}
////
////	@Override
////	public void unregister(TilingPanel tilingPanel) {
////		listenerMap.remove(tilingPanel.hashCode());
////	}
////
////	public void notifyFileChanged(String folder) {
////		Properties data = new Properties();
////		data.put("event.file", folder);
////		for (AppEventListener l : appListeners) {
////			if (l != null) {
////				l.onEvent(Constants.FILE_ADDED, data);
////			}
////		}
////	}
////
////	@Override
////	public void notify(TransferStatus status, FileTransfer transfer) {
////		if (status == TransferStatus.Complete) {
////			String host = transfer.getHostName();
////			String src = transfer.getSourceFileName();
////			String dst = transfer.getTargetFileName();
////
////			Properties data = new Properties();
////			data.put("download.host", host);
////			data.put("download.srcfile", src);
////			data.put("download.dstfile", dst);
////			data.put("download.type",
////					transfer.getType() == FileType.Directory ? "folder"
////							: "file");
////			for (AppEventListener l : appListeners) {
////				if (l != null) {
////					l.onEvent(Constants.DOWNLOAD_FINISHED, data);
////				}
////			}
////		}
////	}
////
////	public void registerAppEventListener(AppEventListener l) {
////		this.appListeners.add(l);
////	}
////
////	public void unregisterAppEventListener(AppEventListener l) {
////		this.appListeners.remove(l);
////	}
////
////	public void showTransferPanel(Component c) {
////		Rectangle r = c.getBounds();
////
////		transferPopup.show(c,
////				(int) (r.width - transferPopup.getPreferredSize().getWidth()),
////				r.height);
////	}
////
////	public void sftpRemoteToLocal(String localFolder,
////			TransferFileInfo infoList) {
////
////	}
////
////	public void createWidget(String className, String[] args, Widget parent) {
////		Map<String, Object> env = new HashMap<>();
////		workspace.addWidget(className, env, args, parent);
////	}
//
//	public void newWorkspace() {
//		workspace.newWorkspace();
//	}
//}

/**
 * 
 */
package nixexplorer.app;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import nixexplorer.TextHolder;
import nixexplorer.app.components.FlatTabbedPane;
import nixexplorer.app.components.TabbedChild;
import nixexplorer.app.components.keygen.KeyGeneratorDialog;
import nixexplorer.app.components.net.NetworkChecker;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.app.settings.ui.ConfigDialog;
import nixexplorer.widgets.BaseSysInfoWidget;
import nixexplorer.widgets.console.TabbedConsoleWidget;
import nixexplorer.widgets.du.DiskUsageViewerWidget;
import nixexplorer.widgets.editor.RemoteEditorWidget;
import nixexplorer.widgets.folderview.files.FileBrowserWidget;
//import nixexplorer.widgets.folderview.foreign.ForeignFolderViewWidget;
import nixexplorer.widgets.folderview.local.LocalFolderViewWidget;
import nixexplorer.widgets.folderview.remote.RemoteFolderViewWidget;
import nixexplorer.widgets.http.HttpClient;
import nixexplorer.widgets.logviewer.LogViewerWidget;
import nixexplorer.widgets.search.FileSearchWidget;
import nixexplorer.widgets.sysmon.SystemMonitorWidget;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class ServerDisplayPanel extends JPanel {
	/**
	 * 
	 */
	private FlatTabbedPane tabs;
	private ServerToolbar toolbar;
	private SessionInfo info;
	private AppSession appSession;
//	private JPanel bottomBar;
//	private JPanel bottomPanel;
	private MouseAdapter adapter;
	private JLabel lblDragSide;
//	private JSplitPane vertSplit;
//	private int lastBottomDivider;
//	private MatteBorder expB, clpB;
	private Window window;
	private SessionListCallback callback;

	private JPopupMenu utilityPopup;
	private JButton utilityButton;

//
	// private LocalFolderViewWidget localFileView;

	public ServerDisplayPanel(SessionInfo info, Window window,
			SessionListCallback callback, AppSession appSession) {
		this.info = info;
		setBackground(UIManager.getColor("Panel.secondary"));// "DefaultBorder.color"));//
																// "Panel.secondary"));
		this.window = window;
		this.callback = callback;
		this.appSession = appSession;
		setLayout(new BorderLayout());
//		vertSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//		vertSplit.setBorder(new EmptyBorder(0, 0, 0, 0));
//		clpB = new MatteBorder(Utility.toPixel(1), Utility.toPixel(0),
//				Utility.toPixel(0), Utility.toPixel(0),
//				UIManager.getColor("DefaultBorder.color"));
//		expB = new MatteBorder(Utility.toPixel(1), Utility.toPixel(0),
//				Utility.toPixel(0), Utility.toPixel(0),
//				UIManager.getColor("DefaultBorder.color"));
//		vertSplit.setUI(new BasicSplitPaneUI() {
//			@Override
//			public BasicSplitPaneDivider createDefaultDivider() {
//				BasicSplitPaneDivider d = new BasicSplitPaneDivider(this) {
//					@Override
//					public Border getBorder() {
//						return null;
//					}
//				};
//				d.setBorder(null);
//				return d;
//			}
//		});
//		vertSplit.setOpaque(false);
//		vertSplit.setDividerSize(Utility.toPixel(8));
//		vertSplit.setContinuousLayout(true);
		// vertSplit.getd
		toolbar = createToolbar();
		add(toolbar, BorderLayout.NORTH);
		add(createContentPanel());
		// localFileView = new LocalFolderViewWidget(info, new String[] {},
		// appSession, window);
		lblDragSide = new JLabel();
		lblDragSide.setMinimumSize(
				new Dimension(Utility.toPixel(8), Utility.toPixel(8)));
		lblDragSide.setPreferredSize(
				new Dimension(Utility.toPixel(8), Utility.toPixel(8)));
		lblDragSide.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
		// add(lblDragSide, BorderLayout.WEST);

	}

	public void addTab(TabbedChild w) {
		tabs.addTab(w.getTitle(), w, true);
	}

	private JPanel createContentPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
//		int gap = Utility.toPixel(8);
//		panel.setBorder(new EmptyBorder(gap, 0, gap, gap));

		tabs = new FlatTabbedPane(true, true, null);
		tabs.setClosable(true);
//		tabs.setBorder(new MatteBorder(Utility.toPixel(0), Utility.toPixel(1),
//				Utility.toPixel(1), Utility.toPixel(1),
//				UIManager.getColor("DefaultBorder.color")));
		panel.add(tabs);

//		bottomBar = new JPanel(new BorderLayout());
//		bottomBar.setOpaque(true);
//		JLabel lblTitle = new JLabel(TextHolder.getString("app.local.title"));
//		lblTitle.setFont(
//				new Font(Font.DIALOG, Font.PLAIN, Utility.toPixel(14)));
//		JButton btnExpandCollapse = new JButton(
//				UIManager.getIcon("ExpandPanel.upIcon"));
//		btnExpandCollapse.addActionListener(e -> {
//			if (btnExpandCollapse
//					.getClientProperty("button.expanded") == null) {
//				panel.removeAll();
//				vertSplit.setTopComponent(tabs);
//				bottomPanel.putClientProperty("panel.size",
//						bottomPanel.getPreferredSize());
//				bottomPanel.removeAll();
//				if (lastBottomDivider == 0) {
//					lastBottomDivider = panel.getHeight() / 2;
//				}
////				bottomPanel.setPreferredSize(new DimensionUIResource(
////						Utility.toPixel(100), Utility.toPixel(300)));
//				bottomPanel.add(bottomBar, BorderLayout.NORTH);
//				bottomPanel.setBorder(expB);
//				btnExpandCollapse.putClientProperty("button.expanded",
//						Boolean.TRUE);
//				btnExpandCollapse
//						.setIcon(UIManager.getIcon("ExpandPanel.downIcon"));
//				bottomPanel.add(localFileView);
//				vertSplit.setBottomComponent(bottomPanel);
//				panel.add(vertSplit);
//				vertSplit.setDividerLocation(lastBottomDivider);
//			} else {
//				lastBottomDivider = vertSplit.getDividerLocation();
//				panel.removeAll();
//				bottomPanel.removeAll();
//				bottomPanel.setPreferredSize((Dimension) bottomPanel
//						.getClientProperty("panel.size"));
//				bottomPanel.add(bottomBar);
//				bottomPanel.setBorder(clpB);
//				btnExpandCollapse.putClientProperty("button.expanded", null);
//				btnExpandCollapse
//						.setIcon(UIManager.getIcon("ExpandPanel.upIcon"));
//				panel.add(tabs);
//				panel.add(bottomPanel, BorderLayout.SOUTH);
//			}
//			doLayout();
//			revalidate();
//			repaint();
//		});
//		bottomBar.add(lblTitle, BorderLayout.WEST);
//		bottomBar.add(btnExpandCollapse, BorderLayout.EAST);
////		tabs1.add(Box.createRigidArea(
////				new Dimension(Utility.toPixel(10), Utility.toPixel(30))));
//		bottomBar.setBorder(new EmptyBorder(Utility.toPixel(5),
//				Utility.toPixel(5), Utility.toPixel(5), Utility.toPixel(5)));
//
//		panel.add(tabs);
//		bottomPanel = new JPanel(new BorderLayout());
//		bottomPanel.setBorder(clpB);
//		bottomPanel.add(bottomBar);
//		panel.add(bottomPanel, BorderLayout.SOUTH);
		return panel;
	}

	private ServerToolbar createToolbar() {
		ServerToolbar toolbar = new ServerToolbar();
		toolbar.setBorder(new MatteBorder(0, 0, Utility.toPixel(1), 0,
				UIManager.getColor("DefaultBorder.color")));
		toolbar.addButton("app.control.terminal", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					TabbedConsoleWidget w = new TabbedConsoleWidget(info,
							new String[] {}, appSession, window);
					appSession.addToSession(w);
					addTab(w);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}, TextHolder.getString("app.control.terminal"),
				UIManager.getIcon("ServerTools.terminalIcon"));
		toolbar.addButton("app.control.files", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createFolderView(null);
			}
		}, TextHolder.getString("app.control.files"),
				UIManager.getIcon("ServerTools.filesIcon"));
//		toolbar.addButton("app.control.editor", new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				try {
//					FormattedEditorWidget w = new FormattedEditorWidget(info,
//							new String[] {}, appSession, window);
//					addTab(w);
//				} catch (Exception e2) {
//					e2.printStackTrace();
//				}
//			}
//		}, TextHolder.getString("app.control.editor"),
//				UIManager.getIcon("ServerTools.editorIcon"));
//		toolbar.addButton("app.control.logviewer", new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				try {
//					LogViewerWidget w = new LogViewerWidget(info,
//							new String[] {}, appSession, window);
//					addTab(w);
//				} catch (Exception e2) {
//					e2.printStackTrace();
//				}
//			}
//		}, TextHolder.getString("app.control.logviewer"),
//				UIManager.getIcon("ServerTools.logViewIcon"));
		toolbar.addButton("app.control.taskmgr", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					SystemMonitorWidget w = new SystemMonitorWidget(info,
							new String[] {}, appSession, window);
					addTab(w);
					appSession.addToSession(w);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}, TextHolder.getString("app.control.taskmgr"),
				UIManager.getIcon("ServerTools.taskmgrIcon"));

		toolbar.addButton("editor.title", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					RemoteEditorWidget w = new RemoteEditorWidget(info,
							new String[] {}, appSession, window);
					appSession.addToSession(w);
					addTab(w);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}, TextHolder.getString("editor.title"),
				UIManager.getIcon("ServerTools.editorIcon"));

		toolbar.addButton("logviewer.title", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					LogViewerWidget w = new LogViewerWidget(info,
							new String[] {}, appSession, window);
					appSession.addToSession(w);
					addTab(w);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}, TextHolder.getString("logviewer.title"),
				UIManager.getIcon("ServerTools.taskmgrIcon"));

		toolbar.addButton("app.control.search", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					FileSearchWidget w = new FileSearchWidget(info,
							new String[] {}, appSession, window);
					appSession.addToSession(w);
					addTab(w);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}, TextHolder.getString("app.control.search"),
				UIManager.getIcon("ServerTools.findFilesIcon"));

		toolbar.addButton("diskUsageViewer.title", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					DiskUsageViewerWidget w = new DiskUsageViewerWidget(info,
							new String[] {}, appSession, window);
					appSession.addToSession(w);
					addTab(w);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}, TextHolder.getString("diskUsageViewer.title"),
				UIManager.getIcon("ServerTools.taskmgrIcon"));

		utilityButton = toolbar.addButton("app.control.utility",
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							openPopup(utilityButton);
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}, TextHolder.getString("app.control.utility"),
				UIManager.getIcon("ServerTools.utilityIcon"));

		toolbar.add(Box.createHorizontalGlue());

		// at the end
		toolbar.addButton("app.control.settings", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					System.out.println("called");
					new ConfigDialog(window, AppContext.INSTANCE.getConfig())
							.setVisible(true);
					AppContext.INSTANCE.configChanged();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}, TextHolder.getString("app.control.settings"),
				UIManager.getIcon("ServerTools.settingsIcon"));

		// at the end
//		toolbar.addButton("app.control.notification", new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				try {
////System.out.println("called");
////					new ConfigDialog(window, AppContext.INSTANCE.getConfig())
////							.setVisible(true);
////					AppContext.INSTANCE.configChanged();
//				} catch (Exception e2) {
//					e2.printStackTrace();
//				}
//			}
//		}, TextHolder.getString("app.control.notification"),
//				UIManager.getIcon("ServerTools.notificationIcon"));

		// at the end
		toolbar.addButton("app.control.disconnect", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					System.out.println("disconnect called");
					appSession.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}, TextHolder.getString("app.control.disconnect"),
				UIManager.getIcon("ServerTools.disconnectIcon"));

//		toolbar.addButton("app.control.settings", new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				try {
//					
//				} catch (Exception e2) {
//					e2.printStackTrace();
//				}
//			}
//		}, TextHolder.getString("app.control.settings"),
//				UIManager.getIcon("ServerTools.settingsIcon"));

		return toolbar;
	}

	public void createInitialView() {
//		try {
//			TabbedConsoleWidget w = new TabbedConsoleWidget(info,
//					new String[] {}, appSession, window);
////			RemoteFolderViewWidget w = new RemoteFolderViewWidget(info,
////					new String[] {}, appSession, window);
////			BaseSysInfoWidget w = new BaseSysInfoWidget(info, new String[] {},
////					appSession, window);
//			appSession.addToSession(w);
//			addTab(w);
//		} catch (Exception e2) {
//			e2.printStackTrace();
//		}
	}

	public void createFolderView(String folder) {
		try {
			FileBrowserWidget w = new FileBrowserWidget(info,
					folder == null ? new String[] {} : new String[] { folder },
					appSession, window);
//			RemoteFolderViewWidget w = new RemoteFolderViewWidget(info,
//					new String[] {}, appSession, window);
			appSession.addToSession(w);
			addTab(w);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * @return the appSession
	 */
	public AppSession getAppSession() {
		return appSession;
	}

	/**
	 * @param appSession the appSession to set
	 */
	public void setAppSession(AppSession appSession) {
		this.appSession = appSession;
	}

	/**
	 * @return the adapter
	 */
	public MouseAdapter getAdapter() {
		return adapter;
	}

	/**
	 * @param adapter the adapter to set
	 */
	public void setAdapter(MouseAdapter adapter) {
		this.lblDragSide.removeMouseMotionListener(this.adapter);
		this.adapter = adapter;
		this.lblDragSide.addMouseMotionListener(adapter);
	}

	public boolean closeTab(TabbedChild c) {
		boolean ret = tabs.closeTab(c);
		return ret;
	}

	public void removeSelf() {
		callback.close(appSession);
	}

	private void openPopup(JComponent c) {
		if (utilityPopup == null) {
			utilityPopup = new JPopupMenu();
			JMenuItem mSshItem = new JMenuItem("SSH Keys");
			mSshItem.addActionListener(e -> {
				KeyGeneratorDialog.show(window, info);
			});
			JMenuItem mChkConnectivity = new JMenuItem("Network tools");
			mChkConnectivity.addActionListener(e -> {
				new NetworkChecker(window, info).setVisible(true);
			});
			utilityPopup.add(mSshItem);
			utilityPopup.add(mChkConnectivity);
		}

		utilityPopup.setInvoker(c);
		utilityPopup.show(c, 0, c.getHeight());
	}

}

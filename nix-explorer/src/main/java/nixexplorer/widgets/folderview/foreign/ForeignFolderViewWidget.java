//package nixexplorer.widgets.folderview.foreign;
//
//import java.awt.Component;
//import java.awt.Window;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//import javax.swing.JOptionPane;
//import javax.swing.SwingUtilities;
//
//import nixexplorer.App;
//import nixexplorer.TextHolder;
//import nixexplorer.app.session.AppSession;
//import nixexplorer.core.FileSystemProvider;
//import nixexplorer.core.FileType;
//import nixexplorer.core.ForeignServerInfo;
//import nixexplorer.app.session.SessionInfo;
//import nixexplorer.core.ftp.FtpFileSystemProvider;
//import nixexplorer.core.ftp.FtpSessionInfo;
//import nixexplorer.core.ftp.FtpWrapper;
//import nixexplorer.core.ssh.SshFileSystemProvider;
//import nixexplorer.core.ssh.SshWrapper;
//import nixexplorer.desktop.TaskbarButton;
//import nixexplorer.drawables.icons.ScaledIcon;
//import nixexplorer.widgets.dnd.TransferFileInfo;
//import nixexplorer.widgets.dnd.TreeBaseTransferHandler;
//import nixexplorer.widgets.folderview.FolderViewUtility;
//import nixexplorer.widgets.folderview.FolderViewWidget;
//import nixexplorer.widgets.folderview.TabbedFolderViewWidget;
//import nixexplorer.widgets.listeners.AppMessageListener;
//import nixexplorer.widgets.util.Utility;
//
//public class ForeignFolderViewWidget extends TabbedFolderViewWidget
//		implements Runnable {
//
//	private FileSystemProvider fs;
//	private FileSystemProvider homeFs;
//	private SshWrapper homeWrapper;
//	private FtpWrapper ftpWrapper;
//	private SshWrapper sshWrapper;
//	// private FtpWrapper wrapper;
//	// private FtpSessionInfo ftpInfo;
//	// private SessionInfo sshInfo;
//	private ForeignServerInfo foreignInfo;
//
//	public static final int TOPIC_INIT_DOWNLOAD = 889876;
//
//	public ForeignFolderViewWidget(SessionInfo info, String[] args,
//			AppSession appSession, Window window) {
//		super(info, args, appSession, window);
//		RemoteServerDialog remoteDlg = new RemoteServerDialog();
//		remoteDlg.setVisible(true);
//		this.foreignInfo = remoteDlg.getServerInfo();
//		if (this.foreignInfo == null) {
//			throw new RuntimeException("server info null");
//		}
//
//		// setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//		try {
//			this.icon = new ScaledIcon(
//					App.class.getResource("/images/local.png"),
//					Utility.toPixel(24), Utility.toPixel(24));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
////		this.setLayout(new BorderLayout());
////		tabbedFolders = new jtabb
////		tabbedFolders.setTabListener(this);
////		add(tabbedFolders);
//
//		new Thread(this).start();
//	}
//
//	@Override
//	public void reconnect() {
//
//	}
//
//	@Override
//	public void close() {
//		// getDesktop().unregisterSignalHandler(this, TOPIC_INIT_DOWNLOAD);
//		tabbedFolders.setVisible(false);
//		if (homeWrapper == null && ftpWrapper == null && sshWrapper == null) {
////			dispose();
////			return;
//		}
//		new Thread(() -> {
//			if (homeWrapper != null) {
//				try {
//					homeWrapper.disconnect();
//					homeWrapper = null;
//					System.out.println("homeWrapper Connection closed");
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//			}
//
//			if (ftpWrapper != null) {
//				try {
//					ftpWrapper.disconnect();
//					ftpWrapper = null;
//					System.out.println("ftpWrapper Connection closed");
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//			}
//
//			if (sshWrapper != null) {
//				try {
//					sshWrapper.disconnect();
//					sshWrapper = null;
//					System.out.println("sshWrapper Connection closed");
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//			}
////			SwingUtilities.invokeLater(() -> {
////				dispose();
////			});
//		}).start();
//		// getParent().remove(this);
////		if (wrapper != null) {
////			wrapper.disconnect();
////		}
//	}
//
//	@Override
//	public FileSystemProvider getFs() {
//		return this.fs;
//	}
//
//	@Override
//	public void reconnectFs() throws Exception {
//		if (this.homeWrapper == null || (!this.homeWrapper.isConnected())) {
//			this.homeWrapper = new SshWrapper(getInfo());
//			this.homeWrapper.connect();
//			this.homeFs = new SshFileSystemProvider(
//					this.homeWrapper.getSftpChannel());
//		}
//		if (this.foreignInfo.getProtocol() == ForeignServerInfo.FTP) {
//			this.ftpWrapper = new FtpWrapper(new FtpSessionInfo(
//					this.foreignInfo.getHost(), this.foreignInfo.getPort(),
//					this.foreignInfo.getUser(), this.foreignInfo.getPassword(),
//					this.foreignInfo.getDirectory()));
//			this.ftpWrapper.connect();
//			this.fs = new FtpFileSystemProvider(ftpWrapper);
//		} else {
//			this.sshWrapper = new SshWrapper(new SessionInfo(
//					UUID.randomUUID().toString(), this.foreignInfo.getHost(),
//					this.foreignInfo.getPort(), this.foreignInfo.getUser(),
//					this.foreignInfo.getPassword(), null,
//					this.foreignInfo.getDirectory(), null,
//					this.foreignInfo.getHost(), null));
//			this.sshWrapper.connect();
//			this.fs = new SshFileSystemProvider(sshWrapper.getSftpChannel());
//		}
//	}
//
//	@Override
//	public void tabClosed(int index, Component c) {
//		// TODO Auto-generated method stub
//
//	}
//
////	private void addTab(String title, Component c) {
////		// int index = tabbedFolders.getTabCount();
////		tabbedFolders.addTab(title, c, true);
////		// tabbedFolders.setTabComponentAt(index, new JLabel(title));
////	}
//
//	@Override
//	public void openNewTab(String path) {
//		System.out.println("called2");
//		String title = "Sftp";
//		ForeignTransferHandler tf = new ForeignTransferHandler(this);
//		TreeBaseTransferHandler treeHandler = new TreeBaseTransferHandler();
//		FolderViewWidget folderView = new FolderViewWidget(path, this, tf,
//				treeHandler, null);
//		addTab(title, folderView);
//	}
//
//	@Override
//	public String getTitleText() {
//		if (this.foreignInfo == null) {
//			return "";
//		}
//		if (this.foreignInfo.getProtocol() == ForeignServerInfo.FTP) {
//			return this.foreignInfo.getHost() + " - Ftp";
//		} else {
//			return this.foreignInfo.getHost() + " - Sftp";
//		}
//	}
//
//	@Override
//	public void editFile(String fileName) {
//		// TODO Auto-generated method stub
//	}
//
//	@Override
//	public void openNewTab(String title, String path) {
//		System.out.println("called2");
//		ForeignTransferHandler tf = new ForeignTransferHandler(this);
//		TreeBaseTransferHandler treeHandler = new TreeBaseTransferHandler();
//		FolderViewWidget folderView = new FolderViewWidget(path, this, tf,
//				treeHandler, null);
//		addTab(title, folderView);
//		// tabbedFolders.setSelectedComponent(folderView);
//	}
//
//	@Override
//	public void run() {
//		try {
//			reconnectFs();
//		} catch (Exception e) {
//			e.printStackTrace();
//			if (!closePending) {
//				System.out.println("Visible showing messagebox");
//				JOptionPane.showMessageDialog(null,
//						TextHolder.getString("folderview.genericError"));
//				SwingUtilities.invokeLater(() -> {
//					TaskbarButton btnTask = (TaskbarButton) getClientProperty(
//							"widget.taskbutton");
//					if (btnTask != null) {
//						try {
//							btnTask.windowClosed();
//							// dispose();
//						} catch (Exception e2) {
//							// TODO: handle exception
//						}
//					}
//				});
//			}
//			return;
//		}
//
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					ForeignTransferHandler tf = new ForeignTransferHandler(
//							ForeignFolderViewWidget.this);
//					TreeBaseTransferHandler treeHandler = new TreeBaseTransferHandler();
//					FolderViewWidget singleFolderView = new FolderViewWidget(
//							foreignInfo.getDirectory() == null ? fs.getHome()
//									: foreignInfo.getDirectory(),
//							ForeignFolderViewWidget.this, tf, treeHandler,
//							null);
//					String title = foreignInfo.getHost();
//					addTab(title, singleFolderView);
//					System.out.println("Called------------");
////					JInternalFrame jint = new JInternalFrame("hello");
////					jint.setClosable(true);
////					jint.setSize(400, 300);
////					getDesktop().getDesktop().add(jint);
////					jint.setVisible(true);
////					startModal(jint);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
//
//	@Override
//	public boolean handleFileDrop(Object infoList, FolderViewWidget widget) {
//		reconnect();
//		try {
//			System.out.println("drop on foreign");
//			TransferFileInfo finfo = (TransferFileInfo) infoList;
//
//			Map<String, String> fileMap = new HashMap<>();
//			Map<String, String> folderMap = new HashMap<>();
//			if (!FolderViewUtility.prepareFileList(widget.getCurrentPath(),
//					finfo.getSourceFiles(), fileMap, false,
//					widget.getCurrentFiles())) {
//				System.out.println("Returing...");
//				return false;
//			}
//
//			if (!FolderViewUtility.prepareFileList(widget.getCurrentPath(),
//					finfo.getSourceFolders(), folderMap, false,
//					widget.getCurrentFiles())) {
//				System.out.println("Returing...");
//				return false;
//			}
//
//			disableUI();
//
//			new Thread(() -> {
//				try {
//					Map<String, String> childFolders = new HashMap<>();
//					for (String key : folderMap.keySet()) {
//						homeFs.getAllFiles(key, widget.getCurrentPath(),
//								fileMap, childFolders);
//					}
//
//					System.out.println("All files are retrieved");
//
//					for (String key : childFolders.keySet()) {
//						fs.mkdirs(childFolders.get(key));
//					}
//
//					System.out.println("folder structure created");
//
//					if (this.foreignInfo
//							.getProtocol() == ForeignServerInfo.SFTP) {// SFTP
//						System.out.println("sftp");
//
//						StringBuilder sb = new StringBuilder();
//						// sb.append("cd \"" + widget.getCurrentPath() +
//						// "\"\n");
//						for (String key : fileMap.keySet()) {
//							sb.append("put \"" + key + "\" \""
//									+ fileMap.get(key) + "\"\n");
//						}
//
//						sb.append("exit\n");
//
//						RemoteCommandExecDlg dlg = new RemoteCommandExecDlg(
//								getInfo(), "", "sftp>",
//								"sftp " + this.foreignInfo.getUser() + "@"
//										+ this.foreignInfo.getHost(),
//								sb.toString());
//						dlg.setVisible(true);
//					}
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				} finally {
//					SwingUtilities.invokeLater(() -> {
//						enableUI();
//					});
//				}
//
//			}).start();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return false;
//	}
//
//	@Override
//	public void pasteItem(TransferFileInfo info, FolderViewWidget w) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void moveFiles(String targetFolder, List<String> sourceFiles,
//			List<String> sourceFolders, boolean copy, FolderViewWidget w) {
//		// TODO Auto-generated method stub
//
//	}
//
//	public void downloadFiles() {
//		System.out.println("Download files called");
//	}
//
//	// private void initUpload()
//
//	private void initDownload(String data, FolderViewWidget widget) {
//		System.out.println("Start download " + data);
//
//		reconnect();
//		try {
//			System.out.println("drop on foreign");
//
//			Map<String, String> fileMap = new HashMap<>();
//			Map<String, String> folderMap = new HashMap<>();
//
//			if (!FolderViewUtility.prepareFileList(data,
//					Arrays.asList(widget.getSelectedFiles()).stream()
//							.filter(item -> item.getType() == FileType.File)
//							.map(m -> m.getPath()).collect(Collectors.toList()),
//					fileMap, false, homeFs.ll(data, false))) {
//				System.out.println("Returing...");
//			}
//
//			if (!FolderViewUtility.prepareFileList(data,
//					Arrays.asList(widget.getSelectedFiles()).stream().filter(
//							item -> item.getType() == FileType.Directory)
//							.map(m -> m.getPath()).collect(Collectors.toList()),
//					folderMap, false, homeFs.ll(data, false))) {
//				System.out.println("Returing...");
//			}
//
//			disableUI();
//
//			new Thread(() -> {
//				try {
//					System.out.println("fileMap: " + fileMap);
//					System.out.println("folderMap: " + folderMap);
//					Map<String, String> childFolders = new HashMap<>();
//					for (String key : folderMap.keySet()) {
//						fs.getAllFiles(key, data, fileMap, childFolders);
//					}
//
//					System.out.println("All files are retrieved");
//
//					System.out.println("childFolders: " + childFolders);
//					System.out.println("fileMap final: " + fileMap);
//
//					for (String key : childFolders.keySet()) {
//						homeFs.mkdirs(childFolders.get(key));
//					}
//
//					System.out.println("folder structure created");
//
//					System.out.println(fileMap);
//
//					if (this.foreignInfo
//							.getProtocol() == ForeignServerInfo.SFTP) {// SFTP
//						System.out.println("sftp");
//
//						StringBuilder sb = new StringBuilder();
//						// sb.append("cd \"" + widget.getCurrentPath() +
//						// "\"\n");
//						for (String key : fileMap.keySet()) {
//							sb.append("get \"" + key + "\" \""
//									+ fileMap.get(key) + "\"\n");
//						}
//
//						sb.append("exit\n");
//
//						System.out.println(sb);
//
//						RemoteCommandExecDlg dlg = new RemoteCommandExecDlg(
//								getInfo(), "", "sftp>",
//								"sftp " + this.foreignInfo.getUser() + "@"
//										+ this.foreignInfo.getHost(),
//								sb.toString());
//						dlg.setVisible(true);
//					} else if (this.foreignInfo
//							.getProtocol() == ForeignServerInfo.FTP) {
//						System.out.println("ftp");
//
//						StringBuilder sb = new StringBuilder();
//						// sb.append("cd \"" + widget.getCurrentPath() +
//						// "\"\n");
//						for (String key : fileMap.keySet()) {
//							sb.append("get \"" + key + "\" \""
//									+ fileMap.get(key) + "\"\n");
//						}
//
//						sb.append("exit\n");
//
//						System.out.println(sb);
//
//						RemoteCommandExecDlg dlg = new RemoteCommandExecDlg(
//								getInfo(), "", "ftp>",
//								"ftp " + this.foreignInfo.getHost(),
//								sb.toString());
//						dlg.setVisible(true);
//					}
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				} finally {
//					SwingUtilities.invokeLater(() -> {
//						enableUI();
//					});
//				}
//
//			}).start();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	public void onSignal(Object signal, Object data, FolderViewWidget widget) {
//		System.out.println("Signal received");
//		new Thread(() -> {
//			initDownload((String) data, widget);
//		}).start();
//
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see nixexplorer.widgets.folderview.TabCallback#getAppListener()
//	 */
//	@Override
//	public AppMessageListener getAppListener() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see nixexplorer.widgets.folderview.TabCallback#getDesktop()
//	 */
//	@Override
//	public AppSession getSession() {
//		return this.appSession;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see nixexplorer.app.components.TabbedChild#tabClosing()
//	 */
//	@Override
//	public boolean tabClosing() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see nixexplorer.app.components.TabbedChild#tabClosed()
//	 */
//	@Override
//	public void tabClosed() {
//		// TODO Auto-generated method stub
//
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see nixexplorer.app.components.TabbedChild#tabSelected()
//	 */
//	@Override
//	public void tabSelected() {
//		// TODO Auto-generated method stub
//
//	}
//
//}

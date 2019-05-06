package nixexplorer.widgets.folderview.remote;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import nixexplorer.PathUtils;
import nixexplorer.TextHolder;
import nixexplorer.app.components.TabbedChild;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionEventAware;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.FileInfo;
import nixexplorer.core.FileSystemProvider;
import nixexplorer.core.ssh.SshFileSystemWrapper;
import nixexplorer.widgets.dnd.TransferFileInfo;
import nixexplorer.widgets.dnd.TransferFileInfo.Action;
import nixexplorer.widgets.dnd.TreeViewTransferHandler;
import nixexplorer.widgets.folderview.ContentChangeListener;
import nixexplorer.widgets.folderview.FolderViewUtility;
import nixexplorer.widgets.folderview.FolderViewWidget;
import nixexplorer.widgets.folderview.ShellActions;
import nixexplorer.widgets.folderview.TabbedFolderViewWidget;
import nixexplorer.widgets.folderview.common.OverflowMenuActionHandlerImpl;
import nixexplorer.widgets.folderview.copy.CopyWidget;
import nixexplorer.widgets.listeners.AppMessageListener;

public class RemoteFolderViewWidget extends TabbedFolderViewWidget
		implements Runnable, ContentChangeListener, SessionEventAware {
	private static final long serialVersionUID = -5517584410162106722L;
	private FileSystemProvider fs;
	private WeakHashMap<FolderViewWidget, Boolean> folderViews = new WeakHashMap<FolderViewWidget, Boolean>();
	private TabbedChild parentTab;

	public RemoteFolderViewWidget(SessionInfo info, String args[],
			AppSession appSession, Window window, TabbedChild parentTab) {
		super(info, args, appSession, window);
		System.out.println("Window-: " + window);
		this.parentTab = parentTab;
		setCursor(waitCursor);
		this.fs = new SshFileSystemWrapper(info);
//		try {
//			setFrameIcon(new ScaledIcon(
//					App.class.getResource("/images/remote_folder.png"),
//					Utility.toPixel(24), Utility.toPixel(24)));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		// tabbedFolders.a
		this.icon = UIManager.getIcon("ServerTools.filesIcon16");
//		add(tabbedFolders);

		new Thread(this).start();
	}

	@Override
	public void reconnect() {
//		for (int i = 0; i < tabbedFolders.getTabCount(); i++) {
//			((FolderViewWidget) tabbedFolders.getComponentAt(i)).reconnect();
//		}
	}

	@Override
	public void close() {
		tabbedFolders.setVisible(false);
		System.out.println("closing tabbed pane "+this.closeListener);
//		if (this.closeListener != null) {
//			this.closeListener.allTabsClosed(this);
//		}
		new Thread(() -> {
			try {
				if (fs != null) {
					fs.close();
				}
				System.out.println("Connection closed");
			} catch (Exception e) {
			}

		}).start();
	}

	@Override
	public void openTerminal(String command) {
		appSession.createWidget(
				"nixexplorer.widgets.console.TabbedConsoleWidget",
				new String[] { "-c", command });

		// RemoteFolderViewUtils.openTerminalDialog(command, this);
//		String[] args = new String[2];
//		args[0] = "-c";
//		args[1] = command.toString();
//		getDesktop().createWidget(TabbedConsoleWidget.class.getName(), env,
//				args, this);
	}

	@Override
	public void run() {
		try {
			String folder = null;

			folder = args == null || args.length < 1
					? (info.getRemoteFolder() == null
							|| info.getRemoteFolder().length() < 1
									? fs.getHome()
									: info.getRemoteFolder())
					: args[0];
			final String initFolder = folder;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					initFolderView(initFolder);
				}
			});
		} catch (Exception e) {
			SwingUtilities.invokeLater(() -> {
				appSession.closeTab(parentTab);
			});
		}
	}

	private void initFolderView(String initFolder) {
		try {
			RemoteFolderViewTransferHandler transferHandler = new RemoteFolderViewTransferHandler(
					RemoteFolderViewWidget.this);
			TreeViewTransferHandler treeHandler = new TreeViewTransferHandler(
					RemoteFolderViewWidget.this);

			FolderViewWidget singleFolderView = new FolderViewWidget(initFolder,
					RemoteFolderViewWidget.this, transferHandler, treeHandler,
					new RemoteContextMenuActionHandler(
							RemoteFolderViewWidget.this),
					new OverflowMenuActionHandlerImpl(),
					new RemoteTreeContextMenuHandler(
							RemoteFolderViewWidget.this));
			folderViews.put(singleFolderView, Boolean.TRUE);
			String title = info.getHost();
			addTab(title, singleFolderView);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openNewTab(String path) {
		String title = "Sftp";
		RemoteFolderViewTransferHandler t = new RemoteFolderViewTransferHandler(
				this);
		TreeViewTransferHandler treeHandler = new TreeViewTransferHandler(
				RemoteFolderViewWidget.this);
		FolderViewWidget folderView = new FolderViewWidget(path, this, t,
				treeHandler,
				new RemoteContextMenuActionHandler(RemoteFolderViewWidget.this),
				new OverflowMenuActionHandlerImpl(),
				new RemoteTreeContextMenuHandler(RemoteFolderViewWidget.this));
		folderViews.put(folderView, Boolean.TRUE);

		addTab(title, folderView);
	}

	@Override
	public void openNewTab(String title, String path) {
		System.out.println("called2");

		RemoteFolderViewTransferHandler t = new RemoteFolderViewTransferHandler(
				this);
		TreeViewTransferHandler treeHandler = new TreeViewTransferHandler(
				RemoteFolderViewWidget.this);
		FolderViewWidget folderView = new FolderViewWidget(path, this, t,
				treeHandler,
				new RemoteContextMenuActionHandler(RemoteFolderViewWidget.this),
				new OverflowMenuActionHandlerImpl(),
				new RemoteTreeContextMenuHandler(RemoteFolderViewWidget.this));
		folderViews.put(folderView, Boolean.TRUE);
		addTab(title, folderView);
		// tabbedFolders.setSelectedComponent(folderView);
	}

	@Override
	public void editFile(String fileName) {

		File tempFolder = new File(System.getProperty("java.io.tmpdir"),
				UUID.randomUUID() + "");
		tempFolder.mkdirs();
		File f = new File(tempFolder, PathUtils.getFileName(fileName));
		disableUI();
		new Thread(() -> {
			SwingUtilities.invokeLater(() -> {
				disableUI();
			});

			FileOutputStream out = null;
			InputStream in = null;
			try {
				out = new FileOutputStream(f);
				in = fs.getInputStream(fileName, 0);
				byte[] b = new byte[8192];
				while (true) {
					int x = in.read(b);
					if (x == -1)
						break;
					out.write(b, 0, x);
				}
				in.close();
				out.close();
				System.out.println("downloaded to->" + f);

			} catch (Exception e) {
				e.printStackTrace();
				try {
					out.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
				try {
					in.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			SwingUtilities.invokeLater(() -> {
				enableUI();
//				AppSessionPanel.getsharedInstance()
//						.openWidget(new FormattedEditorWidget(f.getAbsolutePath(), fileName, this));
			});
		}).start();
	}

	@Override
	public void contentChanged(String local, String remote) {
		new Thread(() -> {
			SwingUtilities.invokeLater(() -> {
				disableUI();
			});
			OutputStream out = null;
			InputStream in = null;
			try {
				out = fs.getOutputStream(remote);
				in = new FileInputStream(local);
				byte[] b = new byte[8192];
				while (true) {
					int x = in.read(b);
					if (x == -1)
						break;
					out.write(b, 0, x);
				}
				in.close();
				out.close();
				System.out.println("uploaded to->" + remote);

			} catch (Exception e) {
				e.printStackTrace();
				try {
					out.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
				try {
					in.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			SwingUtilities.invokeLater(() -> {
				enableUI();
			});
		}).start();

	}

	@Override
	public String getTitleText() {
		return TextHolder.getString("app.files.title");
	}

	@Override
	public FileSystemProvider getFs() {
		return this.fs;
	}

//	@Override
//	public void reconnectFs() throws Exception {
//		while (!closePending) {
//			wrapper = SshUtility.connectWrapper(info, widgetClosed);
//			try {
//				fs = new SshFileSystemProvider(wrapper.getSftpChannel());
//				return;
//			} catch (Exception e) {
//				e.printStackTrace();
//				try {
//					wrapper.disconnect();
//				} catch (Exception e1) {
//				}
//			}
//			if (!closePending) {
//				if (JOptionPane.showConfirmDialog(null,
//						"Unable to connect to server. Retry?") != JOptionPane.YES_OPTION) {
//					throw new Exception("User cancelled the operation");
//				}
//			}
//		}
//	}

	@Override
	public void tabClosed(int index, Component c) {
		// TODO Auto-generated method stub

	}

	public boolean handleFileDrop(Object obj, FolderViewWidget widget) {
		TransferFileInfo finfo = (TransferFileInfo) obj;
		finfo.setBaseFolder(widget.getCurrentPath());

		List<String> args = new ArrayList<>();
		args.add("u");
		args.add(widget.getCurrentPath());
		args.add(finfo.getSourceFiles() == null ? "0"
				: finfo.getSourceFiles().size() + "");
		args.add(finfo.getSourceFolders() == null ? "0"
				: finfo.getSourceFolders().size() + "");
		args.addAll(finfo.getSourceFiles());
		args.addAll(finfo.getSourceFolders());

		String[] arr = new String[args.size()];
		arr = args.toArray(arr);

		System.out.println("args: " + args);

		appSession.createWidget(CopyWidget.class.getName(), arr);

		// addSftpLocal2Remote(infoList, widget);
		return true;
	}

	public void moveFiles(String targetFolder, List<String> sourceFiles,
			List<String> sourceFolders, boolean copy, FolderViewWidget w) {
		disableView();
		Map<String, String> mvMap = new HashMap<>();
		new Thread(() -> {
			try {
				System.out.println("Moving to target: " + targetFolder);
				applyPreviousAction = false;
				List<FileInfo> list = getFs().list(targetFolder);

				if (!FolderViewUtility.prepareFileList(targetFolder,
						sourceFiles, mvMap, false, list)) {
					System.out.println("Returing...");
					return;
				}

				if (!FolderViewUtility.prepareFileList(targetFolder,
						sourceFolders, mvMap, false, list)) {
					System.out.println("Returing...");
					return;
				}

				for (String key : mvMap.keySet()) {
					System.out.println(
							"Moving file: " + key + " -> " + mvMap.get(key));
					if (copy) {
						ShellActions.copy(key, mvMap.get(key),
								((SshFileSystemWrapper) fs).getWrapper());
					} else {
						System.out.println("Renaming file " + key + " -> "
								+ mvMap.get(key));
						getFs().rename(key, mvMap.get(key));
					}
				}

			} catch (FileNotFoundException e) {
				SwingUtilities.invokeLater(() -> {
					String suCmd = AskForPriviledgeDlg.askForPriviledge();// dlg.getCommand();
					StringBuilder command = new StringBuilder();
					command.append(suCmd + " ");
					boolean sudo = false;
					sudo = suCmd.startsWith("sudo");
					if (!sudo) {
						command.append("'");
					}
					if (!copy) {
						command.append("mv ");
						for (String key : mvMap.keySet()) {
							command.append("\"" + key + "\" ");
						}
						command.append(" \"" + targetFolder + "\"");
					} else {
						command.append(" cp -r -f ");
						for (String key : mvMap.keySet()) {
							command.append("\"" + key + "\" ");
						}
						command.append(" \"" + targetFolder + "\"");
					}
					if (!sudo) {
						command.append("'");
					}
					System.out.println("Command: " + command);
					String[] args = new String[2];
					args[0] = "-c";
					args[1] = command.toString();

					RemoteFolderViewUtils.openTerminalDialog(command.toString(),
							this, true, true);

//					getDesktop().createWidget(
//							TabbedConsoleWidget.class.getName(), env, args,
//							this);

				});

			} catch (Exception e) {
				e.printStackTrace();
				SwingUtilities.invokeLater(() -> {
					JOptionPane.showMessageDialog(null,
							TextHolder.getString("folderview.genericError"));
				});

			} finally {
				enableView();
			}
		}).start();
	}

	private void copy(TransferFileInfo info, boolean copy, FolderViewWidget w) {
		info.setBaseFolder(w.getCurrentPath());
		List<String> droppedFiles = info.getSourceFiles();
		List<String> droppedFolders = info.getSourceFolders();
		System.out.println(
				"copying files to: " + w.getCurrentPath() + " dropped files: "
						+ droppedFiles + " dropped folders: " + droppedFolders);
		moveFiles(w.getCurrentPath(), droppedFiles, droppedFolders, copy, w);
	}

	public void move(TransferFileInfo info, String path) {
		path = JOptionPane.showInputDialog(getWindow(), "Move files to", path);
		if (path == null || path.isEmpty()) {
			return;
		}

		info.setBaseFolder(path);
		List<String> droppedFiles = info.getSourceFiles();
		List<String> droppedFolders = info.getSourceFolders();
		System.out.println("copying files to: " + path + " dropped files: "
				+ droppedFiles + " dropped folders: " + droppedFolders);
		moveFiles(path, droppedFiles, droppedFolders, false, null);
	}

//
	public void pasteItem(TransferFileInfo info, FolderViewWidget w) {
		if (info.getInfo() == null || info.getInfo().size() == 0) {
			// initiate upload
			info.addInfo(this.info);
			handleFileDrop(info, w);
		} else {
			if (FolderViewUtility.sameSession(this.info,
					info.getInfo().get(0))) {
				copy(info, info.getAction() == Action.COPY, w);
			} else {
				System.out.println("Paste but not same session");
				// code to handle foreign paste
//				info.addInfo(this.info);
//				handleLocalFileDrop(info);
			}
		}
	}

	@Override
	public AppMessageListener getAppListener() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#tabClosing()
	 */
	@Override
	public boolean viewClosing() {
		closePending = true;
		closeInitiated = true;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#tabClosed()
	 */
	@Override
	public void viewClosed() {
		super.viewClosed();
		close();
		appSession.unregisterSessionAwareComponent(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#tabSelected()
	 */
	@Override
	public void tabSelected() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.widgets.folderview.TabCallback#getSession()
	 */
	@Override
	public AppSession getSession() {
		return this.appSession;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.session.SessionEventAware#configChanged()
	 */
	@Override
	public void configChanged() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.session.SessionEventAware#remoteFileSystemUpdated()
	 */
	@Override
	public void fileSystemUpdated(String path) {
		for (FolderViewWidget view : folderViews.keySet()) {
			System.out.println("Update notification for: " + path);
			if (PathUtils.isSamePath(view.getCurrentPath(), path)) {
				view.render(view.getCurrentPath(), false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#getIcon()
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.widgets.folderview.TabbedFolderViewWidget#cancel()
	 */
	@Override
	protected void cancel() {
		new Thread(() -> {
			try {
				System.out.println("Cancelling...");
				this.fs.close();
			} catch (Exception e) {
			} finally {
				enableView();
			}
		}).start();
	}

	public void createLink(String src, String dst, boolean hardLink) {
		disableView();
		new Thread(() -> {
			try {
				System.out.println("Creating link...");
				fs.createLink(src, dst, hardLink);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						TextHolder.getString("folderview.genericError"));
			} finally {
				enableView();
			}
		}).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.widgets.folderview.TabCallback#listFavourites()
	 */
	@Override
	public List<String> listFavourites() {
		return info.getFavouriteRemoteFolders();
	}

}

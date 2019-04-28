package nixexplorer.widgets.folderview.local;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import javax.swing.JOptionPane;

import nixexplorer.App;
import nixexplorer.PathUtils;
import nixexplorer.TextHolder;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionEventAware;
import nixexplorer.core.FileInfo;
import nixexplorer.core.FileSystemProvider;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.file.Copy;
import nixexplorer.core.file.LocalFileSystemProvider;
import nixexplorer.drawables.icons.ScaledIcon;
import nixexplorer.widgets.dnd.TransferFileInfo;
import nixexplorer.widgets.dnd.TransferFileInfo.Action;
import nixexplorer.widgets.dnd.TreeViewTransferHandler;
import nixexplorer.widgets.folderview.FolderViewUtility;
import nixexplorer.widgets.folderview.FolderViewWidget;
import nixexplorer.widgets.folderview.TabbedFolderViewWidget;
import nixexplorer.widgets.folderview.common.OverflowMenuActionHandlerImpl;
import nixexplorer.widgets.folderview.copy.CopyWidget;
import nixexplorer.widgets.listeners.AppMessageListener;
import nixexplorer.widgets.util.Utility;

public class LocalFolderViewWidget extends TabbedFolderViewWidget
		implements SessionEventAware {
	private static final long serialVersionUID = 1261155106627917513L;
	private FileSystemProvider fs;
	private WeakHashMap<FolderViewWidget, Boolean> folderViews = new WeakHashMap<FolderViewWidget, Boolean>();
	private AppSession appSession;

	public LocalFolderViewWidget(SessionInfo info, String args[],
			AppSession appSession, Window window) {
		super(info, args, appSession, window);
		System.out.println("LocalFolderViewWidget app session: " + appSession);
		try {
			this.icon = new ScaledIcon(
					App.class.getResource("/images/local.png"),
					Utility.toPixel(24), Utility.toPixel(24));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setLayout(new BorderLayout());
		this.fs = new LocalFileSystemProvider();

		LocalFolderViewTransferHandler lt = new LocalFolderViewTransferHandler(
				this);
		TreeViewTransferHandler treeHandler = new TreeViewTransferHandler(
				LocalFolderViewWidget.this);

		String folder = args == null || args.length < 1
				? System.getProperty("user.home")
				: args[0];

		FolderViewWidget singleFolderView = new FolderViewWidget(folder, this,
				lt, treeHandler, new LocalContextMenuActionHandler(this),
				new OverflowMenuActionHandlerImpl(),
				new LocalTreeContextMenuHandler(this));

		folderViews.put(singleFolderView, Boolean.TRUE);

		String title = "Local";
		addTab(title, singleFolderView);
		add(tabbedFolders);
	}

	@Override
	public void close() {
	}

	@Override
	public void reconnect() {

	}

	public void openNewTab(String path) {
		String title = "Local";
		LocalFolderViewTransferHandler lt = new LocalFolderViewTransferHandler(
				this);
		TreeViewTransferHandler treeHandler = new TreeViewTransferHandler(this);
		FolderViewWidget folderView = new FolderViewWidget(path, this, lt,
				treeHandler, new LocalContextMenuActionHandler(this),
				new OverflowMenuActionHandlerImpl(),
				new LocalTreeContextMenuHandler(this));
		addTab(title, folderView);
		folderViews.put(folderView, Boolean.TRUE);
	}

	@Override
	public void openNewTab(String title, String path) {
		System.out.println("called2");
		LocalFolderViewTransferHandler lt = new LocalFolderViewTransferHandler(
				this);
		TreeViewTransferHandler treeHandler = new TreeViewTransferHandler(this);
		FolderViewWidget folderView = new FolderViewWidget(path, this, lt,
				treeHandler, new LocalContextMenuActionHandler(this),
				new OverflowMenuActionHandlerImpl(),
				new LocalTreeContextMenuHandler(this));
		addTab(title, folderView);
		folderViews.put(folderView, Boolean.TRUE);
		// tabbedFolders.setSelectedComponent(folderView);
	}

	@Override
	public void editFile(String fileName) {
		File tempFolder = new File(System.getProperty("java.io.tmpdir"),
				UUID.randomUUID() + "");
		tempFolder.mkdirs();
		// File f = new File(tempFolder, fileName);

	}

	@Override
	public String getTitleText() {
		return "Local";
	}

	@Override
	public FileSystemProvider getFs() {
		return this.fs;
	}

	@Override
	public void tabClosed(int index, Component c) {
		// TODO Auto-generated method stub

	}

	public boolean handleFileDrop(Object obj, FolderViewWidget widget) {
		TransferFileInfo infoList = (TransferFileInfo) obj;
		System.out.println("called1");
		System.out.println("Remote files dropped: " + infoList + " in local");
		infoList.setBaseFolder(widget.getCurrentPath());

		TransferFileInfo finfo = (TransferFileInfo) infoList;

		List<String> args = new ArrayList<>();
		args.add("d");
		args.add(widget.getCurrentPath());
		args.add(finfo.getSourceFiles() == null ? "0"
				: finfo.getSourceFiles().size() + "");
		args.add(finfo.getSourceFolders() == null ? "0"
				: finfo.getSourceFolders().size() + "");
		args.addAll(finfo.getSourceFiles());
		args.addAll(finfo.getSourceFolders());

		String[] arr = new String[args.size()];
		arr = args.toArray(arr);

		System.out.println("Local drop args: " + args);
		try {
			appSession.createWidget(CopyWidget.class.getName(), arr);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		Map<String, String> fileMap = new HashMap<>();
//		Map<String, String> folderMap = new HashMap<>();
//		if (!FolderViewUtility.prepareFileList(widget.getCurrentPath(),
//				finfo.getSourceFiles(), fileMap, false,
//				widget.getCurrentFiles(), this)) {
//			System.out.println("Returing...");
//			return false;
//		}
//
//		if (!FolderViewUtility.prepareFileList(widget.getCurrentPath(),
//				finfo.getSourceFolders(), folderMap, false,
//				widget.getCurrentFiles(), this)) {
//			System.out.println("Returing...");
//			return false;
//		}
//
//		addSftpRemote2Local(infoList, widget);

		return true;
	}

//	private void addSftpRemote2Local(TransferFileInfo data,
//			FolderViewWidget widget) {
//		List<String> folders = data.getSourceFolders();
//		List<String> files = data.getSourceFiles();
//		String baseLocalFolder = data.getBaseFolder();
//		applyPreviousAction = false;
//		int resp = -1;
//		if (files != null && files.size() > 0) {
//			for (String f : files) {
//				String fileName = PathUtils.getFileName(f);
//				outer: {
//					List<FileInfo> fileList = widget.getCurrentFiles();
//					for (int i = 0; i < fileList.size(); i++) {
//						FileInfo info = fileList.get(i);
//						String n = info.getName();
//						if (n.equals(fileName)) {
//							System.out.println(
//									"File '" + n + "' already exists...");
//							if (!applyPreviousAction) {
//								resp = FolderViewUtility
//										.promptDuplicate(fileName);
//							}
//							System.out.println("Resp: " + resp);
//							if (resp == 1) {
//								System.out.println("skipped");
//								break outer;
//							} else if (resp == 2) {
//								// do the rename stuff
//								fileName = FolderViewUtility.autoRename(
//										fileName, widget.getCurrentFiles());
//							} else if (resp != 0) {
//								return;
//							}
//						}
//
//					}
//
//					System.out.println("Adding files for download: " + f);
//					try {
//						BasicFileDownloader fd = new BasicFileDownloader(
//								data.getInfo().get(0), f,
//								new File(baseLocalFolder, fileName)
//										.getAbsolutePath(),
//								getDesktop().getBgTransferQueue());
//
//						getDesktop().getTransferWatcher().addTransfer(fd);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//
//		if (folders != null && folders.size() > 0) {
//			for (String f : folders) {
//				outer: {
//					String folderName = PathUtils.getFileName(f);
//					List<FileInfo> fileList = widget.getCurrentFiles();
//					for (int i = 0; i < fileList.size(); i++) {
//						FileInfo info = fileList.get(i);
//						String n = info.getName();
//						if (n.equals(folderName)) {
//							System.out.println(
//									"File '" + n + "' already exists...");
//							if (!applyPreviousAction) {
//								resp = FolderViewUtility
//										.promptDuplicate(folderName, this);
//							}
//							System.out.println("Resp: " + resp);
//							if (resp == 1) {
//								System.out.println("skipped");
//								break outer;
//							} else if (resp == 2) {
//								// do the rename stuff
//								folderName = FolderViewUtility.autoRename(
//										folderName, widget.getCurrentFiles());
//							} else if (resp != 0) {
//								return;
//							}
//						}
//
//					}
//
//					DirectoryDownloader dd = new DirectoryDownloader(
//							data.getInfo().get(0),
//							new File(baseLocalFolder, folderName)
//									.getAbsolutePath(),
//							f, getDesktop().getBgTransferQueue());
//					getAppListener().getTransferWatcher().addTransfer(dd);
//				}
//			}
//		}
//	}

	private void copyLocal(TransferFileInfo info, FolderViewWidget w) {
		copy(info, info.getAction() == Action.COPY, w);
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

	public void moveFiles(String targetFolder, List<String> sourceFiles,
			List<String> sourceFolders, boolean copy, FolderViewWidget w) {
		disableView();
		new Thread(() -> {
			try {
				System.out.println("Moving...");
				applyPreviousAction = false;
//				List<FileInfo> list = getFs().ll(targetFolder, false);
				Map<String, String> mvMap = new HashMap<>();
				if (!FolderViewUtility.prepareFileList(targetFolder,
						sourceFiles, mvMap, true, w.getCurrentFiles())) {
					System.out.println("Returing...");
					return;
				}

				if (!FolderViewUtility.prepareFileList(targetFolder,
						sourceFolders, mvMap, true, w.getCurrentFiles())) {
					System.out.println("Returing...");
					return;
				}

				for (String key : mvMap.keySet()) {
					System.out.println(
							"Moving file: " + key + " -> " + mvMap.get(key));
					if (copy) {
						Copy.copy(new String[] { "-r", "-p", key,
								mvMap.get(key) });

					} else {
						getFs().rename(key, mvMap.get(key));
					}
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						TextHolder.getString("folderview.genericError"));
			} finally {
				enableView();
			}
		}).start();
	}

	@Override
	public SessionInfo getInfo() {
		return this.info;
	}

	public void pasteItem(TransferFileInfo info, FolderViewWidget w) {
		if (info.getInfo() == null || info.getInfo().size() == 0) {
			copyLocal(info, w);
		} else {
			// download drop
			handleFileDrop(info, w);
		}
//		if (isLocal()) {
//			if (info.getInfo() == null || info.getInfo().size() == 0) {
//				copyLocal(info);
//			} else {
//				// download drop
//				handleRemoteFileDrop(info);
//			}
//		} else {
//			if (info.getInfo() == null || info.getInfo().size() == 0) {
//				// initiate upload
//				info.addInfo(getSessionInfo());
//				handleLocalFileDrop(info);
//			} else {
//				if (FolderViewUtility.sameSession(getSessionInfo(),
//						info.getInfo().get(0))) {
//					copy(info, info.getAction() == Action.COPY);
//				} else {
//					info.addInfo(getSessionInfo());
//					handleLocalFileDrop(info);
//				}
//			}
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.widgets.folderview.TabCallback#getAppListener()
	 */
	@Override
	public AppMessageListener getAppListener() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.widgets.folderview.TabCallback#getSession()
	 */
	@Override
	public AppSession getSession() {
		// TODO Auto-generated method stub
		return this.appSession;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#tabClosing()
	 */
	@Override
	public boolean viewClosing() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#tabClosed()
	 */
	@Override
	public void viewClosed() {
		this.appSession.unregisterSessionAwareComponent(this);
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
	 * @see nixexplorer.widgets.folderview.TabbedFolderViewWidget#cancel()
	 */
	@Override
	protected void cancel() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.widgets.folderview.TabCallback#listFavourites()
	 */
	@Override
	public List<String> listFavourites() {
		return this.info.getFavouriteLocalFolders();
	}

	@Override
	public void configChanged() {
		// TODO Auto-generated method stub

	}

	@Override
	public void fileSystemUpdated(String path) {
		System.out.println("Local views: " + folderViews.keySet());
		for (FolderViewWidget view : folderViews.keySet()) {
			System.out.println("Update notification for: " + path);
			if (PathUtils.isSamePath(view.getCurrentPath(), path)) {
				System.out.println(
						"Local view re rendering: " + folderViews.keySet());
				view.render(view.getCurrentPath(), false);
			}
		}
	}
}

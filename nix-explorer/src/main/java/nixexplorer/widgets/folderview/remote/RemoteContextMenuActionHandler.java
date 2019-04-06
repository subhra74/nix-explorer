/**
 * 
 */
package nixexplorer.widgets.folderview.remote;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import nixexplorer.AppClipboard;
import nixexplorer.PathUtils;
import nixexplorer.TextHolder;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionStore;
import nixexplorer.core.FileInfo;
import nixexplorer.core.FileType;
import nixexplorer.registry.contextmenu.ContextMenuEntry;
import nixexplorer.registry.contextmenu.ContextMenuRegistry;
import nixexplorer.widgets.dnd.TransferFileInfo;
import nixexplorer.widgets.dnd.TransferFileInfo.Action;
import nixexplorer.widgets.editor.ExternalEditorWidget;
import nixexplorer.widgets.editor.FormattedEditorWidget;
import nixexplorer.widgets.folderview.ContextMenuActionHandler;
import nixexplorer.widgets.folderview.FolderViewWidget;
import nixexplorer.widgets.folderview.PermissionsDialog;
import nixexplorer.widgets.folderview.ShellActions;
import nixexplorer.widgets.folderview.copy.CopyWidget;
import nixexplorer.widgets.logviewer.LogViewerWidget;
import nixexplorer.widgets.scp.ScpTransferWidget;

/**
 * @author subhro
 *
 */
public class RemoteContextMenuActionHandler
		implements ContextMenuActionHandler {

	private RemoteFolderViewWidget remoteFolderView;
	private FolderViewWidget folderView;

	/**
	 * 
	 */
	public RemoteContextMenuActionHandler(
			RemoteFolderViewWidget remoteFolderView) {
		this.remoteFolderView = remoteFolderView;
	}

	public void install(FolderViewWidget folderView) {
		this.folderView = folderView;

		InputMap map = folderView
				.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap act = folderView.getActionMap();

		this.initMenuItems(map, act);

	}

	private AbstractAction aOpenInTab, aOpen, aRename, aDelete, aNewFile,
			aNewFolder, aCopy, aPaste, aCut, aAddToFav, aChangePerm, aSendFiles,
			aUpload, aDownload, aCreateLink;

	private KeyStroke ksOpenInTab, ksOpen, ksRename, ksDelete, ksNewFile,
			ksNewFolder, ksCopy, ksPaste, ksCut, ksAddToFav, ksChangePerm,
			ksSendFiles, ksUpload, ksDownload, ksCreateLink;

	private JMenuItem mOpenInTab, mOpen, mRename, mDelete, mNewFile, mNewFolder,
			mCopy, mPaste, mCut, mAddToFav, mChangePerm, mSendFiles, mUpload,
			mOpenWithDefApp, mOpenWthInternalEdit, mOpenWithCustom,
			mOpenWithLogView, mDownload, mCreateLink;

	private JMenu mOpenWith;

	private void initMenuItems(InputMap map, ActionMap act) {

		ksOpenInTab = KeyStroke.getKeyStroke(KeyEvent.VK_T,
				ActionEvent.CTRL_MASK);
		mOpenInTab = new JMenuItem(
				TextHolder.getString("folderview.opennewtab"));
		mOpenInTab.setAccelerator(ksOpenInTab);
		aOpenInTab = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				openNewTab();
			}
		};
		mOpenInTab.addActionListener(aOpenInTab);
		map.put(ksOpenInTab, "ksOpenInTab");
		act.put("ksOpenInTab", aOpenInTab);

		aOpen = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Open called");
				openDefaultAction();
			}
		};
		ksOpen = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		mOpen = new JMenuItem(TextHolder.getString("folderview.open"));
		mOpen.addActionListener(aOpen);
		map.put(ksOpen, "mOpen");
		act.put("mOpen", aOpen);
		mOpen.setAccelerator(ksOpen);

		mOpenWithDefApp = new JMenuItem(
				TextHolder.getString("folderview.openDefault"));
		mOpenWithDefApp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openDefApp();
			}
		});

		mOpenWthInternalEdit = new JMenuItem(
				TextHolder.getString("folderview.openIntern"));
		mOpenWthInternalEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openTextEditor();
			}
		});

		mOpenWithCustom = new JMenuItem(
				TextHolder.getString("folderview.openCust"));
		mOpenWithCustom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		mOpenWithLogView = new JMenuItem(
				TextHolder.getString("folderview.openLogView"));
		mOpenWithLogView.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openLogViewer();
			}
		});

		mOpenWith = new JMenu(TextHolder.getString("folderview.openWith"));
		mOpenWith.add(mOpenWithDefApp);
		mOpenWith.add(mOpenWthInternalEdit);
		mOpenWith.add(mOpenWithCustom);
		mOpenWith.add(mOpenWithLogView);

		aRename = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rename(folderView.getSelectedFiles()[0]);
			}
		};
		ksRename = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);

		mRename = new JMenuItem(TextHolder.getString("folderview.rename"));
		mRename.addActionListener(aRename);
		map.put(ksRename, "mRename");
		act.put("mRename", aRename);
		mRename.setAccelerator(ksRename);

		ksDelete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
		aDelete = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				delete(folderView.getSelectedFiles());
			}
		};
		mDelete = new JMenuItem(TextHolder.getString("folderview.delete"));
		mDelete.addActionListener(aDelete);
		map.put(ksDelete, "ksDelete");
		act.put("ksDelete", aDelete);
		mDelete.setAccelerator(ksDelete);

		ksNewFile = KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
		aNewFile = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFile(folderView.getCurrentPath());
			}
		};
		mNewFile = new JMenuItem(TextHolder.getString("folderview.newFile"));
		mNewFile.addActionListener(aNewFile);
		map.put(ksNewFile, "ksNewFile");
		act.put("ksNewFile", aNewFile);
		mNewFile.setAccelerator(ksNewFile);

		ksNewFolder = KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
		aNewFolder = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFolder(folderView.getCurrentPath());
			}
		};
		mNewFolder = new JMenuItem(
				TextHolder.getString("folderview.newFolder"));
		mNewFolder.addActionListener(aNewFolder);
		mNewFolder.setAccelerator(ksNewFolder);
		map.put(ksNewFolder, "ksNewFolder");
		act.put("ksNewFolder", aNewFolder);

		ksCopy = KeyStroke.getKeyStroke(KeyEvent.VK_C,
				InputEvent.CTRL_DOWN_MASK);
		aCopy = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copyToClipboard(false);
			}
		};
		mCopy = new JMenuItem(TextHolder.getString("folderview.copy"));
		mCopy.addActionListener(aCopy);
		map.put(ksCopy, "ksCopy");
		act.put("ksCopy", aCopy);
		mCopy.setAccelerator(ksCopy);

		ksPaste = KeyStroke.getKeyStroke(KeyEvent.VK_V,
				InputEvent.CTRL_DOWN_MASK);
		aPaste = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (AppClipboard.getContent() instanceof TransferFileInfo) {
					TransferFileInfo info = (TransferFileInfo) AppClipboard
							.getContent();
					remoteFolderView.pasteItem(info, folderView);
					if (info.getAction() == Action.CUT) {
						AppClipboard.setContent(null);
					}
				}
			}
		};
		mPaste = new JMenuItem(TextHolder.getString("folderview.paste"));
		mPaste.addActionListener(aPaste);
		map.put(ksPaste, "ksPaste");
		act.put("ksPaste", aPaste);
		mPaste.setAccelerator(ksPaste);

		ksCut = KeyStroke.getKeyStroke(KeyEvent.VK_X,
				InputEvent.CTRL_DOWN_MASK);
		aCut = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copyToClipboard(true);
			}
		};
		mCut = new JMenuItem(TextHolder.getString("folderview.cut"));
		mCut.addActionListener(aCut);
		map.put(ksCut, "ksCut");
		act.put("ksCut", aCut);
		mCut.setAccelerator(ksCut);

		ksAddToFav = KeyStroke.getKeyStroke(KeyEvent.VK_B,
				InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
		aAddToFav = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addToFavourites();
			}
		};
		mAddToFav = new JMenuItem(TextHolder.getString("folderview.bookmark"));
		mAddToFav.addActionListener(aAddToFav);
		map.put(ksAddToFav, "ksAddToFav");
		act.put("ksAddToFav", aAddToFav);
		mAddToFav.setAccelerator(ksAddToFav);

		ksChangePerm = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
				InputEvent.ALT_DOWN_MASK);
		aChangePerm = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changePermission(folderView.getSelectedFiles());
			}
		};
		mChangePerm = new JMenuItem(TextHolder.getString("folderview.props"));
		mChangePerm.addActionListener(aChangePerm);
		map.put(ksChangePerm, "ksChangePerm");
		act.put("ksChangePerm", aChangePerm);
		mChangePerm.setAccelerator(ksChangePerm);

		ksSendFiles = KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
		aSendFiles = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendFiles(folderView.getSelectedFiles());
			}
		};
		mSendFiles = new JMenuItem(TextHolder.getString("filetransfer.sendto"));
		mSendFiles.addActionListener(aSendFiles);
		map.put(ksSendFiles, "ksSendFiles");
		act.put("ksSendFiles", aSendFiles);
		mSendFiles.setAccelerator(ksSendFiles);

		ksUpload = KeyStroke.getKeyStroke(KeyEvent.VK_U,
				InputEvent.CTRL_DOWN_MASK);
		aUpload = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				jfc.setMultiSelectionEnabled(true);
				if (jfc.showOpenDialog(remoteFolderView
						.getWindow()) == JFileChooser.APPROVE_OPTION) {
					File[] files = jfc.getSelectedFiles();
					if (files != null && files.length > 0) {
						uploadFiles(files);
					}
				}
			}
		};
		mUpload = new JMenuItem(TextHolder.getString("folderview.upload"));
		mUpload.addActionListener(aUpload);
		map.put(ksUpload, "ksUpload");
		act.put("ksUpload", aUpload);
		mUpload.setAccelerator(ksUpload);

		ksDownload = KeyStroke.getKeyStroke(KeyEvent.VK_D,
				InputEvent.CTRL_DOWN_MASK);
		aDownload = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.setMultiSelectionEnabled(false);
				FileInfo[] files = folderView.getSelectedFiles();
				if (files != null && files.length > 0) {
					if (jfc.showOpenDialog(remoteFolderView
							.getWindow()) == JFileChooser.APPROVE_OPTION) {
						File file = jfc.getSelectedFile();
						downloadFiles(files, file.getAbsolutePath());
					}
				}
			}
		};
		mDownload = new JMenuItem(TextHolder.getString("folderview.download"));
		mDownload.addActionListener(aDownload);
		map.put(ksDownload, "ksDownload");
		act.put("ksDownload", aDownload);
		mDownload.setAccelerator(ksDownload);

		ksCreateLink = KeyStroke.getKeyStroke(KeyEvent.VK_L,
				InputEvent.CTRL_DOWN_MASK);
		aCreateLink = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createLink();
			}
		};
		mCreateLink = new JMenuItem(
				TextHolder.getString("folderview.createLink"));
		mCreateLink.addActionListener(aCreateLink);
		map.put(ksCreateLink, "ksCreateLink");
		act.put("ksCreateLink", aCreateLink);
		mCreateLink.setAccelerator(ksCreateLink);

	}

	/**
	 * 
	 */
	protected void createLink() {
		JTextField txtLinkName = new JTextField(30);
		JTextField txtFileName = new JTextField(30);
		JCheckBox chkHardLink = new JCheckBox(
				TextHolder.getString("folderview.hardLink"));

		if (folderView.getSelectedFiles().length > 0) {
			FileInfo info = folderView.getSelectedFiles()[0];
			txtFileName.setText(info.getPath());
		}

		while (JOptionPane.showOptionDialog(remoteFolderView.getWindow(),
				new Object[] { TextHolder.getString("folderview.linkName"),
						txtLinkName,
						TextHolder.getString("folderview.fileName"),
						txtFileName, chkHardLink },
				TextHolder.getString("folderview.createLink"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				null, null) == JOptionPane.OK_OPTION) {
			if (txtLinkName.getText().length() > 0
					&& txtFileName.getText().length() > 0) {
				createLinkAsync(txtFileName.getText(), txtLinkName.getText(),
						chkHardLink.isSelected());
				break;
			}
		}

	}

	/**
	 * 
	 */
	private void createLinkAsync(String src, String dst, boolean hardLink) {
		remoteFolderView.createLink(src, dst, hardLink);
	}

	protected void sendFiles(FileInfo[] selectedFiles) {

		List<String> files = new ArrayList<>();
		List<String> folders = new ArrayList<>();

		for (FileInfo info : selectedFiles) {
			if (info.getType() == FileType.Directory
					|| info.getType() == FileType.DirLink) {
				folders.add(info.getPath());
			} else {
				files.add(info.getPath());
			}
		}

		ScpTransferWidget scpWidget = new ScpTransferWidget(
				remoteFolderView.getInfo(), files, folders,
				remoteFolderView.getSession(),
				SwingUtilities.windowForComponent(remoteFolderView));
		scpWidget.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nixexplorer.widgets.folderview.ContextMenuBuilder#createMenu(javax.swing.
	 * JPopupMenu, nixexplorer.core.FileInfo[], java.util.List)
	 */
	@Override
	public void createMenu(JPopupMenu popup, FileInfo[] selectedFiles) {
		createMenuContext(popup, selectedFiles);
	}

	private void openNewTab() {
		String path = folderView.getCurrentPath();
		String name = PathUtils.getFileName(path);

		FileInfo[] selectedFiles = folderView.getSelectedFiles();
		if (selectedFiles != null && selectedFiles.length > 0) {
			FileInfo info = selectedFiles[0];
			if (info.getType() == FileType.DirLink
					|| info.getType() == FileType.Directory) {
				path = info.getPath();
				name = info.getName();
			}
		}

		try {
			remoteFolderView.openNewTab(name, path);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	private void rename(FileInfo info) {
		String text = JOptionPane
				.showInputDialog(TextHolder.getString("folderview.renameTitle")
						+ "\n" + info.getName());
		if (text != null && text.length() > 0) {
			renameAsync(info.getPath(), PathUtils
					.combineUnix(PathUtils.getParent(info.getPath()), text));
		}
	}

	private void renameAsync(String oldName, String newName) {
		remoteFolderView.disableView();
		new Thread(() -> {
			try {
				remoteFolderView.ensureConnected();
				remoteFolderView.getFs().rename(oldName, newName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				renameWithPriviledge(oldName, newName);
				return;
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						TextHolder.getString("folderview.genericError"));
			} finally {
				notifyReload(null);
				remoteFolderView.enableView();
			}
		}).start();
	}

	private void renameWithPriviledge(String oldName, String newName) {
		SwingUtilities.invokeLater(() -> {
			String suCmd = AskForPriviledgeDlg.askForPriviledge();
			if (suCmd == null)
				return;
			StringBuilder command = new StringBuilder();
			command.append(suCmd + " ");
			boolean sudo = false;
			sudo = suCmd.startsWith("sudo");
			if (!sudo) {
				command.append("'");
			}
			command.append("mv \"" + oldName + "\" \"" + newName + "\"");
			if (!sudo) {
				command.append("; exit'");
			} else {
				command.append("; exit");
			}
			System.out.println("Command: " + command);
			String[] args = new String[2];
			args[0] = "-c";
			args[1] = command.toString();
			System.out.println("Opening dialog window");
			RemoteFolderViewUtils.openTerminalDialog(command.toString(),
					remoteFolderView);
		});
	}

	private void mkdirWithPriviledge(String path, String newFolder) {
		SwingUtilities.invokeLater(() -> {
			String suCmd = AskForPriviledgeDlg.askForPriviledge();
			if (suCmd == null) {
				return;
			}
			StringBuilder command = new StringBuilder("cd \"" + path + "\"; ");
			command.append(suCmd + " ");
			boolean sudo = false;
			sudo = suCmd.startsWith("sudo");
			if (!sudo) {
				command.append("'");
			}
			command.append("mkdir \"" + newFolder + "\"");
			if (!sudo) {
				command.append("; exit'");
			} else {
				command.append("; exit");
			}
			System.out.println("Command: " + command);
			String[] args = new String[2];
			args[0] = "-c";
			args[1] = command.toString();
			System.out.println("Opening terminal: " + command);
			RemoteFolderViewUtils.openTerminalDialog(command.toString(),
					remoteFolderView);
		});
	}

	private void touchWithPriviledge(String path, String newFile) {
		SwingUtilities.invokeLater(() -> {
			String suCmd = AskForPriviledgeDlg.askForPriviledge();
			if (suCmd == null) {
				return;
			}
			StringBuilder command = new StringBuilder("cd \"" + path + "\"; ");
			command.append(suCmd + " ");
			boolean sudo = false;
			sudo = suCmd.startsWith("sudo");
			if (!sudo) {
				command.append("'");
			}
			command.append("touch \"" + newFile + "\"");
			if (!sudo) {
				command.append("; exit'");
			} else {
				command.append("; exit");
			}
			System.out.println("Command: " + command);
			String[] args = new String[2];
			args[0] = "-c";
			args[1] = command.toString();
			System.out.println("Opening terminal: " + command);
			RemoteFolderViewUtils.openTerminalDialog(command.toString(),
					remoteFolderView);
		});
	}

	private void delete(FileInfo[] targetList) {
		remoteFolderView.disableView();
		new Thread(() -> {
			try {
				remoteFolderView.ensureConnected();
				try {
					ShellActions.delete(Arrays.asList(targetList),
							remoteFolderView.getWrapper());
				} catch (FileNotFoundException e) {
					System.out.println("file not found");
					e.printStackTrace();
					throw e;
				} catch (Exception e) {
					e.printStackTrace();
					for (FileInfo s : targetList) {
						remoteFolderView.getFs().delete(s);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				deletePriviledge(targetList);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						TextHolder.getString("folderview.genericError"));
			} finally {
				notifyReload(null);
				remoteFolderView.enableView();
			}

		}).start();
	}

	private void deletePriviledge(FileInfo[] targetList) {
		String suCmd = AskForPriviledgeDlg.askForPriviledge();
		if (suCmd == null) {
			return;
		}
		StringBuilder command = new StringBuilder();
		command.append(suCmd + " ");
		boolean sudo = false;
		sudo = suCmd.startsWith("sudo");
		if (!sudo) {
			command.append("'");
		}

		StringBuilder sb = new StringBuilder("rm -rf ");

		for (FileInfo file : targetList) {
			sb.append("\"" + file.getPath() + "\" ");
		}

		System.out.println("Delete command2: rm -rf " + sb);
		command.append(sb);
		command.append("; exit");

		if (!sudo) {
			command.append("'");
		}
		System.out.println("Command: " + command);
		String[] args = new String[2];
		args[0] = "-c";
		args[1] = command.toString();
		RemoteFolderViewUtils.openTerminalDialog(command.toString(),
				remoteFolderView);
	}

	protected void newFile(String folder) {
		remoteFolderView.disableView();
		new Thread(() -> {
			String text = null;
			try {
				remoteFolderView.ensureConnected();
				while (true) {
					text = JOptionPane.showInputDialog(
							TextHolder.getString("folderview.newFile"));
					if (text == null || text.length() < 1) {
						return;
					}
					for (FileInfo f : folderView.getCurrentFiles()) {
						if (f.getName().equals(text)) {
							JOptionPane.showMessageDialog(null,
									"File with same name already exists");
							break;
						} else {
							remoteFolderView.getFs().createFile(
									PathUtils.combineUnix(folder, text));
							return;
						}
					}

				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				touchWithPriviledge(folder, text);
			} catch (Exception e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null,
						TextHolder.getString("folderview.genericError"));
			} finally {
				notifyReload(null);
				remoteFolderView.enableView();
			}
		}).start();

	}

	protected void newFolder(String folder) {
		String text = JOptionPane
				.showInputDialog(TextHolder.getString("folderview.newFolder"));
		if (text != null && text.length() > 0) {
			remoteFolderView.disableView();
			new Thread(() -> {
				try {
					remoteFolderView.ensureConnected();
					remoteFolderView.getFs()
							.mkdir(PathUtils.combineUnix(folder, text));
					folderView.render(folder);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					mkdirWithPriviledge(folder, text);
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null,
							TextHolder.getString("folderview.genericError"));
				} finally {
					notifyReload(null);
					remoteFolderView.enableView();
				}
			}).start();
		}
	}

	private void changePermission(FileInfo[] files) {
		if (files == null || files.length == 0) {
			return;
		}
		PermissionsDialog pdlg = new PermissionsDialog(
				remoteFolderView.getWindow(), files.length > 1);
		if (files.length == 1) {
			pdlg.setDetails(files[0]);
		} else {
			pdlg.setMultipleDetails(files);
		}
		pdlg.setVisible(true);
		if (pdlg.getDialogResult() == JOptionPane.OK_OPTION) {
			int perm = pdlg.getPermissions();
			String[] paths = new String[files.length];
			int i = 0;
			for (FileInfo r : files) {
				paths[i++] = r.getPath();
			}
			chmodAsync(perm, paths);
		}
	}

	private void chmodAsync(int perm, String paths[]) {
		remoteFolderView.disableView();
		new Thread(() -> {
			try {
				remoteFolderView.ensureConnected();
				for (String path : paths) {
					remoteFolderView.getFs().chmod(perm, path);
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						TextHolder.getString("folderview.genericError"));
			}

			notifyReload(null);
			remoteFolderView.enableView();
		}).start();
	}

	protected void copyToClipboard(boolean cut) {
		TransferFileInfo info = createTransferInfo();
		if (info != null) {
			info.setAction(cut ? TransferFileInfo.Action.CUT
					: TransferFileInfo.Action.COPY);
			AppClipboard.setContent(info);
		}
	}

	private TransferFileInfo createTransferInfo() {

		TransferFileInfo sf = new TransferFileInfo();
		sf.addInfo(remoteFolderView.getInfo());
		List<String> filelist = new ArrayList<>();
		List<String> folderlist = new ArrayList<>();
		for (FileInfo f : folderView.getSelectedFiles()) {
			if (f.getType() == FileType.Directory) {
				folderlist.add(f.getPath());
			} else {
				filelist.add(f.getPath());
			}
		}
		sf.setSourceFiles(filelist);
		sf.setSourceFolders(folderlist);
		return sf;

	}

	protected void addToFavourites() {
		FileInfo[] files = folderView.getSelectedFiles();
		if (files.length == 0) {
			addBookmark(folderView.getCurrentPath());
		} else {
			for (FileInfo f : files) {
				if (f.getType() == FileType.Directory
						|| f.getType() == FileType.DirLink) {
					addBookmark(f.getPath());
				}
			}
		}
		SessionStore.updateFavourites(remoteFolderView.getInfo().getId(),
				remoteFolderView.getInfo().getFavouriteFolders());
		loadFavourites();
		//SessionStore.getSharedInstance().save(null);
	}

	private void addBookmark(String str) {
		remoteFolderView.getInfo().getFavouriteFolders().add(str);
	}

	private void loadFavourites() {
		folderView.loadFavourites(
				remoteFolderView.getInfo().getFavouriteFolders());
	}

	private void createBuitinItems1(int selectionCount, JPopupMenu popup) {
		if (selectionCount == 1) {
			if (folderView.getSelectedFiles()[0].getType() == FileType.Directory
					|| folderView.getSelectedFiles()[0]
							.getType() == FileType.DirLink) {
				popup.add(mOpenInTab);
			}

			if ((folderView.getSelectedFiles()[0].getType() == FileType.File
					|| folderView.getSelectedFiles()[0]
							.getType() == FileType.FileLink)) {
				popup.add(mOpen);
				popup.add(mOpenWith);
			}
		}

		if (selectionCount > 0) {
			popup.add(mCut);
			popup.add(mCopy);
		}

		if (AppClipboard.getContent() instanceof TransferFileInfo) {
			popup.add(mPaste);
		}

		if (selectionCount == 1) {
			popup.add(mRename);
		}
	}

	private void createBuitinItems2(int selectionCount, JPopupMenu popup) {
		if (selectionCount > 0) {
			popup.add(mDelete);
			popup.add(mSendFiles);
		}

		if (selectionCount < 1) {
			popup.add(mNewFolder);
			popup.add(mNewFile);
		}

		// check only if folder is selected
		boolean allFolder = true;
		for (FileInfo f : folderView.getSelectedFiles()) {
			if (f.getType() != FileType.Directory
					&& f.getType() != FileType.DirLink) {
				allFolder = false;
				break;
			}
		}

		if (selectionCount >= 1 && allFolder) {
			popup.add(mAddToFav);
		}

		if (selectionCount == 0) {
			popup.add(mUpload);
		}

		if (selectionCount > 0) {
			popup.add(mDownload);
		}

		if (selectionCount <= 1) {
			popup.add(mCreateLink);
		}

		if (selectionCount >= 1) {
			popup.add(mChangePerm);
		}

	}

	private void createMenuContext(JPopupMenu popup, FileInfo[] files) {
		popup.removeAll();

		int selectionCount = files.length;

		createBuitinItems1(selectionCount, popup);

		for (ContextMenuEntry ent : ContextMenuRegistry.getEntryList()) {
			// System.out.println(ent);
			if (selectionCount > 1) {
				if (ent.isSupportsMultipleItems()) {
					createMenuItem(ent, popup);
				}
			} else if (selectionCount == 1) {
				FileInfo info = files[0];
				if (info.getType() == FileType.Directory
						|| info.getType() == FileType.DirLink) {
					if (ent.isFolderSupported()) {
						createMenuItem(ent, popup);
					}
				} else {
					String path = info.getPath();
					String[] supportedExts = ent.getFileExt();
					if (supportedExts.length > 0) {
						boolean supported = false;
						for (String ext : supportedExts) {
							if (ext.length() == 0 || path.endsWith(ext)) {
								supported = true;
								break;
							}
						}
						if (!supported) {
							continue;
						}
						createMenuItem(ent, popup);
					}
				}
			} else {
				if (ent.isSupportsEmptySelection()) {
					createMenuItem(ent, popup);
				}
			}
		}

		createBuitinItems2(selectionCount, popup);
	}

	private void createMenuItem(ContextMenuEntry ent, JPopupMenu popup) {
		System.out.println(ent);
		JMenuItem item = new JMenuItem(ent.getMenuText());
		item.addActionListener(e -> {
			for (ContextMenuEntry c : ContextMenuRegistry.getEntryList()) {
				if (c == ent) {
					try {
						AppSession desktop = remoteFolderView.getSession();
						if (desktop != null) {
							List<String> argsList = new ArrayList<>();
							for (int i = 0; i < ent.getArgs().length; i++) {
								String arg = ent.getArgs()[i];
								if ("%d".equals(arg)) {
									argsList.add(folderView.getCurrentPath());
								} else if ("%f".equals(arg)) {
									for (FileInfo info : folderView
											.getSelectedFiles()) {
										argsList.add(info.getPath());
									}
								} else {
									argsList.add(arg);
								}
							}
							String[] a = new String[argsList.size()];
							a = argsList.toArray(a);
							desktop.createWidget(ent.getClassName(), a);
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					break;
				}
			}
		});
		popup.add(item);
	}

	public void uploadFiles(File[] files) {
		List<String> fs = new ArrayList<>();
		List<String> ds = new ArrayList<>();
		for (File f : files) {
			if (f.isDirectory()) {
				ds.add(f.getAbsolutePath());
			} else {
				fs.add(f.getAbsolutePath());
			}
		}
		List<String> args = new ArrayList<>();
		args.add("u");
		args.add(folderView.getCurrentPath());
		args.add(fs == null ? "0" : fs.size() + "");
		args.add(ds == null ? "0" : ds.size() + "");
		args.addAll(fs);
		args.addAll(ds);

		String[] arr = new String[args.size()];
		arr = args.toArray(arr);

		System.out.println("args: " + args);

		remoteFolderView.getAppSession()
				.createWidget(CopyWidget.class.getName(), arr);
	}

	private void downloadFiles(FileInfo[] files, String localPath) {
		List<String> fs = new ArrayList<>();
		List<String> ds = new ArrayList<>();
		for (FileInfo f : files) {
			if (f.getType() == FileType.Directory
					|| f.getType() == FileType.DirLink) {
				ds.add(f.getPath());
			} else {
				fs.add(f.getPath());
			}
		}
		List<String> args = new ArrayList<>();
		args.add("d");
		args.add(localPath);
		args.add(fs == null ? "0" : fs.size() + "");
		args.add(ds == null ? "0" : ds.size() + "");
		args.addAll(fs);
		args.addAll(ds);

		String[] arr = new String[args.size()];
		arr = args.toArray(arr);

		System.out.println("Local drop args: " + args);
		try {
			remoteFolderView.getSession()
					.createWidget(CopyWidget.class.getName(), arr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void notifyReload(String path) {
		SwingUtilities.invokeLater(() -> {
			remoteFolderView.getAppSession().remoteFileSystemWasChanged(path);
		});
	}

	/**
	 * 
	 */
	protected void openLogViewer() {
		FileInfo[] selectedFiles = folderView.getSelectedFiles();
		if (selectedFiles != null && selectedFiles.length == 1) {
			FileInfo info = selectedFiles[0];
			remoteFolderView.getAppSession().createWidget(
					LogViewerWidget.class.getName(),
					new String[] { info.getPath() });
		}
	}

	/**
	 * 
	 */
	protected void openTextEditor() {
		FileInfo[] selectedFiles = folderView.getSelectedFiles();
		if (selectedFiles != null && selectedFiles.length == 1) {
			FileInfo info = selectedFiles[0];
			remoteFolderView.getAppSession().createWidget(
					FormattedEditorWidget.class.getName(),
					new String[] { info.getPath() });
		}
	}

	/**
	 * 
	 */
	protected void openDefApp() {
		FileInfo[] selectedFiles = folderView.getSelectedFiles();
		if (selectedFiles != null && selectedFiles.length == 1) {
			FileInfo info = selectedFiles[0];
			remoteFolderView.getAppSession().createWidget(
					ExternalEditorWidget.class.getName(),
					new String[] { "-e", info.getPath() });
		}
	}

	/**
	 * 
	 */
	protected void openDefaultAction() {
		FileInfo[] selectedFiles = folderView.getSelectedFiles();
		if (selectedFiles != null && selectedFiles.length == 1) {
			FileInfo info = selectedFiles[0];
			if (info.getType() == FileType.Directory
					|| info.getType() == FileType.DirLink) {

			}
		}
		int action = remoteFolderView.getAppSession().getApplicationContext()
				.getConfig().getFileBrowser().getDblClickAction();
		switch (action) {
		case 0:
			openTextEditor();
			break;
		case 1:

			break;
		case 2:
			openDefApp();
			break;
		}
	}

}

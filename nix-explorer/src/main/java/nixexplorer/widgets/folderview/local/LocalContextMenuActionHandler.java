package nixexplorer.widgets.folderview.local;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import nixexplorer.AppClipboard;
import nixexplorer.PathUtils;
import nixexplorer.TextHolder;
import nixexplorer.core.FileInfo;
import nixexplorer.core.FileType;
import nixexplorer.widgets.dnd.TransferFileInfo;
import nixexplorer.widgets.dnd.TransferFileInfo.Action;
import nixexplorer.widgets.folderview.ContextMenuActionHandler;
import nixexplorer.widgets.folderview.FolderViewWidget;

public class LocalContextMenuActionHandler implements ContextMenuActionHandler {

	private JMenuItem mOpen, mRename, mDelete, mNewFile, mNewFolder, mCopy,
			mPaste, mCut, mAddToFav;

	private FolderViewWidget folderView;
	private LocalFolderViewWidget localFolderView;

	public LocalContextMenuActionHandler(
			LocalFolderViewWidget localFolderView) {
		super();
		this.localFolderView = localFolderView;
	}

	@Override
	public void createMenu(JPopupMenu popup, FileInfo[] selectedFiles) {
		createMenuContext(popup, selectedFiles);
	}

	private void createBuitinItems1(int selectionCount, JPopupMenu popup) {
		popup.add(mOpen);
		if (selectionCount == 1) {
			popup.add(mRename);
		}

		if (selectionCount > 0) {
			popup.add(mCopy);
			popup.add(mCut);
		}

		if (AppClipboard.getContent() instanceof TransferFileInfo) {
			popup.add(mPaste);
		}
	}

	private void createBuitinItems2(int selectionCount, JPopupMenu popup) {
		popup.add(mNewFolder);
		popup.add(mNewFile);
		// check only if folder is selected
		popup.add(mAddToFav);
	}

	private void createMenuContext(JPopupMenu popup, FileInfo[] files) {
		popup.removeAll();
		int selectionCount = files.length;
		createBuitinItems1(selectionCount, popup);
		createBuitinItems2(selectionCount, popup);
	}

	@Override
	public void install(FolderViewWidget c) {
		this.folderView = c;
		this.initMenuItems();
	}

	private void initMenuItems() {
		mOpen = new JMenuItem(TextHolder.getString("folderview.opennewtab"));
		mOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openNewTab();
			}
		});

		mRename = new JMenuItem(TextHolder.getString("folderview.rename"));
		mRename.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rename(folderView.getSelectedFiles()[0]);
			}
		});

		mDelete = new JMenuItem(TextHolder.getString("folderview.delete"));
		mDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				delete(folderView.getSelectedFiles());
			}
		});

		mNewFile = new JMenuItem(TextHolder.getString("folderview.newFile"));
		mNewFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				newFile();
			}
		});

		mNewFolder = new JMenuItem(
				TextHolder.getString("folderview.newFolder"));
		mNewFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFolder(folderView.getCurrentPath());
			}
		});

		mCopy = new JMenuItem(TextHolder.getString("folderview.copy"));
		mCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copyToClipboard(false);
			}
		});

		mPaste = new JMenuItem(TextHolder.getString("folderview.paste"));
		mPaste.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (AppClipboard.getContent() instanceof TransferFileInfo) {
					TransferFileInfo info = (TransferFileInfo) AppClipboard
							.getContent();
					localFolderView.pasteItem(info, folderView);
					if (info.getAction() == Action.CUT) {
						AppClipboard.setContent(null);
					}
				}
			}
		});

		mCut = new JMenuItem(TextHolder.getString("folderview.cut"));
		mCut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copyToClipboard(true);
			}
		});

		mAddToFav = new JMenuItem(TextHolder.getString("folderview.bookmark"));
		mAddToFav.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addToFavourites();
			}
		});
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
			localFolderView.openNewTab(name, path);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	private void renameAsync(String oldName, String newName) {
		localFolderView.disableView();
		new Thread(() -> {
			try {
				localFolderView.getFs().rename(oldName, newName);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						TextHolder.getString("folderview.genericError"));
			} finally {
				localFolderView.enableView();
			}
		}).start();
	}

	private void rename(FileInfo info) {
		String text = JOptionPane
				.showInputDialog(TextHolder.getString("folderview.renameTitle")
						+ "\n" + info.getName());
		if (text != null && text.length() > 0) {
			renameAsync(info.getPath(), PathUtils.combine(
					PathUtils.getParent(info.getPath()), text, File.separator));
		}
	}

	private void delete(FileInfo[] targetList) {
		localFolderView.disableView();
		new Thread(() -> {
			try {
				for (FileInfo s : targetList) {
					localFolderView.getFs().delete(s);
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						TextHolder.getString("folderview.genericError"));
			} finally {
				localFolderView.enableView();
			}

		}).start();
	}

	protected void newFolder(String folder) {
		String text = JOptionPane.showInputDialog(
				TextHolder.getString("folderview.renameTitle"));
		if (text != null && text.length() > 0) {
			localFolderView.disableView();
			new Thread(() -> {
				try {
					localFolderView.getFs().mkdir(
							PathUtils.combine(folder, text, File.separator));
					folderView.render(folder);
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null,
							TextHolder.getString("folderview.genericError"));
				}
			}).start();
		}
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
		loadFavourites();
	}

	private void addBookmark(String str) {
		localFolderView.getSession().getApplicationContext().getConfig()
				.getFileBrowser().getLocalBookmarks().add(str);
		localFolderView.getSession().getApplicationContext().getConfig().save();
	}

	private void loadFavourites() {
		folderView.loadFavourites(
				localFolderView.getInfo().getFavouriteFolders());
	}

}

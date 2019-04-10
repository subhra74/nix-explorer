/**
 * 
 */
package nixexplorer.widgets.folderview.local;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import nixexplorer.PathUtils;
import nixexplorer.TextHolder;
import nixexplorer.app.session.SessionStore;
import nixexplorer.widgets.folderview.FolderViewWidget;
import nixexplorer.widgets.folderview.TreeContextMenuHandler;

/**
 * @author subhro
 *
 */
public class LocalTreeContextMenuHandler implements TreeContextMenuHandler {
	private JMenuItem mOpenInTab, mCopyPath, mAddToFav;

	private LocalFolderViewWidget localFolderView;

	private FolderViewWidget folderView;

	private String path;

	/**
	 * 
	 */
	public LocalTreeContextMenuHandler(LocalFolderViewWidget localFolderView) {
		this.localFolderView = localFolderView;
		mOpenInTab = new JMenuItem(
				TextHolder.getString("folderview.opennewtab"));
		mOpenInTab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openNewTab();
			}
		});

		mCopyPath = new JMenuItem(TextHolder.getString("folderview.copyPath"));
		mCopyPath.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copyPath();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nixexplorer.widgets.folderview.TreeContextMenuHandler#createMenu(javax.
	 * swing.JPopupMenu, java.lang.String)
	 */
	@Override
	public void createMenu(JPopupMenu popup, String folder) {
		this.path = folder;
		popup.removeAll();
		popup.add(this.mOpenInTab);
		popup.add(this.mCopyPath);
		popup.add(this.mAddToFav);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nixexplorer.widgets.folderview.TreeContextMenuHandler#install(nixexplorer
	 * .widgets.folderview.FolderViewWidget)
	 */
	@Override
	public void install(FolderViewWidget c) {
		this.folderView = c;
	}

	private void openNewTab() {
		String name = PathUtils.getFileName(path);
		try {
			localFolderView.openNewTab(name, path);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	private void addBookmark(String str) {
		localFolderView.listFavourites().add(str);
	}

	protected void addToFavourites() {
		addBookmark(path);
		SessionStore.updateFavourites(localFolderView.getInfo().getId(),
				localFolderView.listFavourites(), null);
		loadFavourites();
	}

	private void loadFavourites() {
		folderView.loadFavourites(localFolderView.listFavourites());
	}

	/**
	 * 
	 */
	protected void copyPath() {
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new StringSelection(path), null);
	}

}

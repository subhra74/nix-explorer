/**
 * 
 */
package nixexplorer.widgets.folderview.remote;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import nixexplorer.PathUtils;
import nixexplorer.TextHolder;
import nixexplorer.core.FileInfo;
import nixexplorer.core.FileType;
import nixexplorer.core.SessionStore;
import nixexplorer.widgets.folderview.FolderViewWidget;
import nixexplorer.widgets.folderview.TreeContextMenuHandler;

/**
 * @author subhro
 *
 */
public class RemoteTreeContextMenuHandler implements TreeContextMenuHandler {

	private JMenuItem mOpenInTab, mCopyPath, mAddToFav, mTerminal;

	private RemoteFolderViewWidget remoteFolderView;

	private FolderViewWidget folderView;

	private String path;

	/**
	 * 
	 */
	public RemoteTreeContextMenuHandler(
			RemoteFolderViewWidget remoteFolderView) {
		this.remoteFolderView = remoteFolderView;
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

		mTerminal = new JMenuItem(TextHolder.getString("folderview.openterm"));
		mTerminal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openTerminal();
			}
		});

	}

	/**
	 * 
	 */
	protected void copyPath() {
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new StringSelection(path), null);
	}

	private void openTerminal() {
		remoteFolderView.getSession().createWidget(
				"nixexplorer.widgets.console.TabbedConsoleWidget",
				new String[] { "-o", this.path });
	}

	public void createMenu(JPopupMenu popup, String folder) {
		this.path = folder;
		popup.removeAll();
		popup.add(this.mOpenInTab);
		popup.add(this.mCopyPath);
		popup.add(this.mTerminal);
		popup.add(this.mAddToFav);
	}

	public void install(FolderViewWidget c) {
		this.folderView = c;
	}

	private void openNewTab() {
		String name = PathUtils.getFileName(path);
		try {
			remoteFolderView.openNewTab(name, path);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	private void addBookmark(String str) {
		remoteFolderView.getInfo().getFavouriteFolders().add(str);
	}

	protected void addToFavourites() {
		addBookmark(path);
		loadFavourites();
		SessionStore.getSharedInstance().save(null);
	}

	private void loadFavourites() {
		folderView.loadFavourites(
				remoteFolderView.getInfo().getFavouriteFolders());
	}
}

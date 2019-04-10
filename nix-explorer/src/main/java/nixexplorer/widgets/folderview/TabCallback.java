package nixexplorer.widgets.folderview;

import java.awt.Component;
import java.util.List;

import nixexplorer.app.session.AppSession;
import nixexplorer.core.FileSystemProvider;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.widgets.Widget;
import nixexplorer.widgets.dnd.TransferFileInfo;
import nixexplorer.widgets.listeners.AppMessageListener;

public interface TabCallback {
	public void openNewTab(String title, String path);

	public void updateTitle(String title, Component c);

	public void openTerminal(String command);

	public void editFile(String fileName);

	public void disableUI();

	public void enableUI();

	public SessionInfo getInfo();

	public AppMessageListener getAppListener();

	public AppSession getSession();

	public Widget getWidget();

	public FileSystemProvider getFs();

	public void reconnectFs() throws Exception;

	public boolean handleFileDrop(Object infoList, FolderViewWidget widget);

	public void pasteItem(TransferFileInfo info, FolderViewWidget w);

	public void moveFiles(String targetFolder, List<String> sourceFiles,
			List<String> sourceFolders, boolean copy, FolderViewWidget w);

	public List<String> listFavourites();
}

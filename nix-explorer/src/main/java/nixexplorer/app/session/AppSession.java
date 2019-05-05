/**
 * 
 */
package nixexplorer.app.session;

import java.awt.Window;
import java.io.File;
import java.nio.file.WatchKey;

import nixexplorer.app.components.TabbedChild;
import nixexplorer.worker.ChangeWatcher;

/**
 * @author subhro
 *
 */
public interface AppSession {
	public void createWidget(String clazz, String[] args);

	public File getDirectory();

	public boolean closeTab(TabbedChild c);

	public void configChanged();

	public void addToSession(Object obj);

	public void remoteFileSystemWasChanged(String path);

	public void createFolderView(String path);

	public void close();

	public void unregisterSessionAwareComponent(SessionEventAware c);

	public Window getWindow();

	public ChangeWatcher getChangeWatcher();
}

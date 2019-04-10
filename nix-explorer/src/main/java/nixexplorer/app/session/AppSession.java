/**
 * 
 */
package nixexplorer.app.session;

import java.io.File;
import java.util.Map;

import nixexplorer.app.AppContext;
import nixexplorer.app.components.TabbedChild;
import nixexplorer.app.settings.AppConfig;
import nixexplorer.worker.editwatcher.ChangeUploader;

/**
 * @author subhro
 *
 */
public interface AppSession {
	public void createWidget(String clazz, String[] args);

//	public Map<String, ChangeUploader> getEditWatchers();
//
//	public void setEditWatchers(Map<String, ChangeUploader> editWatchers);

	public void registerEditWatchers(String file, ChangeUploader editWatcher);

	public File getDirectory();

	public boolean closeTab(TabbedChild c);

	public void configChanged();

	public void addToSession(Object obj);

	public void remoteFileSystemWasChanged(String path);
	
	public void close();
}

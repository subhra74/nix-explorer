/**
 * 
 */
package nixexplorer.worker;

/**
 * @author subhro
 *
 */
public interface ChangeWatcher {
	public void watchForChanges(WatcherEntry ent);

	public void cancelPreviousUploads(WatcherEntry ent);
	
	public void close();
	
	public void uploadComplete(String path,UploadTask task);
}

/**
 * 
 */
package nixexplorer.worker.editwatcher;

/**
 * @author subhro
 *
 */
public interface ChangeUploader {
	public long getLastModified();

	public void onFileChanged(long lastModified);
}

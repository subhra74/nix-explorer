/**
 * 
 */
package nixexplorer.app.session;

/**
 * @author subhro
 *
 */
public interface SessionEventAware {
	public void configChanged();

	public void remoteFileSystemUpdated(String path);

	public void localFileSystemUpdated(String path);
}

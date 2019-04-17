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

	public void fileSystemUpdated(String path);
}

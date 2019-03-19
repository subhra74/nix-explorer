/**
 * 
 */
package nixexplorer.widgets.logviewer;

/**
 * @author subhro
 *
 */
public interface LogNotificationListener {
	public void logChanged();

	public boolean retry();

	public void downloadProgress(int prg);

	public void setIndeterminate(boolean a);
	
	public boolean shouldUpdate();
}

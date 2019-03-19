/**
 * 
 */
package nixexplorer.app.components;

/**
 * @author subhro
 *
 */
public interface DisposableView {
	public boolean viewClosing();

	public void viewClosed();

	public boolean getWidgetClosed();

	public void setWidgetClosed(boolean widgetClosed);

	public boolean closeView();
}

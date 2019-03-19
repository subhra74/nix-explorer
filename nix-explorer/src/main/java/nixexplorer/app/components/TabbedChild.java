/**
 * 
 */
package nixexplorer.app.components;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * @author subhro
 *
 */
public interface TabbedChild extends DisposableView {

	public void tabSelected();

	public Icon getIcon();

	public String getTitle();

	public void setLabel(JLabel labelRef);
}

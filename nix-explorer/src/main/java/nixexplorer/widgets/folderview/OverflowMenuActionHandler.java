/**
 * 
 */
package nixexplorer.widgets.folderview;

import javax.swing.JPopupMenu;

import nixexplorer.core.FileInfo;

/**
 * @author subhro
 *
 */
public interface OverflowMenuActionHandler {
	public void createMenu(JPopupMenu popup);

	public void install(FolderViewWidget c);
}

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
public interface TreeContextMenuHandler {
	public void createMenu(JPopupMenu popup, String folder);

	public void install(FolderViewWidget c);
}

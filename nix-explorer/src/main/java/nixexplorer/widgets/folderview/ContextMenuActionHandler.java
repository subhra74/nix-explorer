/**
 * 
 */
package nixexplorer.widgets.folderview;

import java.util.List;

import javax.swing.JPopupMenu;

import nixexplorer.core.FileInfo;

/**
 * @author subhro
 *
 */
public interface ContextMenuActionHandler {
	public void createMenu(JPopupMenu popup, FileInfo[] selectedFiles);

	public void install(FolderViewWidget c);
}

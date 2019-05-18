/**
 * 
 */
package nixexplorer.widgets.folderview;

import java.util.List;

import javax.swing.JPopupMenu;

import nixexplorer.core.FileInfo;
import nixexplorer.worker.DownloadTask.OpenMode;

/**
 * @author subhro
 *
 */
public interface ContextMenuActionHandler {
	public void createMenu(JPopupMenu popup, FileInfo[] selectedFiles);

	public void install(FolderViewWidget c);
	
	public void openApp(OpenMode mode);
}

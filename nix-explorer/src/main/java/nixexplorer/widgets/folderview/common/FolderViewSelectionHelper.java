/**
 * 
 */
package nixexplorer.widgets.folderview.common;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowFilter.Entry;
import javax.swing.table.TableRowSorter;

import org.apache.commons.io.FilenameUtils;

import nixexplorer.core.FileInfo;
import nixexplorer.core.FileType;
import nixexplorer.widgets.folderview.FolderViewTableModel;
import nixexplorer.widgets.folderview.FolderViewWidget;
import nixexplorer.worker.editwatcher.FileEntry;

/**
 * @author subhro
 *
 */
public class FolderViewSelectionHelper {
	private JTable table;
	private FolderViewTableModel model;
	private FolderViewWidget folderView;
	private TableRowSorter<FolderViewTableModel> sorter;
	private String wildcardMatcher;

	/**
	 * 
	 */
	public FolderViewSelectionHelper(FolderViewWidget folderView, JTable table,
			FolderViewTableModel model,
			TableRowSorter<FolderViewTableModel> sorter) {
		this.table = table;
		this.model = model;
		this.folderView = folderView;
		this.sorter = sorter;
		this.wildcardMatcher = "";
	}

	public void refresh() {
		folderView.render(folderView.getCurrentPath(), false);
	}

	public void selectAll() {
		table.selectAll();
	}

	public void clearSelection() {
		table.clearSelection();
	}

	public void inverseSelection() {
		int[] selectedIndexs = table.getSelectedRows();
		table.selectAll();

		for (int i = 0; i < table.getRowCount(); i++) {
			for (int selectedIndex : selectedIndexs) {
				if (selectedIndex == i) {
					table.removeRowSelectionInterval(i, i);
					break;
				}
			}
		}
	}

	public void applyFilter() {
		wildcardMatcher = JOptionPane.showInputDialog("Filter",
				wildcardMatcher);
		if (wildcardMatcher == null || wildcardMatcher.length() < 1) {
			sorter.setRowFilter(null);
			return;
		}
		sorter.setRowFilter(new RowFilter<FolderViewTableModel, Integer>() {

			@Override
			public boolean include(
					Entry<? extends FolderViewTableModel, ? extends Integer> entry) {
				FileInfo info = entry.getModel()
						.getItemAt(entry.getIdentifier());
				if (FilenameUtils.wildcardMatch(info.getName(),
						wildcardMatcher))
					return true;
				else
					return false;
			}

		});
	}

	public void selectFiltered() {
		String wm = JOptionPane.showInputDialog("Filter");
		if (wm == null || wm.length() < 1) {
			return;
		}

		table.selectAll();

		for (int i = 0; i < table.getRowCount(); i++) {
			FileInfo info = (FileInfo) table.getValueAt(i, 0);
			if (!FilenameUtils.wildcardMatch(info.getName(), wm)) {
				table.removeRowSelectionInterval(i, i);
			}
		}
	}

	public void unselectFiltered() {
		String wm = JOptionPane.showInputDialog("Filter");
		if (wm == null || wm.length() < 1) {
			return;
		}

		int[] selectedIndexs = table.getSelectedRows();

		for (int i = 0; i < table.getRowCount(); i++) {
			for (int selectedIndex : selectedIndexs) {
				if (selectedIndex == i) {
					FileInfo info = (FileInfo) table.getValueAt(i, 0);
					if (FilenameUtils.wildcardMatch(info.getName(), wm)) {
						table.removeRowSelectionInterval(i, i);
					}
					break;
				}
			}
		}
	}

	public void selectSimilarFiles() {
		int r = table.getSelectedRow();
		if (r == -1)
			return;
		table.selectAll();
		FileInfo ent = (FileInfo) table.getValueAt(r, 0);
		FileType type = ent.getType();
		String ext = FilenameUtils.getExtension(ent.getName());
		for (int i = 0; i < table.getRowCount(); i++) {
			FileInfo info = (FileInfo) table.getValueAt(i, 0);
			if (type != info.getType()) {
				table.removeRowSelectionInterval(i, i);
				continue;
			}
			if (type == FileType.File
					|| type == FileType.FileLink && ext.length() > 0) {
				String ext1 = FilenameUtils.getExtension(info.getName());
				if (!ext.equals(ext1)) {
					table.removeRowSelectionInterval(i, i);
				}
			}
		}
	}

	public void unselectSimilarFiles() {
		int[] selectedIndexs = table.getSelectedRows();
		int r = table.getSelectedRow();
		if (r == -1)
			return;
		FileInfo ent = (FileInfo) table.getValueAt(r, 0);
		FileType type = ent.getType();
		String ext = FilenameUtils.getExtension(ent.getName());
		for (int i = 0; i < table.getRowCount(); i++) {
			for (int selectedIndex : selectedIndexs) {
				if (selectedIndex == i) {
					FileInfo info = (FileInfo) table.getValueAt(i, 0);
					if (type == FileType.File
							|| type == FileType.FileLink && ext.length() > 0) {
						String ext1 = FilenameUtils
								.getExtension(info.getName());
						if (ext.equals(ext1)) {
							table.removeRowSelectionInterval(i, i);
						}
					} else {
						if (type == info.getType()) {
							table.removeRowSelectionInterval(i, i);
						}
					}
					break;
				}
			}
		}
	}
}

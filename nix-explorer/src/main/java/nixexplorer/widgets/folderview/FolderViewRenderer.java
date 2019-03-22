package nixexplorer.widgets.folderview;

import java.awt.Component;
import java.time.format.DateTimeFormatter;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import nixexplorer.app.components.FileIcon;
import nixexplorer.core.FileInfo;
import nixexplorer.core.FileType;
import nixexplorer.drawables.icons.ScaledIcon;
import nixexplorer.widgets.util.Utility;

public class FolderViewRenderer implements TableCellRenderer {
	private JLabel label;
	private FileIcon folderIcon, fileIcon;

	public FolderViewRenderer() {
		label = new JLabel();
		label.setOpaque(true);
		folderIcon = new FileIcon(UIManager.getIcon("ListView.smallFolder"),
				true);
		fileIcon = new FileIcon(UIManager.getIcon("ListView.smallFile"), true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		FolderViewTableModel folderViewModel = (FolderViewTableModel) table
				.getModel();
		int r = table.convertRowIndexToModel(row);
		int c = table.convertColumnIndexToModel(column);
		FileInfo ent = folderViewModel.getItemAt(r);
		switch (c) {
		case 0:
			label.setIcon(FolderViewUtility.getIconForFile(ent, folderIcon,
					fileIcon));
			label.setText(ent.getName());
			break;
		case 1:
			label.setIcon(null);
			if (ent.getType() == FileType.Directory
					|| ent.getType() == FileType.DirLink) {
				label.setText("");
			} else {
				label.setText(
						Utility.humanReadableByteCount(ent.getSize(), true));
			}
			break;
		case 2:
			label.setIcon(null);
			label.setText(ent.getType() + "");
			break;
		case 3:
			label.setIcon(null);
			label.setText(Utility.formatDate(ent.getLastModified()));
			break;
		case 4:
			label.setIcon(null);
			label.setText(ent.getPermissionString());
			break;
		default:
			break;
		}

		label.setBackground(isSelected ? table.getSelectionBackground()
				: table.getBackground());
		return label;
	}

	public int getPreferredHeight() {
		label.setText("The quick brown fox jumps over the lazy dog");
		return label.getPreferredSize().height;
	}

}

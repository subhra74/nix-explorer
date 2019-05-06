package nixexplorer.widgets.folderview;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import nixexplorer.core.FileInfo;
import nixexplorer.core.FileType;
import nixexplorer.widgets.util.Utility;

public class FolderViewRenderer implements TableCellRenderer {
	private JLabel label;
	private Font plainFont, boldFont;
//	private FileIcon folderIcon, fileIcon;

	public FolderViewRenderer() {
		label = new JLabel();
		label.setOpaque(true);
		label.setBorder(
				new EmptyBorder(Utility.toPixel(5), Utility.toPixel(10), Utility.toPixel(0), Utility.toPixel(0)));
		label.setIconTextGap(Utility.toPixel(10));
		plainFont = new Font(Font.DIALOG, Font.PLAIN, Utility.toPixel(12));
		boldFont = new Font(Font.DIALOG, Font.BOLD, Utility.toPixel(12));
//		label.setBorder(new CompoundBorder(
//				new MatteBorder(0, Utility.toPixel(0), Utility.toPixel(0), 0, UIManager.getColor("Panel.background")),
//				new EmptyBorder(0, Utility.toPixel(10), 0, 0)));
//		folderIcon = new FileIcon(UIManager.getIcon("ListView.smallFolder"),
//				true);
//		fileIcon = new FileIcon(UIManager.getIcon("ListView.smallFile"), true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		FolderViewTableModel folderViewModel = (FolderViewTableModel) table.getModel();
		int r = table.convertRowIndexToModel(row);
		int c = table.convertColumnIndexToModel(column);
		FileInfo ent = folderViewModel.getItemAt(r);
		label.setFont(c == 0 ? boldFont : plainFont);
		switch (c) {
		case 0:
			label.setIcon(FolderViewUtility.getIconForFile(ent, false));
			label.setText(ent.getName());
			break;
		case 1:
			label.setIcon(null);
			if (ent.getType() == FileType.Directory || ent.getType() == FileType.DirLink) {
				label.setText("");
			} else {
				label.setText(Utility.humanReadableByteCount(ent.getSize(), true));
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
		case 5:
			label.setIcon(null);
			label.setText(ent.getUser());
			break;
		default:
			break;
		}

		label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
		label.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
		return label;
	}

	public int getPreferredHeight() {
		label.setText("The quick brown fox jumps over the lazy dog");
		return label.getPreferredSize().height;
	}

}

package nixexplorer.widgets.folderview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import nixexplorer.TextHolder;
import nixexplorer.core.FileInfo;

public class FolderViewTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 7212506492710233442L;
	private List<FileInfo> files = new ArrayList<>();

	private String[] columns = { TextHolder.getString("folderview.sortByName"),
			TextHolder.getString("folderview.sortBySize"), TextHolder.getString("folderview.sortByType"),
			TextHolder.getString("folderview.sortByModified"), TextHolder.getString("folderview.sortByPerm"),
			TextHolder.getString("folderview.owner") };

	private boolean local = false;

	public FolderViewTableModel(boolean local) {
		this.local = local;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 1:
			return Long.class;
		case 3:
			return Object.class;
		case 0:
			return Object.class;
		default:
			return Object.class;
		}
	}

	public void clear() {
		files.clear();
		fireTableDataChanged();
	}

	public void addAll(List<FileInfo> list) {
		if (list.size() > 0) {
			int sz = files.size();
			files.addAll(list);
			// fireTableDataChanged();
//			if (sz < 0) {
//				sz = 0;
//			}
			fireTableRowsInserted(sz - 1, sz + list.size() - 1);
		}
	}

	public FileInfo getItemAt(int index) {
		return files.get(index);
	}

	public void add(FileInfo ent) {
		int sz = files.size();
		files.add(ent);
		fireTableRowsInserted(sz, sz);
	}

	@Override
	public String getColumnName(int column) {
		return columns[column];
//		switch (column) {
//		case 0:
//			return "Name";
//		case 1:
//			return "Size";
//		case 2:
//			return "Type";
//		case 3:
//			return "Modified";
//		case 4:
//			return "Permission";
//		}
//		return "";
	}

	public int getRowCount() {
		return files.size();
	}

	public int getColumnCount() {
		return local ? 4 : columns.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		FileInfo ent = files.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return ent;
		case 1:
			return ent.getSize();
		case 2:
			return ent.getType().toString();
		case 3:
			return ent;
		case 4:
			// System.out.println(ent.getPermission() + "");
			return ent.getPermission() + "";
		case 5:
			return ent.getExtra();
		}
		return "";
	}

}

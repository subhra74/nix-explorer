package nixexplorer.widgets.du;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import nixexplorer.TextHolder;

public class DiskUsageTableModel extends AbstractTableModel {
	private List<DiskUsageEntry> list = new ArrayList<>();

	private String cols[] = { TextHolder.getString("diskUsageViewer.fileName"),
			TextHolder.getString("diskUsageViewer.fileSize"),
			TextHolder.getString("diskUsageViewer.filePath"),
			TextHolder.getString("diskUsageViewer.usage") };

	public void setData(List<DiskUsageEntry> list) {
		this.list = list;
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public int getColumnCount() {
		return cols.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		DiskUsageEntry ent = list.get(rowIndex);
		//System.out.println("selected: " + ent);
		switch (columnIndex) {
		case 0:
			return ent.getName();
		case 1:
			return ent.getSize();
		case 2:
			return ent.getPath();
		case 3:
			return ent.getUsagePercent();
		}
		return "";
	}

	@Override
	public String getColumnName(int column) {
		return cols[column];
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return Long.class;
		case 2:
			return String.class;
		case 3:
			return Double.class;
		}
		return Object.class;
	}

}

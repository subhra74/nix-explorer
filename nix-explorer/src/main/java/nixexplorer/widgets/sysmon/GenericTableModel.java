package nixexplorer.widgets.sysmon;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class GenericTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 4654102322526088495L;
	private String[] colums = new String[0];
	private List<String[]> rows = new ArrayList<>();

	private Object lockObj = new Object();

	public void updateTable(String data) {
		synchronized (lockObj) {
			if (data == null || data.length() < 1) {
				return;
			}
			int lastColumnCount = colums.length;
			int lastRowCount = rows.size();
			rows.clear();
			String rowArr[] = data.split(";");
			if (rowArr.length > 0) {
				colums = rowArr[0].split("\\|");
			}
			for (int i = 1; i < rowArr.length; i++) {
				rows.add(rowArr[i].split("\\|"));
			}
			System.out.println("Column count: " + colums.length);
			System.out.println("Rows count: " + rows.size());
			if (lastColumnCount != colums.length) {
				fireTableStructureChanged();
			} else if (rows.size() != lastRowCount) {
				fireTableDataChanged();
			} else {
				fireTableRowsUpdated(0, lastRowCount - 1);
			}
		}
	}

	@Override
	public String getColumnName(int column) {
		synchronized (lockObj) {
			return colums[column];
		}
	}

	@Override
	public int getRowCount() {
		synchronized (lockObj) {
			return rows.size();
		}
	}

	@Override
	public int getColumnCount() {
		synchronized (lockObj) {
			return colums.length;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
			synchronized (lockObj) {
				String[] arr = rows.get(rowIndex);
				if (columnIndex < arr.length) {
					return arr[columnIndex];
				}
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

}

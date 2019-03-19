package nixexplorer.widgets.sysmon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import nixexplorer.widgets.util.Utility;

public class ProcessTableModel extends AbstractTableModel {
	private String[] colums = null;
	private Object[][] tableData = null;// new Object[rowArr.length - 1][];
	private List<ColumnInfo> columnList = new ArrayList<>();
	private JTable table;

	public ProcessTableModel() {
		columnList.add(new ColumnInfo("CPU % (pcpu)", "pcpu", Double.class));
		columnList.add(new ColumnInfo("Memory [KiB] (rss)", "rss", Double.class));
		columnList
				.add(new ColumnInfo("Process id (pid)", "pid", Integer.class));
		columnList.add(new ColumnInfo("User (user)", "user", String.class));
		// columnList.add(new ColumnInfo("Group (group)", "group",
		// String.class));
		columnList.add(new ColumnInfo("Parent process id (ppid)", "ppid",
				Integer.class));
//		columnList.add(
//				new ColumnInfo("Parent group (pgid)", "pgid", String.class));
		columnList.add(new ColumnInfo("Nice (nice)", "nice", String.class));
		columnList.add(
				new ColumnInfo("Elapsed time (etime)", "etime", String.class));
		columnList
				.add(new ColumnInfo("CPU time (time)", "etime", String.class));
		columnList.add(new ColumnInfo("Terminal (tty)", "tty", String.class));
		columnList.add(new ColumnInfo("Command (args)", "args", String.class));
	}

	public String getCommandString() {
		StringBuilder sb = new StringBuilder();
		for (ColumnInfo ci : columnList) {
			sb.append("-o " + ci.getFieldName() + "="
					+ ci.fieldName.toUpperCase());
			sb.append(" ");
		}
		return sb.toString();
	}

	public int getFieldCount() {
		return columnList.size();
	}

	public Integer getPid(int row) {
		int c = 0;
		for (ColumnInfo ci : columnList) {
			if (ci.getFieldName().equals("pid")) {
				return (Integer) tableData[row][c];
			}
			c++;
		}
		return -1;
	}

	public void updateData(List<String> list) {
		if (list.size() < 1) {
			return;
		}
		int lastColumnCount = colums == null ? 0 : colums.length;
		int lastRowCount = tableData == null ? 0 : tableData.length;
		// System.out.println(list);
		if (list.size() > 0) {
			colums = list.get(0).replaceAll("\\s+", " ").trim().split(" ");
		}

		List<String> rows = new ArrayList<>();
		for (int i = 1; i < list.size(); i++) {
			// System.out.println(rowArr[i]);
//			if ((rowArr[i].contains("awk_process_table")
//					|| rowArr[i].contains("ps_parse=1"))) {
//				continue;
//			}
			// System.err.println("----\n" + list.get(i) + "\n\n");
			rows.add(list.get(i));
		}

		tableData = new Object[rows.size()][];
		Class<?>[] clazz = new Class<?>[colums.length];
		for (int i = 0; i < colums.length; i++) {
			colums[i] = colums[i].toLowerCase(Locale.ENGLISH);
			clazz[i] = getClassForColumn(colums[i]);
			// System.out.println(clazz[i]);
		}

		try {
			for (int i = 0; i < tableData.length; i++) {
				String str = rows.get(i).replaceAll("\\s+", " ").trim();
				// System.err.println("== " + str);
				String[] cols = str.split(" ");
				tableData[i] = new Object[colums.length];
				int j = 0;
				for (; j < tableData[i].length; j++) {
					Class<?> clz = clazz[j];
					if (clz == Integer.class) {
						try {
							tableData[i][j] = Integer.parseInt(cols[j]);
						} catch (Exception e) {
							tableData[i][j] = Integer.MIN_VALUE;
						}
					} else if (clz == Double.class) {
						tableData[i][j] = Double.parseDouble(cols[j]);
					} else {
						if (j + 1 == tableData[i].length) {
							StringBuilder sb = new StringBuilder();
							for (int k = j; k < cols.length; k++) {
								sb.append(" " + cols[k]);
							}
							tableData[i][j] = sb.toString();
						} else {
							tableData[i][j] = cols[j];
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (lastColumnCount != colums.length) {
			fireTableStructureChanged();
			table.getColumnModel().moveColumn(table.getColumnCount() - 1, 0);
		} else if (tableData.length != lastRowCount) {
			fireTableDataChanged();
		} else {
			System.out.println("Updating: " + tableData.length);
			fireTableRowsUpdated(0, tableData.length - 1);
		}

	}

	@Override
	public int getRowCount() {
		return tableData == null ? 0 : tableData.length;
	}

	@Override
	public int getColumnCount() {
		return colums == null ? 0 : colums.length;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Comparable.class;
	}

	@Override
	public String getColumnName(int column) {
		return getNameForColumn(colums[column]);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
//		if (columnIndex == 1) {
//			double d = (Double) tableData[rowIndex][columnIndex];
//			return Utility.humanReadableByteCount((long) d, false);
//		}
		return tableData[rowIndex][columnIndex];
	}

	class ColumnInfo {
		private String displayName;
		private String fieldName;
		private Class<?> columnType;

		public ColumnInfo(String displayName, String fieldName,
				Class<?> columnType) {
			super();
			this.displayName = displayName;
			this.fieldName = fieldName;
			this.columnType = columnType;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public Class<?> getColumnType() {
			return columnType;
		}

		public void setColumnType(Class<?> columnType) {
			this.columnType = columnType;
		}
	}

	private Class<?> getClassForColumn(String columnName) {
		for (int k = 0; k < columnList.size(); k++) {
			if (columnName.equals(columnList.get(k).fieldName)) {
				return columnList.get(k).getColumnType();
			}
		}
		return null;
	}

	private String getNameForColumn(String columnName) {
		for (int k = 0; k < columnList.size(); k++) {
			if (columnName.equals(columnList.get(k).fieldName)) {
				return columnList.get(k).getDisplayName();
			}
		}
		return null;
	}

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}
}

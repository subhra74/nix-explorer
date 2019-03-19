package nixexplorer.widgets.logviewer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class LoggingTableModel extends AbstractTableModel {
	private List<LogLine> lines = new ArrayList<>();

	public void addLine(LogLine line) {
		int r = lines.size();
		lines.add(line);
		fireTableRowsInserted(r, r);
	}

	public void addLines(List<LogLine> lines) {
		int r = lines.size();
		this.lines.addAll(lines);
		System.out.println("lines inserted");
		fireTableDataChanged();
	}

	public void clear() {
		lines = new ArrayList<>();
		fireTableDataChanged();
	}

	@Override
	public String getColumnName(int column) {
		return "Lines";
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return LogLine.class;
	}

	@Override
	public int getRowCount() {
		return lines.size();
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return lines.get(rowIndex);
	}

}

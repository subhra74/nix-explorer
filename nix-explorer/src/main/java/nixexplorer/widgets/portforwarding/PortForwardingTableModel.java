/**
 * 
 */
package nixexplorer.widgets.portforwarding;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * @author subhro
 *
 */
public class PortForwardingTableModel extends AbstractTableModel {
	private String columns[] = { "Name", "Source port", "Target host",
			"Target port", "Bind address", "Connected" };
	private List<PortForwardingEntry> list = new ArrayList<>();

	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PortForwardingEntry lpf = list.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return lpf;
		case 1:
			return lpf.getSourcePort();
		case 2:
			return lpf.getTarget();
		case 3:
			return lpf.getTargetPort();
		case 4:
			return lpf.getBindAddress();
		case 5:
			return lpf.isConnected() ? "Yes" : "No";
		}
		return "";
	}

	@Override
	public String getColumnName(int column) {
		return columns[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Object.class;
	}

	public void add(PortForwardingEntry e) {
		int r = list.size();
		list.add(e);
		fireTableRowsInserted(r, r);
	}

	public void remove(int index) {
		list.remove(index);
		fireTableDataChanged();
	}

	public PortForwardingEntry get(int index) {
		return list.get(index);
	}

	public void refresh() {
		fireTableDataChanged();
	}

}

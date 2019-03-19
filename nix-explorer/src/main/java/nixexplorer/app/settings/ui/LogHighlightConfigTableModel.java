/**
 * 
 */
package nixexplorer.app.settings.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import nixexplorer.TextHolder;
import nixexplorer.widgets.logviewer.LogHighlightEntry;

/**
 * @author subhro
 *
 */
public class LogHighlightConfigTableModel extends AbstractTableModel {

	private List<LogHighlightEntry> list = new ArrayList<>();

	private String columns[] = {
			TextHolder.getString("logview.highlight.description"),
			TextHolder.getString("logview.highlight.pattern"),
			TextHolder.getString("logview.highlight.color") };

	public LogHighlightEntry getItem(int index) {
		return list.get(index);
	}

	public void setList(List<LogHighlightEntry> list) {
		this.list.addAll(list);
		fireTableDataChanged();
	}

	public void addItem(LogHighlightEntry e) {
		this.list.add(e);
		fireTableDataChanged();
	}

	public void remove(int r) {
		this.list.remove(r);
		fireTableDataChanged();
	}

	public void setItem(int r, LogHighlightEntry e) {
		this.list.set(r, e);
		fireTableDataChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		return columns[column];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Object.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return list.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columns.length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		LogHighlightEntry ent = list.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return ent.getDescription();
		case 1:
			return ent.getPattern();
		case 2:
			ent.getColor();
		}
		return "";
	}

	/**
	 * @return the list
	 */
	public List<LogHighlightEntry> getList() {
		return list;
	}

}

/**
 * 
 */
package nixexplorer.app.settings.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import nixexplorer.TextHolder;
import nixexplorer.app.settings.snippet.SnippetItem;

/**
 * @author subhro
 *
 */
public class SnippetTableModel extends AbstractTableModel {

	private List<SnippetItem> snippetList = new ArrayList<>();

	private String columns[] = { TextHolder.getString("snippet.name"),
			TextHolder.getString("snippet.command"),
			TextHolder.getString("snippet.key") };

	public void addSnippet(SnippetItem item) {
		this.snippetList.add(item);
		fireTableDataChanged();
	}

	public void setList(List<SnippetItem> snippetList) {
		this.snippetList = snippetList;
		fireTableDataChanged();
	}

	public SnippetItem getItemAt(int index) {
		return snippetList.get(index);
	}

	public void addSnippets(List<SnippetItem> items) {
		this.snippetList.addAll(items);
		fireTableDataChanged();
	}

	public void removeSnippetAt(int index) {
		this.snippetList.remove(index);
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
		return snippetList.size();
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
		SnippetItem item = snippetList.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return item.getName();
		case 1:
			return item.getCommand();
		case 2:
			return item.getKeystroke();
		}
		return "";
	}

}

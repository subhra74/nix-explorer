/**
 * 
 */
package nixexplorer.widgets.folderview;

import javax.swing.AbstractListModel;
import javax.swing.JTable;

import nixexplorer.core.FileInfo;

/**
 * @author subhro
 *
 */
public class TableListModel extends AbstractListModel<FileInfo> {
	private JTable table;

	/**
	 * 
	 */
	public TableListModel(JTable table) {
		this.table = table;
	}

	@Override
	public int getSize() {
		return table.getRowCount();
	}

	@Override
	public FileInfo getElementAt(int index) {
		return (FileInfo) table.getValueAt(index, 0);
	}
	
	public void refresh() {
		fireContentsChanged(this, 0, getSize());
	}

}

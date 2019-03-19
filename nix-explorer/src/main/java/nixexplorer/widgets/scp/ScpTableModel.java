/**
 * 
 */
package nixexplorer.widgets.scp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nixexplorer.app.session.AppSession;

/**
 * @author subhro
 *
 */
public class ScpTableModel extends AbstractTableModel {

	private String[] columns = { "Host name", "Port", "Username",
			"Remote folder" };

	private List<ScpServerInfo> list = new ArrayList<>();

	private AppSession appSession;

	private ObjectMapper objectMapper;

	/**
	 * 
	 */
	public ScpTableModel(AppSession appSession) {
		this.appSession = appSession;
		objectMapper=new ObjectMapper();
		loadItems();
	}

	public ScpServerInfo getItemAt(int index) {
		return list.get(index);
	}

	private void loadItems() {
		File f = new File(appSession.getDirectory(), "scp-sessions.json");
		if (f.exists()) {
			List<ScpServerInfo> items = null;
			try {
				items = objectMapper.readValue(f,
						new TypeReference<List<ScpServerInfo>>() {
						});
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (items != null) {
				list.addAll(items);
			}
		}
	}

	public void updateItem(int index, ScpServerInfo scpItem) {
		list.set(index, scpItem);
		fireTableRowsUpdated(index, index);
		saveItems();
	}

	public void saveItems() {
		File f = new File(appSession.getDirectory(), "scp-sessions.json");
		try {
			objectMapper.writeValue(f, list);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addItem(ScpServerInfo scpItem) {
		int r = list.size();
		list.add(scpItem);
		fireTableRowsInserted(r, r);
	}

	public void addItems(List<ScpServerInfo> scpItems) {
		int r = list.size();
		list.addAll(scpItems);
		fireTableRowsInserted(r, list.size() - 1);
	}

	/**
	 * 
	 */
	public void deleteRow(int index) {
		list.remove(index);
		fireTableRowsDeleted(index, index);
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
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
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
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ScpServerInfo scpInfo = list.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return scpInfo.getHost();
		case 1:
			return scpInfo.getPort() + "";
		case 2:
			return scpInfo.getUser();
		case 3:
			return scpInfo.getFolder();
		}
		return "";
	}

}

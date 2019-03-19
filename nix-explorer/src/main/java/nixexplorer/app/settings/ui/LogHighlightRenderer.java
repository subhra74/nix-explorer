/**
 * 
 */
package nixexplorer.app.settings.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import nixexplorer.widgets.logviewer.LogHighlightEntry;

/**
 * @author subhro
 *
 */
public class LogHighlightRenderer extends JLabel implements TableCellRenderer {
	private JTable table;
	private LogHighlightConfigTableModel model;

	/**
	 * 
	 */
	public LogHighlightRenderer(JTable table, LogHighlightConfigTableModel model) {
		this.table = table;
		this.model = model;
		setBackground(table.getSelectionBackground());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.
	 * swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		setOpaque(isSelected);
		setBackground(table.getSelectionBackground());
		LogHighlightEntry ent = model.getItem(row);
		switch (column) {
		case 0:
			setText(ent.getDescription());
			break;
		case 1:
			setText(ent.getPattern());
			break;
		case 2:
			setOpaque(true);
			setText("");
			setBackground(new Color(ent.getColor()));
		}
		return this;
	}

}

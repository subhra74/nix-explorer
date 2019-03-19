package nixexplorer.widgets.search;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import nixexplorer.App;
import nixexplorer.TextHolder;
import nixexplorer.drawables.icons.ScaledIcon;
import nixexplorer.widgets.util.Utility;

public class SearchTableRenderer implements TableCellRenderer {

	private JLabel label;
	private SearchTableModel model;
	private Icon fileIcon, folderIcon;

	public SearchTableRenderer(SearchTableModel model) {
		this.model = model;
		this.label = new JLabel();
		this.label.setOpaque(true);
		fileIcon = new ScaledIcon(App.class.getResource("/images/fileicon.png"),
				Utility.toPixel(20), Utility.toPixel(20));
		folderIcon = new ScaledIcon(App.class.getResource("/images/local.png"),
				Utility.toPixel(20), Utility.toPixel(20));
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		SearchResult ent = this.model.getItemAt(row);
		if (column == 0) {
			label.setIcon(ent.getType() == "Folder" ? folderIcon : fileIcon);
			label.setText(ent.getName());
		} else {
			label.setIcon(null);
			label.setText(value.toString());
		}

		label.setBackground(isSelected ? table.getSelectionBackground()
				: table.getBackground());
		return label;
	}

}

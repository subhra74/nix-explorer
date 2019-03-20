package nixexplorer.widgets.du;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import nixexplorer.App;
import nixexplorer.drawables.icons.ScaledIcon;
import nixexplorer.widgets.util.Utility;

public class TextRenderer extends JLabel implements TableCellRenderer {
	private Icon folderIcon;

	public TextRenderer() {
		setOpaque(true);
		folderIcon = new ScaledIcon(App.class.getResource("/images/local.png"),
				Utility.toPixel(20), Utility.toPixel(20));
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			setBackground(table.getSelectionBackground());
		} else {
			setBackground(table.getBackground());
		}
		if (column == 1) {
			setText(Utility.humanReadableByteCount((long) value, true));
		} else {
			setText(value.toString());
		}
		if (column == 0) {
			setIcon(folderIcon);
		} else {
			setIcon(null);
		}
		return this;
	}

}

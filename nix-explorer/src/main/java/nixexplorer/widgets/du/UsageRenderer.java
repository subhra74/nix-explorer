package nixexplorer.widgets.du;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import nixexplorer.widgets.util.Utility;

public class UsageRenderer extends JProgressBar implements TableCellRenderer {

	public UsageRenderer() {
		setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		setOpaque(false);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		double pct = (Double) value;
		this.setValue((int) pct);
		return this;
	}

}

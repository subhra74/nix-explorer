/**
 * 
 */
package nixexplorer.widgets.portforwarding;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class PortForwardingRenderer extends JLabel
		implements TableCellRenderer {

	/**
	 * 
	 */
	public PortForwardingRenderer() {
		setText("Dummy text");
		setBorder(new EmptyBorder(Utility.toPixel(10), Utility.toPixel(10),
				Utility.toPixel(10), Utility.toPixel(10)));
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (column == 0) {
			this.setText(((PortForwardingEntry) value).getName());
			setIcon(((PortForwardingEntry) value).isConnected()
					? UIManager.getIcon("PortForwarding.on")
					: UIManager.getIcon("PortForwarding.off"));
		} else {
			setIcon(null);
			this.setText(value + "");
		}
		setOpaque(isSelected);
		setBackground(isSelected ? table.getSelectionBackground()
				: table.getBackground());
		return this;
	}

}

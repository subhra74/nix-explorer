/**
 * 
 */
package nixexplorer.skin;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.DefaultTableCellRenderer;

import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class FlatTableHeaderUI extends BasicTableHeaderUI {
	public static ComponentUI createUI(JComponent c) {
		return new FlatTableHeaderUI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicTableHeaderUI#installUI(javax.swing.
	 * JComponent)
	 */
	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		if (this.header
				.getDefaultRenderer() instanceof DefaultTableCellRenderer) {
			DefaultTableCellRenderer render = ((DefaultTableCellRenderer) this.header
					.getDefaultRenderer());
			render.setHorizontalAlignment(JLabel.LEFT);
		}
	}
}

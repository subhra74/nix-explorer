/**
 * 
 */
package nixexplorer.app.settings.ui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class IndexCellRenderer extends JLabel
		implements ListCellRenderer<String> {

	/**
	 * 
	 */
	public IndexCellRenderer() {
		setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.
	 * JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<? extends String> list,
			String value, int index, boolean isSelected, boolean cellHasFocus) {
		setText(value);
		setOpaque(isSelected);
		setBackground(UIManager.getColor("Button.highlight"));
		return this;
	}
}

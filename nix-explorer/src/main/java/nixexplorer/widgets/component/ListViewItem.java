package nixexplorer.widgets.component;

import java.awt.Color;

import javax.swing.Icon;

public interface ListViewItem {
	public Icon getIcon();

	public String getName();

	public Color getForeground();

	public Color getSelectedForeground();
}

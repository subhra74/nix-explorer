package nixexplorer.widgets.folderview;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

public class PlaceRenderer implements ListCellRenderer<FavouritePlaceEntry> {

	private JLabel lblItem;

	public PlaceRenderer() {
		lblItem = new JLabel();
		lblItem.setIcon(UIManager.getIcon("ListView.smallFolder"));
		lblItem.setBackground(UIManager.getColor("Tree.selectionBackground"));
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends FavouritePlaceEntry> list,
			FavouritePlaceEntry value, int index, boolean isSelected,
			boolean cellHasFocus) {
		lblItem.setOpaque(isSelected);
		lblItem.setText(value.toString());
		return lblItem;
	}

}

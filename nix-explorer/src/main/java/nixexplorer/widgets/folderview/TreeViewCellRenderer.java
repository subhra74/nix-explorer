package nixexplorer.widgets.folderview;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreeCellRenderer;

import nixexplorer.widgets.util.Utility;

public class TreeViewCellRenderer implements TreeCellRenderer {
	private JLabel lblRenderer;

	public TreeViewCellRenderer() {
		lblRenderer = new JLabel();
		lblRenderer
				.setBackground(UIManager.getColor("Tree.selectionBackground"));
		//lblRenderer.setIcon(UIManager.getIcon("Tree.openIcon"));
		lblRenderer.setBorder(new EmptyBorder(Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5), Utility.toPixel(5)));
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		lblRenderer.setText(value.toString());
		if (selected) {
			lblRenderer.setOpaque(true);
			lblRenderer.setForeground(UIManager.getColor("Tree.selectionForeground"));
		} else {
			lblRenderer.setOpaque(false);
			lblRenderer.setForeground(UIManager.getColor("Tree.foreground"));
		}
		return lblRenderer;
	}

}

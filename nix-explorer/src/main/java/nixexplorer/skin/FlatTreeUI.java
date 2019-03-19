package nixexplorer.skin;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

public class FlatTreeUI extends BasicTreeUI {
	public static ComponentUI createUI(JComponent c) {
		return new FlatTreeUI();
	}

	protected void paintRow(Graphics g, Rectangle clipBounds, Insets insets,
			Rectangle bounds, TreePath path, int row, boolean isExpanded,
			boolean hasBeenExpanded, boolean isLeaf) {
		// Don't paint the renderer if editing this row.
		if (editingComponent != null && editingRow == row)
			return;

		if (tree.isRowSelected(row)) {
			int h = tree.getRowHeight();
			g.setColor(UIManager.getColor("Tree.selectionBackground"));
			g.fillRect(clipBounds.x, h * row, clipBounds.width, h);
		}

		super.paintRow(g, clipBounds, insets, bounds, path, row, isExpanded,
				hasBeenExpanded, isLeaf);
	}
}

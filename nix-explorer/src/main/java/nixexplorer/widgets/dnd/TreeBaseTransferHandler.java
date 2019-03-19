package nixexplorer.widgets.dnd;

import javax.swing.JTree;

public class TreeBaseTransferHandler extends FolderViewBaseTransferHandler {
	protected JTree tree;

	public JTree getTree() {
		return tree;
	}

	public void setTree(JTree tree) {
		this.tree = tree;
	}
}

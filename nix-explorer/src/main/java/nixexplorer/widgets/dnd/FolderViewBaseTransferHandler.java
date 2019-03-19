package nixexplorer.widgets.dnd;

import javax.swing.TransferHandler;

import nixexplorer.widgets.folderview.FolderViewWidget;

public class FolderViewBaseTransferHandler extends TransferHandler {
	protected FolderViewWidget folderView;

	public FolderViewWidget getWidget() {
		return folderView;
	}

	public void setWidget(FolderViewWidget widget) {
		this.folderView = widget;
	}
}

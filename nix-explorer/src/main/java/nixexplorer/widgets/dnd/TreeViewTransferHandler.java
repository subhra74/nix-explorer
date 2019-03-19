package nixexplorer.widgets.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;

import nixexplorer.app.session.SessionInfo;
import nixexplorer.widgets.Widget;
import nixexplorer.widgets.folderview.FolderViewUtility;
import nixexplorer.widgets.folderview.FolderViewWidget;
import nixexplorer.widgets.folderview.TabCallback;

public class TreeViewTransferHandler extends TreeBaseTransferHandler {
	private DataFlavor flavor = new DataFlavor(TransferFileInfo.class,
			"data-file");
	private TabCallback tabCallback;

	public TreeViewTransferHandler(TabCallback tabCallback) {
		this.tabCallback = tabCallback;
	}

	@Override
	public boolean canImport(TransferSupport support) {
		try {
			if (support.isDataFlavorSupported(flavor)) {
				TransferFileInfo finfo = (TransferFileInfo) support
						.getTransferable().getTransferData(flavor);
				List<SessionInfo> list = finfo.getInfo();
				if (!folderView.isLocal()) {
					if (list != null && list.size() == 1 && FolderViewUtility
							.sameSession(tabCallback.getInfo(), list.get(0))) {
						return true;
					}
				} else {
					if (list == null || list.size() == 0) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean importData(TransferSupport info) {
		if (!info.isDrop()) {
			return false;
		}
		Transferable t = info.getTransferable();
		for (DataFlavor f : t.getTransferDataFlavors()) {
			if (f.equals(flavor)) {
				try {
					Object obj = t.getTransferData(flavor);
					TransferFileInfo finfo = (TransferFileInfo) obj;
					System.out.println(finfo);
					if (info.getDropLocation() != null && info
							.getDropLocation() instanceof JTree.DropLocation) {
						JTree.DropLocation dl = (javax.swing.JTree.DropLocation) info
								.getDropLocation();
						System.out.println("drop location path: "+dl.getPath());
						if (dl.getPath() != null) {
							System.out.println("Tree-: "+tree);
							String treeFolder = FolderViewUtility.getFilePath(
									tree, dl.getPath(), folderView.isLocal());
							if (treeFolder != null && treeFolder.length() > 0) {
								List<String> droppedFiles = finfo
										.getSourceFiles();
								List<String> droppedFolders = finfo
										.getSourceFolders();
								System.out.println("moving files to: "
										+ treeFolder + " dropped files: "
										+ droppedFiles + " dropped folders: "
										+ droppedFolders);
								tabCallback.moveFiles(treeFolder, droppedFiles,
										droppedFolders, false, folderView);
							}
						}
					}
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			} else {
				return false;
			}
		}
		return true;
	}

	public FolderViewWidget getWidget() {
		return folderView;
	}

	public void setWidget(FolderViewWidget widget) {
		this.folderView = widget;
	}

//	private boolean sameSession(SessionInfo info) {
//		return this.sessionInfo.getHost().equals(info.getHost())
//				&& this.sessionInfo.getPort() == info.getPort();
//	}

}

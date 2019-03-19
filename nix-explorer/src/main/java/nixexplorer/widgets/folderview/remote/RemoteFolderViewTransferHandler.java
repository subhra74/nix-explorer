package nixexplorer.widgets.folderview.remote;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import nixexplorer.core.FileInfo;
import nixexplorer.core.FileType;
import nixexplorer.core.ForeignServerInfo;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.widgets.dnd.FolderViewBaseTransferHandler;
import nixexplorer.widgets.dnd.TransferFileInfo;
import nixexplorer.widgets.folderview.FolderViewWidget;
import nixexplorer.widgets.folderview.TabCallback;
//import nixexplorer.widgets.folderview.foreign.ForeignFolderViewWidget;

public class RemoteFolderViewTransferHandler
		extends FolderViewBaseTransferHandler implements Transferable {
	private DataFlavor flavor = new DataFlavor(TransferFileInfo.class,
			"data-file");
//	private DataFlavor foreignFlavor = new DataFlavor(
//			ForeignFolderViewWidget.class, "foreign-data-file");
	private RemoteFolderViewWidget remoteFolderView;

	public RemoteFolderViewTransferHandler(RemoteFolderViewWidget tabCallback) {
		super();
		this.remoteFolderView = tabCallback;
	}

	@Override
	public boolean canImport(TransferSupport support) {
		for (DataFlavor f : support.getDataFlavors()) {
			System.out.println("Dataflavor: " + f);
			if (f.isFlavorJavaFileListType()) {
				return true;
			}
		}

		System.out.println("Can import checking");
		try {
			if (support.isDataFlavorSupported(flavor)) {
				TransferFileInfo finfo = (TransferFileInfo) support
						.getTransferable().getTransferData(flavor);
				List<SessionInfo> list = finfo.getInfo();
				if (list != null && list.size() == 1
						&& sameSession(list.get(0))) {
					System.out.println("same session");
					return false;
					// System.out.println("list of files drop"+list);
//					Point dropPoint = support.getDropLocation().getDropPoint();
//					FileInfo info = getFileInfoForPoint(dropPoint);
//					if (info == null || info.getType() == FileType.File
//							|| info.getType() == FileType.FileLink) {
//						System.out.println("Return false");
//						return false;
//					}
				}
				System.out.println("drop okay");
				return true;
			}

//			else if (support.isDataFlavorSupported(foreignFlavor)) {
//				return true;
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("drop not supported");
		return false;
	}

	private boolean sameSession(SessionInfo info) {
		if (info == null) {
			return false;
		}
		return this.folderView.getSessionInfo().getHost().equals(info.getHost())
				&& this.folderView.getSessionInfo().getPort() == info.getPort();
	}

	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}

	@Override
	public boolean importData(TransferSupport info) {
		if (!info.isDrop()) {
			return false;
		}

		Transferable t = info.getTransferable();
		for (DataFlavor f : t.getTransferDataFlavors()) {
			if (f.isFlavorJavaFileListType()) {
				try {
					@SuppressWarnings("unchecked")
					List<File> files = (List<File>) info.getTransferable()
							.getTransferData(f);
					System.out.println("Dropped files: " + files);
					TransferFileInfo sf = new TransferFileInfo();
					List<String> filelist = new ArrayList<>();
					List<String> folderlist = new ArrayList<>();
					for (File file : files) {
						if (file.isDirectory()) {
							folderlist.add(file.getAbsolutePath());
						} else {
							filelist.add(file.getAbsolutePath());
						}
					}
					sf.setSourceFiles(filelist);
					sf.setSourceFolders(folderlist);
					sf.addInfo(folderView.getTabCallback().getInfo());
					remoteFolderView.handleFileDrop(sf, folderView);
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (f.equals(flavor)) {
				try {
					Object obj = t.getTransferData(flavor);
					System.out.println("Importing from local");
					remoteFolderView.handleFileDrop(obj, folderView);
					return true;
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// else if (f.equals(foreignFlavor)) {
//				System.out.println("Remote on remote drop");
////				try {
////					tabCallback.getDesktop().signal(
////							(Integer) t.getTransferData(foreignFlavor),
////							widget.getCurrentPath());
////				} catch (UnsupportedFlavorException e) {
////					e.printStackTrace();
////				} catch (IOException e) {
////					e.printStackTrace();
////				}
//			} else {
//				return false;
//			}
		}
		return true;
	}

	protected void exportDone(JComponent c, Transferable data, int action) {

	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		return this;
	}

	public FolderViewWidget getWidget() {
		return folderView;
	}

	public void setWidget(FolderViewWidget widget) {
		this.folderView = widget;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor f) {
		return flavor.equals(f);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { flavor };
	}

	@Override
	public Object getTransferData(DataFlavor d)
			throws UnsupportedFlavorException, IOException {
		TransferFileInfo sf = new TransferFileInfo();
		sf.addInfo(folderView.getTabCallback().getInfo());
		List<String> filelist = new ArrayList<>();
		List<String> folderlist = new ArrayList<>();
		for (FileInfo f : folderView.getSelectedFiles()) {
			if (f.getType() == FileType.Directory) {
				folderlist.add(f.getPath());
			} else {
				filelist.add(f.getPath());
			}
		}
		sf.setSourceFiles(filelist);
		sf.setSourceFolders(folderlist);
		return sf;
	}

//	/**
//	 * 
//	 */
//	private FileInfo getFileInfoForPoint(Point dropPoint) {
//		System.out.println("same session " + dropPoint);
//		FileInfo info = folderView.getInfoForPoint(dropPoint);
//		return info;
//	}
}

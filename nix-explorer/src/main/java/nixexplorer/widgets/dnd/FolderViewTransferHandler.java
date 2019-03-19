package nixexplorer.widgets.dnd;
//package nixexplorer.widgets.folderview;
//
//import java.awt.datatransfer.DataFlavor;
//import java.awt.datatransfer.Transferable;
//import java.awt.datatransfer.UnsupportedFlavorException;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.swing.JComponent;
//import javax.swing.TransferHandler;
//
//import nixexplorer.core.FileInfo;
//import nixexplorer.core.FileType;
//import nixexplorer.app.session.SessionInfo;
//import nixexplorer.widgets.folderview.dnd.TransferFileInfo;
//
//public class FolderViewTransferHandler extends TransferHandler
//		implements Transferable {
//	private static final long serialVersionUID = -8381075103730332347L;
//	private FolderViewWidget widget;
//	private DataFlavor flavor = new DataFlavor(TransferFileInfo.class,
//			"data-file");
//
//
//	public FolderViewTransferHandler(FolderViewWidget widget) {
//		this.widget = widget;
//	}
//
//	@Override
//	public boolean canImport(TransferSupport support) {
//		//System.out.println("Can import ");
//		try {
//			if (support.isDataFlavorSupported(flavor)) {
//				TransferFileInfo finfo = (TransferFileInfo) support
//						.getTransferable().getTransferData(flavor);
//				List<SessionInfo> list = finfo.getInfo();
//				if (widget.isLocal()) {
//					if (list == null || list.size() == 0) {
//						System.out.println("List is empty");
//						return false;
//					}
//				} else {
//					if (list != null && list.size() == 1
//							&& sameSession(list.get(0))) {
//						System.out.println("same session");
//						return false;
//					}
//				}
//			}
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
//
//	private boolean sameSession(SessionInfo info) {
//		if (info == null) {
//			if (this.getWidget().getSessionInfo() == null) {
//				return false;
//			}
//		}
//		return this.widget.getSessionInfo().getHost().equals(info.getHost())
//				&& this.widget.getSessionInfo().getPort() == info.getPort();
//	}
//
//	@Override
//	public int getSourceActions(JComponent c) {
//		return TransferHandler.COPY;
//	}
//
//	@Override
//	public boolean importData(TransferSupport info) {
//		if (!info.isDrop()) {
//			return false;
//		}
//		Transferable t = info.getTransferable();
//		for (DataFlavor f : t.getTransferDataFlavors()) {
//			if (f.equals(flavor)) {
//				try {
//					Object obj = t.getTransferData(flavor);
//					if (widget.isLocal()) {
//						System.out.println("Importing from local");
//						widget.handleRemoteFileDrop((TransferFileInfo) obj);
//					} else {
//						System.out.println("Importing from remote");
//						TransferFileInfo tobj = (TransferFileInfo) obj;
//						tobj.addInfo(widget.getTabCallback().getInfo());
//						widget.handleLocalFileDrop(tobj);
//					}
//				} catch (UnsupportedFlavorException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				break;
//			} else {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	protected void exportDone(JComponent c, Transferable data, int action) {
//
//	}
//
//	@Override
//	protected Transferable createTransferable(JComponent c) {
//		return this;
//	}
//
//	public FolderViewWidget getWidget() {
//		return widget;
//	}
//
//	public void setWidget(FolderViewWidget widget) {
//		this.widget = widget;
//	}
//
//	@Override
//	public boolean isDataFlavorSupported(DataFlavor f) {
//		return flavor.equals(f);
//	}
//
//	@Override
//	public DataFlavor[] getTransferDataFlavors() {
//		return new DataFlavor[] { flavor };
//	}
//
//	@Override
//	public Object getTransferData(DataFlavor d)
//			throws UnsupportedFlavorException, IOException {
//		if (widget.isLocal()) {
//			TransferFileInfo sf = new TransferFileInfo();
//			List<String> filelist = new ArrayList<>();
//			List<String> folderlist = new ArrayList<>();
//			for (FileInfo f : widget.getSelectedFiles()) {
//				if (f.getType() == FileType.Directory) {
//					folderlist.add(f.getPath());
//				} else {
//					filelist.add(f.getPath());
//				}
//			}
//			sf.setSourceFiles(filelist);
//			sf.setSourceFolders(folderlist);
//			return sf;
//		} else {
//			TransferFileInfo sf = new TransferFileInfo();
//			sf.addInfo(widget.getTabCallback().getInfo());
//			List<String> filelist = new ArrayList<>();
//			List<String> folderlist = new ArrayList<>();
//			for (FileInfo f : widget.getSelectedFiles()) {
//				if (f.getType() == FileType.Directory) {
//					folderlist.add(f.getPath());
//				} else {
//					filelist.add(f.getPath());
//				}
//			}
//			sf.setSourceFiles(filelist);
//			sf.setSourceFolders(folderlist);
//			return sf;
//		}
//	}
//
//}

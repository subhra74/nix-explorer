//package nixexplorer.widgets.folderview.foreign;
//
//import java.awt.datatransfer.DataFlavor;
//import java.awt.datatransfer.Transferable;
//import java.awt.datatransfer.UnsupportedFlavorException;
//import java.awt.event.InputEvent;
//import java.io.IOException;
//import java.util.List;
//
//import javax.swing.JComponent;
//import javax.swing.TransferHandler;
//
//import nixexplorer.core.ForeignServerInfo;
//import nixexplorer.app.session.SessionInfo;
//import nixexplorer.desktop.SignalHandler;
//import nixexplorer.widgets.dnd.FolderViewBaseTransferHandler;
//import nixexplorer.widgets.dnd.TransferFileInfo;
//
//public class ForeignTransferHandler extends FolderViewBaseTransferHandler
//		implements Transferable, SignalHandler {
//	private ForeignFolderViewWidget tabCalled;
//	private DataFlavor flavor = new DataFlavor(TransferFileInfo.class,
//			"data-file");
//	private DataFlavor foreignFlavor = new DataFlavor(
//			ForeignFolderViewWidget.class, "foreign-data-file");
//
//	public ForeignTransferHandler(ForeignFolderViewWidget tabCalled) {
//		super();
//		this.tabCalled = tabCalled;
//	}
//
//	@Override
//	public void exportAsDrag(JComponent comp, InputEvent e, int action) {
//		System.out.println("Registered");
////		tabCalled.getDesktop().registerSignalHandler(this,
////				ForeignFolderViewWidget.TOPIC_INIT_DOWNLOAD);
//		super.exportAsDrag(comp, e, action);
//	}
//
//	@Override
//	public int getSourceActions(JComponent c) {
//		return TransferHandler.COPY;
//	}
//
//	protected void exportDone(JComponent c, Transferable data, int action) {
////		tabCalled.getDesktop().unregisterSignalHandler(this,
////				ForeignFolderViewWidget.TOPIC_INIT_DOWNLOAD);
//		System.out.println("unRegistered");
//	}
//
//	@Override
//	public boolean canImport(TransferSupport support) {
//		// System.out.println("Can import ");
//		try {
//			if (support.isDataFlavorSupported(flavor)) {
//				TransferFileInfo finfo = (TransferFileInfo) support
//						.getTransferable().getTransferData(flavor);
//				if (finfo.getInfo() == null || finfo.getInfo().size() == 0) {
//					return false;
//				}
////				List<SessionInfo> list = finfo.getInfo();
////				if (list != null && list.size() == 1 && FolderViewUtility
////						.sameSession(tabCalled.getInfo(), list.get(0))) {
////					System.out.println("same session");
////					return false;
////				}
//			}
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false;
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
//					System.out.println("Importing from remote");
//					// TransferFileInfo tobj = (TransferFileInfo) obj;
//					// tobj.addInfo(widget.getTabCallback().getInfo());
//					tabCalled.handleFileDrop(obj, folderView);
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
//	@Override
//	protected Transferable createTransferable(JComponent c) {
//		return this;
//	}
//
//	@Override
//	public DataFlavor[] getTransferDataFlavors() {
//		return new DataFlavor[] { foreignFlavor };
//	}
//
//	@Override
//	public boolean isDataFlavorSupported(DataFlavor flavor) {
//		return foreignFlavor.equals(flavor);
//	}
//
//	@Override
//	public Object getTransferData(DataFlavor flavor)
//			throws UnsupportedFlavorException, IOException {
//		return ForeignFolderViewWidget.TOPIC_INIT_DOWNLOAD;
//	}
//
//	@Override
//	public void onSignal(Object signal, Object data) {
//		tabCalled.onSignal(signal, data, super.getWidget());
//	}
//
//}

//package nixexplorer.widgets.dnd;
//
//import java.awt.datatransfer.DataFlavor;
//
//import nixexplorer.PathUtils;
//import nixexplorer.desktop.DesktopLayoutPanel;
//import nixexplorer.registry.PluginShortcutEntry;
//import nixexplorer.widgets.folderview.local.LocalFolderViewWidget;
//import nixexplorer.widgets.folderview.remote.RemoteFolderViewWidget;
//
//public class DesktopPanelTransferHandler extends TreeBaseTransferHandler {
//	private DataFlavor flavor = new DataFlavor(TransferFileInfo.class,
//			"data-file");
//
//	private DesktopLayoutPanel desktop;
//
//	public DesktopPanelTransferHandler(DesktopLayoutPanel desktop) {
//		super();
//		this.desktop = desktop;
//	}
//
//	@Override
//	public boolean canImport(TransferSupport support) {
//		return support.isDataFlavorSupported(flavor);
//	}
//
//	@Override
//	public boolean importData(TransferSupport support) {
//		if (!support.isDrop()) {
//			return false;
//		}
//		boolean add = false;
//		try {
//			if (support.isDataFlavorSupported(flavor)) {
//				TransferFileInfo finfo = (TransferFileInfo) support
//						.getTransferable().getTransferData(flavor);
//				for (String file : finfo.getSourceFiles()) {
//					desktop.addAndSaveEntry(new PluginShortcutEntry(
//							isLocal(finfo)
//									? LocalFolderViewWidget.class.getName()
//									: RemoteFolderViewWidget.class.getName(),
//							new String[] { file },
//							PathUtils.getFileName(file)));
//					add = true;
//				}
//				for (String folder : finfo.getSourceFolders()) {
//					desktop.addAndSaveEntry(new PluginShortcutEntry(
//							isLocal(finfo)
//									? LocalFolderViewWidget.class.getName()
//									: RemoteFolderViewWidget.class.getName(),
//							new String[] { folder },
//							PathUtils.getFileName(folder)));
//					add = true;
//				}
//				if (add) {
//					desktop.doLayout();
//					desktop.revalidate();
//					desktop.repaint();
//				}
//				return true;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
//
//	private boolean isLocal(TransferFileInfo finfo) {
//		return finfo.getInfo() == null || finfo.getInfo().size() == 0;
//	}
//}

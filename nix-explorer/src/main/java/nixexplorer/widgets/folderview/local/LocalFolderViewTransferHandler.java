package nixexplorer.widgets.folderview.local;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import nixexplorer.core.FileInfo;
import nixexplorer.core.FileType;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.widgets.dnd.FolderViewBaseTransferHandler;
import nixexplorer.widgets.dnd.TransferFileInfo;
import nixexplorer.widgets.folderview.FolderViewWidget;
import nixexplorer.widgets.folderview.TabCallback;
import nixexplorer.widgets.folderview.copy.CopyWidget;

public class LocalFolderViewTransferHandler
		extends FolderViewBaseTransferHandler implements Transferable {
	private TabCallback tabCalled;
	private DataFlavor flavor = new DataFlavor(TransferFileInfo.class,
			"data-file");

	public LocalFolderViewTransferHandler(TabCallback tabCalled) {
		super();
		this.tabCalled = tabCalled;
	}

	@Override
	public boolean canImport(TransferSupport support) {
		// System.out.println("Can import ");
		try {
			if (support.isDataFlavorSupported(flavor)) {
				TransferFileInfo finfo = (TransferFileInfo) support
						.getTransferable().getTransferData(flavor);
				List<SessionInfo> list = finfo.getInfo();

				if (list == null || list.size() == 0) {
					System.out.println("List is empty");
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
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
			if (f.equals(flavor)) {
				try {
					Object obj = t.getTransferData(flavor);
					System.out.println("Importing from local");
					tabCalled.handleFileDrop(obj, folderView);
					// widget.handleRemoteFileDrop((TransferFileInfo) obj);
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			} else {
				return false;
			}
		}
		return true;
	}

	protected void exportDone(JComponent c, Transferable data, int action) {
//		tabCalled.getDesktop().createWidget(CopyWidget.class.getName(),
//				new HashMap<String, Object>(),
//				new String[] { null, null, null }, tabCalled.getWidget());
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

}

package nixexplorer.widgets.listeners;

import nixexplorer.core.ssh.filetransfer.FileTransfer;
import nixexplorer.widgets.listeners.AppMessageListener.TransferStatus;

public interface TransferWatcher {
	public void addTransfer(FileTransfer f);

	public void notify(TransferStatus status, FileTransfer transfer);
}

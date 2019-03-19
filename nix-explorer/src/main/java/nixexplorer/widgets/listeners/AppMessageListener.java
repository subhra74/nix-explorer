package nixexplorer.widgets.listeners;

import nixexplorer.core.ssh.filetransfer.FileTransfer;

public interface AppMessageListener {
	public enum TransferStatus {
		Initiating, InProgress, Failed, Complete, stopped
	}

	public void notify(TransferStatus status, FileTransfer transfer);

	public void registerAppEventListener(AppEventListener l);

	public void unregisterAppEventListener(AppEventListener l);

	public TransferWatcher getTransferWatcher();
}

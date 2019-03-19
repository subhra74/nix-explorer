package nixexplorer.core.ssh.filetransfer;

import nixexplorer.core.FileType;
import nixexplorer.widgets.listeners.AppMessageListener;

public interface FileTransfer {

	public enum TransferType {
		Upload, Download
	}

	public void start();

	public void resume();

	public void stop();

	public int getPercentComplete();

	public String getHostName();

	public String getSourceFileName();

	public String getTargetFileName();

	public long getSize();

	public long getId();

	public void setStatusListener(AppMessageListener listener);

	public void removeStatusListener();

	public FileType getType();
}

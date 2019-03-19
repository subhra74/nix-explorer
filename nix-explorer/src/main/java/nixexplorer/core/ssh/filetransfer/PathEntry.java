package nixexplorer.core.ssh.filetransfer;

public class PathEntry {
	public PathEntry(String remotePath, String localPath) {
		super();
		this.remotePath = remotePath;
		this.localPath = localPath;
	}

	private String localPath, remotePath;

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
}

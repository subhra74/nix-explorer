package nixexplorer.core.ssh.filetransfer.remote2local;

public class FileChunk {
	private long startPos;
	private long length;
	private boolean finished;

	public long getStartPos() {
		return startPos;
	}

	public void setStartPos(long startPos) {
		this.startPos = startPos;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public synchronized boolean isFinished() {
		return finished;
	}

	public synchronized void setFinished(boolean finished) {
		this.finished = finished;
	}
}

package nixexplorer.core.ssh.filetransfer.remote2local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChunkList {
	private List<FileChunk> list = Collections
			.synchronizedList(new ArrayList<>());
	private Object synchronizer;

	public ChunkList(Object synchronizer) {
		this.synchronizer = synchronizer;
	}

	public synchronized void addToList(FileChunk chunk) {
		this.list.add(chunk);
	}

	public synchronized FileChunk pickNextChunk() {
		for (FileChunk c : list) {
			if (!c.isFinished()) {
				return c;
			}
		}
		return null;
	}

	public synchronized void chunkFinished() {
		if (!hasAvailableChunk()) {
			synchronizer.notify();
		}
	}

	public synchronized boolean hasAvailableChunk() {
		for (FileChunk c : list) {
			if (!c.isFinished()) {
				return true;
			}
		}
		return false;
	}
}

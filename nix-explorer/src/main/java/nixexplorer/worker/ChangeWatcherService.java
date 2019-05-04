/**
 * 
 */
package nixexplorer.worker;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;

/**
 * @author subhro
 *
 */
public class ChangeWatcherService implements ChangeWatcher, Runnable {
	private WatchService watchService;
	private Map<String, UploadTask> uploadsInProgress = new ConcurrentHashMap<>();
	private Map<WatchKey, WatcherEntry> itemsBeingWatched = new ConcurrentHashMap<>();
	private SessionInfo info;
	private AppSession appSession;

	/**
	 * 
	 */
	public ChangeWatcherService(SessionInfo info, AppSession appSession) {
		this.info = info;
		this.appSession = appSession;
		try {
			watchService = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(this).start();
	}

	@Override
	public void run() {
		while (true) {
			WatchKey key;
			try {
				System.out.println("Waiting for changes in change watcher...");
				key = watchService.take();
				System.out.println("Found changes");
			} catch (InterruptedException x) {
				return;
			}
			outer: {
				for (WatchEvent<?> event : key.pollEvents()) {
					System.out.println("Event kind:" + event.kind()
							+ ". File affected: " + event.context() + ".");
					String changedFile = event.context() + "";
					WatcherEntry ent = itemsBeingWatched.get(key);
					if (ent == null) {
						key.cancel();
						break outer;
					}
					try {
						if (ent.getRemoteFileName().equals(changedFile) && event
								.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
							long lastModified = Files
									.getLastModifiedTime(
											Paths.get(ent.getLocalTempFile()))
									.toMillis();
							if (ent.getLastModified() < lastModified) {
								ent.setLastModified(lastModified);
								cancelPreviousUploads(ent);
								startUpload(ent);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				key.reset();
			}
		}
	}

	@Override
	public void watchForChanges(WatcherEntry ent) {
		try {
			WatchKey key = Paths.get(ent.getLocalDirectory()).register(
					watchService, StandardWatchEventKinds.ENTRY_MODIFY);
			itemsBeingWatched.put(key, ent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cancelPreviousUploads(WatcherEntry ent) {
		UploadTask upload = uploadsInProgress.get(ent.getRemoteFile());
		if (upload != null) {
			upload.cancel();
		}
	}

	@Override
	public synchronized void uploadComplete(String path, UploadTask task) {
		UploadTask u = this.uploadsInProgress.get(path);
		if (u == task) {
			this.uploadsInProgress.remove(path);
		}
	}

	private synchronized void startUpload(WatcherEntry ent) {
		this.uploadsInProgress.put(ent.getRemoteFile(),
				new UploadTask(info, ent, appSession, this));
	}

	@Override
	public void close() {
		try {
			watchService.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

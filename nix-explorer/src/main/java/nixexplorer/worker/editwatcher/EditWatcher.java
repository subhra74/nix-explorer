package nixexplorer.worker.editwatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.worker.WorkerBase;

public class EditWatcher extends WorkerBase {
	// private ExecutorService threadPool = Executors.newFixedThreadPool(1);
	// private String[] protocols = new String[] { "sftp" };
	private WatchService watchService;
	private Map<WatchKey, FileEntry> keyPath = new ConcurrentHashMap<>();

	public EditWatcher(SessionInfo info, AppSession session, String[] args) {
		super(info, session, args);
		try {
			watchService = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			WatchKey key;
			try {
				System.out.println("Waiting for changes...");
				key = watchService.take();
				System.out.println("Found changes");
			} catch (InterruptedException x) {
				return;
			}
			outer: {
				FileEntry path = null;
				for (WatchEvent<?> event : key.pollEvents()) {
					System.out.println("Event kind:" + event.kind()
							+ ". File affected: " + event.context() + ".");
					String changedFile = event.context() + "";
					FileEntry ent = keyPath.get(key);
					if (ent == null) {
						key.cancel();
						break outer;
					}
					if (ent.getFileName().equals(changedFile) && event
							.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
						path = keyPath.get(key);
					}

				}
				if (path != null) {
					uploadChangedFile(path);
				}
				key.reset();
			}
		}
	}

	public WatchKey register(FileEntry ent) {
		try {
			WatchKey key = ent.getFolder().register(watchService,
					StandardWatchEventKinds.ENTRY_MODIFY);
			keyPath.put(key, ent);
			return key;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized void unregister(WatchKey key) {
		keyPath.remove(key);
		key.cancel();
		System.out.println("Unregistering watcher for: "+key.watchable());
	}

//	public void run1() {
//		String dir = App.getConfig("temp.dir") + File.separatorChar + "" + info.hashCode();
//		try {
//			Files.createDirectories(Paths.get(dir));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println(
//				"Watching for changes in: " + App.getConfig("temp.dir") + File.separatorChar + "" + info.hashCode());
//		try {
//			WatchService watchService = FileSystems.getDefault().newWatchService();
//			Path path = Paths.get(App.getConfig("temp.dir") + File.separatorChar + "" + info.hashCode());
//			path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
//
//			WatchKey key;
//			while ((key = watchService.take()) != null) {
//				for (WatchEvent<?> event : key.pollEvents()) {
//					System.out.println("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");
//					uploadChangedFile(path.resolve((Path) event.context()));
//				}
//				key.reset();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

	private synchronized void uploadChangedFile(FileEntry ent) {
		try {
			System.out.println("Change found in: " + ent.getFile());
			// File f = path.toFile().listFiles()[0];

			// String localFile = f.getAbsolutePath();///
			// path.toAbsolutePath().toString();
			ChangeUploader cu = ent.getCu();// session.getEditWatchers().get(localFile);
			if (cu == null) {
				return;
			}
			long lastModified = Files.getLastModifiedTime(ent.getFile())// Paths.get(localFile))
					.toMillis();

			if (lastModified > cu.getLastModified()) {
				cu.onFileChanged(lastModified);
			}

//			FileInfo fileInfo = getDesktop().getEditorHelper().getExternalEditMap().get(localFile);
//			if(fileInfo==null) {
//				return;
//			}
//			String remoteFile = fileInfo.getName();
//			if (remoteFile != null) {
//				System.out.println("Remote file valid - " + remoteFile);
//				System.out.println(
//						"Checking if file is modified really " + remoteFile);
//				long lastModified = Files
//						.getLastModifiedTime(Paths.get(localFile)).toMillis();
//
//				if (lastModified > fileInfo.getLastModified()) {
//					fileInfo.setLastModified(lastModified);
//					System.out.println("lastModified: " + lastModified);
//					System.out.println("File is modified really " + remoteFile);
//					BasicFileUploader fd = new BasicFileUploader(info,
//							remoteFile, localFile,
//							getDesktop().getEditTransferQueue());
//					getDesktop().getTransferWatcher().addTransfer(fd);
//				}
//			} else {
//				System.out.println("File invalid: " + localFile);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

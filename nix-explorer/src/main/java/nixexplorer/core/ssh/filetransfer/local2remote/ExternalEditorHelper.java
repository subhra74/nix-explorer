//package nixexplorer.core.ssh.filetransfer.local2remote;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//
//import nixexplorer.App;
//import nixexplorer.Constants;
//import nixexplorer.PathUtils;
//import nixexplorer.app.session.SessionInfo;
//import nixexplorer.core.ssh.filetransfer.remote2local.BasicFileDownloader;
//import nixexplorer.desktop.DesktopPanel;
//import nixexplorer.widgets.listeners.AppEventListener;
//
//public class ExternalEditorHelper {
//	private Map<String, FileInfo> externalEditMap = new ConcurrentHashMap<>();
//	private SessionInfo info;
//	private DesktopPanel desktop;
//	private Map<String, FileAction> tempTransferMap = new ConcurrentHashMap<>();
//
//	public ExternalEditorHelper(SessionInfo info, DesktopPanel desktop) {
//		super();
//		this.info = info;
//		this.desktop = desktop;
//		this.desktop.registerAppEventListener(new AppEventListener() {
//
//			@Override
//			public void onEvent(long eventId, Object eventData) {
//				System.out.println("Event received- eventData " + eventId
//						+ " eventData " + eventData);
//				if (eventId == Constants.DOWNLOAD_FINISHED) {
//					System.out.println("Download complete event received");
//
//					Properties data = (Properties) eventData;
//					// String src = data.getProperty("download.srcfile");
//					String dst = data.getProperty("download.dstfile");
//
//					FileAction fa = tempTransferMap.get(dst);
//					
//					if(fa==null) {
//						return;
//					}
//
//					String file = fa.getRemoteFile();
//					if (file != null) {
//						System.out.println(
//								"###File download complete - opening in editor: "
//										+ file);
//						try {
//							externalEditMap.put(dst, new FileInfo(file,
//									Files.getLastModifiedTime(Paths.get(dst))
//											.toMillis()));
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//						tempTransferMap.remove(dst);
//						if (fa.edit) {
//							openEditor(dst);
//						} else {
//							openWithDefaultApp(dst);
//						}
//					}
//				}
//			}
//		});
//	}
//
//	private void openWithDefaultApp(String file) {
//		System.out.println("Edit file: " + file);
//		openDefaultApp(file);
//		Path path = Paths.get(file).getParent();
//		System.out.println("Registerting watcher " + path);
//		desktop.getEditWatcher().register(path);
//	}
//
//	private void openEditor(String file) {
//		System.out.println("Edit file: " + file);
//		// openDefaultApp(file);
//		openEditApp(file);
//		Path path = Paths.get(file).getParent();
//		System.out.println("Registerting watcher " + path);
//		desktop.getEditWatcher().register(path);
//	}
//
//	public void openForEdit(String remoteFile) {
//		// opens with external editor
//		open(remoteFile, true);
//	}
//
//	public void openDefault(String remoteFile) {
//		// opens with default app
//		open(remoteFile, false);
//	}
//
//	private void open(String remoteFile, boolean edit) {
//		String dir = App.getConfig("temp.dir") + File.separatorChar
//				+ info.hashCode() + File.separatorChar + UUID.randomUUID();
//		try {
//			Files.createDirectories(Paths.get(dir));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		String file = Paths.get(dir, PathUtils.getFileName(remoteFile))
//				.toAbsolutePath().toString();
//		tempTransferMap.put(file, new FileAction(remoteFile, edit));
//		BasicFileDownloader d = new BasicFileDownloader(info, remoteFile, file,
//				desktop.getEditTransferQueue());
//		desktop.getTransferWatcher().addTransfer(d);
//	}
//
//	public Map<String, FileInfo> getExternalEditMap() {
//		return externalEditMap;
//	}
//
//	public class FileInfo {
//		private String name;
//		private long lastModified;
//		private boolean edit;
//
//		public FileInfo(String name, long lastModified) {
//			super();
//			this.name = name;
//			this.lastModified = lastModified;
//		}
//
//		public String getName() {
//			return name;
//		}
//
//		public void setName(String name) {
//			this.name = name;
//		}
//
//		public long getLastModified() {
//			return lastModified;
//		}
//
//		public void setLastModified(long lastModified) {
//			this.lastModified = lastModified;
//		}
//
//		public boolean isEdit() {
//			return edit;
//		}
//
//		public void setEdit(boolean edit) {
//			this.edit = edit;
//		}
//	}
//
//	public void openDefaultApp(String file) {
//		String os = System.getProperty("os.name").toLowerCase();
//		System.out.println("Operating system: "+os);
//		if (os.contains("linux")) {
//			openDefaultAppLinux(file);
//		} else if (os.contains("mac") || os.contains("darwin")
//				|| os.contains("os x")) {
//			openDefaultAppOSX(file);
//		} else if (os.contains("windows")) {
//			openDefaultAppWin(file);
//		}
//	}
//
//	public void openDefaultAppLinux(String file) {
//		try {
//			System.out.println("Opening linux app");
//			ProcessBuilder pb = new ProcessBuilder();
//			pb.command("xdg-open", file);
//			pb.start();// .waitFor();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void openDefaultAppWin(String file) {
//		try {
//			ProcessBuilder builder = new ProcessBuilder();
//			List<String> lst = new ArrayList<String>();
//			lst.add("rundll32");
//			lst.add("url.dll,FileProtocolHandler");
//			lst.add(file);
//			builder.command(lst);
//			builder.start();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void openDefaultAppOSX(String file) {
//		try {
//			ProcessBuilder pb = new ProcessBuilder();
//			pb.command("open", file);
//			pb.start();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public class FileAction {
//		private String remoteFile;
//		private boolean edit;
//
//		public FileAction(String remoteFile, boolean edit) {
//			super();
//			this.remoteFile = remoteFile;
//			this.edit = edit;
//		}
//
//		public String getRemoteFile() {
//			return remoteFile;
//		}
//
//		public void setRemoteFile(String remoteFile) {
//			this.remoteFile = remoteFile;
//		}
//
//		public boolean isEdit() {
//			return edit;
//		}
//
//		public void setEdit(boolean edit) {
//			this.edit = edit;
//		}
//	}
//
//	public void openEditApp(String file) {
//		try {
//			ProcessBuilder builder = new ProcessBuilder();
//			List<String> lst = new ArrayList<String>();
//			lst.add(App.getConfig("editor.app"));
//			lst.add(file);
//			builder.command(lst);
//			builder.start();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//}

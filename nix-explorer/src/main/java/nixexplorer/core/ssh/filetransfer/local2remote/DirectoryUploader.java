//package nixexplorer.core.ssh.filetransfer.local2remote;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Future;
//
//import com.jcraft.jsch.ChannelSftp;
//
//import nixexplorer.PathUtils;
//import nixexplorer.core.FileType;
//import nixexplorer.app.session.SessionInfo;
//import nixexplorer.core.ssh.SshWrapper;
//import nixexplorer.core.ssh.filetransfer.FileTransfer;
//import nixexplorer.core.ssh.filetransfer.PathEntry;
//import nixexplorer.core.ssh.filetransfer.SshConnectionPool;
//import nixexplorer.core.ssh.filetransfer.TransferQueue;
//import nixexplorer.widgets.listeners.AppMessageListener;
//import nixexplorer.widgets.listeners.AppMessageListener.TransferStatus;
//
//public class DirectoryUploader implements FileTransfer, Runnable {
//
//	private SshWrapper wrapper;
//	private List<PathEntry> fileList;
//	private String baseFolderLocal;
//	private String baseFolderRemote;
//	private ChannelSftp sftp;
//	private AppMessageListener notifier;
//	private boolean stopFlag;
//	private Future<?> threadHandle;
//	private SessionInfo info;
//	private TransferQueue queue;
//
//	public DirectoryUploader(SessionInfo info, String baseFolderLocal, String baseFolderRemote, TransferQueue queue) {
//		this.info = info;
//		this.baseFolderLocal = baseFolderLocal;
//		this.baseFolderRemote = baseFolderRemote;
//		this.queue = queue;
//	}
//
//	private void notify(TransferStatus status, FileTransfer f) {
//		System.out.println(status + " notifying: " + notifier);
//		if (notifier != null) {
//			notifier.notify(status, f);
//		}
//	}
//
//	@Override
//	public void run() {
//		notify(TransferStatus.Initiating, this);
//		while (true) {
//			if (stopFlag) {
//				notify(TransferStatus.stopped, this);
//				close();
//				return;
//			}
//			try {
//				outer: {
//					for (int i = 0; i < 10; i++) {
//						try {
//							wrapper = SshConnectionPool.getSharedInstance()
//									.getCachedEntry(info.getUser() + "@" + info.getHost());
//							if (wrapper == null) {
//								wrapper = new SshWrapper(info);
//								wrapper.connect();
//							}
//							System.out.println("Directory walker: creating new channel");
//							sftp = wrapper.getSftpChannel();
//							break outer;
//						} catch (Exception e) {
//							e.printStackTrace();
//							Thread.sleep(5 * 1000);
//						}
//						if (stopFlag) {
//							notify(TransferStatus.stopped, this);
//							close();
//							return;
//						}
//					}
//					close();
//					return;
//				}
//				if (stopFlag) {
//					notify(TransferStatus.stopped, this);
//					close();
//					return;
//				}
//				fileList = new ArrayList<>();
//				walk(baseFolderRemote, baseFolderLocal);
//				close();
//				notify(TransferStatus.Complete, this);
//				for (PathEntry e : fileList) {
//					BasicFileUploader bf = new BasicFileUploader(this.info, e.getRemotePath(), e.getLocalPath(), queue);
//					notifier.getTransferWatcher().addTransfer(bf);
//				}
//				return;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	private void close() {
//		try {
//			System.out.println("Directory sftp channel closed");
//			sftp.disconnect();
//		} catch (Exception e) {
//		}
//		try {
//			if (wrapper.isConnected()) {
//				SshConnectionPool.getSharedInstance().putEntry(info.getUser() + "@" + info.getHost(), wrapper);
//				wrapper = null;
//			}
//		} catch (Exception e) {
//		}
//	}
//
//	private void walk(String remotePath, String localPath) throws Exception {
//		System.out.println("Mkdir: " + remotePath);
//		String parent = PathUtils.getParent(remotePath);
//		String name = PathUtils.getFileName(remotePath);
//		sftp.cd(parent);
//		try {
//			sftp.mkdir(name);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		File[] files = new File(localPath).listFiles();
//		for (File f : files) {
//			if (f.isDirectory()) {
//				String folderName = f.getName();
//				walk(PathUtils.combineUnix(remotePath, folderName), f.getAbsolutePath());
//			} else {
//				String fileName = f.getName();
//				System.out.println("Adding file: " + remotePath + "-" + fileName);
//				fileList.add(new PathEntry(PathUtils.combineUnix(remotePath, fileName), f.getAbsolutePath()));
//			}
//		}
//	}
//
//	@Override
//	public void start() {
//		threadHandle = queue.submit(this);
//	}
//
//	@Override
//	public void resume() {
//		stopFlag = false;
//		start();
//	}
//
//	@Override
//	public void stop() {
//		stopFlag = true;
//		if (threadHandle != null) {
//			threadHandle.cancel(true);
//		}
//	}
//
//	@Override
//	public int getPercentComplete() {
//		return 0;
//	}
//
//	@Override
//	public String getHostName() {
//		return info.getHost();
//	}
//
//	@Override
//	public String getSourceFileName() {
//		System.err.println("called for " + baseFolderRemote);
//		return baseFolderRemote;
//	}
//
//	@Override
//	public String getTargetFileName() {
//		System.err.println("called for " + baseFolderLocal);
//		return baseFolderLocal;
//	}
//
//	@Override
//	public long getSize() {
//		return 0;
//	}
//
//	@Override
//	public long getId() {
//		return hashCode();
//	}
//
//	@Override
//	public void setStatusListener(AppMessageListener listener) {
//		this.notifier = listener;
//	}
//
//	@Override
//	public void removeStatusListener() {
//		this.notifier = null;
//	}
//
//	@Override
//	public FileType getType() {
//		return FileType.Directory;
//	}
//
//}

//package nixexplorer.core.ssh.filetransfer.remote2local;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Vector;
//import java.util.concurrent.Future;
//
//import com.jcraft.jsch.ChannelSftp;
//import com.jcraft.jsch.ChannelSftp.LsEntry;
//import com.jcraft.jsch.SftpATTRS;
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
//public class DirectoryDownloader implements FileTransfer, Runnable {
//
//	private SshWrapper wrapper;
//	private List<PathEntry> fileList;
//	private String baseFolderLocal;
//	private String baseFolderRemote;
//	private ChannelSftp sftp;
//	protected AppMessageListener notifier;
//	private boolean stopFlag;
//	private Future<?> threadHandle;
//	private SessionInfo info;
//	private TransferQueue queue;
//
//	public DirectoryDownloader(SessionInfo info, String baseFolderLocal, String baseFolderRemote, TransferQueue queue) {
//		this.info = info;
//		this.baseFolderLocal = baseFolderLocal;
//		this.baseFolderRemote = baseFolderRemote;
//		this.queue = queue;
//	}
//
//	private void notify(TransferStatus status, FileTransfer f) {
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
//					BasicFileDownloader bf = new BasicFileDownloader(this.info, e.getRemotePath(), e.getLocalPath(),
//							queue);
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
//		System.out.println("Mkdir: " + localPath);
//		new File(localPath).mkdirs();
//		Vector list = sftp.ls(remotePath);
//		System.out.println("Sftp ls: " + remotePath);
//		for (int i = 0; i < list.size(); i++) {
//			LsEntry ent = (LsEntry) list.get(i);
//			if (ent.getFilename().equals(".") || ent.getFilename().equals("..")) {
//				continue;
//			}
//			SftpATTRS attrs = ent.getAttrs();
//			if (attrs.isDir()) {
//				String name = ent.getFilename();
//				walk(PathUtils.combineUnix(remotePath, ent.getFilename()), new File(localPath, name).getAbsolutePath());
//			} else {
//				String name = ent.getFilename();
//				System.out.println("Adding file: " + ent.getFilename() + localPath + "-" + name);
//				fileList.add(new PathEntry(PathUtils.combineUnix(remotePath, ent.getFilename()),
//						new File(localPath, name).getAbsolutePath()));
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

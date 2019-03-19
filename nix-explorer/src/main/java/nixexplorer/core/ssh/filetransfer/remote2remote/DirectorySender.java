//package nixexplorer.core.ssh.filetransfer.remote2remote;
//
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
//public class DirectorySender implements FileTransfer, Runnable {
//
//	private SshWrapper wrapper1, wrapper2;
//	private List<PathEntry> fileList;
//	private String baseFolder1;
//	private String baseFolder2;
//	private ChannelSftp sftp1, sftp2;
//	protected AppMessageListener notifier;
//	private boolean stopFlag;
//	private Future<?> threadHandle;
//	private SessionInfo info1, info2;
//	private TransferQueue queue;
//
//	public DirectorySender(SessionInfo info1, SessionInfo info2, String baseFolder1, String baseFolder2,
//			TransferQueue queue) {
//		this.info1 = info1;
//		this.info2 = info2;
//		this.baseFolder1 = baseFolder1;
//		this.baseFolder2 = baseFolder2;
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
//				close1();
//				close2();
//				return;
//			}
//			try {
//				outer1: {
//					for (int i = 0; i < 10; i++) {
//						try {
//							wrapper1 = SshConnectionPool.getSharedInstance()
//									.getCachedEntry(info1.getUser() + "@" + info1.getHost());
//							if (wrapper1 == null) {
//								wrapper1 = new SshWrapper(info1);
//								wrapper1.connect();
//							}
//							System.out.println("Directory walker: creating new channel 1");
//							sftp1 = wrapper1.getSftpChannel();
//							break outer1;
//						} catch (Exception e) {
//							e.printStackTrace();
//							close1();
//							Thread.sleep(5 * 1000);
//						}
//						if (stopFlag) {
//							notify(TransferStatus.stopped, this);
//							close1();
//							close2();
//							return;
//						}
//					}
//					close1();
//					close2();
//					return;
//				}
//
//				outer2: {
//					for (int i = 0; i < 10; i++) {
//						try {
//							wrapper2 = SshConnectionPool.getSharedInstance()
//									.getCachedEntry(info2.getUser() + "@" + info2.getHost());
//							if (wrapper2 == null) {
//								wrapper2 = new SshWrapper(info2);
//								wrapper2.connect();
//							}
//							System.out.println("Directory walker: creating new channel 2");
//							sftp2 = wrapper2.getSftpChannel();
//							break outer2;
//						} catch (Exception e) {
//							e.printStackTrace();
//							close2();
//							Thread.sleep(5 * 1000);
//						}
//						if (stopFlag) {
//							notify(TransferStatus.stopped, this);
//							close1();
//							close2();
//							return;
//						}
//					}
//					close1();
//					close2();
//					return;
//				}
//
//				if (stopFlag) {
//					notify(TransferStatus.stopped, this);
//					close1();
//					close2();
//					return;
//				}
//				fileList = new ArrayList<>();
//				walk(baseFolder2, baseFolder1);
//				close1();
//				close2();
//				notify(TransferStatus.Complete, this);
//				for (PathEntry e : fileList) {
//					BasicFileSender bf = new BasicFileSender(this.info1, this.info2, e.getLocalPath(),
//							e.getRemotePath(), queue);
//					notifier.getTransferWatcher().addTransfer(bf);
//				}
//				return;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	private void close1() {
//		try {
//			System.out.println("Directory sftp channel1 closed");
//			sftp1.disconnect();
//		} catch (Exception e) {
//		}
//		try {
//			if (wrapper1.isConnected()) {
//				SshConnectionPool.getSharedInstance().putEntry(info1.getUser() + "@" + info1.getHost(), wrapper1);
//				wrapper1 = null;
//			}
//		} catch (Exception e) {
//		}
//	}
//
//	private void close2() {
//		try {
//			System.out.println("Directory sftp channel2 closed");
//			sftp2.disconnect();
//		} catch (Exception e) {
//		}
//		try {
//			if (wrapper2.isConnected()) {
//				SshConnectionPool.getSharedInstance().putEntry(info1.getUser() + "@" + info1.getHost(), wrapper2);
//				wrapper2 = null;
//			}
//		} catch (Exception e) {
//		}
//	}
//
//	private void walk(String targetFolder, String sourceFolder) throws Exception {
//		// sftp1 -> source
//		// sftp2 -> destination
//		System.out.println("Mkdir: " + targetFolder);
//		String parent = PathUtils.getParent(targetFolder);
//		String name = PathUtils.getFileName(targetFolder);
//		System.out.println("cd to " + parent);
//		sftp2.cd(parent);
//		try {
//			sftp2.mkdir(name);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		Vector list = sftp1.ls(sourceFolder);
//		System.out.println("Sftp ls: " + sourceFolder);
//		for (int i = 0; i < list.size(); i++) {
//			LsEntry ent = (LsEntry) list.get(i);
//			if (ent.getFilename().equals(".") || ent.getFilename().equals("..")) {
//				continue;
//			}
//			SftpATTRS attrs = ent.getAttrs();
//			if (attrs.isDir()) {
//				walk(PathUtils.combineUnix(targetFolder, ent.getFilename()),
//						PathUtils.combineUnix(sourceFolder, ent.getFilename()));
//			} else {
//				System.out.println("Adding file: " + ent.getFilename() + sourceFolder + "-" + name);
//				fileList.add(new PathEntry(PathUtils.combineUnix(targetFolder, ent.getFilename()),
//						PathUtils.combineUnix(sourceFolder, ent.getFilename())));
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
//		return info1.getHost();
//	}
//
//	@Override
//	public String getSourceFileName() {
//		System.err.println("called for " + baseFolder2);
//		return baseFolder2;
//	}
//
//	@Override
//	public String getTargetFileName() {
//		System.err.println("called for " + baseFolder1);
//		return baseFolder1;
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

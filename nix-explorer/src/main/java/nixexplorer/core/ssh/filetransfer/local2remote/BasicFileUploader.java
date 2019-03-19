//package nixexplorer.core.ssh.filetransfer.local2remote;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.RandomAccessFile;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.Future;
//
//import com.jcraft.jsch.ChannelSftp;
//import com.jcraft.jsch.SftpATTRS;
//
//import nixexplorer.PathUtils;
//import nixexplorer.core.FileType;
//import nixexplorer.app.session.SessionInfo;
//import nixexplorer.core.ssh.SshWrapper;
//import nixexplorer.core.ssh.filetransfer.FileTransfer;
//import nixexplorer.core.ssh.filetransfer.SshConnectionPool;
//import nixexplorer.core.ssh.filetransfer.TransferQueue;
//import nixexplorer.widgets.listeners.AppMessageListener;
//import nixexplorer.widgets.listeners.AppMessageListener.TransferStatus;
//
//public class BasicFileUploader implements Runnable, FileTransfer {
//	protected SshWrapper wrapper;
//	protected AppMessageListener notifier;
//	protected String localFile;
//	protected ChannelSftp sftp;
//	protected int maxretry = 5;
//	protected String remoteFile;
//	protected long offset;
//	protected byte[] b;
//	protected RandomAccessFile raf;
//	protected int backOffTime = 30;
//
//	private boolean stopFlag;
//	private long size;
//	private Future<?> threadHandle;
//	private long lastNotified;
//	private SessionInfo info;
//	private long uploaded;
//	private TransferQueue transferQueue;
//	// private OverWriteMode mode = OverWriteMode.OverWrite;
//
//	public enum OverWriteMode {
//		OverWrite, Skip
//	}
//
//	public BasicFileUploader(SessionInfo info, String remoteFile, String localFile, TransferQueue transferQueue) {
//		this.info = info;
//		this.remoteFile = remoteFile;
//		this.localFile = localFile;
//		this.transferQueue = transferQueue;
//	}
//
//	public void start() {
//		lastNotified = System.currentTimeMillis();
//		threadHandle = transferQueue.submit(this);
//		System.out.println("Subitted upload");
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
//		System.out.println("Staring upload");
//		notify(TransferStatus.Initiating, this);
//		while (true) {
//			if (stopFlag) {
//				notify(TransferStatus.stopped, this);
//				close();
//				return;
//			}
//			outer: {
//				for (int i = 0; i < maxretry; i++) {
//					try {
//						connect();
//						if (stopFlag) {
//							notify(TransferStatus.stopped, this);
//							close();
//							return;
//						}
//						notify(TransferStatus.InProgress, this);
//						break outer;
//					} catch (Exception e) {
//						e.printStackTrace();
//						try {
//							Thread.sleep(backOffTime * 1000);
//						} catch (InterruptedException e1) {
//							e1.printStackTrace();
//						}
//						if (stopFlag) {
//							notify(TransferStatus.stopped, this);
//							close();
//							return;
//						}
//					}
//				}
//				close();
//				return;
//			}
//			try {
//				upload();
//				System.out.println("Completed upload for: " + remoteFile + " at offset: " + offset);
//				notify(TransferStatus.Complete, this);
//				close();
//				return;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			close();
//		}
//	}
//
//	private void close() {
//		try {
//			raf.close();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		if (sftp != null) {
//			try {
//				sftp.disconnect();
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//		}
//
//		try {
//
//			if (wrapper.isConnected()) {
//				SshConnectionPool.getSharedInstance().putEntry(info.getUser() + "@" + info.getHost(), wrapper);
//				System.out.println("Putting entry to pool: " + info.getUser() + "@" + info.getHost());
//				wrapper = null;
//			} else {
//				System.out.println("not connected");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		b = null;
//	}
//
//	private void upload() throws Exception {
//		System.out.println("Starting upload for: " + remoteFile + " at offset: " + offset);
//		File f = new File(localFile);
//		size = f.length();
//		long modified = f.lastModified();
//		raf = new RandomAccessFile(localFile, "rw");
//
//		String folder = PathUtils.getParent(remoteFile);
//
//		String tempFileName = PathUtils.combineUnix(folder, "." + (size + modified) + ".filepart");
//
//		try {
//			SftpATTRS attrs = sftp.stat(tempFileName);
//			offset = attrs.getSize();
//			raf.seek(offset);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		uploaded = offset;
//
//		OutputStream out = sftp.put(tempFileName, ChannelSftp.APPEND);
//		while (true) {
//			if (stopFlag) {
//				throw new IOException("Stopped");
//			}
//			int x = raf.read(b);
//			if (x == -1)
//				break;
//			out.write(b, 0, x);
//			uploaded += x;
//			long t = System.currentTimeMillis();
//			if (t - lastNotified > 1000) {
//				notify(TransferStatus.InProgress, this);
//			}
//		}
//		out.close();
//
//		try {
//			sftp.rm(remoteFile);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		sftp.rename(tempFileName, remoteFile);
//
//		raf.close();
//		sftp.disconnect();
//	}
//
//	private void connect() throws Exception {
//		System.out.println("Connecting...");
//		wrapper = SshConnectionPool.getSharedInstance().getCachedEntry(info.getUser() + "@" + info.getHost());
//		if (wrapper == null) {
//			System.out.println("No cached entry for: " + info.getUser() + "@" + info.getHost());
//
//			wrapper = new SshWrapper(info);
//			wrapper.connect();
//		} else {
//			System.out.println("Reused");
//		}
//		sftp = wrapper.getSftpChannel();
//		b = new byte[8192];
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
//		return (int) (size == 0 ? 0 : ((uploaded * 100) / size));
//	}
//
//	@Override
//	public String getHostName() {
//		return info.getHost();
//	}
//
//	@Override
//	public String getSourceFileName() {
//		return this.remoteFile;
//	}
//
//	@Override
//	public String getTargetFileName() {
//		return this.localFile;
//	}
//
//	@Override
//	public long getSize() {
//		return this.size;
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
//		return FileType.File;
//	}
//}

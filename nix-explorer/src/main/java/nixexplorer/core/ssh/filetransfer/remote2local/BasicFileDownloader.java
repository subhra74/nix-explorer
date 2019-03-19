//package nixexplorer.core.ssh.filetransfer.remote2local;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.RandomAccessFile;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.Future;
//
//import com.jcraft.jsch.ChannelSftp;
//import com.jcraft.jsch.SftpATTRS;
//
//import nixexplorer.core.FileType;
//import nixexplorer.app.session.SessionInfo;
//import nixexplorer.core.ssh.SshWrapper;
//import nixexplorer.core.ssh.filetransfer.FileTransfer;
//import nixexplorer.core.ssh.filetransfer.SshConnectionPool;
//import nixexplorer.core.ssh.filetransfer.TransferQueue;
//import nixexplorer.widgets.listeners.AppMessageListener;
//import nixexplorer.widgets.listeners.AppMessageListener.TransferStatus;
//
//public class BasicFileDownloader implements Runnable, FileTransfer {
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
//	protected boolean sessionOwner = false;
//	private boolean stopFlag;
//	private long size;
//	private Future<?> threadHandle;
//	private long lastNotified;
//	private SessionInfo info;
//	private TransferQueue queue;
//
//	public BasicFileDownloader(SessionInfo info, String remoteFile, String localFile, TransferQueue queue) {
//		this.info = info;
//		this.remoteFile = remoteFile;
//		this.localFile = localFile;
//		this.queue = queue;
//	}
//
//	public void start() {
//		lastNotified = System.currentTimeMillis();
//		threadHandle = queue.submit(this);
//		System.out.println("Subitted download");
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
//		System.out.println("Staring download");
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
//				download();
//				System.out.println("Completed download for: " + remoteFile + " at offset: " + offset);
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
//	private void download() throws Exception {
//		System.out.println("Starting download for: " + remoteFile + " at offset: " + offset);
//		raf = new RandomAccessFile(localFile, "rw");
//		if (size == 0) {
//			SftpATTRS attrs = sftp.stat(remoteFile);
//			size = attrs.getSize();
//			raf.setLength(size);
//			System.out.println("Creating new file at local: " + localFile);
//		}
//		raf.seek(offset);
//		InputStream in = sftp.get(remoteFile, null, offset);
//		while (true) {
//			if (stopFlag) {
//				throw new IOException("Stopped");
//			}
//			int x = in.read(b);
//			if (x == -1)
//				break;
//			raf.write(b, 0, x);
//			offset += x;
//			long t = System.currentTimeMillis();
//			if (t - lastNotified > 1000) {
//				notify(TransferStatus.InProgress, this);
//			}
//		}
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
//		return (int) (size == 0 ? 0 : ((offset * 100) / size));
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
//		notifier = listener;
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

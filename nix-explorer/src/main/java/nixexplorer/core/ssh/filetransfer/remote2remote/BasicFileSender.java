//package nixexplorer.core.ssh.filetransfer.remote2remote;
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
//public class BasicFileSender implements Runnable, FileTransfer {
//	protected SshWrapper wrapper1, wrapper2;
//	protected AppMessageListener notifier;
//	protected String sourceFile;
//	protected ChannelSftp sftp1, sftp2;
//	protected int maxretry = 5;
//	protected String targetFile;
//	protected long offset;
//	protected byte[] b;
//	// protected RandomAccessFile raf;
//	protected int backOffTime = 30;
//
//	private boolean stopFlag;
//	private long size;
//	private Future<?> threadHandle;
//	private long lastNotified;
//	private SessionInfo info1, info2;
//	private TransferQueue queue;
//	// private OverWriteMode mode = OverWriteMode.OverWrite;
//
//	public enum OverWriteMode {
//		OverWrite, Skip
//	}
//
//	public BasicFileSender(SessionInfo sourceInfo, SessionInfo targetInfo, String sourceFile, String targetFile,
//			TransferQueue queue) {
//		this.info1 = sourceInfo;
//		this.info2 = targetInfo;
//		this.sourceFile = sourceFile;
//		this.targetFile = targetFile;
//		b = new byte[8192];
//		this.queue = queue;
//	}
//
//	public void start() {
//		lastNotified = System.currentTimeMillis();
//		threadHandle = queue.submit(this);
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
//		System.out.println("Staring transfer");
//		notify(TransferStatus.Initiating, this);
//		while (true) {
//			if (stopFlag) {
//				close();
//				notify(TransferStatus.stopped, this);
//				return;
//			}
//
//			if (!connect(true)) {
//				close();
//				notify(TransferStatus.stopped, this);
//				return;
//			}
//
//			if (!connect(false)) {
//				close();
//				notify(TransferStatus.stopped, this);
//				return;
//			}
//
//			try {
//				upload();
//				System.out.println("Completed upload for: " + targetFile + " at offset: " + offset);
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
//			sftp1.disconnect();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//
//		try {
//			sftp2.disconnect();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//
//		try {
//			if (wrapper1.isConnected()) {
//				SshConnectionPool.getSharedInstance().putEntry(info1.getUser() + "@" + info1.getHost(), wrapper1);
//				System.out.println("Putting entry to pool: " + info1.getUser() + "@" + info1.getHost());
//				wrapper1 = null;
//			} else {
//				System.out.println("not connected");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		try {
//			if (wrapper2.isConnected()) {
//				SshConnectionPool.getSharedInstance().putEntry(info2.getUser() + "@" + info2.getHost(), wrapper2);
//				System.out.println("Putting entry to pool: " + info2.getUser() + "@" + info2.getHost());
//				wrapper2 = null;
//			} else {
//				System.out.println("not connected");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void upload() throws Exception {
//		InputStream in = null;
//
//		try {
//			System.out
//					.println("Starting upload for: " + targetFile + " at offset: " + offset + " source: " + sourceFile);
//
//			SftpATTRS attrs = sftp1.stat(sourceFile);
//			long size = attrs.getSize();
//			long modified = attrs.getMTime();
//
//			String folder = PathUtils.getParent(targetFile);
//
//			String tempFileName = PathUtils.combineUnix(folder, "." + (size + modified) + ".filepart");
//
//			offset = 0;
//
//			try {
//				SftpATTRS attrs2 = sftp2.stat(tempFileName);
//				offset = attrs2.getSize();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			in = sftp1.get(sourceFile, null, offset);
//
//			System.out.println("Opening output stream: " + tempFileName);
//
//			OutputStream out = sftp2.put(tempFileName, ChannelSftp.APPEND);
//			while (true) {
//				if (stopFlag) {
//					throw new IOException("Stopped");
//				}
//				int x = in.read(b);
//				if (x == -1)
//					break;
//				out.write(b, 0, x);
//				long t = System.currentTimeMillis();
//				if (t - lastNotified > 1000) {
//					notify(TransferStatus.InProgress, this);
//				}
//			}
//			out.close();
//			in.close();
//			try {
//				sftp2.rm(targetFile);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			sftp2.rename(tempFileName, targetFile);
//
//			sftp2.disconnect();
//			sftp1.disconnect();
//
//		} catch (Exception e) {
//			try {
//				in.close();
//			} catch (Exception e2) {
//				// TODO: handle exception
//			}
//			throw e;
//		}
//	}
//
//	private void connect2() throws Exception {
//		System.out.println("Connecting...");
//		wrapper2 = SshConnectionPool.getSharedInstance().getCachedEntry(info2.getUser() + "@" + info2.getHost());
//		if (wrapper2 == null) {
//			System.out.println("No cached entry for: " + info2.getUser() + "@" + info2.getHost());
//
//			wrapper2 = new SshWrapper(info2);
//			wrapper2.connect();
//		} else {
//			System.out.println("Reused");
//		}
//		sftp2 = wrapper2.getSftpChannel();
//		if (b == null) {
//			b = new byte[8192];
//		}
//	}
//
//	private void connect1() throws Exception {
//		System.out.println("Connecting...");
//		wrapper1 = SshConnectionPool.getSharedInstance().getCachedEntry(info1.getUser() + "@" + info1.getHost());
//		if (wrapper1 == null) {
//			System.out.println("No cached entry for: " + info1.getUser() + "@" + info1.getHost());
//
//			wrapper1 = new SshWrapper(info1);
//			wrapper1.connect();
//		} else {
//			System.out.println("Reused");
//		}
//		sftp1 = wrapper1.getSftpChannel();
//		if (b == null) {
//			b = new byte[8192];
//		}
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
//		return info2.getHost();
//	}
//
//	@Override
//	public String getSourceFileName() {
//		return this.targetFile;
//	}
//
//	@Override
//	public String getTargetFileName() {
//		return this.sourceFile;
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
//
//	private boolean connect(boolean first) {
//		try {
//			ChannelSftp sftp = first ? sftp1 : sftp2;
//			if (sftp != null && sftp.isConnected()) {
//				return true;
//			}
//
//			for (int i = 0; i < maxretry; i++) {
//				if (stopFlag)
//					return false;
//				SshWrapper wrapper = null;
//				try {
//					wrapper = new SshWrapper(first ? info1 : info2);
//					wrapper.connect();
//					sftp = wrapper.getSftpChannel();
//
//					if (first) {
//						this.wrapper1 = wrapper;
//						this.sftp1 = sftp;
//					} else {
//						this.wrapper2 = wrapper;
//						this.sftp2 = sftp;
//					}
//					return true;
//				} catch (Exception e) {
//					e.printStackTrace();
//					Thread.sleep(backOffTime * 1000);
//					try {
//						if (wrapper != null)
//							wrapper.disconnect();
//					} catch (Exception e2) {
//						// TODO: handle exception
//					}
//				}
//
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
//
//}

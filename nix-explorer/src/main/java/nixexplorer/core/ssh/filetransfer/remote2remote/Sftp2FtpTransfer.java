//package nixexplorer.core.ssh.filetransfer.remote2remote;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.concurrent.Future;
//
//import org.apache.commons.net.ftp.FTPFile;
//
//import com.jcraft.jsch.ChannelSftp;
//import com.jcraft.jsch.SftpATTRS;
//
//import nixexplorer.PathUtils;
//import nixexplorer.core.FileType;
//import nixexplorer.app.session.SessionInfo;
//import nixexplorer.core.ftp.FtpSessionInfo;
//import nixexplorer.core.ftp.FtpWrapper;
//import nixexplorer.core.ssh.SshWrapper;
//import nixexplorer.core.ssh.filetransfer.FileTransfer;
//import nixexplorer.core.ssh.filetransfer.TransferQueue;
//import nixexplorer.widgets.listeners.AppMessageListener;
//import nixexplorer.widgets.listeners.AppMessageListener.TransferStatus;
//
//public class Sftp2FtpTransfer implements Runnable, FileTransfer {
//	protected SshWrapper wrapper;
//	protected FtpWrapper ftp;
//	protected AppMessageListener notifier;
//	protected String sourceFile;
//	protected ChannelSftp sftp1;
//	protected int maxretry = 5;
//	protected String targetFile;
//	protected long offset;
//	protected byte[] b;
//	private boolean stopFlag;
//	private long size;
//	private Future<?> threadHandle;
//	private long lastNotified;
//	private SessionInfo info1;
//	private TransferQueue queue;
//	private FtpSessionInfo ftpInfo;
//	// private OverWriteMode mode = OverWriteMode.OverWrite;
//
//	public enum OverWriteMode {
//		OverWrite, Skip
//	}
//
//	public Sftp2FtpTransfer(SessionInfo sourceInfo, FtpSessionInfo targetInfo,
//			String sourceFile, String targetFile, TransferQueue queue) {
//		this.info1 = sourceInfo;
//		this.ftpInfo = targetInfo;
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
//	private void close() {
//		try {
//			sftp1.disconnect();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//
//		try {
//			wrapper.disconnect();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//
//		try {
//			ftp.disconnect();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//	}
//
//	private boolean connect1() {
//		try {
//			System.out.println("Connecting...");
//			wrapper = new SshWrapper(info1);
//			wrapper.connect();
//			sftp1 = wrapper.getSftpChannel();
//
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return false;
//	}
//
//	private boolean connect2() {
//		try {
//			System.out.println("Connecting...");
//			ftp = new FtpWrapper(ftpInfo);
//			ftp.connect();
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false;
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
//		return ftpInfo.getHost();
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
//	private void upload() throws Exception {
//		InputStream in = null;
//
//		try {
//			System.out.println("Starting upload for: " + targetFile
//					+ " at offset: " + offset + " source: " + sourceFile);
//
//			SftpATTRS attrs = sftp1.stat(sourceFile);
//			long size = attrs.getSize();
//			long modified = attrs.getMTime();
//
//			String folder = PathUtils.getParent(targetFile);
//			String tmpName = "." + (size + modified) + ".filepart";
//			String tempFileName = PathUtils.combineUnix(folder, tmpName);
//
//			offset = 0;
//
//			try {
//				ftp.getFtp().changeWorkingDirectory(folder);
//				FTPFile[] files = ftp.getFtp().listFiles(folder);
//				for (FTPFile file : files) {
//					if (file.getName().equals(tmpName)) {
//						offset = file.getSize();
//						break;
//					}
//				}
////				SftpATTRS attrs2 = sftp2.stat(tempFileName);
////				offset = attrs2.getSize();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			in = sftp1.get(sourceFile, null, offset);
//
//			System.out.println("Opening output stream: " + tempFileName);
//
//			OutputStream out = ftp.getFtp().appendFileStream(tempFileName);
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
//			if (!ftp.getFtp().completePendingCommand()) {
//				throw new Exception("Failed at completePendingCommand");
//			}
//			try {
//				ftp.getFtp().deleteFile(targetFile);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			ftp.getFtp().rename(tempFileName, targetFile);
//
//			ftp.disconnect();
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
//			if (!connect1()) {
//				close();
//				notify(TransferStatus.stopped, this);
//				return;
//			}
//
//			if (!connect2()) {
//				close();
//				notify(TransferStatus.stopped, this);
//				return;
//			}
//
//			try {
//				upload();
//				System.out.println("Completed upload for: " + targetFile
//						+ " at offset: " + offset);
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
//}

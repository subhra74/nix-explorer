/**
 * 
 */
package nixexplorer.widgets.logviewer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

import nixexplorer.app.components.CredentialsDialog;
import nixexplorer.app.components.CredentialsDialog.Credentials;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.DataTransferProgress;
import nixexplorer.core.FileInfo;
import nixexplorer.core.FileSystemProvider;
import nixexplorer.core.ssh.FileSystemWrapper;
import nixexplorer.core.ssh.SshWrapper;

/**
 * @author subhro
 *
 */
public class LogMonitoringEngine
		implements Runnable, SftpProgressMonitor, DataTransferProgress {

	private FileSystemProvider fs;
	private File tempFile;
	private String remoteFile;

	private LogNotificationListener logListener;

	private File indexFile;

	private long lineCount = 1;

	private boolean first = true;

	private long remoteFileLength = 0L;
	private long processed = 0L;

	private AtomicBoolean stopFlag = new AtomicBoolean(false);

	/**
	 * @param tempFile
	 * @param remoteFile
	 * @param info
	 * @param logListener
	 * @param follow
	 * @throws IOException
	 */
	public LogMonitoringEngine(String remoteFile, FileSystemProvider fs,
			LogNotificationListener logListener) throws IOException {
		super();
		this.tempFile = File.createTempFile(UUID.randomUUID().toString(),
				"data");
		this.remoteFile = remoteFile;
		this.fs = fs;
		this.logListener = logListener;
		this.indexFile = File.createTempFile(UUID.randomUUID().toString(),
				"index");
	}

	public LogMonitoringEngine(String remoteFile, FileSystemProvider fs,
			LogNotificationListener logListener, SshWrapper wrapper,
			ChannelSftp sftp) throws IOException {
		this(remoteFile, fs, logListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (!stopFlag.get()) {
			try {
				retrieveFile();
				System.out.println("File retrieved");
				break;
			} catch (Exception e) {
				e.printStackTrace();
				if (!logListener.retry()) {
					return;
				}
			}
		}

		while (!stopFlag.get()) {
			if (logListener.shouldUpdate()) {
				try {
					updateFile();
				} catch (Exception e) {
					if (!logListener.retry()) {
						return;
					}
				}
			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void disconnect() {
		if (fs != null) {
			try {
				fs.close();
			} catch (Exception e) {
			}
		}
	}

	private void retrieveFile() throws Exception {
		System.out.println(
				"Remote file: " + remoteFile + " tempFile: " + tempFile);
		FileInfo f = fs.getInfo(remoteFile);
		this.remoteFileLength = f.getSize();
		fs.copyTo(remoteFile, tempFile.getAbsolutePath(), this,
				ChannelSftp.RESUME);

		System.out.println("Size of file: " + tempFile.length());

		System.out.println("Indexing lines...");
		logListener.setIndeterminate(true);
		indexLines(0);
		logListener.setIndeterminate(false);
		logListener.logChanged();
	}

	/**
	 * @throws Exception
	 * @throws FileNotFoundException
	 * 
	 */
	private void updateFile() throws Exception {
		long sz = tempFile.length();
		FileInfo f = fs.getInfo(remoteFile);

		if (f.getSize() > sz) {
			logListener.setIndeterminate(true);
			try (OutputStream out = new FileOutputStream(tempFile, true)) {
				fs.copyTo(remoteFile, out, this, ChannelSftp.RESUME, sz);
				try {
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				indexLines(sz);
				logListener.logChanged();
			}
			logListener.setIndeterminate(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.SftpProgressMonitor#init(int, java.lang.String,
	 * java.lang.String, long)
	 */
	@Override
	public void init(int op, String src, String dest, long max) {
		processed = 0L;
		logListener.setIndeterminate(false);
		logListener.downloadProgress(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.SftpProgressMonitor#count(long)
	 */
	@Override
	public boolean count(long count) {
		processed += count;
		if (remoteFileLength > 0) {
			logListener.downloadProgress(
					(int) ((processed * 100) / remoteFileLength));
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.SftpProgressMonitor#end()
	 */
	@Override
	public void end() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the tempFile
	 */
	public File getTempFile() {
		return tempFile;
	}

	/**
	 * 
	 */
	private void indexLines(long offset) {
//		int lc = 0;
		long pos = offset;
		try (InputStream in = new FileInputStream(indexFile);
				BufferedOutputStream bout = new BufferedOutputStream(
						new FileOutputStream(indexFile, true))) {
			if (first) {
				bout.write(toBytes(0));
				first = false;
			}
			try (FileInputStream fin = new FileInputStream(tempFile)) {
				fin.skip(offset);
				BufferedInputStream bin = new BufferedInputStream(fin);
				while (true) {
					int x = bin.read();
					if (x == -1) {
						break;
					}
					pos++;
					if (x == '\n') {
						bout.write(toBytes(pos));
//						System.out.println("Line " + lc + " offset: " + pos);
//						lc++;
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Index file size: " + indexFile.length());
	}

	public long getLineCount() {
		long len = indexFile.length();
		if (len < 1) {
			return 0;
		}
		return len / 8;
	}

	public long getLineStart(long lineNum) {
		try (InputStream in = new FileInputStream(indexFile)) {
			in.skip(lineNum * 8);
			byte[] b = new byte[8];
			int x = in.read(b);
			if (x != 8) {
				throw new IOException("Unexpected eof");
			}
			return toLong(b);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private long toLong(byte[] b) {
		return ((long) b[7] << 56) | ((long) b[6] & 0xff) << 48
				| ((long) b[5] & 0xff) << 40 | ((long) b[4] & 0xff) << 32
				| ((long) b[3] & 0xff) << 24 | ((long) b[2] & 0xff) << 16
				| ((long) b[1] & 0xff) << 8 | ((long) b[0] & 0xff);
	}

	private byte[] toBytes(long lng) {
		return new byte[] { (byte) lng, (byte) (lng >> 8), (byte) (lng >> 16),
				(byte) (lng >> 24), (byte) (lng >> 32), (byte) (lng >> 40),
				(byte) (lng >> 48), (byte) (lng >> 56) };
	}

	public LineTextSearch getSearchView(String pattern, boolean fullWord,
			boolean matchCase) throws FileNotFoundException {
		return new LineTextSearch(pattern, fullWord, matchCase);
	}

	public class LineTextSearch {
		private RandomAccessFile indexPointer, dataPointer;
		private Pattern pattern;

		private byte[] buf = new byte[1024];

		public LineTextSearch(String pattern, boolean fullWord,
				boolean matchCase) throws FileNotFoundException {
			indexPointer = new RandomAccessFile(indexFile, "r");
			dataPointer = new RandomAccessFile(tempFile, "r");
			String text = (fullWord ? "\\b" : "") + Pattern.quote(pattern)
					+ (fullWord ? "\\b" : "");
			System.out.println("Search pattern: " + text);
			if (matchCase) {
				this.pattern = Pattern.compile(text);
			} else {
				this.pattern = Pattern.compile(text, Pattern.CASE_INSENSITIVE);
			}
		}

		public void close() {
			try {
				indexPointer.close();
			} catch (Exception ignore) {
			}
			try {
				dataPointer.close();
			} catch (Exception ignore) {
			}
		}

		private long getOffsetForLine(long lineNumber) throws IOException {
			long startIndex = lineNumber * 8;
			if (startIndex >= indexFile.length() || startIndex < 0) {
				return -1;
			}
			indexPointer.seek(startIndex);
			byte b[] = new byte[8];
			if (indexPointer.read(b) != b.length) {
				throw new IOException("Unexpected eof");
			}
			long offset = toLong(b);
			return offset;
		}

		public long findNext(long lineNumber) throws IOException {
			return find(lineNumber, true);
		}

		public long findPrev(long lineNumber) throws IOException {
			return find(lineNumber, false);
		}

		public long find(long lineNumber, boolean forward) throws IOException {
			if (forward) {
				lineNumber++;
			} else {
				lineNumber--;
			}
			while (true) {
				if (lineNumber < 0) {
					return -1;
				}
				long offset = getOffsetForLine(lineNumber);
				if (offset < 0) {
					System.out
							.println("Invalid offset for line: " + lineNumber);
					return -1;
				}

				dataPointer.seek(offset);

				try (RandomAccessInputStream rin = new RandomAccessInputStream(
						dataPointer);
						InputStreamReader r = new InputStreamReader(rin,
								Charset.forName("utf-8"))) {
					StringBuilder sb = new StringBuilder();
					while (true) {
						int x = r.read();
						if (x == '\n' || x == -1) {
							break;
						}

						if (x != '\r') {
							sb.append((char) x);
						}
					}

					Matcher matcher = pattern.matcher(sb);
					if (matcher.find()) {
						System.out.println("matched");
						return lineNumber;
					}
					lineNumber = forward ? lineNumber + 1 : lineNumber - 1;
				}
			}
		}

	}

	public void setStopFlag() {
		stopFlag.set(true);
	}
}

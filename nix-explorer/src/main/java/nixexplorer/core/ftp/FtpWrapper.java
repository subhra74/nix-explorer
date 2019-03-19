package nixexplorer.core.ftp;

import java.io.Closeable;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import nixexplorer.desktop.ResourceManager;

public class FtpWrapper implements Closeable {
	private FTPClient ftp;
	private FtpSessionInfo ftpInfo;
	private String home;

	public FtpWrapper(FtpSessionInfo ftpInfo) {
		super();
		this.ftpInfo = ftpInfo;
	}

	public void connect() throws Exception {
		ResourceManager.register(ftpInfo.getContainterId(), this);
		ftp = new FTPClient();
		ftp.connect(ftpInfo.getHost(), ftpInfo.getPort());
		ftp.login(ftpInfo.getUser(), ftpInfo.getPassword());
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			throw new IOException(ftp.getReplyString());
		}
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			throw new IOException(ftp.getReplyString());
		}
		ftp.enterLocalPassiveMode();
		home = ftp.printWorkingDirectory();
		if (!FTPReply.isPositiveCompletion(reply)) {
			throw new IOException(ftp.getReplyString());
		}
	}

	@Override
	public void close() throws IOException {
		ftp.disconnect();
	}

	public void disconnect() {
		try {
			ftp.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ResourceManager.unregister(ftpInfo.getContainterId(), this);
	}

	public FTPClient getFtp() {
		return ftp;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}
}

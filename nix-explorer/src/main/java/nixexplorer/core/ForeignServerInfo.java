package nixexplorer.core;

public class ForeignServerInfo {
	public static final int SFTP = 0, FTP = 1;
	private String user, password, host, directory;
	private int port;
	private int protocol;

	public ForeignServerInfo(String user, String password, String host,
			String directory, int port, int protocol) {
		super();
		this.user = user;
		this.password = password;
		this.host = host;
		this.directory = directory;
		this.port = port;
		this.protocol = protocol;
	}

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}

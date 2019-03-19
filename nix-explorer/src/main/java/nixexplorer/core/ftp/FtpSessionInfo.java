package nixexplorer.core.ftp;

public class FtpSessionInfo {
	private String host;
	private int port;
	private String user, password;
	private String baseFolder;

	private int containterId = -1;

	public FtpSessionInfo(String host, int port, String user, String password,
			String baseFolder) {
		super();
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.baseFolder = baseFolder;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
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

	public String getBaseFolder() {
		return baseFolder;
	}

	public void setBaseFolder(String baseFolder) {
		this.baseFolder = baseFolder;
	}

	public int getContainterId() {
		return containterId;
	}

	public void setContainterId(int containterId) {
		this.containterId = containterId;
	}
}

/**
 * 
 */
package nixexplorer.widgets.scp;

/**
 * @author subhro
 *
 */
public class ScpServerInfo {
	private String host, user, folder = ".", temp = "/tmp";
	private int transferMode = 0;
	private int port;

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the folder
	 */
	public String getFolder() {
		return folder;
	}

	/**
	 * @param folder the folder to set
	 */
	public void setFolder(String folder) {
		this.folder = folder;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the temp
	 */
	public synchronized String getTemp() {
		return temp;
	}

	/**
	 * @param temp the temp to set
	 */
	public synchronized void setTemp(String temp) {
		this.temp = temp;
	}

	/**
	 * @return the transferMode
	 */
	public synchronized int getTransferMode() {
		return transferMode;
	}

	/**
	 * @param transferMode the transferMode to set
	 */
	public synchronized void setTransferMode(int transferMode) {
		this.transferMode = transferMode;
	}
}

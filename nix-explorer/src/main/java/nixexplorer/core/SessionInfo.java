package nixexplorer.core;

import java.util.ArrayList;
import java.util.List;

public class SessionInfo implements NamedSessionEntity {
	private String id, parentId, name;
	private String host, user, password, localFolder, remoteFolder;
	private int port = 22;
	private List<String> favouriteFolders = new ArrayList<>();
	private int containterId = -1;
	private String privateKeyFile;

	public SessionInfo() {
	}

	public SessionInfo(String id, String host, int port, String user,
			String password, String localFolder, String remoteFolder,
			String parentId, String name, List<String> favouriteFolders) {
		this(id, host, port, user, password, localFolder, remoteFolder,
				parentId, name, favouriteFolders, null);
	}

	public SessionInfo(String id, String host, int port, String user,
			String password, String localFolder, String remoteFolder,
			String parentId, String name, List<String> favouriteFolders,
			String privateKeyFile) {
		super();
		this.id = id;
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.localFolder = localFolder;
		this.remoteFolder = remoteFolder;
		this.parentId = parentId;
		this.name = name;
		this.privateKeyFile = privateKeyFile;
		if (favouriteFolders != null) {
			this.favouriteFolders.addAll(favouriteFolders);
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
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

	public String getLocalFolder() {
		return localFolder;
	}

	public void setLocalFolder(String localFolder) {
		this.localFolder = localFolder;
	}

	public String getRemoteFolder() {
		return remoteFolder;
	}

	public void setRemoteFolder(String remoteFolder) {
		this.remoteFolder = remoteFolder;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public List<String> getFavouriteFolders() {
		return favouriteFolders;
	}

	public void setFavouriteFolders(List<String> favouriteFolders) {
		this.favouriteFolders = favouriteFolders;
	}

	public int getContainterId() {
		return containterId;
	}

	public void setContainterId(int containterId) {
		this.containterId = containterId;
	}

	/**
	 * @return the privateKeyFile
	 */
	public String getPrivateKeyFile() {
		return privateKeyFile;
	}

	/**
	 * @param privateKeyFile the privateKeyFile to set
	 */
	public void setPrivateKeyFile(String privateKeyFile) {
		this.privateKeyFile = privateKeyFile;
	}
}

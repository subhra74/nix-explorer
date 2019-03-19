package nixexplorer.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import nixexplorer.Constants;

public class SessionStore {
	private static SessionStore store;

	private List<SessionInfo> sessions = null;
	private List<SessionFolder> folders = null;
	private String lastConnected = null;
	private List<String> localFolders = new ArrayList<>();

	private SessionStore() {

	}

	public static SessionStore getSharedInstance() {
		if (store == null) {
			store = new SessionStore();
		}
		return store;
	}

	public List<SessionFolder> getFolders() {
		if (sessions == null || folders == null) {
			sessions = new ArrayList<>();
			folders = new ArrayList<>();
			Properties prop = new Properties();
			load(prop);
		}
		return folders;
	}

	public List<SessionInfo> getSessions() {
		if (sessions == null || folders == null) {
			sessions = new ArrayList<>();
			folders = new ArrayList<>();
			Properties prop = new Properties();
			load(prop);
		}
		return sessions;
	}

	public void save(String lastConnected) {
		int folderCount = folders.size();
		int sessionCount = sessions.size();

		Properties prop = new Properties();

		prop.setProperty("session.folders.count", folderCount + "");
		prop.setProperty("session.records.count", sessionCount + "");

		for (int i = 0; i < folderCount; i++) {
			SessionFolder folder = folders.get(i);
			prop.setProperty("session.folders." + i + ".id", folder.getId());
			prop.setProperty("session.folders." + i + ".name",
					folder.getName());
			if (folder.getParentId() != null) {
				prop.setProperty("session.folders." + i + ".parentId",
						folder.getParentId());
			}
		}

		for (int i = 0; i < sessionCount; i++) {
			SessionInfo info = sessions.get(i);
			prop.setProperty("session.records." + i + ".id", info.getId());
			prop.setProperty("session.records." + i + ".name", info.getName());
			prop.setProperty("session.records." + i + ".parentId",
					info.getParentId());
			if (info.getHost() != null) {
				prop.setProperty("session.records." + i + ".host",
						info.getHost());
			}
			prop.setProperty("session.records." + i + ".port",
					info.getPort() + "");
			if (info.getUser() != null) {
				prop.setProperty("session.records." + i + ".user",
						info.getUser());
			}
			if (info.getPassword() != null) {
				prop.setProperty("session.records." + i + ".pass",
						info.getPassword());
			}
			if (info.getLocalFolder() != null) {
				prop.setProperty("session.records." + i + ".local",
						info.getLocalFolder());
			}
			if (info.getRemoteFolder() != null) {
				prop.setProperty("session.records." + i + ".remote",
						info.getRemoteFolder());
			}

			if (info.getFavouriteFolders() != null
					&& info.getFavouriteFolders().size() > 0) {
				String str = String.join(",", info.getFavouriteFolders());
				prop.setProperty("session.records." + i + ".paths", str);
			}
		}

		if (lastConnected == null || lastConnected.length() < 1) {
			lastConnected = this.lastConnected;
		}

		if (lastConnected != null && lastConnected.length() > 0) {
			prop.setProperty("records.last-connected", lastConnected);
		}

		if (localFolders != null && localFolders.size() > 0) {
			prop.setProperty("records.localFolders",
					String.join(",", localFolders));
		}

		try {
			OutputStream out = new FileOutputStream(
					new File(Constants.CONFIG_DIR, Constants.SESSION_DB_FILE));
			prop.store(out, "Session database");
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void loadFolders(Properties prop) {
		int count = Integer
				.parseInt(prop.getProperty("session.folders.count", "0"));
		for (int i = 0; i < count; i++) {
			String folderId = prop.getProperty("session.folders." + i + ".id");
			if (folderId == null) {
				continue;
			}
			String name = prop.getProperty("session.folders." + i + ".name");
			if (name == null) {
				continue;
			}
			String parentId = prop
					.getProperty("session.folders." + i + ".parentId");

			SessionFolder folder = new SessionFolder(folderId, parentId, name);
			folders.add(folder);
		}
	}

	private void loadRecords(Properties prop) {
		int count = Integer
				.parseInt(prop.getProperty("session.records.count", "0"));
		for (int i = 0; i < count; i++) {
			String sessionId = prop.getProperty("session.records." + i + ".id");
			if (sessionId == null) {
				continue;
			}
			String host = prop.getProperty("session.records." + i + ".host");
			int port = Integer.parseInt(
					prop.getProperty("session.records." + i + ".port", "22"));
			String user = prop.getProperty("session.records." + i + ".user");
			String pass = prop.getProperty("session.records." + i + ".pass");
			String localFolder = prop
					.getProperty("session.records." + i + ".local");
			String remoteFolder = prop
					.getProperty("session.records." + i + ".remote");
			String parentId = prop
					.getProperty("session.records." + i + ".parentId");
			String name = prop.getProperty("session.records." + i + ".name");
			String paths = prop.getProperty("session.records." + i + ".paths");
			List<String> list = new ArrayList<>();
			if (paths != null && paths.length() > 0) {
				list = Arrays.asList(paths.split(","));
			}

			SessionInfo info = new SessionInfo(sessionId, host, port, user,
					pass, localFolder, remoteFolder, parentId, name, list);
			sessions.add(info);
		}

		this.lastConnected = prop.getProperty("records.last-connected");
		String propsFolder = prop.getProperty("records.localFolders");
		if (propsFolder != null && propsFolder.length() > 0) {
			this.localFolders.addAll(Arrays.asList(propsFolder.split(",")));
		}
	}

	public String getLastConnected() {
		return this.lastConnected;
	}

	private void load(Properties prop) {
		try {
			InputStream in = new FileInputStream(
					new File(Constants.CONFIG_DIR, Constants.SESSION_DB_FILE));
			prop.load(in);
			in.close();
			loadRecords(prop);
			loadFolders(prop);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<String> getLocalFolders() {
		return localFolders;
	}

	public void setLocalFolders(List<String> localFolders) {
		this.localFolders = localFolders;
	}

}

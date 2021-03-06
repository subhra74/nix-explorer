package nixexplorer;

public interface Constants {
	public static final String CONFIG_DIR = System.getProperty("user.home");
	public static final String SESSION_DB_FILE = "session-store.json";
	public static final String CONFIG_DB_FILE = "config.json";
	public static final int LARGE = 24, NORMAL = 16, SMALL = 12;
	public static final long DOWNLOAD_FINISHED = 10203049, FILE_COPY = 20103489,
			FILE_ADDED = 897678;
}

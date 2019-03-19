package nixexplorer;

import java.util.Properties;

public class TextHolder {
	private static Properties texts = new Properties();

	public static String getString(String key) {
		return texts.getProperty(key);
	}

	public static void addString(String key, String value) {
		texts.setProperty(key, value);
	}
}

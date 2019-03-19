package nixexplorer;

public final class AppClipboard {
	private static Object content;

	/**
	 * 
	 */
	private AppClipboard() {
		// TODO Auto-generated constructor stub
	}

	public static Object getContent() {
		return content;
	}

	public static void setContent(Object content) {
		AppClipboard.content = content;
	}
}

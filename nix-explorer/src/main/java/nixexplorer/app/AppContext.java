/**
 * 
 */
package nixexplorer.app;

import java.awt.Window;
import java.util.WeakHashMap;

import nixexplorer.app.session.AppSession;
import nixexplorer.app.settings.AppConfig;

/**
 * @author subhro
 *
 */
public class AppContext {
	private WeakHashMap<AppSession, Boolean> sessionRefs = new WeakHashMap<>();
	private AppConfig config;
	private Window window;

	public static final AppContext INSTANCE = new AppContext();

	/**
	 * 
	 */
	private AppContext() {
		sessionRefs = new WeakHashMap<>();
		config = AppConfig.load();
	}

	public void addSession(AppSession session) {
		sessionRefs.put(session, Boolean.TRUE);
	}

	/**
	 * @return the config
	 */
	public AppConfig getConfig() {
		return config;
	}

	public void configChanged() {
		System.out.println("config changed called");
		this.config.save();
		for (AppSession session : sessionRefs.keySet()) {
			session.configChanged();
		}
	}

	/**
	 * @return the window
	 */
	public Window getWindow() {
		return window;
	}

	/**
	 * @param window the window to set
	 */
	public void setWindow(Window window) {
		this.window = window;
	}
}

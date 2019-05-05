package nixexplorer.app.session;

import java.awt.Window;
import java.io.File;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.util.Iterator;
import java.util.WeakHashMap;

import javax.swing.JDialog;

import nixexplorer.App;
import nixexplorer.PathUtils;
import nixexplorer.app.AppContext;
import nixexplorer.app.ServerDisplayPanel;
import nixexplorer.app.components.DisposableView;
import nixexplorer.app.components.TabbedChild;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.worker.ChangeWatcher;
import nixexplorer.worker.ChangeWatcherService;


public final class AppSessionImpl implements AppSession {
	private SessionInfo session;
	private boolean running;
	private ServerDisplayPanel display;
	// private Map<String, ChangeUploader> editWatchers = new
	// ConcurrentHashMap<>();
	private Window window;
	private File sessionFolder;
	private WeakHashMap<SessionEventAware, Boolean> eventAwareComponents;
	private WeakHashMap<DisposableView, Boolean> components;
	private ChangeWatcherService service;

	public AppSessionImpl(SessionInfo session, boolean running, Window window) {
		super();
		this.eventAwareComponents = new WeakHashMap<SessionEventAware, Boolean>();
		this.session = session;
		this.running = running;
		this.components = new WeakHashMap<>();
		this.window = window;
		System.out.println("AppSessionImpl - window: " + window);
		sessionFolder = new File((String) App.getConfig("temp.dir"),
				session.getId());
		if (!sessionFolder.exists()) {
			sessionFolder.mkdirs();
		}
		service = new ChangeWatcherService(session, this);
	}

	public SessionInfo getSession() {
		return session;
	}

	public void setSession(SessionInfo session) {
		this.session = session;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public ServerDisplayPanel getDisplay() {
		return display;
	}

	public void setDisplay(ServerDisplayPanel display) {
		this.display = display;
		this.display.setAppSession(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.session.AppSession#createWidget(java.lang.String,
	 * java.lang.String[])
	 */
	@Override
	public synchronized void createWidget(String className, String[] args) {
		try {
			Class<?> clazz = Class.forName(className);
			Constructor<?> ctor = clazz.getConstructor(SessionInfo.class,
					String[].class, AppSession.class, Window.class);
//			if (window == null) {
//				window = SwingUtilities.getWindowAncestor(getDisplay());
//			}
			Object obj = ctor
					.newInstance(new Object[] { session, args, this, window });

			if (obj instanceof TabbedChild) {
				display.addTab((TabbedChild) obj);
				addToSession(obj);
			} else if (obj instanceof JDialog) {
				JDialog dlg = (JDialog) obj;
				addToSession(obj);
				dlg.setVisible(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	/**
//	 * @return the editWatchers
//	 */
//	public Map<String, ChangeUploader> getEditWatchers() {
//		return editWatchers;
//	}
//
//	/**
//	 * @param editWatchers the editWatchers to set
//	 */
//	public void setEditWatchers(Map<String, ChangeUploader> editWatchers) {
//		this.editWatchers = editWatchers;
//	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.session.AppSession#getDirectory()
	 */
	@Override
	public File getDirectory() {
		return sessionFolder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nixexplorer.app.session.AppSession#closeTab(nixexplorer.app.components.
	 * TabbedChild)
	 */
	@Override
	public boolean closeTab(TabbedChild c) {
		return display.closeTab(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.session.AppSession#configChanged()
	 */
	@Override
	public void configChanged() {
		System.out.println("Config changed");
		for (SessionEventAware c : eventAwareComponents.keySet()) {
			c.configChanged();
		}
	}

	@Override
	public void addToSession(Object obj) {
		System.out.println("About to add to watch list: " + obj);
		if (obj instanceof SessionEventAware) {
			if (obj instanceof JDialog) {
				if (((JDialog) obj).isModal()) {
					return;
				}
			}
			System.out.println("Adding to watch list: " + obj);
			eventAwareComponents.put((SessionEventAware) obj, Boolean.TRUE);
		}
		if (obj instanceof DisposableView) {
			components.put((DisposableView) obj, Boolean.TRUE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nixexplorer.app.session.AppSession#remoteFileSystemWasChanged(java.lang.
	 * String)
	 */
	@Override
	public void remoteFileSystemWasChanged(String path) {
		System.out.println("remote file system was changed " + path);
		System.out.println(
				"Event aware components: " + eventAwareComponents.keySet());
		for (SessionEventAware c : eventAwareComponents.keySet()) {
			c.fileSystemUpdated(path);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.session.AppSession#close()
	 */
	@Override
	public void close() {
		try {
			Iterator<DisposableView> iterator = components.keySet().iterator();
			while (iterator.hasNext()) {
				DisposableView view = iterator.next();
				System.out.println("Closing: " + view);
				if (!view.closeView()) {
					System.out.println(view + " -closeview return false, stop");
					return;
				}
			}
			display.removeSelf();
		} finally {
			window.revalidate();
			window.repaint();
		}

	}

	public void createFolderView(String path) {
		display.createFolderView(path);
	}

	@Override
	public void unregisterSessionAwareComponent(SessionEventAware c) {
		try {
			eventAwareComponents.remove(c);
		} catch (Exception e) {
		}
	}

	/**
	 * @return the window
	 */
	public Window getWindow() {
		return window;
	}

	@Override
	public ChangeWatcher getChangeWatcher() {
		return this.service;
	}
}

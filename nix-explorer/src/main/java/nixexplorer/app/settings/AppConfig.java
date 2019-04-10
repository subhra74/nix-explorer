/**
 * 
 */
package nixexplorer.app.settings;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import nixexplorer.App;
import nixexplorer.Constants;
import nixexplorer.app.settings.snippet.SnippetItem;
import nixexplorer.widgets.folderview.ViewTogglePanel.ViewMode;
import nixexplorer.widgets.logviewer.LogHighlightEntry;

/**
 * @author subhro
 *
 */
public class AppConfig {
	public static final int OPEN_WITH_TEXT_EDITOR = 0, OPEN_IN_TERMINAL = 1,
			OPEN_WITH_EXTERNAL_EDITOR = 2, OPEN_WITH_SYS_DEF_APP = 3;

	private int windowWidth, windowHeight, windowState, x = -1, y = -1;

	private Terminal terminal;
	private FolderBrowser fileBrowser;
	private Editor editor;
	private LogViewer logViewer;
	private ProcessMonitor monitor;
	private boolean showBanner = true;

	/**
	 * 
	 */
	private AppConfig() {
		terminal = new Terminal();
		fileBrowser = new FolderBrowser();
		editor = new Editor();
		logViewer = new LogViewer();
		monitor = new ProcessMonitor();
	}

	public synchronized static AppConfig load() {
		File file = new File(App.getConfig("app.dir"),
				Constants.CONFIG_DB_FILE);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return objectMapper.readValue(file, new TypeReference<AppConfig>() {
			});
		} catch (IOException e) {
			e.printStackTrace();
			return new AppConfig();
		}
	}

	public void save() {
		File file = new File(App.getConfig("app.dir"),
				Constants.CONFIG_DB_FILE);
		System.out.println("saving config to: " + file);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			objectMapper.writeValue(file, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class Editor {
		private int fontSize = 14;
		private boolean wrap = false;

		/**
		 * @return the fontSize
		 */
		public int getFontSize() {
			return fontSize;
		}

		/**
		 * @param fontSize the fontSize to set
		 */
		public void setFontSize(int fontSize) {
			this.fontSize = fontSize;
		}

		/**
		 * @return the wrap
		 */
		public boolean isWrap() {
			return wrap;
		}

		/**
		 * @param wrap the wrap to set
		 */
		public void setWrap(boolean wrap) {
			this.wrap = wrap;
		}

	}

	public static class Terminal {
		private int fontSize = 14;
		private boolean x11CopyPaste;
		private int backGround = Color.BLACK.getRGB(),
				foreGround = Color.WHITE.getRGB();
		private List<SnippetItem> snippets = new ArrayList<SnippetItem>();

		/**
		 * @return the fontSize
		 */
		public int getFontSize() {
			return fontSize;
		}

		/**
		 * @param fontSize the fontSize to set
		 */
		public void setFontSize(int fontSize) {
			this.fontSize = fontSize;
		}

		/**
		 * @return the x11CopyPaste
		 */
		public boolean isX11CopyPaste() {
			return x11CopyPaste;
		}

		/**
		 * @param x11CopyPaste the x11CopyPaste to set
		 */
		public void setX11CopyPaste(boolean x11CopyPaste) {
			this.x11CopyPaste = x11CopyPaste;
		}

		/**
		 * @return the backGround
		 */
		public int getBackGround() {
			return backGround;
		}

		/**
		 * @param backGround the backGround to set
		 */
		public void setBackGround(int backGround) {
			this.backGround = backGround;
		}

		/**
		 * @return the foreGround
		 */
		public int getForeGround() {
			return foreGround;
		}

		/**
		 * @param foreGround the foreGround to set
		 */
		public void setForeGround(int foreGround) {
			this.foreGround = foreGround;
		}

		/**
		 * @return the snippets
		 */
		public List<SnippetItem> getSnippets() {
			return snippets;
		}

		/**
		 * @param snippets the snippets to set
		 */
		public void setSnippets(List<SnippetItem> snippets) {
			this.snippets = snippets;
		}
	}

	public static class FolderBrowser {

		private boolean folderCachingEnabled;
		private int dblClickAction;
		private int sidePanelViewMode;
		private boolean reloadFolderAfterOperation;
		private boolean sidePanelVisible;
		private boolean preferShellOverSftp;
		private boolean confirmBeforeDelete;
		private String externalEditor;
		private ViewMode viewMode = ViewMode.Details;

		private List<String> remoteBookmarks = new ArrayList<>();
		private List<String> localBookmarks = new ArrayList<>();

		/**
		 * @return the folderCachingEnabled
		 */
		public boolean isFolderCachingEnabled() {
			return folderCachingEnabled;
		}

		/**
		 * @param folderCachingEnabled the folderCachingEnabled to set
		 */
		public void setFolderCachingEnabled(boolean folderCachingEnabled) {
			this.folderCachingEnabled = folderCachingEnabled;
		}

		/**
		 * @return the dblClickAction
		 */
		public int getDblClickAction() {
			return dblClickAction;
		}

		/**
		 * @param dblClickAction the dblClickAction to set
		 */
		public void setDblClickAction(int dblClickAction) {
			this.dblClickAction = dblClickAction;
		}

		/**
		 * @return the reloadFolderAfterOperation
		 */
		public boolean isReloadFolderAfterOperation() {
			return reloadFolderAfterOperation;
		}

		/**
		 * @param reloadFolderAfterOperation the reloadFolderAfterOperation to
		 *                                   set
		 */
		public void setReloadFolderAfterOperation(
				boolean reloadFolderAfterOperation) {
			this.reloadFolderAfterOperation = reloadFolderAfterOperation;
		}

		/**
		 * @return the sidePanelVisible
		 */
		public boolean isSidePanelVisible() {
			return sidePanelVisible;
		}

		/**
		 * @param sidePanelVisible the sidePanelVisible to set
		 */
		public void setSidePanelVisible(boolean sidePanelVisible) {
			this.sidePanelVisible = sidePanelVisible;
		}

		/**
		 * @return the preferShellOverSftp
		 */
		public boolean isPreferShellOverSftp() {
			return preferShellOverSftp;
		}

		/**
		 * @param preferShellOverSftp the preferShellOverSftp to set
		 */
		public void setPreferShellOverSftp(boolean preferShellOverSftp) {
			this.preferShellOverSftp = preferShellOverSftp;
		}

		/**
		 * @return the confirmBeforeDelete
		 */
		public boolean isConfirmBeforeDelete() {
			return confirmBeforeDelete;
		}

		/**
		 * @param confirmBeforeDelete the confirmBeforeDelete to set
		 */
		public void setConfirmBeforeDelete(boolean confirmBeforeDelete) {
			this.confirmBeforeDelete = confirmBeforeDelete;
		}

		/**
		 * @return the sidePanelViewMode
		 */
		public int getSidePanelViewMode() {
			return sidePanelViewMode;
		}

		/**
		 * @param sidePanelViewMode the sidePanelViewMode to set
		 */
		public void setSidePanelViewMode(int sidePanelViewMode) {
			this.sidePanelViewMode = sidePanelViewMode;
		}

		/**
		 * @return the externalEditor
		 */
		public String getExternalEditor() {
			return externalEditor;
		}

		/**
		 * @param externalEditor the externalEditor to set
		 */
		public void setExternalEditor(String externalEditor) {
			this.externalEditor = externalEditor;
		}

		public synchronized List<String> getRemoteBookmarks() {
			return remoteBookmarks;
		}

		public synchronized List<String> getLocalBookmarks() {
			return localBookmarks;
		}

		/**
		 * @return the viewMode
		 */
		public ViewMode getViewMode() {
			return viewMode;
		}

		/**
		 * @param viewMode the viewMode to set
		 */
		public void setViewMode(ViewMode viewMode) {
			this.viewMode = viewMode;
		}
	}

	/**
	 * @return the windowWidth
	 */
	public int getWindowWidth() {
		return windowWidth;
	}

	/**
	 * @param windowWidth the windowWidth to set
	 */
	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}

	/**
	 * @return the windowHeight
	 */
	public int getWindowHeight() {
		return windowHeight;
	}

	/**
	 * @param windowHeight the windowHeight to set
	 */
	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}

	/**
	 * @return the windowState
	 */
	public int getWindowState() {
		return windowState;
	}

	/**
	 * @param windowState the windowState to set
	 */
	public void setWindowState(int windowState) {
		this.windowState = windowState;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the terminal
	 */
	public Terminal getTerminal() {
		return terminal;
	}

	/**
	 * @param terminal the terminal to set
	 */
	public void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}

	/**
	 * @return the fileBrowser
	 */
	public FolderBrowser getFileBrowser() {
		return fileBrowser;
	}

	/**
	 * @param fileBrowser the fileBrowser to set
	 */
	public void setFileBrowser(FolderBrowser fileBrowser) {
		this.fileBrowser = fileBrowser;
	}

	/**
	 * @return the editor
	 */
	public Editor getEditor() {
		return editor;
	}

	/**
	 * @param editor the editor to set
	 */
	public void setEditor(Editor editor) {
		this.editor = editor;
	}

	public static class LogViewer {
		private List<LogHighlightEntry> highlightList = new ArrayList<>();
		private int fontSize = 14;

		/**
		 * @return the highlightList
		 */
		public List<LogHighlightEntry> getHighlightList() {
			return highlightList;
		}

		/**
		 * @param highlightList the highlightList to set
		 */
		public void setHighlightList(List<LogHighlightEntry> highlightList) {
			this.highlightList = highlightList;
		}

		/**
		 * @return the fontSize
		 */
		public int getFontSize() {
			return fontSize;
		}

		/**
		 * @param fontSize the fontSize to set
		 */
		public void setFontSize(int fontSize) {
			this.fontSize = fontSize;
		}
	}

	/**
	 * @return the logViewer
	 */
	public LogViewer getLogViewer() {
		return logViewer;
	}

	/**
	 * @param logViewer the logViewer to set
	 */
	public void setLogViewer(LogViewer logViewer) {
		this.logViewer = logViewer;
	}

	public static class ProcessMonitor {
		private int interval = 5;

		/**
		 * @return the interval
		 */
		public int getInterval() {
			return interval;
		}

		/**
		 * @param interval the interval to set
		 */
		public void setInterval(int interval) {
			this.interval = interval;
		}
	}

	/**
	 * @return the monitor
	 */
	public ProcessMonitor getMonitor() {
		return monitor;
	}

	/**
	 * @param monitor the monitor to set
	 */
	public void setMonitor(ProcessMonitor monitor) {
		this.monitor = monitor;
	}

	/**
	 * @return the showBanner
	 */
	public boolean isShowBanner() {
		return showBanner;
	}

	/**
	 * @param showBanner the showBanner to set
	 */
	public void setShowBanner(boolean showBanner) {
		this.showBanner = showBanner;
	}
}

package nixexplorer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.jcraft.jsch.ChannelExec;

import nixexplorer.app.MainAppFrame;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.drawables.icons.EmptyIcon;
import nixexplorer.drawables.icons.ScaledIcon;
import nixexplorer.registry.PluginEntry;
import nixexplorer.registry.PluginRegistry;
import nixexplorer.registry.PluginShortcutEntry;
import nixexplorer.registry.PluginShortcutRegistry;
import nixexplorer.registry.contextmenu.ContextMenuEntry;
import nixexplorer.registry.contextmenu.ContextMenuRegistry;
import nixexplorer.skin.FlatButtonUI;
import nixexplorer.skin.FlatCheckBoxUI;
import nixexplorer.skin.FlatComboBoxUI;
import nixexplorer.skin.FlatInternalFrameUI;
import nixexplorer.skin.FlatLabelUI;
import nixexplorer.skin.FlatMenuItemUI;
import nixexplorer.skin.FlatProgressBarUI;
import nixexplorer.skin.FlatRadioButtonUI;
import nixexplorer.skin.FlatScrollbarUI;
import nixexplorer.skin.FlatSpinnerUI;
import nixexplorer.skin.FlatTabbedPaneUI;
import nixexplorer.skin.FlatTableHeaderUI;
import nixexplorer.skin.FlatTextFieldUI;
import nixexplorer.skin.FlatTreeUI;
import nixexplorer.skin.SeparatorUI;
import nixexplorer.skin.StatefullIcon;
import nixexplorer.widgets.component.FlatButtonBorder;
import nixexplorer.widgets.util.Utility;

/**
 * Hello world!
 *
 */
public final class App {

	private static String theme;

	private static Properties config = new Properties();

	public static String getConfig(String key) {
		return config.getProperty(key);
	}

	private App() {

	}

	public static void main(String[] args)
			throws Exception, URISyntaxException {

//		SessionInfo info = new SessionInfo();
//		info.setUser("subhro");
//		info.setPassword("Starscream@64");
//		info.setHost("192.168.56.101");
//		SshWrapper wr = new SshWrapper(info);
//		wr.connect();
//		ChannelExec exec = wr.getExecChannel();
//		exec.setPty(true);
//		exec.setInputStream(System.in);
//		exec.setOutputStream(System.out);
//		exec.setCommand("echo $PATH");
//		exec.connect();
//		while(!exec.isClosed()) {
//			Thread.sleep(1000);
//		}
//		System.out.println(KeyStroke.getKeyStroke(KeyEvent.VK_C,
//				InputEvent.CTRL_DOWN_MASK));

//		System.out.println(
//				"color: " + Color.BLACK.getRGB() + " " + Color.WHITE.getRGB());

		System.setProperty("awt.useSystemAAFontSettings", "on");
		System.setProperty("swing.aatext", "true");

		Security.addProvider(new BouncyCastleProvider());

		try {
			UIManager.setLookAndFeel(new MetalLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		config.put("temp.dir",
				PathUtils.combine(System.getProperty("user.home"),
						"nix-explorer" + File.separator + "temp",
						File.separator));

		config.put("app.dir", PathUtils.combine(System.getProperty("user.home"),
				"nix-explorer", File.separator));

		new File(config.get("app.dir").toString()).mkdirs();
		new File(config.get("temp.dir").toString()).mkdirs();

		UIManager.put("TabbedPaneUI", FlatTabbedPaneUI.class.getName());
		UIManager.put("ProgressBarUI", FlatProgressBarUI.class.getName());
		UIManager.put("LabelUI", FlatLabelUI.class.getName());
		UIManager.put("ButtonUI", FlatButtonUI.class.getName());
		UIManager.put("ScrollBarUI", FlatScrollbarUI.class.getName());
		UIManager.put("ComboBoxUI", FlatComboBoxUI.class.getName());
		UIManager.put("CheckBoxUI", FlatCheckBoxUI.class.getName());
		UIManager.put("InternalFrameUI", FlatInternalFrameUI.class.getName());
		UIManager.put("MenuItemUI", FlatMenuItemUI.class.getName());
		UIManager.put("TreeUI", FlatTreeUI.class.getName());
		UIManager.put("PopupMenuSeparatorUI", SeparatorUI.class.getName());
		UIManager.put("SpinnerUI", FlatSpinnerUI.class.getName());
		UIManager.put("TextFieldUI", FlatTextFieldUI.class.getName());
		UIManager.put("SplitPaneUI", BasicSplitPaneUI.class.getName());
		UIManager.put("RadioButtonUI", FlatRadioButtonUI.class.getName());
		UIManager.put("TableHeaderUI", FlatTableHeaderUI.class.getName());

		System.out.println(UIManager.get("TabbedPaneUI"));

		// loadDarkTheme();

		loadLightTheme();

//		nixexplorer.core.ssh.filetransfer.SshConnectionPool.getSharedInstance();

		loadStrings();
		registerPlugins();

		createAndShowWindow();
	}

	/**
	 * 
	 */
	private static void loadStrings() {
		TextHolder.addString("common.ok", "OK");
		TextHolder.addString("common.cancel", "Cancel");

		TextHolder.addString("messagebox.ok", "OK");

		TextHolder.addString("host.name", "Host");
		TextHolder.addString("host.port", "Port");
		TextHolder.addString("host.user", "User");
		TextHolder.addString("host.pass", "Password");
		TextHolder.addString("host.localdir", "Local folder");
		TextHolder.addString("host.remotedir", "Remote folder");
		TextHolder.addString("host.keyfile", "Private key file");
		TextHolder.addString("host.browse", "Browse..");
		TextHolder.addString("sessionTab.viewDesktop", "Desktop view");
		TextHolder.addString("sessionTab.viewTabbed", "Tabbed view");
		TextHolder.addString("sessionTab.viewTiled", "Tiled view");
		TextHolder.addString("sessionTab.splitVertically", "Split vertically");
		TextHolder.addString("sessionTab.splitHorizontally",
				"Split Horizontally");
		TextHolder.addString("toolbar.localFileBrowser", "Local file browser");
		TextHolder.addString("toolbar.remoteFileBrowser",
				"Remote file browser");
		TextHolder.addString("toolbar.terminal", "Terminal");
		TextHolder.addString("toolbar.sysmon", "System monitor");
		TextHolder.addString("toolbar.transfers", "Idle");
		TextHolder.addString("toolbar.workspace", "Workspace");
		TextHolder.addString("message.NoHost",
				"Host name can not be left blank");
		TextHolder.addString("message.NoUser",
				"User name can not be left blank");
		TextHolder.addString("sessionTree.defaultText", "My sites");
		TextHolder.addString("sessionTree.defaultFolderText", "New folder");
		TextHolder.addString("session.newHost", "New site");
		TextHolder.addString("session.newFolder", "New folder");
		TextHolder.addString("session.remove", "Remove");
		TextHolder.addString("session.duplicate", "Duplicate");
		TextHolder.addString("session.connect", "Connect");
		TextHolder.addString("session.save", "Save");
		TextHolder.addString("session.cancel", "Close");
		TextHolder.addString("session.name", "Name");

		TextHolder.addString("transfers.pause", "Pause");
		TextHolder.addString("transfers.resume", "Resume");
		TextHolder.addString("transfers.remove", "Remove");
		TextHolder.addString("transfers.transferring", "Transferring");
		TextHolder.addString("transfers.stopped", "Stopped");
		TextHolder.addString("transfers.failed", "Failed");
		TextHolder.addString("transfers.finished", "Finished");

		TextHolder.addString("folderview.localtitle", "Local file browser");
		TextHolder.addString("folderview.sftptitle", "SFTP file browser");
		TextHolder.addString("folderview.opennewtab", "Open in new tab");
		TextHolder.addString("folderview.copyPath", "Copy path");
		TextHolder.addString("folderview.openterm", "Open in terminal");
		TextHolder.addString("folderview.createLink", "Create Link");
		TextHolder.addString("folderview.hardLink", "Hard link");
		TextHolder.addString("folderview.linkName", "Link name");
		TextHolder.addString("folderview.fileName", "File name");
		TextHolder.addString("folderview.run1", "Run in terminal");
		TextHolder.addString("folderview.run2", "Run script...");
		TextHolder.addString("folderview.normal", "Execute normally");
		TextHolder.addString("folderview.nohup", "Execute with nohup");
		TextHolder.addString("folderview.background", "Execute in backgrond");
		TextHolder.addString("folderview.command", "Arguments");
		TextHolder.addString("folderview.runoption", "Run options");
		TextHolder.addString("folderview.background", "Execute in backgrond");
		TextHolder.addString("folderview.logview", "Open with LogViewer");
		TextHolder.addString("folderview.upload", "Upload files here");
		TextHolder.addString("folderview.download", "Download files");
		TextHolder.addString("folderview.openDefault", "Default application");
		TextHolder.addString("folderview.openIntern", "Internal editor");
		TextHolder.addString("folderview.openCust", "External editor");
		TextHolder.addString("folderview.openLogView", "Log viewer");
		TextHolder.addString("folderview.openWith", "Open with");
		TextHolder.addString("folderview.open", "Open");
		TextHolder.addString("folderview.showHidden", "Show hidden files");
		TextHolder.addString("folderview.selectAll", "Select All");
		TextHolder.addString("folderview.clearSelection", "Clear selection");
		TextHolder.addString("folderview.inverseSelection",
				"Inverse selection");
		TextHolder.addString("folderview.selectByPattern", "Select by pattern");
		TextHolder.addString("folderview.unselectByPattern",
				"Unselect by pattern");
		TextHolder.addString("folderview.selectSimilar",
				"Select similar file types");
		TextHolder.addString("folderview.unSelectSimilar",
				"Unselect similar file types");
		TextHolder.addString("folderview.filter", "Filter files");

		TextHolder.addString("sysmon.processTitle", "Processes");
		TextHolder.addString("sysmon.pollInterval", "Refresh interval");
		TextHolder.addString("sysmon.refresh", "Refresh");
		TextHolder.addString("sysmon.sysinfo", "System information");
		TextHolder.addString("sysmon.socketTitle", "Process and ports");
		TextHolder.addString("sysmon.loadTitle", "System load");
		TextHolder.addString("sysmon.diskTitle", "Diskspace usage");
		TextHolder.addString("sysmon.title", "System Monitor");
		TextHolder.addString("sysmon.clear", "Clear");
		TextHolder.addString("sysmon.filterTxt", "Filter");
		TextHolder.addString("sysmon.searchTxt",
				"Show entries containing search text");
		TextHolder.addString("sysmon.processFilter", "Filter process");
		TextHolder.addString("sysmon.processFilterApply", "Apply");
		TextHolder.addString("sysmon.processFilterClear", "Clear");
		TextHolder.addString("sysmon.killText", "Kill process");
		TextHolder.addString("sysmon.sigText", "Send signal");
		TextHolder.addString("sysmon.niceText", "Change priority");
		TextHolder.addString("sysmon.showAll", "Show processes from all users");

		TextHolder.addString("folderview.selectFolder", "Select Folder");
		TextHolder.addString("folderview.selectFile", "Select File");
		TextHolder.addString("folderview.genericError", "Operation failed");
		TextHolder.addString("folderview.select", "Select");
		TextHolder.addString("folderview.save", "Save");
		TextHolder.addString("folderview.cancel", "Cancel");
		TextHolder.addString("folderview.selected", "Selected");
		TextHolder.addString("folderview.reload", "Reload");
		TextHolder.addString("folderview.rename", "Rename");
		TextHolder.addString("folderview.delete", "Delete");
		TextHolder.addString("folderview.move", "Move to");
		TextHolder.addString("folderview.input", "Input");
		TextHolder.addString("folderview.newFile", "New file");
		TextHolder.addString("folderview.newFolder", "New folder");
		TextHolder.addString("folderview.renameTitle", "Rename file to");
		TextHolder.addString("folderview.sortByName", "Name");
		TextHolder.addString("folderview.sortBySize", "Size");
		TextHolder.addString("folderview.sortByType", "Type");
		TextHolder.addString("folderview.sortByModified", "Modified");
		TextHolder.addString("folderview.sortByPerm", "Permission");
		TextHolder.addString("folderview.sortAsc", "Ascending");
		TextHolder.addString("folderview.sortDesc", "Descending");
		TextHolder.addString("folderview.sortBy", "Sort by");
		TextHolder.addString("folderview.renameFailed",
				"Failed to rename file/folder");
		TextHolder.addString("folderview.copy", "Copy");
		TextHolder.addString("folderview.paste", "Paste");
		TextHolder.addString("folderview.cut", "Cut");
		TextHolder.addString("folderview.bookmark", "Add to favourites");
		TextHolder.addString("folderview.editExternal",
				"Open with external editor");
		TextHolder.addString("folderview.openExternal",
				"Open with default app");
		TextHolder.addString("folderview.props", "Properties");
		TextHolder.addString("folderview.upload", "Upload files here");
		TextHolder.addString("folderview.download", "Download selected files");

		TextHolder.addString("archiver.unknownformat",
				"Format is not supported");
		TextHolder.addString("archiver.title", "Archiver");
		TextHolder.addString("archiver.stop", "Stop");
		TextHolder.addString("archiver.close", "Close");
		TextHolder.addString("archiver.exitcode", "Exit code: ");
		TextHolder.addString("archiver.error", "Error extracting file");
		TextHolder.addString("archiver.filename", "Archive name");
		TextHolder.addString("archiver.savein", "Save in");
		TextHolder.addString("archiver.browse", "Browse..");
		TextHolder.addString("archiver.format", "Format");
		TextHolder.addString("archiver.ok", "OK");
		TextHolder.addString("archiver.compressing", "Compressing...");
		TextHolder.addString("archiver.compress", "Compress");
		TextHolder.addString("archiver.extract", "Extract");
		TextHolder.addString("archiver.extractto", "Extract to");
		TextHolder.addString("archiver.extracthere", "Extract here");
		TextHolder.addString("archiver.preview", "View archive");
		TextHolder.addString("archiver.addext",
				"Automatically add extension: %s");
		TextHolder.addString("archiver.search", "Search");
		TextHolder.addString("archiver.open", "Open");
		TextHolder.addString("archiver.cancel", "Cancel");
		TextHolder.addString("archiver.extractto", "Extract to folder");

		TextHolder.addString("duplicate.overwrite", "Replace existing");
		TextHolder.addString("duplicate.skip", "Skip");
		TextHolder.addString("duplicate.rename", "Auto rename");
		TextHolder.addString("duplicate.cancel", "Cancel");
		TextHolder.addString("duplicate.apply",
				"Apply same action for other conflicts");
		TextHolder.addString("duplicate.prompt",
				"Below file already exists on target location\n%s\nPlease select an action");
		TextHolder.addString("duplicate.confirm", "Confirm");

		TextHolder.addString("editor.save", "Save");
		TextHolder.addString("editor.open", "Open");
		TextHolder.addString("editor.find", "Find");
		TextHolder.addString("editor.replace", "Replace");
		TextHolder.addString("editor.gotoline", "Go to Line");
		TextHolder.addString("editor.reload", "Reload");
		TextHolder.addString("editor.cutText", "Cut");
		TextHolder.addString("editor.pasteText", "Paste");
		TextHolder.addString("editor.copyText", "Copy");
		TextHolder.addString("editor.file", "File");
		TextHolder.addString("editor.edit", "Edit");
		TextHolder.addString("editor.options", "Options");
		TextHolder.addString("editor.help", "Help");
		TextHolder.addString("editor.new", "New");
		TextHolder.addString("editor.open", "Open");
		TextHolder.addString("editor.save", "Save");
		TextHolder.addString("editor.saveAs", "Save As..");
		TextHolder.addString("editor.exit", "Exit");
		TextHolder.addString("editor.undo", "Undo");
		TextHolder.addString("editor.redo", "Redo");
		TextHolder.addString("editor.cut", "Cut");
		TextHolder.addString("editor.copy", "Copy");
		TextHolder.addString("editor.paste", "Paste");
		TextHolder.addString("editor.findReplace", "Find/Replace");
		TextHolder.addString("editor.settings", "Settings");
		TextHolder.addString("editor.support", "Contents");
		TextHolder.addString("editor.about", "About");
		TextHolder.addString("editor.fontSize", "Font size");
		TextHolder.addString("editor.wrapText", "Wrap text");
		TextHolder.addString("editor.syntax", "Syntax");
		TextHolder.addString("editor.theme", "Theme");

		TextHolder.addString("searchbox.search", "Find");
		TextHolder.addString("searchbox.replace", "Replace");
		TextHolder.addString("searchbox.replaceAll", "Replace all");
		TextHolder.addString("searchbox.ignoreCase", "Ignore case");
		TextHolder.addString("searchbox.regex", "Regular expression");
		TextHolder.addString("searchbox.reverse", "Reverse");
		TextHolder.addString("searchbox.wholeWord", "Whole word");
		TextHolder.addString("searchbox.close", "Close");

		TextHolder.addString("logviewer.all", "Full content");
		TextHolder.addString("logviewer.autoupdate", "Auto update");
		TextHolder.addString("logviewer.partial", "Show last");
		TextHolder.addString("logviewer.kb", "KB only");
		TextHolder.addString("logviewer.reload", "Reload");
		TextHolder.addString("logviewer.search", "Search");
		TextHolder.addString("logviewer.searchnext", "Find next");
		TextHolder.addString("logviewer.searchprev", "Find prev");
		TextHolder.addString("logviewer.onlymatched", "Show only matched");
		TextHolder.addString("logviewer.clearsearch", "Clear");
		TextHolder.addString("logviewer.openwithtitle", "Open with Log Viewer");
		TextHolder.addString("logviewer.title", "Log Viewer");
		TextHolder.addString("logviewer.pageCount", " / %d");
		TextHolder.addString("logviewer.liveMode", "Live mode");
		TextHolder.addString("logviewer.matchCase", "Match case");
		TextHolder.addString("logviewer.wholeWord", "Whole word");

		TextHolder.addString("logview.highlight.add", "Add");
		TextHolder.addString("logview.highlight.edit", "Edit");
		TextHolder.addString("logview.highlight.del", "Delete");
		TextHolder.addString("logview.highlight.description", "Description");
		TextHolder.addString("logview.highlight.pattern", "Pattern");
		TextHolder.addString("logview.highlight.color", "Highlight color");
		TextHolder.addString("logview.highlight.newHighlight",
				"New pattern highlight");
		TextHolder.addString("logview.highlight.blankField",
				"Description or pattern can not be left blank");
		TextHolder.addString("logview.highlight.pattern", "Pattern");
		TextHolder.addString("logview.highlight.title",
				"Logviewer pattern highlight");

		TextHolder.addString("filesearch.search", "Search");
		TextHolder.addString("filesearch.searchItemCount", "%d items");
		TextHolder.addString("filesearch.searchfor", "Search for");
		TextHolder.addString("filesearch.name", "In filename");
		TextHolder.addString("filesearch.filename", "Name");
		TextHolder.addString("filesearch.content", "In file content");
		TextHolder.addString("filesearch.compress",
				"Look inside compressed files");
		TextHolder.addString("filesearch.contains", "Name");
		TextHolder.addString("filesearch.folder", "Search in");
		TextHolder.addString("filesearch.size", "Size");
		TextHolder.addString("filesearch.eq", "Equal to");
		TextHolder.addString("filesearch.lt", "Less than");
		TextHolder.addString("filesearch.gt", "More than");
		TextHolder.addString("filesearch.mtime", "Modified");
		TextHolder.addString("filesearch.mtime1", "Any time");
		TextHolder.addString("filesearch.mtime2", "Today");
		TextHolder.addString("filesearch.mtime3", "This week");
		TextHolder.addString("filesearch.mtime4", "Between");
		TextHolder.addString("filesearch.from", "From");
		TextHolder.addString("filesearch.to", "To");
		TextHolder.addString("filesearch.find", "Find");
		TextHolder.addString("filesearch.idle", "Idle");
		TextHolder.addString("filesearch.searching", "Searching...");
		TextHolder.addString("filesearch.type", "Type");
		TextHolder.addString("filesearch.size", "Size");
		TextHolder.addString("filesearch.modified", "Modified");
		TextHolder.addString("filesearch.permission", "Permissions");
		TextHolder.addString("filesearch.links", "Link count");
		TextHolder.addString("filesearch.user", "User");
		TextHolder.addString("filesearch.group", "Group");
		TextHolder.addString("filesearch.filepath", "Path");
		TextHolder.addString("filesearch.file", "File");
		TextHolder.addString("filesearch.folder", "Folder");
		TextHolder.addString("filesearch.lookfor", "Look for");
		TextHolder.addString("filesearch.both", "Both file and folder");
		TextHolder.addString("filesearch.fileonly", "File only");
		TextHolder.addString("filesearch.folderonly", "Folder only");
		TextHolder.addString("filesearch.title", "File search");
		TextHolder.addString("filesearch.showInBrowser", "Show location");
		TextHolder.addString("filesearch.deletingLabel",
				"Deleting files, please wait...");
		TextHolder.addString("filesearch.deletingTitle", "Please wait...");
		TextHolder.addString("filesearch.delete", "Delete");
		TextHolder.addString("filesearch.download", "Download");

		TextHolder.addString("waiting.title", "Operation in progress");
		TextHolder.addString("waiting.message",
				"Operation in progress, please wait...");

		TextHolder.addString("downloader.urls", "Urls to download");
		TextHolder.addString("downloader.folder", "Download folder");
		TextHolder.addString("downloader.proxylabel", "Proxy configuration");
		TextHolder.addString("downloader.go", "Download");
		TextHolder.addString("downloader.title", "Download");
		TextHolder.addString("downloader.app", "Application");
		TextHolder.addString("downloader.httpdownload", "HTTP Download");
		TextHolder.addString("downloader.httpupload", "HTTP Upload");
		TextHolder.addString("downloader.sftpdownload", "SFTP Download");
		TextHolder.addString("downloader.sftpupload", "SFTP Upload");
		TextHolder.addString("downloader.ftpdownload", "FTP Download");
		TextHolder.addString("downloader.ftpupload", "FTP Upload");

		TextHolder.addString("uploader.url", "Upload url");
		TextHolder.addString("uploader.files", "Files to upload");
		TextHolder.addString("uploader.proxylabel", "Proxy configuration");
		TextHolder.addString("uploader.go", "Upload");
		TextHolder.addString("uploader.title", "Upload");
		TextHolder.addString("uploader.app", "Application");

		TextHolder.addString("filetransfer.title", "Remote to remote SCP");
		TextHolder.addString("filetransfer.back", "Back");
		TextHolder.addString("filetransfer.sendto", "Send files with SCP");

		TextHolder.addString("appmenu.connect", "Connections");
		TextHolder.addString("appmenu.settings", "Settings");
		TextHolder.addString("appmenu.save", "Save session");
		TextHolder.addString("appmenu.load", "Load session");
		TextHolder.addString("appmenu.delete", "Delete session");
		TextHolder.addString("appmenu.help", "Help");
		TextHolder.addString("desktop.start", "Start");

		TextHolder.addString("runas.title", "Run as");
		TextHolder.addString("runas.run", "Run");
		TextHolder.addString("runas.cancel", "Cancel");
		TextHolder.addString("runas.cmd", "Command");
		TextHolder.addString("runas.args", "Arguments");

		TextHolder.addString("duplicate.prompt",
				"Some files already exists, please select an action");
		TextHolder.addString("duplicate.autorename", "Autorename");
		TextHolder.addString("duplicate.overwrite", "Overwite");
		TextHolder.addString("duplicate.skip", "Skip");
		TextHolder.addString("duplicate.ok", "OK");
		TextHolder.addString("duplicate.cancel", "Cancel");
		TextHolder.addString("duplicate.failed",
				"An error occured while copying files, do you want to retry?");

		TextHolder.addString("edit.default", "Open with text editor");
		TextHolder.addString("edit.extern", "Open with system default app");

		TextHolder.addString("filebrowser.selected", "Path");

		TextHolder.addString("workspace.home", "Home");

		TextHolder.addString("ftp.title", "Ftp browser");

		TextHolder.addString("curl.paramName", "Name");
		TextHolder.addString("curl.paramValue", "Value");
		TextHolder.addString("curl.url", "Url");
		TextHolder.addString("curl.param", "Parameters");
		TextHolder.addString("curl.add", "Add");
		TextHolder.addString("curl.del", "Delete");
		TextHolder.addString("curl.exec", "Execute");
		TextHolder.addString("curl.stop", "Stop");
		TextHolder.addString("curl.result", "Results");
		TextHolder.addString("curl.back", "Back");
		TextHolder.addString("curl.title", "cURL");

		TextHolder.addString("http.title", "Http client");
		TextHolder.addString("appmenu.multiTerm",
				"Run command on multiple servers");

		TextHolder.addString("elevated.title",
				"Perform operation as super user");
		TextHolder.addString("elevated.details",
				"Operation failed. Would you like to perform the operation as super user?");
		TextHolder.addString("elevated.prompt", "Elevation command to use");
		TextHolder.addString("elevated.ok", "Permform action");
		TextHolder.addString("elevated.cancel", "Cancel");

		TextHolder.addString("app.title", "Nix Explorer");
		TextHolder.addString("app.connections", "Connected servers");
		TextHolder.addString("app.control.files", "File browser");
		TextHolder.addString("app.control.terminal", "Terminal");
		TextHolder.addString("app.control.editor", "Text Editor");
		TextHolder.addString("app.control.logviewer", "Log Viewer");
		TextHolder.addString("app.control.taskmgr", "Task Manager");
		TextHolder.addString("app.control.fileshare", "FXP / SCP");
		TextHolder.addString("app.control.curl", "cURL GUI");
		TextHolder.addString("app.control.search", "Find Files");
		TextHolder.addString("app.local.title", "Local Files");
		TextHolder.addString("app.files.title", "Files");
		TextHolder.addString("app.control.disconnect", "Disconnect");

		TextHolder.addString("app.auth.title", "Authorization");
		TextHolder.addString("app.auth.user", "User Name");
		TextHolder.addString("app.auth.pass", "Password");

		TextHolder.addString("app.control.settings", "Settings");

		TextHolder.addString("config.title.folderView", "File browser");
		TextHolder.addString("config.title.terminal", "Terminal");

		TextHolder.addString("config.folderview.caching", "Cache folder");
		TextHolder.addString("config.folderview.browse", "Browse");
		TextHolder.addString("config.folderview.externalEditor",
				"External editor");
		TextHolder.addString("config.folderview.autoReload",
				"Reload folder after operation");
		TextHolder.addString("config.folderview.sidePane", "Show side panel");
		TextHolder.addString("config.folderview.preferShell",
				"Prefer shell over sftp operation");
		TextHolder.addString("config.folderview.delete",
				"Confirm before delete");

		TextHolder.addString("config.folderview.dblClickText",
				"Double click action");

		TextHolder.addString("config.folderview.viewMode", "Sidebar view mode");
		TextHolder.addString("config.folderview.view", "View mode");

		TextHolder.addString("config.folderview.ListView", "List");
		TextHolder.addString("config.folderview.DetailsView", "Details");

		TextHolder.addString("config.folderview.openInTerminal",
				"Open in Terminal");
		TextHolder.addString("config.folderview.openWithExternalEditor",
				"Open with External Editor");
		TextHolder.addString("config.folderview.openWithSystemDefaultApp",
				"Open with System Default App");
		TextHolder.addString("config.folderview.openWithTextEditor",
				"Open with Text Editor");

		TextHolder.addString("config.folderview.treeView", "Tree view");
		TextHolder.addString("config.folderview.listView", "List view");

		TextHolder.addString("config.terminal.select", "Select color");
		TextHolder.addString("config.terminal.foregroundColor", "Text color");
		TextHolder.addString("config.terminal.backgroundColor",
				"Background color");
		TextHolder.addString("config.terminal.fontSize", "Font size");
		TextHolder.addString("config.terminal.x11CopyPaste", "X11 copy paste");
		TextHolder.addString("config.button.save", "Save");
		TextHolder.addString("config.button.cancel", "Cancel");

		TextHolder.addString("snippet.title", "Command snippets");
		TextHolder.addString("snippet.add", "New");
		TextHolder.addString("snippet.delete", "Delete");
		TextHolder.addString("snippet.edit", "Edit");
		TextHolder.addString("snippet.alt", "ALT");
		TextHolder.addString("snippet.shift", "SHIFT");
		TextHolder.addString("snippet.ctrl", "CTRL");
		TextHolder.addString("snippet.new", "New snippet");
		TextHolder.addString("snippet.chars", "Key combination");
		TextHolder.addString("snippet.missingName",
				"Name is missing,\nplease enter an valid name for snippet");
		TextHolder.addString("snippet.missingCommand",
				"Command is missing,\nplease enter an valid command for snippet");
		TextHolder.addString("snippet.noCharSelected",
				"No key combination is selected");
		TextHolder.addString("snippet.command", "Command to execute");
		TextHolder.addString("snippet.name", "Snippet name");
		TextHolder.addString("snippet.key", "Key combination");

		TextHolder.addString("terminal.snippet", "Command shortcuts");
		TextHolder.addString("terminal.manageSnippets", "Manage");
		TextHolder.addString("terminal.reconnect", "Reconnect");
		TextHolder.addString("terminal.reconnectText",
				"Connection interrupted");

		TextHolder.addString("diskUsageViewer.title", "Disk Usage");
		TextHolder.addString("diskUsageViewer.fileName", "Directory name");
		TextHolder.addString("diskUsageViewer.fileSize", "Size");
		TextHolder.addString("diskUsageViewer.filePath", "Path");
		TextHolder.addString("diskUsageViewer.usage", "Usage");

		TextHolder.addString("diskUsageViewer.targetLabel", "Directory");
		TextHolder.addString("diskUsageViewer.go", "Analyze");

		TextHolder.addString("viewMode.listViewText", "List view");
		TextHolder.addString("viewMode.detailsViewText", "Details view");
	}

	private static void registerPlugins() {

		PluginShortcutRegistry.getList().add(new PluginShortcutEntry(
				"nixexplorer.widgets.folderview.local.LocalFolderViewWidget",
				new String[] {}, "Local files"));

		PluginShortcutRegistry.getList().add(new PluginShortcutEntry(
				"nixexplorer.widgets.folderview.remote.RemoteFolderViewWidget",
				new String[] {}, "Server files"));

		// load this from some file
		try {
			PluginRegistry.getSharedInstance().getPluginList()
					.add(new PluginEntry(
							"nixexplorer.widgets.folderview.local.LocalFolderViewWidget",
							new ArrayList<>(),
							TextHolder.getString("toolbar.localFileBrowser"),
							new ScaledIcon(
									App.class.getResource("/images/local.png"),
									Utility.toPixel(48), Utility.toPixel(48)),
							new ScaledIcon(
									App.class.getResource("/images/local.png"),
									Utility.toPixel(20), Utility.toPixel(20)),
							new String[] {}, true));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			PluginRegistry.getSharedInstance().getPluginList()
					.add(new PluginEntry(
							"nixexplorer.widgets.folderview.remote.RemoteFolderViewWidget",
							new ArrayList<>(),
							TextHolder.getString("toolbar.remoteFileBrowser"),
							new ScaledIcon(
									App.class.getResource(
											"/images/remote_folder.png"),
									Utility.toPixel(48), Utility.toPixel(48)),
							new ScaledIcon(
									App.class.getResource(
											"/images/remote_folder.png"),
									Utility.toPixel(20), Utility.toPixel(20)),
							new String[] {}, true));
		} catch (Exception e) {
			e.printStackTrace();
		}

//		try {
//			PluginRegistry.getSharedInstance().getPluginList()
//					.add(new PluginEntry(
//							"nixexplorer.widgets.editor.FormattedEditorWidget",
//							Arrays.asList(ContextMenuEntry.build(
//									TextHolder.getString("edit.default"),
//									"nixexplorer.widgets.editor.FormattedEditorWidget",
//									new String[] { "%f" }, new String[] { "" },
//									"sftp", false, false, false)),
//							TextHolder.getString("edit.default"),
//							new ScaledIcon(
//									App.class.getResource(
//											"/images/editor128.png"),
//									Utility.toPixel(48), Utility.toPixel(48)),
//							new ScaledIcon(
//									App.class.getResource(
//											"/images/editor128.png"),
//									Utility.toPixel(20), Utility.toPixel(20)),
//							new String[] { "" }, true));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		try {
//			PluginRegistry.getSharedInstance().getPluginList()
//					.add(new PluginEntry(
//							"nixexplorer.widgets.logviewer.LogViewerWidget",
//							Arrays.asList(ContextMenuEntry.build(
//									TextHolder.getString(
//											"logviewer.openwithtitle"),
//									"nixexplorer.widgets.logviewer.LogViewerWidget",
//									new String[] { "%f" }, new String[] { "" },
//									"sftp", false, false, false)),
//							TextHolder.getString("logviewer.title"),
//							new ScaledIcon(
//									App.class.getResource("/images/log.png"),
//									Utility.toPixel(48), Utility.toPixel(48)),
//							new ScaledIcon(
//									App.class.getResource("/images/log.png"),
//									Utility.toPixel(20), Utility.toPixel(20)),
//							new String[] { "" }, true));
////							new PluginLauncherEntry(
////									TextHolder.getString("logviewer.title"),
////									"nixexplorer.widgets.logviewer.LogViewerWidget",
////									new String[] { "" },
////									new ScaledIcon(
////											App.class.getResource(
////													"/images/log.png"),
////											Utility.toPixel(48),
////											Utility.toPixel(48)),
////									new ScaledIcon(
////											App.class.getResource(
////													"/images/log.png"),
////											Utility.toPixel(20),
////											Utility.toPixel(20))),
////							TextHolder.getString("logviewer.title")));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		try {
//			PluginRegistry.getSharedInstance().getPluginList()
//					.add(new PluginEntry(
//							"nixexplorer.widgets.editor.ExternalEditorWidget",
//							Arrays.asList(ContextMenuEntry.build(
//									TextHolder.getString("edit.extern"),
//									"nixexplorer.widgets.editor.ExternalEditorWidget",
//									new String[] { "-e", "%f" },
//									new String[] { "" }, "sftp", false, false,
//									false)),
//							TextHolder.getString("edit.extern"),
//							new ScaledIcon(
//									App.class.getResource(
//											"/images/file-transfer.png"),
//									Utility.toPixel(48), Utility.toPixel(48)),
//							new ScaledIcon(
//									App.class.getResource(
//											"/images/file-transfer.png"),
//									Utility.toPixel(20), Utility.toPixel(20)),
//							null, false));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		try {
			PluginRegistry.getSharedInstance().getPluginList()
					.add(new PluginEntry(
							"nixexplorer.widgets.console.TabbedConsoleWidget",
							Arrays.asList(ContextMenuEntry.build(
									TextHolder.getString("folderview.openterm"),
									"nixexplorer.widgets.console.TabbedConsoleWidget",
									new String[] { "-o", "%f" },
									new String[] {}, "sftp", true, false,
									false),
									ContextMenuEntry.build(
											TextHolder.getString(
													"folderview.openterm"),
											"nixexplorer.widgets.console.TabbedConsoleWidget",
											new String[] { "-o", "%d" },
											new String[] {}, "sftp", false,
											false, true),
									ContextMenuEntry.build(
											TextHolder.getString(
													"folderview.run1"),
											"nixexplorer.widgets.console.TabbedConsoleWidget",
											new String[] { "-r", "%f" },
											new String[] { "" }, "sftp", false,
											false, false)),

							TextHolder.getString("folderview.openterm"),
							new ScaledIcon(
									App.class.getResource(
											"/images/Terminalicon2.png"),
									Utility.toPixel(48), Utility.toPixel(48)),
							new ScaledIcon(
									App.class.getResource(
											"/images/Terminalicon2.png"),
									Utility.toPixel(20), Utility.toPixel(20)),
							new String[] { "" }, true));
//							new PluginLauncherEntry(
//									TextHolder.getString("folderview.openterm"),
//									"nixexplorer.widgets.console.TabbedConsoleWidget",
//									new String[] { "" },
//									new ScaledIcon(App.class.getResource(
//											"/images/Terminalicon2.png"),
//											Utility.toPixel(48),
//											Utility.toPixel(48)),
//									new ScaledIcon(App.class.getResource(
//											"/images/Terminalicon2.png"),
//											Utility.toPixel(20),
//											Utility.toPixel(20))),
//							TextHolder.getString("folderview.openterm")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			PluginRegistry.getSharedInstance().getPluginList()
					.add(new PluginEntry(
							"nixexplorer.widgets.runas.RunAsWidget",
							Arrays.asList(ContextMenuEntry.build(
									TextHolder.getString("folderview.run2"),
									"nixexplorer.widgets.runas.RunAsWidget",
									new String[] { "%f" }, new String[] { "" },
									"sftp", false, false, false)),
							TextHolder.getString("folderview.run2"),
							new ScaledIcon(
									App.class.getResource(
											"/images/Run-icon.png"),
									Utility.toPixel(48), Utility.toPixel(48)),
							new ScaledIcon(
									App.class.getResource(
											"/images/Run-icon.png"),
									Utility.toPixel(20), Utility.toPixel(20)),
							new String[] { "" }, true));

//							new PluginLauncherEntry(
//									TextHolder.getString("folderview.run2"),
//									"nixexplorer.widgets.runas.RunAsWidget",
//									new String[] { "" },
//									new ScaledIcon(
//											App.class.getResource(
//													"/images/Run-icon.png"),
//											Utility.toPixel(48),
//											Utility.toPixel(48)),
//									new ScaledIcon(
//											App.class.getResource(
//													"/images/Run-icon.png"),
//											Utility.toPixel(20),
//											Utility.toPixel(20))),
//							TextHolder.getString("folderview.run2")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			PluginRegistry.getSharedInstance().getPluginList()
					.add(new PluginEntry(
							"nixexplorer.widgets.archiver.ArchiveCompressWidget",
							Arrays.asList(ContextMenuEntry.build(
									TextHolder.getString("archiver.compress"),
									"nixexplorer.widgets.archiver.ArchiveCompressWidget",
									new String[] { "%d", "%f" },
									new String[] { "" }, "sftp", true, true,
									false)),
							TextHolder.getString("archiver.compress"),
							new ScaledIcon(
									App.class.getResource(
											"/images/editor128.png"),
									Utility.toPixel(48), Utility.toPixel(48)),
							new ScaledIcon(
									App.class.getResource(
											"/images/editor128.png"),
									Utility.toPixel(20), Utility.toPixel(20)),
							new String[] { "" }, false));
//							new PluginLauncherEntry(
//									TextHolder.getString("archiver.compress"),
//									"nixexplorer.widgets.archiver.ArchiveCompressWidget",
//									new String[] { "" },
//									new ScaledIcon(
//											App.class.getResource(
//													"/images/editor128.png"),
//											Utility.toPixel(48),
//											Utility.toPixel(48))),
//							null, TextHolder.getString("archiver.compress")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			PluginRegistry.getSharedInstance().getPluginList()
					.add(new PluginEntry(
							"nixexplorer.widgets.archiver.ArchiveExtractWidget",
							Arrays.asList(ContextMenuEntry.build(
									TextHolder
											.getString("archiver.extracthere"),
									"nixexplorer.widgets.archiver.ArchiveExtractWidget",
									new String[] { "%d", "%f" },
									new String[] { ".gz", ".tar", ".xz", ".zip",
											".bz2", ".tbz", ".txz", ".tgz",
											".tbz2" },
									"sftp", false, false, false)),
							TextHolder.getString("archiver.extracthere"),
							new ScaledIcon(
									App.class.getResource(
											"/images/editor128.png"),
									Utility.toPixel(48), Utility.toPixel(48)),
							new ScaledIcon(
									App.class.getResource(
											"/images/editor128.png"),
									Utility.toPixel(48), Utility.toPixel(48)),
							null, false));
//							new PluginLauncherEntry(
//									TextHolder
//											.getString("archiver.extracthere"),
//									"nixexplorer.widgets.archiver.ArchiveExtractWidget",
//									new String[] { "" },
//									new ScaledIcon(
//											App.class.getResource(
//													"/images/editor128.png"),
//											Utility.toPixel(48),
//											Utility.toPixel(48))),
//							null,
//							TextHolder.getString("archiver.extracthere")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			PluginRegistry.getSharedInstance().getPluginList()
					.add(new PluginEntry(
							"nixexplorer.widgets.archiver.ArchiveExtractToWidget",
							Arrays.asList(ContextMenuEntry.build(
									TextHolder.getString("archiver.extractto"),
									"nixexplorer.widgets.archiver.ArchiveExtractToWidget",
									new String[] { "%d", "%f" },
									new String[] { ".gz", ".tar", ".xz", ".zip",
											".bz2", ".tbz", ".txz", ".tgz",
											".tbz2" },
									"sftp", false, false, false)),
							TextHolder.getString("archiver.extractto"),
							new ScaledIcon(
									App.class.getResource(
											"/images/editor128.png"),
									Utility.toPixel(48), Utility.toPixel(48)),
							new ScaledIcon(
									App.class.getResource(
											"/images/editor128.png"),
									Utility.toPixel(48), Utility.toPixel(48)),
							null, false));
//							new PluginLauncherEntry(
//									TextHolder
//											.getString("archiver.extracthere"),
//									"nixexplorer.widgets.archiver.ArchiveExtractWidget",
//									new String[] { "" },
//									new ScaledIcon(
//											App.class.getResource(
//													"/images/editor128.png"),
//											Utility.toPixel(48),
//											Utility.toPixel(48))),
//							null,
//							TextHolder.getString("archiver.extracthere")));
		} catch (Exception e) {
			e.printStackTrace();
		}

//		try {
//			PluginRegistry.getSharedInstance().getPluginList()
//					.add(new PluginEntry(
//							"nixexplorer.widgets.archiver.ArchivePreviewWidget",
//							Arrays.asList(ContextMenuEntry.build(
//									TextHolder.getString("archiver.preview"),
//									"nixexplorer.widgets.archiver.ArchivePreviewWidget",
//									new String[] { "-o", "%f" },
//									new String[] { ".gz", ".tar", ".xz", ".zip",
//											".bz2", ".tbz", ".txz", ".tgz",
//											".tbz2" },
//									"sftp", false, false, false)),
//							TextHolder.getString("archiver.preview"),
//							new ScaledIcon(
//									App.class
//											.getResource("/images/archive.png"),
//									Utility.toPixel(48), Utility.toPixel(48)),
//							new ScaledIcon(
//									App.class
//											.getResource("/images/archive.png"),
//									Utility.toPixel(20), Utility.toPixel(20)),
//							new String[] { "" }, true));
//
////							new PluginLauncherEntry(
////									TextHolder.getString("archiver.preview"),
////									"nixexplorer.widgets.archiver.ArchivePreviewWidget",
////									new String[] { "" },
////									new ScaledIcon(
////											App.class.getResource(
////													"/images/archive.png"),
////											Utility.toPixel(48),
////											Utility.toPixel(48)),
////									new ScaledIcon(
////											App.class.getResource(
////													"/images/archive.png"),
////											Utility.toPixel(20),
////											Utility.toPixel(20))),
////							TextHolder.getString("archiver.preview")));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		try {
			PluginRegistry.getSharedInstance().getPluginList()
					.add(new PluginEntry(
							"nixexplorer.widgets.sysmon.SystemMonitorWidget",
							new ArrayList<>(),
							TextHolder.getString("toolbar.sysmon"),
							new ScaledIcon(
									App.class
											.getResource("/images/taskmgr.png"),
									Utility.toPixel(48), Utility.toPixel(48)),
							new ScaledIcon(
									App.class
											.getResource("/images/taskmgr.png"),
									Utility.toPixel(20), Utility.toPixel(20)),
							new String[] { "" }, true));
//							new PluginLauncherEntry(
//									TextHolder.getString("toolbar.sysmon"),
//									"nixexplorer.widgets.sysmon.SystemMonitorWidget",
//									new String[] { "" },
//									new ScaledIcon(
//											App.class.getResource(
//													"/images/taskmgr.png"),
//											Utility.toPixel(48),
//											Utility.toPixel(48)),
//									new ScaledIcon(
//											App.class.getResource(
//													"/images/taskmgr.png"),
//											Utility.toPixel(20),
//											Utility.toPixel(20))),
//							TextHolder.getString("toolbar.sysmon")));
		} catch (Exception e) {
			e.printStackTrace();
		}

//		try {
//			PluginRegistry.getSharedInstance().getPluginList()
//					.add(new PluginEntry(
//							"nixexplorer.widgets.filetransfer.FileTransferWidget",
//							new ArrayList<>(),
//							new PluginLauncherEntry(
//									TextHolder.getString("filetransfer.title"),
//									"nixexplorer.widgets.filetransfer.FileTransferWidget",
//									new String[] { "" },
//									new ScaledIcon(App.class.getResource(
//											"/images/file-transfer.png"),
//											Utility.toPixel(48),
//											Utility.toPixel(48)),
//									new ScaledIcon(App.class.getResource(
//											"/images/file-transfer.png"),
//											Utility.toPixel(20),
//											Utility.toPixel(20))),
//							TextHolder.getString("filetransfer.title")));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
		try {
			PluginRegistry.getSharedInstance().getPluginList()
					.add(new PluginEntry(
							"nixexplorer.widgets.folderview.foregin.ForeignFolderViewWidget",
							new ArrayList<>(),
							TextHolder.getString("ftp.title"),
							new ScaledIcon(
									App.class.getResource(
											"/images/file-transfer.png"),
									Utility.toPixel(48), Utility.toPixel(48)),
							new ScaledIcon(
									App.class.getResource(
											"/images/file-transfer.png"),
									Utility.toPixel(20), Utility.toPixel(20)),
							new String[] { "" }, true));//
//							new PluginLauncherEntry(
//									TextHolder.getString("ftp.title"),
//									"nixexplorer.widgets.folderview.foregin.ForeignFolderViewWidget",
//									new String[] { "" },
//									new ScaledIcon(App.class.getResource(
//											"/images/file-transfer.png"),
//											Utility.toPixel(48),
//											Utility.toPixel(48)),
//									new ScaledIcon(App.class.getResource(
//											"/images/file-transfer.png"),
//											Utility.toPixel(20),
//											Utility.toPixel(20))),
//							TextHolder.getString("filetransfer.title")));
		} catch (Exception e) {
			e.printStackTrace();
		}

//		try {
//			PluginRegistry.getSharedInstance().getPluginList()
//					.add(new PluginEntry(
//							"nixexplorer.widgets.filetransfer.Remote2RemoteTransferWidget",
//							new ArrayList<>(),
//							new PluginLauncherEntry(
//									TextHolder.getString("ftp.title"),
//									"nixexplorer.widgets.filetransfer.Remote2RemoteTransferWidget",
//									new String[] { "" },
//									new ScaledIcon(App.class.getResource(
//											"/images/file-transfer.png"),
//											Utility.toPixel(48),
//											Utility.toPixel(48)),
//									new ScaledIcon(App.class.getResource(
//											"/images/file-transfer.png"),
//											Utility.toPixel(20),
//											Utility.toPixel(20))),
//							TextHolder.getString("filetransfer.title")));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

//		try {
//			PluginRegistry.getSharedInstance().getPluginList()
//					.add(new PluginEntry("nixexplorer.widgets.curl.CurlWidget",
//							new ArrayList<>(),
//							new PluginLauncherEntry(
//									TextHolder.getString("curl.title"),
//									"nixexplorer.widgets.curl.CurlWidget",
//									new String[] { "" },
//									new ScaledIcon(
//											App.class.getResource(
//													"/images/archive.png"),
//											Utility.toPixel(48),
//											Utility.toPixel(48)),
//									new ScaledIcon(
//											App.class.getResource(
//													"/images/archive.png"),
//											Utility.toPixel(20),
//											Utility.toPixel(20))),
//							TextHolder.getString("archiver.preview")));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		try {
			PluginRegistry.getSharedInstance().getPluginList()
					.add(new PluginEntry("nixexplorer.widgets.http.HttpClient",
							new ArrayList<>(),
							TextHolder.getString("http.title"),

							new ScaledIcon(
									App.class
											.getResource("/images/archive.png"),
									Utility.toPixel(48), Utility.toPixel(48)),
							new ScaledIcon(
									App.class
											.getResource("/images/archive.png"),
									Utility.toPixel(20), Utility.toPixel(20)),
							new String[] { "" }, true));
//							new PluginLauncherEntry(
//									TextHolder.getString("http.title"),
//									"nixexplorer.widgets.http.HttpClient",
//									new String[] { "" },
//									new ScaledIcon(
//											App.class.getResource(
//													"/images/archive.png"),
//											Utility.toPixel(48),
//											Utility.toPixel(48)),
//									new ScaledIcon(
//											App.class.getResource(
//													"/images/archive.png"),
//											Utility.toPixel(20),
//											Utility.toPixel(20))),
//							TextHolder.getString("archiver.preview")));
		} catch (Exception e) {
			e.printStackTrace();
		}

		buildPluginMenu();

	}

	private static void buildPluginMenu() {
		for (PluginEntry e : PluginRegistry.getSharedInstance()
				.getPluginList()) {
			for (ContextMenuEntry ce : e.getContextMenuItems()) {
				ContextMenuRegistry.getEntryList().add(ce);
			}

//			if (e.getLauncher() != null) {
//				PluginLauncherRegistry.getList().add(e.getLauncher());
//			}
		}
	}

	private static void createAndShowWindow() {

		SwingUtilities.invokeLater(() -> {
			MainAppFrame.getSharedInstance().setVisible(true);
		});

//		JFrame f = new JFrame();
//
//		JButton btnConnect = new JButton(
//				TextHolder.getString("appmenu.connect"));
//		btnConnect.setBorderPainted(false);
//		btnConnect.setIcon(
//				new ScaledIcon(App.class.getResource("/images/" + "server.png"),
//						Utility.toPixel(24), Utility.toPixel(24)));
//
//		JButton btnMultiTerm = new JButton(
//				TextHolder.getString("appmenu.multiTerm"));
//		btnMultiTerm.setBorderPainted(false);
//		btnMultiTerm.setIcon(new ScaledIcon(
//				App.class.getResource("/images/" + "terminal-icon.png"),
//				Utility.toPixel(24), Utility.toPixel(24)));
//
//		JButton btnSettings = new JButton(
//				TextHolder.getString("appmenu.settings"));
//		btnSettings.setBorderPainted(false);
//		btnSettings.setIcon(new ScaledIcon(
//				App.class.getResource("/images/" + "settings.png"),
//				Utility.toPixel(24), Utility.toPixel(24)));
//		JButton btnHelp = new JButton(TextHolder.getString("appmenu.help"));
//		btnHelp.setBorderPainted(false);
//		btnHelp.setIcon(
//				new ScaledIcon(App.class.getResource("/images/" + "help.png"),
//						Utility.toPixel(24), Utility.toPixel(24)));
//
//		btnConnect.addActionListener(e -> {
//			AppSessionPanel.getsharedInstance().newWorkspace();
//		});
//
//		btnMultiTerm.addActionListener(e -> {
//			MultiHostTerminal mterm = new MultiHostTerminal();
//			mterm.setSize(Utility.toPixel(800), Utility.toPixel(600));
//			mterm.setLocationRelativeTo(f);
//			mterm.setVisible(true);
//		});
//
//		int max = Math.max(btnConnect.getPreferredSize().width,
//				btnSettings.getPreferredSize().width);
//		max = Math.max(btnHelp.getPreferredSize().width, max);
//
//		Dimension d = new Dimension(max, btnConnect.getPreferredSize().height);
//		btnConnect.setPreferredSize(d);
//		btnConnect.setMinimumSize(d);
//		btnConnect.setMaximumSize(d);
//		btnSettings.setPreferredSize(d);
//		btnSettings.setMinimumSize(d);
//		btnSettings.setMaximumSize(d);
//		btnHelp.setPreferredSize(d);
//		btnHelp.setMinimumSize(d);
//		btnHelp.setMaximumSize(d);
//
//		Box b1 = Box.createHorizontalBox();
//		b1.setBorder(new EmptyBorder(Utility.toPixel(2), Utility.toPixel(2),
//				Utility.toPixel(2), Utility.toPixel(2)));
//		b1.add(btnConnect);
//		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
//
//		b1.add(btnSettings);
//		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
//		b1.add(btnHelp);
//		b1.add(Box.createHorizontalGlue());
//		b1.add(btnMultiTerm);
//		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
//		f.add(b1, BorderLayout.NORTH);
//
//		Insets inset = Toolkit.getDefaultToolkit()
//				.getScreenInsets(GraphicsEnvironment
//						.getLocalGraphicsEnvironment().getDefaultScreenDevice()
//						.getDefaultConfiguration());
//
//		Dimension screenD = Toolkit.getDefaultToolkit().getScreenSize();
//
//		int screenWidth = screenD.width - inset.left - inset.right;
//		int screenHeight = screenD.height - inset.top - inset.bottom;
//
//		if (screenWidth < 800 || screenHeight < 600) {
//			f.setSize(screenWidth, screenHeight);
//		} else {
//			int width = (screenWidth * 80) / 100;
//			int height = (screenHeight * 80) / 100;
//			f.setSize(width, height);
//		}
//
//		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		f.setLocationRelativeTo(null);
//		f.add(AppSessionPanel.getsharedInstance());
//		f.setVisible(true);
	}

	public static void loadDarkTheme() {
		try {

			setTheme("light");
			String black = "light";

			Color background = new Color(40, 40, 40);
			Color foreground = new Color(240, 240, 240);
			Color border = new Color(80, 80, 80);
			Color defBorder = new Color(60, 60, 60);
			Color selection = new Color(70, 70, 70);// new Color(51, 181, 229);
			Color prgBg = new Color(51, 181, 229);
			Color c1 = new Color(50, 50, 50);
			Color c2 = new Color(150, 150, 150);
			Color c3 = new Color(45, 45, 45);
			Color brightFg = Color.WHITE;
			Icon tabCloseIcon = new ScaledIcon(
					App.class.getResource("/images/" + black + "_close.png"),
					Utility.toPixel(14), Utility.toPixel(14));

			Icon tabBlankIcon = new EmptyIcon(Utility.toPixel(14),
					Utility.toPixel(14));
			Font normalFont = Utility.getFont(Constants.SMALL);
			Color titleColor = border;

			// MetalCheckBoxIcon

			UIManager.put("welcome.new",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_add_server.png"),
							Utility.toPixel(32), Utility.toPixel(32)));

			UIManager.put("welcome.settings",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_open_settings.png"),
							Utility.toPixel(32), Utility.toPixel(32)));

			UIManager.put("welcome.help",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_help.png"),
							Utility.toPixel(32), Utility.toPixel(32)));

			UIManager.put("Tab.roundCloseIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_round_close.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("RadioButton.selectedIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_radio_checked.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("RadioButton.icon", new ScaledIcon(
					App.class.getResource(
							"/images/" + black + "_radio_unchecked.png"),
					Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("CheckBox.icon",
					new StatefullIcon(
							new ScaledIcon(
									App.class.getResource("/images/" + black
											+ "_unchecked.png"),
									Utility.toPixel(20), Utility.toPixel(20)),
							new ScaledIcon(
									App.class.getResource("/images/" + black
											+ "_checked.png"),
									Utility.toPixel(20), Utility.toPixel(20))));

			UIManager.put("CheckBox.selectedIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_checked.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("RadioButtonMenuItem.checkIcon",
					UIManager.get("RadioButton.selectedIcon"));

			UIManager.put("CheckBoxMenuItem.checkIcon",
					UIManager.get("CheckBox.icon"));
			// UIManager.put("CheckBoxMenuItem.selectedIcon",UIManager.get("CheckBox.selectedIcon"));

			UIManager.put("RadioButtonMenuItem.checkIcon", new StatefullIcon(
					new ScaledIcon(
							App.class.getResource("/images/" + black
									+ "_radio_unchecked.png"),
							Utility.toPixel(20), Utility.toPixel(20)),
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_radio_checked.png"),
							Utility.toPixel(20), Utility.toPixel(20))));
			// UIManager.put("RadioButtonMenuItem.checkIcon",UIManager.get("CheckBox.icon"));
			// UIManager.put("RadioButtonMenuItem.selectedIcon",UIManager.get("RadioButton.selectedIcon"));

//			UIManager.put("RadioButton.selectedIcon",
//					new ScaledIcon(
//							App.class.getResource(
//									"/images/" + black + "_radio_checked.png"),
//							Utility.toPixel(20), Utility.toPixel(20)));
//
//			UIManager.put("RadioButtonMenuItem.checkIcon", new ScaledIcon(
//					App.class.getResource(
//							"/images/" + black + "_radio_unchecked.png"),
//					Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("AddressBar.icon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_arrow.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("AddressBar.back",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_back.png"),
							Utility.toPixel(24), Utility.toPixel(24)));
			UIManager.put("AddressBar.up",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_up.png"),
							Utility.toPixel(24), Utility.toPixel(24)));
			UIManager.put("Table.ascendingSortIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_up.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("Table.descendingSortIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_down.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("AddressBar.forward",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_forward.png"),
							Utility.toPixel(24), Utility.toPixel(24)));
			UIManager.put("AddressBar.reload",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_reload.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("AddressBar.moreMenu",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_more_menu.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("AddressBar.search",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_search.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("AddressBar.edit",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_edit.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("AddressBar.toggle",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_toggle.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("AddressBar.split1",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_split1.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("AddressBar.split2",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_split2.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("Desktop.menu",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_menu.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("Tree.collapsedIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_tree_closed.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("Tree.expandedIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_tree_open.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("InternalFrame.iconifyIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_minimize.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("InternalFrame.closeIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_close.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("FlatTabbedPanel.closeAllIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_close.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("InternalFrame.maximizeIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_maximize.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("InternalFrame.minimizeIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_restore.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("InternalFrame.icon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_restore.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ComboBox.dropIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_tree_open.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("Spinner.downIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_down_arrow.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("Spinner.upIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_up_arrow.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("AddressBar.home",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_home_icon.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("SidePanel.addIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_add.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("SidePanel.collapseIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_left_arrow.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("SidePanel.expandIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_expand.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("ServerList.offlineIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_offline.png"),
							Utility.toPixel(32), Utility.toPixel(32)));

			UIManager.put("ServerList.searchIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_search_icon.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("ServerList.editIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_edit_icon.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("ServerList.deleteIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_delete.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("ServerTools.filesIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_files.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ServerTools.terminalIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_terminal.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ServerTools.editorIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_text_editor.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ServerTools.logViewIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_logview.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ServerTools.taskmgrIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_taskmgr.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ServerTools.fileshareIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_fileshare.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ServerTools.curlIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_curl.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ServerTools.findFilesIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_search_icon.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ServerTools.settingsIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_settings.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ExpandPanel.upIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_up.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ExpandPanel.downIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_down.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("TextEditor.saveIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_save.png"),
							Utility.toPixel(18), Utility.toPixel(18)));
			UIManager.put("TextEditor.findIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_find_in_page.png"),
							Utility.toPixel(18), Utility.toPixel(16)));
			UIManager.put("TextEditor.replaceIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_find_replace.png"),
							Utility.toPixel(18), Utility.toPixel(16)));
			UIManager.put("TextEditor.gotoLineIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_goto_line.png"),
							Utility.toPixel(18), Utility.toPixel(18)));
			UIManager.put("TextEditor.reloadIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_page_reload.png"),
							Utility.toPixel(18), Utility.toPixel(16)));

			UIManager.put("TextEditor.cutTextIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_cut_text.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("TextEditor.pasteTextIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_paste_text.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("TextEditor.copyTextIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_copy_text.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("FolderView.hideSideBarIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_hide_sidebar.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("FolderView.showSideBarIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_show_sidebar.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("ServerTools.terminalIcon16",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_terminal.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("ServerTools.editorIcon16",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_text_editor.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("ServerTools.filesIcon16",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_files.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("ServerTools.curlIcon16",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_curl.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("ServerTools.logViewIcon16",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_logview.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("ServerTools.findFilesIcon16",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_search_icon.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("ServerTools.taskmgrIcon16",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_taskmgr.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("ViewMode.listIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_list_view.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("ViewMode.detailsIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_details_view.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

//			UIManager.put("TextEditor.reloadIcon",
//					new ScaledIcon(
//							App.class.getResource(
//									"/images/" + black + "_page_reload.png"),
//							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("RTextArea.highlight", border);
			UIManager.put("Gutter.foreground", brightFg);
			UIManager.put("TabbedPane.background", background);
			UIManager.put("TabbedPane.foreground", foreground);
			UIManager.put("TabbedPane.highlight", background);
			UIManager.put("TabbedPane.borderHightlightColor", background);
			UIManager.put("TabbedPane.light", background);
			UIManager.put("TabbedPane.selected", background);
			UIManager.put("TabbedPane.selectHighlight", background);
			UIManager.put("TabbedPane.shadow", background);
			UIManager.put("TabbedPane.darkShadow", background);
			UIManager.put("TabbedPane.selectHighlight", background);
//			UIManager.put("TabbedPane.contentBorderInsets",
//					new Insets(Utility.toPixel(1), Utility.toPixel(1),
//							Utility.toPixel(1), Utility.toPixel(1)));
//			UIManager.put("TabbedPane.selectedTabPadInsets",
//					new Insets(0, 0, 0, 0));
//			UIManager.put("TabbedPane.tabAreaInsets", new Insets(0, 0, 0, 0));
//			UIManager.put("TabbedPane.tabInsets", new Insets(0, 0, 0, 0));
//					new Insets(Utility.toPixel(5), Utility.toPixel(5),
//							Utility.toPixel(5), Utility.toPixel(5)));
			UIManager.put("TabbedPane.selectHighlight", foreground);
			UIManager.put("TabbedPane.focus", background);
			UIManager.put("TabbedPane.flatHighlightBorder",
					new MatteBorder(Utility.toPixel(0), Utility.toPixel(0),
							Utility.toPixel(2), Utility.toPixel(0), selection));
			UIManager.put("TabbedPane.flatBorder",
					new MatteBorder(Utility.toPixel(0), Utility.toPixel(0),
							Utility.toPixel(2), Utility.toPixel(0),
							background));
			// UIManager.put("TabbedPane.tabsOverlapBorder", Boolean.TRUE);
			UIManager.put("TabbedPane.contentAreaColor", background);
			UIManager.put("TabbedPane.selectedLabelShift", 0);
			UIManager.put("TabbedPane.labelShift", 0);
			UIManager.put("TabbedPane.tabInsets", new Insets(0, 0, 0, 0));
			UIManager.put("TabbedPane.selectedTabPadInsets",
					new Insets(0, 0, 0, 0));
			UIManager.put("TabbedPane.tabAreaInsets", new Insets(0, 0, 0, 0));
			UIManager.put("TabbedPane.contentBorderInsets",
					new Insets(0, 0, 0, 0));
			UIManager.put("TabbedPane.tabsOverlapBorder", Boolean.FALSE);

			UIManager.put("Component.border",
					new LineBorder(border, Utility.toPixel(1)));

			UIManager.put("RounderBorder.color", border);

			UIManager.put("DefaultBorder.color", defBorder);
			UIManager.put("Panel.secondary", c1);
			UIManager.put("Panel.highlight", c2);
			UIManager.put("Panel.shadow", c3);

			UIManager.put("Taskbar.height", Utility.toPixel(32));
			UIManager.put("AddressBar.borderColor", border);
			UIManager.put("AddressBar.border", border);
			UIManager.put("AddressBar.background", Color.gray);
			UIManager.put("AddressBar.foreground", Color.black);
			UIManager.put("AddressBar.active", Color.blue);
			UIManager.put("AddressBar.hot", border);
			UIManager.put("AddressBar.activeForeground", Color.cyan);
			UIManager.put("AddressBar.textPaddingX", Utility.toPixel(10));
			UIManager.put("AddressBar.textPaddingY", Utility.toPixel(5));

			UIManager.put("Desktop.background", new Color(1, 12, 46));
			UIManager.put("Desktop.foreground", Color.WHITE);
			UIManager.put("Desktop.selectedForeground", Color.GRAY);
			UIManager.put("Desktop.selectedForeground", Color.GRAY);

			UIManager.put("ToggleButton.background", background);
			UIManager.put("ToggleButton.border",
					new LineBorder(border, Utility.toPixel(1)));
			UIManager.put("ToggleButton.foreground", Color.BLACK);
			UIManager.put("ToggleButton.select", selection);

			UIManager.put("Button.background", background);
			UIManager.put("Button.border", new FlatButtonBorder(border));
			UIManager.put("Button.rolloverBorder",
					new LineBorder(selection, Utility.toPixel(1)));
			UIManager.put("Button.rollover", Boolean.TRUE);
			UIManager.put("Button.highlight", selection);
			UIManager.put("Button.select", Color.BLUE);
			UIManager.put("Button.font", normalFont);
			UIManager.put("Button.foreground", foreground);
			UIManager.put("Button.margin",
					new Insets(Utility.toPixel(5), Utility.toPixel(5),
							Utility.toPixel(5), Utility.toPixel(5)));
			UIManager.put("Button.highlight", selection);
			UIManager.put("Button.darkShadow", selection.darker());

			UIManager.put("Label.font", normalFont);
			UIManager.put("Label.foreground", foreground);

			UIManager.put("Panel.background", background);

			UIManager.put("StartMenu.background", background);

			UIManager.put("TaskBar.background", background);
			UIManager.put("TaskBar.border",
					new LineBorder(new Color(20, 20, 20), Utility.toPixel(1)));
			UIManager.put("TaskBar.buttonBackground", border);

			UIManager.put("PopupMenu.border",
					new LineBorder(new Color(30, 30, 30), Utility.toPixel(1)));
			UIManager.put("PopupMenu.background", background);
			UIManager.put("PopupMenu.foreground", foreground);
			UIManager.put("PopupMenu.font", normalFont);

			UIManager.put("Popup.background", background);

			UIManager.put("Separator.background", defBorder);
			UIManager.put("Separator.foreground", defBorder);
			UIManager.put("Separator.thickness", Utility.toPixel(1));
			UIManager.put("Separator.insets",
					new Insets(Utility.toPixel(1), Utility.toPixel(1),
							Utility.toPixel(1), Utility.toPixel(1)));
			// UIManager.put("PopupMenu.font", normalFont);

			UIManager.put("MenuItem.border",
					new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
							Utility.toPixel(5), Utility.toPixel(5)));
			UIManager.put("MenuItem.background", background);
			UIManager.put("MenuItem.foreground", foreground);
			UIManager.put("MenuItem.font", normalFont);
			UIManager.put("MenuItem.selectionBackground", selection);
			UIManager.put("MenuItem.selectionForeground", foreground);
			UIManager.put("MenuItem.acceleratorForeground", border);
			UIManager.put("MenuItem.acceleratorSelectionForeground",
					foreground);

			UIManager.put("Menu.border",
					new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
							Utility.toPixel(5), Utility.toPixel(5)));
			UIManager.put("Menu.background", border);
			UIManager.put("Menu.foreground", foreground);
			UIManager.put("Menu.font", normalFont);
			UIManager.put("Menu.selectionBackground", selection);
			UIManager.put("Menu.selectionForeground", foreground);
			UIManager.put("Menu.acceleratorForeground", border);
			UIManager.put("Menu.acceleratorSelectionForeground", foreground);

			UIManager.put("MenuBar.border",
					new EmptyBorder(Utility.toPixel(0), Utility.toPixel(0),
							Utility.toPixel(0), Utility.toPixel(0)));
			UIManager.put("MenuBar.background", background);
			UIManager.put("MenuBar.borderColor", background);
			UIManager.put("MenuBar.foreground", foreground);
			UIManager.put("MenuBar.font", normalFont);
			UIManager.put("MenuBar.highlight", background);
			UIManager.put("MenuBar.darkShadow", background);

			UIManager.put("textHighlight", selection);
			UIManager.put("textHighlightText", selection);
			UIManager.put("TextField.background", background);
			UIManager.put("TextField.foreground", foreground);
			UIManager.put("TextField.inactiveForeground", foreground);
			UIManager.put("TextField.font", normalFont);
			UIManager.put("TextField.caretForeground", foreground);
			UIManager.put("TextField.border",
					new LineBorder(selection, Utility.toPixel(1)));
			UIManager.put("TextField.inactiveForeground", border);
			UIManager.put("TextField.selectionBackground", prgBg);
			UIManager.put("TextField.selectionForeground", foreground);

			UIManager.put("FormattedTextField.background", background);
			UIManager.put("FormattedTextField.foreground", foreground);
			UIManager.put("FormattedTextField.font", normalFont);
			UIManager.put("FormattedTextField.caretForeground", foreground);
//			UIManager.put("FormattedTextField.border",
//					new LineBorder(selection, Utility.toPixel(1)));
			UIManager.put("FormattedTextField.inactiveForeground", border);
			UIManager.put("FormattedTextField.selectionBackground", prgBg);
			UIManager.put("FormattedTextField.selectionForeground", foreground);

			UIManager.put("TextArea.background", background);
			UIManager.put("TextArea.foreground", foreground);
			UIManager.put("TextArea.font", normalFont);
			UIManager.put("TextArea.caretForeground", foreground);
			UIManager.put("TextArea.border",
					new LineBorder(background, Utility.toPixel(1)));
			UIManager.put("TextArea.selectionBackground", prgBg);
			UIManager.put("TextArea.selectionForeground", foreground);
			UIManager.put("TextPane.selectionForeground", foreground);
			UIManager.put("EditorPane.selectionBackground", prgBg);
			UIManager.put("TextComponent.selectionBackground", prgBg);
//			UIManager.put("nimbusSelectionBackground", selection);
//			UIManager.put("nimbusSelectedText", selection);

			UIManager.put("CheckBox.background", background);
			UIManager.put("CheckBox.foreground", foreground);
			UIManager.put("CheckBox.font", normalFont);
			UIManager.put("Checkbox.select", selection);
			UIManager.put("CheckBox.border",
					new LineBorder(selection, Utility.toPixel(1)));
			UIManager.put("TextArea.selectionBackground", prgBg);

			UIManager.put("RadioButton.background", background);
			UIManager.put("RadioButton.foreground", foreground);
			UIManager.put("RadioButton.font", normalFont);
			UIManager.put("RadioButton.select", selection);
			UIManager.put("RadioButton.border",
					new LineBorder(selection, Utility.toPixel(1)));

			UIManager.put("TextArea.selectionBackground", prgBg);

			UIManager.put("Spinner.background", background);
			UIManager.put("Spinner.foreground", foreground);
			UIManager.put("Spinner.arrowBackground", border);
			UIManager.put("Spinner.font", normalFont);
			UIManager.put("Spinner.select", selection);
			UIManager.put("Spinner.border",
					new LineBorder(selection, Utility.toPixel(1)));
			UIManager.put("Spinner.editorBorderPainted", Boolean.FALSE);
			UIManager.put("Spinner.arrowButtonInsets",
					new Insets(Utility.toPixel(1), Utility.toPixel(1),
							Utility.toPixel(1), Utility.toPixel(1)));

			UIManager.put("Spinner.selectionBackground", prgBg);

			UIManager.put("Table.background", c1);
			UIManager.put("Table.border",
					new LineBorder(selection, Utility.toPixel(1)));
			UIManager.put("Table.shadow", background);
			UIManager.put("Table.darkShadow", background);
			UIManager.put("Table.gridColor", background);
			UIManager.put("Table.selectionBackground", selection);
			UIManager.put("Table.selectionForeground", foreground);
			UIManager.put("Table.focusCellBackground", selection);
			UIManager.put("Table.focusCellHighlightBorder",
					new EmptyBorder(new Insets(0, 0, 0, 0)));
			// UIManager.put("Table.background", background);
			UIManager.put("Table.foreground", foreground);
			UIManager.put("Table.rendererUseTableColors", Boolean.TRUE);
			UIManager.put("Table.scrollPaneBorder",
					new LineBorder(background, Utility.toPixel(0)));

			UIManager.put("TableHeader.background", background);
			UIManager.put("TableHeader.foreground", foreground);

			MatteBorder mb = new MatteBorder(0, 0, Utility.toPixel(1),
					Utility.toPixel(1), border);

			UIManager.put("TableHeader.cellBorder", mb);

//			UIManager.put("Tree.gridColor", background);
//			UIManager.put("Tree.gridColor", background);
//			UIManager.put("Tree.gridColor", background);
//			UIManager.put("Tree.gridColor", background);

			UIManager.put("SplitPane.border",
					new LineBorder(background, Utility.toPixel(1)));
			UIManager.put("SplitPane.dividerSize", Utility.toPixel(1));
			UIManager.put("SplitPaneDivider.border",
					new LineBorder(background, Utility.toPixel(1)));
			UIManager.put("SplitPane.background", background);

			UIManager.put("LineGraph.foreground", foreground);
			UIManager.put("LineGraph.background", background);
			UIManager.put("LineGraph.gridColor", border);
			UIManager.put("LineGraph.lineColor", selection);

			UIManager.put("ScrollBar.background", background);
			UIManager.put("ScrollBar.squareButtons", false);
			UIManager.put("ScrollBar.thumb", selection);
			UIManager.put("ScrollBar.foreground", selection);
			UIManager.put("ScrollBar.highlight", selection);
			UIManager.put("ScrollBar.thumbRollover", new Color(100, 100, 100));
			UIManager.put("ScrollBar.gradient", null);
			UIManager.put("ScrollBar.border",
					new EmptyBorder(new Insets(0, 0, 0, 0)));
			UIManager.put("ScrollBar.width", Utility.toPixel(8));

			UIManager.put("ScrollPane.viewportBorder",
					new LineBorder(background, Utility.toPixel(1)));
			UIManager.put("ScrollPane.viewportBorderInsets",
					new Insets(0, 0, 0, 0));
			UIManager.put("ScrollPane.border",
					new LineBorder(border, Utility.toPixel(1)));
			UIManager.put("ScrollPane.background", background);
			UIManager.put("ScrollPane.border",
					new EmptyBorder(new Insets(0, 0, 0, 0)));

			UIManager.put("Viewport.border",
					new EmptyBorder(new Insets(0, 0, 0, 0)));
			UIManager.put("Viewport.background", background);

			UIManager.put("ComboBox.background", background);
			UIManager.put("ComboBox.foreground", foreground);
			UIManager.put("ComboBox.border",
					new LineBorder(border, Utility.toPixel(1)));
//			UIManager.put("ComboBox.buttonBackground", background);
//			UIManager.put("ComboBox.buttonDarkShadow", background);
//			UIManager.put("ComboBox.buttonHighlight", background);
//			UIManager.put("ComboBox.buttonShadow", background);
			UIManager.put("ComboBox.control", background);
			UIManager.put("ComboBox.controlForeground", foreground);
			UIManager.put("ComboBox.selectionBackground", selection);
			UIManager.put("ComboBox.selectionForeground", foreground);
			UIManager.put("ComboBox.font", normalFont);

			UIManager.put("InternalFrame.border",
					new LineBorder(background, Utility.toPixel(5)));
			UIManager.put("InternalFrame.activeBorder",
					new LineBorder(titleColor, Utility.toPixel(5)));
			UIManager.put("InternalFrame.activeBorderColor", titleColor);
			UIManager.put("InternalFrame.borderColor", border);
			UIManager.put("InternalFrame.activeTitleBackground", titleColor);
			UIManager.put("InternalFrame.inactiveTitleBackground", background);
			UIManager.put("InternalFrame.inactiveTitleForeground", foreground);
			UIManager.put("InternalFrame.activeTitleForeground", foreground);
			UIManager.put("InternalFrame.activeTitleBackground", titleColor);
			UIManager.put("InternalFrame.titlePaneHeight", Utility.toPixel(24));
			UIManager.put("InternalFrame.titleButtonHeight",
					Utility.toPixel(24));
			UIManager.put("InternalFrame.titleButtonWidth",
					Utility.toPixel(24));
			UIManager.put("InternalFrameTitlePane.maximizeButtonOpacity",
					Boolean.TRUE);
			UIManager.put("InternalFrameTitlePane.closeButtonOpacity",
					Boolean.TRUE);
			UIManager.put("InternalFrameTitlePane.iconifyButtonOpacity",
					Boolean.TRUE);
			UIManager.put("InternalFrame.minBackground", Color.DARK_GRAY);
			UIManager.put("InternalFrame.maxBackground", Color.DARK_GRAY);
			UIManager.put("InternalFrame.closeBackground", Color.DARK_GRAY);// Color.RED);

			UIManager.put("FlatTabbedPane.highlight", border);
			UIManager.put("FlatTabbedPane.background", background);
			UIManager.put("FlatTabbedPane.closeIcon", tabCloseIcon);
			UIManager.put("FlatTabbedPane.blankIcon", tabBlankIcon);

//			Icon smallFolder = new ScaledIcon(
//					App.class.getResource("/images/blue_folder.png"),
//					Utility.toPixel(20), Utility.toPixel(20));

			Icon smallFolder = new ScaledIcon(
					App.class.getResource("/images/folder.png"),
					Utility.toPixel(20), Utility.toPixel(20));

			Icon smallFile = new ScaledIcon(
					App.class.getResource("/images/fileicon.png"),
					Utility.toPixel(20), Utility.toPixel(20));

			UIManager.put("ListView.smallFolder", smallFolder);
			UIManager.put("ListView.smallFile", smallFile);
			UIManager.put("ListView.smallFile", smallFile);

			UIManager.put("Tree.background", background);
			UIManager.put("Tree.font", normalFont);
			UIManager.put("Tree.textBackground", background);
			UIManager.put("Tree.textForeground", foreground);
			UIManager.put("Tree.selectionBackground", selection);
			UIManager.put("Tree.selectionForeground", foreground);
			UIManager.put("Tree.closedIcon", smallFolder);
			UIManager.put("Tree.openIcon", smallFolder);
			UIManager.put("Tree.line", selection);
			UIManager.put("Tree.drawDashedFocusIndicator", Boolean.FALSE);
			UIManager.put("Tree.selectionBorderColor", selection);
			UIManager.put("Tree.leafIcon", smallFile);
			UIManager.put("Tree.rowHeight", Utility.toPixel(24));
			UIManager.put("Tree.drawVerticalLines", Boolean.FALSE);
			UIManager.put("Tree.drawHorizontalLines", Boolean.FALSE);
			UIManager.put("Tree.paintLines", Boolean.FALSE);
			UIManager.put("Tree.selectionBackground", selection);
			UIManager.put("Tree.iconShadow", selection);
			UIManager.put("Tree.iconHighlight", background);
			UIManager.put("Tree.iconBackground", selection);
			UIManager.put("Tree.rendererUseTreeColors", Boolean.TRUE);
			UIManager.put("Tree.background", background);
			UIManager.put("Tree.padding", Integer.valueOf(20));
			UIManager.put("Tree.leftChildIndent",
					Integer.valueOf(Utility.toPixel(15)));
			UIManager.put("Tree.rightChildIndent",
					Integer.valueOf(Utility.toPixel(5)));
			// UIManager.put("Tree.expanderSize", Integer.valueOf(30));

			UIManager.put("List.background", background);
			UIManager.put("List.foreground", foreground);
			UIManager.put("List.selectionBackground", selection);
			UIManager.put("List.selectionForeground", foreground);
			UIManager.put("List.rendererUseUIBorder", Boolean.TRUE);
			UIManager.put("List.rendererUseListColors", Boolean.TRUE);
			UIManager.put("List.focusCellHighlightBorder",
					new EmptyBorder(0, 0, 0, 0));
			UIManager.put("List.border", new EmptyBorder(0, 0, 0, 0));

			UIManager.put("Terminal.background", background);
			UIManager.put("Terminal.foreground", foreground);
			UIManager.put("Terminal.font", normalFont);
			UIManager.put("Terminal.selectionBackground", selection);

			UIManager.put("FormattedTextField.background", background);
			UIManager.put("FormattedTextField.foreground", foreground);
			UIManager.put("FormattedTextField.font", normalFont);
			UIManager.put("FormattedTextField.selectionBackground", selection);

			UIManager.put("control", background);
			UIManager.put("controlShadow", background);
			// UIManager.put("controlDkShadow", background);
			UIManager.put("controlLtHighlight", background);

			UIManager.put("PasswordField.background", background);
			UIManager.put("PasswordField.foreground", foreground);
			UIManager.put("PasswordField.caretForeground", foreground);
			UIManager.put("PasswordField.font", normalFont);
			UIManager.put("PasswordField.selectionBackground", selection);
			UIManager.put("PasswordField.border",
					new LineBorder(selection, Utility.toPixel(1)));

			UIManager.put("ProgressBar.background", defBorder);
			UIManager.put("ProgressBar.foreground", prgBg);
			UIManager.put("ProgressBar.border", new EmptyBorder(0, 0, 0, 0));
			UIManager.put("ProgressBar.horizontalSize",
					new Dimension(Utility.toPixel(146), Utility.toPixel(8)));
			UIManager.put("ProgressBar.verticalSize",
					new Dimension(Utility.toPixel(8), Utility.toPixel(146)));

			UIManager.put("OptionPane.background", background);
			UIManager.put("OptionPane.messageForeground", foreground);
			UIManager.put("OptionPane.foreground", foreground);
			UIManager.put("OptionPane.border",
					new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
							Utility.toPixel(5), Utility.toPixel(5)));

			UIManager.put("TableHeader.cellBorder", new CompoundBorder(
					new MatteBorder(0, 0, 0, Utility.toPixel(1),
							UIManager.getColor("DefaultBorder.color")),
					new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
							Utility.toPixel(5), Utility.toPixel(5))));

			UIManager.put("ToolTip.background",
					UIManager.getColor("DefaultBorder.color"));
			UIManager.put("ToolTip.backgroundInactive", background);
			UIManager.put("ToolTip.border",
					new LineBorder(UIManager.getColor("DefaultBorder.color"),
							Utility.toPixel(1)));
			UIManager.put("ToolTip.borderInactive",
					new LineBorder(UIManager.getColor("DefaultBorder.color"),
							Utility.toPixel(1)));
			UIManager.put("ToolTip.foreground", foreground);
			UIManager.put("ToolTip.foregroundInactive", foreground);

			UIManager.put("CheckBoxMenuItem.border",
					new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
							Utility.toPixel(5), Utility.toPixel(5)));
			UIManager.put("CheckBoxMenuItem.background", background);
			UIManager.put("CheckBoxMenuItem.foreground", foreground);
			UIManager.put("CheckBoxMenuItem.font", normalFont);
			UIManager.put("CheckBoxMenuItem.selectionBackground", selection);
			UIManager.put("CheckBoxMenuItem.selectionForeground", foreground);
			UIManager.put("CheckBoxMenuItem.acceleratorForeground", border);
			UIManager.put("CheckBoxMenuItem.acceleratorSelectionForeground",
					foreground);

			UIManager.put("RadioButtonMenuItem.border",
					new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
							Utility.toPixel(5), Utility.toPixel(5)));
			UIManager.put("RadioButtonMenuItem.background", background);
			UIManager.put("RadioButtonMenuItem.foreground", foreground);
			UIManager.put("RadioButtonMenuItem.font", normalFont);
			UIManager.put("RadioButtonMenuItem.selectionBackground", selection);
			UIManager.put("RadioButtonMenuItem.selectionForeground",
					foreground);
			UIManager.put("RadioButtonMenuItem.acceleratorForeground", border);
			UIManager.put("RadioButtonMenuItem.acceleratorSelectionForeground",
					foreground);

			UIManager.put("Editor.theme", "dark");

			// MetalInternalFrameUI

			// MetalTreeUI
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadLightTheme() {
		try {

			setTheme("dark");
			String black = "dark";

			Color background = Color.WHITE;
			Color foreground = new Color(20, 20, 20);
			Color border = new Color(230, 230, 230);
			Color defBorder = new Color(230, 230, 230);
			Color selection = new Color(220, 234, 245);// new Color(51, 181,
														// 229);
			Color prgBg = new Color(220, 234, 245);// new Color(51, 181, 229);
			Color c1 = new Color(248, 248, 248);
			Color c2 = new Color(220, 220, 220);
			Color c3 = new Color(245, 245, 245);
			Color brightFg = Color.WHITE;
			Icon tabCloseIcon = new ScaledIcon(
					App.class.getResource("/images/" + black + "_close.png"),
					Utility.toPixel(14), Utility.toPixel(14));

			Icon tabBlankIcon = new EmptyIcon(Utility.toPixel(14),
					Utility.toPixel(14));
			Font normalFont = Utility.getFont(Constants.SMALL);
			Color titleColor = border;

			// MetalCheckBoxIcon

			UIManager.put("welcome.new",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_add_server.png"),
							Utility.toPixel(32), Utility.toPixel(32)));

			UIManager.put("welcome.settings",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_open_settings.png"),
							Utility.toPixel(32), Utility.toPixel(32)));

			UIManager.put("welcome.help",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_help.png"),
							Utility.toPixel(32), Utility.toPixel(32)));

			UIManager.put("Tab.roundCloseIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_round_close.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("RadioButton.selectedIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_radio_checked.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("RadioButton.icon", new ScaledIcon(
					App.class.getResource(
							"/images/" + black + "_radio_unchecked.png"),
					Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("CheckBox.icon",
					new StatefullIcon(
							new ScaledIcon(
									App.class.getResource("/images/" + black
											+ "_unchecked.png"),
									Utility.toPixel(20), Utility.toPixel(20)),
							new ScaledIcon(
									App.class.getResource("/images/" + black
											+ "_checked.png"),
									Utility.toPixel(20), Utility.toPixel(20))));

			UIManager.put("CheckBox.selectedIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_checked.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("RadioButtonMenuItem.checkIcon",
					UIManager.get("RadioButton.selectedIcon"));

			UIManager.put("CheckBoxMenuItem.checkIcon",
					UIManager.get("CheckBox.icon"));
			// UIManager.put("CheckBoxMenuItem.selectedIcon",UIManager.get("CheckBox.selectedIcon"));

			UIManager.put("RadioButtonMenuItem.checkIcon", new StatefullIcon(
					new ScaledIcon(
							App.class.getResource("/images/" + black
									+ "_radio_unchecked.png"),
							Utility.toPixel(20), Utility.toPixel(20)),
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_radio_checked.png"),
							Utility.toPixel(20), Utility.toPixel(20))));
			// UIManager.put("RadioButtonMenuItem.checkIcon",UIManager.get("CheckBox.icon"));
			// UIManager.put("RadioButtonMenuItem.selectedIcon",UIManager.get("RadioButton.selectedIcon"));

//			UIManager.put("RadioButton.selectedIcon",
//					new ScaledIcon(
//							App.class.getResource(
//									"/images/" + black + "_radio_checked.png"),
//							Utility.toPixel(20), Utility.toPixel(20)));
//
//			UIManager.put("RadioButtonMenuItem.checkIcon", new ScaledIcon(
//					App.class.getResource(
//							"/images/" + black + "_radio_unchecked.png"),
//					Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("AddressBar.icon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_arrow.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("AddressBar.back",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_back.png"),
							Utility.toPixel(24), Utility.toPixel(24)));
			UIManager.put("AddressBar.up",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_up.png"),
							Utility.toPixel(24), Utility.toPixel(24)));
			UIManager.put("Table.ascendingSortIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_up.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("Table.descendingSortIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_down.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("AddressBar.forward",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_forward.png"),
							Utility.toPixel(24), Utility.toPixel(24)));
			UIManager.put("AddressBar.reload",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_reload.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("AddressBar.moreMenu",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_more_menu.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("AddressBar.search",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_search.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("AddressBar.edit",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_edit.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("AddressBar.toggle",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_toggle.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("AddressBar.split1",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_split1.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("AddressBar.split2",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_split2.png"),
							Utility.toPixel(20), Utility.toPixel(20)));
			UIManager.put("Desktop.menu",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_menu.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("Tree.collapsedIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_tree_closed.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("Tree.expandedIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_tree_open.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("InternalFrame.iconifyIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_minimize.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("InternalFrame.closeIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_close.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("FlatTabbedPanel.closeAllIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_close.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("InternalFrame.maximizeIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_maximize.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("InternalFrame.minimizeIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_restore.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("InternalFrame.icon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_restore.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ComboBox.dropIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_tree_open.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("Spinner.downIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_down_arrow.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("Spinner.upIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_up_arrow.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("AddressBar.home",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_home_icon.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("SidePanel.addIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_add.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("SidePanel.collapseIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_left_arrow.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("SidePanel.expandIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_expand.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("ServerList.offlineIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_offline.png"),
							Utility.toPixel(32), Utility.toPixel(32)));

			UIManager.put("ServerList.searchIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_search_icon.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("ServerList.editIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_edit_icon.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("ServerList.deleteIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_delete.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("ServerTools.filesIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_files.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ServerTools.terminalIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_terminal.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ServerTools.editorIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_text_editor.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ServerTools.logViewIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_logview.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ServerTools.taskmgrIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_taskmgr.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ServerTools.fileshareIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_fileshare.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ServerTools.curlIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_curl.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ServerTools.findFilesIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_search_icon.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ServerTools.settingsIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_settings.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ExpandPanel.upIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_up.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("ExpandPanel.downIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_down.png"),
							Utility.toPixel(24), Utility.toPixel(24)));

			UIManager.put("TextEditor.saveIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_save.png"),
							Utility.toPixel(18), Utility.toPixel(18)));
			UIManager.put("TextEditor.findIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_find_in_page.png"),
							Utility.toPixel(18), Utility.toPixel(16)));
			UIManager.put("TextEditor.replaceIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_find_replace.png"),
							Utility.toPixel(18), Utility.toPixel(16)));
			UIManager.put("TextEditor.gotoLineIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_goto_line.png"),
							Utility.toPixel(18), Utility.toPixel(18)));
			UIManager.put("TextEditor.reloadIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_page_reload.png"),
							Utility.toPixel(18), Utility.toPixel(16)));

			UIManager.put("TextEditor.cutTextIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_cut_text.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("TextEditor.pasteTextIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_paste_text.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("TextEditor.copyTextIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_copy_text.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("FolderView.hideSideBarIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_hide_sidebar.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("FolderView.showSideBarIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_show_sidebar.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("ServerTools.terminalIcon16",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_terminal.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("ServerTools.editorIcon16",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_text_editor.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("ServerTools.filesIcon16",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_files.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("ServerTools.curlIcon16",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_curl.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("ServerTools.logViewIcon16",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_logview.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("ServerTools.findFilesIcon16",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_search_icon.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("ServerTools.taskmgrIcon16",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_taskmgr.png"),
							Utility.toPixel(16), Utility.toPixel(16)));

			UIManager.put("ViewMode.listIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_list_view.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("ViewMode.detailsIcon",
					new ScaledIcon(
							App.class.getResource(
									"/images/" + black + "_details_view.png"),
							Utility.toPixel(20), Utility.toPixel(20)));

//			UIManager.put("TextEditor.reloadIcon",
//					new ScaledIcon(
//							App.class.getResource(
//									"/images/" + black + "_page_reload.png"),
//							Utility.toPixel(20), Utility.toPixel(20)));

			UIManager.put("RTextArea.highlight", border);
			UIManager.put("Gutter.foreground", brightFg);
			UIManager.put("TabbedPane.background", background);
			UIManager.put("TabbedPane.foreground", foreground);
			UIManager.put("TabbedPane.highlight", background);
			UIManager.put("TabbedPane.borderHightlightColor", background);
			UIManager.put("TabbedPane.light", background);
			UIManager.put("TabbedPane.selected", background);
			UIManager.put("TabbedPane.selectHighlight", background);
			UIManager.put("TabbedPane.shadow", background);
			UIManager.put("TabbedPane.darkShadow", background);
			UIManager.put("TabbedPane.selectHighlight", background);
//			UIManager.put("TabbedPane.contentBorderInsets",
//					new Insets(Utility.toPixel(1), Utility.toPixel(1),
//							Utility.toPixel(1), Utility.toPixel(1)));
//			UIManager.put("TabbedPane.selectedTabPadInsets",
//					new Insets(0, 0, 0, 0));
//			UIManager.put("TabbedPane.tabAreaInsets", new Insets(0, 0, 0, 0));
//			UIManager.put("TabbedPane.tabInsets", new Insets(0, 0, 0, 0));
//					new Insets(Utility.toPixel(5), Utility.toPixel(5),
//							Utility.toPixel(5), Utility.toPixel(5)));
			UIManager.put("TabbedPane.selectHighlight", foreground);
			UIManager.put("TabbedPane.focus", background);
			UIManager.put("TabbedPane.flatHighlightBorder",
					new MatteBorder(Utility.toPixel(0), Utility.toPixel(0),
							Utility.toPixel(2), Utility.toPixel(0), selection));
			UIManager.put("TabbedPane.flatBorder",
					new MatteBorder(Utility.toPixel(0), Utility.toPixel(0),
							Utility.toPixel(2), Utility.toPixel(0),
							background));
			// UIManager.put("TabbedPane.tabsOverlapBorder", Boolean.TRUE);
			UIManager.put("TabbedPane.contentAreaColor", background);
			UIManager.put("TabbedPane.selectedLabelShift", 0);
			UIManager.put("TabbedPane.labelShift", 0);
			UIManager.put("TabbedPane.tabInsets", new Insets(0, 0, 0, 0));
			UIManager.put("TabbedPane.selectedTabPadInsets",
					new Insets(0, 0, 0, 0));
			UIManager.put("TabbedPane.tabAreaInsets", new Insets(0, 0, 0, 0));
			UIManager.put("TabbedPane.contentBorderInsets",
					new Insets(0, 0, 0, 0));
			UIManager.put("TabbedPane.tabsOverlapBorder", Boolean.FALSE);

			UIManager.put("Component.border",
					new LineBorder(border, Utility.toPixel(1)));

			UIManager.put("RounderBorder.color", border);

			UIManager.put("DefaultBorder.color", defBorder);
			UIManager.put("Panel.secondary", c1);
			UIManager.put("Panel.highlight", c2);
			UIManager.put("Panel.shadow", c3);

			UIManager.put("Taskbar.height", Utility.toPixel(32));
			UIManager.put("AddressBar.borderColor", border);
			UIManager.put("AddressBar.border", border);
			UIManager.put("AddressBar.background", Color.gray);
			UIManager.put("AddressBar.foreground", Color.black);
			UIManager.put("AddressBar.active", Color.blue);
			UIManager.put("AddressBar.hot", border);
			UIManager.put("AddressBar.activeForeground", Color.cyan);
			UIManager.put("AddressBar.textPaddingX", Utility.toPixel(10));
			UIManager.put("AddressBar.textPaddingY", Utility.toPixel(5));

			UIManager.put("Desktop.background", new Color(1, 12, 46));
			UIManager.put("Desktop.foreground", Color.WHITE);
			UIManager.put("Desktop.selectedForeground", Color.GRAY);
			UIManager.put("Desktop.selectedForeground", Color.GRAY);

			UIManager.put("ToggleButton.background", background);
			UIManager.put("ToggleButton.border",
					new LineBorder(border, Utility.toPixel(1)));
			UIManager.put("ToggleButton.foreground", Color.BLACK);
			UIManager.put("ToggleButton.select", selection);

			UIManager.put("Button.background", background);
			UIManager.put("Button.border", new FlatButtonBorder(border));
			UIManager.put("Button.rolloverBorder",
					new LineBorder(selection, Utility.toPixel(1)));
			UIManager.put("Button.rollover", Boolean.TRUE);
			UIManager.put("Button.highlight", selection);
			UIManager.put("Button.select", Color.BLUE);
			UIManager.put("Button.font", normalFont);
			UIManager.put("Button.foreground", foreground);
			UIManager.put("Button.margin",
					new Insets(Utility.toPixel(5), Utility.toPixel(5),
							Utility.toPixel(5), Utility.toPixel(5)));
			UIManager.put("Button.highlight", selection);
			UIManager.put("Button.darkShadow", selection.darker());

			UIManager.put("Label.font", normalFont);
			UIManager.put("Label.foreground", foreground);

			UIManager.put("Panel.background", background);

			UIManager.put("StartMenu.background", background);

			UIManager.put("TaskBar.background", background);
			UIManager.put("TaskBar.border",
					new LineBorder(new Color(20, 20, 20), Utility.toPixel(1)));
			UIManager.put("TaskBar.buttonBackground", border);

			UIManager.put("PopupMenu.border",
					new LineBorder(new Color(30, 30, 30), Utility.toPixel(1)));
			UIManager.put("PopupMenu.background", background);
			UIManager.put("PopupMenu.foreground", foreground);
			UIManager.put("PopupMenu.font", normalFont);

			UIManager.put("Popup.background", background);

			UIManager.put("Separator.background", defBorder);
			UIManager.put("Separator.foreground", defBorder);
			UIManager.put("Separator.thickness", Utility.toPixel(1));
			UIManager.put("Separator.insets",
					new Insets(Utility.toPixel(1), Utility.toPixel(1),
							Utility.toPixel(1), Utility.toPixel(1)));
			// UIManager.put("PopupMenu.font", normalFont);

			UIManager.put("MenuItem.border",
					new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
							Utility.toPixel(5), Utility.toPixel(5)));
			UIManager.put("MenuItem.background", background);
			UIManager.put("MenuItem.foreground", foreground);
			UIManager.put("MenuItem.font", normalFont);
			UIManager.put("MenuItem.selectionBackground", selection);
			UIManager.put("MenuItem.selectionForeground", foreground);
			UIManager.put("MenuItem.acceleratorForeground", border);
			UIManager.put("MenuItem.acceleratorSelectionForeground",
					foreground);

			UIManager.put("Menu.border",
					new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
							Utility.toPixel(5), Utility.toPixel(5)));
			UIManager.put("Menu.background", border);
			UIManager.put("Menu.foreground", foreground);
			UIManager.put("Menu.font", normalFont);
			UIManager.put("Menu.selectionBackground", selection);
			UIManager.put("Menu.selectionForeground", foreground);
			UIManager.put("Menu.acceleratorForeground", border);
			UIManager.put("Menu.acceleratorSelectionForeground", foreground);

			UIManager.put("MenuBar.border",
					new EmptyBorder(Utility.toPixel(0), Utility.toPixel(0),
							Utility.toPixel(0), Utility.toPixel(0)));
			UIManager.put("MenuBar.background", background);
			UIManager.put("MenuBar.borderColor", background);
			UIManager.put("MenuBar.foreground", foreground);
			UIManager.put("MenuBar.font", normalFont);
			UIManager.put("MenuBar.highlight", background);
			UIManager.put("MenuBar.darkShadow", background);

			UIManager.put("textHighlight", selection);
			UIManager.put("textHighlightText", selection);
			UIManager.put("TextField.background", background);
			UIManager.put("TextField.foreground", foreground);
			UIManager.put("TextField.inactiveForeground", foreground);
			UIManager.put("TextField.font", normalFont);
			UIManager.put("TextField.caretForeground", foreground);
			UIManager.put("TextField.border",
					new LineBorder(selection, Utility.toPixel(1)));
			UIManager.put("TextField.inactiveForeground", border);
			UIManager.put("TextField.selectionBackground", prgBg);
			UIManager.put("TextField.selectionForeground", foreground);

			UIManager.put("FormattedTextField.background", background);
			UIManager.put("FormattedTextField.foreground", foreground);
			UIManager.put("FormattedTextField.font", normalFont);
			UIManager.put("FormattedTextField.caretForeground", foreground);
//			UIManager.put("FormattedTextField.border",
//					new LineBorder(selection, Utility.toPixel(1)));
			UIManager.put("FormattedTextField.inactiveForeground", border);
			UIManager.put("FormattedTextField.selectionBackground", prgBg);
			UIManager.put("FormattedTextField.selectionForeground", foreground);

			UIManager.put("TextArea.background", background);
			UIManager.put("TextArea.foreground", foreground);
			UIManager.put("TextArea.font", normalFont);
			UIManager.put("TextArea.caretForeground", foreground);
			UIManager.put("TextArea.border",
					new LineBorder(background, Utility.toPixel(1)));
			UIManager.put("TextArea.selectionBackground", prgBg);
			UIManager.put("TextArea.selectionForeground", foreground);
			UIManager.put("TextPane.selectionForeground", foreground);
			UIManager.put("EditorPane.selectionBackground", prgBg);
			UIManager.put("TextComponent.selectionBackground", prgBg);
//			UIManager.put("nimbusSelectionBackground", selection);
//			UIManager.put("nimbusSelectedText", selection);

			UIManager.put("CheckBox.background", background);
			UIManager.put("CheckBox.foreground", foreground);
			UIManager.put("CheckBox.font", normalFont);
			UIManager.put("Checkbox.select", selection);
			UIManager.put("CheckBox.border",
					new LineBorder(selection, Utility.toPixel(1)));
			UIManager.put("TextArea.selectionBackground", prgBg);

			UIManager.put("RadioButton.background", background);
			UIManager.put("RadioButton.foreground", foreground);
			UIManager.put("RadioButton.font", normalFont);
			UIManager.put("RadioButton.select", selection);
			UIManager.put("RadioButton.border",
					new LineBorder(selection, Utility.toPixel(1)));

			UIManager.put("TextArea.selectionBackground", prgBg);

			UIManager.put("Spinner.background", background);
			UIManager.put("Spinner.foreground", foreground);
			UIManager.put("Spinner.arrowBackground", border);
			UIManager.put("Spinner.font", normalFont);
			UIManager.put("Spinner.select", selection);
			UIManager.put("Spinner.border",
					new LineBorder(selection, Utility.toPixel(1)));
			UIManager.put("Spinner.editorBorderPainted", Boolean.FALSE);
			UIManager.put("Spinner.arrowButtonInsets",
					new Insets(Utility.toPixel(1), Utility.toPixel(1),
							Utility.toPixel(1), Utility.toPixel(1)));

			UIManager.put("Spinner.selectionBackground", prgBg);

			UIManager.put("Table.background", c1);
			UIManager.put("Table.border",
					new LineBorder(selection, Utility.toPixel(1)));
			UIManager.put("Table.shadow", background);
			UIManager.put("Table.darkShadow", background);
			UIManager.put("Table.gridColor", background);
			UIManager.put("Table.selectionBackground", selection);
			UIManager.put("Table.selectionForeground", foreground);
			UIManager.put("Table.focusCellBackground", selection);
			UIManager.put("Table.focusCellHighlightBorder",
					new EmptyBorder(new Insets(0, 0, 0, 0)));
			// UIManager.put("Table.background", background);
			UIManager.put("Table.foreground", foreground);
			UIManager.put("Table.rendererUseTableColors", Boolean.TRUE);
			UIManager.put("Table.scrollPaneBorder",
					new LineBorder(background, Utility.toPixel(0)));

			UIManager.put("TableHeader.background", background);
			UIManager.put("TableHeader.foreground", foreground);

			MatteBorder mb = new MatteBorder(0, 0, Utility.toPixel(1),
					Utility.toPixel(1), border);

			UIManager.put("TableHeader.cellBorder", mb);

//			UIManager.put("Tree.gridColor", background);
//			UIManager.put("Tree.gridColor", background);
//			UIManager.put("Tree.gridColor", background);
//			UIManager.put("Tree.gridColor", background);

			UIManager.put("SplitPane.border",
					new LineBorder(background, Utility.toPixel(1)));
			UIManager.put("SplitPane.dividerSize", Utility.toPixel(1));
			UIManager.put("SplitPaneDivider.border",
					new LineBorder(background, Utility.toPixel(1)));
			UIManager.put("SplitPane.background", background);

			UIManager.put("LineGraph.foreground", foreground);
			UIManager.put("LineGraph.background", background);
			UIManager.put("LineGraph.gridColor", border);
			UIManager.put("LineGraph.lineColor", selection);

			UIManager.put("ScrollBar.background", background);
			UIManager.put("ScrollBar.squareButtons", false);
			UIManager.put("ScrollBar.thumb", selection);
			UIManager.put("ScrollBar.foreground", selection);
			UIManager.put("ScrollBar.highlight", selection);
			UIManager.put("ScrollBar.thumbRollover", c2);
			UIManager.put("ScrollBar.gradient", null);
			UIManager.put("ScrollBar.border",
					new EmptyBorder(new Insets(0, 0, 0, 0)));
			UIManager.put("ScrollBar.width", Utility.toPixel(8));

			UIManager.put("ScrollPane.viewportBorder",
					new LineBorder(background, Utility.toPixel(1)));
			UIManager.put("ScrollPane.viewportBorderInsets",
					new Insets(0, 0, 0, 0));
			UIManager.put("ScrollPane.border",
					new LineBorder(border, Utility.toPixel(1)));
			UIManager.put("ScrollPane.background", background);
			UIManager.put("ScrollPane.border",
					new EmptyBorder(new Insets(0, 0, 0, 0)));

			UIManager.put("Viewport.border",
					new EmptyBorder(new Insets(0, 0, 0, 0)));
			UIManager.put("Viewport.background", background);

			UIManager.put("ComboBox.background", background);
			UIManager.put("ComboBox.foreground", foreground);
			UIManager.put("ComboBox.border",
					new LineBorder(border, Utility.toPixel(1)));
//			UIManager.put("ComboBox.buttonBackground", background);
//			UIManager.put("ComboBox.buttonDarkShadow", background);
//			UIManager.put("ComboBox.buttonHighlight", background);
//			UIManager.put("ComboBox.buttonShadow", background);
			UIManager.put("ComboBox.control", background);
			UIManager.put("ComboBox.controlForeground", foreground);
			UIManager.put("ComboBox.selectionBackground", selection);
			UIManager.put("ComboBox.selectionForeground", foreground);
			UIManager.put("ComboBox.font", normalFont);

			UIManager.put("InternalFrame.border",
					new LineBorder(background, Utility.toPixel(5)));
			UIManager.put("InternalFrame.activeBorder",
					new LineBorder(titleColor, Utility.toPixel(5)));
			UIManager.put("InternalFrame.activeBorderColor", titleColor);
			UIManager.put("InternalFrame.borderColor", border);
			UIManager.put("InternalFrame.activeTitleBackground", titleColor);
			UIManager.put("InternalFrame.inactiveTitleBackground", background);
			UIManager.put("InternalFrame.inactiveTitleForeground", foreground);
			UIManager.put("InternalFrame.activeTitleForeground", foreground);
			UIManager.put("InternalFrame.activeTitleBackground", titleColor);
			UIManager.put("InternalFrame.titlePaneHeight", Utility.toPixel(24));
			UIManager.put("InternalFrame.titleButtonHeight",
					Utility.toPixel(24));
			UIManager.put("InternalFrame.titleButtonWidth",
					Utility.toPixel(24));
			UIManager.put("InternalFrameTitlePane.maximizeButtonOpacity",
					Boolean.TRUE);
			UIManager.put("InternalFrameTitlePane.closeButtonOpacity",
					Boolean.TRUE);
			UIManager.put("InternalFrameTitlePane.iconifyButtonOpacity",
					Boolean.TRUE);
			UIManager.put("InternalFrame.minBackground", Color.DARK_GRAY);
			UIManager.put("InternalFrame.maxBackground", Color.DARK_GRAY);
			UIManager.put("InternalFrame.closeBackground", Color.DARK_GRAY);// Color.RED);

			UIManager.put("FlatTabbedPane.highlight", border);
			UIManager.put("FlatTabbedPane.background", background);
			UIManager.put("FlatTabbedPane.closeIcon", tabCloseIcon);
			UIManager.put("FlatTabbedPane.blankIcon", tabBlankIcon);

//			Icon smallFolder = new ScaledIcon(
//					App.class.getResource("/images/blue_folder.png"),
//					Utility.toPixel(20), Utility.toPixel(20));

			Icon smallFolder = new ScaledIcon(
					App.class.getResource("/images/folder.png"),
					Utility.toPixel(20), Utility.toPixel(20));

			Icon smallFile = new ScaledIcon(
					App.class.getResource("/images/fileicon.png"),
					Utility.toPixel(20), Utility.toPixel(20));

			UIManager.put("ListView.smallFolder", smallFolder);
			UIManager.put("ListView.smallFile", smallFile);
			UIManager.put("ListView.smallFile", smallFile);

			UIManager.put("Tree.background", background);
			UIManager.put("Tree.font", normalFont);
			UIManager.put("Tree.textBackground", background);
			UIManager.put("Tree.textForeground", foreground);
			UIManager.put("Tree.selectionBackground", selection);
			UIManager.put("Tree.selectionForeground", foreground);
			UIManager.put("Tree.closedIcon", smallFolder);
			UIManager.put("Tree.openIcon", smallFolder);
			UIManager.put("Tree.line", selection);
			UIManager.put("Tree.drawDashedFocusIndicator", Boolean.FALSE);
			UIManager.put("Tree.selectionBorderColor", selection);
			UIManager.put("Tree.leafIcon", smallFile);
			UIManager.put("Tree.rowHeight", Utility.toPixel(30));
			UIManager.put("Tree.drawVerticalLines", Boolean.FALSE);
			UIManager.put("Tree.drawHorizontalLines", Boolean.FALSE);
			UIManager.put("Tree.paintLines", Boolean.FALSE);
			UIManager.put("Tree.selectionBackground", selection);
			UIManager.put("Tree.iconShadow", selection);
			UIManager.put("Tree.iconHighlight", background);
			UIManager.put("Tree.iconBackground", selection);
			UIManager.put("Tree.rendererUseTreeColors", Boolean.TRUE);
			UIManager.put("Tree.background", background);
			UIManager.put("Tree.padding", Integer.valueOf(20));
			UIManager.put("Tree.leftChildIndent",
					Integer.valueOf(Utility.toPixel(15)));
			UIManager.put("Tree.rightChildIndent",
					Integer.valueOf(Utility.toPixel(5)));
			// UIManager.put("Tree.expanderSize", Integer.valueOf(30));

			UIManager.put("List.background", background);
			UIManager.put("List.foreground", foreground);
			UIManager.put("List.selectionBackground", selection);
			UIManager.put("List.selectionForeground", foreground);
			UIManager.put("List.rendererUseUIBorder", Boolean.TRUE);
			UIManager.put("List.rendererUseListColors", Boolean.TRUE);
			UIManager.put("List.focusCellHighlightBorder",
					new EmptyBorder(0, 0, 0, 0));
			UIManager.put("List.border", new EmptyBorder(0, 0, 0, 0));

			UIManager.put("Terminal.background", background);
			UIManager.put("Terminal.foreground", foreground);
			UIManager.put("Terminal.font", normalFont);
			UIManager.put("Terminal.selectionBackground", selection);

			UIManager.put("FormattedTextField.background", background);
			UIManager.put("FormattedTextField.foreground", foreground);
			UIManager.put("FormattedTextField.font", normalFont);
			UIManager.put("FormattedTextField.selectionBackground", selection);

			UIManager.put("control", background);
			UIManager.put("controlShadow", background);
			// UIManager.put("controlDkShadow", background);
			UIManager.put("controlLtHighlight", background);

			UIManager.put("PasswordField.background", background);
			UIManager.put("PasswordField.foreground", foreground);
			UIManager.put("PasswordField.caretForeground", foreground);
			UIManager.put("PasswordField.font", normalFont);
			UIManager.put("PasswordField.selectionBackground", selection);
			UIManager.put("PasswordField.border",
					new LineBorder(selection, Utility.toPixel(1)));

			UIManager.put("ProgressBar.background", defBorder);
			UIManager.put("ProgressBar.foreground", prgBg);
			UIManager.put("ProgressBar.border", new EmptyBorder(0, 0, 0, 0));
			UIManager.put("ProgressBar.horizontalSize",
					new Dimension(Utility.toPixel(146), Utility.toPixel(8)));
			UIManager.put("ProgressBar.verticalSize",
					new Dimension(Utility.toPixel(8), Utility.toPixel(146)));

			UIManager.put("OptionPane.background", background);
			UIManager.put("OptionPane.messageForeground", foreground);
			UIManager.put("OptionPane.foreground", foreground);
			UIManager.put("OptionPane.border",
					new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
							Utility.toPixel(5), Utility.toPixel(5)));

			UIManager.put("TableHeader.cellBorder", new CompoundBorder(
					new MatteBorder(0, 0, 0, Utility.toPixel(1),
							UIManager.getColor("DefaultBorder.color")),
					new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
							Utility.toPixel(5), Utility.toPixel(5))));

			UIManager.put("ToolTip.background",
					UIManager.getColor("DefaultBorder.color"));
			UIManager.put("ToolTip.backgroundInactive", background);
			UIManager.put("ToolTip.border",
					new LineBorder(UIManager.getColor("DefaultBorder.color"),
							Utility.toPixel(1)));
			UIManager.put("ToolTip.borderInactive",
					new LineBorder(UIManager.getColor("DefaultBorder.color"),
							Utility.toPixel(1)));
			UIManager.put("ToolTip.foreground", foreground);
			UIManager.put("ToolTip.foregroundInactive", foreground);

			UIManager.put("CheckBoxMenuItem.border",
					new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
							Utility.toPixel(5), Utility.toPixel(5)));
			UIManager.put("CheckBoxMenuItem.background", background);
			UIManager.put("CheckBoxMenuItem.foreground", foreground);
			UIManager.put("CheckBoxMenuItem.font", normalFont);
			UIManager.put("CheckBoxMenuItem.selectionBackground", selection);
			UIManager.put("CheckBoxMenuItem.selectionForeground", foreground);
			UIManager.put("CheckBoxMenuItem.acceleratorForeground", border);
			UIManager.put("CheckBoxMenuItem.acceleratorSelectionForeground",
					foreground);

			UIManager.put("RadioButtonMenuItem.border",
					new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
							Utility.toPixel(5), Utility.toPixel(5)));
			UIManager.put("RadioButtonMenuItem.background", background);
			UIManager.put("RadioButtonMenuItem.foreground", foreground);
			UIManager.put("RadioButtonMenuItem.font", normalFont);
			UIManager.put("RadioButtonMenuItem.selectionBackground", selection);
			UIManager.put("RadioButtonMenuItem.selectionForeground",
					background);
			UIManager.put("RadioButtonMenuItem.acceleratorForeground", border);
			UIManager.put("RadioButtonMenuItem.acceleratorSelectionForeground",
					foreground);

			UIManager.put("Editor.theme", "default");

			// MetalInternalFrameUI

			// MetalTreeUI
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the theme
	 */
	public static synchronized String getTheme() {
		return theme;
	}

	/**
	 * @param theme the theme to set
	 */
	public static synchronized void setTheme(String theme) {
		App.theme = theme;
	}

}

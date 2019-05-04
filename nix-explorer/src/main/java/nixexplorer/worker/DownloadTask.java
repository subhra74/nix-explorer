package nixexplorer.worker;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.jcraft.jsch.ChannelSftp;

import nixexplorer.PathUtils;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshFileSystemWrapper;
import nixexplorer.widgets.util.Utility;

public class DownloadTask implements Runnable {
	private String remoteFile, localTempFile;
	private String localTempFolder;
	private JDialog dlg;
	private AtomicBoolean stopFlag = new AtomicBoolean(false);
	private SshFileSystemWrapper fs;
	private ChangeWatcher changeWatcher;
	private AppSession appSession;

	/**
	 * 
	 */
	public DownloadTask(String remoteFile, SessionInfo info,
			AppSession appSession, ChangeWatcher changeWatcher) {
		this.fs = new SshFileSystemWrapper(info);
		this.remoteFile = remoteFile;
		this.appSession = appSession;
		this.changeWatcher = changeWatcher;
		new Thread(this).start();
	}

	private void init() {
		try {
			Path dir = Files.createTempDirectory(UUID.randomUUID().toString());
			this.localTempFolder = dir.toString();
			this.localTempFile = dir.resolve(PathUtils.getFileName(remoteFile))
					.toAbsolutePath().toString();
		} catch (IOException e) {
			e.printStackTrace();
			if (!stopFlag.get()) {
				JOptionPane.showMessageDialog(appSession.getWindow(),
						"Unable to create temp file");
			}
		}

	}

	@Override
	public void run() {

		if (Thread.currentThread().isInterrupted()) {
			cancelTask();
		}

		SwingUtilities.invokeLater(() -> {
			dlg = createAndShowDialog();
		});

		init();

		if (Thread.currentThread().isInterrupted()) {
			cancelTask();
		}
		while (!stopFlag.get()) {
			if (downloadFile()) {
				openDefaultApp(localTempFile);
				changeWatcher.watchForChanges(createWatcherEntry());
				break;
			}
			if (JOptionPane.showConfirmDialog(appSession.getWindow(),
					"Retry") == JOptionPane.CANCEL_OPTION) {
				break;
			}
		}

		SwingUtilities.invokeLater(() -> {
			dlg.dispose();
		});
	}

	private boolean downloadFile() {
		ChannelSftp sftp = null;
		try {
			if (stopFlag.get()) {
				cancelTask();
			}
			sftp = fs.getSftp();
			sftp.get(remoteFile, localTempFile, null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (sftp != null) {
				try {
					sftp.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private JDialog createAndShowDialog() {
		JDialog dlg = new JDialog(this.appSession.getWindow());
		dlg.setSize(Utility.toPixel(200), Utility.toPixel(70));
		dlg.setLocationRelativeTo(this.appSession.getWindow());
		dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dlg.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				cancelTask();
			}
		});
		dlg.setVisible(true);
		return dlg;
	}

	public void cancelTask() {
		stopFlag.set(true);
		try {
			fs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private WatcherEntry createWatcherEntry() {
		return new WatcherEntry(remoteFile, localTempFile, localTempFolder, 0,
				PathUtils.getFileName(remoteFile));
	}

	public void openDefaultApp(String file) {
		String os = System.getProperty("os.name").toLowerCase();
		System.out.println("Operating system: " + os);
		if (os.contains("linux")) {
			openDefaultAppLinux(file);
		} else if (os.contains("mac") || os.contains("darwin")
				|| os.contains("os x")) {
			openDefaultAppOSX(file);
		} else if (os.contains("windows")) {
			openDefaultAppWin(file);
		}
	}

	public void openDefaultAppLinux(String file) {
		try {
			System.out.println("Opening linux app");
			ProcessBuilder pb = new ProcessBuilder();
			pb.command("xdg-open", file);
			pb.start();// .waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openDefaultAppWin(String file) {
		try {
			ProcessBuilder builder = new ProcessBuilder();
			List<String> lst = new ArrayList<String>();
			lst.add("rundll32");
			lst.add("url.dll,FileProtocolHandler");
			lst.add(file);
			builder.command(lst);
			System.out.println("Exit code: " + builder.start().waitFor());

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void openDefaultAppOSX(String file) {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command("open", file);
			pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

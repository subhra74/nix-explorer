package nixexplorer.worker;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpProgressMonitor;

import nixexplorer.PathUtils;
import nixexplorer.ProcessUtils;
import nixexplorer.app.AppContext;
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
	private JProgressBar prg;
	private JLabel lblText;

	public enum OpenMode {
		External, Default
	}

	private OpenMode openMode;

	public DownloadTask(String remoteFile, SessionInfo info,
			AppSession appSession, ChangeWatcher changeWatcher,
			OpenMode openMode) {
		this.fs = new SshFileSystemWrapper(info);
		this.openMode = openMode;
		this.remoteFile = remoteFile;
		this.appSession = appSession;
		this.changeWatcher = changeWatcher;
		prg = new JProgressBar();
		lblText = new JLabel(
				info.getName() + ":" + PathUtils.getFileName(remoteFile));
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
				if (this.openMode == OpenMode.Default) {
					ProcessUtils.openDefaultApp(localTempFile);
				} else {
					ProcessUtils.openExternalApp(localTempFile);
				}

				changeWatcher.watchForChanges(createWatcherEntry());
				break;
			}
			if (stopFlag.get()) {
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
			download(sftp, remoteFile, localTempFile);
			// sftp.get(remoteFile, localTempFile, new PrgMonitor(prg));
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
		dlg.setTitle("Downloading");
		dlg.setSize(Utility.toPixel(200), Utility.toPixel(100));
		dlg.setLocationRelativeTo(this.appSession.getWindow());
		dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		Box box = Box.createVerticalBox();
		box.setBorder(new EmptyBorder(Utility.toPixel(10), Utility.toPixel(10),
				Utility.toPixel(10), Utility.toPixel(10)));
		prg.setAlignmentX(Box.LEFT_ALIGNMENT);
		lblText.setAlignmentX(Box.LEFT_ALIGNMENT);
		box.add(Box.createVerticalGlue());
		box.add(lblText);
		box.add(Box.createVerticalStrut(Utility.toPixel(10)));
		box.add(prg);
		box.add(Box.createVerticalGlue());
		dlg.add(box);
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

	

	private void download(ChannelSftp sftp, String source, String dest)
			throws Exception {
		byte[] b = new byte[8192];
		long len = sftp.stat(source).getSize();
		long tot = 0;
		InputStream in = sftp.get(source);
		OutputStream out = new FileOutputStream(dest);
		while (!stopFlag.get()) {
			int x = in.read(b);
			if (x == -1)
				break;
			out.write(b, 0, x);
			tot += x;
			prg.setValue((int) ((tot * 100) / len));
		}
		in.close();
		out.close();
	}

}

/**
 * 
 */
package nixexplorer.worker;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
import com.jcraft.jsch.SftpATTRS;

import nixexplorer.App;
import nixexplorer.PathUtils;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshFileSystemWrapper;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class UploadTask implements Runnable {
	private WatcherEntry ent;
	private AtomicBoolean stopFlag = new AtomicBoolean(false);
	private SshFileSystemWrapper fs;
	private JDialog dlg;
	private ChangeWatcher changeWatcher;
	private boolean useTempFile = true;
	private AppSession appSession;
	private JProgressBar prg;
	private JLabel lblText;

	/**
	 * 
	 */
	public UploadTask(SessionInfo info, WatcherEntry ent, AppSession appSession,
			ChangeWatcher changeWatcher) {
		this.ent = ent;
		this.appSession = appSession;
		this.changeWatcher = changeWatcher;
		prg = new JProgressBar();
		lblText = new JLabel(info.getName() + ":" + PathUtils
				.getFileName(PathUtils.getFileName(ent.getRemoteFile())));
		this.fs = new SshFileSystemWrapper(info);
		new Thread(this).start();
	}

	@Override
	public void run() {
		if (Thread.currentThread().isInterrupted()) {
			cancelTask();
		}
		SwingUtilities.invokeLater(() -> {
			dlg = createAndShowDialog();
		});
		if (Thread.currentThread().isInterrupted()) {
			cancelTask();
		}
		while (!stopFlag.get()) {
			if (uploadFile()) {
				changeWatcher.uploadComplete(ent.getRemoteFile(), this);
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
		try {
			fs.close();
		} catch (Exception e) {
		}
		SwingUtilities.invokeLater(() -> {
			dlg.dispose();
		});
	}

	private boolean uploadFile() {
		ChannelSftp sftp = null;
		try {
			if (Thread.currentThread().isInterrupted()) {
				cancelTask();
			}

			String targetFile = useTempFile ? generateTempFile()
					: this.ent.getRemoteFile();
			sftp = fs.getSftp();
			upload(sftp, ent.getLocalTempFile(), targetFile);
			if (useTempFile) {
				SftpATTRS attrs = sftp.stat(targetFile);
				sftp.rm(this.ent.getRemoteFile());
				sftp.rename(targetFile, this.ent.getRemoteFile());
				sftp.setStat(this.ent.getRemoteFile(), attrs);
			}
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

	private String generateTempFile() {
		String tmpFile = UUID.randomUUID().toString();
		String folder = PathUtils.getParent(this.ent.getRemoteFile());
		String tmpFullPath = PathUtils.combineUnix(folder, tmpFile);
		return tmpFullPath;
	}

	/**
	 * @return the ent
	 */
	public WatcherEntry getEnt() {
		return ent;
	}

	/**
	 * @param ent the ent to set
	 */
	public void setEnt(WatcherEntry ent) {
		this.ent = ent;
	}

	public void cancel() {

	}

	public void cancelTask() {
		System.out.println("Cancelling task");
		stopFlag.set(true);
		try {
			fs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JDialog createAndShowDialog() {
		JDialog dlg = new JDialog(this.appSession.getWindow());
		dlg.setIconImage(App.getAppIcon());
		dlg.setTitle("Uploading");
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

	private void upload(ChannelSftp sftp, String source, String dest)
			throws Exception {
		byte[] b = new byte[8192];
		File f = new File(source);
		long len = f.length();
		long tot = 0;
		InputStream in = new FileInputStream(f);
		OutputStream out = sftp.put(dest);
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

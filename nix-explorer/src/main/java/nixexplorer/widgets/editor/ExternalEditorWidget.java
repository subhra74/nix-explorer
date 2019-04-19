/**
 * 
 */
package nixexplorer.widgets.editor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpProgressMonitor;

import nixexplorer.App;
import nixexplorer.PathUtils;
import nixexplorer.app.components.CredentialsDialog;
import nixexplorer.app.components.DisposableView;
import nixexplorer.app.components.CredentialsDialog.Credentials;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.widgets.Widget;
import nixexplorer.widgets.util.Utility;
import nixexplorer.worker.editwatcher.ChangeUploader;

/**
 * @author subhro
 *
 */
public class ExternalEditorWidget extends JDialog
		implements ChangeUploader, Runnable, IProgress, DisposableView {

	/**
	 * @param env
	 * @param args
	 * @param parent
	 */
	private JLabel lblTitle;
	private byte[] b = new byte[8192];
	private JProgressBar prg;
	private long totalSize;
	private long processedSize;
	private Thread t;
	private Object lock = new Object();
	private SshWrapper wrapper = null;
	private String file;
	private String remoteFile;
	private AtomicBoolean fileChanged;
	private long lastModified;
	private ExecutorService threadPool;
	private Future<Boolean> callback;
	private JPanel panelWaiting;
	private JLabel lblError, lblWaiting;
	private boolean downloadFinished = false;
	private JPanel mainPanel;
	private SessionInfo info;
	private AppSession appSession;
	private AtomicBoolean stopFlag = new AtomicBoolean(false);
	protected AtomicBoolean widgetClosed = new AtomicBoolean(Boolean.FALSE);
	private WatchKey watchKey;

	enum Mode {
		OpenWithEditor, OpenWithDefApp
	}

	private Mode mode;

	public ExternalEditorWidget(SessionInfo info, String[] args,
			AppSession appSession, Window window) {
		super(window);
		System.out.println("External editor");
		this.info = info;
		this.appSession = appSession;
		setTitle("External edit...");
		setSize(Utility.toPixel(300), Utility.toPixel(150));
		setLocationRelativeTo(null);
		setPreferredSize(getSize());
		setResizable(true);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeWindow();
			}
		});

		if (args.length != 2) {
			throw new RuntimeException("Invalid number of arguments");
		}

		mode = args[0].equals("-e") ? Mode.OpenWithEditor : Mode.OpenWithDefApp;
		this.remoteFile = args[1];

		createUI();

		threadPool = Executors.newFixedThreadPool(1);
		threadPool.submit(this);
	}

	/**
	 * 
	 */
	protected void closeWindow() {
		stopFlag.set(true);
		if (this.watchKey != null) {
			appSession.unregisterWatcher(watchKey);
		}
		dispose();
		new Thread(() -> {
			try {
				if (wrapper != null) {
					System.out.println("disconnect file watcher");
					wrapper.disconnect();
				}
			} catch (Exception e1) {
			}
		}).start();
	}

	private void createUI() {
		this.setLayout(new BorderLayout());
		Box box1 = Box.createVerticalBox();

		lblTitle = new JLabel("Initializing...");
		prg = new JProgressBar();

		lblTitle.setAlignmentX(Box.LEFT_ALIGNMENT);
		prg.setAlignmentX(Box.LEFT_ALIGNMENT);

		box1.add(lblTitle);
		box1.add(Box.createVerticalStrut(Utility.toPixel(10)));
		box1.add(prg);
		box1.add(Box.createVerticalGlue());
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(new EmptyBorder(Utility.toPixel(10),
				Utility.toPixel(10), Utility.toPixel(10), Utility.toPixel(10)));
		mainPanel.add(box1);
		// add(box1);

		panelWaiting = new JPanel();
		lblWaiting = new JLabel("Monitoring changes in file");
		panelWaiting.add(lblWaiting);

	}

	private void retry() {
		if (stopFlag.get()) {
			return;
		}
		if (downloadFinished) {
			startNewUpload();
		} else {
			threadPool.submit(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.worker.editwatcher.ChangeUploader#getLastModified()
	 */
	@Override
	public long getLastModified() {
		return lastModified;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.worker.editwatcher.ChangeUploader#onFileChanged()
	 */
	@Override
	public void onFileChanged(long lastModified) {
		this.lastModified = lastModified;
		cancelCurrentUploadIfAny();
		startNewUpload();
	}

	/**
	 * 
	 */
	private void startNewUpload() {
		System.out.println("Starting new upload...");
		SwingUtilities.invokeLater(() -> {
			ExternalEditorWidget.this.getContentPane().removeAll();
			ExternalEditorWidget.this.add(mainPanel);
			ExternalEditorWidget.this.revalidate();
			ExternalEditorWidget.this.repaint();
		});
		UploadTask task = new UploadTask(wrapper, remoteFile, file, this);
		callback = threadPool.submit(task);
	}

	/**
	 * 
	 */
	private void cancelCurrentUploadIfAny() {
		if (callback != null) {
			callback.cancel(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		downloadFile();
	}

	private void connectWrapper() throws Exception {
		if (wrapper == null || !wrapper.isConnected()) {
			while (!stopFlag.get()) {

				try {
					wrapper = new SshWrapper(info);
					wrapper.connect();
					return;
				} catch (Exception e) {
					e.printStackTrace();
					if (!stopFlag.get()) {
						if (JOptionPane.showConfirmDialog(null,
								"Unable to connect to server. Retry?") != JOptionPane.YES_OPTION) {
							throw new Exception("User cancelled the operation");
						}
					}
				}
			}
		}
	}

	private void downloadFile() {
		SwingUtilities.invokeLater(() -> {
			ExternalEditorWidget.this.getContentPane().removeAll();
			ExternalEditorWidget.this.add(mainPanel);
			ExternalEditorWidget.this.revalidate();
			ExternalEditorWidget.this.repaint();
		});
		try {
			Path dir = Files.createTempDirectory(UUID.randomUUID().toString());// App.getConfig("temp.dir")
																				// +
																				// File.separatorChar
//			// + info.hashCode() + File.separatorChar + UUID.randomUUID();
//			try {
//				Files.createDirectories(dir);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			this.file = dir.resolve(PathUtils.getFileName(remoteFile)) // Paths.get(dir.,
																		// PathUtils.getFileName(remoteFile))
					.toAbsolutePath().toString();
			connectWrapper();
			wrapper.getSftpChannel().get(remoteFile, file,
					new SftpProgressMonitor() {
						long total = 0L, received = 0L;
						int progress = 0;

						@Override
						public void init(int op, String src, String dest,
								long max) {
							this.total = max;
							SwingUtilities.invokeLater(() -> {
								prg.setValue(progress);
							});
						}

						@Override
						public void end() {

						}

						@Override
						public boolean count(long count) {
							received += count;
							int progress1 = (int) ((received * 100) / total);
							if (progress1 > progress) {
								progress = progress1;
								SwingUtilities.invokeLater(() -> {
									prg.setValue(progress);
								});
							}

							return true;
						}
					});
			this.lastModified = Files.getLastModifiedTime(Paths.get(file))
					.toMillis();
			this.watchKey = appSession.registerEditWatchers(file, this);
			openDefaultApp(file);
			SwingUtilities.invokeLater(() -> {
				ExternalEditorWidget.this.getContentPane().removeAll();
				ExternalEditorWidget.this.add(panelWaiting);
				ExternalEditorWidget.this.revalidate();
				ExternalEditorWidget.this.repaint();
			});
		} catch (Exception e) {
			e.printStackTrace();
			if (!stopFlag.get()) {
				if (JOptionPane.showConfirmDialog(this,
						"Operation failed, retry?", "Error",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					retry();
				}
			}
//			SwingUtilities.invokeLater(() -> {
//				ExternalEditorWidget.this.getContentPane().removeAll();
//				ExternalEditorWidget.this.add(panelError);
//				ExternalEditorWidget.this.revalidate();
//				ExternalEditorWidget.this.repaint();
//			});
		}

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
			builder.start();
		} catch (IOException e) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.widgets.editor.IProgress#update(int)
	 */
	@Override
	public void update(int prg) {
		this.prg.setValue(prg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.widgets.editor.IProgress#failed()
	 */
	@Override
	public void failed() {
		if (!stopFlag.get()) {
			if (JOptionPane.showConfirmDialog(this, "Operation failed, retry?",
					"Error",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				retry();
			}
		}
//		SwingUtilities.invokeLater(() -> {
//			ExternalEditorWidget.this.getContentPane().removeAll();
//			ExternalEditorWidget.this.add(panelError);
//			ExternalEditorWidget.this.revalidate();
//			ExternalEditorWidget.this.repaint();
//		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.widgets.editor.IProgress#done()
	 */
	@Override
	public void done() {
		SwingUtilities.invokeLater(() -> {
			ExternalEditorWidget.this.getContentPane().removeAll();
			ExternalEditorWidget.this.add(panelWaiting);
			ExternalEditorWidget.this.revalidate();
			ExternalEditorWidget.this.repaint();
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.DisposableView#viewClosing()
	 */
	@Override
	public boolean viewClosing() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.DisposableView#viewClosed()
	 */
	@Override
	public void viewClosed() {

	}

	@Override
	public boolean getWidgetClosed() {
		return widgetClosed.get();
	}

	@Override
	public void setWidgetClosed(boolean widgetClosed) {
		this.widgetClosed.set(widgetClosed);
	}

	@Override
	public boolean closeView() {
		closeWindow();
		return true;
	}

	@Override
	public String getPathHost() {
		return this.info.getHost() + ":" + this.remoteFile;
	}
}

interface IProgress {
	public void update(int prg);

	public void failed();

	public void done();
}

class UploadTask implements Callable<Boolean> {
	private SshWrapper wrapper;
	private String remote, local;
	private byte[] b = new byte[8192];
	private IProgress prg;

	/**
	 * @param wrapper
	 * @param remote
	 * @param local
	 */
	public UploadTask(SshWrapper wrapper, String remote, String local,
			IProgress prg) {
		super();
		this.wrapper = wrapper;
		this.remote = remote;
		this.local = local;
		this.prg = prg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Boolean call() {
		InputStream in = null;
		OutputStream out = null;
		ChannelSftp sftp = null;
		try {
			if (!wrapper.isConnected()) {
				wrapper = new SshWrapper(wrapper.getInfo());
				wrapper.connect();
			}
			sftp = wrapper.getSftpChannel();
			in = new FileInputStream(local);
			long total = in.available();
			long processed = 0;
			out = sftp.put(remote, ChannelSftp.OVERWRITE);
			while (true) {
				if (Thread.currentThread().isInterrupted()) {
					System.out.println("upload interrupted");
					break;
				}
				int x = in.read(b);
				if (x == -1) {
					break;
				}
				processed += x;
				int progress = (int) ((processed * 100) / total);
				out.write(b, 0, x);
				SwingUtilities.invokeLater(() -> {
					prg.update(progress);
				});
			}
			SwingUtilities.invokeLater(() -> {
				prg.done();
			});
			return Boolean.TRUE;
		} catch (Exception e) {
			e.printStackTrace();
			SwingUtilities.invokeLater(() -> {
				prg.failed();
			});
			return Boolean.FALSE;
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				out.close();
			} catch (Exception e) {
				// TODO: handle exception
			}

			try {
				sftp.disconnect();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}

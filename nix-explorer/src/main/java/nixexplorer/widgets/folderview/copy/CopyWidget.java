/**
 * 
 */
package nixexplorer.widgets.folderview.copy;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.jcraft.jsch.ChannelSftp;

import nixexplorer.PathUtils;
import nixexplorer.TextHolder;
import nixexplorer.app.components.CredentialsDialog;
import nixexplorer.app.components.CredentialsDialog.Credentials;
import nixexplorer.app.components.DisposableView;
import nixexplorer.app.session.AppSession;
import nixexplorer.core.FileInfo;
import nixexplorer.core.FileSystemProvider;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.file.LocalFileSystemProvider;
import nixexplorer.core.ssh.SshFileSystemWrapper;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.widgets.folderview.FolderViewUtility;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class CopyWidget extends JDialog implements DisposableView {

	enum CopyMode {
		Upload, Download
	}

	private String targetFolder;
	private List<String> files, folders;
	private CopyMode mode;
	// private SshWrapper wrapper = null;
	private JLabel lblTitle;
	private byte[] b = new byte[8192];
	private JProgressBar prg;
	private JButton btnCancel;
	private long totalSize;
	private long processedSize;
	private Thread t;
	private AtomicBoolean stopFlag = new AtomicBoolean(false);
	private Object lock = new Object();
	private SessionInfo info;
	private AppSession appSession;
	protected AtomicBoolean widgetClosed = new AtomicBoolean(Boolean.FALSE);
	private FileSystemProvider fs;
	/*
	 * args: d/u targetfolder <file_count> <folder_count> file1 file2 folder1
	 * folder2
	 */

	/**
	 * @param env
	 * @param args
	 * @param parent
	 */
	public CopyWidget(SessionInfo info, String[] args, AppSession appSession,
			Window window) {
		super(window);
		this.info = info;
		this.appSession = appSession;

		this.setSize(Utility.toPixel(300), Utility.toPixel(130));

		if (args.length < 4) {
			JOptionPane.showMessageDialog(null, "min 3 argument required");
			throw new RuntimeException("min 3 argument required");
		}

		String mode = args[0];
		if (!("d".equals(mode) || "u".equals(mode))) {
			JOptionPane.showMessageDialog(null, "invalid mode");
			throw new RuntimeException("invalid mode");
		}

		if ("d".equals(mode)) {
			this.mode = CopyMode.Download;
		} else {
			this.mode = CopyMode.Upload;
		}

		targetFolder = args[1];

		int fileCount = Integer.parseInt(args[2]);
		int folderCount = Integer.parseInt(args[3]);

		files = new ArrayList<String>();
		folders = new ArrayList<String>();

		int pos = 4;

		for (int i = 0; i < fileCount; i++) {
			files.add(args[pos + i]);
		}

		pos += fileCount;

		for (int i = 0; i < folderCount; i++) {
			folders.add(args[pos + i]);
		}

		// in background thread, list all files, match names and find which are
		// file and which are folder

//		setPreferredSize(
//				new Dimension(Utility.toPixel(400), Utility.toPixel(200)));

		this.setLayout(new BorderLayout());
		Box box1 = Box.createVerticalBox();
		box1.setBorder(new EmptyBorder(Utility.toPixel(10), Utility.toPixel(10),
				Utility.toPixel(10), Utility.toPixel(10)));
		lblTitle = new JLabel("Initializing...");
		prg = new JProgressBar();
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(e -> {
			stopFlag.set(true);
			setVisible(false);
			new Thread(() -> {
				try {
					if (fs != null) {
						fs.close();
					}
				} catch (Exception e2) {
				}
			}).start();
		});

		lblTitle.setAlignmentX(Box.LEFT_ALIGNMENT);
		prg.setAlignmentX(Box.LEFT_ALIGNMENT);
		Box box2 = Box.createHorizontalBox();
		box2.add(Box.createHorizontalGlue());
		box2.add(Box.createHorizontalStrut(Utility.toPixel(10)));
		box2.add(btnCancel);
		box2.setAlignmentX(Box.LEFT_ALIGNMENT);
		box1.add(lblTitle);
		box1.add(Box.createVerticalStrut(Utility.toPixel(10)));
		box1.add(prg);
		box1.add(Box.createVerticalStrut(Utility.toPixel(10)));
		box1.add(box2);
		add(box1);

		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				btnCancel.doClick();
			}
		});

		this.fs = new SshFileSystemWrapper(info);

		// this.pack();

		this.setLocationRelativeTo(null);

		t = new Thread(() -> {
			copy();
		});
		t.start();
	}

	private FileSystemProvider createSourceFs() throws Exception {
		if (mode == CopyMode.Download) {
//			wrapper = new SshWrapper(info);
//			wrapper.connect();
//			return new SshFileSystemProvider(wrapper.getSftpChannel());
			return fs;
		} else {
			return new LocalFileSystemProvider();
		}
	}

	private FileSystemProvider createTargetFs() throws Exception {
		if (mode == CopyMode.Download) {
			return new LocalFileSystemProvider();
		} else {
//			wrapper = new SshWrapper(info);
//			wrapper.connect();
//			return new SshFileSystemProvider(wrapper.getSftpChannel());
			return fs;
		}
	}

	private void copy() {
		stopFlag.set(false);
		Map<String, String> fileMap = new HashMap<>();
		List<String> keys = new ArrayList<String>();
		boolean listCreated = false;
		this.totalSize = 0L;
		this.processedSize = 0L;
		int lastProcessed = 0;

		while (!stopFlag.get()) {
			try {
				FileSystemProvider sourceFs = createSourceFs();
				FileSystemProvider targetFs = createTargetFs();
				if (!listCreated) {
					totalSize = createFileList(sourceFs, targetFs, fileMap);
					listCreated = true;
					keys.addAll(fileMap.keySet());
				}

				System.out.println("Total file size: " + totalSize);

				for (int i = lastProcessed; i < keys.size(); i++) {
					processedSize += copyFile(sourceFs, targetFs, keys.get(i),
							fileMap.get(keys.get(i)));
					lastProcessed = i;
				}

				break;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					fs.close();
				} catch (Exception e2) {
				}
			}
			if (stopFlag.get()) {
				break;
			}
			if (JOptionPane.showConfirmDialog(null,
					TextHolder.getString("duplicate.failed"),
					TextHolder.getString("duplicate.failed"),
					JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
				stopFlag.set(true);
			}
		}

		SwingUtilities.invokeLater(() -> {
			if (mode == CopyMode.Upload) {
				appSession.remoteFileSystemWasChanged(targetFolder);
			} else {
				appSession.remoteFileSystemWasChanged(targetFolder);
			}
			dispose();
		});
	}

	private long copyFile(FileSystemProvider sourceFs,
			FileSystemProvider targetFs, String source, String target)
			throws Exception {
		System.out.println("Copying: " + source + " to: " + target);
		lblTitle.setText("Copying: " + source + " to: " + target);
		long lastUpdated = 0;
		FileInfo fileInfo = sourceFs.getInfo(source);
		long size = fileInfo.getSize();
		long offset = 0;
		long modified = Utility.toEpochMilli(fileInfo.getLastModified());
		String folder = PathUtils.getParent(target);
		String tempFile = PathUtils.combine(folder,
				PathUtils.getFileName(source) + "-" + modified + "-" + size
						+ ".filepart",
				mode == CopyMode.Download ? File.separator : "/");
		System.out.println("Temp file: " + tempFile);
		try {
			offset = targetFs.getInfo(tempFile).getSize();
		} catch (FileNotFoundException e) {
			System.out.println("No temp file exists");
		}

		long bytesCopied = offset;

		if (offset <= size) {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = sourceFs.getInputStream(source, offset);
				out = targetFs.getOutputStream(tempFile);
				while (true) {
					int x = in.read(b);
					if (x == -1) {
						break;
					}
					bytesCopied += x;
					out.write(b, 0, x);
					if (totalSize > 0) {
						final int pc = (int) (((processedSize + bytesCopied)
								* 100) / totalSize);
						long time = System.currentTimeMillis();
						if (time - lastUpdated > 1000) {
							System.out.println("Progress: " + pc);
							lastUpdated = time;
							SwingUtilities.invokeLater(() -> {
								prg.setValue(pc);
							});
						}
					}
				}
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					in.close();
				} catch (Exception e2) {
				}
				try {
					out.close();
				} catch (Exception e2) {
				}
			}
		}
		try {
			targetFs.deleteFile(target);
		} catch (Exception e) {
			// TODO: handle exception
		}

		targetFs.rename(tempFile, target);

		return size;
	}

	private long createFileList(FileSystemProvider sourceFs,
			FileSystemProvider targetFs, Map<String, String> fileMap)
			throws Exception {

		List<FileInfo> filesInTargetFolder = targetFs.list(targetFolder);

//			Map<String, String> fileMap = new HashMap<>();
		Map<String, String> folderMap = new HashMap<>();

		if (!FolderViewUtility.prepareFileList(targetFolder, files, fileMap,
				mode == CopyMode.Download, filesInTargetFolder)) {
			System.out.println("Returing...");
		}

		if (!FolderViewUtility.prepareFileList(targetFolder, folders, folderMap,
				mode == CopyMode.Download, filesInTargetFolder)) {
			System.out.println("Returing...");
		}

		System.out.println("fileMap: " + fileMap);
		System.out.println("folderMap: " + folderMap);
		Map<String, String> childFolders = new HashMap<>();
		long size = 0;
		for (String key : folderMap.keySet()) {
			size += sourceFs.getAllFiles(key, targetFolder, fileMap,
					childFolders);
			System.out.println("Size: " + size);
		}

		for (String file : files) {
			size += sourceFs.getInfo(file).getSize();
		}

		System.out.println("All files are retrieved");

		System.out.println("childFolders: " + childFolders);
		System.out.println("fileMap final: " + fileMap);

		for (String key : childFolders.keySet()) {
			targetFs.mkdirs(childFolders.get(key));
		}

		System.out.println("folder structure created");

		System.out.println("filemap: " + fileMap);

		return size;
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
		widgetClosed.set(true);
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
		btnCancel.doClick();
		return true;
	}

}

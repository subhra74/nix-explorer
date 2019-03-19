package nixexplorer.widgets.archiver;

import java.awt.BorderLayout;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;

import nixexplorer.TextHolder;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshFileSystemProvider;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.widgets.folderview.FileSelectionDialog;
import nixexplorer.widgets.util.Utility;

public class ArchivePreviewWidget extends JDialog
		implements TreeExpansionListener {
	private JTree tree;
	private DefaultTreeModel treeModel;
	private SshWrapper wrapper;
	private String path;
	private static final Pattern ZIP_PATTERN = Pattern.compile(
			"\\d+\\s+[\\d]{4}-[\\d]{2}-[\\d]{2}\\s+[\\d]{2}:[\\d]{2}\\s+(.*)");
	private static final String clazz = "nixexplorer.widgets.archiver.ArchiveExtractWidget";
	private Map<String, String> listCommands;
	private List<String> files = new ArrayList<>();
	private Box toolbox;
	private Box optionBox;
	private JTextField txtSearch;
	private SessionInfo info;
	private AppSession appSession;

	public ArchivePreviewWidget(SessionInfo info, String[] args,
			AppSession appSession, Window window) {
		super(window);
		this.info = info;
		this.appSession = appSession;
		setSize(Utility.toPixel(400), Utility.toPixel(300));
		setPreferredSize(getSize());
		setResizable(true);
		setLayout(new BorderLayout());
		treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("Archive"),
				true);
		tree = new JTree(treeModel);
		JScrollPane jsp = new JScrollPane(tree);
		add(jsp);

		if (args.length >= 1) {
			if (args[0].equals("-o") && args.length == 2) {
				path = args[1];
				openArchive();
			}
		}

		toolbox = Box.createHorizontalBox();
		JLabel lblsearch = new JLabel(TextHolder.getString("archiver.search"));
		JButton btnsearch = new JButton(
				TextHolder.getString("archiver.search"));
		btnsearch.addActionListener(e -> {
			String filter = txtSearch.getText();
			parseFolder(filter);
		});
		txtSearch = new JTextField(30);
		toolbox.add(lblsearch);
		toolbox.add(txtSearch);
		toolbox.add(btnsearch);

		JButton btnExtract = new JButton(
				TextHolder.getString("archiver.extract"));
		btnExtract.addActionListener(e -> {
			selectFolder();
		});
		JButton btnOpen = new JButton(TextHolder.getString("archiver.open"));
		btnOpen.addActionListener(e -> {
			selectFile();
		});
		JButton btnClose = new JButton(TextHolder.getString("archiver.close"));

		optionBox = Box.createHorizontalBox();
		optionBox.add(btnOpen);
		optionBox.add(btnExtract);
		optionBox.add(btnClose);
		optionBox.add(Box.createHorizontalGlue());

		Box toolbar = Box.createVerticalBox();
		toolbox.setAlignmentX(Box.LEFT_ALIGNMENT);
		optionBox.setAlignmentX(Box.LEFT_ALIGNMENT);
		toolbar.add(optionBox);
		toolbar.add(toolbox);
		add(toolbar, BorderLayout.NORTH);
		setModal(true);
		setLocationRelativeTo(null);
	}

	public String getViewCmd(String file) {
		if (listCommands == null) {
			listCommands = new TreeMap<>(new Comparator<String>() {
				public int compare(String o1, String o2) {
					if (o1.length() == o2.length()) {
						return o1.compareTo(o2);
					}
					return o1.length() - o2.length();
				};
			});
			listCommands.put(".tar", "cat \"%s\"|tar -tf -");
			listCommands.put(".tar.gz", "gunzip -c <\"%s\"|tar -tf -");
			listCommands.put(".tgz", "gunzip -c <\"%s\"|tar  -tf -");
			listCommands.put(".tar.bz2", "bzip2 -d -c <\"%s\"|tar  -tf -");
			listCommands.put(".tbz2", "bzip2 -d -c <\"%s\"|tar  -tf -");
			listCommands.put(".tbz", "bzip2 -d -c <\"%s\"|tar  -tf -");
			listCommands.put(".tar.xz", "xz -d -c <\"%s\"|tar  -tf -");
			listCommands.put(".txz", "xz -d -c <\"%s\"|tar  -tf -");
			listCommands.put(".zip", "unzip -l \"%s\"");
		}
		for (String key : listCommands.keySet()) {
			System.out.println(file + " " + key + " " + (file.endsWith(key)));
			if (file.endsWith(key)) {
				return listCommands.get(key);
			}
		}
		return null;
	}

	private void selectFolder() {
		new Thread(() -> {
			try {
				if (wrapper == null || !wrapper.isConnected()) {
					wrapper = new SshWrapper(info);
					wrapper.connect();
				}
				ChannelSftp sftp = wrapper.getSftpChannel();
				// sftp.connect();
				SshFileSystemProvider sshfs = new SshFileSystemProvider(sftp);
				FileSelectionDialog dlg = new FileSelectionDialog(
						sftp.getHome(), sshfs, ArchivePreviewWidget.this);
				dlg.setFolderOnly(true);
				dlg.setVisible(true);
				if (dlg.getResult() == JOptionPane.OK_OPTION) {
					System.out.println(dlg.getSelectedPath());
					String folder = dlg.getSelectedPath();
					extractArchive(path, folder);
				}

				sftp.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		}).start();
	}

	private void extractArchive(String path2, String folder) {
		String sel = createSelectedList();
		System.out.println(sel);
		String[] a = new String[sel != null && sel.length() > 0 ? 3 : 2];
		a[0] = folder;
		a[1] = path2;
		if (a.length == 3) {
			a[2] = sel;
		}
		appSession.createWidget(clazz, a);
	}

	private void selectFile() {
		new Thread(() -> {
			try {
				if (wrapper == null || !wrapper.isConnected()) {
					wrapper = new SshWrapper(info);
					wrapper.connect();
				}
				ChannelSftp sftp = wrapper.getSftpChannel();
				// sftp.connect();
				SshFileSystemProvider sshfs = new SshFileSystemProvider(sftp);
				FileSelectionDialog dlg = new FileSelectionDialog(
						sftp.getHome(), sshfs, ArchivePreviewWidget.this);
				dlg.setFolderOnly(false);
				dlg.setVisible(true);
				if (dlg.getResult() == JOptionPane.OK_OPTION) {
					System.out.println(dlg.getSelectedPath());
					path = dlg.getSelectedPath();
					openArchive();
				}

				sftp.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		}).start();
	}

	private void extractAsync(String path) {
		System.out.println("Opening.. " + path);
		try {
			if (wrapper == null || !wrapper.isConnected()) {
				wrapper = new SshWrapper(info);
				wrapper.connect();
			}
			String extractCmd = getViewCmd(path);
			if (extractCmd == null) {
				JOptionPane.showMessageDialog(null,
						TextHolder.getString("archiver.unknownformat"));
				return;
			}
			extractCmd = String.format(extractCmd, path);
			System.out.println(extractCmd);

			boolean zip = extractCmd.startsWith("unzip");
			// log(extractCmd);
			ChannelExec exec = wrapper.getExecChannel();
			InputStream in = exec.getInputStream();
			exec.setCommand(extractCmd);
			exec.connect();

			files = new ArrayList<>();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				try {
					if (zip) {
						Matcher m = ZIP_PATTERN.matcher(line);
						if (m.find()) {
							line = m.group(1);
							files.add(line);
						}
					} else {
						files.add(line);
					}
					System.err.println(line);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			System.err.flush();

			reader.close();
			exec.disconnect();
			int ret = exec.getExitStatus();
			System.err.println("Exit code: " + ret);
			// log(TextHolder.getString("archiver.exitcode") + ret);

			// wrapper.disconnect();
			if (ret < 1) {
				// dispose();
				SwingUtilities.invokeLater(() -> {
					parseFolder(null);
				});
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					TextHolder.getString("archiver.error"));
		} finally {
		}
	}

	private void parseFolder(String filter) {
		treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("Archive"),
				true);
		tree.setModel(treeModel);
		for (String item : files) {
			if (filter != null && filter.length() > 0) {
				if (item.contains(filter)) {
					renderTree(item);
				}
			} else {
				renderTree(item);
			}
		}
	}

	private String getFilePath(JTree tree, TreePath path) {
		if (path == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		// boolean slashAdded = true;

		for (Object obj : path.getPath()) {
			if (tree.getModel().getRoot() == obj) {
				continue;
			}
			DefaultMutableTreeNode n = (DefaultMutableTreeNode) obj;
//			if (!slashAdded) {
//				sb.append("/");
//			}
			sb.append(n.getUserObject());
			if (n.getAllowsChildren()) {
				sb.append("/");
			}
		}
		// System.out.println("selected path: " + sb + " current path: " +
		// file);
		String s = sb.toString();
		if (this.path.endsWith(".zip")) {
			if (s.endsWith("/")) {
				return s + "*";
			} else {
				return s;
			}
		}
		return s;
	}

	private String createSelectedList() {
		TreePath[] paths = tree.getSelectionPaths();
		StringBuilder sb = new StringBuilder();
		if (paths != null && paths.length > 0) {
			for (TreePath pp : paths) {
				String selectedPath = getFilePath(tree, pp);
				if (selectedPath == null || selectedPath.length() < 1) {
					continue;
				} else {
					sb.append(" \"" + selectedPath + "\"");
				}
			}
		}
		return sb.toString();
	}

	private void renderTree(String filePath) {
		String[] arr = filePath.split("[\\/\\\\]");
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel()
				.getRoot();
		for (int i = 0; i < arr.length; i++) {
			String s = arr[i];
			if (s.length() > 0) {
				node = getChild(node, s);
			}
		}

		node.setAllowsChildren(filePath.endsWith("/"));
	}

	private DefaultMutableTreeNode getChild(DefaultMutableTreeNode node,
			String text) {

		for (int i = 0; i < node.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
					.getChildAt(i);
			// System.out.println(child.getUserObject() + " -> " + text);
			if (text.equals(child.getUserObject())) {
				return child;
			}
		}

		DefaultMutableTreeNode child = new DefaultMutableTreeNode(text);
		child.setAllowsChildren(true);
		int index = node.getChildCount();
		treeModel.insertNodeInto(child, node, index);
		return child;
	}

	private void stopProgress() {
		// TODO Auto-generated method stub

	}

	private void openArchive() {
		new Thread(() -> {
			extractAsync(path);
		}).start();
	}

	@Override
	public void treeExpanded(TreeExpansionEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void treeCollapsed(TreeExpansionEvent event) {
		// TODO Auto-generated method stub

	}
}

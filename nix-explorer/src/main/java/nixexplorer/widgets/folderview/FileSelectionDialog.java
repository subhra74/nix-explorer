package nixexplorer.widgets.folderview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import nixexplorer.TextHolder;
import nixexplorer.core.FileInfo;
import nixexplorer.core.FileSystemProvider;
import nixexplorer.core.FileType;
import nixexplorer.widgets.util.Utility;

public class FileSelectionDialog extends JDialog
		implements TreeExpansionListener, TreeSelectionListener {
	private DefaultTreeModel treeModel;
	private JTree tree;
	private FileSystemProvider fs;
	private boolean treeLoading = false;
	private boolean folderOnly = false;
	private JTextField txtSelection;
	private JButton btnOk, btnCancel;
	private int res = JOptionPane.CANCEL_OPTION;

	public FileSelectionDialog(String path, FileSystemProvider fs,
			Window window) {
		super(window);
		setTitle("File/folder selection");
		setModal(true);
		this.fs = fs;
		setSize(Utility.toPixel(400), Utility.toPixel(300));
		setResizable(true);
		setLocationRelativeTo(window);
		JPanel pan = new JPanel(
				new BorderLayout(Utility.toPixel(5), Utility.toPixel(5)));
		pan.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		treeModel = new DefaultTreeModel(
				new DefaultMutableTreeNode("File system"), true);
		tree = new JTree(treeModel);
		tree.addTreeExpansionListener(this);
		tree.addTreeSelectionListener(this);
		JScrollPane jsp = new JScrollPane(tree);
		jsp.setBorder(UIManager.getBorder("Component.border"));
		pan.add(jsp);

		Box b1 = Box.createVerticalBox();
		Box b2 = Box.createHorizontalBox();
		JLabel lbl1 = new JLabel(TextHolder.getString("filebrowser.selected"));
		b2.add(lbl1);
		txtSelection = new JTextField(30);
		txtSelection.setMaximumSize(new Dimension(Integer.MAX_VALUE,
				txtSelection.getPreferredSize().height));
		b2.add(txtSelection);
		b2.setAlignmentX(Box.LEFT_ALIGNMENT);
		b1.add(b2);
		b1.add(Box.createVerticalStrut(Utility.toPixel(5)));

		Box b3 = Box.createHorizontalBox();
		b3.add(Box.createHorizontalGlue());
		btnOk = new JButton(TextHolder.getString("common.ok"));
		btnOk.addActionListener(e -> {
			res = JOptionPane.OK_OPTION;
			dispose();
		});
		b3.add(btnOk);
		b3.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		btnCancel = new JButton(TextHolder.getString("common.cancel"));
		btnCancel.addActionListener(e -> {
			res = JOptionPane.CANCEL_OPTION;
			dispose();
		});
		b3.add(btnCancel);
		b3.setAlignmentX(Box.LEFT_ALIGNMENT);
		b1.add(b3);

		pan.add(b1, BorderLayout.SOUTH);
		add(pan);

		load(path);
	}

	public String getSelectedPath() {
		return txtSelection.getText();
	}

	public void disconnect() {
		fs.close();
	}

	private void disableView() {

	}

	private void enableView() {

	}

	private void renderTree(String filePath, List<FileInfo> list) {
		treeLoading = true;
		String[] arr = filePath.split("[\\/\\\\]");
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel()
				.getRoot();
		for (int i = 0; i < arr.length; i++) {
			String s = arr[i];
			if (s.length() > 0) {
				node = getChild(node, s);
			}
		}

		TreePath path = new TreePath(treeModel.getPathToRoot(node));
		tree.expandPath(path);
		tree.setSelectionPath(path);
		tree.scrollPathToVisible(path);

		for (int i = 0; i < list.size(); i++) {
			FileInfo info = list.get(i);
			if (folderOnly) {
				if (info.getType() != FileType.Directory
						&& info.getType() != FileType.DirLink) {
					continue;
				}
			}

			boolean exists = false;

			for (int j = 0; j < node.getChildCount(); j++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
						.getChildAt(j);
				if (info.getName().equals(child.getUserObject())) {
					exists = true;
					break;
				}
			}
			if (!exists) {
				int index = node.getChildCount();
				DefaultMutableTreeNode c = new DefaultMutableTreeNode(
						info.getName());
				c.setAllowsChildren(info.getType() == FileType.Directory
						|| info.getType() == FileType.DirLink);
				treeModel.insertNodeInto(c, node, index);
			}
		}

		// txtSelection.setText(filePath);

		treeLoading = false;
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
		// node.add(child);

		return child;
	}

	private String getFilePath(TreePath path) {
		if (path == null) {
			return null;
		}
		String selectedPath = FolderViewUtility.getFilePath(tree, path,
				fs.isLocal());
		if (selectedPath == null) {
			return null;
		}
		return selectedPath;
	}

	@Override
	public void treeExpanded(TreeExpansionEvent event) {
		if (treeLoading) {
			return;
		}
		String path = getFilePath(event.getPath());
		if (path != null) {
			disableView();
			load(path);
		}
	}

	private void load(String filePath) {
		disableView();
		new Thread(() -> {
			try {
				String path = filePath;
				if (path == null) {
					path = fs.getHome();
				}
				System.out.println("Home: " + path);
				String path2 = path;
				List<FileInfo> list = fs.ll(path, folderOnly);
				System.out.println("List: " + list);
				if (list != null) {
					SwingUtilities.invokeLater(() -> {
						renderTree(path2, list);
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				SwingUtilities.invokeLater(() -> {
					enableView();
				});
			}
		}).start();
	}

	@Override
	public void treeCollapsed(TreeExpansionEvent event) {
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		String file = getFilePath(e.getPath());
		if (file != null && file.length() > 0) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();
			if (node != null) {
				if (folderOnly) {
					if (node.getAllowsChildren()) {
						txtSelection.setText(file);
					}
				} else {
					if (!node.getAllowsChildren()) {
						txtSelection.setText(file);
					}
				}
			}
		}
	}

	public int getResult() {
		return res;
	}

	public boolean isFolderOnly() {
		return folderOnly;
	}

	public void setFolderOnly(boolean folderOnly) {
		this.folderOnly = folderOnly;
	}

}

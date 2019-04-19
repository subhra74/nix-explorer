package nixexplorer.widgets.folderview;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableRowSorter;

import nixexplorer.PathUtils;
import nixexplorer.TextHolder;
import nixexplorer.core.FileInfo;
import nixexplorer.core.FileType;
import nixexplorer.core.ssh.FileSystemWrapper;
import nixexplorer.widgets.util.Utility;

public class FileSelectionDialog extends JDialog {
//	private DefaultTreeModel treeModel;
//	private JTree tree;
	private FileSystemWrapper fs;
	private boolean loading = false;
	private boolean folderOnly = false;
	private JTextField txtSelection;
	private JButton btnOk, btnCancel;
	private Cursor waitCursor, normalCursor;
	private boolean okClicked = false;
	private JTable folderTable;
	private FolderViewTableModel folderViewModel;
	private JButton btnUp;
	private JButton btnGo;
	private JTextField txtAddress;
	private String folder;

	public enum DialogResult {
		APPROVE, CANCEL
	}

	private DialogResult res = DialogResult.CANCEL;

	public FileSelectionDialog(String path, FileSystemWrapper fs, Window window, boolean folderOnly) {
		super(window);
		this.fs = fs;
		setSize(Utility.toPixel(640), Utility.toPixel(480));
		setLocationRelativeTo(window);
		this.folderOnly = folderOnly;
		init();
		render(path);
	}

	public DialogResult showDialog() {
		setVisible(true);
		return res;
	}

	private void init() {
		setTitle("File/folder selection");
		setModal(true);
		setResizable(true);
		waitCursor = new Cursor(Cursor.WAIT_CURSOR);
		normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		JPanel pan = new JPanel(new BorderLayout());
		pan.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5), Utility.toPixel(5), Utility.toPixel(5)));

		btnUp = new JButton(UIManager.getIcon("AddressBar.up"));
		btnUp.addActionListener(e -> {
			if (this.folder.equals("/")) {
				return;
			}
			String f = PathUtils.getParent(this.folder);
			render(f);
		});
		btnUp.setBorderPainted(false);

		txtAddress = new JTextField(30);
		Dimension d = txtAddress.getPreferredSize();
		txtAddress.setMaximumSize(new Dimension(Integer.MAX_VALUE, d.height));
		txtAddress.setMinimumSize(new Dimension(0, d.height));
		txtAddress.addActionListener(e -> {
			if (loading) {
				return;
			}
			render(txtAddress.getText());
		});

		Box boxAddr = Box.createHorizontalBox();
		boxAddr.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		boxAddr.add(btnUp);
		boxAddr.add(txtAddress);
		boxAddr.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		pan.add(boxAddr, BorderLayout.NORTH);

		FolderViewRenderer r = new FolderViewRenderer();
		folderViewModel = new FolderViewTableModel();
		folderTable = new JTable(folderViewModel);
		folderTable.setBorder(new EmptyBorder(0, 0, 0, 0));
		folderTable.setIntercellSpacing(new Dimension(0, 0));
		folderTable.setBorder(null);

		folderTable.setDropMode(DropMode.USE_SELECTION);
		folderTable.setShowGrid(false);
		folderTable.setRowHeight(r.getPreferredHeight() + Utility.toPixel(0));
		folderTable.setFillsViewportHeight(true);

		folderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		folderTable.setDefaultRenderer(Object.class, r);
		folderTable.setDefaultRenderer(Long.class, r);
		folderTable.setDefaultRenderer(Date.class, r);

		TableRowSorter<FolderViewTableModel> sorter = new TableRowSorter<FolderViewTableModel>(folderViewModel);
		sorter.setComparator(0, new Comparator<Object>() {
			@Override
			public int compare(Object s1, Object s2) {
				FileInfo info1 = (FileInfo) s1;
				FileInfo info2 = (FileInfo) s2;
				if (info1.getType() == FileType.Directory || info1.getType() == FileType.DirLink) {
					if (info2.getType() == FileType.Directory || info2.getType() == FileType.DirLink) {
						return info1.getName().compareToIgnoreCase(info2.getName());
					} else {
						return 1;
					}
				} else {
					if (info2.getType() == FileType.Directory || info2.getType() == FileType.DirLink) {
						return -1;
					} else {
						return info1.getName().compareToIgnoreCase(info2.getName());
					}
				}
			}
		});

		sorter.setComparator(1, new Comparator<Long>() {
			@Override
			public int compare(Long s1, Long s2) {
				return s1.compareTo(s2);
			}
		});

		sorter.setComparator(3, new Comparator<FileInfo>() {

			@Override
			public int compare(FileInfo info1, FileInfo info2) {
				if (info1.getType() == FileType.Directory || info1.getType() == FileType.DirLink) {
					if (info2.getType() == FileType.Directory || info2.getType() == FileType.DirLink) {
						return info1.getLastModified().compareTo(info2.getLastModified());
					} else {
						return 1;
					}
				} else {
					if (info2.getType() == FileType.Directory || info2.getType() == FileType.DirLink) {
						return -1;
					} else {
						return info1.getLastModified().compareTo(info2.getLastModified());
					}
				}
			}

		});

		folderTable.setRowSorter(sorter);

		folderTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
		folderTable.getActionMap().put("Enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				FileInfo[] files = getSelectedFiles();
				if (files.length > 0) {
					if (files[0].getType() == FileType.Directory || files[0].getType() == FileType.DirLink) {
						String str = files[0].getPath();
						render(str);
					}
				}
			}
		});

		folderTable.addKeyListener(new FolderViewKeyHandler(folderTable, folderViewModel));

		folderTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					Point p = e.getPoint();
					int r = folderTable.rowAtPoint(p);
					int x = folderTable.getSelectedRow();
					if (x == -1) {
						return;
					}
					if (r == folderTable.getSelectedRow()) {
						FileInfo fileInfo = folderViewModel.getItemAt(getRow(r));
						if (fileInfo.getType() == FileType.Directory || fileInfo.getType() == FileType.DirLink) {
							render(fileInfo.getPath());
						}
					}
				} else if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
					FileInfo[] files = getSelectedFiles();
					if (files != null && files.length > 0) {
						FileInfo f = files[0];
						if (folderOnly && (f.getType() == FileType.Directory || f.getType() == FileType.DirLink)) {
							txtSelection.setText(f.getPath());
						}
						if (!folderOnly && (f.getType() == FileType.File || f.getType() == FileType.FileLink)) {
							txtSelection.setText(f.getPath());
						}
					}
				}
			}
		});

		JScrollPane jsp = new JScrollPane(folderTable);
		jsp.setBorder(UIManager.getBorder("Component.border"));
		pan.add(jsp);

		Box b1 = Box.createVerticalBox();
		Box b2 = Box.createHorizontalBox();
		JLabel lbl1 = new JLabel(TextHolder.getString("filebrowser.selected"));
		b2.add(lbl1);
		b2.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		txtSelection = new JTextField(30);
		txtSelection.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtSelection.getPreferredSize().height));
		b2.add(txtSelection);
		b2.setAlignmentX(Box.LEFT_ALIGNMENT);
		b1.add(b2);
		b1.add(Box.createVerticalStrut(Utility.toPixel(5)));

		Box b3 = Box.createHorizontalBox();
		b3.add(Box.createHorizontalGlue());
		btnOk = new JButton(TextHolder.getString("common.ok"));
		btnOk.addActionListener(e -> {
			if (txtSelection.getText().length() < 1) {
				JOptionPane.showMessageDialog(this, TextHolder.getString("fileselection.empty"));
				return;
			}
			res = DialogResult.APPROVE;
			okClicked = true;
			dispose();
		});
		b3.add(btnOk);
		b3.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		btnCancel = new JButton(TextHolder.getString("common.cancel"));
		btnCancel.addActionListener(e -> {
			res = DialogResult.CANCEL;
			dispose();
		});

		b3.add(btnCancel);
		b3.setAlignmentX(Box.LEFT_ALIGNMENT);
		b1.add(b3);

		b1.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5), Utility.toPixel(5), Utility.toPixel(5)));

		pan.add(b1, BorderLayout.SOUTH);
		add(pan);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				if (!okClicked) {
					System.out.println("Window closed by user, setting result to cancel");
					res = DialogResult.CANCEL;
				}
			}
		});
		txtSelection.addActionListener(e -> {
			System.out.println("Text action listener called");
			if (txtSelection.getText().trim().length() < 1) {
				return;
			}
			render(txtSelection.getText().trim());
		});
	}

	public FileInfo[] getSelectedFiles() {
		FileInfo fs[] = new FileInfo[folderTable.getSelectedRows().length];
		int rows[] = folderTable.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			fs[i] = folderViewModel.getItemAt(getRow(rows[i]));
			System.out.println("Selected item: " + fs[i]);
		}
		return fs;
	}

	private int getRow(int r) {
		if (r == -1) {
			return -1;
		}
		return folderTable.convertRowIndexToModel(r);
	}

	public String getSelectedPath() {
		return txtSelection.getText();
	}

	private void disableView() {
		loading = true;
		setCursor(waitCursor);
		folderTable.setEnabled(false);
		txtSelection.setEnabled(false);
		btnOk.setEnabled(false);
		btnCancel.setEnabled(false);
		btnUp.setEnabled(false);
		txtAddress.setEnabled(false);
	}

	private void enableView() {
		setCursor(normalCursor);
		folderTable.setEnabled(true);
		txtSelection.setEnabled(true);
		btnOk.setEnabled(true);
		btnCancel.setEnabled(true);
		btnUp.setEnabled(true);
		txtAddress.setEnabled(true);
		loading = false;
	}

	private void render(String filePath) {
		disableView();
		new Thread(() -> {
			try {
				String path = filePath;
				if (path == null) {
					path = fs.getHome();
				}
				System.out.println("Home: " + path);
				List<FileInfo> list = fs.list(path);
				System.out.println("List: " + list);
				this.folder = path;
				if (list != null) {
					SwingUtilities.invokeLater(() -> {
						folderViewModel.clear();
						List<FileInfo> files = folderOnly
								? list.stream()
										.filter(a -> a.getType() == FileType.Directory
												|| a.getType() == FileType.DirLink)
										.collect(Collectors.toList())
								: list;
						folderViewModel.addAll(files);
						txtAddress.setText(this.folder);
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

	public boolean isFolderOnly() {
		return folderOnly;
	}

	public void setFolderOnly(boolean folderOnly) {
		this.folderOnly = folderOnly;
	}

}

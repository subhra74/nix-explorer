package nixexplorer.widgets.folderview;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import nixexplorer.AppClipboard;
import nixexplorer.Constants;
import nixexplorer.PathUtils;
import nixexplorer.TextHolder;
import nixexplorer.app.session.AppSession;
import nixexplorer.core.FileInfo;
import nixexplorer.core.FileType;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.app.settings.AppConfig;
import nixexplorer.core.SessionStore;
import nixexplorer.core.ssh.SshFileSystemProvider;
import nixexplorer.registry.contextmenu.ContextMenuEntry;
import nixexplorer.registry.contextmenu.ContextMenuRegistry;
import nixexplorer.widgets.component.AddressBarPanel;
import nixexplorer.widgets.dnd.FolderViewBaseTransferHandler;
import nixexplorer.widgets.dnd.TransferFileInfo;
import nixexplorer.widgets.dnd.TransferFileInfo.Action;
import nixexplorer.widgets.dnd.TreeBaseTransferHandler;
import nixexplorer.widgets.folderview.ViewTogglePanel.ViewMode;
import nixexplorer.widgets.folderview.common.FolderViewSelectionHelper;
import nixexplorer.widgets.folderview.remote.AskForPriviledgeDlg;
import nixexplorer.widgets.folderview.remote.RemoteFolderViewWidget;
import nixexplorer.widgets.listeners.AppEventListener;
import nixexplorer.widgets.util.Utility;

/*
 * %f - all selected files
 * %d - current directory
 */

public class FolderViewWidget extends JPanel
		implements TableCellRenderer, AppEventListener, TreeSelectionListener {
	private static final long serialVersionUID = 4145936478620976613L;
	// private JTextField txtAddressBar;
	private AddressBarPanel txtAddressBar;
	private JButton btnBack, btnForward, btnUp, btnReload,
			// btnSearch,
			btnHome, btnMoreMenu;
	private FolderViewTableModel folderViewModel;
	private JTable folderTable;
	private JLabel label = new JLabel();
	private JMenuItem mOpen, mRename, mDelete, mNewFile, mNewFolder, mCopy,
			mPaste, mCut, mAddToFav, mEditExtern, mOpenExtern, mChangePerm;

	private String file;

	private Map<String, List<FileInfo>> cachedFolders = new HashMap<>();
	private TabCallback tabCallback;
	private JPopupMenu popup, popup2, popup3;
	private boolean embedded = false;
	private boolean dirOnly = false;
	private SelectionCallback selectionCallback;
	private boolean applyPreviousAction = false;
	private JSplitPane splitPane;
	private DefaultTreeModel treeModel;
	private JTree tree;
	private JComboBox<String> cmbView1;
	private JPanel contentHolder;
	private JList<FavouritePlaceEntry> listPlaces;
	private DefaultListModel<FavouritePlaceEntry> modelPlaces;
	private JScrollPane listScroll, treeScroll;
	private JScrollPane scrollTable;
	private int dividerPos;
	private boolean treeLoading = false;
	private NavigationHistory history;
	private boolean noSidePane = false;
	private JLabel lblDetails;
	private ContextMenuActionHandler menuHandler;
	private OverflowMenuActionHandler menuHandler2;
	private TreeContextMenuHandler menuHandler3;
	private long lastTyped = 0L;
	private StringBuilder sb = new StringBuilder();
	private JPanel navigationPanel;
	private JCheckBox chkSideNav;
	private boolean showingHiddenFiles = false;
	private FolderViewSelectionHelper selectionHelper;
	private TableRowSorter<FolderViewTableModel> sorter;
	private TableListModel fileListModel;
	private JList<FileInfo> fileListView;
	private JScrollPane scrollListView;
	private ViewTogglePanel toggleView;
	// private NavigationListModel modelNav;

	public FolderViewWidget(String file, TabCallback tabCallback,
			FolderViewBaseTransferHandler transferHandler,
			TreeBaseTransferHandler treeHandler,
			ContextMenuActionHandler menuHandler,
			OverflowMenuActionHandler menuHandler2,
			TreeContextMenuHandler menuHandler3) {
		this.file = file;
		this.tabCallback = tabCallback;
		this.menuHandler = menuHandler;
		this.menuHandler2 = menuHandler2;
		this.menuHandler3 = menuHandler3;
		this.menuHandler.install(this);
		this.menuHandler2.install(this);
		this.menuHandler3.install(this);

		setLayout(new BorderLayout());
		history = new NavigationHistory();

		splitPane = new JSplitPane();
		splitPane.setBorder(
				new MatteBorder(Utility.toPixel(1), 0, Utility.toPixel(1), 0,
						UIManager.getColor("DefaultBorder.color")));
		splitPane.setContinuousLayout(true);
		lblDetails = new JLabel();
		// splitPane.setBorder(null);
		dividerPos = Utility.toPixel(200);
		splitPane.setDividerLocation(dividerPos);
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("ROOT");
		root.setAllowsChildren(true);
		treeModel = new DefaultTreeModel(root, true);
		tree = new JTree(treeModel);
		tree.setCellRenderer(new TreeViewCellRenderer());
		tree.getSelectionModel()
				.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeExpansionListener(new TreeExpansionListener() {
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				if (treeLoading) {
					return;
				}
				String path = getFilePath(event.getPath());
				if (path != null && path.length() > 0) {
					history.addBack(FolderViewWidget.this.file);
					render(path);
				}
//				tree.setSelectionPath(event.getPath());
//				loadPath();
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent event) {

			}
		});
		tree.getSelectionModel().addTreeSelectionListener(this);
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
					TreePath treePath = tree.getPathForLocation(e.getX(),
							e.getY());
					if (treePath != null) {
						String path = FolderViewUtility.getFilePath(tree,
								treePath, isLocal());
						menuHandler3.createMenu(popup3, path);
						popup3.pack();
						popup3.show(tree, e.getX(), e.getY());
					}
				} else {
					String path = getFilePath(tree.getSelectionPath());
					if (path != null && path.length() > 0) {
						history.addBack(FolderViewWidget.this.file);
						System.out.println("Rendering from tree: " + path);
						render(path);
					}
				}
			}
		});

		treeHandler.setWidget(this);
		treeHandler.setTree(tree);
		tree.setTransferHandler(treeHandler);
		// tree.setBackground(UIManager.getColor("Panel.secondary"));

//		tree.setTransferHandler(
//				new TreeViewTransferHandler(tabCallback.getInfo(), tree, this));

		treeScroll = new JScrollPane(tree);
		// treeScroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new
		// JButton("..."));

		modelPlaces = new DefaultListModel<>();

		listPlaces = new JList<>(modelPlaces);
		listPlaces.setCellRenderer(new PlaceRenderer());
		listScroll = new JScrollPane(listPlaces);
		listPlaces.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = listPlaces.getSelectedIndex();
				if (x != -1) {
					render(listPlaces.getSelectedValue().getFullPath());
				}
			}
		});

		loadFavourites();

		createViewComboBox();

		navigationPanel = new JPanel(
				new BorderLayout(Utility.toPixel(5), Utility.toPixel(5)));
		navigationPanel.add(treeScroll);
		navigationPanel.add(cmbView1, BorderLayout.NORTH);

		navigationPanel.setBorder(new MatteBorder(0, 0, 0, 1,
				UIManager.getColor("DefaultBorder.color")));

		// splitPane.setLeftComponent(navigationPanel);

		Box b1 = Box.createHorizontalBox();
		b1.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
//		btnSplit = new JToggleButton(UIManager.getIcon("AddressBar.split1"));
//		// btnSplit.setMargin(new Insets(0, 0, 0, 0));
//		btnSplit.setBorderPainted(false);
//		// btnSplit.setContentAreaFilled(false);
//		// btnSplit.setFocusPainted(false);
//		btnSplit.setSelected(true);
//		btnSplit.addActionListener(e -> {
//			if (btnSplit.isSelected()) {
//				remove(contentHolder);
//				splitPane.setRightComponent(contentHolder);
//				add(splitPane);
//				btnSplit.setIcon(UIManager.getIcon("AddressBar.split1"));
//				splitPane.setDividerLocation(dividerPos);
//			} else {
//				dividerPos = splitPane.getDividerLocation();
//				splitPane.remove(contentHolder);
//				remove(splitPane);
//				add(contentHolder);
//				btnSplit.setIcon(UIManager.getIcon("AddressBar.split2"));
//			}
//			revalidate();
//			repaint();
//		});
//		b1.add(btnSplit);

		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));

		// Dimension p1 = btnSplit.getPreferredSize();
		// Dimension p2 = cmbView1.getPreferredSize();

		// int cmbWidth = getFontMetrics(getFont()).stringWidth("Tree View");

		// int h = Math.max(p1.height, p2.height);
		// btnSplit.setPreferredSize(new Dimension(p1.width, h));
		// cmbView1.setPreferredSize(new Dimension(cmbWidth * 2, h));
//		Dimension d = new Dimension(
//				cmbView1.getPreferredSize().width + Utility.toPixel(10),
//				cmbView1.getPreferredSize().height + Utility.toPixel(5));
//		// cmbView1.setPreferredSize(d);
//		cmbView1.setMaximumSize(d);
		// b1.add(cmbView1);
		chkSideNav = new JCheckBox("Sidebar");
		chkSideNav.setSelected(true);
		chkSideNav.addActionListener(e -> {
			showHideSidebar();
		});
		b1.add(chkSideNav);

		b1.add(Box.createHorizontalGlue());
		b1.add(lblDetails);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));

		add(b1, BorderLayout.SOUTH);

		Box addressBox = Box.createHorizontalBox();
		addressBox.setBorder(new EmptyBorder(Utility.toPixel(3),
				Utility.toPixel(3), Utility.toPixel(3), Utility.toPixel(3)));

		// Dimension d2 = new Dimension(Utility.toPixel(30),
		// Utility.toPixel(30));

		btnUp = new JButton(UIManager.getIcon("AddressBar.up"));
		// btnUp.setMargin(new Insets(0, 0, 0, 0));
		// btnUp.setPreferredSize(d2);
		btnUp.setBorderPainted(false);
		// btnUp.setRolloverEnabled(true);
		// btnUp.setContentAreaFilled(false);
		btnUp.setFocusPainted(false);

		btnBack = new JButton(UIManager.getIcon("AddressBar.back"));
		// btnBack.setMargin(new Insets(0, 0, 0, 0));
		// btnBack.setPreferredSize(d2);
		btnBack.putClientProperty("button.toolbar", true);
		btnBack.setBorderPainted(false);
		// btnBack.setContentAreaFilled(false);
		btnBack.setFocusPainted(false);
		btnBack.addActionListener(e -> {
			String item = history.prevElement();
			history.addForward(FolderViewWidget.this.file);
			render(item);
		});

		btnForward = new JButton(UIManager.getIcon("AddressBar.forward"));
		btnForward.addActionListener(e -> {
			String item = history.nextElement();
			history.addBack(FolderViewWidget.this.file);
			render(item);
		});

		// btnForward.setMargin(new Insets(0, 0, 0, 0));
		// btnForward.setPreferredSize(d2);
		// btnForward.setMinimumSize(d2);
		btnForward.setBorderPainted(false);
		// btnForward.setContentAreaFilled(false);
		btnForward.setFocusPainted(false);

		btnHome = new JButton(UIManager.getIcon("AddressBar.home"));
		// btnBack.setMargin(new Insets(0, 0, 0, 0));
		// btnHome.setPreferredSize(d2);
		// btnHome.setMinimumSize(d2);
		btnHome.putClientProperty("button.toolbar", true);
		btnHome.setBorderPainted(false);
		// btnHome.setContentAreaFilled(false);
		btnHome.setFocusPainted(false);

		// btnDropdown = new JButton("V");
		btnReload = new JButton(UIManager.getIcon("AddressBar.reload"));// "TextEditor.reloadIcon"));//
																		// "AddressBar.reload"));
		// btnReload.setMargin(new Insets(0, 0, 0, 0));
		// btnReload.setPreferredSize(d2);
		btnReload.setBorderPainted(false);
		// btnReload.setContentAreaFilled(false);
		btnReload.setFocusPainted(false);

//		btnSearch = new JButton(UIManager.getIcon("AddressBar.search"));
//		// btnSearch.setMargin(new Insets(0, 0, 0, 0));
//		// btnSearch.setPreferredSize(d2);
//		btnSearch.setBorderPainted(false);
//		// btnSearch.setContentAreaFilled(false);
//		btnSearch.setFocusPainted(false);

		btnMoreMenu = new JButton(UIManager.getIcon("AddressBar.moreMenu"));
		btnMoreMenu.setBorderPainted(false);
		btnMoreMenu.setFocusPainted(false);
		btnMoreMenu.addActionListener(e -> {
			this.menuHandler2.createMenu(popup2);
			this.popup2.pack();
			Dimension d = this.popup2.getPreferredSize();
			int x = btnMoreMenu.getWidth() - d.width;
			int y = btnMoreMenu.getHeight();
			this.popup2.show(btnMoreMenu, x, y);
		});

		txtAddressBar = new AddressBarPanel(
				tabCallback.getFs().isLocal() ? File.separatorChar : '/');
		txtAddressBar.addActionListener(e -> {
			String text = txtAddressBar.getText();
			System.out.println("Address changed: " + text + " old: "
					+ FolderViewWidget.this.file);
			if (FolderViewWidget.this.file.equals(text)
					|| (FolderViewWidget.this.file + "/").equals(text)
					|| (FolderViewWidget.this.file + "\\").equals(text)) {
				System.out.println("Same text");
				return;
			}
			if (text != null && text.length() > 0) {
				history.addBack(FolderViewWidget.this.file);
				render(text);
			}
		});

		createFolderTable(transferHandler);

		AbstractAction upAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				up();
			}
		};
		this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "up");
		this.getActionMap().put("up", upAction);
		btnUp.addActionListener(upAction);

		AbstractAction reloadAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				render(FolderViewWidget.this.file, false);
			}
		};
		this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_R,
						InputEvent.CTRL_DOWN_MASK), "reload");
		this.getActionMap().put("reload", reloadAction);
		btnReload.addActionListener(reloadAction);

//		btnSearch.addActionListener(e -> {
////			AppSessionPanel.getsharedInstance().createWidget(className, args, parent);
////					.openWidget(new FileSearchWidget(tabCallback.getInfo(), FolderViewWidget.this.file));
//		});

		btnHome.addActionListener(e -> {
			if (isLocal()) {
				history.addBack(FolderViewWidget.this.file);
				render(System.getProperty("user.home"));
			} else {
				history.addBack(FolderViewWidget.this.file);
				// render("/~");
				disableView();
				new Thread(() -> {
					try {
						ensureConnected();
						render(tabCallback.getFs().getHome());
					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, TextHolder
								.getString("folderview.genericError"));
					}
				}).start();
			}
		});

		resizeColumnWidth(folderTable);

		scrollTable = new JScrollPane(folderTable);
		scrollTable.getVerticalScrollBar()
				.setBackground(folderTable.getBackground());
		scrollTable.setViewportBorder(null);
		scrollTable.addMouseListener(new MouseAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.
			 * MouseEvent)
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("Mouse click on scrollpane");
				folderTable.clearSelection();
				if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
					menuHandler.createMenu(popup, getSelectedFiles());
					popup.pack();
					popup.show(folderTable, e.getX(), e.getY());
				}
			}
		});

		fileListModel = new TableListModel(folderTable);
		fileListView = new JList<>(fileListModel);
		fileListView.setCellRenderer(new ListViewRenderer());
		fileListView.setFixedCellWidth(Utility.toPixel(96));
		fileListView.setFixedCellHeight(Utility.toPixel(96));
		fileListView.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		fileListView.setVisibleRowCount(-1);
		scrollListView = new JScrollPane(fileListView);

		toggleView = new ViewTogglePanel(ViewMode.List);
		toggleView.setViewListener(e -> {
			updateContentView();
			revalidate();
			repaint();
		});

		JLabel lblCorner = new JLabel();
		lblCorner.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		scrollTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, lblCorner);
		scrollTable.setBorder(null);
		scrollTable.getViewport().setBackground(folderTable.getBackground());
		addressBox.add(btnBack);
		addressBox.add(btnForward);
		addressBox.add(btnHome);
		addressBox.add(btnUp);
		addressBox.add(txtAddressBar);
		addressBox.add(btnReload);
		addressBox.add(toggleView.getComponent());
		// addressBox.add(btnSearch);
		addressBox.add(btnMoreMenu);

		contentHolder = new JPanel(new BorderLayout());

		add(addressBox, BorderLayout.NORTH);
		// contentHolder.add(scrollTable);

		splitPane.setLeftComponent(navigationPanel);
		splitPane.setRightComponent(contentHolder);
		add(splitPane);

//		tabCallback.getAppListener().registerAppEventListener(this);

		createPopupMenu();
		createMoreMenu();

		this.selectionHelper = new FolderViewSelectionHelper(this, folderTable,
				folderViewModel, sorter);

		cmbView1.setSelectedIndex(
				getConfig().getFileBrowser().getSidePanelViewMode());

		resizeColumnWidth(folderTable);

		updateContentView();

		addAncestorListener(new AncestorListener() {

			@Override
			public void ancestorRemoved(AncestorEvent event) {
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
				System.out.println("Requesting focus on table");
				if (isShowing()) {
					focus();
				}
			}

			@Override
			public void ancestorAdded(AncestorEvent event) {
				System.out.println("Requesting focus on table");
				if (isShowing()) {
					focus();
				}
			}
		});

		reconnect();
	}

	public final void resizeColumnWidth(JTable table) {
		folderTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		final TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			// System.out.println("running..");
			TableColumn col = columnModel.getColumn(column);
//			col.getHeaderRenderer().getTableCellRendererComponent(table, col.getHeaderValue(),
//					false, false, 0, 0).getpre;
			if (column == 0) {
				col.setPreferredWidth(Utility.toPixel(200));
			} else if (column == 3) {
				col.setPreferredWidth(Utility.toPixel(150));
			} else {
				col.setPreferredWidth(Utility.toPixel(100));
			}
		}
	}

	public final boolean isLocal() {
		return tabCallback.getFs().isLocal();
	}

	private void up() {
		System.out.println(file);
		if (tabCallback.getFs().isLocal()) {
			String s = new File(file).getParent();
			if (s != null) {
				history.addBack(FolderViewWidget.this.file);
				render(s);
			}
		} else if (!file.equals("/")) {
			String parent = PathUtils.getParent(file);
			history.addBack(FolderViewWidget.this.file);
			render(parent);
		}

//		if(!file.equals("/")) {
//			String parent = PathUtils.getParent(file);
//			render(parent);
//		}
	}

	public final void reconnect() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ensureConnected();
					if (file == null || file.length() < 1) {
						file = tabCallback.getFs().getHome();
					}
					render(file);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null,
							TextHolder.getString("folderview.genericError"));
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}

	public void render(String f) {
		render(f, true);
	}

	public void render(String f, boolean enableCache) {
		disableView();
//		if (file != null && file.length() > 0) {
//			history.add(file);
//		}
		System.out.println("Render: " + f);
		if (f == null) {
			enableView();
			return;
		}
		// throw new NullPointerException("f can not be null at this point");
		folderTable.setEnabled(false);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					List<FileInfo> childs = enableCache ? cachedFolders.get(f)
							: null;
					if (childs == null) {
						ensureConnected();
						System.out.println("Listing file: " + f);
						childs = tabCallback.getFs().ll(f, dirOnly);
						System.out.println("Done Listing file: " + f);
						cachedFolders.put(f, childs);
					}

					final List<FileInfo> flist = childs;
					FolderViewWidget.this.file = f;

					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							txtAddressBar.setText(FolderViewWidget.this.file);
							folderViewModel.clear();
							// fileListModel.clear();

							for (FileInfo finfo : flist) {
								if (finfo.getName().startsWith(".")) {
									if (showingHiddenFiles) {
										folderViewModel.add(finfo);
										// fileListModel.addElement(finfo);
									}
								} else {
									folderViewModel.add(finfo);
									// fileListModel.addElement(finfo);
								}
							}

							folderTable.setEnabled(true);
							// System.out.println("Rendered");
							String title = "/"
									.equals(FolderViewWidget.this.file) ? "ROOT"
											: PathUtils.getFileName(
													FolderViewWidget.this.file);

							tabCallback.updateTitle(title,
									FolderViewWidget.this);
							
							fileListModel.refresh();

							renderTree();

							// history.add(file);
							updateNavButtons();
							lblDetails.setText(String.format("Total %d items",
									folderTable.getRowCount()));
							folderTable.requestFocus();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null,
							TextHolder.getString("folderview.genericError"));
				} finally {
					enableView();
				}
			}
		});
		t.setDaemon(true);
		t.start();

	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		FileInfo ent = folderViewModel.getItemAt(row);
		switch (column) {
		case 0:
			label.setIcon((ent.getType() == FileType.Directory
					|| ent.getType() == FileType.DirLink)
							? UIManager.getIcon("FileView.directoryIcon")
							: UIManager.getIcon("FileView.fileIcon"));
			label.setText(ent.getName());
			break;
		case 1:
			label.setIcon(null);
			label.setText(ent.getSize() + "");
			break;
		case 2:
			label.setIcon(null);
			label.setText(ent.getType() + "");
			break;
		case 3:
			label.setIcon(null);
			label.setText(ent.getLastModified().toString());
			break;
		default:
			break;
		}

		label.setBackground(isSelected ? table.getSelectionBackground()
				: table.getBackground());
		return label;
	}

//	public boolean handleRemoteFileDrop(TransferFileInfo infoList,
//			String file) {
//		System.out.println("called1");
//		System.out.println("Remote files dropped: " + infoList + " in "
//				+ (tabCallback.getFs().isLocal() ? "local browser"
//						: "remote browser"));
//		infoList.setBaseFolder(file);
//		addSftpRemote2Local(infoList);
//		return true;
//	}

//	public boolean handleRemoteFileDrop(TransferFileInfo infoList) {
//		return handleRemoteFileDrop(infoList, file);
//	}
//
//	public boolean handleLocalFileDrop(TransferFileInfo infoList, String file) {
//		System.out.println("called2");
//		System.out.println("Localfiles dropped: " + infoList + " in "
//				+ (tabCallback.getFs().isLocal() ? "local browser"
//						: "remote browser"));
//		infoList.setBaseFolder(file);
//		if (infoList.getInfo().size() > 1) {
//			addSftpRemote2Remote(infoList);
//		} else {
//			addSftpLocal2Remote(infoList);
//		}
//		return true;
//	}

//	public boolean handleLocalFileDrop(TransferFileInfo infoList) {
//		return handleLocalFileDrop(infoList, file);
//	}

	public FileInfo[] getSelectedFiles() {
		if (toggleView.getViewMode() == ViewMode.Details) {
			FileInfo fs[] = new FileInfo[folderTable.getSelectedRows().length];
			int rows[] = folderTable.getSelectedRows();
			for (int i = 0; i < rows.length; i++) {
				fs[i] = folderViewModel.getItemAt(getRow(rows[i]));
			}
			return fs;
		} else {
			FileInfo fs[] = new FileInfo[fileListView
					.getSelectedIndices().length];
			int rows[] = fileListView.getSelectedIndices();
			for (int i = 0; i < rows.length; i++) {
				fs[i] = fileListModel.getElementAt(rows[i]);// folderViewModel.getItemAt(getRow(rows[i]));
			}
			return fs;
		}
	}

	private boolean shouldRender(String name) {
		String name1 = new File(name).getParent();
		System.out.println("should render: " + name1);
		return this.file.equals(name1);
	}

	@Override
	public void onEvent(long eventId, Object eventData) {
		try {
			if (eventId == Constants.DOWNLOAD_FINISHED) {
				System.out.println("Download complete event received");

				Properties data = (Properties) eventData;
				String src = data.getProperty("download.srcfile");
				String dst = data.getProperty("download.dstfile");

				if (src != null) {
					if (shouldRender(src)) {
						render(this.file, false);
						return;
					}
				}

				if (dst != null) {
					if (shouldRender(dst)) {
						render(this.file, false);
						return;
					}
				}

//				if (!(this.hashCode() + "")
//						.equals(data.getProperty("download.id"))) {
//					return;
//				}
//				String name = null;
//				if ("folder".equals(data.getProperty("download.type"))) {
//					name = new File(file).getName();
//				} else {
//					name = new File(file).getParentFile().getName();
//				}
//				if (new File(this.file).getName().equals(name)) {
//					render(this.file, false);
//				}
//				if (this.getWrapper() != null) {
//					if (this.getWrapper().getInfo().getHost().equals(host)) {
//						String folder = null;
//						if ("folder"
//								.equals(data.getProperty("download.type"))) {
//							folder = file;
//						} else {
//							folder = PathUtils.getParent(file);
//						}
//						System.out.println("download notify for: " + folder);
//						if (this.file.equals(folder)) {
//							render(folder, false);
//						}
//					}
//				} else {
//					String folder = null;
//					if ("folder".equals(data.getProperty("download.type"))) {
//						folder = file;
//					} else {
//						folder = PathUtils.getParent(file);
//					}
//					System.out.println("download notify for: " + folder);
//					if (this.file.equals(folder)) {
//						render(folder, false);
//					}
//				}
			}
			if (eventId == Constants.FILE_COPY) {
				Properties data = (Properties) eventData;
				String file = data.getProperty("copy.file");

				if (tabCallback.getFs().isLocal()) {
					String folder = PathUtils.getParent(file);
					if (this.file.equals(folder)) {
						render(file, false);
					}
				}
			}

			if (eventId == Constants.FILE_ADDED) {
				System.out.println("File added event received");

				Properties data = (Properties) eventData;
				String src = data.getProperty("event.file");

				if (this.file.equals(src)) {
					render(this.file, false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createPopupMenu() {
		popup = new JPopupMenu();
		popup.setInvoker(folderTable);

		popup3 = new JPopupMenu();
		popup3.setInvoker(tree);
	}

	private TransferFileInfo createTransferInfo() {
		if (isLocal()) {
			TransferFileInfo sf = new TransferFileInfo();
			List<String> filelist = new ArrayList<>();
			List<String> folderlist = new ArrayList<>();
			for (FileInfo f : getSelectedFiles()) {
				if (f.getType() == FileType.Directory) {
					folderlist.add(f.getPath());
				} else {
					filelist.add(f.getPath());
				}
			}
			sf.setSourceFiles(filelist);
			sf.setSourceFolders(folderlist);
			return sf;
		} else {
			TransferFileInfo sf = new TransferFileInfo();
			sf.addInfo(getTabCallback().getInfo());
			List<String> filelist = new ArrayList<>();
			List<String> folderlist = new ArrayList<>();
			for (FileInfo f : getSelectedFiles()) {
				if (f.getType() == FileType.Directory) {
					folderlist.add(f.getPath());
				} else {
					filelist.add(f.getPath());
				}
			}
			sf.setSourceFiles(filelist);
			sf.setSourceFolders(folderlist);
			return sf;
		}

	}

	protected void copyToClipboard(boolean cut) {
		TransferFileInfo info = createTransferInfo();
		if (info != null) {
			info.setAction(cut ? TransferFileInfo.Action.CUT
					: TransferFileInfo.Action.COPY);
			AppClipboard.setContent(info);

		}
	}

	protected void newFile() {

	}

	protected void newFolder() {
		String text = JOptionPane.showInputDialog(
				TextHolder.getString("folderview.renameTitle"));
		if (text != null && text.length() > 0) {
			disableView();
			new Thread(() -> {
				try {
					ensureConnected();
					tabCallback.getFs().mkdir(PathUtils.combine(file, text,
							isLocal() ? File.separator : "/"));
					render(file, false);
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null,
							TextHolder.getString("folderview.genericError"));
				}
			}).start();
		}
	}

	private void createMenuItem(ContextMenuEntry ent, JPopupMenu popup) {
		System.out.println(ent);
		JMenuItem item = new JMenuItem(ent.getMenuText());
		item.addActionListener(e -> {
			for (ContextMenuEntry c : ContextMenuRegistry.getEntryList()) {
				if (c == ent) {
					try {
						AppSession desktop = tabCallback.getSession();
						if (desktop != null) {
							int[] selectedRows = folderTable.getSelectedRows();
							List<String> argsList = new ArrayList<>();
							for (int i = 0; i < ent.getArgs().length; i++) {
								String arg = ent.getArgs()[i];
								if ("%d".equals(arg)) {
									argsList.add(file);
								} else if ("%f".equals(arg)) {
									for (int j = 0; j < selectedRows.length; j++) {
										int index = selectedRows[j];
										FileInfo info = folderViewModel
												.getItemAt(getRow(index));
										if (!info.getProtocol()
												.equals(ent.getProtocol())) {
											System.out.println("returing.. "
													+ info.getProtocol() + "-"
													+ ent.getProtocol());
											return;
										}
										argsList.add(info.getPath());
									}
								} else {
									argsList.add(arg);
								}
							}
							String[] a = new String[argsList.size()];
							a = argsList.toArray(a);
							desktop.createWidget(ent.getClassName(), a);
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					break;
				}
			}
		});
		popup.add(item);
	}

	private void createBuitinItems1(int selectionCount) {
		popup.add(mOpen);
		if (selectionCount == 1) {
//			int row = getRow(folderTable.getSelectedRow());
//			FileInfo info = folderViewModel.getItemAt(getRow(row));
//			if (info.getType() == FileType.Directory
//					|| info.getType() == FileType.DirLink) {
//				popup.add(mOpen);
//			}

			popup.add(mRename);
		}

		if (selectionCount > 0) {
			popup.add(mCopy);
			popup.add(mCut);
		}

		if (AppClipboard.getContent() instanceof TransferFileInfo) {
			popup.add(mPaste);
		}
	}

	private void createBuitinItems2(int selectionCount) {
		if (selectionCount > 0) {
			popup.add(mDelete);
		}
		popup.add(mNewFolder);
		popup.add(mNewFile);
		// check only if folder is selected
		popup.add(mAddToFav);

		if (selectionCount == 1 && (getSelectedFiles()[0]
				.getType() == FileType.File
				|| getSelectedFiles()[0].getType() == FileType.FileLink)) {
			popup.add(mEditExtern);
			popup.add(mOpenExtern);
		}
		if (!isLocal()) {
			if (selectionCount >= 1) {
				popup.add(mChangePerm);
			}
		}
	}

	private void createMenuContext() {
		popup.removeAll();
		ListSelectionModel sm = folderTable.getSelectionModel();
		if (sm.getValueIsAdjusting() || embedded) {
			return;
		}

		int selectionCount = folderTable.getSelectedRowCount();
		createBuitinItems1(selectionCount);

		for (ContextMenuEntry ent : ContextMenuRegistry.getEntryList()) {
			// System.out.println(ent);
			if (selectionCount > 1) {
				if (ent.isSupportsMultipleItems()) {
					createMenuItem(ent, popup);
				}
			} else if (selectionCount == 1) {
				int row = getRow(folderTable.getSelectedRow());
				FileInfo info = folderViewModel.getItemAt(row);
				System.out.println(info);
				if (!info.getProtocol().equals(ent.getProtocol())) {
					continue;
				}
				if (info.getType() == FileType.Directory
						|| info.getType() == FileType.DirLink) {
					if (ent.isFolderSupported()) {
						createMenuItem(ent, popup);
					}
				} else {
					String path = info.getPath();
					String[] supportedExts = ent.getFileExt();
					if (supportedExts.length > 0) {
						boolean supported = false;
						for (String ext : supportedExts) {
							if (ext.length() == 0 || path.endsWith(ext)) {
								supported = true;
								break;
							}
						}
						if (!supported) {
							continue;
						}
						createMenuItem(ent, popup);
					}
				}
			} else {
				if (ent.isSupportsEmptySelection()) {
					createMenuItem(ent, popup);
				}
			}
		}

		createBuitinItems2(selectionCount);

//		if (sm.isSelectionEmpty()) {
//			popup.add(mOpen);
//			popup.add(mOpenTerm);
//		} else if (folderTable.getSelectedRowCount() == 1) {
//			int x = folderTable.getSelectedRow();
//			FileInfo info = folderViewModel.getItemAt(getRow(x));
//			if (info.getType() == FileType.Directory) {
//				popup.add(mOpen);
//				popup.add(mOpenTerm);
//				popup.add(mCompress);
//			} else {
//				popup.add(mEdit);
//				popup.add(mLogViewer);
//				popup.add(mRunInTerm);
//				popup.add(mRunWithArgs);
//				popup.add(mCompress);
//
//				int index = info.getName().lastIndexOf('.');
//				if (index != -1) {
//					String ext = info.getName().substring(index);
//					if (ArchiveExtractWidget.getExtractCmd(ext) != null) {
//						popup.add(mExtract);
//						popup.add(mExtractTo);
//					}
//				}
//			}
//		} else {
//			popup.add(mCompress);
//		}
//		popup.add(mOpen);
//		popup.add(mEdit);
//		popup.add(mOpenTerm);
//		popup.add(mRunInTerm);
//		popup.add(mRunWithArgs);
//		popup.add(mCompress);
//		popup.add(mExtractTo);
//
//		popup.add(mExtract);
	}

//	public int promptDuplicate(String fileName) {
//		JCheckBox chkApplyAction = new JCheckBox(
//				TextHolder.getString("duplicate.apply"));
//		chkApplyAction.addActionListener(e -> {
//			applyPreviousAction = chkApplyAction.isSelected();
//		});
//
//		return JOptionPane.showInternalOptionDialog(this,
//				new Object[] {
//						String.format(TextHolder.getString("duplicate.prompt"),
//								fileName),
//						chkApplyAction },
//				TextHolder.getString("duplicate.confirm"),
//				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
//				new Object[] { TextHolder.getString("duplicate.overwrite"),
//						TextHolder.getString("duplicate.skip"),
//						TextHolder.getString("duplicate.rename"),
//						TextHolder.getString("duplicate.cancel") },
//				null);
//	}

//	public String autoRename(String name) {
//		String newName = name;
//		boolean moreCheck = true;
//		while (moreCheck) {
//			newName = "copy_of_" + newName;
//			moreCheck = false;
//			for (int i = 0; i < folderViewModel.getRowCount(); i++) {
//				FileInfo info = folderViewModel.getItemAt(i);
//				String n = info.getName();
//				if (n.equals(newName)) {
//					moreCheck = true;
//					break;
//				}
//			}
//		}
//		return newName;
//	}

//	private void addSftpLocal2Remote(TransferFileInfo data) {
//		this.applyPreviousAction = false;
//		int resp = -1;
//		List<String> folders = data.getSourceFolders();
//		List<String> files = data.getSourceFiles();
//		String baseRemoteFolder = data.getBaseFolder();
//
//		if (files != null && files.size() > 0) {
//			for (String f : files) {
//				outer: {
//					File localFile = new File(f);
//					String fileName = localFile.getName();
//					for (int i = 0; i < folderViewModel.getRowCount(); i++) {
//						FileInfo info = folderViewModel.getItemAt(i);
//						String n = info.getName();
//						if (n.equals(fileName)) {
//							System.out.println(
//									"File '" + n + "' already exists...");
//							if (!applyPreviousAction) {
//								resp = promptDuplicate(fileName);
//							}
//							System.out.println("Resp: " + resp);
//							if (resp == 1) {
//								System.out.println("skipped");
//								break outer;
//							} else if (resp == 2) {
//								// do the rename stuff
//								fileName = autoRename(fileName);
//							} else if (resp != 0) {
//								return;
//							}
//						}
//
//					}
//
//					String remoteFile = PathUtils.combineUnix(baseRemoteFolder,
//							fileName);
//					System.out
//							.println("Adding files for upload: " + remoteFile);
//					try {
//						BasicFileUploader fd = new BasicFileUploader(
//								data.getInfo().get(0), remoteFile,
//								localFile.getAbsolutePath(),
//								tabCallback.getDesktop().getBgTransferQueue());
//
//						tabCallback.getAppListener().getTransferWatcher()
//								.addTransfer(fd);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//
//		if (folders != null && folders.size() > 0) {
//			for (String f : folders) {
//				File localFolder = new File(f);
//				outer: {
//					String folderName = localFolder.getName();
//
//					for (int i = 0; i < folderViewModel.getRowCount(); i++) {
//						FileInfo info = folderViewModel.getItemAt(i);
//						String n = info.getName();
//						if (n.equals(folderName)) {
//							System.out.println(
//									"File '" + n + "' already exists...");
//							if (!applyPreviousAction) {
//								resp = promptDuplicate(folderName);
//							}
//							System.out.println("Resp: " + resp);
//							if (resp == 1) {
//								System.out.println("skipped");
//								break outer;
//							} else if (resp == 2) {
//								// do the rename stuff
//								folderName = autoRename(folderName);
//							} else if (resp != 0) {
//								return;
//							}
//						}
//
//					}
//
//					String remoteFolder = PathUtils
//							.combineUnix(baseRemoteFolder, folderName);
//
//					System.out
//							.println("Adding folder for upload: " + folderName);
//
//					DirectoryUploader dd = new DirectoryUploader(
//							data.getInfo().get(0),
//							localFolder.getAbsolutePath(), remoteFolder,
//							tabCallback.getDesktop().getBgTransferQueue());
//					tabCallback.getAppListener().getTransferWatcher()
//							.addTransfer(dd);
//				}
//			}
//		}
//
//	}
//
//	private void addSftpRemote2Local(TransferFileInfo data) {
//		List<String> folders = data.getSourceFolders();
//		List<String> files = data.getSourceFiles();
//		String baseLocalFolder = data.getBaseFolder();
//		applyPreviousAction = false;
//		int resp = -1;
//		if (files != null && files.size() > 0) {
//			for (String f : files) {
//				String fileName = PathUtils.getFileName(f);
//				outer: {
//					for (int i = 0; i < folderViewModel.getRowCount(); i++) {
//						FileInfo info = folderViewModel.getItemAt(i);
//						String n = info.getName();
//						if (n.equals(fileName)) {
//							System.out.println(
//									"File '" + n + "' already exists...");
//							if (!applyPreviousAction) {
//								resp = promptDuplicate(fileName);
//							}
//							System.out.println("Resp: " + resp);
//							if (resp == 1) {
//								System.out.println("skipped");
//								break outer;
//							} else if (resp == 2) {
//								// do the rename stuff
//								fileName = autoRename(fileName);
//							} else if (resp != 0) {
//								return;
//							}
//						}
//
//					}
//
//					System.out.println("Adding files for download: " + f);
//					try {
//						BasicFileDownloader fd = new BasicFileDownloader(
//								data.getInfo().get(0), f,
//								new File(baseLocalFolder, fileName)
//										.getAbsolutePath(),
//								tabCallback.getDesktop().getBgTransferQueue());
//
//						tabCallback.getDesktop().getTransferWatcher()
//								.addTransfer(fd);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//
//		if (folders != null && folders.size() > 0) {
//			for (String f : folders) {
//				outer: {
//					String folderName = PathUtils.getFileName(f);
//
//					for (int i = 0; i < folderViewModel.getRowCount(); i++) {
//						FileInfo info = folderViewModel.getItemAt(i);
//						String n = info.getName();
//						if (n.equals(folderName)) {
//							System.out.println(
//									"File '" + n + "' already exists...");
//							if (!applyPreviousAction) {
//								resp = promptDuplicate(folderName);
//							}
//							System.out.println("Resp: " + resp);
//							if (resp == 1) {
//								System.out.println("skipped");
//								break outer;
//							} else if (resp == 2) {
//								// do the rename stuff
//								folderName = autoRename(folderName);
//							} else if (resp != 0) {
//								return;
//							}
//						}
//
//					}
//
//					DirectoryDownloader dd = new DirectoryDownloader(
//							data.getInfo().get(0),
//							new File(baseLocalFolder, folderName)
//									.getAbsolutePath(),
//							f, tabCallback.getDesktop().getBgTransferQueue());
//					tabCallback.getAppListener().getTransferWatcher()
//							.addTransfer(dd);
//				}
//			}
//		}
//	}
//
//	private void addSftpRemote2Remote(TransferFileInfo data) {
//		applyPreviousAction = true;
//		List<String> folders = data.getSourceFolders();
//		List<String> files = data.getSourceFiles();
//		String targetFolder = data.getBaseFolder();
//		int resp = -1;
//		if (files != null && files.size() > 0) {
//			for (String f : files) {
//				String fileName = PathUtils.getFileName(f);
//				outer: {
//					for (int i = 0; i < folderViewModel.getRowCount(); i++) {
//						FileInfo info = folderViewModel.getItemAt(i);
//						String n = info.getName();
//						if (n.equals(fileName)) {
//							System.out.println(
//									"File '" + n + "' already exists...");
//							if (!applyPreviousAction) {
//								resp = promptDuplicate(fileName);
//							}
//							System.out.println("Resp: " + resp);
//							if (resp == 1) {
//								System.out.println("skipped");
//								break outer;
//							} else if (resp == 2) {
//								// do the rename stuff
//								fileName = autoRename(fileName);
//							} else if (resp != 0) {
//								return;
//							}
//						}
//
//					}
//
//					System.out.println("Adding files for download: " + f);
//					try {
//						BasicFileSender fd = new BasicFileSender(
//								data.getInfo().get(0), data.getInfo().get(1), f,
//								PathUtils.combineUnix(targetFolder, fileName),
//								tabCallback.getDesktop().getBgTransferQueue());
//
//						tabCallback.getAppListener().getTransferWatcher()
//								.addTransfer(fd);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//
//		if (folders != null && folders.size() > 0) {
//			for (String f : folders) {
//				outer: {
//					String folderName = PathUtils.getFileName(f);
//
//					for (int i = 0; i < folderViewModel.getRowCount(); i++) {
//						FileInfo info = folderViewModel.getItemAt(i);
//						String n = info.getName();
//						if (n.equals(folderName)) {
//							System.out.println(
//									"File '" + n + "' already exists...");
//							if (!applyPreviousAction) {
//								resp = promptDuplicate(folderName);
//							}
//							System.out.println("Resp: " + resp);
//							if (resp == 1) {
//								System.out.println("skipped");
//								break outer;
//							} else if (resp == 2) {
//								// do the rename stuff
//								folderName = autoRename(folderName);
//							} else if (resp != 0) {
//								return;
//							}
//						}
//					}
//
//					DirectorySender dd = new DirectorySender(
//							data.getInfo().get(0), data.getInfo().get(1), f,
//							PathUtils.combineUnix(targetFolder, folderName),
//							tabCallback.getDesktop().getBgTransferQueue());
//					tabCallback.getAppListener().getTransferWatcher()
//							.addTransfer(dd);
//				}
//			}
//		}
//
//	}

	public boolean isEmbedded() {
		return embedded;
	}

	public void setEmbedded(boolean embedded) {
		this.embedded = embedded;
	}

	public boolean isDirOnly() {
		return dirOnly;
	}

	public void setDirOnly(boolean dirOnly) {
		this.dirOnly = dirOnly;
	}

	public void setSelectionCallback(SelectionCallback selectionCallback) {
		this.selectionCallback = selectionCallback;
	}

	private void disableView() {
//		System.out.println("disabling");
//		this.txtAddressBar.setEnabled(false);
//		this.btnUp.setEnabled(false);
//		this.folderTable.setEnabled(false);
		SwingUtilities.invokeLater(() -> {
			tabCallback.disableUI();
		});

	}

	private void enableView() {
		SwingUtilities.invokeLater(() -> {
			tabCallback.enableUI();
		});
//		System.out.println("enabling");
//		this.txtAddressBar.setEnabled(true);
//		this.btnUp.setEnabled(true);
//		this.folderTable.setEnabled(true);

	}

	private int getRow(int r) {
		if (r == -1) {
			return -1;
		}
		return folderTable.convertRowIndexToModel(r);
	}

	private int[] getRows(int[] r) {
		int rs[] = new int[r.length];
		for (int i = 0; i < r.length; i++) {
			rs[i] = folderTable.convertRowIndexToModel(r[i]);
		}
		return rs;
	}

	public TabCallback getTabCallback() {
		return tabCallback;
	}

	private void renderTree() {
		treeLoading = true;
		try {
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree
					.getModel().getRoot();
			DefaultMutableTreeNode node = root;
			String[] arr = file.split("[\\/\\\\]");
			for (int i = 0; i < arr.length; i++) {
				String s = arr[i];
				System.out.println("Node to traverse: '" + s + "'");
				if (s.length() > 0) {
					node = getChild(node, s);
				}
			}

			// Enumeration<TreePath>expandedChilds=

//			System.out.println(
//					tree.getExpandedDescendants(new TreePath(node.getPath())));

			TreePath path = new TreePath(treeModel.getPathToRoot(node));
			tree.expandPath(path);
			tree.setSelectionPath(path);
			tree.scrollPathToVisible(path);

			for (int i = 0; i < folderViewModel.getRowCount(); i++) {
				FileInfo info = folderViewModel.getItemAt(i);
				if (info.getType() == FileType.Directory
						|| info.getType() == FileType.DirLink) {
					boolean exists = false;
					for (int j = 0; j < node.getChildCount(); j++) {
						DefaultMutableTreeNode child = (DefaultMutableTreeNode) node
								.getChildAt(j);
						if (info.getName().equals(child.getUserObject())) {
//							System.out.println("Node already exists: "
//									+ info.getName() + " under: "
//									+ node.getUserObject());
							exists = true;
							break;
						}
					}
					if (!exists) {
						int index = node.getChildCount();
						DefaultMutableTreeNode c = new DefaultMutableTreeNode(
								info.getName());
						c.setAllowsChildren(true);
						treeModel.insertNodeInto(c, node, index);

						// node.add(c);
						// treeModel.nodesWereInserted(node, childIndices);
					}
				}
			}

			// treeModel.nodeStructureChanged(node);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		System.out.println("Tree selected");
	}

	// private

	private String getFilePath(TreePath path) {
		if (path == null) {
			return null;
		}
		String selectedPath = FolderViewUtility.getFilePath(tree, path,
				isLocal());
		if (selectedPath == null) {
			return null;
		}
		if (file.equals(selectedPath)) {
			return null;
		}
		return selectedPath;
	}

	private void renameAsync(String oldName, String newName) {
		disableView();
		new Thread(() -> {
			try {
				ensureConnected();
				tabCallback.getFs().rename(oldName, newName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				if (tabCallback.getFs().getProtocol()
						.equals(SshFileSystemProvider.PROTO_SFTP)) {
					SwingUtilities.invokeLater(() -> {
						String suCmd = AskForPriviledgeDlg.askForPriviledge();
						StringBuilder command = new StringBuilder();
						command.append(suCmd + " ");
						boolean sudo = false;
						sudo = suCmd.startsWith("sudo");
						if (!sudo) {
							command.append("'");
						}
						command.append(
								"mv \"" + oldName + "\" \"" + newName + "\"");
						if (!sudo) {
							command.append("; exit'");
						}
						System.out.println("Command: " + command);
						String[] args = new String[2];
						args[0] = "-c";
						args[1] = command.toString();
						tabCallback.openTerminal(command.toString());
					});
				} else {
					SwingUtilities.invokeLater(() -> {
						JOptionPane.showMessageDialog(null, TextHolder
								.getString("folderview.renameFailed"));
						enableView();
						return;
					});
				}
				return;
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						TextHolder.getString("folderview.genericError"));
			} finally {
				enableView();
			}
		}).start();
	}

	private void rename() {
		if (folderTable.getSelectedRowCount() < 1) {
			return;
		}
		int x = folderTable.getSelectedRow();
		int row = getRow(x);

		FileInfo info = folderViewModel.getItemAt(row);

		String text = JOptionPane
				.showInputDialog(TextHolder.getString("folderview.renameTitle")
						+ "\n" + info.getName());
		if (text != null && text.length() > 0) {
			renameAsync(info.getPath(), isLocal()
					? PathUtils.combine(PathUtils.getParent(info.getPath()),
							text, File.separator)
					: PathUtils.combineUnix(PathUtils.getParent(info.getPath()),
							text));
		}
	}

//	public boolean checkFile(String targetFolder, List<String> sourceFiles,
//			List<FileInfo> list, Map<String, String> mvMap) {
//		int resp = -1;
//		for (String file : sourceFiles) {
//			boolean duplicate = false;
//			for (int i = 0; i < list.size(); i++) {
//				String name1 = list.get(i).getName();
//				String name2 = PathUtils.getFileName(file);
//				if (name1.equals(name2)) {
//					duplicate = true;
//					break;
//				}
//				boolean skip = false;
//				if (duplicate) {
//					if (!applyPreviousAction) {
//						resp = promptDuplicate(name1);
//					}
//					System.out.println("Resp: " + resp);
//					if (resp == 1) {
//						System.out.println("skipped");
//						skip = true;
//					} else if (resp == 2) {
//						// do the rename stuff
//						name2 = autoRename(name2);
//					} else if (resp != 0) {
//						return false;
//					}
//				}
//				if (!skip) {
//					mvMap.put(file, PathUtils.combine(targetFolder, name2,
//							isLocal() ? File.separator : "/"));
//				}
//			}
//		}
//		return true;
//
//	}

	public SessionInfo getSessionInfo() {
		return tabCallback.getInfo();
	}

//	public void moveFiles(String targetFolder, List<String> sourceFiles,
//			List<String> sourceFolders, boolean copy) {
//		disableView();
//		new Thread(() -> {
//			try {
//				System.out.println("Moving...");
//				applyPreviousAction = false;
//				ensureConnected();
//				List<FileInfo> list = tabCallback.getFs().ll(targetFolder,
//						false);
//				Map<String, String> mvMap = new HashMap<>();
//				if (!checkFile(targetFolder, sourceFiles, list, mvMap)) {
//					System.out.println("Returing...");
//					return;
//				}
//
//				if (!checkFile(targetFolder, sourceFolders, list, mvMap)) {
//					System.out.println("Returing...");
//					return;
//				}
//
//				for (String key : mvMap.keySet()) {
//					System.out.println(
//							"Moving file: " + key + " -> " + mvMap.get(key));
//					if (copy) {
//						if (isLocal()) {
//							Copy.copy(new String[] { "-r", "-p", key,
//									mvMap.get(key) });
//						} else {
//							ShellActions.copy(key, mvMap.get(key),
//									((RemoteFolderViewWidget) tabCallback
//											.getWidget()).getWrapper());
//						}
//					} else {
//						tabCallback.getFs().rename(key, mvMap.get(key));
//					}
//				}
//
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				MessageDialog.show(tabCallback.getWidget(),
//						TextHolder.getString("folderview.genericError"),
//						MessageDialog.MessageType.Ok);
//			} finally {
//				enableView();
//			}
//		}).start();
//	}

	private void updateNavButtons() {
		btnBack.setEnabled(history.hasPrevElement());
		btnForward.setEnabled(history.hasNextElement());
	}

//	private void copyLocal(TransferFileInfo info) {
//		copy(info, info.getAction() == Action.COPY);
//	}

//	private void copy(TransferFileInfo info, boolean copy) {
//		info.setBaseFolder(file);
//		List<String> droppedFiles = info.getSourceFiles();
//		List<String> droppedFolders = info.getSourceFolders();
//		System.out.println("copying files to: " + file + " dropped files: "
//				+ droppedFiles + " dropped folders: " + droppedFolders);
//		moveFiles(file, droppedFiles, droppedFolders, copy);
//	}

//	private void pasteItem(TransferFileInfo info) {
//		if (isLocal()) {
//			if (info.getInfo() == null || info.getInfo().size() == 0) {
//				copyLocal(info);
//			} else {
//				// download drop
//				handleRemoteFileDrop(info);
//			}
//		} else {
//			if (info.getInfo() == null || info.getInfo().size() == 0) {
//				// initiate upload
//				info.addInfo(getSessionInfo());
//				handleLocalFileDrop(info);
//			} else {
//				if (FolderViewUtility.sameSession(getSessionInfo(),
//						info.getInfo().get(0))) {
//					copy(info, info.getAction() == Action.COPY);
//				} else {
//					info.addInfo(getSessionInfo());
//					handleLocalFileDrop(info);
//				}
//			}
//		}
//	}

	private void selectRow(MouseEvent e) {
		int r = folderTable.rowAtPoint(e.getPoint());
		System.out.println("Row at point: " + r);
		if (r == -1) {
			folderTable.clearSelection();
		} else {
			if (folderTable.getSelectedRowCount() > 0) {
				int[] rows = folderTable.getSelectedRows();
				for (int row : rows) {
					if (r == row) {
						return;
					}
				}
			}
			folderTable.setRowSelectionInterval(r, r);
		}
	}

	private void loadFavourites() {
		List<String> favourites = isLocal()
				? SessionStore.getSharedInstance().getLocalFolders()
				: getSessionInfo().getFavouriteFolders();
		modelPlaces.removeAllElements();
		for (String f : favourites) {
			modelPlaces.addElement(
					new FavouritePlaceEntry(f, PathUtils.getFileName(f)));
		}
	}

	public void loadFavourites(List<String> favourites) {
		modelPlaces.removeAllElements();
		for (String f : favourites) {
			modelPlaces.addElement(
					new FavouritePlaceEntry(f, PathUtils.getFileName(f)));
		}
	}

	private void addBookmark(String str) {
		if (!isLocal()) {
			getSessionInfo().getFavouriteFolders().add(str);
		} else {
			SessionStore.getSharedInstance().getLocalFolders().add(str);
		}
	}

	protected void addToFavourites() {
		FileInfo[] files = getSelectedFiles();
		if (files.length == 0) {
			addBookmark(file);
		} else {
			for (FileInfo f : files) {
				if (f.getType() == FileType.Directory
						|| f.getType() == FileType.DirLink) {
					addBookmark(f.getPath());
				}
			}
		}
		loadFavourites();
		SessionStore.getSharedInstance().save(null);
	}

	protected void editExternal() {
		if (!isLocal()) {
			FileInfo[] files = getSelectedFiles();
			if (files.length == 1 && (files[0].getType() == FileType.File
					|| files[0].getType() == FileType.FileLink)) {
//				tabCallback.getDesktop().getEditorHelper()
//						.openForEdit(files[0].getPath());
			}
		} else {
			//
		}
	}

	protected void openExternal() {
		if (!isLocal()) {
			FileInfo[] files = getSelectedFiles();
			if (files.length == 1 && (files[0].getType() == FileType.File
					|| files[0].getType() == FileType.FileLink)) {
//				tabCallback.getDesktop().getEditorHelper()
//						.openDefault(files[0].getPath());
			}
		} else {
			//
		}
	}

	private void hideSidePane() {
		if (!noSidePane) {
			dividerPos = splitPane.getDividerLocation();
			splitPane.remove(contentHolder);
			remove(splitPane);
			add(contentHolder);
			noSidePane = true;
		}
	}

	private void showSidePane() {
		if (noSidePane) {
			remove(contentHolder);
			splitPane.setRightComponent(contentHolder);
			add(splitPane);
			splitPane.setDividerLocation(dividerPos);
			noSidePane = false;
		}
	}

	private void ensureConnected() throws Exception {
		if (!tabCallback.getFs().isConnected()) {
			System.out.println("reconnecting again");
			tabCallback.reconnectFs();
		}
	}

//	private void toggleSidePane() {
//		if (!noSidePane) {
//			remove(contentHolder);
//			splitPane.setRightComponent(contentHolder);
//			add(splitPane);
//			btnSplit.setIcon(UIManager.getIcon("AddressBar.split1"));
//			splitPane.setDividerLocation(dividerPos);
//		} else {
//			dividerPos = splitPane.getDividerLocation();
//			splitPane.remove(contentHolder);
//			remove(splitPane);
//			add(contentHolder);
//			btnSplit.setIcon(UIManager.getIcon("AddressBar.split2"));
//		}
//	}

	private void changePermission() {
		PermissionsDialog pdlg = new PermissionsDialog(
				tabCallback.getWidget().getWindow(),
				folderTable.getSelectedRowCount() == 1);
		int rc = folderTable.getSelectedRowCount();
		if (rc < 1) {
			return;
		}
		if (rc == 1) {
			pdlg.setDetails(getSelectedFiles()[0]);
		} else {
			pdlg.setMultipleDetails(getSelectedFiles());
		}
		pdlg.setVisible(true);
		if (pdlg.getDialogResult() == JOptionPane.OK_OPTION) {
			int perm = pdlg.getPermissions();
			String[] paths = new String[folderTable.getSelectedRowCount()];
			int i = 0;
			for (int r : folderTable.getSelectedRows()) {
				int row = getRow(r);
				FileInfo info = folderViewModel.getItemAt(row);
				paths[i++] = info.getPath();
			}
			chmodAsync(perm, paths);
		}
	}

	private void chmodAsync(int perm, String paths[]) {
		disableView();
		new Thread(() -> {
			try {
				ensureConnected();
				for (String path : paths) {
					tabCallback.getFs().chmod(perm, path);
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						TextHolder.getString("folderview.genericError"));
			}

			enableView();
		}).start();
	}

	public String getCurrentPath() {
		return file;
	}

	public List<FileInfo> getCurrentFiles() {
		List<FileInfo> files = new ArrayList<>();
		for (int i = 0; i < folderViewModel.getRowCount(); i++) {
			files.add(folderViewModel.getItemAt(i));
		}
		return files;
	}

	private void createViewComboBox() {
		cmbView1 = new JComboBox<>(new String[] { "Tree view", "Favourites" });
//		cmbView1.setBorder(new MatteBorder(0, 0, 0,
//				Utility.toPixel(1), UIManager.getColor("DefaultBorder.color")));
		cmbView1.setPreferredSize(
				new Dimension(Utility.toPixel(150), Utility.toPixel(25)));
		cmbView1.setBorder(null);
		cmbView1.setBorder(new MatteBorder(0, 0, Utility.toPixel(1), 0,
				UIManager.getColor("DefaultBorder.color")));
		cmbView1.addActionListener(e -> {
			// int loc = splitPane.getDividerLocation();
			if (cmbView1.getSelectedIndex() == 0) {
				// showSidePane();
				navigationPanel.removeAll();
				navigationPanel.add(cmbView1, BorderLayout.NORTH);
				navigationPanel.add(treeScroll);
//				splitPane.setLeftComponent(treeScroll);
//				splitPane.setDividerLocation(loc);
			} else if (cmbView1.getSelectedIndex() == 1) {
				// showSidePane();
//				splitPane.setLeftComponent(listScroll);
//				splitPane.setDividerLocation(loc);
				navigationPanel.removeAll();
				navigationPanel.add(cmbView1, BorderLayout.NORTH);
				navigationPanel.add(listScroll);
			}
//			else {
//				hideSidePane();
//			}
			repaint();
			revalidate();
		});

//		cmbView1.addActionListener(e -> {
//			int loc = splitPane.getDividerLocation();
//			if (cmbView1.getSelectedIndex() == 0) {
//				showSidePane();
//				splitPane.setLeftComponent(treeScroll);
//				splitPane.setDividerLocation(loc);
//			} else if (cmbView1.getSelectedIndex() == 1) {
//				showSidePane();
//				splitPane.setLeftComponent(listScroll);
//				splitPane.setDividerLocation(loc);
//			} else {
//				hideSidePane();
//			}
//			repaint();
//			revalidate();
//		});
	}

	/**
	 * 
	 */
	private void showHideSidebar() {
		if (chkSideNav.isSelected()) {
			showSidePane();
		} else {
			hideSidePane();
		}
		repaint();
		revalidate();
	}

	public FileInfo getInfoForPoint(Point point) {
		int row = folderTable.rowAtPoint(point);
		if (row == -1) {
			return null;
		}
		row = getRow(row);
		if (row == -1) {
			return null;
		}
		return folderViewModel.getItemAt(row);
	}

	private AppConfig getConfig() {
		return this.tabCallback.getSession().getApplicationContext()
				.getConfig();
	}

	public synchronized boolean isShowingHiddenFiles() {
		return showingHiddenFiles;
	}

	public synchronized void setShowingHiddenFiles(boolean showingHiddenFiles) {
		this.showingHiddenFiles = showingHiddenFiles;
	}

	private void createMoreMenu() {
		this.popup2 = new JPopupMenu();
		this.popup2.setInvoker(btnMoreMenu);
	}

	/**
	 * @return the selectionHelper
	 */
	public FolderViewSelectionHelper getSelectionHelper() {
		return selectionHelper;
	}

	/**
	 * @param selectionHelper the selectionHelper to set
	 */
	public void setSelectionHelper(FolderViewSelectionHelper selectionHelper) {
		this.selectionHelper = selectionHelper;
	}

	private void focus() {
		folderTable.requestFocusInWindow();
	}

	private void createFolderTable(
			FolderViewBaseTransferHandler transferHandler) {
		folderViewModel = new FolderViewTableModel();

		FolderViewRenderer r = new FolderViewRenderer();

		folderTable = new JTable(folderViewModel);
		folderTable.setIntercellSpacing(new Dimension(0, 0));
		folderTable.setBorder(null);
		if (!embedded) {
			folderTable.setDragEnabled(true);
		}
		folderTable.setDropMode(DropMode.USE_SELECTION);
		folderTable.setShowGrid(false);
		folderTable.setRowHeight(r.getPreferredHeight() + Utility.toPixel(5));
		folderTable.setFillsViewportHeight(true);

		if (transferHandler != null) {
			transferHandler.setWidget(this);
			folderTable.setTransferHandler(transferHandler);
		}

		folderTable.setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		label.setOpaque(true);
		folderTable.setDefaultRenderer(Object.class, r);
		folderTable.setDefaultRenderer(Long.class, r);
		folderTable.setDefaultRenderer(Date.class, r);

		sorter = new TableRowSorter<FolderViewTableModel>(folderViewModel);
		sorter.setComparator(0, new Comparator<Object>() {
			@Override
			public int compare(Object s1, Object s2) {
				FileInfo info1 = (FileInfo) s1;
				FileInfo info2 = (FileInfo) s2;
				if (info1.getType() == FileType.Directory
						|| info1.getType() == FileType.DirLink) {
					if (info2.getType() == FileType.Directory
							|| info2.getType() == FileType.DirLink) {
						return info1.getName()
								.compareToIgnoreCase(info2.getName());
					} else {
						return 1;
					}
				} else {
					if (info2.getType() == FileType.Directory
							|| info2.getType() == FileType.DirLink) {
						return -1;
					} else {
						return info1.getName()
								.compareToIgnoreCase(info2.getName());
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
				if (info1.getType() == FileType.Directory
						|| info1.getType() == FileType.DirLink) {
					if (info2.getType() == FileType.Directory
							|| info2.getType() == FileType.DirLink) {
						return info1.getLastModified()
								.compareTo(info2.getLastModified());
					} else {
						return 1;
					}
				} else {
					if (info2.getType() == FileType.Directory
							|| info2.getType() == FileType.DirLink) {
						return -1;
					} else {
						return info1.getLastModified()
								.compareTo(info2.getLastModified());
					}
				}
			}

		});

		folderTable.setRowSorter(sorter);

		ArrayList<RowSorter.SortKey> list = new ArrayList<>();
		list.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
		sorter.setSortKeys(list);

		sorter.sort();

		folderTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
		folderTable.getActionMap().put("Enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				FileInfo[] files = getSelectedFiles();
				if (files.length > 0) {
					if (files[0].getType() == FileType.Directory
							|| files[0].getType() == FileType.DirLink) {
						String str = files[0].getPath();
						render(str);
					}
				}
			}
		});

		folderTable.addKeyListener(
				new FolderViewKeyHandler(folderTable, folderViewModel));

		folderTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println("Mouse click on table");
				if (folderTable.getSelectionModel().getValueIsAdjusting()) {
					System.out.println("Value adjusting");
					selectRow(e);
					return;
				}
				if (e.getClickCount() == 2) {
					Point p = e.getPoint();
					int r = folderTable.rowAtPoint(p);
					int x = folderTable.getSelectedRow();
					if (x == -1) {
						return;
					}
					if (r == folderTable.getSelectedRow()) {
						FileInfo fileInfo = folderViewModel
								.getItemAt(getRow(r));
						if (fileInfo.getType() == FileType.Directory
								|| fileInfo.getType() == FileType.DirLink) {
							System.out.println("Current file: "
									+ FolderViewWidget.this.file);
							history.addBack(FolderViewWidget.this.file);
							render(fileInfo.getPath());
							updateNavButtons();
						}
					}
				} else if (e.isPopupTrigger()
						|| e.getButton() == MouseEvent.BUTTON3) {
					selectRow(e);
					if (embedded)
						return;
					System.out.println("called");
					menuHandler.createMenu(popup, getSelectedFiles());
					popup.pack();
					popup.show(folderTable, e.getX(), e.getY());
				}
			}
		});

		folderTable.getSelectionModel().addListSelectionListener(e -> {
			ListSelectionModel model = folderTable.getSelectionModel();
			if (e.getValueIsAdjusting() || embedded) {
				return;
			}
			if (model.isSelectionEmpty()) {
				return;
			}
		});
	}

	private void updateContentView() {
		ViewMode viewMode = toggleView.getViewMode();
		if (viewMode == ViewMode.List) {
			contentHolder.remove(scrollTable);
			contentHolder.add(scrollListView);
		} else {
			contentHolder.remove(scrollListView);
			contentHolder.add(scrollTable);
		}
	}
}

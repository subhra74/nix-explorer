package nixexplorer.widgets.logviewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;

import nixexplorer.PathUtils;
import nixexplorer.TextHolder;
import nixexplorer.app.AppContext;
import nixexplorer.app.components.FlatTabbedPane;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.FileSystemProvider;
import nixexplorer.core.ssh.FileSystemWrapper;
import nixexplorer.core.ssh.SshFileSystemWrapper;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.drawables.icons.ScaledIcon;
import nixexplorer.widgets.Widget;
import nixexplorer.widgets.component.WaitDialog;
import nixexplorer.widgets.folderview.FileSelectionDialog;
import nixexplorer.widgets.logviewer.LogMonitoringEngine.LineTextSearch;
import nixexplorer.widgets.util.Utility;

public class LogViewerWidget extends Widget implements LogNotificationListener {
	private JCheckBox chkAutoUpdate, chkLiveMode, chkMatchCase, chkWholeWord;// ,
																				// chkOnlyMatched;
	private Thread t;
	private JProgressBar prgProgress;
	private JLabel lblSearch, lblSearchStat;
	private JTextField txtSearch;
	private JButton btnSearchNext;
	private JButton btnSearch;
	private JButton btnSearchPrev;
	private JButton btnCopyText;
	private JList<String> list;
	private DefaultListModel<String> model;
	private JScrollPane jsp;
	private LogMonitoringEngine logEngine;
	private String path;
	private int LINE_PER_PAGE = 200;
	private LineTextSearch lineSearch;
	private JTextField txtPages;
	private JButton btnNext, btnPrev;
	private JLabel lblPages;
	private String pageFormat;
	private long pageNumber = -1;
	private long pageCount = 0;
	private boolean closing = false;
	private JSpinner spFont;
	private LogTableRenderer renderer;
	private FileSystemProvider fs;

	public LogViewerWidget(SessionInfo info, String[] args,
			AppSession appSession, Window window) {
		super(info, args, appSession, window);
		pageFormat = TextHolder.getString("logviewer.pageCount");
		this.fs = new SshFileSystemWrapper(info);
//		setTitle(PathUtils.getFileName(this.path) + " "
//				+ TextHolder.getString("logviewer.title"));
		if (args.length > 0) {
			System.out.println("Path log: " + args[0]);
			this.path = args[0];
		} else {
			FileSelectionDialog dlg = new FileSelectionDialog(null, fs,
					getWindow(), false);
			dlg.setLocationRelativeTo(getWindow());
			if (dlg.showDialog() == FileSelectionDialog.DialogResult.APPROVE) {
				this.path = dlg.getSelectedPath();
			} else {
				System.out.println("No file selected closing tab");
				this.viewClosing();
				throw new RuntimeException("No file selected");
			}
		}
		createUI();
		disableUI();
		if (this.path != null && this.path.length() > 0) {
			load();
		}

	}

//	@Override
//	public void run() {
//		System.out.println("Starting read log");
//		try {
//			if (wrapper == null || !wrapper.isConnected()) {
//				wrapper = new SshWrapper(info);
//				wrapper.connect();
//			}
//
//			this.sftp = wrapper.getSftpChannel();
//
//			while (chkAutoUpdate.isSelected()) {
//				loadLogBytes();
//				Thread.sleep(2 * 1000);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			try {
//				this.sftp.disconnect();
//			} catch (Exception e1) {
//
//			}
//		}
//	}

//	private void loadLogBytes() throws Exception {
//		getFileSize();
//
//		final int lastIndex = model.getRowCount() > 0 ? model.getRowCount() - 1
//				: 0;
//
//		byteCount = radFull.isSelected() ? 0 : (Integer) spinLines.getValue();
//
//		ArrayList<LogLine> lines = new ArrayList<>();
//
//		if (fileSize < 1 || lastPos == fileSize) {
//			return;
//		}
//
//		if (lastPos == 0) {
//			if (byteCount > 0) {
//				if (fileSize > byteCount) {
//					lastPos = fileSize - byteCount;
//				}
//			}
//		}
//
//		InputStream in = sftp.get(path, null, lastPos);
//		prgProgress.setIndeterminate(true);
//		BufferedReader r = new BufferedReader(new InputStreamReader(in));
//		while (true) {
//			String ln = r.readLine();
//			if (ln == null)
//				break;
//			LogLine line = new LogLine();
//			line.setLine(ln);
//			lines.add(line);
//		}
//		prgProgress.setIndeterminate(false);
//		r.close();
//		lastPos = this.fileSize;
//
//		SwingUtilities.invokeLater(() -> {
//			int selectedIndex = logTable.getSelectedRow();
//			// int lastIndex=model.getRowCount();
//			model.addLines(lines);
//			if (fromEnd) {
//				if (logTable.getRowCount() > 0) {
//					if (selectedIndex == -1 || selectedIndex == lastIndex) {
//						int index = logTable.getRowCount() - 1;
//						logTable.setRowSelectionInterval(index, index);
//						logTable.scrollRectToVisible(
//								logTable.getCellRect(index, 0, true));
//					} else {
//						logTable.setRowSelectionInterval(selectedIndex,
//								selectedIndex);
//						logTable.scrollRectToVisible(
//								logTable.getCellRect(selectedIndex, 0, true));
//					}
//				}
//			}
//
//			adjustColumnSize(lastIndex);
//			// lblSearchStat.setText(model.getSearchStat());
//		});
//	}
//
//	private void getFileSize() throws FileNotFoundException, Exception {
//		try {
//			SftpATTRS attrs = sftp.stat(path);
//			this.fileSize = attrs.getSize();
//		} catch (Exception e) {
//			e.printStackTrace();
//			if (wrapper.isConnected()) {
//				throw new FileNotFoundException(e.getMessage());
//			}
//			throw new IOException(e.getMessage());
//		}
//	}

	private void createUI() {
		setLayout(new BorderLayout());
		JPanel panHolder = new JPanel(new BorderLayout());

//		addComponentListener(new ComponentAdapter() {
//			/*
//			 * (non-Javadoc)
//			 * 
//			 * @see
//			 * java.awt.event.ComponentAdapter#componentResized(java.awt.event.
//			 * ComponentEvent)
//			 */
//			@Override
//			public void componentResized(ComponentEvent e) {
//				adjustColumnSize();
//			}
//		});

		chkLiveMode = new JCheckBox(TextHolder.getString("logviewer.liveMode"));
		chkLiveMode.addActionListener(e -> {
			if (chkLiveMode.isSelected()) {
				btnNext.setVisible(false);
				btnPrev.setVisible(false);
				txtPages.setVisible(false);
				lblPages.setVisible(false);

				lblSearch.setVisible(false);
				txtSearch.setVisible(false);
				lblSearchStat.setVisible(false);
				btnSearchNext.setVisible(false);
				btnSearchPrev.setVisible(false);
				chkMatchCase.setVisible(false);
				chkWholeWord.setVisible(false);

				pageNumber = pageCount - 1;
				logChanged();
			} else {
				btnNext.setVisible(true);
				btnPrev.setVisible(true);
				txtPages.setVisible(true);
				lblPages.setVisible(true);

				lblSearch.setVisible(true);
				txtSearch.setVisible(true);
				lblSearchStat.setVisible(true);
				btnSearchNext.setVisible(true);
				btnSearchPrev.setVisible(true);
				chkMatchCase.setVisible(true);
				chkWholeWord.setVisible(true);
			}
		});

		btnSearch = new JButton(UIManager.getIcon("AddressBar.search"));

		btnSearch.setBorderPainted(false);

		spFont = new JSpinner(new SpinnerNumberModel(
				AppContext.INSTANCE.getConfig().getEditor().getFontSize(), 1,
				100, 1));
		spFont.addChangeListener(e -> {
			System.out.println("Setting font: " + (Integer) spFont.getValue());
			Font font = this.list.getFont().deriveFont(
					(float) Utility.toPixel((Integer) spFont.getValue()));
			this.list.setFont(font);
			this.renderer.setFont(font);
			AppContext.INSTANCE.getConfig().getLogViewer()
					.setFontSize(font.getSize());
			AppContext.INSTANCE.getConfig().save();
		});
		spFont.setMaximumSize(new Dimension(
				spFont.getPreferredSize().width + Utility.toPixel(30),
				spFont.getPreferredSize().height));

		btnPrev = new JButton(UIManager.getIcon("ExpandPanel.upIcon"));
		btnNext = new JButton(UIManager.getIcon("ExpandPanel.downIcon"));
		btnCopyText = new JButton(TextHolder.getString("logviewer.copy"));

		btnPrev.setBorderPainted(false);
		btnNext.setBorderPainted(false);

		btnCopyText.addActionListener(e -> {
			String text = String.join("\n", list.getSelectedValuesList());
			Toolkit.getDefaultToolkit().getSystemClipboard()
					.setContents(new StringSelection(text), null);
		});

		btnPrev.addActionListener(e -> {
			if (pageNumber == 0) {
				return;
			}
			pageNumber--;
			loadPage();
			txtPages.setText((pageNumber + 1) + "");
		});

		btnNext.addActionListener(e -> {
			if (pageNumber == pageCount - 1) {
				return;
			}
			pageNumber++;
			loadPage();
			txtPages.setText((pageNumber + 1) + "");
		});

		txtPages = new JTextField();
		Dimension d = new Dimension(Utility.toPixel(40), Utility.toPixel(25));
		txtPages.setMaximumSize(d);
		txtPages.setPreferredSize(d);
		txtPages.setMinimumSize(d);
		txtPages.addActionListener(e -> {
			String text = txtPages.getText();
			try {
				long pg = Long.parseLong(text);
				if (pg < 1 || pg > pageCount + 1) {
					throw new Exception("Invalid page");
				}
				this.pageNumber = pg - 1;
				loadPage();
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(getWindow(),
						"Please enter a valid page number");
			}
		});
		lblPages = new JLabel(String.format(pageFormat, 0L));

		model = new DefaultListModel<>();

		list = new JList<>(model);
		list.setFixedCellHeight(Utility.toPixel(20));
		list.setFont(new Font(Font.MONOSPACED, Font.PLAIN,
				Utility.toPixel((Integer) spFont.getValue())));

		renderer = new LogTableRenderer(
				AppContext.INSTANCE.getConfig().getLogViewer());
		renderer.setFont(this.list.getFont());

//		logTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		TableRowSorter<LoggingTableModel> sorter = new TableRowSorter<LoggingTableModel>(
//				model);
////		sorter.setRowFilter(new RowFilter<LoggingTableModel, Integer>() {
////			@Override
////			public boolean include(
////					Entry<? extends LoggingTableModel, ? extends Integer> entry) {
////				Integer index = entry.getIdentifier();
////				return ((LogLine) entry.getModel().getValueAt(index, 0))
////						.getLine().contains("END");
////			}
////		});
//		logTable.setRowSorter(sorter);
		list.setCellRenderer(renderer);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
//		logTable.setDefaultRenderer(Object.class, new LogTableRenderer());
//
//		logTable.setTableHeader(null);

		jsp = new JScrollPane(list);
		jsp.setBorder(new LineBorder(UIManager.getColor("DefaultBorder.color"),
				Utility.toPixel(1)));

		JPanel panCenter = new JPanel(new BorderLayout());
		panCenter.add(jsp);
		panHolder.add(panCenter);

//		radFull = new JRadioButton(TextHolder.getString("logviewer.all"));
//		radBytes = new JRadioButton(TextHolder.getString("logviewer.partial"));
//
//		bg.add(radBytes);
//		bg.add(radFull);
//
//		radBytes.setSelected(true);

//		JLabel lblBytes = new JLabel(TextHolder.getString("logviewer.kb"));
		chkAutoUpdate = new JCheckBox(
				TextHolder.getString("logviewer.autoupdate"));
		chkAutoUpdate.setSelected(true);
		chkAutoUpdate.addActionListener(e -> {
			if (t != null) {
				t.interrupt();
			}
		});
//		spinLines = new JSpinner(
//				new SpinnerNumberModel(8192, 0, Integer.MAX_VALUE, 1));
//		spinLines.setMaximumSize(spinLines.getPreferredSize());
//		JButton btnReload = new JButton(
//				TextHolder.getString("logviewer.reload"));

		// cmbPages = new JComboBox<>();
//		cmbPages.addActionListener(e -> {
//			int pg = cmbPages.getSelectedIndex();
//			if (pg < 0) {
//				return;
//			}
//			loadPage(pg);
//		});

		Box b1 = Box.createHorizontalBox();
		b1.add(chkLiveMode);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b1.add(btnSearch);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));

		b1.add(new JLabel(TextHolder.getString("editor.fontSize")));
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b1.add(spFont);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b1.add(chkAutoUpdate);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b1.add(btnCopyText);
		b1.add(Box.createHorizontalGlue());

		b1.add(btnPrev);
		b1.add(btnNext);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b1.add(txtPages);
		b1.add(lblPages);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));

		b1.setBorder(new EmptyBorder(Utility.toPixel(0), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		// b1.add(Box.createHorizontalGlue());

//		b1.add(radFull);
//		b1.add(radBytes);
//		b1.add(spinLines);
//		b1.add(lblBytes);
//
//		b1.add(btnReload);

//		b1.add(cmbPages);

		Box b2 = Box.createHorizontalBox();

		lblSearch = new JLabel(TextHolder.getString("logviewer.search"));
		lblSearchStat = new JLabel();
		txtSearch = new JTextField(30);
		txtSearch.setMaximumSize(txtSearch.getPreferredSize());
		txtSearch.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				clearSearch();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				clearSearch();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				clearSearch();
			}
		});
		btnSearchNext = new JButton(UIManager.getIcon("ExpandPanel.downIcon"));
		btnSearchNext.setBorderPainted(false);
		btnSearchPrev = new JButton(UIManager.getIcon("ExpandPanel.upIcon"));
		btnSearchPrev.setBorderPainted(false);

		chkMatchCase = new JCheckBox(
				TextHolder.getString("logviewer.matchCase"));
		chkMatchCase.addActionListener(e -> {
			clearSearch();
		});
		chkWholeWord = new JCheckBox(
				TextHolder.getString("logviewer.wholeWord"));
		chkWholeWord.addActionListener(e -> {
			clearSearch();
		});

//		chkOnlyMatched = new JCheckBox(
//				TextHolder.getString("logviewer.onlymatched"));
//		chkOnlyMatched.addActionListener(e -> {
//			if (chkOnlyMatched.isSelected()) {
//				String searchText = txtSearch.getText();
//				if (searchText.length() > 0) {
//					if (searchPattern == null
//							|| (!searchPattern.pattern().equals(searchText))) {
//						searchPattern = Pattern.compile(searchText);
//					}
//					sorter.setRowFilter(
//							new RowFilter<LoggingTableModel, Integer>() {
//								@Override
//								public boolean include(
//										Entry<? extends LoggingTableModel, ? extends Integer> entry) {
//									Integer index = entry.getIdentifier();
//									return searchPattern
//											.matcher(((LogLine) entry.getModel()
//													.getValueAt(index, 0))
//															.getLine())
//											.find();
//								}
//							});
//
//				}
//			} else {
//				sorter.setRowFilter(null);
//			}
//		});

		prgProgress = new JProgressBar();
		prgProgress.setPreferredSize(new Dimension(
				prgProgress.getPreferredSize().width, Utility.toPixel(2)));
		prgProgress.setBackground(UIManager.getColor("Panel.background"));
		panCenter.add(prgProgress, BorderLayout.NORTH);

		JButton btnCloseSeach = new JButton(
				UIManager.getIcon("FlatTabbedPane.closeIcon"));
		btnCloseSeach.setBorderPainted(false);
		btnCloseSeach.addActionListener(e -> {
			panHolder.remove(b2);
			revalidate();
			repaint();
		});

		b2.add(lblSearch);
		b2.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b2.add(txtSearch);
		b2.add(Box.createHorizontalStrut(Utility.toPixel(5)));
//		b2.add(lblSearchStat);
		b2.add(btnSearchNext);
		b2.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b2.add(btnSearchPrev);
		b2.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b2.add(chkMatchCase);
		b2.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b2.add(chkWholeWord);
		// b2.add(chkOnlyMatched);
//		b2.add(btnClearSearch);
		b2.add(Box.createHorizontalGlue());
		b2.add(btnCloseSeach);
		b2.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		// b2.add(prgProgress);

		JTextField txtFilePath = new JTextField(30);
		txtFilePath.setFont(
				new Font(Font.DIALOG, Font.PLAIN, Utility.toPixel(14)));
		txtFilePath.setEditable(false);
		txtFilePath.setText(path);
		txtFilePath.setBorder(new EmptyBorder(Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5), Utility.toPixel(5)));
		add(txtFilePath, BorderLayout.NORTH);
		add(panHolder);

		chkAutoUpdate.addActionListener(e -> {
			if (!chkAutoUpdate.isSelected()) {
				t.interrupt();
			} else {
				load();
			}
		});

//		btnReload.addActionListener(e -> {
//			t.interrupt();
//			try {
//				sftp.disconnect();
//			} catch (Exception e2) {
//			}
//			model.clear();
//			lastPos = 0;
//			load();
//			// lblSearchStat.setText(model.getSearchStat());
//		});

		btnSearchNext.addActionListener(e -> {
			String searchText = txtSearch.getText();
			if (searchText.length() > 0) {
				try {
					searchNext();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});

		btnSearchPrev.addActionListener(e -> {
			String searchText = txtSearch.getText();
			if (searchText.length() > 0) {
				try {
					searchPrev();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});

		btnSearch.addActionListener(e -> {
			System.out.println("Logviewer search clicked");
			panHolder.add(b2, BorderLayout.SOUTH);
			this.revalidate();
			this.repaint();
		});

		panHolder.add(b1, BorderLayout.NORTH);
	}

	private void clearSearch() {
		if (lineSearch != null) {
			lineSearch.close();
		}
		lineSearch = null;
	}

	private void loadPage() {
		System.out.println("Loading page: " + this.pageNumber);
		long startLineNumber = LINE_PER_PAGE * this.pageNumber;
		System.out.println("Line number: " + startLineNumber);
		long offset = logEngine.getLineStart(startLineNumber);
		System.out.println("offset: " + offset);
		model.clear();
		int lc = 0;
		try (InputStream in = new FileInputStream(logEngine.getTempFile())) {
			in.skip(offset);
			try (BufferedReader r = new BufferedReader(
					new InputStreamReader(in, Charset.forName("utf-8")))) {
				for (int i = 0; i < LINE_PER_PAGE; i++) {
					String line = readLine(r);
					if (line == null) {
						break;
					}
					model.addElement(line);
					lc++;
				}
			}
			System.out.println("Total line in page: " + lc);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String readLine(Reader r) throws IOException {
		StringBuilder sb = new StringBuilder();
		while (true) {
			int x = r.read();
			if (x == -1) {
				if (sb.length() < 1) {
					return null;
				} else {
					return sb.toString();
				}
			}
			if (x == '\n') {
				return sb.toString();
			}
			if (x != '\r') {
				sb.append((char) x);
			}
		}
	}

	private void load() {
		try {
			logEngine = new LogMonitoringEngine(this.path, this.fs, this);
			t = new Thread(logEngine);
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void reconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

//	private void adjustColumnSize() {
//		for (int i = 0; i < logTable.getRowCount(); i++) {
//			TableCellRenderer ren = logTable.getCellRenderer(i, 0);
//			Component c = logTable.prepareRenderer(ren, i, 0);
//			columnWidth = Math.max(c.getPreferredSize().width,
//					jsp.getViewport().getWidth());
//		}
//		logTable.getColumnModel().getColumn(0).setMinWidth(columnWidth);
//	}

	private void searchNext() throws FileNotFoundException {
		if (pageNumber < 0) {
			return;
		}

		int li = list.getSelectedIndex();
		if (li < 0) {
			li = 0;
		}

		long lineNumber = pageNumber * LINE_PER_PAGE + li;

		if (lineSearch == null) {
			lineSearch = logEngine.getSearchView(txtSearch.getText(),
					chkWholeWord.isSelected(), chkMatchCase.isSelected());
		}

		disableUI();

		new Thread(() -> {
			try {
				long line = lineSearch.findNext(lineNumber);
				System.out.println("Found next on: " + line);
				if (line != -1) {
					SwingUtilities.invokeLater(() -> {
						showLine(line);
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(getWindow(), "Error");
			} finally {
				SwingUtilities.invokeLater(() -> {
					enableUI();
				});
			}
		}).start();

	}

	/**
	 * @param line
	 */
	private void showLine(long line) {
		int page = (int) (line / LINE_PER_PAGE);
		int lineInPage = (int) (line % LINE_PER_PAGE);
		System.out.println("Found in Page: " + page + " line: " + lineInPage);

		pageNumber = page;
		txtPages.setText((pageNumber + 1) + "");
		loadPage();
		list.setSelectedIndex(lineInPage);
		list.ensureIndexIsVisible(lineInPage);
//		logTable.setRowSelectionInterval(lineInPage, lineInPage);
//		logTable.scrollRectToVisible(logTable.getCellRect(lineInPage, 0, true));
	}

	private void searchPrev() throws FileNotFoundException {
		if (pageNumber < 0) {
			return;
		}

		int li = list.getSelectedIndex();
		if (li < 0) {
			li = 0;
		}

		long lineNumber = pageNumber * LINE_PER_PAGE + li;

		if (lineSearch == null) {
			lineSearch = logEngine.getSearchView(txtSearch.getText(), false,
					false);
		}

		disableUI();

		new Thread(() -> {
			try {
				long line = lineSearch.findPrev(lineNumber);
				System.out.println("Found next on: " + line);
				if (line != -1) {
					SwingUtilities.invokeLater(() -> {
						showLine(line);
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(getWindow(), "Error");
			} finally {
				SwingUtilities.invokeLater(() -> {
					enableUI();
				});
			}
		}).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#tabClosing()
	 */
	@Override
	public boolean viewClosing() {
		System.out.println("Viewclosing called");
		closing = true;
		if (logEngine != null) {
			logEngine.setStopFlag();
		}
		if (t != null) {
			t.interrupt();
		}
		System.out.println("Viewclosing called1");
		new Thread(() -> {
			System.out.println("Viewclosing called3");
			if (this.fs != null) {
				try {
					System.out.println("Closing filesystem wrapper");
					this.fs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (this.logEngine != null) {
				logEngine.disconnect();
			}
		}).start();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#tabSelected()
	 */
	@Override
	public void tabSelected() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#getIcon()
	 */
	@Override
	public Icon getIcon() {
		return UIManager.getIcon("ServerTools.logViewIcon16");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#getTitle()
	 */
	@Override
	public String getTitle() {
		return TextHolder.getString("logviewer.title");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nixexplorer.widgets.logviewer.LogNotificationListener#logChanged(long)
	 */
	@Override
	public void logChanged() {
		long lines = logEngine.getLineCount();
		System.out.println("Line count: " + lines);

		pageCount = (long) Math.ceil((double) lines / LINE_PER_PAGE);
		System.out.println("Page count: " + pageCount);

		long oldPage = pageNumber;

		SwingUtilities.invokeLater(() -> {
			lblPages.setText(String.format(pageFormat, pageCount));

			if (chkLiveMode.isSelected()) {
				pageNumber = pageCount - 1;
			}

			if (pageNumber < 0) {
				pageNumber = 0;
			}

			int lineIndexes[] = null;
			Rectangle r = null;

			if (oldPage == pageNumber) {
				lineIndexes = list.getSelectedIndices();
				r = list.getVisibleRect();
			}

			txtPages.setText((pageNumber + 1) + "");

			loadPage();

			if (oldPage == pageNumber) {
				if (lineIndexes != null && lineIndexes.length > 0) {
					list.setSelectedIndices(lineIndexes);
				}
				if (r != null) {
					list.scrollRectToVisible(r);
				}
			}

			prgProgress.setValue(0);

			if (chkLiveMode.isSelected() && model.size() > 0) {
				int index = model.size() - 1;
				list.setSelectedIndex(index);
				list.ensureIndexIsVisible(index);
			}

			enableUI();
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.widgets.logviewer.LogNotificationListener#retry()
	 */
	@Override
	public boolean retry() {
		if (closing) {
			return false;
		}
		return JOptionPane.showConfirmDialog(getWindow(),
				"Retry?") == JOptionPane.YES_OPTION;
	}

	private void disableUI() {
		txtPages.setEnabled(false);
		btnNext.setEnabled(false);
		btnPrev.setEnabled(false);
		chkAutoUpdate.setEnabled(false);
		chkLiveMode.setEnabled(false);
		btnSearchNext.setEnabled(false);
		btnSearchPrev.setEnabled(false);
		chkMatchCase.setEnabled(false);
		chkWholeWord.setEnabled(false);
		txtSearch.setEnabled(false);
	}

	private void enableUI() {
		txtPages.setEnabled(true);
		btnNext.setEnabled(true);
		btnPrev.setEnabled(true);
		chkAutoUpdate.setEnabled(true);
		chkLiveMode.setEnabled(true);
		btnSearchNext.setEnabled(true);
		btnSearchPrev.setEnabled(true);
		chkMatchCase.setEnabled(true);
		chkWholeWord.setEnabled(true);
		txtSearch.setEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nixexplorer.widgets.logviewer.LogNotificationListener#downloadProgress(
	 * int)
	 */
	@Override
	public void downloadProgress(int prg) {
		SwingUtilities.invokeLater(() -> {
			if (prg >= 0 && prg <= 100) {
				prgProgress.setValue(prg);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nixexplorer.widgets.logviewer.LogNotificationListener#setIndeterminate(
	 * boolean)
	 */
	@Override
	public void setIndeterminate(boolean a) {
		prgProgress.setIndeterminate(a);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.widgets.logviewer.LogNotificationListener#shouldUpdate()
	 */
	@Override
	public boolean shouldUpdate() {
		return chkAutoUpdate.isSelected();
	}

}

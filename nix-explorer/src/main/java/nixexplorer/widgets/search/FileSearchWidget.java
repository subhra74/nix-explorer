package nixexplorer.widgets.search;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.time.temporal.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.jcraft.jsch.ChannelExec;

import nixexplorer.PathUtils;
import nixexplorer.ShellScriptLoader;
import nixexplorer.TextHolder;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.widgets.Widget;
import nixexplorer.widgets.component.WaitDialog;
import nixexplorer.widgets.folderview.copy.CopyWidget;
import nixexplorer.widgets.util.Utility;

public class FileSearchWidget extends Widget {

	private JTextField txtName;
	private JComboBox<String> cmbSize;
	private JTextField txtSize;
	private JRadioButton radAny, radWeek, radCust;
	private JRadioButton radBoth, radFile, radFolder;
	private JSpinner spDate1, spDate2;
	private JTextField txtFolder;
	private JButton btnSearch;

	private SearchTableModel model;
	private JTable table;

	private JLabel lblStat, lblCount;
	private SshWrapper wrapper;

	private Thread t;

	private ChannelExec exec;

	private static final String lsRegex1 = "([dflo])\\|(.*)";// lsRegex1 =
																// "([^\\s]+)\\s+([^\\s]+)\\s+([^\\s]+)\\s+([^\\s]+)\\s+([\\d]+)\\s+(.{3}\\s+\\d{1,2}\\s+\\d\\d\\:\\d\\d|.{3}\\s+\\d{1,2}\\s+\\d\\d\\d\\d|\\d{4}\\-\\d{2}-\\d{2}\\s+\\d{2}\\:\\d{2})\\s+(.*)";

	private Pattern pattern;

	private SimpleDateFormat df1, df2, df3;

	private Calendar cal;

	private JRadioButton radFileName, radFileContents;

	private JCheckBox chkIncludeCompressed;

	private String searchScript;

	private JButton btnShowInBrowser, btnDelete, btnDownload;

	private WaitDialog deleteWaitDialog;

	private AtomicBoolean stopFlag = new AtomicBoolean(false);

	public FileSearchWidget(SessionInfo info, String[] args,
			AppSession appSession, Window window) {
		super(info, args, appSession, window);
		// setTitle("filesearch.title");
		Dimension pref = null;

		chkIncludeCompressed = new JCheckBox(
				TextHolder.getString("filesearch.compress"));
		chkIncludeCompressed.setAlignmentX(LEFT_ALIGNMENT);
		radFileName = new JRadioButton(TextHolder.getString("filesearch.name"));
		radFileName.setAlignmentX(LEFT_ALIGNMENT);
		radFileContents = new JRadioButton(
				TextHolder.getString("filesearch.content"));
		radFileContents.setAlignmentX(LEFT_ALIGNMENT);

		ButtonGroup bg = new ButtonGroup();
		bg.add(radFileName);
		bg.add(radFileContents);

		radFileName.setSelected(true);

		setBackground(UIManager.getColor("DefaultBorder.color"));

		setLayout(new BorderLayout(Utility.toPixel(1), Utility.toPixel(1)));
		Box b1 = Box.createVerticalBox();
		b1.setOpaque(true);
		b1.setBackground(UIManager.getColor("Panel.background"));

		b1.setBorder(new EmptyBorder(Utility.toPixel(10), Utility.toPixel(10),
				Utility.toPixel(10), Utility.toPixel(10)));

		JLabel lblName = new JLabel(
				TextHolder.getString("filesearch.searchfor"));
		lblName.setAlignmentX(LEFT_ALIGNMENT);
		txtName = new JTextField(20);
		pref = txtName.getPreferredSize();
		txtName.setMaximumSize(pref);
		txtName.setAlignmentX(LEFT_ALIGNMENT);

		JLabel lblFolder = new JLabel(
				TextHolder.getString("filesearch.folder"));
		lblFolder.setAlignmentX(LEFT_ALIGNMENT);
		txtFolder = new JTextField(20);
		txtFolder.setPreferredSize(pref);
		txtFolder.setMaximumSize(pref);
		txtFolder.setAlignmentX(LEFT_ALIGNMENT);
		if (args == null || args.length < 1) {
			txtFolder.setText("$HOME");
		} else {
			txtFolder.setText(args[0]);
		}

		JLabel lblSize = new JLabel(TextHolder.getString("filesearch.size"));
		lblSize.setAlignmentX(LEFT_ALIGNMENT);
		cmbSize = new JComboBox<>(
				new String[] { TextHolder.getString("filesearch.eq"),
						TextHolder.getString("filesearch.lt"),
						TextHolder.getString("filesearch.gt") });
		cmbSize.setMaximumSize(pref);
		cmbSize.setAlignmentX(LEFT_ALIGNMENT);

		txtSize = new JTextField(10);
		txtSize.setPreferredSize(pref);
		txtSize.setAlignmentX(LEFT_ALIGNMENT);
		txtSize.setMaximumSize(pref);

		JLabel lblMtime = new JLabel(TextHolder.getString("filesearch.mtime"));
		lblMtime.setAlignmentX(LEFT_ALIGNMENT);

		ButtonGroup btnGroup1 = new ButtonGroup();
		radAny = new JRadioButton(TextHolder.getString("filesearch.mtime1"));
		radAny.setAlignmentX(LEFT_ALIGNMENT);
		radWeek = new JRadioButton(TextHolder.getString("filesearch.mtime3"));
		radWeek.setAlignmentX(LEFT_ALIGNMENT);
		radCust = new JRadioButton(TextHolder.getString("filesearch.mtime4"));
		radCust.setAlignmentX(LEFT_ALIGNMENT);

		btnGroup1.add(radAny);
		btnGroup1.add(radWeek);
		btnGroup1.add(radCust);

		ActionListener radSelected = new ActionListener() {

			private void disableSpinners() {
				spDate1.setEnabled(false);
				spDate2.setEnabled(false);
			}

			private void enableSpinners() {
				spDate1.setEnabled(true);
				spDate2.setEnabled(true);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == radAny) {
					disableSpinners();
				} else if (e.getSource() == radWeek) {
					disableSpinners();
				} else {
					enableSpinners();
				}
			}
		};

		radAny.addActionListener(radSelected);
		radWeek.addActionListener(radSelected);
		radCust.addActionListener(radSelected);

		radAny.setSelected(true);

		JLabel lblFrom = new JLabel(TextHolder.getString("filesearch.from"));
		lblFrom.setAlignmentX(LEFT_ALIGNMENT);
		JLabel lblTo = new JLabel(TextHolder.getString("filesearch.to"));
		lblTo.setAlignmentX(LEFT_ALIGNMENT);

		SpinnerDateModel sm1 = new SpinnerDateModel();
		sm1.setEnd(new Date());
		spDate1 = new JSpinner(sm1);
		spDate1.setPreferredSize(pref);
		spDate1.setMaximumSize(pref);
		spDate1.setAlignmentX(LEFT_ALIGNMENT);
		spDate1.setEditor(new JSpinner.DateEditor(spDate1, "dd/MM/yyyy"));

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		spDate1.setValue(cal.getTime());
		spDate1.setEnabled(false);

		SpinnerDateModel sm2 = new SpinnerDateModel();
		sm2.setEnd(new Date());
		spDate2 = new JSpinner(sm2);
		spDate2.setMaximumSize(pref);
		spDate2.setPreferredSize(pref);
		spDate2.setAlignmentX(LEFT_ALIGNMENT);
		spDate2.setEditor(new JSpinner.DateEditor(spDate2, "dd/MM/yyyy"));
		spDate2.setEnabled(false);

		JLabel lblLookfor = new JLabel(
				TextHolder.getString("filesearch.lookfor"));
		lblLookfor.setAlignmentX(LEFT_ALIGNMENT);

		ButtonGroup btnGroup2 = new ButtonGroup();
		radBoth = new JRadioButton(TextHolder.getString("filesearch.both"));
		radBoth.setAlignmentX(LEFT_ALIGNMENT);
		radFile = new JRadioButton(TextHolder.getString("filesearch.fileonly"));
		radFile.setAlignmentX(LEFT_ALIGNMENT);
		radFolder = new JRadioButton(
				TextHolder.getString("filesearch.folderonly"));
		radFolder.setAlignmentX(LEFT_ALIGNMENT);

		btnGroup2.add(radBoth);
		btnGroup2.add(radFile);
		btnGroup2.add(radFolder);

		radBoth.setSelected(true);

		btnSearch = new JButton(TextHolder.getString("filesearch.find"));
		btnSearch.setAlignmentX(LEFT_ALIGNMENT);
		// btnSearch.setPreferredSize(pref);

		btnSearch.addActionListener(e -> {
			find();
		});

//		model = new DefaultTableModel(new String[0][],
//				new String[] { TextHolder.getString("filesearch.name"), TextHolder.getString("filesearch.type"),
//						TextHolder.getString("filesearch.size"), TextHolder.getString("filesearch.modified"),
//						TextHolder.getString("filesearch.permission"), TextHolder.getString("filesearch.links"),
//						TextHolder.getString("filesearch.user"), TextHolder.getString("filesearch.group"),
//						TextHolder.getString("filesearch.filepath") });

		model = new SearchTableModel();

		// PathUtils.getFileName(text), size, modified, perm, link, user, group,
		// file

		table = new JTable(model);
		table.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) {
				return;
			}

			if (table.getSelectedRowCount() > 0) {
				enableButtons();
			} else {
				disableButtons();
			}
		});

		table.setIntercellSpacing(new Dimension(0, 0));
		table.setRowHeight(Utility.toPixel(24));
		table.setShowGrid(false);
		resizeColumnWidth(table);
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);
		SearchTableRenderer r = new SearchTableRenderer(model);
		table.setDefaultRenderer(String.class, r);
		table.setDefaultRenderer(Date.class, r);
		table.setDefaultRenderer(Integer.class, r);
		table.setDefaultRenderer(Long.class, r);

		JScrollPane jsp = new JScrollPane(table);

		lblStat = new JLabel(TextHolder.getString("filesearch.idle"));
		lblCount = new JLabel(TextHolder.getString(""));
		lblCount.setHorizontalAlignment(JLabel.RIGHT);

		b1.add(Box.createVerticalStrut(Utility.toPixel(10)));

		b1.add(lblName);
		b1.add(Box.createVerticalStrut(Utility.toPixel(3)));
		b1.add(txtName);

		b1.add(Box.createVerticalStrut(Utility.toPixel(10)));

		b1.add(radFileName);
		b1.add(Box.createVerticalStrut(Utility.toPixel(3)));
		b1.add(radFileContents);
		b1.add(Box.createVerticalStrut(Utility.toPixel(3)));
		b1.add(chkIncludeCompressed);

		b1.add(Box.createVerticalStrut(Utility.toPixel(10)));

		b1.add(lblFolder);
		b1.add(Box.createVerticalStrut(Utility.toPixel(3)));
		b1.add(txtFolder);

		b1.add(Box.createVerticalStrut(Utility.toPixel(10)));

		b1.add(lblSize);
		b1.add(Box.createVerticalStrut(Utility.toPixel(3)));
		b1.add(cmbSize);
		b1.add(Box.createVerticalStrut(Utility.toPixel(3)));
		b1.add(txtSize);

		b1.add(Box.createVerticalStrut(Utility.toPixel(10)));

		// b1.add(b2);
		b1.add(lblMtime);
		b1.add(Box.createVerticalStrut(Utility.toPixel(3)));
		b1.add(radAny);
		b1.add(Box.createVerticalStrut(Utility.toPixel(3)));
		b1.add(radWeek);
		b1.add(Box.createVerticalStrut(Utility.toPixel(3)));
		b1.add(radCust);

		b1.add(Box.createVerticalStrut(Utility.toPixel(10)));

		b1.add(lblFrom);
		b1.add(Box.createVerticalStrut(Utility.toPixel(3)));
		b1.add(spDate1);
		b1.add(Box.createVerticalStrut(Utility.toPixel(3)));
		b1.add(lblTo);
		b1.add(Box.createVerticalStrut(Utility.toPixel(3)));
		b1.add(spDate2);

		b1.add(Box.createVerticalStrut(Utility.toPixel(10)));

		b1.add(lblLookfor);
		b1.add(Box.createVerticalStrut(Utility.toPixel(3)));
		b1.add(radBoth);
		b1.add(Box.createVerticalStrut(Utility.toPixel(3)));
		b1.add(radFile);
		b1.add(Box.createVerticalStrut(Utility.toPixel(3)));
		b1.add(radFolder);

		b1.add(Box.createVerticalStrut(Utility.toPixel(10)));

		b1.add(btnSearch);

		Box statBox = Box.createHorizontalBox();
		statBox.setOpaque(true);
		statBox.add(lblStat);
		statBox.add(Box.createHorizontalGlue());
		statBox.add(lblCount);
		statBox.setBorder(new EmptyBorder(Utility.toPixel(5),
				Utility.toPixel(10), Utility.toPixel(5), Utility.toPixel(10)));
		statBox.setBackground(UIManager.getColor("Panel.background"));

		btnShowInBrowser = new JButton(
				TextHolder.getString("filesearch.showInBrowser"));
		btnDelete = new JButton(TextHolder.getString("filesearch.delete"));
		btnDownload = new JButton(TextHolder.getString("filesearch.download"));

		disableButtons();

		btnShowInBrowser.addActionListener(e -> {
			int index = table.getSelectedRow();
			if (index != -1) {
				SearchResult res = model.getItemAt(index);
				String path = res.getPath();
				path = PathUtils.getParent(path);
				if (path.length() > 0) {
					appSession.createWidget(
							"nixexplorer.widgets.folderview.remote.RemoteFolderViewWidget",
							new String[] { path });
				}
			}
		});

		btnDelete.addActionListener(e -> {
			if (table.getSelectedRowCount() > 0) {
				if (t != null && t.isAlive()) {
					t.interrupt();
				}
				t = new Thread(() -> {
					deleteItems();
				});
				t.start();
				if (t.isAlive()) {
					deleteWaitDialog.setVisible(true);
				}
			}
		});

		btnDownload.addActionListener(e -> {
			int c = table.getSelectedRowCount();
			if (c < 1) {
				return;
			}
			JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (jfc.showSaveDialog(
					getWindow()) == JFileChooser.APPROVE_OPTION) {
				String downloadFolder = jfc.getSelectedFile().getAbsolutePath();
				downloadItems(downloadFolder);
			}
		});

		Box bActions = Box.createHorizontalBox();
		bActions.setOpaque(true);
		bActions.setBackground(UIManager.getColor("Panel.background"));
		bActions.setBorder(new EmptyBorder(Utility.toPixel(5),
				Utility.toPixel(10), Utility.toPixel(5), Utility.toPixel(10)));
		bActions.add(Box.createHorizontalGlue());
		bActions.add(btnShowInBrowser);
		bActions.add(Box.createHorizontalStrut(Utility.toPixel(10)));
		bActions.add(btnDelete);
		bActions.add(Box.createHorizontalStrut(Utility.toPixel(10)));
		bActions.add(btnDownload);

		JScrollPane jspB1 = new JScrollPane(b1,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		add(jspB1, BorderLayout.WEST);

		JPanel p = new JPanel(
				new BorderLayout(Utility.toPixel(1), Utility.toPixel(1)));
		p.setBackground(UIManager.getColor("DefaultBorder.color"));

		p.add(jsp, BorderLayout.CENTER);
		p.add(bActions, BorderLayout.SOUTH);

		add(statBox, BorderLayout.SOUTH);

		add(p, BorderLayout.CENTER);

		deleteWaitDialog = new WaitDialog(getWindow(), e -> {
			new Thread(() -> {
				try {
					exec.disconnect();
				} catch (Exception e1) {
				}
			}).start();
		});

	}

	private void disableButtons() {
		btnShowInBrowser.setEnabled(false);
		btnDelete.setEnabled(false);
		btnDownload.setEnabled(false);
	}

	private void enableButtons() {
		btnShowInBrowser.setEnabled(true);
		btnDelete.setEnabled(true);
		btnDownload.setEnabled(true);
	}

	private void find() {
		if (t != null && t.isAlive()) {
			t.interrupt();
		}
		t = new Thread(() -> {
			findAsync();
		});
		t.start();
	}

	private void findAsync() {

		if (exec != null && exec.isConnected()) {
			exec.disconnect();
		}

		SwingUtilities.invokeLater(() -> {
			model.clear();
			lblStat.setText(TextHolder.getString("filesearch.searching"));
			lblCount.setText(String.format(
					TextHolder.getString("filesearch.searchItemCount"),
					model.getRowCount()));
			disableButtons();
		});

		System.out.println("Starting search.. ");
		try {
			if (wrapper == null || !wrapper.isConnected()) {
				wrapper = connect();
			}

			if (searchScript == null) {
				searchScript = ShellScriptLoader.loadShellScript("search.sh",
						"common");
			}

			StringBuilder criteriaBuffer = new StringBuilder();

			String folder = txtFolder.getText();
//			if (folder.contains(" ")) {
//				folder = "\"" + folder + "\"";
//			}

			criteriaBuffer.append(" ");

			if (txtSize.getText().length() > 0) {
				criteriaBuffer.append("-size");
				switch (cmbSize.getSelectedIndex()) {
				case 1:
					criteriaBuffer.append(" -");
					break;
				case 2:
					criteriaBuffer.append(" +");
					break;
				default:
					criteriaBuffer.append(" ");
				}
				criteriaBuffer.append(txtSize.getText() + "c");
				criteriaBuffer.append(" ");
			}

			if (radFile.isSelected() || radFileContents.isSelected()) {
				criteriaBuffer.append(" -type f");
			} else if (radFolder.isSelected()) {
				criteriaBuffer.append(" -type d");
			}

			if (radWeek.isSelected()) {
				criteriaBuffer.append(" -mtime -7");
			} else if (radCust.isSelected()) {
				Date d1 = (Date) spDate1.getValue();
				Date d2 = (Date) spDate2.getValue();

				LocalDate now = LocalDate.now();
				LocalDate date1 = d1.toInstant().atZone(ZoneId.systemDefault())
						.toLocalDate();
				LocalDate date2 = d2.toInstant().atZone(ZoneId.systemDefault())
						.toLocalDate();

				long days1 = ChronoUnit.DAYS.between(date1, now);
				long days2 = ChronoUnit.DAYS.between(date2, now);

				criteriaBuffer
						.append(" -mtime +" + days2 + " -a -mtime -" + days1);
			}

			StringBuilder scriptBuffer = new StringBuilder();

			if (txtName.getText().length() > 0 && radFileName.isSelected()) {
				scriptBuffer
						.append("export NAME='" + txtName.getText() + "'\n");
			}

			scriptBuffer.append("export LOCATION=\"" + folder + "\"\n");
			scriptBuffer.append("export CRITERIA='" + criteriaBuffer + "'\n");
			if (radFileContents.isSelected()) {
				scriptBuffer.append("export CONTENT=1\n");
				scriptBuffer
						.append("export PATTERN='" + txtName.getText() + "'\n");
				if (chkIncludeCompressed.isSelected()) {
					scriptBuffer.append("export UNCOMPRESS=1\n");
				}
			}

			scriptBuffer.append(searchScript);

			String findCmd = scriptBuffer.toString();
			System.out.println(findCmd);

			exec = wrapper.getExecChannel();
			InputStream in = exec.getInputStream();
			exec.setCommand(findCmd);
			exec.connect();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				// System.err.println(line);
				if (line.length() > 0) {
					SearchResult res = parseOutput(line);
					if (res != null) {
						SwingUtilities.invokeLater(() -> {
							model.add(res);
							lblCount.setText(String.format(
									TextHolder.getString(
											"filesearch.searchItemCount"),
									model.getRowCount()));
						});
					}
				}
			}

			reader.close();
			exec.disconnect();
			int ret = exec.getExitStatus();
			System.err.println("Exit code: " + ret);

			// wrapper.disconnect();
			lblStat.setText(TextHolder.getString("filesearch.idle"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(() -> {
			lblStat.setText(TextHolder.getString("filesearch.idle"));
			lblCount.setText(String.format(
					TextHolder.getString("filesearch.searchItemCount"),
					model.getRowCount()));
		});

	}

	@Override
	public void reconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	private SearchResult parseOutput(String text) {
		if (this.pattern == null) {
			this.pattern = Pattern.compile(lsRegex1);
		}

		Matcher matcher = this.pattern.matcher(text);
		if (matcher.matches()) {
			String type = matcher.group(1);
			String path = matcher.group(2);

			String fileType = "Other";

			switch (type) {
			case "d":
				fileType = "Folder";
				break;
			case "l":
				fileType = "Link";
				break;
			case "f":
				fileType = "File";
				break;
			}

			return new SearchResult(PathUtils.getFileName(path), path,
					fileType);

		}

		return null;
	}

//	private Date parseDate(String text) {
//		text = text.replaceAll("\\s+", " ");
//		if (df1 == null) {
//			df1 = new SimpleDateFormat("MMM d HH:mm", Locale.ENGLISH);
//		}
//
//		try {
//			Date dt = df1.parse(text);
//
//			Calendar parsedCal = Calendar.getInstance();
//			int CAL_YEAR = Calendar.YEAR;
//
//			if (cal == null) {
//				cal = Calendar.getInstance();
//			}
//			parsedCal.setTime(dt);
//			parsedCal.set(CAL_YEAR, cal.get(CAL_YEAR));
//
//			return parsedCal.getTime();
//
//		} catch (Exception e) {
//			// e.printStackTrace();
//			if (df2 == null) {
//				df2 = new SimpleDateFormat("MMM d yyyy", Locale.ENGLISH);
//			}
//
//			try {
//				return df2.parse(text);
//			} catch (Exception e2) {
//				// e2.printStackTrace();
//				if (df3 == null) {
//					df3 = new SimpleDateFormat("YYYY-MM-dd HH:mm",
//							Locale.ENGLISH);
//				}
//
//				try {
//					return df3.parse(text);
//				} catch (Exception e3) {
//					// e3.printStackTrace();
//				}
//			}
//		}
//		System.err.println(text);
//		return new Date();
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#tabClosing()
	 */
	@Override
	public boolean viewClosing() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#tabClosed()
	 */
	@Override
	public void viewClosed() {
		stopFlag.set(true);
		if (wrapper != null && wrapper.isConnected()) {
			new Thread(() -> {
				try {
					System.out.println("Closing connections...");
					wrapper.disconnect();
				} catch (Exception e) {
				}
			}).start();
		}
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
		return UIManager.getIcon("ServerTools.findFilesIcon16");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return TextHolder.getString("filesearch.title");
	}

	public void resizeColumnWidth(JTable table) {
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		final TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			TableColumn col = columnModel.getColumn(column);
			if (column == 0) {
				col.setPreferredWidth(Utility.toPixel(300));
			} else if (column == 1) {
				col.setPreferredWidth(Utility.toPixel(100));
			} else {
				col.setPreferredWidth(Utility.toPixel(400));
			}
		}
	}

	private void downloadItems(String path) {
		try {

			List<String> args = new ArrayList<>();
			args.add("d");
			args.add(path);

			List<String> files = new ArrayList<>();
			List<String> folders = new ArrayList<>();
			for (int r : table.getSelectedRows()) {
				SearchResult sr = model.getItemAt(r);
				if (sr.getType().equals("Folder")) {
					folders.add(sr.getPath());
				} else {
					files.add(sr.getPath());
				}
			}

			args.add(files.size() + "");
			args.add(folders.size() + "");
			args.addAll(files);
			args.addAll(folders);

			String[] arr = new String[args.size()];
			arr = args.toArray(arr);

			System.out.println("Local drop args: " + args);
			try {
				appSession.createWidget(CopyWidget.class.getName(), arr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteItems() {
		try {
			if (table.getSelectedRowCount() < 1) {
				return;
			}
			if (exec != null && exec.isConnected()) {
				exec.disconnect();
			}

			if (wrapper == null || !wrapper.isConnected()) {
				wrapper = connect();
			}

			StringBuilder sb = new StringBuilder();
			for (int i : table.getSelectedRows()) {
				sb.append("rm -rf \"" + model.getItemAt(i).getPath() + "\"\n");
			}
			System.out.println("del: " + sb);
			exec = wrapper.getExecChannel();
			exec.setCommand(sb.toString());
			exec.connect();
			while (exec.getExitStatus() < 0 || exec.isConnected()) {
				System.out.println("sleeping...");
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
			System.out.println("Command executed");
			exec.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SwingUtilities.invokeLater(() -> {
				deleteWaitDialog.setVisible(false);
			});
		}
	}

}

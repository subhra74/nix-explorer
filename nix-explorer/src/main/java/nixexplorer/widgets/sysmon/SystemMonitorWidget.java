package nixexplorer.widgets.sysmon;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import com.jcraft.jsch.ChannelExec;

import nixexplorer.ShellScriptLoader;
import nixexplorer.TextHolder;
import nixexplorer.app.AppContext;
import nixexplorer.app.components.CredentialsDialog;
import nixexplorer.app.components.CustomTabbedPane;
import nixexplorer.app.components.FlatTabbedPane;
import nixexplorer.app.components.CredentialsDialog.Credentials;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionManagerPanel;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshFileSystemProvider;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.widgets.Widget;
import nixexplorer.widgets.util.Utility;

public class SystemMonitorWidget extends Widget implements Runnable {
	private static final long serialVersionUID = 1L;
	private SshWrapper wrapper;
	// private Map<String, String> environment = new HashMap<String, String>();
	private int sleepInterval = 2;
	private String statScript, sockScript, procScript, diskScript;
	private String os = null;
	private Thread t;
	private CustomTabbedPane tabs;
	private JTable processTable;
	private ProcessTableModel processTableModel;
	// private JTable socketTable;
	// private GenericTableModel socketTableModel;
	private JTable diskTable;
	private GenericTableModel diskTableModel;
	private SystemLoadPanel loadPanel;
	private String unameCmd = "uname";
	private String[] knownOS = new String[] { "Linux", "HP-UX", "OpenBSD",
			"FreeBSD" };
	private List<String> listLines = new ArrayList<>(50);
	private JTextArea socketStatus;
	private JTextField txtSockSearch;
	private JButton filterBtn;
	private JButton clearBtn;
	private String searchText;
	private JTextField txtProcessSearch;
	private String processFilter;
	private List<String> procList = new ArrayList<>();
	private String commandToExecute;
	private JPopupMenu popPrio, popSig;
	private AtomicBoolean stopFlag = new AtomicBoolean(false);
	private String diskText, sysInfoText;
	private List<String> sockText = new ArrayList<>();
	private JTextArea txtDiskStat;
	private JTextArea txtSysInfo;
	private String sysInfoScript;
	private boolean readSysinfo = true, readNetworkStatus = true,
			readDiskStatus = true;
	private JButton btnSockRefresh, btnDiskRefresh, btnInfoRefresh;
	private Map<String, String> statMap;
	private Map<String, String> psMap;
	private boolean tableResized = false;
	private JSpinner spInterval;

	public SystemMonitorWidget(SessionInfo info, String[] args,
			AppSession appSession, Window window) {
		super(info, args, appSession, window);

		statMap = new HashMap<>();
		psMap = new HashMap<>();

		btnDiskRefresh = new JButton(TextHolder.getString("sysmon.refresh"));
		btnInfoRefresh = new JButton(TextHolder.getString("sysmon.refresh"));
		btnSockRefresh = new JButton(TextHolder.getString("sysmon.refresh"));

		btnDiskRefresh.addActionListener(e -> {
			readDiskStatus = true;
			t.interrupt();
		});

		btnInfoRefresh.addActionListener(e -> {
			readSysinfo = true;
			t.interrupt();
		});

		btnSockRefresh.addActionListener(e -> {
			readNetworkStatus = true;
			t.interrupt();
		});

//		setTitle((info == null ? "" : info.getName() + " - ")
//				+ TextHolder.getString("sysmon.title"));
		this.setLayout(new BorderLayout());

		processTableModel = new ProcessTableModel();

//		TableRowSorter<GenericTableModel> sorter = new TableRowSorter<GenericTableModel>(
//				processTableModel);
//		sorter.

		processTable = new JTable(processTableModel);
		processTable.setIntercellSpacing(new Dimension(0, 0));
		processTable.setShowGrid(false);

		TableRowSorter<ProcessTableModel> sorter = new TableRowSorter<ProcessTableModel>(
				processTableModel);
		processTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		processTable.setFillsViewportHeight(true);

		// processTable.setAutoCreateRowSorter(true);
		processTable.setRowSorter(sorter);
		processTableModel.setTable(processTable);

		spInterval = new JSpinner(new SpinnerNumberModel(
				AppContext.INSTANCE.getConfig().getMonitor().getInterval(), 1,
				100, 1));
		spInterval.addChangeListener(e -> {
			int interval = (Integer) spInterval.getValue();
			System.out.println("New interval: " + interval);
			this.sleepInterval = interval;
			this.t.interrupt();

			AppContext.INSTANCE.getConfig().getMonitor().setInterval(interval);
			AppContext.INSTANCE.getConfig().save();
		});

		JLabel lblInterval = new JLabel(
				TextHolder.getString("sysmon.pollInterval"));

		JLabel lbl1 = new JLabel(TextHolder.getString("sysmon.processFilter"));
		txtProcessSearch = new JTextField(30);
		JButton btnFilterProc = new JButton(
				TextHolder.getString("sysmon.processFilterApply"));

		btnFilterProc.addActionListener(e -> {
			processFilter = txtProcessSearch.getText();
			System.out.println("Applying filter: " + processFilter);
			sorter.setRowFilter(new RowFilter<ProcessTableModel, Integer>() {

				@Override
				public boolean include(
						Entry<? extends ProcessTableModel, ? extends Integer> entry) {
					if (processFilter == null || processFilter.length() < 1) {
						return true;
					}
					Integer index = entry.getIdentifier();
					int c = entry.getModel().getColumnCount();
					for (int i = 0; i < c; i++) {
						if ((entry.getModel().getValueAt(index, i)).toString()
								.toLowerCase(Locale.ENGLISH)
								.contains(processFilter
										.toLowerCase(Locale.ENGLISH))) {
							return true;
						}
					}
					return false;
				}

			});
			processTableModel.updateData(procList);
		});
		JButton btnFilterClear = new JButton(
				TextHolder.getString("sysmon.processFilterClear"));
		btnFilterClear.addActionListener(e -> {
			processFilter = null;
			System.out.println("Applying filter: " + processFilter);
			sorter.setRowFilter(null);
			processTableModel.updateData(procList);
		});

		JButton btnKill = new JButton(TextHolder.getString("sysmon.killText"));
		btnKill.addActionListener(e -> {
			killProcess();
		});
		JButton btnNice = new JButton(TextHolder.getString("sysmon.niceText"));
		btnNice.addActionListener(e -> {
			int h = popPrio.getPreferredSize().height;
			popPrio.setInvoker(btnNice);
			popPrio.show(btnNice, 0, btnNice.getY() - h);
		});
		JButton btnSig = new JButton(TextHolder.getString("sysmon.sigText"));
		btnSig.addActionListener(e -> {
			int h = popSig.getPreferredSize().height;
			popSig.setInvoker(btnSig);
			popSig.show(btnSig, 0, btnSig.getY() - h);
		});

		JCheckBox chkAllProcs = new JCheckBox(
				TextHolder.getString("sysmon.showAll"));
		chkAllProcs.addActionListener(e -> {
			if (chkAllProcs.isSelected()) {
				psMap.put("show_all", "\"true\"");
			} else {
				psMap.remove("show_all");
			}
			t.interrupt();
		});

		Box bottomBox = Box.createHorizontalBox();
//		bottomBox.setBorder(new EmptyBorder(Utility.toPixel(5),
//				Utility.toPixel(5), Utility.toPixel(5), Utility.toPixel(5)));
//		bottomBox.add(
//				Box.createRigidArea(new Dimension(Utility.toPixel(10), 0)));
		bottomBox.add(chkAllProcs);
		bottomBox.add(Box.createHorizontalGlue());
		bottomBox.add(btnKill);
		bottomBox.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		bottomBox.add(btnNice);
		bottomBox.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		bottomBox.add(btnSig);

//		socketTableModel = new GenericTableModel();
//		socketTable = new JTable(socketTableModel);
//		socketTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		socketTable.setFillsViewportHeight(true);

		diskTableModel = new GenericTableModel();
		diskTable = new JTable(diskTableModel);
		diskTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		diskTable.setFillsViewportHeight(true);

		psMap.put("ps_options",
				"\"" + processTableModel.getCommandString() + "\"");

		tabs = new CustomTabbedPane();

		Box btop1 = Box.createHorizontalBox();
		btop1.add(lbl1);
		btop1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		btop1.add(txtProcessSearch);
		btop1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		btop1.add(btnFilterProc);
		btop1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		btop1.add(btnFilterClear);
		btop1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		btop1.add(lblInterval);
		btop1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		btop1.add(spInterval);

		JScrollPane jsp1 = new JScrollPane(processTable);
		jsp1.getViewport().setBackground(processTable.getBackground());

		JPanel panel1 = new JPanel(
				new BorderLayout(Utility.toPixel(5), Utility.toPixel(5)));
		panel1.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		panel1.add(btop1, BorderLayout.NORTH);
		panel1.add(jsp1);
		panel1.add(bottomBox, BorderLayout.SOUTH);
		panel1.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));

		loadPanel = new SystemLoadPanel();
		loadPanel.setBorder(new EmptyBorder(Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5), Utility.toPixel(5)));

		JPanel socketPanel = new JPanel(new BorderLayout());
		socketStatus = new JTextArea();
		socketStatus.setEditable(false);
		socketStatus.setFont(
				new Font(Font.MONOSPACED, Font.BOLD, Utility.toPixel(14)));

		JScrollPane jsp2 = new JScrollPane(socketStatus);
		// jsp2.setBorder(null);
		socketPanel.add(jsp2);

		JLabel lblSockLabel = new JLabel(
				TextHolder.getString("sysmon.searchTxt"));
		txtSockSearch = new JTextField(30);
		filterBtn = new JButton(TextHolder.getString("sysmon.filterTxt"));
		filterBtn.addActionListener(e -> {
			searchText = txtSockSearch.getText();
			setSocketText();
		});
		clearBtn = new JButton(TextHolder.getString("sysmon.clear"));
		clearBtn.addActionListener(e -> {
			searchText = null;
			txtSockSearch.setText("");
			setSocketText();
		});

		Box bx1 = Box.createHorizontalBox();
		bx1.add(lblSockLabel);
		bx1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		bx1.add(txtSockSearch);
		bx1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		bx1.add(filterBtn);
		bx1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		bx1.add(clearBtn);
		bx1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		bx1.add(btnSockRefresh);

		socketPanel.add(bx1, BorderLayout.SOUTH);

		txtDiskStat = new JTextArea();
		txtDiskStat.setFont(
				new Font(Font.MONOSPACED, Font.BOLD, Utility.toPixel(14)));
		txtDiskStat.setEditable(false);

		JScrollPane jsp3 = new JScrollPane(txtDiskStat);
		jsp3.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		JPanel panDiskInfo = new JPanel(new BorderLayout());
		panDiskInfo.add(jsp3);
		panDiskInfo.add(createRefreshPanel(btnDiskRefresh), BorderLayout.SOUTH);

		txtSysInfo = new JTextArea();
		txtSysInfo.setFont(
				new Font(Font.MONOSPACED, Font.BOLD, Utility.toPixel(14)));
		txtSysInfo.setEditable(false);
		JScrollPane jsp4 = new JScrollPane(txtSysInfo);
		jsp4.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		JPanel panSysInfo = new JPanel(new BorderLayout());
		panSysInfo.add(jsp4);
		panSysInfo.add(createRefreshPanel(btnInfoRefresh), BorderLayout.SOUTH);

		tabs.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));

		this.add(tabs);

		createNiceMenu();
		createSigMenu();

		tabs.addCustomTab(TextHolder.getString("sysmon.socketTitle"),
				socketPanel);
		tabs.addCustomTab(TextHolder.getString("sysmon.diskTitle"),
				panDiskInfo);
		tabs.addCustomTab(TextHolder.getString("sysmon.loadTitle"), loadPanel);
		tabs.addCustomTab(TextHolder.getString("sysmon.processTitle"), panel1);
		tabs.addCustomTab(TextHolder.getString("sysmon.sysinfo"), panSysInfo);

//		final TableColumnModel columnModel = processTable.getColumnModel();
//		for (int column = 0; column < processTable.getColumnCount(); column++) {
//			TableColumn col = columnModel.getColumn(column);
//			if (column == 0) {
//				col.setPreferredWidth(Utility.toPixel(300));
//			}
//		}

		init();
	}

	private Box createRefreshPanel(JButton btn) {
		Box b1 = Box.createHorizontalBox();
		b1.add(Box.createHorizontalGlue());
		b1.add(btn);
		return b1;
	}

	private void init() {
		t = new Thread(this);
		t.start();
	}

	private void retrieveStats() throws Exception {
		listLines.clear();
		if (wrapper == null || !wrapper.isConnected()) {
			wrapper = connect();
		}

		if (os == null) {
			List<String> list = executeCommand(unameCmd, false);
			String os = null;
			if (list.size() < 1) {
				System.out.println("Unable to detect OS");
				return;
			}
			os = list.get(0);
			statScript = ShellScriptLoader.loadShellScript("sysmon.sh",
					os.toLowerCase());
			sockScript = ShellScriptLoader.loadShellScript("sockstat.sh",
					os.toLowerCase());
			procScript = ShellScriptLoader.loadShellScript("pslist.sh",
					os.toLowerCase());
			diskScript = ShellScriptLoader.loadShellScript("diskstat.sh",
					os.toLowerCase());
			sysInfoScript = ShellScriptLoader.loadShellScript("sysinfo.sh",
					os.toLowerCase());
		}

		String script = applyEnv(statScript, statMap);

		executeCommand(script, false, statMap);

		if (readNetworkStatus) {
			List<String> list = executeCommand(sockScript, true);
			sockText = list;
		}

		if (readDiskStatus) {
			List<String> list = executeCommand(diskScript, true);
			diskText = String.join("\n", list);
		}

		if (readSysinfo) {
			List<String> list = executeCommand(sysInfoScript, false);
			sysInfoText = String.join("\n", list);
		}

		procList = executeCommand(applyEnv(procScript, psMap), true);

		System.out.println("Process count:" + procList.size());

		// System.out.println("PROC_LIST: " + procList + "\n\n");
	}

	private String applyEnv(String text, Map<String, String> environment) {
		for (String key : environment.keySet()) {
			String val = environment.get(key);
			if (val != null) {
				text = text.replace("#" + key + "={env}", key + "=" + val);
			}
		}
		return text;
	}

	private int executeCmd(String cmd, List<String> lines) throws Exception {
		// System.out.println(cmd);
		if (wrapper == null || !wrapper.isConnected()) {
			wrapper = connect();
		}
		ChannelExec exec = wrapper.getExecChannel();
		InputStream in = exec.getInputStream();
		exec.setCommand(cmd);
		exec.connect();

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new GZIPInputStream(in)));
		while (true) {
			String line = reader.readLine();
			if (line != null && line.length() > 0) {
				lines.add(line);
			}
			if (exec.getExitStatus() >= 0) {
				break;
			}
		}

		// System.err.flush();

		reader.close();
		int ret = exec.getExitStatus();
		System.err.println("Exit code: " + ret);
		exec.disconnect();
		return ret;
	}

	private void executeCommand(String cmd, boolean compressed,
			Map<String, String> environment) throws Exception {

		List<String> list = executeCommand(cmd, compressed);

		for (String line : list) {
			if (line.contains("=")) {
				int index = line.indexOf("=");
				String key = line.substring(0, index).trim();
				String val = line.substring(index + 1).trim();
				environment.put(key, val);
			}
		}
	}

//	private List<String> executePsCmd(String cmd) throws Exception {
//		List<String> list = new ArrayList<>();
//		if (wrapper == null || !wrapper.isConnected()) {
//			wrapper = connect();
//		}
//		ChannelExec exec = wrapper.getExecChannel();
//		InputStream in = exec.getInputStream();
//		exec.setCommand(cmd);
//		exec.connect();
//
//		InputStream in2 = new GZIPInputStream(in);
//		StringBuilder sb = new StringBuilder();
//
////		BufferedReader reader = new BufferedReader(
////				new InputStreamReader(new GZIPInputStream(in)));
//		while (true) {
//			int x = in2.read();
//
//			if (x == '\n') {
//				list.add(sb.toString());
//				sb = new StringBuilder();
//			}
//			if (x != -1)
//				sb.append((char) x);
//
//			if (exec.getExitStatus() != -1)
//				break;
//		}
//
//		// System.err.flush();
//		in.close();
//		int ret = exec.getExitStatus();
//		System.err.println("Exit code: " + ret);
//
//		exec.disconnect();
//		return list;
//	}

//	private void connect() throws Exception {
//		wrapper = new SshWrapper(info);
//		boolean askForAuth = false;
//		while (true) {
//			if (info.getUser() == null || info.getUser().length() < 1
//					|| askForAuth) {
//				Credentials cr = CredentialsDialog.promptCredentials();
//				if (cr == null) {
//					throw new Exception("User cancelled the operation");
//				}
//				info.setUser(cr.getUser());
//				info.setPassword(cr.getPass());
//			}
//			try {
//				wrapper.connect();
//				break;
//			} catch (Exception e) {
//				if (!(e.getCause() instanceof IOException)) { // jsch throws
//																// jsch
//																// exception for
//																// everything
//					askForAuth = true;
//				}
//				e.printStackTrace();
//				if (JOptionPane.showConfirmDialog(null,
//						"Unable to connect to server. Retry?") != JOptionPane.YES_OPTION) {
//					throw new Exception("User cancelled the operation");
//				}
//			}
//		}
//	}

	@Override
	public void reconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		try {
			wrapper.disconnect();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void setSocketText() {
		System.out.println(sockText);
		if (readNetworkStatus) {
			StringBuilder sb = new StringBuilder();
			for (String l : sockText) {
				if (searchText == null || searchText.length() < 1) {
					sb.append(l + "\n");
				} else if (l.contains(searchText)) {
					sb.append(l + "\n");
				}
			}
			socketStatus.setText(sb.toString());
			socketStatus.setCaretPosition(0);
			readNetworkStatus = false;
		}
	}

	private void updateStats() {

		SwingUtilities.invokeLater(() -> {
			// System.out.println(environment);
			processTableModel.updateData(procList);// environment.get("PROCESS_TABLE"));
			setSocketText();
			if (!tableResized) {
				final TableColumnModel columnModel = processTable
						.getColumnModel();
				for (int column = 0; column < processTable
						.getColumnCount(); column++) {
					TableColumn col = columnModel.getColumn(column);
					if (column == 0) {
						col.setPreferredWidth(300);
					} // col.getPreferredWidth());
				}
				tableResized = true;
			}

//			StringBuilder sb = new StringBuilder();
//			for (String l : listLines) {
//				sb.append(l + "\n");
//			}
//			socketStatus.setText(sb.toString());
			// System.out.println("SB--" + sb);
			if (readDiskStatus) {
				txtDiskStat.setText(diskText);
				txtDiskStat.setCaretPosition(0);
				readDiskStatus = false;
			}

			if (readSysinfo) {
				txtSysInfo.setText(sysInfoText);
				txtSysInfo.setCaretPosition(0);
				readSysinfo = false;
			}
			// diskTableModel.updateTable(environment.get("DISK_USAGE_TABLE"));
			loadPanel.updateValues(statMap);
		});

	}

	private void killProcess() {
		int[] rows = processTable.getSelectedRows();
		if (rows == null || rows.length < 1) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (int i : rows) {
			int pid = processTableModel
					.getPid(processTable.convertRowIndexToModel(i));
			if (pid != -1) {
				sb.append(" " + pid);
			}
		}
		System.out.println(sb);
		commandToExecute = "#!/bin/sh\nkill -9 " + sb;
		t.interrupt();
	}

	private void runCommandsIfAny() {
		if (commandToExecute == null || commandToExecute.length() < 1) {
			return;
		}
		listLines.clear();
		try {
			System.out.println("commandToExecute: " + commandToExecute);
			executeCmd(commandToExecute, listLines);
		} catch (Exception e) {
			e.printStackTrace();
		}
		commandToExecute = null;
	}

	@Override
	public void run() {
		while (!stopFlag.get()) {
			try {
				runCommandsIfAny();
				retrieveStats();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateStats();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(sleepInterval * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

//	private void killProc(int[] pid) {
//
//	}

//	private List<ProcessTableEntry> parseProcessTable(String data) {
//		List<ProcessTableEntry> plist = new ArrayList<>();
//		if (data == null || data.length() < 1) {
//			return plist;
//		}
//		String rowArr[] = data.split(";");
//		String[] colums = null;
//		if (rowArr.length > 0) {
//			colums = rowArr[0].split("\\|");
//		}
//
//		Object[][] tableData = new Object[rowArr.length - 1][];
//		Class<?>[] clazz = new Class<?>[colums.length];
//		for (int i = 0; i < colums.length; i++) {
//			clazz[i] = getClassForColumn(colums[i]);
//		}
//
//		for (int i = 1; i < rowArr.length; i++) {
//			String[] cols = rowArr[i].split("\\|");
//			tableData[i] = new Object[cols.length];
//			for (int j = 0; j < tableData[i].length; j++) {
//				Class<?> clz = clazz[j];
//				if (clz == Integer.class) {
//					tableData[i][j] = Integer.parseInt(cols[j]);
//				} else if (clz == Double.class) {
//					tableData[i][j] = Double.parseDouble(cols[j]);
//				} else {
//					tableData[i][j] = cols[j];
//				}
//			}
//		}
//
//		return plist;
//	}

	private void createNiceMenu() {
		popPrio = new JPopupMenu();
		ActionListener e = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JMenuItem item = (JMenuItem) e.getSource();
				Integer nice = (Integer) item
						.getClientProperty("nice.priority");
				int[] rows = processTable.getSelectedRows();
				if (rows == null || rows.length < 1) {
					return;
				}
				StringBuilder sb = new StringBuilder();
				for (int i : rows) {
					int pid = processTableModel
							.getPid(processTable.convertRowIndexToModel(i));
					if (pid != -1) {
						sb.append(" " + pid);
					}
				}
				System.out.println(sb);
				commandToExecute = "#!/bin/sh\nrenice " + nice + " " + sb
						+ "|gzip|cat";
				t.interrupt();
			}
		};
		JMenuItem item1 = new JMenuItem("-20 Highest priority");
		item1.putClientProperty("nice.priority", -20);
		JMenuItem item2 = new JMenuItem("-10 High priority");
		item2.putClientProperty("nice.priority", -10);
		JMenuItem item3 = new JMenuItem("0 Normal priority");
		item3.putClientProperty("nice.priority", 0);
		JMenuItem item4 = new JMenuItem("10 Lower priority");
		item4.putClientProperty("nice.priority", 10);
		JMenuItem item5 = new JMenuItem("19 Lowest priority");
		item5.putClientProperty("nice.priority", 19);
		popPrio.add(item1);
		popPrio.add(item2);
		popPrio.add(item3);
		popPrio.add(item4);
		popPrio.add(item5);

		item1.addActionListener(e);
		item2.addActionListener(e);
		item3.addActionListener(e);
		item4.addActionListener(e);
		item5.addActionListener(e);
	}

	private void createSigMenu() {
		popSig = new JPopupMenu();
		ActionListener e = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String sig = e.getActionCommand();
				int[] rows = processTable.getSelectedRows();
				if (rows == null || rows.length < 1) {
					return;
				}
				StringBuilder sb = new StringBuilder();
				for (int i : rows) {
					int pid = processTableModel
							.getPid(processTable.convertRowIndexToModel(i));
					if (pid != -1) {
						sb.append(" " + pid);
					}
				}
				System.out.println(sb);
				commandToExecute = "#!/bin/sh\nkill -s " + sig + " " + sb
						+ "|gzip|cat";
				t.interrupt();
			}
		};

		try (InputStream in = getClass()
				.getResourceAsStream("/etc/signals.txt")) {
			if (in == null) {
				return;
			}
			StringBuilder sb = new StringBuilder();
			while (true) {
				int x = in.read();
				if (x == -1) {
					break;
				}
				sb.append((char) x);
			}
			String[] sigs = sb.toString().split("\n");
			for (String sig : sigs) {
				if (sig == null || sig.length() < 1) {
					continue;
				}
				JMenuItem item1 = new JMenuItem(sig);
				popSig.add(item1);
				item1.addActionListener(e);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

//		popSig.setPreferredSize(
//				new Dimension(Utility.toPixel(150), Utility.toPixel(200)));

	}

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
		return UIManager.getIcon("ServerTools.taskmgrIcon16");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#getTitle()
	 */
	@Override
	public String getTitle() {
		return TextHolder.getString("sysmon.title");
	}

//	private ChannelExec connectImpl() throws Exception {
//		while (true) {
//			wrapper = super.connect();
//			try {
//				return wrapper.getExecChannel();
//			} catch (Exception e) {
//				try {
//					wrapper.disconnect();
//				} catch (Exception e1) {
//				}
//			}
//			if (JOptionPane.showConfirmDialog(null,
//					"Unable to connect to server. Retry?") != JOptionPane.YES_OPTION) {
//				throw new Exception("User cancelled the operation");
//			}
//		}
//	}

	private List<String> executeCommand(String cmd, boolean compressed)
			throws Exception {
		List<String> list = new ArrayList<>();
		if (wrapper == null || !wrapper.isConnected()) {
			wrapper = connect();
		}
		ChannelExec exec = wrapper.getExecChannel();
		InputStream in = exec.getInputStream();
		exec.setCommand(cmd);
		exec.connect();

		InputStream inStream = compressed ? new GZIPInputStream(in) : in;
		StringBuilder sb = new StringBuilder();
		while (true) {
			int x = inStream.read();

			if (x == '\n') {
				list.add(sb.toString());
				sb = new StringBuilder();
			}
			if (x != -1 && x != '\n')
				sb.append((char) x);

			if (exec.getExitStatus() != -1 && x == -1) {
				break;
			}
		}

		in.close();
		int ret = exec.getExitStatus();
		System.err.println("Exit code: " + ret);

		exec.disconnect();
		return list;
	}

}

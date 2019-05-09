package nixexplorer.widgets.sysmon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

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

import nixexplorer.ShellScriptLoader;
import nixexplorer.TextHolder;
import nixexplorer.app.AppContext;
import nixexplorer.app.components.CustomTabbedPane;
import nixexplorer.app.components.PriviledgedUtility;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.FileInfo;
import nixexplorer.core.ssh.SshUtility;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.widgets.Widget;
import nixexplorer.widgets.component.SudoDialog;
import nixexplorer.widgets.component.SudoDialog.SudoResult;
import nixexplorer.widgets.console.TerminalDialog;
import nixexplorer.widgets.folderview.remote.AskForPriviledgeDlg;
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
	private AtomicBoolean readSysinfo = new AtomicBoolean(true),
			readNetworkStatus = new AtomicBoolean(true),
			readDiskStatus = new AtomicBoolean(true),
			readPsStatus = new AtomicBoolean(true),
			readServiceStatus = new AtomicBoolean(true);
	private JButton btnSockRefresh, btnDiskRefresh, btnInfoRefresh,
			btnProcRefresh;
	private Map<String, String> statMap;
	private Map<String, String> psMap;
	private boolean tableResized = false;
	private JSpinner spInterval;
	private AtomicBoolean runAsSuperUser = new AtomicBoolean(false);
	private JCheckBox chkRunAsSuperUserSock, chkRunAsSuperUserPs;
	private boolean first = true;
	private final Cursor DEFAULT_CURSOR;
	private ServicePanel servicePanel;
	private List<String> services = new ArrayList<>();

	public SystemMonitorWidget(SessionInfo info, String[] args,
			AppSession appSession, Window window) {
		super(info, args, appSession, window);

		DEFAULT_CURSOR = getCursor();
		setCursor(new Cursor(Cursor.WAIT_CURSOR));

		statMap = new HashMap<>();
		psMap = new HashMap<>();

		chkRunAsSuperUserSock = new JCheckBox(
				TextHolder.getString("sysmon.superuser"));
		chkRunAsSuperUserPs = new JCheckBox(
				TextHolder.getString("sysmon.superuser"));

		chkRunAsSuperUserSock.addActionListener(e -> {
			runAsSuperUser.set(chkRunAsSuperUserSock.isSelected());
			chkRunAsSuperUserPs.setSelected(chkRunAsSuperUserSock.isSelected());
			servicePanel.setUseSuperUser(chkRunAsSuperUserSock.isSelected());
		});

		chkRunAsSuperUserPs.addActionListener(e -> {
			runAsSuperUser.set(chkRunAsSuperUserPs.isSelected());
			chkRunAsSuperUserSock.setSelected(chkRunAsSuperUserPs.isSelected());
			servicePanel.setUseSuperUser(chkRunAsSuperUserPs.isSelected());
		});

		btnDiskRefresh = new JButton(TextHolder.getString("sysmon.refresh"));
		btnInfoRefresh = new JButton(TextHolder.getString("sysmon.refresh"));
		btnSockRefresh = new JButton(TextHolder.getString("sysmon.refresh"));
		btnProcRefresh = new JButton(TextHolder.getString("sysmon.refresh"));

		btnDiskRefresh.addActionListener(e -> {
			readDiskStatus.set(true);
			t.interrupt();
		});

		btnInfoRefresh.addActionListener(e -> {
			readSysinfo.set(true);
			t.interrupt();
		});

		btnSockRefresh.addActionListener(e -> {
			readNetworkStatus.set(true);
			t.interrupt();
		});

		btnProcRefresh.addActionListener(e -> {
			readPsStatus.set(true);
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
		processTable.setRowHeight(Utility.toPixel(30));
		processTable.setShowGrid(false);

		TableRowSorter<ProcessTableModel> sorter = new TableRowSorter<ProcessTableModel>(
				processTableModel);
		processTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		processTable.setFillsViewportHeight(true);

		// processTable.setAutoCreateRowSorter(true);
		processTable.setRowSorter(sorter);
		processTableModel.setTable(processTable);

		spInterval = new JSpinner(new SpinnerNumberModel(100, 1, 100, 1));
		spInterval.setMaximumSize(spInterval.getPreferredSize());

		spInterval.setValue(
				AppContext.INSTANCE.getConfig().getMonitor().getInterval());

		spInterval.addChangeListener(e -> {
			int interval = (Integer) spInterval.getValue();
			System.out.println("New interval: " + interval);
			this.sleepInterval = interval;
			this.t.interrupt();

			AppContext.INSTANCE.getConfig().getMonitor().setInterval(interval);
			AppContext.INSTANCE.getConfig().save();
		});

		this.sleepInterval = AppContext.INSTANCE.getConfig().getMonitor()
				.getInterval();

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
			txtProcessSearch.setText("");
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

		Box bottomBox = Box.createHorizontalBox();
//		bottomBox.setBorder(new EmptyBorder(Utility.toPixel(5),
//				Utility.toPixel(5), Utility.toPixel(5), Utility.toPixel(5)));
//		bottomBox.add(
//				Box.createRigidArea(new Dimension(Utility.toPixel(10), 0)));
		bottomBox.add(chkRunAsSuperUserPs);
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
		btop1.add(btnProcRefresh);
		// btop1.add(lblInterval);
		// btop1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		// btop1.add(spInterval);

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

		loadPanel = new SystemLoadPanel(lblInterval, spInterval);
		loadPanel.setBorder(new EmptyBorder(Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5), Utility.toPixel(5)));

		JPanel socketPanel = new JPanel(new BorderLayout());
		socketPanel.add(chkRunAsSuperUserSock, BorderLayout.NORTH);
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
			filterAndSetSocketText();
		});
		clearBtn = new JButton(TextHolder.getString("sysmon.clear"));
		clearBtn.addActionListener(e -> {
			searchText = null;
			txtSockSearch.setText("");
			filterAndSetSocketText();
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

		servicePanel = new ServicePanel();
		servicePanel.setRefreshActionListener(e -> {
			readServiceStatus.set(true);
			t.interrupt();
		});
		servicePanel.setElevationActionListener(e -> {
			runAsSuperUser.set(servicePanel.getUseSuperUser());
			chkRunAsSuperUserSock.setSelected(servicePanel.getUseSuperUser());
			chkRunAsSuperUserPs.setSelected(servicePanel.getUseSuperUser());
		});

		servicePanel.setStartServiceActionListener(e -> {
			String cmd = servicePanel.getStartServiceCommand();
			if (cmd != null) {
				commandToExecute = cmd;
				t.interrupt();
			}
		});

		servicePanel.setStopServiceActionListener(e -> {
			String cmd = servicePanel.getStopServiceCommand();
			if (cmd != null) {
				commandToExecute = cmd;
				t.interrupt();
			}
		});

		servicePanel.setRestartServiceActionListener(e -> {
			String cmd = servicePanel.getRestartServiceCommand();
			if (cmd != null) {
				commandToExecute = cmd;
				t.interrupt();
			}
		});

		servicePanel.setReloadServiceActionListener(e -> {
			String cmd = servicePanel.getReloadServiceCommand();
			if (cmd != null) {
				commandToExecute = cmd;
				t.interrupt();
			}
		});

		servicePanel.setEnableServiceActionListener(e -> {
			String cmd = servicePanel.getEnableServiceCommand();
			if (cmd != null) {
				commandToExecute = cmd;
				t.interrupt();
			}
		});

		servicePanel.setDisableServiceActionListener(e -> {
			String cmd = servicePanel.getDisableServiceCommand();
			if (cmd != null) {
				commandToExecute = cmd;
				t.interrupt();
			}
		});

		tabs.addCustomTab(TextHolder.getString("sysmon.socketTitle"),
				socketPanel);
		tabs.addCustomTab(TextHolder.getString("sysmon.serviceTitle"),
				servicePanel);
		tabs.addCustomTab(TextHolder.getString("sysmon.diskTitle"),
				panDiskInfo);
		tabs.addCustomTab(TextHolder.getString("sysmon.processTitle"), panel1);
		tabs.addCustomTab(TextHolder.getString("sysmon.sysinfo"), panSysInfo);
		tabs.addCustomTab(TextHolder.getString("sysmon.loadTitle"), loadPanel);

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
			wrapper = SshUtility.connectWrapper(info, widgetClosed);
		}

		if (os == null) {
			List<String> list = executeCommand(unameCmd, false);
			String os = null;
			if (list.size() < 1) {
				System.out.println("Unable to detect OS");
				return;
			}
			os = list.get(0);

			final String strOs = os;

			if (!"Linux".equals(strOs)) {
				this.stopFlag.set(true);
				SwingUtilities.invokeLater(() -> {
					showUnsupportedOS(strOs);
				});
			}

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

		if (readNetworkStatus.get()) {
			List<String> list = runAsSuperUser.get()
					? runPriviledged(sockScript)
					: executeCommand(sockScript, false);
			sockText = list;
		}

		if (readDiskStatus.get()) {
			List<String> list = executeCommand(diskScript, false);
			diskText = String.join("\n", list);
		}

		if (readSysinfo.get()) {
			List<String> list = executeCommand(sysInfoScript, false);
			sysInfoText = String.join("\n", list);
		}

		if (readPsStatus.get()) {
			procScript = procScript.replace("<PROC_LIST_COLUMS>",
					processTableModel.getCommandString());
			procList = executeCommand(procScript, false);
			System.out.println("Process count:" + procList.size());
		}

		if (readServiceStatus.get()) {
			services = executeCommand(servicePanel.getServiceListCommand(),
					false);
			System.out.println("services count:" + services.size());
			servicePanel.setServiceData(services);
		}

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

	private void executeCmd(String cmd, List<String> lines) throws Exception {
		if (wrapper == null || !wrapper.isConnected()) {
			wrapper = SshUtility.connectWrapper(info, widgetClosed);
		}
		if (SshUtility.executeCommand(wrapper, cmd, false, lines) != 0) {
			throw new Exception();
		}

//		ChannelExec exec = wrapper.getExecChannel();
//		InputStream in = exec.getInputStream();
//		exec.setCommand(cmd);
//		exec.connect();
//
//		BufferedReader reader = new BufferedReader(
//				new InputStreamReader(new GZIPInputStream(in)));
//		while (true) {
//			String line = reader.readLine();
//			if (line != null && line.length() > 0) {
//				lines.add(line);
//			}
//			if (exec.getExitStatus() >= 0) {
//				break;
//			}
//		}
//
//		// System.err.flush();
//
//		reader.close();
//		int ret = exec.getExitStatus();
//		System.err.println("Exit code: " + ret);
//		exec.disconnect();
//		return ret;
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

	private void updateStats() {

		SwingUtilities.invokeLater(() -> {
			if (first) {
				setCursor(DEFAULT_CURSOR);
			}
			// System.out.println(environment);

//			StringBuilder sb = new StringBuilder();
//			for (String l : listLines) {
//				sb.append(l + "\n");
//			}
//			socketStatus.setText(sb.toString());
			// System.out.println("SB--" + sb);
			if (readDiskStatus.get()) {
				txtDiskStat.setText(diskText);
				txtDiskStat.setCaretPosition(0);
				readDiskStatus.set(false);
			}

			if (readSysinfo.get()) {
				txtSysInfo.setText(sysInfoText);
				txtSysInfo.setCaretPosition(0);
				readSysinfo.set(false);
			}

			if (readNetworkStatus.get()) {
				filterAndSetSocketText();
				readNetworkStatus.set(false);
			}

			if (readPsStatus.get()) {
				processTableModel.updateData(procList);// environment.get("PROCESS_TABLE"));
				if (!tableResized) {
					final TableColumnModel columnModel = processTable
							.getColumnModel();
					for (int column = 0; column < processTable
							.getColumnCount(); column++) {
						TableColumn col = columnModel.getColumn(column);
						if (column == 0) {
							col.setPreferredWidth(Utility.toPixel(300));
						} else {
							col.setPreferredWidth(Utility.toPixel(150));// col.getPreferredWidth());
						}
					}
					tableResized = true;
				}
				readPsStatus.set(false);
			}
			// diskTableModel.updateTable(environment.get("DISK_USAGE_TABLE"));
			loadPanel.updateValues(statMap, sysInfoText);
		});

		if (readServiceStatus.get()) {
			servicePanel.setServiceData(services);
			readServiceStatus.set(false);
		}

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
		commandToExecute = "kill -9 " + sb;
		t.interrupt();
	}

	private void runCommandsIfAny() {
		if (commandToExecute == null || commandToExecute.length() < 1) {
			return;
		}
		listLines.clear();
		try {
			System.out.println("commandToExecute: " + commandToExecute);
			if (runAsSuperUser.get()) {
				listLines = runPriviledged(commandToExecute);
			} else {
				executeCmd(commandToExecute, listLines);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (!widgetClosed.get()) {
				JOptionPane.showMessageDialog(getWindow(),
						TextHolder.getString("sysmon.error"));
			}
		}
		commandToExecute = null;
	}

	/**
	 * @param commandToExecute
	 */
	private List<String> runPriviledged(String commandToExecute)
			throws Exception {
		List<String> list = new ArrayList<>();
		
//		String suCmd = AskForPriviledgeDlg.askForPriviledge();
//		if (suCmd == null) {
//			return list;
//		}
//		
//		boolean sudo = suCmd.startsWith("sudo");
//		if (sudo) {
//			SudoResult res=SudoDialog.executeCommand(getWindow(), commandToExecute, wrapper);
//			if(res.getExitCode()!=0) {
//				return list;
//			}else {
//				list.addAll(Arrays.asList(
//						new String(res.getOutput()).split("\n")));
//			}
//		}
		
		String suCmd = PriviledgedUtility
				.generatePriviledgedCommand(commandToExecute);
		if (suCmd == null) {
			return list;
		}
		TerminalDialog terminalDialog = new TerminalDialog(getInfo(),
				new String[] { "-c", suCmd }, getAppSession(), getWindow(),
				"Command window", true, true, wrapper);
		terminalDialog.setLocationRelativeTo(getWindow());
		terminalDialog.setVisible(true);
		if (terminalDialog.getExitCode() == 0) {
			list.addAll(Arrays.asList(
					new String(terminalDialog.getOutput()).split("\n")));
		} else {
			throw new Exception(
					"Unsuccessfull exit code: " + terminalDialog.getExitCode());
		}
		return list;
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
			if (stopFlag.get()) {
				break;
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
				commandToExecute = "renice " + nice + " " + sb;
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
				commandToExecute = "kill -s " + sig + " " + sb;
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
		super.viewClosed();
		stopFlag.set(true);
		closeInitiated = true;
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
			wrapper = SshUtility.connectWrapper(info, widgetClosed);
		}
		SshUtility.executeCommand(wrapper, cmd, compressed, list);
		return list;

//		ChannelExec exec = wrapper.getExecChannel();
//		InputStream in = exec.getInputStream();
//		exec.setCommand(cmd);
//		exec.connect();
//
//		InputStream inStream = compressed ? new GZIPInputStream(in) : in;
//		StringBuilder sb = new StringBuilder();
//		while (true) {
//			int x = inStream.read();
//
//			if (x == '\n') {
//				list.add(sb.toString());
//				sb = new StringBuilder();
//			}
//			if (x != -1 && x != '\n')
//				sb.append((char) x);
//
//			if (exec.getExitStatus() != -1 && x == -1) {
//				break;
//			}
//		}
//
//		in.close();
//		int ret = exec.getExitStatus();
//		System.err.println("Exit code: " + ret);
//
//		exec.disconnect();
//		return list;
	}

	private void showUnsupportedOS(String os) {
		JLabel lbl = new JLabel(
				String.format(TextHolder.getString("sysmon.unsupported"), os));
		lbl.setFont(new Font(Font.DIALOG, Font.PLAIN, Utility.toPixel(15)));
		lbl.setForeground(Color.WHITE);
		lbl.setBorder(new EmptyBorder(Utility.toPixel(10), Utility.toPixel(10),
				Utility.toPixel(10), Utility.toPixel(10)));
		lbl.setOpaque(true);
		lbl.setBackground(Color.RED);
		this.removeAll();
		this.add(lbl, BorderLayout.NORTH);
		this.revalidate();
		this.repaint();
	}

	private void filterAndSetSocketText() {
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
	}

	private void createServicePanel() {

	}
}

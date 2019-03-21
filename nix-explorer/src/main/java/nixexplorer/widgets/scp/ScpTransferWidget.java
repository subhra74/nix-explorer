/**
 * 
 */
package nixexplorer.widgets.scp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import nixexplorer.TextHolder;
import nixexplorer.app.components.DisposableView;
import nixexplorer.app.session.AppSession;
import nixexplorer.core.ForeignServerInfo;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.widgets.console.TabbedConsoleWidget;
import nixexplorer.widgets.console.TerminalDialog;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class ScpTransferWidget extends JDialog implements DisposableView {
	/**
	 * 
	 */
	private AppSession appSession;
	private SessionInfo info;
	private JPanel content, frontPanel, backPanel;
	private JPanel panels[][];
	private JButton btnSave, btnManage, btnConnect, btnDelete, btnNew;
	private JTextField txtHost, txtUser, txtFolder;
	private JSpinner spPort;
	private JLabel lblError;
	private ScpServerInfo serverInfo;
	private ScpTableModel connectionTableModel;
	private JTable connectionTable;
	private List<String> files = new ArrayList<>(), folders = new ArrayList<>();
	private JPanel contentPage;
	private TabbedConsoleWidget console;
	private Window window;
	protected AtomicBoolean widgetClosed = new AtomicBoolean(Boolean.FALSE);
	// private List<ScpServerInfo> serverList = new ArrayList<>();

	public ScpTransferWidget(SessionInfo info, List<String> files,
			List<String> folders, AppSession appSession, Window window) {
		super(window);
		this.info = info;
		this.window = window;
		this.appSession = appSession;
		init();
		setSize(Utility.toPixel(640), Utility.toPixel(480));
		setLocationRelativeTo(null);
		this.files.addAll(files);
		this.folders.addAll(folders);
	}

	public void run() {
		String scpCommand = genrateScpCmd();
		System.out.println("Scp command: " + scpCommand);
		TerminalDialog dlg = new TerminalDialog(info,
				new String[] { "-c", scpCommand }, appSession, window,
				"Command window");
		this.dispose();
		dlg.setLocationRelativeTo(window);
		dlg.setVisible(true);
//		this.getContentPane().removeAll();
//		this.console = new TabbedConsoleWidget(info,
//				new String[] { "-c", genrateScpCmd() }, appSession,
//				this.window);
//		this.add(console);
//		this.getContentPane().revalidate();
//		this.getContentPane().repaint();
//		System.out.println("done");
	}

	private String genrateScpCmd() {
		StringBuilder sb = new StringBuilder();
		if (folders.size() > 0) {
			for (String folder : folders) {
				if (sb.length() > 0) {
					sb.append("; ");
				}
				sb.append("scp -pr \"" + folder + "\" "
						+ (serverInfo.getUser() + "@" + serverInfo.getHost()
								+ ":\"'" + serverInfo.getFolder() + "'\""));
			}
		}

		if (files.size() > 0) {
			if (sb.length() > 0) {
				sb.append("; ");
			}
			sb.append("scp ");
			for (String file : files) {
				sb.append(" \"" + file + "\" ");
			}
			sb.append((serverInfo.getUser() + "@" + serverInfo.getHost()
					+ ":\"'" + serverInfo.getFolder() + "'\""));
			sb.append(";exit");
		}
		return sb.toString();
	}

	private void init() {
		contentPage = new JPanel(new BorderLayout());
		setTitle(TextHolder.getString("filetransfer.title"));
		setSize(new Dimension(Utility.toPixel(640), Utility.toPixel(480)));
		setLocationRelativeTo(null);
		JPanel connectionPanel = new JPanel(new BorderLayout());
		connectionPanel.add(new JLabel("Connections"), BorderLayout.NORTH);
		lblError = new JLabel("");
		lblError.setForeground(Color.RED);
		connectionPanel.add(lblError, BorderLayout.SOUTH);

		connectionTableModel = new ScpTableModel(appSession, info);

		connectionTable = new JTable(connectionTableModel);
		connectionTable.getSelectionModel().addListSelectionListener(e -> {
			int index = e.getFirstIndex();
			if (index != -1) {
				ScpServerInfo info = connectionTableModel.getItemAt(index);
				txtUser.setText(info.getUser());
				txtHost.setText(info.getHost());
				txtFolder.setText(info.getFolder());
				spPort.setValue(info.getPort());
			}
		});

		connectionPanel.add(new JScrollPane(connectionTable));

		Box cb = Box.createVerticalBox();

		JLabel lblHost = new JLabel("Host");
		lblHost.setAlignmentX(Box.LEFT_ALIGNMENT);
		cb.add(lblHost);

		txtHost = new JTextField(20);
		txtHost.setMaximumSize(txtHost.getPreferredSize());
		txtHost.setAlignmentX(Box.LEFT_ALIGNMENT);
		cb.add(txtHost);

		JLabel lblPort = new JLabel("Port");
		lblPort.setAlignmentX(Box.LEFT_ALIGNMENT);
		cb.add(lblPort);

		spPort = new JSpinner(
				new SpinnerNumberModel(22, 1, Short.MAX_VALUE, 1));
		spPort.setAlignmentX(Box.LEFT_ALIGNMENT);
		cb.add(spPort);

		JLabel lblUser = new JLabel("User");
		lblUser.setAlignmentX(Box.LEFT_ALIGNMENT);
		cb.add(lblUser);

		txtUser = new JTextField(20);
		txtUser.setAlignmentX(Box.LEFT_ALIGNMENT);
		txtUser.setMaximumSize(txtUser.getPreferredSize());
		cb.add(txtUser);

		JLabel lblDir = new JLabel("Remote directory");
		lblDir.setAlignmentX(Box.LEFT_ALIGNMENT);
		cb.add(lblDir);

		txtFolder = new JTextField(20);
		txtFolder.setAlignmentX(Box.LEFT_ALIGNMENT);
		txtFolder.setMaximumSize(txtFolder.getPreferredSize());
		cb.add(txtFolder);

		cb.add(Box.createVerticalGlue());

		Box bb = Box.createHorizontalBox();

		btnNew = new JButton("New");
		btnNew.addActionListener(e -> {
			connectionTable.clearSelection();
			txtHost.setText("");
			txtUser.setText("");
			txtFolder.setText("");
			spPort.setValue(22);
		});
		bb.add(btnNew);
		bb.add(Box.createRigidArea(
				new Dimension(Utility.toPixel(5), Utility.toPixel(5))));
		btnSave = new JButton("Save");
		btnSave.addActionListener(e -> {
			updateAndSave();
		});
		bb.add(btnSave);
		bb.add(Box.createRigidArea(
				new Dimension(Utility.toPixel(5), Utility.toPixel(5))));

		btnDelete = new JButton("Delete");

		bb.add(btnDelete);

		bb.add(Box.createHorizontalGlue());

		btnConnect = new JButton("Send");
		btnConnect.addActionListener(e -> {
			updateAndSave();

			ScpServerInfo scpItem = new ScpServerInfo();
			scpItem.setFolder(txtFolder.getText());
			scpItem.setHost(txtHost.getText());
			scpItem.setUser(txtUser.getText());
			scpItem.setPort((Integer) spPort.getValue());

			serverInfo = scpItem;

			run();
		});
		bb.add(btnConnect);

		bb.setAlignmentX(Box.LEFT_ALIGNMENT);

		cb.add(bb);

		spPort.setMaximumSize(new Dimension(txtUser.getPreferredSize().width,
				spPort.getPreferredSize().height));

		contentPage.add(cb, BorderLayout.EAST);

		((JComponent) getContentPane()).setBorder(
				new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
						Utility.toPixel(5), Utility.toPixel(5)));

		contentPage.add(connectionPanel);

		this.add(contentPage);

//	panels = new JPanel[][] {
//			{ createSftpUploadPanel(), createScpUploadPanel(),
//					createFtpUploadPanel() },
//			{ createSftpDownloadPanel(), createScpDownloadPanel(),
//					createFtpDownloadPanel() } };
//	content = new JPanel(new BorderLayout());
//
//	cmbMode = new JComboBox<>(new String[] {
//			"Upload files from this server to a remote server",
//			"Download files from a remote server to this server" });
//
//	cmbSaveSites = new JComboBox<>(new String[] { "New stie" });
////	cmbSaveSites.setPreferredSize(new Dimension(
////			cmbSaveSites.getPreferredSize().width
////					+ cmbSaveSites.getPreferredSize().width / 2,
////			cmbSaveSites.getPreferredSize().height));
//
//	cmbProtocol.setPreferredSize(new Dimension(
//			cmbProtocol.getPreferredSize().width
//					+ cmbProtocol.getPreferredSize().width / 2,
//			cmbProtocol.getPreferredSize().height));
//	JPanel jp = new JPanel(new BorderLayout(5, 5));
//	jp.setAlignmentX(Box.LEFT_ALIGNMENT);
//	jp.add(cmbMode);
////	jp.add(cmbSaveSites, BorderLayout.EAST);
//
////	Box b2 = Box.createHorizontalBox();
////	b2.setAlignmentX(Box.LEFT_ALIGNMENT);
////	b2.add(new JLabel("Action"));
////	b2.add(Box.createHorizontalStrut(Utility.toPixel(5)));
////	b2.add(cmbMode);
////	b2.add(Box.createHorizontalGlue());
////	b2.add(btnSession);
////	b2.add(Box.createHorizontalGlue());
////	
////	b2.add(new JLabel("Protocol"));
////	b2.add(Box.createHorizontalStrut(Utility.toPixel(5)));
////	b2.add(cmbProtocol);
//
//	Box box1 = Box.createHorizontalBox();
//	box1.setAlignmentX(Box.LEFT_ALIGNMENT);
//	box1.add(cmbSaveSites);
//	// box1.add(Box.createHorizontalGlue());
//	box1.add(btnSave);
//
//	Box box2 = Box.createHorizontalBox();
//	box2.setAlignmentX(Box.LEFT_ALIGNMENT);
//	box2.add(new JLabel("Protocol"));
//	box2.add(Box.createHorizontalStrut(Utility.toPixel(5)));
//	box2.add(cmbProtocol);
//	box2.add(new JLabel("Host"));
//	box2.add(txtHost);
//	box2.add(new JLabel("Port"));
//	box2.add(spPort);
//	box2.add(new JLabel("User"));
//	box2.add(txtUser);
//	box2.add(Box.createHorizontalGlue());
//	box2.add(btnConnect);
//
//	Box b1 = Box.createVerticalBox();
//	b1.add(jp);
//	b1.add(box1);
//	b1.add(box2);
//
//	b1.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
//			Utility.toPixel(5), Utility.toPixel(5)));
//
//	cmbMode.addActionListener(e -> {
//		updateContent();
//	});
//
//	cmbProtocol.addActionListener(e -> {
//		updateContent();
//	});
//
//	frontPanel = new JPanel(new BorderLayout());
//	backPanel = new JPanel(new BorderLayout());
//
//	frontPanel.add(b1, BorderLayout.NORTH);
//	frontPanel.add(content);
//
//	add(frontPanel);

	}

	/**
	 * 
	 */
	private void updateAndSave() {
		if (txtUser.getText().length() < 1) {
			lblError.setText("User can not be blank");
			return;
		}

		if (txtHost.getText().length() < 1) {
			lblError.setText("Host can not be blank");
			return;
		}

		int index = connectionTable.getSelectedRow();

		ScpServerInfo scpItem = new ScpServerInfo();
		scpItem.setFolder(txtFolder.getText());
		scpItem.setHost(txtHost.getText());
		scpItem.setUser(txtUser.getText());
		scpItem.setPort((Integer) spPort.getValue());

		if (index != -1) {
			connectionTableModel.updateItem(index, scpItem);
		} else {
			connectionTableModel.addItem(scpItem);
		}
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
		dispose();
		return true;
	}

}

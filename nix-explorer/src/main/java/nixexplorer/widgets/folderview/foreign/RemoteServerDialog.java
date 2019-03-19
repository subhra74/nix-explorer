//package nixexplorer.widgets.folderview.foreign;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dimension;
//
//import javax.swing.Box;
//import javax.swing.JButton;
//import javax.swing.JComboBox;
//import javax.swing.JComponent;
//import javax.swing.JDialog;
//import javax.swing.JLabel;
//import javax.swing.JList;
//import javax.swing.JPanel;
//import javax.swing.JPasswordField;
//import javax.swing.JScrollPane;
//import javax.swing.JSpinner;
//import javax.swing.JTextField;
//import javax.swing.SpinnerNumberModel;
//import javax.swing.border.EmptyBorder;
//
//import nixexplorer.TextHolder;
//import nixexplorer.core.ForeignServerInfo;
//import nixexplorer.widgets.util.Utility;
//
//public class RemoteServerDialog extends JDialog {
//	private JComboBox<String> cmbMode, cmbProtocol, cmbSaveSites;
//	private JPanel content, frontPanel, backPanel;
//	private JPanel panels[][];
//	private JButton btnSave, btnManage, btnConnect, btnDelete;
//	private JTextField txtHost, txtUser, txtFolder;
//	private JPasswordField txtPassword;
//	private JSpinner spPort;
//
//	private ForeignServerInfo serverInfo;
//	private JLabel lblError;
//
//	public RemoteServerDialog() {
//		init();
//	}
//
//	private void init() {
//		setModal(true);
//		setTitle(TextHolder.getString("filetransfer.title"));
//		setSize(
//				new Dimension(Utility.toPixel(640), Utility.toPixel(480)));
//		setLocationRelativeTo(null);
//		JPanel connectionPanel = new JPanel(new BorderLayout());
//		connectionPanel.add(new JLabel("Connections"), BorderLayout.NORTH);
//		JList<String> listConnections = new JList<>();
//		connectionPanel.add(new JScrollPane(listConnections));
//		lblError = new JLabel("");
//		lblError.setForeground(Color.RED);
//		connectionPanel.add(lblError, BorderLayout.SOUTH);
//
//		Box cb = Box.createVerticalBox();
//
//		cmbProtocol = new JComboBox<>(new String[] { "SFTP", "FTP" });
//
//		cmbProtocol.setAlignmentX(Box.LEFT_ALIGNMENT);
//		cb.add(cmbProtocol);
//
//		JLabel lblHost = new JLabel("Host");
//		lblHost.setAlignmentX(Box.LEFT_ALIGNMENT);
//		cb.add(lblHost);
//
//		txtHost = new JTextField(20);
//		txtHost.setMaximumSize(txtHost.getPreferredSize());
//		txtHost.setAlignmentX(Box.LEFT_ALIGNMENT);
//		cb.add(txtHost);
//
//		JLabel lblPort = new JLabel("Port");
//		lblPort.setAlignmentX(Box.LEFT_ALIGNMENT);
//		cb.add(lblPort);
//
//		spPort = new JSpinner(
//				new SpinnerNumberModel(22, 1, Short.MAX_VALUE, 1));
//		spPort.setAlignmentX(Box.LEFT_ALIGNMENT);
//		cb.add(spPort);
//
//		JLabel lblUser = new JLabel("User");
//		lblUser.setAlignmentX(Box.LEFT_ALIGNMENT);
//		cb.add(lblUser);
//
//		txtUser = new JTextField(20);
//		txtUser.setAlignmentX(Box.LEFT_ALIGNMENT);
//		txtUser.setMaximumSize(txtUser.getPreferredSize());
//		cb.add(txtUser);
//
//		JLabel lblPass = new JLabel("Password");
//		lblPass.setAlignmentX(Box.LEFT_ALIGNMENT);
//		cb.add(lblPass);
//
//		txtPassword = new JPasswordField(20);
//		txtPassword.setAlignmentX(Box.LEFT_ALIGNMENT);
//		txtPassword.setMaximumSize(txtUser.getPreferredSize());
//		cb.add(txtPassword);
//
//		JLabel lblDir = new JLabel("Remote directory");
//		lblDir.setAlignmentX(Box.LEFT_ALIGNMENT);
//		cb.add(lblDir);
//
//		txtFolder = new JTextField(20);
//		txtFolder.setAlignmentX(Box.LEFT_ALIGNMENT);
//		txtFolder.setMaximumSize(txtFolder.getPreferredSize());
//		cb.add(txtFolder);
//
//		cb.add(Box.createVerticalGlue());
//
//		Box bb = Box.createHorizontalBox();
//
//		btnSave = new JButton("Save");
////	btnSave.setPreferredSize(new Dimension(txtUser.getPreferredSize().width,
////			btnSave.getPreferredSize().height));
//
//		bb.add(btnSave);
//
//		btnDelete = new JButton("Delete");
////	btnDelete.setPreferredSize(
////			new Dimension(txtUser.getPreferredSize().width,
////					btnDelete.getPreferredSize().height));
//
//		bb.add(btnDelete);
//
//		bb.add(Box.createHorizontalGlue());
//
//		btnConnect = new JButton("Connect");
//		btnConnect.addActionListener(e -> {
//			if (txtUser.getText().length() < 1) {
//				lblError.setText("User can not be blank");
//				return;
//			}
//
//			if (txtHost.getText().length() < 1) {
//				lblError.setText("Host can not be blank");
//				return;
//			}
//
//			serverInfo = new ForeignServerInfo(txtUser.getText(),
//					new String(txtPassword.getPassword()), txtHost.getText(),
//					txtFolder.getText(), (Integer) spPort.getValue(),
//					cmbProtocol.getSelectedIndex());
//
//			dispose();
//		});
//		bb.add(btnConnect);
//
//		bb.setAlignmentX(Box.LEFT_ALIGNMENT);
//
//		cb.add(bb);
//
//		cmbProtocol
//				.setMaximumSize(new Dimension(txtUser.getPreferredSize().width,
//						cmbProtocol.getPreferredSize().height));
//
//		spPort.setMaximumSize(new Dimension(txtUser.getPreferredSize().width,
//				spPort.getPreferredSize().height));
//
//		add(cb, BorderLayout.EAST);
//
//		((JComponent) getContentPane()).setBorder(
//				new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
//						Utility.toPixel(5), Utility.toPixel(5)));
//
//		add(connectionPanel);
//
////	panels = new JPanel[][] {
////			{ createSftpUploadPanel(), createScpUploadPanel(),
////					createFtpUploadPanel() },
////			{ createSftpDownloadPanel(), createScpDownloadPanel(),
////					createFtpDownloadPanel() } };
////	content = new JPanel(new BorderLayout());
////
////	cmbMode = new JComboBox<>(new String[] {
////			"Upload files from this server to a remote server",
////			"Download files from a remote server to this server" });
////
////	cmbSaveSites = new JComboBox<>(new String[] { "New stie" });
//////	cmbSaveSites.setPreferredSize(new Dimension(
//////			cmbSaveSites.getPreferredSize().width
//////					+ cmbSaveSites.getPreferredSize().width / 2,
//////			cmbSaveSites.getPreferredSize().height));
////
////	cmbProtocol.setPreferredSize(new Dimension(
////			cmbProtocol.getPreferredSize().width
////					+ cmbProtocol.getPreferredSize().width / 2,
////			cmbProtocol.getPreferredSize().height));
////	JPanel jp = new JPanel(new BorderLayout(5, 5));
////	jp.setAlignmentX(Box.LEFT_ALIGNMENT);
////	jp.add(cmbMode);
//////	jp.add(cmbSaveSites, BorderLayout.EAST);
////
//////	Box b2 = Box.createHorizontalBox();
//////	b2.setAlignmentX(Box.LEFT_ALIGNMENT);
//////	b2.add(new JLabel("Action"));
//////	b2.add(Box.createHorizontalStrut(Utility.toPixel(5)));
//////	b2.add(cmbMode);
//////	b2.add(Box.createHorizontalGlue());
//////	b2.add(btnSession);
//////	b2.add(Box.createHorizontalGlue());
//////	
//////	b2.add(new JLabel("Protocol"));
//////	b2.add(Box.createHorizontalStrut(Utility.toPixel(5)));
//////	b2.add(cmbProtocol);
////
////	Box box1 = Box.createHorizontalBox();
////	box1.setAlignmentX(Box.LEFT_ALIGNMENT);
////	box1.add(cmbSaveSites);
////	// box1.add(Box.createHorizontalGlue());
////	box1.add(btnSave);
////
////	Box box2 = Box.createHorizontalBox();
////	box2.setAlignmentX(Box.LEFT_ALIGNMENT);
////	box2.add(new JLabel("Protocol"));
////	box2.add(Box.createHorizontalStrut(Utility.toPixel(5)));
////	box2.add(cmbProtocol);
////	box2.add(new JLabel("Host"));
////	box2.add(txtHost);
////	box2.add(new JLabel("Port"));
////	box2.add(spPort);
////	box2.add(new JLabel("User"));
////	box2.add(txtUser);
////	box2.add(Box.createHorizontalGlue());
////	box2.add(btnConnect);
////
////	Box b1 = Box.createVerticalBox();
////	b1.add(jp);
////	b1.add(box1);
////	b1.add(box2);
////
////	b1.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
////			Utility.toPixel(5), Utility.toPixel(5)));
////
////	cmbMode.addActionListener(e -> {
////		updateContent();
////	});
////
////	cmbProtocol.addActionListener(e -> {
////		updateContent();
////	});
////
////	frontPanel = new JPanel(new BorderLayout());
////	backPanel = new JPanel(new BorderLayout());
////
////	frontPanel.add(b1, BorderLayout.NORTH);
////	frontPanel.add(content);
////
////	add(frontPanel);
//
//	}
//
//	public ForeignServerInfo getServerInfo() {
//		return serverInfo;
//	}
//
//	public void setServerInfo(ForeignServerInfo serverInfo) {
//		this.serverInfo = serverInfo;
//	}
//}

/**
 * 
 */
package nixexplorer.widgets.http;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.jcraft.jsch.ChannelShell;

import nixexplorer.TextHolder;
import nixexplorer.app.components.CustomTabbedPane;
import nixexplorer.app.components.FlatTabbedPane;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.widgets.Widget;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */

public class HttpClient extends Widget {
	private JComboBox<String> cmbProto;
	private JTextField txtUrl;
	private JButton btnSend;
	private JButton btnSave;
	private JSplitPane splitPane1, splitPane2, splitPane3;
	private JButton btnAdd, btnDel, btnExec, btnStop, btnBack, btnNew, btnClear;
	private JTextArea txtResult;
	private JTextField txtFile;
	private CustomTabbedPane requestTabs;
	private KeyValueTable queryTable, headerTable, formTable1, formTable2;
	private JTextField txtCommand;
	private JComboBox<String> cmbOptions;
	private JComboBox<String> cmbType;
	private JTextArea txtRawBody;
	private JComboBox<String> cmbOutputOptions;
	private SshWrapper wrapper;
	private DefaultListModel<HttpRequest> modelHistory;
	private JList<HttpRequest> listHistory;
	private JCheckBox chkFile;
	private HttpRequest request;
	private HttpRequestStore store;
	private JCheckBox chkAutoDetectAuthMethod, chkAutoDetectAuthMethodProxy,
			chkIgnoreSslError, chkVerboseOutput;
	private JTextField txtUser, txtProxyUser, txtProxy;
	private JPasswordField txtPass, txtProxyPass;
	private JTextField txtUploadFile;
	private JRadioButton radNoHeader, radVerbose, radResponseHeader;
	private AtomicBoolean stopFlag = new AtomicBoolean(false);

	/**
	 * @param env
	 * @param args
	 * @param parent
	 */
	public HttpClient(SessionInfo info, String[] args, AppSession appSession,
			Window window) {
		super(info, args, appSession, window);
		this.store = HttpRequestStore.getSharedInstance(appSession);
//		setTitle(TextHolder.getString("http.title"));
//		setAutoSize(false);
		setSize(new Dimension(Utility.toPixel(800), Utility.toPixel(600)));
		setPreferredSize(getSize());

		JPanel b0 = new JPanel(
				new BorderLayout(Utility.toPixel(5), Utility.toPixel(5)));
		b0.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));

		JLabel titleHistory = new JLabel("History");
		titleHistory
				.setFont(new Font(Font.DIALOG, Font.BOLD, Utility.toPixel(15)));
		b0.add(titleHistory, BorderLayout.NORTH);

		modelHistory = new DefaultListModel<>();
		listHistory = new JList<>(modelHistory);

		try {
			modelHistory.addAll(store.load());
		} catch (Exception e) {
			e.printStackTrace();
		}

		JScrollPane jspp = new JScrollPane(listHistory);
		// jspp.setBorder(UIManager.getBorder("Component.border"));
		b0.add(jspp);

		btnNew = new JButton("New request");
		btnClear = new JButton("Clear history");

		btnNew.addActionListener(e -> {
			newRequest();
		});

		btnClear.addActionListener(e -> {
			modelHistory.removeAllElements();
			saveList();
		});

		Box b20 = Box.createHorizontalBox();
		b20.add(btnNew);
		b20.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b20.add(btnClear);
		b0.add(b20, BorderLayout.SOUTH);

		cmbProto = new JComboBox<String>(new String[] { "GET", "POST", "PUT",
				"PATCH", "DELETE", "HEAD" });
		cmbProto.setEditable(true);

		txtUrl = new JTextField(30);

		btnSend = new JButton("Send");
		btnSend.addActionListener(e -> {
			if (request == null) {
				newRequest();
			}
			// headerTable
			fireRequest();
		});

//		Box b1 = Box.createHorizontalBox();
//		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
//		b1.add(btnNew);
//		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
//		b1.add(btnLoad);
//		b1.add(Box.createHorizontalGlue());
//		b1.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
//				Utility.toPixel(5), Utility.toPixel(5)));
//
//		add(b1, BorderLayout.NORTH);

		Box b2 = Box.createHorizontalBox();
		b2.setAlignmentX(Box.LEFT_ALIGNMENT);
		cmbProto.setMaximumSize(new Dimension(cmbProto.getMaximumSize().width,
				cmbProto.getPreferredSize().height));
		txtUrl.setMaximumSize(new Dimension(txtUrl.getMaximumSize().width,
				txtUrl.getPreferredSize().height));
		// b2.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b2.add(cmbProto);
		b2.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b2.add(txtUrl);
		b2.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b2.add(btnSend);
		// b2.add(Box.createHorizontalStrut(Utility.toPixel(5)));

		Box b3 = Box.createVerticalBox();
		b3.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		JLabel titleReq = new JLabel("Request");
		titleReq.setAlignmentX(Box.LEFT_ALIGNMENT);
		b3.add(titleReq);
		titleReq.setFont(new Font(Font.DIALOG, Font.BOLD, Utility.toPixel(15)));
		b3.add(Box.createVerticalStrut(Utility.toPixel(5)));
		b3.add(b2);

		requestTabs = new CustomTabbedPane();
		requestTabs.setAlignmentX(Box.LEFT_ALIGNMENT);

		// JPanel panelQuery = new JPanel(new BorderLayout());
//		DefaultTableModel queryModel = new DefaultTableModel(
//				new String[] { "Name", "Value" }, 0);
//		JTable queryTable = new JTable(queryModel);
//		panelQuery.add(new JScrollPane(queryTable));
//		Box bb1 = Box.createHorizontalBox();
//		JButton add1 = new JButton("Add");
//		add1.addActionListener(e -> {
//			int index = queryModel.getRowCount();
//			queryModel.addRow(new String[] { "", "" });
//			queryModel.fireTableDataChanged();
//			queryTable.setRowSelectionInterval(index, index);
//		});
//		makeSingleClickEditor(queryTable);
//		JButton del1 = new JButton("Delete");
//		del1.addActionListener(e -> {
//			int index = queryTable.getSelectedRow();
//			if (index != -1) {
//				queryModel.removeRow(index);
//				queryModel.fireTableDataChanged();
//			}
//		});
//		bb1.add(Box.createHorizontalGlue());
//		bb1.add(add1);
//		bb1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
//		bb1.add(del1);
//		panelQuery.add(bb1, BorderLayout.SOUTH);
		queryTable = new KeyValueTable(false);

		requestTabs.addCustomTab("Query parameters", queryTable);

		JPanel panelAuth = new JPanel(new BorderLayout());

		Box b12 = Box.createVerticalBox();
		JLabel lblHttpAuth = new JLabel("HTTP Authentication");
		lblHttpAuth
				.setFont(new Font(Font.DIALOG, Font.BOLD, Utility.toPixel(14)));
		JLabel lblUser = new JLabel("User name");
		JLabel lblPass = new JLabel("Password");

		txtUser = new JTextField(30);
		txtPass = new JPasswordField(30);

		lblHttpAuth.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		lblUser.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		lblPass.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		txtUser.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		txtPass.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		chkAutoDetectAuthMethod = new JCheckBox(
				"Automatically detect best authentication method");

		b12.add(lblHttpAuth);
		b12.add(lblUser);
		b12.add(txtUser);
		b12.add(lblPass);
		b12.add(txtPass);
		b12.add(chkAutoDetectAuthMethod, BorderLayout.SOUTH);

		JPanel floatPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		floatPanel.add(b12);

		panelAuth.add(floatPanel);

		requestTabs.addCustomTab("Authentication", panelAuth);

		requestTabs.addCustomTab("Proxy", createProxyPanel());

//		JPanel panelHeaders = new JPanel(new BorderLayout());
//		DefaultTableModel headerModel = new DefaultTableModel(
//				new String[] { "Name", "Value" }, 0);
//		JTable headerTable = new JTable(headerModel);
//		panelHeaders.add(new JScrollPane(headerTable));
//		Box bb2 = Box.createHorizontalBox();
//		JButton add2 = new JButton("Add");
//		add2.addActionListener(e -> {
//			int index = headerModel.getRowCount();
//			headerModel.addRow(new String[] { "", "" });
//			headerModel.fireTableDataChanged();
//			headerTable.setRowSelectionInterval(index, index);
//		});
//		JButton del2 = new JButton("Delete");
//		bb2.add(Box.createHorizontalGlue());
//		bb2.add(add2);
//		bb2.add(Box.createHorizontalStrut(Utility.toPixel(5)));
//		bb2.add(del2);
//		panelHeaders.add(bb2, BorderLayout.SOUTH);
		headerTable = new KeyValueTable(false);
		requestTabs.addCustomTab("Headers", headerTable);

		JPanel panelBody = new JPanel(
				new BorderLayout(Utility.toPixel(5), Utility.toPixel(5)));
		createBodyPanel(panelBody);
		requestTabs.addCustomTab("Request body", panelBody);

		b3.add(requestTabs);

		b3.add(Box.createVerticalStrut(Utility.toPixel(5)));

		JPanel b30 = new JPanel(
				new BorderLayout(Utility.toPixel(5), Utility.toPixel(5)));
		b30.setAlignmentX(Box.LEFT_ALIGNMENT);

		txtFile = new JTextField(30);
		txtFile.setEnabled(false);

		chkFile = new JCheckBox("Download to file");
		chkFile.addActionListener(e -> {
			txtFile.setEnabled(chkFile.isSelected());
		});

		chkIgnoreSslError = new JCheckBox("Ignore SSL/Certificate errors");

		b30.add(chkFile, BorderLayout.WEST);
		b30.add(txtFile);
		b30.add(chkIgnoreSslError, BorderLayout.EAST);

		cmbOutputOptions = new JComboBox<String>(new String[] {
				"Show header and body", "Only body", "Only headers", "" });
		b3.add(b30);

//		cmbOutputOptions = new JComboBox<String>(new String[] {
//				"Show header and body", "Only body", "Only headers", "" });
		// b3.add(b40);

		Box b4 = Box.createVerticalBox();
		b4.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		JLabel titleResp = new JLabel("Response");

		titleResp.setAlignmentX(Box.LEFT_ALIGNMENT);
		b4.add(titleResp);
		titleResp
				.setFont(new Font(Font.DIALOG, Font.BOLD, Utility.toPixel(15)));

		b4.add(Box.createVerticalStrut(Utility.toPixel(5)));
		// b4.add(cmbOutputOptions);

		txtResult = new JTextArea();
		txtResult.setEditable(false);
		JScrollPane jsp2 = new JScrollPane(txtResult);
		jsp2.setBorder(UIManager.getBorder("Component.border"));
		jsp2.setAlignmentX(Box.LEFT_ALIGNMENT);
		b4.add(jsp2);
		chkVerboseOutput = new JCheckBox("Show verbose output");
		chkVerboseOutput.setAlignmentX(Box.LEFT_ALIGNMENT);
		// b4.add(chkVerboseOutput);

		Box bH = Box.createHorizontalBox();
		radNoHeader = new JRadioButton("Dont show header infomation");
		radVerbose = new JRadioButton("Show verbose infomation");
		radResponseHeader = new JRadioButton("Show response header infomation");

		ButtonGroup bg = new ButtonGroup();
		bg.add(radVerbose);
		bg.add(radNoHeader);
		bg.add(radResponseHeader);

		bH.add(radNoHeader);
		bH.add(radVerbose);
		bH.add(radResponseHeader);
		bH.setAlignmentX(Box.LEFT_ALIGNMENT);

		radResponseHeader.setSelected(true);

		b4.add(bH);

//		txtCommand = new JTextArea();
//		JScrollPane jsp3 = new JScrollPane(txtCommand);
////		jsp3.setAlignmentX(Box.LEFT_ALIGNMENT);
//		Box b5 = Box.createVerticalBox();
//		JLabel titleCmd = new JLabel("Command");
//		titleCmd.setAlignmentX(Box.LEFT_ALIGNMENT);
//		titleCmd.setFont(new Font(Font.DIALOG, Font.BOLD, Utility.toPixel(15)));
//		b5.add(titleCmd);
//		b5.add(jsp3);

//		splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//		splitPane2.setDividerSize(Utility.toPixel(5));
//		splitPane2.setTopComponent(b5);
//		splitPane2.setDividerLocation(Utility.toPixel(100));
//		splitPane2.setBottomComponent(b4);

		splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane1.setBorder(null);
		splitPane1.setDividerSize(Utility.toPixel(3));
		splitPane1.setTopComponent(b3);
		splitPane1.setBottomComponent(b4);

		splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane3.setBorder(null);
		splitPane3.setDividerSize(Utility.toPixel(3));
		splitPane3.setLeftComponent(b0);
		splitPane3.setRightComponent(splitPane1);

		add(splitPane3);

		JPanel b40 = new JPanel(
				new BorderLayout(Utility.toPixel(5), Utility.toPixel(5)));
		b40.setAlignmentX(Box.LEFT_ALIGNMENT);
		txtCommand = new JTextField(30);
		// txtCommand.setBorder(null);
		b40.add(new JLabel("Curl command"), BorderLayout.WEST);
		b40.add(txtCommand);
		b40.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));

		add(b40, BorderLayout.SOUTH);

		listHistory.addListSelectionListener(e -> {
			if (modelHistory.getSize() > 0) {
				saveRequest();
			}
		});

		if (modelHistory.getSize() > 0) {
			listHistory.setSelectedIndex(0);
		}

		splitPane3.setDividerLocation(Utility.toPixel(200));

	}

	private void saveRequest() {
		if (request != null) {
			updateRequest();
		}
		int index = listHistory.getSelectedIndex();
		setRequest(modelHistory.get(index));
		saveList();
	}

	private Container createProxyPanel() {
		Box b12 = Box.createVerticalBox();
		JLabel lblProxyAuth = new JLabel("Proxy configuration");
		lblProxyAuth
				.setFont(new Font(Font.DIALOG, Font.BOLD, Utility.toPixel(14)));
		JLabel lblProxy = new JLabel("Proxy");
		JLabel lblUser = new JLabel("User name");
		JLabel lblPass = new JLabel("Password");

		txtProxy = new JTextField(30);
		txtProxyUser = new JTextField(30);
		txtProxyPass = new JPasswordField(30);

		lblProxyAuth.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		lblProxy.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		txtProxy.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		lblUser.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		lblPass.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		txtUser.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		txtUser.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		txtPass.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		chkAutoDetectAuthMethodProxy = new JCheckBox(
				"Automatically detect best authentication method");

		b12.add(lblProxyAuth);

		b12.add(lblProxy);
		b12.add(txtProxy);

		b12.add(lblUser);
		b12.add(txtProxyUser);

		b12.add(lblPass);
		b12.add(txtProxyPass);

		b12.add(chkAutoDetectAuthMethodProxy, BorderLayout.SOUTH);

		JPanel floatPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		floatPanel.add(b12);

		return floatPanel;
	}

	private void newRequest() {
		saveList();
		int index = modelHistory.getSize();
		HttpRequest req = new HttpRequest();
		modelHistory.addElement(req);
		listHistory.setSelectedIndex(index);
	}

	/**
	 * 
	 */
	private void saveList() {
		try {
			ArrayList<HttpRequest> list = new ArrayList<>();
			for (int i = 0; i < modelHistory.size(); i++) {
				list.add(modelHistory.get(i));
			}
			store.store(list);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	/**
	 * @param httpRequest
	 */
	private void setRequest(HttpRequest httpRequest) {
		this.request = httpRequest;
		this.txtUrl.setText(httpRequest.getUrl());
		this.cmbProto.getEditor().setItem(httpRequest.getMethod());
		if (httpRequest.getQueryParams() != null
				&& httpRequest.getQueryParams().size() > 0) {
			this.queryTable.getModel().setRowCount(0);
			for (KeyValuePair ent : httpRequest.getQueryParams()) {
				this.queryTable.getModel()
						.addRow(new String[] { ent.getKey(), ent.getValue() });
			}
		}

		if (httpRequest.getHeadres() != null
				&& httpRequest.getHeadres().size() > 0) {
			this.headerTable.getModel().setRowCount(0);
			for (KeyValuePair ent : httpRequest.getHeadres()) {
				this.headerTable.getModel()
						.addRow(new String[] { ent.getKey(), ent.getValue() });
			}
		}

		this.cmbType.setSelectedIndex(httpRequest.getPayloadType());
		this.cmbOutputOptions.getEditor().setItem(httpRequest.getContentType());
	}

	private void updateRequest() {
		this.request.setUrl(txtUrl.getText());
		this.request.setMethod(cmbProto.getSelectedItem() + "");
		this.request.setPayloadType(this.cmbType.getSelectedIndex());
		this.request
				.setContentType(this.cmbOutputOptions.getSelectedItem() + "");
		this.request.setHeadres(
				this.headerTable.getModel().getDataVector().stream().map(e -> {
					return new KeyValuePair((String) e.get(0),
							(String) e.get(1));
				}).collect(Collectors.toList()));
		this.request.setQueryParams(
				this.queryTable.getModel().getDataVector().stream().map(e -> {
					return new KeyValuePair((String) e.get(0),
							(String) e.get(1));
				}).collect(Collectors.toList()));
	}

	private void createBodyPanel(JPanel panel) {
		panel.setLayout(new BorderLayout());
		String[] options = new String[] { "Raw",
				"form-data (multipart/form-data)", "x-www-form-urlencoded",
				"binary" };

		formTable1 = new KeyValueTable(true);
		formTable2 = new KeyValueTable(false);

		txtRawBody = new JTextArea();

		// DefaultListModel<String> fileModel = new DefaultListModel<String>();
		// JList<String> fileList = new JList<String>(fileModel);
		CardLayout c = new CardLayout();

		JPanel pan = new JPanel(c);
		pan.setBorder(
				new EmptyBorder(Utility.toPixel(5), 0, Utility.toPixel(5), 0));
		cmbType = new JComboBox<String>(new String[] { "application/json",
				"text/plain", "application/xml" });
		cmbType.setEditable(true);
		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(cmbType, BorderLayout.NORTH);
		p1.add(txtRawBody);
		pan.add(p1, options[0]);

		pan.add(formTable1, options[1]);

		pan.add(formTable2, options[2]);

		Box b11 = Box.createVerticalBox();
		JLabel lblFile = new JLabel("File to upload");
		lblFile.setAlignmentX(Box.LEFT_ALIGNMENT);
		b11.add(lblFile);
		txtUploadFile = new JTextField(30);
		txtUploadFile.setAlignmentX(Box.LEFT_ALIGNMENT);
		b11.add(txtUploadFile);
		b11.add(Box.createVerticalGlue());

		JPanel pb11 = new JPanel(new FlowLayout(FlowLayout.LEADING));
		pb11.add(b11);

		JPanel p4 = new JPanel(new BorderLayout());
		p4.add(pb11);// new JScrollPane(fileList));
		// Box b3 = Box.createHorizontalBox();
//		JButton add3 = new JButton("Add");
//		JButton del3 = new JButton("Delete");
//		b3.add(Box.createHorizontalGlue());
//		b3.add(add3);
//		b3.add(Box.createHorizontalStrut(Utility.toPixel(5)));
//		b3.add(del3);
//		p4.add(b3, BorderLayout.SOUTH);
		pan.add(p4, options[3]);

		cmbOptions = new JComboBox<String>(options);
		cmbOptions.addActionListener(e -> {
			c.show(pan, cmbOptions.getSelectedItem().toString());
		});

		panel.add(cmbOptions, BorderLayout.NORTH);
		panel.add(pan);
	}

	/**
	 * 
	 */
	private void fireRequest() {

		StringBuilder cmdBuf = new StringBuilder("curl ");
		if (chkIgnoreSslError.isSelected()) {
			cmdBuf.append(" -k");
		}
		if (txtUser.getText().length() > 0) {
			String s = txtUser.getText() + ":"
					+ new String(txtPass.getPassword());
			if (chkAutoDetectAuthMethod.isSelected()) {
				cmdBuf.append(" --anyauth");
			}
			cmdBuf.append(" --user " + s);
		}
		if (txtProxy.getText().length() > 0) {
			String proxy = txtProxy.getText();
			cmdBuf.append(" --proxy " + proxy);
			if (txtProxyUser.getText().length() > 0) {
				if (chkAutoDetectAuthMethodProxy.isSelected()) {
					cmdBuf.append(" --proxy-anyauth");
				}
				String px = txtProxyUser.getText() + ":"
						+ new String(txtProxyPass.getPassword());
				cmdBuf.append(" --proxy-user " + px);
			}
		}
		if (radVerbose.isSelected()) {
			cmdBuf.append(" -v");
		} else if (radResponseHeader.isSelected()) {
			cmdBuf.append(" -i");
		}
		cmdBuf.append(" -X " + cmbProto.getSelectedItem());
		String[][] headers = headerTable.getValues();
		for (int i = 0; i < headers.length; i++) {
			String key = headers[i][0];
			String val = headers[i][1];
			if (key == null || val == null || key.trim().length() < 1
					|| val.trim().length() < 1) {
				continue;
			}
			cmdBuf.append(" -H '" + key.trim() + ": " + val.trim() + "'");
		}

		int index = cmbOptions.getSelectedIndex();
		switch (index) {
		case 0:
			if (txtRawBody.getText().length() > 0) {
				String contentType = cmbType.getSelectedItem() + "";
				cmdBuf.append(" -H 'Content-Type: " + contentType + "'");
				cmdBuf.append(" -d '" + txtRawBody.getText() + "'");
			}
			break;
		case 1:
			StringBuilder sb2 = new StringBuilder();
			String[][] formData1 = formTable1.getValues();
			for (int i = 0; i < formData1.length; i++) {
				String key = formData1[i][0];
				String val = formData1[i][1];
				if (key == null || val == null || key.trim().length() < 1
						|| val.trim().length() < 1) {
					continue;
				}
				sb2.append(" -F '" + key.trim() + "=" + val.trim() + "'");
			}
			if (sb2.length() > 0) {
				String contentType = "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW";
				cmdBuf.append(" -H 'Content-Type: " + contentType + "'");
				cmdBuf.append(sb2);
			}
		case 2:
			StringBuilder sb3 = new StringBuilder();
			String contentType = "Content-Type: application/x-www-form-urlencoded";
			String[][] formData2 = formTable2.getValues();
			for (int i = 0; i < formData2.length; i++) {
				String key = formData2[i][0];
				String val = formData2[i][1];
				if (key == null || val == null || key.trim().length() < 1
						|| val.trim().length() < 1) {
					continue;
				}
				sb3.append(" -d '" + key.trim() + "=" + val.trim() + "'");
			}
			if (sb3.length() > 0) {
				cmdBuf.append(" -H 'Content-Type: " + contentType + "'");
				cmdBuf.append(sb3);
			}
		case 3:
			String text = txtUploadFile.getText();
			if (text.length() > 0) {
				cmdBuf.append(" --data-binary @" + text);
			}
//			String[][] formData3 = formTable2.getValues();
//			for (int i = 0; i < formData3.length; i++) {
//				String path = formData3[i][0];
//				if (path == null || path.trim().length() < 1) {
//					continue;
//				}
//				cmdBuf.append(" -d '" + path.trim() + "'");
//			}
		}

		if (chkFile.isSelected() && txtFile.getText().length() > 0) {
			cmdBuf.append(" -o '" + txtFile + "' ");
		}

		txtCommand.setText(cmdBuf.toString() + " " + txtUrl.getText());
		txtResult.setText("");
		saveRequest();
		new Thread(() -> {
			runCurl();
		}).start();
	}

	public void runCurl() {
		try {
			if (this.wrapper == null || !this.wrapper.isConnected()) {
				this.wrapper = connect();
			}
			ChannelShell shell = this.wrapper.getShellChannel();
			shell.setPty(true);
			InputStream in = shell.getInputStream();
			OutputStream out = shell.getOutputStream();
			shell.connect();

			out.write(String.format("echo ''; %s ; echo '';exit\n",
					txtCommand.getText()).getBytes());
			out.flush();
			while (true) {
				int r = in.read();
				if (r == -1) {
					break;
				}
				txtResult.append("" + ((char) r));
			}
			in.close();
			out.close();
			shell.disconnect();
			System.out.println("shell exit");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.widgets.Widget#reconnect()
	 */
	@Override
	public void reconnect() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.widgets.Widget#close()
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub

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
		return UIManager.getIcon("ServerTools.curlIcon16");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#getTitle()
	 */
	@Override
	public String getTitle() {
		return TextHolder.getString("http.title");
	}

}

class KeyValueTable extends JPanel {
	private DefaultTableModel model;
	private JTable table;
	private JButton add, del, addFile;

	/**
	 * 
	 */
	public KeyValueTable(boolean allowFile) {
		initUI(allowFile);
	}

	private void initUI(boolean allowFile) {
		setLayout(new BorderLayout());
		model = new DefaultTableModel(new String[] { "Name", "Value" }, 0);
		table = new JTable(model);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		add(new JScrollPane(table));
		add = new JButton("Add");
		add.addActionListener(e -> {
			int index = model.getRowCount();
			model.addRow(new String[] { "", "" });
			model.fireTableDataChanged();
			table.setRowSelectionInterval(index, index);
		});
		addFile = new JButton("Add file");
		addFile.addActionListener(e -> {
			int index = model.getRowCount();
			model.addRow(new String[] { "", "@" });
			model.fireTableDataChanged();
			table.setRowSelectionInterval(index, index);
		});
		makeSingleClickEditor(table);
		del = new JButton("Delete");
		del.addActionListener(e -> {
			int index = table.getSelectedRow();
			if (index != -1) {
				model.removeRow(index);
				model.fireTableDataChanged();
			}
		});
		Box bb1 = Box.createHorizontalBox();
		bb1.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		bb1.add(Box.createHorizontalGlue());

		bb1.add(add);
		bb1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		if (allowFile) {
			bb1.add(addFile);
			bb1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		}
		bb1.add(del);
		add(bb1, BorderLayout.SOUTH);
	}

	private void makeSingleClickEditor(JTable table) {
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			final DefaultCellEditor defaultEditor = (DefaultCellEditor) table
					.getDefaultEditor(table.getColumnClass(i));
			defaultEditor.setClickCountToStart(1);
		}
	}

	public String[][] getValues() {
		String[][] items = new String[table.getRowCount()][2];
		for (int i = 0; i < table.getRowCount(); i++) {
			items[i][0] = table.getValueAt(i, 0) + "";
			items[i][1] = table.getValueAt(i, 1) + "";
		}
		return items;
	}

	/**
	 * @return the model
	 */
	public DefaultTableModel getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(DefaultTableModel model) {
		this.model = model;
	}

	/**
	 * @return the table
	 */
	public JTable getTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setTable(JTable table) {
		this.table = table;
	}

}

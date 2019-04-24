/**
 * 
 */
package nixexplorer.app.components.keygen;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import nixexplorer.TextHolder;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.app.session.SessionManagerPanel;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public abstract class KeyGeneratorPanel extends JPanel {
	private JTextField txtKeyFile;
	private JButton btnAdd, btnImport, btnDelete, btnGenNewKey, btnLoadFromFile, btnStartCopy;
	private JTextArea txtPubKey;
	protected HostTableModel model;
	protected JTable table;
	protected SessionInfo info;
	private JLabel lblKeyLabel;

	private CardLayout card;
	private JComponent loadingPanel, keyPanel;
	protected AtomicBoolean stopFlag = new AtomicBoolean(false);

	protected String pubKey, pubKeyPath;
	protected JDialog dlg;

	/**
	 * 
	 */
	public KeyGeneratorPanel(SessionInfo info, JDialog dlg) {
		this.info = info;
		this.dlg = dlg;
		dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.dlg.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				stopFlag.set(true);
				cleanup();
				dlg.dispose();
			}
		});
		initUI();
	}

	private void initUI() {
		card = new CardLayout();
		setLayout(card);
		setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5), Utility.toPixel(5), Utility.toPixel(5)));

		loadingPanel = createLoadingPanel();
		keyPanel = createKeyPanel();

		this.add(loadingPanel, loadingPanel.getName());
		this.add(keyPanel, keyPanel.getName());
		card.first(this);
	}

	private JComponent createLoadingPanel() {
		loadingPanel = new JPanel(new BorderLayout());
		loadingPanel.setName("LoadingPanel");
		loadingPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		JLabel lblLoading = new JLabel(TextHolder.getString("keygen.loading"));
		lblLoading.setHorizontalAlignment(JLabel.CENTER);
		lblLoading.setVerticalAlignment(JLabel.CENTER);
		loadingPanel.add(lblLoading);
		return loadingPanel;
	}

	private void updateTexts() {
		lblKeyLabel.setText(
				pubKey == null ? TextHolder.getString("keygen.noKey") : TextHolder.getString("keygen.fileLabel"));
		btnLoadFromFile.setText(pubKey == null ? TextHolder.getString("keygen.loadFromFile")
				: TextHolder.getString("keygen.selectAnother"));
		txtPubKey.setText(pubKey);
		txtKeyFile.setText(pubKeyPath);
	}

	private JComponent createKeyPanel() {
		Box vbox = Box.createVerticalBox();
		vbox.setName("KeyPanel");
		JLabel lblKeyTitle = new JLabel(TextHolder.getString("keygen.pubKeyTitle"));
		lblKeyTitle.setAlignmentX(Box.LEFT_ALIGNMENT);

		lblKeyLabel = new JLabel();

		txtKeyFile = new JTextField(30);
		txtKeyFile.setBorder(null);
		txtKeyFile.setEditable(false);

		txtPubKey = new JTextArea();
		txtPubKey.setLineWrap(true);
		txtPubKey.setEditable(false);

		JScrollPane jsp1 = new JScrollPane(txtPubKey);
		jsp1.setMaximumSize(new Dimension(Integer.MAX_VALUE, Utility.toPixel(100)));
		jsp1.setPreferredSize(new Dimension(jsp1.getPreferredSize().width, Utility.toPixel(100)));
		jsp1.setMinimumSize(new Dimension(jsp1.getPreferredSize().width, Utility.toPixel(100)));
		jsp1.setBorder(UIManager.getBorder("TextField.border"));

		btnLoadFromFile = new JButton();
		btnGenNewKey = new JButton(TextHolder.getString("keygen.genKey"));

		btnLoadFromFile.addActionListener(e -> {
			selectKeyFile();
		});

		btnGenNewKey.addActionListener(e -> {
			promptGenKey();
		});

		JLabel lblCopyLabel = new JLabel(TextHolder.getString("keygen.copyDesc"));
		lblCopyLabel.setAlignmentX(Box.LEFT_ALIGNMENT);

		model = new HostTableModel();
		table = new JTable(model);
		table.setFillsViewportHeight(true);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setShowGrid(false);
		JScrollPane jsp = new JScrollPane(table);
		jsp.setAlignmentX(Box.LEFT_ALIGNMENT);
		jsp.setBorder(new LineBorder(UIManager.getColor("DefaultBorder.color"), Utility.toPixel(1)));

		btnAdd = new JButton(TextHolder.getString("keygen.add"));
		btnImport = new JButton(TextHolder.getString("keygen.import"));
		btnDelete = new JButton(TextHolder.getString("keygen.delete"));

		btnAdd.addActionListener(e -> {
			HostEntry ent = addNew();
			if (ent != null) {
				model.add(ent);
			}
		});

		btnImport.addActionListener(e -> {
			SessionInfo info = new SessionManagerPanel().newSession();
			if (info == null)
				return;
			model.add(new HostEntry(info.getHost(), info.getUser(), info.getPassword(), "Pending"));
		});

		btnDelete.addActionListener(e -> {
			int r[] = table.getSelectedRows();
			List<HostEntry> list = new ArrayList<>();
			if (r != null && r.length > 0) {
				for (Integer index : r) {
					list.add(model.get(index));
				}
			}
			for (HostEntry ent : list) {
				model.remove(ent);
			}
		});

		btnStartCopy = new JButton(TextHolder.getString("keygen.start"));
		btnStartCopy.addActionListener(e -> {
			startKeyCopy();
		});

		Box hb1 = Box.createHorizontalBox();
		hb1.setAlignmentX(Box.LEFT_ALIGNMENT);

		hb1.add(lblKeyLabel);
		hb1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		hb1.add(txtKeyFile);

		Box hb2 = Box.createHorizontalBox();
		hb2.setAlignmentX(Box.LEFT_ALIGNMENT);
		hb2.add(Box.createRigidArea(new Dimension(0, Utility.toPixel(100))));
		hb2.add(jsp1);

		Box hb3 = Box.createHorizontalBox();
		hb3.setAlignmentX(Box.LEFT_ALIGNMENT);
		hb3.add(Box.createHorizontalGlue());
		hb3.add(btnLoadFromFile);
		hb3.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		hb3.add(btnGenNewKey);

		Box hb4 = Box.createHorizontalBox();
		hb4.setAlignmentX(Box.LEFT_ALIGNMENT);
		hb4.add(btnAdd);
		hb4.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		hb4.add(btnImport);
		hb4.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		hb4.add(btnDelete);
		hb4.add(Box.createHorizontalGlue());
		hb4.add(btnStartCopy);

		vbox.add(lblKeyTitle);
		vbox.add(Box.createVerticalStrut(Utility.toPixel(5)));
		vbox.add(hb1);
		vbox.add(Box.createVerticalStrut(Utility.toPixel(5)));
		vbox.add(hb2);
		vbox.add(Box.createVerticalStrut(Utility.toPixel(5)));
		vbox.add(hb3);
		vbox.add(Box.createVerticalStrut(Utility.toPixel(5)));
		vbox.add(lblCopyLabel);
		vbox.add(Box.createVerticalStrut(Utility.toPixel(5)));
		vbox.add(jsp);
		vbox.add(Box.createVerticalStrut(Utility.toPixel(5)));
		vbox.add(hb4);

		return vbox;
	}

//	private void setMax(JComponent c) {
//		c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
//	}

	private void promptGenKey() {
		if (this.pubKey != null) {
			if (JOptionPane.showConfirmDialog(this, TextHolder.getString("keygen.warnoverwrite"),
					TextHolder.getString("keygen.warn"), JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
				return;
			}
		}
		card.first(this);
		revalidate();
		repaint();
		JCheckBox chkGenPassPhrase = new JCheckBox(TextHolder.getString("keygen.promptPassphrase"));
		JPasswordField txtPassPhrase = new JPasswordField(30);
		txtPassPhrase.setEditable(false);
		chkGenPassPhrase.addActionListener(e -> {
			txtPassPhrase.setEditable(chkGenPassPhrase.isSelected());
		});

		if (JOptionPane.showOptionDialog(this,
				new Object[] { chkGenPassPhrase, TextHolder.getString("keygen.passphrase"), txtPassPhrase },
				TextHolder.getString("keygen.passphrase"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, null, null) == JOptionPane.YES_OPTION) {

			Thread t = new Thread(() -> {
				try {
					generateKeys("");
					loadPublicKey();
					SwingUtilities.invokeLater(() -> {
						updateTexts();
						card.last(this);
						revalidate();
						repaint();
					});
				} catch (Exception e) {
					e.printStackTrace();
					if (!stopFlag.get()) {
						JOptionPane.showMessageDialog(this, TextHolder.getString("keygen.error"));
					}
					cleanup();
					dlg.dispose();
				}
			});
			t.start();
		}
	}

	public void loadKeys() {
		Thread t = new Thread(() -> {
			try {
				loadPublicKey();
				SwingUtilities.invokeLater(() -> {
					updateTexts();
					card.last(this);
					revalidate();
					repaint();
				});
			} catch (Exception e) {
				e.printStackTrace();
				if (!stopFlag.get()) {
					JOptionPane.showMessageDialog(this, TextHolder.getString("keygen.error"));
				}
				cleanup();
				dlg.dispose();
			}
		});
		t.start();
	}

	public void startKeyCopy() {
		if (model.size() < 1) {
			JOptionPane.showMessageDialog(this, TextHolder.getString("keygen.emptyList"));
			return;
		}

		card.first(this);
		revalidate();
		repaint();

		Thread t = new Thread(() -> {
			try {
				copyKeys();
				SwingUtilities.invokeLater(() -> {
					updateTexts();
					card.last(this);
					revalidate();
					repaint();
				});
			} catch (Exception e) {
				e.printStackTrace();
				if (!stopFlag.get()) {
					JOptionPane.showMessageDialog(this, TextHolder.getString("keygen.error"));
				}
				cleanup();
				dlg.dispose();
			}
		});
		t.start();
	}

	protected abstract void loadPublicKey() throws Exception;

	protected abstract void generateKeys(String passPhrase) throws Exception;

	protected abstract void cleanup();

	protected abstract void selectKeyFile();

	protected void copyKeys() {
		for (HostEntry ent : model.list()) {
			KeyCopier c = null;
			try {
				c = new KeyCopier(ent.getHost(), ent.getUser(), ent.getPassword(), pubKey, this.stopFlag);
				c.copy();
				ent.setStatus("Done");
			} catch (Exception e) {
				e.printStackTrace();
				ent.setStatus("Failed");
			}
			if (stopFlag.get()) {
				break;
			}
		}
	}

	private HostEntry addNew() {
		JTextField txtHost = new JTextField(30);
		JTextField txtUser = new JTextField(30);
		JPasswordField txtPass = new JPasswordField(30);

		while (JOptionPane.showOptionDialog(this, new Object[] { TextHolder.getString("keygen.hostName"), txtHost,
				TextHolder.getString("keygen.userName"), txtUser, TextHolder.getString("keygen.password"), txtPass },
				TextHolder.getString("keygen.addServer"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				null, null) == JOptionPane.OK_OPTION) {
			if (txtHost.getText().length() < 1) {
				JOptionPane.showMessageDialog(this, TextHolder.getString("keygen.blankHost"));
			} else if (txtUser.getText().length() < 1) {
				JOptionPane.showMessageDialog(this, TextHolder.getString("keygen.blankUser"));
			} else {
				return new HostEntry(txtHost.getText(), txtUser.getText(), new String(txtPass.getPassword()),
						"Pending");
			}
		}

		return null;
	}
}

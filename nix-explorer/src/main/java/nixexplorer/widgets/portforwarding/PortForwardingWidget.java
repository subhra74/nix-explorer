/**
 * 
 */
package nixexplorer.widgets.portforwarding;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;

import nixexplorer.TextHolder;
import nixexplorer.app.components.CustomTabbedPane;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshUtility;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.widgets.Widget;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class PortForwardingWidget extends Widget {
	private CustomTabbedPane tabs;
	private JTable localTable, remoteTable;
	private PortForwardingTableModel localModel, remoteModel;
	private SshWrapper wrapper;
	private static final Object LOCK = new Object();

	/**
	 * @param info
	 * @param args
	 * @param appSession
	 * @param window
	 */
	public PortForwardingWidget(SessionInfo info, String[] args,
			AppSession appSession, Window window) {
		super(info, args, appSession, window);
		tabs = new CustomTabbedPane();
		tabs.addCustomTab(TextHolder.getString("pfapp.local"), createLocalPF());
		tabs.addCustomTab(TextHolder.getString("pfapp.remote"),
				createRemotePF());
//		tabs.addCustomTab(TextHolder.getString("pfapp.dynamic"),
//				createDynamicPF());

		add(tabs);
	}

	private JComponent createLocalPF() {
		JPanel p = new JPanel(
				new BorderLayout(Utility.toPixel(5), Utility.toPixel(5)));
		JLabel lblTitle = new JLabel("Local port forwarding rules");
		localModel = new PortForwardingTableModel();
		localTable = new JTable(localModel);
		PortForwardingRenderer rr = new PortForwardingRenderer();
		localTable.setDefaultRenderer(Object.class, rr);
		localTable.setRowHeight(
				rr.getPreferredSize().height + Utility.toPixel(10));
		localTable.setShowGrid(false);
		localTable.setFillsViewportHeight(true);
		localTable.setIntercellSpacing(new Dimension(0, 0));
		JScrollPane jsp = new JScrollPane(localTable);
		p.add(lblTitle, BorderLayout.NORTH);
		p.add(jsp, BorderLayout.CENTER);
		jsp.setBorder(new LineBorder(UIManager.getColor("DefaultBorder.color"),
				Utility.toPixel(1)));
		Box b1 = Box.createHorizontalBox();
		JButton btnAdd = new JButton("Add rule");
		JButton btnEdit = new JButton("Edit rule");
		JButton btnDel = new JButton("Delete rule");
		JButton btnConn = new JButton("Connect");
		JButton btnDisConn = new JButton("Disconnect");
		b1.add(btnAdd);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b1.add(btnEdit);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b1.add(btnDel);
		b1.add(Box.createHorizontalGlue());
		b1.add(btnConn);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b1.add(btnDisConn);
		p.add(b1, BorderLayout.SOUTH);
		p.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));

		btnAdd.addActionListener(e -> {
			PortForwardingEntry ent = createOrEditEntry(null);
			if (ent != null) {
				localModel.add(ent);
			}
		});

		btnEdit.addActionListener(e -> {
			int r = localTable.getSelectedRow();
			if (r != -1) {
				PortForwardingEntry ent = localModel.get(r);
				if (ent.isConnected()) {
					JOptionPane.showMessageDialog(this,
							"To edit this entry, please disconnect first");
					return;
				}
				if (createOrEditEntry(ent) != null) {
					localModel.refresh();
				}
			}
		});

		btnDel.addActionListener(e -> {
			int r = localTable.getSelectedRow();
			if (r != -1) {
				PortForwardingEntry ent = localModel.get(r);
				if (ent.isConnected()) {
					JOptionPane.showMessageDialog(this,
							"To delete this entry, please disconnect first");
					return;
				}
				localModel.remove(r);
			}
		});

		btnConn.addActionListener(e -> {
			int r = localTable.getSelectedRow();
			if (r != -1) {

				PortForwardingEntry ent = localModel.get(r);
				if (ent.isConnected()) {
					return;
				}
				ent.setConnected(true);

				new Thread(() -> {
					try {
						if (wrapper == null || !wrapper.isConnected()) {
							synchronized (LOCK) {
								this.wrapper = SshUtility.connectWrapper(info,
										widgetClosed);
							}
						}
						this.wrapper.getSession().setPortForwardingL(
								ent.getBindAddress(), ent.getSourcePort(),
								ent.getTarget(), ent.getTargetPort());

					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(this, "Error");
						SwingUtilities.invokeLater(() -> {
							ent.setConnected(false);
						});
					} finally {
						SwingUtilities.invokeLater(() -> {
							localModel.refresh();
						});
					}
				}).start();

			}
		});

		btnDisConn.addActionListener(e -> {
			int r = localTable.getSelectedRow();
			if (r != -1) {
				PortForwardingEntry ent = localModel.get(r);
				if (!ent.isConnected()) {
					return;
				}
				try {
					if (wrapper == null || !wrapper.isConnected()
							|| !ent.isConnected()) {
						return;
					}
					new Thread(() -> {
						try {
							this.wrapper.getSession().delPortForwardingL(
									ent.getBindAddress(), ent.getSourcePort());

						} catch (Exception e1) {
							e1.printStackTrace();
						} finally {
							SwingUtilities.invokeLater(() -> {
								ent.setConnected(false);
								localModel.refresh();
							});
						}
					}).start();

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		return p;
	}

	private JComponent createRemotePF() {
		JPanel p = new JPanel(
				new BorderLayout(Utility.toPixel(5), Utility.toPixel(5)));
		JLabel lblTitle = new JLabel("Remote port forwarding rules");
		remoteModel = new PortForwardingTableModel();
		remoteTable = new JTable(remoteModel);
		PortForwardingRenderer rr = new PortForwardingRenderer();
		remoteTable.setDefaultRenderer(Object.class, rr);
		remoteTable.setRowHeight(
				rr.getPreferredSize().height + Utility.toPixel(10));
		remoteTable.setShowGrid(false);
		remoteTable.setIntercellSpacing(new Dimension(0, 0));
		remoteTable.setFillsViewportHeight(true);
		JScrollPane jsp = new JScrollPane(remoteTable);
		jsp.setBorder(new LineBorder(UIManager.getColor("DefaultBorder.color"),
				Utility.toPixel(1)));
		p.add(lblTitle, BorderLayout.NORTH);
		p.add(jsp, BorderLayout.CENTER);
		Box b1 = Box.createHorizontalBox();
		JButton btnAdd = new JButton("Add rule");
		JButton btnEdit = new JButton("Edit rule");
		JButton btnDel = new JButton("Delete rule");
		JButton btnConn = new JButton("Connect");
		JButton btnDisConn = new JButton("Disconnect");
		b1.add(btnAdd);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b1.add(btnEdit);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b1.add(btnDel);
		b1.add(Box.createHorizontalGlue());
		b1.add(btnConn);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b1.add(btnDisConn);
		p.add(b1, BorderLayout.SOUTH);
		p.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));

		btnAdd.addActionListener(e -> {
			PortForwardingEntry ent = createOrEditEntry(null);
			if (ent != null) {
				remoteModel.add(ent);
			}
		});

		btnEdit.addActionListener(e -> {
			int r = remoteTable.getSelectedRow();
			if (r != -1) {
				PortForwardingEntry ent = remoteModel.get(r);
				if (ent.isConnected()) {
					JOptionPane.showMessageDialog(this,
							"To edit this entry, please disconnect first");
					return;
				}
				if (createOrEditEntry(ent) != null) {
					remoteModel.refresh();
				}
			}
		});

		btnDel.addActionListener(e -> {
			int r = remoteTable.getSelectedRow();
			if (r != -1) {
				PortForwardingEntry ent = remoteModel.get(r);
				if (ent.isConnected()) {
					JOptionPane.showMessageDialog(this,
							"To delete this entry, please disconnect first");
					return;
				}
				remoteModel.remove(r);
			}
		});

		btnConn.addActionListener(e -> {
			int r = remoteTable.getSelectedRow();
			if (r != -1) {

				PortForwardingEntry ent = remoteModel.get(r);
				if (ent.isConnected()) {
					return;
				}
				ent.setConnected(true);

				new Thread(() -> {
					try {
						if (wrapper == null || !wrapper.isConnected()) {
							synchronized (LOCK) {
								this.wrapper = SshUtility.connectWrapper(info,
										widgetClosed);
							}
						}

						this.wrapper.getSession().setPortForwardingR(
								ent.getBindAddress(), ent.getSourcePort(),
								ent.getTarget(), ent.getTargetPort());

//						this.wrapper.getSession().setPortForwardingL(
//								ent.getBindAddress(), ent.getSourcePort(),
//								ent.getTarget(), ent.getTargetPort());

					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(this, "Error");
						SwingUtilities.invokeLater(() -> {
							ent.setConnected(false);
						});
					} finally {
						SwingUtilities.invokeLater(() -> {
							remoteModel.refresh();
						});
					}
				}).start();

			}
		});

		btnDisConn.addActionListener(e -> {
			int r = remoteTable.getSelectedRow();
			if (r != -1) {
				PortForwardingEntry ent = remoteModel.get(r);
				if (!ent.isConnected()) {
					return;
				}
				try {
					if (wrapper == null || !wrapper.isConnected()
							|| !ent.isConnected()) {
						return;
					}
					new Thread(() -> {
						try {
							this.wrapper.getSession().delPortForwardingR(
									ent.getBindAddress(), ent.getSourcePort());

						} catch (Exception e1) {
							e1.printStackTrace();
						} finally {
							SwingUtilities.invokeLater(() -> {
								ent.setConnected(false);
								remoteModel.refresh();
							});
						}
					}).start();

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		return p;
	}

	private PortForwardingEntry createOrEditEntry(PortForwardingEntry e) {
		JTextField txtName = new JTextField(30), txtHost = new JTextField(30),
				txtBindAddr = new JTextField("127.0.0.1");
		JSpinner sp1 = new JSpinner(
				new SpinnerNumberModel(9099, 1, Short.MAX_VALUE, 1));
		JSpinner sp2 = new JSpinner(
				new SpinnerNumberModel(9099, 1, Short.MAX_VALUE, 1));

		if (e != null) {
			txtName.setText(e.getName());
			txtHost.setText(e.getTarget());
			sp1.setValue(e.getSourcePort());
			sp2.setValue(e.getTargetPort());
			txtBindAddr.setText(e.getBindAddress());
		}
		Object[] arr = { "Rule name", txtName, "Source port", sp1,
				"Destination host", txtHost, "Destination port", sp2,
				"Bind address", txtBindAddr };

		while (JOptionPane.showOptionDialog(this, arr, "Add/edit Rule",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				null, null) == JOptionPane.OK_OPTION) {
			String name = txtName.getText();
			String host = txtHost.getText();
			int p1 = (int) sp1.getValue();
			int p2 = (int) sp2.getValue();
			String bindAddress = txtBindAddr.getText();
			if (name.length() < 1) {
				JOptionPane.showMessageDialog(this, "Name can not be empty");
				continue;
			}
			if (host.length() < 1) {
				JOptionPane.showMessageDialog(this, "Host can not be empty");
				continue;
			}
			if (bindAddress.length() < 1) {
				JOptionPane.showMessageDialog(this,
						"Bind address can not be empty");
				continue;
			}
			if (e == null) {
				e = new PortForwardingEntry(name, host, p2, p1, bindAddress);
			} else {
				e.setName(name);
				e.setTarget(host);
				e.setSourcePort(p1);
				e.setTargetPort(p2);
				e.setBindAddress(bindAddress);
			}
			return e;
		}
		return null;
	}

	private JComponent createDynamicPF() {
		JPanel p = new JPanel(new BorderLayout());
		JCheckBox chkDynEnable = new JCheckBox(
				"Enable dynamic port forwarding");
		chkDynEnable.setAlignmentX(Box.LEFT_ALIGNMENT);
		Box b2 = Box.createHorizontalBox();

		JLabel lblPort = new JLabel("Port");
		JSpinner sp = new JSpinner(
				new SpinnerNumberModel(9099, 1, Short.MAX_VALUE, 1));
		Dimension d = new Dimension(sp.getPreferredSize().width,
				sp.getPreferredSize().height);
		sp.setPreferredSize(d);
		sp.setMaximumSize(d);
		b2.add(lblPort);
		b2.add(Box.createRigidArea(new Dimension(Utility.toPixel(5), 0)));
		b2.add(sp);
		b2.setAlignmentX(Box.LEFT_ALIGNMENT);
		JCheckBox chkAllowRemote = new JCheckBox(
				"Accepts requests from remote servers");
		chkAllowRemote.setAlignmentX(Box.LEFT_ALIGNMENT);

		Box b1 = Box.createVerticalBox();
		b1.add(chkDynEnable);
		b1.add(Box.createRigidArea(new Dimension(0, Utility.toPixel(5))));
		b1.add(b2);
		b1.add(Box.createRigidArea(new Dimension(0, Utility.toPixel(5))));
		b1.add(chkAllowRemote);

		// b1.add(Box.createVerticalGlue());

		p.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));

		p.add(b1);
		
		
		
		return p;
	}

	@Override
	public void tabSelected() {
		// TODO Auto-generated method stub

	}

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		return "Port forwarding";
	}

	@Override
	public boolean viewClosing() {
		new Thread(() -> {
			synchronized (LOCK) {
				if (wrapper != null) {
					try {
						wrapper.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		return true;
	}

	@Override
	public void reconnect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}

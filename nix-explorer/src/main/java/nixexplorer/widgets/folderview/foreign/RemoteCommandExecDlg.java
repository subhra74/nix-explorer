package nixexplorer.widgets.folderview.foreign;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.jcraft.jsch.ChannelShell;

import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.widgets.util.Utility;

public class RemoteCommandExecDlg extends JDialog {
	private DefaultListModel<String> consoleModel;
	private JList<String> consoleList;
	private JTextField txtInput;
	private JButton btnSend;
	private SessionInfo info;
	private OutputStream out;
	private String shellPrompt, pattern;
	private String command;
	private String inputFeed;

	public RemoteCommandExecDlg(SessionInfo info, String shellPrompt,
			String pattern, String command, String inputFeed) {
		this.info = info;
		this.shellPrompt = shellPrompt;
		this.pattern = pattern;
		this.command = command;
		this.inputFeed = inputFeed;
		setPreferredSize(
				new Dimension(Utility.toPixel(400), Utility.toPixel(300)));
		consoleModel = new DefaultListModel<>();
		consoleList = new JList<>(consoleModel);
		txtInput = new JTextField(30);
		btnSend = new JButton("Send");
		btnSend.addActionListener(e -> {
			if (out != null) {
				try {
					out.write((txtInput.getText() + "\n").getBytes());
					out.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		add(new JScrollPane(consoleList));
		Box b = Box.createHorizontalBox();
		b.add(txtInput);
		b.add(btnSend);
		add(b, BorderLayout.SOUTH);
		start();
	}

	public void start() {
		consoleModel.addElement("");
		Thread t = new Thread(() -> {
			try {

				try (SshWrapper wrapper = new SshWrapper(info)) {
					wrapper.connect();
					ChannelShell shell = wrapper.getShellChannel();
					shell.setPty(true);
					InputStream in = shell.getInputStream();
					out = shell.getOutputStream();
					shell.connect();
					out.write(
							String.format("PS1=%s\n", shellPrompt).getBytes());

					out.write(String.format("%s ; exit\n", command).getBytes());
					out.flush();
					StringBuilder line = new StringBuilder();
					char ch = 0;
					boolean sent = false;
					while (true) {
						int r = in.read();
						if (r == -1) {
							break;
						}

						//System.out.print((char) r);

						if (r == '\n') {
							line = new StringBuilder();
							SwingUtilities.invokeLater(() -> {
								consoleModel.addElement("");

								int index = consoleModel.size() - 1;
								consoleList.setSelectedIndex(index);
								consoleList.ensureIndexIsVisible(index);
								//System.out.println("Adding blank item: ");
							});
						}

//						else if (r == '\r') {
//							System.out.println("\\r found");
//							SwingUtilities.invokeLater(() -> {
//								System.out.println("Setting item: ");
//								consoleModel.set(consoleModel.getSize() - 1,
//										"");
//							});
//						}

						int ch2 = ch;

						if (r != '\n' && r != '\r') {
							line.append((char) r);
							SwingUtilities.invokeLater(() -> {
//								System.out.println("Setting item: "
//										+ consoleModel.lastElement()
//										+ (char) r);
								if (ch2 == '\r') {
									consoleModel.set(consoleModel.getSize() - 1,
											"");
								} else {
									consoleModel.set(consoleModel.getSize() - 1,
											consoleModel.lastElement()
													+ (char) r);
								}

							});
						}

						final String ln = line.toString();

						if (pattern != null && pattern.length() > 0) {
							if (ln.trim().equals(pattern)) {
								if (inputFeed != null
										&& inputFeed.length() > 0) {
									if (!sent) {
										out.write(
												(inputFeed + "\n").getBytes());
										out.flush();
										sent = true;
									}
								}
							}
						}
						ch = (char) r;
					}
				}
				btnSend.setEnabled(false);
				txtInput.setEnabled(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		t.start();
	}
}

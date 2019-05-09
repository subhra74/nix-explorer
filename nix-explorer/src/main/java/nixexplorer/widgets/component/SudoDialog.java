/**
 * 
 */
package nixexplorer.widgets.component;

import java.awt.Window;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.jcraft.jsch.ChannelExec;

import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class SudoDialog extends JDialog {
	/**
	 * 
	 */
	private SudoDialog(Window w) {
		super(w);
		setModal(true);
		setSize(Utility.toPixel(200), Utility.toPixel(70));
		setLocationRelativeTo(w);
	}

	public static SudoResult executeCommand(Window w, String command,
			SshWrapper wrapper) {
		SudoResult res = new SudoResult(null, -1);

		SudoDialog dlg = new SudoDialog(w);

		new Thread(() -> {

			ChannelExec exec = null;
			try {
				String prompt = UUID.randomUUID().toString();
				exec = wrapper.getExecChannel();
				exec.setCommand("sudo -S -p '' " + command);
				System.out.println("sudo -S -p '' " + command);
				InputStream in = exec.getInputStream();
				OutputStream out = exec.getOutputStream();
				exec.connect();
				StringBuilder sb = new StringBuilder();
				StringBuilder output = new StringBuilder();
				boolean askPassword = false;
				while (true) {
					if (sb.toString().equals(prompt)) {
						askPassword = true;
						sb=new StringBuilder();
					}
					if (in.available() == 0) {
						System.out.println("Prompt ready");
						String password = JOptionPane
								.showInputDialog("Password");
						if (password == null) {
							return;
						}
						sb = new StringBuilder();
						out.write((password + "\n").getBytes());
						out.flush();
					}

					int x = in.read();
					if (x == '\n') {
						output.append(sb.toString());
						sb = new StringBuilder();
					}

					if (x != -1 && x != '\n')
						sb.append((char) x);

					if (exec.isClosed()) {
						System.out.println("Channel closed");
						return;
					}
					if (exec.getExitStatus() != -1 && x == -1) {
						break;
					}
				}
				res.setExitCode(exec.getExitStatus());
				res.setOutput(output.toString());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (exec != null) {
					exec.disconnect();
				}
				System.out.println("done");
				SwingUtilities.invokeLater(() -> {
					dlg.setVisible(false);
				});
			}
		}).start();

		dlg.setVisible(true);

		return res;
	}

	public static class SudoResult {
		private String output;
		private int exitCode;

		/**
		 * @param output
		 * @param exitCode
		 */
		public SudoResult(String output, int exitCode) {
			super();
			this.output = output;
			this.exitCode = exitCode;
		}

		/**
		 * @return the output
		 */
		public String getOutput() {
			return output;
		}

		/**
		 * @param output the output to set
		 */
		public void setOutput(String output) {
			this.output = output;
		}

		/**
		 * @return the exitCode
		 */
		public int getExitCode() {
			return exitCode;
		}

		/**
		 * @param exitCode the exitCode to set
		 */
		public void setExitCode(int exitCode) {
			this.exitCode = exitCode;
		}
	}
}

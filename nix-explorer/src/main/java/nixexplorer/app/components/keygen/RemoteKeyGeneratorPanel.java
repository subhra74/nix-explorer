package nixexplorer.app.components.keygen;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.swing.JDialog;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshFileSystemWrapper;
import nixexplorer.core.ssh.SshUtility;
import nixexplorer.widgets.folderview.FileSelectionDialog;

public class RemoteKeyGeneratorPanel extends KeyGeneratorPanel {
	private SshFileSystemWrapper ssh;

	public RemoteKeyGeneratorPanel(SessionInfo info, JDialog dlg) {
		super(info, dlg);
		ssh = new SshFileSystemWrapper(info);
	}

	@Override
	protected void loadPublicKey() throws Exception {

		if (stopFlag.get()) {
			throw new Exception("Operation cancelled");
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			String path = this.pubKeyPath == null ? ssh.getHome() + "/.ssh/id_rsa.pub" : pubKeyPath;
			ssh.getSftp().get(path, out);
			this.pubKey = new String(out.toByteArray(), "utf-8");
			this.pubKeyPath = path;
		} catch (SftpException e) {
			if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
				return;
			}
			throw new Exception(e);
		}
	}

	@Override
	protected void generateKeys(String passPhrase) throws Exception {

		if (stopFlag.get()) {
			throw new Exception("Operation cancelled");
		}

		String path1 = ssh.getHome() + "/.ssh/id_rsa";
		String path = path1 + ".pub";

		String cmd = "ssh-keygen -q -N \"" + passPhrase + "\" -f \"" + path1 + "\"";

		try {
			ssh.getSftp().rm(path1);
		} catch (SftpException e) {
			if (e.id != ChannelSftp.SSH_FX_NO_SUCH_FILE) {
				throw new Exception(e);
			}
		}

		try {
			ssh.getSftp().rm(path);
		} catch (SftpException e) {
			if (e.id != ChannelSftp.SSH_FX_NO_SUCH_FILE) {
				throw new Exception(e);
			}
		}

		if (SshUtility.executeCommand(ssh.getWrapper(), cmd, new ArrayList<String>()) != 0) {
			throw new Exception();
		}
	}

	@Override
	protected void cleanup() {
		System.out.println("Cleaning up...");
		stopFlag.set(true);
		Thread t = new Thread(() -> {
			if (this.ssh != null) {
				System.out.println("Disconnecting wrapper");
				try {
					this.ssh.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}

	@Override
	protected void selectKeyFile() {
		FileSelectionDialog dlg = new FileSelectionDialog(null, ssh, super.dlg, false);
		dlg.setLocationRelativeTo(super.dlg);
		if (dlg.showDialog() == FileSelectionDialog.DialogResult.APPROVE) {
			this.pubKeyPath = dlg.getSelectedPath();
			loadKeys();
		}
	}

}

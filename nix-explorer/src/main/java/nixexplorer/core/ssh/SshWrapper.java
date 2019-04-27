package nixexplorer.core.ssh;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JOptionPane;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import nixexplorer.App;
import nixexplorer.app.MainAppFrame;
import nixexplorer.app.components.UserInfoUI;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.desktop.ResourceManager;

/**
 * This class executes a shell script on remote host using ssh. It is possible
 * to provide user variables. If the script has a variable section, then all the
 * variables will be inserted there before executing the script. After
 * execution, the script's output is parsed and any line containing KEY=VALUE
 * pair will be extracted as variables. Return value from the script will also
 * be recorded.
 * 
 * The script will be searched for any characters which are interpreted by shell
 * and will be escaped.
 * 
 * Example of script format: <br>
 * #!/bin/bash<br>
 * #DISK USAGE - LINUX<br>
 * <br>
 * #VARIABLES <br>
 * RESULT=`df / --output=pcent|tail -n 1|tr -d ' %'` <br>
 * echo "RESULT=$RESULT" <br>
 * <br>
 * 
 * @author subhro
 *
 */
public class SshWrapper implements Closeable {
	private JSch jsch;
	private Session session;
	private SessionInfo info;

	/**
	 * Constructor
	 * 
	 * @param info The detail of the remote host where script will be executed
	 */
	public SshWrapper(SessionInfo info) {
		System.out.println("New wrapper session");
		this.info = info;
	}

	public boolean isConnected() {
		if (session == null)
			return false;
		return session.isConnected();
	}

	public int connectWithReturn() {
		try {
			connect();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				disconnect();
			} catch (Exception e2) {
			}
			return 1;
		}
	}

	@Override
	public String toString() {
		return info.getName();
	}

	public void connect() throws Exception {
		// ResourceManager.register(info.getContainterId(), this);
		jsch = new JSch();
		try {
			jsch.setKnownHosts(new File(App.getConfig("app.dir"), "known_hosts")
					.getAbsolutePath());
		} catch (Exception e) {

		}

		// JSch.setLogger(new JSCHLogger());
//		JSch.setConfig("PreferredAuthentications",
//				"password,keyboard-interactive");
		JSch.setConfig("MaxAuthTries", "5");

		if (info.getPrivateKeyFile() != null
				&& info.getPrivateKeyFile().length() > 0) {
			jsch.addIdentity(info.getPrivateKeyFile());
		}

		String user = info.getUser();

		if (info.getUser() == null || info.getUser().length() < 1) {
			user = JOptionPane.showInputDialog(null, "Username");
			if (user != null && user.length() > 0) {
				info.setUser(user);
			} else {
				throw new Exception("User cancelled operation");
			}
		}

		session = jsch.getSession(info.getUser(), info.getHost(),
				info.getPort());

		session.setUserInfo(
				new UserInfoUI(info, MainAppFrame.getSharedInstance()));

		session.setPassword(info.getPassword());
		// session.setConfig("StrictHostKeyChecking", "no");
		session.setConfig("PreferredAuthentications",
				"publickey,keyboard-interactive,password");

		session.setTimeout(60000);
		session.connect();

		System.out.println("Client version: " + session.getClientVersion());
		System.out.println("Server host: " + session.getHost());
		System.out.println("Server version: " + session.getServerVersion());
		System.out.println(
				"Hostkey: " + session.getHostKey().getFingerPrint(jsch));
	}

	public void disconnect() {
		try {
			session.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ResourceManager.unregister(info.getContainterId(), this);
	}

	public ChannelSftp getSftpChannel() throws Exception {
		ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
		sftp.connect();
		return sftp;
	}

	public ChannelShell getShellChannel() throws Exception {
		ChannelShell shell = (ChannelShell) session.openChannel("shell");
		return shell;
	}

	public ChannelExec getExecChannel() throws Exception {
		ChannelExec exec = (ChannelExec) session.openChannel("exec");
		return exec;
	}

	public SessionInfo getInfo() {
		return info;
	}

	public void setInfo(SessionInfo info) {
		this.info = info;
	}

	class MyUserInfo implements UserInfo, UIKeyboardInteractive {

		@Override
		public String[] promptKeyboardInteractive(String destination,
				String name, String instruction, String[] prompt,
				boolean[] echo) {
			for (String s : Arrays.asList(prompt)) {
				System.out.println(s);
			}
			System.out.println(destination + " " + name + instruction);
			return new String[] { info.getPassword() };
		}

		@Override
		public void showMessage(String message) {
			System.out.println("showMessage: " + message);
		}

		@Override
		public boolean promptYesNo(String message) {
			System.out.println("promptYesNo: " + message);
			return true;
		}

		@Override
		public boolean promptPassword(String message) {
			System.out.println("promptPassword: " + message);
			return true;
		}

		@Override
		public boolean promptPassphrase(String message) {
			System.out.println("promptPassphrase: " + message);
			return false;
		}

		@Override
		public String getPassword() {
			System.out.println("getPassword");
			return info.getPassword();
		}

		@Override
		public String getPassphrase() {
			System.out.println("getPassphrase");
			return null;
		}
	}

	class JSCHLogger implements com.jcraft.jsch.Logger {

		@Override
		public boolean isEnabled(int level) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void log(int level, String message) {
			// TODO Auto-generated method stub
			System.out.println(message);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			System.out.println("Wrapper closing");
			session.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Session getSession() {
		return session;
	}
}

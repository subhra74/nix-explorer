package nixexplorer.core.ssh;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;

import javax.swing.JOptionPane;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;

import nixexplorer.app.session.SessionInfo;

public class SshUtility {

	public static final int executeCommand(SshWrapper wrapper, String command, boolean compressed,
			List<String> output) {
		System.out.println("Executing: " + command);
		ChannelExec exec = null;
		try {
			exec = wrapper.getExecChannel();
			InputStream in = exec.getInputStream();
			exec.setCommand(command);
			exec.connect();

			InputStream inStream = compressed ? new GZIPInputStream(in) : in;
			StringBuilder sb = new StringBuilder();
			while (true) {
				int x = inStream.read();

				if (x == '\n') {
					output.add(sb.toString());
					sb = new StringBuilder();
				}
				if (x != -1 && x != '\n')
					sb.append((char) x);

				if (exec.getExitStatus() != -1 && x == -1) {
					break;
				}
			}

			in.close();
			int ret = exec.getExitStatus();
			System.err.println("Exit code: " + ret);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			if (exec != null) {
				exec.disconnect();
			}
		}
	}

	public static final int executeCommand(SshWrapper wrapper, String command, List<String> output) {
		return executeCommand(wrapper, command, false, output);
	}

	public static final SshWrapper connectWrapper(SessionInfo info, AtomicBoolean stopFlag) throws Exception {
		return connecReal(info, stopFlag, 0).wrapper;
	}

	private static SshContext connecReal(SessionInfo info, AtomicBoolean stopFlag, int type) throws Exception {
		SshWrapper wrapper = new SshWrapper(info);
		while (!stopFlag.get()) {
			try {
				wrapper.connect();
				if (stopFlag.get()) {
					break;
				}
				switch (type) {
				case 0: {
					SshContext res = new SshContext();
					res.wrapper = wrapper;
					return res;
				}
				case 1: {
					ChannelSftp sftp = wrapper.getSftpChannel();
					SftpContext res = new SftpContext();
					res.sftp = sftp;
					res.wrapper = wrapper;
					return res;
				}
				case 2: {
					ChannelShell shell = wrapper.getShellChannel();
					ShellContext res = new ShellContext();
					res.shell = shell;
					res.wrapper = wrapper;
					return res;
				}
				case 3: {
					ChannelExec exec = wrapper.getExecChannel();
					ExecContext res = new ExecContext();
					res.exec = exec;
					res.wrapper = wrapper;
					return res;
				}
				default:
					throw new Exception("Invalid type");
				}

			} catch (Exception e) {
				e.printStackTrace();
				try {
					wrapper.close();
				} catch (Exception ex) {
				}
				System.out.println("Stop flag check: " + stopFlag.get());
				if (stopFlag.get()) {
					break;
				}
				if (JOptionPane.showConfirmDialog(null,
						"Unable to connect to server. Retry?") != JOptionPane.YES_OPTION) {
					break;
				}
			}
		}
		throw new Exception("User cancelled the operation");
	}

	public static final SftpContext connectSftp(SessionInfo info, AtomicBoolean stopFlag) throws Exception {
		return (SftpContext) connecReal(info, stopFlag, 1);
	}

	public static final ShellContext connectShell(SessionInfo info, AtomicBoolean stopFlag) throws Exception {
		return (ShellContext) connecReal(info, stopFlag, 2);
	}

	public static final ExecContext connectExec(SessionInfo info, AtomicBoolean stopFlag) throws Exception {
		return (ExecContext) connecReal(info, stopFlag, 3);
	}

	public static class SshContext {
		protected SshWrapper wrapper;

		/**
		 * @return the wrapper
		 */
		public SshWrapper getWrapper() {
			return wrapper;
		}

	}

	public static class ShellContext extends SshContext {
		private ChannelShell shell;

		/**
		 * @return the shell
		 */
		public ChannelShell getShell() {
			return shell;
		}
	}

	public static class ExecContext extends SshContext {
		private ChannelExec exec;

		/**
		 * @return the exec
		 */
		public ChannelExec getExec() {
			return exec;
		}
	}

	public static class SftpContext extends SshContext {
		private ChannelSftp sftp;

		/**
		 * @return the sftp
		 */
		public ChannelSftp getSftp() {
			return sftp;
		}
	}
}

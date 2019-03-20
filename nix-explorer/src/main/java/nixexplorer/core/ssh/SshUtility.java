package nixexplorer.core.ssh;

import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.jcraft.jsch.ChannelExec;

public class SshUtility {
	public static final int executeCommand(SshWrapper wrapper, String command,
			boolean compressed, List<String> output) {
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

	public static final int executeCommand(SshWrapper wrapper, String command,
			List<String> output) {
		return executeCommand(wrapper, command, false, output);
	}
}

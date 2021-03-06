package nixexplorer.widgets.folderview;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.ChannelExec;

import nixexplorer.core.FileInfo;
import nixexplorer.core.ssh.SshUtility;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.widgets.dnd.TransferFileInfo;

public class ShellActions {
	public static void copy(String src, String dst, SshWrapper wrapper)
			throws Exception {
		System.out.println("Copy start");
		StringBuilder cmd = new StringBuilder();
		cmd.append("cp -r -f \"" + src + "\" \"" + dst + "\"");
		List<String> list = new ArrayList<>();
		if (SshUtility.executeCommand(wrapper, cmd.toString(), list) != 0) {
			throw new FileNotFoundException("Return code not zero");
		}
	}

	public static void delete(List<FileInfo> files, SshWrapper wrapper)
			throws Exception {
		ChannelExec exec = null;
		try {
			exec = wrapper.getExecChannel();
			StringBuilder sb = new StringBuilder();

			for (FileInfo file : files) {
				sb.append("\"" + file.getPath() + "\" ");
			}

			System.out.println("Delete command1: rm -rf " + sb);

			exec.setCommand("rm -rf " + sb);
			exec.connect();
			BufferedReader r = new BufferedReader(
					new InputStreamReader(exec.getInputStream()));
			while (true) {
				String s = r.readLine();
				if (s == null && exec.getExitStatus() >= 0) {
					break;
				}
			}
			r.close();
			int exit = exec.getExitStatus();
			System.out.println("exit: " + exit);
//			while (exit < 0) {
//				System.out.println("operation not finished, waiting 1 sec");
//				Thread.sleep(1000);
//				if (exit > 0) {
//					break;
//				}
//				exit = exec.getExitStatus();
//				System.out.println("exit: " + exit);
//			}
			if (exit != 0) {
				throw new FileNotFoundException();
			}
		} finally {
			try {
				exec.disconnect();
			} catch (Exception e2) {
			}
		}
	}
}

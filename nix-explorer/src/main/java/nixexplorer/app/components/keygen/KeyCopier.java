package nixexplorer.app.components.keygen;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jcraft.jsch.ChannelSftp;

import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshUtility;
import nixexplorer.core.ssh.SshUtility.SftpContext;

public class KeyCopier {

	private String host, user, password, key;
	private AtomicBoolean stopFlag = new AtomicBoolean(false);

	public KeyCopier(String host, String user, String password, String key,
			AtomicBoolean stopFlag) {
		super();
		this.host = host;
		this.user = user;
		this.password = password;
		this.key = key;
		this.stopFlag = stopFlag;
	}

	public void copy() throws Exception {
		SftpContext ctx = null;
		try {
			SessionInfo info = new SessionInfo();
			info.setHost(host);
			info.setPassword(password);
			info.setUser(user);
			ctx = SshUtility.connectSftp(info, stopFlag);
			if (stopFlag.get()) {
				throw new Exception("Operation cancelled");
			}
			InputStream in = new ByteArrayInputStream(key.getBytes("utf-8"));
			ctx.getSftp().put(in,
					ctx.getSftp().getHome() + "/.ssh/authorized_keys", null,
					ChannelSftp.APPEND);
			if (stopFlag.get()) {
				throw new Exception("Operation cancelled");
			}
		} finally {
			if (ctx != null) {
				ctx.getSftp().disconnect();
				ctx.getWrapper().disconnect();
			}
		}
	}

}

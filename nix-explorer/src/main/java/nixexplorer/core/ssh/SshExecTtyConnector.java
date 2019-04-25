package nixexplorer.core.ssh;

import java.awt.Dimension;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jediterm.terminal.Questioner;

import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshUtility.ExecContext;

public class SshExecTtyConnector implements DisposableTtyConnector {
	private InputStreamReader myInputStreamReader;
	private InputStream myInputStream = null;
	private OutputStream myOutputStream = null;
	private AtomicBoolean isInitiated = new AtomicBoolean(false);
	private SessionInfo info;
	private SshWrapper wr;
	private AtomicBoolean isCancelled = new AtomicBoolean(false);
	private AtomicBoolean stopFlag = new AtomicBoolean(false);
	private ChannelExec channel;
	private String command;
	private Dimension myPendingTermSize;
	private Dimension myPendingPixelSize;
	private CharArrayWriter buffer;
	private boolean alreadyConnected = false;

	public SshExecTtyConnector(SessionInfo info, String command,
			boolean captureOutput) {
		this.info = info;
		this.command = command;
		if (captureOutput) {
			buffer = new CharArrayWriter();
		}
	}

	public SshExecTtyConnector(SshWrapper wrapper, String command,
			boolean captureOutput) {
		this.info = wrapper.getInfo();
		this.wr = wrapper;
		this.command = command;
		this.alreadyConnected = true;
		if (captureOutput) {
			buffer = new CharArrayWriter();
		}
	}

	@Override
	public boolean init(Questioner q) {
		System.out.println("Connecting exec tty connector");
		try {
//			wr = new SshWrapper(info);
//			while (!stopFlag.get()) {
//				try {
//					wr.connect();
//					this.channel = wr.getExecChannel();
//					this.channel.setCommand(command);
//					break;
//				} catch (Exception e) {
//					e.printStackTrace();
//					if (!stopFlag.get()) {
//						if (JOptionPane.showConfirmDialog(null,
//								"Unable to connect to server. Retry?") != JOptionPane.YES_OPTION) {
//							throw new Exception("User cancelled the operation");
//						}
//					}
//				}
//			}
//
//			if (!wr.isConnected()) {
//				throw new IOException("Unable to connect");
//			}
			if (!alreadyConnected) {
				ExecContext exec = SshUtility.connectExec(info, stopFlag);
				wr = exec.wrapper;
				channel = exec.getExec();
			} else {
				System.out.println("already established session");
				channel = wr.getExecChannel();
			}

			String lang = System.getenv().get("LANG");
			channel.setEnv("LANG", lang != null ? lang : "en_US.UTF-8");
			channel.setPtyType("xterm");
			channel.setPty(true);
			System.out.println("Setting command for execution: " + command);
			channel.setCommand(command);
//			PipedOutputStream pout1 = new PipedOutputStream();
//			PipedInputStream pin1 = new PipedInputStream(pout1);
//			channel.setOutputStream(pout1);
//
//			PipedOutputStream pout2 = new PipedOutputStream();
//			PipedInputStream pin2 = new PipedInputStream(pout2);
//			channel.setInputStream(pin2);

			myInputStream = channel.getInputStream();
			myOutputStream = channel.getOutputStream();
			myInputStreamReader = new InputStreamReader(myInputStream, "utf-8");
			channel.connect();
			System.out.println("Initiated");

			// resize(termSize, pixelSize);
			isInitiated.set(true);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			isInitiated.set(false);
			isCancelled.set(true);
			return false;
		}
	}

	@Override
	public void close() {
		System.out.println("Closed has been called");
		stopFlag.set(true);

		try {
			if (channel != null) {
				System.out.println("Channel exit: " + channel.getExitStatus());
			}
			System.out.println("Terminal wrapper disconnecting");
			if (alreadyConnected) {
				System.out.println("Not closing as not owner");
				return;
			}
			wr.disconnect();
		} catch (Exception e) {
		}
	}

	@Override
	public void resize(Dimension termSize, Dimension pixelSize) {
		myPendingTermSize = termSize;
		myPendingPixelSize = pixelSize;
		if (channel != null) {
			resizeImmediately();
		}

//		if (channel == null) {
//			return;
//		}
//		System.out.println("Terminal resized");
//		channel.setPtySize(termSize.width, termSize.height, pixelSize.width, pixelSize.height);
	}

	@Override
	public String getName() {
		return "Remote";
	}

	@Override
	public int read(char[] buf, int offset, int length) throws IOException {
		int r = myInputStreamReader.read(buf, offset, length);
		if (r == -1) {
			while (channel.isConnected() && (!channel.isClosed())) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (buffer != null) {
				buffer.write(buf, offset, r);
			}
		}
		return r;
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		myOutputStream.write(bytes);
		myOutputStream.flush();
	}

	@Override
	public boolean isConnected() {
		System.out.println("Is connected?");
		if (channel != null && channel.isConnected() && isInitiated.get()) {
			return true;
		}
		return false;
	}

	@Override
	public void write(String string) throws IOException {
		write(string.getBytes("utf-8"));
	}

	@Override
	public int waitFor() throws InterruptedException {
		System.out.println("Start waiting...");
		while (!isInitiated.get() || isRunning(channel)) {
			System.out.println("waiting");
			Thread.sleep(100); // TODO: remove busy wait
		}
		System.out.println("waiting exit");
		return channel.getExitStatus();
	}

	public boolean isRunning(Channel channel) {
		System.out.println("Is running?");
		return channel != null && channel.getExitStatus() < 0
				&& channel.isConnected();
	}

	public boolean isBusy() {
		System.out.println("Is busy?");
		return channel.getExitStatus() < 0 && channel.isConnected();
	}

	public boolean isCancelled() {
		return isCancelled.get();
	}

	public void stop() {
		stopFlag.set(true);
		close();
	}

	public int getExitStatus() {
		if (channel != null) {
			return channel.getExitStatus();
		}
		return -2;
	}

	private void resizeImmediately() {
		if (myPendingTermSize != null && myPendingPixelSize != null) {
			setPtySize(channel, myPendingTermSize.width,
					myPendingTermSize.height, myPendingPixelSize.width,
					myPendingPixelSize.height);
			myPendingTermSize = null;
			myPendingPixelSize = null;
		}
	}

	private void setPtySize(ChannelExec channel, int col, int row, int wp,
			int hp) {
		if (channel != null && channel.isConnected()) {
			System.out.println("Exec pty resized");
			channel.setPtySize(col, row, wp, hp);
		}
	}

	@Override
	public boolean isInitialized() {
		return isInitiated.get();
	}

	public char[] getOutput() {
		if (buffer == null) {
			return new char[] {};
		}
		return this.buffer.toCharArray();
	}
}

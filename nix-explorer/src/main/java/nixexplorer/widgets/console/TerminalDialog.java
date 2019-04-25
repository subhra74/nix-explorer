/**
 * 
 */
package nixexplorer.widgets.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import nixexplorer.Constants;
import nixexplorer.app.components.DisposableView;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshExecTtyConnector;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class TerminalDialog extends JDialog implements DisposableView {
	/**
	 * 
	 */
	private TabbedConsoleWidget terminal;
	protected AtomicBoolean widgetClosed = new AtomicBoolean(Boolean.FALSE);
	private Thread t;
	private JLabel lblResult;

	public TerminalDialog(SessionInfo info, String[] args,
			AppSession appSession, Window window, String title,
			boolean autoClose, boolean modal) {
		super(window);
		setTitle(title);
		setModal(modal);
		lblResult = new JLabel();
		lblResult.setForeground(Color.WHITE);
		lblResult.setOpaque(true);
		lblResult.setBorder(new EmptyBorder(Utility.toPixel(10),
				Utility.toPixel(10), Utility.toPixel(10), Utility.toPixel(10)));
		lblResult.setFont(Utility.getFont(Constants.NORMAL));
		this.terminal = new TabbedConsoleWidget(info, args, appSession, window,
				false);
		this.add(terminal);
		this.setSize(Utility.toPixel(640), Utility.toPixel(480));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println(
						"Exit status: " + terminal.getTtyConnector() != null
								? terminal.getTtyConnector().getExitStatus()
								: "");
				System.out.println("Closing terminal dialog");
				terminal.close();
				if (t != null) {
					t.interrupt();
				}
				dispose();
			}
		});

		t = new Thread(() -> {
			while (!t.isInterrupted()) {
				try {
					if (this.terminal.getTtyConnector() != null && this.terminal
							.getTtyConnector().isInitialized()) {
						int res = this.terminal.getTtyConnector()
								.getExitStatus();
						if (!this.terminal.getTtyConnector().isConnected()) {
							System.out.println("Command exit code: " + res);
							SwingUtilities.invokeLater(() -> {
								this.lblResult.setBackground(
										res == 0 ? new Color(1, 99, 26)
												: new Color(249, 40, 1));
								this.lblResult.setText(res == 0
										? "Command completed successfully"
										: "Command completed with error");
								this.add(this.lblResult, BorderLayout.NORTH);
								this.revalidate();
								this.repaint();
								if (autoClose && res == 0) {
									terminal.close();
									this.dispose();
								}
							});
							return;
						}
					}
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.DisposableView#viewClosing()
	 */
	@Override
	public boolean viewClosing() {
		terminal.close();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.DisposableView#viewClosed()
	 */
	@Override
	public void viewClosed() {

	}

	@Override
	public boolean getWidgetClosed() {
		return widgetClosed.get();
	}

	@Override
	public void setWidgetClosed(boolean widgetClosed) {
		this.widgetClosed.set(widgetClosed);
	}

	@Override
	public boolean closeView() {
		dispose();
		return true;
	}

	public char[] getOutput() {
		if (this.terminal != null && this.terminal.getTtyConnector() != null) {
			return ((SshExecTtyConnector) (this.terminal.getTtyConnector()))
					.getOutput();
		}
		return new char[0];
	}

	public int getExitCode() {
		return this.terminal.getTtyConnector().getExitStatus();
	}
}

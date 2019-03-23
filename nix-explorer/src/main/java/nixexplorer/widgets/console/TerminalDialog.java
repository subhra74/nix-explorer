/**
 * 
 */
package nixexplorer.widgets.console;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JDialog;
import javax.swing.JFrame;

import nixexplorer.app.components.DisposableView;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
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

	public TerminalDialog(SessionInfo info, String[] args,
			AppSession appSession, Window window, String title) {
		super(window);
		setTitle(title);
		this.terminal = new TabbedConsoleWidget(info, args, appSession, window,
				true);
		this.add(terminal);
		this.setSize(Utility.toPixel(640), Utility.toPixel(480));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Closing terminal dialog");
				terminal.close();
				dispose();
			}
		});
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
}

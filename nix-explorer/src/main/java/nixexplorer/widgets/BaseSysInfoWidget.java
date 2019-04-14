/**
 * 
 */
package nixexplorer.widgets;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import nixexplorer.ShellScriptLoader;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshUtility;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class BaseSysInfoWidget extends Widget implements Runnable {
	private JTextArea txt;
	private SshWrapper wrapper;

	/**
	 * @param info
	 * @param args
	 * @param appSession
	 * @param window
	 */
	public BaseSysInfoWidget(SessionInfo info, String[] args,
			AppSession appSession, Window window) {
		super(info, args, appSession, window);
		
		Box b1=Box.createVerticalBox();
		b1.add(Box.createVerticalGlue());
		JLabel l1=new JLabel("Connected to "+info.getName());
		l1.setFont(new Font(Font.DIALOG,Font.BOLD,Utility.toPixel(20)));
		l1.setAlignmentX(Box.CENTER_ALIGNMENT);
		JLabel l2=new JLabel("Please start an application from above");
		l2.setFont(new Font(Font.DIALOG,Font.PLAIN,Utility.toPixel(20)));
		l2.setAlignmentX(Box.CENTER_ALIGNMENT);
		b1.add(l1);
		b1.add(l2);
		b1.add(Box.createVerticalGlue());
		
		add(b1);
		
//		txt = new JTextArea();
//		txt.setFont(new Font(Font.MONOSPACED, Font.PLAIN, Utility.toPixel(14)));
//		txt.setEditable(false);
//		this.add(new JScrollPane(txt));
//		new Thread(this).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#tabSelected()
	 */
	@Override
	public void tabSelected() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#getIcon()
	 */
	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#getTitle()
	 */
	@Override
	public String getTitle() {
		return info.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.DisposableView#viewClosing()
	 */
	@Override
	public boolean viewClosing() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.DisposableView#viewClosed()
	 */
	@Override
	public void viewClosed() {
		try {
			this.wrapper.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.widgets.Widget#reconnect()
	 */
	@Override
	public void reconnect() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.widgets.Widget#close()
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
//		try {
//			setCursor(new Cursor(Cursor.WAIT_CURSOR));
//			this.wrapper = connect();
//			String command = ShellScriptLoader.loadShellScript("sysinfo.sh",
//					"Linux");
//			List<String> list = new ArrayList<String>();
//			int r = SshUtility.executeCommand(wrapper, command, list);
//			if (r == 0) {
//				this.txt.setText(String.join("\n", list));
//			}
//			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}

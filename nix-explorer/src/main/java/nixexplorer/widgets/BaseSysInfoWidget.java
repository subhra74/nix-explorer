/**
 * 
 */
package nixexplorer.widgets;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import nixexplorer.App;
import nixexplorer.ShellScriptLoader;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshUtility;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.drawables.icons.ScaledIcon;
import nixexplorer.widgets.folderview.FolderViewUtility;
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
		
		JPanel panel=new JPanel(new BorderLayout());

		Box b1 = Box.createVerticalBox();

		ToolbarItems filesItem = new ToolbarItems("Files",
				"Browse, create, edit and delete files and directories",
				new ScaledIcon(
						App.class.getResource(
								"/images/" + App.getTheme() + "_files.png"),
						Utility.toPixel(64), Utility.toPixel(64)));
		filesItem.setAlignmentX(Box.LEFT_ALIGNMENT);

		ToolbarItems terminalItem = new ToolbarItems("Terminal",
				"Open terminal and execute command on remote system",
				new ScaledIcon(
						App.class.getResource(
								"/images/" + App.getTheme() + "_terminal.png"),
						Utility.toPixel(64), Utility.toPixel(64)));
		terminalItem.setAlignmentX(Box.LEFT_ALIGNMENT);

		b1.add(filesItem);
		b1.add(terminalItem);
		b1.add(Box.createVerticalGlue());

		JScrollPane jsp = new JScrollPane(b1);
		jsp.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		add(jsp);

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

	static class ToolbarItems extends JPanel {
		private JLabel lblTitle, lblIcon, lblDescBottomLeft;
		static final Border BORDER = new CompoundBorder(
				new MatteBorder(0, Utility.toPixel(0), Utility.toPixel(1),
						Utility.toPixel(0),
						UIManager.getColor("DefaultBorder.color")),
				new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
						Utility.toPixel(5), Utility.toPixel(5)));

		/**
		 * 
		 */
		public ToolbarItems(String title, String description, Icon icon) {
			setPreferredSize(new Dimension(Integer.MAX_VALUE, Utility.toPixel(80)));
			setMaximumSize(new Dimension(Integer.MAX_VALUE, Utility.toPixel(80)));
			setLayout(new BorderLayout());
			JPanel mainPanel = new JPanel(new BorderLayout());
			mainPanel.setOpaque(false);
			mainPanel.setBorder(
					new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
							Utility.toPixel(5), Utility.toPixel(5)));

			lblIcon = new JLabel();
			mainPanel.add(lblIcon, BorderLayout.WEST);

			Box titlePanel = Box.createVerticalBox();// new JPanel(new
														// BorderLayout());
			titlePanel.setOpaque(false);
			Box hBox = Box.createHorizontalBox();

			lblDescBottomLeft = new JLabel();
			lblDescBottomLeft.setFont(
					new Font(Font.DIALOG, Font.PLAIN, Utility.toPixel(12)));
			lblDescBottomLeft.setVerticalAlignment(JLabel.TOP);
			lblDescBottomLeft.setVerticalTextPosition(JLabel.TOP);

			hBox.add(lblDescBottomLeft);
			hBox.add(Box.createHorizontalGlue());

			lblTitle = new JLabel();
			lblTitle.setFont(
					new Font(Font.DIALOG, Font.PLAIN, Utility.toPixel(14)));
			lblTitle.setAlignmentX(Box.LEFT_ALIGNMENT);
			hBox.setAlignmentX(Box.LEFT_ALIGNMENT);
			titlePanel.add(Box.createVerticalGlue());
			titlePanel.add(lblTitle);
			titlePanel.add(hBox, BorderLayout.SOUTH);
			titlePanel.add(Box.createVerticalGlue());
			titlePanel.setBorder(
					new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
							Utility.toPixel(5), Utility.toPixel(5)));
			mainPanel.add(titlePanel);
			add(mainPanel);
			setBorder(BORDER);

			lblIcon.setIcon(icon);
			lblTitle.setText(title);
			lblDescBottomLeft.setText(description);
		}
	}

}

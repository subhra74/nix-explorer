package nixexplorer.widgets.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.jediterm.terminal.RequestOrigin;
import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.emulator.ColorPalette;
import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.TerminalAction;
import com.jediterm.terminal.ui.TerminalActionProvider;
import com.jediterm.terminal.ui.TerminalActionProviderBase;
import com.jediterm.terminal.ui.TerminalPanel;
import com.jediterm.terminal.ui.TerminalWidget;
import com.jediterm.terminal.ui.TerminalWidgetListener;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;

import nixexplorer.PathUtils;
import nixexplorer.TextHolder;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionEventAware;
import nixexplorer.app.settings.AppConfig;
import nixexplorer.app.settings.snippet.SnippetItem;
import nixexplorer.app.settings.ui.ConfigDialog;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshTtyConnector;
import nixexplorer.widgets.Widget;
import nixexplorer.widgets.util.Utility;

public final class TabbedConsoleWidget extends Widget
		implements SessionEventAware {
	private static final long serialVersionUID = 628110766420603243L;
	private JediTermWidget term;
	private Icon icon;
	private boolean embedded = false;
	private DefaultComboBoxModel<SnippetItem> model;
	private JComboBox<SnippetItem> cmbSnippets;
	private SnippetActionProvider snippetProvider;
	private AtomicBoolean stopFlag = new AtomicBoolean(false);
	private SshTtyConnector tty;

	public TabbedConsoleWidget(SessionInfo info, String[] args,
			AppSession appSession, Window window) {

		super(info, args, appSession, window);

		setFocusable(true);
		// setTitle((info == null ? "" : info.getName() + " - ") + "Terminal");
		this.setLayout(new BorderLayout());

		model = new DefaultComboBoxModel<>();
		model.addAll(appSession.getApplicationContext().getConfig()
				.getTerminal().getSnippets());
		cmbSnippets = new JComboBox<>(model);
		cmbSnippets.addActionListener(e -> {
			try {
				SnippetItem item = (SnippetItem) cmbSnippets.getSelectedItem();
				if (item == null) {
					return;
				}
				System.out.println("Insert snippet: " + item.getName() + " "
						+ item.getCommand());
				term.getTerminalStarter().sendString(item.getCommand());
			} finally {
				if (term != null) {
					term.requestFocusInWindow();
				}
			}
		});

		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				System.out.println("Focus lost");
			}

			@Override
			public void focusGained(FocusEvent e) {
				System.out.println("Focus gained");
			}
		});

		JButton btnReconnect = new JButton(
				TextHolder.getString("terminal.reconnect"));
		btnReconnect.addActionListener(e -> {
			reconnect();
		});

		Box hb = Box.createHorizontalBox();
		hb.add(new JLabel(TextHolder.getString("terminal.snippet")));
		hb.add(Box.createHorizontalStrut(Utility.toPixel(10)));
		hb.add(cmbSnippets);
		hb.add(Box.createHorizontalStrut(Utility.toPixel(10)));

		JButton btnManageSnippets = new JButton(
				TextHolder.getString("terminal.manageSnippets"));
		btnManageSnippets.addActionListener(e -> {
			ConfigDialog dlg = new ConfigDialog(getWindow(), appSession);
			dlg.selectPage(1);
			dlg.setLocationRelativeTo(getWindow());
			dlg.setVisible(true);
			if (term != null) {
				term.requestFocusInWindow();
			}
		});
		hb.add(btnManageSnippets);
		hb.add(Box.createHorizontalStrut(Utility.toPixel(10)));
		hb.add(btnReconnect);

		hb.setBorder(new EmptyBorder(Utility.toPixel(10), Utility.toPixel(10),
				Utility.toPixel(10), Utility.toPixel(10)));

		if (!embedded) {
			add(hb, BorderLayout.NORTH);
		}

		icon = UIManager.getIcon("ServerTools.terminalIcon");

		AppConfig config = appSession.getApplicationContext().getConfig();

		DefaultSettingsProvider p = new DefaultSettingsProvider() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.jediterm.terminal.ui.settings.DefaultSettingsProvider#
			 * getTerminalColorPalette()
			 */
			@Override
			public ColorPalette getTerminalColorPalette() {
				return ColorPalette.XTERM_PALETTE;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.jediterm.terminal.ui.settings.DefaultSettingsProvider#
			 * getTerminalFontSize()
			 */
			@Override
			public float getTerminalFontSize() {
				return config.getTerminal().getFontSize();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.jediterm.terminal.ui.settings.DefaultSettingsProvider#
			 * useAntialiasing()
			 */
			@Override
			public boolean useAntialiasing() {
				return true;
			}

//			@Override
//			public boolean copyOnSelect() {
//				return true;
//			}
//
//			@Override
//			public boolean pasteOnMiddleMouseClick() {
//				return true;
//			}

			@Override
			public TextStyle getDefaultStyle() {
				System.out.println("Default style called");
				return new TextStyle(
						TerminalColor.awt(new Color(
								config.getTerminal().getForeGround())),
						TerminalColor.awt(new Color(
								config.getTerminal().getBackGround())));
				// return new TextStyle(foreground, background)
			}

			@Override
			public boolean emulateX11CopyPaste() {
				return config.getTerminal().isX11CopyPaste();
			}

			@Override
			public boolean enableMouseReporting() {
				return true;
			}

			@Override
			public TextStyle getFoundPatternColor() {
				return new TextStyle(
						TerminalColor
								.awt(UIManager.getColor("Terminal.foreground")),
						TerminalColor.awt(UIManager
								.getColor("Terminal.selectionBackground")));
			}

			@Override
			public TextStyle getSelectionColor() {
				return new TextStyle(
						TerminalColor
								.awt(UIManager.getColor("Terminal.foreground")),
						TerminalColor.awt(UIManager
								.getColor("Terminal.selectionBackground")));
			}

//			@Override
//			public Font getTerminalFont() {
//				return UIManager.getFont("Terminal.font");
//			}

			@Override
			public TextStyle getHyperlinkColor() {
				return new TextStyle(
						TerminalColor
								.awt(UIManager.getColor("Terminal.foreground")),
						TerminalColor.awt(
								UIManager.getColor("Terminal.background")));
			}
		};

		term = new CustomJediterm(p);

//		term.addListener(new TerminalWidgetListener() {
//			@Override
//			public void allSessionsClosed(TerminalWidget widget) {
//				System.out.println("session closed");
//				add(hbox, BorderLayout.NORTH);
//				revalidate();
//				repaint();
//			}
//		});

		if (!embedded) {
			snippetProvider = new SnippetActionProvider();
			snippetProvider.setList(appSession.getApplicationContext()
					.getConfig().getTerminal().getSnippets());
			term.setNextProvider(snippetProvider);
		}

		String cmd = null;
//		for (String a : args) {
//			System.out.println("Args: " + a);
//		}
		if (args != null && args.length > 0) {
			String a1 = args[0];
			if ("-o".equals(a1) && args.length == 2) {
				cmd = "cd \"" + args[1] + "\"";
			} else if ("-r".equals(a1) && args.length == 2) {
				String scriptPath = args[1];
				String folder = PathUtils.getParent(scriptPath);
				String file = PathUtils.getFileName(scriptPath);
				if (file.indexOf(' ') != -1) {
					file = "\"" + file + "\"";
				}
				cmd = "cd \"" + folder + "\"; ./" + file;
			} else if ("-f".equals(a1) && args.length == 3) {
				String scriptPath = args[1];
				if (scriptPath.startsWith("/")) {
					String folder = PathUtils.getParent(scriptPath);
					String file = PathUtils.getFileName(scriptPath);
					if (file.indexOf(' ') != -1) {
						file = "\"" + file + "\"";
					}
					cmd = "cd \"" + folder + "\"; ./" + file + " " + args[2];
				} else {
					cmd = args[1] + " " + args[2];
				}
			} else if ("-n".equals(a1) && args.length == 3) {
				String scriptPath = args[1];
				if (scriptPath.startsWith("/")) {
					String folder = PathUtils.getParent(scriptPath);
					if (scriptPath.indexOf(' ') != -1) {
						scriptPath = "\"" + scriptPath + "\"";
					}
					cmd = "cd \"" + folder + "\"; nohup " + scriptPath + " "
							+ args[2];
				} else {
					cmd = "nohup " + args[1] + " ";
					// + (args.length > 3 ? args[3] : "");
				}
			} else if ("-b".equals(a1) && args.length == 3) {
				String scriptPath = args[1];
				if (scriptPath.startsWith("/")) {
					String folder = PathUtils.getParent(scriptPath);
					String file = PathUtils.getFileName(scriptPath);
					if (file.indexOf(' ') != -1) {
						file = "\"" + file + "\"";
					}
					cmd = "cd \"" + folder + "\"; ./" + file + " " + args[2]
							+ " &";
				} else {
					cmd = args[1] + " " + args[2];
				}
			} else if ("-c".equals(a1) && args.length == 2) {
				cmd = args[1];
			}
		}
		System.out.println("Commnd: " + cmd);
		tty = new SshTtyConnector(info);
		term.setTtyConnector(tty);
		term.start();

		add(term);

		String command = cmd;

		new Thread(() -> {
			System.out.println("Command sending");
			while (!stopFlag.get()) {
				if (tty.isConnected()) {
					TerminalPanel panel = term.getTerminalPanel();
					Dimension d = panel.getTerminalSizeFromComponent();
					term.getTerminalStarter().postResize(d, RequestOrigin.User);

					if (command != null && command.length() > 0) {
						System.out.println("Command sent");
						term.getTerminalStarter().sendString(command + "\n");
					}
					break;
				} else {
					System.out.println("not connected");
					if (tty.isCancelled()) {
						System.out.println("operation cancelled");
						break;
					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		add(term);

	}

//	public void openNewTab(String cmd) {
//		JediTermWidget w = new JediTermWidget(new DefaultSettingsProvider());
//		w.setTtyConnector(new SshTtyConnector(info, cmd));
//		tabbedTerminals.addTab("New tab", w, true);
//	}

	@Override
	public void reconnect() {
		new Thread(() -> {
			if (tty != null) {
				tty.stop();
			}

			tty = new SshTtyConnector(info);

			term.setTtyConnector(tty);
			term.start();

			System.out.println("Command sending");
			while (!stopFlag.get()) {
				if (tty.isConnected()) {
					TerminalPanel panel = term.getTerminalPanel();
					Dimension d = panel.getTerminalSizeFromComponent();
					term.getTerminalStarter().postResize(d, RequestOrigin.User);
					break;
				} else {
					System.out.println("not connected");
					if (tty.isCancelled()) {
						System.out.println("operation cancelled");
						break;
					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public void close() {
		stopFlag.set(true);
		tty.stop();
		new Thread(() -> {
			try {
				System.out.println("Terminal connection closing...");
				term.getTtyConnector().close();
				System.out.println("Connection closed");
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Tab is closed");
		}).start();
//		getParent().remove(this);
//		for (int i = 0; i < tabbedTerminals.getTabCount(); i++) {
//			JediTermWidget w = (JediTermWidget) tabbedTerminals.getTabAt(i);
//			w.getTtyConnector().close();
//		}
		// term.getTtyConnector().close();
	}

	@Override
	public boolean viewClosing() {
		System.out.println("Closing");
		close();
		return true;
	}

	@Override
	public void viewClosed() {
		// TODO Auto-generated method stub
		System.out.println("Closed");
	}

	@Override
	public void tabSelected() {
		if (term != null) {
			term.requestFocusInWindow();
		}
	}

	@Override
	public Icon getIcon() {
		return icon;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.components.TabbedChild#getTitle()
	 */
	@Override
	public String getTitle() {
		return "Terminal";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.session.SessionEventAware#configChanged()
	 */
	@Override
	public void configChanged() {
		System.out.println("Config changed on console");
		if (!embedded) {
			snippetProvider.setList(appSession.getApplicationContext()
					.getConfig().getTerminal().getSnippets());

			model.removeAllElements();
			model.addAll(appSession.getApplicationContext().getConfig()
					.getTerminal().getSnippets());
		}
//term.setNextProvider(snippetProvider);
//		model.removeAllElements();
//		model.addAll(appSession.getApplicationContext().getConfig()
//				.getTerminal().getSnippets());
//		registerSnippetShortcuts(appSession.getApplicationContext().getConfig()
//				.getTerminal().getSnippets());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.session.SessionEventAware#remoteFileSystemUpdated()
	 */
	@Override
	public void remoteFileSystemUpdated(String path) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.app.session.SessionEventAware#localFileSystemUpdated()
	 */
	@Override
	public void localFileSystemUpdated(String path) {
	}

	/**
	 * @return the embedded
	 */
	public boolean isEmbedded() {
		return embedded;
	}

	/**
	 * @param embedded the embedded to set
	 */
	public void setEmbedded(boolean embedded) {
		this.embedded = embedded;
	}

	private KeyStroke getKeystroke(SnippetItem item) {
		int modifier = 0;
		if (item.isAltDown()) {
			modifier |= InputEvent.ALT_DOWN_MASK;
		}
		if (item.isCtrlDown()) {
			modifier |= InputEvent.CTRL_DOWN_MASK;
		}
		if (item.isShiftDown()) {
			modifier |= InputEvent.SHIFT_DOWN_MASK;
		}
		return KeyStroke.getKeyStroke(item.getKeyChar(), modifier);
	}

	class SnippetActionProvider extends TerminalActionProviderBase {
		private List<TerminalAction> list;

		public SnippetActionProvider() {
			list = new ArrayList<>();
		}

		public void setList(List<SnippetItem> snippets) {
			list.clear();
			for (SnippetItem item : appSession.getApplicationContext()
					.getConfig().getTerminal().getSnippets()) {
				TerminalAction ta = new TerminalAction(item.getName(),
						new KeyStroke[] { getKeystroke(item) }, e -> {
							System.out.println("Insert snippet: "
									+ item.getName() + " " + item.getCommand());
							term.getTerminalStarter()
									.sendString(item.getCommand());
							return true;
						});
				list.add(ta);
			}
		}

		@Override
		public List<TerminalAction> getActions() {
			return list;
		}
	}

}

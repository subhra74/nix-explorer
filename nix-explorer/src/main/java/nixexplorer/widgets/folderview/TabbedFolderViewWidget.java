package nixexplorer.widgets.folderview;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import nixexplorer.app.components.FlatTabbedPane.TabListener;
import nixexplorer.app.session.AppSession;
import nixexplorer.core.FileSystemProvider;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.widgets.Widget;
import nixexplorer.widgets.util.Utility;

public abstract class TabbedFolderViewWidget extends Widget
		implements TabCallback, TabListener {
	private static final long serialVersionUID = -7543786446063512989L;
	protected JTabbedPane tabbedFolders;
	protected FileSystemProvider fs;
	protected Cursor waitCursor, defCursor;
	protected boolean applyPreviousAction = false;
	protected Icon icon;
	private JLayeredPane layer;
	private JPanel glassPane;

	protected TabbedFolderViewWidget(SessionInfo info, String args[],
			AppSession appSession, Window window) {
		super(info, args, appSession, window);
		this.setLayout(new BorderLayout());
		layer = new JLayeredPane();
		add(layer);
		glassPane = createGlassPane();
		layer.add(glassPane, JLayeredPane.PALETTE_LAYER);
		layer.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				tabbedFolders.setBounds(0, 0, layer.getWidth(),
						layer.getHeight());
				glassPane.setBounds(0, 0, layer.getWidth(), layer.getHeight());
			}
		});
		defCursor = getCursor();
		waitCursor = new Cursor(Cursor.WAIT_CURSOR);
		tabbedFolders = new JTabbedPane();
		tabbedFolders.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		layer.add(tabbedFolders, JLayeredPane.DEFAULT_LAYER);
		// add(tabbedFolders);
		tabbedFolders.addChangeListener(e -> {
			try {
				System.out.println(
						"selected: " + tabbedFolders.getSelectedIndex());
				int index = tabbedFolders.getSelectedIndex();
				if (index < 0)
					return;
				System.out.println("Selected index: " + index + " tab count: "
						+ tabbedFolders.getTabCount());

				for (int i = 0; i < tabbedFolders.getTabCount(); i++) {
					JComponent c = (JComponent) tabbedFolders
							.getTabComponentAt(i);
					if (c == null) {
						continue;
					}
					JButton btn = (JButton) c.getClientProperty("tab.button");
					btn.setVisible(false);
					c.setBorder(UIManager.getBorder("TabbedPane.flatBorder"));
				}

				JComponent c = (JComponent) tabbedFolders
						.getTabComponentAt(index);
				System.out.println("selected com: " + c);
				if (c == null) {
					return;
				}
				c.setBorder(
						UIManager.getBorder("TabbedPane.flatHighlightBorder"));
				JButton btn = (JButton) c.getClientProperty("tab.button");
				JLabel lblTitle = (JLabel) c.getClientProperty("tab.label");
				super.updateTabTitle(lblTitle.getText());
//				if (this.lblTitleTab != null) {
//					this.lblTitleTab.setText(getTabTitle(lblTitle.getText()));
//				}
				btn.setVisible(true);
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		});
	}

	@Override
	public void reconnect() {

	}

	public void openNewTab(String path) {
	}

	public String getTitle() {
		return getTitleText();
	}

	public abstract String getTitleText();

	@Override
	public void close() {

	}

	@Override
	public void openNewTab(String title, String path) {
	}

	@Override
	public void updateTitle(String title, Component c) {
		JComponent jc = (JComponent) c;
		if (jc.getClientProperty("tab.header") != null) {
			JComponent tabComponent = (JComponent) jc
					.getClientProperty("tab.header");
			if (tabComponent.getClientProperty("tab.label") != null) {
				JLabel tabLabel = (JLabel) tabComponent
						.getClientProperty("tab.label");
				tabLabel.setText(getTabTitle(title));
			}
		}
		super.updateTabTitle(title);
//		if (this.lblTitleTab != null) {
//			lblTitleTab.setText(getTabTitle(title));
//		}
		// tabbedFolders.getSelectedComponent();
//		if (c == tabbedFolders.getSelectedComponent()) {
//			tabbedFolders.setTitleAt(tabbedFolders.getSelectedIndex(), title);
//		}
	}

	protected void addTab(String title, Component c) {
		JPanel tabComponent = new JPanel(new BorderLayout());
		JButton btnClose = new JButton(UIManager.getIcon("Tab.roundCloseIcon"));
		btnClose.setBorderPainted(false);
		btnClose.setVisible(false);
		btnClose.addActionListener(e -> {
			int index = tabbedFolders.getSelectedIndex();
			if (index != -1) {
				tabbedFolders.removeTabAt(index);
			}
		});
//		btnClose.setBorder(new EmptyBorder(Utility.toPixel(3), Utility.toPixel(3), Utility.toPixel(3), Utility.toPixel(3)));
		JLabel lblTitle = new JLabel(title);
		lblTitle.setBorder(new EmptyBorder(Utility.toPixel(8),
				Utility.toPixel(8), Utility.toPixel(8), Utility.toPixel(8)));
		tabComponent.add(lblTitle);
		tabComponent.add(btnClose, BorderLayout.EAST);

		tabComponent.setBorder(UIManager.getBorder("TabbedPane.flatBorder"));
		tabComponent.putClientProperty("tab.label", lblTitle);
		tabComponent.putClientProperty("tab.button", btnClose);
		int index = tabbedFolders.getTabCount();
		System.out.println("Inserting tab at: " + index);
		tabbedFolders.insertTab(null, null, c, title, index);
//		tabbedFolders.addTab(null, c);
		tabbedFolders.setTabComponentAt(index, tabComponent);
		((JComponent) c).putClientProperty("tab.header", tabComponent);
		tabbedFolders.setSelectedIndex(index);
	}

	@Override
	public void openTerminal(String command) {

	}

	public abstract void editFile(String fileName);

	@Override
	public void disableUI() {
		setCursor(waitCursor);
		System.out.println("Showing glasspnae");
		this.glassPane.requestFocusInWindow();
		this.glassPane.setFocusTraversalKeysEnabled(false);
		this.glassPane.setVisible(true);
	}

	@Override
	public void enableUI() {
		setCursor(defCursor);
		System.out.println("Hiding glasspnae");
		this.glassPane.setVisible(false);
	}

//	@Override
//	public AppMessageListener getAppListener() {
//		return (AppMessageListener) super.env.get("app.msglistener");
//	}

	@Override
	public Widget getWidget() {
		return this;
	}

	@Override
	public void allTabClosed() {
		close();
	}

	public void disableView() {
//		System.out.println("disabling");
//		this.txtAddressBar.setEnabled(false);
//		this.btnUp.setEnabled(false);
//		this.folderTable.setEnabled(false);
		SwingUtilities.invokeLater(() -> {
			disableUI();
		});

	}

	public void enableView() {
		SwingUtilities.invokeLater(() -> {
			enableUI();
		});
//		System.out.println("enabling");
//		this.txtAddressBar.setEnabled(true);
//		this.btnUp.setEnabled(true);
//		this.folderTable.setEnabled(true);

	}

//	private String getTabTitle(String title) {
//		if (title.length() > 20) {
//			return title.substring(0, 20) + "...";
//		}
//		return title;
//	}

	
	@Override
	public Icon getIcon() {
		return this.icon;
	}

	private JPanel createGlassPane() {
		JPanel glassPane = new JPanel();
		glassPane.setFocusable(true);
		glassPane.addMouseListener(new MouseAdapter() {
		});
		glassPane.setOpaque(false);
//		JButton btn = new JButton("Cancel");
//		btn.addActionListener(e -> {
//			cancel();
//		});
//		glassPane.add(btn);
		glassPane.setVisible(false);
		return glassPane;
	}

	protected abstract void cancel();

}

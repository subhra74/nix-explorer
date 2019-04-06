/**
 * 
 */
package nixexplorer.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionListener;

import nixexplorer.TextHolder;
import nixexplorer.app.server.list.ServerListCellRenderer;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.AppSessionImpl;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.app.session.SessionManagerPanel;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class AppSidePanel extends JPanel implements SessionListCallback {
	/**
	 * 
	 */
	private Box b1, b2;
	private JScrollPane jsp;
	// private JPanel bottomPanel;
	private JButton btnCollapse;
	private JButton btnExpand;
	private Dimension expandedPreferedSize;
	private JList<AppSessionImpl> serverList;
	private DefaultListModel<AppSessionImpl> serverListModel;
	// private List<SessionInfo> sessions;
	private Window window;
	private AppContext context;

	public AppSidePanel(Window window, AppContext context) {
		System.out.println("Window-frame: " + window);
		setLayout(new BorderLayout());
		this.window = window;
		this.context = context;
		b1 = Box.createHorizontalBox();
		b1.setBorder(new EmptyBorder(Utility.toPixel(10), Utility.toPixel(10),
				Utility.toPixel(10), Utility.toPixel(10)));
		JLabel lblTitle = new JLabel(TextHolder.getString("app.connections"));
		lblTitle.setFont(
				new Font(Font.DIALOG, Font.PLAIN, Utility.toPixel(16)));
		b1.add(lblTitle);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(10)));
		b1.add(Box.createHorizontalGlue());
		JButton btnNew = new JButton(UIManager.getIcon("SidePanel.addIcon"));
		btnNew.addActionListener(e -> {
			makeNewSession();
		});
		// btnNew.setBorderPainted(false);
		b1.add(btnNew);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		btnCollapse = new JButton(UIManager.getIcon("SidePanel.collapseIcon"));
		btnCollapse.addActionListener(e -> {
			shrink();
		});

		btnExpand = new JButton(UIManager.getIcon("SidePanel.expandIcon"));
		btnExpand.addActionListener(e -> {
			expand();
		});
		// btnCollapse.setBorderPainted(false);
		b1.add(btnCollapse);

		b2 = Box.createHorizontalBox();
		b2.setBorder(new EmptyBorder(Utility.toPixel(10), Utility.toPixel(10),
				Utility.toPixel(10), Utility.toPixel(10)));

		b2.add(btnExpand);

		setBorder(new MatteBorder(0, 0, 0, Utility.toPixel(1),
				UIManager.getColor("DefaultBorder.color")));

		serverListModel = new DefaultListModel<>();
		// sessions = SessionStore.getSharedInstance().getSessions();

		// loadList();

		serverList = new JList<>(serverListModel);
		// serverList.setBackground(UIManager.getColor("Panel.secondary"));
		serverList.setCellRenderer(new ServerListCellRenderer());

		jsp = new JScrollPane(serverList);
		jsp.setViewportBorder(null);
		jsp.setBorder(new MatteBorder(Utility.toPixel(1), 0, 0, 0,
				UIManager.getColor("DefaultBorder.color")));

//		bottomPanel = createBottomPanel();

		add(b1, BorderLayout.NORTH);
		add(jsp);
//		add(bottomPanel, BorderLayout.SOUTH);
	}

	/**
		 * 
		 */
	public void makeNewSession() {
		SessionInfo info = new SessionManagerPanel().newSession();
		if (info == null) {
			return;
		}
		AppSessionImpl appSession = new AppSessionImpl(info, false, window,
				this.context);
		this.context.addSession(appSession);
		ServerDisplayPanel display = new ServerDisplayPanel(info, window, this,
				appSession);
		appSession.setDisplay(display);
		display.createInitialFolderView();
		serverListModel.addElement(appSession);
		serverList.setSelectedIndex(serverListModel.size() - 1);
	}

//	/**
//	 * 
//	 */
//	private void loadList() {
//		serverListModel.removeAllElements();
//		serverListModel.addAll(sessions.stream().map(e -> {
//			AppSessionImpl appSession = new AppSessionImpl(e, false);
//			appSession.setDisplay(new ServerDisplayPanel(e));
//			return appSession;
//		}).collect(Collectors.toList()));
//	}

//	private JPanel createBottomPanel() {
//		JPanel panel = new JPanel(
//				new BorderLayout(Utility.toPixel(1), Utility.toPixel(1)));
//		panel.setBackground(UIManager.getColor("DefaultBorder.color"));
//		panel.setBorder(new EmptyBorder(Utility.toPixel(1), 0, 0, 0));
//		panel.add(createSearchPanel());
//		JButton btnEdit = new JButton(UIManager.getIcon("ServerList.editIcon"));
//		btnEdit.setBorderPainted(false);
//		JButton btnDelete = new JButton(
//				UIManager.getIcon("ServerList.deleteIcon"));
//		btnDelete.setBorderPainted(false);
//		Box box = Box.createHorizontalBox();
//		box.add(btnEdit);
//		box.add(Box.createHorizontalStrut(Utility.toPixel(1)));
//		box.add(btnDelete);
//		panel.add(box, BorderLayout.EAST);
//		return panel;
//	}

	private JPanel createSearchPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JTextField txt = new JTextField(20);
		txt.setBorder(null);
		JButton btnSearch = new JButton(
				UIManager.getIcon("ServerList.searchIcon"));
		btnSearch.setBorderPainted(false);
		panel.add(txt);
		panel.add(btnSearch, BorderLayout.EAST);
		return panel;
	}

	private void shrink() {
		this.expandedPreferedSize = getPreferredSize();
		this.removeAll();
		this.setPreferredSize(b2.getPreferredSize());
		this.add(b2, BorderLayout.NORTH);
		doLayout();
		revalidate();
		repaint();
	}

	private void expand() {
		this.removeAll();
		add(b1, BorderLayout.NORTH);
		add(jsp);
		// add(bottomPanel, BorderLayout.SOUTH);
		setPreferredSize(expandedPreferedSize);
		doLayout();
		revalidate();
		repaint();
	}

	public AppSessionImpl getSelectedSession() {
		return serverList.getSelectedValue();
	}

	public void setListSelectionListener(ListSelectionListener listener) {
		this.serverList.addListSelectionListener(listener);
	}

	@Override
	public void close(AppSession session) {
		System.out.println("Removing");
		System.out.println(
				"Found and removed: " + serverListModel.removeElement(session));
	}
}

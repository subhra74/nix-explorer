package nixexplorer.widgets.du;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import nixexplorer.TextHolder;
import nixexplorer.app.session.AppSession;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshUtility;
import nixexplorer.core.ssh.SshWrapper;
import nixexplorer.widgets.Widget;
import nixexplorer.widgets.component.WaitDialog;
import nixexplorer.widgets.util.Utility;

public class DiskUsageViewerWidget extends Widget implements Runnable {

	private JComboBox<String> cmbFolderHistory;
	private DefaultComboBoxModel<String> folderModel;
	private AtomicBoolean stopRequested = new AtomicBoolean(false);
	private JButton btnGo;
	private Thread t;
	private WaitDialog waitDialog;
	private DiskUsageTableModel model;
	private JTable table;
	private SshWrapper wrapper;
	private JLabel lblTotal;

	public DiskUsageViewerWidget(SessionInfo info, String[] args,
			AppSession appSession, Window window) {
		super(info, args, appSession, window);
		createUI();
		waitDialog = new WaitDialog(getWindow(), e -> {
			dialogClosed();
		});
	}

	private void dialogClosed() {
		if (waitDialog != null) {
			waitDialog.dispose();
		}
		new Thread(() -> {
			try {
				if (this.wrapper != null) {
					this.wrapper.disconnect();
				}
			} catch (Exception e) {
			}
		}).start();
	}

	private void createUI() {
		setLayout(new BorderLayout(Utility.toPixel(5), Utility.toPixel(5)));
		setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		folderModel = new DefaultComboBoxModel<>();
		cmbFolderHistory = new JComboBox<>(folderModel);

//		cmbFolderHistory.addActionListener(e -> {
//			
//			btnGo.doClick();
//		});
		cmbFolderHistory.setEditable(true);

		JPanel topPanel = new JPanel(
				new BorderLayout(Utility.toPixel(5), Utility.toPixel(5)));

		topPanel.add(
				new JLabel(TextHolder.getString("diskUsageViewer.targetLabel")),
				BorderLayout.WEST);
		topPanel.add(cmbFolderHistory);
		btnGo = new JButton(TextHolder.getString("diskUsageViewer.go"));
		btnGo.addActionListener(e -> {
			System.out.println("calling action listener");
			String item = (String) cmbFolderHistory.getSelectedItem();
			boolean found = false;
			for (int i = 0; i < folderModel.getSize(); i++) {
				if (folderModel.getElementAt(i).equals(item)) {
					found = true;
					break;
				}
			}
			if (!found) {
				cmbFolderHistory.addItem(item);
			}
			if (item != null && item.length() > 0) {
				stopRequested.set(false);
				t = new Thread(this);
				t.start();
			}
		});
		topPanel.add(btnGo, BorderLayout.EAST);
		add(topPanel, BorderLayout.NORTH);

		TextRenderer r = new TextRenderer();
		r.setText("Dummy");
		int height = r.getPreferredSize().height;
		r.setText("");

		model = new DiskUsageTableModel();
		table = new JTable(model);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setDefaultRenderer(Double.class, new UsageRenderer());
		table.setDefaultRenderer(Long.class, r);
		table.setDefaultRenderer(String.class, r);
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);
		table.setRowHeight(height + Utility.toPixel(10));
		table.getRowSorter().setSortKeys(
				Arrays.asList(new RowSorter.SortKey(1, SortOrder.DESCENDING)));
		JScrollPane jsp = new JScrollPane(table);
		jsp.getViewport().setBackground(table.getBackground());

		add(jsp);

		Box b1 = Box.createHorizontalBox();
		b1.add(Box.createRigidArea(
				new Dimension(Utility.toPixel(10), Utility.toPixel(30))));
		lblTotal = new JLabel();
		b1.add(lblTotal);
		add(b1, BorderLayout.SOUTH);
	}

	@Override
	public void tabSelected() {
		cmbFolderHistory.requestFocus();
	}

	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public String getTitle() {
		return TextHolder.getString("diskUsageViewer.title");
	}

	@Override
	public boolean viewClosing() {
		dialogClosed();
		return true;
	}

	@Override
	public void reconnect() {

	}

	@Override
	public void close() {

	}

	@Override
	public void run() {
		SwingUtilities.invokeLater(() -> {
			waitDialog.setLocationRelativeTo(getWindow());
			waitDialog.setVisible(true);
		});

		try {
			String text = (String) cmbFolderHistory.getSelectedItem();
			if (wrapper == null || !wrapper.isConnected()) {
				wrapper = connect();
			}
			List<String> output = new LinkedList<>();

			if (SshUtility.executeCommand(wrapper,
					"export BLOCKSIZE=512; du -c \"" + text + "\"|gzip|cat",
					true, output) != 0) {
				throw new Exception();
			}
			if (stopRequested.get()) {
				return;
			}
			List<DiskUsageEntry> list = new ArrayList<>();
			long total = DuOuputParser.parse(output, list);
			if (stopRequested.get()) {
				return;
			}
			SwingUtilities.invokeLater(() -> {
				model.setData(list);
				lblTotal.setText("Total size: "
						+ Utility.humanReadableByteCount(total, true));
			});
		} catch (Exception e) {
			e.printStackTrace();
			if (!(stopRequested.get() || widgetClosed.get())) {
				JOptionPane.showMessageDialog(this,
						TextHolder.getString("folderview.genericError"));
			}
		} finally {
			SwingUtilities.invokeLater(() -> {
				waitDialog.setVisible(false);
			});
		}
	}

}

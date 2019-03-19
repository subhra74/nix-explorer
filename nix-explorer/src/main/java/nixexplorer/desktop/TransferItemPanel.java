package nixexplorer.desktop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import nixexplorer.PathUtils;
import nixexplorer.TextHolder;
import nixexplorer.core.ssh.filetransfer.FileTransfer;
import nixexplorer.widgets.listeners.AppMessageListener;
import nixexplorer.widgets.listeners.AppMessageListener.TransferStatus;
import nixexplorer.widgets.util.Utility;

public class TransferItemPanel extends JPanel implements Runnable {
	private static final long serialVersionUID = -5656905924748520207L;
	private FileTransfer transfer;
	private JLabel iconLabel;
	private JLabel nameLabel;
	private JLabel statusLabel;
	private JButton btnPause, btnRemove;
	private JProgressBar prgProgress;
	private TransferOverviewPanel panel;
	private TransferStatus status;
	private int prg;

	public TransferItemPanel(FileTransfer transfer,
			TransferOverviewPanel panel) {
		this.panel = panel;
		this.transfer = transfer;
		// this.transfer.addStatusListener(this);
		setLayout(new BorderLayout());
		setPreferredSize(
				new Dimension(Utility.toPixel(250), Utility.toPixel(60)));
		iconLabel = new JLabel();
		iconLabel.setOpaque(true);
		iconLabel.setBackground(Color.GRAY);
		iconLabel.setPreferredSize(
				new Dimension(Utility.toPixel(48), Utility.toPixel(48)));
		iconLabel.setBorder(new EmptyBorder(3, 3, 3, 3));
		add(iconLabel, BorderLayout.WEST);

		Box b1 = Box.createVerticalBox();
		String file = new File(transfer.getSourceFileName()).getName();
		nameLabel = new JLabel(file + " - " + transfer.getHostName());
		b1.add(nameLabel);

		prgProgress = new JProgressBar();
		b1.add(prgProgress);

		statusLabel = new JLabel();
		b1.add(statusLabel);

		add(b1);

		Box b2 = Box.createVerticalBox();

		btnPause = new JButton(TextHolder.getString("transfer.pause"));
		btnPause.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if ("false"
						.equals(btnPause.getClientProperty("state.paused"))) {
					transfer.stop();
					btnPause.putClientProperty("state.paused", "true");
					btnPause.setText(TextHolder.getString("transfer.resume"));
				} else {
					transfer.resume();
					btnPause.putClientProperty("state.paused", "false");
					btnPause.setText(TextHolder.getString("transfer.pause"));
				}
			}
		});
		btnRemove = new JButton(TextHolder.getString("transfer.remove"));

		b2.add(btnPause);
		b2.add(btnRemove);

		add(b2, BorderLayout.EAST);
	}

	public void run() {
		System.out.println("Updating progress: " + status + " " + prg);
		String statText = "";
		System.out.println("notified " + status);
		if (status == TransferStatus.Complete) {
			statText = TextHolder.getString("transfers.finished");
			panel.removeItem(this);
			return;
		} else if (status == TransferStatus.Failed) {
			statText = TextHolder.getString("transfers.failed");
		} else if (status == TransferStatus.stopped) {
			statText = TextHolder.getString("transfers.stopped");
		} else if (status == TransferStatus.InProgress
				|| status == TransferStatus.Initiating) {
			statText = TextHolder.getString("transfers.transferring");
		}

		statusLabel.setText(statText);
		prgProgress.setValue(prg);
	}

	public void notify(TransferStatus status, FileTransfer transfer) {
		if (this.transfer == transfer) {
			this.status = status;
			this.prg = transfer.getPercentComplete();
			SwingUtilities.invokeLater(this);
		}
	}

}

package nixexplorer.desktop;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import nixexplorer.TextHolder;
import nixexplorer.core.ssh.filetransfer.FileTransfer;
import nixexplorer.widgets.listeners.AppMessageListener;
import nixexplorer.widgets.listeners.TransferWatcher;
import nixexplorer.widgets.listeners.AppMessageListener.TransferStatus;
import nixexplorer.widgets.util.Utility;

public class TransferOverviewPanel extends JPanel implements TransferWatcher {
	private static final long serialVersionUID = -8683990631791418758L;
	private JScrollPane jsp;
	private Box b1;
	private AppMessageListener appListener;

	public TransferOverviewPanel(AppMessageListener appListener) {
		this.appListener = appListener;
		setPreferredSize(
				new Dimension(Utility.toPixel(300), Utility.toPixel(180)));
		BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(layout);
		b1 = Box.createVerticalBox();
		jsp = new JScrollPane(b1);
		add(jsp);
	}

	@Override
	public void addTransfer(FileTransfer f) {
		System.out.println("Subitting download");
		try {
			f.setStatusListener(appListener);
			TransferItemPanel p = new TransferItemPanel(f, this);
			b1.add(p);
			revalidate();
			repaint();
			System.out.println("Starting download");
			f.start();
			System.out.println("Added");
			JLabel lbl = (JLabel) this.getClientProperty("label");
			if (lbl != null) {
				lbl.setText(b1.getComponentCount() + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void removeItem(JPanel p) {
		b1.remove(p);
		b1.revalidate();
		b1.repaint();
		System.out.println("Removed panel");
		JLabel lbl = (JLabel) this.getClientProperty("label");
		if (lbl != null) {
			int c = b1.getComponentCount();
			if (c == 0) {
				lbl.setText(TextHolder.getString("toolbar.transfers"));
			} else {
				lbl.setText(c + "");
			}
		}
	}

	public void notify(TransferStatus status, FileTransfer transfer) {
		SwingUtilities.invokeLater(() -> {
			for (int i = 0; i < b1.getComponentCount(); i++) {
				Component c = b1.getComponent(i);
				if (c instanceof TransferItemPanel) {
					((TransferItemPanel) c).notify(status, transfer);
				}
			}
		});
	}
}

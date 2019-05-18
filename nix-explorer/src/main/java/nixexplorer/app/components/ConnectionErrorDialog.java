/**
 * 
 */
package nixexplorer.app.components;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import nixexplorer.Constants;
import nixexplorer.TextHolder;
import nixexplorer.app.AppContext;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class ConnectionErrorDialog extends JDialog {
	private DefaultListModel<String> model;
	private JList<String> list;
	private static Object lock = new Object();
	private AtomicBoolean visible = new AtomicBoolean(false);
	private AtomicBoolean yes = new AtomicBoolean(true);
	public static final ConnectionErrorDialog INSTANCE = new ConnectionErrorDialog();

	private ConnectionErrorDialog() {
		setModal(true);
		model = new DefaultListModel<>();
		setTitle(TextHolder.getString("common.confirm"));
		list = new JList<>(model);
		setSize(Utility.toPixel(400), Utility.toPixel(400));
		JScrollPane jsp = new JScrollPane(list);
		jsp.setBorder(new LineBorder(UIManager.getColor("Panel.secondary"),
				Utility.toPixel(1)));
		JButton btnYes = new JButton(TextHolder.getString("common.yes"));
		JButton btnNo = new JButton(TextHolder.getString("common.no"));
		Box b1 = Box.createHorizontalBox();
		b1.add(Box.createHorizontalGlue());
		b1.add(btnYes);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				btnNo.doClick();
			}
		});
		btnYes.addActionListener(e -> {
			yes.set(true);
			model.removeAllElements();
			visible.set(false);
			setVisible(false);
			synchronized (lock) {
				System.out.println("Notifiying waiting threads");
				lock.notifyAll();
			}
		});
		btnNo.addActionListener(e -> {
			yes.set(false);
			visible.set(false);
			model.removeAllElements();
			setVisible(false);
			synchronized (lock) {
				System.out.println("Notifiying waiting threads");
				lock.notifyAll();
			}
		});
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b1.add(btnNo);

		JPanel p = new JPanel(
				new BorderLayout(Utility.toPixel(5), Utility.toPixel(5)));
		p.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));

		p.add(jsp);
		p.add(b1, BorderLayout.SOUTH);
		JLabel lbl = new JLabel(TextHolder.getString("common.faied"));
		lbl.setFont(Utility.getFont(Constants.NORMAL));
		p.add(lbl, BorderLayout.NORTH);

		add(p);
	}

	public boolean shouldRetry(String message) {
		SwingUtilities.invokeLater(() -> {
			boolean found = false;
			for (int i = 0; i < model.getSize(); i++) {
				String s = model.elementAt(i);
				if (s.equals(message)) {
					found = true;
					break;
				}
			}
			if (!found) {
				model.addElement(message);
			}
		});
		if (!visible.get()) {
			setLocationRelativeTo(AppContext.INSTANCE.getWindow());
			visible.set(true);
			setVisible(true);
		} else {
			while (visible.get()) {
				try {
					synchronized (lock) {
						lock.wait(1000);
					}
				} catch (InterruptedException e) {
				}
			}
		}
		return yes.get();
	}

	class ErrorItem {
		String name;
		Icon icon;
	}
}

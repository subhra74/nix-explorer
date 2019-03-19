/**
 * 
 */
package nixexplorer.widgets.editor;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class SaveProgressDialog extends JDialog {
	/**
	 * 
	 */
	private JLabel lbl;
	private JProgressBar prg;
	private JButton btnSave;

	public SaveProgressDialog(Window parent, ActionListener a) {
		lbl = new JLabel("Saving file, please wait...");
		prg = new JProgressBar();
		btnSave = new JButton("Cancel");
		setModal(true);

		Box b1 = Box.createHorizontalBox();
		b1.add(prg);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b1.add(btnSave);

		lbl.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		b1.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		add(lbl);
		add(b1, BorderLayout.SOUTH);

		btnSave.addActionListener(a);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				btnSave.doClick();
			}
		});

		pack();
	}

	public void setValue(int value) {
		this.prg.setValue(value);
	}
}

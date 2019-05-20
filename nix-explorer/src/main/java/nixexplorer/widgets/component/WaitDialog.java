/**
 * 
 */
package nixexplorer.widgets.component;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import nixexplorer.App;
import nixexplorer.TextHolder;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class WaitDialog extends JDialog {

	/**
	 * 
	 */
	public WaitDialog(Window window, ActionListener a) {
		super(window);
		setIconImage(App.getAppIcon());
		setModal(true);
		setTitle(TextHolder.getString("waiting.title"));
		JLabel lblText = new JLabel(TextHolder.getString("waiting.message"));
		lblText.setBorder(new EmptyBorder(Utility.toPixel(20),
				Utility.toPixel(20), Utility.toPixel(20), Utility.toPixel(20)));
		add(lblText);
		pack();
		setLocationRelativeTo(window);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.
			 * WindowEvent)
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				a.actionPerformed(new ActionEvent(this, this.hashCode(),
						"Window closing"));
			}
		});
	}
}

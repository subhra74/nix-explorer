/**
 * 
 */
package nixexplorer.app.components.keygen;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import nixexplorer.TextHolder;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class KeyGeneratorDialog extends JDialog {
	private KeyGeneratorPanel panel;

	public static void show(Window window, SessionInfo info) {
		JComboBox<String> cmbMode = new JComboBox<String>(
				new String[] { TextHolder.getString("keygen.remote"), TextHolder.getString("keygen.local") });
		if (JOptionPane.showOptionDialog(window, new Object[] { cmbMode }, TextHolder.getString("keygen.prompt"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null) == JOptionPane.OK_OPTION) {
			KeyGeneratorDialog dlg = null;
			dlg = new KeyGeneratorDialog(window, info, cmbMode.getSelectedIndex() == 0);
			dlg.setLocationRelativeTo(window);
			dlg.loadKeys();
			dlg.setVisible(true);
		}
	}

	/**
	 * 
	 */
	public KeyGeneratorDialog(Window window, SessionInfo info, boolean remote) {
		super(window);
		setModal(true);
		setLayout(new BorderLayout());
		setSize(Utility.toPixel(640), Utility.toPixel(480));
		if (remote) {
			panel = new RemoteKeyGeneratorPanel(info, this);
			setTitle(TextHolder.getString("keygen.remote2remote"));
		} else {
			panel = new LocalKeyGeneratorPanel(info, this);
			setTitle(TextHolder.getString("keygen.local2remote"));
		}

		add(panel);
	}

	public void loadKeys() {
		panel.loadKeys();
	}
}

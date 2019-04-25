/**
 * 
 */
package nixexplorer.widgets.folderview.remote;

import nixexplorer.widgets.console.TerminalDialog;

/**
 * @author subhro
 *
 */
public class RemoteFolderViewUtils {
	public static void openTerminalDialog(String command,
			RemoteFolderViewWidget remoteFolderView, boolean autoClose,
			boolean modal) {
		System.out.println("Window: " + remoteFolderView.getWindow());
		TerminalDialog terminalDialog = new TerminalDialog(
				remoteFolderView.getInfo(), new String[] { "-c", command },
				remoteFolderView.getAppSession(), remoteFolderView.getWindow(),
				"Command window", autoClose, modal);
		terminalDialog.setLocationRelativeTo(remoteFolderView.getWindow());
		terminalDialog.setVisible(true);
	}
}

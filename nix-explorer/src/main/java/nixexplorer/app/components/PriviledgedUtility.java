/**
 * 
 */
package nixexplorer.app.components;

import nixexplorer.widgets.folderview.remote.AskForPriviledgeDlg;

/**
 * @author subhro
 *
 */
public class PriviledgedUtility {
	public static final String generatePriviledgedCommand(String cmd) {
		String suCmd = AskForPriviledgeDlg.askForPriviledge();
		if (suCmd == null) {
			return null;
		}
		StringBuilder command = new StringBuilder();
		command.append(suCmd + " ");
		boolean sudo = false;
		sudo = suCmd.startsWith("sudo");
		if (!sudo) {
			command.append(" '");
		}

		command.append("sh<<EOF\n" + cmd + "\nEOF\n");

		if (!sudo) {
			command.append("'");
		}
		System.out.println("Command: " + command);
		return command.toString();
	}
}

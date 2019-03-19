package nixexplorer.app.components;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import nixexplorer.TextHolder;

public class CredentialsDialog {
	private static JTextField txtUser = new JTextField(30);
	private static JPasswordField txtPass = new JPasswordField(30);

	static Object[] components = new Object[] {
			TextHolder.getString("app.auth.title"),
			TextHolder.getString("app.auth.user"), txtUser,
			TextHolder.getString("app.auth.pass"), txtPass };

	public static Credentials promptCredentials() {
		if (JOptionPane.showOptionDialog(null, components,
				TextHolder.getString("app.auth.title"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, null, null) == JOptionPane.OK_OPTION) {
			return new Credentials(txtUser.getText(),
					new String(txtPass.getPassword()));
		}
		return null;
	}

	public static class Credentials {
		private String user, pass;

		public Credentials(String user, String pass) {
			super();
			this.user = user;
			this.pass = pass;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getPass() {
			return pass;
		}

		public void setPass(String pass) {
			this.pass = pass;
		}
	}
}

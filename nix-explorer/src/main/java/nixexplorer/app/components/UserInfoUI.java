package nixexplorer.app.components;

import java.awt.Dimension;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import nixexplorer.app.session.SessionInfo;
import nixexplorer.widgets.util.Utility;

public class UserInfoUI implements UserInfo, UIKeyboardInteractive {
	// private String lastEnteredPassword;
//	private boolean yesNoAnswer;
//	private String lastEnteredPassphrase;
	private Window mainFrame;
	private static Map<String, String> passwordMap = new ConcurrentHashMap<>();
	private static Map<String, String> passphraseMap = new ConcurrentHashMap<>();
	private SessionInfo info;
	private JPasswordField password = new JPasswordField(30);
	private static AtomicBoolean confirmYes = new AtomicBoolean(false);
	private static AtomicBoolean suppressMessage = new AtomicBoolean(false);
	private AtomicLong attempt = new AtomicLong(0);

	/**
	 * 
	 */
	public UserInfoUI(SessionInfo info, Window mainFrame) {
		this.info = info;
		this.mainFrame = mainFrame;
	}

	@Override
	public String[] promptKeyboardInteractive(String destination, String name,
			String instruction, String[] prompt, boolean[] echo) {
		if (attempt.get() == 0) {
			if (prompt.length == 1 && prompt[0] != null
					&& prompt[0].toLowerCase().startsWith("password")
					&& info.getPassword() != null) {
				System.out.println(
						"Keyboard interactive - Assuming password is being asked for");
				return new String[] { info.getPassword() };
			}
		}

		attempt.incrementAndGet();

		List<Object> list = new ArrayList<>();
		list.add(destination);
		list.add(name);
		list.add(instruction);

		int i = 0;
		for (String s : Arrays.asList(prompt)) {
			System.out.println(s);
			list.add(s);
			if (echo[i++]) {
				JTextField txt = new JTextField(30);
				list.add(txt);
			} else {
				JPasswordField pass = new JPasswordField(30);
				list.add(pass);
			}
		}

		Object arr[] = new Object[list.size()];
		list.toArray(arr);

		if (JOptionPane.showOptionDialog(mainFrame, arr, "Input",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				null, null) == JOptionPane.OK_OPTION) {
			List<String> responses = new ArrayList<>();
			for (Object obj : list) {
				if (obj instanceof JPasswordField) {
					responses.add(
							new String(((JPasswordField) obj).getPassword()));
				} else if (obj instanceof JTextField) {
					responses.add(((JTextField) obj).getText());
				}
			}

			String arr1[] = new String[responses.size()];
			responses.toArray(arr1);
			return arr1;
		}

		return null;
	}

	@Override
	public void showMessage(String message) {
		System.out.println("showMessage: " + message);
		if (!UserInfoUI.suppressMessage.get()) {
			JCheckBox chkHideWarn = new JCheckBox("Hide warnings");
			chkHideWarn.setSelected(true);
			JTextArea txtMsg = new JTextArea();
			txtMsg.setEditable(false);
			txtMsg.setText(message);
			JScrollPane jsp = new JScrollPane(txtMsg);
			jsp.setPreferredSize(
					new Dimension(Utility.toPixel(600), Utility.toPixel(300)));
			jsp.setBorder(
					new LineBorder(UIManager.getColor("DefaultBorder.color"),
							Utility.toPixel(1)));
			JOptionPane.showOptionDialog(mainFrame,
					new Object[] { jsp, chkHideWarn }, "Info",
					JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
					null, null);
			if (chkHideWarn.isSelected()) {
				UserInfoUI.suppressMessage.set(true);
			}
		}
	}

	@Override
	public boolean promptYesNo(String message) {
		System.out.println("promptYesNo: " + message);
		if (UserInfoUI.confirmYes.get()) {
			return true;
		}
		if (JOptionPane.showConfirmDialog(mainFrame,
				message) == JOptionPane.YES_OPTION) {
			if (!UserInfoUI.confirmYes.get()) {
				UserInfoUI.confirmYes.set(true);
			}
			return true;
		}
		return false;
		// return true;
	}

	@Override
	public boolean promptPassword(String message) {
		System.out.println("promptPassword: " + message);
		if (attempt.get() == 0
				&& UserInfoUI.getPreEnteredPassword(info.getId()) != null
				&& UserInfoUI.getPreEnteredPassword(info.getId())
						.length() > 0) {
			return true;
		}
		attempt.getAndIncrement();
		password.setText("");
		if (JOptionPane.showOptionDialog(mainFrame,
				new Object[] { message, password }, "Password",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, null, null) == JOptionPane.YES_OPTION) {
			UserInfoUI.setPreEnteredPassword(info.getId(),
					new String(password.getPassword()));
			return true;
		}
		return false;
	}

	@Override
	public boolean promptPassphrase(String message) {
		System.out.println("promptPassphrase: " + message);
		if (attempt.get() == 0
				&& UserInfoUI.getPreEnteredPassphrase(info.getId()) != null
				&& UserInfoUI.getPreEnteredPassphrase(info.getId())
						.length() > 0) {
			return true;
		}
		attempt.getAndIncrement();
		password.setText("");
		if (JOptionPane.showOptionDialog(mainFrame,
				new Object[] { message, password }, "Passphrase",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
				null, null, null) == JOptionPane.YES_OPTION) {
			UserInfoUI.setPreEnteredPassphrase(info.getId(),
					new String(password.getPassword()));
			return true;
		}
		return false;
	}

	@Override
	public String getPassword() {
		System.out.println("getPassword");
		return UserInfoUI.getPreEnteredPassword(info.getId());
	}

	@Override
	public String getPassphrase() {
		System.out.println("getPassphrase");
		return UserInfoUI.getPreEnteredPassphrase(info.getId());
	}

	public static synchronized String getPreEnteredPassword(String id) {
		return passwordMap.get(id);
	}

	public static synchronized void setPreEnteredPassword(String id,
			String preEnteredPassword) {
		passwordMap.put(id, preEnteredPassword);
	}

	public static synchronized String getPreEnteredPassphrase(String id) {
		return passphraseMap.get(id);
	}

	public static synchronized void setPreEnteredPassphrase(String id,
			String preEnteredPassphrase) {
		passphraseMap.put(id, preEnteredPassphrase);
	}
}

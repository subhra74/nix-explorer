/**
 * 
 */
package nixexplorer.app.settings.snippet;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author subhro
 *
 */
public class SnippetItem {

	private String name, command;
	private char keyChar;
	private boolean isAltDown, isCtrlDown, isShiftDown;

	/**
	 * 
	 */
	public SnippetItem() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 * @param command
	 * @param keyChar
	 * @param isAltDown
	 * @param isCtrlDown
	 * @param isShiftDown
	 */
	public SnippetItem(String name, String command, char keyChar,
			boolean isAltDown, boolean isCtrlDown, boolean isShiftDown) {
		super();
		this.name = name;
		this.command = command;
		this.keyChar = keyChar;
		this.isAltDown = isAltDown;
		this.isCtrlDown = isCtrlDown;
		this.isShiftDown = isShiftDown;
	}

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * @return the keyChar
	 */
	public char getKeyChar() {
		return keyChar;
	}

	/**
	 * @param keyChar the keyChar to set
	 */
	public void setKeyChar(char keyChar) {
		this.keyChar = keyChar;
	}

	/**
	 * @return the isAltDown
	 */
	public boolean isAltDown() {
		return isAltDown;
	}

	/**
	 * @param isAltDown the isAltDown to set
	 */
	public void setAltDown(boolean isAltDown) {
		this.isAltDown = isAltDown;
	}

	/**
	 * @return the isCtrlDown
	 */
	public boolean isCtrlDown() {
		return isCtrlDown;
	}

	/**
	 * @param isCtrlDown the isCtrlDown to set
	 */
	public void setCtrlDown(boolean isCtrlDown) {
		this.isCtrlDown = isCtrlDown;
	}

	/**
	 * @return the isShiftDown
	 */
	public boolean isShiftDown() {
		return isShiftDown;
	}

	/**
	 * @param isShiftDown the isShiftDown to set
	 */
	public void setShiftDown(boolean isShiftDown) {
		this.isShiftDown = isShiftDown;
	}

	@JsonIgnore
	public String getKeystroke() {
		StringBuilder sb = new StringBuilder();
		if (isAltDown) {
			sb.append("ALT");
		}
		if (isCtrlDown) {
			if (sb.length() > 0) {
				sb.append("+");
			}
			sb.append("CTRL");
		}
		if (isShiftDown) {
			if (sb.length() > 0) {
				sb.append("+");
			}
			sb.append("SHIFT");
		}
		sb.insert(0, keyChar + " ");
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name + " " + getKeystroke();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}

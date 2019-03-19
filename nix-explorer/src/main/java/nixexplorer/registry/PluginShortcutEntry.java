/**
 * 
 */
package nixexplorer.registry;

/**
 * @author subhro
 *
 */
public class PluginShortcutEntry {
	private String className;
	private String[] command;
	private String name;

	/**
	 * @param className
	 * @param command
	 */
	public PluginShortcutEntry(String className, String[] command,
			String name) {
		super();
		this.className = className;
		this.command = command;
		this.name = name;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the command
	 */
	public String[] getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	public void setCommand(String[] command) {
		this.command = command;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

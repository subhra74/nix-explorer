package nixexplorer.registry;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import nixexplorer.registry.contextmenu.ContextMenuEntry;

public class PluginEntry {
	private String className;
	private List<ContextMenuEntry> contextMenuList = new ArrayList<>();
	private String name;
	private Icon icon;
	private Icon smallIcon;
	private String[] commands = new String[0];
	private boolean showOnLauncher;

	public PluginEntry(String className, List<ContextMenuEntry> contextMenuList,
			String name, Icon icon, Icon smallIcon, String[] commands,
			boolean showOnLauncher) {
		super();
		this.className = className;
		this.contextMenuList = contextMenuList;
		this.name = name;
		this.icon = icon;
		this.smallIcon = smallIcon;
		this.commands = commands;
		this.showOnLauncher = showOnLauncher;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<ContextMenuEntry> getContextMenuItems() {
		return contextMenuList;
	}

	public void addContextMenuItems(List<ContextMenuEntry> contextMenuList) {
		this.contextMenuList.addAll(contextMenuList);
	}

	public void addContextMenuItem(ContextMenuEntry ent) {
		this.contextMenuList.add(ent);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the icon
	 */
	public Icon getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	/**
	 * @return the smallIcon
	 */
	public Icon getSmallIcon() {
		return smallIcon;
	}

	/**
	 * @param smallIcon the smallIcon to set
	 */
	public void setSmallIcon(Icon smallIcon) {
		this.smallIcon = smallIcon;
	}

	/**
	 * @return the commands
	 */
	public String[] getCommands() {
		return commands;
	}

	/**
	 * @param commands the commands to set
	 */
	public void setCommands(String[] commands) {
		this.commands = commands;
	}

	/**
	 * @return the showOnLauncher
	 */
	public boolean isShowOnLauncher() {
		return showOnLauncher;
	}

	/**
	 * @param showOnLauncher the showOnLauncher to set
	 */
	public void setShowOnLauncher(boolean showOnLauncher) {
		this.showOnLauncher = showOnLauncher;
	}
}

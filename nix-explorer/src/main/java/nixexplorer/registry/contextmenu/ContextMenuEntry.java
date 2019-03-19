package nixexplorer.registry.contextmenu;

import java.util.Arrays;

public class ContextMenuEntry {
	private boolean folderSupported;
	private String menuText;
	private String[] fileExt;
	private String protocol;
	private String className;
	private String[] args;
	private boolean supportsMultipleItems;
	private boolean supportsEmptySelection;

	public static ContextMenuEntry build(String menuText, String className,
			String[] args, String[] fileExt, String protocol,
			boolean folderSupported, boolean supportsMultipleItems,
			boolean supportsEmptySelection) {
		ContextMenuEntry ent = new ContextMenuEntry();
		ent.setMenuText(menuText);
		ent.setArgs(args);
		ent.setClassName(className);
		ent.setFileExt(fileExt);
		ent.setFolderSupported(folderSupported);
		ent.setProtocol(protocol);
		ent.setSupportsMultipleItems(supportsMultipleItems);
		ent.supportsEmptySelection = supportsEmptySelection;
		return ent;
	}

	public boolean isFolderSupported() {
		return folderSupported;
	}

	public void setFolderSupported(boolean folderSupported) {
		this.folderSupported = folderSupported;
	}

	public String[] getFileExt() {
		return fileExt;
	}

	public void setFileExt(String[] fileExt) {
		this.fileExt = fileExt;
	}

	public String getMenuText() {
		return menuText;
	}

	public void setMenuText(String menuText) {
		this.menuText = menuText;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public boolean isSupportsMultipleItems() {
		return supportsMultipleItems;
	}

	public void setSupportsMultipleItems(boolean supportsMultipleItems) {
		this.supportsMultipleItems = supportsMultipleItems;
	}

	public boolean isSupportsEmptySelection() {
		return supportsEmptySelection;
	}

	public void setSupportsEmptySelection(boolean supportsEmptySelection) {
		this.supportsEmptySelection = supportsEmptySelection;
	}

	@Override
	public String toString() {
		return "ContextMenuEntry [folderSupported=" + folderSupported
				+ ", menuText=" + menuText + ", fileExt="
				+ Arrays.toString(fileExt) + ", protocol=" + protocol
				+ ", className=" + className + ", args=" + Arrays.toString(args)
				+ ", supportsMultipleItems=" + supportsMultipleItems
				+ ", supportsEmptySelection=" + supportsEmptySelection + "]";
	}
}

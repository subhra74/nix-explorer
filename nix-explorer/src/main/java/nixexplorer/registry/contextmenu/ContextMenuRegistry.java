package nixexplorer.registry.contextmenu;

import java.util.ArrayList;
import java.util.List;

public class ContextMenuRegistry {
	private static List<ContextMenuEntry> entryList = new ArrayList<>();

	public static List<ContextMenuEntry> getEntryList() {
		return entryList;
	}

	public static void setEntryList(List<ContextMenuEntry> entries) {
		entryList.addAll(entries);
	}
}

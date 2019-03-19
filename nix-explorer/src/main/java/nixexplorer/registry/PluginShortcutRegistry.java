/**
 * 
 */
package nixexplorer.registry;

import java.util.ArrayList;

/**
 * @author subhro
 *
 */
public class PluginShortcutRegistry {
	private static ArrayList<PluginShortcutEntry> list = new ArrayList<PluginShortcutEntry>();

	/**
	 * @return the list
	 */
	public static ArrayList<PluginShortcutEntry> getList() {
		return list;
	}
}

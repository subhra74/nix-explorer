package nixexplorer.registry;

import java.util.ArrayList;
import java.util.List;

public class PluginRegistry {
	private static PluginRegistry me;

	private PluginRegistry() {

	}

	public static synchronized PluginRegistry getSharedInstance() {
		if (me == null) {
			me = new PluginRegistry();
		}
		return me;
	}

	private List<PluginEntry> pluginList = new ArrayList<>();

	public List<PluginEntry> getPluginList() {
		return pluginList;
	}

	public void setPluginList(List<PluginEntry> pluginList) {
		this.pluginList = pluginList;
	}
}

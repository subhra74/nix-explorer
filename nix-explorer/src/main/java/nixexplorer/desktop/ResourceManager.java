package nixexplorer.desktop;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceManager {
	private static Map<Integer, List<Closeable>> resources = new ConcurrentHashMap<>();

	public static synchronized void register(Integer id, Closeable c) {
		List<Closeable> list = resources.get(id);
		if (list == null) {
			list = new ArrayList<>();
		}
		if (!list.contains(c)) {
			list.add(c);
		}
		resources.put(id, list);
	}

	public static synchronized void unregister(Integer id, Closeable c) {
		List<Closeable> list = resources.get(id);
		if (list != null) {
			if (list.contains(c)) {
				list.remove(c);
			}
		}
	}

	public static synchronized void unregisterAll(Integer id) {
		System.out.println("Closing all connections of: " + id);
		List<Closeable> list = resources.get(id);
		if (list != null) {
			for (Closeable c : list) {
				try {
					System.out.println("Closing: " + c);
					c.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			resources.remove(id);
		}
	}
}

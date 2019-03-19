package nixexplorer.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockedWidgets {
	private static List<Widget> blockedWidgets = Collections
			.synchronizedList(new ArrayList<>());

	public static synchronized void add(Widget w) {
		blockedWidgets.add(w);
	}

	public static synchronized void remove(Widget w) {
		blockedWidgets.remove(w);
	}

	public static synchronized boolean contains(Widget w) {
		return blockedWidgets.contains(w);
	}

	public static synchronized List<Widget> getBlockedWidgets() {
		return blockedWidgets;
	}

}

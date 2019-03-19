package nixexplorer.widgets.folderview;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.event.ListDataListener;

public class NavigationHistory {
	private Deque<String> back = new LinkedList<>(),
			forward = new LinkedList<>();

	public boolean hasPrevElement() {
		return !back.isEmpty();
	}

	public boolean hasNextElement() {
		return !forward.isEmpty();
	}

	public String prevElement() {
		String item = back.pop();
		return item;
	}

	public String nextElement() {
		String item = forward.pop();
		return item;
	}

	public void addBack(String item) {
		back.push(item);
	}

	public void addForward(String item) {
		forward.push(item);
	}
}

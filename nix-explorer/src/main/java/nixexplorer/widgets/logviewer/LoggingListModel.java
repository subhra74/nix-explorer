package nixexplorer.widgets.logviewer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractListModel;

public class LoggingListModel extends AbstractListModel<String> {

	private boolean filter;
	private Pattern pattern;

	private List<TextEntry> list = new ArrayList<>();

	private int size, r = -1, c = -1, count = 0, fcount = 0;

	public LoggingListModel() {
	}

	@Override
	public String getElementAt(int index) {
		return list.get(index).toString();
	}

	@Override
	public int getSize() {
		return list.size();
	}

	public void add(String s) {
		TextEntry text = new TextEntry();
		text.text = s;
		if (this.pattern != null) {
			Matcher matcher = this.pattern.matcher(text.text);
			text.indexes = new ArrayList<>();
			while (matcher.find()) {
				IndexPair pair = new IndexPair();
				pair.start = matcher.start();
				pair.end = matcher.end();
				text.indexes.add(pair);
				count++;
			}
		}
		int index = list.size();
		list.add(text);
		fireIntervalAdded(this, index, index);
	}

	public String getPattern() {
		if (this.pattern != null) {
			return this.pattern.pattern();
		}
		return null;
	}

	public void clearSearch() {
		r = -1;
		c = -1;
		count = 0;
		fcount = 0;

		this.pattern = null;

		for (int i = 0; i < list.size(); i++) {
			TextEntry text = list.get(i);
			text.indexes.clear();
		}

		if (list.size() > 0) {
			fireContentsChanged(this, 0, list.size() - 1);
		}
	}

	public int search(String search) {
		r = -1;
		c = -1;
		count = 0;
		fcount = 0;
		this.pattern = Pattern.compile(search);
		if (this.pattern != null) {
			for (int i = 0; i < list.size(); i++) {
				TextEntry text = list.get(i);
				Matcher matcher = this.pattern.matcher(text.text);
				text.indexes.clear();
				while (matcher.find()) {
					IndexPair pair = new IndexPair();
					pair.start = matcher.start();
					pair.end = matcher.end();
					text.indexes.add(pair);
					if (r == -1 && c == -1) {
						r = i;
						c = 0;
						pair.selected = true;
						fcount = 1;
					}
					count++;
				}
			}
		}
		if (list.size() > 0) {
			fireContentsChanged(this, 0, list.size() - 1);
		}
		return r;
	}

	private int findNextEntry(int index) {
		for (int i = index + 1; i < list.size(); i++) {
			if (list.get(i).indexes.size() > 0) {
				return i;
			}
		}
		return -1;
	}

	private int findPrevEntry(int index) {
		for (int i = index - 1; i >= 0; i--) {
			if (list.get(i).indexes.size() > 0) {
				return i;
			}
		}
		return -1;
	}

	public String getSearchStat() {
		return fcount + "/" + count;
	}

	public int prev() {
		list.get(r).indexes.get(c).selected = false;

		fireContentsChanged(this, r, r);

		c--;
		if (c < 0) {
			r = findPrevEntry(r);
			fcount--;
			if (r == -1) {
				r = findPrevEntry(list.size() - 1);
				fcount = count;
			}
			c = list.get(r).indexes.size() - 1;
		}

		list.get(r).indexes.get(c).selected = true;

		fireContentsChanged(this, r, r);

		return r;
	}

	public int next() {
		list.get(r).indexes.get(c).selected = false;

		fireContentsChanged(this, r, r);

		c++;
		if (list.get(r).indexes.size() == c) {
			c = 0;
			r = findNextEntry(r);
			fcount++;
			if (r == -1) {
				r = findNextEntry(0);
				fcount = 1;
			}
		}

		list.get(r).indexes.get(c).selected = true;

		fireContentsChanged(this, r, r);

		return r;

//		int r = -1, c = -1;
//		outer: for (int i = 0; i < list.size(); i++) {
//			TextEntry text = list.get(i);
//			for (int j = 0; j < text.indexes.size(); j++) {
//				IndexPair p = text.indexes.get(j);
//				if (p.selected) {
//					r = i;
//					c = j;
//					System.out
//							.println("Selectedf found - r: " + r + " c: " + c);
//					break outer;
//				}
//			}
//		}
//		if (r == list.size() - 1 && c == list.get(r).indexes.size() - 1) {
//			r = -1;
//			c = -1;
//		}
//
//		if (c == -1) {
//			for (int i = 0; i < list.size(); i++) {
//				TextEntry text = list.get(i);
//				if (text.indexes.size() > 0) {
//					c = 0;
//					r = i;
//					break;
//				}
//			}
//		} else {
//			c = c + 1;
//			for (; r < list.size(); r++) {
//				TextEntry text = list.get(r);
//				if (c < text.indexes.size()) {
//					break;
//				} else {
//					c = 0;
//				}
//			}
//		}
//
//		for (int i = 0; i < list.size(); i++) {
//			TextEntry text = list.get(i);
//			for (int j = 0; j < text.indexes.size(); j++) {
//				text.indexes.get(j).selected = false;
//			}
//		}
//
//		System.out.println("selected - r:" + r + " c:" + c);
//
//		if (r != -1 && c != -1) {
//			list.get(r).indexes.get(c).selected = true;
//			fireContentsChanged(this, r, r);
//		}
//		return r;
	}

//	public int prev() {
//		int r = -1, c = -1;
//		outer: for (int i = 0; i < list.size(); i++) {
//			TextEntry text = list.get(i);
//			for (int j = 0; j < text.indexes.size(); j++) {
//				IndexPair p = text.indexes.get(j);
//				if (p.selected) {
//					r = i;
//					c = j;
//					System.out
//							.println("Selectedf found - r: " + r + " c: " + c);
//					break outer;
//				}
//			}
//		}
//
//		if (r == -1 || c == -1)
//			return -1;
//
//		c = c - 1;
//		for (; r >= 0;) {
//			if (c >= 0) {
//				break;
//			} else {
//				r--;
//				TextEntry text = list.get(r);
//				c = text.indexes.size();
//			}
//		}
//
//		if (r == -1) {
//			for (int i = list.size() - 1; i >= 0; i--) {
//				TextEntry text = list.get(i);
//				if (text.indexes.size() > 0) {
//					r = i;
//					c = text.indexes.size() - 1;
//					break;
//				}
//			}
//		}
//
//		System.out.println("selected - r:" + r + " c:" + c);
//
//		if (r != -1 && c != -1) {
//			list.get(r).indexes.get(c).selected = true;
//			fireContentsChanged(this, r, r);
//		}
//		return r;
//	}

	public void clear() {
		r = -1;
		c = -1;
		fcount = count = 0;
		int index1 = list.size() - 1;
		this.pattern = null;
		list.clear();
		if (index1 >= 0) {
			fireIntervalRemoved(this, 0, index1);
		}
	}

	class TextEntry {
		String text;
		List<IndexPair> indexes = new ArrayList<>();

		@Override
		public String toString() {
			if (indexes != null && indexes.size() > 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("<html>");
				int prevIndex = 0;
				for (int i = 0; i < indexes.size(); i++) {
					IndexPair pair = indexes.get(i);
					if (prevIndex != pair.start - 1) {
						sb.append(text.substring(prevIndex, pair.start));
					}
					if (pair.selected) {
						sb.append("<span bgcolor='orange'>");
					} else {
						sb.append("<span bgcolor='yellow'>");
					}
					sb.append(text.substring(pair.start, pair.end));
					sb.append("</span>");
					prevIndex = pair.end;
				}

				if (prevIndex != text.length() - 1) {
					sb.append(text.substring(prevIndex));
				}

				sb.append("</html>");
				return sb.toString();
			}
			return text;
		}
	}

	class IndexPair {
		int start, end;
		boolean selected;

		@Override
		public String toString() {
			return "IndexPair [start=" + start + ", end=" + end + "]";
		}
	}

}

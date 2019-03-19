package nixexplorer.widgets.logviewer;

import java.awt.Color;

public class LogLine implements Comparable<LogLine> {
	private String line;
	private byte colorId;
	private boolean searchHighlight;

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public byte getColor() {
		return colorId;
	}

	public void setColor(byte color) {
		this.colorId = color;
	}

	@Override
	public int compareTo(LogLine o) {
		return this.line.compareTo(o.line);
	}

	@Override
	public String toString() {
		return line;
	}

	public boolean isSearchHighlight() {
		return searchHighlight;
	}

	public void setSearchHighlight(boolean searchHighlight) {
		this.searchHighlight = searchHighlight;
	}
}

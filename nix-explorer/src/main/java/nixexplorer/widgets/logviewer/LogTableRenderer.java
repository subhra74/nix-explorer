package nixexplorer.widgets.logviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import nixexplorer.app.settings.AppConfig.LogViewer;
import nixexplorer.widgets.util.Utility;

public class LogTableRenderer extends JLabel
		implements ListCellRenderer<String> {
	private Map<String, Pattern> patternCache;
	private LogViewer config;

	/**
	 * 
	 */
	public LogTableRenderer(LogViewer config) {
		this.config = config;
		patternCache = new HashMap<>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.
	 * JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList<? extends String> list,
			String value, int index, boolean isSelected, boolean cellHasFocus) {
		setText(value);
		if (isSelected) {
			setOpaque(true);
			setBackground(list.getSelectionBackground());
		} else {
			setOpaque(false);
		}
		Color c = getColor(value);
		setForeground(list.getForeground());
		if (c != null) {
			setForeground(c);
		}
		return this;
	}

	private Color getColor(String line) {
		for (LogHighlightEntry ent : config.getHighlightList()) {
			String patternStr = ent.getPattern();

			Pattern p = patternCache.get(patternStr);
			if (p == null) {
				p = Pattern.compile(patternStr);
				patternCache.put(patternStr, p);
			}

			Matcher m = p.matcher(line);
			if (m.find()) {
				return new Color(ent.getColor());
			}
		}
		return null;
	}

}

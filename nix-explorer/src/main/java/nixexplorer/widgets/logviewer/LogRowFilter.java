package nixexplorer.widgets.logviewer;

import java.util.regex.Pattern;

import javax.swing.RowFilter;

public class LogRowFilter extends RowFilter<LoggingTableModel, Integer> {
	private Pattern pattern;

	public LogRowFilter(Pattern pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean include(
			Entry<? extends LoggingTableModel, ? extends Integer> entry) {
		Integer index = entry.getIdentifier();
		String text = ((LogLine) entry.getModel().getValueAt(index, 0))
				.getLine();
		try {
			return pattern.matcher(text).matches();
		} catch (Exception e) {
		}

		return false;
	}

}

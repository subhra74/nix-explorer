package nixexplorer.widgets.util;

import java.awt.Font;
import java.awt.Toolkit;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import nixexplorer.Constants;

public final class Utility {
	private static float dpiScale;
	private static Font smallFont, normatFont, largeFont;

	public static final int toPixel(int value) {
		if (dpiScale == 0.0f) {
			int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
			dpiScale = dpi / 96.0f;
		}
		return (int) (value * dpiScale);
	}

	public static final long toEpochMilli(LocalDateTime dateTime) {
		return dateTime.atZone(ZoneId.systemDefault()).toInstant()
				.toEpochMilli();
	}
	
	public static final String formatDate(LocalDateTime dateTime) {
		return dateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"));
	}

	public static final LocalDateTime toDateTime(long epochMilli) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli),
				ZoneId.systemDefault());
	}

	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
				+ (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static Font getFont(int font) {
		switch (font) {
		case Constants.LARGE:
			if (largeFont == null) {
				largeFont = new Font(Font.DIALOG, Font.PLAIN,
						toPixel(Constants.LARGE));
			}
			return largeFont;
		case Constants.NORMAL:
			if (normatFont == null) {
				normatFont = new Font(Font.DIALOG, Font.PLAIN,
						toPixel(Constants.NORMAL));
			}
			return normatFont;
		default:
			if (smallFont == null) {
				smallFont = new Font(Font.DIALOG, Font.PLAIN,
						toPixel(Constants.SMALL));
			}
			return smallFont;
		}
	}
}

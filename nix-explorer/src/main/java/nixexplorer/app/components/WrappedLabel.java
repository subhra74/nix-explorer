package nixexplorer.app.components;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

public class WrappedLabel extends JLabel {

	private List<StringBuilder> wrappedText = new ArrayList<>();

	public WrappedLabel() {
	}

	public WrappedLabel(String text) {
		super(text);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		System.out.println("Text: " + getText());
		super.setBounds(x, y, width, height);
		this.wrappedText = getWrappedText(getText());
		System.out.println("Wrapped text: " + this.wrappedText);
	}

	private List<StringBuilder> getWrappedText(String text) {
		List<StringBuilder> list = new ArrayList<>();
		if (text == null || text.length() < 1) {
			return list;
		}
		FontMetrics frc = getFontMetrics(getFont());
		int y = 0;
		int x = 0;
		char[] chars = text.toCharArray();
		StringBuilder sb = new StringBuilder();
		list.add(sb);
		for (int i = 0; i < chars.length; i++) {
			char ch = chars[i];
			int charWidth = frc.charWidth(ch);
			sb.append(ch);
			if (x + charWidth > getWidth()) {
				y += frc.getHeight();
				x = 0;

				if (y + frc.getHeight() > getHeight()) {
					if (sb.length() > 3) {
						sb.setCharAt(sb.length() - 1, '.');
						sb.setCharAt(sb.length() - 2, '.');
						sb.setCharAt(sb.length() - 3, '.');
					}
					break;
				}

				sb = new StringBuilder();
				list.add(sb);
			}
			x += charWidth;
		}
		return list;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		FontMetrics frc = getFontMetrics(getFont());
		int y = frc.getAscent();
		for (StringBuilder sb : wrappedText) {
			g2.drawString(sb.toString(),
					getWidth() / 2 - frc.stringWidth(sb.toString()) / 2, y);
			y += frc.getHeight();
		}
		g2.dispose();
	}
}

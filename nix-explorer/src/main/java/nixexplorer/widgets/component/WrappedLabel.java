package nixexplorer.widgets.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.swing.JComponent;
import javax.swing.UIManager;

public class WrappedLabel extends JComponent {
	private String text;
	private AttributedString attributedString;

	public WrappedLabel() {
		this("");
	}

	public WrappedLabel(String text) {
		setText(text);
	}

	public void setText(String text) {
		this.text = text;
		if (text.length() > 0) {
			attributedString = new AttributedString(this.text);
			attributedString.addAttribute(TextAttribute.FONT,
					(Font) UIManager.get("Label.font"));
			setForeground((Color) UIManager.get("Label.foreground"));
			revalidate();
			repaint();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (text.length() > 0) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			int width = getSize().width;
			int x = 0;
			int y = 0;
			

			AttributedCharacterIterator characterIterator = attributedString
					.getIterator();
			FontRenderContext fontRenderContext = g2d.getFontRenderContext();
			LineBreakMeasurer measurer = new LineBreakMeasurer(
					characterIterator, fontRenderContext);
			while (measurer.getPosition() < characterIterator.getEndIndex()) {
				TextLayout textLayout = measurer.nextLayout(width);
				
				int stringWidth = (int) textLayout.getBounds().getWidth();
						

				if (width > stringWidth) {
					x = (width / 2) - (stringWidth / 2);
				}
				
				y += textLayout.getAscent();
				textLayout.draw(g2d, x, y);
				y += textLayout.getDescent() + textLayout.getLeading();
			}
		}
	}
}

package nixexplorer.command;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.RepaintManager;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import nixexplorer.drawables.icons.ScaledIcon;

import static nixexplorer.widgets.util.Utility.toPixel;;

public class HostRenderer extends JLabel
		implements ListCellRenderer<TermHolder> {

	public HostRenderer() {
		setIconTextGap(toPixel(5));
		setHorizontalTextPosition(JLabel.CENTER);
		setVerticalTextPosition(JLabel.BOTTOM);
		setHorizontalAlignment(JLabel.CENTER);
		setIcon(new ScaledIcon(
				HostRenderer.class.getResource("/images/terminal-icon.png"),
				toPixel(256), toPixel(200)));
		setBackground(UIManager.getColor("ToggleButton.select"));
		setBorder(new EmptyBorder(toPixel(5), toPixel(5), toPixel(5),
				toPixel(5)));
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends TermHolder> list, TermHolder value, int index,
			boolean isSelected, boolean cellHasFocus) {
		setText(value + "");
		setOpaque(isSelected);
		if (value.getIcon() != null) {
			setIcon(value.getIcon());
		}

//		try {
//
//			//value.getTerm().setSize(640, 480);
//			System.out.println(value.getTerm().getSize());
//			BufferedImage img = new BufferedImage(640, 480,
//					BufferedImage.TYPE_INT_ARGB);
//			Graphics g = img.createGraphics();
//			value.getTerm().print(g);
//			System.out.println(img);
//			//Image img2 = img.getScaledInstance(48, 48, Image.SCALE_FAST);
//			setIcon(new ImageIcon(img));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

//		if (value.getTerm().isSessionRunning() && value.getTty().isBusy()) {
//			System.out.println("Session running " + value);
//		}
		return this;
	}

}

/**
 * 
 */
package nixexplorer.skin;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.RepaintManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

/**
 * @author subhro
 *
 */
public class FlatTextFieldUI extends BasicTextFieldUI {

	private static JPopupMenu popup(JTextField c) {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem mCut = new JMenuItem("Cut");
		JMenuItem mCopy = new JMenuItem("Copy");
		JMenuItem mPaste = new JMenuItem("Paste");
		JMenuItem mSelect = new JMenuItem("Select all");

		popup.add(mCut);
		popup.add(mCopy);
		popup.add(mPaste);
		popup.add(mSelect);

		mCut.addActionListener(e -> {
			c.cut();
		});

		mCopy.addActionListener(e -> {
			c.copy();
		});

		mPaste.addActionListener(e -> {
			c.paste();
		});

		mSelect.addActionListener(e -> {
			c.selectAll();
		});

		return popup;
	}

	public static ComponentUI createUI(JComponent c) {
		if (c instanceof JTextField) {
			c.putClientProperty("flat.popup", popup((JTextField) c));
			c.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON3
							|| e.isPopupTrigger()) {
						JPopupMenu pop = (JPopupMenu) c
								.getClientProperty("flat.popup");
						if (pop != null) {
							pop.show(c, e.getX(), e.getY());
						}
					}
				}
			});
		}
		return new FlatTextFieldUI();
	}
}

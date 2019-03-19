/**
 * 
 */
package nixexplorer.widgets.component;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import static nixexplorer.widgets.util.Utility.toPixel;

/**
 * @author subhro
 *
 */
public class FencedPanel extends JPanel {
	private Insets borderInsets;
	private int x, y;

	/**
	 * 
	 */
	public FencedPanel() {
		setLayout(new BorderLayout());
		borderInsets = new Insets(toPixel(5), toPixel(5), toPixel(5),
				toPixel(5));

		JLabel toplLabel = new JLabel();
		toplLabel.setOpaque(true);
		toplLabel.setBackground(Color.green);
		toplLabel.setText("Sample fence");
		toplLabel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		add(toplLabel, BorderLayout.NORTH);

		JLabel bottomlLabel = new JLabel();
		bottomlLabel.setOpaque(true);
		bottomlLabel.setBackground(Color.green);
		bottomlLabel.setPreferredSize(new Dimension(5, 5));
		bottomlLabel.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
		add(bottomlLabel, BorderLayout.SOUTH);

		toplLabel.addMouseListener(new MouseAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.
			 * MouseEvent)
			 */
			@Override
			public void mousePressed(MouseEvent e) {
				x = e.getX() - getLocation().x;
				y = e.getY() - getLocation().y;
			}
		});

		toplLabel.addMouseMotionListener(new MouseAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.MouseAdapter#mouseDragged(java.awt.event.
			 * MouseEvent)
			 */
			@Override
			public void mouseDragged(MouseEvent e) {
				int x1 = e.getX() - x;
				int y1 = e.getY() - y;
				setLocation(x1, y1);
			}
		});

		bottomlLabel.addMouseMotionListener(new MouseAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.MouseAdapter#mouseDragged(java.awt.event.
			 * MouseEvent)
			 */
			@Override
			public void mouseDragged(MouseEvent e) {
				int y = e.getYOnScreen() - getLocationOnScreen().y;
				if (y > 0) {
					Dimension d = getSize();
					setSize(d.width, y);
					validate();
					RepaintManager.currentManager(
							(JComponent) getParent()/* FencedPanel.this */)
							.addDirtyRegion((JComponent) getParent(), getX(),
									getY(), getWidth(), getHeight());
					RepaintManager.currentManager((JComponent) getParent())// FencedPanel.this)
							.paintDirtyRegions();
				}
			}
		});

//		setBorder(new Border() {
//			@Override
//			public void paintBorder(Component c, Graphics g, int x, int y,
//					int width, int height) {
//
//			}
//
//			@Override
//			public boolean isBorderOpaque() {
//				return false;
//			}
//
//			@Override
//			public Insets getBorderInsets(Component c) {
//				return borderInsets;
//			}
//		});
//
//		addMouseListener(new MouseAdapter() {
//		});
//
//		addMouseMotionListener(new MouseAdapter() {
//		});
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(getBackground());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setComposite(
				AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
		super.paintComponent(g2d);

	}

	@Override
	protected void paintChildren(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(getBackground());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setComposite(
				AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.7f));

		super.paintChildren(g);
	}

//	/* (non-Javadoc)
//	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
//	 */
//	@Override
//	protected void paintComponent(Graphics g) {
//		super.paintComponent(g);
//	}
}

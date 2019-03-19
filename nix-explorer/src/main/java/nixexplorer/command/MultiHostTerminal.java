/**
 * 
 */
package nixexplorer.command;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;

import nixexplorer.app.session.SessionManagerPanel;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.core.ssh.SshTtyConnector;
import nixexplorer.widgets.console.CustomJediterm;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class MultiHostTerminal extends JDialog {
	private DefaultListModel<TermHolder> termModel;
	private JList<TermHolder> termList;
	private JTextField txtCmd;
	private JButton run, add, del;
	private JComboBox<PanelHolder> cmbTerms;

	/**
	 * 
	 */
	public MultiHostTerminal() {
		termModel = new DefaultListModel<>();
		termList = new JList<TermHolder>(termModel);
		termList.setCellRenderer(new HostRenderer());
		termList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		termList.setFixedCellHeight(Utility.toPixel(256));
		termList.setFixedCellWidth(Utility.toPixel(256));
		termList.setVisibleRowCount(-1);

		JScrollPane jsp = new JScrollPane(termList);
		jsp.setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));

		JPanel pc = new JPanel(new BorderLayout());
		cmbTerms = new JComboBox<>(
				new PanelHolder[] { new PanelHolder(jsp, "Overview") });
		pc.add(cmbTerms, BorderLayout.NORTH);
		JPanel cc = new JPanel(new BorderLayout());
		pc.add(cc);
		cmbTerms.addActionListener(e -> {
			cc.removeAll();
			cc.add(((PanelHolder) cmbTerms.getSelectedItem()).getPanel());
			cc.revalidate();
			cc.repaint();
		});

		add(pc, BorderLayout.CENTER);

		JPanel top = new JPanel(new BorderLayout());
		top.add(new JLabel("Command"), BorderLayout.WEST);
		txtCmd = new JTextField(30);
		run = new JButton("Run");
//		run.addActionListener(e -> {
//			for (int i = 0; i < termModel.size(); i++) {
//				TermHolder holder = termModel.get(i);
//				if (holder.getTty() == null) {
//					SshTtyConnector tty = new SshTtyConnector(holder.getInfo(),
//							txtCmd.getText() + "\n");
//					holder.getTerm().setTtyConnector(tty);
//					// holder.setTerm(term);
//					holder.setTty(tty);
//					holder.getTerm().start();
//				} else {
//					try {
//						holder.getTty().write(txtCmd.getText() + "\n");
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
//				}
//			}
//		});
		top.add(txtCmd);
		top.add(run, BorderLayout.EAST);
		add(top, BorderLayout.NORTH);

		Box b1 = Box.createHorizontalBox();
		b1.add(Box.createHorizontalGlue());
		add = new JButton("Add server");
		add.addActionListener(e -> {
			SessionInfo info = new SessionManagerPanel().newSession();
			if (info != null) {
				CustomJediterm term = new CustomJediterm(
						new DefaultSettingsProvider());
				term.setSize(new Dimension(640, 480));
				TermHolder h = new TermHolder();
				h.setInfo(info);
				h.setTerm(term);
				termModel.addElement(h);
				cmbTerms.addItem(new PanelHolder(term, h.toString()));
			}
		});
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		del = new JButton("Remove");
		b1.add(add);
		b1.add(del);
		add(b1, BorderLayout.SOUTH);

		((JComponent) getContentPane()).setBorder(
				new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
						Utility.toPixel(5), Utility.toPixel(5)));

		cmbTerms.setSelectedIndex(0);

		Timer t = new Timer(1000, e -> {
			if (cmbTerms.getSelectedIndex() != 0) {
				System.out.println("already visible");
				return;
			}
			try {
				for (int i = 1; i < cmbTerms.getItemCount(); i++) {
					PanelHolder holder = cmbTerms.getItemAt(i);
					holder.getPanel().setBounds(0, 0, Utility.toPixel(640),
							Utility.toPixel(480));
					BufferedImage img = new BufferedImage(Utility.toPixel(640),
							Utility.toPixel(480), BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2 = img.createGraphics();
					JComponent jc = ((JediTermWidget) holder.getPanel())
							.getTerminalPanel();
					jc.setBounds(0, 0, Utility.toPixel(640),
							Utility.toPixel(480));
					jc.print(g2);
					g2.dispose();
					Image img2 = img.getScaledInstance(Utility.toPixel(250),
							Utility.toPixel(200), Image.SCALE_SMOOTH);
					TermHolder h = termModel.get(i - 1);
					h.setIcon(new ImageIcon(img2));
					img.flush();
				}
				termList.revalidate();
				termList.repaint();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
//			if (!isVisible()) {
//				return;
//			}
//
//			PanelHolder h = cmbTerms.getItemAt(1);
//
//			h.getPanel().setBounds(0, 0, 400, 300);
//			// btn.setText(UUID.randomUUID() + "");
//			BufferedImage img = new BufferedImage(400, 300,
//					BufferedImage.TYPE_INT_ARGB);
//			Graphics2D g2 = img.createGraphics();
//
//			JComponent jc = ((JediTermWidget) h.getPanel()).getTerminalPanel();
//			jc.setBounds(0, 0, 400, 300);
//			g2.setColor(Color.RED);
//			g2.fillRect(0, 0, 400, 300);
//			jc.print(g2);
////			termModel.getElementAt(0).getTerm().setSize(400, 300);
////			termModel.getElementAt(0).getTerm().setVisible(true);
////			termModel.getElementAt(0).getTerm().print(g2);
//			g2.dispose();
//			System.out.println(
//					"icon set " + SwingUtilities.isEventDispatchThread());
//			lblImg.setIcon(new ImageIcon(img));
		});
		t.setInitialDelay(5000);
		t.start();

//		Timer t = new Timer(1000, e -> {
//			try {
//				for (int i = 0; i < termModel.size(); i++) {
//					TermHolder holder = termModel.get(i);
////					if(!holder.getTerm().isVisible()) {
////						holder.getTerm().setSize(400, 300);
////					}
//					btn.setText("hellow"+System.currentTimeMillis());
//					BufferedImage img = new BufferedImage(400, 300,
//							BufferedImage.TYPE_INT_ARGB);
//					Graphics2D g = img.createGraphics();
//					btn.print(g);
//					g.dispose();
//					lblImg.setIcon(new ImageIcon(img));
//					System.out.println(img);
//				}
//			} catch (Exception e2) {
//				e2.printStackTrace();
//			}
//
//		});
//		t.start();
	}
}

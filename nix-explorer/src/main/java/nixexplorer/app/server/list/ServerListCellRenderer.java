/**
 * 
 */
package nixexplorer.app.server.list;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import nixexplorer.app.session.AppSessionImpl;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.widgets.util.Utility;

/**
 * @author subhro
 *
 */
public class ServerListCellRenderer extends JPanel
		implements ListCellRenderer<AppSessionImpl> {
	private JLabel icon, title, desc;

	/**
	 * 
	 */
	public ServerListCellRenderer() {
		icon = new JLabel(UIManager.getIcon("ServerList.offlineIcon"));
		title = new JLabel();
		desc = new JLabel();
		desc.setForeground(UIManager.getColor("Panel.highlight"));
		title.setFont(new Font(Font.DIALOG, Font.PLAIN, Utility.toPixel(14)));

		setOpaque(false);

		setBackground(UIManager.getColor("Button.highlight"));

		JPanel p1 = new JPanel(new BorderLayout());
		p1.setOpaque(false);
		p1.add(title);
		p1.add(desc, BorderLayout.SOUTH);

		int border = Utility.toPixel(8);

		setLayout(new BorderLayout(border, border));
		add(icon, BorderLayout.WEST);
		add(p1);

		setBorder(new EmptyBorder(border, border, border, border));

		setPreferredSize(new Dimension(
				Math.max(getPreferredSize().width, Utility.toPixel(200)),
				getPreferredSize().height));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.
	 * JList, java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(
			JList<? extends AppSessionImpl> list, AppSessionImpl value, int index,
			boolean isSelected, boolean cellHasFocus) {
		setOpaque(isSelected);
		this.title.setText(value.getSession().getName());
		this.desc.setText(value.getSession().getHost());
		desc.setForeground(isSelected ? UIManager.getColor("Label.foreground")
				: UIManager.getColor("Panel.highlight"));
		return this;
	}

}

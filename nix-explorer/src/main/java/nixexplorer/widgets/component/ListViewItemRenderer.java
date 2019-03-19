package nixexplorer.widgets.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class ListViewItemRenderer extends JPanel
		implements ListCellRenderer<ListViewItem> {
	private Icon icon;

	private JLabel lblIcon;
	private WrappedLabel lblText;

	public ListViewItemRenderer(int width, int iconHeight, int textHeight) {
		setLayout(new BorderLayout());
		lblIcon = new JLabel();
		lblIcon.setVerticalAlignment(JLabel.BOTTOM);
		// lblIcon.setPreferredSize(new Dimension(width, iconHeight));
		lblText = new WrappedLabel();
		lblText.setOpaque(false);
		lblText.setPreferredSize(new Dimension(width, textHeight));
		add(lblIcon);
		add(lblText, BorderLayout.SOUTH);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends ListViewItem> list, ListViewItem value, int index,
			boolean isSelected, boolean cellHasFocus) {
		lblIcon.setIcon(value.getIcon());
		lblText.setText(value.getName());

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			lblText.setForeground(value.getSelectedForeground());
			setOpaque(true);
		} else {
			setBackground(list.getBackground());
			lblText.setForeground(value.getForeground());
			setOpaque(false);
		}
		return this;
	}

}

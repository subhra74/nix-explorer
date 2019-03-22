package nixexplorer.widgets.folderview;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import nixexplorer.app.components.FileIcon;
import nixexplorer.app.components.WrappedLabel;
import nixexplorer.core.FileInfo;
import nixexplorer.core.FileType;
import nixexplorer.drawables.icons.ScaledIcon;
import nixexplorer.widgets.util.Utility;

public class ListViewRenderer extends JPanel
		implements ListCellRenderer<FileInfo> {

	private JLabel lblText;

	private FileIcon folderIcon, fileIcon;

	public ListViewRenderer() {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		lblText = new JLabel();
		lblText.setHorizontalAlignment(JLabel.CENTER);
		lblText.setVerticalAlignment(JLabel.CENTER);
		lblText.setHorizontalTextPosition(JLabel.CENTER);
		lblText.setVerticalTextPosition(JLabel.BOTTOM);

		folderIcon = new FileIcon(
				new ScaledIcon(getClass().getResource("/images/local.png"),
						Utility.toPixel(48), Utility.toPixel(48)),
				false);
		add(lblText);

		fileIcon = new FileIcon(
				new ScaledIcon(getClass().getResource("/images/fileicon.png"),
						Utility.toPixel(48), Utility.toPixel(48)),
				false);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends FileInfo> list, FileInfo value, int index,
			boolean isSelected, boolean cellHasFocus) {
		lblText.setIcon(
				FolderViewUtility.getIconForFile(value, folderIcon, fileIcon));
		if (isSelected) {
			setBackground(list.getSelectionBackground());
		} else {
			setBackground(list.getBackground());
		}
		lblText.setText(value.getName());
		return this;
	}

}

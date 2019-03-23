package nixexplorer.widgets.folderview;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import nixexplorer.core.FileInfo;
import nixexplorer.core.FileType;
import nixexplorer.widgets.util.Utility;

public class ListViewRenderer extends JPanel
		implements ListCellRenderer<FileInfo> {

	private JLabel lblText;

	// private FileIcon folderIcon, fileIcon;

	public ListViewRenderer() {
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5)));
		lblText = new JLabel();
		lblText.setHorizontalAlignment(JLabel.CENTER);
		lblText.setVerticalAlignment(JLabel.CENTER);
		lblText.setHorizontalTextPosition(JLabel.CENTER);
		lblText.setVerticalTextPosition(JLabel.BOTTOM);

//		folderIcon = new FileIcon(
//				new ScaledIcon(getClass().getResource("/images/local.png"),
//						Utility.toPixel(48), Utility.toPixel(48)),
//				false);
		add(lblText);

//		fileIcon = new FileIcon(
//				new ScaledIcon(getClass().getResource("/images/fileicon.png"),
//						Utility.toPixel(48), Utility.toPixel(48)),
//				false);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends FileInfo> list, FileInfo value, int index,
			boolean isSelected, boolean cellHasFocus) {
		String text = "<html>" + value.getName()
				+ (value.getType() == FileType.File
						|| value.getType() == FileType.FileLink
								? "<br>Size: " + Utility.humanReadableByteCount(
										value.getSize(), true)
								: "")
				+ "<br>Modified: " + Utility.formatDate(value.getLastModified())
				+ (value.getType() == FileType.FileLink
						|| value.getType() == FileType.DirLink
								? "<br>Shortcut / link"
								: "")
				+ (FolderViewUtility.isExecutable(value.getPermission())
						? "<br>Executable file"
						: "")
				+ "</html>";
		list.setToolTipText(text);
		lblText.setIcon(FolderViewUtility.getIconForFile(value, true));
		if (isSelected) {
			setBackground(list.getSelectionBackground());
		} else {
			setBackground(list.getBackground());
		}
		lblText.setText(value.getName());
		return this;
	}

}

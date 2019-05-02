package nixexplorer.widgets.folderview;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import nixexplorer.core.FileInfo;
import nixexplorer.core.FileType;
import nixexplorer.widgets.util.Utility;

public class ListViewRenderer extends JPanel
		implements ListCellRenderer<FileInfo> {

	private JLabel lblTitle, lblIcon, lblDescBottomLeft, lblDescBottomRight;

	private static final Border BORDER_NORMAL = new CompoundBorder(
			new MatteBorder(0, Utility.toPixel(0), Utility.toPixel(1),
					Utility.toPixel(0),
					UIManager.getColor("DefaultBorder.color")),
			new EmptyBorder(Utility.toPixel(5), Utility.toPixel(5),
					Utility.toPixel(5), Utility.toPixel(5))),
			BORDER_LAST = new MatteBorder(0, 0, Utility.toPixel(1), 0,
					UIManager.getColor("List.foreground"));

	// private FileIcon folderIcon, fileIcon;

	public ListViewRenderer() {
		setLayout(new BorderLayout());
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setOpaque(false);
		mainPanel.setBorder(new EmptyBorder(Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5), Utility.toPixel(5)));

		lblIcon = new JLabel();
		mainPanel.add(lblIcon, BorderLayout.WEST);

		Box titlePanel = Box.createVerticalBox();// new JPanel(new
													// BorderLayout());
		titlePanel.setOpaque(false);
		Box hBox = Box.createHorizontalBox();

		lblDescBottomLeft = new JLabel();
		lblDescBottomLeft.setFont(
				new Font(Font.DIALOG, Font.PLAIN, Utility.toPixel(12)));
		lblDescBottomLeft.setVerticalAlignment(JLabel.TOP);
		lblDescBottomLeft.setVerticalTextPosition(JLabel.TOP);
		lblDescBottomRight = new JLabel();
		lblDescBottomRight.setFont(
				new Font(Font.DIALOG, Font.PLAIN, Utility.toPixel(12)));
		lblDescBottomRight.setVerticalTextPosition(JLabel.TOP);
		lblDescBottomRight.setVerticalAlignment(JLabel.TOP);

		hBox.add(lblDescBottomLeft);
		hBox.add(Box.createHorizontalGlue());
		hBox.add(lblDescBottomRight);

		lblTitle = new JLabel();
		lblTitle.setFont(
				new Font(Font.DIALOG, Font.BOLD, Utility.toPixel(14)));
		lblTitle.setAlignmentX(Box.LEFT_ALIGNMENT);
		hBox.setAlignmentX(Box.LEFT_ALIGNMENT);
//		lblTitle.setHorizontalAlignment(JLabel.CENTER);
		// lblTitle.setVerticalAlignment(JLabel.BOTTOM);
//		lblTitle.setHorizontalTextPosition(JLabel.CENTER);
		// lblTitle.setVerticalTextPosition(JLabel.BOTTOM);

//		folderIcon = new FileIcon(
//				new ScaledIcon(getClass().getResource("/images/local.png"),
//						Utility.toPixel(48), Utility.toPixel(48)),
//				false);
		titlePanel.add(Box.createVerticalGlue());
		titlePanel.add(lblTitle);
		titlePanel.add(hBox, BorderLayout.SOUTH);
		titlePanel.add(Box.createVerticalGlue());
		titlePanel.setBorder(new EmptyBorder(Utility.toPixel(5),
				Utility.toPixel(5), Utility.toPixel(5), Utility.toPixel(5)));
		mainPanel.add(titlePanel);
		add(mainPanel);
		setBorder(BORDER_NORMAL);

//		fileIcon = new FileIcon(
//				new ScaledIcon(getClass().getResource("/images/fileicon.png"),
//						Utility.toPixel(48), Utility.toPixel(48)),
//				false);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends FileInfo> list, FileInfo value, int index,
			boolean isSelected, boolean cellHasFocus) {
		String text = (value.getType() == FileType.File
				|| value.getType() == FileType.FileLink
						? Utility.humanReadableByteCount(value.getSize(), true)
						: "")
				+ "Modified: " + Utility.formatDate(value.getLastModified());
		list.setToolTipText(text);
		lblIcon.setIcon(FolderViewUtility.getIconForFile(value, true));
		lblTitle.setText(value.getName());
		lblDescBottomLeft.setText(text);
		lblDescBottomRight.setText(value.getPermissionString());
		if (isSelected) {
			setBackground(list.getSelectionBackground());
		} else {
			setBackground(list.getBackground());
		}
		lblTitle.setText(value.getName());
		return this;
	}

}

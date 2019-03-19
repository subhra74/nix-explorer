//package nixexplorer.widgets.folderview;
//
//import java.awt.BorderLayout;
//import java.awt.Dimension;
//
//import javax.swing.Box;
//import javax.swing.JButton;
//import javax.swing.JInternalFrame;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JTextField;
//import javax.swing.border.EmptyBorder;
//
//import nixexplorer.TextHolder;
//import nixexplorer.core.FileSystemProvider;
//import nixexplorer.app.session.SessionInfo;
//import nixexplorer.core.ssh.SshWrapper;
//import nixexplorer.widgets.Widget;
//import nixexplorer.widgets.util.Utility;
//
//public class FolderBrowserDialog extends Widget {
//	private static final long serialVersionUID = 4128106354134809745L;
//	private FileSystemProvider fs;
//	private FolderViewWidget folderBrowser;
//	private String path;
//	private JButton btnOk, btnClose;
//	private Mode mode;
//	private JTextField txtSelectedPath;
//	private SelectionCallback selectionCallback;
//
//	public enum Mode {
//		FileOpen, FolderOpen, FileSave
//	}
//
//	public FolderBrowserDialog(FileSystemProvider fs, String path, Mode mode) {
//		super();
//		this.fs = fs;
//		this.path = path;
//		this.mode = mode;
//		this.setLayout(new BorderLayout());
//		setPreferredSize(new Dimension(Utility.toPixel(350), Utility.toPixel(250)));
//		folderBrowser = new FolderViewWidget(fs, path, null);
//		folderBrowser.setSelectionCallback(s -> {
//			txtSelectedPath.setText(s);
//		});
//		add(folderBrowser);
//		folderBrowser.setEmbedded(true);
//		setBorder(new EmptyBorder(Utility.toPixel(10), Utility.toPixel(10), Utility.toPixel(10), Utility.toPixel(10)));
//
//		putClientProperty("location.center", "true");
//		putClientProperty("frame.dialog", "true");
//
//		if (mode == Mode.FolderOpen) {
//			folderBrowser.setDirOnly(true);
//		}
//
//		Box b2 = Box.createVerticalBox();
//
//		Box b3 = Box.createHorizontalBox();
//		b3.add(new JLabel(TextHolder.getString("folderview.selected")));
//		txtSelectedPath = new JTextField(30);
//		txtSelectedPath.setMaximumSize(txtSelectedPath.getPreferredSize());
//		b3.add(txtSelectedPath);
//		b2.add(b3);
//
//		String okText = mode == Mode.FileSave ? TextHolder.getString("folderview.save")
//				: TextHolder.getString("folderview.select");
//
//		btnOk = new JButton(okText);
//		btnOk.addActionListener(e -> {
//			String text = txtSelectedPath.getText();
//			if (text.length() < 1) {
//				return;
//			}
//			if (selectionCallback != null) {
//				selectionCallback.onPathSelection(text);
//				getFrame().dispose();
//			}
//		});
//		btnClose = new JButton(TextHolder.getString("folderview.cancel"));
//		btnClose.addActionListener(e -> {
//			if (selectionCallback != null) {
//				selectionCallback.onPathSelection(null);
//				getFrame().dispose();
//			}
//		});
//		Box b1 = Box.createHorizontalBox();
//		b1.add(Box.createHorizontalGlue());
//		b1.add(btnOk);
//		b1.add(btnClose);
//		b2.add(b1);
//		add(b2, BorderLayout.SOUTH);
//	}
//
//	@Override
//	public String getTitleText() {
//		return "Folder browser";
//	}
//
//	@Override
//	public void reconnect() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void close() {
//		// TODO Auto-generated method stub
//
//	}
//
//	public void setSelectionCallback(SelectionCallback selectionCallback) {
//		this.selectionCallback = selectionCallback;
//	}
//}

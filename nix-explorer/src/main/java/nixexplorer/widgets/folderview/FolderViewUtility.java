package nixexplorer.widgets.folderview;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import nixexplorer.PathUtils;
import nixexplorer.TextHolder;
import nixexplorer.core.FileInfo;
import nixexplorer.core.FileType;
import nixexplorer.app.components.FileIcon;
import nixexplorer.app.session.SessionInfo;
import nixexplorer.widgets.DuplicatePromptDialog;
import nixexplorer.widgets.Widget;

public class FolderViewUtility {
	public static final Icon getIconForFile(FileInfo value, FileIcon folderIcon,
			FileIcon fileIcon) {
		FileIcon icon = value.getType() == FileType.Directory
				|| value.getType() == FileType.DirLink ? folderIcon : fileIcon;
		icon.setShowingLinkArrow(value.getType() == FileType.DirLink
				|| value.getType() == FileType.FileLink);
		return icon;
	}

	public static String getFilePath(JTree tree, TreePath path,
			boolean isLocal) {
		if (path == null) {
			return null;
		}

		System.out.println("Treepath: " + path);

		StringBuilder sb = new StringBuilder();
		boolean slashAdded = false;
		if (!isLocal) {
			sb.append("/");
			slashAdded = true;
		}
		if (isLocal && File.separatorChar == '\\') {
			slashAdded = true;
		}
		for (Object obj : path.getPath()) {
			if (tree.getModel().getRoot() == obj) {
				if (isLocal && File.separatorChar == '/') {
					sb.append("/");
					slashAdded = true;
				}
				continue;
			}
			DefaultMutableTreeNode n = (DefaultMutableTreeNode) obj;
			if (!slashAdded) {
				sb.append((isLocal ? File.separator : "/"));
			}
			sb.append(n.getUserObject());
			slashAdded = false;
		}
		System.out.println("selected path: " + sb);

		return sb.toString();
	}

	public static boolean sameSession(SessionInfo info1, SessionInfo info2) {
		if (info1 == null) {
			return (info2 == null);
		} else {
			return info1.getHost().equals(info2.getHost())
					&& info1.getPort() == info2.getPort();
		}
	}

	public static String autoRename(String name, List<FileInfo> list) {
		String newName = name;
		boolean moreCheck = true;
		while (moreCheck) {
			newName = "copy_of_" + newName;
			moreCheck = false;
			for (int i = 0; i < list.size(); i++) {
				FileInfo info = list.get(i);
				String n = info.getName();
				if (n.equals(newName)) {
					moreCheck = true;
					break;
				}
			}
		}
		return newName;
	}

	public static int promptDuplicate(String text) {
		return DuplicatePromptDialog.selectDuplicateAction(text);
//		DuplicatePromptDialog dlg = new (text);
//		dlg.showDialog(w);
//		if (dlg.isApproved()) {
//			return dlg.getAction();
//		}
//		return -1;
//		JCheckBox chkApplyAction = new JCheckBox(
//				TextHolder.getString("duplicate.apply"));
//		chkApplyAction.addActionListener(e -> {
//			applyPreviousAction = chkApplyAction.isSelected();
//		});
//
//		return JOptionPane.showInternalOptionDialog(this,
//				new Object[] {
//						String.format(TextHolder.getString("duplicate.prompt"),
//								fileName),
//						chkApplyAction },
//				TextHolder.getString("duplicate.confirm"),
//				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
//				new Object[] { TextHolder.getString("duplicate.overwrite"),
//						TextHolder.getString("duplicate.skip"),
//						TextHolder.getString("duplicate.rename"),
//						TextHolder.getString("duplicate.cancel") },
//				null);
	}

	private static List<String> findDuplicates(List<String> sourceFiles,
			List<FileInfo> list) {
		List<String> dupFileList = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			String name1 = list.get(i).getName();

			for (int j = 0; j < sourceFiles.size(); j++) {
				String file = sourceFiles.get(j);
				String name2 = PathUtils.getFileName(file);
				if (name1.equals(name2)) {
					dupFileList.add(file);
				}
			}
		}
		return dupFileList;
	}

	public static boolean prepareFileList(String targetFolder,
			List<String> sourceFiles, Map<String, String> mvMap, boolean local,
			List<FileInfo> list) {
		int resp = -1;
		List<String> dupFileList = findDuplicates(sourceFiles, list);

		if (dupFileList.size() > 0) {
			resp = promptDuplicate(String.join("\n", dupFileList));

			if (resp == -1) {// cancel
				return false;
			}
			if (resp == 2) {// skip
				sourceFiles.removeAll(dupFileList);
			}
		}

		for (String file : sourceFiles) {
			String name = PathUtils.getFileName(file);

			mvMap.put(file, PathUtils.combine(targetFolder, name,
					local ? File.separator : "/"));
		}

		for (String file : dupFileList) {
			String name = PathUtils.getFileName(file);
			if (resp == 0) {
				// auto rename
				name = FolderViewUtility.autoRename(name, list);
			}
			mvMap.put(file, PathUtils.combine(targetFolder, name,
					local ? File.separator : "/"));
		}

		return true;
	}

}

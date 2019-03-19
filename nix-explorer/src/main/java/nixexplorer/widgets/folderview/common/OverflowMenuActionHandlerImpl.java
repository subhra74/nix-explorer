/**
 * 
 */
package nixexplorer.widgets.folderview.common;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import nixexplorer.TextHolder;
import nixexplorer.core.FileInfo;
import nixexplorer.widgets.folderview.FolderViewWidget;
import nixexplorer.widgets.folderview.OverflowMenuActionHandler;
import nixexplorer.widgets.folderview.local.LocalFolderViewWidget;

/**
 * @author subhro
 *
 */
public class OverflowMenuActionHandlerImpl
		implements OverflowMenuActionHandler {

	private FolderViewWidget folderView;

	private JMenuItem mSelectAll, mClearSelection, mInverseSelection,
			mSelectSimilar, mUnSelectSimilar, mFilter, mSelectFiles,
			mUnselectFiles;
	private JCheckBoxMenuItem mShowHiddenFiles;
	private KeyStroke ksHideShow, ksInvSel, ksFilter;

	/**
	 * 
	 */
	public OverflowMenuActionHandlerImpl() {
		ksHideShow = KeyStroke.getKeyStroke(KeyEvent.VK_H,
				InputEvent.CTRL_DOWN_MASK);
		ksInvSel = KeyStroke.getKeyStroke(KeyEvent.VK_I,
				InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
		ksFilter = KeyStroke.getKeyStroke(KeyEvent.VK_F,
				InputEvent.CTRL_DOWN_MASK);
		mShowHiddenFiles = new JCheckBoxMenuItem(
				TextHolder.getString("folderview.showHidden"));
		mShowHiddenFiles.addActionListener(e -> {
			hideOptAction();
		});
		mShowHiddenFiles.setAccelerator(ksHideShow);

		mSelectAll = new JMenuItem(
				TextHolder.getString("folderview.selectAll"));
		mSelectAll.addActionListener(e -> {
			selectAllAction();
		});
		mSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				InputEvent.CTRL_DOWN_MASK));

		mClearSelection = new JMenuItem(
				TextHolder.getString("folderview.clearSelection"));
		mClearSelection.addActionListener(e -> {
			clearSelectionAction();
		});

		mInverseSelection = new JMenuItem(
				TextHolder.getString("folderview.inverseSelection"));
		mInverseSelection.addActionListener(e -> {
			invertSelectionAction();
		});
		mInverseSelection.setAccelerator(ksInvSel);

		mSelectFiles = new JMenuItem(
				TextHolder.getString("folderview.selectByPattern"));
		mSelectFiles.addActionListener(e -> {
			selectAction();
		});

		mUnselectFiles = new JMenuItem(
				TextHolder.getString("folderview.unselectByPattern"));
		mUnselectFiles.addActionListener(e -> {
			unSelectAction();
		});

		mSelectSimilar = new JMenuItem(
				TextHolder.getString("folderview.selectSimilar"));
		mSelectSimilar.addActionListener(e -> {
			selectSimilarAction();
		});

		mUnSelectSimilar = new JMenuItem(
				TextHolder.getString("folderview.unSelectSimilar"));
		mUnSelectSimilar.addActionListener(e -> {
			unSelectSimilarAction();
		});

		mFilter = new JMenuItem(TextHolder.getString("folderview.filter"));
		mFilter.addActionListener(e -> {
			appyFiterAction();
		});
		mFilter.setAccelerator(ksFilter);
	}

	/**
	 * 
	 */
	private void selectAllAction() {
		folderView.getSelectionHelper().selectAll();
	}

	/**
	 * 
	 */
	private void clearSelectionAction() {
		folderView.getSelectionHelper().clearSelection();
	}

	/**
	 * 
	 */
	private void invertSelectionAction() {
		folderView.getSelectionHelper().inverseSelection();
	}

	/**
	 * 
	 */
	private void selectAction() {
		folderView.getSelectionHelper().selectFiltered();
	}

	/**
	 * 
	 */
	private void unSelectAction() {
		folderView.getSelectionHelper().unselectFiltered();
	}

	/**
	 * 
	 */
	private void selectSimilarAction() {
		folderView.getSelectionHelper().selectSimilarFiles();
	}

	/**
	 * 
	 */
	private void unSelectSimilarAction() {
		folderView.getSelectionHelper().unselectSimilarFiles();
	}

	/**
	 * 
	 */
	private void appyFiterAction() {
		folderView.getSelectionHelper().applyFilter();
	}

	/**
	 * 
	 */
	private void hideOptAction() {
		folderView.setShowingHiddenFiles(mShowHiddenFiles.isSelected());
		folderView.getSelectionHelper().refresh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nixexplorer.widgets.folderview.OverflowMenuActionHandler#createMenu(javax
	 * .swing.JPopupMenu)
	 */
	@Override
	public void createMenu(JPopupMenu popup) {
		popup.removeAll();

		List<FileInfo> allFiles = folderView.getCurrentFiles();
		FileInfo[] selectedFiles = folderView.getSelectedFiles();
		String path = folderView.getCurrentPath();

		popup.add(mSelectAll);
		popup.add(mClearSelection);
		popup.add(mInverseSelection);
		popup.add(mSelectFiles);
		popup.add(mUnselectFiles);
		popup.add(mSelectSimilar);
		popup.add(mUnSelectSimilar);
		popup.add(mFilter);
		popup.add(mShowHiddenFiles);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nixexplorer.widgets.folderview.OverflowMenuActionHandler#install(
	 * nixexplorer.widgets.folderview.FolderViewWidget)
	 */
	@Override
	public void install(FolderViewWidget c) {
		folderView = c;
		InputMap map = c
				.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap act = c.getActionMap();
		map.put(ksFilter, "ksFilter");
		act.put("ksFilter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				appyFiterAction();
			}
		});

		map.put(ksHideShow, "ksHideShow");
		act.put("ksHideShow", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mShowHiddenFiles.setSelected(!mShowHiddenFiles.isSelected());
				hideOptAction();
			}
		});

		map.put(ksInvSel, "ksInvSel");
		act.put("ksInvSel", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				invertSelectionAction();
			}
		});
	}

}

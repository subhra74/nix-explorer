/**
 * 
 */
package nixexplorer.widgets.folderview.common;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
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

	private JRadioButtonMenuItem mSortName, mSortSize, mSortType, mSortModified,
			mSortPermission, mSortAsc, mSortDesc;

	private AtomicBoolean sortingChanging = new AtomicBoolean(false);

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

		ButtonGroup bg1 = new ButtonGroup();

		mSortName = createSortMenuItem(
				TextHolder.getString("folderview.sortByName"), 0, bg1);

		mSortSize = createSortMenuItem(
				TextHolder.getString("folderview.sortBySize"), 1, bg1);

		mSortType = createSortMenuItem(
				TextHolder.getString("folderview.sortByType"), 2, bg1);

		mSortModified = createSortMenuItem(
				TextHolder.getString("folderview.sortByModified"), 3, bg1);

		mSortPermission = createSortMenuItem(
				TextHolder.getString("folderview.sortByPerm"), 4, bg1);

		ButtonGroup bg2 = new ButtonGroup();

		mSortAsc = createSortMenuItem(
				TextHolder.getString("folderview.sortAsc"), 0, bg2);

		mSortDesc = createSortMenuItem(
				TextHolder.getString("folderview.sortDesc"), 1, bg2);
	}

	private JRadioButtonMenuItem createSortMenuItem(String text, Integer index,
			ButtonGroup bg) {
		JRadioButtonMenuItem mSortItem = new JRadioButtonMenuItem(text);
		mSortItem.putClientProperty("sort.index", index);
		mSortItem.addActionListener(e -> {
			sortMenuClicked(mSortItem);
		});
		bg.add(mSortItem);
		return mSortItem;
	}

	private void sortMenuClicked(JRadioButtonMenuItem mSortItem) {
		if (sortingChanging.get()) {
			return;
		}
		int index = (int) mSortItem.getClientProperty("sort.index");
		boolean asc = mSortAsc.isSelected();
		folderView.sortView(index, asc);
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

		JMenu mSortMenu = new JMenu(TextHolder.getString("folderview.sortBy"));
		popup.add(mSortMenu);

		mSortMenu.add(mSortName);
		mSortMenu.add(mSortSize);
		mSortMenu.add(mSortType);
		mSortMenu.add(mSortModified);
		mSortMenu.add(mSortPermission);
		mSortMenu.addSeparator();
		mSortMenu.add(mSortAsc);
		mSortMenu.add(mSortDesc);

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

	@Override
	public void updateMenu() {
		sortingChanging.set(true);
		int index = folderView.getSortField();
		for (JRadioButtonMenuItem item : new JRadioButtonMenuItem[] { mSortName,
				mSortSize, mSortType, mSortModified, mSortPermission }) {
			if (index == (int) item.getClientProperty("sort.index")) {
				item.setSelected(true);
				break;
			}
		}
		if (folderView.isSortingAscending()) {
			mSortAsc.setSelected(true);
		} else {
			mSortDesc.setSelected(true);
		}
		sortingChanging.set(false);
	}

}

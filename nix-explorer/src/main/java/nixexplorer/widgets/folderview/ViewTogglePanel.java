package nixexplorer.widgets.folderview;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.UIManager;

import nixexplorer.TextHolder;
import nixexplorer.widgets.util.Utility;

public class ViewTogglePanel {
	private Box b1;
	private JButton btnListView, btnDetailsView;
	private ActionListener viewChangeListener;

	public enum ViewMode {
		List, Details
	}

	private ViewMode viewMode = ViewMode.List;

	public ViewTogglePanel(ViewMode viewMode) {
		this.viewMode = viewMode;
		btnListView = new JButton(UIManager.getIcon("ViewMode.listIcon"));
		btnDetailsView = new JButton(UIManager.getIcon("ViewMode.detailsIcon"));

		btnListView
				.setToolTipText(TextHolder.getString("viewMode.listViewText"));
		btnDetailsView.setToolTipText(
				TextHolder.getString("viewMode.detailsViewText"));

		btnDetailsView.setBorderPainted(false);
		btnDetailsView.setFocusPainted(false);

		btnListView.setBorderPainted(false);
		btnListView.setFocusPainted(false);

		b1 = Box.createHorizontalBox();
		b1.add(btnListView);
		b1.add(Box.createHorizontalStrut(Utility.toPixel(5)));
		b1.add(btnDetailsView);

		setViewMode(viewMode);

		btnDetailsView.addActionListener(e -> {
			setViewMode(ViewMode.Details);
		});

		btnListView.addActionListener(e -> {
			setViewMode(ViewMode.List);
		});
	}

	public Box getComponent() {
		return this.b1;
	}

	private void updateButtonState() {
		btnDetailsView.setBackground(this.viewMode == ViewMode.Details
				? UIManager.getColor("DefaultBorder.color")
				: UIManager.getColor("Button.background"));
		btnListView.setBackground(this.viewMode == ViewMode.List
				? UIManager.getColor("DefaultBorder.color")
				: UIManager.getColor("Button.background"));
	}

	public void setViewListener(ActionListener a) {
		this.viewChangeListener = a;
	}

	public final void setViewMode(ViewMode viewMode) {
		this.viewMode = viewMode;
		updateButtonState();
		if (this.viewChangeListener != null) {
			this.viewChangeListener
					.actionPerformed(new ActionEvent(this, -1, ""));
		}
	}

	public synchronized ViewMode getViewMode() {
		return viewMode;
	}
}

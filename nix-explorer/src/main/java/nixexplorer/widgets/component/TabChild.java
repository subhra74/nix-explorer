package nixexplorer.widgets.component;

import javax.swing.JLabel;

public interface TabChild {
	public boolean onClosing();

	public void onClosed();

	public void onInit(JLabel lblTabLabel);
}

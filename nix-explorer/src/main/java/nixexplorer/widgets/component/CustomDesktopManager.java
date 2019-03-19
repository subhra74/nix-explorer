package nixexplorer.widgets.component;

import java.awt.Rectangle;
import java.util.Map;

import javax.swing.DefaultDesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JToolBar;

public class CustomDesktopManager extends DefaultDesktopManager {
	private static final long serialVersionUID = -2890379774059804768L;
	private JDesktopPane desktop;

	@Override
	public void iconifyFrame(JInternalFrame f) {
		if (f.getClientProperty("modalChild") != null) {
			return;
		}
		super.iconifyFrame(f);
		// f.setVisible(false);
	}

	@Override
	protected Rectangle getBoundsForIconOf(JInternalFrame f) {
		return new Rectangle(0, 0, 0, 0);
	}

	public JDesktopPane getDesktop() {
		return desktop;
	}

	public void setDesktop(JDesktopPane desktop) {
		this.desktop = desktop;
	}

	@Override
	public void activateFrame(JInternalFrame f) {
		System.out.println("Activating frame " + f);
		if (f.getClientProperty("modalChild") != null) {
			JInternalFrame dlg = (JInternalFrame) f.getClientProperty("modalChild");
			System.out.println("Activating child modal: " + dlg);
			activate(dlg);
			return;
		}
		if (f.getClientProperty("desktop") == null) {
			activate(f);
			return;
		}
	}

	@Override
	public void minimizeFrame(JInternalFrame f) {
		if (f.getClientProperty("modalChild") != null) {
			return;
		}
		super.minimizeFrame(f);
	}

	@Override
	public void maximizeFrame(JInternalFrame f) {
		if (f.getClientProperty("modalChild") != null) {
			return;
		}
		super.maximizeFrame(f);
	}

	private void activate(JInternalFrame f) {
		System.out.println("Activating frame real " + f);
		super.activateFrame(f);
	}

	@Override
	public void closeFrame(JInternalFrame f) {
		if (f.getClientProperty("modalChild") != null) {
			return;
		}
		super.closeFrame(f);
	}

}

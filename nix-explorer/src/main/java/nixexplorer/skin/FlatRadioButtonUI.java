package nixexplorer.skin;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonUI;

public class FlatRadioButtonUI extends BasicRadioButtonUI {
	public static ComponentUI createUI(JComponent c) {
		return new FlatRadioButtonUI();
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		if (c instanceof AbstractButton) {
			AbstractButton ab = (AbstractButton) c;
			ab.setSelectedIcon(UIManager.getIcon("RadioButton.selectedIcon"));
			ab.setIcon(UIManager.getIcon("RadioButton.icon"));
		}
	}
}

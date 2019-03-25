/**
 * 
 */
package nixexplorer.skin;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;

/**
 * @author subhro
 *
 */
public class FlatRadioButtonMenuItemUI extends BasicRadioButtonMenuItemUI {
	public static ComponentUI createUI(JComponent c) {
		return new FlatRadioButtonMenuItemUI();
	}
}
